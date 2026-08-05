package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
enum class DailyChallengeState {
    LOADING,
    READY,
    ANSWERED,
    COMPLETED,
    ERROR
}

@Serializable
enum class DailyChallengeQuestionType {
    MCQ,
    FILL_IN_THE_BLANK,
    SENTENCE_CORRECTION,
    TRANSLATION,
    VOCABULARY,
    IDIOM
}

@Serializable
data class DailyChallengeQuestion(
    val id: String,
    val topic: String,
    val type: DailyChallengeQuestionType,
    val questionText: String,
    val options: List<String> = emptyList(), // For MCQ
    val correctOptionIndex: Int? = null, // For MCQ
    val acceptedAnswers: List<String> = emptyList(), // For Fill-in-the-blank / Sentence Correction / Translation
    val explanationEn: String,
    val explanationUr: String, // Urdu explanation
    val arabicReference: String? = null, // Relevant Arabic grammar concept
    val extraExample: String? = null, // Additional example
    val urduTranslation: String? = null,
    val wordBank: List<String>? = null,
    val blankMarker: String = "_____"
)

@Entity(tableName = "daily_challenge_results")
@Serializable
data class DailyChallengeResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val questionId: String,
    val topic: String,
    val type: String,
    val userAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean,
    val timeTakenMillis: Long
)
