package com.example.data.local

import com.example.data.model.ChatMessageEntity
import com.example.data.model.Content
import com.example.data.model.Part
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatMessageRepository(private val chatMessageDao: ChatMessageDao) {

    val chatMessagesFlow: Flow<List<Content>> = chatMessageDao.getAllMessages().map { list ->
        list.map { entity ->
            Content(role = entity.role, parts = listOf(Part(text = entity.text)))
        }
    }

    suspend fun loadMessagesList(): List<Content> {
        return chatMessageDao.getAllMessagesList().map { entity ->
            Content(role = entity.role, parts = listOf(Part(text = entity.text)))
        }
    }

    suspend fun saveMessage(role: String, text: String) {
        chatMessageDao.insertMessage(ChatMessageEntity(role = role, text = text))
    }

    suspend fun clearHistory() {
        chatMessageDao.clearAllMessages()
    }
}
