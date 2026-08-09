package com.example.data.local

import androidx.room.*
import com.example.data.model.PronunciationHistoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PronunciationHistoryDao {
    @Query("SELECT * FROM pronunciation_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<PronunciationHistoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryItem(item: PronunciationHistoryItem)

    @Query("DELETE FROM pronunciation_history WHERE id = :id")
    suspend fun deleteHistoryItem(id: String)

    @Query("DELETE FROM pronunciation_history")
    suspend fun clearAllHistory()
}
