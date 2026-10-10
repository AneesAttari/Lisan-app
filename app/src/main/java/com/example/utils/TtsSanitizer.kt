package com.example.utils

/**
 * Reusable TTS Preprocessing and Sanitization Layer.
 * Ensures:
 * 1. Emojis and visual UI icons are completely stripped before reaching Android TextToSpeech or Cloud TTS,
 *    preventing the TTS engine from pronouncing Unicode emoji names (e.g., "light bulb", "sparkles").
 * 2. Markdown formatting artifacts (asterisks, hashtags, backticks, table bars) are cleaned.
 * 3. Legitimate Urdu, Arabic, and English letters, diacritics, and vocabulary are 100% preserved.
 * 4. Punctuation (periods, commas, question marks) is preserved and normalized for natural speech pauses.
 */
object TtsSanitizer {

    // Comprehensive Emoji & Pictograph regex covering Unicode surrogate pairs, dingbats, symbols, variation selectors, and ZWJ
    private val EMOJI_AND_SYMBOLS_REGEX = Regex(
        "[\\uD83C-\\uDBFF][\\uDC00-\\uDFFF]" + // Surrogate pairs (U+10000..U+10FFFF) covering Emoticons, Pictographs, Transport, Extended
        "|[\\u2600-\\u27BF]" +                 // Misc symbols & Dingbats (U+2600..U+27BF)
        "|[\\u2300-\\u23FF]" +                 // Misc Technical (U+2300..U+23FF)
        "|[\\u2B50-\\u2B55\\u25A0-\\u25FF\\u2B00-\\u2BFF]" + // Stars, shapes, arrows
        "|[\\u200D\\uFE0E\\uFE0F\\u20E3]" +     // ZWJ, variation selectors, keycaps
        "|[\\u3030\\u303D\\u3297\\u3299]"      // Enclosed Ideographic / CJK symbols
    )

    private val BULLETS_AND_ICONS_REGEX = Regex("[•▪●★☆✓✔✕✖⚡💡🔥🌟✨🎯🚀🎧📢🔊📝📖]")

    /**
     * Strips all emojis and pictographs from text.
     */
    fun removeEmojis(text: String): String {
        if (text.isBlank()) return ""
        return text
            .replace(EMOJI_AND_SYMBOLS_REGEX, "")
            .replace(BULLETS_AND_ICONS_REGEX, "")
    }

    /**
     * Full sanitization pipeline for TTS engines (Native & Cloud).
     */
    fun sanitizeForSpeech(input: String): String {
        if (input.isBlank()) return ""

        // 1. Remove markdown formatting syntax
        var cleaned = input
            .replace(Regex("(\\*\\*|\\*|___|__|#|`|\\||~~~|---|===)"), "")
            .replace(Regex("\\[(.*?)\\]\\(.*?\\)"), "$1")

        // 2. Strip all emojis and visual UI glyphs
        cleaned = removeEmojis(cleaned)

        // 3. Normalize Arabic / Urdu punctuation for natural TTS pauses while preserving text integrity
        cleaned = cleaned
            .replace("۔", ". ")
            .replace("،", ", ")
            .replace("؛", ", ")
            .replace("؟", "? ")
            .replace("¡", "")
            .replace("—", " ")
            .replace("–", " ")

        // 4. Clean up noise punctuation without removing Urdu/Arabic letters or diacritics
        cleaned = cleaned.replace(Regex("[*#`|~^=+_\\\\/\\<\\>\\[\\]{}\\(\\)\"\'@$%&]"), " ")

        // 5. Clean up redundant spaces and trim
        return cleaned
            .replace(Regex("[ \\t]+"), " ")
            .replace(Regex("\\n{2,}"), "\n")
            .trim()
    }
}
