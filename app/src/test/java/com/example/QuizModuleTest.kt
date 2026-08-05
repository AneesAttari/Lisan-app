package com.example

import com.example.data.model.QuizData
import com.example.data.model.QuizQuestion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizModuleTest {

    @Test
    fun `test quiz question bank contains all tenses`() {
        val questions = QuizData.questions
        assertTrue("Question bank should have at least 12 questions", questions.size >= 12)

        val categories = questions.map { it.category }.toSet()
        assertTrue("Should cover PRESENT category", categories.contains("PRESENT"))
        assertTrue("Should cover PAST category", categories.contains("PAST"))
        assertTrue("Should cover FUTURE category", categories.contains("FUTURE"))

        questions.forEach { question ->
            assertNotNull(question.questionText)
            assertNotNull(question.arabicTranslation)
            assertEquals("Each question must have 4 options", 4, question.options.size)
            assertTrue("Correct option index must be valid 0..3", question.correctOptionIndex in 0..3)
            assertTrue("Explanation in Urdu should contain Urdu text", question.explanationUr.isNotEmpty())
        }
    }

    @Test
    fun `test curriculum topics count and categories`() {
        val topics = com.example.data.model.CurriculumData.topics
        assertTrue("Curriculum should have topics", topics.isNotEmpty())
        topics.forEach { topic ->
            assertNotNull(topic.id)
            assertNotNull(topic.titleEn)
            assertNotNull(topic.titleUr)
        }
    }
}
