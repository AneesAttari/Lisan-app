package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class ProficiencyLevel(val labelEn: String, val labelUr: String, val labelAr: String) {
    BEGINNER("Beginner", "ابتدائی (Beginner)", "المبتدئ"),
    INTERMEDIATE("Intermediate", "متوسط (Intermediate)", "المتوسط"),
    ADVANCED("Advanced", "اعلیٰ (Advanced)", "المتقدم")
}

@Serializable
data class GrammarTopic(
    val id: String,
    val titleEn: String,
    val titleUr: String,
    val category: String,
    val quickComparison: String, // Phrased: "Likewise in Arabic, you have learned..."
    val level: String
)

@Serializable
data class BilingualExample(
    val english: String,
    val urdu: String,
    val note: String = "" // "Likewise in Arabic, you say..."
)

@Serializable
data class LessonContent(
    val topicTitleEn: String,
    val topicTitleUr: String,
    val englishRule: String, // Simple English explanation
    val ruleUrdu: String, // Urdu translation of the rule
    val arabicGrammarComparison: String, // "Likewise in Arabic, you have learned..."
    val similarities: List<String>,
    val differences: List<String>,
    val examples: List<BilingualExample>,
    val commonMistake: String, // Explained in Urdu
    val practiceQuestion: String,
    val practiceAnswerHint: String = "",
    val mcqs: List<LessonMcq> = emptyList()
)

object CurriculumData {
    val topics = listOf(
        // Foundations
        GrammarTopic(
            id = "verb_to_be",
            titleEn = "Verb 'To Be' (Am/Is/Are)",
            titleUr = "فعلِ ربط (Verb To Be)",
            category = "بنیادی قواعد • Foundations",
            quickComparison = "انگریزی جملوں میں 'am/is/are' جیسے فعلِ ربط کا ہونا لازمی ہے۔ جیسے آپ نے عربی میں الجملة الاسمية کے بارے میں سیکھا ہے، جہاں حال میں کسی فعل کی ضرورت نہیں ہوتی۔",
            level = "Beginner"
        ),
        GrammarTopic(
            id = "articles_a_an_the",
            titleEn = "Definite & Indefinite Articles",
            titleUr = "حروفِ تنکیر و تعریف (A / An / The)",
            category = "بنیادی قواعد • Foundations",
            quickComparison = "انگریزی کا 'The' مخصوص اسموں کے لیے ہے۔ جیسے آپ نے عربی میں نکرہ (تنوین) اور معرفہ (أل التعريف) کا قانون سیکھا ہے۔",
            level = "Beginner"
        ),

        // 12 Tenses
        GrammarTopic(
            id = "present_simple",
            titleEn = "Present Simple Tense",
            titleUr = "فعلِ حال سادہ (Present Simple)",
            category = "زمانے • Tenses",
            quickComparison = "روزمرہ عادات اور دائمی حقائق کے لیے۔ جیسے آپ نے عربی میں 'الفعل المضارع' کے بارے میں سیکھا ہے۔",
            level = "Beginner"
        ),
        GrammarTopic(
            id = "present_continuous",
            titleEn = "Present Continuous Tense",
            titleUr = "فعلِ حال جاری (Present Continuous)",
            category = "زمانے • Tenses",
            quickComparison = "فی الحال جاری کاموں کے لیے۔ جیسے عربی میں 'الفعل المضارع' کے ساتھ 'الآنَ' یا اسم الفاعل کا استعمال۔",
            level = "Intermediate"
        ),
        GrammarTopic(
            id = "present_perfect",
            titleEn = "Present Perfect Tense",
            titleUr = "فعلِ حال مکمل (Present Perfect)",
            category = "زمانے • Tenses",
            quickComparison = "حال ہی میں مکمل ہونے والے کاموں کے لیے جن کا اثر موجود ہو۔ جیسے عربی میں 'قَدْ + الفعل الماضي'۔",
            level = "Intermediate"
        ),
        GrammarTopic(
            id = "present_perfect_continuous",
            titleEn = "Present Perfect Continuous Tense",
            titleUr = "فعلِ حال مکمل جاری (Present Perfect Continuous)",
            category = "زمانے • Tenses",
            quickComparison = "ماضی میں شروع ہو کر اب تک جاری کاموں کے لیے (For/Since کا استعمال)۔ جیسے عربی میں 'منذ' کا استعمال۔",
            level = "Intermediate"
        ),
        GrammarTopic(
            id = "past_simple",
            titleEn = "Past Simple Tense",
            titleUr = "فعلِ ماضی سادہ (Past Simple)",
            category = "زمانے • Tenses",
            quickComparison = "ماضی کے مکمل کاموں کے لیے۔ جیسے آپ نے عربی میں 'الفعل الماضي' کے بارے میں سیکھا ہے۔",
            level = "Beginner"
        ),
        GrammarTopic(
            id = "past_continuous",
            titleEn = "Past Continuous Tense",
            titleUr = "فعلِ ماضی جاری (Past Continuous)",
            category = "زمانے • Tenses",
            quickComparison = "ماضی میں کسی مخصوص وقت پر جاری کام کے لیے۔ جیسے عربی میں 'كانَ + الفعل المضارع' (مثلاً: كانَ يكتبُ)۔",
            level = "Intermediate"
        ),
        GrammarTopic(
            id = "past_perfect",
            titleEn = "Past Perfect Tense",
            titleUr = "فعلِ ماضی مکمل (Past Perfect)",
            category = "زمانے • Tenses",
            quickComparison = "ماضی میں کسی دوسرے کام سے پہلے مکمل ہو جانے والے کام کے لیے۔ جیسے عربی میں 'كانَ قَدْ + الفعل الماضي'۔",
            level = "Advanced"
        ),
        GrammarTopic(
            id = "past_perfect_continuous",
            titleEn = "Past Perfect Continuous Tense",
            titleUr = "فعلِ ماضی مکمل جاری (Past Perfect Continuous)",
            category = "زمانے • Tenses",
            quickComparison = "ماضی کے کسی وقت تک ایک مدت سے جاری رہنے والے کام کے لیے۔",
            level = "Advanced"
        ),
        GrammarTopic(
            id = "future_simple",
            titleEn = "Future Simple Tense",
            titleUr = "فعلِ مستقبل سادہ (Future Simple)",
            category = "زمانے • Tenses",
            quickComparison = "مستقبل کے فیصلوں یا پیشگوئیوں کے لیے۔ جیسے عربی میں فعل کے شروع میں 'سـ' یا 'سوف' کا اضافہ۔",
            level = "Beginner"
        ),
        GrammarTopic(
            id = "future_continuous",
            titleEn = "Future Continuous Tense",
            titleUr = "فعلِ مستقبل جاری (Future Continuous)",
            category = "زمانے • Tenses",
            quickComparison = "مستقبل میں کسی طے شدہ وقت پر جاری رہنے والے کام کے لیے۔ جیسے عربی میں 'سيكونُ + اسم الفاعل/المضارع'۔",
            level = "Intermediate"
        ),
        GrammarTopic(
            id = "future_perfect",
            titleEn = "Future Perfect Tense",
            titleUr = "فعلِ مستقبل مکمل (Future Perfect)",
            category = "زمانے • Tenses",
            quickComparison = "مستقبل کے کسی طے شدہ وقت تک مکمل ہو جانے والے کام کے لیے۔ جیسے عربی میں 'سيكونُ قد + الماضي'۔",
            level = "Advanced"
        ),
        GrammarTopic(
            id = "future_perfect_continuous",
            titleEn = "Future Perfect Continuous Tense",
            titleUr = "فعلِ مستقبل مکمل جاری (Future Perfect Continuous)",
            category = "زمانے • Tenses",
            quickComparison = "مستقبل کے کسی وقت تک ایک مخصوص مدت سے جاری کام کے لیے۔",
            level = "Advanced"
        ),

        // Active & Passive Voice
        GrammarTopic(
            id = "passive_voice",
            titleEn = "Active & Passive Voice — Foundations",
            titleUr = "فعلِ معروف و مجہول - بنیاد (Foundations)",
            category = "فعلِ معروف و مجہول • Active & Passive",
            quickComparison = "مفعول پر توجہ دینے اور فاعل کو مجہول رکھنے کے لیے۔ جیسے عربی میں 'المبني للمجهول' اور نائب فاعل۔",
            level = "Intermediate"
        ),
        GrammarTopic(
            id = "passive_voice_conversion",
            titleEn = "Active & Passive Voice — Complete Conversion",
            titleUr = "فعلِ معروف و مجہول - تبدیلیاں (Complete Conversion)",
            category = "فعلِ معروف و مجہول • Active & Passive",
            quickComparison = "تمام زمانوں، Modals اور Imperative جملوں کو Active سے Passive میں تبدیل کرنے کے جامع قواعد۔",
            level = "Advanced"
        ),

        // Direct & Indirect Speech
        GrammarTopic(
            id = "direct_indirect_foundations",
            titleEn = "Direct & Indirect Speech — Foundations",
            titleUr = "بلاواسطہ و بالواسطہ - بنیاد (Foundations)",
            category = "بلاواسطہ و بالواسطہ • Direct & Indirect Speech",
            quickComparison = "کسی کی بات کو اپنے الفاظ میں بیان کرنا (Reported Speech)۔ ضمائر، زمانوں اور الفاظ کی تبدیلیاں۔",
            level = "Intermediate"
        ),
        GrammarTopic(
            id = "direct_indirect_sentences",
            titleEn = "Direct & Indirect Speech — Complete Sentence Types",
            titleUr = "بلاواسطہ و بالواسطہ - جملوں کی اقسام (Sentence Types)",
            category = "بلاواسطہ و بالواسطہ • Direct & Indirect Speech",
            quickComparison = "سوالیہ، حکمیہ، التجائیہ، استعجابیہ اور دعائیہ جملوں کو Direct سے Indirect میں تبدیل کرنے کا طریقہ۔",
            level = "Advanced"
        )
    )

    fun getLessonMcqs(topicId: String): List<LessonMcq> {
        return when (topicId) {
            "verb_to_be" -> FoundationsMcqData.verbToBeMcqs
            "articles_a_an_the" -> FoundationsMcqData.articlesMcqs
            "present_simple" -> PresentTensesMcqData.presentSimpleMcqs
            "present_continuous" -> PresentTensesMcqData.presentContinuousMcqs
            "present_perfect" -> PresentTensesMcqData.presentPerfectMcqs
            "present_perfect_continuous" -> PresentTensesMcqData.presentPerfectContinuousMcqs
            "past_simple" -> PastTensesMcqData.pastSimpleMcqs
            "past_continuous" -> PastTensesMcqData.pastContinuousMcqs
            "past_perfect" -> PastTensesMcqData.pastPerfectMcqs
            "past_perfect_continuous" -> PastTensesMcqData.pastPerfectContinuousMcqs
            "future_simple" -> FutureTensesMcqData.futureSimpleMcqs
            "future_continuous" -> FutureTensesMcqData.futureContinuousMcqs
            "future_perfect" -> FutureTensesMcqData.futurePerfectMcqs
            "future_perfect_continuous" -> FutureTensesMcqData.futurePerfectContinuousMcqs
            "passive_voice" -> AdvancedGrammarMcqData.passiveVoiceFoundationsMcqs
            "passive_voice_conversion" -> AdvancedGrammarMcqData.passiveVoiceConversionMcqs
            "direct_indirect_foundations" -> AdvancedGrammarMcqData.directIndirectFoundationsMcqs
            "direct_indirect_sentences" -> AdvancedGrammarMcqData.directIndirectSentencesMcqs
            else -> emptyList()
        }
    }

    fun getDefaultLesson(topicId: String): LessonContent {
        val baseLesson = when (topicId) {
            "verb_to_be" -> LessonContent(
                topicTitleEn = "Verb 'To Be' (Am/Is/Are)",
                topicTitleUr = "فعلِ ربط (Verb To Be)",
                englishRule = "In English, every complete sentence requires a verb. 'Am', 'is', and 'are' connect a subject to its description, role, or state.",
                ruleUrdu = """انگریزی میں ہر جملے میں فعل (Verb) کا ہونا لازمی ہے۔ جب ہم کسی شخص یا چیز کی حالت، پیشے یا تعارف کو ظاہر کرتے ہیں تو 'am', 'is', یا 'are' کا استعمال کرتے ہیں۔

【 1. Formulas & Structures / جملوں کا ڈھانچہ 】
• Affirmative (مثبت): Subject + am/is/are + Complement
  - I am a student.
• Negative (منفی): Subject + am/is/are + not + Complement
  - I am not a student.
• Interrogative (سوالیہ): Am/Is/Are + Subject + Complement?
  - Am I a student?
• Negative Interrogative (منفی سوالیہ): Am/Is/Are + Subject + not + Complement?
  - Am I not a student?
• WH-Questions (سوالیہ الفاظ): WH-Word + am/is/are + Subject + Complement?
  - Why are you happy? / Where is he?

【 2. Subject Rules / فاعل کے قوانین 】
• I -> am (I am a doctor)
• He / She / It / Singular Noun -> is (He is rich)
• We / You / They / Plural Noun -> are (We are ready)""",
                arabicGrammarComparison = "جیسے آپ نے عربی میں سیکھا ہے، 'الجملة الاسمية' (اسمیہ جملے) میں حال کے زمانے میں کسی فعل کی ضرورت نہیں ہوتی (مثلاً: 'أنا طالبٌ' یا 'زيدٌ عالمٌ')۔ لیکن انگریزی میں اس کی جگہ 'Am/Is/Are' لگانا فرض ہے۔",
                similarities = listOf(
                    "دونوں زبانوں میں مبتدا (Subject) اور خبر (Predicate) کا رابطہ دکھایا جاتا ہے",
                    "دونوں میں واحد (Singular) اور جمع (Plural) کے لحاظ سے شکلیں تبدیل ہوتی ہیں"
                ),
                differences = listOf(
                    "انگریزی میں فعلِ ربط (Am/Is/Are) کا لکھنا لازمی ہے (جیسے: I am a student)",
                    "عربی میں حال کے اسمیہ جملے میں فعل نہیں لکھا جاتا (أنا طالبٌ = بغیر فعل کے مکمل جملہ)"
                ),
                examples = listOf(
                    BilingualExample("I am a student.", "میں ایک طالب علم ہوں۔", "Affirmative (مثبت) - عربی: أنا طالبٌ"),
                    BilingualExample("She is not afraid.", "وہ خوفزدہ نہیں ہے۔", "Negative (منفی)"),
                    BilingualExample("Are they ready?", "کیا وہ تیار ہیں؟", "Interrogative (سوالیہ)"),
                    BilingualExample("Is he not your brother?", "کیا وہ آپ کا بھائی نہیں ہے؟", "Negative Interrogative (منفی سوالیہ)"),
                    BilingualExample("Where are you from?", "آپ کہاں سے ہیں؟", "WH-Question (سوالیہ لفظ)"),
                    BilingualExample("Why is she absent today?", "وہ آج غیر حاضر کیوں ہے؟", "WH-Question")
                ),
                commonMistake = "اردو سوچ کر انگریزی میں 'is/am/are' نہ لکھنا، جیسے 'He happy' کہنا جو غلط ہے (درست: 'He is happy')۔",
                practiceQuestion = "خالی جگہ پر کریں: 'They ___ ready for the exam.' (am / is / are)",
                practiceAnswerHint = "They are ready for the exam."
            )

            "articles_a_an_the" -> LessonContent(
                topicTitleEn = "Articles (A / An / The)",
                topicTitleUr = "حروفِ تنکیر و تعریف (A / An / The)",
                englishRule = "We use 'a' and 'an' for singular, non-specific countable nouns. We use 'the' for specific nouns known to the listener or unique things in nature.",
                ruleUrdu = """انگریزی میں اسمِ نکرہ (عام یا غیر مخصوص چیزوں) کے لیے 'a' اور 'an' استعمال ہوتا ہے، جبکہ اسمِ معرفہ (خاص یا معلوم چیزوں) کے لیے 'the' کا استعمال کیا جاتا ہے۔

【 1. Rules & Usage / قواعد 】
• 'a': singular countable nouns starting with a CONSONANT sound (a book, a doctor, a university).
• 'an': singular countable nouns starting with a VOWEL sound (a, e, i, o, u) (an apple, an hour, an honest man).
• 'the': specific nouns, unique things (the sun, the Quran), rivers/oceans, or nouns mentioned a second time.

【 2. Sentences / جملے 】
• Affirmative: I bought a book. The book was interesting.
• Negative: She did not see an apple on the table.
• Interrogative: Did you find the key you lost?""",
                arabicGrammarComparison = "جیسے آپ نے عربی میں سیکھا ہے، اسم نکرہ کے لیے تنوین (ـٌ) اور اسم معرفہ بنانے کے لیے 'أل التعريف' کا استعمال ہوتا ہے (مثلًا: 'کتابٌ' سے 'الکتابُ')۔ انگریزی میں 'a/an' نکرہ اور 'the' معرفہ کو ظاہر کرتا ہے۔",
                similarities = listOf(
                    "دونوں زبانوں میں نکرہ (Indefinite) اور معرفہ (Definite) کا واضح فرق موجود ہے",
                    "معرفہ بنانے سے اسم خاص اور معلوم بن جاتا ہے"
                ),
                differences = listOf(
                    "انگریزی میں الگ الفاظ (a, an, the) استعمال ہوتے ہیں",
                    "عربی میں حرف 'أل' اسم کے شروع میں جڑتا ہے یا آخر میں تنوین آتی ہے"
                ),
                examples = listOf(
                    BilingualExample("A book", "ایک کتاب (نکرہ)", "جیسے عربی میں: کتابٌ (تنوین کے ساتھ)۔"),
                    BilingualExample("The book", "وہ خاص کتاب (معرفہ)", "جیسے عربی میں: الکتابُ (أل التعريف کے ساتھ)۔"),
                    BilingualExample("An apple", "ایک سیب (واول کی آواز)", "جیسے عربی میں: تفاحةٌ۔"),
                    BilingualExample("The sun shines in the sky.", "سورج آسمان میں چمکتا ہے۔", "قانون: فطرت کی واحد اور بے مثل چیزوں کے ساتھ 'The' لگتا ہے۔"),
                    BilingualExample("He is an honest man.", "وہ ایک ایماندار آدمی ہے۔", "قانون: 'honest' میں 'h' خاموش ہے، آواز واول (o) کی ہے اس لیے 'an' لگا۔")
                ),
                commonMistake = "انگریزی میں اسمِ نکرہ کے ساتھ 'a' یا 'an' لگانا بھول جانا، جیسے 'I have book' کہنا (درست: 'I have a book')۔",
                practiceQuestion = "درست آرٹیکل کا انتخاب کریں: 'I saw ___ elephant in the zoo.' (a / an / the)",
                practiceAnswerHint = "I saw an elephant in the zoo."
            )

            "present_simple" -> LessonContent(
                topicTitleEn = "Present Simple Tense",
                topicTitleUr = "فعلِ حال سادہ (Present Simple)",
                englishRule = "Present Simple expresses daily habits, routines, general facts, and permanent truths. Auxiliary verbs 'do' and 'does' are used in negative and interrogative sentences.",
                ruleUrdu = """فعلِ حال سادہ روزمرہ عادات، معمولات، دائمی حقائق اور عمومی سچائیوں کو بیان کرنے کے لیے استعمال ہوتا ہے۔

【 1. Formulas / جملوں کی ساخت 】
• A. Affirmative (مثبت): Subject + V1(s/es) + Object
  - Example: I read a book. / She reads a book.
• B. Negative (منفی): Subject + do/does + not + V1 + Object
  - Example: I do not read a book. / She does not read a book.
• C. Interrogative (سوالیہ): Do/Does + Subject + V1 + Object?
  - Example: Do I read a book? / Does she read a book?
• D. Negative Interrogative (منفی سوالیہ): Do/Does + Subject + not + V1 + Object?
  - Example: Do I not read a book? / Does she not read a book?
• E. WH-Questions (سوالیہ الفاظ): WH-Word + do/does + Subject + V1 + Object?
  - Example: Where do you live? / Why does he study English?

【 2. Helping Verbs & Verb Rules / امدادی افعال اور قوانین 】
• Helping Verbs: 'Do' (I / We / You / They / Plural), 'Does' (He / She / It / Singular).
• Verb Form: Base form (V1).
• Third-Person Singular Rule: When subject is He, She, It, or Singular Noun, add '-s' or '-es' to V1 in Affirmative sentences only!
  - Add '-es' to verbs ending in -ch, -sh, -ss, -x, -z, -o (watch -> watches, go -> goes).
  - Note: After 'does' or 'did', NEVER add '-s/es' to the main verb (e.g., 'Does he go?' NOT 'Does he goes?').

【 3. Tense Comparison / موازنہ 】
• Present Simple (عادات/حقائق): I play cricket every Sunday. (معمول)
• Present Continuous (جاری کام): I am playing cricket right now. (اس وقت جاری)""",
                arabicGrammarComparison = "جیسے آپ نے عربی میں سیکھا ہے، 'الفعل المضارع' حال کی عادات اور عمومی افعال کو ظاہر کرتا ہے۔ عربی میں فاعل کے مطابق علامتِ مضارع (أ، ن، ي، ت) لگتی ہے، جبکہ انگریزی میں صرف He/She/It کے ساتھ 's/es' کا اضافہ ہوتا ہے۔",
                similarities = listOf(
                    "دونوں زمانے عادات، معمولات اور دائمی حقائق کو بیان کرنے کے لیے استعمال ہوتے ہیں",
                    "فاعل کے بدلنے سے فعل کی شکل میں تبدیلی آتی ہے"
                ),
                differences = listOf(
                    "انگریزی میں صرف He/She/It کے ساتھ فعل کے آخر میں 's/es' لگتا ہے",
                    "انگریزی نفی اور سوالیہ میں 'Do/Does' امدادی فعل استعمال کرتی ہے جبکہ مثبت میں کوئی امدادی فعل نہیں ہوتا"
                ),
                examples = listOf(
                    BilingualExample("I read a book.", "میں ایک کتاب پڑھتا ہوں۔", "Affirmative (مثبت)"),
                    BilingualExample("I do not read a book.", "میں کتاب نہیں پڑھتا ہوں۔", "Negative (منفی)"),
                    BilingualExample("Do I read a book?", "کیا میں ایک کتاب پڑھتا ہوں؟", "Interrogative (سوالیہ)"),
                    BilingualExample("Do I not read a book?", "کیا میں کتاب نہیں پڑھتا ہوں؟", "Negative Interrogative (منفی سوالیہ)"),
                    BilingualExample("Where do you live?", "آپ کہاں رہتے ہیں؟", "WH-Question (سوالیہ لفظ)"),
                    BilingualExample("She speaks English fluently.", "وہ روانی سے انگریزی بولتی ہے۔", "Third-person singular (-s کے ساتھ)")
                ),
                commonMistake = "'Does' کے بعد بھی فعل کے ساتھ 's/es' لگا دینا، جیسے 'Does he works?' کہنا (درست: 'Does he work?').",
                practiceQuestion = "جملے کو منفی میں تبدیل کریں: 'He plays cricket every day.'",
                practiceAnswerHint = "He does not play cricket every day."
            )

            "present_continuous" -> LessonContent(
                topicTitleEn = "Present Continuous Tense",
                topicTitleUr = "فعلِ حال جاری (Present Continuous)",
                englishRule = "Present Continuous describes actions that are happening right now at the moment of speaking or temporary situations in progress.",
                ruleUrdu = """فعلِ حال جاری ان کاموں کو بیان کرنے کے لیے استعمال ہوتا ہے جو بات چیت کے وقت جاری ہوں یا عارضی طور پر ہو رہے ہوں۔

【 1. Formulas / جملوں کی ساخت 】
• A. Affirmative (مثبت): Subject + am/is/are + V1(-ing) + Object
  - Example: I am writing a letter. / He is writing a letter.
• B. Negative (منفی): Subject + am/is/are + not + V1(-ing) + Object
  - Example: I am not writing a letter. / He is not writing a letter.
• C. Interrogative (سوالیہ): Am/Is/Are + Subject + V1(-ing) + Object?
  - Example: Am I writing a letter? / Is he writing a letter?
• D. Negative Interrogative (منفی سوالیہ): Am/Is/Are + Subject + not + V1(-ing) + Object?
  - Example: Am I not writing a letter? / Is he not writing a letter?
• E. WH-Questions (سوالیہ الفاظ): WH-Word + am/is/are + Subject + V1(-ing) + Object?
  - Example: What are you doing? / Why is she crying?

【 2. Helping Verbs & Verb Rules / امدادی افعال اور قوانین 】
• Helping Verbs:
  - 'Am' for I
  - 'Is' for He / She / It / Singular Noun
  - 'Are' for We / You / They / Plural Noun
• Verb Form: V1 + ing (Present Participle).

【 3. Tense Comparison / موازنہ 】
• Present Simple: I teach English. (میری مستقل ملازمت/پیشہ)
• Present Continuous: I am teaching English right now. (اس لمحے جاری عمل)""",
                arabicGrammarComparison = "جیسے آپ نے عربی میں سیکھا ہے، 'الفعل المضارع' کے ساتھ 'الآنَ' یا 'اسم الفاعل' استعمال کر کے حالِ جاری کو ظاہر کیا جاتا ہے (مثلاً: 'هو يكتبُ الآنَ' یا 'هو كاتبٌ الآنَ')۔",
                similarities = listOf(
                    "دونوں زبانوں میں گفتگو کے وقت جاری کام کو ظاہر کرنے کی صلاحیت موجود ہے"
                ),
                differences = listOf(
                    "انگریزی میں 'ing' فارم سے پہلے امدادی فعل (is/am/are) لگانا بالکل فرض ہے",
                    "عربی میں صرف فعل مضارع یا اسم الفاعل سے ہی جاری کیفیت ظاہر ہو جاتی ہے"
                ),
                examples = listOf(
                    BilingualExample("I am writing a letter.", "میں ایک خط لکھ رہا ہوں۔", "Affirmative (مثبت)"),
                    BilingualExample("He is not writing a letter.", "وہ خط نہیں لکھ رہا ہے۔", "Negative (منفی)"),
                    BilingualExample("Are they playing football?", "کیا وہ فٹ بال کھیل رہے ہیں؟", "Interrogative (سوالیہ)"),
                    BilingualExample("Is she not coming with us?", "کیا وہ ہمارے ساتھ نہیں آ رہی ہے؟", "Negative Interrogative (منفی سوالیہ)"),
                    BilingualExample("What are you reading now?", "آپ اس وقت کیا پڑھ رہے ہیں؟", "WH-Question (سوالیہ لفظ)"),
                    BilingualExample("Why is the baby crying?", "بچہ کیوں رو رہا ہے؟", "WH-Question")
                ),
                commonMistake = "امدادی فعل (is/am/are) چھوڑ دینا، جیسے 'I writing letter' کہنا (درست: 'I am writing a letter')۔",
                practiceQuestion = "خالی جگہ پر کریں: 'They ___ (study) in the library right now.'",
                practiceAnswerHint = "They are studying in the library right now."
            )

            "present_perfect" -> LessonContent(
                topicTitleEn = "Present Perfect Tense",
                topicTitleUr = "فعلِ حال مکمل (Present Perfect)",
                englishRule = "Present Perfect describes actions completed in the recent past whose results or effects connect directly to the present, or life experiences without specific time.",
                ruleUrdu = """فعلِ حال مکمل ان افعال کے لیے استعمال ہوتا ہے جو ماضی میں مکمل ہو چکے ہوں مگر ان کا اثر یا نتیجہ حال سے جڑا ہو۔

【 1. Formulas / جملوں کی ساخت 】
• A. Affirmative (مثبت): Subject + has/have + V3 (Past Participle) + Object
  - Example: I have eaten food. / He has eaten food.
• B. Negative (منفی): Subject + has/have + not + V3 + Object
  - Example: I have not eaten food. / He has not eaten food.
• C. Interrogative (سوالیہ): Has/Have + Subject + V3 + Object?
  - Example: Have I eaten food? / Has he eaten food?
• D. Negative Interrogative (منفی سوالیہ): Has/Have + Subject + not + V3 + Object?
  - Example: Have I not eaten food? / Has he not eaten food?
• E. WH-Questions (سوالیہ الفاظ): WH-Word + has/have + Subject + V3 + Object?
  - Example: Where have you gone? / What has she done?

【 2. Helping Verbs & Verb Rules / امدادی افعال اور قوانین 】
• Helping Verbs:
  - 'Has' for He / She / It / Singular Noun
  - 'Have' for I / We / You / They / Plural Noun
• Verb Form: Always 3rd Form of Verb (V3 - Past Participle).
• Keywords: already, just, yet, ever, never, so far, recently.

【 3. Tense Comparison / موازنہ 】
• Present Perfect (وقت معلوم نہیں/اثر حال میں ہے): I have bought a car. (کار اب بھی موجود ہے)
• Past Simple (ماضی کا مخصوص وقت): I bought a car yesterday. (کل خریدی تھی)""",
                arabicGrammarComparison = "جیسے آپ نے عربی میں سیکھا ہے، 'قَدْ + الفعل الماضي' قریب کے ماضی اور حال سے جڑے مکمل فعل کو ظاہر کرتا ہے (مثلاً: 'قَدْ كَتَبْتُ' - میں لکھ چکا ہوں)۔ انگریزی کا Present Perfect بالکل یہی کام کرتا ہے۔",
                similarities = listOf(
                    "دونوں میں ماضی کے کام کا اثر حال میں قائم رہنے کا مفہوم پایا جاتا ہے",
                    "کام کے مکمل ہونے کی تصدیق کی جاتی ہے"
                ),
                differences = listOf(
                    "انگریزی میں 'has/have' کے ساتھ 3rd Form (V3) استعمال ہوتی ہے",
                    "عربی میں حرف 'قَدْ' ماضی سے پہلے بڑھایا جاتا ہے"
                ),
                examples = listOf(
                    BilingualExample("I have finished my work.", "میں اپنا کام مکمل کر چکا ہوں۔", "Affirmative (مثبت)"),
                    BilingualExample("She has not received the news.", "اسے خبر نہیں ملی ہے۔", "Negative (منفی)"),
                    BilingualExample("Have you ever visited Makkah?", "کیا آپ نے کبھی مکہ کا سفر کیا ہے؟", "Interrogative (سوالیہ)"),
                    BilingualExample("Has he not completed his essay?", "کیا اس نے اپنا مضمون مکمل نہیں کیا ہے؟", "Negative Interrogative (منفی سوالیہ)"),
                    BilingualExample("What have you cooked today?", "آج آپ نے کیا پکایا ہے؟", "WH-Question (سوالیہ لفظ)"),
                    BilingualExample("They have just arrived.", "وہ ابھی ابھی پہنچے ہیں۔", "Keyword 'just' کے ساتھ")
                ),
                commonMistake = "Present Perfect میں ماضی کا مخصوص وقت (مثلًا yesterday, last year) استعمال کرنا، جیسے 'I have seen him yesterday' (درست: 'I saw him yesterday').",
                practiceQuestion = "درست شکل منتخب کریں: 'She (has finished / finished) her test just now.'",
                practiceAnswerHint = "She has finished her test just now."
            )

            "present_perfect_continuous" -> LessonContent(
                topicTitleEn = "Present Perfect Continuous Tense",
                topicTitleUr = "فعلِ حال مکمل جاری (Present Perfect Continuous)",
                englishRule = "Present Perfect Continuous expresses actions that started at a point or duration in the past and are still continuing up to the present moment.",
                ruleUrdu = """فعلِ حال مکمل جاری ان کاموں کے لیے استعمال ہوتا ہے جو ماضی میں کسی وقت یا مدت سے شروع ہو کر اب تک مسلسل جاری ہوں۔

【 1. Formulas / جملوں کی ساخت 】
• A. Affirmative (مثبت): Subject + has/have + been + V1(-ing) + Object + for/since + Time
  - Example: It has been raining since morning.
• B. Negative (منفی): Subject + has/have + not + been + V1(-ing) + Object + for/since + Time
  - Example: It has not been raining since morning.
• C. Interrogative (سوالیہ): Has/Have + Subject + been + V1(-ing) + Object + for/since + Time?
  - Example: Has it been raining since morning?
• D. Negative Interrogative (منفی سوالیہ): Has/Have + Subject + not + been + V1(-ing) + Object + for/since + Time?
  - Example: Has it not been raining since morning?
• E. WH-Questions (سوالیہ الفاظ): WH-Word + has/have + Subject + been + V1(-ing) + Object + for/since + Time?
  - Example: How long have you been waiting here?

【 2. Helping Verbs & For/Since Rule / امدادی افعال اور 'For/Since' کا قانون 】
• Helping Verbs: 'Has been' (He/She/It/Singular), 'Have been' (I/We/You/They/Plural).
• Verb Form: V1 + ing.
• Use of SINCE (ابتدائی وقت / Point of time):
  - since morning, since 2 PM, since Monday, since 2010, since childhood.
• Use of FOR (مدتِ وقت / Period of time):
  - for 2 hours, for 5 days, for 3 years, for a long time.

【 3. Tense Comparison / موازنہ 】
• Present Perfect (کام کا نتیجہ): I have built a house. (گھر بن چکا ہے)
• Present Perfect Continuous (کام کا تسلسل): I have been building this house for 6 months. (۶ ماہ سے بنا رہا ہوں)""",
                arabicGrammarComparison = "عربی میں اس مفہوم کو ظاہر کرنے کے لیے مضارع کے ساتھ 'منذُ' (since/for) استعمال کیا جاتا ہے (مثلاً: 'أنا أدرسُ منذُ ساعتينِ' - میں دو گھنٹے سے پڑھ رہا ہوں)۔",
                similarities = listOf(
                    "دونوں میں ماضی سے جاری عمل کا رابطہ حال سے قائم رہتا ہے"
                ),
                differences = listOf(
                    "انگریزی میں 'has/have + been + V1(-ing)' کی خاص ترکیب کے ساتھ for/since لگایا جاتا ہے",
                    "عربی میں 'منذ' کا لفظ استعمال ہوتا ہے"
                ),
                examples = listOf(
                    BilingualExample("It has been raining for two hours.", "دو گھنٹے سے بارش ہو رہی ہے۔", "Affirmative (duration 'for')"),
                    BilingualExample("He has not been studying since morning.", "وہ صبح سے نہیں پڑھ رہا ہے۔", "Negative (point of time 'since')"),
                    BilingualExample("Has she been working here for five years?", "کیا وہ یہاں پانچ سال سے کام کر رہی ہے؟", "Interrogative (سوالیہ)"),
                    BilingualExample("Has he not been feeling well since yesterday?", "کیا وہ کل سے بہتر محسوس نہیں کر رہا ہے؟", "Negative Interrogative"),
                    BilingualExample("How long have you been learning English?", "آپ کتنے عرصے سے انگریزی سیکھ رہے ہیں؟", "WH-Question"),
                    BilingualExample("They have been playing cricket for an hour.", "وہ ایک گھنٹے سے کرکٹ کھیل رہے ہیں۔", "Affirmative ('for an hour')")
                ),
                commonMistake = "مدتِ وقت (Period of time) کے ساتھ 'since' لگا دینا، جیسے 'since 2 hours' کہنا (درست: 'for 2 hours')۔",
                practiceQuestion = "خالی جگہ پر کریں: 'She has been living here ___ 2018.' (for / since)",
                practiceAnswerHint = "She has been living here since 2018."
            )

            "past_simple" -> LessonContent(
                topicTitleEn = "Past Simple Tense",
                topicTitleUr = "فعلِ ماضی سادہ (Past Simple)",
                englishRule = "Past Simple expresses actions completed at a specific, definite time in the past. In affirmative sentences we use V2; in negative and questions we use 'did' + V1.",
                ruleUrdu = """فعلِ ماضی سادہ ماضی میں کسی مخصوص وقت پر مکمل ہو جانے والے کاموں کو بیان کرنے کے لیے استعمال ہوتا ہے۔

【 1. Formulas / جملوں کی ساخت 】
• A. Affirmative (مثبت): Subject + V2 (Past Form) + Object
  - Example: I wrote a letter yesterday. / He wrote a letter.
• B. Negative (منفی): Subject + did + not + V1 + Object
  - Example: I did not write a letter. / He did not write a letter.
• C. Interrogative (سوالیہ): Did + Subject + V1 + Object?
  - Example: Did I write a letter? / Did he write a letter?
• D. Negative Interrogative (منفی سوالیہ): Did + Subject + not + V1 + Object?
  - Example: Did I not write a letter? / Did he not write a letter?
• E. WH-Questions (سوالیہ الفاظ): WH-Word + did + Subject + V1 + Object?
  - Example: Where did you go last night? / Why did she leave early?

【 2. Helping Verbs & Verb Rules / امدادی افعال اور قوانین 】
• Helping Verb: 'Did' for all subjects (I, He, She, It, We, You, They).
• CRITICAL RULE:
  - In Affirmative sentences: Use 2nd Form of Verb (V2) without any helping verb (e.g. went, played, wrote).
  - In Negative & Interrogative sentences: After 'Did' or 'Did not', ALWAYS use 1st Form of Verb (V1)! (e.g. 'Did he go?' NOT 'Did he went?').

【 3. Tense Comparison / موازنہ 】
• Past Simple (ماضی میں وقت ختم ہو گیا): I visited London last year. (کلیر ماضی)
• Present Perfect (تلقین/اثر حال میں): I have visited London. (تجربہ ہے، وقت کا ذکر نہیں)""",
                arabicGrammarComparison = "جیسے آپ نے عربی میں سیکھا ہے، 'الفعل الماضي' ماضی کے مکمل افعال کو ظاہر کرتا ہے (مثلاً: 'كَتَبَ' - اس نے لکھا، 'ذَهَبَ' - وہ گیا)۔",
                similarities = listOf(
                    "دونوں ماضی کے مکمل افعال کو بیان کرنے کے لیے بنیادی زمانے ہیں",
                    "جملے میں وقت ماضی میں مکمل واضح ہوتا ہے"
                ),
                differences = listOf(
                    "انگریزی میں نفی اور سوالیہ میں 'Did' کے بعد پہلی فارم (V1) لگتی ہے جبکہ مثبت میں دوسری فارم (V2)",
                    "عربی میں ماضی کی آخر میں ضمائر کی گردان ہوتی ہے (كَتَبْتُ، كَتَبْتَ، كَتَبُوا)"
                ),
                examples = listOf(
                    BilingualExample("I wrote a letter yesterday.", "میں نے کل ایک خط لکھا۔", "Affirmative (V2 'wrote')"),
                    BilingualExample("I did not write a letter.", "میں نے خط نہیں لکھا۔", "Negative ('did not' + V1 'write')"),
                    BilingualExample("Did you write a letter?", "کیا آپ نے خط لکھا؟", "Interrogative ('Did' + V1)"),
                    BilingualExample("Did she not attend the lecture?", "کیا اس نے لیکچر میں شرکت نہیں کی؟", "Negative Interrogative"),
                    BilingualExample("Where did you buy this watch?", "آپ نے یہ گھڑی کہاں سے خریدی؟", "WH-Question"),
                    BilingualExample("They arrived late last night.", "وہ کل رات دیر سے پہنچے۔", "Affirmative (V2 'arrived')")
                ),
                commonMistake = "Did کے بعد دوسری فارم (V2) استعمال کر دینا، جیسے 'Did he went?' کہنا (درست: 'Did he go?').",
                practiceQuestion = "جملہ درست کریں: 'She did not went to school yesterday.'",
                practiceAnswerHint = "She did not go to school yesterday."
            )

            "past_continuous" -> LessonContent(
                topicTitleEn = "Past Continuous Tense",
                topicTitleUr = "فعلِ ماضی جاری (Past Continuous)",
                englishRule = "Past Continuous describes actions that were in progress at a specific moment in the past, or an action interrupted by a shorter Past Simple event.",
                ruleUrdu = """فعلِ ماضی جاری ان کاموں کو بیان کرنے کے لیے استعمال ہوتا ہے جو ماضی میں کسی خاص وقت پر جاری تھے، یا کسی دوسرے کام کے وقت ہو رہے تھے۔

【 1. Formulas / جملوں کی ساخت 】
• A. Affirmative (مثبت): Subject + was/were + V1(-ing) + Object
  - Example: I was reading a book. / They were reading a book.
• B. Negative (منفی): Subject + was/were + not + V1(-ing) + Object
  - Example: I was not reading a book. / They were not reading a book.
• C. Interrogative (سوالیہ): Was/Were + Subject + V1(-ing) + Object?
  - Example: Was I reading a book? / Were they reading a book?
• D. Negative Interrogative (منفی سوالیہ): Was/Were + Subject + not + V1(-ing) + Object?
  - Example: Was I not reading a book? / Were they not reading a book?
• E. WH-Questions (سوالیہ الفاظ): WH-Word + was/were + Subject + V1(-ing) + Object?
  - Example: What were you doing at 8 PM? / Why was he crying?

【 2. Helping Verbs & Interrupted Past / امدادی افعال 】
• Helping Verbs:
  - 'Was' for I / He / She / It / Singular Noun
  - 'Were' for We / You / They / Plural Noun
• Verb Form: V1 + ing.
• Interrupted Past Structure: Past Continuous + when + Past Simple
  - Example: I was sleeping when the phone rang. (جب فون کی گھنٹی بجی تو میں سو رہا تھا)

【 3. Tense Comparison / موازنہ 】
• Past Simple (مکمل عمل): I ate dinner at 8 PM. (۸ بجے کھانا کھایا)
• Past Continuous (جاری عمل): I was eating dinner at 8 PM. (۸ بجے میں کھانا کھا رہا تھا)""",
                arabicGrammarComparison = "جیسے آپ نے عربی میں سیکھا ہے، 'كانَ + الفعل المضارع' ماضی میں جاری کاموں کو ظاہر کرتا ہے (مثلاً: 'كانَ يَكْتُبُ' - وہ لکھ رہا تھا)۔ انگریزی کا Past Continuous یہی کام کرتا ہے۔",
                similarities = listOf(
                    "دونوں زبانوں میں ماضی میں جاری رہنے والی کیفیت دکھائی جاتی ہے"
                ),
                differences = listOf(
                    "انگریزی میں Was/Were کے ساتھ فعل کی 'ing' فارم لگتی ہے",
                    "عربی میں 'كان' کے بعد فعل مضارع لایا جاتا ہے"
                ),
                examples = listOf(
                    BilingualExample("I was reading a book.", "میں کتاب پڑھ رہا تھا۔", "Affirmative (Was + V1-ing)"),
                    BilingualExample("They were not playing football.", "وہ فٹ بال نہیں کھیل رہے تھے۔", "Negative (Were not + V1-ing)"),
                    BilingualExample("Were you watching TV at 8 PM?", "کیا آپ رات ۸ بجے ٹی وی دیکھ رہے تھے؟", "Interrogative"),
                    BilingualExample("Was she not cooking dinner when you arrived?", "جب آپ پہنچے تو کیا وہ رات کا کھانا نہیں پکا رہی تھی؟", "Negative Interrogative"),
                    BilingualExample("What were they doing yesterday evening?", "وہ کل شام کیا کر رہے تھے؟", "WH-Question"),
                    BilingualExample("It was raining heavily all night.", "تمام رات تیز بارش ہو رہی تھی۔", "Affirmative")
                ),
                commonMistake = "فاعل He/She/I کے ساتھ 'were' لگا دینا، جیسے 'He were working' (درست: 'He was working').",
                practiceQuestion = "جملہ مکمل کریں: 'We ___ (sleep) when the lights went out.'",
                practiceAnswerHint = "We were sleeping when the lights went out."
            )

            "past_perfect" -> LessonContent(
                topicTitleEn = "Past Perfect Tense",
                topicTitleUr = "فعلِ ماضی مکمل (Past Perfect)",
                englishRule = "Past Perfect describes an action completed BEFORE another action or time in the past. It is the 'earlier past'.",
                ruleUrdu = """فعلِ ماضی مکمل ایسے کام کو بیان کرتا ہے جو ماضی میں کسی دوسرے کام یا وقت سے پہلے ہی مکمل ہو چکا تھا۔

【 1. Formulas / جملوں کی ساخت 】
• A. Affirmative (مثبت): Subject + had + V3 (Past Participle) + Object
  - Example: I had reached home before it rained.
• B. Negative (منفی): Subject + had + not + V3 + Object
  - Example: I had not reached home before it rained.
• C. Interrogative (سوالیہ): Had + Subject + V3 + Object?
  - Example: Had I reached home before it rained?
• D. Negative Interrogative (منفی سوالیہ): Had + Subject + not + V3 + Object?
  - Example: Had I not reached home before it rained?
• E. WH-Questions (سوالیہ الفاظ): WH-Word + had + Subject + V3 + Object?
  - Example: Why had he left before the meeting started?

【 2. Helping Verbs & Double Past Rule / امدادی افعال اور دوہرے ماضی کا قانون 】
• Helping Verb: 'Had' for ALL subjects (I, He, She, It, We, You, They).
• Verb Form: Always 3rd Form of Verb (V3).
• Double Past Rule (پہلے اور بعد کا کام):
  - Earlier Past Action (پہلے مکمل ہونے والا کام) -> Past Perfect (had + V3)
  - Later Past Action (بعد میں ہونے والا کام) -> Past Simple (V2)
  - Example: The train HAD LEFT (پہلے) before I REACHED (بعد میں) the station.

【 3. Tense Comparison / موازنہ 】
• Past Simple: I cooked food. (میں نے کھانا پکایا - عام ماضی)
• Past Perfect: I had cooked food before guests arrived. (مہمانوں کے آنے سے پہلے میں کھانا پکا چکا تھا)""",
                arabicGrammarComparison = "جیسے آپ نے عربی میں سیکھا ہے، 'كانَ قَدْ + الفعل الماضي' بعید اور مکمل ماضی کو ظاہر کرتا ہے (مثلاً: 'كانَ قَدْ كَتَبَ' - وہ لکھ چکا تھا)۔",
                similarities = listOf(
                    "دونوں میں بعید ماضی اور قبل از وقت مکمل کام کو دکھایا جاتا ہے"
                ),
                differences = listOf(
                    "انگریزی میں 'had' کے ساتھ V3 استعمال ہوتا ہے",
                    "عربی میں 'كان قد' کے بعد فعل ماضی آتا ہے"
                ),
                examples = listOf(
                    BilingualExample("The train had left before I reached.", "میرے پہنچنے سے پہلے ٹرین جا چکی تھی۔", "Affirmative (Earlier action = Had left)"),
                    BilingualExample("I had not seen him before yesterday.", "میں نے کل سے پہلے اسے نہیں دیکھا تھا۔", "Negative"),
                    BilingualExample("Had they finished the exam before time was up?", "کیا وقت ختم ہونے سے پہلے وہ امتحان مکمل کر چکے تھے؟", "Interrogative"),
                    BilingualExample("Had you not met her before the party?", "کیا آپ پارٹی سے پہلے اس سے نہیں ملے تھے؟", "Negative Interrogative"),
                    BilingualExample("Why had he resigned before the announcement?", "اس نے اعلان سے پہلے استعفیٰ کیوں دے دیا تھا؟", "WH-Question"),
                    BilingualExample("The patient had died before the doctor arrived.", "ڈاکٹر کے آنے سے پہلے مریض فوت ہو چکا تھا۔", "Double Past Rule")
                ),
                commonMistake = "تنہا ماضی کے جملے میں بغیر کسی نسبتی ماضی کے 'had' لگا دینا، جیسے 'Yesterday I had bought a car' (درست: 'Yesterday I bought a car').",
                practiceQuestion = "درست شکل منتخب کریں: 'When I arrived, the bus (already left / had already left).' ",
                practiceAnswerHint = "When I arrived, the bus had already left."
            )

            "past_perfect_continuous" -> LessonContent(
                topicTitleEn = "Past Perfect Continuous Tense",
                topicTitleUr = "فعلِ ماضی مکمل جاری (Past Perfect Continuous)",
                englishRule = "Past Perfect Continuous expresses an action that started in the past and continued over a duration up until another point or event in the past.",
                ruleUrdu = """فعلِ ماضی مکمل جاری ایسے کام کو بیان کرتا ہے جو ماضی میں ایک مخصوص وقت یا مدت سے شروع ہو کر ماضی ہی کے کسی دوسرے وقت تک مسلسل جاری رہا ہو۔

【 1. Formulas / جملوں کی ساخت 】
• A. Affirmative (مثبت): Subject + had + been + V1(-ing) + Object + for/since + Time
  - Example: He had been working for three hours before he took a break.
• B. Negative (منفی): Subject + had + not + been + V1(-ing) + Object + for/since + Time
  - Example: He had not been working for three hours.
• C. Interrogative (سوالیہ): Had + Subject + been + V1(-ing) + Object + for/since + Time?
  - Example: Had he been working for three hours?
• D. Negative Interrogative (منفی سوالیہ): Had + Subject + not + been + V1(-ing) + Object + for/since + Time?
  - Example: Had he not been working for three hours?
• E. WH-Questions (سوالیہ الفاظ): WH-Word + had + Subject + been + V1(-ing) + Object + for/since + Time?
  - Example: How long had you been waiting before the train arrived?

【 2. Helping Verbs & Rules / امدادی افعال 】
• Helping Verb: 'Had been' for all subjects.
• Verb Form: V1 + ing.
• Uses 'for' (duration) and 'since' (starting point).""",
                arabicGrammarComparison = "عربی میں اسے ظاہر کرنے کے لیے 'كانَ + الفعل المضارع + منذُ' استعمال کیا جاتا ہے (مثلاً: 'كانَ يدرسُ منذُ ساعتينِ' - وہ دو گھنٹے سے پڑھ رہا تھا)۔",
                similarities = listOf(
                    "دونوں زبانوں میں ماضی میں ایک مدت تک جاری عمل دکھایا جاتا ہے"
                ),
                differences = listOf(
                    "انگریزی میں 'had been + V1(-ing)' کے ساتھ for/since لگتا ہے",
                    "عربی میں 'كان' اور 'منذ' کا استعمال ہوتا ہے"
                ),
                examples = listOf(
                    BilingualExample("He had been working for three hours.", "وہ تین گھنٹے سے کام کر رہا تھا۔", "Affirmative"),
                    BilingualExample("She had not been living there since 2015.", "وہ ۲۰۱۵ء سے وہاں نہیں رہ رہی تھی۔", "Negative"),
                    BilingualExample("Had you been waiting long before the bus came?", "کیا بس آنے سے پہلے آپ کافی دیر سے انتظار کر رہے تھے؟", "Interrogative"),
                    BilingualExample("Had they not been practicing since morning?", "کیا وہ صبح سے مشق نہیں کر رہے تھے؟", "Negative Interrogative"),
                    BilingualExample("How long had he been teaching before retirement?", "ریٹائرمنٹ سے پہلے وہ کتنی دیر سے پڑھا رہا تھا؟", "WH-Question"),
                    BilingualExample("They had been playing cricket for two hours when it rained.", "جب بارش ہوئی تو وہ دو گھنٹے سے کرکٹ کھیل رہے تھے۔", "Interrupted duration")
                ),
                commonMistake = "ماضی کے واقعے کے لیے 'have/has been' لگا دینا، جیسے 'He has been working yesterday' (درست: 'He had been working').",
                practiceQuestion = "خالی جگہ پر کریں: 'They had been playing ___ two hours before the match stopped.' (for / since)",
                practiceAnswerHint = "They had been playing for two hours before the match stopped."
            )

            "future_simple" -> LessonContent(
                topicTitleEn = "Future Simple Tense",
                topicTitleUr = "فعلِ مستقبل سادہ (Future Simple)",
                englishRule = "Future Simple expresses decisions made at the moment of speaking, future predictions, promises, or intentions using 'will' + V1.",
                ruleUrdu = """فعلِ مستقبل سادہ مستقبل میں ہونے والے کاموں، فوری فیصلوں، پیشگوئیوں اور وعدوں کو بیان کرنے کے لیے استعمال ہوتا ہے۔

【 1. Formulas / جملوں کی ساخت 】
• A. Affirmative (مثبت): Subject + will + V1 + Object
  - Example: I will call you tonight. / She will help us.
• B. Negative (منفی): Subject + will + not (won't) + V1 + Object
  - Example: I will not call you tonight. / She will not help us.
• C. Interrogative (سوالیہ): Will + Subject + V1 + Object?
  - Example: Will I call you tonight? / Will she help us?
• D. Negative Interrogative (منفی سوالیہ): Will + Subject + not + V1 + Object?
  - Example: Will I not call you tonight? / Will she not help us?
• E. WH-Questions (سوالیہ الفاظ): WH-Word + will + Subject + V1 + Object?
  - Example: Where will we meet tomorrow? / Why will he leave?

【 2. Helping Verbs & Verb Rules / امدادی افعال 】
• Helping Verb: 'Will' for all subjects. (Note: 'Shall' is used in formal contexts or offers with I/We).
• Verb Form: Base form of verb (V1).

【 3. Tense Comparison / موازنہ 】
• Future Simple (فوری فیصلہ/پیشگوئی): I will help you. (ابھی فیصلہ کیا)
• 'Going to' (پہلے سے طے شدہ منصوبہ): I am going to buy a car next month. (پہلے سے ارادہ ہے)""",
                arabicGrammarComparison = "جیسے آپ نے عربی میں سیکھا ہے، فعلِ مضارع کے شروع میں حرف 'سـ' یا 'سوف' لگانے سے مستقبل کا معنی بنتا ہے (مثلاً: 'سَيَكْتُبُ' یا 'سَوْفَ يَكْتُبُ')۔ انگریزی کا 'Will' بھی یہی کام کرتا ہے۔",
                similarities = listOf(
                    "دونوں میں مستقبل کو ظاہر کرنے کے لیے خاص علامت (Will / سـ / سوف) جوڑی جاتی ہے"
                ),
                differences = listOf(
                    "انگریزی میں 'Will' فاعل کے بعد الگ امدادی فعل کے طور پر آتا ہے",
                    "عربی میں 'سـ' حرف فعل مضارع کے شروع میں جڑ جاتا ہے"
                ),
                examples = listOf(
                    BilingualExample("I will call you tonight.", "میں آج رات آپ کو فون کروں گا۔", "Affirmative (فوری فیصلہ)"),
                    BilingualExample("He will not attend the meeting.", "وہ اجلاس میں شرکت نہیں کرے گا۔", "Negative ('will not / won't')"),
                    BilingualExample("Will you help me with this task?", "کیا آپ اس کام میں میری مدد کریں گے؟", "Interrogative (سوالیہ)"),
                    BilingualExample("Will she not come tomorrow?", "کیا وہ کل نہیں آئے گی؟", "Negative Interrogative"),
                    BilingualExample("Where will we go for vacation?", "ہم چھٹیوں کے لیے کہاں جائیں گے؟", "WH-Question"),
                    BilingualExample("It will rain tomorrow.", "کل بارش ہوگی۔", "Prediction (پیشگوئی)")
                ),
                commonMistake = "Will کے بعد فعل کے ساتھ 'ing' یا 'to' لگانا، جیسے 'Will going' یا 'Will to go' (درست: 'Will go')۔",
                practiceQuestion = "جملہ منفی میں تبدیل کریں: 'They will win the match.'",
                practiceAnswerHint = "They will not win the match. (or They won't win the match.)"
            )

            "future_continuous" -> LessonContent(
                topicTitleEn = "Future Continuous Tense",
                topicTitleUr = "فعلِ مستقبل جاری (Future Continuous)",
                englishRule = "Future Continuous expresses actions that will be in progress at a specific moment or duration in the future.",
                ruleUrdu = """فعلِ مستقبل جاری ان کاموں کو بیان کرنے کے لیے استعمال ہوتا ہے جو مستقبل میں کسی مخصوص وقت پر جاری ہوں گے۔

【 1. Formulas / جملوں کی ساخت 】
• A. Affirmative (مثبت): Subject + will + be + V1(-ing) + Object
  - Example: At 10 AM tomorrow, I will be taking my exam.
• B. Negative (منفی): Subject + will + not + be + V1(-ing) + Object
  - Example: I will not be taking my exam tomorrow.
• C. Interrogative (سوالیہ): Will + Subject + be + V1(-ing) + Object?
  - Example: Will you be taking your exam tomorrow?
• D. Negative Interrogative (منفی سوالیہ): Will + Subject + not + be + V1(-ing) + Object?
  - Example: Will you not be taking your exam tomorrow?
• E. WH-Questions (سوالیہ الفاظ): WH-Word + will + Subject + be + V1(-ing) + Object?
  - Example: What will you be doing at this time tomorrow?

【 2. Helping Verbs & Verb Rules / امدادی افعال 】
• Helping Verb: 'Will be' for all subjects.
• Verb Form: V1 + ing.""",
                arabicGrammarComparison = "جیسے آپ نے عربی میں سیکھا ہے، 'سيكونُ + اسم الفاعل / المضارع' مستقبل میں جاری کیفیت کو ظاہر کرتا ہے (مثلاً: 'سيكونُ يدرسُ غداً' - وہ کل پڑھ رہا ہوگا)۔",
                similarities = listOf(
                    "دونوں زبانوں میں مستقبل کے طے شدہ وقت پر جاری عمل کو دکھایا جاتا ہے"
                ),
                differences = listOf(
                    "انگریزی میں 'will be' کے بعد V1-ing آتا ہے",
                    "عربی میں 'سيكون' کے ساتھ فعل مضارع لایا جاتا ہے"
                ),
                examples = listOf(
                    BilingualExample("At 10 AM tomorrow, I will be taking my exam.", "کل صبح ۱۰ بجے میں اپنا امتحان دے رہا ہوں گا۔", "Affirmative"),
                    BilingualExample("They will not be traveling next week.", "وہ اگلے ہفتے سفر نہیں کر رہے ہوں گے۔", "Negative"),
                    BilingualExample("Will you be working late tonight?", "کیا آپ آج رات دیر تک کام کر رہے ہوں گے؟", "Interrogative"),
                    BilingualExample("Will he not be attending the class?", "کیا وہ کلاس میں حاضر نہیں ہو رہا ہوگا؟", "Negative Interrogative"),
                    BilingualExample("What will she be doing at this time tomorrow?", "وہ کل اس وقت کیا کر رہی ہوگی؟", "WH-Question"),
                    BilingualExample("We will be waiting for you at the airport.", "ہم ہوائی اڈے پر آپ کا انتظار کر رہے ہوں گے۔", "Affirmative")
                ),
                commonMistake = "Will کے بعد 'be' نہ لکھنا، جیسے 'I will working' (درست: 'I will be working').",
                practiceQuestion = "خالی جگہ پر کریں: 'At 8 PM tonight, she ___ (cook) dinner.'",
                practiceAnswerHint = "At 8 PM tonight, she will be cooking dinner."
            )

            "future_perfect" -> LessonContent(
                topicTitleEn = "Future Perfect Tense",
                topicTitleUr = "فعلِ مستقبل مکمل (Future Perfect)",
                englishRule = "Future Perfect expresses an action that will be completely finished before a specific point of time in the future.",
                ruleUrdu = """فعلِ مستقبل مکمل ایسے کام کے لیے استعمال ہوتا ہے جو مستقبل میں کسی طے شدہ وقت یا کام سے پہلے مکمل ہو چکا ہو گا۔

【 1. Formulas / جملوں کی ساخت 】
• A. Affirmative (مثبت): Subject + will + have + V3 (Past Participle) + Object
  - Example: I will have completed the project by Friday.
• B. Negative (منفی): Subject + will + not + have + V3 + Object
  - Example: I will not have completed the project by Friday.
• C. Interrogative (سوالیہ): Will + Subject + have + V3 + Object?
  - Example: Will I have completed the project by Friday?
• D. Negative Interrogative (منفی سوالیہ): Will + Subject + not + have + V3 + Object?
  - Example: Will I not have completed the project by Friday?
• E. WH-Questions (سوالیہ الفاظ): WH-Word + will + Subject + have + V3 + Object?
  - Example: What will he have achieved by the end of this year?

【 2. Helping Verbs & Time Keywords / امدادی افعال 】
• Helping Verb: 'Will have' for ALL subjects (I, He, She, They, etc. NEVER say 'will has').
• Verb Form: Always 3rd Form of Verb (V3).
• Keywords: 'by Friday', 'by next month', 'by 2030', 'by the time...'.""",
                arabicGrammarComparison = "جیسے آپ نے عربی میں سیکھا ہے، 'سيكونُ قَدْ + الفعل الماضي' مستقبل کے طے شدہ وقت پر مکمل افعال کو ظاہر کرتا ہے (مثلاً: 'سيكونُ قد أتمَّ عملَهُ' - وہ اپنا کام مکمل کر چکا ہوگا)۔",
                similarities = listOf(
                    "دونوں میں مستقبل کی مقررہ حد تک کام کی ہٹمی تکمیل دکھائی جاتی ہے"
                ),
                differences = listOf(
                    "انگریزی میں 'will have + V3' کی ترکیب استعمال ہوتی ہے",
                    "عربی میں 'سيكون قد' کے بعد فعل ماضی آتا ہے"
                ),
                examples = listOf(
                    BilingualExample("I will have completed the project by Friday.", "میں جمعہ تک پروجیکٹ مکمل کر چکا ہوں گا۔", "Affirmative ('by Friday')"),
                    BilingualExample("She will not have arrived by 6 PM.", "وہ شام ۶ بجے تک نہیں پہنچی ہوگی۔", "Negative"),
                    BilingualExample("Will they have finished the house by next year?", "کیا وہ اگلے سال تک مکان تعمیر کر چکے ہوں گے؟", "Interrogative"),
                    BilingualExample("Will you not have prepared the report by then?", "کیا آپ تب تک رپورٹ تیار نہیں کر چکے ہوں گے؟", "Negative Interrogative"),
                    BilingualExample("What will he have achieved by 2030?", "وہ ۲۰۳۰ء تک کیا حاصل کر چکا ہوگا؟", "WH-Question"),
                    BilingualExample("By the time you reach, we will have left.", "جب تک آپ پہنچیں گے، ہم جا چکے ہوں گے۔", "Affirmative")
                ),
                commonMistake = "فاعل He/She کے ساتھ 'will has' کہنا، جو شدید غلط ہے (درست: 'He will have completed').",
                practiceQuestion = "درست شکل منتخب کریں: 'By tomorrow, they (will finish / will have finished) the assignment.'",
                practiceAnswerHint = "By tomorrow, they will have finished the assignment."
            )

            "future_perfect_continuous" -> LessonContent(
                topicTitleEn = "Future Perfect Continuous Tense",
                topicTitleUr = "فعلِ مستقبل مکمل جاری (Future Perfect Continuous)",
                englishRule = "Future Perfect Continuous expresses an ongoing action that will continue up until a specific future moment, emphasizing its ongoing duration.",
                ruleUrdu = """فعلِ مستقبل مکمل جاری ایسے کام کے لیے استعمال ہوتا ہے جو مستقبل کے کسی مخصوص وقت تک ایک مقررہ مدت سے مسلسل جاری رہے گا۔

【 1. Formulas / جملوں کی ساخت 】
• A. Affirmative (مثبت): Subject + will + have + been + V1(-ing) + Object + for/since + Time
  - Example: By next month, I will have been working here for five years.
• B. Negative (منفی): Subject + will + not + have + been + V1(-ing) + Object + for/since + Time
  - Example: She will not have been living here for long by December.
• C. Interrogative (سوالیہ): Will + Subject + have + been + V1(-ing) + Object + for/since + Time?
  - Example: Will you have been studying for three hours by midnight?
• D. Negative Interrogative (منفی سوالیہ): Will + Subject + not + have + been + V1(-ing) + Object + for/since + Time?
  - Example: Will they not have been practicing for a week by tomorrow?
• E. WH-Questions (سوالیہ الفاظ): WH-Word + will + Subject + have + been + V1(-ing) + Object + for/since + Time?
  - Example: How long will he have been teaching by the time he retires?

【 2. Helping Verbs & Duration / امدادی افعال 】
• Helping Verb: 'Will have been' for all subjects.
• Verb Form: V1 + ing. Uses 'for' (duration) or 'since' (start time).""",
                arabicGrammarComparison = "عربی میں اسے ظاہر کرنے کے لیے 'سيكونُ يدرسُ منذُ...' کا ڈھانچہ استعمال کیا جاتا ہے۔",
                similarities = listOf(
                    "دونوں میں مستقبل کے کسی وقت تک جاری مدت کو دکھایا جاتا ہے"
                ),
                differences = listOf(
                    "انگریزی میں 'will have been + V1(-ing)' کی خاص لمبی ترکیب ہوتی ہے",
                    "عربی میں 'سيكون' کے ساتھ 'منذ' کا لفظ استعمال ہوتا ہے"
                ),
                examples = listOf(
                    BilingualExample("By next month, I will have been working here for 5 years.", "اگلے ماہ تک میں یہاں ۵ سال سے کام کر رہا ہوں گا۔", "Affirmative"),
                    BilingualExample("She will not have been living in London for long by December.", "دسمبر تک وہ زیادہ عرصے سے لندن میں نہیں رہ رہی ہوگی۔", "Negative"),
                    BilingualExample("Will you have been studying for three hours by midnight?", "کیا آپ آدھی رات تک ۳ گھنٹے سے پڑھ رہے ہوں گے؟", "Interrogative"),
                    BilingualExample("Will they not have been practicing for a week?", "کیا وہ ایک ہفتے سے مشق نہیں کر رہے ہوں گے؟", "Negative Interrogative"),
                    BilingualExample("How long will he have been teaching by retirement?", "ریٹائرمنٹ تک وہ کتنی دیر سے پڑھا رہا ہوگا؟", "WH-Question"),
                    BilingualExample("By 5 PM, they will have been playing for four hours.", "شام ۵ بجے تک وہ چار گھنٹے سے کھیل رہے ہوں گے۔", "Affirmative")
                ),
                commonMistake = "امدادی افعال کی ترتیب خراب کر دینا، جیسے 'will been have' کہنا (درست: 'will have been').",
                practiceQuestion = "خالی جگہ پر کریں: 'By next year, I will have been learning English ___ two years.' (for / since)",
                practiceAnswerHint = "By next year, I will have been learning English for two years."
            )

            "passive_voice" -> LessonContent(
                topicTitleEn = "Active & Passive Voice — Foundations",
                topicTitleUr = "فعلِ معروف و مجہول - بنیادی قواعد (Foundations)",
                englishRule = "Active Voice focuses on the subject (doer). Passive Voice shifts focus to the object (receiver). In Passive sentences, V3 (Past Participle) is ALWAYS used.",
                ruleUrdu = """فعلِ معروف (Active Voice) میں بنیادی توجہ فاعل (کام کرنے والے) پر ہوتی ہے، جبکہ فعلِ مجہول (Passive Voice) میں بنیادی توجہ مفعول (جس پر کام ہوا) پر منتقل ہو جاتی ہے۔

【 1. Core Definitions / بنیادی مفہوم 】
• Active Voice (فعلِ معروف): Subject + Verb + Object (فاعل معلوم ہے)
  - Example: Ali wrote a letter. (علی نے خط لکھا - علی فاعل ہے)
• Passive Voice (فعلِ مجہول): Object + Be + V3 + by + Subject (مفعول پر توجہ ہے)
  - Example: A letter was written by Ali. (علی کے ذریعے خط لکھا گیا)

【 2. The 5-Step Conversion Rule / ۵ سنہری اصول 】
1. Bring the Object of the Active sentence to the position of Subject. (مفعول کو فاعل بنائیں)
2. Add appropriate 'Be' Auxiliary Verb based on Tense. (زمانے کے مطابق امدادی فعل لگائیں)
3. Convert the Main Verb into Past Participle (3rd Form - V3). (ہمیشہ فعل کی تیسری فارم لگائیں)
4. Add the preposition 'by' before the agent. (فاعل سے پہلے 'by' لگائیں)
5. Change the Subject into Object Form. (فاعل کو مفعول کی شکل دیں)

【 3. Subject / Object Pronoun Changes / ضمائر کی تبدیلی 】
• I -> me
• He -> him
• She -> her
• We -> us
• They -> them
• You -> you
• It -> it

【 4. When 'by' is Omitted / 'by' کب حذف ہوتا ہے 】
When the agent is unknown, obvious, or unimportant, omit 'by':
• Active: Someone stole my bike. -> Passive: My bike was stolen. (کوئی بائی سائیکل چرا لے گیا -> میری بائی سائیکل چوری ہو گئی)
• Active: People speak English worldwide. -> Passive: English is spoken worldwide.

【 5. Untranslatable Sentences / جن کا مجہول نہیں بنتا 】
Intransitive verbs (افعالِ لازمی) that do not have a direct object cannot be converted into Passive Voice!
- Examples: sleep, go, run, laugh, die, sit.
- Sentence: 'He goes to school.' -> CANNOT be made passive because 'school' is a destination (prepositional phrase), not a direct object!""",
                arabicGrammarComparison = "جیسے آپ نے عربی میں سیکھا ہے، 'المبني للمجهول' میں فاعل کو حذف کر کے نائب فاعل لایا جاتا ہے اور فعل کی حرکات تبدیل کر دی جاتی ہیں (مثلاً: 'كُتِبَ الكِتابُ' - کتاب لکھی گئی)۔",
                similarities = listOf(
                    "دونوں زبانوں میں فاعل کے بجائے مفعول/نائب فاعل کو بنیادی اہمیت دی جاتی ہے",
                    "فاعل کے معلوم نہ ہونے یا ظاہر نہ کرنے کی صورت میں استعمال ہوتے ہیں"
                ),
                differences = listOf(
                    "انگریزی میں امدادی فعل (Be) اور 3rd Form (V3) کے ساتھ 'by' کا اضافہ کیا جاتا ہے",
                    "عربی میں فعل کے اندرونی اعراب پیش اور زیر سے تبدیل ہوتے ہیں (كَتَبَ سے كُتِبَ)"
                ),
                examples = listOf(
                    BilingualExample("Active: Ali wrote a letter.", "علی نے ایک خط لکھا۔", "Active Voice (فعلِ معروف)"),
                    BilingualExample("Passive: A letter was written by Ali.", "علی کے ذریعہ ایک خط لکھا گیا۔", "Passive Voice (فعلِ مجہول)"),
                    BilingualExample("Active: Someone stole my wallet.", "کسی نے میرا بٹووا چرا لیا۔", "Active"),
                    BilingualExample("Passive: My wallet was stolen.", "میرا بٹووا چوری ہو گیا۔", "Passive (فاعل نامعلوم ہے اس لیے 'by' حذف کر دیا)"),
                    BilingualExample("Active: They speak English everywhere.", "وہ ہر جگہ انگریزی بولتے ہیں۔", "Active"),
                    BilingualExample("Passive: English is spoken everywhere.", "انگریزی ہر جگہ بولی جاتی ہے۔", "Passive")
                ),
                commonMistake = "فعلِ مجہول (Passive Voice) میں 3rd Form (V3) کے بجائے 1st یا 2nd Form لگا دینا (یاد رکھیں: Passive میں ہمیشہ 3rd Form لگتی ہے!).",
                practiceQuestion = "Active سے Passive میں تبدیل کریں: 'He broke the window.'",
                practiceAnswerHint = "The window was broken by him."
            )

            "passive_voice_conversion" -> LessonContent(
                topicTitleEn = "Active & Passive Voice — Complete Conversion",
                topicTitleUr = "فعلِ معروف و مجہول - مکمل تبدیلیاں (Complete Conversion)",
                englishRule = "Complete tense-by-tense conversion structures for Present, Past, Future, Modals, Interrogatives, and Imperative sentences into Passive Voice.",
                ruleUrdu = """یہاں تمام اہم زمانوں، سوالیہ جملوں، Modals اور حکمیہ (Imperative) جملوں کو Active سے Passive میں تبدیل کرنے کے تمام جامع قوانین مع مثالیں بیان کیے گئے ہیں۔

【 1. Tense Conversion Formulas / زمانوں کے لحاظ سے تبادلہ 】
1. Present Simple: Object + is/am/are + V3 (+ by agent)
   - Active: She cleans the room. -> Passive: The room is cleaned by her.
2. Present Continuous: Object + is/am/are + being + V3
   - Active: She is cleaning the room. -> Passive: The room is being cleaned by her.
3. Present Perfect: Object + has/have + been + V3
   - Active: She has cleaned the room. -> Passive: The room has been cleaned by her.
4. Past Simple: Object + was/were + V3
   - Active: She cleaned the room. -> Passive: The room was cleaned by her.
5. Past Continuous: Object + was/were + being + V3
   - Active: She was cleaning the room. -> Passive: The room was being cleaned by her.
6. Past Perfect: Object + had + been + V3
   - Active: She had cleaned the room. -> Passive: The room had been cleaned by her.
7. Future Simple: Object + will + be + V3
   - Active: She will clean the room. -> Passive: The room will be cleaned by her.
8. Future Perfect: Object + will + have + been + V3
   - Active: She will have cleaned the room. -> Passive: The room will have been cleaned by her.

【 2. Interrogative & WH-Questions Conversion / سوالیہ جملوں کی تبدیلی 】
• Active: Did he solve the paper? -> Passive: Was the paper solved by him?
• Active: Who wrote this letter? -> Passive: By whom was this letter written?
• Active: What are you doing? -> Passive: What is being done by you?

【 3. Modal Verbs Conversion / Modal افعال (Can, Could, Must, Should) 】
• Formula: Object + Modal + be + V3
• Active: You must complete this task. -> Passive: This task must be completed by you.
• Active: She can solve this puzzle. -> Passive: This puzzle can be solved by her.

【 4. Imperative Sentences / حکمیہ و التجائیہ جملے 】
• Command / Order: Let + Object + be + V3
  - Active: Close the door. -> Passive: Let the door be closed.
• Request: You are requested to + V1...
  - Active: Please help me. -> Passive: You are requested to help me.
• Advice: Object + should + be + V3
  - Active: Respect your teachers. -> Passive: Your teachers should be respected.""",
                arabicGrammarComparison = "جیسے عربی میں مختلف ابواب اور اوزان کے لحاظ سے مجہول کے اوزان بدلتے ہیں، انگریزی میں بھی امدادی افعال (is/was/been/being/be) سے ہر زمانے کا مجہول تیار ہوتا ہے۔",
                similarities = listOf(
                    "تمام زمانوں میں بنیادی قانون وہی رہتا ہے: مفعول کو فاعل بنانا اور V3 استعمال کرنا"
                ),
                differences = listOf(
                    "انگریزی میں جاری زمانوں میں 'being' اور مکمل زمانوں میں 'been' کا لفظ بڑھایا جاتا ہے"
                ),
                examples = listOf(
                    BilingualExample("Active: She eats an apple. / Passive: An apple is eaten by her.", "وہ سیب کھاتی ہے۔ / سیب اس کے ذریعہ کھایا جاتا ہے۔", "Present Simple Passive"),
                    BilingualExample("Active: They are building a bridge. / Passive: A bridge is being built by them.", "وہ پل بنا رہے ہیں۔ / ان کے ذریعہ پل بنایا جا رہا ہے۔", "Present Continuous Passive"),
                    BilingualExample("Active: Who wrote this article? / Passive: By whom was this article written?", "یہ مضمون کس نے لکھا؟ / یہ مضمون کس کے ذریعہ لکھا گیا؟", "WH-Question ('Who' -> 'By whom')"),
                    BilingualExample("Active: You should wash hands. / Passive: Hands should be washed.", "آپ کو ہاتھ دھونے چاہئیں۔ / ہاتھ دھوئے جانے چاہئیں۔", "Modal 'should be'"),
                    BilingualExample("Active: Open the book. / Passive: Let the book be opened.", "کتاب کھولیں۔ / کتاب کھولی جائے۔", "Imperative ('Let + obj + be + V3')"),
                    BilingualExample("Active: Please sit down. / Passive: You are requested to sit down.", "براہِ کرم بیٹھ جائیں۔ / آپ سے بیٹھنے کی التجا کی جاتی ہے۔", "Request")
                ),
                commonMistake = "Continuous Tense کے Passive میں 'being' لگانا بھول جانا، جیسے 'The room is cleaned' (یہ Simple کا ہے) جبکہ ہونا چاہیے 'The room is being cleaned'.",
                practiceQuestion = "Passive میں تبدیل کریں: 'Who broken this vase?'",
                practiceAnswerHint = "By whom was this vase broken?"
            )

            "direct_indirect_foundations" -> LessonContent(
                topicTitleEn = "Direct & Indirect Speech — Foundations",
                topicTitleUr = "بلاواسطہ و بالواسطہ - بنیادی قواعد (Foundations)",
                englishRule = "Direct Speech quotes exact words in quotation marks. Indirect Speech reports the substance of what was said without exact quotes, applying pronoun, tense, and time/place backshifts.",
                ruleUrdu = """بلاواسطہ گفتگو (Direct Speech) میں متکلم کے اصل الفاظ کو واوین (Quotation Marks "... ") میں بیان کیا جاتا ہے۔ بالواسطہ گفتگو (Indirect / Reported Speech) میں اس بات کو اپنے الفاظ میں بغیر واوین کے بیان کیا جاتا ہے۔

【 1. Terminology / بنیادی اصطلاحات 】
• Reporting Verb: واوین سے باہر والا فعل (مثلاً: He said, She says).
• Reported Speech: واوین کے اندر لکھا گیا جملہ (مثلاً: "I am tired").
• Example Direct: He said, "I am tired." -> Indirect: He said that he was tired.

【 2. Basic Conversion Principles / بنیادی اصول 】
1. Remove commas and quotation marks ("... ").
2. Connect clauses using 'that' (for assertive statements).
3. Change Pronouns according to the SON Rule.
4. Backshift Tenses (if Reporting Verb is in Past).
5. Change Time & Place expressions.

【 3. The SON Rule for Pronoun Changes / ضمائر کی تبدیلی کا قانون 】
• S = Subject (1st Person pronouns: I, my, me, we, us change according to the Subject of Reporting Verb).
• O = Object (2nd Person pronouns: you, your, yourself change according to the Object of Reporting Verb).
• N = No Change (3rd Person pronouns: he, she, it, they, him, her, them remain UNCHANGED!).

【 4. Tense Backshift Rules / زمانوں کی تبدیلی کا قانون 】
When Reporting Verb is in PAST (e.g., 'said'):
• Present Simple -> Past Simple (am/is/are -> was/were, V1 -> V2)
• Present Continuous -> Past Continuous (is/am/are -ing -> was/were -ing)
• Present Perfect -> Past Perfect (has/have V3 -> had V3)
• Present Perfect Continuous -> Past Perfect Continuous
• Past Simple -> Past Perfect (V2 -> had V3)
• Past Continuous -> Past Perfect Continuous (was/were -ing -> had been -ing)
• Will/Shall -> Would / Can -> Could / May -> Might

【 5. NO Tense Change Exceptions / جب زمانہ نہیں بدلتا 】
1. When Reporting Verb is in PRESENT or FUTURE tense:
   - Direct: He says, "I am happy." -> Indirect: He says that he is happy. (زمانہ نہیں بدلا!)
2. When Reported Speech expresses a Universal Truth, Scientific Fact, or Habitual Action:
   - Direct: The teacher said, "The earth is round." -> Indirect: The teacher said that the earth is round.

【 6. Time and Place Expression Changes / الفاظ کی تبدیلی 】
• today -> that day
• tomorrow -> the next day / the following day
• yesterday -> the previous day / the day before
• now -> then | ago -> before | here -> there | this -> that | these -> those | tonight -> that night""",
                arabicGrammarComparison = "عربی میں حکایتِ قول کے لیے 'قالَ إِنَّ...' کا استعمال کیا جاتا ہے جہاں قولِ منقول کو نقل کیا جاتا ہے۔ انگریزی میں Indirect Speech بناتے وقت زمانوں اور ضمائر کی تفصیلی تبدیلیاں کی جاتی ہیں۔",
                similarities = listOf(
                    "دونوں زبانوں میں کسی کی گفتگو کو بیان کرنے کا بلاواسطہ اور بالواسطہ طریقہ موجود ہے"
                ),
                differences = listOf(
                    "انگریزی میں Indirect Speech میں زمانوں کا 'Backshift' (ماضی کی طرف تبدیلی) اور Time/Place الفاظ کا بدلنا ضروری ہے",
                    "عربی میں عموماً مقولہ قول کو اس کے اصلی اعراب اور الفاظ میں برقرار رکھا جاتا ہے"
                ),
                examples = listOf(
                    BilingualExample("Direct: He said, \"I am reading a book.\"", "اس نے کہا: \"میں ایک کتاب پڑھ رہا ہوں۔\"", "Direct Speech"),
                    BilingualExample("Indirect: He said that he was reading a book.", "اس نے کہا کہ وہ ایک کتاب پڑھ رہا تھا۔", "Indirect Speech (Tense backshift + pronoun change)"),
                    BilingualExample("Direct: She says, \"I love tea.\"", "وہ کہتی ہے: \"مجھے چائے پسند ہے۔\"", "Reporting verb in Present ('says')"),
                    BilingualExample("Indirect: She says that she loves tea.", "وہ کہتی ہے کہ اسے چائے پسند ہے۔", "No tense change because reporting verb is Present!"),
                    BilingualExample("Direct: The teacher said, \"The sun rises in the east.\"", "استاد نے کہا: \"سورج مشرق سے نکلتا ہے۔\"", "Universal Truth (دائمی سچائی)"),
                    BilingualExample("Indirect: The teacher said that the sun rises in the east.", "استاد نے کہا کہ سورج مشرق سے نکلتا ہے۔", "No tense change for Universal Truth!")
                ),
                commonMistake = "Reporting verb حال میں ہونے کے باوجود اندر کا زمانہ بدل دینا، جیسے 'He says that he was happy' (درست: 'He says that he is happy').",
                practiceQuestion = "Indirect میں تبدیل کریں: He said, \"I will visit Lahore tomorrow.\"",
                practiceAnswerHint = "He said that he would visit Lahore the next day."
            )

            "direct_indirect_sentences" -> LessonContent(
                topicTitleEn = "Direct & Indirect Speech — Complete Sentence Types",
                topicTitleUr = "بلاواسطہ و بالواسطہ - جملوں کی اقسام (Sentence Types)",
                englishRule = "Specific conversion rules for Statements, Questions, Commands, Requests, Advice, Suggestions, Exclamations, and Wishes into Indirect Speech.",
                ruleUrdu = """مختلف قسم کے جملوں (سوالیہ، حکمیہ، التجائیہ، استعجابیہ اور دعائیہ) کو Direct سے Indirect میں تبدیل کرنے کے مخصوص اور جامع قوانین ذیل میں پیش ہیں:

【 1. Statements (بیانی جملے) 】
• Reporting verb: 'said to' -> 'told', 'said' -> 'said'.
• Conjunction: 'that'.
• Direct: Ali said to me, "I am ready." -> Indirect: Ali told me that he was ready.

【 2. Yes/No Questions (سوالیہ جملے - ہاں/نہ والے) 】
• Reporting verb: 'said' -> 'asked' or 'inquired of'.
• Conjunction: Replace commas with 'if' or 'whether' (Do NOT use 'that'!).
• Sentence Order: Change question structure to Assertive statement order (Subject + Verb).
• Direct: He said to me, "Are you coming?" -> Indirect: He asked me if I was coming.

【 3. WH-Questions (سوالیہ الفاظ والے جملے) 】
• Reporting verb: 'asked'.
• Conjunction: Retain the WH-word (what, where, why, when, how) as connector! Do NOT use 'that' or 'if'.
• Sentence Order: Subject comes BEFORE verb.
• Direct: She said to him, "Where do you live?" -> Indirect: She asked him where he lived.

【 4. Commands & Orders (حکمیہ جملے) 】
• Reporting verb: 'ordered', 'commanded', 'instructed'.
• Conjunction: Use 'to + V1' (for negative commands use 'not to + V1').
• Direct: The officer said to the soldier, "Stand at ease." -> Indirect: The officer commanded the soldier to stand at ease.
• Direct: Mother said, "Do not touch the fire." -> Indirect: Mother ordered me not to touch the fire.

【 5. Requests (التجائیہ جملے) 】
• Reporting verb: 'requested', 'begged', 'implored'. Omit 'please' / 'kindly'.
• Direct: He said to her, "Please help me." -> Indirect: He requested her to help him.

【 6. Advice & Suggestions (نصیحت اور تجاویز) 】
• Advice: Reporting verb -> 'advised'. (Direct: Doctor said, "Take rest." -> Indirect: Doctor advised me to take rest.)
• Suggestions (Let's...): Reporting verb -> 'suggested that' / 'proposed that' + subject + should + V1...
• Direct: He said, "Let's play cricket." -> Indirect: He suggested that we should play cricket.

【 7. Exclamatory Sentences (استعجابیہ جملے) 】
• Reporting verb: 'exclaimed with joy / sorrow / surprise / wonder'. Conjunction: 'that'.
• Direct: They said, "Hurrah! We won the match." -> Indirect: They exclaimed with joy that they had won the match.

【 8. Wishes & Prayers (دعائیہ جملے) 】
• Reporting verb: 'prayed' or 'wished'. Change 'may' to 'might'.
• Direct: She said, "May you live long!" -> Indirect: She prayed that I might live long.""",
                arabicGrammarComparison = "انگریزی میں ہر طرزِ گفتگو (سوال، حکم، التجا، تعجب) کا اپنا الگ ربطی لفظ اور Reporting Verb ہوتا ہے جس سے بات کا انداز واضح ہو جاتا ہے۔",
                similarities = listOf(
                    "تمام اقسام کے جملوں میں ضمائر اور زمانوں کی بنیادی تبدیلیاں وہی رہتی ہیں"
                ),
                differences = listOf(
                    "سوالیہ جملوں میں 'that' نہیں لگتا بلکہ 'if' یا وہی WH-word جڑتا ہے",
                    "حکمیہ جملوں میں 'to + V1' استعمال ہوتا ہے"
                ),
                examples = listOf(
                    BilingualExample("Direct: He said to me, \"Are you ill?\"", "اس نے مجھ سے پوچھا: \"کیا آپ بیمار ہیں؟\"", "Yes/No Question"),
                    BilingualExample("Indirect: He asked me if I was ill.", "اس نے مجھ سے پوچھا کہ کیا میں بیمار تھا۔", "Indirect Question ('asked + if')"),
                    BilingualExample("Direct: She said, \"Where is my bag?\"", "اس نے کہا: \"میرا بیگ کہاں ہے؟\"", "WH-Question"),
                    BilingualExample("Indirect: She asked where her bag was.", "اس نے پوچھا کہ اس کا بیگ کہاں تھا۔", "Indirect WH-Question"),
                    BilingualExample("Direct: He said, \"Please pass the salt.\"", "اس نے کہا: \"براہِ کرم نمک آگے بڑھائیں۔\"", "Request"),
                    BilingualExample("Indirect: He requested to pass the salt.", "اس نے نمک آگے بڑھانے کی التجا کی۔", "Indirect Request ('requested to')")
                ),
                commonMistake = "سوالیہ جملے کو Indirect بناتے وقت 'that' لگا دینا جیسے 'He asked that where I live' (درست: 'He asked where I lived').",
                practiceQuestion = "Indirect میں تبدیل کریں: She said to him, \"Do not waste your time.\"",
                practiceAnswerHint = "She advised him not to waste his time."
            )

            else -> LessonContent(
                topicTitleEn = "English Grammar",
                topicTitleUr = "انگریزی گرامر",
                englishRule = "English grammar provides clear rules for forming correct sentences.",
                ruleUrdu = "انگریزی گرامر جملوں کی درستی اور گفتگو میں صفائی کے لیے قواعد فراہم کرتی ہے۔",
                arabicGrammarComparison = "جیسے آپ نے عربی النحو میں سیکھا ہے، گرامر مفہوم کو درست ادا کرنے کے لیے ضروری ہے۔",
                similarities = listOf("دونوں زبانوں میں قواعد و ضوابط ضروری ہیں"),
                differences = listOf("جملوں کی ترتیب مختلف ہو سکتی ہے"),
                examples = listOf(
                    BilingualExample("I learn English.", "میں انگریزی سیکھتا ہوں۔", "جیسے عربی میں: أتعلمُ الإنجليزيةَ۔")
                ),
                commonMistake = "قواعد کا خیال رکھے بغیر لفظ بہ لفظ ترجمہ کرنا۔",
                practiceQuestion = "Is grammar important?",
                practiceAnswerHint = "Yes, it is."
            )
        }
        return baseLesson.copy(mcqs = getLessonMcqs(topicId))
    }
}
