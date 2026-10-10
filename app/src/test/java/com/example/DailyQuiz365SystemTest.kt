package com.example

import com.example.data.model.DailyQuiz365Generator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DailyQuiz365SystemTest {

    @Test
    fun test365DaysFullCoverageAndIntegrity() {
        var totalQuestionsCount = 0
        val correctIndexCounts = mutableMapOf<Int, Int>()

        for (day in 1..365) {
            val questions = DailyQuiz365Generator.getQuestionsForDay(day)
            assertEquals("Day $day must have exactly 10 questions", 10, questions.size)
            totalQuestionsCount += questions.size

            // Intra-day question ID uniqueness
            val ids = questions.map { it.id }
            assertEquals("Day $day question IDs must be strictly unique", 10, ids.distinct().size)

            questions.forEachIndexed { index, q ->
                assertNotNull("Question $index on day $day must have text", q.questionText)
                assertTrue("Question text on day $day must not be blank", q.questionText.isNotBlank())
                assertTrue("Urdu translation on day $day must not be blank", q.arabicTranslation.isNotBlank())
                assertEquals("Question on day $day must have exactly 4 options", 4, q.options.size)
                assertEquals("Options on day $day must be unique within question", 4, q.options.distinct().size)
                assertTrue("Correct option index must be in 0..3", q.correctOptionIndex in 0..3)
                assertTrue("English explanation must not be blank", q.explanationEn.isNotBlank())
                assertTrue("Urdu explanation must not be blank", q.explanationUr.isNotBlank())

                val idx = q.correctOptionIndex
                correctIndexCounts[idx] = (correctIndexCounts[idx] ?: 0) + 1
            }
        }

        assertEquals("Total questions generated for 365 days must equal 3,650", 3650, totalQuestionsCount)

        // Verify correct answer positions are varied across 0, 1, 2, 3
        for (i in 0..3) {
            val count = correctIndexCounts[i] ?: 0
            assertTrue("Option index $i must appear as correct answer at least 400 times across 3,650 questions", count > 400)
        }
    }

    @Test
    fun testIntraDayStructuralDiversityAndZeroDuplicates() {
        for (day in 1..365) {
            val diag = DailyQuiz365Generator.getDiagnosticsForDay(day)
            assertEquals("Day $day must report 10 questions in diagnostics", 10, diag.questionCount)
            assertEquals("Day $day must have 0 intra-day structural duplicates", 0, diag.intraDayStructuralDuplicates)
            assertEquals("Day $day must have 10 distinct structural fingerprints", 10, diag.structuralFingerprints.distinct().size)
            assertTrue("Day $day must be strictly deterministic", diag.isDeterministic)
        }
    }

    @Test
    fun testGenerationDeterminism() {
        for (day in listOf(1, 15, 73, 150, 260, 365)) {
            val run1 = DailyQuiz365Generator.getQuestionsForDay(day)
            val run2 = DailyQuiz365Generator.getQuestionsForDay(day)
            assertEquals("Day $day generation must be strictly deterministic across calls", run1, run2)
        }
    }

    @Test
    fun testConsecutiveDayDistinctness() {
        for (day in 1..364) {
            val dayQuestions = DailyQuiz365Generator.getQuestionsForDay(day)
            val nextDayQuestions = DailyQuiz365Generator.getQuestionsForDay(day + 1)
            assertNotEquals("Day $day and Day ${day + 1} must produce distinct question sets", dayQuestions, nextDayQuestions)
            val dayIds = dayQuestions.map { it.id }.toSet()
            val nextDayIds = nextDayQuestions.map { it.id }.toSet()
            assertTrue("Consecutive days must have distinct question IDs", dayIds.intersect(nextDayIds).isEmpty())
        }
    }

    @Test
    fun testCrossDayGrammarDiversityAndSystemIntegrity() {
        val allQuestions = mutableListOf<com.example.data.model.QuizQuestion>()
        val fingerprintsByDay = mutableMapOf<Int, List<String>>()
        val templateIdsByDay = mutableMapOf<Int, List<String>>()
        val allFingerprints = mutableListOf<String>()
        val allTemplateIds = mutableListOf<String>()
        val allQuestionTypePatternCombos = mutableListOf<String>()

        for (day in 1..365) {
            val questions = DailyQuiz365Generator.getQuestionsForDay(day)
            val diag = DailyQuiz365Generator.getDiagnosticsForDay(day)

            allQuestions.addAll(questions)
            fingerprintsByDay[day] = diag.structuralFingerprints
            templateIdsByDay[day] = diag.templateIds
            allFingerprints.addAll(diag.structuralFingerprints)
            allTemplateIds.addAll(diag.templateIds)

            questions.forEach { q ->
                allQuestionTypePatternCombos.add("Fill-in-the-blank:${q.tenseId}")
            }
        }

        // 1. Total exact-question duplicates across all 3,650 questions
        val uniqueQuestionTexts = allQuestions.map { it.questionText }.distinct()
        val exactQuestionDuplicates = allQuestions.size - uniqueQuestionTexts.size

        // 2. Total structural-fingerprint duplicates across different days
        val uniqueFingerprints = allFingerprints.distinct()
        val totalFingerprintOccurrences = allFingerprints.size
        val totalCrossDayFingerprintDuplicates = totalFingerprintOccurrences - uniqueFingerprints.size

        // 3. Number of unique structural fingerprints
        val uniqueFingerprintCount = uniqueFingerprints.size

        // 4. Number of unique grammar-template IDs
        val uniqueTemplateIdCount = allTemplateIds.distinct().size

        // 5. Number of unique question-type + grammar-pattern combinations
        val uniqueTypePatternCount = allQuestionTypePatternCombos.distinct().size

        // 6. Maximum number of times any single structural fingerprint occurs
        val fingerprintCounts = allFingerprints.groupingBy { it }.eachCount()
        val maxFingerprintFrequency = fingerprintCounts.values.maxOrNull() ?: 0

        // 7. Maximum number of consecutive days using the same structural fingerprint (per slot)
        var maxConsecutiveSameFingerprintInSlot = 0
        for (slotIndex in 0 until 10) {
            var currentStreak = 1
            var maxStreak = 1
            for (day in 2..365) {
                val prevFp = fingerprintsByDay[day - 1]!![slotIndex]
                val currFp = fingerprintsByDay[day]!![slotIndex]
                if (prevFp == currFp) {
                    currentStreak++
                    if (currentStreak > maxStreak) maxStreak = currentStreak
                } else {
                    currentStreak = 1
                }
            }
            if (maxStreak > maxConsecutiveSameFingerprintInSlot) {
                maxConsecutiveSameFingerprintInSlot = maxStreak
            }
        }

        // Output metrics report to console
        println("=== CROSS-DAY GRAMMAR DIVERSITY REPORT ===")
        println("Total Questions Generated: ${allQuestions.size}")
        println("1. Total Exact-Question Duplicates: $exactQuestionDuplicates")
        println("2. Total Cross-Day Fingerprint Duplicates: $totalCrossDayFingerprintDuplicates")
        println("3. Number of Unique Structural Fingerprints: $uniqueFingerprintCount")
        println("4. Number of Unique Grammar-Template IDs: $uniqueTemplateIdCount")
        println("5. Number of Unique Question-Type + Grammar-Pattern Combinations: $uniqueTypePatternCount")
        println("6. Maximum Occurrences of Any Single Structural Fingerprint: $maxFingerprintFrequency")
        println("7. Maximum Consecutive Days with Same Fingerprint in Same Slot: $maxConsecutiveSameFingerprintInSlot")
        println("==========================================")

        // Assertions
        assertEquals("Total questions must be 3,650", 3650, allQuestions.size)
        assertEquals("There should be 0 exact duplicate question texts across 3,650 questions", 0, exactQuestionDuplicates)
        assertEquals("There should be 3,650 unique question texts across 3,650 questions", 3650, uniqueQuestionTexts.size)
        assertEquals("There should be 360 unique structural fingerprints across slots", 360, uniqueFingerprintCount)
        assertEquals("There should be 360 unique grammar-template IDs", 360, uniqueTemplateIdCount)
        assertTrue("Max occurrences of any single structural fingerprint should be <= 11 across 365 days", maxFingerprintFrequency <= 11)
        assertEquals("Max consecutive days with same fingerprint in same slot should be 1", 1, maxConsecutiveSameFingerprintInSlot)
    }
}
