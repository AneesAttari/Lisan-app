package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "revision_questions")
data class RevisionQuestionEntity(
    @PrimaryKey val id: String,
    val topicId: String,
    val subtopicId: String,
    val difficulty: String, // "Beginner", "Intermediate", "Advanced"
    val type: String, // "fill_in_the_blank", "choose_correct_sentence", etc.
    val stem: String, // The question text, e.g. "Ahmed ___ a doctor."
    val context: String? = null,
    val optionsString: String, // Comma or pipe-separated options, e.g. "is||am||are||be"
    val correctOptionIndex: Int,
    val explanationEnglish: String,
    val explanationUrdu: String,
    val explanationArabic: String,
    val tagsString: String, // Comma-separated tags
    val normalizedFingerprint: String,
    val contentVersion: Int = 1,
    val isActive: Boolean = true
) {
    fun getOptions(): List<String> = optionsString.split("||")
    fun getTags(): List<String> = tagsString.split(",")
}

@Entity(tableName = "question_progress")
data class QuestionProgressEntity(
    @PrimaryKey val questionId: String,
    val topicId: String,
    val timesSeen: Int = 0,
    val timesCorrect: Int = 0,
    val timesIncorrect: Int = 0,
    val lastShownAt: Long = 0L,
    val lastAnsweredAt: Long = 0L,
    val lastAnswerCorrect: Boolean? = null,
    val masteryScore: Int = 0, // 0 to 100 representing mastery level
    val nextEligibleAt: Long = 0L,
    val lastSessionId: String? = null,
    val lastAnswerOptionIndex: Int? = null
)

@Entity(tableName = "revision_sessions")
data class RevisionSessionEntity(
    @PrimaryKey val sessionId: String,
    val sessionType: String, // "today", "topic", "mistakes", "mixed", "quick"
    val topicId: String? = null,
    val requestedQuestionCount: Int,
    val actualQuestionCount: Int,
    val startedAt: Long,
    val completedAt: Long? = null,
    val scoreCorrect: Int = 0,
    val scoreIncorrect: Int = 0,
    val status: String // "started", "completed"
)

@Entity(tableName = "revision_session_questions")
data class RevisionSessionQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionId: String,
    val questionId: String,
    val displayOrder: Int,
    val shownAt: Long,
    val selectedOptionIndex: Int? = null,
    val answeredCorrectly: Boolean? = null,
    val answeredAt: Long? = null
)

@Entity(tableName = "topic_progress")
data class TopicProgressEntity(
    @PrimaryKey val topicId: String,
    val totalQuestions: Int,
    val unseenCount: Int,
    val dueCount: Int,
    val weakCount: Int,
    val masteredCount: Int,
    val overallMastery: Int, // percentage 0-100
    val nextReviewAt: Long? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

data class DashboardSummary(
    val totalQuestions: Int,
    val unseenCount: Int,
    val dueCount: Int,
    val weakCount: Int,
    val masteredCount: Int,
    val overallMastery: Int
)

data class AnswerResult(
    val isCorrect: Boolean,
    val correctOptionIndex: Int,
    val explanationEnglish: String,
    val explanationUrdu: String,
    val explanationArabic: String
)
