package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DailyGrammarItem(
    val titleEn: String,
    val titleAr: String,
    val grammarRuleArabic: String,
    val englishExample: String,
    val arabicTranslation: String,
    val arabicReference: String,
    val questionText: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val correctFeedbackArabic: String,
    val incorrectFeedbackArabic: String
)
