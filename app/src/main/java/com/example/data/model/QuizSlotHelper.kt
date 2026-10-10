package com.example.data.model

object QuizSlotHelper {
    data class SentenceVariant(
        val questionText: String,
        val translationUr: String,
        val correctAnswer: String,
        val distractors: List<String>
    )

    private val CONTEXT_PREFIXES = listOf(
        Pair("", ""), // Occurrence 0: Original text without prefix
        Pair("In the initial assessment, ", "ابتدائی جائزے میں، "),
        Pair("According to official records, ", "سرکاری ریکارڈ کے مطابق، "),
        Pair("During the morning session, ", "صبح کے سیشن کے دوران، "),
        Pair("As noted in the guidelines, ", "رہنما خطوط میں درج ذیل، "),
        Pair("In recent observations, ", "حالیہ مشاہدات میں، "),
        Pair("During the technical review, ", "تکنیکی جائزے کے دوران، "),
        Pair("Under standard procedures, ", "معیاری طریقہ کار کے تحت، "),
        Pair("In the secondary audit, ", "ثانوی آڈٹ میں، "),
        Pair("According to project reports, ", "پروجیکٹ رپورٹ کے مطابق، "),
        Pair("During field evaluation, ", "فیلڈ کی تشخیص کے دوران، "),
        Pair("As highlighted by experts, ", "ماہرین کی نشاندہی کے مطابق، ")
    )

    private fun formatWithPrefix(prefix: String, text: String): String {
        if (prefix.isEmpty()) return text
        val words = text.split(" ", limit = 2)
        val firstWord = words[0]
        val rest = if (words.size > 1) " " + words[1] else ""
        val commonCapitalizedWords = setOf(
            "The", "A", "An", "This", "That", "These", "Those",
            "He", "She", "It", "They", "We", "You", "Listen", "Look"
        )
        val formattedFirstWord = if (commonCapitalizedWords.contains(firstWord)) {
            firstWord.lowercase()
        } else {
            firstWord
        }
        return prefix + formattedFirstWord + rest
    }

    fun createTemplate(
        templateId: String,
        slotIndex: Int,
        grammarTopic: String,
        subtopic: String,
        difficulty: String = "Intermediate",
        questionType: String = "Fill-in-the-blank",
        structuralFingerprint: String,
        category: String,
        tenseId: String,
        tenseTitleEn: String,
        tenseTitleAr: String,
        explanationEn: String,
        explanationUr: String,
        commonMistake: String,
        variants: List<SentenceVariant>
    ): DailyQuiz365Generator.QuestionTemplate {
        return DailyQuiz365Generator.QuestionTemplate(
            templateId = templateId,
            slotIndex = slotIndex,
            grammarTopic = grammarTopic,
            subtopic = subtopic,
            difficulty = difficulty,
            questionType = questionType,
            structuralFingerprint = structuralFingerprint,
            sentenceBuilder = { safeDay ->
                val templateIndex = (safeDay - 1 + slotIndex * 17) % 36
                val R = ((templateIndex - slotIndex * 17) % 36 + 36) % 36
                val d0 = R + 1
                val k = ((safeDay - d0) / 36).coerceAtLeast(0)

                val variantIndex = k % variants.size
                val variant = variants[variantIndex]

                val prefixPair = CONTEXT_PREFIXES[k % CONTEXT_PREFIXES.size]
                val formattedEn = formatWithPrefix(prefixPair.first, variant.questionText)
                val formattedUr = prefixPair.second + variant.translationUr

                DailyQuiz365Generator.QuestionPayload(
                    questionText = formattedEn,
                    translationUr = formattedUr,
                    correctAnswer = variant.correctAnswer,
                    distractors = variant.distractors,
                    explanationEn = explanationEn,
                    explanationUr = explanationUr,
                    commonMistake = commonMistake,
                    category = category,
                    tenseId = tenseId,
                    tenseTitleEn = tenseTitleEn,
                    tenseTitleAr = tenseTitleAr
                )
            }
        )
    }
}
