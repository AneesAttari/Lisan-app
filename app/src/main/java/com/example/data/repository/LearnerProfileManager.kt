package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import java.util.Locale

class LearnerProfileManager(context: Context) {
    private val prefs = context.getSharedPreferences("lisan_prefs", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true }

    private val _learnerProfile = MutableStateFlow(loadProfile())
    val learnerProfile: StateFlow<LearnerProfile> = _learnerProfile.asStateFlow()

    companion object {
        private const val TAG = "LearnerProfileManager"
        private const val PREF_KEY_PROFILE = "learner_profile_data_v2"
    }

    private fun loadProfile(): LearnerProfile {
        val rawJson = prefs.getString(PREF_KEY_PROFILE, null)
        if (!rawJson.isNullOrBlank()) {
            try {
                return json.decodeFromString<LearnerProfile>(rawJson)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to decode learner profile JSON, initializing default profile", e)
            }
        }
        return LearnerProfile()
    }

    private fun saveProfile(profile: LearnerProfile) {
        com.example.utils.RevisionReminderScheduler.scheduleRevisionReminder(com.example.LisanApplication.instance)
        val updatedProfile = profile.copy(lastUpdated = System.currentTimeMillis())
        _learnerProfile.value = updatedProfile
        try {
            val rawJson = json.encodeToString(LearnerProfile.serializer(), updatedProfile)
            prefs.edit().putString(PREF_KEY_PROFILE, rawJson).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save learner profile JSON", e)
        }
    }

    private fun calculatePreferredStudyHour(): String {
        val profile = _learnerProfile.value
        if (profile.interactionTimestamps.isEmpty()) return "not yet determined"
        
        val hours = profile.interactionTimestamps.map { 
            java.util.Calendar.getInstance().apply { timeInMillis = it }.get(java.util.Calendar.HOUR_OF_DAY)
        }
        val mostFrequentHour = hours.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: return "not yet determined"
        
        val period = if (mostFrequentHour in 5..11) "morning" else if (mostFrequentHour in 12..16) "afternoon" else if (mostFrequentHour in 17..20) "evening" else "night"
        return "$mostFrequentHour:00 ($period)"
    }

    fun buildPersonalizedPromptContext(): String {
        val profile = _learnerProfile.value
        if (!profile.isMemoryEnabled) {
            return "PERSONALIZED MEMORY: Disabled by user."
        }

        val accuracyPercent = if (profile.totalQuestionsAnswered > 0) {
            ((profile.correctAnswersCount.toFloat() / profile.totalQuestionsAnswered) * 100).toInt()
        } else 70

        val weakTopicsStr = if (profile.commonMistakes.isNotEmpty()) {
            profile.commonMistakes.take(4).joinToString { "${it.topic} (${it.pattern})" }
        } else "Needs practice with prepositions (in/on/at) and verb tenses."

        val recentLearningStr = if (profile.recentLearning.isNotEmpty()) {
            profile.recentLearning.take(5).joinToString { "${it.type}: ${it.topic} (${it.status})" }
        } else "Started learning English grammar fundamentals."

        val memoriesStr = if (profile.usefulMemories.isNotEmpty()) {
            profile.usefulMemories.take(6).joinToString(separator = "\n") { "- $it" }
        } else "- Prefers Urdu explanations with Arabic grammar comparisons."

        return """
            AUTOMATIC LEARNER PROFILE & MEMORY (USE THIS TO PERSONALIZING RESPONSES):
            - Learner ID: ${profile.userId}
            - Current Level: ${profile.learningLevel.overall} (Confidence Score: ${(profile.learningLevel.confidence * 100).toInt()}%)
            - Total Interactions: ${profile.totalInteractions} | Practice Accuracy: $accuracyPercent% (${profile.correctAnswersCount}/${profile.totalQuestionsAnswered})
            - Primary Explanation Language: Urdu (اردو)
            - Weak Areas / Common Mistakes: $weakTopicsStr
            - Mastered Topics (Achievements): ${if (profile.masteredContentIds.isEmpty()) "Present Simple fundamentals" else profile.masteredContentIds.joinToString()}
            - Review Recommended Topics: ${if (profile.reviewDueContentIds.isEmpty()) "Prepositions, Present Perfect" else profile.reviewDueContentIds.joinToString()}
            - Preferred Study Time: ${calculatePreferredStudyHour()}
            - Recent Student Activity:
              $recentLearningStr
            - Student Facts & Useful Memory:
              $memoriesStr
            
            TEACHER INSTRUCTIONS BASED ON PROFILE:
            1. Tailor your English vocabulary and sentence complexity to the student's level (${profile.learningLevel.overall}).
            2. When explaining weak areas like $weakTopicsStr, compare them naturally with concepts the student knows without sounding intrusive.
            3. Use natural Urdu explanations and ALWAYS include Arabic grammar comparisons starting with "جیسے آپ نے عربی میں سیکھا ہے...".
            4. If the student repeats a question or asks for practice on a recently studied topic, seamlessly continue that context.
            5. PROACTIVE SUGGESTION: If the learner has pending revisions (${if (profile.reviewDueContentIds.isEmpty()) "None" else profile.reviewDueContentIds.joinToString()}), kindly suggest they practice one of these topics in the next interaction.
        """.trimIndent()
    }

    fun recordQuestionResult(type: String, topic: String, isCorrect: Boolean, userAnswer: String, correctAnswer: String) {
        val current = _learnerProfile.value
        if (!current.isMemoryEnabled) return

        val totalInteractions = current.totalInteractions + 1
        val totalQuestions = current.totalQuestionsAnswered + 1
        val correctCount = if (isCorrect) current.correctAnswersCount + 1 else current.correctAnswersCount

        // Update Topic Mastery
        val currentMastery = current.topicMastery[topic] ?: 0.5f
        val newMastery = if (isCorrect) (currentMastery + 0.08f).coerceAtMost(1.0f) else (currentMastery - 0.06f).coerceAtLeast(0.0f)

        val updatedMasteryMap = current.topicMastery.toMutableMap()
        updatedMasteryMap[topic] = newMastery

        // Update Mistake Patterns
        val mistakes = current.commonMistakes.toMutableList()
        if (!isCorrect) {
            val existingIndex = mistakes.indexOfFirst { it.topic.equals(topic, ignoreCase = true) }
            val patternDesc = "Struggles with $topic (User answer: '$userAnswer', Correct: '$correctAnswer')"
            if (existingIndex >= 0) {
                val existing = mistakes[existingIndex]
                mistakes[existingIndex] = existing.copy(
                    frequency = existing.frequency + 1,
                    lastSeen = System.currentTimeMillis(),
                    example = userAnswer
                )
            } else {
                mistakes.add(MistakePattern(topic = topic, pattern = patternDesc, frequency = 1, example = userAnswer))
            }
        }

        // Mastered & Review Due lists
        val mastered = current.masteredContentIds.toMutableSet()
        val reviewDue = current.reviewDueContentIds.toMutableSet()

        if (newMastery >= 0.75f) {
            mastered.add(topic)
            reviewDue.remove(topic)
        } else if (newMastery < 0.45f) {
            reviewDue.add(topic)
            mastered.remove(topic)
        }

        // Useful Memories
        val memories = current.usefulMemories.toMutableList()
        val memoryText = if (isCorrect) {
            "Student answered $topic question correctly ($type)."
        } else {
            "Student needs review on $topic (Answered '$userAnswer' instead of '$correctAnswer')."
        }
        addUniqueMemory(memories, memoryText)

        // Recent Learning
        val recent = current.recentLearning.toMutableList()
        recent.add(0, RecentLearningItem(
            type = type,
            topic = topic,
            status = if (isCorrect) "correct" else "incorrect",
            score = if (isCorrect) "1/1" else "0/1"
        ))
        if (recent.size > 20) recent.removeAt(recent.lastIndex)

        val evalLevel = evaluateLevel(totalInteractions, correctCount, totalQuestions, updatedMasteryMap)

        saveProfile(current.copy(
            totalInteractions = totalInteractions,
            totalQuestionsAnswered = totalQuestions,
            correctAnswersCount = correctCount,
            topicMastery = updatedMasteryMap,
            commonMistakes = mistakes.take(10),
            masteredContentIds = mastered.toList(),
            reviewDueContentIds = reviewDue.toList(),
            usefulMemories = memories.take(25),
            recentLearning = recent,
            learningLevel = evalLevel
        ))
    }

    fun recordTutorInteraction(userPrompt: String, tutorReply: String) {
        val current = _learnerProfile.value
        if (!current.isMemoryEnabled) return

        val totalInteractions = current.totalInteractions + 1
        val detectedTopic = detectTopicFromText(userPrompt)
        val memories = current.usefulMemories.toMutableList()

        if (userPrompt.contains("confused", ignoreCase = true) ||
            userPrompt.contains("سمجھ نہیں آتا", ignoreCase = true) ||
            userPrompt.contains("فرق", ignoreCase = true) ||
            userPrompt.contains("difference", ignoreCase = true)) {
            addUniqueMemory(memories, "Student requested detailed clarification on $detectedTopic.")
        } else {
            addUniqueMemory(memories, "Student asked Tutor AI about $detectedTopic.")
        }

        // Explicit Heuristics Extractions
        val lowerPrompt = userPrompt.lowercase(Locale.getDefault())
        if (lowerPrompt.contains("talk") && (lowerPrompt.contains("urdu") || lowerPrompt.contains("اردو"))) {
            addUniqueMemory(memories, "User requested to ALWAYS communicate/explain in Urdu.")
        }
        if (userPrompt.contains("اردو میں") || userPrompt.contains("اردو سیکھ")) {
            addUniqueMemory(memories, "User requested to ALWAYS communicate/explain in Urdu.")
        }
        if (lowerPrompt.contains("talk") && (lowerPrompt.contains("arabic") || lowerPrompt.contains("عربی"))) {
            addUniqueMemory(memories, "User requested to ALWAYS communicate/explain in Arabic.")
        }
        if (userPrompt.contains("عربی میں") || userPrompt.contains("بالعربية")) {
            addUniqueMemory(memories, "User requested to ALWAYS communicate/explain in Arabic.")
        }
        if (lowerPrompt.contains("my name is ")) {
            val namePart = userPrompt.substringAfter("my name is ", "").trim().substringBefore(".").substringBefore(" ")
            if (namePart.isNotBlank() && namePart.length > 1) {
                addUniqueMemory(memories, "User's name is $namePart.")
            }
        }

        val mistakes = current.commonMistakes.toMutableList()
        if (userPrompt.contains("in on at", ignoreCase = true) || userPrompt.contains("preposition", ignoreCase = true)) {
            val existingIdx = mistakes.indexOfFirst { it.topic == "Prepositions" }
            if (existingIdx >= 0) {
                mistakes[existingIdx] = mistakes[existingIdx].copy(frequency = mistakes[existingIdx].frequency + 1)
            } else {
                mistakes.add(MistakePattern("Prepositions", "confuses in, on, and at", 1, example = userPrompt))
            }
        }

        val recent = current.recentLearning.toMutableList()
        recent.add(0, RecentLearningItem(
            type = "tutor_chat",
            topic = detectedTopic,
            status = "asked"
        ))
        if (recent.size > 20) recent.removeAt(recent.lastIndex)

        val evalLevel = evaluateLevel(totalInteractions, current.correctAnswersCount, current.totalQuestionsAnswered, current.topicMastery)

        saveProfile(current.copy(
            totalInteractions = totalInteractions,
            commonMistakes = mistakes,
            usefulMemories = memories.take(25),
            recentLearning = recent,
            learningLevel = evalLevel
        ))
    }

    fun recordAnalyzerRequest(sentence: String) {
        val current = _learnerProfile.value
        if (!current.isMemoryEnabled) return

        val totalInteractions = current.totalInteractions + 1
        val topic = detectTopicFromText(sentence)
        val memories = current.usefulMemories.toMutableList()
        addUniqueMemory(memories, "Student analyzed sentence: '$sentence' ($topic).")

        val recent = current.recentLearning.toMutableList()
        recent.add(0, RecentLearningItem(
            type = "analyzer",
            topic = topic,
            status = "analyzed"
        ))
        if (recent.size > 20) recent.removeAt(recent.lastIndex)

        saveProfile(current.copy(
            totalInteractions = totalInteractions,
            usefulMemories = memories.take(25),
            recentLearning = recent
        ))
    }

    fun recordLessonActivity(topicTitle: String, isCompleted: Boolean) {
        val current = _learnerProfile.value
        if (!current.isMemoryEnabled) return

        val totalInteractions = current.totalInteractions + 1
        val memories = current.usefulMemories.toMutableList()
        val status = if (isCompleted) "completed" else "opened"
        addUniqueMemory(memories, "Student $status lesson: '$topicTitle'.")

        val updatedMastery = current.topicMastery.toMutableMap()
        if (isCompleted) {
            val currentScore = updatedMastery[topicTitle] ?: 0.5f
            updatedMastery[topicTitle] = (currentScore + 0.15f).coerceAtMost(1.0f)
        }

        val recent = current.recentLearning.toMutableList()
        recent.add(0, RecentLearningItem(
            type = "lesson",
            topic = topicTitle,
            status = status
        ))
        if (recent.size > 20) recent.removeAt(recent.lastIndex)

        saveProfile(current.copy(
            totalInteractions = totalInteractions,
            topicMastery = updatedMastery,
            usefulMemories = memories.take(25),
            recentLearning = recent
        ))
    }

    fun recordItemSavedOrLiked(type: String, title: String) {
        val current = _learnerProfile.value
        if (!current.isMemoryEnabled) return

        val memories = current.usefulMemories.toMutableList()
        addUniqueMemory(memories, "Student bookmarked/saved $type: '$title'.")

        saveProfile(current.copy(
            usefulMemories = memories.take(25)
        ))
    }

    fun recordDailyChallengeActivity(topic: String, isCorrect: Boolean) {
        val current = _learnerProfile.value
        if (!current.isMemoryEnabled) return

        val totalInteractions = current.totalInteractions + 1
        val totalQuestions = current.totalQuestionsAnswered + 1
        val correctCount = if (isCorrect) current.correctAnswersCount + 1 else current.correctAnswersCount

        // Update topic mastery
        val currentMastery = current.topicMastery[topic] ?: 0.5f
        val newMastery = if (isCorrect) (currentMastery + 0.10f).coerceAtMost(1.0f) else (currentMastery - 0.08f).coerceAtLeast(0.0f)
        val updatedMasteryMap = current.topicMastery.toMutableMap()
        updatedMasteryMap[topic] = newMastery

        // Useful Memories
        val memories = current.usefulMemories.toMutableList()
        val outcome = if (isCorrect) "correctly" else "incorrectly"
        addUniqueMemory(memories, "Student answered Lisan Daily Challenge on '$topic' $outcome.")

        // Recent Learning
        val recent = current.recentLearning.toMutableList()
        recent.add(0, RecentLearningItem(
            type = "daily_challenge",
            topic = topic,
            status = if (isCorrect) "completed" else "attempted",
            score = if (isCorrect) "1/1" else "0/1"
        ))
        if (recent.size > 20) recent.removeAt(recent.lastIndex)

        val evalLevel = evaluateLevel(totalInteractions, correctCount, totalQuestions, updatedMasteryMap)

        saveProfile(current.copy(
            totalInteractions = totalInteractions,
            totalQuestionsAnswered = totalQuestions,
            correctAnswersCount = correctCount,
            topicMastery = updatedMasteryMap,
            usefulMemories = memories.take(25),
            recentLearning = recent,
            learningLevel = evalLevel
        ))
    }

    fun recordDailyIdiomCompleted(expression: String) {
        val current = _learnerProfile.value
        if (!current.isMemoryEnabled) return

        val totalInteractions = current.totalInteractions + 1
        val memories = current.usefulMemories.toMutableList()
        addUniqueMemory(memories, "Student mastered Daily Idiom: '$expression'.")

        // Recent Learning
        val recent = current.recentLearning.toMutableList()
        recent.add(0, RecentLearningItem(
            type = "idiom",
            topic = expression,
            status = "completed"
        ))
        if (recent.size > 20) recent.removeAt(recent.lastIndex)

        saveProfile(current.copy(
            totalInteractions = totalInteractions,
            usefulMemories = memories.take(25),
            recentLearning = recent
        ))
    }

    fun clearLearningHistory() {
        val freshProfile = LearnerProfile(
            userId = _learnerProfile.value.userId,
            isMemoryEnabled = _learnerProfile.value.isMemoryEnabled
        )
        saveProfile(freshProfile)
        Log.d(TAG, "Learning profile history reset successfully.")
    }

    fun toggleMemoryEnabled(enabled: Boolean) {
        val updated = _learnerProfile.value.copy(isMemoryEnabled = enabled)
        saveProfile(updated)
    }

    fun addManualMemory(memory: String) {
        val current = _learnerProfile.value
        val memories = current.usefulMemories.toMutableList()
        addUniqueMemory(memories, memory)
        saveProfile(current.copy(usefulMemories = memories.take(25)))
    }

    fun removeMemory(memory: String) {
        val current = _learnerProfile.value
        val memories = current.usefulMemories.toMutableList()
        memories.remove(memory)
        saveProfile(current.copy(usefulMemories = memories))
    }

    fun updateMemory(oldMemory: String, newMemory: String) {
        val current = _learnerProfile.value
        val memories = current.usefulMemories.toMutableList()
        val index = memories.indexOf(oldMemory)
        if (index >= 0) {
            memories[index] = newMemory
            saveProfile(current.copy(usefulMemories = memories))
        }
    }

    private fun addInteractionTimestamp(current: LearnerProfile): LearnerProfile {
        val timestamps = current.interactionTimestamps.toMutableList()
        timestamps.add(System.currentTimeMillis())
        if (timestamps.size > 50) timestamps.removeAt(0)
        return current.copy(interactionTimestamps = timestamps)
    }

    private fun addUniqueMemory(list: MutableList<String>, newMemory: String) {
        list.remove(newMemory)
        list.add(0, newMemory)
    }

    private fun detectTopicFromText(text: String): String {
        val lower = text.lowercase(Locale.getDefault())
        return when {
            lower.contains("present simple") || lower.contains("حال مطلق") -> "Present Simple"
            lower.contains("present continuous") || lower.contains("ing") || lower.contains("حال جاری") -> "Present Continuous"
            lower.contains("present perfect") || lower.contains("has") || lower.contains("have") -> "Present Perfect"
            lower.contains("past simple") || lower.contains("did") || lower.contains("ماضی") -> "Past Simple"
            lower.contains("future") || lower.contains("will") || lower.contains("مستقبل") -> "Future Forms"
            lower.contains("preposition") || lower.contains("in on at") || lower.contains("حرف جر") -> "Prepositions"
            lower.contains("article") || lower.contains("a an the") -> "Articles"
            lower.contains("idiom") || lower.contains("محاورہ") -> "Idioms"
            lower.contains("passive") || lower.contains("مجهول") -> "Passive Voice"
            lower.contains("conditional") || lower.contains("if") -> "Conditional Sentences"
            else -> "General Grammar"
        }
    }

    private fun evaluateLevel(
        interactions: Int,
        correctAnswers: Int,
        totalAnswered: Int,
        topicMastery: Map<String, Float>
    ): LearningLevel {
        if (interactions < 3 || totalAnswered == 0) {
            return LearningLevel(overall = "Beginner", confidence = 0.5f)
        }

        val accuracy = correctAnswers.toFloat() / totalAnswered
        val avgMastery = if (topicMastery.isNotEmpty()) topicMastery.values.average().toFloat() else 0.5f

        val overallLevel = when {
            accuracy >= 0.90f && avgMastery >= 0.82f -> "Advanced"
            accuracy >= 0.82f && avgMastery >= 0.72f -> "Upper-Intermediate"
            accuracy >= 0.72f && avgMastery >= 0.60f -> "Intermediate"
            accuracy >= 0.60f -> "Pre-Intermediate"
            accuracy >= 0.45f -> "Elementary"
            accuracy >= 0.30f -> "Beginner"
            else -> "Absolute Beginner"
        }

        val confidence = (0.5f + (interactions.coerceAtMost(60) / 120.0f)).coerceAtMost(0.95f)
        return LearningLevel(overall = overallLevel, confidence = confidence, lastUpdated = System.currentTimeMillis())
    }
}
