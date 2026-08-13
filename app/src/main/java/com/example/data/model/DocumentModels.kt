package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

enum class ProcessingStatus {
    PROCESSING,
    READY,
    FAILED,
    REQUIRES_OCR
}

@Serializable
@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey val documentId: String = UUID.randomUUID().toString(),
    val fileName: String,
    val mimeType: String,
    val fileSize: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val sourceUri: String,
    val extractedText: String = "",
    val structureJson: String? = null,
    val processingStatus: String = ProcessingStatus.PROCESSING.name,
    val statusMessage: String? = null,
    val pageCount: Int = 0,
    val wordCount: Int = 0,
    val chunkCount: Int = 0
)

@Serializable
@Entity(
    tableName = "document_chunks",
    indices = [Index(value = ["documentId"])]
)
data class DocumentChunkEntity(
    @PrimaryKey val chunkId: String = UUID.randomUUID().toString(),
    val documentId: String,
    val chunkIndex: Int,
    val pageNumber: Int? = null,
    val chapter: String? = null,
    val section: String? = null,
    val heading: String? = null,
    val text: String,
    val wordCount: Int = 0
)

@Serializable
data class DocumentPageMetadata(
    val pageNumber: Int,
    val heading: String? = null,
    val characterCount: Int = 0
)

@Serializable
data class DocumentChapterMetadata(
    val title: String,
    val startPage: Int = 1,
    val endPage: Int = 1,
    val sections: List<String> = emptyList()
)

@Serializable
data class DocumentStructure(
    val pages: List<DocumentPageMetadata> = emptyList(),
    val chapters: List<DocumentChapterMetadata> = emptyList(),
    val documentType: String = "Educational Document",
    val detectedLanguage: String = "English"
)

sealed class DocumentProcessingState {
    object Idle : DocumentProcessingState()
    data class Processing(val fileName: String) : DocumentProcessingState()
    data class Ready(val document: DocumentEntity) : DocumentProcessingState()
    data class Error(val fileName: String, val reason: String) : DocumentProcessingState()
}
