package com.example.data.model

import kotlinx.serialization.Serializable

/**
 * Metadata defining a grammatical question template for the 365-day quiz engine.
 */
@Serializable
data class DailyQuizTemplateMeta(
    val templateId: String,
    val slotIndex: Int, // 0..9 representing the 10 pedagogical pillars
    val grammarTopic: String,
    val subtopic: String,
    val difficulty: String, // Beginner, Intermediate, Advanced
    val questionType: String, // Fill-in-the-blank, Error identification, Sentence completion, Transformation, etc.
    val grammaticalStructure: String,
    val structuralFingerprint: String,
    val distractorStrategy: String
)

/**
 * Diagnostic data for inspecting daily quiz generation in debug mode.
 */
data class DailyQuizDiagnostics(
    val dayNumber: Int,
    val questionCount: Int,
    val templateIds: List<String>,
    val grammarTopics: List<String>,
    val structuralFingerprints: List<String>,
    val intraDayStructuralDuplicates: Int,
    val isDeterministic: Boolean
)
