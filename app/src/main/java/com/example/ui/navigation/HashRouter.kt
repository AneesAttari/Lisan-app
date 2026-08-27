package com.example.ui.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A Hash-based router designed for Lisan Grammar.
 * It simulates a single-page application (SPA) routing mechanism
 * using web-like hash URLs to navigate between Android Jetpack Compose screens.
 */
object HashRouter {
    // Current active route path
    private val _currentRoute = MutableStateFlow("#/tutor")
    val currentRoute: StateFlow<String> = _currentRoute.asStateFlow()

    // Navigation history to allow back-navigation if needed
    private val history = mutableListOf("#/tutor")

    /**
     * Navigates to a new hash route.
     * Supported routes:
     * - `#/lessons` (Lesson Dashboard)
     * - `#/analyzer` (Sentence Analyzer)
     * - `#/tutor` (AI Tutor Chat)
     * - `#/voice-tutor` (Voice Conversational Tutor)
     * - `#/idioms` (Idioms & Expressions)
     * - `#/library` (My Library)
     * - `#/daily` (Lisan Daily)
     * - `#/quizzes` (Quizzes)
     * - `#/revision` (Revision Planner)
     * - `#/achievements` or `#/lisan-journey` (Lisan Journey)
     * - `#/progress` (Progress)
     * - `#/settings` (Settings)
     * - `#/help` (Help & Feedback)
     */
    fun navigate(route: String) {
        val cleanRoute = if (route.startsWith("#")) route else "#/$route"
        if (_currentRoute.value != cleanRoute) {
            _currentRoute.value = cleanRoute
            if (history.isEmpty() || history.last() != cleanRoute) {
                history.add(cleanRoute)
            }
        }
    }

    /**
     * Navigates back to the previous route in the history.
     */
    fun goBack(): Boolean {
        return if (history.size > 1) {
            history.removeAt(history.lastIndex)
            _currentRoute.value = history.last()
            true
        } else {
            false
        }
    }

    /**
     * Maps a web-like hash route to a MainActivity tab index.
     */
    fun routeToTab(route: String): Int {
        if (route.startsWith("#/picture-vocabulary")) return 14
        return when (route) {
            "#/lessons" -> 0
            "#/analyzer" -> 1
            "#/tutor" -> 2
            "#/idioms" -> 3
            "#/library" -> 4
            "#/daily" -> 5
            "#/quizzes" -> 6
            "#/revision" -> 7
            "#/achievements", "#/lisan-journey" -> 8
            "#/progress" -> 9
            "#/settings" -> 10
            "#/help" -> 11
            "#/past-conversations" -> 12
            "#/pronunciation" -> 13
            "#/picture-vocabulary" -> 14
            "#/api-key-guide", "#/implement-api-key" -> 15
            "#/chat-theme" -> 16
            "#/create-image" -> 17
            "#/haq-mehr", "#/silver-calculator", "#/silver-rate", "#/islamic-calculator", "#/zakat-calculator", "#/fitrana-calculator", "#/ushr-calculator" -> 18
            else -> 2
        }
    }

    /**
     * Maps a MainActivity tab index to a web-like hash route.
     */
    fun tabToRoute(tab: Int): String {
        return when (tab) {
            0 -> "#/lessons"
            1 -> "#/analyzer"
            2 -> "#/tutor"
            3 -> "#/idioms"
            4 -> "#/library"
            5 -> "#/daily"
            6 -> "#/quizzes"
            7 -> "#/revision"
            8 -> "#/lisan-journey"
            9 -> "#/progress"
            10 -> "#/settings"
            11 -> "#/help"
            12 -> "#/past-conversations"
            13 -> "#/pronunciation"
            14 -> "#/picture-vocabulary"
            15 -> "#/api-key-guide"
            16 -> "#/chat-theme"
            17 -> "#/create-image"
            18 -> "#/haq-mehr"
            else -> "#/tutor"
        }
    }

    private var isStartupRouteInitialized = false

    fun tabNameToRoute(tabName: String): String {
        return when (tabName) {
            "Learn" -> "#/lessons"
            "Analyze" -> "#/analyzer"
            "Tutor" -> "#/tutor"
            "Idioms" -> "#/idioms"
            "Library" -> "#/library"
            else -> "#/tutor"
        }
    }

    fun initializeStartupRoute(tabName: String) {
        if (!isStartupRouteInitialized) {
            isStartupRouteInitialized = true
            val route = tabNameToRoute(tabName)
            _currentRoute.value = route
            if (history.isEmpty()) {
                history.add(route)
            } else {
                history[0] = route
            }
        }
    }
}
