package com.example.data.repository

import com.example.data.model.DailyChallengeQuestion
import com.example.data.model.DailyChallengeQuestionType
import java.text.SimpleDateFormat
import java.util.*

object DailyGrammarRepository {

    private val allQuestions = listOf(
        DailyChallengeQuestion(
            id = "dg_1",
            topic = "Present Simple",
            type = DailyChallengeQuestionType.MCQ,
            questionText = "She _____ to school every day.",
            options = listOf("go", "goes", "going", "gone"),
            correctOptionIndex = 1,
            explanationEn = "Use 'goes' because 'she' is third-person singular in the Present Simple tense.",
            explanationUr = "Present Simple میں 'she' کے ساتھ 'goes' کا استعمال کریں کیونکہ یہ third-person singular ہے۔",
            arabicReference = "Likewise in Arabic, you say 'تذهبُ' (Tadhhabu) for 'she goes', reflecting the singular feminine form of the present tense (Al-Mudari).",
            extraExample = "Example: He plays football. (وہ فٹ بال کھیلتا ہے۔)",
            urduTranslation = "وہ ہر روز اسکول جاتی ہے۔"
        ),
        DailyChallengeQuestion(
            id = "dg_2",
            topic = "Articles",
            type = DailyChallengeQuestionType.FILL_IN_THE_BLANK,
            questionText = "I saw _____ elephant at the zoo.",
            acceptedAnswers = listOf("an"),
            explanationEn = "Use 'an' before words starting with a vowel sound.",
            explanationUr = "ان الفاظ سے پہلے 'an' استعمال کریں جو vowel آواز سے شروع ہوتے ہیں۔",
            arabicReference = "Likewise in Arabic, you learned about 'Tanween' (فيلًا - Filan) where the nunation indicates an indefinite noun, similar to the English indefinite article 'a/an'.",
            extraExample = "Example: An apple a day... (روزانہ ایک سیب...)",
            urduTranslation = "میں نے چڑیا گھر میں ایک ہاتھی دیکھا۔"
        ),
        DailyChallengeQuestion(
            id = "dg_3",
            topic = "Prepositions",
            type = DailyChallengeQuestionType.MCQ,
            questionText = "The book is _____ the table.",
            options = listOf("in", "on", "at", "under"),
            correctOptionIndex = 1,
            explanationEn = "Use 'on' for surfaces.",
            explanationUr = "سطح کے لیے 'on' کا استعمال کریں۔",
            arabicReference = "Likewise in Arabic, you say 'على الطاولةِ' (Ala al-tawilah) using the preposition 'على' (Ala) for indicating position on a surface.",
            extraExample = "Example: The pen is on the notebook. (قلم کاپی پر ہے۔)",
            urduTranslation = "کتاب میز پر ہے۔"
        ),
        DailyChallengeQuestion(
            id = "dg_4",
            topic = "Past Simple",
            type = DailyChallengeQuestionType.FILL_IN_THE_BLANK,
            questionText = "They _____ to London last year.",
            acceptedAnswers = listOf("went"),
            explanationEn = "The past tense of 'go' is 'went'.",
            explanationUr = "'go' کی ماضی کی شکل 'went' ہے۔",
            arabicReference = "Likewise in Arabic, you say 'ذهبوا' (Dhahabu) for 'they went', which is the past tense (Madi) form of the verb.",
            extraExample = "Example: I saw him yesterday. (میں نے اسے کل دیکھا تھا۔)",
            urduTranslation = "وہ پچھلے سال لندن گئے تھے۔"
        ),
        DailyChallengeQuestion(
            id = "dg_5",
            topic = "Sentence Correction",
            type = DailyChallengeQuestionType.SENTENCE_CORRECTION,
            questionText = "Correct the sentence: 'He don't like apples.'",
            acceptedAnswers = listOf("He doesn't like apples", "He does not like apples"),
            explanationEn = "With 'he', use 'doesn't' or 'does not'.",
            explanationUr = "'he' کے ساتھ 'doesn't' یا 'does not' استعمال کریں۔",
            arabicReference = "Likewise in Arabic, you say 'لا يحبُّ' (La yuhibbu) where 'لا' (La) is used for negation in the present tense, similar to 'does not'.",
            extraExample = "Example: She doesn't speak French. (وہ فرانسیسی نہیں بولتی۔)",
            urduTranslation = "جملہ درست کریں: 'اسے سیب پسند نہیں ہیں۔'"
        ),
        DailyChallengeQuestion(
            id = "dg_6",
            topic = "Future Forms",
            type = DailyChallengeQuestionType.MCQ,
            questionText = "I _____ visit you tomorrow.",
            options = listOf("will", "shall", "am", "going"),
            correctOptionIndex = 0,
            explanationEn = "Use 'will' for future promises or plans.",
            explanationUr = "مستقبل کے وعدوں یا منصوبوں کے لیے 'will' استعمال کریں۔",
            arabicReference = "Likewise in Arabic, you say 'سأزورُكَ' (Sa-azuruka) where the prefix 'سـ' (sa) or the word 'سوف' (sawfa) indicates the future tense.",
            extraExample = "Example: I will help you. (میں آپ کی مدد کروں گا۔)",
            urduTranslation = "میں کل آپ سے ملنے آؤں گا۔"
        ),
        DailyChallengeQuestion(
            id = "dg_7",
            topic = "Vocabulary",
            type = DailyChallengeQuestionType.FILL_IN_THE_BLANK,
            questionText = "A person who treats sick people is a _____.",
            acceptedAnswers = listOf("doctor", "physician"),
            explanationEn = "A doctor treats patients.",
            explanationUr = "ڈاکٹر مریضوں کا علاج کرتا ہے۔",
            arabicReference = "Likewise in Arabic, you say 'طبيب' (Tabib) for someone who heals others, derived from the root 'ط ب ب'.",
            extraExample = "Example: The doctor is very kind. (ڈاکٹر بہت مہربان ہے۔)",
            urduTranslation = "بیمار لوگوں کا علاج کرنے والا شخص ڈاکٹر ہوتا ہے۔"
        ),
        DailyChallengeQuestion(
            id = "dg_8",
            topic = "Idioms",
            type = DailyChallengeQuestionType.MCQ,
            questionText = "What does 'break a leg' mean?",
            options = listOf("To get injured", "Good luck", "To be angry", "To run fast"),
            correctOptionIndex = 1,
            explanationEn = "'Break a leg' is a common idiom meaning 'Good luck'.",
            explanationUr = "'Break a leg' ایک عام محاورہ ہے جس کا مطلب ہے 'نیک خواہشات'۔",
            arabicReference = "Likewise in Arabic, you say 'بالتوفيق' (Bi-tawfiq) or 'حظًا سعيدًا' (Hazzan sa'idan) to wish someone good luck.",
            extraExample = "Example: Break a leg on your exam! (اپنے امتحان میں کامیاب ہو!)",
            urduTranslation = "'Break a leg' کا کیا مطلب ہے؟"
        ),
        DailyChallengeQuestion(
            id = "dg_9",
            topic = "Modal Verbs",
            type = DailyChallengeQuestionType.FILL_IN_THE_BLANK,
            questionText = "You _____ wear a seatbelt while driving.",
            acceptedAnswers = listOf("must", "should", "ought to"),
            explanationEn = "'Must' or 'should' express obligation or recommendation.",
            explanationUr = "'Must' یا 'should' فرض یا مشورے کا اظہار کرتے ہیں۔",
            arabicReference = "Likewise in Arabic, you say 'يجبُ أن' (Yajibu an) or 'عليك أن' (Alaika an) to express obligation or necessity.",
            extraExample = "Example: We must respect our parents. (ہمیں اپنے والدین کا احترام کرنا چاہیے۔)",
            urduTranslation = "ڈرائیونگ کے دوران آپ کو سیٹ بیلٹ پہننا چاہیے۔"
        ),
        DailyChallengeQuestion(
            id = "dg_10",
            topic = "Question Forms",
            type = DailyChallengeQuestionType.MCQ,
            questionText = "_____ are you going?",
            options = listOf("Who", "What", "Where", "When"),
            correctOptionIndex = 2,
            explanationEn = "Use 'Where' for asking about locations.",
            explanationUr = "مقامات کے بارے میں پوچھنے کے لیے 'Where' استعمال کریں۔",
            arabicReference = "Likewise in Arabic, you say 'أينَ' (Ayna) for asking about place or location.",
            extraExample = "Example: Where do you live? (آپ کہاں رہتے ہیں؟)",
            urduTranslation = "آپ کہاں جا رہے ہیں؟"
        )
    )

    fun getDailyQuestions(): List<DailyChallengeQuestion> {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.US)
        val dateStr = sdf.format(Date())
        val seed = dateStr.toLong()
        val random = Random(seed)
        
        // Select 5 varied questions
        return allQuestions.shuffled(random).take(5)
    }
}
