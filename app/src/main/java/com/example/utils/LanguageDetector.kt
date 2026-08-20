package com.example.utils

import java.util.Locale

enum class DetectedLanguage(val label: String, val defaultResponseLang: String) {
    ENGLISH("English", "English"),
    ROMAN_URDU("Roman Urdu", "Urdu"),
    URDU_SCRIPT("Urdu", "Urdu"),
    ARABIC_SCRIPT("Arabic", "Arabic")
}

data class InstructionIntent(
    val requestedLanguage: String, // "Urdu", "English", "Arabic", "Custom", or "Auto"
    val isStrictSingleLanguage: Boolean = false,
    val mixedDetails: String? = null,
    val customLanguageName: String? = null,
    val isExplicitInstructionPresent: Boolean = false
) {
    fun toSystemPromptDirective(): String {
        if (!isExplicitInstructionPresent) return ""

        return when {
            mixedDetails != null -> """
                [CRITICAL ACTIVE USER INSTRUCTION - MIXED LANGUAGE FORMAT]
                The learner explicitly instructed: $mixedDetails
                You MUST follow this format directive strictly.
            """.trimIndent()

            requestedLanguage == "Urdu" && isStrictSingleLanguage -> """
                [CRITICAL ACTIVE USER INSTRUCTION - STRICT URDU ONLY]
                - The learner explicitly commanded: EXPLAIN ONLY IN URDU (صرف اردو میں سمجھاؤ).
                - You MUST generate the ENTIRE response strictly in Urdu script (اردو نستعلیق رسم الخط).
                - CRITICAL PERSONA & CONSTRAINTS:
                  1. FEMALE TUTOR PERSONA: As a female teacher (خاتون ٹیچر), use FEMININE self-reference in Urdu (e.g., "پڑھاؤں گی", "سکھاؤں گی", "سمجھاؤں گی", "بتاؤں گی", "کروں گی", "میں آپ کی ٹیچر ہوں"). NEVER use masculine forms like "پڑھاؤں گا", "سکھاؤں گا", "سمجھاؤں گا", "بتاؤں گا", "کروں گا", or "استاد ہوں".
                  2. ABSOLUTELY NO HINDI SCRIPT (देवनागरी / Devanagari): You MUST write in genuine Urdu script (نستعلیق). NEVER output Hindi script (Devanagari) under any circumstances.
                  3. ABSOLUTELY NO ENGLISH EXPLANATIONS OR TEXT (unless the learner explicitly asks for English word translations).
                  4. ABSOLUTELY NO ENGLISH HEADINGS. Use Urdu headings with emojis (e.g. 💡 وضاحت, 📌 اہم اصول, 📝 مثالیں, 🤔 خود کوشش کریں).
                  5. DO NOT automatically provide English translations, English headings, or English summaries.
                  6. DO NOT switch back to English or Arabic.
                  7. Maintain all friendly teaching tone, emojis, structured sections, and educational quality, but present EVERYTHING in Urdu script.
            """.trimIndent()

            requestedLanguage == "English" && isStrictSingleLanguage -> """
                [CRITICAL ACTIVE USER INSTRUCTION - STRICT ENGLISH ONLY]
                - The learner explicitly commanded: EXPLAIN ONLY IN ENGLISH.
                - You MUST generate the ENTIRE response strictly in English.
                - CRITICAL CONSTRAINTS:
                  1. ABSOLUTELY NO URDU OR ARABIC TRANSLATIONS OR TEXT unless explicitly requested by the learner.
                  2. DO NOT automatically add Arabic or Urdu comparison blocks.
                  3. Maintain all friendly teaching tone, emojis, structured sections, and educational quality, but present EVERYTHING in English.
            """.trimIndent()

            requestedLanguage == "Arabic" && isStrictSingleLanguage -> """
                [CRITICAL ACTIVE USER INSTRUCTION - STRICT ARABIC ONLY]
                - The learner explicitly commanded: EXPLAIN ONLY IN ARABIC (صرف باللغة العربية).
                - You MUST generate the ENTIRE response strictly in Modern Standard Arabic script (اللغة العربية الفصحى).
                - CRITICAL CONSTRAINTS:
                  1. ABSOLUTELY NO ENGLISH OR URDU EXPLANATIONS OR HEADINGS. Use Arabic headings paired with emojis (e.g. 💡 الشرح, 📌 قاعدة مهمة, 📝 الأمثلة, 🤔 جرب بنفسك).
                  2. Maintain all friendly teaching tone, emojis, structured sections, and educational quality, but present EVERYTHING in Arabic script.
            """.trimIndent()

            requestedLanguage == "Urdu" -> """
                [ACTIVE USER INSTRUCTION - RESPONSE LANGUAGE: URDU]
                - The learner requested response in Urdu (اردو میں).
                - Provide core explanations and guidance in Urdu script (اردو نستعلیق).
                - FEMALE TUTOR PERSONA: As a female teacher (خاتون ٹیچر), use FEMININE self-reference in Urdu (e.g., "پڑھاؤں گی", "سکھاؤں گی", "سمجھاؤں گی", "بتاؤں گی", "کروں گی", "میں آپ کی ٹیچر ہوں"). NEVER use masculine forms like "پڑھاؤں گا", "سکھاؤں گا", "سمجھاؤں گا", "بتاؤں گا", "کروں گا", or "استاد ہوں".
                - ABSOLUTELY NO HINDI SCRIPT (देवناगरी / Devanagari): NEVER output Hindi script under any circumstances.
            """.trimIndent()

            requestedLanguage == "English" -> """
                [ACTIVE USER INSTRUCTION - RESPONSE LANGUAGE: ENGLISH]
                - The learner requested response in English.
                - Provide core explanations and guidance in clear English.
            """.trimIndent()

            requestedLanguage == "Arabic" -> """
                [ACTIVE USER INSTRUCTION - RESPONSE LANGUAGE: ARABIC]
                - The learner requested response in Arabic (باللغة العربية).
                - Provide core explanations and guidance in Arabic script.
            """.trimIndent()

            requestedLanguage == "Custom" && customLanguageName != null -> """
                [ACTIVE USER INSTRUCTION - RESPONSE LANGUAGE: $customLanguageName]
                - The learner requested response in $customLanguageName.
                - Provide core explanations and guidance in $customLanguageName.
            """.trimIndent()

            else -> ""
        }
    }
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

    /**
     * Identifies intent of explicit user instructions regarding response language, format, or restrictions.
     */
    fun detectInstructionIntent(text: String): InstructionIntent {
        val cleanText = text.trim()
        if (cleanText.isEmpty()) {
            return InstructionIntent(requestedLanguage = "Auto", isExplicitInstructionPresent = false)
        }

        val lowerText = cleanText.lowercase(Locale.ROOT)

        // Exclusivity / Single Language Markers
        val exclusivityTokens = listOf(
            "sirf", "srf", "khali", "only", "just", "strictly", "purely", "solely",
            "without english", "no english", "without urdu", "no urdu", "without arabic", "no arabic",
            "english ke bina", "english bina", "english k bina", "urdu ke bina",
            "صرف", "فقط", "محض", "مجرد", "بدون", "بلا"
        )
        val hasExclusivityMarker = exclusivityTokens.any { lowerText.contains(it) }

        // Instruction Verbs / Action Triggers
        val instructionVerbs = listOf(
            "samjhao", "samjhayein", "samjhaen", "samjhado", "batao", "batayein",
            "explain", "speak", "write", "tell", "teach", "translate", "response", "reply",
            "سمجھاؤ", "سمجھائیں", "سمجھادیں", "بتاؤ", "وضاحت", "تشرىح", "اشرح", "تكلم", "اكتب", "ترجم"
        )
        val hasInstructionVerb = instructionVerbs.any { lowerText.contains(it) }

        // Check for Mixed Instructions (e.g. "Urdu mein explain karo aur examples English mein do")
        val mentionsUrdu = lowerText.contains("urdu") || lowerText.contains("urdoo") || lowerText.contains("اردو")
        val mentionsEnglish = lowerText.contains("english") || lowerText.contains("inglish") || lowerText.contains("angrezi") || lowerText.contains("انگریزی") || lowerText.contains("انگلیش")
        val mentionsArabic = lowerText.contains("arabic") || lowerText.contains("arbi") || lowerText.contains("عربی") || lowerText.contains("العربية")
        val mentionsExample = lowerText.contains("example") || lowerText.contains("misal") || lowerText.contains("مثال")

        if (mentionsUrdu && mentionsEnglish && (hasInstructionVerb || mentionsExample)) {
            val isUrduExplainEnglishExamples = lowerText.contains("example") && (lowerText.indexOf("urdu") < lowerText.indexOf("english") || lowerText.contains("examples english"))
            if (isUrduExplainEnglishExamples) {
                return InstructionIntent(
                    requestedLanguage = "Urdu",
                    isStrictSingleLanguage = false,
                    mixedDetails = "Provide grammar explanations and descriptions in Urdu script (نستعلیق), but provide all example sentences and practice questions in English.",
                    isExplicitInstructionPresent = true
                )
            }
        }

        // 1. Urdu Instruction Detection
        if (mentionsUrdu) {
            val isExplicitUrdu = hasInstructionVerb || hasExclusivityMarker ||
                lowerText.contains("in urdu") || lowerText.contains("urdu mein") || lowerText.contains("urdu me") ||
                lowerText.contains("urdu main") || lowerText.contains("urdu mai") || lowerText.contains("urdu only") ||
                lowerText.contains("sirf urdu") || lowerText.contains("اردو میں") || lowerText.contains("صرف اردو") ||
                lowerText.contains("یہ اردو میں")

            if (isExplicitUrdu) {
                return InstructionIntent(
                    requestedLanguage = "Urdu",
                    isStrictSingleLanguage = hasExclusivityMarker || lowerText.contains("urdu only") || lowerText.contains("sirf urdu") || lowerText.contains("صرف اردو"),
                    isExplicitInstructionPresent = true
                )
            }
        }

        // Implicit Roman Urdu instruction trigger (e.g. "Sirf samjhao", "Urdu me samjha dein", "Mujhe samjhao")
        val isRomanUrduPhrase = lowerText.contains("samjhao") || lowerText.contains("samjhayein") || lowerText.contains("samjhado") || lowerText.contains("samjha dein") || lowerText.contains("سمجھاؤ")
        if (isRomanUrduPhrase && (hasExclusivityMarker || lowerText.contains("me ") || lowerText.contains("mein ") || lowerText.contains("main ") || lowerText.contains("میں"))) {
            return InstructionIntent(
                requestedLanguage = "Urdu",
                isStrictSingleLanguage = hasExclusivityMarker,
                isExplicitInstructionPresent = true
            )
        }

        // 2. English Instruction Detection
        if (mentionsEnglish) {
            val isExplicitEnglish = hasInstructionVerb || hasExclusivityMarker ||
                lowerText.contains("in english") || lowerText.contains("english mein") || lowerText.contains("english me") ||
                lowerText.contains("english main") || lowerText.contains("english only") || lowerText.contains("sirf english") ||
                lowerText.contains("انگریزی میں") || lowerText.contains("صرف انگریزی")

            if (isExplicitEnglish) {
                return InstructionIntent(
                    requestedLanguage = "English",
                    isStrictSingleLanguage = hasExclusivityMarker || lowerText.contains("english only") || lowerText.contains("sirf english") || lowerText.contains("only in english"),
                    isExplicitInstructionPresent = true
                )
            }
        }

        // 3. Arabic Instruction Detection
        if (mentionsArabic) {
            val isExplicitArabic = hasInstructionVerb || hasExclusivityMarker ||
                lowerText.contains("in arabic") || lowerText.contains("arabic mein") || lowerText.contains("arabic me") ||
                lowerText.contains("arabic only") || lowerText.contains("بالعربية") || lowerText.contains("عربی میں") || lowerText.contains("صرف عربی")

            if (isExplicitArabic) {
                return InstructionIntent(
                    requestedLanguage = "Arabic",
                    isStrictSingleLanguage = hasExclusivityMarker || lowerText.contains("arabic only") || lowerText.contains("بالعربية فقط") || lowerText.contains("صرف عربی"),
                    isExplicitInstructionPresent = true
                )
            }
        }

        // 4. Custom Language Detection (e.g. French, Spanish, German, Hindi)
        val customLanguages = mapOf(
            "french" to "French", "فرانسیسی" to "French",
            "spanish" to "Spanish", "اسپینی" to "Spanish",
            "german" to "German", "جرمن" to "German",
            "hindi" to "Hindi", "ہندی" to "Hindi"
        )
        for ((key, name) in customLanguages) {
            if (lowerText.contains(key) && (hasInstructionVerb || hasExclusivityMarker || lowerText.contains("in $key"))) {
                return InstructionIntent(
                    requestedLanguage = "Custom",
                    customLanguageName = name,
                    isStrictSingleLanguage = hasExclusivityMarker,
                    isExplicitInstructionPresent = true
                )
            }
        }

        return InstructionIntent(requestedLanguage = "Auto", isExplicitInstructionPresent = false)
    }

    /**
     * Resolves active instruction intent by scanning conversation history from most recent to oldest user turn.
     * Keeps the active preference persistent across turns until updated by a new explicit instruction.
     */
    fun resolveActiveInstructionIntent(
        history: List<com.example.data.model.Content>,
        fallbackSelectedLanguage: String
    ): InstructionIntent {
        val userMessages = history.filter { it.role == "user" }
        for (msg in userMessages.reversed()) {
            val text = msg.parts.firstOrNull()?.text ?: continue
            val intent = detectInstructionIntent(text)
            if (intent.isExplicitInstructionPresent) {
                return intent
            }
        }

        val lastMsgText = userMessages.lastOrNull()?.parts?.firstOrNull()?.text ?: ""
        val detected = detect(lastMsgText)
        val normMode = fallbackSelectedLanguage.trim()

        val reqLang = when {
            normMode.equals("English", ignoreCase = true) -> "English"
            normMode.equals("Urdu", ignoreCase = true) || normMode == "اردو" -> "Urdu"
            normMode.equals("Arabic", ignoreCase = true) || normMode == "العربية" -> "Arabic"
            else -> detected.defaultResponseLang
        }

        return InstructionIntent(
            requestedLanguage = reqLang,
            isStrictSingleLanguage = false,
            isExplicitInstructionPresent = false
        )
    }

    fun resolveEffectiveResponseLanguage(
        selectedMode: String, // "Auto", "English", "Urdu", "Arabic" or "اردو", "العربية"
        inputPrompt: String
    ): Pair<DetectedLanguage, String> {
        val detected = detect(inputPrompt)
        val intent = detectInstructionIntent(inputPrompt)

        val effectiveLang = if (intent.isExplicitInstructionPresent) {
            if (intent.requestedLanguage == "Custom") {
                intent.customLanguageName ?: "English"
            } else {
                intent.requestedLanguage
            }
        } else {
            val normMode = selectedMode.trim()
            when {
                normMode.equals("English", ignoreCase = true) -> "English"
                normMode.equals("Urdu", ignoreCase = true) || normMode == "اردو" -> "Urdu"
                normMode.equals("Arabic", ignoreCase = true) || normMode == "العربية" -> "Arabic"
                else -> detected.defaultResponseLang
            }
        }

        return Pair(detected, effectiveLang)
    }
}

