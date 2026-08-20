package com.example.utils

import java.util.Locale

/**
 * Intelligent Speech-to-Text Post Processor for Lisan AI.
 * Handles Urdu, Arabic, and English script normalization, mixed language detection,
 * and punctuation cleanup for speech recognition output.
 */
object SpeechTextProcessor {

    /**
     * Cleans and normalizes transcribed speech text according to active language context.
     */
    fun processTranscribedText(
        rawText: String,
        targetLanguage: String = "Urdu"
    ): String {
        var text = rawText.trim()
        if (text.isBlank()) return ""

        // Collapse multiple spaces
        text = text.replace(Regex("\\s+"), " ")

        // Language specific enhancements
        return when {
            targetLanguage.contains("Urdu", ignoreCase = true) || isUrduScript(text) -> {
                formatUrduSpeechText(text)
            }
            targetLanguage.contains("Arabic", ignoreCase = true) || isArabicScript(text) -> {
                formatArabicSpeechText(text)
            }
            else -> {
                formatEnglishSpeechText(text)
            }
        }
    }

    /**
     * Checks if text contains Urdu character range (\u0600..\u06FF) or Urdu specific characters (\u0750..\u077F).
     */
    fun isUrduScript(text: String): Boolean {
        return text.any { char ->
            char in '\u0600'..'\u06FF' || char in '\u0750'..'\u077F'
        }
    }

    /**
     * Checks if text contains Arabic/Urdu script.
     */
    fun isArabicScript(text: String): Boolean {
        return text.any { char -> char in '\u0600'..'\u06FF' }
    }

    private fun formatUrduSpeechText(text: String): String {
        var formatted = text

        // Ensure Urdu full stop (۔) if sentence is complete without trailing punctuation
        if (!formatted.endsWith("۔") && !formatted.endsWith("؟") && !formatted.endsWith("!")) {
            if (formatted.endsWith("?") || formatted.endsWith("کیا") || formatted.endsWith("کیوں") || formatted.endsWith("کیسے")) {
                formatted = formatted.trimEnd('.', '?', '!', ' ') + "؟"
            } else {
                formatted = formatted.trimEnd('.', '?', '!', ' ') + "۔"
            }
        }
        return formatted
    }

    private fun formatArabicSpeechText(text: String): String {
        var formatted = text
        if (!formatted.endsWith("۔") && !formatted.endsWith("؟") && !formatted.endsWith(".")) {
            if (formatted.endsWith("?") || formatted.startsWith("هل") || formatted.startsWith("لماذا") || formatted.startsWith("كيف")) {
                formatted = formatted.trimEnd('.', '?', '!', ' ') + "؟"
            } else {
                formatted = formatted.trimEnd('.', '?', '!', ' ') + "."
            }
        }
        return formatted
    }

    private fun formatEnglishSpeechText(text: String): String {
        if (text.isEmpty()) return ""
        var formatted = text.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        if (!formatted.endsWith(".") && !formatted.endsWith("?") && !formatted.endsWith("!")) {
            if (formatted.lowercase().startsWith("what") ||
                formatted.lowercase().startsWith("how") ||
                formatted.lowercase().startsWith("why") ||
                formatted.lowercase().startsWith("can") ||
                formatted.lowercase().startsWith("could") ||
                formatted.lowercase().startsWith("is") ||
                formatted.lowercase().startsWith("are") ||
                formatted.lowercase().startsWith("do") ||
                formatted.lowercase().startsWith("does")
            ) {
                formatted += "?"
            } else {
                formatted += "."
            }
        }
        return formatted
    }

    /**
     * Get BCP-47 language tag for SpeechRecognizer based on app selection.
     */
    fun getSpeechLanguageTag(selectedLanguage: String): String {
        return when {
            selectedLanguage.contains("Urdu", ignoreCase = true) || selectedLanguage.contains("اردو", ignoreCase = true) -> "ur-PK"
            selectedLanguage.contains("Arabic", ignoreCase = true) || selectedLanguage.contains("العربية", ignoreCase = true) -> "ar-SA"
            else -> "en-US"
        }
    }
}
