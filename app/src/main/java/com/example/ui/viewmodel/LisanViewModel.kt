package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.SavedNoteRepository
import com.example.data.model.Content
import com.example.data.model.CurriculumData
import com.example.data.model.LessonContent
import com.example.data.model.Part
import com.example.data.model.ProficiencyLevel
import com.example.data.model.SavedNote
import com.example.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LisanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SavedNoteRepository by lazy {
        SavedNoteRepository(AppDatabase.getDatabase(getApplication()).savedNoteDao())
    }
    val savedNotes: StateFlow<List<SavedNote>> by lazy {
        repository.allNotes
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    // --- Proficiency Level & Navigation ---
    private val _proficiencyLevel = MutableStateFlow(ProficiencyLevel.BEGINNER)
    val proficiencyLevel: StateFlow<ProficiencyLevel> = _proficiencyLevel.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    fun setProficiencyLevel(level: ProficiencyLevel) {
        _proficiencyLevel.value = level
        // Send a level update note to chat if history is present
        appendSystemLevelNotification(level)
    }

    fun setSelectedTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    // --- Tab 1: Comparative Lessons ---
    private val _selectedTopicId = MutableStateFlow("verb_to_be")
    val selectedTopicId: StateFlow<String> = _selectedTopicId.asStateFlow()

    private val _activeLesson = MutableStateFlow<LessonContent?>(null)
    val activeLesson: StateFlow<LessonContent?> = _activeLesson.asStateFlow()

    private val _isGeneratingLesson = MutableStateFlow(false)
    val isGeneratingLesson: StateFlow<Boolean> = _isGeneratingLesson.asStateFlow()

    private val _lessonError = MutableStateFlow<String?>(null)
    val lessonError: StateFlow<String?> = _lessonError.asStateFlow()

    // Practice Quiz state inside active lesson
    private val _userQuizAnswer = MutableStateFlow("")
    val userQuizAnswer: StateFlow<String> = _userQuizAnswer.asStateFlow()

    private val _quizFeedback = MutableStateFlow<String?>(null)
    val quizFeedback: StateFlow<String?> = _quizFeedback.asStateFlow()

    private val _isCheckingQuiz = MutableStateFlow(false)
    val isCheckingQuiz: StateFlow<Boolean> = _isCheckingQuiz.asStateFlow()

    fun selectTopic(topicId: String) {
        _selectedTopicId.value = topicId
        _userQuizAnswer.value = ""
        _quizFeedback.value = null
        _lessonError.value = null
        loadDefaultLesson(topicId)
    }

    private fun loadDefaultLesson(topicId: String) {
        val lesson = CurriculumData.getDefaultLesson(topicId)
        _activeLesson.value = lesson
    }

    fun generateCustomLessonWithGemini(topicEn: String, topicAr: String) {
        viewModelScope.launch {
            _isGeneratingLesson.value = true
            _lessonError.value = null
            _userQuizAnswer.value = ""
            _quizFeedback.value = null
            try {
                val generated = RetrofitClient.generateBilingualLesson(
                    topicEn = topicEn,
                    topicAr = topicAr,
                    level = _proficiencyLevel.value.labelEn
                )
                _activeLesson.value = generated
            } catch (e: Exception) {
                _lessonError.value = formatErrorMessage(e, "اس وقت سبق تیار نہیں کیا جا سکا")
            } finally {
                _isGeneratingLesson.value = false
            }
        }
    }

    fun setUserQuizAnswer(answer: String) {
        _userQuizAnswer.value = answer
    }

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
                    ruleContext = "English Rule: ${lesson.englishRule} | Arabic Connection: ${lesson.arabicConnection}"
                )
                _quizFeedback.value = feedback
            } catch (e: Exception) {
                _quizFeedback.value = "✅ تم استلام إجابتك ('$answer')!\n\n💡 الإجابة النموذجية المقترحة:\n${lesson.practiceAnswerHint}\n\nاستمر في الممارسة والتدريب لربط القواعد الإنجليزية بالقواعد العربية بطلاقة."
            } finally {
                _isCheckingQuiz.value = false
            }
        }
    }

    // --- Tab 2: Multi-Turn AI Tutor Chat ---
    private val _chatHistory = MutableStateFlow<List<Content>>(emptyList())
    val chatHistory: StateFlow<List<Content>> = _chatHistory.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    private val _chatError = MutableStateFlow<String?>(null)
    val chatError: StateFlow<String?> = _chatError.asStateFlow()

    private fun initChatWelcomeMessage() {
        val welcome = """
            لسان گرامر میں خوش آمدید! میں آپ کا انگریزی گرامر کا استاد ہوں جو عربی قواعد کے موازنے کے ذریعے سکھاتا ہے۔ 📚 English-Arabic Comparative Grammar Teacher.

            میں یہاں آپ کو انگریزی گرامر سکھانے کے لیے ہوں، اسے براہ راست عربی قواعد (جیسے جملہ اسمیہ، مبتدا اور خبر، حرف تعریف 'ال'، اور حروف شرط) سے جوڑ کر جس سے آپ پہلے سے واقف ہیں۔ تمام وضاحتیں آپ کی مادری زبان اردو میں ہوں گی۔

            • آپ کا منتخب کردہ تعلیمی لیول ہے: **${_proficiencyLevel.value.labelAr} (${_proficiencyLevel.value.labelEn})**۔
            • **آئیے مل کر شروع کریں:** مجھے بتائیں، آپ انگریزی یا عربی کا کون سا ٹاپک پڑھنا چاہتے ہیں؟ یا نیچے دیے گئے سوالات میں سے کوئی ایک منتخب کریں!
        """.trimIndent()

        _chatHistory.value = listOf(
            Content(role = "model", parts = listOf(Part(text = welcome)))
        )
    }

    private fun appendSystemLevelNotification(level: ProficiencyLevel) {
        val current = _chatHistory.value.toMutableList()
        if (current.isNotEmpty()) {
            current.add(
                Content(
                    role = "model",
                    parts = listOf(
                        Part(
                            text = "💡 *تعلیمی لیول اپ ڈیٹ ہو کر: ${level.labelAr} (${level.labelEn}) ہو گیا ہے۔ میں اس کے مطابق اپنی وضاحتوں اور مثالوں کی گہرائی کو ایڈجسٹ کروں گا۔*"
                        )
                    )
                )
            )
            _chatHistory.value = current
        }
    }

    fun sendChatMessage(userText: String) {
        val text = userText.trim()
        if (text.isEmpty()) return

        viewModelScope.launch {
            _isChatLoading.value = true
            _chatError.value = null
            try {
                // Add user message optimistically
                val optimisticList = _chatHistory.value.toMutableList()
                optimisticList.add(Content(role = "user", parts = listOf(Part(text = text))))
                _chatHistory.value = optimisticList

                val (reply, newHistory) = RetrofitClient.sendTutorChatMessage(
                    conversationHistory = _chatHistory.value,
                    newUserMessage = text,
                    level = _proficiencyLevel.value.labelEn
                )
                _chatHistory.value = newHistory
            } catch (e: Exception) {
                _chatError.value = formatErrorMessage(e, "اس وقت ٹیوٹر سے رابطہ نہیں ہو سکا")
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    fun clearChat() {
        initChatWelcomeMessage()
    }

    // --- Tab 3: Deep Grammatical Analyzer ---
    private val _analyzerInput = MutableStateFlow("If I had known about the meeting, I would have arrived early.")
    val analyzerInput: StateFlow<String> = _analyzerInput.asStateFlow()

    private val _analyzerOutput = MutableStateFlow<String?>(
        """
        ### تقابلی گرامر کا نمونہ تجزیہ (Sample Analysis)

        **1. انگریزی گرامر کا تجزیہ (English Grammatical Dissection):**
        • **"If I had known about the meeting,"** -> یہ شرطیہ جملہ ہے (Type 3 Conditional / Unreal Past)۔ اس میں Past Perfect (*had known*) کا استعمال ہوا ہے۔
        • **"I would have arrived early."** -> یہ جزا ہے (Result clause)۔ اس میں Modal Perfect (*would have arrived*) استعمال ہوا ہے۔

        **2. تقابلی عربی تجزیہ اور اعراب (Arabic Comparative Analysis):**
        • عربی ترجمہ: *"لو كنتُ قد علمتُ بالاجتماع، لوصلتُ مبكراً."*
        • یہاں عربی میں حرفِ شرط **"لَوْ"** استعمال ہوا ہے، جو نحو میں: *حرف شرط غیر جازم یفید امتناع الجواب لامتناع الشرط* کہلاتا ہے (یعنی ماضی میں کام نہ ہونے کی وجہ سے جزا بھی واقع نہیں ہوئی)۔
        • لفظ *"لوصلتُ"* میں جو 'ل' ہے، وہ *لام جواب الشرط* ہے۔
        • بنیادی فرق: انگریزی زبان ماضی کے ناممکن حالات کے لیے Past Perfect کا فارمولا استعمال کرتی ہے، جبکہ عربی میں براہ راست ماضی کے فعل کے ساتھ حرفِ شرط **"لو"** لگایا جاتا ہے۔
        """.trimIndent()
    )
    val analyzerOutput: StateFlow<String?> = _analyzerOutput.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _analyzerError = MutableStateFlow<String?>(null)
    val analyzerError: StateFlow<String?> = _analyzerError.asStateFlow()

    fun setAnalyzerInput(text: String) {
        _analyzerInput.value = text
    }

    fun runDeepAnalyzer() {
        val input = _analyzerInput.value.trim()
        if (input.isEmpty()) return

        viewModelScope.launch {
            _isAnalyzing.value = true
            _analyzerError.value = null
            try {
                val result = RetrofitClient.analyzeDeepGrammar(
                    inputSentence = input,
                    level = _proficiencyLevel.value.labelEn
                )
                _analyzerOutput.value = result
            } catch (e: Exception) {
                _analyzerError.value = formatErrorMessage(e, "تفصیلی تجزیہ کرنے میں دشواری پیش آئی ہے")
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    // --- Tab 4: Idioms & Real-World Usage ---
    private val _idiomQuery = MutableStateFlow("Break the ice")
    val idiomQuery: StateFlow<String> = _idiomQuery.asStateFlow()

    private val _idiomOutput = MutableStateFlow<String?>(
        """
        ### انگریزی محاورہ: "Break the ice"
        
        **1. انگریزی معنی اور استعمال (English Meaning & Usage):**
        کسی سماجی محفل میں گفتگو کا آغاز کرنا یا اجنبیوں کے درمیان جھجھک اور دباؤ کو ختم کرنا تاکہ سب پرسکون محسوس کریں۔
        
        **2. عربی اور اردو متبادل (Closest Arabic Equivalent & Urdu Context):**
        • عربی متبادل: **"إذابة الجليد" یا "كسر حاجز الصمت"** (برف پگھلانا یا خاموشی کا تالا توڑنا)۔
        • اردو متبادل: **"برف پگھلانا" یا "جھجھک دور کرنا / بات چیت کا آغاز کرنا"**۔
        
        **3. عملی مثالیں (Bilingual Examples):**
        • *"He told a joke to break the ice at the start of the meeting."*
        • *"اس نے میٹنگ کے شروع میں ماحول کی سنجیدگی دور کرنے (Break the ice / إذابة الجليد) کے لیے ایک لطیفہ سنایا۔"*
        • **نوٹ:** اگر ہم اس کا لفظی ترجمہ کریں تو یہ عجیب لگے گا۔ ایسے محاورات کو عربی اور اردو کے ثقافتی متبادلات کے ساتھ سمجھنا ہی بہترین طریقہ ہے۔
        """.trimIndent()
    )
    val idiomOutput: StateFlow<String?> = _idiomOutput.asStateFlow()

    private val _isSearchingIdiom = MutableStateFlow(false)
    val isSearchingIdiom: StateFlow<Boolean> = _isSearchingIdiom.asStateFlow()

    private val _idiomError = MutableStateFlow<String?>(null)
    val idiomError: StateFlow<String?> = _idiomError.asStateFlow()

    init {
        loadDefaultLesson("verb_to_be")
        initChatWelcomeMessage()
    }

    fun setIdiomQuery(query: String) {
        _idiomQuery.value = query
    }

    fun searchIdiom() {
        val q = _idiomQuery.value.trim()
        if (q.isEmpty()) return

        viewModelScope.launch {
            _isSearchingIdiom.value = true
            _idiomError.value = null
            try {
                val result = RetrofitClient.searchIdiomAndRealWorldUsage(q)
                _idiomOutput.value = result
            } catch (e: Exception) {
                _idiomError.value = formatErrorMessage(e, "تلاش کے دوران خرابی پیش آئی ہے")
            } finally {
                _isSearchingIdiom.value = false
            }
        }
    }

    private fun formatErrorMessage(e: Exception, defaultPrefix: String): String {
        val msg = e.localizedMessage ?: e.message ?: ""
        return if (msg.contains("resource_exhausted", ignoreCase = true) || msg.contains("quota", ignoreCase = true) || msg.contains("limit", ignoreCase = true)) {
            "⚠️ استعمال کی حد ختم ہو گئی ہے (Quota Exceeded / Rate Limit)۔ براہ کرم تھوڑی دیر بعد دوبارہ کوشش کریں یا Gemini API کی سیٹنگز چیک کریں۔"
        } else {
            "$defaultPrefix: $msg"
        }
    }
    fun saveLessonNote(lesson: LessonContent) {
        viewModelScope.launch {
            val note = SavedNote(
                titleEn = lesson.topicTitleEn,
                titleAr = lesson.topicTitleAr,
                englishText = "English Rule: ${lesson.englishRule}",
                arabicComparisonText = "Arabic Connection (الفصحى): ${lesson.arabicConnection}\n\nCommon Mistake: ${lesson.commonMistake}",
                category = "درس مقارن • Lesson"
            )
            repository.insert(note)
        }
    }

    fun saveCustomNote(titleEn: String, titleAr: String, contentEn: String, contentAr: String, category: String) {
        viewModelScope.launch {
            val note = SavedNote(
                titleEn = titleEn,
                titleAr = titleAr,
                englishText = contentEn,
                arabicComparisonText = contentAr,
                category = category
            )
            repository.insert(note)
        }
    }

    fun deleteSavedNote(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }
}
