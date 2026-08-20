package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Content
import com.example.data.model.ExamSessionEntity
import com.example.data.model.GenerateContentRequest
import com.example.data.model.GenerationConfig
import com.example.data.model.Part
import com.example.data.remote.RetrofitClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

enum class ExamSourceType {
    BOOK_PDF,
    PREVIOUS_LEARNED
}

@Serializable
data class ExamSourceBook(
    val id: String,
    val title: String,
    val author: String,
    val description: String,
    val chapters: List<String>,
    val sampleExcerpt: String = ""
)

data class ExamSetupConfig(
    val language: String = "English", // "English" or "Urdu"
    val sourceType: ExamSourceType = ExamSourceType.BOOK_PDF,
    val selectedBook: ExamSourceBook? = null,
    val selectedChapter: String = "All Chapters",
    val uploadedPdfName: String? = null,
    val uploadedPdfUri: String? = null,
    val isProcessingPdf: Boolean = false,
    val pdfProcessingError: String? = null,
    val processedDocumentId: String? = null,
    val mcqPercentage: String = "100%", // "50%" or "100%"
    val shortCount: Int = 20, // 20, 30, 40
    val longCount: Int = 10, // 10, 20, 30
    val difficulty: String = "Intermediate",
    val timeLimitMinutes: Int = 10,
    val learnedTopicsSummary: String = "",
    val customInstructions: String = ""
)

@Serializable
data class ExamQuestion(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: String = "MULTIPLE_CHOICE", // "MULTIPLE_CHOICE" or "SHORT_ANSWER"
    val questionText: String,
    val options: List<String> = emptyList(), // 4 options for MC
    val correctAnswerIndex: Int = 0, // for MC
    val modelAnswer: String = "", // for Short Answer
    val keyConcepts: List<String> = emptyList(), // key concepts to match
    val explanation: String = "",
    val topic: String = "General Grammar",
    val sourceSnippet: String = ""
)

@Serializable
data class ExamData(
    val questions: List<ExamQuestion> = emptyList(),
    val shortQuestions: List<String> = emptyList(),
    val longQuestions: List<String> = emptyList()
)

data class ExamLearnerAnswer(
    val questionId: String,
    val selectedOptionIndex: Int? = null,
    val shortAnswerText: String? = null,
    val isCorrect: Boolean? = null,
    val scorePercentage: Int = 0, // 0 to 100
    val feedback: String = ""
)

data class ExamResult(
    val totalScore: Int,
    val maxScore: Int,
    val percentage: Int,
    val gradeLetter: String,
    val timeTakenSeconds: Long,
    val weakTopics: List<String>,
    val strongTopics: List<String>,
    val objectiveCorrectCount: Int,
    val shortAnswerTotalScore: Int,
    val shortAnswerMaxPossible: Int
)

class ExamViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)

    // Pre-populated English Learning Books for Urdu Learners
    val defaultBooks = listOf(
        ExamSourceBook(
            id = "english_grammar_urdu",
            title = "Essential English Grammar (انگریزی گرامر)",
            author = "Lisan English Series",
            description = "Fundamentals of English grammar explained for Urdu speakers: Tenses, Parts of Speech, Auxiliary Verbs, and Sentence Structure.",
            chapters = listOf("All Chapters", "Tenses (Present, Past, Future)", "Parts of Speech & Articles", "Subject-Verb Agreement", "Daily Conversation & Vocabulary"),
            sampleExcerpt = "In Present Simple Tense, use 's' or 'es' with singular subjects (He, She, It). Example: 'He goes to school every day' (وہ روزانہ اسکول جاتا ہے)."
        ),
        ExamSourceBook(
            id = "urdu_to_english_spoken",
            title = "Urdu to English Spoken & Vocabulary Guide (آسان انگریزی بول چال)",
            author = "Lisan Conversation Academy",
            description = "Essential English vocabulary, daily idioms, sentence translation (Urdu to English) and common spoken phrases.",
            chapters = listOf("All Chapters", "Daily Conversational Phrases", "Urdu to English Sentence Translation", "Common Idioms & Phrasal Verbs", "Prepositions & Usage"),
            sampleExcerpt = "Translating Urdu thoughts into active English sentences. Urdu: 'وہ کام کر رہا ہے' -> English: 'He is working'."
        ),
        ExamSourceBook(
            id = "english_tenses_composition",
            title = "Practical English Tenses & Composition (انگریزی جملہ سازی)",
            author = "Prof. M. A. Khan",
            description = "Mastering active/passive voice, indirect speech, correct prepositions, and English sentence writing for Urdu learners.",
            chapters = listOf("All Chapters", "Present & Past Perfect Tenses", "Active & Passive Voice", "Modal Verbs (Can, Should, Must)", "Urdu-English Translation Skills"),
            sampleExcerpt = "Modal verbs like 'should' and 'must' are followed by base verbs. Example: 'You must complete your homework' (آپ کو اپنا ہوم ورک لازمی مکمل کرنا چاہیے)."
        )
    )

    private val _availableBooks = MutableStateFlow<List<ExamSourceBook>>(defaultBooks)
    val availableBooks: StateFlow<List<ExamSourceBook>> = _availableBooks.asStateFlow()

    private val _config = MutableStateFlow(ExamSetupConfig(selectedBook = defaultBooks.first()))
    val config: StateFlow<ExamSetupConfig> = _config.asStateFlow()

    private val _isGeneratingExam = MutableStateFlow(false)
    val isGeneratingExam: StateFlow<Boolean> = _isGeneratingExam.asStateFlow()

    private val _examError = MutableStateFlow<String?>(null)
    val examError: StateFlow<String?> = _examError.asStateFlow()

    private val _examData = MutableStateFlow<ExamData?>(null)
    val examData: StateFlow<ExamData?> = _examData.asStateFlow()

    private val _userAnswers = MutableStateFlow<Map<String, ExamLearnerAnswer>>(emptyMap())
    val userAnswers: StateFlow<Map<String, ExamLearnerAnswer>> = _userAnswers.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _remainingTimeSeconds = MutableStateFlow<Int?>(null)
    val remainingTimeSeconds: StateFlow<Int?> = _remainingTimeSeconds.asStateFlow()

    private val _isEvaluatingShortAnswer = MutableStateFlow(false)
    val isEvaluatingShortAnswer: StateFlow<Boolean> = _isEvaluatingShortAnswer.asStateFlow()

    private val _examResult = MutableStateFlow<ExamResult?>(null)
    val examResult: StateFlow<ExamResult?> = _examResult.asStateFlow()

    private var timerJob: Job? = null
    private var examStartTimeMillis: Long = 0L

    init {
        loadUserLibraryBooks()
        scanLearnedHistory()
    }

    private fun loadUserLibraryBooks() {
        viewModelScope.launch {
            try {
                val libraryItems = db.libraryDao().getAllItems().firstOrNull() ?: emptyList()
                val userBooks = libraryItems.map { item ->
                    ExamSourceBook(
                        id = item.id,
                        title = item.title,
                        author = item.category ?: "Library Saved Book",
                        description = item.preview ?: "Saved from your personal Lisan study library.",
                        chapters = listOf("All Chapters", "Key Concepts & Excerpts"),
                        sampleExcerpt = item.preview ?: item.title
                    )
                }

                val processedDocs = try { db.documentDao().getAllDocumentsList() } catch (e: Exception) { emptyList() }
                val docBooks = processedDocs
                    .filter { it.processingStatus == com.example.data.model.ProcessingStatus.READY.name }
                    .map { doc ->
                        ExamSourceBook(
                            id = doc.documentId,
                            title = doc.fileName,
                            author = "Uploaded Document (${doc.pageCount} pages)",
                            description = "Processed Document (${doc.wordCount} words, ${doc.chunkCount} chunks)",
                            chapters = listOf("All Chapters", "Key Concepts & Excerpts"),
                            sampleExcerpt = doc.extractedText.take(500)
                        )
                    }

                _availableBooks.value = defaultBooks + userBooks + docBooks
            } catch (e: Exception) {
                Log.e("ExamViewModel", "Error loading user library books", e)
            }
        }
    }

    fun scanLearnedHistory() {
        viewModelScope.launch {
            try {
                val recentMessages = db.chatMessageDao().getAllMessagesList().takeLast(40)
                val topicProgresses = db.topicProgressDao().getAllTopicProgress()
                val savedNotes = try { db.savedNoteDao().getAllNotes().firstOrNull() ?: emptyList() } catch (e: Exception) { emptyList() }
                
                val chatTopics = recentMessages
                    .map { it.text }
                    .filter { it.length > 10 }
                    .take(12)
                    .joinToString("; ") { it.take(80) }

                val completedTopicsList = topicProgresses
                    .filter { it.overallMastery > 20 || it.masteredCount > 0 }
                    .map { "${it.topicId} (${it.overallMastery}% mastery)" }
                    .joinToString(", ")

                val noteTitles = savedNotes.take(5).joinToString(", ") { it.titleEn }

                val summary = buildString {
                    if (completedTopicsList.isNotBlank()) {
                        append("Mastered English Topics: ").append(completedTopicsList).append(". ")
                    }
                    if (noteTitles.isNotBlank()) {
                        append("Learner Notes Topics: ").append(noteTitles).append(". ")
                    }
                    if (chatTopics.isNotBlank()) {
                        append("Recent English Learning Discussions: ").append(chatTopics.take(400)).append(".")
                    }
                    if (isBlank()) {
                        append("Learner has completed introductory English sessions on Present & Past Tenses, sentence structure, Urdu-to-English translation, and daily vocabulary.")
                    }
                }

                _config.value = _config.value.copy(learnedTopicsSummary = summary)
            } catch (e: Exception) {
                Log.e("ExamViewModel", "Error scanning learning history", e)
                _config.value = _config.value.copy(
                    learnedTopicsSummary = "Learner studied English tenses (Present & Past), Urdu to English translation, English sentence structure, and vocabulary in Lisan AI Tutor."
                )
            }
        }
    }

    fun setLanguage(lang: String) {
        _config.value = _config.value.copy(language = lang)
    }

    fun setSourceType(type: ExamSourceType) {
        _config.value = _config.value.copy(sourceType = type)
    }

    fun setSelectedBook(book: ExamSourceBook) {
        _config.value = _config.value.copy(
            selectedBook = book,
            selectedChapter = book.chapters.firstOrNull() ?: "All Chapters"
        )
    }

    fun setSelectedChapter(chapter: String) {
        _config.value = _config.value.copy(selectedChapter = chapter)
    }

    fun setMcqPercentage(pct: String) {
        _config.value = _config.value.copy(mcqPercentage = pct)
    }

    fun setShortCount(count: Int) {
        _config.value = _config.value.copy(shortCount = count)
    }

    fun setLongCount(count: Int) {
        _config.value = _config.value.copy(longCount = count)
    }

    fun setDifficulty(difficulty: String) {
        _config.value = _config.value.copy(difficulty = difficulty)
    }

    fun setTimeLimit(minutes: Int) {
        _config.value = _config.value.copy(timeLimitMinutes = minutes)
    }

    fun setCustomInstructions(instructions: String) {
        _config.value = _config.value.copy(customInstructions = instructions)
    }

    fun processUploadedPdf(context: Context, uri: android.net.Uri) {
        viewModelScope.launch {
            _config.value = _config.value.copy(
                isProcessingPdf = true,
                pdfProcessingError = null,
                uploadedPdfUri = uri.toString()
            )
            try {
                // Call standard DocumentProcessor
                val (doc, chunks) = com.example.data.document.DocumentProcessor.processDocument(context, uri)
                
                // Save to Room Database
                db.documentDao().insertDocument(doc)
                if (chunks.isNotEmpty()) {
                    db.documentDao().insertChunks(chunks)
                }

                if (doc.processingStatus == com.example.data.model.ProcessingStatus.READY.name) {
                    _config.value = _config.value.copy(
                        uploadedPdfName = doc.fileName,
                        processedDocumentId = doc.documentId,
                        isProcessingPdf = false
                    )
                    // Refresh available books to include this doc
                    loadUserLibraryBooks()
                } else {
                    _config.value = _config.value.copy(
                        isProcessingPdf = false,
                        pdfProcessingError = doc.statusMessage ?: "Failed to process PDF content."
                    )
                }
            } catch (e: Exception) {
                Log.e("ExamViewModel", "PDF upload error", e)
                _config.value = _config.value.copy(
                    isProcessingPdf = false,
                    pdfProcessingError = e.message ?: "Failed to process selected file."
                )
            }
        }
    }

    fun startExamWithConfig() {
        val currentConfig = _config.value
        val sourceTitle = if (currentConfig.sourceType == ExamSourceType.BOOK_PDF) {
            if (currentConfig.processedDocumentId != null) {
                "Uploaded PDF: ${currentConfig.uploadedPdfName ?: "Document"}"
            } else {
                "${currentConfig.selectedBook?.title ?: "English Grammar"} (${currentConfig.selectedChapter})"
            }
        } else {
            "Things I've Learned (Personalized Progress)"
        }
        
        generateExam(sourceTitle = sourceTitle, config = currentConfig)
    }

    fun startPracticeWeakAreas(weakTopicsList: List<String>) {
        val topicsText = weakTopicsList.joinToString(", ")
        val customConfig = _config.value.copy(
            mcqPercentage = "50%",
            shortCount = 1,
            longCount = 0,
            difficulty = "Focused Practice",
            timeLimitMinutes = 5
        )
        generateExam(
            sourceTitle = "Weak Topics Focused Practice: $topicsText",
            config = customConfig,
            topicConstraint = "Focus exclusively on testing and reinforcing the learner's weak areas: $topicsText."
        )
    }

    private fun generateExam(
        sourceTitle: String,
        config: ExamSetupConfig,
        topicConstraint: String = ""
    ) {
        viewModelScope.launch {
            _isGeneratingExam.value = true
            _examError.value = null
            _examResult.value = null
            _userAnswers.value = emptyMap()
            _currentQuestionIndex.value = 0

            try {
                val prefs = getApplication<Application>().getSharedPreferences("lisan_prefs", Context.MODE_PRIVATE)
                val userKey = prefs.getString("user_gemini_api_key", null)?.takeIf { it.isNotBlank() } ?: ""
                val useBackend = RetrofitClient.isUsingBackend()

                if (!useBackend && userKey.isBlank()) {
                    _examError.value = "Gemini service is not configured. Please enter your personal API Key under Settings, or configure a secure backend proxy URL."
                    _isGeneratingExam.value = false
                    return@launch
                }

                val schema = buildJsonObject {
                    put("type", "OBJECT")
                    put("properties", buildJsonObject {
                        put("questions", buildJsonObject {
                            put("type", "ARRAY")
                            put("items", buildJsonObject {
                                put("type", "OBJECT")
                                put("properties", buildJsonObject {
                                    put("id", buildJsonObject { put("type", "STRING") })
                                    put("questionText", buildJsonObject { put("type", "STRING") })
                                    put("options", buildJsonObject {
                                        put("type", "ARRAY")
                                        put("items", buildJsonObject { put("type", "STRING") })
                                    })
                                    put("correctAnswerIndex", buildJsonObject { put("type", "INTEGER") })
                                    put("explanation", buildJsonObject { put("type", "STRING") })
                                    put("topic", buildJsonObject { put("type", "STRING") })
                                })
                                put("required", buildJsonArray {
                                    add("questionText")
                                    add("options")
                                    add("correctAnswerIndex")
                                    add("explanation")
                                    add("topic")
                                })
                            })
                        })
                        put("shortQuestions", buildJsonObject {
                            put("type", "ARRAY")
                            put("items", buildJsonObject { put("type", "STRING") })
                        })
                        put("longQuestions", buildJsonObject {
                            put("type", "ARRAY")
                            put("items", buildJsonObject { put("type", "STRING") })
                        })
                    })
                    put("required", buildJsonArray { add("questions"); add("shortQuestions"); add("longQuestions") })
                }

                val genConfig = GenerationConfig(
                    responseMimeType = "application/json",
                    responseSchema = schema,
                    temperature = 0.4f
                )

                val is50Percent = config.mcqPercentage == "50%"
                val mcqCount = if (is50Percent) 15 else 25
                val shortCount = if (is50Percent) (config.shortCount / 4).coerceAtLeast(4).coerceAtMost(10) else 0
                val longCount = if (is50Percent) (config.longCount / 3).coerceAtLeast(2).coerceAtMost(6) else 0

                val groundingMaterial = if (config.sourceType == ExamSourceType.BOOK_PDF) {
                    val targetDocId = config.processedDocumentId ?: config.selectedBook?.id
                    val matchedDoc = if (targetDocId != null) {
                        db.documentDao().getDocumentById(targetDocId)
                    } else null

                    val libraryDoc = if (matchedDoc == null && targetDocId != null) {
                        try { db.libraryDao().getAllItems().firstOrNull()?.firstOrNull { it.id == targetDocId } } catch (e: Exception) { null }
                    } else null

                    if (matchedDoc != null) {
                        val chunks = db.documentDao().getChunksForDocument(matchedDoc.documentId)
                        val sampledText = if (chunks.isNotEmpty()) {
                            if (chunks.size <= 25) {
                                chunks.joinToString("\n\n") { "--- Section #${it.chunkIndex + 1} (Page ${it.pageNumber ?: "?"}) ---\n${it.text}" }
                            } else {
                                val step = (chunks.size / 25).coerceAtLeast(1)
                                val selectedChunks = mutableListOf<com.example.data.model.DocumentChunkEntity>()
                                for (i in chunks.indices step step) {
                                    selectedChunks.add(chunks[i])
                                    if (selectedChunks.size >= 25) break
                                }
                                selectedChunks.joinToString("\n\n") { "--- Section #${it.chunkIndex + 1} (Page ${it.pageNumber ?: "?"}) ---\n${it.text}" }
                            }
                        } else {
                            val fullText = matchedDoc.extractedText
                            if (fullText.length > 30000) {
                                val segmentLen = 6000
                                val p1 = fullText.take(segmentLen)
                                val p2 = fullText.substring((fullText.length * 0.25).toInt(), ((fullText.length * 0.25) + segmentLen).toInt().coerceAtMost(fullText.length))
                                val p3 = fullText.substring((fullText.length * 0.50).toInt(), ((fullText.length * 0.50) + segmentLen).toInt().coerceAtMost(fullText.length))
                                val p4 = fullText.substring((fullText.length * 0.75).toInt(), ((fullText.length * 0.75) + segmentLen).toInt().coerceAtMost(fullText.length))
                                val p5 = fullText.takeLast(segmentLen)
                                "--- START OF BOOK ---\n$p1\n\n--- 25% MARK ---\n$p2\n\n--- MIDDLE OF BOOK ---\n$p3\n\n--- 75% MARK ---\n$p4\n\n--- END OF BOOK ---\n$p5"
                            } else {
                                fullText
                            }
                        }

                        """
                        ACTUAL UPLOADED BOOK / DOCUMENT CONTENT:
                        Document Name: '${matchedDoc.fileName}' (${matchedDoc.pageCount} pages, ${matchedDoc.wordCount} words)
                        
                        SOURCE TEXT FROM WHOLE BOOK (SAMPLED FROM BEGINNING, MIDDLE, AND END SECTIONS):
                        $sampledText
                        
                        MANDATE: Every single question (MCQs, Short Questions, and Long Questions) MUST be strictly based on and grounded in the grammar, vocabulary, rules, and concepts present in this uploaded text above. Distribute questions evenly across the beginning, middle, and later chapters.
                        """.trimIndent()
                    } else if (libraryDoc != null) {
                        """
                        SAVED LIBRARY BOOK CONTENT:
                        Title: '${libraryDoc.title}' (${libraryDoc.category ?: "Grammar"})
                        Text / Excerpt:
                        ${libraryDoc.preview ?: libraryDoc.title}
                        
                        MANDATE: Generate exam questions strictly testing the concepts and rules in this saved book material.
                        """.trimIndent()
                    } else {
                        val book = config.selectedBook
                        "BOOK SOURCE: '${book?.title}' by ${book?.author}.\nSelected Chapter: ${config.selectedChapter}.\nBook Overview: ${book?.description}.\nSample Content & Excerpt: ${book?.sampleExcerpt}"
                    }
                } else {
                    "LEARNER HISTORY SOURCE CONTEXT: ${config.learnedTopicsSummary}"
                }

                val isUrdu = config.language == "Urdu"
                val languagePrompt = if (isUrdu) {
                    """
                    EXAM LANGUAGE PREFERENCE: URDU (اردو)
                    - The questions should be written with Urdu-language prompts/guidance where possible (testing English skills, e.g. translation, fill-in-the-blanks, grammar rules).
                    - All explanations, correct option justifications, and feedback MUST be written in URDU (اردو).
                    - Example MCQ prompt: "اس انگریزی جملے کا درست اردو ترجمہ منتخب کریں:" or "درج ذیل جملے میں خالی جگہ پر کریں:"
                    """.trimIndent()
                } else {
                    """
                    EXAM LANGUAGE PREFERENCE: ENGLISH
                    - The questions, prompts, choices, explanations, justifications, and feedback MUST be written ENTIRELY in ENGLISH.
                    - Example MCQ prompt: "Choose the correct tense for the following sentence:" or "What is the correct translation of this word?"
                    """.trimIndent()
                }

                val prompt = """
                    ${RetrofitClient.FEMALE_TUTOR_PERSONA_INSTRUCTION}

                    You are an expert female English language examination system for URDU-speaking learners.
                    Generate a personalized, highly accurate English exam strictly grounded in the following source material.
                    
                    $languagePrompt
                    
                    SOURCE MATERIAL:
                    $groundingMaterial
                    
                    EXAM SPECIFICATIONS:
                    - Generate EXACTLY $mcqCount Multiple Choice Questions (MCQs) in the "questions" array.
                    - Generate EXACTLY $shortCount conversational Short Questions in the "shortQuestions" array.
                    - Generate EXACTLY $longCount conversational Long Questions in the "longQuestions" array.
                    - Difficulty Level: ${config.difficulty}
                    - Source Title: $sourceTitle
                    ${if (config.customInstructions.isNotBlank()) "ADDITIONAL LEARNER INSTRUCTIONS: ${config.customInstructions}" else ""}
                    $topicConstraint
                    
                    STRICT RULES:
                    1. For the "questions" array (MCQs):
                       - 'questionText': Test English grammar, vocabulary, usage, or sentence translation.
                       - 'options': Exactly 4 choices.
                       - 'correctAnswerIndex': index of correct choice (0 to 3).
                       - 'explanation': Explains why it is correct and why other options are wrong, matching the requested language preference (Urdu or English).
                       - 'topic': Topic tag.
                    2. For the "shortQuestions" array:
                       - Provide simple, direct question strings that test comprehension or invite translation or short sentence formation.
                    3. For the "longQuestions" array:
                       - Provide analytical or open-ended scenario-based question strings requiring longer essay/descriptive style English answers.
                    4. Never generate empty arrays unless the count is specified as 0. Do NOT exceed or drop the requested counts.
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(Content(role = "user", parts = listOf(Part(text = prompt)))),
                    generationConfig = genConfig
                )

                val response = RetrofitClient.executeGenerateContent("gemini-3.5-flash", userKey, request)

                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (responseText != null) {
                    val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
                    val parsedData = json.decodeFromString<ExamData>(responseText)
                    
                    // Validate and ensure non-empty questions
                    val sanitizedQuestions = parsedData.questions.mapIndexed { idx, q ->
                        val validId = if (q.id.isBlank()) "q_${idx + 1}_${System.currentTimeMillis()}" else q.id
                        val validOptions = if (q.options.size < 4) {
                            listOf("Option A", "Option B", "Option C", "Option D")
                        } else q.options

                        q.copy(
                            id = validId,
                            type = "MULTIPLE_CHOICE",
                            options = validOptions,
                            correctAnswerIndex = q.correctAnswerIndex.coerceIn(0, (validOptions.size - 1).coerceAtLeast(0))
                        )
                    }

                    if (sanitizedQuestions.isNotEmpty() || parsedData.shortQuestions.isNotEmpty() || parsedData.longQuestions.isNotEmpty()) {
                        _examData.value = ExamData(
                            questions = sanitizedQuestions,
                            shortQuestions = parsedData.shortQuestions,
                            longQuestions = parsedData.longQuestions
                        )
                        startExamTimer(config.timeLimitMinutes)
                    } else {
                        _examError.value = "Failed to generate valid exam questions. Please try again."
                    }
                } else {
                    val errorMsg = response.error?.message ?: "Failed to generate exam."
                    _examError.value = errorMsg
                }
            } catch (e: Exception) {
                Log.e("ExamViewModel", "Error generating exam", e)
                val msg = e.message ?: ""
                if (msg.contains("503") || (e is retrofit2.HttpException && e.code() == 503)) {
                    _examError.value = "Lisan Exam service is temporarily unavailable due to high server demand (HTTP 503). Please retry in a moment, or consider adding your own Gemini API Key under Settings to bypass shared limits."
                } else {
                    _examError.value = "Generation error: ${e.message ?: "Unknown error"}"
                }
            } finally {
                _isGeneratingExam.value = false
            }
        }
    }

    private fun startExamTimer(limitMinutes: Int) {
        timerJob?.cancel()
        examStartTimeMillis = System.currentTimeMillis()

        if (limitMinutes <= 0) {
            _remainingTimeSeconds.value = null
            return
        }

        val totalSeconds = limitMinutes * 60
        _remainingTimeSeconds.value = totalSeconds

        timerJob = viewModelScope.launch {
            var currentSecs = totalSeconds
            while (currentSecs > 0) {
                delay(1000L)
                currentSecs--
                _remainingTimeSeconds.value = currentSecs
            }
            // Time is up! Auto calculate exam result
            finishExam()
        }
    }

    fun submitMultipleChoiceAnswer(questionId: String, selectedIndex: Int) {
        val currentQuestions = _examData.value?.questions ?: return
        val question = currentQuestions.find { it.id == questionId } ?: return

        val isCorrect = selectedIndex == question.correctAnswerIndex
        val score = if (isCorrect) 100 else 0
        val feedback = if (isCorrect) {
            "✅ درست جواب! ${question.explanation}"
        } else {
            "❌ غلط جواب۔ درست جواب ہے: '${question.options.getOrNull(question.correctAnswerIndex)}'\n💡 وضاحت: ${question.explanation}"
        }

        val updatedMap = _userAnswers.value.toMutableMap()
        updatedMap[questionId] = ExamLearnerAnswer(
            questionId = questionId,
            selectedOptionIndex = selectedIndex,
            isCorrect = isCorrect,
            scorePercentage = score,
            feedback = feedback
        )
        _userAnswers.value = updatedMap
    }

    fun evaluateAndSubmitShortAnswer(questionId: String, learnerResponse: String) {
        val currentQuestions = _examData.value?.questions ?: return
        val question = currentQuestions.find { it.id == questionId } ?: return

        if (learnerResponse.isBlank()) return

        viewModelScope.launch {
            _isEvaluatingShortAnswer.value = true
            try {
                val prefs = getApplication<Application>().getSharedPreferences("lisan_prefs", Context.MODE_PRIVATE)
                val userKey = prefs.getString("user_gemini_api_key", null)?.takeIf { it.isNotBlank() } ?: ""
                val useBackend = RetrofitClient.isUsingBackend()

                val prompt = """
                    ${RetrofitClient.FEMALE_TUTOR_PERSONA_INSTRUCTION}

                    You are an expert female English teacher for Urdu-speaking students grading a short answer question.
                    
                    QUESTION: ${question.questionText}
                    MODEL ANSWER: ${question.modelAnswer}
                    KEY CONCEPTS TO LOOK FOR: ${question.keyConcepts.joinToString(", ")}
                    EXPLANATION REFERENCE: ${question.explanation}
                    
                    STUDENT ANSWER: "$learnerResponse"
                    
                    Grade the student's answer fairly. Respond strictly in valid JSON with:
                    {
                      "scorePercentage": <integer 0 to 100>,
                      "isCorrect": <boolean, true if score >= 70>,
                      "feedback": "<2-3 sentences in URDU (اردو - نستعلیق) using feminine self-reference explaining what was correct, any mistakes, and the correct English form with clear Urdu explanation. NEVER output Hindi script (Devanagari).>"
                    }
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(Content(role = "user", parts = listOf(Part(text = prompt)))),
                    generationConfig = GenerationConfig(responseMimeType = "application/json", temperature = 0.2f)
                )

                val response = RetrofitClient.executeGenerateContent("gemini-3.5-flash", userKey, request)

                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (responseText != null) {
                    val jsonObj = Json.parseToJsonElement(responseText).jsonObject
                    val score = jsonObj["scorePercentage"]?.jsonPrimitive?.intOrNull ?: 70
                    val isCorr = jsonObj["isCorrect"]?.jsonPrimitive?.booleanOrNull ?: (score >= 70)
                    val feedbackText = jsonObj["feedback"]?.jsonPrimitive?.content ?: "Evaluated successfully."

                    val updatedMap = _userAnswers.value.toMutableMap()
                    updatedMap[questionId] = ExamLearnerAnswer(
                        questionId = questionId,
                        shortAnswerText = learnerResponse,
                        isCorrect = isCorr,
                        scorePercentage = score,
                        feedback = feedbackText
                    )
                    _userAnswers.value = updatedMap
                } else {
                    // Fallback evaluation based on key concepts matching
                    val matchedCount = question.keyConcepts.count { concept ->
                        learnerResponse.contains(concept, ignoreCase = true)
                    }
                    val fallbackScore = if (question.keyConcepts.isEmpty()) 80 else ((matchedCount.toFloat() / question.keyConcepts.size) * 100).toInt()
                    val updatedMap = _userAnswers.value.toMutableMap()
                    updatedMap[questionId] = ExamLearnerAnswer(
                        questionId = questionId,
                        shortAnswerText = learnerResponse,
                        isCorrect = fallbackScore >= 60,
                        scorePercentage = fallbackScore,
                        feedback = "Evaluated against model answer: ${question.modelAnswer}"
                    )
                    _userAnswers.value = updatedMap
                }
            } catch (e: Exception) {
                Log.e("ExamViewModel", "Error evaluating short answer", e)
                val updatedMap = _userAnswers.value.toMutableMap()
                updatedMap[questionId] = ExamLearnerAnswer(
                    questionId = questionId,
                    shortAnswerText = learnerResponse,
                    isCorrect = true,
                    scorePercentage = 75,
                    feedback = "Submitted! Model answer: ${question.modelAnswer}"
                )
                _userAnswers.value = updatedMap
            } finally {
                _isEvaluatingShortAnswer.value = false
            }
        }
    }

    fun nextQuestion() {
        val maxIndex = (_examData.value?.questions?.size ?: 1) - 1
        if (_currentQuestionIndex.value < maxIndex) {
            _currentQuestionIndex.value = _currentQuestionIndex.value + 1
        }
    }

    fun previousQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value = _currentQuestionIndex.value - 1
        }
    }

    fun finishExam() {
        timerJob?.cancel()
        val questions = _examData.value?.questions ?: return
        val answers = _userAnswers.value

        var mcqCorrectCount = 0
        var mcqTotalCount = 0
        var shortAnswerScoreSum = 0
        var shortAnswerMaxSum = 0

        val topicScores = mutableMapOf<String, Pair<Int, Int>>() // topic -> (scoreSum, maxPossible)

        questions.forEach { q ->
            val ans = answers[q.id]
            val topic = q.topic.ifBlank { "General Grammar" }
            val existing = topicScores.getOrDefault(topic, Pair(0, 0))

            if (q.type == "MULTIPLE_CHOICE") {
                mcqTotalCount++
                if (ans?.isCorrect == true) {
                    mcqCorrectCount++
                    topicScores[topic] = Pair(existing.first + 100, existing.second + 100)
                } else {
                    topicScores[topic] = Pair(existing.first + 0, existing.second + 100)
                }
            } else {
                shortAnswerMaxSum += 100
                val shortScore = ans?.scorePercentage ?: 0
                shortAnswerScoreSum += shortScore
                topicScores[topic] = Pair(existing.first + shortScore, existing.second + 100)
            }
        }

        val totalPoints = (mcqCorrectCount * 100) + shortAnswerScoreSum
        val maxPoints = (mcqTotalCount * 100) + shortAnswerMaxSum
        val overallPct = if (maxPoints > 0) ((totalPoints.toFloat() / maxPoints) * 100).toInt() else 0

        val grade = when {
            overallPct >= 90 -> "A+"
            overallPct >= 80 -> "A"
            overallPct >= 70 -> "B"
            overallPct >= 60 -> "C"
            else -> "Needs Practice"
        }

        val weak = mutableListOf<String>()
        val strong = mutableListOf<String>()

        topicScores.forEach { (topic, scorePair) ->
            val pct = if (scorePair.second > 0) ((scorePair.first.toFloat() / scorePair.second) * 100).toInt() else 0
            if (pct < 70) {
                weak.add(topic)
            } else {
                strong.add(topic)
            }
        }

        val elapsedSecs = (System.currentTimeMillis() - examStartTimeMillis) / 1000L

        val result = ExamResult(
            totalScore = totalPoints,
            maxScore = maxPoints,
            percentage = overallPct,
            gradeLetter = grade,
            timeTakenSeconds = elapsedSecs,
            weakTopics = weak.ifEmpty { if (overallPct < 70) listOf("General Grammar Syntax") else emptyList() },
            strongTopics = strong,
            objectiveCorrectCount = mcqCorrectCount,
            shortAnswerTotalScore = shortAnswerScoreSum,
            shortAnswerMaxPossible = shortAnswerMaxSum
        )

        _examResult.value = result

        // Save session to Room database
        viewModelScope.launch {
            try {
                val entity = ExamSessionEntity(
                    topic = _config.value.selectedBook?.title ?: "Learned History Exam",
                    score = totalPoints,
                    totalQuestions = questions.size,
                    isCompleted = true,
                    summary = "Grade: $grade ($overallPct%), Weak Topics: ${weak.joinToString()}"
                )
                db.examSessionDao().insertSession(entity)
            } catch (e: Exception) {
                Log.e("ExamViewModel", "Failed to save exam session", e)
            }
        }
    }

    fun dismissError() {
        _examError.value = null
    }

    fun clearExam() {
        timerJob?.cancel()
        _examData.value = null
        _examResult.value = null
        _userAnswers.value = emptyMap()
        _remainingTimeSeconds.value = null
    }

    fun transitionToChatExam(lisanViewModel: LisanViewModel) {
        val exam = _examData.value ?: return
        val examLanguage = _config.value.language
        val sourceName = if (_config.value.sourceType == ExamSourceType.BOOK_PDF) {
            _config.value.uploadedPdfName ?: _config.value.selectedBook?.title ?: "Uploaded Document"
        } else {
            "Learned Topics & History"
        }

        val mcqCorrect = _userAnswers.value.values.count { it.isCorrect == true }
        val mcqTotal = exam.questions.size

        val prompt = buildString {
            if (examLanguage == "Urdu") {
                append("🎓 امتحانی اطلاع • LISAN EXAM INTERACTIVE PHASE\n\n")
                append("طالب علم نے اپنے امتحان کا MCQ حصہ مکمل کر لیا ہے!\n")
                append("امتحان کی زبان: اردو رہنمائی (Urdu)\n")
                append("ماخذ: $sourceName\n")
                append("ایم سی کیو (MCQs) کا نتیجہ: $mcqCorrect / $mcqTotal درست\n\n")
                append("طرزِ عمل کی ہدایت (Instruction for Tutor):\n")
                append("1. اب مندرجہ ذیل سوالات گفتگو کی صورت میں ایک ایک کر کے پوچھیں۔\n")
                append("2. طالب علم کے ہر جواب کا فوری جائزہ لیں، فیڈ بیک دیں اور نمبر بتائیں۔\n")
                append("3. تمام شارٹ سوالات (Short Questions) مکمل ہونے کے بعد لانگ سوالات (Long Questions) کی طرف بڑھیں۔\n\n")
            } else {
                append("🎓 LISAN EXAM • INTERACTIVE CHAT PHASE\n\n")
                append("The learner has completed the MCQ section of their exam!\n")
                append("Language: English\n")
                append("Source: $sourceName\n")
                append("MCQ Results: $mcqCorrect / $mcqTotal Correct\n\n")
                append("TUTOR DIRECTIVE:\n")
                append("1. Administer the following Short Questions and Long Questions ONE BY ONE.\n")
                append("2. Evaluate each learner response with feedback and score out of 100.\n")
                append("3. Complete all Short Questions first, then smoothly proceed to the Long Questions.\n\n")
            }

            if (exam.shortQuestions.isNotEmpty()) {
                append("SHORT QUESTIONS (${exam.shortQuestions.size}):\n")
                exam.shortQuestions.forEachIndexed { i, q ->
                    append("SQ${i + 1}: $q\n")
                }
            }

            if (exam.longQuestions.isNotEmpty()) {
                append("\nLONG QUESTIONS (${exam.longQuestions.size}):\n")
                exam.longQuestions.forEachIndexed { i, q ->
                    append("LQ${i + 1}: $q\n")
                }
            }

            if (examLanguage == "Urdu") {
                append("\nبراہ کرم طالب علم سے پہلا سوال (SQ1) پوچھ کر آغاز کریں!")
            } else {
                append("\nPlease greet the learner and ask Short Question #1 (SQ1) to begin!")
            }
        }

        lisanViewModel.injectSystemExamMessage(prompt)
    }
}
