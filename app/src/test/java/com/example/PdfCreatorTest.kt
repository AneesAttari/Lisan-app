package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Content
import com.example.data.model.Part
import com.example.utils.PdfCreatorHelper
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PdfCreatorTest {

    @Test
    fun testPdfColorThemesDefinition() {
        val classic = PdfCreatorHelper.PdfColorTheme.CLASSIC_EDITORIAL
        assertNotNull(classic.primaryColor)
        assertNotNull(classic.cardBgColor)

        val emerald = PdfCreatorHelper.PdfColorTheme.EMERALD_STUDY
        assertNotNull(emerald.primaryColor)

        val purple = PdfCreatorHelper.PdfColorTheme.ROYAL_PURPLE
        assertNotNull(purple.primaryColor)

        val amber = PdfCreatorHelper.PdfColorTheme.WARM_AMBER
        assertNotNull(amber.primaryColor)
    }

    @Test
    fun testChatTranscriptBlockBuilding() {
        val messages = listOf(
            Content(
                role = "user",
                parts = listOf(Part(text = "Can you explain the Present Perfect tense?"))
            ),
            Content(
                role = "model",
                parts = listOf(Part(text = "Present Perfect tense links past actions to the present. In Urdu, it corresponds to فعل ماضی قریب (e.g. میں نے کام ختم کر لیا ہے)."))
            )
        )

        val blocks = PdfCreatorHelper.buildChatTranscriptBlocks(messages)
        assertTrue(blocks.size >= 3)
        assertTrue(blocks[0] is PdfCreatorHelper.PdfBlock.SectionHeader)
        
        val userBubble = blocks[1] as PdfCreatorHelper.PdfBlock.ChatBubble
        assertTrue(userBubble.isUser)
        assertEquals("Learner", userBubble.sender)
        assertTrue(userBubble.grammarTags.contains("Tense"))

        val tutorBubble = blocks[2] as PdfCreatorHelper.PdfBlock.ChatBubble
        assertFalse(tutorBubble.isUser)
        assertEquals("Lisan AI", tutorBubble.sender)
        assertTrue(tutorBubble.grammarTags.contains("Tense"))
    }

    @Test
    fun testCreatePdfDocumentGeneration() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val metadata = PdfCreatorHelper.PdfMetadata(
            title = "Test English Grammar Guide",
            subtitle = "Unit Test Document",
            learnerName = "Test Learner",
            proficiencyLevel = "Intermediate"
        )

        val blocks = listOf(
            PdfCreatorHelper.PdfBlock.SectionHeader("CHAPTER 1: INTRODUCTION", "Basic English & Urdu concepts"),
            PdfCreatorHelper.PdfBlock.Callout("Core Rule", "Every sentence must contain a subject and a verb.", "ہر جملے میں فاعل اور فعل کا ہونا لازمی ہے۔"),
            PdfCreatorHelper.PdfBlock.VocabCard("Obligation", "Noun", "فرض یا لازمی ذمہ داری", "Wearing a helmet is a legal obligation.", "ہیلمٹ پہننا قانونی فرض ہے۔"),
            PdfCreatorHelper.PdfBlock.ExerciseItem(
                number = 1,
                question = "He _______ (live) in London for five years.",
                options = listOf("has lived", "lives", "is living", "living"),
                urduHint = "ماضی سے شروع ہو کر اب تک جاری عمل",
                answerKey = "has lived"
            ),
            PdfCreatorHelper.PdfBlock.KeyValueRow("Noun", "Ism (اسم)", "نام"),
            PdfCreatorHelper.PdfBlock.Paragraph("This concludes the first introductory section of the test guide.")
        )

        val result = PdfCreatorHelper.createPdf(context, metadata, blocks)
        assertNotNull(result)
        assertTrue(result.file.exists())
        assertTrue(result.file.length() > 0)
        assertTrue(result.pageCount >= 1)
        assertNotNull(result.uri)
    }

    @Test
    fun testSavePdfToDownloads() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val tempFile = File(context.cacheDir, "test_sample.pdf").apply {
            writeText("%PDF-1.4 sample test bytes")
        }

        val success = PdfCreatorHelper.savePdfToDownloads(context, tempFile, "Test_Lisan_Guide.pdf")
        assertTrue(success)
    }
}
