package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ConversationSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationSessionDao {
    @Query("SELECT * FROM conversation_sessions WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getSessionsForUser(userId: String): Flow<List<ConversationSessionEntity>>

    @Query("SELECT * FROM conversation_sessions WHERE userId = :userId ORDER BY updatedAt DESC")
    suspend fun getSessionsForUserList(userId: String): List<ConversationSessionEntity>

    @Query("SELECT * FROM conversation_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: String): ConversationSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ConversationSessionEntity)

    @Query("DELETE FROM conversation_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: String)

    @Query("DELETE FROM conversation_sessions WHERE userId = :userId")
    suspend fun clearSessionsForUser(userId: String)
}
