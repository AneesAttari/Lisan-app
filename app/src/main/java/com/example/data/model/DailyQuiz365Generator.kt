package com.example.data.model

import com.example.data.model.quizslots.*
import java.util.Random

/**
 * 365-Day Daily Quiz Engine
 *
 * Provides 365 days × 10 questions = 3,650 unique, high-quality MCQs.
 * Each calendar day (1..365) is composed of 10 distinct grammatical pillars (slots 0..9),
 * ensuring 10 completely distinct structural fingerprints per day (0 structural duplicates),
 * deterministic selection based on day number, varied correct option positions (0..3),
 * rich English & Urdu explanations, and common mistake notes.
 */
object DailyQuiz365Generator {

    // Definition of a structural template within the generator
    data class QuestionTemplate(
        val templateId: String,
        val slotIndex: Int,
        val grammarTopic: String,
        val subtopic: String,
        val difficulty: String,
        val questionType: String,
        val structuralFingerprint: String,
        val sentenceBuilder: (variantIndex: Int) -> QuestionPayload
    )

    data class QuestionPayload(
        val questionText: String,
        val translationUr: String,
        val correctAnswer: String,
        val distractors: List<String>,
        val explanationEn: String,
        val explanationUr: String,
        val commonMistake: String,
        val category: String, // "PRESENT", "PAST", "FUTURE"
        val tenseId: String,
        val tenseTitleEn: String,
        val tenseTitleAr: String
    )

    // Modular slot pools (10 slots × 36 unique templates = 360 templates)
    val slot0Tenses: List<QuestionTemplate> by lazy { Slot0Tenses.templates }
    val slot1Modals: List<QuestionTemplate> by lazy { Slot1Modals.templates }
    val slot2Voice: List<QuestionTemplate> by lazy { Slot2Voice.templates }
    val slot3Conditionals: List<QuestionTemplate> by lazy { Slot3Conditionals.templates }
    val slot4ReportedSpeech: List<QuestionTemplate> by lazy { Slot4ReportedSpeech.templates }
    val slot5GerundsInfinitives: List<QuestionTemplate> by lazy { Slot5Relatives.templates }
    val slot6ArticlesQuantifiers: List<QuestionTemplate> by lazy { Slot6InversionFocus.templates }
    val slot7PrepositionsPhrasalVerbs: List<QuestionTemplate> by lazy { Slot7InfinitivesGerunds.templates }
    val slot8PronounsAgreement: List<QuestionTemplate> by lazy { Slot8ArticlesPrepositions.templates }
    val slot9TransformationConnectors: List<QuestionTemplate> by lazy { Slot9TransformationConnectors.templates }

    /**
     * Retrieves the 10 MCQs for a specified day (1..365).
     */
    fun getQuestionsForDay(dayNumber: Int): List<QuizQuestion> {
        val safeDay = ((dayNumber - 1) % 365) + 1
        val questions = mutableListOf<QuizQuestion>()

        // 10 distinct slots for the 10 daily questions
        val slots = listOf(
            slot0Tenses,
            slot1Modals,
            slot2Voice,
            slot3Conditionals,
            slot4ReportedSpeech,
            slot5GerundsInfinitives,
            slot6ArticlesQuantifiers,
            slot7PrepositionsPhrasalVerbs,
            slot8PronounsAgreement,
            slot9TransformationConnectors
        )

        for (slotIndex in 0 until 10) {
            val templatePool = slots[slotIndex]
            if (templatePool.isEmpty()) continue

            // Deterministic template selection for this slot on this day
            val templateIndex = (safeDay - 1 + slotIndex * 17) % templatePool.size
            val template = templatePool[templateIndex]

            val payload = template.sentenceBuilder(safeDay)

            // Deterministically shuffle options so correct answer is not always index 0
            val optionSeed = (safeDay * 1000 + slotIndex * 37).toLong()
            val rng = Random(optionSeed)

            val rawOptions = mutableListOf(payload.correctAnswer).apply {
                addAll(payload.distractors.take(3))
            }
            // Ensure exactly 4 options
            while (rawOptions.size < 4) {
                rawOptions.add("Option ${rawOptions.size + 1}")
            }

            // Deterministic shuffle
            val shuffledOptions = rawOptions.distinct().toMutableList()
            while (shuffledOptions.size < 4) {
                shuffledOptions.add("Distractor ${shuffledOptions.size + 1}")
            }
            // Permute options
            for (i in shuffledOptions.indices.reversed()) {
                val j = rng.nextInt(i + 1)
                val temp = shuffledOptions[i]
                shuffledOptions[i] = shuffledOptions[j]
                shuffledOptions[j] = temp
            }

            val correctIdx = shuffledOptions.indexOf(payload.correctAnswer).coerceAtLeast(0)

            val rawQuestion = QuizQuestion(
                id = "d${safeDay}_q${slotIndex + 1}_${template.templateId}",
                tenseId = payload.tenseId,
                tenseTitleEn = "Day $safeDay • ${payload.tenseTitleEn}",
                tenseTitleAr = "${payload.tenseTitleAr} (یوم $safeDay)",
                category = payload.category,
                level = "Day $safeDay • ${template.difficulty}",
                questionText = payload.questionText,
                arabicTranslation = payload.translationUr,
                options = shuffledOptions,
                correctOptionIndex = correctIdx,
                explanationEn = payload.explanationEn,
                explanationUr = payload.explanationUr,
                commonMistake = payload.commonMistake
            )

            val enriched = EducationalExplanationEngine.enrichQuestion(rawQuestion)
            questions.add(enriched)
        }

        return questions
    }

    /**
     * Computes debug diagnostics for a given day's quiz set.
     */
    fun getDiagnosticsForDay(dayNumber: Int): DailyQuizDiagnostics {
        val questions = getQuestionsForDay(dayNumber)
        val templateIds = questions.map { it.id.substringAfter("_q").substringAfter("_") }
        val topics = questions.map { it.tenseTitleEn }
        val fingerprints = mutableListOf<String>()

        val slots = listOf(
            slot0Tenses, slot1Modals, slot2Voice, slot3Conditionals,
            slot4ReportedSpeech, slot5GerundsInfinitives, slot6ArticlesQuantifiers,
            slot7PrepositionsPhrasalVerbs, slot8PronounsAgreement, slot9TransformationConnectors
        )

        for (slotIndex in 0 until 10) {
            val templatePool = slots[slotIndex]
            val safeDay = ((dayNumber - 1) % 365) + 1
            val templateIndex = (safeDay - 1 + slotIndex * 17) % templatePool.size
            fingerprints.add(templatePool[templateIndex].structuralFingerprint)
        }

        val duplicates = fingerprints.size - fingerprints.distinct().size

        // Verify determinism
        val run1 = getQuestionsForDay(dayNumber)
        val run2 = getQuestionsForDay(dayNumber)
        val isDet = run1 == run2

        return DailyQuizDiagnostics(
            dayNumber = dayNumber,
            questionCount = questions.size,
            templateIds = templateIds,
            grammarTopics = topics,
            structuralFingerprints = fingerprints,
            intraDayStructuralDuplicates = duplicates,
            isDeterministic = isDet
        )
    }
}
