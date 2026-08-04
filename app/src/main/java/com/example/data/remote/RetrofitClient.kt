package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.BilingualExample
import com.example.data.model.Content
import com.example.data.model.GenerateContentRequest
import com.example.data.model.GenerationConfig
import com.example.data.model.LessonContent
import com.example.data.model.Part
import com.example.data.model.ThinkingConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"
    private const val TAG = "RetrofitClient"

    // Models according to gemini-api skill
    const val MODEL_FLASH = "gemini-3.5-flash"
    const val MODEL_PRO = "gemini-3.1-pro-preview"
    const val MODEL_FLASH_LITE = "gemini-3.5-flash"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        retrofit.create(GeminiApiService::class.java)
    }

    private fun getApiKey(): String {
        return BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() && it != "MY_GEMINI_API_KEY" }
            ?: ""
    }

    private fun cleanJsonString(raw: String): String {
        return raw.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
    }

    suspend fun generateBilingualLesson(
        topicEn: String,
        topicAr: String,
        level: String
    ): LessonContent = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            throw IllegalStateException("GEMINI_API_KEY is not set in .env or Secrets.")
        }

        val prompt = """
            You are a bilingual English-to-Arabic grammar teacher who explains things in Urdu (اردو) because the student's native language is Urdu.
            Create a comprehensive, clear, and highly engaging lesson on:
            English Topic: '$topicEn'
            Arabic Equivalent: '$topicAr'
            Student Level: '$level'

            Your lesson MUST follow this exact 7-step pedagogical method comparing English grammar to Classical Arabic grammar (الفصحى), but explained entirely in Urdu (اردو):
            1. English rule explained clearly in Urdu.
            2. "Likewise in Arabic... (اسی طرح عربی میں)" connecting the English rule to the closest Arabic grammar rule, explained in Urdu.
            3. Important similarities between the two languages (explained in Urdu).
            4. Important differences between the two languages (explained in Urdu).
            5. Bilingual examples: English sentence, Arabic translation, and explanation/connection note in Urdu.
            6. Common mistakes that students make with this rule (e.g. word-for-word translation mistakes), explained in Urdu, with correct version.
            7. One short practice question at the end with the answer hint in Urdu.

            Important: Every explanation, similarity, difference, mistake, note, and practice answer hint MUST be written in fluent, grammatically correct Urdu (اردو).

            Return strictly a valid JSON object matching this schema:
            {
              "topicTitleEn": "$topicEn",
              "topicTitleAr": "Urdu/Arabic Title matching the concept",
              "englishRule": "Explanation of English rule in Urdu",
              "arabicConnection": "Connecting to Arabic grammar rule, explained in Urdu",
              "similarities": ["similarity 1 in Urdu", "similarity 2 in Urdu"],
              "differences": ["difference 1 in Urdu", "difference 2 in Urdu"],
              "examples": [
                {"englishSentence": "English example", "arabicTranslation": "Arabic translation (MSA)", "note": "Grammar connection note in Urdu"}
              ],
              "commonMistake": "Common mistake and correction explained in Urdu",
              "practiceQuestion": "Practice question in English or Urdu",
              "practiceAnswerHint": "Practice answer hint in Urdu"
            }
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(
                temperature = 0.4f
            )
        )

        try {
            val response = service.generateContent(MODEL_FLASH, apiKey, request)
            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw Exception("Empty response from Gemini")
            val cleaned = cleanJsonString(rawText)
            json.decodeFromString<LessonContent>(cleaned)
        } catch (e: Exception) {
            Log.e(TAG, "Error generating bilingual lesson", e)
            throw e
        }
    }

    suspend fun sendTutorChatMessage(
        conversationHistory: List<Content>,
        newUserMessage: String,
        level: String
    ): Pair<String, List<Content>> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            throw IllegalStateException("GEMINI_API_KEY is not set.")
        }

        val systemInstruction = Content(
            role = "system",
            parts = listOf(Part(text = """
                You are my comparative grammar teacher. You explain English grammar concepts by comparing them with Arabic grammar rules, but you write all your explanations, feedback, and dialogues in Urdu (اردو) because Urdu is my native language.
                
                For every lesson or correction:
                1. Explain the English rule in simple, clear Urdu.
                2. Connect it to the closest Classical Arabic grammar concept (likewise in Arabic... اسی طرح عربی میں آپ نے سیکھا ہے...) using Urdu explanations.
                3. Explain the key similarities and differences in Urdu.
                4. Give examples in English followed by their Arabic translations and Urdu notes.
                5. Point out common mistakes students make with this rule, explained in Urdu.
                6. Ask me one short practice question in Urdu or English at the end.
                7. Correct my answer kindly, explaining the rule again through Arabic comparisons when useful, in Urdu.

                Always explain the concepts in Urdu, using Arabic grammar terms (like Mubtada, Khabar, Harf, Fe'l, Fail, Maf'ool, Harf-e-Shart, Al-Ta'reef) to make English structures familiar to me.
                My current level is: '$level'. Do not simply translate. Teach the grammar connection.
            """.trimIndent()))
        )

        val updatedContents = conversationHistory.toMutableList()
        updatedContents.add(Content(role = "user", parts = listOf(Part(text = newUserMessage))))

        val request = GenerateContentRequest(
            contents = updatedContents,
            systemInstruction = systemInstruction,
            generationConfig = GenerationConfig(temperature = 0.6f)
        )

        val response = service.generateContent(MODEL_FLASH, apiKey, request)
        val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: "معذرت، میں ابھی جواب دینے قاصر ہوں۔ دوبارہ کوشش کریں۔"

        updatedContents.add(Content(role = "model", parts = listOf(Part(text = replyText))))
        Pair(replyText, updatedContents)
    }

    suspend fun analyzeDeepGrammar(
        inputSentence: String,
        level: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            throw IllegalStateException("GEMINI_API_KEY is not set.")
        }

        val prompt = """
            You are an advanced bilingual linguist and English-Arabic comparative grammar analyst.
            Perform a Deep Grammatical Analysis (تقابلی گرامر کا تفصیلی تجزیہ) on the following sentence or grammatical query:
            "$inputSentence"

            The student's native language is Urdu, and they know Arabic grammar. Explain everything in Urdu (اردو) using comparisons with Arabic grammar.
            The student level is: $level.
            Provide:
            1. **English Grammatical Dissection**: Break down clause structure, tense, voice, and syntax role of key words in Urdu.
            2. **Arabic Comparative Analysis**: Show how this sentence translates to Modern Standard Arabic (الفصحى), compare grammatical roles (like Mubtada/Khabar, Fail/Fa'il/Mafo'ol, Hal, Tamyiz, Mudhaf Ilaih) and explain the difference between English and Arabic structures, all explained in Urdu.
            3. **Subtle Nuances & Edge Cases**: Explain any idiomatic or syntactic subtleties where literal translation fails, explained in Urdu.
            4. **Summary Rule**: A clear takeaway rule in Urdu.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(
                thinkingConfig = ThinkingConfig(thinkingLevel = "high")
            )
        )

        val response = service.generateContent(MODEL_PRO, apiKey, request)
        response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: "میں ابھی تفصیلی تجزیہ مکمل نہیں کر سکا۔"
    }

    suspend fun searchIdiomAndRealWorldUsage(
        query: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            throw IllegalStateException("GEMINI_API_KEY is not set.")
        }

        val prompt = """
            You are a bilingual English-Arabic idioms and real-world usage expert. You explain things in Urdu (اردو) for Urdu native students.
            Use Google Search to find accurate, up-to-date real-world usage, origins, and modern context for:
            "$query"

            Please provide the following explained in Urdu:
            1. **English Meaning & Authentic Context**: What it means and how modern native English speakers actually use it today.
            2. **Closest Arabic Equivalent / Proverb (عربی متبادل مثل یا تعبیر)**: Provide the authentic Arabic proverb/expression (MSA), and explain its meaning and Urdu cultural equivalent.
            3. **Bilingual Examples**: 3 practical sentence examples with Arabic translation and grammatical/contextual explanation in Urdu.
            4. **Why Direct Translation Fails**: Explain in Urdu why translating word-for-word creates incorrect or funny meanings.
        """.trimIndent()

        val googleSearchTool = buildJsonObject {
            putJsonObject("googleSearch") {}
        }

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            tools = listOf(googleSearchTool),
            generationConfig = GenerationConfig(temperature = 0.5f)
        )

        val response = service.generateContent(MODEL_FLASH, apiKey, request)
        val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: "میں اس وقت تلاش کا نتیجہ حاصل نہیں کر سکا۔"
        replyText
    }

    suspend fun checkPracticeAnswer(
        question: String,
        userAnswer: String,
        ruleContext: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            throw IllegalStateException("GEMINI_API_KEY is not set.")
        }

        val prompt = """
            You are a kind bilingual English-to-Arabic grammar teacher who explains things in Urdu.
            Question asked: "$question"
            Student's answer: "$userAnswer"
            Rule context: "$ruleContext"

            Please:
            1. State clearly in Urdu if the answer is Correct (درست جواب!) or Needs Improvement (اچھی کوشش، لیکن...).
            2. Explain kindly in Urdu, through comparisons with Arabic grammar, why it is right or what the correction is.
            3. Give one brief encouraging remark in Urdu.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(temperature = 0.3f)
        )

        val response = service.generateContent(MODEL_FLASH_LITE, apiKey, request)
        response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: "آپ کے جواب کا شکریہ! ممارست اور مشق جاری رکھیں۔"
    }
}
