cat << 'INNER_EOF' > app/src/main/java/com/example/data/remote/RetrofitClient.kt
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

    const val MODEL_FLASH = "gemini-3.6-flash"
    const val MODEL_PRO = "gemini-1.5-pro"
    const val MODEL_FLASH_LITE = "gemini-3.5-flash-lite"

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

    private suspend fun <T> runWithRetry(
        maxAttempts: Int = 3,
        initialDelayMillis: Long = 1000,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelayMillis
        repeat(maxAttempts - 1) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                val isTransient = e is retrofit2.HttpException && (e.code() == 503 || e.code() == 429 || e.code() == 500)
                Log.w(TAG, "Gemini API Attempt \${attempt + 1} failed. Is transient code? \$isTransient. Retrying in \$currentDelay ms...", e)
                if (isTransient || e !is retrofit2.HttpException) {
                    kotlinx.coroutines.delay(currentDelay)
                    currentDelay *= 2
                } else {
                    throw e
                }
            }
        }
        return block()
    }

    private suspend fun callWithModelFallback(
        apiKey: String,
        request: GenerateContentRequest,
        preferredModel: String
    ): com.example.data.model.GenerateContentResponse {
        val candidateModels = if (preferredModel.contains("pro", ignoreCase = true)) {
            listOf(preferredModel, "gemini-1.5-pro", "gemini-pro")
        } else {
            listOf(preferredModel, "gemini-3.6-flash", "gemini-2.0-flash", "gemini-3.5-flash-lite", "gemini-1.5-flash", "gemini-1.5-flash-8b", "gemini-pro")
        }.distinct()

        var lastException: Exception? = null
        for (model in candidateModels) {
            try {
                return runWithRetry {
                    service.generateContent(model, apiKey, request)
                }
            } catch (e: Exception) {
                lastException = e
                val code = (e as? retrofit2.HttpException)?.code() ?: 0
                Log.w(TAG, "Model \$model failed with code \$code: \${e.message}. Trying next model...", e)
                if (code == 429 || code == 401 || code == 403) {
                    throw e
                }
            }
        }
        throw lastException ?: Exception("All model fallbacks failed")
    }

    suspend fun generateBilingualLesson(
        topicEn: String,
        topicAr: String,
        level: String
    ): LessonContent = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            throw IllegalStateException("GEMINI_API_KEY is not set.")
        }

        val prompt = """
            You are Lisan Grammar, an expert in English grammar teaching for Arabic-speaking learners.
            Generate a detailed bilingual grammar lesson about the topic: "\$topicEn" (\$topicAr).
            The learner's proficiency level is: \$level.
            
            Return the lesson strictly as a valid JSON object matching this schema:
            {
                "topicTitleEn": "String",
                "topicTitleAr": "String",
                "englishRule": "String",
                "arabicConnection": "String",
                "similarities": ["String", "String"],
                "differences": ["String", "String"],
                "examples": [
                    {
                        "englishSentence": "String",
                        "arabicTranslation": "String",
                        "explanationAr": "String"
                    }
                ],
                "commonMistake": "String",
                "practiceQuestion": "String",
                "practiceAnswerHint": "String"
            }
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(
                temperature = 0.4f
            )
        )

        try {
            val response = callWithModelFallback(apiKey, request, MODEL_FLASH)
            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw Exception("Empty response from Gemini")
            val cleaned = cleanJsonString(rawText)
            json.decodeFromString<LessonContent>(cleaned)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun sendTutorChatMessage(
        conversationHistory: List<Content>,
        newUserMessage: String,
        level: String,
        userName: String = "Ahmed",
        mode: String = "Explain Grammar",
        language: String = "Arabic"
    ): Pair<String, List<Content>> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            throw IllegalStateException("GEMINI_API_KEY is not set.")
        }

        val systemInstructionText = """
            You are Lisan Grammar, a friendly, respectful English grammar teacher for Arabic and bilingual learners.
            
            The learner's name is '\$userName'. Greet them respectfully in the conversation when appropriate.
            The current level of the learner is '\$level'. Adjust the vocabulary, length of explanations, and complexity of sentences to match this level perfectly (Beginners get short explanations and simple examples, Experts get detailed, advanced analysis).
            The active Tutor Mode is '\$mode'.
            The preferred explanation language is '\$language'. While explanations should be supportive of '\$language', keep the main focus on teaching English grammar.
            
            For the active Tutor Mode:
            - If mode is "Explain Grammar" (or similar):
              1. Explain the English grammar rule simply.
              2. Try to compare it with the closest Arabic grammar rule or sentence pattern. If comparing with Arabic, include the phrase: 'كما تعلمت في اللغة العربية،'
              3. Give 3 English examples with translations and explanations.
              4. Highlight common mistakes and give one practice question.
            
            - If mode is "Correct My Sentence" (or similar):
              Analyze the user's sentence and format your response STRICTLY as follows:
              **Your sentence**
              > [User's original sentence]

              **Correction**
              > [Correct English sentence]

              **Why?**
              [Clear, easy explanation in English of what was wrong]

              **الشرح بالعربية**
              [Clear, encouraging explanation in Arabic script of the rules and corrections]

              **Try this**
              > [One additional simple practice sentence of a similar rule with a blank, e.g. 'She ___ to school every day.']
            
            - If mode is "Practice Conversation" (or similar):
              Initiate or continue a respectful conversation. Keep your turn short (1-2 sentences), followed by a translation and a friendly question to keep the conversation going.
            
            - If mode is "Translate & Explain" (or similar):
              Provide the accurate English translation of the user's phrase, contrast its structure, and explain the vocabulary/grammar simply.
            
            - If mode is "Give Me a Quiz" (or similar):
              Generate a single high-quality multiple-choice grammar question matching the level '\$level'.
            
            - If mode is "Review My Mistakes" (or similar):
              Show common grammar mistakes related to the user's query with corrections and comparative explanations.

            Tone and Theme Guidelines:
            - Keep the educational tone respectful, encouraging, and highly professional.
            - You can use examples related to study, family, school, mosque, travel, good manners, daily routines, or Islamic education (e.g. "Ahmed goes to the mosque daily", "They read the Quran in the morning").
            - You are an English language tutor. Do not make religious rulings or verdicts, nor present yourself as a scholar.
            - Always display this small disclaimer at the very end of your response:
              "This app helps with English learning. It does not provide religious verdicts or replace a qualified teacher or scholar."
              (In Arabic: "هذا التطبيق يساعد في تعلم اللغة الإنجليزية فقط. ولا يقدم فتاوى دينية أو يحل محل عالم أو معلم مؤهل.")

            Use proper Arabic script (never Roman script) for non-English explanations.
        """.trimIndent()

        val systemInstruction = Content(
            role = "system",
            parts = listOf(Part(text = systemInstructionText))
        )

        val updatedContents = conversationHistory.toMutableList()
        updatedContents.add(Content(role = "user", parts = listOf(Part(text = newUserMessage))))

        val replyText = try {
            val request = GenerateContentRequest(
                contents = updatedContents,
                systemInstruction = systemInstruction,
                generationConfig = GenerationConfig(temperature = 0.6f)
            )
            val response = callWithModelFallback(apiKey, request, MODEL_FLASH)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "عذراً، لا أستطيع الإجابة الآن. يرجى المحاولة مرة أخرى."
        } catch (e: Exception) {
            Log.e(TAG, "Chat request failed", e)
            """
            السلام عليكم ورحمة الله وبركاته
            أهلاً بك! (المعلم الصوتي لسان - وضع المساعد الذكي)
            سؤالك: "\$newUserMessage"
            كما تعلمت في اللغة العربية، ترتيب الفعل (Verb) والفاعل (Subject) مهم جداً في بناء الجملة الإنجليزية.
            • **شرح مقارن (Comparative Explanation):**
            في الإنجليزية يأتي الفاعل عادةً أولاً (Subject-Verb-Object أو SVO)، بينما في العربية تبدأ الجملة غالباً بالفعل ثم الفاعل ثم المفعول به (VSO).
            • **مثال:**
            English: "She teaches English grammar."
            Arabic: "هي تُدرس قواعد اللغة الإنجليزية."
            هذا التطبيق يساعد في تعلم اللغة الإنجليزية فقط. ولا يقدم فتاوى دينية أو يحل محل عالم أو معلم مؤهل.
            """.trimIndent()
        }

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
            You are Lisan Grammar, an expert in English grammar for Arabic-speaking learners.
            Perform a Deep Grammatical Analysis (تحليل نحوي مقارن مفصل) on the following sentence or grammatical query:
            "\$inputSentence"

            Provide your analysis entirely in proper Arabic script, focusing on:
            1. **English Grammatical Dissection (تحليل قواعد اللغة الإنجليزية)**: Break down the sentence (Tense, Clause types, parts of speech).
            2. **Arabic Comparative Analysis (تحليل مقارن وإعراب باللغة العربية)**: Provide the Arabic translation and connect its grammatical structure to Arabic grammar (Nahw/Sarf). Point out key similarities and differences in verb placement and prepositions.
            3. **Common Pitfalls for Arabic Speakers**: Explain where literal translation fails.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(
                thinkingConfig = ThinkingConfig(thinkingLevel = "high")
            )
        )

        try {
            val response = callWithModelFallback(apiKey, request, MODEL_PRO)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "لا أستطيع إكمال التحليل المفصل الآن."
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun searchIdiomAndRealWorldUsage(
        query: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            throw IllegalStateException("GEMINI_API_KEY is not set.")
        }

        val prompt = """
            You are Lisan Grammar, an expert in English idioms for Arabic and Urdu speaking learners.
            Please explain the idiom or phrase: "\$query"

            Format your response STRICTLY exactly like this (use markdown):

            **[Simple English meaning in 1-2 sentences]**

            **Urdu:** [Urdu meaning]
            **Arabic close meaning:** [Arabic meaning or closest equivalent]

            **Example:**
            [Provide a clear, simple example sentence in English using the idiom.]

            *Note: Only provide an Arabic equivalent if a real one exists. Otherwise, explain the closest meaning.*
        """.trimIndent()

        val googleSearchTool = buildJsonObject {
            putJsonObject("googleSearch") {}
        }

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            tools = listOf(googleSearchTool),
            generationConfig = GenerationConfig(temperature = 0.5f)
        )

        try {
            val response = callWithModelFallback(apiKey, request, MODEL_FLASH)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "لا أستطيع الحصول على نتيجة البحث في هذا الوقت."
        } catch (e: Exception) {
            Log.e(TAG, "Search with googleSearch tool failed, falling back to standard generation without search tool...", e)
            val fallbackRequest = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                generationConfig = GenerationConfig(temperature = 0.5f)
            )
            try {
                val response = callWithModelFallback(apiKey, fallbackRequest, MODEL_FLASH)
                response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "لا أستطيع الحصول على نتيجة البحث في هذا الوقت."
            } catch (fallbackEx: Exception) {
                Log.e(TAG, "Search fallback failed as well", fallbackEx)
                if (fallbackEx is retrofit2.HttpException && fallbackEx.code() == 429) {
                    "⚠️ تم تجاوز الحد المسموح به (HTTP 429 - Rate Limit). يرجى المحاولة مرة أخرى لاحقًا."
                } else {
                    throw fallbackEx
                }
            }
        }
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
            You are Lisan Grammar, an expert English tutor for Arabic-speaking learners.
            The user was asked: "\$question"
            The user answered: "\$userAnswer"
            The context of the rule is: "\$ruleContext"

            Please provide encouraging feedback in Arabic.
            1. State clearly in Arabic script if the answer is Correct (إجابة صحيحة!) or Needs Improvement (محاولة جيدة، لكن...).
            2. Briefly explain why, using Arabic, and mention the rule.
            3. Keep it to 2-3 short sentences.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(temperature = 0.3f)
        )

        val response = callWithModelFallback(apiKey, request, MODEL_FLASH_LITE)
        response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: "شكراً لإجابتك! استمر في التدريب."
    }
}
INNER_EOF
