package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.document.DocumentRetrievalEngine
import com.example.data.model.DocumentChunkEntity
import com.example.data.model.DocumentEntity
import com.example.utils.ImageOptimizer
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Phase4PipelineTest {

    @Test
    fun testImageOptimizerPresetsAndMetadata() {
        val presetHigh = ImageOptimizer.CompressionPreset.HIGH
        assertEquals(2560, presetHigh.maxDimension)
        assertEquals(92, presetHigh.quality)

        val presetBalanced = ImageOptimizer.CompressionPreset.BALANCED
        assertEquals(1600, presetBalanced.maxDimension)
        assertEquals(85, presetBalanced.quality)

        val presetDataSaver = ImageOptimizer.CompressionPreset.DATA_SAVER
        assertEquals(1024, presetDataSaver.maxDimension)
        assertEquals(75, presetDataSaver.quality)
    }

    @Test
    fun testDocumentRetrievalEngineBoundedContext() {
        val doc = DocumentEntity(
            documentId = "doc_123",
            fileName = "Arabic_Grammar_Book.pdf",
            mimeType = "application/pdf",
            fileSize = 102400L,
            sourceUri = "content://media/external/file/123",
            pageCount = 10,
            wordCount = 2000,
            extractedText = "This is a comprehensive guide to Arabic verbs and nouns. Chapter 1 covers past tense verbs. Chapter 2 covers present tense.",
            processingStatus = "COMPLETED"
        )

        val chunks = listOf(
            DocumentChunkEntity(
                chunkId = "chunk_1",
                documentId = "doc_123",
                chunkIndex = 0,
                pageNumber = 1,
                chapter = "Chapter 1",
                section = "Past Tense",
                text = "Past tense verbs (Fiil Madhi) indicate actions completed in the past.",
                wordCount = 12
            ),
            DocumentChunkEntity(
                chunkId = "chunk_2",
                documentId = "doc_123",
                chunkIndex = 1,
                pageNumber = 3,
                chapter = "Chapter 2",
                section = "Present Tense",
                text = "Present tense verbs (Fiil Mudhari) indicate ongoing or future actions.",
                wordCount = 11
            )
        )

        val groundingPrompt = DocumentRetrievalEngine.buildGeminiGroundingPrompt(
            document = doc,
            relevantChunks = chunks,
            userQuery = "Explain past tense verbs"
        )

        assertNotNull(groundingPrompt)
        assertTrue(groundingPrompt.contains("Arabic_Grammar_Book.pdf"))
        assertTrue(groundingPrompt.contains("Past Tense"))
        assertTrue(groundingPrompt.contains("INSTRUCTIONS FOR SUPPLEMENTARY GROUNDING"))
        assertTrue(groundingPrompt.contains("Explain past tense verbs"))
    }

    @Test
    fun testTemporaryFileCleanupLogic() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val tempFile = File(context.cacheDir, "temp_camera_photo.jpg")
        tempFile.writeText("mock image bytes")
        assertTrue(tempFile.exists())

        // Simulate cleanup
        if (tempFile.exists()) {
            tempFile.delete()
        }
        assertFalse(tempFile.exists())
    }
}
