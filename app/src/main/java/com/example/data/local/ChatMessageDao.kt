package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC, id ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC, id ASC")
    suspend fun getAllMessagesList(): List<ChatMessageEntity>

    @Query("SELECT * FROM (SELECT * FROM chat_messages ORDER BY timestamp DESC, id DESC LIMIT :limit) ORDER BY timestamp ASC, id ASC")
    fun getRecentMessages(limit: Int = 40): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM (SELECT * FROM chat_messages ORDER BY timestamp DESC, id DESC LIMIT :limit) ORDER BY timestamp ASC, id ASC")
    suspend fun getRecentMessagesList(limit: Int = 40): List<ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessageEntity>)

    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()
}
