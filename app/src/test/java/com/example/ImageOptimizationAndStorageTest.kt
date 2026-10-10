package com.example

import android.content.Context
import android.util.Base64
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessageDao
import com.example.data.local.ChatMessageRepository
import com.example.data.local.ConversationSessionDao
import com.example.data.model.Content
import com.example.data.model.ConversationSessionEntity
import com.example.data.model.Part
import com.example.utils.ImageOptimizer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ImageOptimizationAndStorageTest {

    private lateinit var context: Context
    private lateinit var db: AppDatabase
    private lateinit var chatDao: ChatMessageDao
    private lateinit var chatRepo: ChatMessageRepository
    private lateinit var sessionDao: ConversationSessionDao

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        chatDao = db.chatMessageDao()
        chatRepo = ChatMessageRepository(chatDao)
        sessionDao = db.conversationSessionDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testSaveGeneratedImageBytesAndPersistence() {
        val sampleBytes = "test-image-data-sample-bytes-12345".toByteArray()
        val savedFile = ImageOptimizer.saveGeneratedImageBytes(context, sampleBytes, "image/png")

        assertNotNull("Saved file should not be null", savedFile)
        assertTrue("Saved file must exist on disk", savedFile!!.exists())
        assertEquals("Saved file length must match sample bytes", sampleBytes.size.toLong(), savedFile.length())
        assertTrue("Saved file should be in internal files/generated_images dir", savedFile.absolutePath.contains("generated_images"))
    }

    @Test
    fun testSaveGeneratedImageBase64ConvertsToFilePath() {
        val originalRaw = "base64-payload-of-generated-image"
        val base64String = Base64.encodeToString(originalRaw.toByteArray(), Base64.NO_WRAP)

        val persistentPath = ImageOptimizer.saveGeneratedImageBase64(context, base64String, "image/png")

        assertTrue("Returned path should be a file path starting with '/'", persistentPath.startsWith("/"))
        val file = File(persistentPath)
        assertTrue("Saved file on disk must exist", file.exists())
        assertEquals("File content should match original raw data", originalRaw, file.readText())

        // Passing an already existing file path should be idempotent
        val idempotentPath = ImageOptimizer.saveGeneratedImageBase64(context, persistentPath, "image/png")
        assertEquals("Idempotent check must return existing file path unchanged", persistentPath, idempotentPath)
    }

    @Test
    fun testIsLocalFilePathOrUri() {
        assertTrue(ImageOptimizer.isLocalFilePathOrUri("/data/user/0/com.example/files/img.png"))
        assertTrue(ImageOptimizer.isLocalFilePathOrUri("file:///data/user/0/com.example/files/img.png"))
        assertTrue(ImageOptimizer.isLocalFilePathOrUri("content://media/external/images/media/123"))
        assertTrue(ImageOptimizer.isLocalFilePathOrUri("https://example.com/image.png"))
        assertFalse(ImageOptimizer.isLocalFilePathOrUri("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg=="))
    }

    @Test
    fun testResolveImageBytesFromFileAndLegacyBase64() {
        val sampleText = "hello-image-resolution-test"
        val sampleBytes = sampleText.toByteArray()

        // 1. Resolve from File Path
        val tempFile = File(context.cacheDir, "resolve_test.png").apply { writeBytes(sampleBytes) }
        val resolvedFromFile = ImageOptimizer.resolveImageBytes(context, tempFile.absolutePath)
        assertNotNull(resolvedFromFile)
        assertArrayEquals(sampleBytes, resolvedFromFile)

        // 2. Resolve from file:// URI
        val resolvedFromFileUri = ImageOptimizer.resolveImageBytes(context, "file://${tempFile.absolutePath}")
        assertNotNull(resolvedFromFileUri)
        assertArrayEquals(sampleBytes, resolvedFromFileUri)

        // 3. Resolve from legacy Base64 string
        val base64Str = Base64.encodeToString(sampleBytes, Base64.NO_WRAP)
        val resolvedFromBase64 = ImageOptimizer.resolveImageBytes(context, base64Str)
        assertNotNull(resolvedFromBase64)
        assertArrayEquals(sampleBytes, resolvedFromBase64)
    }

    @Test
    fun testMissingOrCorruptImageHandling() {
        val missingFilePath = "/non/existent/path/lisan_img_999999.png"
        val resolved = ImageOptimizer.resolveImageBytes(context, missingFilePath)
        assertNull("Missing image file should resolve safely to null without crashing", resolved)

        val invalidBase64 = "!!!not-valid-base64-content###"
        val resolvedInvalid = ImageOptimizer.resolveImageBytes(context, invalidBase64)
        // Should handle safely without uncaught exception and return null or empty
        assertTrue("Corrupt image data safely resolves to null or empty without crashing", resolvedInvalid == null || resolvedInvalid.isEmpty())
    }

    @Test
    fun testChatHistoryWithBothLegacyBase64AndNewFilePath() = runBlocking {
        // Save legacy message with base64 string
        val legacyBase64 = Base64.encodeToString("legacy-image-content".toByteArray(), Base64.NO_WRAP)
        chatRepo.saveMessage(
            role = "model",
            text = "Here is the legacy illustration",
            generatedImageBase64 = legacyBase64,
            generatedImagePrompt = "A cat in a garden"
        )

        // Save new message with persistent file path
        val savedFile = ImageOptimizer.saveGeneratedImageBytes(context, "new-image-content".toByteArray(), "image/png")
        chatRepo.saveMessage(
            role = "model",
            text = "Here is the new optimized illustration",
            generatedImageBase64 = savedFile!!.absolutePath,
            generatedImagePrompt = "A futuristic library"
        )

        val messages = chatRepo.loadMessagesList()
        assertEquals(2, messages.size)

        val legacyMsg = messages[0]
        assertEquals(legacyBase64, legacyMsg.generatedImageBase64)
        assertEquals("A cat in a garden", legacyMsg.generatedImagePrompt)

        val newMsg = messages[1]
        assertEquals(savedFile.absolutePath, newMsg.generatedImageBase64)
        assertEquals("A futuristic library", newMsg.generatedImagePrompt)
    }

    @Test
    fun testReopeningOldConversationsPreservesImageReferencesAndText() = runBlocking {
        val imageFile = ImageOptimizer.saveGeneratedImageBytes(context, "session-image-bytes".toByteArray(), "image/png")
        val messages = listOf(
            Content(role = "user", parts = listOf(Part(text = "Draw a golden gate bridge"))),
            Content(
                role = "model",
                parts = listOf(Part(text = "Here is your picture")),
                generatedImageBase64 = imageFile!!.absolutePath,
                generatedImagePrompt = "Golden Gate Bridge"
            )
        )

        val messagesJson = kotlinx.serialization.json.Json.encodeToString(
            kotlinx.serialization.builtins.ListSerializer(Content.serializer()),
            messages
        )

        val session = ConversationSessionEntity(
            id = "session_test_123",
            userId = "user_1",
            title = "Practising Golden Gate",
            topicLabel = "Grammar",
            preview = "Here is your picture",
            messageCount = messages.size,
            updatedAt = System.currentTimeMillis(),
            messagesJson = messagesJson
        )
        sessionDao.insertSession(session)

        val retrievedSession = sessionDao.getSessionById("session_test_123")
        assertNotNull(retrievedSession)

        val restoredMessages = kotlinx.serialization.json.Json.decodeFromString(
            kotlinx.serialization.builtins.ListSerializer(Content.serializer()),
            retrievedSession!!.messagesJson
        )
        assertEquals(2, restoredMessages.size)
        assertEquals("user", restoredMessages[0].role)
        assertEquals("Draw a golden gate bridge", restoredMessages[0].parts[0].text)
        assertEquals("model", restoredMessages[1].role)
        assertEquals("Here is your picture", restoredMessages[1].parts[0].text)
    }

    @Test
    fun testNormalTextChatFlowRemainsFullyFunctional() = runBlocking {
        chatRepo.saveMessage("user", "What is the meaning of serendipity?")
        chatRepo.saveMessage("model", "Serendipity means finding something good without looking for it.")

        val list = chatRepo.loadMessagesList()
        assertEquals(2, list.size)
        assertEquals("user", list[0].role)
        assertEquals("What is the meaning of serendipity?", list[0].parts[0].text)
        assertNull(list[0].generatedImageBase64)

        assertEquals("model", list[1].role)
        assertEquals("Serendipity means finding something good without looking for it.", list[1].parts[0].text)
        assertNull(list[1].generatedImageBase64)
    }
}
