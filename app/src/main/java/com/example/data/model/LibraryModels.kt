package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "library_items")
data class LibraryItem(
    @PrimaryKey val id: String, // Combined key or unique ID
    val userId: String = "default_user",
    val contentType: String, // "Lesson", "Idiom", "Analysis", "Quiz", "Note"
    val contentId: String,
    val title: String,
    val preview: String? = null,
    val sourcePage: String? = null,
    val savedTimestamp: Long = System.currentTimeMillis(),
    val category: String? = null,
    val payloadJson: String? = null // Optional JSON for complete content restoration
)

@Serializable
@Entity(tableName = "liked_items")
data class LikedItem(
    @PrimaryKey val id: String,
    val userId: String = "default_user",
    val contentType: String,
    val contentId: String,
    val title: String,
    val likedTimestamp: Long = System.currentTimeMillis(),
    val preview: String? = null
)

@Serializable
@Entity(tableName = "disliked_items")
data class DislikedItem(
    @PrimaryKey val id: String,
    val userId: String = "default_user",
    val contentType: String,
    val contentId: String,
    val title: String,
    val dislikedTimestamp: Long = System.currentTimeMillis()
)

@Serializable
@Entity(tableName = "help_feedback_items")
data class FeedbackEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "default_user",
    val subject: String,
    val message: String,
    val category: String = "General",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Submitted"
)

