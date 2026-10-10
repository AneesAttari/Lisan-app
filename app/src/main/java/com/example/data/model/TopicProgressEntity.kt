package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "topic_progress")
@Serializable
data class TopicProgressEntity(
    @PrimaryKey val topicId: String,
    val isCompleted: Boolean = false,
    val masteryScore: Float = 0.0f,
    val completedAt: Long? = null,
    val updatedAt: Long = System.currentTimeMillis()
)
