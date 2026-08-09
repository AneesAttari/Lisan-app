package com.example.data.repository

import com.example.data.local.PronunciationHistoryDao
import com.example.data.model.PronunciationHistoryItem
import kotlinx.coroutines.flow.Flow

class PronunciationHistoryRepository(private val dao: PronunciationHistoryDao) {
    val allHistory: Flow<List<PronunciationHistoryItem>> = dao.getAllHistory()

    suspend fun insert(item: PronunciationHistoryItem) {
        dao.insertHistoryItem(item)
    }

    suspend fun delete(id: String) {
        dao.deleteHistoryItem(id)
    }

    suspend fun clearAll() {
        dao.clearAllHistory()
    }
}
