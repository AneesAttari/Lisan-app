package com.example.data.repository

import android.content.Context
import com.example.data.model.ArabicEquivalentType
import com.example.data.model.Idiom
import com.example.data.model.IdiomCategory
import com.example.data.model.IdiomLevel
import kotlinx.serialization.json.Json

object IdiomRepository {
    private var _idioms: List<Idiom> = emptyList()
    
    val idioms: List<Idiom>
        get() = _idioms

    fun initialize(context: Context) {
        if (_idioms.isNotEmpty()) return
        try {
            val jsonString = context.assets.open("idioms.json").bufferedReader().use { it.readText() }
            val jsonDecoder = Json { 
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
            _idioms = jsonDecoder.decodeFromString<List<Idiom>>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            // Robust fallback to ensure the app never crashes
            _idioms = listOf(
                Idiom(
                    id = "break_the_ice",
                    expression = "Break the ice",
                    simpleMeaning = "Start a friendly conversation.",
                    urduMeaning = "گفتگو کو خوشگوار انداز میں شروع کرنا",
                    arabicMeaning = "كسر الجليد / بدء الحديث بطريقة ودية",
                    arabicType = ArabicEquivalentType.MEANING,
                    example = "The new teacher smiled to break the ice.",
                    level = IdiomLevel.BEGINNER,
                    category = IdiomCategory.DAILY_CONVERSATION
                ),
                Idiom(
                    id = "piece_of_cake",
                    expression = "A piece of cake",
                    simpleMeaning = "Very easy.",
                    urduMeaning = "بہت آسان کام",
                    arabicMeaning = "أمر في غاية السهولة",
                    arabicType = ArabicEquivalentType.MEANING,
                    example = "Don't worry about the test, it was a piece of cake.",
                    level = IdiomLevel.BEGINNER,
                    category = IdiomCategory.SUCCESS_FAILURE
                )
            )
        }
    }
}
