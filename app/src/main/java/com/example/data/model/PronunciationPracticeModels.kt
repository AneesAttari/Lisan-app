package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class PronunciationItem(
    val id: String,
    val text: String,
    val phonetic: String? = null,
    val meaning: String,
    val translation: String, // Arabic / Urdu translation
    val example: String,
    val difficulty: String, // "Beginner", "Intermediate", "Advanced"
    val category: String // "word", "idiom", "sentence"
)

@Entity(tableName = "pronunciation_history")
data class PronunciationHistoryItem(
    @PrimaryKey val id: String,
    val targetText: String,
    val spokenText: String,
    val score: Int,
    val category: String,
    val timestamp: Long,
    val accuracyFeedback: String
)
