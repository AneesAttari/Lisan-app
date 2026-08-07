package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversation_sessions")
data class ConversationSessionEntity(
    @PrimaryKey val id: String,
    val userId: String = "current-user-id",
    val title: String,
    val topicLabel: String, // Grammar, Idiom, Pronunciation, Revision, Daily Challenge, Tutor Chat
    val preview: String,
    val messageCount: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val messagesJson: String = ""
)
