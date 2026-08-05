package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Idiom(
    val id: String,
    val expression: String,
    val simpleMeaning: String,
    val urduMeaning: String,
    val arabicMeaning: String,
    val arabicType: ArabicEquivalentType = ArabicEquivalentType.MEANING,
    val example: String,
    val level: IdiomLevel = IdiomLevel.BEGINNER,
    val category: IdiomCategory = IdiomCategory.DAILY_CONVERSATION,
    val origin: String? = null
)

enum class IdiomLevel(val label: String) {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced")
}

enum class IdiomCategory(val label: String) {
    DAILY_CONVERSATION("Daily Conversation"),
    STUDY_SCHOOL("Study & School"),
    FEELINGS_EMOTIONS("Feelings & Emotions"),
    SUCCESS_FAILURE("Success & Failure"),
    WORK_EFFORT("Work & Effort"),
    TIME_HABITS("Time & Habits"),
    FAMILY_FRIENDS("Family & Friends"),
    SPEAKING_COMMUNICATION("Speaking & Communication"),
    MONEY_VALUE("Money & Value"),
    ISLAMIC_CONTEXT("Islamic / Respectful")
}

enum class ArabicEquivalentType {
    PROVERB,
    MEANING
}
