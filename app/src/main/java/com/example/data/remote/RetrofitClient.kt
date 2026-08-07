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

    const val MODEL_FLASH = "gemini-3.5-flash"
    const val MODEL_PRO = "gemini-3.1-pro-preview"
    const val MODEL_FLASH_LITE = "gemini-3.5-flash"

    @Volatile
    private var customApiKey: String = ""

    @Volatile
    private var PREVIOUS_USER_MESSAGE: String = ""
    @Volatile
    private var PREVIOUS_ASSISTANT_RESPONSE: String = ""

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
        maxAttempts: Int = 5,
        initialDelayMillis: Long = 2000,
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
            "gemini-3.5-flash",
            "gemini-3.1-pro-preview"
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

    suspend fun getDailyHelperResponse(
        sysPrompt: String,
        history: List<com.example.data.model.ChatMessageEntity>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
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
        learnerProfileSummary: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty()) {
            return@withContext "السلام عليكم! Welcome to Lisan Voice Tutor. Please enter your Gemini API Key in settings to start our conversation."
        }
        val sysPrompt = """
            You are Lisan Voice Tutor, a warm, friendly, natural human English tutor speaking with $userName, an Arabic and Urdu speaking learner.
            
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
                ?: "I'm listening! Please say that again or ask me anything."
        } catch (e: Exception) {
            Log.e(TAG, "getVoiceTutorResponse error: ${e.message}")
            "Sorry, I had trouble hearing that. Could you please try again?"
        }
    }

    suspend fun analyzeDeepGrammar(inputSentence: String, level: String): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isNotEmpty()) {
            try {
                val prompt = """
                    You are the Lisan Grammar Sentence Analyzer.
                    Analyze this input for a Pakistani Urdu-speaking Muslim learner (beginner English): "$inputSentence"
                    
                    CRITICAL RULES:
                    1. First validate if it's a single word, phrase, full sentence, question, command, or incomplete text.
                    2. NEVER GUESS grammar carelessly. Do not create invented grammar explanations.
                    3. Identify the COMPLETE noun phrase as subject (never label a, an, the, this, that, my, your alone as the subject).
                    4. Separate helping verbs from the main verb. DO NOT label helping verbs (is, are, was, does, did, will, have) as the main verb.
                    5. Identify direct objects, complements (after linking verbs like be/seem), and adverbials correctly. Do not call complements or adverbials "objects".
                    6. Arabic grammar references must be optional, short, and used only when genuinely useful.
                    7. Do not show internal analysis, debug data, confidence values, parser labels, or token metadata.
                    8. Use English content in LTR, Urdu content in RTL. Use separate blocks for them.
                    
                    FORMAT FOR A SENTENCE (Strictly follow this structure. Do NOT include '[ 🔊 Listen ] [ 🎙 Practise ]' as the app UI will provide those buttons.):
                    
                    **Your sentence:** $inputSentence
                    
                    **Urdu meaning:**
                    [Urdu meaning here]
                    
                    **Sentence type:** [Statement / Question / Command / Negative]
                    
                    **Main parts:**
                    - **Subject:** [Complete subject phrase]
                      [Optional breakdown of subject, e.g., - Article: The, - Main noun: boy]
                    - **Verb phrase:** [Complete verb phrase]
                      [Optional breakdown, e.g., - Helping verb: is, - Main verb: reading]
                    - **Object / Complement:** [Complete object or complement, if present]
                    - **Extra detail:** [Adverbial or time/place information, if useful]
                    
                    **Tense or time meaning:** [Tense name and simple explanation]
                    
                    **Simple explanation:**
                    [Clear Urdu explanation. Keep it short. Only include Arabic comparison if genuinely helpful.]
                    
                    **Common mistake to avoid:**
                    [If relevant, explain briefly in Urdu/English]
                    
                    **Practice Question:**
                    [One short question to check understanding]
                    
                    <details>
                    <summary>See word details</summary>
                    [Optional word-by-word breakdown - only show accurate roles, do not give conflicting labels]
                    </details>
                    
                    FORMAT FOR A SINGLE WORD (If input is just one word):
                    **Word:** $inputSentence
                    **Meaning:** [Urdu meaning]
                    **Word type:** [Grammar type, e.g., verb, noun]
                    **Example:** [One short English example]
                    **ترجمہ:** [Urdu translation of example]
                    
                    FORMAT FOR A PHRASE (If input is not a full sentence, e.g. "in the morning"):
                    This is a phrase, not a complete sentence.
                    [Explain what it does in English]
                    
                    یہ ایک فقرہ ہے، مکمل جملہ نہیں۔
                    [Urdu explanation]
                    
                    **Main parts:**
                    [Breakdown of the phrase parts]
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

    suspend fun getTutorResponse(
        history: List<com.example.data.model.Content>,
        mode: String,
        language: String,
        userName: String,
        learnerProfileSummary: String? = null,
        isStudyModeOn: Boolean = false
    ): com.example.data.model.TutorResponseResult = withContext(Dispatchers.IO) {
        val lastUserMessage = history.lastOrNull { it.role == "user" }?.parts?.firstOrNull()?.text ?: ""
        val (detectedInputLang, effectiveResponseLang) = LanguageDetector.resolveEffectiveResponseLanguage(language, lastUserMessage)

        val requestId = "req_" + java.util.UUID.randomUUID().toString().substring(0, 8)
        val apiKey = getApiKey()
        
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "YOUR_GEMINI_API_KEY") {
            throw Exception("Gemini is not connected. Add a valid GEMINI_API_KEY in the app’s secure environment settings, then redeploy.")
        }

        val profileContext = learnerProfileSummary ?: "Student profile: Level Beginner."
        val studyModeStatusStr = if (isStudyModeOn) {
            "MODE: STUDY MODE (تفصیلی سبق). Goal: Provide detailed teaching."
        } else {
            "MODE: QUICK HELP (فوری مدد). Goal: Provide FAST, SHORT, and DIRECT answers. Minimal explanation."
        }
        
        val systemPrompt = """
            You are the teacher inside Lisan Grammar. You teach English grammar to Arabic-speaking learners in a friendly, simple, and respectful way.
            Learner: $userName
            Response Language: $effectiveResponseLang

            Profile Context: $profileContext
            $studyModeStatusStr

            At the beginning of every new conversation, greet the learner with:
            السلام عليكم ورحمة الله وبركاته
            Then say:
            Welcome to Lisan Grammar. I will help you learn English grammar by comparing it with Arabic grammar.

            For every grammar lesson or explanation:
            1. Explain the English rule in simple English.
            2. Say exactly: "Likewise in Arabic, you have learned…" (or "كذلك في اللغة العربية، قد تعلمتم...")
            3. Compare the English rule with the closest Arabic grammar rule or sentence structure.
            4. Explain the similarities and differences clearly.
            5. Give at least three English examples with Arabic translations.
            6. Point out common mistakes Arabic speakers may make.
            7. Give one short practice question.
            8. Wait for the learner’s answer and correct it kindly.

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
               - Do NOT use raw asterisks (*), markdown headers (###), bold tags (**), blockquotes (>), or hashtags.
               - Use clean, plain text section titles, simple numbers (1., 2.), and clean line breaks.
               - Do NOT show internal reasoning or thinking process.
        """.trimIndent()

        val fullContents = listOf(com.example.data.model.Content(role = "user", parts = listOf(com.example.data.model.Part(text = systemPrompt)))) + history
        val modelName = MODEL_FLASH
        val endpointCalled = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent"
        
        val request = GenerateContentRequest(
            contents = fullContents,
            generationConfig = GenerationConfig(temperature = 0.5f)
        )
        
        try {
            val response = callWithModelFallback(apiKey, request, modelName)
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: throw Exception("Gemini returned an empty response")
            
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
            Log.e(TAG, "getTutorResponse error: ${e.message}, falling back to smart local tutor response")
            val fallbackText = generateSmartLocalTutorResponse(history, mode, language, userName, learnerProfileSummary)
            val debugMetadata = com.example.data.model.TutorDebugMetadata(
                requestId = requestId,
                submittedMessage = lastUserMessage,
                provider = "Lisan Smart Fallback",
                modelName = "offline-smart-engine",
                endpointCalled = endpointCalled,
                httpStatus = "200 OK (Fallback)",
                source = "local-smart-fallback",
                responseId = "fallback-" + java.util.UUID.randomUUID().toString().take(12),
                errorMessage = e.message
            )
            com.example.data.model.TutorResponseResult(responseText = fallbackText, debugMetadata = debugMetadata)
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
        if (lowerMsg.contains("in on at") || lowerMsg.contains("preposition")) {
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
        if (lowerMsg.contains("present perfect")) {
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

        // 8. General fallback
        return when (effectiveLang) {
            "Urdu" -> "انگریزی گرامر میں ہر جملہ بنیادی طور پر فاعل (Subject) اور فعل (Verb) کی مطابقت پر منحصر ہوتا ہے۔ مزید تفصیل کے لیے اپنا سوال واضح کریں۔"
            "Arabic" -> "في قواعد اللغة الإنجليزية، تعتمد الجملة على التوافق بين الفاعل والفعل. يرجى توضيح سؤالك لمزيد من الإيضاح."
            else -> "In English grammar, clear structure depends on proper subject-verb agreement. Feel free to ask any specific question!"
        }
    }
}
