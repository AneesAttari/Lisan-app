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
    val date: String, // e.g. "day_1", "day_2", ..., "day_365"
    val questionIds: String = "", // Stored as comma-separated string
    val completedMcqsCount: Int = 0, // 0..10 completed MCQs
    val completed: Boolean = false, // true ONLY when completedMcqsCount >= 10
    val completionTimestamp: Long? = null,
    val score: Int = 0,
    val lastAnsweredIndex: Int = 0 // Index of next incomplete MCQ (0..9)
)

enum class DayQuizStatus {
    COMPLETED,
    IN_PROGRESS,
    NOT_STARTED,
    LOCKED
}

data class DayProgressInfo(
    val dayNumber: Int, // 1..365 absolute day
    val dayOfWeekIndex: Int = 1, // 1..7 (1=Mon, 7=Sun)
    val dayNameEn: String, // "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
    val dayNameUr: String, // "پیر", "منگل", "بدھ", "جمعرات", "جمعہ", "ہفتہ", "اتوار"
    val dateStr: String = "", // "yyyy-MM-dd"
    val isToday: Boolean = false, // Is this the active / current day
    val isCompleted: Boolean = false, // true ONLY when completedMcqsCount == 10
    val isInProgress: Boolean = false, // true when completedMcqsCount in 1..9
    val isLocked: Boolean = false,
    val completedMcqsCount: Int = 0, // 0..10
    val totalQuizzes: Int = 10,
    val score: Int = 0,
    val lastAnsweredIndex: Int = 0,
    val grammarTopicTitle: String = "10 Grammar Pillars",
    val grammarTopicTitleUr: String = "گرامر کے 10 بنیادی پہلو"
) {
    val status: DayQuizStatus
        get() = when {
            isCompleted -> DayQuizStatus.COMPLETED
            isInProgress -> DayQuizStatus.IN_PROGRESS
            isLocked -> DayQuizStatus.LOCKED
            else -> DayQuizStatus.NOT_STARTED
        }
}

data class WeeklyQuizProgression(
    val weekNumber: Int = 1, // 1..53
    val totalWeeks: Int = 53,
    val currentActiveDayNumber: Int = 1, // 1..365
    val currentActiveWeekNumber: Int = 1, // 1..53
    val currentDayIndex: Int = 1, // 1..7 (e.g. 1 for Mon, 6 for Sat)
    val currentDayNameEn: String = "Mon",
    val currentDayNameUr: String = "پیر",
    val completedDaysCount: Int = 0, // e.g. 2 / 7 (ONLY fully completed days)
    val totalDaysInWeek: Int = 7,
    val days: List<DayProgressInfo> = emptyList(),
    val isWeekCompleted: Boolean = false, // true when completedDaysCount == totalDaysInWeek
    val completedWeeksCount: Int = 0, // Total weeks where all 7 days are fully completed
    val totalCompletedDays: Int = 0, // Total fully completed days in 365 days
    val todayCompleted: Boolean = false, // Whether current active day is completed
    val weekStartDateStr: String = "",
    val weekEndDateStr: String = ""
)


