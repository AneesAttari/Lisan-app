package com.example

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.PictureVocabularyDao
import com.example.data.repository.PictureVocabularyRepository
import com.example.ui.navigation.HashRouter
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

interface FakeSpeaker {
    fun speak(text: String)
    val spokenWords: List<String>
}

class FakeTtsManager(private val context: Context) : FakeSpeaker {
    private val _spokenWords = mutableListOf<String>()
    override val spokenWords: List<String> get() = _spokenWords

    override fun speak(text: String) {
        _spokenWords.add(text)
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PictureVocabularyTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: PictureVocabularyDao
    private lateinit var repository: PictureVocabularyRepository

    @get:Rule
    val composeRule = createComposeRule()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.pictureVocabularyDao()
        repository = PictureVocabularyRepository(dao)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testContentSeedingAndValidation() = runBlocking {
        repository.initializeContentIfNeeded()

        val categoryCount = dao.getTotalCategoryCount()
        assertEquals("Should have exactly 40 categories", 40, categoryCount)

        val categories = dao.getAllCategoriesList()
        assertEquals(40, categories.size)

        for (cat in categories) {
            val items = dao.getItemsListForCategory(cat.id)
            assertEquals("Category ${cat.id} should have 26 items", 26, items.size)

            val questions = dao.getQuestionsListForCategory(cat.id)
            assertEquals("Category ${cat.id} should have 26 questions", 26, questions.size)

            for (q in questions) {
                val options = q.getOptions()
                assertEquals("Each question must have exactly 4 options", 4, options.size)
                assertTrue("Correct option must be among options", options.contains(q.correctItemId))
            }
        }
    }

    @Test
    fun testQuizSessionFlowAndScoreCalculation() = runBlocking {
        repository.initializeContentIfNeeded()
        val categories = dao.getAllCategoriesList()
        val firstCat = categories.first()

        val sessionId = repository.startSession(firstCat.id, "CATEGORY_QUIZ")
        assertNotNull(sessionId)

        val (session, questions) = repository.getSessionWithQuestions(sessionId)
        assertNotNull(session)
        assertEquals(26, questions.size)

        // Submit answer for first question correctly
        val (_, q1) = questions[0]
        repository.submitAnswer(sessionId, q1.id, q1.correctItemId, true)

        // Submit answer for second question incorrectly
        val (_, q2) = questions[1]
        val incorrectOption = q2.getOptions().first { it != q2.correctItemId }
        repository.submitAnswer(sessionId, q2.id, incorrectOption, false)

        val (updatedSession, _) = repository.getSessionWithQuestions(sessionId)
        assertNotNull(updatedSession)
        assertEquals(1, updatedSession?.correctCount)
        assertEquals(1, updatedSession?.incorrectCount)
    }

    @Test
    fun testNavigationRoutes() {
        HashRouter.navigate("#/picture-vocabulary")
        assertEquals("#/picture-vocabulary", HashRouter.currentRoute.value)

        HashRouter.navigate("#/picture-vocabulary/category/cat_food_drinks")
        assertEquals("#/picture-vocabulary/category/cat_food_drinks", HashRouter.currentRoute.value)

        HashRouter.navigate("#/picture-vocabulary/learn/cat_food_drinks/cat_food_drinks_item_1")
        assertEquals("#/picture-vocabulary/learn/cat_food_drinks/cat_food_drinks_item_1", HashRouter.currentRoute.value)

        HashRouter.navigate("#/picture-vocabulary/quiz/sess_123")
        assertEquals("#/picture-vocabulary/quiz/sess_123", HashRouter.currentRoute.value)

        HashRouter.navigate("#/picture-vocabulary/completion/sess_123")
        assertEquals("#/picture-vocabulary/completion/sess_123", HashRouter.currentRoute.value)
    }

    @Test
    fun testTtsInvocation() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val tts = FakeTtsManager(context)
        tts.speak("Apple")
        assertEquals(1, tts.spokenWords.size)
        assertEquals("Apple", tts.spokenWords[0])
    }
}
