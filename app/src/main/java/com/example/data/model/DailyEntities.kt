package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "daily_idiom_assignments",
    indices = [
        Index(value = ["userId", "idiomId"], unique = true),
        Index(value = ["userId", "assignedDate"], unique = true)
    ]
)
@Serializable
data class DailyIdiomAssignment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "current-user-id",
    val idiomId: String,
    val assignedDate: String, // "yyyy-MM-dd"
    val completedAt: Long? = null,
    val savedAt: Long? = null,
    val practiseStartedAt: Long? = null,
    val source: String = "daily"
)

@Entity(
    tableName = "daily_challenge_assignments",
    indices = [
        Index(value = ["userId", "assignedDate"], unique = true)
    ]
)
@Serializable
data class DailyChallengeAssignment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "current-user-id",
    val questionId: String,
    val assignedDate: String, // "yyyy-MM-dd"
    val completedAt: Long? = null
)

@Entity(
    tableName = "revision_items",
    indices = [
        Index(value = ["userId", "sourceType", "sourceId"], unique = true)
    ]
)
@Serializable
data class RevisionItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "current-user-id",
    val sourceType: String, // "daily_idiom" | "daily_challenge" | "tutor_practice" | "lesson" | "quiz"
    val sourceId: String,
    val topic: String,
    val contentSnapshot: String, // JSON snapshot of the item
    val learnerLevel: String,
    val difficulty: String, // "easy", "medium", "hard"
    val status: String, // "pending", "mastered", "review_due"
    val timesReviewed: Int = 0,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val confidenceRating: Int = 3, // 1 to 5 scale
    val nextReviewAt: Long,
    val lastReviewedAt: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "achievement_unlocks",
    indices = [
        Index(value = ["userId", "achievementId"], unique = true)
    ]
)
@Serializable
data class AchievementUnlock(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "current-user-id",
    val achievementId: String,
    val unlockedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "daily_completion_rewards",
    indices = [
        Index(value = ["userId", "completionDate"], unique = true)
    ]
)
@Serializable
data class DailyCompletionReward(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "current-user-id",
    val completionDate: String, // "yyyy-MM-dd"
    val rewardedAt: Long = System.currentTimeMillis(),
    val xpEarned: Int = 50
)

@Serializable
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val currentProgress: Int,
    val maxProgress: Int,
    val isUnlocked: Boolean
)
