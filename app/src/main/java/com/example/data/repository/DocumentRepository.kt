package com.example.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.data.document.DocumentProcessor
import com.example.data.document.DocumentRetrievalEngine
import com.example.data.local.AppDatabase
import com.example.data.local.DocumentDao
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class DocumentRepository private constructor(
    private val documentDao: DocumentDao
) {
    val allDocuments: Flow<List<DocumentEntity>> = documentDao.getAllDocuments()

    private val _activeDocument = MutableStateFlow<DocumentEntity?>(null)
    val activeDocument: StateFlow<DocumentEntity?> = _activeDocument.asStateFlow()

    private val _processingState = MutableStateFlow<DocumentProcessingState>(DocumentProcessingState.Idle)
    val processingState: StateFlow<DocumentProcessingState> = _processingState.asStateFlow()

    suspend fun processAndStoreDocument(
        context: Context,
        uri: Uri,
        customFileName: String? = null
    ): DocumentEntity = withContext(Dispatchers.IO) {
        val nameToDisplay = customFileName ?: "Document_${System.currentTimeMillis()}"
        _processingState.value = DocumentProcessingState.Processing(nameToDisplay)

        try {
            val (doc, chunks) = DocumentProcessor.processDocument(context, uri, customFileName)
            
            // Store in Room DB
            documentDao.insertDocument(doc)
            if (chunks.isNotEmpty()) {
                documentDao.insertChunks(chunks)
            }

            if (doc.processingStatus == ProcessingStatus.READY.name) {
                _activeDocument.value = doc
                _processingState.value = DocumentProcessingState.Ready(doc)
            } else if (doc.processingStatus == ProcessingStatus.REQUIRES_OCR.name) {
                _processingState.value = DocumentProcessingState.Error(
                    doc.fileName,
                    doc.statusMessage ?: "Scanned document detected. Text requires OCR support that is not currently available."
                )
            } else {
                _processingState.value = DocumentProcessingState.Error(
                    doc.fileName,
                    doc.statusMessage ?: "Couldn't analyze this document."
                )
            }

            doc
        } catch (e: Exception) {
            Log.e("DocumentRepository", "Failed processing document: ${e.message}", e)
            val errorMsg = e.localizedMessage ?: "Unknown error while reading document"
            _processingState.value = DocumentProcessingState.Error(nameToDisplay, errorMsg)

            val failedDoc = DocumentEntity(
                fileName = nameToDisplay,
                mimeType = "application/octet-stream",
                fileSize = 0L,
                sourceUri = uri.toString(),
                processingStatus = ProcessingStatus.FAILED.name,
                statusMessage = errorMsg
            )
            documentDao.insertDocument(failedDoc)
            failedDoc
        }
    }

    fun setActiveDocument(document: DocumentEntity?) {
        _activeDocument.value = document
    }

    suspend fun retrieveGroundingContext(
        documentId: String,
        query: String,
        maxChunks: Int = 4
    ): String = withContext(Dispatchers.IO) {
        val document = documentDao.getDocumentById(documentId) ?: return@withContext ""
        val chunks = documentDao.getChunksForDocument(documentId)

        val relevantChunks = DocumentRetrievalEngine.retrieveRelevantChunks(
            document = document,
            chunks = chunks,
            userQuery = query,
            maxChunks = maxChunks
        )

        DocumentRetrievalEngine.buildGeminiGroundingPrompt(
            document = document,
            relevantChunks = relevantChunks,
            userQuery = query
        )
    }

    suspend fun getChunksForDocument(documentId: String): List<DocumentChunkEntity> = withContext(Dispatchers.IO) {
        documentDao.getChunksForDocument(documentId)
    }

    suspend fun deleteDocument(documentId: String) = withContext(Dispatchers.IO) {
        documentDao.deleteChunksForDocument(documentId)
        documentDao.deleteDocument(documentId)
        if (_activeDocument.value?.documentId == documentId) {
            _activeDocument.value = null
        }
    }

    fun resetProcessingState() {
        _processingState.value = DocumentProcessingState.Idle
    }

    companion object {
        @Volatile
        private var INSTANCE: DocumentRepository? = null

        fun getInstance(context: Context): DocumentRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getDatabase(context)
                val instance = DocumentRepository(db.documentDao())
                INSTANCE = instance
                instance
            }
        }

        fun getInstance(documentDao: DocumentDao): DocumentRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = DocumentRepository(documentDao)
                INSTANCE = instance
                instance
            }
        }
    }
}
