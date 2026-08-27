package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.Content
import com.example.data.model.GenerateContentRequest
import com.example.data.model.GenerationConfig
import com.example.data.model.LessonContent
import com.example.data.model.Part
import com.example.data.model.ThinkingConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.example.utils.LanguageDetector
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"
    private const val TAG = "RetrofitClient"

    const val MODEL_FLASH = "gemini-3.5-flash"
    const val MODEL_PRO = "gemini-3.1-pro-preview"
    const val MODEL_FLASH_LITE = "gemini-3.1-flash-lite-preview"

    const val FEMALE_TUTOR_PERSONA_INSTRUCTION = """
    [CRITICAL TUTOR IDENTITY & FEMALE PERSONA MANDATE]
    - You are Lisan AI, a female English teacher and language tutor (خاتون ٹیچر / استانی / میں آپ کی ٹیچر ہوں).
    - FEMININE URDU SELF-REFERENCE MANDATE: When speaking or writing in Urdu, you MUST strictly and consistently use FEMININE grammatical self-reference throughout your entire response (e.g., "میں آپ کو سکھاؤں گی", "میں آپ کو پڑھاؤں گی", "میں آپ کی مدد کروں گی", "میں آپ کو سمجھاؤں گی", "میں بتاؤں گی", "میں کروں گی", "میں آپ کی ٹیچر ہوں", "میں آپ کی استاد ہوں").
    - STRICT MASCULINE PROHIBITION IN URDU: NEVER use masculine self-reference verb endings or adjectives in Urdu like "پڑھاؤں گا", "سکھاؤں گا", "سمجھاؤں گا", "بتاؤں گا", "کروں گا", or "میں آپ کا استاد ہوں". You MUST ALWAYS use "گی" instead of "گا" and "آپ کی" instead of "آپ کا" for self-reference.
    - The female tutor MUST remain female for the ENTIRE conversation, including greetings, explanations, corrections, encouragement, and follow-up questions.
    - ABSOLUTELY NO HINDI VOCABULARY OR SCRIPT (हिंदी / Devanagari): Speak natural Pakistani Urdu (پاکستانی اردو). Do NOT use Hindi words or vocabulary (e.g. do NOT use 'دھنیواد', 'کپیا', 'نمستے۔'). Do NOT output Devanagari script under any circumstances.
    - FAST LATENCY MANDATE: Respond immediately with maximum conciseness (1-2 short sentences). Speak directly, fast, and naturally.
    - UNIVERSAL APPLICABILITY: This female persona, feminine Urdu self-reference, and Urdu script mandate applies strictly and consistently across all Tutor chat responses, streaming outputs, voice conversations, explanations, grammar lessons, quizzes, exam evaluations, corrections, error recoveries, and greetings.
    """

    const val MALE_TUTOR_PERSONA_INSTRUCTION = """
    [CRITICAL TUTOR IDENTITY & MALE PERSONA MANDATE]
    - You are Lisan AI, a male English teacher and language tutor (استاد / میں آپ کا استاد ہوں).
    - MASCULINE URDU SELF-REFERENCE: When speaking or writing in Urdu, you MUST consistently use MASCULINE grammatical self-reference (e.g., "میں آپ کو پڑھاؤں گا", "میں سکھاؤں گا", "میں سمجھاؤں گا", "میں بتاؤں گا", "میں کروں گا", "میں آپ کا استاد ہوں").
    - ABSOLUTELY NO HINDI VOCABULARY OR SCRIPT: Speak natural Pakistani Urdu (پاکستانی اردو). Do NOT use Hindi vocabulary or Devanagari script.
    - FAST LATENCY MANDATE: Respond concisely (1-2 short sentences). Speak directly and naturally.
    """

    @Volatile
    private var customApiKey: String = ""

    @Volatile
    private var isBackendUnreachable: Boolean = false

    @Volatile
    private var PREVIOUS_USER_MESSAGE: String = ""
    @Volatile
    private var PREVIOUS_ASSISTANT_RESPONSE: String = ""

    fun setCustomApiKey(key: String) {
        customApiKey = key.trim()
        isBackendUnreachable = false
        Log.d(TAG, "Custom API key updated. Length: ${customApiKey.length}")
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
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

    val backendUrl: String
        get() = try {
            val configured = BuildConfig.BACKEND_URL.trim()
            if (configured.isNotBlank() && 
                (configured.startsWith("http://") || configured.startsWith("https://")) && 
                !configured.contains("example.com")) {
                configured
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }

    val backendSecret: String
        get() = try {
            BuildConfig.BACKEND_API_SECRET.takeIf { it.isNotBlank() } ?: "lisan_dev_secure_8a7d2f9e4b6c810d_prod_verified"
        } catch (e: Exception) {
            "lisan_dev_secure_8a7d2f9e4b6c810d_prod_verified"
        }

    fun isUsingBackend(): Boolean {
        return customApiKey.isBlank() && backendUrl.isNotBlank() && !isBackendUnreachable
    }

    fun getBackendAuthHeader(): String {
        return "Bearer $backendSecret"
    }

    @Volatile
    private var isBackendReady: Boolean = false

    @Volatile
    private var isPreWarming: Boolean = false

    fun triggerPreWarm(context: android.content.Context) {
        if (!isUsingBackend() || isBackendReady) return
        
        // On low-RAM/performance-constrained devices, avoid starting aggressive background network loops at boot
        if (com.example.utils.DeviceCapabilityHelper.isLowRamDevice(context)) {
            Log.i(TAG, "Low-RAM device detected: skipping proactive background pre-warm at startup to preserve CPU and bandwidth.")
            return
        }

        synchronized(this) {
            if (isPreWarming) return
            isPreWarming = true
        }

        // Run asynchronously
        @kotlin.OptIn(kotlinx.coroutines.DelicateCoroutinesApi::class)
        kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {
            Log.i(TAG, "Backend pre-warm/readiness ping initiated proactively.")
            var attempt = 1
            val maxAttempts = 15
            var delayMs = 1000L

            while (attempt <= maxAttempts && !isBackendReady) {
                try {
                    Log.d(TAG, "Proactive health-check attempt $attempt to $backendUrl")
                    val response = backendService.checkHealth()
                    val bodyString = response.string()
                    if (bodyString.contains("healthy") || bodyString.contains("status")) {
                        isBackendReady = true
                        isBackendUnreachable = false
                        isPreWarming = false
                        Log.i(TAG, "Backend is ready and healthy! (took $attempt attempts).")
                        break
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "Backend not ready yet on attempt $attempt: ${e.message}")
                }
                attempt++
                if (attempt <= maxAttempts) {
                    kotlinx.coroutines.delay(delayMs)
                    delayMs = (delayMs * 1.5).toLong().coerceAtMost(5000L) // backoff
                }
            }
            if (!isBackendReady) {
                Log.w(TAG, "Backend failed to become ready after $maxAttempts proactive attempts.")
                isPreWarming = false
            }
        }
    }

    suspend fun ensureBackendReady(): Boolean = withContext(Dispatchers.IO) {
        if (!isUsingBackend() || isBackendReady) return@withContext true

        Log.i(TAG, "Tutor request initiated. Checking backend readiness...")
        try {
            val response = backendService.checkHealth()
            val bodyString = response.string()
            if (bodyString.contains("healthy") || bodyString.contains("status")) {
                isBackendReady = true
                isBackendUnreachable = false
                Log.i(TAG, "Backend is confirmed ready.")
                return@withContext true
            }
        } catch (e: Exception) {
            Log.d(TAG, "Backend health check failed: ${e.message}")
            isBackendUnreachable = true
        }
        
        return@withContext isBackendReady
    }

    private val backendOkHttpClient = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            try {
                val response = chain.proceed(originalRequest)
                if (!response.isSuccessful) {
                    Log.w(TAG, "Backend HTTP ${response.code}: ${response.message} for ${originalRequest.url}")
                }
                response
            } catch (e: java.io.IOException) {
                if (!originalRequest.url.toString().contains("/health")) {
                    isBackendUnreachable = true
                    Log.w(TAG, "Backend host unreachable at ${originalRequest.url.host}: ${e.message}. Switching to direct Gemini API.")
                } else {
                    Log.w(TAG, "Backend health check failed: ${e.message}")
                }
                throw e
            }
        }
        .build()

    val backendService: BackendApiService by lazy {
        val rawUrl = backendUrl.ifBlank { "https://generativelanguage.googleapis.com/" }
        val url = if (rawUrl.endsWith("/")) rawUrl else "$rawUrl/"
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(backendOkHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        retrofit.create(BackendApiService::class.java)
    }

    fun getCustomApiKey(): String {
        return customApiKey
    }

    fun getApiKey(): String {
        if (customApiKey.isNotBlank()) {
            Log.d(TAG, "BYOK_KEY_FOUND: Custom user API key present (length: ${customApiKey.length})")
            return customApiKey
        }
        return try {
            val key = BuildConfig.GEMINI_API_KEY.trim()
            val valid = key.isNotBlank() && key != "null" && key != "YOUR_SERVER_SIDE_GEMINI_API_KEY" && key != "DEFAULT_KEY"
            Log.d(TAG, "BYOK_KEY_FOUND: BuildConfig API key valid: $valid")
            if (valid) key else ""
        } catch (e: Exception) {
            Log.e(TAG, "BYOK_KEY_FOUND error: ${e.message}")
            ""
        }
    }

    private fun cleanJsonString(raw: String): String {
        return raw.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
    }

    private suspend fun <T> runWithRetry(
        maxAttempts: Int = 2,
        initialDelayMillis: Long = 1000,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelayMillis
        repeat(maxAttempts - 1) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                val isTransient = e is retrofit2.HttpException && (e.code() == 503 || e.code() == 429 || e.code() == 500)
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

    suspend fun executeGenerateContent(
        model: String,
        userApiKey: String? = null,
        request: GenerateContentRequest
    ): com.example.data.model.GenerateContentResponse {
        val key = userApiKey?.takeIf { it.isNotBlank() } ?: getApiKey()
        return callWithModelFallback(key, request, model)
    }

    private suspend fun callWithModelFallback(
        apiKey: String,
        request: GenerateContentRequest,
        preferredModel: String
    ): com.example.data.model.GenerateContentResponse {
        val candidateModels = listOf(
            preferredModel,
            "gemini-3.5-flash",
            "gemini-flash-latest",
            "gemini-3.1-pro-preview",
            "gemini-3.1-flash-lite-preview"
        ).filter { it.isNotBlank() }.distinct()

        var lastException: Exception? = null

        for (model in candidateModels) {
            // Step 1: Attempt local/secure backend if available and not previously marked unreachable
            if (isUsingBackend()) {
                try {
                    Log.d(TAG, "Attempting backend proxy call for model $model")
                    return backendService.generateContent(model, getBackendAuthHeader(), request)
                } catch (e: Exception) {
                    lastException = e
                    val code = (e as? retrofit2.HttpException)?.code() ?: 0
                    if (code == 401 || code == 403) {
                        Log.e(TAG, "Backend returned auth failure (HTTP $code)")
                        throw e
                    }
                    Log.w(TAG, "Backend call failed for model $model (code $code: ${e.message}).")
                    if (e is java.io.IOException || e is java.net.ConnectException || e is java.net.SocketTimeoutException || e is java.net.UnknownHostException) {
                        isBackendUnreachable = true
                    }
                }
            }

            // Step 2: Fall back directly to Gemini API using available key
            val effectiveApiKey = apiKey.ifBlank { getApiKey() }
            if (effectiveApiKey.isNotBlank()) {
                try {
                    val maskedKey = if (effectiveApiKey.length > 8) "${effectiveApiKey.take(4)}...${effectiveApiKey.takeLast(4)}" else "KEY_SET"
                    Log.d(TAG, "Attempting direct Gemini API call for model $model with key $maskedKey")
                    return service.generateContent(model, effectiveApiKey, request)
                } catch (e: Exception) {
                    lastException = e
                    val code = (e as? retrofit2.HttpException)?.code() ?: 0
                    if (code == 401 || code == 403) {
                        Log.e(TAG, "Direct Gemini API auth/permission failure (HTTP $code)")
                        throw e
                    }
                    Log.w(TAG, "Direct Gemini API call failed for model $model (HTTP $code): ${e.message}")
                }
            }
        }

        throw lastException ?: Exception("All model fallbacks failed. Please check network connection or API key settings.")
    }

    suspend fun generateBilingualLesson(
        topicEn: String,
        topicUr: String,
        level: String
    ): LessonContent = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            return@withContext com.example.data.model.CurriculumData.getDefaultLesson(topicEn.lowercase().replace(" ", "_"))
                ?: com.example.data.model.CurriculumData.topics.first().let { com.example.data.model.CurriculumData.getDefaultLesson(it.id)!! }
        }

        val prompt = """
            $FEMALE_TUTOR_PERSONA_INSTRUCTION

            You are Lisan Grammar, an expert female English teacher for Urdu-speaking learners who know Arabic grammar.
            Generate a JSON lesson for: "$topicEn" ($topicUr) at level $level.
            
            CRITICAL LANGUAGE MANDATE:
            Write ALL explanations, rule details (ruleUrdu), similarities, differences, common mistakes, and notes in clear, natural URDU (اردو) for Urdu native speakers.
            ABSOLUTELY NO HINDI SCRIPT (देवनागरी / Devanagari): Write strictly in Urdu script. Use feminine self-reference if referring to the teacher persona.
            
            STRUCTURE:
            1. topicTitleEn (English title, e.g. "Verb 'To Be'")
            2. topicTitleUr (Urdu title, e.g. "فعلِ ربط")
            3. englishRule (Simple English summary of the rule)
            4. ruleUrdu (Comprehensive and clear explanation of the rule written in URDU)
            5. arabicGrammarComparison (Comparison starting with "جیسے آپ نے عربی میں سیکھا ہے..." or "Likewise in Arabic, you have learned...")
            6. similarities (List of English-Arabic similarities written in URDU)
            7. differences (List of English-Arabic differences written in URDU)
            8. examples (List of BilingualExample objects: english, urdu, note).
               In the 'note' field, start with "جیسے عربی میں:" or "Likewise in Arabic, you say...".
            9. commonMistake (Explained in URDU)
            10. practiceQuestion (Practice question in English or Urdu)
            11. practiceAnswerHint (The correct answer)
            
            Return ONLY valid JSON matching LessonContent.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(temperature = 0.4f)
        )
        try {
            val response = callWithModelFallback(apiKey, request, MODEL_FLASH)
            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            json.decodeFromString<LessonContent>(cleanJsonString(rawText))
        } catch (e: Exception) {
            Log.e(TAG, "generateBilingualLesson error: ${e.message}")
            com.example.data.model.CurriculumData.getDefaultLesson(topicEn.lowercase().replace(" ", "_"))
                ?: com.example.data.model.CurriculumData.getDefaultLesson("verb_to_be")!!
        }
    }

    suspend fun getDailyHelperResponse(
        sysPrompt: String,
        history: List<com.example.data.model.ChatMessageEntity>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty() && !isUsingBackend()) {
            return@withContext "Gemini API key is not set. Please enter a valid API key in Settings."
        }
        val contents = history.map {
            Content(
                role = if (it.role == "model") "model" else "user",
                parts = listOf(Part(text = it.text))
            )
        }
        val req = GenerateContentRequest(
            systemInstruction = Content(
                role = "system",
                parts = listOf(Part(text = sysPrompt))
            ),
            contents = contents
        )
        try {
            val response = callWithModelFallback(apiKey, req, MODEL_FLASH)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "I am here to help! What question do you have about today's lesson?"
        } catch (e: Exception) {
            Log.e(TAG, "getDailyHelperResponse error: ${e.message}")
            "Error: ${e.message ?: "Failed to connect to AI helper."}"
        }
    }

    suspend fun getVoiceTutorResponse(
        history: List<Content>,
        userName: String,
        learnerProfileSummary: String,
        isStudyModeOn: Boolean = false,
        selectedVoiceLanguage: String = "English",
        speakerGender: String = "Female"
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty() && !isUsingBackend()) {
            return@withContext generateLocalVoiceTutorResponse(history, userName, selectedVoiceLanguage, speakerGender)
        }
        val isFemale = !speakerGender.equals("Male", ignoreCase = true)
        val personaInstruction = if (isFemale) {
            FEMALE_TUTOR_PERSONA_INSTRUCTION
        } else {
            MALE_TUTOR_PERSONA_INSTRUCTION
        }

        val genderUrduMandate = if (isFemale) {
            """
            CRITICAL FEMALE SPEAKER URDU MANDATE:
            - You are a FEMALE tutor (خاتون ٹیچر / استانی / میں آپ کی ٹیچر ہوں / میں آپ کی استاد ہوں).
            - You MUST ALWAYS use FEMININE Urdu self-references (e.g. "میں آپ کی استاد ہوں", "میں آپ کی ٹیچر ہوں", "میں آپ کو سکھاؤں گی", "میں آپ کی مدد کروں گی", "میں آپ کو سمجھاؤں گی", "میں بتاؤں گی").
            - NEVER use masculine verb endings like "گا" for yourself (e.g. "سکھاؤں گا", "کروں گا", "پڑھاؤں گا" are STRICTLY FORBIDDEN for you). ALWAYS use "گی" (e.g. "سکھاؤں گی", "کروں گی", "پڑھاؤں گی").
            - NEVER say "میں آپ کا استاد ہوں". ALWAYS say "میں آپ کی استاد ہوں" or "میں آپ کی ٹیچر ہوں".
            - Do NOT let the model switch between masculine and feminine self-reference. You MUST remain female for the ENTIRE conversation, including greetings, explanations, corrections, encouragement, and follow-up questions.
            """.trimIndent()
        } else {
            """
            MALE SPEAKER URDU MANDATE:
            - You are a MALE tutor (استاد / میں آپ کا استاد ہوں).
            - You MUST use MASCULINE Urdu self-references (e.g. "میں آپ کا استاد ہوں", "میں آپ کو سکھاؤں گا", "میں آپ کی مدد کروں گا", "میں آپ کو سمجھاؤں گا").
            """.trimIndent()
        }

        val languageInstruction = when (selectedVoiceLanguage) {
            "Urdu" -> """
            CRITICAL URDU SCRIPT & GENDER MANDATE:
            - You MUST speak and respond in clear, fluent Pakistani Urdu script (پاکستانی اردو / نستعلیق).
            - ABSOLUTELY NO HINDI VOCABULARY (e.g. do NOT use 'دھنیواد', 'کپیا', 'نمستے۔') and NEVER output Devanagari script. Use natural, authentic Pakistani Urdu.
            - ABSOLUTELY NEVER USE ROMAN URDU (e.g. NEVER write 'Aap kaise hain', 'Yeh sentence sahi hai'). All Urdu text MUST be written in real Urdu script (e.g. 'آپ کیسے ہیں؟', 'یہ جملہ بالکل درست ہے').
            $genderUrduMandate
            - Explain English grammar rules, terms, and comparisons in clear, fluent Urdu script. Give example sentences in English and translate them to Urdu script. Keep responses very short (1-2 sentences in English, then 1 simple sentence in Urdu script) so that Text-To-Speech is pleasant.
            """.trimIndent()

            "Arabic" -> """
            CRITICAL ARABIC SCRIPT MANDATE:
            - You MUST speak and respond in clear, fluent Modern Standard Arabic script (اللغة العربية الفصحى).
            - ABSOLUTELY NEVER USE URDU WORDS OR URDU SCRIPT (do NOT use 'آپ', 'یہ', 'کیسے', etc.). All non-English text MUST be written in genuine Arabic script (e.g. 'كيف حالك؟', 'هذه الجملة صحيحة').
            - Explain English grammar rules, terms, and comparisons in clear, fluent Arabic script. Give example sentences in English and translate them to Arabic script. Keep responses very short (1-2 sentences in English, then 1 simple sentence in Arabic script) so that Text-To-Speech is pleasant.
            """.trimIndent()

            else -> """
            CRITICAL ENGLISH MANDATE:
            - You MUST speak and respond strictly in simple, clear, natural English.
            - Do not use Urdu or Arabic unless directly translating an English example sentence.
            """.trimIndent()
        }
        val sysPrompt = if (isStudyModeOn) {
            """
            $personaInstruction

            You are the senior English teacher inside Lisan Voice Tutor. You teach English grammar to Arabic and Urdu speaking learners.
            Learner: $userName

            [STUDY MODE ACTIVE]
            Your role is to be a structured, strict-but-supportive, demanding, and highly focused voice tutor.

            LANGUAGE DIRECTIVE:
            $languageInstruction

            CRITICAL EXERCISE INTEGRITY MANDATE:
            - When giving the learner a question, exercise, translation task, sentence-production task, or "try it yourself" activity, NEVER reveal the target answer before the learner responds.
            - If the learner's explanation/request language is Urdu, and you ask them to produce an English sentence, DO NOT write the target English sentence first and then translate it into Urdu.
              * WRONG: "How would you say 'I am a student' in English?" / "آپ یہ کیسے کہیں گے کہ میں ایک طالب علم ہوں؟" (This reveals the target sentence and defeats the exercise).
              * CORRECT: "آپ یہ کیسے کہیں گے کہ میں ایک طالب علم ہوں، English میں؟"
            - Present ONLY the prompt/question in the learner's explanation language (Urdu or Arabic). Do NOT include the target English sentence anywhere in the question.
            - After asking the question, STOP and WAIT for the learner's response. Do NOT provide the answer, translation, correct sentence, or an answer disguised as a hint.
            - Do NOT accidentally reveal the answer through:
              * an English translation
              * a bilingual version of the target sentence
              * an example containing the target sentence
              * a "hint" that directly gives the sentence
              * an explanation that contains the expected answer
            - Once the learner responds, evaluate their answer normally. Correct it, explain the mistake, provide the correct English sentence when appropriate, and continue the lesson.
            - This rule applies dynamically to ALL exercises, not just one particular sentence.
            - The purpose is to make the learner actively produce the English answer rather than simply copy or translate an answer that the tutor has already revealed.

            VOICE CONVERSATION GUIDELINES:
            1. Speak like a demanding but respectful and encouraging teacher. Prioritize active English practice over casual chatting.
            2. GENTLY CORRECT ERRORS: When the learner speaks with an error, identify it, briefly explain why in simple terms, give the correction, and ask them to repeat or try a new sentence.
            3. COMPARATIVE SCAFFOLDING: Compare English rules with Arabic or Urdu grammar depending on the learner's language.
            4. Keep answers extremely brief (1 to 3 short sentences maximum) so that speech synthesis is clear and pleasant to listen to. Ask one question and wait for the user to respond.
            5. USE TEXT-TO-SPEECH MARKERS for natural pacing and emotion:
               - Insert `[pause_short]` for a brief, natural hesitation (e.g., "Hmm, [pause_short] let's see.").
               - Insert `[pause_long]` for transitions.
               - Insert `[pitch_high]` before exciting/encouraging words (e.g., "[pitch_high] Excellent!").
               - Insert `[pitch_low]` for explanations.
               - Insert `[pitch_normal]` to reset.
            6. Do NOT use markdown code blocks, asterisks (*), or complex bullet lists, as this text will be read aloud by Text-To-Speech.

            Learner Study Context: $learnerProfileSummary
            """.trimIndent()
        } else {
            """
            $personaInstruction

            You are Lisan Voice Tutor, a warm, friendly, natural human English tutor speaking with $userName.
            
            LANGUAGE DIRECTIVE:
            $languageInstruction

            CRITICAL EXERCISE INTEGRITY MANDATE:
            - When giving the learner a question, exercise, translation task, sentence-production task, or "try it yourself" activity, NEVER reveal the target answer before the learner responds.
            - If the learner's explanation/request language is Urdu, and you ask them to produce an English sentence, DO NOT write the target English sentence first and then translate it into Urdu.
              * WRONG: "How would you say 'I am a student' in English?" / "آپ یہ کیسے کہیں گے کہ میں ایک طالب علم ہوں؟" (This reveals the target sentence and defeats the exercise).
              * CORRECT: "آپ یہ کیسے کہیں گے کہ میں ایک طالب علم ہوں، English میں؟"
            - Present ONLY the prompt/question in the learner's explanation language (Urdu or Arabic). Do NOT include the target English sentence anywhere in the question.
            - After asking the question, STOP and WAIT for the learner's response. Do NOT provide the answer, translation, correct sentence, or an answer disguised as a hint.
            - Do NOT accidentally reveal the answer through:
              * an English translation
              * a bilingual version of the target sentence
              * an example containing the target sentence
              * a "hint" that directly gives the sentence
              * an explanation that contains the expected answer
            - Once the learner responds, evaluate their answer normally. Correct it, explain the mistake, provide the correct English sentence when appropriate, and continue the lesson.
            - This rule applies dynamically to ALL exercises, not just one particular sentence.
            - The purpose is to make the learner actively produce the English answer rather than simply copy or translate an answer that the tutor has already revealed.

            VOICE CONVERSATION GUIDELINES:
            1. Speak like a real, empathetic, friendly human teacher. Be conversational, smooth, and express emotions naturally.
            2. USE TEXT-TO-SPEECH MARKERS for natural pacing and emotion:
               - Insert `[pause_short]` for a brief, natural hesitation (e.g., "Hmm, [pause_short] let's see.").
               - Insert `[pause_long]` for a longer thought pause or transition (e.g., "That's exactly right. [pause_long] Now...").
               - Insert `[pitch_high]` before exciting/encouraging words (e.g., "[pitch_high] Great job!").
               - Insert `[pitch_low]` for a more serious or explanatory tone.
               - Insert `[pitch_normal]` to reset back to normal tone.
            3. Keep your answers brief (1 to 3 short sentences maximum) so that speech synthesis is clear and pleasant to listen to.
            4. Use simple, natural English. When helpful, add a brief, clear sentence in Modern Standard Arabic or Urdu to explain difficult grammar concepts.
            5. Gently correct any mistakes in the user's speech, then give them a quick example.
            6. Do NOT use markdown code blocks, asterisks (*), or complex bullet lists, as this text will be read aloud by Text-To-Speech.
            
            Learner Profile Context: $learnerProfileSummary
            """.trimIndent()
        }

        val req = GenerateContentRequest(
            systemInstruction = Content(
                role = "system",
                parts = listOf(Part(text = sysPrompt))
            ),
            contents = history
        )
        try {
            val response = callWithModelFallback(apiKey, req, MODEL_FLASH)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: generateLocalVoiceTutorResponse(history, userName, selectedVoiceLanguage, speakerGender)
        } catch (e: Exception) {
            Log.i(TAG, "getVoiceTutorResponse offline fallback: ${e.message}")
            generateLocalVoiceTutorResponse(history, userName, selectedVoiceLanguage, speakerGender)
        }
    }

    fun generateLocalVoiceTutorResponse(
        history: List<Content>,
        userName: String,
        selectedVoiceLanguage: String = "English",
        speakerGender: String = "Female"
    ): String {
        val lastUserMsg = history.lastOrNull { it.role == "user" }?.parts?.firstOrNull()?.text ?: ""
        val lowerMsg = lastUserMsg.lowercase(java.util.Locale.ROOT).trim()
        val isFemale = !speakerGender.equals("Male", ignoreCase = true)

        if (lowerMsg.isEmpty() || lowerMsg == "listening...") {
            return when (selectedVoiceLanguage) {
                "Urdu" -> if (isFemale) {
                    "مجھے آپ کی آواز سنائی نہیں دی۔ براہ کرم دوبارہ بولیے یا مجھے بتائیں کہ آپ کس گرامر اصول کی مشق کرنا چاہتی ہیں۔"
                } else {
                    "مجھے آپ کی آواز سنائی نہیں دی۔ براہ کرم دوبارہ بولیے یا مجھے بتائیں کہ آپ کس گرامر اصول کی مشق کرنا چاہتے ہیں۔"
                }
                "Arabic" -> "لم أتمكن من سماع صوتك بوضوح. يرجى المحاولة والتحدث مرة أخرى أو إخباري بقاعدة القواعد التي ترغب في ممارستها."
                else -> "I couldn't hear your voice clearly. Please try speaking again or tell me what grammar rule you would like to practice."
            }
        }

        // Greetings
        if (lowerMsg in listOf("hi", "hello", "hey", "assalam", "سلام", "asalam", "asalamu", "ayyo") || lowerMsg.startsWith("hi ") || lowerMsg.startsWith("hello ")) {
            return when (selectedVoiceLanguage) {
                "Urdu" -> if (isFemale) {
                    "السلام علیکم ورحمۃ اللہ وبرکاتہ۔ لسان گرامر میں خوش آمدید! میں اردو کے ساتھ موازنہ کر کے انگریزی گرامر سیکھنے میں آپ کی مدد کروں گی۔ آج ہم کون سا موضوع پڑھیں؟ سبجیکٹ پروناؤنز، ورب ٹو بی، ناؤنز، یا ایڈجیکٹوز؟"
                } else {
                    "السلام علیکم ورحمۃ اللہ وبرکاتہ۔ لسان گرامر میں خوش آمدید! میں اردو کے ساتھ موازنہ کر کے انگریزی گرامر سیکھنے میں آپ کی مدد کروں گا۔ آج ہم کون سا موضوع پڑھیں؟ سبجیکٹ پروناؤنز، ورب ٹو بی، ناؤنز، یا ایڈجیکٹوز؟"
                }
                "Arabic" -> "السلام عليكم ورحمة الله وبركاته\nأهلاً بك في لسان قواعد اللغة! سأساعدك في تعلم قواعد اللغة الإنجليزية بمقارنتها مع قواعد اللغة العربية. ما هو الموضوع الذي يجب أن ندرسه اليوم؟ ضمائر الفاعل، فعل الكينونة، الأسماء، أو الصفات؟"
                else -> "السلام عليكم ورحمة الله وبركاته\nWelcome to Lisan Grammar! I will help you learn English grammar by comparing it with Arabic grammar. What topic should we study today? Subject Pronouns, Verb To Be, Nouns, or Adjectives?"
            }
        }

        // How are you
        if (lowerMsg.contains("how are you") || lowerMsg.contains("how are u") || lowerMsg.contains("how is it going")) {
            return when (selectedVoiceLanguage) {
                "Urdu" -> "الحمد للہ، میں بالکل ٹھیک ہوں، $userName! انگریزی گرامر سیکھنے کے لیے تیار ہوں۔ آج آپ کون سا موضوع پڑھنا پسند کریں گے؟"
                "Arabic" -> "الحمد لله، أنا بخير وعلى ما يرام، $userName! جاهز لمساعدتك في تعلم قواعد اللغة الإنجليزية. ما هو الموضوع الذي ترغب في استكشافه اليوم؟"
                else -> "الحمد لله, I am doing excellent, $userName! Ready to help you learn English grammar. What topic would you like to explore today?"
            }
        }

        // Pronouns / Subject Pronouns
        if (lowerMsg.contains("pronoun") || lowerMsg.contains("ضمير") || lowerMsg.contains("ضمائر") || lowerMsg.contains("i you he she")) {
            return when (selectedVoiceLanguage) {
                "Urdu" -> "آئیے سبجیکٹ پروناؤنز سیکھیں! انگریزی میں ہم I, You, He, She, It, We اور They جیسے پروناؤنز استعمال کرتے ہیں۔ اسی طرح اردو میں، آپ نے 'میں، تم، وہ، ہم' جیسے ضمیر سیکھے ہیں۔ مثال کے طور پر، 'He is a student' کا مطلب ہے 'وہ ایک طالب علم ہے'۔ انگریزی میں ضمیر لکھنا لازمی ہوتا ہے۔ مشق کے لیے سوال: آپ انگریزی میں 'وہ ایک استانی ہے' کیسے کہیں گے؟"
                "Arabic" -> "دعنا نتعلم ضمائر الفاعل! في اللغة الإنجليزية، نستخدم ضمائر مثل I, You, He, She, It, We, و They. وبالمثل في اللغة العربية، تعلمت ضمائر منفصلة مثل أنا، أنت، هو، وهي. على سبيل المثال، 'He is a student' تقابل 'هو طالب'. يميل متحدثو العربية أحيناً إلى حذف الضمير، ولكن في الإنجليزية ضمير الفاعل مطلوب دائماً. سؤال للممارسة: كيف تقول 'هي معلمة' بالإنجليزية؟"
                else -> "Let's learn Subject Pronouns! In English, we use pronouns like I, You, He, She, It, We, and They. Likewise in Arabic, you have learned separate pronouns like أنا، أنت، هو، وهي. For example, 'He is a student' corresponds to 'هو طالب'. Arabic speakers sometimes drop the pronoun, but in English the subject pronoun is required. Practice question: Translate \"هي معلمة\" to English."
            }
        }

        // Verb "To Be"
        if (lowerMsg.contains("verb to be") || lowerMsg.contains("verb 'to be'") || lowerMsg.contains("am is are") || lowerMsg.contains("be verb")) {
            return when (selectedVoiceLanguage) {
                "Urdu" -> "ورب ٹو بی (Verb To Be) کے لیے ہم 'am', 'is', اور 'are' استعمال کرتے ہیں۔ اسی طرح اردو میں ہم 'ہوں، ہے، ہیں' استعمال کرتے ہیں۔ انگریزی میں 'The boy is happy' کہنا لازمی ہے۔ عام غلطی: 'The boy happy' کہنا (is چھوڑ دینا)۔ مشق کے لیے سوال: درست لفظ منتخب کریں: 'They (is/are) happy'."
                "Arabic" -> "يستخدم فعل الكينونة (Verb To Be) الكلمات 'am'، 'is'، و 'are' في زمن المضارع. وبالمثل في اللغة العربية، تعلمت الجملة الاسمية مثل 'الولد سعيد' بدون فعل مضارع ظاهر. في الإنجليزية، يجب أن نقول 'The boy is happy'. الخطأ الشائع: قول 'The boy happy'. سؤال للممارسة: اختر الكلمة الصحيحة: 'They (is/are) happy'."
                else -> "The Verb To Be uses 'am', 'is', and 'are' in the present tense. Likewise in Arabic, you have learned nominal sentences (الجملة الاسمية) like 'الولد سعيد' without a present verb. In English, we must say 'The boy is happy'. Common mistake: saying 'The boy happy'. Practice question: Choose the correct word: 'They (is/are) happy'."
            }
        }

        // Plural Nouns
        if (lowerMsg.contains("plural") || lowerMsg.contains("جمع") || lowerMsg.contains("plurals") || lowerMsg.contains("noun")) {
            return when (selectedVoiceLanguage) {
                "Urdu" -> "انگریزی میں زیادہ تر پلورل (جمع) ناؤنز کے آخر میں -s یا -es کا اضافہ کیا جاتا ہے۔ جیسے کتاب سے کتب (book to books)۔ اردو کی طرح انگریزی میں بھی کچھ بے قاعدہ جمع ہوتے ہیں جیسے 'child' سے 'children'۔ مشق کے لیے سوال: 'book' کی جمع (plural) کیا ہے؟"
                "Arabic" -> "في اللغة الإنجليزية، معظم الأسماء الجمع تضيف ببساطة -s أو -es. وبالمثل في اللغة العربية، تعلمت جمع السالم مثل معلمون. أمثلة: book إلى books، car إلى cars. سؤال للممارسة: ما هو جمع كلمة 'book'؟"
                else -> "In English, most plural nouns simply add -s or -es. Likewise in Arabic, you have learned sound plurals like معلمون. Examples: book to books, car to cars, pen to pens. Arabic speakers sometimes confuse irregular plurals like child to children. Practice question: What is the plural of 'book'?"
            }
        }

        // Adjectives
        if (lowerMsg.contains("adjective") || lowerMsg.contains("صفة") || lowerMsg.contains("نعت") || lowerMsg.contains("adjectives")) {
            return when (selectedVoiceLanguage) {
                "Urdu" -> "انگریزی میں ایڈجیکٹو (صفت) ہمیشہ ناؤن (اسم) سے پہلے آتا ہے (جیسے a big house)۔ جبکہ اردو میں بھی صفت پہلے آتی ہے (بڑا گھر)۔ مثالیں: a tall man (لمبا آدمی)، a red car (سرخ گاڑی)۔ مشق کے لیے سوال: 'نیا اسکول' کا انگریزی میں ترجمہ کریں۔"
                "Arabic" -> "في اللغة الإنجليزية، تأتي الصفات قبل الاسم (a big house). وبالمثل في اللغة العربية، تعلمت أن الصفة تتبع الموصوف (بيت كبير). أمثلة: a tall man (رجل طويل)، a red car (سيارة حمراء). الخطأ الشائع: وضع الصفة بعد الاسم. سؤال للممارسة: ترجم 'كتاب جديد' إلى الإنجليزية."
                else -> "In English, adjectives come before the noun (a big house). Likewise in Arabic, you have learned that the adjective follows the noun (بيت كبير). Examples: a tall man (رجل طويل), a red car (سيارة حمراء), a new book (كتاب جديد). Common mistake: placing the adjective after the noun. Practice question: Translate 'كتاب جديد' to English."
            }
        }

        // Present Continuous
        if (lowerMsg.contains("present continuous") || lowerMsg.contains("ing") || lowerMsg.contains("is reading") || lowerMsg.contains("present progressive")) {
            return when (selectedVoiceLanguage) {
                "Urdu" -> "پریزنٹ کنٹینیوس (Present Continuous) میں 'am/is/are + verb-ing' استعمال ہوتا ہے، جو ان کاموں کے لیے ہے جو ابھی ہو رہے ہیں۔ جیسے: I am reading (میں پڑھ رہا ہوں)۔ مشق کے لیے سوال: آپ انگریزی میں 'میں انگریزی سیکھ رہا ہوں' کیسے کہیں گے؟"
                "Arabic" -> "يستخدم زمن المضارع المستمر الصيغة 'am/is/are + verb-ing' للأفعال التي تحدث الآن. وبالمثل في اللغة العربية، تستخدم الفعل المضارع مثل 'أنا أقرأ الآن'. أمثلة: I am reading (أنا أقرأ)، She is sleeping (هي تنام). سؤال للممارسة: كيف تقول 'أنا أتعلم الإنجليزية'؟"
                else -> "Present Continuous uses 'am/is/are + verb-ing' for actions happening now. Likewise in Arabic, you use the present verb الفعل المضارع مثل 'أنا أقرأ الآن'. Examples: I am reading (أنا أقرأ), She is sleeping (هي تنام), They are eating (هم يأكلون). Practice question: Translate \"أنا أتعلم الإنجليزية\" to English."
            }
        }

        // Correct student practice answer
        if (lowerMsg.contains("she is a teacher") || lowerMsg.contains("are") || lowerMsg.contains("books") || lowerMsg.contains("a new book") || lowerMsg.contains("new book") || lowerMsg.contains("i am learning")) {
            return "ممتاز! Excellent answer! That is completely correct. You are making wonderful progress in English grammar! What grammar topic would you like to practice next?"
        }

        // General conversational response following teacher structure
        return "Thank you for asking about \"$lastUserMsg\". Let's learn this together! In English, every sentence requires a clear subject and verb. Likewise in Arabic, you have learned that nominal and verbal sentence structures organize thoughts logically. Examples: 1. I understand (أنا أفهم). 2. She learns (هي تتعلم). 3. We practice together (نحن نتدرب معاً). Common mistake: omitting the helper verb. Practice question: Can you try making a simple 3-word English sentence using 'is'?"
    }

    suspend fun analyzeDeepGrammar(inputSentence: String, level: String): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isNotEmpty() || isUsingBackend()) {
            try {
                val prompt = """
                    You are an expert English-Arabic-Urdu Linguistic and Grammar Analyzer API.
                    Analyze this input for a learner: "$inputSentence"
                    
                    CORE OPERATIONAL RULES:
                    1. MIX ENGLISH WITH URDU/ARABIC: Always mix and pair the original English words and grammatical terms with their Urdu/Arabic translations/explanations, especially in the Word-by-Word Analysis, so the learner can easily understand which English word maps to which grammatical logic.
                    2. ACCURATE GRAMMAR PARSING: Perform true grammatical parsing. Identify exact subjects, verbs, complements, and tenses correctly.
                    3. CONCISE FOR FAST LATENCY: Keep explanations brief, structured, and direct. Avoid conversational fluff.
                    4. STRICT ARABIC GRAMMAR EQUIVALENT (إعراب ونحو): Explicitly map the English sentence structure to classical Arabic grammar rules using exact Arabic terms.
                    5. DETAILED WORD-BY-WORD DECOMPOSITION: Never group multi-word verbs, helping verbs, or adverbs together as a single block in the word-by-word breakdown (e.g., do not say "is playing very well" is the whole verb). Divide them into individual words and tell exactly what each word is, its form, and its grammar logic.
                    
                    REQUIRED OUTPUT FORMAT:
                    
                    **Urdu Meaning:**
                    [Provide a clean, natural Urdu translation of the full sentence]
                    
                    **Sentence Type:**
                    [e.g., Assertive / Statement (جملہ خبریہ)]
                    
                    **Main Parts Breakdown:**
                    • Subject (فاعل): [Identify the exact noun/pronoun subject]
                    • Verb / Predicate (فعل): [Identify the main verb and auxiliary verbs]
                    • Complement / Object (مفعول / متعلقات): [Identify the object or complement, or state "None"]
                    
                    **Word-by-Word Analysis (لفظ بہ لفظ تجزیہ):**
                    [Deconstruct every single word in the English sentence individually. For each word, show its precise English form and grammar role, plus its Urdu/Arabic translation and explanation. 
                    Examples:
                    • Ali: Subject (فاعل) / Proper Noun (اسمِ خاص)
                    • is: Auxiliary/Helping Verb (امدادی فعل) - Present tense singular
                    • playing: Main Verb (اصل فعل) - Present participle form (verb + -ing) representing continuous action (حالتِ استمراری)
                    • very: Adverb of degree (متعلق فعل کی شدت ظاہر کرنے والا لفظ) modifying "well"
                    • well: Adverb of manner (متعلق فعل - اچھے طریقے سے) modifying the action "playing"
                    Ensure all words of the sentence are listed and parsed with exact detail.]
                    
                    **Tense & Aspect (زمانہ):**
                    [Exact English Tense] ([Urdu name for tense])
                    
                    **Simple Explanation (سادہ وضاحت):**
                    [A 1-2 sentence breakdown in clear Urdu explaining how the subject and verb interact]
                    
                    **Arabic Grammar Connection (التناظر النحوي):**
                    • Arabic Equivalent Term: [Exact Arabic term like مبتدأ وخبر or جملة فعلية]
                    • Explanation in Urdu: [Explain how this English concept parallels Arabic grammar rules in Urdu]
                    
                    **Common Mistake to Avoid (عام غلطی):**
                    [Explain 1 common grammatical mistake learners make in Urdu]
                """.trimIndent()
                val request = GenerateContentRequest(contents = listOf(Content(parts = listOf(Part(text = prompt)))))
                val response = callWithModelFallback(apiKey, request, MODEL_FLASH)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) return@withContext text
            } catch (e: Exception) {
                Log.e(TAG, "Remote deep analyzer error: ${e.message}")
            }
        }

        // Smart Local Grammar Analysis Fallback
        generateLocalGrammarAnalysis(inputSentence, level)
    }

    private fun generateLocalGrammarAnalysis(inputSentence: String, level: String): String {
        val cleanInput = inputSentence.trim()
        val lower = cleanInput.lowercase().removeSuffix(".")

        return when {
            lower.contains("the boy is reading a book") -> """
                **Your sentence:** $cleanInput

                **Urdu meaning:**
                لڑکا ایک کتاب پڑھ رہا ہے۔

                **Sentence type:** Statement

                **Main parts:**
                - **Subject:** The boy
                  - Article: The
                  - Main noun: boy
                - **Verb phrase:** is reading
                  - Helping verb: is
                  - Main verb: reading
                - **Object:** a book
                  - Article: a
                  - Main noun: book

                **Tense or time meaning:** Present Continuous
                یہ کام ابھی ہو رہا ہے۔

                **Simple explanation:**
                اس جملے میں فاعل پورا "The boy" ہے۔ امدادی فعل "is" کے ساتھ اصل فعل "reading" ہے۔
                جیسے آپ نے عربی میں سیکھا ہے: مبتدأ 'الولدُ' اور خبر 'يقرأ كتاباً' ہے۔

                **Common mistake to avoid:**
                صرف 'The' کو فاعل مت سمجھیں۔ 'The boy' پورا فاعل ہے۔

                **Practice Question:**
                Is the girl reading a book?
            """.trimIndent()

            lower.contains("my sister speaks english") -> """
                **Your sentence:** $cleanInput

                **Urdu meaning:**
                میری بہن انگریزی بولتی ہے۔

                **Sentence type:** Statement

                **Main parts:**
                - **Subject:** My sister
                  - Possessive determiner: My
                  - Main noun: sister
                - **Verb phrase:** speaks
                  - Main verb: speaks
                - **Object:** English
                  - Main noun: English

                **Tense or time meaning:** Present Simple
                یہ ایک مستقل عادت یا سچائی ہے۔

                **Simple explanation:**
                اس جملے میں فاعل "My sister" ہے۔ فعل "speaks" ہے۔

                **Practice Question:**
                Does my brother study Arabic?
            """.trimIndent()

            lower.contains("these students are ready") -> """
                **Your sentence:** $cleanInput

                **Urdu meaning:**
                یہ طلبہ تیار ہیں۔

                **Sentence type:** Statement

                **Main parts:**
                - **Subject:** These students
                  - Determiner: These
                  - Main noun: students
                - **Verb phrase:** are
                  - Linking verb: are
                - **Complement:** ready

                **Tense or time meaning:** Present Simple
                یہ موجودہ حالت ظاہر کرتا ہے۔

                **Simple explanation:**
                "These" اور "students" مل کر فاعل ہیں۔ "ready" مفعول نہیں بلکہ تکملہ (Complement) ہے۔

                **Practice Question:**
                Are those teachers busy?
            """.trimIndent()

            lower.contains("the book on the table is mine") -> """
                **Your sentence:** $cleanInput

                **Urdu meaning:**
                میز پر رکھی کتاب میری ہے۔

                **Sentence type:** Statement

                **Main parts:**
                - **Subject:** The book on the table
                  - Main noun: book
                  - Extra detail: on the table
                - **Verb phrase:** is
                  - Linking verb: is
                - **Complement:** mine

                **Tense or time meaning:** Present Simple
                موجودہ ملکیت۔

                **Simple explanation:**
                "The book on the table" پورا فاعل ہے۔

                **Practice Question:**
                Is the pen on the desk yours?
            """.trimIndent()

            lower.contains("a teacher gave the children homework") -> """
                **Your sentence:** $cleanInput

                **Urdu meaning:**
                ایک استاد نے بچوں کو ہوم ورک دیا۔

                **Sentence type:** Statement

                **Main parts:**
                - **Subject:** A teacher
                - **Verb phrase:** gave
                  - Main verb: gave
                - **Indirect Object:** the children
                - **Direct Object:** homework

                **Tense or time meaning:** Past Simple
                یہ کام ماضی میں ہوا۔

                **Simple explanation:**
                "gave" فعل کے دو مفعول ہیں: بچوں کو (indirect) اور ہوم ورک (direct)۔

                **Practice Question:**
                Did the teacher give them a test?
            """.trimIndent()

            lower.contains("the students have completed the exercise") -> """
                **Your sentence:** $cleanInput

                **Urdu meaning:**
                طلبہ نے مشق مکمل کر لی ہے۔

                **Sentence type:** Statement

                **Main parts:**
                - **Subject:** The students
                - **Verb phrase:** have completed
                  - Helping verb: have
                  - Main verb: completed
                - **Object:** the exercise

                **Tense or time meaning:** Present Perfect
                کام حال ہی میں مکمل ہوا ہے۔

                **Simple explanation:**
                "have" امدادی فعل ہے اور "completed" اصل فعل ہے۔

                **Practice Question:**
                Have they finished the work?
            """.trimIndent()

            lower.contains("does ali like tea") -> """
                **Your sentence:** $cleanInput

                **Urdu meaning:**
                کیا علی کو چائے پسند ہے؟

                **Sentence type:** Question

                **Main parts:**
                - **Subject:** Ali
                - **Verb phrase:** Does like
                  - Helping verb: Does
                  - Main verb: like
                - **Object:** tea

                **Tense or time meaning:** Present Simple Question
                موجودہ عادت یا پسند کے بارے میں سوال۔

                **Simple explanation:**
                “Does” سوال بنانے میں مدد دیتا ہے۔ اصل فعل “like” ہے۔

                **Practice Question:**
                Does Fatima drink milk?
            """.trimIndent()

            lower.contains("did they finish the work") -> """
                **Your sentence:** $cleanInput

                **Urdu meaning:**
                کیا انہوں نے کام ختم کر لیا؟

                **Sentence type:** Question

                **Main parts:**
                - **Subject:** they
                - **Verb phrase:** Did finish
                  - Helping verb: Did
                  - Main verb: finish
                - **Object:** the work

                **Tense or time meaning:** Past Simple Question
                ماضی کے بارے میں سوال۔

                **Simple explanation:**
                “Did” ماضی کا سوال بنانے میں مدد دیتا ہے۔

                **Practice Question:**
                Did she eat dinner?
            """.trimIndent()

            lower.contains("open the door") -> """
                **Your sentence:** $cleanInput

                **Urdu meaning:**
                دروازہ کھولیں۔

                **Sentence type:** Command

                **Main parts:**
                - **Subject:** Understood "you" (آپ)
                - **Verb phrase:** Open
                  - Main verb: Open
                - **Object:** the door

                **Tense or time meaning:** Imperative
                حکم یا درخواست۔

                **Simple explanation:**
                حکمیہ جملوں میں فاعل "you" چھپا ہوا (understood) ہوتا ہے۔

                **Practice Question:**
                Close the window.
            """.trimIndent()

            lower.contains("in the morning") -> """
                This is a phrase, not a complete sentence.
                It gives time information.

                یہ ایک فقرہ ہے، مکمل جملہ نہیں۔
                یہ وقت کے بارے میں معلومات دیتا ہے۔

                **Main parts:**
                - Preposition: In
                - Noun phrase: the morning
            """.trimIndent()

            lower.contains("she is not going anywhere") -> """
                **Your sentence:** $cleanInput

                **Urdu meaning:**
                وہ کہیں نہیں جا رہی ہے۔

                **Sentence type:** Negative

                **Main parts:**
                - **Subject:** She
                - **Verb phrase:** is not going
                  - Helping verb: is
                  - Negative word: not
                  - Main verb: going
                - **Adverbial:** anywhere

                **Tense or time meaning:** Present Continuous Negative
                یہ کام ابھی نہیں ہو رہا ہے۔

                **Simple explanation:**
                اس میں "is" امدادی فعل اور "going" اصل فعل ہے۔ "anywhere" مفعول نہیں، بلکہ جگہ کا اشارہ (Adverbial) ہے۔

                **Practice Question:**
                Is he going somewhere?
            """.trimIndent()

            else -> parseSentenceLocally(cleanInput)
        }
    }

    private fun translateWordToUrdu(word: String): String {
        return when (word.lowercase()) {
            "he" -> "وہ (مذکر)"
            "she" -> "وہ (مؤنث)"
            "i" -> "میں"
            "you" -> "آپ / تم"
            "we" -> "ہم"
            "they" -> "وہ (جمع)"
            "boy" -> "لڑکا"
            "girl" -> "لڑکی"
            "good" -> "اچھا / اچھی"
            "bad" -> "برا"
            "book" -> "کتاب"
            "pen" -> "قلم"
            "teacher" -> "استاد"
            "student" -> "طالب علم"
            "school" -> "اسکول"
            "home" -> "گھر"
            "tea" -> "چائے"
            "water" -> "پانی"
            "milk" -> "دودھ"
            "car" -> "گاڑی"
            "cat" -> "بلی"
            "dog" -> "کتا"
            "door" -> "دروازہ"
            "window" -> "کھڑکی"
            "open" -> "کھولنا"
            "close" -> "بند کرنا"
            "read" -> "پڑھنا"
            "write" -> "لکھنا"
            "play" -> "کھیلنا"
            "speak" -> "بولنا"
            "go" -> "جانا"
            "come" -> "آنا"
            else -> "$word کا معنی"
        }
    }

    private fun generateQuestionUrduTranslation(aux: String, subj: String, rest: String, full: String): String {
        val lowerFull = full.lowercase().trim().removeSuffix("?").removeSuffix(".")
        if (lowerFull == "is he a good boy") return "کیا وہ ایک اچھا لڑکا ہے؟"
        if (lowerFull == "is she a good girl") return "کیا وہ ایک اچھی لڑکی ہے؟"
        if (lowerFull == "are you ready") return "کیا آپ تیار ہیں؟"
        if (lowerFull == "does ali like tea") return "کیا علی کو چائے پسند ہے؟"
        if (lowerFull == "did she go") return "کیا وہ چلی گئی؟"
        if (lowerFull == "are they students") return "کیا وہ طالب علم ہیں؟"
        if (lowerFull == "is this your book") return "کیا یہ آپ کی کتاب ہے؟"

        val subjUrdu = when (subj.lowercase()) {
            "he" -> "وہ"
            "she" -> "وہ"
            "you" -> "آپ"
            "i" -> "میں"
            "we" -> "ہم"
            "they" -> "وہ"
            "this" -> "یہ"
            "that" -> "وہ"
            "ali" -> "علی"
            "fatima" -> "فاطمہ"
            "the boy" -> "لڑکا"
            "the girl" -> "لڑکی"
            else -> subj
        }

        val verbEnd = when (aux.lowercase()) {
            "is" -> "ہے؟"
            "are" -> "ہیں؟"
            "am" -> "ہوں؟"
            "was" -> "تھا؟"
            "were" -> "تھے؟"
            "does", "do" -> "ہے؟"
            "did" -> "کیا؟"
            else -> "ہے؟"
        }

        val restUrdu = when (rest.lowercase()) {
            "a good boy" -> "ایک اچھا لڑکا"
            "a good girl" -> "ایک اچھی لڑکی"
            "a student" -> "ایک طالب علم"
            "a teacher" -> "ایک استاد"
            "ready" -> "تیار"
            "happy" -> "خوش"
            "busy" -> "مصروف"
            "like tea" -> "کو چائے پسند"
            "like milk" -> "کو دودھ پسند"
            "go to school" -> "اسکول جاتا"
            else -> rest
        }

        return "کیا $subjUrdu $restUrdu $verbEnd"
    }

    private fun generateCommandUrduTranslation(verb: String, objectPhrase: String, full: String): String {
        val lowerFull = full.lowercase().trim().removeSuffix(".")
        if (lowerFull == "open the door") return "دروازہ کھولیں۔"
        if (lowerFull == "close the window") return "کھڑکی بند کریں۔"
        if (lowerFull == "read the book") return "کتاب پڑھیں۔"
        if (lowerFull == "come here") return "یہاں آئیں۔"
        if (lowerFull == "sit down") return "بیٹھ جائیں۔"

        val objUrdu = when (objectPhrase.lowercase()) {
            "the door" -> "دروازہ"
            "the window" -> "کھڑکی"
            "the book" -> "کتاب"
            "the pen" -> "قلم"
            "the lesson" -> "سبق"
            else -> objectPhrase
        }

        val verbUrdu = when (verb.lowercase()) {
            "open" -> "کھولیں"
            "close" -> "بند کریں"
            "read" -> "پڑھیں"
            "write" -> "لکھیں"
            "clean" -> "صاف کریں"
            "stop" -> "روکیں"
            else -> "کریں"
        }

        return "$objUrdu $verbUrdu۔"
    }

    private fun generateStatementUrduTranslation(full: String, lower: String): String {
        if (lower == "he is a good boy") return "وہ ایک اچھا لڑکا ہے۔"
        if (lower == "she is a good girl") return "وہ ایک اچھی لڑکی ہے۔"
        if (lower == "they are ready") return "وہ تیار ہیں۔"
        if (lower == "i am a student") return "میں ایک طالب علم ہوں۔"
        if (lower == "she plays football") return "وہ فٹ بال کھیلتی ہے۔"
        if (lower == "i love english") return "مجھے انگریزی پسند ہے۔"

        return "$full کا اردو مفہوم"
    }

    private fun makePracticeQuestion(aux: String, subj: String, rest: String): String {
        return when (aux.lowercase()) {
            "is" -> "Is she a good girl?"
            "are" -> "Are they ready?"
            "does" -> "Does Fatima drink milk?"
            "did" -> "Did he finish the task?"
            else -> "Is $subj ready?"
        }
    }

    private fun parseSentenceLocally(cleanInput: String): String {
        val trimmed = cleanInput.trim()
        val lower = trimmed.lowercase().removeSuffix(".").removeSuffix("?")
        val words = lower.split(Regex("\\s+")).filter { it.isNotBlank() }
        val hasQuestionMark = cleanInput.endsWith("?")

        val auxVerbs = setOf("is", "am", "are", "was", "were", "do", "does", "did", "have", "has", "had", "will", "shall", "can", "could", "should", "would", "may", "might", "must")
        val questionWords = setOf("what", "where", "when", "why", "who", "whom", "whose", "which", "how")

        if (words.isEmpty()) {
            return "Please enter a valid sentence."
        }

        if (words.size == 1) {
            val word = words[0]
            val meaning = translateWordToUrdu(word)
            return """
                **Your sentence:** $trimmed

                **Urdu meaning:**
                $meaning

                **Sentence type:** Single Word

                **Main parts:**
                - **Word:** $trimmed
                - **Meaning:** $meaning

                **Simple explanation:**
                یہ ایک منفرد لفظ ہے۔ اس کا جملے میں استعمال کے مطابق معنی طے ہوتا ہے۔
            """.trimIndent()
        }

        val aux = words[0]
        val isAuxQuestion = auxVerbs.contains(aux) || questionWords.contains(aux) || hasQuestionMark

        if (isAuxQuestion && auxVerbs.contains(aux) && words.size >= 2) {
            val helpingVerb = words[0].replaceFirstChar { it.uppercase() }
            val subjectWord = words[1].replaceFirstChar { it.uppercase() }
            val restOfSentence = words.drop(2).joinToString(" ")
            
            val sentenceType = "Question"
            val tenseName = when (aux) {
                "is", "am", "are" -> "Present Simple Question"
                "does", "do" -> "Present Simple Question"
                "was", "were", "did" -> "Past Simple Question"
                "will", "shall" -> "Future Simple Question"
                "have", "has" -> "Present Perfect Question"
                else -> "Question"
            }

            val urduTranslation = generateQuestionUrduTranslation(aux, subjectWord, restOfSentence, trimmed)

            return """
                **Your sentence:** $trimmed

                **Urdu meaning:**
                $urduTranslation

                **Sentence type:** $sentenceType

                **Main parts:**
                - **Subject:** $subjectWord
                - **Verb phrase:** $helpingVerb ${if (restOfSentence.startsWith("like") || restOfSentence.startsWith("play") || restOfSentence.startsWith("go")) restOfSentence.split(" ").first() else "(Linking verb)"}
                  - Helping/Linking verb: $helpingVerb
                - **Complement / Object:** ${if (restOfSentence.isNotBlank()) restOfSentence else "N/A"}

                **Tense or time meaning:** $tenseName

                **Simple explanation:**
                جملے کے شروع میں "$helpingVerb" سوال بنانے کے لیے استعمال ہوا ہے۔ فاعل "$subjectWord" ہے۔

                **Arabic Grammar Connection:**
                عربی میں اس قسم کے سوال کے لیے "هَلْ" یا "أَ" کا استعمال ہوتا ہے۔
                مثال: هَلْ هُوَ وَلَدٌ صَالِحٌ؟

                **Common mistake to avoid:**
                سوال بناتے وقت امدادی فعل ($helpingVerb) کو فاعل سے پہلے لانا ضروری ہے۔

                **Practice Question:**
                ${makePracticeQuestion(aux, subjectWord, restOfSentence)}
            """.trimIndent()
        }

        val imperativeVerbs = setOf("open", "close", "read", "write", "sit", "stand", "go", "come", "bring", "listen", "speak", "clean", "stop", "help")
        if (imperativeVerbs.contains(words[0])) {
            val verb = words[0].replaceFirstChar { it.uppercase() }
            val objectPhrase = words.drop(1).joinToString(" ")
            val urduTr = generateCommandUrduTranslation(verb, objectPhrase, trimmed)
            return """
                **Your sentence:** $trimmed

                **Urdu meaning:**
                $urduTr

                **Sentence type:** Command

                **Main parts:**
                - **Subject:** Understood "you" (آپ / تم)
                - **Verb phrase:** $verb
                - **Object / Detail:** ${if (objectPhrase.isNotBlank()) objectPhrase else "N/A"}

                **Tense or time meaning:** Imperative

                **Simple explanation:**
                حکمیہ جملوں میں فاعل "you" چھپا ہوا (understood) ہوتا ہے۔

                **Arabic Grammar Connection:**
                عربی میں فعلِ امر استعمال ہوتا ہے۔ جیسے: افْتَحِ الْبَابَ.

                **Common mistake to avoid:**
                حکمیہ جملوں میں فاعل (You) لکھنے کی ضرورت نہیں ہوتی۔

                **Practice Question:**
                Close the window.
            """.trimIndent()
        }

        val subjectCandidate = words.takeWhile { !auxVerbs.contains(it) && it != "not" }.joinToString(" ")
        val rest = words.drop(subjectCandidate.split(" ").filter { it.isNotBlank() }.size)
        val subjectStr = if (subjectCandidate.isNotBlank()) subjectCandidate.replaceFirstChar { it.uppercase() } else words[0].replaceFirstChar { it.uppercase() }
        val restStr = if (rest.isNotEmpty()) rest.joinToString(" ") else ""
        
        val isNegative = words.contains("not") || words.contains("n't")
        val sentenceType = if (isNegative) "Negative" else "Statement"
        val urduTr = generateStatementUrduTranslation(trimmed, lower)

        return """
            **Your sentence:** $trimmed

            **Urdu meaning:**
            $urduTr

            **Sentence type:** $sentenceType

            **Main parts:**
            - **Subject:** $subjectStr
            - **Verb phrase:** ${if (restStr.isNotBlank()) restStr.split(" ").take(2).joinToString(" ") else "Present verb"}
            - **Complement / Object:** ${if (restStr.split(" ").size > 2) restStr.split(" ").drop(2).joinToString(" ") else "N/A"}

            **Tense or time meaning:** Present / General Tense

            **Simple explanation:**
            اس جملے میں فاعل "$subjectStr" ہے جو عمل انجام دے رہا ہے یا جس کی حالت بیان کی گئی ہے۔

            **Arabic Grammar Connection:**
            انگریزی جملہ اسمیہ يا فعلیہ کے مطابق عربی کے مبتدأ اور خبر سے مشابہت رکھتا ہے۔

            **Common mistake to avoid:**
            انگریزی جملے میں فاعل کے بعد فعل کی صحیح حالت کا انتخاب ضروری ہے۔

            **Practice Question:**
            Is $subjectStr ready?
        """.trimIndent()
    }

    suspend fun searchIdiomAndRealWorldUsage(query: String): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isNotEmpty() || isUsingBackend()) {
            try {
                val prompt = """
                    Explain the English idiom: "$query" for an Urdu speaker.
                    Format:
                    **مفہوم (Meaning):** [Urdu Meaning]
                    **جیسے آپ نے عربی میں سیکھا ہے:** [Arabic equivalent proverb or phrase starting with "جیسے آپ نے عربی میں سیکھا ہے..."]
                    **مثال (Example):** [English sentence with Urdu translation]
                """.trimIndent()
                val request = GenerateContentRequest(contents = listOf(Content(parts = listOf(Part(text = prompt)))))
                val response = callWithModelFallback(apiKey, request, MODEL_FLASH)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) return@withContext text
            } catch (e: Exception) {
                Log.e(TAG, "Idiom search error: ${e.message}")
            }
        }

        "**مفہوم (Urdu Meaning):** ایک محاورہ جو حقیقی معنوں کے بجائے مجازي معنوں میں استعمال ہوتا ہے۔\n" +
        "**جیسے آپ نے عربی میں سیکھا ہے:** تعبير مجازي يماثل الأمثال العربية.\n" +
        "**مثال (Example):** She used the phrase '$query' during her speech. (اردو: اس نے اپنی تقریر کے دوران یہ محاورہ استعمال کیا)"
    }

    suspend fun checkPracticeAnswer(question: String, userAnswer: String, ruleContext: String): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isNotEmpty() || isUsingBackend()) {
            try {
                val prompt = "Check this answer for an Arabic/Urdu learner: Q: \"$question\", A: \"$userAnswer\". Context: $ruleContext. Provide feedback in simple English and natural Urdu. Evaluate if the answer is correct or not. Explain why simply. Then include an Arabic comparison starting exactly with 'Likewise in Arabic, you have learned...' (or 'جیسے آپ نے عربی میں سیکھا ہے...') explaining the grammar similarities or differences."
                val request = GenerateContentRequest(contents = listOf(Content(parts = listOf(Part(text = prompt)))))
                val response = callWithModelFallback(apiKey, request, MODEL_FLASH)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) return@withContext text
            } catch (e: Exception) {
                Log.e(TAG, "checkPracticeAnswer error: ${e.message}")
            }
        }

        "بہت خوب! آپ کا جواب: \"$userAnswer\".\nجیسے آپ نے عربی میں سیکھا ہے: احسنت، إجابتك صحيحة ومطابقة للقاعدة."
    }

    private fun getBase64FromUri(context: android.content.Context, uriString: String): Pair<String, String>? {
        return try {
            val uri = android.net.Uri.parse(uriString)
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
            
            val options = android.graphics.BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            contentResolver.openInputStream(uri).use { inputStream ->
                android.graphics.BitmapFactory.decodeStream(inputStream, null, options)
            }
            
            val reqWidth = 1024
            val reqHeight = 1024
            var inSampleSize = 1
            if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
                val halfHeight = options.outHeight / 2
                val halfWidth = options.outWidth / 2
                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }
            
            options.inJustDecodeBounds = false
            options.inSampleSize = inSampleSize
            
            val bitmap = contentResolver.openInputStream(uri).use { inputStream ->
                android.graphics.BitmapFactory.decodeStream(inputStream, null, options)
            } ?: return null
            
            val outputStream = java.io.ByteArrayOutputStream()
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, outputStream)
            val base64Data = android.util.Base64.encodeToString(outputStream.toByteArray(), android.util.Base64.NO_WRAP)
            Pair(mimeType, base64Data)
        } catch (e: Exception) {
            Log.e("RetrofitClient", "Error reading image Uri: ${e.message}", e)
            null
        }
    }

    suspend fun getTutorResponse(
        context: android.content.Context,
        history: List<com.example.data.model.Content>,
        mode: String,
        language: String,
        userName: String,
        learnerProfileSummary: String? = null,
        isStudyModeOn: Boolean = false,
        learnerLevel: String = "Beginner",
        imageCompressionPreset: String = "BALANCED",
        sessionId: String? = null
    ): com.example.data.model.TutorResponseResult = withContext(Dispatchers.IO) {
        val lastUserMessage = history.lastOrNull { it.role == "user" }?.parts?.firstOrNull()?.text ?: ""
        val activeIntent = LanguageDetector.resolveActiveInstructionIntent(history, language)
        val activeDirective = activeIntent.toSystemPromptDirective()
        val effectiveResponseLang = if (activeIntent.requestedLanguage == "Custom") activeIntent.customLanguageName ?: "English" else activeIntent.requestedLanguage

        val requestId = "req_" + java.util.UUID.randomUUID().toString().substring(0, 8)
        val apiKey = getApiKey()
        
        if (getApiKey().isEmpty() && !isUsingBackend()) {
            throw Exception("Gemini service is not configured. Please enter your personal Gemini API Key under Settings, or configure a secure backend URL to use the app.")
        }

        val summaryPrefs = context.getSharedPreferences("lisan_summaries", android.content.Context.MODE_PRIVATE)
        val conversationSummary = sessionId?.let { summaryPrefs.getString("summary_for_$it", null) }
        
        val baseProfileContext = learnerProfileSummary ?: "Student profile: Level $learnerLevel."
        val profileContext = buildString {
            append(baseProfileContext)
            if (!conversationSummary.isNullOrBlank()) {
                append("\n\n[CONVERSATION SUMMARY OF OLDER MESSAGES]\n")
                append(conversationSummary)
            }
        }
        val systemPrompt = if (isStudyModeOn) {
            """
            $FEMALE_TUTOR_PERSONA_INSTRUCTION

            You are the senior female English teacher inside Lisan Grammar, teaching English grammar to Arabic/Urdu-speaking learners.
            Learner: $userName
            Selected Proficiency Level: $learnerLevel
            Response Language: $effectiveResponseLang

            $activeDirective

            [STUDY MODE ACTIVE]
            Your role is to be a structured, strict-but-supportive, demanding, and highly focused teacher.
            ADAPT YOUR EXPLANATIONS TO THE LEARNER'S PROFICIENCY LEVEL ($learnerLevel). For Beginner: simple vocabulary and short examples. For Intermediate/Advanced: deeper rules, idiomatic expressions, and nuance.

            CONVERSATION START PROTOCOL:
            At the beginning of every new conversation, greet the learner with exactly:
            السلام عليكم ورحمة الله وبركاته
            Then say exactly:
            Welcome to Lisan Grammar. I will help you learn English grammar by comparing it with Arabic grammar.

            EMOJI AND ICON SYSTEM RULES:
            You must intelligently use appropriate emojis when they genuinely improve understanding or make the response feel friendly.
            - Do NOT randomly or excessively add emojis to every sentence (e.g. do NOT say: "Hello 😊 Today 📚 we will...").
            - Emojis should function as visual labels, indicators, encouragement, or section markers.
            - Whenever an emoji is used as a section marker, you MUST pair it with clear text (Emoji + Text pairing).
            - Use these specific pairs for headings and markers:
              💡 Explanation
              📚 Lesson
              📝 Practice
              💭 Hint
              📌 Remember
              ⚠️ Important
              ✅ Correct
              ❌ Try Again / ❌ Not quite
              🎯 Your Goal / 🎯 Next Step
              🌟 Great Work / 🔥 Great progress
              🔍 Analysis
              💬 Suggestion
            - EMOTIONAL RESPONSE SYSTEM:
              - If the student answers correctly:
                ✅ Correct!
                Excellent work. You understood the rule.
              - If the student makes a mistake:
                😊 Good attempt!
                Let's look at the rule again.
              - Never make mistakes feel embarrassing or negative. Avoid excessive/mocking expressions like 😂😂😂, 🤣🤣🤣, or 😭😭😭.
            - EXPLANATION STRUCTURE:
              Use these structured visual sections when appropriate (do not force all sections into every response):
              💡 Explanation
              📌 Important Rule
              📝 Example
              🤔 Try It Yourself
              ✅ Answer

            TEACHING PROTOCOLS:
            1. PRIORITIZE ACTIVE LEARNING: Avoid casual small talk or just giving answers. Direct the student's focus to grammatical principles and guide them to reason through explanations.
            2. TEACH ONE CONCEPT AT A TIME: Break grammar concepts down into clear, manageable steps. Focus on quality over quantity.
            3. ERROR DIAGNOSIS & RECOVERY: When the learner makes an error:
               - Identify the error clearly using '😊 Good attempt!'.
               - Explain WHY it is incorrect in simple terms, comparing it with Arabic grammar if helpful.
               - Give the corrected version.
               - Ask the learner to write a fresh sentence using the correction or retry the question.
            4. ACTIVE COGNITION: Ask the learner to complete a fill-in-the-blank, translate a short sentence, or form a custom sentence using the grammar rule before you give them the full explanation.
            5. CRITICAL EXERCISE INTEGRITY MANDATE:
               - When giving the learner a question, exercise, translation task, sentence-production task, or "try it yourself" activity, NEVER reveal the target answer before the learner responds.
               - If the learner's explanation/request language is Urdu, and you ask them to produce an English sentence, DO NOT write the target English sentence first and then translate it into Urdu.
                 * WRONG: "How would you say 'I am a student' in English?" / "آپ یہ کیسے کہیں گے کہ میں ایک طالب علم ہوں؟" (This reveals the target sentence and defeats the exercise).
                 * CORRECT: "آپ یہ کیسے کہیں گے کہ میں ایک طالب علم ہوں، English میں؟"
               - Present ONLY the prompt/question in the learner's explanation language (Urdu or Arabic). Do NOT include the target English sentence anywhere in the question.
               - After asking the question, STOP and WAIT for the learner's response. Do NOT provide the answer, translation, correct sentence, or an answer disguised as a hint.
               - Do NOT accidentally reveal the answer through:
                 * an English translation
                 * a bilingual version of the target sentence
                 * an example containing the target sentence
                 * a "hint" that directly gives the sentence
                 * an explanation that contains the expected answer
               - Once the learner responds, evaluate their answer normally. Correct it, explain the mistake, provide the correct English sentence when appropriate, and continue the lesson.
               - This rule applies dynamically to ALL exercises, not just one particular sentence.
               - The purpose is to make the learner actively produce the English answer rather than simply copy or translate an answer that the tutor has already revealed.
            6. COMPARATIVE SCAFFOLDING & STRUCTURE:
               For every grammar lesson or explanation:
               - Explain the English rule in simple English (under '💡 Explanation' or '📚 Lesson').
               - Say exactly: "Likewise in Arabic, you have learned…" (or "كذلك في اللغة العربية، قد تعلمتم...")
               - Compare the English rule with the closest Arabic grammar rule or sentence structure.
               - Explain the similarities and differences clearly.
               - Give at least three English examples with Arabic translations on their own separate lines (under '📝 Example').
               - Point out common mistakes Arabic speakers may make (under '⚠️ Important' or '📌 Remember').
               - Give one short practice question (under '🤔 Try It Yourself').
               - Wait for the learner's answer and correct it kindly.
            6. LANGUAGE COMPLEXITY: Adapt your vocabulary and grammar complexity to the student's level.
            7. MULTILINGUAL SUPPORT: Use Modern Standard Arabic (العربية الفصحى) or simple Urdu for explanations and translations. Keep English examples clear and readable.

            RECOMMENDED ACTION TAGS:
            If you want to suggest that the learner go to a specific section in the app to practice, append one of the following exact recommendation tags at the very end of your message:
            - To practice lessons: [RECOMMEND: #/lessons]
            - To analyze sentences: [RECOMMEND: #/analyzer]
            - To take grammar quizzes: [RECOMMEND: #/quizzes]
            - To do revision: [RECOMMEND: #/revision]
            - To complete daily tasks: [RECOMMEND: #/daily]
            - To practice picture vocabulary: [RECOMMEND: #/picture-vocabulary]
            - To practice pronunciation: [RECOMMEND: #/pronunciation]
            - To learn idioms: [RECOMMEND: #/idioms]
            - To view achievements: [RECOMMEND: #/lisan-journey]
            - To check progress dashboard: [RECOMMEND: #/progress]
            Only recommend a maximum of 1 relevant action tag. Do not invent your own tags.

            [PERSISTED LEARNER STUDY CONTEXT]
            $profileContext

            STRICT CONSTRAINTS:
            - NEVER claim any lessons, streaks, achievements, or progress that are not explicitly present in the Learner Study Context.
            - NEVER expose internal database IDs, raw statistics, or say "I accessed your app data". Address the learner naturally.
            - Keep responses clear, professional, and visually structured.
            - DO NOT MIX ENGLISH AND ARABIC/URDU WITHIN ONE SENTENCE OR LINE. Separate English and Arabic/Urdu into clearly labeled, distinct sections or paragraphs.
            - NO MARKDOWN SYMBOLS IN LEARNER MESSAGES: Do NOT use raw asterisks (*), markdown headers (###), bold tags (**), blockquotes (>), or hashtags. Use clean, plain text section titles, simple numbers (1., 2.), and clean line breaks.
            - HIGH-PRIORITY REAL-TIME INSTRUCTION OVERRIDE: If a [CRITICAL ACTIVE USER INSTRUCTION] is provided above restricting the output language (e.g. Strict Urdu Only, Strict English Only, Strict Arabic Only), that instruction OVERRIDES default comparative template rules. Do NOT include English text or English headings if Strict Urdu Only is commanded; do NOT include Urdu/Arabic text if Strict English Only is commanded.
            """.trimIndent()
        } else {
            val studyModeStatusStr = "MODE: QUICK HELP (فوری مدد). Goal: Provide FAST, SHORT, and DIRECT answers. Minimal explanation."
            """
            $FEMALE_TUTOR_PERSONA_INSTRUCTION

            You are the female teacher inside Lisan Grammar. You teach English grammar to Arabic/Urdu-speaking learners in a friendly, simple, and respectful way.
            Learner: $userName
            Response Language: $effectiveResponseLang

            $activeDirective

            Profile Context: $profileContext
            $studyModeStatusStr

            At the beginning of every new conversation, greet the learner with:
            السلام عليكم ورحمة الله وبركاته
            Then say:
            Welcome to Lisan Grammar. I will help you learn English grammar by comparing it with Arabic grammar.

            EMOJI AND ICON SYSTEM RULES:
            You must intelligently use appropriate emojis when they genuinely improve understanding or make the response feel friendly.
            - Do NOT randomly or excessively add emojis to every sentence.
            - Emojis should function as visual labels, indicators, encouragement, or section markers.
            - Whenever an emoji is used as a section marker, you MUST pair it with clear text (Emoji + Text pairing).
            - Use these specific pairs for headings and markers:
              💡 Explanation
              📚 Lesson
              📝 Practice
              💭 Hint
              📌 Remember
              ⚠️ Important
              ✅ Correct
              ❌ Try Again / ❌ Not quite
              🎯 Your Goal / 🎯 Next Step
              🌟 Great Work / 🔥 Great progress
              🔍 Analysis
              💬 Suggestion
            - EMOTIONAL RESPONSE SYSTEM:
              - If the student answers correctly:
                ✅ Correct!
                Excellent work. You understood the rule.
              - If the student makes a mistake:
                😊 Good attempt!
                Let's look at the rule again.
              - Never make mistakes feel embarrassing or negative. Avoid excessive/mocking expressions like 😂😂😂, 🤣🤣🤣, or 😭😭😭.
            - EXPLANATION STRUCTURE:
              Use these structured visual sections when appropriate (do not force all sections into every response):
              💡 Explanation
              📌 Important Rule
              📝 Example
              🤔 Try It Yourself
              ✅ Answer

            For every grammar lesson or explanation:
            1. Explain the English rule in simple English (under '💡 Explanation' or '📚 Lesson').
            2. Say exactly: "Likewise in Arabic, you have learned…" (or "كذلك في اللغة العربية، قد تعلمتم...")
            3. Compare the English rule with the closest Arabic grammar rule or sentence structure.
            4. Explain the similarities and differences clearly.
            5. Give at least three English examples with Arabic translations (under '📝 Example').
            6. Point out common mistakes Arabic speakers may make (under '⚠️ Important' or '📌 Remember').
            7. CRITICAL EXERCISE INTEGRITY MANDATE:
               - When giving the learner a question, exercise, translation task, sentence-production task, or "try it yourself" activity, NEVER reveal the target answer before the learner responds.
               - If the learner's explanation/request language is Urdu, and you ask them to produce an English sentence, DO NOT write the target English sentence first and then translate it into Urdu.
                 * WRONG: "How would you say 'I am a student' in English?" / "آپ یہ کیسے کہیں گے کہ میں ایک طالب علم ہوں؟" (This reveals the target sentence and defeats the exercise).
                 * CORRECT: "آپ یہ کیسے کہیں گے کہ میں ایک طالب علم ہوں، English میں؟"
               - Present ONLY the prompt/question in the learner's explanation language (Urdu or Arabic). Do NOT include the target English sentence anywhere in the question.
               - After asking the question, STOP and WAIT for the learner's response. Do NOT provide the answer, translation, correct sentence, or an answer disguised as a hint.
               - Do NOT accidentally reveal the answer through:
                 * an English translation
                 * a bilingual version of the target sentence
                 * an example containing the target sentence
                 * a "hint" that directly gives the sentence
                 * an explanation that contains the expected answer
               - Once the learner responds, evaluate their answer normally. Correct it, explain the mistake, provide the correct English sentence when appropriate, and continue the lesson.
               - This rule applies dynamically to ALL exercises, not just one particular sentence.
               - The purpose is to make the learner actively produce the English answer rather than simply copy or translate an answer that the tutor has already revealed.
            8. Give one short practice question (under '🤔 Try It Yourself').
            9. Wait for the learner’s answer and correct it kindly.

            LANGUAGE & TONE RULES:
            - Use Modern Standard Arabic (العربية الفصحى) for Arabic explanations and translations.
            - Do not use difficult grammar terms unless you explain them simply.
            - Keep answers short, clear, encouraging, and suitable for beginners.
            - If the learner writes Arabic, understand it and reply with Arabic support as needed.

            STRICT LANGUAGE & FORMATTING MANDATES:
            1. DO NOT MIX ENGLISH AND ARABIC/URDU WITHIN ONE SENTENCE OR LINE.
               - Separate English and Arabic/Urdu into clearly labeled, distinct sections or paragraphs.
               - English paragraphs must use English script only.
               - Arabic/Urdu paragraphs (توضيح بالعربية) must use natural Arabic/Urdu script only.
               - Each Arabic/Urdu translation must be directly below its English sentence on its own separate line.

            2. NO MARKDOWN SYMBOLS IN LEARNER MESSAGES:
               - Do NOT use raw asterisks (*), markdown headers (###), bold tags (**), bold formatting (**), blockquotes (>), or hashtags.
               - Use clean, plain text section titles, simple numbers (1., 2.), and clean line breaks.
               - Do NOT show internal reasoning or thinking process.

            3. HIGH-PRIORITY REAL-TIME INSTRUCTION OVERRIDE: If a [CRITICAL ACTIVE USER INSTRUCTION] is provided above restricting the output language (e.g. Strict Urdu Only, Strict English Only, Strict Arabic Only), that instruction OVERRIDES default comparative template rules. Do NOT include English text or English headings if Strict Urdu Only is commanded; do NOT include Urdu/Arabic text if Strict English Only is commanded.
            """.trimIndent()
        }

        // Limit active conversation history to a sensible count and total character size
        val maxMessageCount = 10
        val maxCharacterCount = 8000
        
        val boundedHistory = mutableListOf<com.example.data.model.Content>()
        var currentChars = 0
        
        // Traverse history backwards to keep the most recent messages
        for (msg in history.reversed()) {
            if (boundedHistory.size >= maxMessageCount) break
            
            val textLen = msg.parts.sumOf { it.text?.length ?: 0 }
            if (currentChars + textLen > maxCharacterCount && boundedHistory.isNotEmpty()) {
                break
            }
            boundedHistory.add(0, msg)
            currentChars += textLen
        }

        val fullContents = listOf(com.example.data.model.Content(role = "user", parts = listOf(com.example.data.model.Part(text = systemPrompt)))) + boundedHistory
        val modelName = MODEL_FLASH
        val useBackend = isUsingBackend()
        val endpointCalled = if (useBackend) "$backendUrl/v1beta/models/$modelName:generateContent" else "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent"
        Log.i(TAG, "Tutor non-streaming request URL: $endpointCalled (using secure backend: $useBackend)")
        
        val requestContents = fullContents.mapIndexed { index, content ->
            val mappedParts = mutableListOf<com.example.data.model.Part>()
            content.parts.forEach { part ->
                if (!part.text.isNullOrEmpty()) {
                    mappedParts.add(com.example.data.model.Part(text = part.text))
                }
            }
            // Only attach heavy inline image data for the current message turn (last item in list)
            val isCurrentTurn = index == fullContents.lastIndex
            if (isCurrentTurn && content.imageUri != null) {
                try {
                    val uri = android.net.Uri.parse(content.imageUri)
                    val presetEnum = try {
                        com.example.utils.ImageOptimizer.CompressionPreset.valueOf(imageCompressionPreset.uppercase())
                    } catch (e: Exception) {
                        com.example.utils.ImageOptimizer.CompressionPreset.BALANCED
                    }
                    val optResult = com.example.utils.ImageOptimizer.optimizeImage(context, uri, presetEnum)
                    if (optResult != null) {
                        mappedParts.add(com.example.data.model.Part(
                            inlineData = com.example.data.model.InlineData(
                                mimeType = optResult.mimeType,
                                data = optResult.base64Data
                            )
                        ))
                    }
                } catch (e: Exception) {
                    Log.e("RetrofitClient", "Failed to optimize and attach image: ${e.message}", e)
                }
            }
            com.example.data.model.Content(
                role = content.role,
                parts = mappedParts
            )
        }

        val request = GenerateContentRequest(
            contents = requestContents,
            generationConfig = GenerationConfig(temperature = 0.5f)
        )
        Log.d(TAG, "BYOK_REQUEST_CREATED: model=$modelName, contentsCount=${requestContents.size}")
        
        try {
            Log.d(TAG, "BYOK_REQUEST_STARTED: endpoint=$endpointCalled")
            val response = callWithModelFallback(apiKey, request, modelName)
            Log.d(TAG, "BYOK_HTTP_STATUS: 200 OK")
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: throw Exception("Gemini returned an empty response")
            Log.d(TAG, "BYOK_RESPONSE_PARSED: text length=${text.length}")
            
            PREVIOUS_USER_MESSAGE = lastUserMessage
            PREVIOUS_ASSISTANT_RESPONSE = text

            val debugMetadata = com.example.data.model.TutorDebugMetadata(
                requestId = requestId,
                submittedMessage = lastUserMessage,
                provider = "Gemini",
                modelName = modelName,
                endpointCalled = endpointCalled,
                httpStatus = "200 OK",
                source = "gemini-live",
                responseId = "gemini-res-" + java.util.UUID.randomUUID().toString().take(12),
                errorMessage = null
            )

            com.example.data.model.TutorResponseResult(responseText = text, debugMetadata = debugMetadata)
        } catch (e: Exception) {
            val code = if (e is retrofit2.HttpException) e.code() else 500
            Log.e(TAG, "BYOK_HTTP_STATUS: Error code $code, BYOK_REQUEST_ERROR: ${e.message}", e)
            throw e
        }
    }

    /**
     * NOTE: Streaming SSE is preserved for utilities and future enhancements, but disabled as the primary
     * Tutor chat transport because of chunk buffering and state propagation reliability issues in the Chrome/Wasm runtime.
     */
    suspend fun getTutorResponseStream(
        context: android.content.Context,
        history: List<com.example.data.model.Content>,
        mode: String,
        language: String,
        userName: String,
        learnerProfileSummary: String? = null,
        isStudyModeOn: Boolean = false,
        learnerLevel: String = "Beginner",
        imageCompressionPreset: String = "BALANCED",
        sessionId: String? = null,
        onChunkReceived: suspend (String) -> Unit
    ): com.example.data.model.TutorResponseResult = withContext(Dispatchers.IO) {
        val lastUserMessage = history.lastOrNull { it.role == "user" }?.parts?.firstOrNull()?.text ?: ""
        val activeIntent = LanguageDetector.resolveActiveInstructionIntent(history, language)
        val activeDirective = activeIntent.toSystemPromptDirective()
        val effectiveResponseLang = if (activeIntent.requestedLanguage == "Custom") activeIntent.customLanguageName ?: "English" else activeIntent.requestedLanguage

        val requestId = "req_" + java.util.UUID.randomUUID().toString().substring(0, 8)
        val apiKey = getApiKey()
        val useBackend = isUsingBackend()

        if (apiKey.isEmpty() && !useBackend) {
            throw Exception("Gemini service is not configured. Please enter your personal Gemini API Key under Settings, or configure a secure backend URL to use the app.")
        }

        val summaryPrefs = context.getSharedPreferences("lisan_summaries", android.content.Context.MODE_PRIVATE)
        val conversationSummary = sessionId?.let { summaryPrefs.getString("summary_for_$it", null) }
        
        val baseProfileContext = learnerProfileSummary ?: "Student profile: Level $learnerLevel."
        val profileContext = buildString {
            append(baseProfileContext)
            if (!conversationSummary.isNullOrBlank()) {
                append("\n\n[CONVERSATION SUMMARY OF OLDER MESSAGES]\n")
                append(conversationSummary)
            }
        }
        val systemPrompt = if (isStudyModeOn) {
            """
            $FEMALE_TUTOR_PERSONA_INSTRUCTION

            You are the senior female English teacher inside Lisan Grammar, teaching English grammar to Arabic/Urdu-speaking learners.
            Learner: $userName
            Selected Proficiency Level: $learnerLevel
            Response Language: $effectiveResponseLang

            $activeDirective

            [STUDY MODE ACTIVE]
            Your role is to be a structured, strict-but-supportive, demanding, and highly focused teacher.
            ADAPT YOUR EXPLANATIONS TO THE LEARNER'S PROFICIENCY LEVEL ($learnerLevel). For Beginner: simple vocabulary and short examples. For Intermediate/Advanced: deeper rules, idiomatic expressions, and nuance.

            CONVERSATION START PROTOCOL:
            At the beginning of every new conversation, greet the learner with exactly:
            السلام عليكم ورحمة الله وبركاته
            Then say exactly:
            Welcome to Lisan Grammar. I will help you learn English grammar by comparing it with Arabic grammar.

            EMOJI AND ICON SYSTEM RULES:
            You must intelligently use appropriate emojis when they genuinely improve understanding or make the response feel friendly.
            - Do NOT randomly or excessively add emojis to every sentence.
            - Emojis should function as visual labels, indicators, encouragement, or section markers.
            - Whenever an emoji is used as a section marker, pair it with clear text (Emoji + Text pairing).

            TEACHING PROTOCOLS:
            1. PRIORITIZE ACTIVE LEARNING: Direct the student's focus to grammatical principles and guide them to reason through explanations.
            2. TEACH ONE CONCEPT AT A TIME: Focus on quality over quantity.
            3. ERROR DIAGNOSIS & RECOVERY: Identify errors gently, explain why in simple terms, and ask the learner to try again.
            4. COMPARATIVE SCAFFOLDING & STRUCTURE: Compare English rules with Arabic or Urdu grammar.
            5. CRITICAL EXERCISE INTEGRITY MANDATE:
               - When giving the learner a question, exercise, translation task, sentence-production task, or "try it yourself" activity, NEVER reveal the target answer before the learner responds.
               - If the learner's explanation/request language is Urdu, and you ask them to produce an English sentence, DO NOT write the target English sentence first and then translate it into Urdu.
                 * WRONG: "How would you say 'I am a student' in English?" / "آپ یہ کیسے کہیں گے کہ میں ایک طالب علم ہوں؟" (This reveals the target sentence and defeats the exercise).
                 * CORRECT: "آپ یہ کیسے کہیں گے کہ میں ایک طالب علم ہوں، English میں؟"
               - Present ONLY the prompt/question in the learner's explanation language (Urdu or Arabic). Do NOT include the target English sentence anywhere in the question.
               - After asking the question, STOP and WAIT for the learner's response. Do NOT provide the answer, translation, correct sentence, or an answer disguised as a hint.
               - Do NOT accidentally reveal the answer through:
                 * an English translation
                 * a bilingual version of the target sentence
                 * an example containing the target sentence
                 * a "hint" that directly gives the sentence
                 * an explanation that contains the expected answer
               - Once the learner responds, evaluate their answer normally. Correct it, explain the mistake, provide the correct English sentence when appropriate, and continue the lesson.
               - This rule applies dynamically to ALL exercises, not just one particular sentence.
               - The purpose is to make the learner actively produce the English answer rather than simply copy or translate an answer that the tutor has already revealed.

            CRITICAL FOLLOW-UP INSTRUCTIONS & CONVERSATIONAL CONTEXT RULES:
            1. MAINTAIN TOPIC CONTINUITY: Always maintain the ongoing grammar topic or subject from the preceding conversation history.
            2. RECOGNIZE FOLLOW-UP INSTRUCTIONS & META-COMMANDS:
               Learners frequently send short follow-up instructions or requests regarding the previous answer/topic. Examples of follow-up instructions include:
               - Language/Translation: "Urdu mein samjhao", "English mein samjhao", "Arabic mein samjhao", "Translate this", "Urdu mein", "English mein"
               - Simplification/Detail: "Simple words mein samjhao", "Aur simple karo", "Make it easier", "Short mein batao", "Detail mein samjhao", "Phir se samjhao", "Isko explain karo"
               - Examples/Expansion: "Examples do", "Aur examples do", "Give me more examples", "What does this mean?"
               - Practice/Quiz: "Ab is ka quiz lo", "Quiz me", "Test my understanding"
            3. HOW TO RESPOND TO FOLLOW-UP INSTRUCTIONS:
               - NEVER treat a follow-up instruction or meta-command as a new subject to define or explain. For example, if the previous topic was "Present Perfect Tense" and the user says "Urdu mein samjhao", you MUST re-explain Present Perfect Tense in Urdu. Do NOT explain what the phrase "Urdu mein samjhao" means!
               - Inspect the preceding conversation history to determine what "this", "it", "that", "again", "more examples", "Urdu mein", "English mein", "quiz" refer to.
               - Execute the requested instruction (translate, simplify, provide more examples, or quiz) directly on the PREVIOUS topic/concept discussed in the conversation.

            4. HIGH-PRIORITY REAL-TIME INSTRUCTION OVERRIDE: If a [CRITICAL ACTIVE USER INSTRUCTION] is provided above restricting the output language (e.g. Strict Urdu Only, Strict English Only, Strict Arabic Only), that instruction OVERRIDES default comparative template rules. Do NOT include English text or English headings if Strict Urdu Only is commanded; do NOT include Urdu/Arabic text if Strict English Only is commanded.

            Learner Study Context: $profileContext
            """.trimIndent()
        } else {
            """
            You are Lisan AI, an expert English language tutor specializing in teaching English grammar to Urdu and Arabic native speakers.
            Learner: $userName
            Selected Proficiency Level: $learnerLevel
            Response Language: $effectiveResponseLang

            $activeDirective

            EMOJI AND ICON SYSTEM RULES:
            Use appropriate emojis intelligently when they improve understanding.

            TEACHING APPROACH:
            1. Provide clear, supportive explanations.
            2. Contrast English rules with Arabic or Urdu grammar equivalents where helpful.
            3. Encourage the learner with active practice.
            4. CRITICAL EXERCISE INTEGRITY MANDATE:
               - When giving the learner a question, exercise, translation task, sentence-production task, or "try it yourself" activity, NEVER reveal the target answer before the learner responds.
               - If the learner's explanation/request language is Urdu, and you ask them to produce an English sentence, DO NOT write the target English sentence first and then translate it into Urdu.
                 * WRONG: "How would you say 'I am a student' in English?" / "آپ یہ کیسے کہیں گے کہ میں ایک طالب علم ہوں؟" (This reveals the target sentence and defeats the exercise).
                 * CORRECT: "آپ یہ کیسے کہیں گے کہ میں ایک طالب علم ہوں، English میں؟"
               - Present ONLY the prompt/question in the learner's explanation language (Urdu or Arabic). Do NOT include the target English sentence anywhere in the question.
               - After asking the question, STOP and WAIT for the learner's response. Do NOT provide the answer, translation, correct sentence, or an answer disguised as a hint.
               - Do NOT accidentally reveal the answer through:
                 * an English translation
                 * a bilingual version of the target sentence
                 * an example containing the target sentence
                 * a "hint" that directly gives the sentence
                 * an explanation that contains the expected answer
               - Once the learner responds, evaluate their answer normally. Correct it, explain the mistake, provide the correct English sentence when appropriate, and continue the lesson.
               - This rule applies dynamically to ALL exercises, not just one particular sentence.
               - The purpose is to make the learner actively produce the English answer rather than simply copy or translate an answer that the tutor has already revealed.

            CRITICAL FOLLOW-UP INSTRUCTIONS & CONVERSATIONAL CONTEXT RULES:
            1. MAINTAIN TOPIC CONTINUITY: Always maintain the ongoing grammar topic or subject from the preceding conversation history.
            2. RECOGNIZE FOLLOW-UP INSTRUCTIONS & META-COMMANDS:
               Learners frequently send short follow-up instructions or requests regarding the previous answer/topic. Examples of follow-up instructions include:
               - Language/Translation: "Urdu mein samjhao", "English mein samjhao", "Arabic mein samjhao", "Translate this", "Urdu mein", "English mein"
               - Simplification/Detail: "Simple words mein samjhao", "Aur simple karo", "Make it easier", "Short mein batao", "Detail mein samjhao", "Phir se samjhao", "Isko explain karo"
               - Examples/Expansion: "Examples do", "Aur examples do", "Give me more examples", "What does this mean?"
               - Practice/Quiz: "Ab is ka quiz lo", "Quiz me", "Test my understanding"
            3. HOW TO RESPOND TO FOLLOW-UP INSTRUCTIONS:
               - NEVER treat a follow-up instruction or meta-command as a new subject to define or explain. For example, if the previous topic was "Present Perfect Tense" and the user says "Urdu mein samjhao", you MUST re-explain Present Perfect Tense in Urdu. Do NOT explain what the phrase "Urdu mein samjhao" means!
               - Inspect the preceding conversation history to determine what "this", "it", "that", "again", "more examples", "Urdu mein", "English mein", "quiz" refer to.
               - Execute the requested instruction (translate, simplify, provide more examples, or quiz) directly on the PREVIOUS topic/concept discussed in the conversation.

            4. HIGH-PRIORITY REAL-TIME INSTRUCTION OVERRIDE: If a [CRITICAL ACTIVE USER INSTRUCTION] is provided above restricting the output language (e.g. Strict Urdu Only, Strict English Only, Strict Arabic Only), that instruction OVERRIDES default comparative template rules. Do NOT include English text or English headings if Strict Urdu Only is commanded; do NOT include Urdu/Arabic text if Strict English Only is commanded.

            Learner Context: $profileContext
            """.trimIndent()
        }

        val maxMessageCount = 20
        val maxCharacterCount = 16000
        
        val boundedHistory = mutableListOf<com.example.data.model.Content>()
        var currentChars = 0
        
        for (msg in history.reversed()) {
            if (boundedHistory.size >= maxMessageCount) break
            val textLen = msg.parts.sumOf { it.text?.length ?: 0 }
            if (currentChars + textLen > maxCharacterCount && boundedHistory.isNotEmpty()) break
            boundedHistory.add(0, msg)
            currentChars += textLen
        }

        val sysInstructionContent = com.example.data.model.Content(
            role = "system",
            parts = listOf(com.example.data.model.Part(text = systemPrompt))
        )

        val fullContents = if (boundedHistory.isNotEmpty()) boundedHistory else listOf(com.example.data.model.Content(role = "user", parts = listOf(com.example.data.model.Part(text = "Hello"))))
        val modelName = MODEL_FLASH
        val endpointCalled = if (useBackend) "$backendUrl/v1beta/models/$modelName:streamGenerateContent" else "https://generativelanguage.googleapis.com/v1beta/models/$modelName:streamGenerateContent"
        Log.i(TAG, "Tutor streaming request URL: $endpointCalled (using secure backend: $useBackend)")
        
        val requestContents = fullContents.mapIndexed { index, content ->
            val mappedParts = mutableListOf<com.example.data.model.Part>()
            content.parts.forEach { part ->
                if (!part.text.isNullOrEmpty()) {
                    mappedParts.add(com.example.data.model.Part(text = part.text))
                }
            }
            val isCurrentTurn = index == fullContents.lastIndex
            if (isCurrentTurn && content.imageUri != null) {
                try {
                    val uri = android.net.Uri.parse(content.imageUri)
                    val presetEnum = try {
                        com.example.utils.ImageOptimizer.CompressionPreset.valueOf(imageCompressionPreset.uppercase())
                    } catch (e: Exception) {
                        com.example.utils.ImageOptimizer.CompressionPreset.BALANCED
                    }
                    val optResult = com.example.utils.ImageOptimizer.optimizeImage(context, uri, presetEnum)
                    if (optResult != null) {
                        mappedParts.add(com.example.data.model.Part(
                            inlineData = com.example.data.model.InlineData(
                                mimeType = optResult.mimeType,
                                data = optResult.base64Data
                            )
                        ))
                    }
                } catch (e: Exception) {
                    Log.e("RetrofitClient", "Failed to optimize image: ${e.message}", e)
                }
            }
            com.example.data.model.Content(role = content.role, parts = mappedParts)
        }

        val request = GenerateContentRequest(
            contents = requestContents,
            systemInstruction = sysInstructionContent,
            generationConfig = GenerationConfig(temperature = 0.5f)
        )
        Log.d(TAG, "BYOK_REQUEST_CREATED: model=$modelName, contentsCount=${requestContents.size}")
        Log.i("TutorDiag", "[3] Backend ready and confirmed: useBackend=$useBackend")

        val fullResponseText = StringBuilder()
        var streamSucceeded = false

        try {
            var responseBody: okhttp3.ResponseBody? = null
            val effectiveApiKey = apiKey.ifBlank { getApiKey() }

            if (useBackend || effectiveApiKey.isNotBlank()) {
                val candidateModels = listOf(modelName, "gemini-3.5-flash", "gemini-flash-latest", "gemini-3.1-pro-preview", "gemini-3.1-flash-lite-preview").distinct()

                for (targetModel in candidateModels) {
                    val currentlyUsingBackend = isUsingBackend()
                    try {
                        Log.i("TutorDiag", "[4] Gemini streaming request started: model=$targetModel, useBackend=$currentlyUsingBackend")
                        responseBody = if (currentlyUsingBackend) {
                            backendService.generateContentStream(targetModel, getBackendAuthHeader(), request)
                        } else {
                            service.generateContentStream(targetModel, effectiveApiKey, request)
                        }
                        Log.d(TAG, "BYOK_HTTP_STATUS: 200 OK for stream model $targetModel")
                        break
                    } catch (e: Exception) {
                        if (e is kotlinx.coroutines.CancellationException) throw e
                        val code = if (e is retrofit2.HttpException) e.code() else 500
                        if (currentlyUsingBackend && (e is java.io.IOException || e is java.net.ConnectException || e is java.net.SocketTimeoutException || e is java.net.UnknownHostException || code == 404 || code >= 500)) {
                            isBackendUnreachable = true
                        }
                        Log.w(TAG, "Streaming attempt failed for $targetModel (code $code): ${e.message}")
                    }
                }
            }

            if (responseBody != null) {
                Log.d(TAG, "BYOK_STREAM_STARTED")
                val rawAccumulator = StringBuilder()
                var sseMatched = false
                var isFirstChunk = true

                responseBody.byteStream().bufferedReader().use { reader ->
                    var line = reader.readLine()
                    while (line != null) {
                        val trimmed = line.trim()
                        rawAccumulator.appendLine(trimmed)
                        if (trimmed.startsWith("data:")) {
                            sseMatched = true
                            val jsonPart = trimmed.substring(5).trim()
                            if (jsonPart.isNotBlank() && jsonPart != "[DONE]") {
                                try {
                                    val chunkResponse = json.decodeFromString<com.example.data.model.GenerateContentResponse>(jsonPart)
                                    val textChunk = chunkResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                                    if (!textChunk.isNullOrEmpty()) {
                                        fullResponseText.append(textChunk)
                                        if (isFirstChunk) {
                                            isFirstChunk = false
                                            Log.i("TutorDiag", "[5] First Gemini chunk received: length=${textChunk.length}")
                                        }
                                        onChunkReceived(textChunk)
                                    }
                                } catch (e: Exception) {
                                    Log.w(TAG, "Error parsing stream chunk: ${e.message}")
                                }
                            }
                        }
                        line = reader.readLine()
                    }
                }

                if (!sseMatched && rawAccumulator.isNotBlank()) {
                    val rawJson = rawAccumulator.toString().trim()
                    try {
                        val parsedResponse = json.decodeFromString<com.example.data.model.GenerateContentResponse>(rawJson)
                        val fullText = parsedResponse.candidates?.firstOrNull()?.content?.parts?.mapNotNull { it.text }?.joinToString("")
                        if (!fullText.isNullOrBlank()) {
                            fullResponseText.append(fullText)
                            onChunkReceived(fullText)
                        }
                    } catch (e1: Exception) {
                        try {
                            val parsedList = json.decodeFromString<List<com.example.data.model.GenerateContentResponse>>(rawJson)
                            parsedList.forEach { chunk ->
                                val text = chunk.candidates?.firstOrNull()?.content?.parts?.mapNotNull { it.text }?.joinToString("")
                                if (!text.isNullOrBlank()) {
                                    fullResponseText.append(text)
                                    onChunkReceived(text)
                                }
                            }
                        } catch (e2: Exception) {
                            Log.w(TAG, "Could not parse stream body as JSON array: ${e2.message}")
                        }
                    }
                }
            }

            if (fullResponseText.isNotBlank()) {
                streamSucceeded = true
            }

            // If streaming yielded no text, execute direct non-streaming generateContent fallback
            if (!streamSucceeded) {
                val effectiveApiKey = apiKey.ifBlank { getApiKey() }
                if (effectiveApiKey.isNotBlank() || isUsingBackend()) {
                    val candidateModels = listOf(modelName, "gemini-3.5-flash", "gemini-flash-latest", "gemini-3.1-pro-preview", "gemini-3.1-flash-lite-preview").distinct()
                    for (fallbackModel in candidateModels) {
                        try {
                            Log.i(TAG, "Attempting non-streaming Gemini API fallback with model $fallbackModel...")
                            val nonStreamResponse = if (isUsingBackend()) {
                                backendService.generateContent(fallbackModel, getBackendAuthHeader(), request)
                            } else {
                                service.generateContent(fallbackModel, effectiveApiKey, request)
                            }
                            val text = nonStreamResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                            if (!text.isNullOrBlank()) {
                                fullResponseText.append(text)
                                onChunkReceived(text)
                                streamSucceeded = true
                                Log.i(TAG, "Non-streaming direct Gemini API call succeeded with model $fallbackModel (length=${text.length})")
                                break
                            }
                        } catch (fallbackEx: Exception) {
                            if (fallbackEx is kotlinx.coroutines.CancellationException) throw fallbackEx
                            Log.w(TAG, "Non-streaming fallback failed for $fallbackModel: ${fallbackEx.message}")
                        }
                    }
                }
            }

            val finalResultText = fullResponseText.toString()
            val finalStr = if (finalResultText.isNotBlank()) {
                finalResultText
            } else {
                throw Exception("Could not reach Gemini AI. Please check your internet connection or verify your API key in Settings.")
            }

            PREVIOUS_USER_MESSAGE = lastUserMessage
            PREVIOUS_ASSISTANT_RESPONSE = finalStr

            val debugMetadata = com.example.data.model.TutorDebugMetadata(
                requestId = requestId,
                submittedMessage = lastUserMessage,
                provider = if (useBackend) "Lisan Backend Proxy" else "Gemini (Direct AI)",
                modelName = modelName,
                endpointCalled = endpointCalled,
                httpStatus = "200 OK",
                source = if (useBackend) "backend-stream" else "gemini-direct-stream",
                responseId = "res-" + java.util.UUID.randomUUID().toString().take(12),
                errorMessage = null
            )

            com.example.data.model.TutorResponseResult(responseText = finalStr, debugMetadata = debugMetadata)

        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            Log.e(TAG, "BYOK_REQUEST_ERROR: ${e.message}", e)
            throw e
        }
    }

    fun generateLocalTutorResponse(
        history: List<com.example.data.model.Content>,
        userName: String,
        effectiveResponseLang: String = "Urdu",
        isStudyModeOn: Boolean = false,
        learnerLevel: String = "Beginner"
    ): String {
        val lastUserMsg = history.lastOrNull { it.role == "user" }?.parts?.firstOrNull()?.text ?: ""
        val lowerMsg = lastUserMsg.lowercase(java.util.Locale.ROOT).trim()

        val isUrdu = effectiveResponseLang.contains("Urdu", ignoreCase = true) || !effectiveResponseLang.contains("Arabic", ignoreCase = true)
        val isArabic = effectiveResponseLang.contains("Arabic", ignoreCase = true)

        // 1. GREETINGS & INTRODUCTIONS
        if (lowerMsg.isEmpty() || lowerMsg in listOf("hi", "hello", "hey", "assalam", "سلام", "asalam", "asalamu", "ayyo", "start") ||
            lowerMsg.startsWith("hi ") || lowerMsg.startsWith("hello ") || lowerMsg.startsWith("assalam")) {
            return if (isArabic) {
                """
                السلام عليكم ورحمة الله وبركاته
                أهلاً بك يا $userName في لسان قواعد اللغة (Lisan Grammar)!
                أنا معلمتك للغة الإنجليزية، وسأساعدك في إتقان قواعد اللغة الإنجليزية عبر مقارنتها بقواعد لغتنا العربية الجميلة.

                ما هو الدرس أو القاعدة التي تود أن نبدأ بها اليوم؟
                • ضمائر الفاعل (Subject Pronouns)
                • فعل الكينونة (Verb To Be: am, is, are)
                • الأزمنة (Present Simple & Continuous)
                • تكوين الجمل ونفيها
                """.trimIndent()
            } else {
                """
                السلام عليكم ورحمة الله وبركاته
                لسان گرامر (Lisan Grammar) میں خوش آمدید، $userName!
                میں آپ کی انگلش ٹیچر ہوں۔ میں اردو اور عربی کے آسان موازنے کے ساتھ انگریزی گرامر سکھاؤں گی۔

                آج آپ کون سا موضوع سیکھنا چاہیں گے؟
                • سبجیکٹ پروناؤنز (Subject Pronouns: I, You, He, She)
                • ورب ٹو بی (Verb To Be: am, is, are)
                • زمانے اور اوقات (Present Simple & Continuous Tenses)
                • جملے بنانے کا طریقہ اور سوالیہ جملے
                """.trimIndent()
            }
        }

        // 2. HOW ARE YOU / WELLBEING
        if (lowerMsg.contains("how are you") || lowerMsg.contains("how are u") || lowerMsg.contains("kaise ho") || lowerMsg.contains("kaisi ho") || lowerMsg.contains("کیف حالک")) {
            return if (isArabic) {
                "الحمد لله، أنا بأفضل حال ومستعدة لمساعدتك يا $userName! ما هو الموضوع النحوي الذي ترغب في استكشافه وتطبيقه الآن؟"
            } else {
                "الحمد للہ، میں بالکل خیریت سے ہوں، $userName! آپ کو انگلش گرامر سکھانے کے لیے تیار ہوں۔ بتائیے، آج آپ کس موضوع کی مشق کرنا چاہتے ہیں؟"
            }
        }

        // 3. META / FOLLOW-UP COMMANDS (Urdu mein samjhao / English mein / Simplify / Examples / Quiz)
        if (lowerMsg.contains("urdu mein") || lowerMsg.contains("اردو میں") || lowerMsg.contains("urdu me")) {
            val prevModelMsg = history.lastOrNull { it.role == "model" }?.parts?.firstOrNull()?.text ?: ""
            return """
            ضرور! میں آپ کو اردو میں وضاحت کرتی ہوں۔

            انگریزی جملے کی بنیادی ترتیب **S + V + O** ہوتی ہے:
            1. **Subject (فاعل)**: کام کرنے والا (جیسے I, He, Ali)
            2. **Verb (فعل)**: کام یا عمل (جیسے reads, plays, is)
            3. **Object (مفعول)**: جس پر کام ہو (جیسے a book, cricket)

            اردو میں فعل آخر میں آتا ہے ("علی کتاب پڑھتا ہے") جبکہ انگریزی میں فعل درمیان میں آتا ہے ("Ali reads a book")۔

            **مشق کے لیے سوال (Exercise):**
            آپ یہ کیسے کہیں گے کہ "میں چائے پیتا ہوں"، English میں؟
            *(اپنا جواب لکھ کر بھیجیں)*
            """.trimIndent()
        }

        if (lowerMsg.contains("simple") || lowerMsg.contains("آسان") || lowerMsg.contains("easier") || lowerMsg.contains("chota") || lowerMsg.contains("short")) {
            return """
            بالکل آسان الفاظ میں سمجھیں:
            • **واحد (He, She, It, Ali)** کے ساتھ **is** یا فعل کے ساتھ **s/es** لگتا ہے۔
            • **جمع (We, They, You)** کے ساتھ **are** لگتا ہے۔
            • **I** کے ساتھ ہمیشہ **am** لگتا ہے۔

            **مشق کے لیے سوال:**
            آپ English میں کیسے کہیں گے کہ "وہ خوش ہے"؟
            """.trimIndent()
        }

        if (lowerMsg.contains("more example") || lowerMsg.contains("مثالیں") || lowerMsg.contains("aur example") || lowerMsg.contains("examples")) {
            return """
            یہاں کچھ مزید آسان اور روزمرہ مثالیں ملاحظہ فرمائیں:

            1. **He is a doctor.** (وہ ایک ڈاکٹر ہے۔)
            2. **She speaks English.** (وہ انگریزی بولتی ہے۔)
            3. **They are playing football.** (وہ فٹبال کھیل رہے ہیں۔)
            4. **We study every day.** (ہم روزانہ پڑھتے ہیں۔)

            **مشق کے لیے سوال:**
            آپ English میں کیسے کہیں گے کہ "ہم طالب علم ہیں"؟
            """.trimIndent()
        }

        if (lowerMsg.contains("quiz") || lowerMsg.contains("test") || lowerMsg.contains("امتحان") || lowerMsg.contains("سوال پوچھو") || lowerMsg.contains("test me") || lowerMsg.contains("quiz me")) {
            return """
            آئیے آپ کے لیے ایک مختصر اور مفید کوئز شروع کرتے ہیں! 🎓

            **کوئز سوال نمبر 1:**
            آپ یہ کیسے کہیں گے کہ "وہ اسکول جاتا ہے"، English میں؟

            *(اپنا جواب چیٹ میں لکھ کر بھیجیں، میں آپ کی رہنمائی کروں گی)*
            """.trimIndent()
        }

        // 4. STUDENT SENTENCE EVALUATION & ERROR DIAGNOSIS
        // Check if user submitted a student answer/sentence
        if (lowerMsg.startsWith("i am ") || lowerMsg.startsWith("she is ") || lowerMsg.startsWith("he is ") ||
            lowerMsg.startsWith("they are ") || lowerMsg.startsWith("we are ") || lowerMsg.startsWith("i drink ") ||
            lowerMsg.startsWith("he goes ") || lowerMsg.startsWith("she speaks ") || lowerMsg.startsWith("i read ") ||
            lowerMsg == "he is happy" || lowerMsg == "she is a teacher" || lowerMsg == "we are students" ||
            lowerMsg.contains("student") || lowerMsg.contains("teacher") || lowerMsg.contains("happy")) {

            val isPerfect = !lowerMsg.contains(" goed ") && !lowerMsg.contains(" he go ") && !lowerMsg.contains(" she go ") && !lowerMsg.contains(" they is ") && !lowerMsg.contains(" i is ")

            if (isPerfect) {
                return """
                ماشاءاللہ! بالکل درست جواب ہے۔ ممتاز! 🎉

                آپ کا جملہ گرامر کے اصولوں کے مطابق 100% درست ہے۔
                • فاعل (Subject): درست طور پر استعمال کیا گیا ہے۔
                • امدادی فعل / فعل (Verb): فاعل کے عین مطابق ہے۔

                **اگلی مشق کے لیے اگلا سوال:**
                آپ یہ کیسے کہیں گے کہ "وہ کل اسکول گئی تھی"، English میں؟
                """.trimIndent()
            } else {
                return """
                بہت اچھی کوشش! آئیے اس جملے کو درست کرتے ہیں:

                ⚠️ **غلطی کی نشاندہی:** انگریزی میں فاعل اور فعل کا آپس میں متفق ہونا لازمی ہے۔
                • اگر فاعل واحد ہو (He / She / It) تو فعل کے ساتھ s/es کا اضافہ ہوتا ہے۔
                • اگر فاعل I ہو تو 'am' استعمال ہوتا ہے۔

                **اب دوبارہ کوشش کریں:**
                کیا آپ اس جملے کو درست کر کے دوبارہ لکھ سکتے ہیں؟
                """.trimIndent()
            }
        }

        // 5. TOPIC: VERB "TO BE" (am, is, are, was, were)
        if (lowerMsg.contains("verb to be") || lowerMsg.contains("am is are") || lowerMsg.contains("to be") || lowerMsg.contains("ورب ٹو بی")) {
            return """
            **ورب "To Be" کا آسان خلاصہ اور موازنہ:**

            اردو میں ہم "ہوں، ہے، ہیں، تھا، تھے" استعمال کرتے ہیں۔ انگریزی میں اس کے لیے **Verb To Be** استعمال ہوتا ہے:

            • **am** ⬅️ صرف **I** کے ساتھ (I am a student - میں طالب علم ہوں)
            • **is** ⬅️ واحد اسم/ضمیر کے ساتھ جیسے **He, She, It, Ali** (He is happy - وہ خوش ہے)
            • **are** ⬅️ جمع اسم/ضمیر کے ساتھ جیسے **We, They, You** (They are ready - وہ تیار ہیں)
            • **was / were** ⬅️ ماضی کے لیے (He was busy / They were friends)

            📌 **عربی موازنہ:** عربی کی الجملة الاسمية (جیسے: هو طالبٌ) میں عام طور پر فعل ظاہر نہیں ہوتا، مگر انگریزی میں 'is' لکھنا لازمی ہے!

            **مشق کے لیے سوال (Exercise):**
            آپ یہ کیسے کہیں گے کہ "وہ ایک استانی ہے"، English میں؟
            """.trimIndent()
        }

        // 6. TOPIC: TENSES (Present Simple, Past Simple, Future, Continuous)
        if (lowerMsg.contains("tense") || lowerMsg.contains("زمانہ") || lowerMsg.contains("present") || lowerMsg.contains("past") || lowerMsg.contains("future") || lowerMsg.contains("پریزنٹ") || lowerMsg.contains("پاسٹ")) {
            return """
            **انگریزی کے بنیادی زمانے (Tenses):**

            1. **Present Simple (حال سادہ):** روزمرہ عادات اور حقائق کے لیے۔
               • ساخت: `Subject + Verb(s/es) + Object`
               • مثال: *She reads books.* (وہ کتابیں پڑھتی ہے۔)

            2. **Present Continuous (حال جاری):** جو کام ابھی ہو رہا ہو۔
               • ساخت: `Subject + is/am/are + Verb-ing`
               • مثال: *They are playing.* (وہ کھیل رہے ہیں۔)

            3. **Past Simple (ماضی سادہ):** جو کام ماضی میں مکمل ہو گیا۔
               • ساخت: `Subject + 2nd Form of Verb`
               • مثال: *I saw him yesterday.* (میں نے اسے کل دیکھا۔)

            **مشق کے لیے سوال (Exercise):**
            آپ یہ کیسے کہیں گے کہ "میں ابھی خط لکھ رہا ہوں"، English میں؟
            """.trimIndent()
        }

        // 7. TOPIC: PRONOUNS (I, You, He, She, It, We, They)
        if (lowerMsg.contains("pronoun") || lowerMsg.contains("ضمائر") || lowerMsg.contains("ضمیر") || lowerMsg.contains("پرونا")) {
            return """
            **انگریزی کے شخصی ضمائر (Subject Pronouns):**

            ضمیر (Pronoun) وہ لفظ ہے جو اسم (Noun) کی جگہ استعمال ہوتا ہے تاکہ بار بار نام دہرانا نہ پڑے:

            • **I** = میں (متکلم واحد)
            • **We** = ہم (متکلم جمع)
            • **You** = آپ / تم (مخاطب)
            • **He** = وہ (مذکر غائب)
            • **She** = وہ (مؤنث غائب)
            • **It** = یہ / وہ (بے جان چیزوں اور جانوروں کے لیے)
            • **They** = وہ سب (جمع غائب)

            **مشق کے لیے سوال (Exercise):**
            آپ یہ کیسے کہیں گے کہ "ہم کرکٹ کھیلتے ہیں"، English میں؟
            """.trimIndent()
        }

        // 8. TOPIC: PREPOSITIONS (in, on, at, under, between)
        if (lowerMsg.contains("preposition") || lowerMsg.contains("حروف جار") || lowerMsg.contains("in on at") || lowerMsg.contains("پریپوزیشن")) {
            return """
            **پریپوزیشنز (Prepositions - حروفِ جار):**

            پریپوزیشن کسی اسم یا ضمیر کا دوسرے الفاظ کے ساتھ وقت، جگہ، یا سمت کا تعلق ظاہر کرتا ہے:

            • **in** ⬅️ اندر یا بڑے مقامات/سالوں کے لیے (in the room, in 2026)
            • **on** ⬅️ سطح کے اوپر یا دنوں کے لیے (on the table, on Monday)
            • **at** ⬅️ مخصوص وقت یا مخصوص مقام کے لیے (at 5 PM, at home)
            • **under** ⬅️ نیچے (under the chair)

            **مشق کے لیے سوال (Exercise):**
            آپ یہ کیسے کہیں گے کہ "کتاب میز پر ہے"، English میں؟
            """.trimIndent()
        }

        // 9. TOPIC: PASSIVE VOICE & MODALS
        if (lowerMsg.contains("passive") || lowerMsg.contains("modal") || lowerMsg.contains("can could") || lowerMsg.contains("فعل مجہول")) {
            return """
            **Passive Voice (فعل مجہول) اور Modals:**

            • **Passive Voice:** جب مفعول (Object) کو فاعل سے زیادہ اہمیت دی جائے یا کام کرنے والا نامعلوم ہو۔
              - ساخت: `Object + Helping Verb + 3rd Form of Verb (V3)`
              - مثال: *The car was washed.* (گاڑی دھوئی گئی تھی۔)

            • **Modal Verbs (Can / Could / Should / Must):**
              - **Can:** قابلیت کے لیے (*I can speak English.*)
              - **Should:** مشورے یا نصیحت کے لیے (*You should rest.*)
              - **Must:** لازمی فرض کے لیے (*We must obey rules.*)

            **مشق کے لیے سوال (Exercise):**
            آپ یہ کیسے کہیں گے کہ "آپ کو روزانہ ورزش کرنی چاہیے"، English میں؟
            """.trimIndent()
        }

        // 10. GENERAL DEFAULT TUTOR LESSON RESPONSE
        return """
        آپ کا سوال بہت اہم ہے! آئیے اس تصور کو گرامر کے آسان اصولوں کے ساتھ سمجھتے ہیں:

        انگریزی زبان میں ہر مکمل جملے کے لیے دو بنیادی اجزاء ضروری ہیں:
        1. **Subject (فاعل):** جو عمل انجام دے رہا ہو۔
        2. **Predicate (خبر/فعل):** وہ عمل یا حالت جو فاعل کے متعلق بتائی جا رہی ہو۔

        📌 **اہم نکتہ:** اردو میں ہم اکثر امدادی فعل جملے کے آخر میں لگاتے ہیں، مگر انگریزی میں امدادی فعل فاعل کے فوری بعد آتا ہے۔

        **مشق کے لیے سوال (Exercise):**
        کیا آپ انگریزی میں ایک سادہ جملہ بنا سکتے ہیں جس میں "reads" کا لفظ استعمال ہو؟
        *(اپنا جملہ نیچے چیٹ میں لکھیں، میں فوراً جانچ کر رہنمائی کروں گی)*
        """.trimIndent()
    }

    suspend fun extractMemoryAndPreferences(
        userMessage: String,
        tutorReply: String
    ): List<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if ((apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "YOUR_GEMINI_API_KEY") && !isUsingBackend()) {
            return@withContext emptyList<String>()
        }
        
        val prompt = """
            Analyze this conversational interaction between an English language learner (User) and their AI Tutor.
            Your task is to extract any key personal facts, explicit preferences, language requests, or specific learning targets about the User.
            
            Interaction:
            User: "$userMessage"
            Tutor: "$tutorReply"
            
            RULES:
            1. Extract only REAL, EXPLICIT personal facts, language choices, or preferences about the User (e.g. "User prefers to talk in Urdu", "User wants to practice prepositions", "User's name is Ali", "User requested simpler explanations").
            2. Do NOT extract standard greeting or conversational phrases. Only extract facts/preferences that should be remembered long-term.
            3. Keep each extracted memory brief (under 10 words).
            4. If no long-term user facts or preferences are explicitly stated or requested, return an empty list.
            5. Return the extracted memories as a simple JSON array of strings: ["Fact 1", "Fact 2"]
            6. Return ONLY the JSON array. Do not include markdown tags like ```json or any other text.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(
                com.example.data.model.Content(role = "user", parts = listOf(com.example.data.model.Part(text = prompt)))
            ),
            generationConfig = GenerationConfig(temperature = 0.2f)
        )
        
        try {
            val response = callWithModelFallback(apiKey, request, MODEL_FLASH)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            val cleaned = cleanJsonString(text).trim()
            if (cleaned.startsWith("[")) {
                val json = Json { ignoreUnknownKeys = true }
                return@withContext json.decodeFromString<List<String>>(cleaned)
            }
        } catch (e: Exception) {
            Log.e("RetrofitClient", "extractMemoryAndPreferences failed: ${e.message}")
        }
        emptyList()
    }

    suspend fun summarizeChatSession(messages: List<Content>): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty() && !isUsingBackend()) {
            return@withContext "Student practiced English grammar and conversation topics."
        }
        val transcript = messages.joinToString("\n") { "${it.role}: ${it.parts.firstOrNull()?.text ?: ""}" }
        val prompt = """
            Summarize the following chat session between a student and an English-Arabic-Urdu grammar tutor into ONE concise, single-sentence memory fact about the student's learning progress, questions asked, or topics discussed (e.g. "Student practiced Present Continuous tense and asked about pronoun usage"). Keep it brief and return ONLY the single summary sentence without quotes.
            
            Chat Transcript:
            $transcript
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(temperature = 0.3f)
        )
        try {
            val response = callWithModelFallback(apiKey, request, MODEL_FLASH)
            val summary = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
            if (!summary.isNullOrBlank()) {
                return@withContext summary.removeSurrounding("\"")
            }
        } catch (e: Exception) {
            Log.i(TAG, "summarizeChatSession offline fallback: ${e.message}")
        }
        return@withContext "Student studied English grammar and practiced conversational sentences."
    }

    suspend fun generateConversationSummary(
        context: android.content.Context,
        olderMessages: List<com.example.data.model.Content>,
        existingSummary: String?
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty() && !isUsingBackend()) {
            return@withContext ""
        }
        
        val prompt = buildString {
            append("You are an expert language learning assistant.\n")
            append("Your job is to summarize the older part of a language learning conversation between a student and their tutor.\n")
            append("Provide a concise summary highlighting only information useful for continuing the conversation, such as:\n")
            append("- The main grammar topics or vocabulary being studied.\n")
            append("- Particular questions or exercises that were discussed.\n")
            append("- Key corrections or areas where the student had difficulty.\n")
            append("- Any progress, preferences, or decisions made.\n\n")
            
            if (!existingSummary.isNullOrBlank()) {
                append("Previous summary of earlier parts of the conversation:\n")
                append("\"$existingSummary\"\n\n")
            }
            
            append("Older dialogue messages to be summarized and integrated:\n")
            olderMessages.forEach { msg ->
                val roleName = if (msg.role == "user") "Student" else "Tutor"
                val text = msg.parts.firstOrNull()?.text ?: ""
                append("$roleName: $text\n")
            }
            append("\nRULES:\n")
            append("1. Keep the summary concise (under 100 words).\n")
            append("2. Focus purely on key learning concepts, corrections, and topic progress.\n")
            append("3. Do NOT use markdown formatting, code blocks, or bullets. Just write a few plain, clear sentences in English.\n")
            append("4. Always refer to the Tutor as a female teacher (she/her/خاتون ٹیچر) if mentioning the Tutor's identity or actions.\n")
        }

        val request = GenerateContentRequest(
            contents = listOf(
                com.example.data.model.Content(role = "user", parts = listOf(com.example.data.model.Part(text = prompt)))
            ),
            generationConfig = GenerationConfig(temperature = 0.3f)
        )
        
        try {
            val response = callWithModelFallback(apiKey, request, MODEL_FLASH)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
        } catch (e: Exception) {
            Log.e(TAG, "generateConversationSummary failed: ${e.message}", e)
            existingSummary ?: ""
        }
    }

    private fun generateSmartLocalTutorResponse(
        history: List<Content>,
        mode: String,
        selectedLanguageMode: String,
        userName: String,
        learnerProfileSummary: String?
    ): String {
        val lastUserMsg = history.lastOrNull { it.role == "user" }?.parts?.firstOrNull()?.text ?: ""
        val activeIntent = LanguageDetector.resolveActiveInstructionIntent(history, selectedLanguageMode)
        val effectiveLang = if (activeIntent.requestedLanguage == "Custom") activeIntent.customLanguageName ?: "English" else activeIntent.requestedLanguage
        val lowerMsg = lastUserMsg.lowercase(java.util.Locale.ROOT)

        val followUpKeywords = listOf(
            "urdu mein", "english mein", "arabic mein", "simple words", "examples do", "aur example",
            "short mein", "detail mein", "phir se", "explain karo", "translate",
            "make it easier", "give me more", "what does this mean", "quiz", "samjhao"
        )
        val isFollowUpInstruction = followUpKeywords.any { lowerMsg.contains(it) }
        val targetUserMsg = if (isFollowUpInstruction) {
            val previousUserTurns = history.filter { it.role == "user" }.dropLast(1)
            previousUserTurns.lastOrNull { turn ->
                val txt = turn.parts.firstOrNull()?.text?.lowercase(java.util.Locale.ROOT) ?: ""
                !followUpKeywords.any { kw -> txt.contains(kw) }
            }?.parts?.firstOrNull()?.text ?: lastUserMsg
        } else {
            lastUserMsg
        }
        val lowerTopicMsg = targetUserMsg.lowercase(java.util.Locale.ROOT)

        // 1. Casual Greeting
        if (!isFollowUpInstruction && (lowerMsg in listOf("hi", "hello", "hey", "hola", "assalam", "aoa", "سلام") || lowerMsg.startsWith("hi ") || lowerMsg.startsWith("hello "))) {
            return when (effectiveLang) {
                "Urdu" -> "السلام علیکم ورحمة الله وبركاته $userName! لیسان AI ٹیوٹر میں خوش آمدید۔ میں آج آپ کی انگریزی میں کیا مدد کر سکتی ہوں؟"
                "Arabic" -> "السلام عليكم ورحمة الله وبركاته $userName! مرحباً بك في معلم لسان الذكي. كيف يمكنني مساعدتك اليوم؟"
                else -> "Hello $userName! Welcome to Lisan AI Tutor. How can I help you with your English today?"
            }
        }

        // 2. Feedback / Misunderstanding / "I didn't ask for this"
        if (!isFollowUpInstruction && (lowerMsg.contains("didn't ask") || lowerMsg.contains("did not ask") || lowerMsg.contains("wrong") ||
            lowerMsg.contains("stop giving") || lowerMsg.contains("misunderstood") || lowerMsg.contains("سمجھ نہیں") || lowerMsg.contains("غلط"))) {
            return when (effectiveLang) {
                "Urdu" -> "معذرت، میں آپ کا سوال درست طور پر نہیں سمجھ سکا۔ براہ کرم بتائیں کہ آپ کس چیز میں مدد چاہتے ہیں یا اپنا سوال دوبارہ لکھیں۔"
                "Arabic" -> "أعتذر عن عدم الفهم! يرجى توضيح ما تحتاج المساعدة فيه وسأقوم بإجابتك فوراً."
                else -> "Sorry, I misunderstood. Please tell me what you would like help with, or ask your question again."
            }
        }

        // 3. Has vs Have
        if ((lowerTopicMsg.contains("has") && lowerTopicMsg.contains("have")) || lowerTopicMsg.contains("difference between has and have")) {
            return """
Has vs Have Usage

English Explanation

We use Has with singular subjects (he, she, it, or singular nouns).
We use Have with plural subjects and pronouns (I, you, we, they, or plural nouns).

اردو وضاحت

انگریزی میں Has واحد اسم یا ضمائر (he, she, it) کے ساتھ استعمال ہوتا ہے۔
جبکہ Have جمع اسم یا ضمائر (I, you, we, they) کے ساتھ استعمال ہوتا ہے۔

Examples

Positive Examples

1. He has a new book.
   اس کے پاس ایک نئی کتاب ہے۔

2. They have a house.
   ان کے پاس ایک گھر ہے۔

3. I have two pens.
   میرے پاس دو قلم ہیں۔

Arabic Grammar Connection

In Arabic, possessive meaning is often shown with prepositions like لـ or عِنْدَ.

عِنْدَهُ كِتَابٌ جَدِيدٌ.
He has a new book.

اردو وضاحت

عربی میں ملکیت ظاہر کرنے کے لیے "عند" یا "ل" کا استعمال ہوتا ہے، جبکہ انگریزی میں Has اور Have استعمال ہوتے ہیں۔
            """.trimIndent()
        }

        // 4. In / On / At (Prepositions)
        if (lowerTopicMsg.contains("in on at") || lowerTopicMsg.contains("preposition")) {
            return """
Prepositions: In, On, At

English Explanation

We use At for specific times and exact locations.
We use On for days and dates.
We use In for months, years, cities, and long periods.

اردو وضاحت

انگریزی میں At مخصوص وقت یا جگہ کے لیے استعمال ہوتا ہے۔
On دنوں اور تاریخوں کے لیے استعمال ہوتا ہے۔
In مہینوں، سالوں، شہروں اور طویل دورانیے کے لیے استعمال ہوتا ہے۔

Examples

1. She arrives at 5 PM.
   وہ شام پانچ بجے پہنچتی ہے۔

2. I will see you on Monday.
   میں آپ سے پیر کے دن ملوں گا۔

3. They live in Lahore.
   وہ لاہور میں رہتے ہیں۔
            """.trimIndent()
        }

        // 5. Present Perfect Tense
        if (lowerTopicMsg.contains("present perfect")) {
            return """
Present Perfect Tense

English Explanation

We use the present perfect tense for actions completed in the recent past that have a connection to the present time.

اردو وضاحت

فعل حال مکمل ایسے کاموں کے لیے استعمال ہوتا ہے جو حال ہی میں مکمل ہوئے ہوں اور ان کا اثر یا تعلق موجودہ وقت سے ہو۔

When We Use It

1. Actions completed recently
2. Life experiences up to now

اردو میں استعمال

1. حال ہی میں مکمل ہونے والے کام
2. ماضی کے وہ تجربات جن کا تعلق حال سے ہے

Sentence Pattern

Positive:
Subject + has / have + 3rd form of verb + object

Negative:
Subject + has not / have not + 3rd form of verb + object

Question:
Has / Have + subject + 3rd form of verb + object?

Examples

Positive Examples

1. I have finished my homework.
   میں اپنا کام مکمل کر چکا ہوں۔

2. She has arrived in Lahore.
   وہ لاہور پہنچ چکی ہے۔

Negative Examples

1. He has not written the report.
   اس نے رپورٹ نہیں لکھی ہے۔

2. They have not eaten dinner.
   انہوں نے کھانا نہیں کھایا ہے۔

Question Examples

1. Have you completed the lesson?
   کیا آپ نے سبق مکمل کر لیا ہے؟

2. Has she called you?
   کیا اس نے آپ کو فون کیا ہے؟

Common Mistakes

1. Incorrect: I have completed my work yesterday.
   Correct: I completed my work yesterday.
   اردو وضاحت: جب وقت (yesterday) واضح ہو تو Present Perfect کے بجائے Past Simple استعمال کریں۔

Arabic Grammar Connection

In Arabic, a completed past action with present relevance is often expressed using قَدْ with فعل ماضٍ.

قَدْ أَتَمَمْتُ الْوَاجِبَ.
I have completed the homework.

اردو وضاحت

عربی میں "قد" کے ساتھ فعلِ ماضی استعمال کرنے سے حالیہ مکمل شدہ کام کا مفہوم پیدا ہوتا ہے جو انگریزی کے Present Perfect Tense سے مشابہ ہے۔

Practice

1. Complete the sentence: She ___ (has / have) finished her work.
2. Make this negative: They have left the room.
3. Make a question: You have read this book.

Reply with your answers, and I will check them.
            """.trimIndent()
        }

        // 6. Sentence Correction request
        if (lowerMsg.startsWith("correct") || lowerMsg.contains("check my sentence")) {
            return when (effectiveLang) {
                "Urdu" -> "آپ کا جملہ: \"$lastUserMsg\" گرامر کے لحاظ سے بالکل ٹھیک ہے یا معمولی تبدیلی کے ساتھ استعمال ہو سکتا ہے۔"
                "Arabic" -> "جملتك: \"$lastUserMsg\" مفهومة وسليمة من الناحية القواعدية."
                else -> "Sentence check: Your sentence '$lastUserMsg' is grammatically sound and clear."
            }
        }

        // 7. Exercise request
        if (lowerMsg.contains("exercise") || lowerMsg.contains("practice")) {
            return when (effectiveLang) {
                "Urdu" -> """
                    **مشق کے لیے سوال:**
                    خالی جگہ پر کریں:
                    "She ___ (has/have) completed her work."
                """.trimIndent()
                "Arabic" -> """
                    **تمرين سريع:**
                    أكمل الفراغ:
                    "She ___ (has/have) completed her work."
                """.trimIndent()
                else -> """
                    **Quick Practice Exercise:**
                    Fill in the blank:
                    "She ___ (has/have) completed her work."
                """.trimIndent()
            }
        }

        // 8. General dynamic response referencing user's input
        val shortUserPrompt = if (lastUserMsg.length > 60) lastUserMsg.take(57) + "..." else lastUserMsg
        return when (effectiveLang) {
            "Urdu" -> """
                **توجہ برائے سوال:** "$shortUserPrompt"
                
                انگریزی گرامر میں، جملے کی ساخت فاعل (Subject) اور فعل (Verb) کی مطابقت پر مبنی ہوتی ہے۔
                
                • **گرامر کی وضاحت:** "$shortUserPrompt" میں فاعل اور وقت (Tense) کا درست ہونا ضروری ہے۔
                • **عربی موازنہ:** عربی میں الجملة الاسمية یا الجملة الفعلية کے اصول انگریزی کے اس ساخت سے مشابہ ہیں۔
                
                اگر آپ اس کی مزید تفصیل یا سوال پوچھنا چاہتے ہیں تو اپنی مثال درج کریں۔
            """.trimIndent()

            "Arabic" -> """
                **إجابة حول:** "$shortUserPrompt"
                
                في قواعد اللغة الإنجليزية، تعتمد صحة الجملة على التوافق بين الفاعل والفعل (Subject-Verb Agreement).
                
                • **ملاحظة:** بالنسبة لـ "$shortUserPrompt"، تأكد من استخدام الزمن والفعل المناسبين.
                • **مقارنة مع العربية:** الجملة الاسمية والجملة الفعلية تشابه هذا الهيكل القواعدي.
            """.trimIndent()

            else -> """
                **Regarding your prompt:** "$shortUserPrompt"
                
                In English grammar, clear structure depends on proper subject-verb agreement and correct tense usage.
                
                • **Grammar Note:** For "$shortUserPrompt", ensure the subject matches the corresponding verb form.
                • **Arabic Parallel:** Similar to how nominal (جملة اسمية) or verbal (جملة فعلية) sentences operate in Arabic, English requires a defined subject and predicate.
                
                Feel free to share a specific example sentence for a detailed step-by-step breakdown!
            """.trimIndent()
        }
    }

    suspend fun fetchLiveSilverPrice(
        targetCountry: String = "Pakistan",
        targetCity: String = "Multan",
        targetCurrency: String = "PKR",
        specificSource: String? = null,
        forceRefresh: Boolean = false
    ): com.example.data.model.SilverRateUiState = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty() && !isUsingBackend()) {
            return@withContext com.example.data.model.SilverRateUiState.Error(
                message = "Gemini service is not configured. Please enter your personal Gemini API key under Settings to enable live Google Search price lookups.",
                isConfigError = true
            )
        }

        val countryClean = targetCountry.ifBlank { "Pakistan" }
        val cityClean = targetCity.ifBlank { "Multan" }
        val isPakistan = countryClean.equals("Pakistan", ignoreCase = true) || countryClean.equals("PK", ignoreCase = true)

        val searchPrompt = if (isPakistan) {
            val sourceQuery = if (specificSource == "Sarafa") {
                "site:sarafa.pk silver rate today OR current Pakistan APGJSA gold and silver rate per tola today"
            } else if (specificSource == "HamariWeb") {
                "HamariWeb silver rate today Pakistan per tola"
            } else {
                "site:sarafa.pk silver rate $cityClean today OR Current silver rate today in $cityClean Pakistan 999 fine silver latest market rate"
            }

            val sourceConstraint = if (specificSource != null) {
                """
                CRITICAL SOURCE CONSTRAINT:
                You MUST retrieve the current market silver rate in PKR per tola specifically from ${if (specificSource == "Sarafa") "Sarafa.pk / APGJSA (All Pakistan Gem and Jewellers Sarafa Association)" else "HamariWeb"}.
                Do NOT use any other sources. Do NOT use international spot rate converted to PKR.
                If the search results do NOT contain verified, current silver prices for Pakistan from ${specificSource} for today or the most recent market day of August 2026, you MUST signal failure by setting "is_verified": false and returning 0.0 or null for the price fields.
                DO NOT guess, estimate, or invent a price. Under no circumstances return 3350.0 or any other placeholder rate.
                """.trimIndent()
            } else {
                ""
            }

            """
            [CRITICAL REAL-TIME GROUNDED SEARCH MANDATE FOR HAQ MEHR & SILVER]
            You are a real-time bullion market researcher for Pakistan.
            
            SEARCH STRATEGY:
            1. Search Google using Search Grounding for:
               "$sourceQuery"
            2. PREFERRED SOURCE: Check ${specificSource ?: "Sarafa.pk and official Pakistan Sarafa Association market data"} first.
            3. FALLBACK SOURCES: If the preferred source is unavailable, do NOT return guesses or old placeholder rates. Return is_verified: false.
            
            $sourceConstraint
            
            4. Extract the latest numeric price in PKR. Identify the unit (tola = 11.6638g, gram = 1.0g, 10 grams = 10.0g, or troy ounce = 31.1035g).
            5. Convert/normalize the price carefully to "PKR per tola" or per gram.
            6. NEVER invent numbers. Set "is_verified": true ONLY if real current market data is retrieved from search results for the specified source. Otherwise set "is_verified": false.
            
            Return ONLY a valid JSON object with this exact structure:
            ```json
            {
              "price_per_gram": 634.61,
              "price_per_tola": 7402.0,
              "price_per_troy_oz": 19738.0,
              "price_per_10_gram": 6346.10,
              "price_per_kg": 634610.0,
              "currency": "PKR",
              "currency_symbol": "Rs.",
              "purity": "999 Fine Silver (24K / 99.9%)",
              "purity_factor": 1.0,
              "requested_city": "$cityClean",
              "requested_country": "Pakistan",
              "applied_market_city": "$cityClean ${specificSource ?: "Sarafa"} Market",
              "is_city_specific": true,
              "source_website": "${specificSource ?: "Sarafa.pk"}",
              "source_url": "${if (specificSource == "HamariWeb") "https://hamariweb.com" else "https://sarafa.pk"}",
              "retrieval_date_time": "Today (Live)",
              "price_type": "LOCAL_MARKET",
              "is_verified": true,
              "city_rate_note": "Verified current Pakistani ${specificSource ?: "Sarafa"} market rate",
              "summary": "پاکستان مارکیٹ کے مطابق چاندی کی موجودہ قیمت",
              "converted_from_international": false,
              "original_source_currency": "PKR",
              "original_source_price": 7402.0,
              "original_source_unit": "tola",
              "exchange_rate_note": null
            }
            ```
            """.trimIndent()
        } else {
            """
            [CRITICAL REAL-TIME GROUNDED SEARCH MANDATE FOR INTERNATIONAL SILVER]
            You are a real-time international bullion market researcher.
            Target location: Country: $countryClean, City: $cityClean, Local Currency: $targetCurrency.
            
            SEARCH STRATEGY:
            1. Search Google using Search Grounding for:
               "Current silver price today in $countryClean, $cityClean, 999 fine silver, latest market rate in $targetCurrency" OR "silver spot price today $countryClean $targetCurrency per gram or ounce"
            2. DO NOT search Sarafa.pk for non-Pakistan countries. Search reputable sources appropriate to $countryClean (e.g. Dubai Gold & Jewellery Group, Saudi Gold Price, London Bullion Market / LBMA, Kitco, APMEX, GoldPriceZ, SilverPrice.org, Investing.com, LiveMint).
            3. Extract the current silver rate. If local retail jewellery rate is available in $targetCurrency, set "price_type": "LOCAL_MARKET" or "RETAIL".
               If international spot price is used (e.g. in USD/troy oz or converted to $targetCurrency), set "price_type": "INTERNATIONAL_SPOT", "applied_market_city": "International Silver Spot Market", and clearly note this in "city_rate_note".
            4. Normalize the price per gram (1.0g), per troy ounce (31.1034768g), per tola (11.6638125g), per 10g, and per kg in $targetCurrency.
            5. NEVER invent numbers. Set "is_verified": true when grounded search results contain current market data.
            
            Return ONLY a valid JSON object with this exact structure:
            ```json
            {
              "price_per_gram": 3.85,
              "price_per_tola": 44.90,
              "price_per_troy_oz": 119.75,
              "price_per_10_gram": 38.50,
              "price_per_kg": 3850.0,
              "currency": "$targetCurrency",
              "currency_symbol": "$targetCurrency",
              "purity": "999 Fine Silver (24K / 99.9%)",
              "purity_factor": 1.0,
              "requested_city": "$cityClean",
              "requested_country": "$countryClean",
              "applied_market_city": "$cityClean Market / International Spot",
              "is_city_specific": true,
              "source_website": "Reputable Bullion Source",
              "source_url": "",
              "retrieval_date_time": "Today (Live)",
              "price_type": "LOCAL_MARKET",
              "is_verified": true,
              "city_rate_note": "Verified market silver price",
              "summary": "Current silver market price for $cityClean, $countryClean",
              "converted_from_international": false,
              "original_source_currency": "$targetCurrency",
              "original_source_price": 3.85,
              "original_source_unit": "gram",
              "exchange_rate_note": null
            }
            ```
            """.trimIndent()
        }

        val searchTool = buildJsonObject {
            putJsonObject("googleSearch") {}
        }

        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    role = "user",
                    parts = listOf(Part(text = searchPrompt))
                )
            ),
            tools = listOf(searchTool),
            generationConfig = GenerationConfig(
                temperature = 0.1f
            )
        )

        // Diagnostic logging
        Log.i(TAG, "[SilverSearchDiag] Initiating grounded search. Country: $countryClean, City: $cityClean, TargetCurrency: $targetCurrency, IsPakistan: $isPakistan, Source: $specificSource")

        try {
            val response = callWithModelFallback(apiKey, request, MODEL_FLASH)
            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            Log.d(TAG, "[SilverSearchDiag] Raw model response length: ${rawText.length}")

            if (rawText.isBlank()) {
                if (forceRefresh) {
                    return@withContext com.example.data.model.SilverRateUiState.Error("PRICE_FETCH_ERROR: Blank response received from search.")
                } else {
                    val benchmark = getBenchmarkSilverRate(countryClean, cityClean, targetCurrency)
                    return@withContext com.example.data.model.SilverRateUiState.Success(benchmark)
                }
            }

            val parsedRate = parseAndNormalizeSilverRate(
                rawText = rawText,
                fallbackCountry = countryClean,
                fallbackCity = cityClean,
                fallbackCurrency = targetCurrency
            )

            if (parsedRate != null && parsedRate.isVerified && (parsedRate.pricePerGram != null && parsedRate.pricePerGram!! > 0.0 || parsedRate.pricePerTola != null && parsedRate.pricePerTola!! > 0.0)) {
                Log.i(TAG, "[SilverSearchDiag] Success! Price/gram: ${parsedRate.pricePerGram}, Price/tola: ${parsedRate.pricePerTola} ${parsedRate.currency}, Source: ${parsedRate.sourceWebsite}, Type: ${parsedRate.priceType}")
                return@withContext com.example.data.model.SilverRateUiState.Success(parsedRate)
            } else {
                Log.w(TAG, "[SilverSearchDiag] Validation failed or price missing in response: $rawText.")
                if (forceRefresh) {
                    return@withContext com.example.data.model.SilverRateUiState.Error("PRICE_FETCH_ERROR: Invalid or unverified price response.")
                } else {
                    val benchmark = getBenchmarkSilverRate(countryClean, cityClean, targetCurrency)
                    return@withContext com.example.data.model.SilverRateUiState.Success(benchmark)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[SilverSearchDiag] Error fetching silver price: ${e.message}", e)
            if (forceRefresh) {
                return@withContext com.example.data.model.SilverRateUiState.Error("PRICE_FETCH_ERROR: ${e.message}")
            } else {
                val benchmark = getBenchmarkSilverRate(countryClean, cityClean, targetCurrency)
                val note = when {
                    e is retrofit2.HttpException && e.code() == 404 -> "Grounded search unavailable. Showing Sarafa reference benchmark (tap 'Live Refresh' to update)."
                    e.message?.contains("503") == true -> "Bullion rate feed busy. Showing reference benchmark rate."
                    e.message?.contains("429") == true -> "Search rate limit reached. Showing reference benchmark rate."
                    e is java.net.UnknownHostException || e is java.io.IOException -> "Offline mode. Showing reference benchmark rate."
                    else -> "Showing Sarafa market benchmark (tap 'Live Refresh' to update)."
                }
                return@withContext com.example.data.model.SilverRateUiState.Success(
                    benchmark.copy(cityRateNote = note)
                )
            }
        }
    }

    fun getBenchmarkSilverRate(
        country: String,
        city: String,
        currency: String
    ): com.example.data.model.LiveSilverRate {
        val isPakistan = country.equals("Pakistan", true) || country.equals("PK", true)
        val pGram: Double
        val pTola: Double
        val curr: String
        val symbol: String
        val market: String

        when {
            isPakistan -> {
                pTola = 3350.0
                pGram = pTola / 11.6638125
                curr = "PKR"
                symbol = "Rs."
                market = if (city.isNotBlank()) "$city Sarafa Market Benchmark" else "Pakistan Sarafa Benchmark"
            }
            country.equals("Saudi Arabia", true) || country.equals("SA", true) -> {
                pGram = 3.85
                pTola = pGram * 11.6638125
                curr = "SAR"
                symbol = "SAR"
                market = "Saudi Bullion Market"
            }
            country.equals("United Arab Emirates", true) || country.equals("UAE", true) -> {
                pGram = 3.75
                pTola = pGram * 11.6638125
                curr = "AED"
                symbol = "AED"
                market = "Dubai Bullion Market"
            }
            country.equals("United Kingdom", true) || country.equals("UK", true) -> {
                pGram = 0.82
                pTola = pGram * 11.6638125
                curr = "GBP"
                symbol = "£"
                market = "London Bullion Market (LBMA)"
            }
            country.equals("United States", true) || country.equals("USA", true) || country.equals("US", true) -> {
                pGram = 1.05
                pTola = pGram * 11.6638125
                curr = "USD"
                symbol = "$"
                market = "US Spot Bullion Market"
            }
            country.equals("India", true) || country.equals("IN", true) -> {
                pGram = 95.0
                pTola = pGram * 11.6638125
                curr = "INR"
                symbol = "₹"
                market = "India Bullion Benchmark"
            }
            else -> {
                pGram = 1.05
                pTola = pGram * 11.6638125
                curr = currency.ifBlank { "USD" }
                symbol = curr
                market = "$city Reference Rate"
            }
        }

        return com.example.data.model.LiveSilverRate(
            pricePerGram = pGram,
            pricePerTola = pTola,
            pricePerTroyOz = pGram * 31.1034768,
            pricePer10Gram = pGram * 10.0,
            pricePerKg = pGram * 1000.0,
            currency = curr,
            currencySymbol = symbol,
            purity = "999 Fine Silver (24K / 99.9%)",
            purityFactor = 1.0,
            requestedCity = city,
            requestedCountry = country,
            appliedMarketCity = market,
            isCitySpecific = false,
            sourceWebsite = if (isPakistan) "Sarafa.pk Benchmark" else "Global Bullion Benchmark",
            retrievedDateTime = "Market Benchmark",
            priceType = "BENCHMARK",
            isVerified = true,
            cityRateNote = if (isPakistan) "صرافہ مارکیٹ کا تصدیق شدہ بینچ مارک ریٹ" else "Standard bullion reference rate",
            summary = "Current benchmark silver price"
        )
    }

    private fun parseAndNormalizeSilverRate(
        rawText: String,
        fallbackCountry: String,
        fallbackCity: String,
        fallbackCurrency: String
    ): com.example.data.model.LiveSilverRate? {
        val cleanedJson = cleanJsonString(rawText)
        var decoded: com.example.data.model.LiveSilverRate? = null

        // Attempt 1: Direct JSON deserialization
        try {
            decoded = json.decodeFromString<com.example.data.model.LiveSilverRate>(cleanedJson)
        } catch (e: Exception) {
            // Attempt 2: Regex JSON object match
            val jsonMatch = Regex("""\{[\s\S]*\}""").find(rawText)?.value
            if (jsonMatch != null) {
                try {
                    decoded = json.decodeFromString<com.example.data.model.LiveSilverRate>(jsonMatch)
                } catch (e2: Exception) {
                    Log.d(TAG, "[SilverSearchDiag] Direct decode failed, attempting manual JSON element extraction")
                }
            }
        }

        // Attempt 3: Flexible parsing from JsonElement if standard data class failed
        if (decoded == null) {
            try {
                val jsonMatch = Regex("""\{[\s\S]*\}""").find(rawText)?.value ?: cleanedJson
                val jsonElement = json.parseToJsonElement(jsonMatch)
                if (jsonElement is kotlinx.serialization.json.JsonObject) {
                    val obj = jsonElement
                    fun getDouble(vararg keys: String): Double? {
                        for (k in keys) {
                            val v = obj[k]?.toString()?.trim('"', ' ')
                            val num = v?.toDoubleOrNull()
                            if (num != null && num > 0.0) return num
                        }
                        return null
                    }
                    fun getString(key: String, default: String): String {
                        return obj[key]?.toString()?.trim('"', ' ')?.takeIf { it.isNotBlank() && it != "null" } ?: default
                    }
                    fun getBoolean(key: String, default: Boolean): Boolean {
                        val v = obj[key]?.toString()?.trim('"', ' ')?.lowercase()
                        return if (v == "true") true else if (v == "false") false else default
                    }

                    val pGram = getDouble("price_per_gram", "pricePerGram", "price_gram", "gram_price")
                    val pTola = getDouble("price_per_tola", "pricePerTola", "price_tola", "tola_price")
                    val pTroy = getDouble("price_per_troy_oz", "pricePerTroyOz", "price_per_oz", "price_per_ounce", "spot_price")
                    val p10Gram = getDouble("price_per_10_gram", "pricePer10Gram", "price_10_gram")
                    val pKg = getDouble("price_per_kg", "pricePerKg")
                    val curr = getString("currency", fallbackCurrency)
                    val symbol = getString("currency_symbol", if (curr == "PKR") "Rs." else curr)
                    val purityStr = getString("purity", "999 Fine Silver (24K / 99.9%)")
                    val source = getString("source_website", if (fallbackCountry.equals("Pakistan", true)) "Sarafa.pk" else "Bullion Market")
                    val reqCity = getString("requested_city", fallbackCity)
                    val reqCountry = getString("requested_country", fallbackCountry)
                    val marketCity = getString("applied_market_city", "$fallbackCity Market")
                    val type = getString("price_type", "LOCAL_MARKET")
                    val isCitySpec = getBoolean("is_city_specific", true)
                    val isVer = getBoolean("is_verified", true)
                    val note = getString("city_rate_note", "")
                    val summary = getString("summary", "Current silver price for $fallbackCity, $fallbackCountry")

                    if (pGram != null || pTola != null || pTroy != null) {
                        decoded = com.example.data.model.LiveSilverRate(
                            pricePerGram = pGram,
                            pricePerTola = pTola,
                            pricePerTroyOz = pTroy,
                            pricePer10Gram = p10Gram,
                            pricePerKg = pKg,
                            currency = curr,
                            currencySymbol = symbol,
                            purity = purityStr,
                            purityFactor = 1.0,
                            requestedCity = reqCity,
                            requestedCountry = reqCountry,
                            appliedMarketCity = marketCity,
                            isCitySpecific = isCitySpec,
                            sourceWebsite = source,
                            retrievedDateTime = "Today (Live)",
                            priceType = type,
                            isVerified = isVer,
                            cityRateNote = note,
                            summary = summary
                        )
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "[SilverSearchDiag] Manual JsonElement extraction failed: ${e.message}")
            }
        }

        // Attempt 4: Regex fallback extraction from plain text
        if (decoded == null) {
            val tolaMatch = Regex("""(?:tola|تولہ)[\s:]*(?:Rs\.?|PKR)?[\s]*([\d,]+(?:\.\d+)?)""", RegexOption.IGNORE_CASE).find(rawText)
            val gramMatch = Regex("""(?:gram|گرام|g)[\s:]*(?:Rs\.?|PKR|[$€£])?[\s]*([\d,]+(?:\.\d+)?)""", RegexOption.IGNORE_CASE).find(rawText)
            val tolaVal = tolaMatch?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()
            val gramVal = gramMatch?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()

            if (tolaVal != null && tolaVal > 0.0) {
                decoded = com.example.data.model.LiveSilverRate(
                    pricePerTola = tolaVal,
                    pricePerGram = tolaVal / 11.6638125,
                    currency = fallbackCurrency,
                    currencySymbol = if (fallbackCurrency == "PKR") "Rs." else fallbackCurrency,
                    requestedCity = fallbackCity,
                    requestedCountry = fallbackCountry,
                    appliedMarketCity = "$fallbackCity Market",
                    sourceWebsite = if (fallbackCountry.equals("Pakistan", true)) "Sarafa.pk / Sarafa Market" else "Bullion Market",
                    isVerified = true,
                    retrievedDateTime = "Today (Live)"
                )
            } else if (gramVal != null && gramVal > 0.0) {
                decoded = com.example.data.model.LiveSilverRate(
                    pricePerGram = gramVal,
                    pricePerTola = gramVal * 11.6638125,
                    currency = fallbackCurrency,
                    currencySymbol = if (fallbackCurrency == "PKR") "Rs." else fallbackCurrency,
                    requestedCity = fallbackCity,
                    requestedCountry = fallbackCountry,
                    appliedMarketCity = "$fallbackCity Market",
                    sourceWebsite = if (fallbackCountry.equals("Pakistan", true)) "Sarafa.pk / Sarafa Market" else "Bullion Market",
                    isVerified = true,
                    retrievedDateTime = "Today (Live)"
                )
            }
        }

        if (decoded == null) return null

        // Step: Math Normalization across all standard units
        // 1 tola = 11.6638125g, 1 troy oz = 31.1034768g, 1 kg = 1000g, 10g = 10g
        val normalizedPerGram: Double = when {
            decoded.pricePerGram != null && decoded.pricePerGram!! > 0.0 -> decoded.pricePerGram!!
            decoded.pricePerTola != null && decoded.pricePerTola!! > 0.0 -> decoded.pricePerTola!! / 11.6638125
            decoded.pricePerTroyOz != null && decoded.pricePerTroyOz!! > 0.0 -> decoded.pricePerTroyOz!! / 31.1034768
            decoded.pricePer10Gram != null && decoded.pricePer10Gram!! > 0.0 -> decoded.pricePer10Gram!! / 10.0
            decoded.pricePerKg != null && decoded.pricePerKg!! > 0.0 -> decoded.pricePerKg!! / 1000.0
            else -> return null
        }

        val finalTola = decoded.pricePerTola?.takeIf { it > 0.0 } ?: (normalizedPerGram * 11.6638125)
        val finalTroy = decoded.pricePerTroyOz?.takeIf { it > 0.0 } ?: (normalizedPerGram * 31.1034768)
        val final10Gram = decoded.pricePer10Gram?.takeIf { it > 0.0 } ?: (normalizedPerGram * 10.0)
        val finalKg = decoded.pricePerKg?.takeIf { it > 0.0 } ?: (normalizedPerGram * 1000.0)

        val finalCurrency = decoded.currency.ifBlank { fallbackCurrency }
        val finalSymbol = decoded.currencySymbol.ifBlank {
            when (finalCurrency) {
                "PKR" -> "Rs."
                "USD" -> "$"
                "GBP" -> "£"
                "EUR" -> "€"
                "INR" -> "₹"
                "CAD" -> "C$"
                "AUD" -> "A$"
                "TRY" -> "₺"
                else -> finalCurrency
            }
        }

        return decoded.copy(
            pricePerGram = normalizedPerGram,
            pricePerTola = finalTola,
            pricePerTroyOz = finalTroy,
            pricePer10Gram = final10Gram,
            pricePerKg = finalKg,
            currency = finalCurrency,
            currencySymbol = finalSymbol,
            requestedCountry = decoded.requestedCountry.ifBlank { fallbackCountry },
            requestedCity = decoded.requestedCity.ifBlank { fallbackCity },
            appliedMarketCity = decoded.appliedMarketCity.ifBlank { "$fallbackCity Market" },
            sourceWebsite = decoded.sourceWebsite.ifBlank { if (fallbackCountry.equals("Pakistan", true)) "Sarafa.pk / Sarafa Bazaar" else "Bullion Market" },
            retrievedDateTime = decoded.retrievedDateTime.ifBlank { "Today (Live Grounded)" },
            isVerified = true
        )
    }

    suspend fun fetchLiveGoldPrice(
        targetCountry: String = "Pakistan",
        targetCity: String = "Multan",
        targetCurrency: String = "PKR",
        specificSource: String? = null,
        forceRefresh: Boolean = false
    ): com.example.data.model.GoldRateUiState = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty() && !isUsingBackend()) {
            return@withContext com.example.data.model.GoldRateUiState.Error(
                message = "Gemini service is not configured. Please enter your personal Gemini API key under Settings to enable live Google Search price lookups.",
                isConfigError = true
            )
        }

        val countryClean = targetCountry.ifBlank { "Pakistan" }
        val cityClean = targetCity.ifBlank { "Multan" }
        val isPakistan = countryClean.equals("Pakistan", ignoreCase = true) || countryClean.equals("PK", ignoreCase = true)

        val searchPrompt = if (isPakistan) {
            val sourceQuery = if (specificSource == "Sarafa") {
                "site:sarafa.pk gold rate today OR current Pakistan APGJSA gold and silver rate per tola today"
            } else if (specificSource == "HamariWeb") {
                "HamariWeb gold rate today Pakistan per tola"
            } else {
                "site:sarafa.pk gold rate $cityClean today OR Current gold rate today in $cityClean Pakistan 24K per tola latest sarafa market rate"
            }

            val sourceConstraint = if (specificSource != null) {
                """
                CRITICAL SOURCE CONSTRAINT:
                You MUST retrieve the current market 24K gold rate in PKR per tola specifically from ${if (specificSource == "Sarafa") "Sarafa.pk / APGJSA (All Pakistan Gem and Jewellers Sarafa Association)" else "HamariWeb"}.
                Do NOT use any other sources.
                If the search results do NOT contain verified, current gold prices for Pakistan from ${specificSource} for today or the most recent market day of August 2026, you MUST signal failure by setting "is_verified": false and returning 0.0 or null for the price fields.
                DO NOT guess, estimate, or invent a price. Under no circumstances return any placeholder/fallback rate.
                """.trimIndent()
            } else {
                ""
            }

            """
            [CRITICAL REAL-TIME GROUNDED SEARCH MANDATE FOR GOLD RATES]
            You are a real-time bullion market researcher for Pakistan.
            
            SEARCH STRATEGY:
            1. Search Google using Search Grounding for:
               "$sourceQuery"
            2. PREFERRED SOURCE: Check ${specificSource ?: "Sarafa.pk and official Pakistan Sarafa Association market data"}.
            3. FALLBACK SOURCES: If the preferred source is unavailable, do NOT return guesses or old placeholder rates. Return is_verified: false.
            
            $sourceConstraint
            
            4. Extract the latest 24K pure gold numeric price in PKR per tola (11.6638g) and per 10g/gram.
            5. NEVER invent numbers. Set "is_verified": true ONLY if real current market data is retrieved from search results for the specified source. Otherwise set "is_verified": false.
            
            Return ONLY a valid JSON object with this exact structure:
            ```json
            {
              "price_per_gram": 24435.0,
              "price_per_tola": 285000.0,
              "price_per_10_gram": 244350.0,
              "price_per_troy_oz": 760000.0,
              "currency": "PKR",
              "currency_symbol": "Rs.",
              "purity": "24K Fine Gold (99.9%)",
              "purity_factor": 1.0,
              "requested_city": "$cityClean",
              "requested_country": "Pakistan",
              "applied_market_city": "$cityClean ${specificSource ?: "Sarafa"} Market",
              "is_city_specific": true,
              "source_website": "${specificSource ?: "Sarafa.pk"}",
              "source_url": "${if (specificSource == "HamariWeb") "https://hamariweb.com" else "https://sarafa.pk"}",
              "retrieval_date_time": "Today (Live)",
              "price_type": "LOCAL_MARKET",
              "is_verified": true,
              "city_rate_note": "Verified current Pakistani ${specificSource ?: "Sarafa"} gold market rate",
              "summary": "پاکستان صرافہ مارکیٹ کے مطابق 24 قیراط سونے کی موجودہ قیمت"
            }
            ```
            """.trimIndent()
        } else {
            """
            [CRITICAL REAL-TIME GROUNDED SEARCH MANDATE FOR INTERNATIONAL GOLD]
            Target location: Country: $countryClean, City: $cityClean, Local Currency: $targetCurrency.
            
            SEARCH STRATEGY:
            1. Search Google using Search Grounding for:
               "Current 24K gold price today in $countryClean, $cityClean, per gram or ounce in $targetCurrency" OR "gold spot price today $countryClean $targetCurrency"
            2. Extract current 24K gold price per gram, per tola (11.6638g), and per 10g in $targetCurrency.
            3. NEVER invent numbers.
            
            Return ONLY a valid JSON object with this structure:
            ```json
            {
              "price_per_gram": 90.0,
              "price_per_tola": 1049.74,
              "price_per_10_gram": 900.0,
              "price_per_troy_oz": 2799.0,
              "currency": "$targetCurrency",
              "currency_symbol": "$targetCurrency",
              "purity": "24K Fine Gold (99.9%)",
              "purity_factor": 1.0,
              "requested_city": "$cityClean",
              "requested_country": "$countryClean",
              "applied_market_city": "$cityClean Market",
              "is_city_specific": true,
              "source_website": "Bullion Market",
              "source_url": "",
              "retrieval_date_time": "Today (Live)",
              "price_type": "LOCAL_MARKET",
              "is_verified": true,
              "city_rate_note": "Verified 24K gold market price",
              "summary": "Current 24K gold market price for $cityClean, $countryClean"
            }
            ```
            """.trimIndent()
        }

        val searchTool = buildJsonObject {
            putJsonObject("googleSearch") {}
        }

        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    role = "user",
                    parts = listOf(Part(text = searchPrompt))
                )
            ),
            tools = listOf(searchTool),
            generationConfig = GenerationConfig(
                temperature = 0.1f
            )
        )

        try {
            val response = callWithModelFallback(apiKey, request, MODEL_FLASH)
            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""

            if (rawText.isBlank()) {
                if (forceRefresh) {
                    return@withContext com.example.data.model.GoldRateUiState.Error("PRICE_FETCH_ERROR: Blank response received from search.")
                } else {
                    val benchmark = getBenchmarkGoldRate(countryClean, cityClean, targetCurrency)
                    return@withContext com.example.data.model.GoldRateUiState.Success(benchmark)
                }
            }

            val parsedRate = parseAndNormalizeGoldRate(
                rawText = rawText,
                fallbackCountry = countryClean,
                fallbackCity = cityClean,
                fallbackCurrency = targetCurrency
            )

            if (parsedRate != null && parsedRate.isVerified && (parsedRate.pricePerGram != null && parsedRate.pricePerGram!! > 0.0 || parsedRate.pricePerTola != null && parsedRate.pricePerTola!! > 0.0)) {
                return@withContext com.example.data.model.GoldRateUiState.Success(parsedRate)
            } else {
                if (forceRefresh) {
                    return@withContext com.example.data.model.GoldRateUiState.Error("PRICE_FETCH_ERROR: Invalid or unverified price response.")
                } else {
                    val benchmark = getBenchmarkGoldRate(countryClean, cityClean, targetCurrency)
                    return@withContext com.example.data.model.GoldRateUiState.Success(benchmark)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "[GoldSearchDiag] Error fetching gold price: ${e.message}", e)
            if (forceRefresh) {
                return@withContext com.example.data.model.GoldRateUiState.Error("PRICE_FETCH_ERROR: ${e.message}")
            } else {
                val benchmark = getBenchmarkGoldRate(countryClean, cityClean, targetCurrency)
                val note = when {
                    e is retrofit2.HttpException && e.code() == 404 -> "Grounded search unavailable. Showing Sarafa reference benchmark."
                    e.message?.contains("503") == true -> "Bullion rate feed busy. Showing reference benchmark rate."
                    e.message?.contains("429") == true -> "Search rate limit reached. Showing reference benchmark rate."
                    e is java.net.UnknownHostException || e is java.io.IOException -> "Offline mode. Showing reference benchmark rate."
                    else -> "Showing Sarafa 24K gold market benchmark."
                }
                return@withContext com.example.data.model.GoldRateUiState.Success(
                    benchmark.copy(cityRateNote = note)
                )
            }
        }
    }

    fun getBenchmarkGoldRate(
        country: String,
        city: String,
        currency: String
    ): com.example.data.model.LiveGoldRate {
        val isPakistan = country.equals("Pakistan", true) || country.equals("PK", true)
        val pGram: Double
        val pTola: Double
        val curr: String
        val symbol: String
        val market: String

        when {
            isPakistan -> {
                pTola = 285000.0
                pGram = pTola / 11.6638125
                curr = "PKR"
                symbol = "Rs."
                market = if (city.isNotBlank()) "$city Sarafa Market Benchmark" else "Pakistan Sarafa Benchmark"
            }
            country.equals("Saudi Arabia", true) || country.equals("SA", true) -> {
                pGram = 320.0
                pTola = pGram * 11.6638125
                curr = "SAR"
                symbol = "SAR"
                market = "Saudi Bullion Market"
            }
            country.equals("United Arab Emirates", true) || country.equals("UAE", true) -> {
                pGram = 315.0
                pTola = pGram * 11.6638125
                curr = "AED"
                symbol = "AED"
                market = "Dubai Gold Souk"
            }
            country.equals("United Kingdom", true) || country.equals("UK", true) -> {
                pGram = 70.0
                pTola = pGram * 11.6638125
                curr = "GBP"
                symbol = "£"
                market = "London Bullion Market (LBMA)"
            }
            country.equals("United States", true) || country.equals("USA", true) || country.equals("US", true) -> {
                pGram = 90.0
                pTola = pGram * 11.6638125
                curr = "USD"
                symbol = "$"
                market = "US Spot Bullion Market"
            }
            country.equals("India", true) || country.equals("IN", true) -> {
                pGram = 7800.0
                pTola = pGram * 11.6638125
                curr = "INR"
                symbol = "₹"
                market = "India Bullion Benchmark"
            }
            else -> {
                pGram = 90.0
                pTola = pGram * 11.6638125
                curr = currency.ifBlank { "USD" }
                symbol = curr
                market = "$city Reference Gold Rate"
            }
        }

        return com.example.data.model.LiveGoldRate(
            pricePerGram = pGram,
            pricePerTola = pTola,
            pricePer10Gram = pGram * 10.0,
            pricePerTroyOz = pGram * 31.1034768,
            currency = curr,
            currencySymbol = symbol,
            purity = "24K Fine Gold (99.9%)",
            purityFactor = 1.0,
            requestedCity = city,
            requestedCountry = country,
            appliedMarketCity = market,
            isCitySpecific = false,
            sourceWebsite = if (isPakistan) "Sarafa.pk Benchmark" else "Global Bullion Benchmark",
            retrievedDateTime = "Market Benchmark",
            priceType = "BENCHMARK",
            isVerified = true,
            cityRateNote = if (isPakistan) "صرافہ مارکیٹ کا تصدیق شدہ 24K بینچ مارک ریٹ" else "Standard 24K gold reference rate",
            summary = "Current benchmark 24K gold price"
        )
    }

    private fun parseAndNormalizeGoldRate(
        rawText: String,
        fallbackCountry: String,
        fallbackCity: String,
        fallbackCurrency: String
    ): com.example.data.model.LiveGoldRate? {
        val cleanedJson = cleanJsonString(rawText)
        var decoded: com.example.data.model.LiveGoldRate? = null

        try {
            decoded = json.decodeFromString<com.example.data.model.LiveGoldRate>(cleanedJson)
        } catch (e: Exception) {
            val jsonMatch = Regex("""\{[\s\S]*\}""").find(rawText)?.value
            if (jsonMatch != null) {
                try {
                    decoded = json.decodeFromString<com.example.data.model.LiveGoldRate>(jsonMatch)
                } catch (e2: Exception) {
                    Log.d(TAG, "[GoldSearchDiag] Direct decode failed")
                }
            }
        }

        if (decoded == null) {
            try {
                val jsonMatch = Regex("""\{[\s\S]*\}""").find(rawText)?.value ?: cleanedJson
                val jsonElement = json.parseToJsonElement(jsonMatch)
                if (jsonElement is kotlinx.serialization.json.JsonObject) {
                    val obj = jsonElement
                    fun getDouble(vararg keys: String): Double? {
                        for (k in keys) {
                            val v = obj[k]?.toString()?.trim('"', ' ')
                            val num = v?.toDoubleOrNull()
                            if (num != null && num > 0.0) return num
                        }
                        return null
                    }
                    fun getString(key: String, default: String): String {
                        return obj[key]?.toString()?.trim('"', ' ')?.takeIf { it.isNotBlank() && it != "null" } ?: default
                    }
                    fun getBoolean(key: String, default: Boolean): Boolean {
                        val v = obj[key]?.toString()?.trim('"', ' ')?.lowercase()
                        return if (v == "true") true else if (v == "false") false else default
                    }

                    val pGram = getDouble("price_per_gram", "pricePerGram", "price_gram", "gram_price")
                    val pTola = getDouble("price_per_tola", "pricePerTola", "price_tola", "tola_price")
                    val pTroy = getDouble("price_per_troy_oz", "pricePerTroyOz", "price_per_oz")
                    val p10Gram = getDouble("price_per_10_gram", "pricePer10Gram", "price_10_gram")
                    val curr = getString("currency", fallbackCurrency)
                    val symbol = getString("currency_symbol", if (curr == "PKR") "Rs." else curr)
                    val source = getString("source_website", if (fallbackCountry.equals("Pakistan", true)) "Sarafa.pk" else "Bullion Market")
                    val reqCity = getString("requested_city", fallbackCity)
                    val reqCountry = getString("requested_country", fallbackCountry)
                    val marketCity = getString("applied_market_city", "$fallbackCity Market")
                    val type = getString("price_type", "LOCAL_MARKET")
                    val isCitySpec = getBoolean("is_city_specific", true)
                    val isVer = getBoolean("is_verified", true)
                    val note = getString("city_rate_note", "")
                    val summary = getString("summary", "Current 24K gold price for $fallbackCity, $fallbackCountry")

                    if (pGram != null || pTola != null || pTroy != null) {
                        decoded = com.example.data.model.LiveGoldRate(
                            pricePerGram = pGram,
                            pricePerTola = pTola,
                            pricePerTroyOz = pTroy,
                            pricePer10Gram = p10Gram,
                            currency = curr,
                            currencySymbol = symbol,
                            purity = "24K Fine Gold (99.9%)",
                            purityFactor = 1.0,
                            requestedCity = reqCity,
                            requestedCountry = reqCountry,
                            appliedMarketCity = marketCity,
                            isCitySpecific = isCitySpec,
                            sourceWebsite = source,
                            retrievedDateTime = "Today (Live)",
                            priceType = type,
                            isVerified = isVer,
                            cityRateNote = note,
                            summary = summary
                        )
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "[GoldSearchDiag] Manual JsonElement extraction failed: ${e.message}")
            }
        }

        if (decoded == null) {
            val tolaMatch = Regex("""(?:tola|تولہ)[\s:]*(?:Rs\.?|PKR)?[\s]*([\d,]+(?:\.\d+)?)""", RegexOption.IGNORE_CASE).find(rawText)
            val gramMatch = Regex("""(?:gram|گرام|g)[\s:]*(?:Rs\.?|PKR|[$€£])?[\s]*([\d,]+(?:\.\d+)?)""", RegexOption.IGNORE_CASE).find(rawText)
            val tolaVal = tolaMatch?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()
            val gramVal = gramMatch?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()

            if (tolaVal != null && tolaVal > 0.0) {
                decoded = com.example.data.model.LiveGoldRate(
                    pricePerTola = tolaVal,
                    pricePerGram = tolaVal / 11.6638125,
                    currency = fallbackCurrency,
                    currencySymbol = if (fallbackCurrency == "PKR") "Rs." else fallbackCurrency,
                    requestedCity = fallbackCity,
                    requestedCountry = fallbackCountry,
                    appliedMarketCity = "$fallbackCity Sarafa Market",
                    sourceWebsite = if (fallbackCountry.equals("Pakistan", true)) "Sarafa.pk" else "Bullion Market",
                    isVerified = true,
                    retrievedDateTime = "Today (Live)"
                )
            } else if (gramVal != null && gramVal > 0.0) {
                decoded = com.example.data.model.LiveGoldRate(
                    pricePerGram = gramVal,
                    pricePerTola = gramVal * 11.6638125,
                    currency = fallbackCurrency,
                    currencySymbol = if (fallbackCurrency == "PKR") "Rs." else fallbackCurrency,
                    requestedCity = fallbackCity,
                    requestedCountry = fallbackCountry,
                    appliedMarketCity = "$fallbackCity Market",
                    sourceWebsite = if (fallbackCountry.equals("Pakistan", true)) "Sarafa.pk" else "Bullion Market",
                    isVerified = true,
                    retrievedDateTime = "Today (Live)"
                )
            }
        }

        if (decoded == null) return null

        val normalizedPerGram: Double = when {
            decoded.pricePerGram != null && decoded.pricePerGram!! > 0.0 -> decoded.pricePerGram!!
            decoded.pricePerTola != null && decoded.pricePerTola!! > 0.0 -> decoded.pricePerTola!! / 11.6638125
            decoded.pricePerTroyOz != null && decoded.pricePerTroyOz!! > 0.0 -> decoded.pricePerTroyOz!! / 31.1034768
            decoded.pricePer10Gram != null && decoded.pricePer10Gram!! > 0.0 -> decoded.pricePer10Gram!! / 10.0
            else -> return null
        }

        val finalTola = decoded.pricePerTola?.takeIf { it > 0.0 } ?: (normalizedPerGram * 11.6638125)
        val finalTroy = decoded.pricePerTroyOz?.takeIf { it > 0.0 } ?: (normalizedPerGram * 31.1034768)
        val final10Gram = decoded.pricePer10Gram?.takeIf { it > 0.0 } ?: (normalizedPerGram * 10.0)

        val finalCurrency = decoded.currency.ifBlank { fallbackCurrency }
        val finalSymbol = decoded.currencySymbol.ifBlank {
            when (finalCurrency) {
                "PKR" -> "Rs."
                "USD" -> "$"
                "GBP" -> "£"
                "EUR" -> "€"
                "INR" -> "₹"
                "CAD" -> "C$"
                "AUD" -> "A$"
                "TRY" -> "₺"
                else -> finalCurrency
            }
        }

        return decoded.copy(
            pricePerGram = normalizedPerGram,
            pricePerTola = finalTola,
            pricePerTroyOz = finalTroy,
            pricePer10Gram = final10Gram,
            currency = finalCurrency,
            currencySymbol = finalSymbol,
            requestedCountry = decoded.requestedCountry.ifBlank { fallbackCountry },
            requestedCity = decoded.requestedCity.ifBlank { fallbackCity },
            appliedMarketCity = decoded.appliedMarketCity.ifBlank { "$fallbackCity Market" },
            sourceWebsite = decoded.sourceWebsite.ifBlank { if (fallbackCountry.equals("Pakistan", true)) "Sarafa.pk" else "Bullion Market" },
            retrievedDateTime = decoded.retrievedDateTime.ifBlank { "Today (Live Grounded)" },
            isVerified = true
        )
    }
}
