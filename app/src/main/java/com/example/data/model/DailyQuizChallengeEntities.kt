package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "daily_quiz_challenge_progress")
data class DailyQuizChallengeProgressEntity(
    @PrimaryKey val userId: String,
    val startDate: String,
    val totalCompletedDays: Int = 0,
    val lastCompletedDate: String? = null
)

@Entity(tableName = "daily_quiz_assignments")
data class DailyQuizAssignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val date: String,
    val questionIds: String, // Stored as comma-separated string
    val completed: Boolean = false,
    val completionTimestamp: Long? = null,
    val score: Int = 0
)
