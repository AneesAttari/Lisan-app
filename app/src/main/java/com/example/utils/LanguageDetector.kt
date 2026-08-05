package com.example.utils

import java.util.Locale

enum class DetectedLanguage(val label: String, val defaultResponseLang: String) {
    ENGLISH("English", "English"),
    ROMAN_URDU("Roman Urdu", "Urdu"),
    URDU_SCRIPT("Urdu", "Urdu"),
    ARABIC_SCRIPT("Arabic", "Arabic")
}

object LanguageDetector {

    private val ROMAN_URDU_WORDS = setOf(
        "mujhe", "mujhay", "mujko", "muj", "mujh",
        "samjhao", "samjha", "samajh", "samajhe", "samjhayein", "samjhado",
        "kya", "kyun", "kyon", "kkyun",
        "kaise", "kaisay", "kese", "kesay",
        "hai", "hain", "hoon", "hun", "ho",
        "tha", "thi", "the",
        "karna", "karo", "karein", "karen", "karr", "karne",
        "batao", "batayein", "batayen", "bata", "bataen",
        "mera", "meri", "mere", "mereko",
        "hum", "humne", "humaara", "humari",
        "aap", "tum", "tumhein", "tumhain", "tu",
        "acha", "achha", "accha", "theek", "thik",
        "ye", "yeh", "yi", "wo", "woh", "us", "is",
        "ka", "ki", "ke", "ko", "se", "say", "si", "me", "mein", "main", "par", "pe",
        "wala", "wali", "wale", "walay",
        "nahin", "nhi", "nahi", "na",
        "hoga", "hogi", "hogay", "hogaye",
        "suno", "bol", "bolo", "batan",
        "zaroor", "zarur", "zaroori", "shukriya", "shukria",
        "dono", "donon", "alag", "farq", "phark", "karenge",
        "parhao", "sikhao", "samjhado", "bataen"
    )

    private val ROMAN_URDU_PHRASES = listOf(
        "samajh nahi aaya", "samajh nahi", "samajh nhe",
        "ka difference batao", "ka matlab kya hai", "ye kya hai", "kya matlab",
        "difference batao", "mujhe batao", "mujhe samjhao", "please mujhe",
        "samjhao", "kaise use", "kaise hota hai", "kyun hota hai",
        "ka matlab", "in urdu", "urdoo me", "urdu mein", "kaise banta hai",
        "bata do", "bataen"
    )

    fun detect(text: String): DetectedLanguage {
        val cleanText = text.trim()
        if (cleanText.isEmpty()) return DetectedLanguage.ENGLISH

        // 1. Check for Arabic/Urdu Script (Unicode range \u0600-\u06FF)
        var scriptCharCount = 0
        var urduSpecificCharCount = 0
        for (char in cleanText) {
            val code = char.code
            if (code in 0x0600..0x06FF || code in 0x0750..0x077F || code in 0xFB50..0xFDFF || code in 0xFE70..0xFEFF) {
                scriptCharCount++
                if (char == 'پ' || char == 'چ' || char == 'ڈ' || char == 'ڑ' || char == 'ں' || char == 'ھ' || char == 'ے' || char == 'گ') {
                    urduSpecificCharCount++
                }
            }
        }

        if (scriptCharCount > 0 && scriptCharCount.toFloat() / cleanText.length > 0.25f) {
            return if (urduSpecificCharCount > 0) DetectedLanguage.URDU_SCRIPT else DetectedLanguage.ARABIC_SCRIPT
        }

        // 2. Check for Roman Urdu
        val lowerText = cleanText.lowercase(Locale.ROOT)

        for (phrase in ROMAN_URDU_PHRASES) {
            if (lowerText.contains(phrase)) {
                return DetectedLanguage.ROMAN_URDU
            }
        }

        val words = lowerText.split(Regex("[^a-zA-Z]+")).filter { it.isNotBlank() }
        var romanUrduWordCount = 0
        for (w in words) {
            if (ROMAN_URDU_WORDS.contains(w)) {
                romanUrduWordCount++
            }
        }

        if (words.isNotEmpty()) {
            val ratio = romanUrduWordCount.toFloat() / words.size
            if (romanUrduWordCount >= 2 || (romanUrduWordCount >= 1 && words.size <= 6) || ratio >= 0.2f) {
                return DetectedLanguage.ROMAN_URDU
            }
        }

        return DetectedLanguage.ENGLISH
    }

    fun resolveEffectiveResponseLanguage(
        selectedMode: String, // "Auto", "English", "Urdu", "Arabic" or "اردو", "العربية"
        inputPrompt: String
    ): Pair<DetectedLanguage, String> {
        val detected = detect(inputPrompt)

        val lowerPrompt = inputPrompt.lowercase(Locale.ROOT)
        val explicitLang: String? = when {
            lowerPrompt.contains("in english") || lowerPrompt.contains("english mein") || lowerPrompt.contains("english main") -> "English"
            lowerPrompt.contains("in urdu") || lowerPrompt.contains("urdu mein") || lowerPrompt.contains("urdu main") || lowerPrompt.contains("اردو میں") -> "Urdu"
            lowerPrompt.contains("in arabic") || lowerPrompt.contains("arabic mein") || lowerPrompt.contains("arabic main") || lowerPrompt.contains("بالعربية") -> "Arabic"
            else -> null
        }

        val normMode = selectedMode.trim()
        val effectiveLang = if (explicitLang != null) {
            explicitLang
        } else {
            when {
                normMode.equals("English", ignoreCase = true) -> "English"
                normMode.equals("Urdu", ignoreCase = true) || normMode == "اردو" -> "Urdu"
                normMode.equals("Arabic", ignoreCase = true) || normMode == "العربية" -> "Arabic"
                else -> { // Auto
                    detected.defaultResponseLang
                }
            }
        }

        return Pair(detected, effectiveLang)
    }
}
