package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LessonMcq(
    val id: String,
    val question: String,
    val questionUr: String = "",
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanationUr: String,
    val explanationEn: String = ""
)
