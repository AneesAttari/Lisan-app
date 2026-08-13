package com.example.data.local

import com.example.data.model.ChatMessageEntity
import com.example.data.model.Content
import com.example.data.model.Part
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatMessageRepository(private val chatMessageDao: ChatMessageDao) {

    val chatMessagesFlow: Flow<List<Content>> = chatMessageDao.getRecentMessages(40).map { list ->
        list.map { entity ->
            Content(role = entity.role, parts = listOf(Part(text = entity.text)), imageUri = entity.imageUri)
        }
    }

    fun getRecentMessagesFlow(limit: Int = 40): Flow<List<Content>> = chatMessageDao.getRecentMessages(limit).map { list ->
        list.map { entity ->
            Content(role = entity.role, parts = listOf(Part(text = entity.text)), imageUri = entity.imageUri)
        }
    }

    suspend fun loadMessagesList(): List<Content> {
        return chatMessageDao.getRecentMessagesList(40).map { entity ->
            Content(role = entity.role, parts = listOf(Part(text = entity.text)), imageUri = entity.imageUri)
        }
    }

    suspend fun loadAllMessagesList(): List<Content> {
        return chatMessageDao.getAllMessagesList().map { entity ->
            Content(role = entity.role, parts = listOf(Part(text = entity.text)), imageUri = entity.imageUri)
        }
    }

    suspend fun saveMessage(role: String, text: String, imageUri: String? = null) {
        chatMessageDao.insertMessage(ChatMessageEntity(role = role, text = text, imageUri = imageUri))
    }

    suspend fun clearHistory() {
        chatMessageDao.clearAllMessages()
    }
}
