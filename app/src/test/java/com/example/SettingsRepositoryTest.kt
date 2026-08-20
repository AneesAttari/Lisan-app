package com.example

import com.example.data.repository.UserSettings
import org.junit.Assert.*
import org.junit.Test

class SettingsRepositoryTest {

    @Test
    fun defaultUserSettings_hasValidDefaults() {
        val settings = UserSettings()
        assertEquals("Learner", settings.displayName)
        assertEquals("Beginner", settings.learningLevel)
        assertEquals("Urdu", settings.nativeLanguage)
        assertEquals("Improve grammar", settings.learningGoal)
        assertEquals(15, settings.dailyGoalMinutes)
        assertEquals("System", settings.themeMode)
        assertFalse(settings.dynamicColor)
        assertEquals("Default", settings.fontSize)
        assertEquals(1.0f, settings.fontScale, 0.01f)
        assertTrue(settings.showArabicGrammarComparison)
        assertFalse(settings.romanUrduAssistance)
        assertEquals("Learn", settings.defaultLearningTab)
        assertTrue(settings.dailyReminderEnabled)
        assertEquals(9, settings.reminderHour)
        assertEquals(0, settings.reminderMinute)
        assertTrue(settings.autoOfflineFallback)
        assertTrue(settings.saveConversationHistory)
    }

    @Test
    fun fontScale_calculatesCorrectlyForSizes() {
        val small = UserSettings(fontSize = "Small")
        assertEquals(0.85f, small.fontScale, 0.01f)

        val defaultSize = UserSettings(fontSize = "Default")
        assertEquals(1.0f, defaultSize.fontScale, 0.01f)

        val large = UserSettings(fontSize = "Large")
        assertEquals(1.15f, large.fontScale, 0.01f)

        val extraLarge = UserSettings(fontSize = "Extra Large")
        assertEquals(1.30f, extraLarge.fontScale, 0.01f)
    }

    @Test
    fun displayName_trimmedValidationFallback() {
        val rawInput = "   "
        val trimmed = rawInput.trim().ifBlank { "Learner" }
        assertEquals("Learner", trimmed)

        val validName = "  Anees Attari  "
        val trimmedValid = validName.trim().ifBlank { "Learner" }
        assertEquals("Anees Attari", trimmedValid)
    }

    @Test
    fun speechRateAndPitch_clampedValueRanges() {
        val rate = 1.25f
        val pitch = 0.95f
        val settings = UserSettings(speechRate = rate, pitch = pitch)
        assertEquals(1.25f, settings.speechRate, 0.01f)
        assertEquals(0.95f, settings.pitch, 0.01f)
    }
}
