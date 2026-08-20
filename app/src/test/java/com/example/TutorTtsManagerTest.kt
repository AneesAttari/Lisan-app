package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.utils.TutorTtsManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class TutorTtsManagerTest {

    private lateinit var context: Context
    private lateinit var ttsManager: TutorTtsManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        ttsManager = TutorTtsManager(context)
    }

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

    @Test
    fun testSegmentTextByScript() {
        // Simple English only
        val segs1 = ttsManager.segmentTextByScript("Hello World")
        assertEquals(1, segs1.size)
        assertEquals("Hello World", segs1[0].first)
        assertEquals("latin", segs1[0].second)

        // Simple Arabic script only
        val segs2 = ttsManager.segmentTextByScript("السلام عليكم")
        assertEquals(1, segs2.size)
        assertEquals("السلام عليكم", segs2[0].first)
        assertEquals("arabic_script", segs2[0].second)

        // Mixed sentence
        val segs3 = ttsManager.segmentTextByScript("The word is 'کتاب' which means book.")
        assertTrue(segs3.size >= 3)
        assertTrue(segs3.any { it.second == "latin" && it.first.contains("The word is") })
        assertTrue(segs3.any { it.second == "arabic_script" && it.first.contains("کتاب") })
        assertTrue(segs3.any { it.second == "latin" && it.first.contains("which means book") })
    }

    @Test
    fun testDetectArabicOrUrdu() {
        // Distinct Urdu features
        val isUrdu = ttsManager.detectArabicOrUrdu("یہ ایک خوبصورت کتاب ہے جس میں بہت سے جملے ہیں۔")
        assertEquals("ur", isUrdu)

        // Distinct Arabic features
        val isArabic = ttsManager.detectArabicOrUrdu("هذا الدرس يتحدث عن قواعد اللغة العربية في الجملة الاسمية.")
        assertEquals("ar", isArabic)
    }

    @Test
    fun testSplitTextByLanguage() {
        // Bilingual sentence: English -> Urdu -> English
        val chunks = ttsManager.splitTextByLanguage("Translate the sentence: 'آپ کیسے ہیں؟' into English.", 1.0f)
        assertTrue(chunks.size >= 2)
        assertEquals("en", chunks[0].languageTag)
        assertEquals("ur", chunks[1].languageTag)
    }
}
