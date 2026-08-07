package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PreferredLanguages(
    val primaryExplanation: String = "ur",
    val englishSupport: Boolean = true,
    val arabicGrammarSupport: Boolean = true
)

@Serializable
data class LearningLevel(
    val overall: String = "Beginner", // Absolute Beginner, Beginner, Elementary, Pre-Intermediate, Intermediate, Upper-Intermediate, Advanced
    val confidence: Float = 0.5f,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Serializable
data class MistakePattern(
    val topic: String,
    val pattern: String,
    val frequency: Int = 1,
    val lastSeen: Long = System.currentTimeMillis(),
    val example: String = ""
)

@Serializable
data class LearningPreferences(
    val preferredAnswerLength: String = "short_then_expandable",
    val needsUrduExplanation: Boolean = true,
    val needsArabicComparisonWhenRelevant: Boolean = true,
    val preferredQuestionTypes: List<String> = listOf("fill_blank", "examples", "translation")
)

@Serializable
data class RecentLearningItem(
    val type: String, // "lesson", "quiz", "daily_challenge", "analyzer", "idiom", "tutor_chat"
    val topic: String,
    val status: String, // "completed", "attempted", "mastered", "saved", "asked"
    val score: String? = null,
    val date: Long = System.currentTimeMillis()
)

@Serializable
data class LearnerProfile(
    val userId: String = "current-user-id",
    val preferredLanguages: PreferredLanguages = PreferredLanguages(),
    val learningLevel: LearningLevel = LearningLevel(),
    val topicMastery: Map<String, Float> = mapOf(
        "Present Simple" to 0.5f,
        "Present Continuous" to 0.4f,
        "Past Simple" to 0.4f,
        "Past Continuous" to 0.3f,
        "Present Perfect" to 0.2f,
        "Future Forms" to 0.4f,
        "Articles" to 0.4f,
        "Prepositions" to 0.3f,
        "Idioms" to 0.5f,
        "Vocabulary" to 0.5f
    ),
    val commonMistakes: List<MistakePattern> = emptyList(),
    val learningPreferences: LearningPreferences = LearningPreferences(),
    val recentLearning: List<RecentLearningItem> = emptyList(),
    val usefulMemories: List<String> = listOf(
        "Student is learning English grammar with Urdu explanations.",
        "Student benefits from Arabic grammar comparisons."
    ),
    val masteredContentIds: List<String> = emptyList(),
    val reviewDueContentIds: List<String> = emptyList(),
    val isMemoryEnabled: Boolean = true,
    val totalInteractions: Int = 0,
    val correctAnswersCount: Int = 0,
    val totalQuestionsAnswered: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis(),
    val interactionTimestamps: List<Long> = emptyList()
)
