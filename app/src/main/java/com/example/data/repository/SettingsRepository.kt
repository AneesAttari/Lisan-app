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
    val primaryColorPreset: String = "Purple",
    val compactMode: Boolean = false,
    val roundedUi: Boolean = true,
    val animationEffects: Boolean = true,

    // Language & Learning
    val explanationLanguage: String = "English + Urdu", // English, Urdu, English + Urdu
    val showArabicGrammarComparison: Boolean = true,
    val romanUrduAssistance: Boolean = true,
    val defaultLearningTab: String = "Learn", // Learn, Analyze, Tutor, Idioms, Library
    val adaptDifficulty: Boolean = true,
    val appLanguage: String = "English / Urdu",
    val englishLevel: String = "Beginner",
    val arabicSupport: Boolean = true,
    val urduHelp: Boolean = true,
    val dailyLessonGoal: Int = 3,
    val dailyRevisionReminder: Boolean = true,
    val grammarExplanationStyle: String = "Normal",
    val autoAdvance: Boolean = true,
    val showHints: Boolean = true,
    val showCorrectAnswerImmediately: Boolean = true,
    val practiceMode: Boolean = true,
    val lessonAutoplay: Boolean = false,
    val continueFromLastLesson: Boolean = true,

    // Speech & Pronunciation
    val selectedSpeaker: String = "Young Voice", // Young Voice, Mature Voice
    val ttsVoiceLocale: String = "en-US",
    val speechRate: Float = 1.0f,
    val pitch: Float = 1.0f,
    val autoPlayPronunciation: Boolean = true,
    val voiceInputLanguage: String = "en-US",
    val pronunciationFeedbackStyle: String = "Supportive", // Supportive, Detailed, Concise
    val voiceControlEnabled: Boolean = true,
    val wakeWordEnabled: Boolean = false,
    val voiceFeedbackEnabled: Boolean = true,
    val autoListenOnOpen: Boolean = false,
    val speakAnswersAutomatically: Boolean = true,
    val voiceVolume: Float = 1.0f,
    val selectedVoiceLanguage: String = "English",

    // Notifications
    val dailyReminderEnabled: Boolean = true,
    val reminderHour: Int = 9,
    val reminderMinute: Int = 0,
    val dailyChallengeReminderEnabled: Boolean = true,
    val reminderMessageStyle: String = "Motivational", // Gentle, Motivational, Minimal
    val lessonCompletionReminder: Boolean = true,
    val streakReminder: Boolean = true,
    val newContentAlerts: Boolean = true,
    val reminderTime: String = "09:00 AM",
    val quietHours: Boolean = false,

    // Data & Offline
    val autoOfflineFallback: Boolean = true,
    val saveConversationHistory: Boolean = true,
    val saveProgressLocally: Boolean = true,
    val cloudBackup: Boolean = false,
    val imageCompressionPreset: String = "BALANCED",

    // Privacy
    val analyticsConsent: Boolean = false,
    val developerOptions: Boolean = false,
    val diagnostics: Boolean = true,

    // Chat Customization
    val chatTheme: String = "Default",
    val chatBackgroundImageUri: String = "",

    // Usage & API
    val userApiKey: String? = null,
    val usageCount: Int = 0,
    val lastUsageDate: String = "",
    val activeUsersCount: Int = 100,
    val dailyLimit: Int = 15
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
        val PRIMARY_COLOR_PRESET = stringPreferencesKey("primary_color_preset")
        val COMPACT_MODE = booleanPreferencesKey("compact_mode")
        val ROUNDED_UI = booleanPreferencesKey("rounded_ui")
        val ANIMATION_EFFECTS = booleanPreferencesKey("animation_effects")

        val EXPLANATION_LANGUAGE = stringPreferencesKey("explanation_language")
        val SHOW_ARABIC_GRAMMAR_COMPARISON = booleanPreferencesKey("show_arabic_grammar_comparison")
        val ROMAN_URDU_ASSISTANCE = booleanPreferencesKey("roman_urdu_assistance")
        val DEFAULT_LEARNING_TAB = stringPreferencesKey("default_learning_tab")
        val ADAPT_DIFFICULTY = booleanPreferencesKey("adapt_difficulty")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
        val ENGLISH_LEVEL = stringPreferencesKey("english_level")
        val ARABIC_SUPPORT = booleanPreferencesKey("arabic_support")
        val URDU_HELP = booleanPreferencesKey("urdu_help")
        val DAILY_LESSON_GOAL = intPreferencesKey("daily_lesson_goal")
        val DAILY_REVISION_REMINDER = booleanPreferencesKey("daily_revision_reminder")
        val GRAMMAR_EXPLANATION_STYLE = stringPreferencesKey("grammar_explanation_style")
        val AUTO_ADVANCE = booleanPreferencesKey("auto_advance")
        val SHOW_HINTS = booleanPreferencesKey("show_hints")
        val SHOW_CORRECT_ANSWER_IMMEDIATELY = booleanPreferencesKey("show_correct_answer_immediately")
        val PRACTICE_MODE = booleanPreferencesKey("practice_mode")
        val LESSON_AUTOPLAY = booleanPreferencesKey("lesson_autoplay")
        val CONTINUE_FROM_LAST_LESSON = booleanPreferencesKey("continue_from_last_lesson")

        val SELECTED_SPEAKER = stringPreferencesKey("selected_speaker")
        val TTS_VOICE_LOCALE = stringPreferencesKey("tts_voice_locale")
        val SPEECH_RATE = floatPreferencesKey("speech_rate")
        val PITCH = floatPreferencesKey("pitch")
        val AUTO_PLAY_PRONUNCIATION = booleanPreferencesKey("auto_play_pronunciation")
        val VOICE_INPUT_LANGUAGE = stringPreferencesKey("voice_input_language")
        val PRONUNCIATION_FEEDBACK_STYLE = stringPreferencesKey("pronunciation_feedback_style")
        val VOICE_CONTROL_ENABLED = booleanPreferencesKey("voice_control_enabled")
        val WAKE_WORD_ENABLED = booleanPreferencesKey("wake_word_enabled")
        val VOICE_FEEDBACK_ENABLED = booleanPreferencesKey("voice_feedback_enabled")
        val AUTO_LISTEN_ON_OPEN = booleanPreferencesKey("auto_listen_on_open")
        val SPEAK_ANSWERS_AUTOMATICALLY = booleanPreferencesKey("speak_answers_automatically")
        val VOICE_VOLUME = floatPreferencesKey("voice_volume")
        val SELECTED_VOICE_LANGUAGE = stringPreferencesKey("selected_voice_language")

        val DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminder_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
        val DAILY_CHALLENGE_REMINDER_ENABLED = booleanPreferencesKey("daily_challenge_reminder_enabled")
        val REMINDER_MESSAGE_STYLE = stringPreferencesKey("reminder_message_style")
        val LESSON_COMPLETION_REMINDER = booleanPreferencesKey("lesson_completion_reminder")
        val STREAK_REMINDER = booleanPreferencesKey("streak_reminder")
        val NEW_CONTENT_ALERTS = booleanPreferencesKey("new_content_alerts")
        val REMINDER_TIME = stringPreferencesKey("reminder_time")
        val QUIET_HOURS = booleanPreferencesKey("quiet_hours")

        val AUTO_OFFLINE_FALLBACK = booleanPreferencesKey("auto_offline_fallback")
        val SAVE_CONVERSATION_HISTORY = booleanPreferencesKey("save_conversation_history")
        val SAVE_PROGRESS_LOCALLY = booleanPreferencesKey("save_progress_locally")
        val CLOUD_BACKUP = booleanPreferencesKey("cloud_backup")
        val IMAGE_COMPRESSION_PRESET = stringPreferencesKey("image_compression_preset")

        val ANALYTICS_CONSENT = booleanPreferencesKey("analytics_consent")
        val DEVELOPER_OPTIONS = booleanPreferencesKey("developer_options")
        val DIAGNOSTICS = booleanPreferencesKey("diagnostics")

        val CHAT_THEME = stringPreferencesKey("chat_theme")
        val CHAT_BACKGROUND_IMAGE_URI = stringPreferencesKey("chat_background_image_uri")

        val USER_API_KEY = stringPreferencesKey("user_api_key")
        val USAGE_COUNT = intPreferencesKey("usage_count")
        val LAST_USAGE_DATE = stringPreferencesKey("last_usage_date")
        val ACTIVE_USERS_COUNT = intPreferencesKey("active_users_count")
        val DAILY_LIMIT = intPreferencesKey("daily_limit")
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
            primaryColorPreset = preferences[PreferencesKeys.PRIMARY_COLOR_PRESET] ?: "Purple",
            compactMode = preferences[PreferencesKeys.COMPACT_MODE] ?: false,
            roundedUi = preferences[PreferencesKeys.ROUNDED_UI] ?: true,
            animationEffects = preferences[PreferencesKeys.ANIMATION_EFFECTS] ?: true,

            explanationLanguage = preferences[PreferencesKeys.EXPLANATION_LANGUAGE] ?: "English + Urdu",
            showArabicGrammarComparison = preferences[PreferencesKeys.SHOW_ARABIC_GRAMMAR_COMPARISON] ?: true,
            romanUrduAssistance = preferences[PreferencesKeys.ROMAN_URDU_ASSISTANCE] ?: true,
            defaultLearningTab = preferences[PreferencesKeys.DEFAULT_LEARNING_TAB] ?: "Learn",
            adaptDifficulty = preferences[PreferencesKeys.ADAPT_DIFFICULTY] ?: true,
            appLanguage = preferences[PreferencesKeys.APP_LANGUAGE] ?: "English / Urdu",
            englishLevel = preferences[PreferencesKeys.ENGLISH_LEVEL] ?: "Beginner",
            arabicSupport = preferences[PreferencesKeys.ARABIC_SUPPORT] ?: true,
            urduHelp = preferences[PreferencesKeys.URDU_HELP] ?: true,
            dailyLessonGoal = preferences[PreferencesKeys.DAILY_LESSON_GOAL] ?: 3,
            dailyRevisionReminder = preferences[PreferencesKeys.DAILY_REVISION_REMINDER] ?: true,
            grammarExplanationStyle = preferences[PreferencesKeys.GRAMMAR_EXPLANATION_STYLE] ?: "Normal",
            autoAdvance = preferences[PreferencesKeys.AUTO_ADVANCE] ?: true,
            showHints = preferences[PreferencesKeys.SHOW_HINTS] ?: true,
            showCorrectAnswerImmediately = preferences[PreferencesKeys.SHOW_CORRECT_ANSWER_IMMEDIATELY] ?: true,
            practiceMode = preferences[PreferencesKeys.PRACTICE_MODE] ?: true,
            lessonAutoplay = preferences[PreferencesKeys.LESSON_AUTOPLAY] ?: false,
            continueFromLastLesson = preferences[PreferencesKeys.CONTINUE_FROM_LAST_LESSON] ?: true,

            selectedSpeaker = preferences[PreferencesKeys.SELECTED_SPEAKER] ?: "Young Voice",
            ttsVoiceLocale = preferences[PreferencesKeys.TTS_VOICE_LOCALE] ?: "en-US",
            speechRate = preferences[PreferencesKeys.SPEECH_RATE] ?: 1.0f,
            pitch = preferences[PreferencesKeys.PITCH] ?: 1.0f,
            autoPlayPronunciation = preferences[PreferencesKeys.AUTO_PLAY_PRONUNCIATION] ?: true,
            voiceInputLanguage = preferences[PreferencesKeys.VOICE_INPUT_LANGUAGE] ?: "en-US",
            pronunciationFeedbackStyle = preferences[PreferencesKeys.PRONUNCIATION_FEEDBACK_STYLE] ?: "Supportive",
            voiceControlEnabled = preferences[PreferencesKeys.VOICE_CONTROL_ENABLED] ?: true,
            wakeWordEnabled = preferences[PreferencesKeys.WAKE_WORD_ENABLED] ?: false,
            voiceFeedbackEnabled = preferences[PreferencesKeys.VOICE_FEEDBACK_ENABLED] ?: true,
            autoListenOnOpen = preferences[PreferencesKeys.AUTO_LISTEN_ON_OPEN] ?: false,
            speakAnswersAutomatically = preferences[PreferencesKeys.SPEAK_ANSWERS_AUTOMATICALLY] ?: true,
            voiceVolume = preferences[PreferencesKeys.VOICE_VOLUME] ?: 1.0f,
            selectedVoiceLanguage = preferences[PreferencesKeys.SELECTED_VOICE_LANGUAGE] ?: "English",

            dailyReminderEnabled = preferences[PreferencesKeys.DAILY_REMINDER_ENABLED] ?: true,
            reminderHour = preferences[PreferencesKeys.REMINDER_HOUR] ?: 9,
            reminderMinute = preferences[PreferencesKeys.REMINDER_MINUTE] ?: 0,
            dailyChallengeReminderEnabled = preferences[PreferencesKeys.DAILY_CHALLENGE_REMINDER_ENABLED] ?: true,
            reminderMessageStyle = preferences[PreferencesKeys.REMINDER_MESSAGE_STYLE] ?: "Motivational",
            lessonCompletionReminder = preferences[PreferencesKeys.LESSON_COMPLETION_REMINDER] ?: true,
            streakReminder = preferences[PreferencesKeys.STREAK_REMINDER] ?: true,
            newContentAlerts = preferences[PreferencesKeys.NEW_CONTENT_ALERTS] ?: true,
            reminderTime = preferences[PreferencesKeys.REMINDER_TIME] ?: "09:00 AM",
            quietHours = preferences[PreferencesKeys.QUIET_HOURS] ?: false,

            autoOfflineFallback = preferences[PreferencesKeys.AUTO_OFFLINE_FALLBACK] ?: true,
            saveConversationHistory = preferences[PreferencesKeys.SAVE_CONVERSATION_HISTORY] ?: true,
            saveProgressLocally = preferences[PreferencesKeys.SAVE_PROGRESS_LOCALLY] ?: true,
            cloudBackup = preferences[PreferencesKeys.CLOUD_BACKUP] ?: false,
            imageCompressionPreset = preferences[PreferencesKeys.IMAGE_COMPRESSION_PRESET] ?: "BALANCED",

            analyticsConsent = preferences[PreferencesKeys.ANALYTICS_CONSENT] ?: false,
            developerOptions = preferences[PreferencesKeys.DEVELOPER_OPTIONS] ?: false,
            diagnostics = preferences[PreferencesKeys.DIAGNOSTICS] ?: true,

            chatTheme = preferences[PreferencesKeys.CHAT_THEME] ?: "Default",
            chatBackgroundImageUri = preferences[PreferencesKeys.CHAT_BACKGROUND_IMAGE_URI] ?: "",

            userApiKey = preferences[PreferencesKeys.USER_API_KEY],
            usageCount = preferences[PreferencesKeys.USAGE_COUNT] ?: 0,
            lastUsageDate = preferences[PreferencesKeys.LAST_USAGE_DATE] ?: "",
            activeUsersCount = preferences[PreferencesKeys.ACTIVE_USERS_COUNT] ?: 100,
            dailyLimit = preferences[PreferencesKeys.DAILY_LIMIT] ?: 15
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
            preferences[PreferencesKeys.PRIMARY_COLOR_PRESET] = updated.primaryColorPreset
            preferences[PreferencesKeys.COMPACT_MODE] = updated.compactMode
            preferences[PreferencesKeys.ROUNDED_UI] = updated.roundedUi
            preferences[PreferencesKeys.ANIMATION_EFFECTS] = updated.animationEffects

            preferences[PreferencesKeys.EXPLANATION_LANGUAGE] = updated.explanationLanguage
            preferences[PreferencesKeys.SHOW_ARABIC_GRAMMAR_COMPARISON] = updated.showArabicGrammarComparison
            preferences[PreferencesKeys.ROMAN_URDU_ASSISTANCE] = updated.romanUrduAssistance
            preferences[PreferencesKeys.DEFAULT_LEARNING_TAB] = updated.defaultLearningTab
            preferences[PreferencesKeys.ADAPT_DIFFICULTY] = updated.adaptDifficulty
            preferences[PreferencesKeys.APP_LANGUAGE] = updated.appLanguage
            preferences[PreferencesKeys.ENGLISH_LEVEL] = updated.englishLevel
            preferences[PreferencesKeys.ARABIC_SUPPORT] = updated.arabicSupport
            preferences[PreferencesKeys.URDU_HELP] = updated.urduHelp
            preferences[PreferencesKeys.DAILY_LESSON_GOAL] = updated.dailyLessonGoal
            preferences[PreferencesKeys.DAILY_REVISION_REMINDER] = updated.dailyRevisionReminder
            preferences[PreferencesKeys.GRAMMAR_EXPLANATION_STYLE] = updated.grammarExplanationStyle
            preferences[PreferencesKeys.AUTO_ADVANCE] = updated.autoAdvance
            preferences[PreferencesKeys.SHOW_HINTS] = updated.showHints
            preferences[PreferencesKeys.SHOW_CORRECT_ANSWER_IMMEDIATELY] = updated.showCorrectAnswerImmediately
            preferences[PreferencesKeys.PRACTICE_MODE] = updated.practiceMode
            preferences[PreferencesKeys.LESSON_AUTOPLAY] = updated.lessonAutoplay
            preferences[PreferencesKeys.CONTINUE_FROM_LAST_LESSON] = updated.continueFromLastLesson

            preferences[PreferencesKeys.SELECTED_SPEAKER] = updated.selectedSpeaker
            preferences[PreferencesKeys.TTS_VOICE_LOCALE] = updated.ttsVoiceLocale
            preferences[PreferencesKeys.SPEECH_RATE] = updated.speechRate
            preferences[PreferencesKeys.PITCH] = updated.pitch
            preferences[PreferencesKeys.AUTO_PLAY_PRONUNCIATION] = updated.autoPlayPronunciation
            preferences[PreferencesKeys.VOICE_INPUT_LANGUAGE] = updated.voiceInputLanguage
            preferences[PreferencesKeys.PRONUNCIATION_FEEDBACK_STYLE] = updated.pronunciationFeedbackStyle
            preferences[PreferencesKeys.VOICE_CONTROL_ENABLED] = updated.voiceControlEnabled
            preferences[PreferencesKeys.WAKE_WORD_ENABLED] = updated.wakeWordEnabled
            preferences[PreferencesKeys.VOICE_FEEDBACK_ENABLED] = updated.voiceFeedbackEnabled
            preferences[PreferencesKeys.AUTO_LISTEN_ON_OPEN] = updated.autoListenOnOpen
            preferences[PreferencesKeys.SPEAK_ANSWERS_AUTOMATICALLY] = updated.speakAnswersAutomatically
            preferences[PreferencesKeys.VOICE_VOLUME] = updated.voiceVolume
            preferences[PreferencesKeys.SELECTED_VOICE_LANGUAGE] = updated.selectedVoiceLanguage

            preferences[PreferencesKeys.DAILY_REMINDER_ENABLED] = updated.dailyReminderEnabled
            preferences[PreferencesKeys.REMINDER_HOUR] = updated.reminderHour
            preferences[PreferencesKeys.REMINDER_MINUTE] = updated.reminderMinute
            preferences[PreferencesKeys.DAILY_CHALLENGE_REMINDER_ENABLED] = updated.dailyChallengeReminderEnabled
            preferences[PreferencesKeys.REMINDER_MESSAGE_STYLE] = updated.reminderMessageStyle
            preferences[PreferencesKeys.LESSON_COMPLETION_REMINDER] = updated.lessonCompletionReminder
            preferences[PreferencesKeys.STREAK_REMINDER] = updated.streakReminder
            preferences[PreferencesKeys.NEW_CONTENT_ALERTS] = updated.newContentAlerts
            preferences[PreferencesKeys.REMINDER_TIME] = updated.reminderTime
            preferences[PreferencesKeys.QUIET_HOURS] = updated.quietHours

            preferences[PreferencesKeys.AUTO_OFFLINE_FALLBACK] = updated.autoOfflineFallback
            preferences[PreferencesKeys.SAVE_CONVERSATION_HISTORY] = updated.saveConversationHistory
            preferences[PreferencesKeys.SAVE_PROGRESS_LOCALLY] = updated.saveProgressLocally
            preferences[PreferencesKeys.CLOUD_BACKUP] = updated.cloudBackup
            preferences[PreferencesKeys.IMAGE_COMPRESSION_PRESET] = updated.imageCompressionPreset

            preferences[PreferencesKeys.ANALYTICS_CONSENT] = updated.analyticsConsent
            preferences[PreferencesKeys.DEVELOPER_OPTIONS] = updated.developerOptions
            preferences[PreferencesKeys.DIAGNOSTICS] = updated.diagnostics

            preferences[PreferencesKeys.CHAT_THEME] = updated.chatTheme
            preferences[PreferencesKeys.CHAT_BACKGROUND_IMAGE_URI] = updated.chatBackgroundImageUri

            if (updated.userApiKey != null) preferences[PreferencesKeys.USER_API_KEY] = updated.userApiKey!!
            else preferences.remove(PreferencesKeys.USER_API_KEY)
            preferences[PreferencesKeys.USAGE_COUNT] = updated.usageCount
            preferences[PreferencesKeys.LAST_USAGE_DATE] = updated.lastUsageDate
            preferences[PreferencesKeys.ACTIVE_USERS_COUNT] = updated.activeUsersCount
            preferences[PreferencesKeys.DAILY_LIMIT] = updated.dailyLimit
        }
    }

    suspend fun resetToDefaults() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
