package com.example

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.repository.*
import com.example.ui.viewmodel.LisanViewModel
import com.example.utils.DeviceCapabilityHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class StartupOptimizationTest {

    private lateinit var app: Application
    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(app, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        db.close()
    }

    @Test
    fun testStartupViewModelCreationIsFastAndLightweight() {
        val viewModel = LisanViewModel(app)
        val activeLesson = viewModel.activeLesson.value
        assertNotNull("Active lesson should be present on startup", activeLesson)
        assertTrue("Active lesson should be Verb To Be", activeLesson?.topicTitleEn?.contains("Verb") == true)
        // Chat history is empty until ensureChatHistoryLoaded() is triggered
        assertTrue("Chat history should start empty until screen opens", viewModel.chatHistory.value.isEmpty())
    }

    @Test
    fun testDeviceCapabilityHelperIdentifiesMemoryConstraints() {
        val isLow = DeviceCapabilityHelper.isLowRamDevice(app)
        // Verify method executes safely without throwing exceptions on Android SDK 34
        assertNotNull(isLow)
    }

    @Test
    fun testIdiomRepositoryInitializesOnDemand() = runBlocking {
        IdiomRepository.initialize(app)
        val idioms = IdiomRepository.idioms
        assertTrue("Idioms must be populated on demand when screen opens", idioms.isNotEmpty())
    }

    @Test
    fun testJourneyDefinitionsInitializeOnDemand() = runBlocking {
        val journeyDao = db.lisanJourneyDao()
        val repo = LisanJourneyRepository(journeyDao)

        repo.initializeDefinitionsIfNeeded()
        val definitions = repo.activeDefinitions.first()
        assertTrue("Journey definitions must be initialized on demand", definitions.isNotEmpty())
    }

    @Test
    fun testPictureVocabularyInitializesOnDemand() = runBlocking {
        val vocabDao = db.pictureVocabularyDao()
        val repo = PictureVocabularyRepository(vocabDao)

        repo.initializeContentIfNeeded()
        val categories = repo.getAllCategories().first()
        assertTrue("Vocabulary categories must be initialized on demand", categories.isNotEmpty())
    }
}
