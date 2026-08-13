package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.LisanJourneyDao
import com.example.data.model.*
import com.example.data.repository.LisanJourneyRepository
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
class LisanJourneyTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: LisanJourneyDao
    private lateinit var repository: LisanJourneyRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.lisanJourneyDao()
        repository = LisanJourneyRepository(dao)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInitializeAndFetchDefinitions() = runBlocking {
        repository.initializeDefinitionsIfNeeded()
        val defs = repository.activeDefinitions.first()
        assertTrue(defs.isNotEmpty())
        val firstStep = defs.find { it.id == "first_step" }
        assertNotNull(firstStep)
        assertEquals("getting_started", firstStep?.groupId)
    }

    @Test
    fun testRecordEventAndDeduplication() = runBlocking {
        repository.initializeDefinitionsIfNeeded()
        val success1 = repository.recordEvent(
            eventType = "LESSON_COMPLETED",
            featureSource = "GRAMMAR_LESSON",
            entityId = "lesson_1",
            deduplicationKey = "dedup_key_1"
        )
        assertTrue(success1)

        // Duplicate insert should be ignored
        val success2 = repository.recordEvent(
            eventType = "LESSON_COMPLETED",
            featureSource = "GRAMMAR_LESSON",
            entityId = "lesson_1",
            deduplicationKey = "dedup_key_1"
        )
        assertFalse(success2)

        val stats = repository.userStats.first()
        assertNotNull(stats)
        assertEquals(1L, stats?.totalGrammarLessonsCompleted)
    }

    @Test
    fun testDailyTutorTimeScheduleAndCompletion() = runBlocking {
        repository.initializeDefinitionsIfNeeded()

        // Enable schedule from 20:00 to 21:00
        val schedule = TutorTimeScheduleEntity(
            isEnabled = true,
            startTime = "20:00",
            endTime = "21:00"
        )
        repository.saveTutorSchedule(schedule)

        val retrievedSchedule = repository.tutorSchedule.first()
        assertNotNull(retrievedSchedule)
        assertTrue(retrievedSchedule!!.isEnabled)
        assertEquals("20:00", retrievedSchedule.startTime)
    }
}
