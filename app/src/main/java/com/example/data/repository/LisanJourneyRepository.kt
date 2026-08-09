package com.example.data.repository

import com.example.data.local.LisanJourneyDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class LisanJourneyRepository(private val dao: LisanJourneyDao) {

    val activeDefinitions: Flow<List<AchievementDefinitionEntity>> = dao.getAllActiveDefinitions()
    val allProgress: Flow<List<AchievementProgressEntity>> = dao.getAllProgress()
    val userStats: Flow<UserAchievementStatsEntity?> = dao.getUserStatsFlow()
    val tutorSchedule: Flow<TutorTimeScheduleEntity?> = dao.getTutorScheduleFlow()
    val pendingRewards: Flow<List<RewardTransactionEntity>> = dao.getPendingRewards()

    suspend fun initializeDefinitionsIfNeeded() {
        val existing = dao.getAllActiveDefinitions().first()
        if (existing.isNotEmpty()) return

        val definitions = listOf(
            // Getting Started
            AchievementDefinitionEntity(
                id = "first_step",
                groupId = "getting_started",
                titleEnglish = "First Step",
                titleUrdu = "پہلا قدم",
                descriptionEnglish = "Complete your first meaningful learning activity.",
                descriptionUrdu = "اپنی پہلی بامقصد سیکھنے کی سرگرمی مکمل کریں۔",
                iconKey = "emoji_events",
                requirementType = "FIRST_ACTIVITY",
                targetValue = 1,
                tier = 1,
                sortOrder = 1
            ),
            AchievementDefinitionEntity(
                id = "first_tutor",
                groupId = "tutor",
                titleEnglish = "First Tutor Talk",
                titleUrdu = "پہلی ٹیوٹر گفتگو",
                descriptionEnglish = "Complete your first valid tutor conversation.",
                descriptionUrdu = "اپنی پہلی درست ٹیوٹر گفتگو مکمل کریں۔",
                iconKey = "smart_toy",
                requirementType = "TUTOR_CONVERSATION_COUNT",
                targetValue = 1,
                tier = 1,
                sortOrder = 2
            ),
            AchievementDefinitionEntity(
                id = "first_grammar",
                groupId = "grammar_revision",
                titleEnglish = "First Grammar Win",
                titleUrdu = "پہلی گرامر کامیابی",
                descriptionEnglish = "Complete your first grammar lesson or practice session.",
                descriptionUrdu = "اپنا پہلا گرامر سبق یا پریکٹس سیشن مکمل کریں۔",
                iconKey = "school",
                requirementType = "GRAMMAR_LESSON_COUNT",
                targetValue = 1,
                tier = 1,
                sortOrder = 3
            ),
            AchievementDefinitionEntity(
                id = "first_revision",
                groupId = "grammar_revision",
                titleEnglish = "First Revision",
                titleUrdu = "پہلی دہرائی",
                descriptionEnglish = "Complete your first revision session.",
                descriptionUrdu = "اپنا پہلا دہرائی کا سیشن مکمل کریں۔",
                iconKey = "history",
                requirementType = "REVISION_SESSION_COUNT",
                targetValue = 1,
                tier = 1,
                sortOrder = 4
            ),
            AchievementDefinitionEntity(
                id = "first_picture",
                groupId = "picture_vocabulary",
                titleEnglish = "First Picture Lesson",
                titleUrdu = "پہلا تصویری سبق",
                descriptionEnglish = "Complete your first Picture Vocabulary session.",
                descriptionUrdu = "اپنا پہلا تصویری ذخیرہ الفاظ کا سیشن مکمل کریں۔",
                iconKey = "image",
                requirementType = "PICTURE_CATEGORY_COUNT",
                targetValue = 1,
                tier = 1,
                sortOrder = 5
            ),
            AchievementDefinitionEntity(
                id = "first_analyzer",
                groupId = "sentence_analyzer",
                titleEnglish = "First Sentence Check",
                titleUrdu = "پہلا جملہ تجزیہ",
                descriptionEnglish = "Complete your first valid sentence analysis.",
                descriptionUrdu = "اپنا پہلا درست جملے کا تجزیہ مکمل کریں۔",
                iconKey = "psychology",
                requirementType = "SENTENCE_ANALYSIS_COUNT",
                targetValue = 1,
                tier = 1,
                sortOrder = 6
            ),
            AchievementDefinitionEntity(
                id = "first_challenge",
                groupId = "daily_challenge",
                titleEnglish = "First Daily Challenge",
                titleUrdu = "پہلا روزانہ چیلنج",
                descriptionEnglish = "Complete your first daily challenge.",
                descriptionUrdu = "اپنا پہلا روزانہ چیلنج مکمل کریں۔",
                iconKey = "bolt",
                requirementType = "DAILY_CHALLENGE_COUNT",
                targetValue = 1,
                tier = 1,
                sortOrder = 7
            ),
            AchievementDefinitionEntity(
                id = "first_pronunciation",
                groupId = "pronunciation",
                titleEnglish = "First Speaking Step",
                titleUrdu = "پہلا بولنے کا قدم",
                descriptionEnglish = "Complete your first pronunciation or listening session.",
                descriptionUrdu = "اپنی پہلی آواز یا بولنے کی پریکٹس مکمل کریں۔",
                iconKey = "mic",
                requirementType = "PRONUNCIATION_COUNT",
                targetValue = 1,
                tier = 1,
                sortOrder = 8
            ),

            // Daily Habits & Daily Tutor Time
            AchievementDefinitionEntity(
                id = "tutor_time_complete_1",
                groupId = "daily_habit",
                titleEnglish = "Tutor Time Complete",
                titleUrdu = "ٹیوٹر کا مقررہ وقت مکمل",
                descriptionEnglish = "Complete a valid tutor conversation during your planned Daily Tutor Time.",
                descriptionUrdu = "اپنے مقررہ ٹیوٹر وقت کے دوران ایک درست گفتگو مکمل کریں۔",
                iconKey = "alarm",
                requirementType = "ON_TIME_TUTOR_COMPLETION_COUNT",
                targetValue = 1,
                tier = 1,
                sortOrder = 10
            ),
            AchievementDefinitionEntity(
                id = "tutor_time_complete_3",
                groupId = "daily_habit",
                titleEnglish = "Planned Practice",
                titleUrdu = "مقررہ مشق",
                descriptionEnglish = "Complete Daily Tutor Time on 3 separate eligible days.",
                descriptionUrdu = "3 مختلف دنوں میں مقررہ ٹیوٹر وقت مکمل کریں۔",
                iconKey = "event_available",
                requirementType = "ON_TIME_TUTOR_COMPLETION_COUNT",
                targetValue = 3,
                tier = 2,
                sortOrder = 11
            ),
            AchievementDefinitionEntity(
                id = "tutor_time_complete_15",
                groupId = "daily_habit",
                titleEnglish = "Routine Builder",
                titleUrdu = "روٹین بنانے والا",
                descriptionEnglish = "Complete Daily Tutor Time on 15 eligible days.",
                descriptionUrdu = "15 دنوں میں مقررہ ٹیوٹر وقت مکمل کریں۔",
                iconKey = "date_range",
                requirementType = "ON_TIME_TUTOR_COMPLETION_COUNT",
                targetValue = 15,
                tier = 3,
                sortOrder = 12
            ),
            AchievementDefinitionEntity(
                id = "tutor_time_complete_50",
                groupId = "daily_habit",
                titleEnglish = "Reliable Learner",
                titleUrdu = "قابل اعتماد سیکھنے والا",
                descriptionEnglish = "Complete Daily Tutor Time on 50 eligible days.",
                descriptionUrdu = "50 دنوں میں مقررہ ٹیوٹر وقت مکمل کریں۔",
                iconKey = "verified",
                requirementType = "ON_TIME_TUTOR_COMPLETION_COUNT",
                targetValue = 50,
                tier = 4,
                sortOrder = 13
            ),
            AchievementDefinitionEntity(
                id = "tutor_time_complete_100",
                groupId = "daily_habit",
                titleEnglish = "Tutor Time Master",
                titleUrdu = "ٹیوٹر ٹائم ماسٹر",
                descriptionEnglish = "Complete Daily Tutor Time on 100 eligible days.",
                descriptionUrdu = "100 دنوں میں مقررہ ٹیوٹر وقت مکمل کریں۔",
                iconKey = "workspace_premium",
                requirementType = "ON_TIME_TUTOR_COMPLETION_COUNT",
                targetValue = 100,
                tier = 5,
                sortOrder = 14
            ),

            // Streaks (Learning & On-Time Tutor Streaks)
            AchievementDefinitionEntity(
                id = "streak_3",
                groupId = "streak",
                titleEnglish = "3-Day Learning Streak",
                titleUrdu = "3 دن کا سیکھنے کا تسلسل",
                descriptionEnglish = "Maintain a learning streak for 3 consecutive days.",
                descriptionUrdu = "مسلسل 3 دن پڑھائی کا تسلسل جاری رکھیں۔",
                iconKey = "whatshot",
                requirementType = "LEARNING_STREAK_DAYS",
                targetValue = 3,
                tier = 1,
                sortOrder = 20
            ),
            AchievementDefinitionEntity(
                id = "streak_7",
                groupId = "streak",
                titleEnglish = "7-Day Learning Streak",
                titleUrdu = "7 دن کا سیکھنے کا تسلسل",
                descriptionEnglish = "Maintain a learning streak for 7 consecutive days.",
                descriptionUrdu = "مسلسل 7 دن پڑھائی کا تسلسل جاری رکھیں۔",
                iconKey = "whatshot",
                requirementType = "LEARNING_STREAK_DAYS",
                targetValue = 7,
                tier = 2,
                sortOrder = 21
            ),
            AchievementDefinitionEntity(
                id = "streak_14",
                groupId = "streak",
                titleEnglish = "14-Day Learning Streak",
                titleUrdu = "14 دن کا سیکھنے کا تسلسل",
                descriptionEnglish = "Maintain a learning streak for 14 consecutive days.",
                descriptionUrdu = "مسلسل 14 دن پڑھائی کا تسلسل جاری رکھیں۔",
                iconKey = "whatshot",
                requirementType = "LEARNING_STREAK_DAYS",
                targetValue = 14,
                tier = 3,
                sortOrder = 22
            ),
            AchievementDefinitionEntity(
                id = "streak_30",
                groupId = "streak",
                titleEnglish = "30-Day Learning Streak",
                titleUrdu = "30 دن کا سیکھنے کا تسلسل",
                descriptionEnglish = "Maintain a learning streak for 30 consecutive days.",
                descriptionUrdu = "مسلسل 30 دن پڑھائی کا تسلسل جاری رکھیں۔",
                iconKey = "local_fire_department",
                requirementType = "LEARNING_STREAK_DAYS",
                targetValue = 30,
                tier = 4,
                sortOrder = 23
            ),
            AchievementDefinitionEntity(
                id = "streak_100",
                groupId = "streak",
                titleEnglish = "100-Day Learning Streak",
                titleUrdu = "100 دن کا سیکھنے کا تسلسل",
                descriptionEnglish = "Maintain a learning streak for 100 consecutive days.",
                descriptionUrdu = "مسلسل 100 دن پڑھائی کا تسلسل جاری رکھیں۔",
                iconKey = "local_fire_department",
                requirementType = "LEARNING_STREAK_DAYS",
                targetValue = 100,
                tier = 5,
                sortOrder = 24
            ),

            AchievementDefinitionEntity(
                id = "on_time_streak_3",
                groupId = "streak",
                titleEnglish = "3-Day On-Time Tutor Streak",
                titleUrdu = "3 دن کا وقت پر ٹیوٹر تسلسل",
                descriptionEnglish = "Maintain an On-Time Tutor Streak for 3 eligible days.",
                descriptionUrdu = "مسلسل 3 اہل دنوں میں وقت پر ٹیوٹر پریکٹس کریں۔",
                iconKey = "timer",
                requirementType = "ON_TIME_TUTOR_STREAK_DAYS",
                targetValue = 3,
                tier = 1,
                sortOrder = 30
            ),
            AchievementDefinitionEntity(
                id = "on_time_streak_7",
                groupId = "streak",
                titleEnglish = "7-Day On-Time Tutor Streak",
                titleUrdu = "7 دن کا وقت پر ٹیوٹر تسلسل",
                descriptionEnglish = "Maintain an On-Time Tutor Streak for 7 eligible days.",
                descriptionUrdu = "مسلسل 7 اہل دنوں میں وقت پر ٹیوٹر پریکٹس کریں۔",
                iconKey = "timer",
                requirementType = "ON_TIME_TUTOR_STREAK_DAYS",
                targetValue = 7,
                tier = 2,
                sortOrder = 31
            ),
            AchievementDefinitionEntity(
                id = "on_time_streak_30",
                groupId = "streak",
                titleEnglish = "30-Day On-Time Tutor Streak",
                titleUrdu = "30 دن کا وقت پر ٹیوٹر تسلسل",
                descriptionEnglish = "Maintain an On-Time Tutor Streak for 30 eligible days.",
                descriptionUrdu = "مسلسل 30 اہل دنوں میں وقت پر ٹیوٹر پریکٹس کریں۔",
                iconKey = "timer",
                requirementType = "ON_TIME_TUTOR_STREAK_DAYS",
                targetValue = 30,
                tier = 3,
                sortOrder = 32
            ),

            // Tutor Chat Achievements
            AchievementDefinitionEntity(
                id = "tutor_10",
                groupId = "tutor",
                titleEnglish = "Curious Learner",
                titleUrdu = "متجسس طالب علم",
                descriptionEnglish = "Complete 10 valid tutor conversations.",
                descriptionUrdu = "10 درست ٹیوٹر گفتگو مکمل کریں۔",
                iconKey = "chat",
                requirementType = "TUTOR_CONVERSATION_COUNT",
                targetValue = 10,
                tier = 2,
                sortOrder = 40
            ),
            AchievementDefinitionEntity(
                id = "tutor_50",
                groupId = "tutor",
                titleEnglish = "Practice Partner",
                titleUrdu = "مشق کا ساتھی",
                descriptionEnglish = "Complete 50 valid tutor conversations.",
                descriptionUrdu = "50 درست ٹیوٹر گفتگو مکمل کریں۔",
                iconKey = "forum",
                requirementType = "TUTOR_CONVERSATION_COUNT",
                targetValue = 50,
                tier = 3,
                sortOrder = 41
            ),
            AchievementDefinitionEntity(
                id = "tutor_100",
                groupId = "tutor",
                titleEnglish = "Tutor Regular",
                titleUrdu = "ٹیوٹر کا باقاعدہ ساتھی",
                descriptionEnglish = "Complete 100 valid tutor conversations.",
                descriptionUrdu = "100 درست ٹیوٹر گفتگو مکمل کریں۔",
                iconKey = "record_voice_over",
                requirementType = "TUTOR_CONVERSATION_COUNT",
                targetValue = 100,
                tier = 4,
                sortOrder = 42
            ),
            AchievementDefinitionEntity(
                id = "tutor_500",
                groupId = "tutor",
                titleEnglish = "Fluent Explorer",
                titleUrdu = "روانی کا متلاشی",
                descriptionEnglish = "Complete 500 valid tutor conversations.",
                descriptionUrdu = "500 درست ٹیوٹر گفتگو مکمل کریں۔",
                iconKey = "campaign",
                requirementType = "TUTOR_CONVERSATION_COUNT",
                targetValue = 500,
                tier = 5,
                sortOrder = 43
            ),

            // Grammar & Revision
            AchievementDefinitionEntity(
                id = "grammar_5",
                groupId = "grammar_revision",
                titleEnglish = "Grammar Explorer",
                titleUrdu = "گرامر کا متلاشی",
                descriptionEnglish = "Complete 5 grammar lessons.",
                descriptionUrdu = "5 گرامر اسباق مکمل کریں۔",
                iconKey = "menu_book",
                requirementType = "GRAMMAR_LESSON_COUNT",
                targetValue = 5,
                tier = 2,
                sortOrder = 50
            ),
            AchievementDefinitionEntity(
                id = "grammar_25",
                groupId = "grammar_revision",
                titleEnglish = "Grammar Builder",
                titleUrdu = "گرامر ساز",
                descriptionEnglish = "Complete 25 grammar lessons.",
                descriptionUrdu = "25 گرامر اسباق مکمل کریں۔",
                iconKey = "library_books",
                requirementType = "GRAMMAR_LESSON_COUNT",
                targetValue = 25,
                tier = 3,
                sortOrder = 51
            ),
            AchievementDefinitionEntity(
                id = "revision_10",
                groupId = "grammar_revision",
                titleEnglish = "Revision Regular",
                titleUrdu = "دہرائی کا باقاعدہ",
                descriptionEnglish = "Complete 10 revision sessions.",
                descriptionUrdu = "10 دہرائی کے سیشنز مکمل کریں۔",
                iconKey = "update",
                requirementType = "REVISION_SESSION_COUNT",
                targetValue = 10,
                tier = 2,
                sortOrder = 55
            ),
            AchievementDefinitionEntity(
                id = "revision_50",
                groupId = "grammar_revision",
                titleEnglish = "Revision Builder",
                titleUrdu = "دہرائی ساز",
                descriptionEnglish = "Complete 50 revision sessions.",
                descriptionUrdu = "50 دہرائی کے سیشنز مکمل کریں۔",
                iconKey = "autorenew",
                requirementType = "REVISION_SESSION_COUNT",
                targetValue = 50,
                tier = 3,
                sortOrder = 56
            ),

            // Vocabulary & Picture Learning
            AchievementDefinitionEntity(
                id = "vocab_25",
                groupId = "picture_vocabulary",
                titleEnglish = "Word Collector",
                titleUrdu = "الفاظ کا جمع کنندہ",
                descriptionEnglish = "Learn 25 vocabulary words.",
                descriptionUrdu = "25 الفاظ سیکھیں۔",
                iconKey = "collections_bookmark",
                requirementType = "PICTURE_WORD_COUNT",
                targetValue = 25,
                tier = 2,
                sortOrder = 60
            ),
            AchievementDefinitionEntity(
                id = "vocab_100",
                groupId = "picture_vocabulary",
                titleEnglish = "Vocabulary Builder",
                titleUrdu = "الفاظ کا ذخیرہ ساز",
                descriptionEnglish = "Learn 100 vocabulary words.",
                descriptionUrdu = "100 الفاظ سیکھیں۔",
                iconKey = "style",
                requirementType = "PICTURE_WORD_COUNT",
                targetValue = 100,
                tier = 3,
                sortOrder = 61
            ),
            AchievementDefinitionEntity(
                id = "picture_5",
                groupId = "picture_vocabulary",
                titleEnglish = "Picture Explorer",
                titleUrdu = "تصویری متلاشی",
                descriptionEnglish = "Complete 5 Picture Vocabulary categories.",
                descriptionUrdu = "5 تصویری زمرے مکمل کریں۔",
                iconKey = "photo_library",
                requirementType = "PICTURE_CATEGORY_COUNT",
                targetValue = 5,
                tier = 2,
                sortOrder = 65
            ),

            // Pronunciation & Listening
            AchievementDefinitionEntity(
                id = "pronunciation_10",
                groupId = "pronunciation",
                titleEnglish = "Speaking Starter",
                titleUrdu = "بولنے کی ابتدا",
                descriptionEnglish = "Complete 10 valid speaking/pronunciation activities.",
                descriptionUrdu = "10 درست بولنے کی سرگرمیاں مکمل کریں۔",
                iconKey = "mic_none",
                requirementType = "PRONUNCIATION_COUNT",
                targetValue = 10,
                tier = 2,
                sortOrder = 70
            ),
            AchievementDefinitionEntity(
                id = "pronunciation_50",
                groupId = "pronunciation",
                titleEnglish = "Clear Practice",
                titleUrdu = "واضح مشق",
                descriptionEnglish = "Complete 50 pronunciation activities.",
                descriptionUrdu = "50 تلفظ کی سرگرمیاں مکمل کریں۔",
                iconKey = "headphones",
                requirementType = "PRONUNCIATION_COUNT",
                targetValue = 50,
                tier = 3,
                sortOrder = 71
            ),

            // Sentence Analyzer
            AchievementDefinitionEntity(
                id = "analyzer_10",
                groupId = "sentence_analyzer",
                titleEnglish = "Sentence Explorer",
                titleUrdu = "جملہ تجزیہ کار",
                descriptionEnglish = "Complete 10 valid sentence analyses.",
                descriptionUrdu = "10 درست جملوں کا تجزیہ مکمل کریں۔",
                iconKey = "fact_check",
                requirementType = "SENTENCE_ANALYSIS_COUNT",
                targetValue = 10,
                tier = 2,
                sortOrder = 80
            ),

            // Daily Challenge
            AchievementDefinitionEntity(
                id = "challenge_7",
                groupId = "daily_challenge",
                titleEnglish = "Challenge Regular",
                titleUrdu = "چیلنج کا باقاعدہ",
                descriptionEnglish = "Complete 7 daily challenges.",
                descriptionUrdu = "7 روزانہ چیلنجز مکمل کریں۔",
                iconKey = "local_fire_department",
                requirementType = "DAILY_CHALLENGE_COUNT",
                targetValue = 7,
                tier = 2,
                sortOrder = 90
            ),
            AchievementDefinitionEntity(
                id = "challenge_30",
                groupId = "daily_challenge",
                titleEnglish = "Challenge Builder",
                titleUrdu = "چیلنج بلڈر",
                descriptionEnglish = "Complete 30 daily challenges.",
                descriptionUrdu = "30 روزانہ چیلنجز مکمل کریں۔",
                iconKey = "emoji_events",
                requirementType = "DAILY_CHALLENGE_COUNT",
                targetValue = 30,
                tier = 3,
                sortOrder = 91
            )
        )

        dao.insertDefinitions(definitions)
    }

    suspend fun recordEvent(
        eventType: String,
        featureSource: String,
        entityId: String? = null,
        sessionId: String? = null,
        metadataJson: String? = null,
        deduplicationKey: String
    ): Boolean {
        val now = System.currentTimeMillis()
        val localDateStr = getLocalDateString(now)

        val event = LearningEventEntity(
            id = UUID.randomUUID().toString(),
            eventType = eventType,
            featureSource = featureSource,
            occurredAt = now,
            localDate = localDateStr,
            entityId = entityId,
            sessionId = sessionId,
            metadataJson = metadataJson,
            deduplicationKey = deduplicationKey
        )

        val rowId = dao.insertEvent(event)
        if (rowId == -1L) {
            // Duplicate event ignored by UNIQUE index on deduplicationKey
            return false
        }

        // Update daily summary and user stats / achievements
        processLearningActivityEvent(event, localDateStr)
        return true
    }

    private suspend fun processLearningActivityEvent(event: LearningEventEntity, localDateStr: String) {
        // 1. Update Daily Summary
        var summary = dao.getDailySummary(localDateStr)
        if (summary == null) {
            summary = DailyLearningSummaryEntity(localDate = localDateStr)
        }

        val activityTypes = try {
            org.json.JSONArray(summary.completedActivityTypesJson).let { arr ->
                val list = mutableSetOf<String>()
                for (i in 0 until arr.length()) {
                    list.add(arr.getString(i))
                }
                list
            }
        } catch (e: Exception) {
            mutableSetOf()
        }

        activityTypes.add(event.featureSource)
        val jsonArrayStr = org.json.JSONArray(activityTypes.toList()).toString()

        val updatedSummary = summary.copy(
            completedActivityTypesJson = jsonArrayStr,
            totalMeaningfulActivities = summary.totalMeaningfulActivities + 1,
            tutorConversationCompleted = summary.tutorConversationCompleted || event.featureSource == "TUTOR_CHAT",
            grammarActivityCompleted = summary.grammarActivityCompleted || event.featureSource == "GRAMMAR_LESSON",
            revisionActivityCompleted = summary.revisionActivityCompleted || event.featureSource == "REVISION",
            pictureVocabularyActivityCompleted = summary.pictureVocabularyActivityCompleted || event.featureSource == "PICTURE_VOCABULARY",
            pronunciationActivityCompleted = summary.pronunciationActivityCompleted || event.featureSource == "PRONUNCIATION",
            sentenceAnalysisCompleted = summary.sentenceAnalysisCompleted || event.featureSource == "SENTENCE_ANALYZER",
            dailyChallengeCompleted = summary.dailyChallengeCompleted || event.featureSource == "DAILY_CHALLENGE",
            updatedAt = System.currentTimeMillis()
        )
        dao.insertOrUpdateDailySummary(updatedSummary)

        // 2. Update Streaks & User Stats
        updateStreaksAndStats(localDateStr, event)
    }

    private suspend fun updateStreaksAndStats(localDateStr: String, event: LearningEventEntity) {
        var stats = dao.getUserStats() ?: UserAchievementStatsEntity()

        // Check if learning streak needs update for today
        val cal = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = dateFormat.parse(localDateStr) ?: Date()

        val lastStreakDateStr = prefsGetLastStreakDate()
        if (lastStreakDateStr != localDateStr) {
            // Different day
            val calendarYesterday = Calendar.getInstance().apply {
                time = todayDate
                add(Calendar.DAY_OF_YEAR, -1)
            }
            val yesterdayStr = dateFormat.format(calendarYesterday.time)

            val newStreak = if (lastStreakDateStr == yesterdayStr) {
                stats.currentLearningStreak + 1
            } else {
                1 // reset or start new streak
            }
            val bestStreak = maxOf(newStreak, stats.bestLearningStreak)

            stats = stats.copy(
                currentLearningStreak = newStreak,
                bestLearningStreak = bestStreak
            )
            prefsSetLastStreakDate(localDateStr)
        }

        // Increment specific totals based on event featureSource / eventType
        stats = when (event.featureSource) {
            "TUTOR_CHAT" -> stats.copy(totalTutorConversations = stats.totalTutorConversations + 1)
            "GRAMMAR_LESSON" -> stats.copy(totalGrammarLessonsCompleted = stats.totalGrammarLessonsCompleted + 1)
            "REVISION" -> stats.copy(totalRevisionSessionsCompleted = stats.totalRevisionSessionsCompleted + 1)
            "PICTURE_VOCABULARY" -> {
                if (event.eventType == "WORD_LEARNED") stats.copy(totalPictureVocabularyWordsLearned = stats.totalPictureVocabularyWordsLearned + 1)
                else if (event.eventType == "CATEGORY_COMPLETED") stats.copy(totalPictureVocabularyCategoriesCompleted = stats.totalPictureVocabularyCategoriesCompleted + 1)
                else stats
            }
            "PRONUNCIATION" -> stats.copy(totalPronunciationSessionsCompleted = stats.totalPronunciationSessionsCompleted + 1)
            "SENTENCE_ANALYZER" -> stats.copy(totalSentenceAnalysesCompleted = stats.totalSentenceAnalysesCompleted + 1)
            "DAILY_CHALLENGE" -> stats.copy(totalDailyChallengesCompleted = stats.totalDailyChallengesCompleted + 1)
            else -> stats
        }

        stats = stats.copy(updatedAt = System.currentTimeMillis())
        dao.insertOrUpdateUserStats(stats)

        // Evaluate all achievements against updated stats
        evaluateAllAchievements(stats)
    }

    suspend fun checkDailyTutorTimeCompletion(sessionId: String, completionTimeMillis: Long): Boolean {
        val schedule = dao.getTutorSchedule() ?: return false
        if (!schedule.isEnabled || schedule.pausedAt != null) return false

        val localDateStr = getLocalDateString(completionTimeMillis)
        
        // Check if day of week is selected
        val cal = Calendar.getInstance().apply { timeInMillis = completionTimeMillis }
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // Sunday=1, Monday=2...
        // Map to 1=Monday ... 7=Sunday
        val isoDayOfWeek = if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1

        val selectedDays = try {
            org.json.JSONArray(schedule.selectedDaysJson).let { arr ->
                val list = mutableListOf<Int>()
                for (i in 0 until arr.length()) list.add(arr.getInt(i))
                list
            }
        } catch (e: Exception) {
            listOf(1, 2, 3, 4, 5, 6, 7)
        }

        if (!selectedDays.contains(isoDayOfWeek)) return false

        // Check if already completed today
        val existingCompletion = dao.getTutorCompletionForDate(localDateStr)
        if (existingCompletion != null && existingCompletion.rewardGranted) return false

        // Check time window (handling cross-midnight)
        val isInside = isTimeInsideWindow(completionTimeMillis, schedule.startTime, schedule.endTime, schedule.timezoneId)
        if (!isInside) return false

        // Grant Daily Tutor Time reward & update On-Time Tutor Streak
        var stats = dao.getUserStats() ?: UserAchievementStatsEntity()
        val newOnTimeStreak = stats.currentOnTimeTutorStreak + 1
        val bestOnTimeStreak = maxOf(newOnTimeStreak, stats.bestOnTimeTutorStreak)

        stats = stats.copy(
            currentOnTimeTutorStreak = newOnTimeStreak,
            bestOnTimeTutorStreak = bestOnTimeStreak,
            totalOnTimeTutorCompletions = stats.totalOnTimeTutorCompletions + 1,
            lisanPointsBalance = stats.lisanPointsBalance + 100,
            updatedAt = System.currentTimeMillis()
        )
        dao.insertOrUpdateUserStats(stats)

        val completionEntity = TutorTimeDailyCompletionEntity(
            localDate = localDateStr,
            scheduleId = schedule.id,
            tutorSessionId = sessionId,
            completedAt = completionTimeMillis,
            wasInsideWindow = true,
            rewardGranted = true,
            streakCountAfterCompletion = newOnTimeStreak
        )
        dao.insertTutorCompletion(completionEntity)

        // Record reward transaction
        val tx = RewardTransactionEntity(
            id = UUID.randomUUID().toString(),
            rewardType = "LISAN_POINTS",
            amount = 100,
            sourceType = "DAILY_TUTOR_TIME",
            sourceId = localDateStr,
            awardedAt = completionTimeMillis,
            status = "AUTO_AWARDED",
            deduplicationKey = "tutor_time_reward_$localDateStr"
        )
        dao.insertRewardTransaction(tx)

        // Record event
        recordEvent(
            eventType = "DAILY_TUTOR_TIME_COMPLETED",
            featureSource = "TUTOR_CHAT",
            sessionId = sessionId,
            deduplicationKey = "daily_tutor_time_$localDateStr"
        )

        evaluateAllAchievements(stats)
        return true
    }

    private fun isTimeInsideWindow(timeMillis: Long, startHm: String, endHm: String, tzId: String): Boolean {
        try {
            val tz = TimeZone.getTimeZone(tzId)
            val cal = Calendar.getInstance(tz).apply { timeInMillis = timeMillis }
            val currentHour = cal.get(Calendar.HOUR_OF_DAY)
            val currentMin = cal.get(Calendar.MINUTE)
            val currentTotalMins = currentHour * 60 + currentMin

            val startParts = startHm.split(":")
            val startTotalMins = startParts[0].toInt() * 60 + startParts[1].toInt()

            val endParts = endHm.split(":")
            val endTotalMins = endParts[0].toInt() * 60 + endParts[1].toInt()

            if (startTotalMins <= endTotalMins) {
                return currentTotalMins in startTotalMins..endTotalMins
            } else {
                // Cross midnight e.g. 23:30 to 00:30
                return currentTotalMins >= startTotalMins || currentTotalMins <= endTotalMins
            }
        } catch (e: Exception) {
            return false
        }
    }

    private suspend fun evaluateAllAchievements(stats: UserAchievementStatsEntity) {
        val definitions = dao.getAllActiveDefinitions().first()
        for (def in definitions) {
            val currentProg = dao.getProgress(def.id) ?: AchievementProgressEntity(achievementId = def.id)
            if (currentProg.isUnlocked) continue

            val progressValue = when (def.requirementType) {
                "FIRST_ACTIVITY" -> if (stats.totalTutorConversations > 0 || stats.totalGrammarLessonsCompleted > 0 || stats.totalRevisionSessionsCompleted > 0 || stats.totalPictureVocabularyWordsLearned > 0 || stats.totalSentenceAnalysesCompleted > 0 || stats.totalDailyChallengesCompleted > 0 || stats.totalPronunciationSessionsCompleted > 0) 1L else 0L
                "TUTOR_CONVERSATION_COUNT" -> stats.totalTutorConversations
                "GRAMMAR_LESSON_COUNT" -> stats.totalGrammarLessonsCompleted
                "REVISION_SESSION_COUNT" -> stats.totalRevisionSessionsCompleted
                "PICTURE_WORD_COUNT" -> stats.totalPictureVocabularyWordsLearned
                "PICTURE_CATEGORY_COUNT" -> stats.totalPictureVocabularyCategoriesCompleted
                "SENTENCE_ANALYSIS_COUNT" -> stats.totalSentenceAnalysesCompleted
                "DAILY_CHALLENGE_COUNT" -> stats.totalDailyChallengesCompleted
                "PRONUNCIATION_COUNT" -> stats.totalPronunciationSessionsCompleted
                "ON_TIME_TUTOR_COMPLETION_COUNT" -> stats.totalOnTimeTutorCompletions
                "LEARNING_STREAK_DAYS" -> stats.currentLearningStreak.toLong()
                "ON_TIME_TUTOR_STREAK_DAYS" -> stats.currentOnTimeTutorStreak.toLong()
                else -> 0L
            }

            val isUnlocked = progressValue >= def.targetValue
            val now = System.currentTimeMillis()

            val updatedProg = currentProg.copy(
                currentValue = minOf(progressValue, def.targetValue),
                isUnlocked = isUnlocked,
                unlockedAt = if (isUnlocked && !currentProg.isUnlocked) now else currentProg.unlockedAt,
                lastUpdatedAt = now
            )
            dao.insertOrUpdateProgress(updatedProg)

            if (isUnlocked && !currentProg.isUnlocked) {
                // Unlock reward
                val rewardTx = RewardTransactionEntity(
                    id = UUID.randomUUID().toString(),
                    achievementId = def.id,
                    rewardType = def.rewardType ?: "LISAN_POINTS",
                    amount = def.rewardValue ?: 50,
                    sourceType = "ACHIEVEMENT",
                    sourceId = def.id,
                    awardedAt = now,
                    status = "PENDING",
                    deduplicationKey = "achievement_reward_${def.id}"
                )
                dao.insertRewardTransaction(rewardTx)

                // Update unlocked count in stats
                val updatedStats = stats.copy(
                    totalAchievementsUnlocked = stats.totalAchievementsUnlocked + 1,
                    lisanPointsBalance = stats.lisanPointsBalance + (def.rewardValue ?: 50),
                    updatedAt = now
                )
                dao.insertOrUpdateUserStats(updatedStats)
            }
        }
    }

    suspend fun claimReward(txId: String): Boolean {
        val tx = dao.getRewardTransaction(txId) ?: return false
        if (tx.status == "CLAIMED") return true

        val updated = tx.copy(status = "CLAIMED", claimedAt = System.currentTimeMillis())
        dao.updateRewardTransaction(updated)
        return true
    }

    suspend fun saveTutorSchedule(schedule: TutorTimeScheduleEntity) {
        dao.insertOrUpdateTutorSchedule(schedule)
    }

    private fun getLocalDateString(millis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(millis))
    }

    private val prefsStorage = com.example.LisanApplication.instance.getSharedPreferences("lisan_journey_prefs", android.content.Context.MODE_PRIVATE)

    private fun prefsGetLastStreakDate(): String {
        return prefsStorage.getString("last_learning_streak_date", "") ?: ""
    }

    private fun prefsSetLastStreakDate(dateStr: String) {
        prefsStorage.edit().putString("last_learning_streak_date", dateStr).apply()
    }
}
