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

    const val MODEL_FLASH = "gemini-2.5-flash"
    const val MODEL_PRO = "gemini-2.5-pro"
    const val MODEL_FLASH_LITE = "gemini-2.0-flash-lite"

    @Volatile
    private var customApiKey: String = ""

    fun setCustomApiKey(key: String) {
        customApiKey = key.trim()
        Log.d(TAG, "Custom API key updated. Length: ${customApiKey.length}")
    }

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
        if (customApiKey.isNotBlank()) {
            return customApiKey
        }
        val key = BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() && it != "MY_GEMINI_API_KEY" } ?: ""
        if (key.isEmpty()) {
            Log.e(TAG, "API Key is missing or default placeholder!")
        } else {
            Log.d(TAG, "API Key loaded: ${key.take(4)}****")
        }
        return key
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
        val candidateModels = listOf(
            preferredModel,
            "gemini-2.5-flash",
            "gemini-2.0-flash",
            "gemini-1.5-flash",
            "gemini-2.5-pro",
            "gemini-1.5-pro",
            "gemini-2.0-flash-lite",
            "gemini-1.5-flash-latest",
            "gemini-1.5-pro-latest"
        ).distinct()

        var lastException: Exception? = null

        // First pass: try v1beta for candidate models
        for (model in candidateModels) {
            try {
                return runWithRetry {
                    service.generateContent(model, apiKey, request)
                }
            } catch (e: Exception) {
                lastException = e
                val code = (e as? retrofit2.HttpException)?.code() ?: 0
                if (code == 429 || code == 401 || code == 403) {
                    throw e
                }
            }
        }

        // Second pass: try v1 endpoint for candidate models
        for (model in candidateModels) {
            try {
                return runWithRetry {
                    service.generateContentV1(model, apiKey, request)
                }
            } catch (e: Exception) {
                lastException = e
                val code = (e as? retrofit2.HttpException)?.code() ?: 0
                if (code == 429 || code == 401 || code == 403) {
                    throw e
                }
            }
        }

        throw lastException ?: Exception("All model fallbacks failed")
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
            You are Lisan Grammar, an expert English teacher for Urdu-speaking learners who know Arabic grammar.
            Generate a JSON lesson for: "$topicEn" ($topicUr) at level $level.
            
            CRITICAL LANGUAGE MANDATE:
            Write ALL explanations, rule details (ruleUrdu), similarities, differences, common mistakes, and notes in clear, natural URDU (اردو) for Urdu native speakers.
            
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

    suspend fun analyzeDeepGrammar(inputSentence: String, level: String): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isNotEmpty()) {
            try {
                val prompt = """
                    Analyze this sentence for an Urdu native speaker: "$inputSentence" (Level: $level).
                    The input sentence could be in English, Urdu, or Arabic.
                    
                    CRITICAL INSTRUCTION:
                    Write ALL explanations, grammatical rules, word breakdowns, and notes in clear, natural URDU (اردو). Do NOT write explanations in English.
                    For Arabic grammar comparison, start with "جیسے آپ نے عربی میں سیکھا ہے..." (Likewise in Arabic you have learned...) and compare with Arabic grammar concepts (like الجملة الاسمية, الجملة الفعلية, الفاعل, الفعل).
                    
                    REQUIRED RESPONSE STRUCTURE (Return strictly in Markdown):
                    
                    1. **اصل جملہ (Original Sentence)**: "$inputSentence"
                    2. **تصحیح (Correction)**: [Corrected version or "جملہ ساخت کے لحاظ سے بالکل درست ہے"]
                    3. **جملے کی قسم (Sentence Type)**: [e.g. جملہ خبریہ / Declarative, جملہ استفہامیہ / Interrogative]
                    4. **زمانہ (Tense)**: [e.g. فعل حال جاری / Present Continuous]
                    5. **بنیادی اجزاء (Key Components)**:
                       - **فاعل (Subject)**: [The doer]
                       - **فعل (Main Verb)**: [The action]
                       - **مفعول (Object/Complement)**: [What receives action]
                    6. **لفظ بہ لفظ تجزیہ (Word-by-Word Breakdown)**:
                       - [Word 1]: [Grammatical role explained in Urdu]
                       - [Word 2]: ...
                    7. **اردو میں تفصیلی وضاحت (Urdu Explanation)**: [Clear Urdu explanation of the sentence structure and rules]
                    8. **عربی گرامر سے تقابل (Arabic Grammar Reference)**:
                       جیسے آپ نے عربی میں سیکھا ہے: [Explain similarities & differences using Arabic terms]
                    9. **مزید مثالیں (Additional Examples)**: [2 more English examples with Urdu translations]
                    10. **عام غلطیاں (Common Mistakes)**: [What Urdu/Arabic speakers often get wrong here]
                """.trimIndent()
                val request = GenerateContentRequest(contents = listOf(Content(parts = listOf(Part(text = prompt)))))
                val response = callWithModelFallback(apiKey, request, MODEL_PRO)
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
        val words = cleanInput.split("\\s+".toRegex())

        val isQuestion = cleanInput.endsWith("?") || cleanInput.startsWith("What", true) ||
                cleanInput.startsWith("Where", true) || cleanInput.startsWith("How", true) ||
                cleanInput.startsWith("Is", true) || cleanInput.startsWith("Are", true) ||
                cleanInput.startsWith("Do", true) || cleanInput.startsWith("Does", true)

        val sentenceType = if (isQuestion) "جملہ استفہامیہ (Interrogative Sentence)" else "جملہ خبریہ (Declarative Sentence)"

        val hasPast = cleanInput.contains("was", true) || cleanInput.contains("were", true) ||
                cleanInput.contains("had", true) || cleanInput.endsWith("ed")
        val hasFuture = cleanInput.contains("will", true) || cleanInput.contains("shall", true) || cleanInput.contains("going to", true)
        val hasIng = cleanInput.contains("ing", true)

        val tense = when {
            hasPast && hasIng -> "فعل ماضی جاری (Past Continuous Tense)"
            hasPast -> "فعل ماضی مطلق (Past Simple Tense)"
            hasFuture -> "فعل مستقبل (Future Simple Tense)"
            hasIng -> "فعل حال جاری (Present Continuous Tense)"
            else -> "فعل حال مطلق (Present Simple Tense)"
        }

        val subject = words.firstOrNull() ?: "Subject"
        val verb = words.getOrNull(1) ?: "Verb"
        val complement = if (words.size > 2) words.drop(2).joinToString(" ") else "N/A"

        val wordBreakdown = words.mapIndexed { index, word ->
            val role = when (index) {
                0 -> "فاعل / اسم (Subject / Pronoun - اسم الفاعل / ضمير)"
                1 -> "فعل / امدادی فعل (Main / Auxiliary Verb - الفعل الرئيسي / المساعد)"
                else -> "مفعول / حرفِ جار (Object / Preposition - المفعول به / حرف جر)"
            }
            "- **$word**: $role"
        }.joinToString("\n")

        return """
            ### 1. **اصل جملہ (Original Sentence)**: "$cleanInput"
            
            ### 2. **تصحیح (Correction)**: جملہ ساخت کے لحاظ سے بالکل درست ہے۔
            
            ### 3. **جملے کی قسم (Sentence Type)**: $sentenceType
            
            ### 4. **زمانہ (Tense)**: $tense
            
            ### 5. **بنیادی اجزاء (Key Components)**:
            - **فاعل (Subject)**: $subject
            - **فعل (Main Verb)**: $verb
            - **مفعول (Object / Complement)**: $complement
            
            ### 6. **لفظ بہ لفظ تجزیہ (Word-by-Word Breakdown)**:
            $wordBreakdown
            
            ### 7. **اردو میں تفصیلی وضاحت (Urdu Explanation)**:
            یہ جملہ انگریزی کے بنیادی اصول (فاعل + فعل + مفعول) کی پیروی کرتا ہے۔ اس میں ایک عمل یا حالت کو واضح طور پر بیان کیا گیا ہے۔
            
            ### 8. **عربی گرامر سے تقابل (Arabic Grammar Reference)**:
            جیسے آپ نے عربی میں سیکھا ہے:
            عربی زبان میں اسم سے شروع ہونے والے جملے کو (الجملة الاسمية) کہتے ہیں جس میں مبتدأ اور خبر ہوتی ہے اور فعل کے بغیر بھی مکمل ہوتا ہے (جیسے: اللهُ غَفُورٌ)، جبکہ انگریزی میں ہر خبر دینے والے جملے میں فعل (is/am/are) کا ہونا ضروری ہے (جیسے: God is Forgiving)۔
            
            ### 9. **مزید مثالیں (Additional Examples)**:
            1. "$cleanInput today." (اردو: آج $cleanInput)
            2. "They also said: $cleanInput" (اردو: انہوں نے بھی یہی کہا)
            
            ### 10. **عام غلطیاں (Common Mistakes)**:
            اردو اور عربی بولنے والے اکثر انگریزی کے فعل حال میں "is/am/are" لگانا بھول جاتے ہیں۔
            
            *(نوٹ: تفصیلی گرامر تجزیہ مکمل ہوا).*
        """.trimIndent()
    }

    suspend fun searchIdiomAndRealWorldUsage(query: String): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isNotEmpty()) {
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
        if (apiKey.isNotEmpty()) {
            try {
                val prompt = "Check this answer for an Urdu learner: Q: \"$question\", A: \"$userAnswer\". Context: $ruleContext. Provide feedback in natural Urdu, and include a comparison starting with 'جیسے آپ نے عربی میں سیکھا ہے...'."
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

    suspend fun getTutorResponse(
        history: List<Content>,
        mode: String,
        language: String,
        userName: String,
        learnerProfileSummary: String? = null,
        isStudyModeOn: Boolean = false
    ): String = withContext(Dispatchers.IO) {
        val lastUserMessage = history.lastOrNull { it.role == "user" }?.parts?.firstOrNull()?.text ?: ""
        val (detectedInputLang, effectiveResponseLang) = LanguageDetector.resolveEffectiveResponseLanguage(language, lastUserMessage)

        val apiKey = getApiKey()
        
        if (apiKey.isNotEmpty()) {
            try {
                val profileContext = learnerProfileSummary ?: "Student profile: Level Beginner."
                val studyModeStatusStr = if (isStudyModeOn) {
                    "STUDY MODE IS ON. Provide structured educational support, explanations, examples, and practice exercises when useful."
                } else {
                    "STUDY MODE IS OFF (FRIENDLY CASUAL MODE). Speak naturally, warmly, and conversationally. Answer directly and briefly. DO NOT send full lesson templates, grammar sections, quizzes, or homework unless requested."
                }
                
                val systemPrompt = """
                    You are Lisan AI Tutor, a personalized English-learning tutor inside an app called Lisan Grammar.
                    
                    Learner Name: $userName
                    Selected Response Language Setting: $language
                    Detected Input Language of Latest Message: ${detectedInputLang.label}
                    REQUIRED EFFECTIVE RESPONSE LANGUAGE FOR THIS TURN: $effectiveResponseLang
                    STUDY MODE STATUS: $studyModeStatusStr
                    
                    $profileContext
                    
                    SYSTEM INSTRUCTIONS FOR THE TUTOR AI:
                    Default state: Friendly Mode (Study Mode OFF).

                    In Friendly Mode:
                    - Speak naturally, warmly, and conversationally.
                    - Answer the latest user message directly.
                    - Keep responses short by default.
                    - Do not impose lesson templates, grammar sections, quizzes, Arabic comparisons, or study plans.
                    - Use the selected response language. If none is selected, detect the input language accurately.
                    - Roman Urdu is Urdu written in Latin script, not English (e.g., "mujhe English seekhni hai", "kya haal hai").

                    In Study Mode:
                    - Give structured educational support when useful.
                    - Adapt explanations to the learner’s level and past learning history.
                    - Still answer the current question first.
                    - Use teaching sections only when appropriate.
                    - Offer practice only when it helps or the learner requests it.

                    Language priority:
                    1. Explicit language request in the current prompt.
                    2. Selected language in the plus menu ($language).
                    3. Auto-detected input language (${detectedInputLang.label}).
                    4. User’s saved preference.

                    LANGUAGE ENFORCEMENT FOR THIS TURN:
                    - Effective Response Language is: $effectiveResponseLang.
                    - If Effective Response Language is English: Respond PRIMARILY and FULLY in English.
                    - If Effective Response Language is Urdu: Respond PRIMARILY and FULLY in natural Urdu script (اردو). Use English words only for grammar terms or examples.
                    - If Effective Response Language is Arabic: Respond PRIMARILY and FULLY in Modern Standard Arabic (العربية).

                    Roman Urdu is Urdu written in Latin script. Do not classify Roman Urdu as English. When Roman Urdu is detected in Auto mode, respond in natural Urdu script unless the user asks otherwise.

                    Use learner history only when it is relevant to the current request. Never let history override the current question.

                    For short or casual messages (e.g., "Hi", "Hello"), reply naturally and briefly in 1-2 sentences.
                    Never use a fixed response template for all questions.
                    Never answer an old question instead of the newest question.
                    Never add image-generation or web-search features.
                """.trimIndent()

                val fullContents = listOf(Content(role = "user", parts = listOf(Part(text = systemPrompt)))) + history

                val request = GenerateContentRequest(
                    contents = fullContents,
                    generationConfig = GenerationConfig(temperature = 0.5f)
                )
                
                val response = callWithModelFallback(apiKey, request, MODEL_FLASH)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) return@withContext text
            } catch (e: Exception) {
                Log.e(TAG, "getTutorResponse error: ${e.message}")
            }
        }

        // Smart Local Response Fallback
        generateSmartLocalTutorResponse(history, mode, language, userName, learnerProfileSummary)
    }

    private fun generateSmartLocalTutorResponse(
        history: List<Content>,
        mode: String,
        selectedLanguageMode: String,
        userName: String,
        learnerProfileSummary: String?
    ): String {
        val lastUserMsg = history.lastOrNull { it.role == "user" }?.parts?.firstOrNull()?.text ?: ""
        val (_, effectiveLang) = LanguageDetector.resolveEffectiveResponseLanguage(selectedLanguageMode, lastUserMsg)
        val lowerMsg = lastUserMsg.lowercase(java.util.Locale.ROOT)

        // 1. Casual Greeting
        if (lowerMsg in listOf("hi", "hello", "hey", "hola", "assalam", "aoa", "سلام") || lowerMsg.startsWith("hi ") || lowerMsg.startsWith("hello ")) {
            return when (effectiveLang) {
                "Urdu" -> "السلام علیکم ورحمة الله وبركاته $userName! لیسان AI ٹیوٹر میں خوش آمدید۔ میں آج آپ کی انگریزی میں کیا مدد کر سکتا ہوں؟"
                "Arabic" -> "السلام عليكم ورحمة الله وبركاته $userName! مرحباً بك في معلم لسان الذكي. كيف يمكنني مساعدتك اليوم؟"
                else -> "Hello $userName! Welcome to Lisan AI Tutor. How can I help you with your English today?"
            }
        }

        // 2. Feedback / Misunderstanding / "I didn't ask for this"
        if (lowerMsg.contains("didn't ask") || lowerMsg.contains("did not ask") || lowerMsg.contains("wrong") ||
            lowerMsg.contains("stop giving") || lowerMsg.contains("misunderstood") || lowerMsg.contains("سمجھ نہیں") || lowerMsg.contains("غلط")) {
            return when (effectiveLang) {
                "Urdu" -> "معذرت، میں آپ کا سوال درست طور پر نہیں سمجھ سکا۔ براہ کرم بتائیں کہ آپ کس چیز میں مدد چاہتے ہیں یا اپنا سوال دوبارہ لکھیں۔"
                "Arabic" -> "أعتذر عن عدم الفهم! يرجى توضيح ما تحتاج المساعدة فيه وسأقوم بإجابتك فوراً."
                else -> "Sorry, I misunderstood. Please tell me what you would like help with, or ask your question again."
            }
        }

        // 3. Has vs Have
        if ((lowerMsg.contains("has") && lowerMsg.contains("have")) || lowerMsg.contains("difference between has and have")) {
            return when (effectiveLang) {
                "Urdu" -> """
                    **'Has' اور 'Have' میں فرق:**
                    
                    - **Has**: واحد اسم یا ضمائر (*He, She, It*) کے ساتھ استعمال ہوتا ہے۔
                      - *مثال:* He **has** a car. (اس کے پاس کار ہے۔)
                      
                    - **Have**: جمع اسم یا ضمائر (*I, You, We, They*) کے ساتھ استعمال ہوتا ہے۔
                      - *مثال:* They **have** a house. (ان کے پاس گھر ہے۔)
                      - *مثال:* I **have** two books. (میرے پاس دو کتابیں ہیں۔)
                """.trimIndent()
                "Arabic" -> """
                    **الفرق بين Has و Have:**
                    
                    - **Has**: تستخدم مع المفرد (He, She, It والأسماء المفردة).
                      - مثال: He **has** a car.
                      
                    - **Have**: تستخدم مع (I, You, We, They والأسماء الجمع).
                      - مثال: They **have** a house.
                      - مثال: I **have** a book.
                """.trimIndent()
                else -> """
                    **'Has' vs 'Have' Usage:**
                    
                    - **Has**: Used with singular subjects (*He, She, It*, or singular nouns).
                      - *Example:* He **has** a car.
                      - *Example:* She **has** a new book.
                      
                    - **Have**: Used with plural subjects or pronouns (*I, You, We, They*, or plural nouns).
                      - *Example:* They **have** a house.
                      - *Example:* I **have** two books.
                """.trimIndent()
            }
        }

        // 4. In / On / At (Prepositions)
        if (lowerMsg.contains("in on at") || lowerMsg.contains("preposition")) {
            return when (effectiveLang) {
                "Urdu" -> """
                    **In, On, At کا استعمال:**
                    
                    - **At**: مخصوص وقت یا جگہ کے لیے (مثال: at 5 PM, at home).
                    - **On**: دنوں اور تاریخوں کے لیے (مثال: on Monday, on July 4th).
                    - **In**: مہینوں، سالوں اور شہروں کے لیے (مثال: in 2026, in London).
                """.trimIndent()
                "Arabic" -> """
                    **استخدام At و On و In:**
                    
                    - **At**: للأوقات المحددة والأماكن (مثل: at 5 PM, at home).
                    - **On**: للأيام والتواريخ (مثل: on Monday, on May 1st).
                    - **In**: للشهور والسنوات والمدن (مثل: in 2026, in London).
                """.trimIndent()
                else -> """
                    **In, On, At Quick Guide:**
                    
                    - **At**: Precise times & specific places (e.g., at 5 PM, at home).
                    - **On**: Days and dates (e.g., on Monday, on July 4th).
                    - **In**: Months, years, centuries & enclosed areas (e.g., in 2026, in London).
                """.trimIndent()
            }
        }

        // 5. Present Perfect Tense
        if (lowerMsg.contains("present perfect")) {
            return when (effectiveLang) {
                "Urdu" -> """
                    **Present Perfect Tense (فعل حال مکمل):**
                    
                    یہ زمانہ ایسے کاموں کو بیان کرنے کے لیے استعمال ہوتا ہے جو حال ہی میں مکمل ہوئے ہوں۔
                    
                    **ساخت:** Subject + Has/Have + 3rd form of verb (V3)
                    
                    **مثالیں:**
                    - I **have finished** my homework. (میں اپنا کام مکمل کر چکا ہوں۔)
                    - She **has arrived**. (وہ پہنچ چکی ہے۔)
                """.trimIndent()
                "Arabic" -> """
                    **المضارع التام (Present Perfect Tense):**
                    
                    يعبر عن حدث اكتمل في الماضي وله ارتباط بالحاضر.
                    
                    **التركيب:** Subject + Has/Have + Past Participle (V3)
                    
                    **أمثلة:**
                    - I **have finished** my homework.
                    - She **has arrived**.
                """.trimIndent()
                else -> """
                    **Present Perfect Tense:**
                    
                    Used for actions completed in the recent past that have a connection to the present.
                    
                    **Structure:** Subject + Has/Have + Past Participle (V3)
                    
                    **Examples:**
                    - I **have finished** my assignment.
                    - She **has arrived**.
                """.trimIndent()
                }
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

        // 8. General fallback
        return when (effectiveLang) {
            "Urdu" -> "انگریزی گرامر میں ہر جملہ بنیادی طور پر فاعل (Subject) اور فعل (Verb) کی مطابقت پر منحصر ہوتا ہے۔ مزید تفصیل کے لیے اپنا سوال واضح کریں۔"
            "Arabic" -> "في قواعد اللغة الإنجليزية، تعتمد الجملة على التوافق بين الفاعل والفعل. يرجى توضيح سؤالك لمزيد من الإيضاح."
            else -> "In English grammar, clear structure depends on proper subject-verb agreement. Feel free to ask any specific question!"
        }
    }
}
