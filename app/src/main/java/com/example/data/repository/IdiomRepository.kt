package com.example.data.repository

import android.content.Context
import com.example.data.model.ArabicEquivalentType
import com.example.data.model.Idiom
import com.example.data.model.IdiomCategory
import com.example.data.model.IdiomLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

object IdiomRepository {
    private val _idiomsState = MutableStateFlow<List<Idiom>>(emptyList())
    val idiomsFlow: StateFlow<List<Idiom>> = _idiomsState.asStateFlow()
    
    val idioms: List<Idiom>
        get() = _idiomsState.value

    fun initialize(context: Context) {
        if (_idiomsState.value.isNotEmpty()) return
        try {
            val jsonString = context.assets.open("idioms.json").bufferedReader().use { it.readText() }
            val jsonDecoder = Json { 
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
            val list = jsonDecoder.decodeFromString<List<Idiom>>(jsonString)
            _idiomsState.value = list
        } catch (e: Exception) {
            e.printStackTrace()
            // Robust fallback to ensure the app never crashes
            _idiomsState.value = listOf(
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

    fun getPopularToday5(): List<Idiom> {
        val all = _idiomsState.value
        if (all.isEmpty()) return emptyList()
        val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        val count = all.size
        val startIndex = (dayOfYear * 5) % count
        val list = mutableListOf<Idiom>()
        for (i in 0 until 5) {
            val idx = (startIndex + i) % count
            list.add(all[idx])
        }
        return list
    }
}
