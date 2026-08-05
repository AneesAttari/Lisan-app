package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.example.data.local.AppDatabase
import com.example.data.local.SavedIdiomRepository
import com.example.data.model.*
import com.example.data.remote.RetrofitClient
import com.example.data.model.CurriculumData
import com.example.data.repository.DailyGrammarRepository
import com.example.data.repository.IdiomRepository
import com.example.utils.NotificationHelper
import com.example.utils.TutorAudioManager
import com.example.utils.TutorTtsManager
import com.example.utils.LanguageDetector
import com.example.utils.DetectedLanguage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LisanViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("lisan_prefs", Context.MODE_PRIVATE)
    private val database = AppDatabase.getDatabase(application)
    private val savedIdiomRepository = SavedIdiomRepository(database.savedIdiomDao())
    private val libraryRepository = com.example.data.repository.LibraryRepository(database.libraryDao(), database.likedDao())
    private val chatMessageRepository = com.example.data.local.ChatMessageRepository(database.chatMessageDao())
    val tutorAudioManager = TutorAudioManager(application)
    val learnerProfileManager = com.example.data.repository.LearnerProfileManager(application)
    val learnerProfile = learnerProfileManager.learnerProfile

    private val speechRecognizer by lazy {
        try {
            SpeechRecognizer.createSpeechRecognizer(application)
        } catch (e: Exception) {
            null
        }
    }
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private var audioTimeoutJob: Job? = null

    // --- Navigation & More Menu ---
    private val _isMoreMenuOpen = MutableStateFlow(false)
    val isMoreMenuOpen: StateFlow<Boolean> = _isMoreMenuOpen.asStateFlow()

    fun toggleMoreMenu(open: Boolean) { _isMoreMenuOpen.value = open }

    // --- Daily Challenge ---
    private val dailyChallengeDao = database.dailyChallengeDao()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val todayDateKey = dateFormat.format(Date())

    private val _dailyChallengeQuestions = MutableStateFlow<List<DailyChallengeQuestion>>(emptyList())
    val dailyChallengeQuestions: StateFlow<List<DailyChallengeQuestion>> = _dailyChallengeQuestions.asStateFlow()

    private val _currentDailyQuestionIndex = MutableStateFlow(0)
    val currentDailyQuestionIndex: StateFlow<Int> = _currentDailyQuestionIndex.asStateFlow()

    private val _dailyChallengeState = MutableStateFlow(DailyChallengeState.LOADING)
    val dailyChallengeState: StateFlow<DailyChallengeState> = _dailyChallengeState.asStateFlow()

    private val _dailyUserAnswer = MutableStateFlow("")
    val dailyUserAnswer: StateFlow<String> = _dailyUserAnswer.asStateFlow()

    private val _dailyFeedback = MutableStateFlow<String?>(null)
    val dailyFeedback: StateFlow<String?> = _dailyFeedback.asStateFlow()

    private val _isDailyCorrect = MutableStateFlow<Boolean?>(null)
    val isDailyCorrect: StateFlow<Boolean?> = _isDailyCorrect.asStateFlow()

    private val _dailyChallengeResults = MutableStateFlow<List<DailyChallengeResultEntity>>(emptyList())
    val dailyChallengeResults: StateFlow<List<DailyChallengeResultEntity>> = _dailyChallengeResults.asStateFlow()

    private fun loadDailyChallenge() {
        viewModelScope.launch {
            _dailyChallengeState.value = DailyChallengeState.LOADING
            try {
                val questions = DailyGrammarRepository.getDailyQuestions()
                if (questions.isNotEmpty()) {
                    _dailyChallengeQuestions.value = questions
                    // Check if already completed today
                    val todayResults = dailyChallengeDao.getResultsByDate(todayDateKey).first()
                    if (todayResults.size >= questions.size) {
                        _dailyChallengeState.value = DailyChallengeState.COMPLETED
                    } else {
                        _dailyChallengeState.value = DailyChallengeState.READY
                    }
                } else {
                    _dailyChallengeState.value = DailyChallengeState.ERROR
                }
            } catch (e: Exception) {
                _dailyChallengeState.value = DailyChallengeState.ERROR
            }
        }
    }

    private fun observeDailyChallengeResults() {
        viewModelScope.launch {
            dailyChallengeDao.getResultsByDate(todayDateKey).collect {
                _dailyChallengeResults.value = it
            }
        }
    }

    fun setDailyUserAnswer(answer: String) { _dailyUserAnswer.value = answer }

    fun submitDailyAnswer() {
        val currentQuestion = _dailyChallengeQuestions.value.getOrNull(_currentDailyQuestionIndex.value) ?: return
        val answer = _dailyUserAnswer.value.trim()
        if (answer.isEmpty()) return

        viewModelScope.launch {
            val isCorrect = when (currentQuestion.type) {
                DailyChallengeQuestionType.MCQ -> {
                    val index = answer.toIntOrNull()
                    index == currentQuestion.correctOptionIndex
                }
                else -> {
                    currentQuestion.acceptedAnswers.any { it.equals(answer, ignoreCase = true) }
                }
            }

            _isDailyCorrect.value = isCorrect
            _dailyFeedback.value = if (isCorrect) currentQuestion.explanationUr else currentQuestion.explanationUr
            _dailyChallengeState.value = DailyChallengeState.ANSWERED

            // Save result
            val result = DailyChallengeResultEntity(
                date = todayDateKey,
                questionId = currentQuestion.id,
                topic = currentQuestion.topic,
                type = currentQuestion.type.name,
                userAnswer = answer,
                correctAnswer = when (currentQuestion.type) {
                    DailyChallengeQuestionType.MCQ -> currentQuestion.options.getOrNull(currentQuestion.correctOptionIndex ?: -1) ?: ""
                    else -> currentQuestion.acceptedAnswers.firstOrNull() ?: ""
                },
                isCorrect = isCorrect,
                timeTakenMillis = 0 // Optional: implement timer
            )
            dailyChallengeDao.insertResult(result)
            
            // Automatic long-term learning memory update
            learnerProfileManager.recordQuestionResult(
                type = "daily_challenge",
                topic = currentQuestion.topic,
                isCorrect = isCorrect,
                userAnswer = answer,
                correctAnswer = result.correctAnswer
            )
        }
    }

    fun nextDailyQuestion() {
        if (_currentDailyQuestionIndex.value < _dailyChallengeQuestions.value.size - 1) {
            _currentDailyQuestionIndex.value++
            _dailyUserAnswer.value = ""
            _dailyFeedback.value = null
            _isDailyCorrect.value = null
            _dailyChallengeState.value = DailyChallengeState.READY
        } else {
            _dailyChallengeState.value = DailyChallengeState.COMPLETED
            _isDailyCompleted.value = true
            prefs.edit().putBoolean("daily_completed_$todayDateKey", true).apply()
            // Increment streak only once per day
            if (prefs.getInt("last_streak_date", 0) != todayDateKey.replace("-", "").toInt()) {
                _dailyStreak.value++
                prefs.edit().putInt("daily_streak_count", _dailyStreak.value)
                    .putInt("last_streak_date", todayDateKey.replace("-", "").toInt()).apply()
            }
        }
    }

    fun retryDailyChallenge() {
        loadDailyChallenge()
    }

    private fun setupSpeechRecognizer() {
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { _isListening.value = true }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { _isListening.value = false }
            override fun onError(error: Int) { _isListening.value = false }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    _idiomSearchQuery.value = matches[0]
                }
                _isListening.value = false
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startVoiceSearch() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        speechRecognizer?.startListening(intent) ?: run {
            _chatError.value = "Speech recognition is not available on this device."
        }
    }

    fun stopVoiceSearch() {
        speechRecognizer?.stopListening()
        _isListening.value = false
    }

    // --- Library & Liked ---
    val allLibraryItems = libraryRepository.allLibraryItems.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allLikedItems = libraryRepository.allLikedItems.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun isSaved(type: String, id: String) = libraryRepository.isSaved(type, id).stateIn(viewModelScope, SharingStarted.Lazily, false)
    fun isLiked(type: String, id: String) = libraryRepository.isLiked(type, id).stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun toggleLibrarySave(item: LibraryItem) {
        viewModelScope.launch {
            val currentlySaved = libraryRepository.isSaved(item.contentType, item.contentId).first()
            if (currentlySaved) {
                libraryRepository.removeFromLibrary(item.contentType, item.contentId)
            } else {
                libraryRepository.saveToLibrary(item)
            }
        }
    }

    fun toggleLike(item: LikedItem) {
        viewModelScope.launch {
            val currentlyLiked = libraryRepository.isLiked(item.contentType, item.contentId).first()
            if (currentlyLiked) {
                libraryRepository.unlikeItem(item.contentType, item.contentId)
            } else {
                libraryRepository.likeItem(item)
            }
        }
    }

    // --- System State ---
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(prefs.getBoolean("is_dark_theme", false))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _proficiencyLevel = MutableStateFlow(ProficiencyLevel.BEGINNER)
    val proficiencyLevel: StateFlow<ProficiencyLevel> = _proficiencyLevel.asStateFlow()

    fun setSelectedTab(index: Int) { _selectedTab.value = index }
    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
        prefs.edit().putBoolean("is_dark_theme", _isDarkTheme.value).apply()
    }
    fun setProficiencyLevel(level: ProficiencyLevel) { _proficiencyLevel.value = level }

    // --- Tab 1: Comparative Grammar Lessons ---
    private val _selectedTopicId = MutableStateFlow("verb_to_be")
    val selectedTopicId: StateFlow<String> = _selectedTopicId.asStateFlow()

    private val _activeLesson = MutableStateFlow<LessonContent?>(CurriculumData.getDefaultLesson("verb_to_be"))
    val activeLesson: StateFlow<LessonContent?> = _activeLesson.asStateFlow()

    private val _isGeneratingLesson = MutableStateFlow(false)
    val isGeneratingLesson: StateFlow<Boolean> = _isGeneratingLesson.asStateFlow()

    private val _lessonError = MutableStateFlow<String?>(null)
    val lessonError: StateFlow<String?> = _lessonError.asStateFlow()

    private val _userQuizAnswer = MutableStateFlow("")
    val userQuizAnswer: StateFlow<String> = _userQuizAnswer.asStateFlow()

    private val _quizFeedback = MutableStateFlow<String?>(null)
    val quizFeedback: StateFlow<String?> = _quizFeedback.asStateFlow()

    private val _isCheckingQuiz = MutableStateFlow(false)
    val isCheckingQuiz: StateFlow<Boolean> = _isCheckingQuiz.asStateFlow()

    fun selectTopic(topicId: String) {
        _selectedTopicId.value = topicId
        _activeLesson.value = CurriculumData.getDefaultLesson(topicId)
        learnerProfileManager.recordLessonActivity(topicId, false)
    }

    fun setUserQuizAnswer(answer: String) { _userQuizAnswer.value = answer }

    fun checkPracticeAnswer() {
        val answer = _userQuizAnswer.value.trim()
        if (answer.isEmpty()) return
        val lesson = _activeLesson.value ?: return
        viewModelScope.launch {
            _isCheckingQuiz.value = true
            try {
                val feedback = RetrofitClient.checkPracticeAnswer(
                    question = lesson.practiceQuestion,
                    userAnswer = answer,
                    ruleContext = lesson.englishRule
                )
                _quizFeedback.value = feedback
            } catch (e: Exception) {
                _quizFeedback.value = "Incorrect. Try again!"
            } finally {
                _isCheckingQuiz.value = false
            }
        }
    }

    // --- Tab 2: AI Tutor Chat ---
    private val _chatHistory = MutableStateFlow<List<com.example.data.model.Content>>(emptyList())
    val chatHistory: StateFlow<List<com.example.data.model.Content>> = _chatHistory.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    private val _chatError = MutableStateFlow<String?>(null)
    val chatError: StateFlow<String?> = _chatError.asStateFlow()

    private val _activeTutorMode = MutableStateFlow("General")
    val activeTutorMode: StateFlow<String> = _activeTutorMode.asStateFlow()

    private val _activeLanguage = MutableStateFlow(prefs.getString("tutor_response_language", "Auto") ?: "Auto")
    val activeLanguage: StateFlow<String> = _activeLanguage.asStateFlow()

    private val _isStudyModeOn = MutableStateFlow(prefs.getBoolean("tutor_study_mode_on", false))
    val isStudyModeOn: StateFlow<Boolean> = _isStudyModeOn.asStateFlow()

    private val _detectedInputLanguage = MutableStateFlow("English")
    val detectedInputLanguage: StateFlow<String> = _detectedInputLanguage.asStateFlow()

    private val _effectiveResponseLanguage = MutableStateFlow("English")
    val effectiveResponseLanguage: StateFlow<String> = _effectiveResponseLanguage.asStateFlow()

    private val _userName = MutableStateFlow(prefs.getString("user_name", "Learner") ?: "Learner")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _geminiApiKey = MutableStateFlow(prefs.getString("user_gemini_api_key", "") ?: "")
    val geminiApiKey: StateFlow<String> = _geminiApiKey.asStateFlow()

    fun setUserName(name: String) {
        _userName.value = name
        prefs.edit().putString("user_name", name).apply()
    }

    fun setGeminiApiKey(key: String) {
        val cleanKey = key.trim()
        _geminiApiKey.value = cleanKey
        prefs.edit().putString("user_gemini_api_key", cleanKey).apply()
        RetrofitClient.setCustomApiKey(cleanKey)
    }

    fun setActiveTutorMode(mode: String) { _activeTutorMode.value = mode }

    fun setStudyMode(enabled: Boolean) {
        _isStudyModeOn.value = enabled
        prefs.edit().putBoolean("tutor_study_mode_on", enabled).apply()
    }

    fun setActiveLanguage(lang: String) {
        _activeLanguage.value = lang
        prefs.edit().putString("tutor_response_language", lang).apply()
        val lastMsg = _chatHistory.value.lastOrNull { it.role == "user" }?.parts?.firstOrNull()?.text ?: ""
        val (detected, effective) = LanguageDetector.resolveEffectiveResponseLanguage(lang, lastMsg)
        _detectedInputLanguage.value = detected.label
        _effectiveResponseLanguage.value = effective
    }

    private fun initChatWelcomeMessage() {
        val welcome = "السلام عليكم ورحمة الله وبركاته\n\nWelcome to Lisan Grammar. I will help you learn English grammar by comparing it with Arabic grammar."
        val welcomeContent = com.example.data.model.Content(role = "model", parts = listOf(com.example.data.model.Part(text = welcome)))
        _chatHistory.value = listOf(welcomeContent)
        viewModelScope.launch {
            chatMessageRepository.saveMessage("model", welcome)
        }
    }

    private fun loadStoredChatHistory() {
        viewModelScope.launch {
            val stored = chatMessageRepository.loadMessagesList()
            if (stored.isEmpty()) {
                initChatWelcomeMessage()
            } else {
                _chatHistory.value = stored
                val lastUserMsg = stored.lastOrNull { it.role == "user" }?.parts?.firstOrNull()?.text ?: ""
                val (detected, effective) = LanguageDetector.resolveEffectiveResponseLanguage(_activeLanguage.value, lastUserMsg)
                _detectedInputLanguage.value = detected.label
                _effectiveResponseLanguage.value = effective
            }
        }
    }

    fun sendChatMessage(message: String) {
        if (message.isBlank()) return
        val userContent = com.example.data.model.Content(role = "user", parts = listOf(com.example.data.model.Part(text = message)))
        _chatHistory.value = _chatHistory.value + userContent

        val (detected, effective) = LanguageDetector.resolveEffectiveResponseLanguage(_activeLanguage.value, message)
        _detectedInputLanguage.value = detected.label
        _effectiveResponseLanguage.value = effective
        
        viewModelScope.launch {
            chatMessageRepository.saveMessage("user", message)
            _isChatLoading.value = true
            _chatError.value = null
            try {
                val profileSummary = learnerProfileManager.buildPersonalizedPromptContext()
                val response = RetrofitClient.getTutorResponse(
                    history = _chatHistory.value,
                    mode = _activeTutorMode.value,
                    language = _activeLanguage.value,
                    userName = _userName.value,
                    learnerProfileSummary = profileSummary,
                    isStudyModeOn = _isStudyModeOn.value
                )
                _chatHistory.value = _chatHistory.value + com.example.data.model.Content(role = "model", parts = listOf(com.example.data.model.Part(text = response)))
                chatMessageRepository.saveMessage("model", response)
                learnerProfileManager.recordTutorInteraction(message, response)
            } catch (e: Exception) {
                _chatError.value = "Failed to get response. Please check your connection."
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            chatMessageRepository.clearHistory()
            initChatWelcomeMessage()
        }
    }

    // --- Tab 3: Deep Analyzer ---
    private val _analyzerInput = MutableStateFlow("")
    val analyzerInput: StateFlow<String> = _analyzerInput.asStateFlow()

    private val _analyzerOutput = MutableStateFlow<String?>(null)
    val analyzerOutput: StateFlow<String?> = _analyzerOutput.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _analyzerError = MutableStateFlow<String?>(null)
    val analyzerError: StateFlow<String?> = _analyzerError.asStateFlow()

    fun setAnalyzerInput(input: String) { _analyzerInput.value = input }

    fun runDeepAnalyzer() {
        val input = _analyzerInput.value.trim()
        if (input.isEmpty()) return
        viewModelScope.launch {
            _isAnalyzing.value = true
            _analyzerError.value = null
            try {
                val output = RetrofitClient.analyzeDeepGrammar(input, _proficiencyLevel.value.labelEn)
                _analyzerOutput.value = output
                learnerProfileManager.recordAnalyzerRequest(input)
            } catch (e: Exception) {
                _analyzerError.value = e.message
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    // --- Tab 4: Idioms & Expressions ---
    private val _idiomSearchQuery = MutableStateFlow("")
    val idiomSearchQuery: StateFlow<String> = _idiomSearchQuery.asStateFlow()

    private val _selectedIdiomLevel = MutableStateFlow<IdiomLevel?>(null)
    val selectedIdiomLevel: StateFlow<IdiomLevel?> = _selectedIdiomLevel.asStateFlow()

    private val _selectedIdiomCategory = MutableStateFlow<IdiomCategory?>(null)
    val selectedIdiomCategory: StateFlow<IdiomCategory?> = _selectedIdiomCategory.asStateFlow()

    val savedIdiomIds: StateFlow<Set<String>> = savedIdiomRepository.allSavedIdioms
        .map { it.map { item -> item.idiomId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    val filteredIdioms: StateFlow<List<Idiom>> = combine(
        _idiomSearchQuery,
        _selectedIdiomLevel,
        _selectedIdiomCategory
    ) { query, level, category ->
        IdiomRepository.idioms.filter { idiom ->
            val matchesSearch = if (query.isBlank()) {
                true
            } else {
                val q = query.trim().lowercase()
                idiom.expression.lowercase().contains(q) ||
                idiom.simpleMeaning.lowercase().contains(q) ||
                idiom.urduMeaning.lowercase().contains(q) ||
                idiom.arabicMeaning.lowercase().contains(q) ||
                idiom.example.lowercase().contains(q) ||
                idiom.category.label.lowercase().contains(q) ||
                idiom.level.label.lowercase().contains(q)
            }
            matchesSearch &&
            (level == null || idiom.level == level) &&
            (category == null || idiom.category == category)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, IdiomRepository.idioms)

    fun setIdiomSearchQuery(query: String) { _idiomSearchQuery.value = query }
    fun setIdiomLevelFilter(level: IdiomLevel?) { _selectedIdiomLevel.value = level }
    fun setIdiomCategoryFilter(category: IdiomCategory?) { _selectedIdiomCategory.value = category }
    fun clearIdiomFilters() {
        _idiomSearchQuery.value = ""
        _selectedIdiomLevel.value = null
        _selectedIdiomCategory.value = null
    }

    fun toggleIdiomSaved(idiomId: String) {
        viewModelScope.launch {
            if (savedIdiomIds.value.contains(idiomId)) {
                savedIdiomRepository.deleteIdiom(idiomId)
            } else {
                savedIdiomRepository.saveIdiom(idiomId)
            }
        }
    }

    fun playIdiomAudio(idiomId: String) {
        val idiom = IdiomRepository.idioms.find { it.id == idiomId }
        if (idiom != null) {
            val currentlySpeaking = currentlySpeakingText.value
            if (currentlySpeaking == idiomId) {
                stopSpeech()
            } else {
                speak("${idiom.expression}. ${idiom.urduMeaning}", idiomId)
            }
        }
    }

    // --- Quiz Module ---
    private val _quizCategoryFilter = MutableStateFlow("ALL")
    val quizCategoryFilter: StateFlow<String> = _quizCategoryFilter.asStateFlow()

    private val _currentQuizIndex = MutableStateFlow(0)
    val currentQuizIndex: StateFlow<Int> = _currentQuizIndex.asStateFlow()

    private val _selectedQuizOption = MutableStateFlow<Int?>(null)
    val selectedQuizOption: StateFlow<Int?> = _selectedQuizOption.asStateFlow()

    private val _isQuizAnswerSubmitted = MutableStateFlow(false)
    val isQuizAnswerSubmitted: StateFlow<Boolean> = _isQuizAnswerSubmitted.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _quizStreak = MutableStateFlow(0)
    val quizStreak: StateFlow<Int> = _quizStreak.asStateFlow()

    private val _quizTotalAnswered = MutableStateFlow(0)
    val quizTotalAnswered: StateFlow<Int> = _quizTotalAnswered.asStateFlow()

    fun getFilteredQuestions(): List<QuizQuestion> {
        return if (_quizCategoryFilter.value == "ALL") QuizData.questions
        else QuizData.questions.filter { it.category == _quizCategoryFilter.value }
    }

    fun setQuizCategoryFilter(category: String) {
        _quizCategoryFilter.value = category
        restartQuiz()
    }

    fun selectQuizOption(index: Int) {
        if (_isQuizAnswerSubmitted.value) return
        _selectedQuizOption.value = index
        _isQuizAnswerSubmitted.value = true
        _quizTotalAnswered.value++
        val questions = getFilteredQuestions()
        val current = questions.getOrNull(_currentQuizIndex.value)
        if (current != null) {
            val isCorrect = index == current.correctOptionIndex
            if (isCorrect) {
                _quizScore.value++
                _quizStreak.value++
            } else {
                _quizStreak.value = 0
            }
            val userAnswer = current.options.getOrNull(index) ?: index.toString()
            val correctAnswer = current.options.getOrNull(current.correctOptionIndex) ?: ""
            learnerProfileManager.recordQuestionResult(
                type = "quiz",
                topic = current.category,
                isCorrect = isCorrect,
                userAnswer = userAnswer,
                correctAnswer = correctAnswer
            )
        }
    }

    fun nextQuizQuestion() {
        val questions = getFilteredQuestions()
        if (_currentQuizIndex.value < questions.size - 1) {
            _currentQuizIndex.value++
            _selectedQuizOption.value = null
            _isQuizAnswerSubmitted.value = false
        }
    }

    fun restartQuiz() {
        _currentQuizIndex.value = 0
        _selectedQuizOption.value = null
        _isQuizAnswerSubmitted.value = false
        _quizScore.value = 0
        _quizStreak.value = 0
        _quizTotalAnswered.value = 0
    }

    // --- Progress & Saved Notes ---
    private val _completedTopicIds = MutableStateFlow<Set<String>>(setOf())
    val completedTopicIds: StateFlow<Set<String>> = _completedTopicIds.asStateFlow()

    private val _savedNotes = MutableStateFlow<List<SavedNote>>(emptyList())
    val savedNotes: StateFlow<List<SavedNote>> = _savedNotes.asStateFlow()

    fun toggleTopicCompleted(id: String) {
        val current = _completedTopicIds.value.toMutableSet()
        val isCompleted = !current.contains(id)
        if (current.contains(id)) current.remove(id) else current.add(id)
        _completedTopicIds.value = current
        learnerProfileManager.recordLessonActivity(id, isCompleted)
    }

    fun clearLearningHistory() {
        learnerProfileManager.clearLearningHistory()
    }

    fun toggleMemoryEnabled(enabled: Boolean) {
        learnerProfileManager.toggleMemoryEnabled(enabled)
    }

    fun saveLessonNote(lesson: LessonContent) { /* Room implementation ignored */ }
    fun saveQuizQuestionToNotes(q: QuizQuestion) {}
    fun saveProgressSummaryNote(
        completedCount: Int,
        totalTopics: Int,
        masteryPercentage: Int,
        quizAccuracyPercentage: Int
    ) {}
    fun deleteSavedNote(id: Int) {}

    // --- Daily Grammar ---
    private val _isDailyCompleted = MutableStateFlow(prefs.getBoolean("daily_completed_$todayDateKey", false))
    val isDailyCompleted: StateFlow<Boolean> = _isDailyCompleted.asStateFlow()

    private val _dailyStreak = MutableStateFlow(prefs.getInt("daily_streak_count", 0))
    val dailyStreak: StateFlow<Int> = _dailyStreak.asStateFlow()

    private val _isDailyNotificationEnabled = MutableStateFlow(prefs.getBoolean("daily_notification_enabled", false))
    val isDailyNotificationEnabled: StateFlow<Boolean> = _isDailyNotificationEnabled.asStateFlow()

    fun selectDailyOption(index: Int) {
        setDailyUserAnswer(index.toString())
        submitDailyAnswer()
    }

    fun toggleDailyNotification(enabled: Boolean, context: Context) {
        _isDailyNotificationEnabled.value = enabled
        prefs.edit().putBoolean("daily_notification_enabled", enabled).apply()
        if (enabled) NotificationHelper.showDailyReminderNotification(context)
    }

    // --- TTS Manager ---
    val ttsManager = TutorTtsManager(application.applicationContext)
    val currentlySpeakingText: StateFlow<String?> = ttsManager.currentlySpeaking

    fun speak(text: String, id: String? = null) { ttsManager.speak(text, id) }
    fun stopSpeech() { ttsManager.stop() }

    init {
        val savedApiKey = prefs.getString("user_gemini_api_key", "") ?: ""
        if (savedApiKey.isNotBlank()) {
            RetrofitClient.setCustomApiKey(savedApiKey)
        }
        try {
            setupSpeechRecognizer()
        } catch (e: Exception) {
            // Speech recognizer not available
        }
        loadDailyChallenge()
        observeDailyChallengeResults()
        loadStoredChatHistory()
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
        tutorAudioManager.release()
        speechRecognizer?.destroy()
    }
}
