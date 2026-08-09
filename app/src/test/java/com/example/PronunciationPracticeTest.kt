package com.example

import com.example.data.repository.PronunciationPracticeRepository
import org.junit.Assert.*
import org.junit.Test

class PronunciationPracticeTest {

    @Test
    fun testWordCountIsExactly100() {
        val words = PronunciationPracticeRepository.words
        assertEquals("Words category must have exactly 100 items", 100, words.size)
    }

    @Test
    fun testIdiomCountIsExactly100() {
        val idioms = PronunciationPracticeRepository.idioms
        assertEquals("Idioms category must have exactly 100 items", 100, idioms.size)
    }

    @Test
    fun testSentenceCountIsExactly100() {
        val sentences = PronunciationPracticeRepository.sentences
        assertEquals("Sentences category must have exactly 100 items", 100, sentences.size)
    }

    @Test
    fun testNoDuplicateIdsAcrossAllCategories() {
        val allItems = PronunciationPracticeRepository.words +
                PronunciationPracticeRepository.idioms +
                PronunciationPracticeRepository.sentences

        val ids = allItems.map { it.id }
        val uniqueIds = ids.toSet()

        assertEquals("All practice items must have globally unique IDs", ids.size, uniqueIds.size)
    }

    @Test
    fun testNoBlankContentOrEmptyFields() {
        val allItems = PronunciationPracticeRepository.words +
                PronunciationPracticeRepository.idioms +
                PronunciationPracticeRepository.sentences

        for (item in allItems) {
            assertFalse("ID should not be blank: ${item.id}", item.id.isBlank())
            assertFalse("Text should not be blank: ${item.id}", item.text.isBlank())
            assertFalse("Meaning should not be blank: ${item.id}", item.meaning.isBlank())
            assertFalse("Translation should not be blank: ${item.id}", item.translation.isBlank())
            assertFalse("Example should not be blank: ${item.id}", item.example.isBlank())
            assertFalse("Difficulty should not be blank: ${item.id}", item.difficulty.isBlank())
            assertFalse("Category should not be blank: ${item.id}", item.category.isBlank())
        }
    }

    @Test
    fun testNoDuplicateNormalizedPhrasesWithinEachCategory() {
        // Normalizes string: lowercase, trims, removes punctuation, reduces white spaces
        fun normalize(text: String): String {
            return text.lowercase().trim().replace(Regex("[^a-z0-9\\s]"), "").replace(Regex("\\s+"), " ")
        }

        val words = PronunciationPracticeRepository.words
        val normalizedWords = words.map { normalize(it.text) }
        assertEquals("Words category has duplicate normalized phrases", words.size, normalizedWords.toSet().size)

        val idioms = PronunciationPracticeRepository.idioms
        val normalizedIdioms = idioms.map { normalize(it.text) }
        assertEquals("Idioms category has duplicate normalized phrases", idioms.size, normalizedIdioms.toSet().size)

        val sentences = PronunciationPracticeRepository.sentences
        val normalizedSentences = sentences.map { normalize(it.text) }
        assertEquals("Sentences category has duplicate normalized phrases", sentences.size, normalizedSentences.toSet().size)
    }
}
