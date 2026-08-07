package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    // 1. Voice Control
    val voiceControlEnabled: Boolean = true,
    val wakeWordEnabled: Boolean = false,
    val voiceFeedbackEnabled: Boolean = true,
    val autoListenOnOpen: Boolean = false,
    val speakAnswersAutomatically: Boolean = true,
    val voiceSpeed: Float = 1.0f,
    val voicePitch: Float = 1.0f,
    val voiceLanguage: String = "en-US",
    val selectedSpeaker: String = "Young Voice", // "Young Voice", "Mature Voice"
    val selectedVoiceLanguage: String = "English", // "English", "Urdu", "Arabic"
    val voiceVolume: Float = 1.0f,

    // 2. Appearance
    val themeMode: String = "System", // Light, Dark, System
    val primaryColorPreset: String = "Purple", // Purple, Blue, Emerald, Amber
    val fontSize: String = "Medium", // Small, Medium, Large
    val compactMode: Boolean = false,
    val roundedUi: Boolean = true,
    val animationEffects: Boolean = true,

    // 3. Language & Learning Preferences
    val appLanguage: String = "English / Urdu",
    val englishLevel: String = "Beginner",
    val arabicSupport: Boolean = true,
    val urduHelp: Boolean = true,
    val dailyLessonGoal: Int = 3,
    val dailyRevisionReminder: Boolean = true,
    val grammarExplanationStyle: String = "Normal", // Short, Normal, Detailed

    // 4. Notifications
    val dailyReminder: Boolean = true,
    val lessonCompletionReminder: Boolean = true,
    val streakReminder: Boolean = true,
    val newContentAlerts: Boolean = true,
    val reminderTime: String = "09:00 AM",
    val quietHours: Boolean = false,

    // 5. Privacy & Data
    val saveProgressLocally: Boolean = true,
    val cloudBackup: Boolean = false,

    // 6. Accessibility
    val largeText: Boolean = false,
    val highContrast: Boolean = false,
    val reduceMotion: Boolean = false,
    val voiceGuidance: Boolean = false,

    // 8. Lesson & App Behavior
    val autoAdvance: Boolean = true,
    val showHints: Boolean = true,
    val showCorrectAnswerImmediately: Boolean = true,
    val practiceMode: Boolean = true,
    val lessonAutoplay: Boolean = false,
    val continueFromLastLesson: Boolean = true,

    // 10. Advanced
    val developerOptions: Boolean = false,
    val diagnostics: Boolean = true
)
