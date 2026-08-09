package com.example.utils

enum class WordStatus {
    MATCH, MISSING, EXTRA, INCORRECT
}

data class WordFeedback(
    val word: String,
    val status: WordStatus,
    val detail: String = ""
)

data class PronunciationResult(
    val score: Int,
    val feedbackWords: List<WordFeedback>,
    val matchCount: Int,
    val missingCount: Int,
    val extraCount: Int,
    val incorrectCount: Int,
    val teacherComment: String
)

object PronunciationEvaluator {

    fun evaluate(spoken: String, target: String, style: String = "Supportive"): PronunciationResult {
        // Clean and tokenize target
        val targetClean = target.trim().replace(Regex("[^a-zA-Z0-9'\\s]"), "")
        val targetWords = targetClean.split(Regex("\\s+")).filter { it.isNotBlank() }

        // Clean and tokenize spoken
        val spokenClean = spoken.trim().replace(Regex("[^a-zA-Z0-9'\\s]"), "")
        val spokenWords = spokenClean.split(Regex("\\s+")).filter { it.isNotBlank() }

        if (targetWords.isEmpty()) {
            return PronunciationResult(
                score = 0,
                feedbackWords = emptyList(),
                matchCount = 0,
                missingCount = 0,
                extraCount = 0,
                incorrectCount = 0,
                teacherComment = "Please select or type a phrase first."
            )
        }

        if (spokenWords.isEmpty()) {
            val styleComment = when (style) {
                "Detailed" -> "No speech was detected by the microphone. Please ensure your microphone is working, speak clearly, and try again. Your score is 0%."
                "Concise" -> "No speech detected. Try again."
                else -> "I couldn't hear any words. Please try speaking a bit louder or closer to the microphone. حاول مرة أخرى!"
            }
            return PronunciationResult(
                score = 0,
                feedbackWords = targetWords.map { WordFeedback(it, WordStatus.MISSING) },
                matchCount = 0,
                missingCount = targetWords.size,
                extraCount = 0,
                incorrectCount = 0,
                teacherComment = styleComment
            )
        }

        val m = targetWords.size
        val n = spokenWords.size
        val dp = Array(m + 1) { IntArray(n + 1) }

        for (i in 0..m) dp[i][0] = i
        for (j in 0..n) dp[0][j] = j

        for (i in 1..m) {
            for (j in 1..n) {
                if (targetWords[i - 1].equals(spokenWords[j - 1], ignoreCase = true)) {
                    dp[i][j] = dp[i - 1][j - 1]
                } else {
                    val replace = dp[i - 1][j - 1] + 1
                    val delete = dp[i - 1][j] + 1 // deletion = missing word from spoken
                    val insert = dp[i][j - 1] + 1 // insertion = extra word in spoken
                    dp[i][j] = minOf(replace, minOf(delete, insert))
                }
            }
        }

        var i = m
        var j = n
        val feedbackWords = mutableListOf<WordFeedback>()

        var matches = 0
        var missing = 0
        var extras = 0
        var incorrect = 0

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && targetWords[i - 1].equals(spokenWords[j - 1], ignoreCase = true)) {
                feedbackWords.add(WordFeedback(targetWords[i - 1], WordStatus.MATCH))
                matches++
                i--
                j--
            } else if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1) {
                feedbackWords.add(WordFeedback(targetWords[i - 1], WordStatus.INCORRECT, "Heard: \"${spokenWords[j - 1]}\""))
                incorrect++
                i--
                j--
            } else if (i > 0 && (j == 0 || dp[i][j] == dp[i - 1][j] + 1)) {
                feedbackWords.add(WordFeedback(targetWords[i - 1], WordStatus.MISSING))
                missing++
                i--
            } else {
                feedbackWords.add(WordFeedback(spokenWords[j - 1], WordStatus.EXTRA))
                extras++
                j--
            }
        }
        feedbackWords.reverse()

        // Calculate a fair accuracy percentage based on target words
        val rawAccuracy = matches.toDouble() / targetWords.size
        // Penalty for extra words or incorrect words can be subtracted to make it realistic
        val penalty = (extras * 0.1) + (incorrect * 0.1)
        val finalScore = ((rawAccuracy - penalty).coerceIn(0.0, 1.0) * 100).toInt()

        val comment = when (style) {
            "Detailed" -> {
                when {
                    finalScore >= 90 -> "Excellent accuracy ($finalScore%). You pronounced $matches out of ${targetWords.size} target words correctly. Your speech was highly articulate."
                    finalScore >= 75 -> "Good accuracy ($finalScore%). You matched $matches words. Pay attention to the $missing missing words and $incorrect incorrect pronunciations to improve."
                    finalScore >= 50 -> "Fair accuracy ($finalScore%). You had $missing missing words and $incorrect incorrect words. Recommend reviewing the TTS audio and practicing individual words."
                    else -> "Low accuracy ($finalScore%). Only $matches correct words detected out of ${targetWords.size}. Listen to the audio model and practice slowly before attempting again."
                }
            }
            "Concise" -> {
                when {
                    finalScore >= 90 -> "Great! Score: $finalScore%"
                    finalScore >= 75 -> "Good! Score: $finalScore%"
                    finalScore >= 50 -> "Okay. Score: $finalScore%"
                    else -> "Needs work. Score: $finalScore%"
                }
            }
            else -> {
                // Supportive (Default)
                when {
                    finalScore >= 90 -> "Excellent! Your pronunciation is incredibly clear. You got almost every word right! بارك الله فيك!"
                    finalScore >= 75 -> "Very good effort! You spoke clearly with minor mistakes. Keep practicing! ممتاز!"
                    finalScore >= 50 -> "Good attempt! Some words were missed or mispronounced. Listen to the Text-to-Speech and try repeating it slowly. أحسنت، حاول مرة أخرى!"
                    else -> "Keep going! Don't worry about mistakes. Listen to the correct pronunciation and repeat it slowly word by word. استمر في المحاولة!"
                }
            }
        }

        return PronunciationResult(
            score = finalScore,
            feedbackWords = feedbackWords,
            matchCount = matches,
            missingCount = missing,
            extraCount = extras,
            incorrectCount = incorrect,
            teacherComment = comment
        )
    }
}
