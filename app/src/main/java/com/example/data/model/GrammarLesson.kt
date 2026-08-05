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
    val practiceAnswerHint: String = ""
)

object CurriculumData {
    val topics = listOf(
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
        GrammarTopic(
            id = "present_simple",
            titleEn = "Present Simple Tense",
            titleUr = "فعلِ حال سادہ (Present Simple)",
            category = "زمانے • Tenses",
            quickComparison = "روزمرہ عادات کے لیے استعمال ہوتا ہے۔ جیسے آپ نے عربی میں 'الفعل المضارع' کے بارے میں سیکھا ہے۔",
            level = "Beginner"
        ),
        GrammarTopic(
            id = "present_continuous",
            titleEn = "Present Continuous",
            titleUr = "فعلِ حال جاری (Present Continuous)",
            category = "زمانے • Tenses",
            quickComparison = "فی الحال جاری کام کے لیے۔ جیسے آپ نے عربی میں الفعل المضارع یا اسم الفاعل کے ساتھ جاری حالت سیکھی ہے۔",
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
            id = "future_simple",
            titleEn = "Future Simple",
            titleUr = "فعلِ مستقبل سادہ (Future Simple)",
            category = "زمانے • Tenses",
            quickComparison = "مستقبل کے فیصلوں کے لیے۔ جیسے آپ نے عربی میں فعل کے شروع میں 'سـ' یا 'سوف' جوڑنا سیکھا ہے۔",
            level = "Beginner"
        ),
        GrammarTopic(
            id = "passive_voice",
            titleEn = "Passive Voice",
            titleUr = "فعلِ مجہول (Passive Voice)",
            category = "پیچیدہ ڈھانچے • Complex Structures",
            quickComparison = "مفعول پر توجہ دینے کے لیے۔ جیسے آپ نے عربی میں 'المبني للمجهول' اور نائب فاعل کا قانون سیکھا ہے۔",
            level = "Advanced"
        )
    )

    fun getDefaultLesson(topicId: String): LessonContent {
        return when (topicId) {
            "verb_to_be" -> LessonContent(
                topicTitleEn = "Verb 'To Be' (Am/Is/Are)",
                topicTitleUr = "فعلِ ربط (Verb To Be)",
                englishRule = "In English, every complete sentence requires a verb. 'Am', 'is', and 'are' connect a subject to its description or state.",
                ruleUrdu = "انگریزی میں ہر جملے میں فعل (Verb) کا ہونا لازمی ہے۔ جب ہم کسی شخص یا چیز کی حالت، پیشے یا تعارف کو ظاہر کرتے ہیں تو 'am', 'is', یا 'are' استعمال کرتے ہیں۔ (I am, He/She/It is, We/You/They are)",
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
                    BilingualExample("I am a student.", "میں ایک طالب علم ہوں۔", "جیسے عربی میں آپ کہتے ہیں: أنا طالبٌ (عربی میں فعلِ ربط ظاہر نہیں ہوتا)۔"),
                    BilingualExample("She is happy.", "وہ خوش ہے۔", "جیسے عربی میں: هي سعيدةٌ۔"),
                    BilingualExample("They are teachers.", "وہ اساتذہ ہیں۔", "جیسے عربی میں: هم معلمون۔")
                ),
                commonMistake = "اردو یا عربی سوچ کر انگریزی میں 'is/am/are' نہ لکھنا، جیسے 'He happy' کہنا جو غلط ہے (درست: 'He is happy')۔",
                practiceQuestion = "خالی جگہ پر کریں: 'They ___ ready.' (am / is / are)",
                practiceAnswerHint = "They are ready."
            )
            "articles_a_an_the" -> LessonContent(
                topicTitleEn = "Articles (A / An / The)",
                topicTitleUr = "حروفِ تنکیر و تعریف (A / An / The)",
                englishRule = "We use 'a' and 'an' for singular, non-specific countable nouns. We use 'the' for specific nouns known to the listener.",
                ruleUrdu = "انگریزی میں عام یا نکرہ چیزوں کے لیے 'a' اور 'an' استعمال ہوتا ہے، جبکہ خاص یا معرفہ چیزوں کے لیے 'the' کا استعمال کیا جاتا ہے۔ اگر لفظ کسی واول (a, e, i, o, u) کی آواز سے شروع ہو تو 'an' لگائیں۔",
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
                    BilingualExample("An apple", "ایک سیب (واول کی آواز)", "جیسے عربی میں: تفاحةٌ۔")
                ),
                commonMistake = "انگریزی میں اسمِ نکرہ کے ساتھ 'a' یا 'an' لگانا بھول جانا، جیسے 'I have book' کہنا (درست: 'I have a book')۔",
                practiceQuestion = "درست آرٹیکل کا انتخاب کریں: 'I saw ___ elephant.' (a / an / the)",
                practiceAnswerHint = "I saw an elephant."
            )
            "present_simple" -> LessonContent(
                topicTitleEn = "Present Simple Tense",
                topicTitleUr = "فعلِ حال سادہ (Present Simple)",
                englishRule = "Present Simple is used for daily habits, routines, and universal facts. Add '-s' or '-es' to the verb for He, She, It, or singular subjects.",
                ruleUrdu = "فعلِ حال سادہ روزمرہ کی عادات، معمولات اور دائمی حقائق بیان کرنے کے لیے استعمال ہوتا ہے۔ جب فاعل (Subject) 'He, She, It' یا واحد اسم ہو، تو فعل کے ساتھ '-s' یا '-es' کا اضافہ کرتے ہیں۔",
                arabicGrammarComparison = "جیسے آپ نے عربی میں سیکھا ہے، 'الفعل المضارع' حال کی عادات اور جاری کاموں کو ظاہر کرتا ہے۔ عربی میں فاعل کے مطابق علامتِ مضارع (أ، ن، ي، ت) لگتی ہے، جبکہ انگریزی میں صرف He/She/It کے ساتھ 's/es' کا اضافہ ہوتا ہے۔",
                similarities = listOf(
                    "دونوں زمانے عادات، معمولات اور حقائق کو بیان کرنے کے لیے استعمال ہوتے ہیں",
                    "فاعل کے بدلنے سے فعل کی شکل میں تبدیلی آتی ہے"
                ),
                differences = listOf(
                    "انگریزی میں صرف He/She/It کے ساتھ فعل کے آخر میں 's/es' لگتا ہے",
                    "عربی میں مضارع کی شروعات میں علامتیں (أ، ن، ي، ت) اور آخر میں اعراب بدلتے ہیں"
                ),
                examples = listOf(
                    BilingualExample("I pray every day.", "میں ہر روز نماز پڑھتا ہوں۔", "جیسے عربی میں: أُصلِّي كلَّ يومٍ۔"),
                    BilingualExample("He works hard.", "وہ سخت محنت کرتا ہے۔", "جیسے عربی میں: يَعْمَلُ بجدٍّ (علامتِ مضارع يـ کے ساتھ)۔"),
                    BilingualExample("The sun rises in the east.", "سورج مشرق سے نکلتا ہے۔", "جیسے عربی میں: تَطْلُعُ الشَّمْسُ من المَشْرِقِ۔")
                ),
                commonMistake = "He, She, It کے بعد فعل کے ساتھ 's' لگانا بھول جانا، جیسے 'He work here' کہنا (درست: 'He works here')۔",
                practiceQuestion = "درست شکل منتخب کریں: 'She (like/likes) ice cream.'",
                practiceAnswerHint = "She likes ice cream."
            )
            "present_continuous" -> LessonContent(
                topicTitleEn = "Present Continuous Tense",
                topicTitleUr = "فعلِ حال جاری (Present Continuous)",
                englishRule = "Used for actions happening right now at the moment of speaking. Structure: Subject + am/is/are + Verb(-ing).",
                ruleUrdu = "فعلِ حال جاری ان کاموں کو بیان کرنے کے لیے استعمال ہوتا ہے جو بات چیت کے وقت جاری ہوں۔ اس کا ڈھانچہ: فاعل + am/is/are + فعل کی 1st Form کے ساتھ ing ہوتا ہے۔",
                arabicGrammarComparison = "جیسے آپ نے عربی میں سیکھا ہے، 'الفعل المضارع' کے ساتھ 'الآنَ' یا 'اسم الفاعل' استعمال کر کے حالِ جاری کو ظاہر کیا جاتا ہے (مثلاً: 'هو يكتبُ الآنَ' یا 'هو كاتبٌ الآنَ')۔",
                similarities = listOf(
                    "دونوں زبانوں میں اس وقت جاری کام کو ظاہر کیا جاتا ہے"
                ),
                differences = listOf(
                    "انگریزی میں فعل کے ساتھ 'ing' اور امدادی فعل (is/am/are) لگانا لازمی ہے",
                    "عربی میں صرف فعل مضارع یا اسم الفاعل سے ہی جاری کیفیت مکمل ہو جاتی ہے"
                ),
                examples = listOf(
                    BilingualExample("I am reading a book.", "میں ایک کتاب پڑھ رہا ہوں۔", "جیسے عربی میں: أنا أقرأُ كتاباً الآنَ۔"),
                    BilingualExample("He is sleeping.", "وہ سو رہا ہے۔", "جیسے عربی میں: هو نائمٌ / هو ينامُ الآنَ۔")
                ),
                commonMistake = "'ing' والے فعل سے پہلے 'am/is/are' چھوڑ دینا، جیسے 'I reading' کہنا (درست: 'I am reading')۔",
                practiceQuestion = "جملہ مکمل کریں: 'They ___ playing football now.' (am / is / are)",
                practiceAnswerHint = "They are playing football now."
            )
            "past_simple" -> LessonContent(
                topicTitleEn = "Past Simple Tense",
                topicTitleUr = "فعلِ ماضی سادہ (Past Simple)",
                englishRule = "Used for completed actions in the past. We use the second form of the verb (ed for regular verbs).",
                ruleUrdu = "فعلِ ماضی سادہ ماضی میں مکمل ہو جانے والے کاموں کے لیے استعمال ہوتا ہے۔ اس میں فعل کی دوسری فارم (Past Form) استعمال کی جاتی ہے (باقاعدہ افعال میں ed لگتا ہے)۔",
                arabicGrammarComparison = "جیسے آپ نے عربی میں سیکھا ہے، 'الفعل الماضي' ماضی کے مکمل افعال کو ظاہر کرتا ہے (مثلاً: 'كَتَبَ' - اس نے لکھا، 'ذَهَبَ' - وہ گیا)۔",
                similarities = listOf(
                    "دونوں ماضی کے مکمل افعال کے لیے استعمال ہوتے ہیں",
                    "جملے کا زمانہ ماضی میں واضح ہوتا ہے"
                ),
                differences = listOf(
                    "انگریزی میں باقاعدہ افعال کے آخر میں 'ed' لگتا ہے (جیسے walked, played)",
                    "عربی میں ماضی کی آخر میں ضمائر کے مطابق گردان ہوتی ہے (كَتَبْتُ، كَتَبْتَ، كَتَبُوا)"
                ),
                examples = listOf(
                    BilingualExample("He went to school.", "وہ سکول گیا۔", "جیسے عربی میں: ذَهَبَ إلى المَدْرَسَةِ۔"),
                    BilingualExample("I wrote a letter.", "میں نے خط لکھا۔", "جیسے عربی میں: كَتَبْتُ رِسالَةً۔")
                ),
                commonMistake = "سوالیہ یا نفی جملوں میں Did کے بعد پہلی فارم کی جگہ دوسری فارم لگانا، جیسے 'Did he went?' کہنا (درست: 'Did he go?')۔",
                practiceQuestion = "جملہ درست کریں: 'I did not went there.'",
                practiceAnswerHint = "I did not go there."
            )
            "future_simple" -> LessonContent(
                topicTitleEn = "Future Simple Tense",
                topicTitleUr = "فعلِ مستقبل سادہ (Future Simple)",
                englishRule = "Used to express actions that will happen in the future. Structure: Subject + Will + Verb (Base Form).",
                ruleUrdu = "مستقبل میں ہونے والے کاموں، فیصلوں یا پیشگوئیوں کے لیے استعمال ہوتا ہے۔ اس کا بنیادی ڈھانچہ: فاعل + Will + فعل کی 1st Form ہے۔",
                arabicGrammarComparison = "جیسے آپ نے عربی میں سیکھا ہے، فعلِ مضارع کے شروع میں حرف 'سـ' یا 'سوف' لگانے سے مستقبل کا معنی بنتا ہے (مثلاً: 'سَيَكْتُبُ' یا 'سَوْفَ يَكْتُبُ')۔ انگریزی کا 'Will' بھی یہی کام کرتا ہے۔",
                similarities = listOf(
                    "دونوں میں مستقبل کو ظاہر کرنے کے لیے خاص علامات (Will / سـ / سوف) کا اضافہ کیا جاتا ہے"
                ),
                differences = listOf(
                    "انگریزی میں 'Will' فاعل اور فعل کے درمیان مستقل لفظ کے طور پر آتا ہے",
                    "عربی میں 'سـ' حرف فعلِ مضارع کے ساتھ جڑ جاتا ہے (جیسے: سَأَذْهَبُ)"
                ),
                examples = listOf(
                    BilingualExample("I will travel tomorrow.", "میں کل سفر کروں گا۔", "جیسے عربی میں: سَأُسافِرُ غَداً۔"),
                    BilingualExample("She will help you.", "وہ آپ کی مدد کرے گی۔", "جیسے عربی میں: سَوْفَ تُساعِدُكَ۔")
                ),
                commonMistake = "Will کے بعد فعل کے ساتھ ing یا 2nd form لگا دینا، جیسے 'Will going' کہنا (درست: 'Will go')۔",
                practiceQuestion = "خالی جگہ پر کریں: 'We ___ (visit) Lahore next week.'",
                practiceAnswerHint = "We will visit Lahore next week."
            )
            "passive_voice" -> LessonContent(
                topicTitleEn = "Passive Voice",
                topicTitleUr = "فعلِ مجہول (Passive Voice)",
                englishRule = "In Passive Voice, the focus shifts from the doer (subject) to the receiver (object) of the action. Structure: Object + Be + Past Participle (3rd Form).",
                ruleUrdu = "فعلِ مجہول میں فاعل (کام کرنے والے) کے بجائے مفعول (جس پر کام ہوا) پر توجہ دی جاتی ہے۔ اس کا ڈھانچہ: مفعول + Be (is/am/are/was/were) + 3rd Form ہوتا ہے۔",
                arabicGrammarComparison = "جیسے آپ نے عربی میں سیکھا ہے، 'المبني للمجهول' میں فاعل کو حذف کر کے نائب فاعل لایا جاتا ہے اور فعل کی حرکات تبدیل کر دی جاتی ہیں (مثلاً: 'كُتِبَ الكِتابُ' - کتاب لکھی گئی)۔",
                similarities = listOf(
                    "دونوں میں فاعل کے بجائے مفعول/نائب فاعل کو بنیادی اہمیت دی جاتی ہے",
                    "فاعل کے معلوم نہ ہونے یا اسے ظاہر نہ کرنے کی صورت میں استعمال ہوتے ہیں"
                ),
                differences = listOf(
                    "انگریزی میں امدادی فعل (Be) اور 3rd Form کا استعمال ہوتا ہے",
                    "عربی میں فعل کے اندرونی اعراب پیش اور زیر سے تبدیل ہوتے ہیں (كَتَبَ سے كُتِبَ)"
                ),
                examples = listOf(
                    BilingualExample("The book was written.", "کتاب لکھی گئی۔", "جیسے عربی میں: كُتِبَ الكِتابُ (نائب فاعل مرفوع ہوتا ہے)۔"),
                    BilingualExample("The door is opened.", "دروازہ کھولا جاتا ہے۔", "جیسے عربی میں: يُفْتَحُ البابُ۔")
                ),
                commonMistake = "فعلِ مجہول میں 'Be' کی صورت یا 3rd Form نہ لگانا، جیسے 'The book written' کہنا (درست: 'The book was written')۔",
                practiceQuestion = "مجہول میں تبدیل کریں: 'He broke the glass.'",
                practiceAnswerHint = "The glass was broken."
            )
            else -> LessonContent(
                topicTitleEn = "English Grammar",
                topicTitleUr = "انگریزی گرامر",
                englishRule = "English grammar provides structure for sentences.",
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
    }
}
