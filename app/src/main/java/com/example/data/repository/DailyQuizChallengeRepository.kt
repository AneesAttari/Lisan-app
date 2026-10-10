package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.DailyQuizAssignmentEntity
import com.example.data.model.DailyQuizChallengeProgressEntity
import com.example.data.model.DayProgressInfo
import com.example.data.model.DayQuizStatus
import com.example.data.model.WeeklyQuizProgression
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class DailyQuizChallengeRepository(private val database: AppDatabase) {
    private val dao = database.dailyQuizChallengeDao()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    suspend fun getDailyChallengeProgress(userId: String): DailyQuizChallengeProgressEntity = withContext(Dispatchers.IO) {
        dao.getProgress(userId) ?: DailyQuizChallengeProgressEntity(userId, dateFormat.format(Date()))
    }

    suspend fun getOrCreateTodayAssignment(userId: String, questionIds: List<String>): DailyQuizAssignmentEntity = withContext(Dispatchers.IO) {
        val today = dateFormat.format(Date())
        val existing = dao.getAssignment(userId, today)
        if (existing != null) {
            existing
        } else {
            val assignment = DailyQuizAssignmentEntity(
                userId = userId, 
                date = today, 
                questionIds = questionIds.joinToString(",")
            )
            dao.insertAssignment(assignment)
            assignment
        }
    }

    suspend fun getAssignmentForDay(userId: String, dayNumber: Int): DailyQuizAssignmentEntity? = withContext(Dispatchers.IO) {
        dao.getAssignment(userId, "day_$dayNumber")
    }

    suspend fun recordMcqAnswer(
        userId: String,
        dayNumber: Int,
        questionIndex: Int,
        isCorrect: Boolean,
        currentScore: Int
    ): DailyQuizAssignmentEntity = withContext(Dispatchers.IO) {
        val dayKey = "day_$dayNumber"
        val existing = dao.getAssignment(userId, dayKey)
        val newMcqsCount = maxOf(existing?.completedMcqsCount ?: 0, questionIndex + 1).coerceIn(0, 10)
        val isFullyComplete = newMcqsCount >= 10
        val nextIndex = if (isFullyComplete) 9 else minOf(questionIndex + 1, 9)

        val updatedAssignment = (existing ?: DailyQuizAssignmentEntity(
            userId = userId,
            date = dayKey,
            questionIds = ""
        )).copy(
            completedMcqsCount = newMcqsCount,
            completed = isFullyComplete,
            completionTimestamp = if (isFullyComplete) System.currentTimeMillis() else existing?.completionTimestamp,
            score = currentScore,
            lastAnsweredIndex = nextIndex
        )

        dao.insertAssignment(updatedAssignment)

        if (isFullyComplete) {
            val allAssignments = dao.getAllAssignments(userId)
            val fullyCompletedCount = allAssignments.count { 
                it.date.startsWith("day_") && (it.completed || it.completedMcqsCount >= 10) 
            }
            val currentProgress = getDailyChallengeProgress(userId)
            val updatedProgress = currentProgress.copy(
                totalCompletedDays = fullyCompletedCount,
                lastCompletedDate = dateFormat.format(Date())
            )
            dao.insertProgress(updatedProgress)
        }

        updatedAssignment
    }

    suspend fun updateAssignment(assignment: DailyQuizAssignmentEntity) = withContext(Dispatchers.IO) {
        dao.updateAssignment(assignment)
    }

    suspend fun updateProgress(progress: DailyQuizChallengeProgressEntity) = withContext(Dispatchers.IO) {
        dao.insertProgress(progress)
    }

    suspend fun completeDay(userId: String, dayNumber: Int, finalScore: Int): WeeklyQuizProgression = withContext(Dispatchers.IO) {
        val todayStr = dateFormat.format(Date())
        
        // Mark Day Assignment as fully completed (10/10)
        val dayKey = "day_$dayNumber"
        val existingDayAssignment = dao.getAssignment(userId, dayKey)
        val updatedDayAssignment = (existingDayAssignment ?: DailyQuizAssignmentEntity(
            userId = userId,
            date = dayKey,
            questionIds = ""
        )).copy(
            completedMcqsCount = 10,
            completed = true,
            completionTimestamp = System.currentTimeMillis(),
            score = finalScore,
            lastAnsweredIndex = 9
        )
        dao.insertAssignment(updatedDayAssignment)

        // Also record today's calendar assignment
        val todayAssignment = dao.getAssignment(userId, todayStr)
        val updatedTodayAssignment = (todayAssignment ?: DailyQuizAssignmentEntity(
            userId = userId,
            date = todayStr,
            questionIds = ""
        )).copy(
            completedMcqsCount = 10,
            completed = true,
            completionTimestamp = System.currentTimeMillis(),
            score = finalScore,
            lastAnsweredIndex = 9
        )
        dao.insertAssignment(updatedTodayAssignment)

        // Authoritatively recalculate total fully completed days
        val allAssignments = dao.getAllAssignments(userId)
        val fullyCompletedCount = allAssignments.count { 
            it.date.startsWith("day_") && (it.completed || it.completedMcqsCount >= 10) 
        }

        val existingProgress = getDailyChallengeProgress(userId)
        val updatedProgress = existingProgress.copy(
            totalCompletedDays = fullyCompletedCount,
            lastCompletedDate = todayStr
        )
        dao.insertProgress(updatedProgress)

        val targetWeek = ((dayNumber - 1) / 7) + 1
        getWeeklyProgression(userId, targetWeek)
    }

    suspend fun getWeeklyProgression(userId: String, targetWeekNumber: Int? = null): WeeklyQuizProgression = withContext(Dispatchers.IO) {
        val todayDateStr = dateFormat.format(Date())
        val allAssignments = dao.getAllAssignments(userId)
        
        // Map day number -> assignment from Room
        val dayAssignmentsMap = mutableMapOf<Int, DailyQuizAssignmentEntity>()
        allAssignments.forEach { item ->
            if (item.date.startsWith("day_")) {
                item.date.removePrefix("day_").toIntOrNull()?.let { d ->
                    dayAssignmentsMap[d] = item
                }
            }
        }

        // Authoritative count of fully completed days (10/10)
        val totalFullyCompletedDays = (1..365).count { d ->
            val a = dayAssignmentsMap[d]
            a != null && (a.completed || a.completedMcqsCount >= 10)
        }

        // Active day is strictly the first incomplete day (1..365)
        val currentActiveDayNumber = (1..365).firstOrNull { d ->
            val a = dayAssignmentsMap[d]
            a == null || (!a.completed && a.completedMcqsCount < 10)
        } ?: 365

        val currentActiveWeekNumber = ((currentActiveDayNumber - 1) / 7) + 1 // 1..53

        val selectedWeek = (targetWeekNumber ?: currentActiveWeekNumber).coerceIn(1, 53)
        val startDayOfSelectedWeek = (selectedWeek - 1) * 7 + 1
        val endDayOfSelectedWeek = minOf(selectedWeek * 7, 365)

        val dayNamesEn = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val dayNamesUr = listOf("پیر", "منگل", "بدھ", "جمعرات", "جمعہ", "ہفتہ", "اتوار")
        val grammarTopicsEn = listOf(
            "Tenses & Verb Aspects",
            "Modals & Auxiliaries",
            "Passive & Active Voice",
            "Conditionals & Hypotheses",
            "Reported Speech & Discourse",
            "Gerunds & Infinitives",
            "Synthesis & Mastery"
        )
        val grammarTopicsUr = listOf(
            "فعل کے زمانے اور بناوٹ",
            "افعال ناقصہ اور امکانیات",
            "معروف اور مجہول جملے",
            "شرطیہ اور امکانی جملے",
            "بالواسطہ کلام اور اقوال",
            "اسم مصدر اور لاحقے",
            "ہفتہ وار مکمل جائزہ"
        )

        val days = (0 until 7).mapNotNull { offset ->
            val dayNumber = startDayOfSelectedWeek + offset
            if (dayNumber > 365) return@mapNotNull null

            val dayOfWeekIndex = offset + 1
            val assignment = dayAssignmentsMap[dayNumber]
            val completedMcqs = assignment?.completedMcqsCount ?: 0
            val isCompleted = assignment?.completed == true || completedMcqs >= 10
            val isInProgress = !isCompleted && completedMcqs > 0
            val isToday = dayNumber == currentActiveDayNumber
            // A day is locked if it is beyond the current active day AND not completed
            val isLocked = dayNumber > currentActiveDayNumber && !isCompleted
            val score = assignment?.score ?: if (isCompleted) 10 else 0
            val lastAnsweredIndex = assignment?.lastAnsweredIndex ?: 0

            DayProgressInfo(
                dayNumber = dayNumber,
                dayOfWeekIndex = dayOfWeekIndex,
                dayNameEn = dayNamesEn[offset % 7],
                dayNameUr = dayNamesUr[offset % 7],
                dateStr = assignment?.date ?: "",
                isToday = isToday,
                isCompleted = isCompleted,
                isInProgress = isInProgress,
                isLocked = isLocked,
                completedMcqsCount = completedMcqs,
                score = score,
                lastAnsweredIndex = lastAnsweredIndex,
                totalQuizzes = 10,
                grammarTopicTitle = grammarTopicsEn[offset % 7],
                grammarTopicTitleUr = grammarTopicsUr[offset % 7]
            )
        }

        // Weekly progress ONLY counts fully completed days (10/10)
        val completedCountThisWeek = days.count { it.isCompleted }
        val isWeekCompleted = completedCountThisWeek >= days.size && days.isNotEmpty()

        // Count fully completed weeks
        var completedWeeksCount = 0
        for (w in 1..52) {
            val wStart = (w - 1) * 7 + 1
            val allDaysInWCompleted = (0 until 7).all { off ->
                val d = wStart + off
                val a = dayAssignmentsMap[d]
                a != null && (a.completed || a.completedMcqsCount >= 10)
            }
            if (allDaysInWCompleted) {
                completedWeeksCount++
            }
        }

        val activeDayAssignment = dayAssignmentsMap[currentActiveDayNumber]
        val isTodayCompleted = activeDayAssignment != null && (activeDayAssignment.completed || activeDayAssignment.completedMcqsCount >= 10)

        val currentDayInSelectedWeek = ((currentActiveDayNumber - 1) % 7) + 1

        WeeklyQuizProgression(
            weekNumber = selectedWeek,
            totalWeeks = 53,
            currentActiveDayNumber = currentActiveDayNumber,
            currentActiveWeekNumber = currentActiveWeekNumber,
            currentDayIndex = currentDayInSelectedWeek,
            currentDayNameEn = dayNamesEn.getOrElse(currentDayInSelectedWeek - 1) { "Mon" },
            currentDayNameUr = dayNamesUr.getOrElse(currentDayInSelectedWeek - 1) { "پیر" },
            completedDaysCount = completedCountThisWeek,
            totalDaysInWeek = days.size,
            days = days,
            isWeekCompleted = isWeekCompleted,
            completedWeeksCount = completedWeeksCount,
            totalCompletedDays = totalFullyCompletedDays,
            todayCompleted = isTodayCompleted,
            weekStartDateStr = "Day $startDayOfSelectedWeek",
            weekEndDateStr = "Day $endDayOfSelectedWeek"
        )
    }
}


