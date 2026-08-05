package com.example

import com.example.utils.TutorTtsManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TutorTtsManagerTest {

    @Test
    fun testFemaleVoicesAreBlockedForEnglish() {
        // Known English female voices
        assertTrue(TutorTtsManager.isFemaleVoiceId("en-US-Standard-A"))
        assertTrue(TutorTtsManager.isFemaleVoiceId("en-US-Standard-C"))
        assertTrue(TutorTtsManager.isFemaleVoiceId("en-US-Standard-E"))
        assertTrue(TutorTtsManager.isFemaleVoiceId("en-US-Standard-F"))
        assertTrue(TutorTtsManager.isFemaleVoiceId("en-US-Standard-G"))
        assertTrue(TutorTtsManager.isFemaleVoiceId("en-US-Standard-H"))
        assertTrue(TutorTtsManager.isFemaleVoiceId("en-US-Wavenet-A"))
        assertTrue(TutorTtsManager.isFemaleVoiceId("en-US-Wavenet-C"))

        // Male English voices must NOT be blocked
        assertFalse(TutorTtsManager.isFemaleVoiceId("en-US-Standard-D"))
        assertFalse(TutorTtsManager.isFemaleVoiceId("en-US-Standard-I"))
        assertFalse(TutorTtsManager.isFemaleVoiceId("en-US-Wavenet-D"))
    }

    @Test
    fun testFemaleVoicesAreBlockedForArabic() {
        // Known Arabic female voices
        assertTrue(TutorTtsManager.isFemaleVoiceId("ar-XA-Standard-A"))
        assertTrue(TutorTtsManager.isFemaleVoiceId("ar-XA-Standard-D"))
        assertTrue(TutorTtsManager.isFemaleVoiceId("ar-XA-Wavenet-A"))

        // Male Arabic voices must NOT be blocked
        assertFalse(TutorTtsManager.isFemaleVoiceId("ar-XA-Standard-B"))
        assertFalse(TutorTtsManager.isFemaleVoiceId("ar-XA-Wavenet-B"))
    }

    @Test
    fun testFemaleVoicesAreBlockedForUrdu() {
        // Known Urdu female voice
        assertTrue(TutorTtsManager.isFemaleVoiceId("ur-PK-Standard-A"))
        assertTrue(TutorTtsManager.isFemaleVoiceId("ur-PK-Wavenet-A"))

        // Male Urdu voices must NOT be blocked
        assertFalse(TutorTtsManager.isFemaleVoiceId("ur-PK-Wavenet-B"))
        assertFalse(TutorTtsManager.isFemaleVoiceId("ur-PK-Standard-B"))
    }
}
