package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "exam_sessions")
data class ExamSessionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val topic: String = "Grammar Exam",
    val score: Int = 0,
    val totalQuestions: Int = 10,
    val isCompleted: Boolean = false,
    val summary: String? = null
)
