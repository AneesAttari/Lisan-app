package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.PictureVocabularyRepository
import com.example.data.repository.LisanJourneyRepository
import com.example.utils.PictureTtsManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PictureVocabularyViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val dao = db.pictureVocabularyDao()
    private val repository = PictureVocabularyRepository(dao)
    private val lisanJourneyRepository = LisanJourneyRepository(db.lisanJourneyDao())
    private val ttsManager = PictureTtsManager(application)

    private val _categories = MutableStateFlow<List<PictureVocabularyCategoryEntity>>(emptyList())
    val categories: StateFlow<List<PictureVocabularyCategoryEntity>> = _categories.asStateFlow()

    private val _currentSessionId = MutableStateFlow<String?>(null)
    val currentSessionId: StateFlow<String?> = _currentSessionId.asStateFlow()

    private val _currentSession = MutableStateFlow<PictureVocabularySessionEntity?>(null)
    val currentSession: StateFlow<PictureVocabularySessionEntity?> = _currentSession.asStateFlow()

    private val _sessionQuestions = MutableStateFlow<List<Pair<PictureVocabularySessionQuestionEntity, PictureVocabularyQuestionEntity>>>(emptyList())
    val sessionQuestions: StateFlow<List<Pair<PictureVocabularySessionQuestionEntity, PictureVocabularyQuestionEntity>>> = _sessionQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedOptionId = MutableStateFlow<String?>(null)
    val selectedOptionId: StateFlow<String?> = _selectedOptionId.asStateFlow()

    private val _isAnswerSubmitted = MutableStateFlow(false)
    val isAnswerSubmitted: StateFlow<Boolean> = _isAnswerSubmitted.asStateFlow()

    private val _isCorrectAnswer = MutableStateFlow(false)
    val isCorrectAnswer: StateFlow<Boolean> = _isCorrectAnswer.asStateFlow()

    val isSpeaking = ttsManager.isSpeaking

    init {
        viewModelScope.launch {
            repository.initializeContentIfNeeded()
            repository.getAllCategories().collect { list ->
                _categories.value = list
            }
        }
    }

    fun clearCurrentSession() {
        _currentSessionId.value = null
        _currentSession.value = null
    }

    suspend fun getItemsListForCategory(categoryId: String): List<PictureVocabularyItemEntity> {
        return repository.getItemsListForCategory(categoryId)
    }

    fun startCategoryQuiz(categoryId: String) {
        viewModelScope.launch {
            val sessionId = repository.startSession(categoryId, "CATEGORY_QUIZ")
            _currentSessionId.value = sessionId
            loadSession(sessionId)
        }
    }

    fun startMixedQuiz() {
        viewModelScope.launch {
            val sessionId = repository.startSession(null, "MIXED_QUIZ")
            _currentSessionId.value = sessionId
            loadSession(sessionId)
        }
    }

    fun loadSession(sessionId: String) {
        viewModelScope.launch {
            val (session, questions) = repository.getSessionWithQuestions(sessionId)
            _currentSession.value = session
            _sessionQuestions.value = questions
            _currentQuestionIndex.value = 0
            _selectedOptionId.value = null
            _isAnswerSubmitted.value = false
        }
    }

    fun selectOption(optionId: String) {
        if (!_isAnswerSubmitted.value) {
            _selectedOptionId.value = optionId
        }
    }

    suspend fun getItemsForQuestion(question: PictureVocabularyQuestionEntity): List<PictureVocabularyItemEntity> {
        val optionIds = question.getOptions()
        return optionIds.mapNotNull { dao.getItemById(it) }
    }

    suspend fun ensureUniqueOptionImages(
        items: List<PictureVocabularyItemEntity>,
        question: PictureVocabularyQuestionEntity
    ): List<PictureVocabularyItemEntity> {
        val imageUrls = items.map { it.getEffectiveImageUrl() }
        val words = items.map { it.wordEnglish.lowercase().trim() }
        val uniqueImages = imageUrls.toSet()
        val uniqueWords = words.toSet()

        if (uniqueImages.size == items.size && uniqueWords.size == items.size && items.size == 4) {
            return items
        }

        val categoryItems = dao.getItemsListForCategory(question.categoryId)
        val allItems = if (categoryItems.size >= 4) categoryItems else dao.getAllItemsList()

        val targetItem = items.find { it.id == question.correctItemId } ?: items.firstOrNull() ?: allItems.first()
        val resultList = mutableListOf<PictureVocabularyItemEntity>()
        resultList.add(targetItem)

        val usedUrls = mutableSetOf(targetItem.getEffectiveImageUrl())
        val usedWords = mutableSetOf(targetItem.wordEnglish.lowercase().trim())

        for (candidate in allItems.shuffled()) {
            if (resultList.size >= 4) break
            val candidateUrl = candidate.getEffectiveImageUrl()
            val candidateWord = candidate.wordEnglish.lowercase().trim()

            if (!usedUrls.contains(candidateUrl) && !usedWords.contains(candidateWord)) {
                resultList.add(candidate)
                usedUrls.add(candidateUrl)
                usedWords.add(candidateWord)
            }
        }

        return resultList.shuffled()
    }

    suspend fun getItem(itemId: String): PictureVocabularyItemEntity? {
        return dao.getItemById(itemId)
    }

    fun recordWordLearned(itemId: String) {
        viewModelScope.launch {
            lisanJourneyRepository.recordEvent(
                eventType = "WORD_LEARNED",
                featureSource = "PICTURE_VOCABULARY",
                entityId = itemId,
                deduplicationKey = "word_learned_$itemId"
            )
        }
    }

    fun submitAnswer() {
        val selected = _selectedOptionId.value ?: return
        val questions = _sessionQuestions.value
        val index = _currentQuestionIndex.value
        if (index !in questions.indices) return

        val (sq, q) = questions[index]
        val isCorrect = selected == q.correctItemId

        _isAnswerSubmitted.value = true
        _isCorrectAnswer.value = isCorrect

        val sessionId = _currentSessionId.value ?: return
        viewModelScope.launch {
            repository.submitAnswer(sessionId, q.id, selected, isCorrect)
            val (session, updatedQuestions) = repository.getSessionWithQuestions(sessionId)
            _currentSession.value = session
            _sessionQuestions.value = updatedQuestions

            if (session?.status == "COMPLETED") {
                val catId = session.categoryId ?: "mixed"
                lisanJourneyRepository.recordEvent(
                    eventType = "CATEGORY_COMPLETED",
                    featureSource = "PICTURE_VOCABULARY",
                    entityId = catId,
                    sessionId = sessionId,
                    deduplicationKey = "cat_completed_${catId}_$sessionId"
                )
            }
        }
    }

    fun nextQuestion() {
        val questions = _sessionQuestions.value
        if (_currentQuestionIndex.value < questions.size - 1) {
            _currentQuestionIndex.value += 1
            _selectedOptionId.value = null
            _isAnswerSubmitted.value = false
        }
    }

    fun speakWord(word: String) {
        ttsManager.speak(word)
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
