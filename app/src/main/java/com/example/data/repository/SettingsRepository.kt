package com.example.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lisan_settings_prefs")

data class UserSettings(
    // Learner Profile
    val displayName: String = "Learner",
    val learningLevel: String = "Beginner",
    val nativeLanguage: String = "Urdu",
    val learningGoal: String = "Improve grammar",
    val dailyGoalMinutes: Int = 15,

    // Appearance
    val themeMode: String = "System", // System, Light, Dark
    val dynamicColor: Boolean = false,
    val fontSize: String = "Default", // Small, Default, Large, Extra Large
    val reduceMotion: Boolean = false,

    // Language & Learning
    val explanationLanguage: String = "English + Urdu", // English, Urdu, English + Urdu
    val showArabicGrammarComparison: Boolean = true,
    val romanUrduAssistance: Boolean = true,
    val defaultLearningTab: String = "Learn", // Learn, Analyze, Tutor, Idioms, Library
    val adaptDifficulty: Boolean = true,

    // Speech & Pronunciation
    val selectedSpeaker: String = "Young Voice", // Young Voice, Mature Voice
    val ttsVoiceLocale: String = "en-US",
    val speechRate: Float = 1.0f,
    val pitch: Float = 1.0f,
    val autoPlayPronunciation: Boolean = true,
    val voiceInputLanguage: String = "en-US",
    val pronunciationFeedbackStyle: String = "Supportive", // Supportive, Detailed, Concise

    // Notifications
    val dailyReminderEnabled: Boolean = true,
    val reminderHour: Int = 9,
    val reminderMinute: Int = 0,
    val dailyChallengeReminderEnabled: Boolean = true,
    val reminderMessageStyle: String = "Motivational", // Gentle, Motivational, Minimal

    // Data & Offline
    val autoOfflineFallback: Boolean = true,
    val saveConversationHistory: Boolean = true,

    // Privacy
    val analyticsConsent: Boolean = false
) {
    val fontScale: Float
        get() = when (fontSize) {
            "Small" -> 0.85f
            "Large" -> 1.15f
            "Extra Large" -> 1.30f
            else -> 1.0f
        }
}

class SettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val LEARNING_LEVEL = stringPreferencesKey("learning_level")
        val NATIVE_LANGUAGE = stringPreferencesKey("native_language")
        val LEARNING_GOAL = stringPreferencesKey("learning_goal")
        val DAILY_GOAL_MINUTES = intPreferencesKey("daily_goal_minutes")

        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val FONT_SIZE = stringPreferencesKey("font_size")
        val REDUCE_MOTION = booleanPreferencesKey("reduce_motion")

        val EXPLANATION_LANGUAGE = stringPreferencesKey("explanation_language")
        val SHOW_ARABIC_GRAMMAR_COMPARISON = booleanPreferencesKey("show_arabic_grammar_comparison")
        val ROMAN_URDU_ASSISTANCE = booleanPreferencesKey("roman_urdu_assistance")
        val DEFAULT_LEARNING_TAB = stringPreferencesKey("default_learning_tab")
        val ADAPT_DIFFICULTY = booleanPreferencesKey("adapt_difficulty")

        val SELECTED_SPEAKER = stringPreferencesKey("selected_speaker")
        val TTS_VOICE_LOCALE = stringPreferencesKey("tts_voice_locale")
        val SPEECH_RATE = floatPreferencesKey("speech_rate")
        val PITCH = floatPreferencesKey("pitch")
        val AUTO_PLAY_PRONUNCIATION = booleanPreferencesKey("auto_play_pronunciation")
        val VOICE_INPUT_LANGUAGE = stringPreferencesKey("voice_input_language")
        val PRONUNCIATION_FEEDBACK_STYLE = stringPreferencesKey("pronunciation_feedback_style")

        val DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminder_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
        val DAILY_CHALLENGE_REMINDER_ENABLED = booleanPreferencesKey("daily_challenge_reminder_enabled")
        val REMINDER_MESSAGE_STYLE = stringPreferencesKey("reminder_message_style")

        val AUTO_OFFLINE_FALLBACK = booleanPreferencesKey("auto_offline_fallback")
        val SAVE_CONVERSATION_HISTORY = booleanPreferencesKey("save_conversation_history")

        val ANALYTICS_CONSENT = booleanPreferencesKey("analytics_consent")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            mapPreferencesToSettings(preferences)
        }

    private fun mapPreferencesToSettings(preferences: Preferences): UserSettings {
        return UserSettings(
            displayName = preferences[PreferencesKeys.DISPLAY_NAME] ?: "Learner",
            learningLevel = preferences[PreferencesKeys.LEARNING_LEVEL] ?: "Beginner",
            nativeLanguage = preferences[PreferencesKeys.NATIVE_LANGUAGE] ?: "Urdu",
            learningGoal = preferences[PreferencesKeys.LEARNING_GOAL] ?: "Improve grammar",
            dailyGoalMinutes = preferences[PreferencesKeys.DAILY_GOAL_MINUTES] ?: 15,

            themeMode = preferences[PreferencesKeys.THEME_MODE] ?: "System",
            dynamicColor = preferences[PreferencesKeys.DYNAMIC_COLOR] ?: false,
            fontSize = preferences[PreferencesKeys.FONT_SIZE] ?: "Default",
            reduceMotion = preferences[PreferencesKeys.REDUCE_MOTION] ?: false,

            explanationLanguage = preferences[PreferencesKeys.EXPLANATION_LANGUAGE] ?: "English + Urdu",
            showArabicGrammarComparison = preferences[PreferencesKeys.SHOW_ARABIC_GRAMMAR_COMPARISON] ?: true,
            romanUrduAssistance = preferences[PreferencesKeys.ROMAN_URDU_ASSISTANCE] ?: true,
            defaultLearningTab = preferences[PreferencesKeys.DEFAULT_LEARNING_TAB] ?: "Learn",
            adaptDifficulty = preferences[PreferencesKeys.ADAPT_DIFFICULTY] ?: true,

            selectedSpeaker = preferences[PreferencesKeys.SELECTED_SPEAKER] ?: "Young Voice",
            ttsVoiceLocale = preferences[PreferencesKeys.TTS_VOICE_LOCALE] ?: "en-US",
            speechRate = preferences[PreferencesKeys.SPEECH_RATE] ?: 1.0f,
            pitch = preferences[PreferencesKeys.PITCH] ?: 1.0f,
            autoPlayPronunciation = preferences[PreferencesKeys.AUTO_PLAY_PRONUNCIATION] ?: true,
            voiceInputLanguage = preferences[PreferencesKeys.VOICE_INPUT_LANGUAGE] ?: "en-US",
            pronunciationFeedbackStyle = preferences[PreferencesKeys.PRONUNCIATION_FEEDBACK_STYLE] ?: "Supportive",

            dailyReminderEnabled = preferences[PreferencesKeys.DAILY_REMINDER_ENABLED] ?: true,
            reminderHour = preferences[PreferencesKeys.REMINDER_HOUR] ?: 9,
            reminderMinute = preferences[PreferencesKeys.REMINDER_MINUTE] ?: 0,
            dailyChallengeReminderEnabled = preferences[PreferencesKeys.DAILY_CHALLENGE_REMINDER_ENABLED] ?: true,
            reminderMessageStyle = preferences[PreferencesKeys.REMINDER_MESSAGE_STYLE] ?: "Motivational",

            autoOfflineFallback = preferences[PreferencesKeys.AUTO_OFFLINE_FALLBACK] ?: true,
            saveConversationHistory = preferences[PreferencesKeys.SAVE_CONVERSATION_HISTORY] ?: true,

            analyticsConsent = preferences[PreferencesKeys.ANALYTICS_CONSENT] ?: false
        )
    }

    suspend fun updateSettings(transform: (UserSettings) -> UserSettings) {
        context.dataStore.edit { preferences ->
            val currentSettings = mapPreferencesToSettings(preferences)
            val updated = transform(currentSettings)

            preferences[PreferencesKeys.DISPLAY_NAME] = updated.displayName
            preferences[PreferencesKeys.LEARNING_LEVEL] = updated.learningLevel
            preferences[PreferencesKeys.NATIVE_LANGUAGE] = updated.nativeLanguage
            preferences[PreferencesKeys.LEARNING_GOAL] = updated.learningGoal
            preferences[PreferencesKeys.DAILY_GOAL_MINUTES] = updated.dailyGoalMinutes

            preferences[PreferencesKeys.THEME_MODE] = updated.themeMode
            preferences[PreferencesKeys.DYNAMIC_COLOR] = updated.dynamicColor
            preferences[PreferencesKeys.FONT_SIZE] = updated.fontSize
            preferences[PreferencesKeys.REDUCE_MOTION] = updated.reduceMotion

            preferences[PreferencesKeys.EXPLANATION_LANGUAGE] = updated.explanationLanguage
            preferences[PreferencesKeys.SHOW_ARABIC_GRAMMAR_COMPARISON] = updated.showArabicGrammarComparison
            preferences[PreferencesKeys.ROMAN_URDU_ASSISTANCE] = updated.romanUrduAssistance
            preferences[PreferencesKeys.DEFAULT_LEARNING_TAB] = updated.defaultLearningTab
            preferences[PreferencesKeys.ADAPT_DIFFICULTY] = updated.adaptDifficulty

            preferences[PreferencesKeys.SELECTED_SPEAKER] = updated.selectedSpeaker
            preferences[PreferencesKeys.TTS_VOICE_LOCALE] = updated.ttsVoiceLocale
            preferences[PreferencesKeys.SPEECH_RATE] = updated.speechRate
            preferences[PreferencesKeys.PITCH] = updated.pitch
            preferences[PreferencesKeys.AUTO_PLAY_PRONUNCIATION] = updated.autoPlayPronunciation
            preferences[PreferencesKeys.VOICE_INPUT_LANGUAGE] = updated.voiceInputLanguage
            preferences[PreferencesKeys.PRONUNCIATION_FEEDBACK_STYLE] = updated.pronunciationFeedbackStyle

            preferences[PreferencesKeys.DAILY_REMINDER_ENABLED] = updated.dailyReminderEnabled
            preferences[PreferencesKeys.REMINDER_HOUR] = updated.reminderHour
            preferences[PreferencesKeys.REMINDER_MINUTE] = updated.reminderMinute
            preferences[PreferencesKeys.DAILY_CHALLENGE_REMINDER_ENABLED] = updated.dailyChallengeReminderEnabled
            preferences[PreferencesKeys.REMINDER_MESSAGE_STYLE] = updated.reminderMessageStyle

            preferences[PreferencesKeys.AUTO_OFFLINE_FALLBACK] = updated.autoOfflineFallback
            preferences[PreferencesKeys.SAVE_CONVERSATION_HISTORY] = updated.saveConversationHistory

            preferences[PreferencesKeys.ANALYTICS_CONSENT] = updated.analyticsConsent
        }
    }

    suspend fun resetToDefaults() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
