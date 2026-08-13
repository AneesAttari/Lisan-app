package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.document.DocumentRetrievalEngine
import com.example.data.local.*
import com.example.data.model.*
import com.example.data.repository.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PerformanceAndScalabilityTest {

    private lateinit var db: AppDatabase
    private lateinit var chatDao: ChatMessageDao
    private lateinit var chatRepo: ChatMessageRepository
    private lateinit var libraryDao: LibraryDao
    private lateinit var likedDao: LikedDao
    private lateinit var dislikedDao: DislikedDao
    private lateinit var feedbackDao: FeedbackDao
    private lateinit var libraryRepo: LibraryRepository
    private lateinit var vocabDao: PictureVocabularyDao
    private lateinit var vocabRepo: PictureVocabularyRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        chatDao = db.chatMessageDao()
        chatRepo = ChatMessageRepository(chatDao)

        libraryDao = db.libraryDao()
        likedDao = db.likedDao()
        dislikedDao = db.dislikedDao()
        feedbackDao = db.feedbackDao()
        libraryRepo = LibraryRepository(libraryDao, likedDao, dislikedDao, feedbackDao)

        vocabDao = db.pictureVocabularyDao()
        vocabRepo = PictureVocabularyRepository(vocabDao)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun test365DaysQuizScalabilityOnDemand() {
        val startTime = System.currentTimeMillis()
        
        // Verify all 365 days can be generated dynamically on demand without huge RAM allocation
        for (day in 1..365) {
            val questions = DailyQuiz365Generator.getQuestionsForDay(day)
            assertEquals("Day $day should have 10 questions", 10, questions.size)
            assertTrue("Day title should contain Day number", questions.first().tenseTitleEn.contains("Day $day"))
        }

        val elapsedTime = System.currentTimeMillis() - startTime
        assertTrue("Generating 365 days of quizzes on demand should take less than 1000ms", elapsedTime < 1000)
    }

    @Test
    fun test40TopicsVocabularyDataset() = runBlocking {
        vocabRepo.initializeContentIfNeeded()

        val categoryCount = vocabDao.getTotalCategoryCount()
        assertEquals(40, categoryCount)

        val categories = vocabDao.getAllCategoriesList()
        assertEquals(40, categories.size)

        val totalQuestions = vocabDao.getTotalQuestionCount()
        assertEquals(40 * 26, totalQuestions)

        // Verify fast category retrieval
        val sampleCategory = categories[5]
        val items = vocabRepo.getItemsListForCategory(sampleCategory.id)
        assertEquals(26, items.size)
    }

    @Test
    fun testChatHistoryPaginationAndMemoryBounding() = runBlocking {
        // Insert 500 messages simulating a long-term conversation history
        val largeHistoryList = (1..500).map { index ->
            ChatMessageEntity(
                id = index.toLong(),
                role = if (index % 2 == 1) "user" else "model",
                text = "Message $index: English grammar rule testing long term chat history persistence.",
                timestamp = System.currentTimeMillis() + index
            )
        }
        chatDao.insertMessages(largeHistoryList)

        // Verify DB contains all 500 messages
        val totalList = chatDao.getAllMessagesList()
        assertEquals(500, totalList.size)

        // Verify paginated load fetches only recent 40 messages to conserve RAM
        val recentList = chatRepo.loadMessagesList()
        assertEquals(40, recentList.size)
        assertEquals("Message 500: English grammar rule testing long term chat history persistence.", recentList.last().parts.first().text)

        // Verify Flow emits bounded recent list
        val flowMessages = chatRepo.chatMessagesFlow.first()
        assertEquals(40, flowMessages.size)
    }

    @Test
    fun testLargeLibraryScalabilityAndSearch() = runBlocking {
        val startTime = System.currentTimeMillis()

        // Insert 300 library items
        val items = (1..300).map { i ->
            LibraryItem(
                id = "lib_$i",
                contentType = if (i % 2 == 0) "BOOK" else "LESSON",
                contentId = "item_$i",
                title = "English Grammar Volume $i",
                category = "GRAMMAR",
                savedTimestamp = System.currentTimeMillis() + i
            )
        }
        items.forEach { libraryRepo.saveToLibrary(it) }

        val loaded = libraryRepo.allLibraryItems.first()
        assertEquals(300, loaded.size)

        val duration = System.currentTimeMillis() - startTime
        assertTrue("Inserting and retrieving 300 library items should complete in under 1500ms", duration < 1500)
    }

    @Test
    fun testDocumentChunkingAndGroundingRetrieval() {
        val doc = DocumentEntity(
            documentId = "doc_test",
            fileName = "Advanced_English_Grammar.pdf",
            mimeType = "application/pdf",
            fileSize = 10485760L, // 10MB
            sourceUri = "content://pdf/test",
            processingStatus = ProcessingStatus.READY.name,
            chunkCount = 100
        )

        val chunks = (1..100).map { i ->
            DocumentChunkEntity(
                chunkId = "chunk_$i",
                documentId = "doc_test",
                chunkIndex = i,
                pageNumber = (i / 2) + 1,
                text = "Chunk $i discussion on conditional sentences (If clauses, type 1, type 2, type 3) in English grammar."
            )
        }

        // Verify top chunks retrieval limits context size
        val relevant = DocumentRetrievalEngine.retrieveRelevantChunks(
            document = doc,
            chunks = chunks,
            userQuery = "conditional sentences type 2",
            maxChunks = 4
        )

        assertEquals("Should retrieve at most 4 chunks for grounding", 4, relevant.size)

        val prompt = DocumentRetrievalEngine.buildGeminiGroundingPrompt(
            document = doc,
            relevantChunks = relevant,
            userQuery = "conditional sentences type 2"
        )

        assertTrue(prompt.contains("Advanced_English_Grammar.pdf"))
        assertTrue(prompt.contains("conditional sentences"))
    }
}
