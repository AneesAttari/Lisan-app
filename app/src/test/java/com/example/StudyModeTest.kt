package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.screens.cleanRecommendationTags
import com.example.ui.screens.parseRecommendationRoute
import com.example.ui.viewmodel.LisanViewModel
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * End-to-end tests for Lisan Grammar Study Mode.
 * Verifies routing parsing, tag sanitization, preference persistence, privacy consent settings,
 * and data-grounded contextual prompt construction.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class StudyModeTest {

    // ==========================================
    // 1. ROUTE PARSING & SANITIZATION UNIT TESTS
    // ==========================================

    @Test
    fun testParseRecommendationRoute_withValidAllowedRoutes() {
        // Test allowlisted routes return the exact route string
        assertEquals("#/lessons", parseRecommendationRoute("Here is your lesson [RECOMMEND: #/lessons]"))
        assertEquals("#/analyzer", parseRecommendationRoute("Check this sentence [RECOMMEND: #/analyzer]"))
        assertEquals("#/quizzes", parseRecommendationRoute("[RECOMMEND: #/quizzes] Go here!"))
        assertEquals("#/revision", parseRecommendationRoute("[RECOMMEND: #/revision]"))
        assertEquals("#/daily", parseRecommendationRoute("Do your tasks [RECOMMEND: #/daily]"))
        assertEquals("#/picture-vocabulary", parseRecommendationRoute("[RECOMMEND: #/picture-vocabulary]"))
        assertEquals("#/pronunciation", parseRecommendationRoute("[RECOMMEND: #/pronunciation]"))
        assertEquals("#/idioms", parseRecommendationRoute("[RECOMMEND: #/idioms]"))
        assertEquals("#/lisan-journey", parseRecommendationRoute("[RECOMMEND: #/lisan-journey]"))
        assertEquals("#/progress", parseRecommendationRoute("[RECOMMEND: #/progress]"))
    }

    @Test
    fun testParseRecommendationRoute_withInvalidOrUnsafeRoutes() {
        // Test unallowed/untrusted/arbitrary model recommendations are rejected and return null
        assertNull(parseRecommendationRoute("Check out google [RECOMMEND: http://google.com]"))
        assertNull(parseRecommendationRoute("Malicious deep links [RECOMMEND: #/delete_all_data]"))
        assertNull(parseRecommendationRoute("Unknown path [RECOMMEND: #/some_new_unsupported_path]"))
        assertNull(parseRecommendationRoute("Empty recommend tag [RECOMMEND: ]"))
        assertNull(parseRecommendationRoute("Plain text with no recommendation tag"))
    }

    @Test
    fun testCleanRecommendationTags_removesTagsSuccessfully() {
        val rawResponse = "Here is a great lesson on prepositions. [RECOMMEND: #/lessons]"
        val expectedCleaned = "Here is a great lesson on prepositions."
        assertEquals(expectedCleaned, cleanRecommendationTags(rawResponse))

        val multipleTags = "Look at this: [RECOMMEND: #/analyzer] and [RECOMMEND: #/lessons]"
        val expectedMultipleCleaned = "Look at this:  and"
        assertEquals(expectedMultipleCleaned, cleanRecommendationTags(multipleTags))

        val plainResponse = "No recommendations here."
        assertEquals(plainResponse, cleanRecommendationTags(plainResponse))
    }

    // ==========================================
    // 2. VIEWMODEL PERSISTENCE & PRIVACY ROBOLECTRIC TESTS
    // ==========================================

    @Test
    fun testStudyMode_statePersistence() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = LisanViewModel(app)

        // Set state to true
        viewModel.setStudyMode(true)
        assertTrue("StateFlow should update immediately", viewModel.isStudyModeOn.value)

        // Read directly from SharedPreferences to verify disk persistence
        val prefs = app.getSharedPreferences("lisan_prefs", Context.MODE_PRIVATE)
        assertTrue("SharedPreferences must persist tutor_study_mode_on", prefs.getBoolean("tutor_study_mode_on", false))

        // Set state to false
        viewModel.setStudyMode(false)
        assertFalse("StateFlow should update to false", viewModel.isStudyModeOn.value)
        assertFalse("SharedPreferences must persist false state", prefs.getBoolean("tutor_study_mode_on", true))
    }

    @Test
    fun testPersonalizationConsent_statePersistence() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = LisanViewModel(app)

        // Set personalization consent to false
        viewModel.setStudyPersonalizationEnabled(false)
        assertFalse("StateFlow should reflect disabled consent", viewModel.isStudyPersonalizationEnabled.value)

        // Read directly from SharedPreferences
        val prefs = app.getSharedPreferences("lisan_prefs", Context.MODE_PRIVATE)
        assertFalse("SharedPreferences must persist study_mode_use_personalization", prefs.getBoolean("study_mode_use_personalization", true))

        // Set personalization consent to true
        viewModel.setStudyPersonalizationEnabled(true)
        assertTrue("StateFlow should reflect enabled consent", viewModel.isStudyPersonalizationEnabled.value)
        assertTrue("SharedPreferences must persist true state", prefs.getBoolean("study_mode_use_personalization", false))
    }

    @Test
    fun testBuildPersonalizedStudyContext_withPersonalizationDisabled() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = LisanViewModel(app)

        // Disable consent
        viewModel.setStudyPersonalizationEnabled(false)

        val contextString = viewModel.buildPersonalizedStudyContext()

        assertTrue(
            "When consent is disabled, the system MUST return a privacy-safe message",
            contextString.contains("Disabled by user privacy preference")
        )
        assertFalse(
            "It must not contain any stats or profile headers",
            contextString.contains("[GENUINE PERSISTED APP CONTEXT]")
        )
    }

    @Test
    fun testBuildPersonalizedStudyContext_withPersonalizationEnabled_truthfulAndSanitized() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = LisanViewModel(app)

        // Enable consent
        viewModel.setStudyPersonalizationEnabled(true)

        val contextString = viewModel.buildPersonalizedStudyContext()

        // Verify the context header is present
        assertTrue("Must contain the authentic context header", contextString.contains("[GENUINE PERSISTED APP CONTEXT]"))

        // Verify it contains level tracking
        assertTrue("Must contain student level information", contextString.contains("- Level:"))

        // Verify that empty/zero-value stats are completely omitted to prevent hallucinations
        // In the newly initialized ViewModel, stats (like streak, words, achievements) are zero
        assertFalse("Zero or unavailable streak must be omitted", contextString.contains("Streak:"))
        assertFalse("Zero lessons completed must be omitted", contextString.contains("Lessons Completed:"))
        assertFalse("Zero vocabulary words learned must be omitted", contextString.contains("Picture Vocabulary Words Learned:"))
        assertFalse("Zero revision sessions completed must be omitted", contextString.contains("Revision Sessions Completed:"))
        assertFalse("Zero daily challenges completed must be omitted", contextString.contains("Daily Challenges Completed:"))
        assertFalse("Zero achievements must be omitted", contextString.contains("Unlocked Achievements:"))
    }
}
