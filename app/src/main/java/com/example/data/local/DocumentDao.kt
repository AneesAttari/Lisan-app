package com.example.data.local

import androidx.room.*
import com.example.data.model.DocumentChunkEntity
import com.example.data.model.DocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentEntity)

    @Update
    suspend fun updateDocument(document: DocumentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChunks(chunks: List<DocumentChunkEntity>)

    @Query("SELECT * FROM documents ORDER BY createdAt DESC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents ORDER BY createdAt DESC")
    suspend fun getAllDocumentsList(): List<DocumentEntity>

    @Query("SELECT * FROM documents WHERE documentId = :id LIMIT 1")
    suspend fun getDocumentById(id: String): DocumentEntity?

    @Query("SELECT * FROM document_chunks WHERE documentId = :documentId ORDER BY chunkIndex ASC")
    suspend fun getChunksForDocument(documentId: String): List<DocumentChunkEntity>

    @Query("DELETE FROM documents WHERE documentId = :documentId")
    suspend fun deleteDocument(documentId: String)

    @Query("DELETE FROM document_chunks WHERE documentId = :documentId")
    suspend fun deleteChunksForDocument(documentId: String)
}
