package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ExamSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamSessionDao {
    @Query("SELECT * FROM exam_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<ExamSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ExamSessionEntity)

    @Query("SELECT * FROM exam_sessions WHERE id = :id LIMIT 1")
    suspend fun getSessionById(id: String): ExamSessionEntity?
}
