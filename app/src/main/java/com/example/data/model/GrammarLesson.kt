package com.example.data.model

import kotlinx.serialization.Serializable

enum class ProficiencyLevel(val labelEn: String, val labelAr: String, val promptContext: String) {
    BEGINNER("Beginner", "ابتدائی", "beginner (focus on basic sentence structures, present tense, verbs to be, articles, simple vocabulary, and direct Arabic comparisons)"),
    INTERMEDIATE("Intermediate", "درمیانی", "intermediate (focus on continuous/perfect tenses, conditionals, modal verbs, relative clauses, and subtle differences with Arabic verb forms)"),
    ADVANCED("Advanced", "اعلیٰ", "advanced (focus on subjunctive mood, complex passive voices, inverted conditionals, idiomatic phrasal verbs, and nuanced rhetoric rhetoric in Arabic vs English)")
}

@Serializable
data class GrammarTopic(
    val id: String,
    val titleEn: String,
    val titleAr: String,
    val category: String,
    val quickComparison: String,
    val level: String
)

@Serializable
data class BilingualExample(
    val englishSentence: String,
    val arabicTranslation: String,
    val note: String
)

@Serializable
data class LessonContent(
    val topicTitleEn: String,
    val topicTitleAr: String,
    val englishRule: String,
    val arabicConnection: String,
    val similarities: List<String>,
    val differences: List<String>,
    val examples: List<BilingualExample>,
    val commonMistake: String,
    val practiceQuestion: String,
    val practiceAnswerHint: String = ""
)

object CurriculumData {
    val topics = listOf(
        GrammarTopic(
            id = "verb_to_be",
            titleEn = "Verb 'To Be' (Am/Is/Are)",
            titleAr = "فعلِ ربط اور جملہ اسمیہ (Verb To Be)",
            category = "بنیادی قواعد • Foundations",
            quickComparison = "انگریزی جملوں میں 'am/is/are' جیسے فعلِ ربط کا ہونا لازمی ہے۔ عربی گرامر میں، زمانہ حال میں جملہ اسمیہ (مبتدا اور خبر) کے درمیان کسی اضافی فعل کی ضرورت نہیں ہوتی۔",
            level = "Beginner"
        ),
        GrammarTopic(
            id = "articles_a_an_the",
            titleEn = "Definite & Indefinite Articles (A / An / The)",
            titleAr = "حرفِ تعریف اور تنکیر (Articles vs أل التعريف)",
            category = "بنیادی قواعد • Foundations",
            quickComparison = "انگریزی کا 'The' عربی کے (أل التعريف) کی طرح مخصوص اسموں کے لیے کام کرتا ہے، جبکہ عام اسموں (نکرہ) کے لیے انگریزی 'a/an' استعمال کرتی ہے اور عربی میں صرف تنوین استعمال ہوتی ہے۔",
            level = "Beginner"
        ),
        GrammarTopic(
            id = "present_simple_vs_nominal",
            titleEn = "Present Simple Tense",
            titleAr = "فعلِ حال سادہ اور جملہ فعلیہ (Present Simple)",
            category = "زمانے • Tenses",
            quickComparison = "انگریزی میں Present Simple میں ہی/شی/اٹ (He/She/It) کے لیے فعل کے ساتھ '-s' لگتا ہے۔ عربی میں، فعل مضارع میں شروع میں مخصوص علامتیں (يـ، تـ، أ، نـ) لگائی جاتی ہیں۔",
            level = "Beginner"
        ),
        GrammarTopic(
            id = "present_continuous",
            titleEn = "Present Continuous (am/is/are + -ing)",
            titleAr = "فعلِ حال جاری اور اسمِ فاعل (Present Continuous)",
            category = "زمانے • Tenses",
            quickComparison = "عربی میں حال کے کام کو ظاہر کرنے کے لیے فعل مضارع (يأكل الآن) یا اسمِ فاعل (هو ذاهب) استعمال ہوتا ہے، جبکہ انگریزی میں 'to be + verb-ing' کا فارمولا لازمی ہے۔",
            level = "Intermediate"
        ),
        GrammarTopic(
            id = "present_perfect",
            titleEn = "Present Perfect (have/has + past participle)",
            titleAr = "فعلِ حال تمام اور موازنہ بـ (قد + فعل ماضی)",
            category = "زمانے • Tenses",
            quickComparison = "انگریزی کا Present Perfect زمانہ عربی کے (قَدْ + فعل ماضی) کے قریب ترین ہے، جہاں کام ماضی میں ہوا ہو لیکن اس کا اثر یا تعلق حال سے برقرار ہو۔",
            level = "Intermediate"
        ),
        GrammarTopic(
            id = "present_perfect_continuous",
            titleEn = "Present Perfect Continuous (have/has been + -ing)",
            titleAr = "فعلِ حال تمام جاری (Present Perfect Continuous)",
            category = "زمانے • Tenses",
            quickComparison = "انگریزی میں ماضی میں شروع ہونے والے اور حال میں بھی جاری کام کے لیے 'have/has been + verb-ing' استعمال ہوتا ہے۔ عربی میں (ما زالَ / لا يزالُ + فعل مضارع) کا اسلوب استعمال ہوتا ہے۔",
            level = "Intermediate"
        ),
        GrammarTopic(
            id = "past_simple",
            titleEn = "Past Simple Tense (Verb + -ed / Irregular)",
            titleAr = "فعلِ ماضی سادہ (Past Simple / الماضي البسيط)",
            category = "زمانے • Tenses",
            quickComparison = "ماضی میں مکمل ہو جانے والے کاموں کے لیے استعمال ہوتا ہے۔ عربی کے فعلِ ماضی (الفعل الماضي / فَعَلَ) کے ساتھ مکمل مطابقت رکھتا ہے۔",
            level = "Beginner"
        ),
        GrammarTopic(
            id = "past_continuous",
            titleEn = "Past Continuous (was/were + -ing)",
            titleAr = "فعلِ ماضی استمراری / جاری (Past Continuous)",
            category = "زمانے • Tenses",
            quickComparison = "ماضی میں کسی مخصوص وقت پر جاری کام کو ظاہر کرتا ہے۔ عربی گرامر میں اس کا متبادل (كانَ + فعل مضارع) ہے، جیسے: كانَ يأكلُ (وہ کھا رہا تھا)۔",
            level = "Intermediate"
        ),
        GrammarTopic(
            id = "past_perfect",
            titleEn = "Past Perfect (had + past participle)",
            titleAr = "فعلِ ماضی بعید / تمام (Past Perfect)",
            category = "زمانے • Tenses",
            quickComparison = "ماضی کے دو کاموں میں سے پہلے مکمل ہونے والے کام کے لیے استعمال ہوتا ہے۔ عربی میں (كانَ + قَدْ + فعل ماضی) کا اسلوب اس کا متبادل ہے، جیسے: كانَ قد غادَرَ۔",
            level = "Intermediate"
        ),
        GrammarTopic(
            id = "past_perfect_continuous",
            titleEn = "Past Perfect Continuous (had been + -ing)",
            titleAr = "فعلِ ماضی بعید جاری (Past Perfect Continuous)",
            category = "زمانے • Tenses",
            quickComparison = "ماضی کے ایک نقطے سے پہلے جاری رہنے والے طویل عمل کو بیان کرتا ہے۔ عربی میں (كانَ + ما زالَ) یا (ظَلَّ / بَقِيَ + فعل مضارع) کا اسلوب استعمال ہوتا ہے۔",
            level = "Advanced"
        ),
        GrammarTopic(
            id = "future_simple",
            titleEn = "Future Simple (Will / Be going to)",
            titleAr = "فعلِ مستقبل سادہ (Future Simple)",
            category = "زمانے • Tenses",
            quickComparison = "مستقبل کے فیصلوں اور پیشگوئیوں کے لیے 'will' یا 'be going to' استعمال ہوتا ہے۔ عربی میں فعل مضارع سے پہلے حرفِ استقبال (سَـ / سَوْفَ) لگانے کے برابر ہے۔",
            level = "Beginner"
        ),
        GrammarTopic(
            id = "future_continuous",
            titleEn = "Future Continuous (will be + -ing)",
            titleAr = "فعلِ مستقبل جاری (Future Continuous)",
            category = "زمانے • Tenses",
            quickComparison = "مستقبل میں کسی مخصوص وقت پر جاری کام کو ظاہر کرتا ہے۔ عربی میں (سَيَكُونُ + فعل مضارع / اسم فاعل) کا اسلوب اس کا متبادل ہے۔",
            level = "Intermediate"
        ),
        GrammarTopic(
            id = "future_perfect",
            titleEn = "Future Perfect (will have + past participle)",
            titleAr = "فعلِ مستقبل تمام (Future Perfect)",
            category = "زمانے • Tenses",
            quickComparison = "مستقبل کے کسی مخصوص وقت سے پہلے مکمل ہو جانے والے کام کے لیے استعمال ہوتا ہے۔ عربی متبادل (سَيَكُونُ قد + فعل ماضی) ہے، جیسے: سَيَكُونُ قد أَتَمَّ عَمَلَهُ۔",
            level = "Advanced"
        ),
        GrammarTopic(
            id = "future_perfect_continuous",
            titleEn = "Future Perfect Continuous (will have been + -ing)",
            titleAr = "فعلِ مستقبل تمام جاری (Future Perfect Continuous)",
            category = "زمانے • Tenses",
            quickComparison = "مستقبل کے کسی معین وقت تک کسی کام کے مسلسل جاری رہنے کے دورانیے کو بیان کرتا ہے۔ عربی میں (سَيَكُونُ قد مَضَى عليه... وهو يَفْعَلُ) کا اسلوب ہوتا ہے۔",
            level = "Advanced"
        ),
        GrammarTopic(
            id = "conditionals_if",
            titleEn = "Conditionals (If sentences: Zero, First, Second)",
            titleAr = "شرطیہ جملے اور حروفِ شرط (If Conditionals)",
            category = "پیچیدہ ڈھانچے • Complex Structures",
            quickComparison = "عربی گرامر ممکنہ شرط (إذا/إنْ) اور غیر حقیقی یا ناممکن شرط (لَوْ) کے درمیان فرق کرتی ہے۔ انگریزی میں فعل کے زمانے تبدیل کر کے یہ فرق ظاہر کیا جاتا ہے۔",
            level = "Intermediate"
        ),
        GrammarTopic(
            id = "relative_pronouns",
            titleEn = "Relative Pronouns (Who / Which / That / Whose)",
            titleAr = "اسمائے موصولہ اور عائد کا موازنہ (Relative Pronouns)",
            category = "پیچیدہ ڈھانچے • Complex Structures",
            quickComparison = "عربی کے اسمائے موصولہ (الذي، التي، الذين) جنس اور تعداد کے مطابق بدلتے ہیں اور ان کے لیے ضمیرِ عائد کی ضرورت ہوتی ہے، جبکہ انگریزی میں Who/Which کے بعد عائد ضمیر نہیں لگتی۔",
            level = "Intermediate"
        ),
        GrammarTopic(
            id = "passive_voice",
            titleEn = "Passive Voice (Be + Past Participle)",
            titleAr = "فعلِ مجہول کا تقابلی جائزہ (Passive Voice)",
            category = "اعلیٰ گرامر • Advanced",
            quickComparison = "عربی میں فعلِ مجہول صرف حرکات (زبر، زیر، پیش) بدلنے سے بنتا ہے (کُتِبَ / يُكْتَب)۔ انگریزی میں فعل 'To Be' کے ساتھ فعل کی تیسری فارم استعمال ہوتی ہے اور فاعل کا ذکر 'by' سے ممکن ہے۔",
            level = "Advanced"
        ),
        GrammarTopic(
            id = "modals_obligation",
            titleEn = "Modal Verbs of Obligation (Must / Have to / Should)",
            titleAr = "وجوب اور نصیحت کے افعال (Modal Verbs)",
            category = "اعلیٰ گرامر • Advanced",
            quickComparison = "انگریزی کے امدادی افعال (Modals) فاعل کے مطابق تبدیل نہیں ہوتے (must go)۔ عربی میں اس کے متبادل کے طور پر (يجب عليك أنْ تفعل) جیسے اسلوب استعمال کیے جاتے ہیں۔",
            level = "Advanced"
        )
    )

    fun getDefaultLesson(topicId: String): LessonContent {
        return when (topicId) {
            "verb_to_be" -> LessonContent(
                topicTitleEn = "Verb 'To Be' (Am/Is/Are)",
                topicTitleAr = "فعلِ ربط اور جملہ اسمیہ (Verb To Be)",
                englishRule = "انگریزی زبان میں ہر مکمل جملے میں فعل (Verb) کا ہونا لازمی ہے۔ جب ہم موجودہ زمانے میں کسی شخص کی پہچان، پیشہ یا حالت بیان کرتے ہیں، تو ہمیں فعلِ ربط 'to be' (am, is, are) کا استعمال کرنا پڑتا ہے۔",
                arabicConnection = "عربی گرامر میں اس کے متبادل کے طور پر آپ نے جملہ اسمیہ (Nominal Sentence) پڑھا ہے جو مبتدا اور خبر سے مل کر بنتا ہے۔ عربی میں زمانہ حال کے لیے جملہ اسمیہ میں کسی اضافی فعلِ ربط کی ضرورت نہیں ہوتی۔",
                similarities = listOf(
                    "دونوں زبانیں ایک فاعل (مبتدا / Subject) کو اس کی حالت، صفت یا پہچان (خبر / Complement) سے جوڑتی ہیں۔",
                    "دونوں زبانوں میں واحد اور جمع ضمیروں کے لحاظ سے جملے کی ساخت بدلتی ہے۔"
                ),
                differences = listOf(
                    "انگریزی میں موجودہ زمانے میں فعل 'am/is/are' کا لکھنا لازمی ہے (جیسے 'He is happy')۔",
                    "عربی میں آپ بغیر کسی فعل کے براہِ راست کہتے ہیں 'هو سعيدٌ'، مبتدا اور صفت کے درمیان کوئی ظاہری فعل نہیں ہوتا۔",
                    "ماضی یا مستقبل میں بات کرتے ہوئے، عربی میں بھی 'كانَ / سَيَكونُ' کا استعمال کیا جاتا ہے، جو انگریزی 'was / will be' کی طرح کام کرتے ہیں۔"
                ),
                examples = listOf(
                    BilingualExample("She is a teacher.", "هي معلمةٌ.", "وضاحت: عربی میں مبتدا 'هي' اور خبر 'معلمة' کے درمیان کوئی فعل نہیں ہے، لیکن انگریزی میں 'is' لگانا لازمی ہے۔"),
                    BilingualExample("We are ready for the lesson.", "نحن مستعدون للدرس.", "وضاحت: جمع فاعل 'We' کے ساتھ انگریزی میں 'are' استعمال ہوا جو کہ مبتدا 'نحن' کے متبادل کام کر رہا ہے۔"),
                    BilingualExample("I am an engineer.", "أنا مهندسٌ.", "وضاحت: ضمیرِ متکلم 'I' کے ساتھ مخصوص فعلِ ربط 'am' کا استعمال کیا جاتا ہے۔")
                ),
                commonMistake = "اردو اور عربی مادری زبان والے طلبہ اکثر انگریزی لکھتے وقت فعلِ ربط کو چھوڑ دیتے ہیں اور 'He student' یا 'They happy' کہتے ہیں کیونکہ وہ اپنے ذہن میں جملہ اسمیہ (هو طالبٌ) کا لفظی ترجمہ کر رہے ہوتے ہیں۔ انگریزی میں 'am/is/are' لگانا کبھی نہ بھولیں!",
                practiceQuestion = "جملے کو درست کریں: 'My brother ___ an Arabic teacher at the school.' (am / is / are / nothing)",
                practiceAnswerHint = "درست جواب 'is' ہے کیونکہ 'My brother' واحد غائب (He = هو) ہے، جس کے ساتھ 'is' استعمال ہوتا ہے۔"
            )
            "articles_a_an_the" -> LessonContent(
                topicTitleEn = "Definite & Indefinite Articles (A / An / The)",
                topicTitleAr = "حرفِ تعریف اور تنکیر (Definite vs Indefinite)",
                englishRule = "انگریزی میں 'The' ایک معرفہ آرٹیکل (Definite Article) ہے جو مخصوص اور جانے پہچانے اسموں کے لیے استعمال ہوتا ہے۔ 'A' اور 'An' نکرہ آرٹیکلز (Indefinite Articles) ہیں جو واحد، قابلِ شمار اسموں کے شروع میں عام تذکرے کے لیے استعمال ہوتے ہیں۔",
                arabicConnection = "عربی گرامر میں بھی آپ نے اسمِ معرفہ بنانے کے لیے (أل التعريف) پڑھا ہے، اور نکرہ کو ظاہر کرنے کے لیے اسم کے آخر میں تنوین (التنوين) کا استعمال سیکھا ہے۔",
                similarities = listOf(
                    "انگریزی کا 'The' بالکل عربی کے (أل التعريف) کی طرح کام کرتا ہے، جو کسی بھی اسم کو خاص اور معین بناتا ہے۔",
                    "دونوں زبانیں پہلے سے معلوم (خاص) اسم اور عمومی (غیر معین) اسم کے درمیان واضح فرق کرتی ہیں۔"
                ),
                differences = listOf(
                    "عربی میں نکرہ (Indefinite) کے لیے کوئی الگ سے لفظ نہیں ہوتا، بلکہ صرف تنوین (جیسے: كتابٌ) استعمال ہوتی ہے۔ انگریزی میں واحد نکرہ اسم سے پہلے 'a' یا 'an' لگانا لازمی ہے (a book)۔",
                    "عربی میں عام مادی تصورات یا خلاصہ علوم کے ساتھ بھی 'أل' لگایا جاتا ہے (جیسے: العلم نورٌ)، جبکہ انگریزی میں غیر مادی اور ناقابلِ شمار اسموں کے ساتھ عمومی حالات میں 'The' نہیں لگتا (e.g. 'Science is light', NOT 'The science').",
                    "عربی میں صفت بھی موصوف کے مطابق معرفہ یا نکرہ بنتی ہے (البيتُ الكبيرُ)، لیکن انگریزی میں 'the' صرف ایک بار صفت سے پہلے آتا ہے (the big house)۔"
                ),
                examples = listOf(
                    BilingualExample("I read a book yesterday.", "قرأتُ كتاباً بالأمس.", "وضاحت: لفظ 'a book' نکرہ ہے، جو عربی میں تنوین 'كتاباً' کے متبادل ہے۔"),
                    BilingualExample("The book was interesting.", "كان الكتابُ ممتعاً.", "وضاحت: چونکہ کتاب کا ذکر پہلے ہو چکا ہے اور وہ اب معلوم ہے، اس لیے ہم نے 'The' (الكتاب) استعمال کیا۔"),
                    BilingualExample("An apple is good for health.", "تفاحةٌ مفيدةٌ للصحة.", "وضاحت: 'An' کا استعمال ایسے الفاظ سے پہلے ہوتا ہے جو واول آواز (a, e, i, o, u) سے شروع ہوں۔")
                ),
                commonMistake = "طالب علم اکثر عام تصورات کے ساتھ بھی غلطی سے 'The' لگا دیتے ہیں (مثلاً 'The life is beautiful' کہہ دیتے ہیں، کیونکہ عربی میں 'الحياة جميلة' کہا جاتا ہے)۔ انگریزی میں عام حقائق میں 'The' حذف کیا جاتا ہے۔",
                practiceQuestion = "درست جملہ منتخب کریں: A) 'I saw elephant.' B) 'I saw an elephant.' C) 'I saw a elephant.'",
                practiceAnswerHint = "درست جواب 'B' ہے کیونکہ واحد قابلِ شمار اسم کے ساتھ نکرہ آرٹیکل ضروری ہے، اور 'elephant' چونکہ واول آواز سے شروع ہو رہا ہے، اس لیے 'an' لگے گا۔"
            )
            "conditionals_if" -> LessonContent(
                topicTitleEn = "Conditionals (If sentences: Zero, First, Second)",
                topicTitleAr = "شرطیہ جملے اور حروفِ شرط (If Conditionals)",
                englishRule = "شرطیہ جملے کسی شرط (if-clause) اور اس کے نتیجے (result) کو جوڑتے ہیں۔ First Conditional حقیقی یا ممکنہ حالات کے لیے استعمال ہوتا ہے۔ Second Conditional فرضی، تصوراتی یا نا ممکن حالات کے لیے استعمال ہوتا ہے جس میں 'if' کے بعد ماضی سادہ (Past Simple) کا استعمال ہوتا ہے۔",
                arabicConnection = "عربی گرامر میں اس کے متبادل کے طور پر آپ نے اسلوبِ شرط اور ادواتِ شرط سیکھے ہیں۔ عربی ممکنہ شرط کے لیے (إذا / إنْ) کا استعمال کرتی ہے، جبکہ ناممکن یا فرضی شرط کے لیے (لَوْ) کا انتخاب کرتی ہے۔",
                similarities = listOf(
                    "دونوں زبانوں میں جملہ دو حصوں پر مشتمل ہوتا ہے: جملہ شرط (If clause) اور جواب شرط (Result clause)۔",
                    "دونوں زبانیں حقیقی امکانات اور غیر حقیقی خواہشات کے لیے مختلف گرامر اور علامات استعمال کرتی ہیں۔"
                ),
                differences = listOf(
                    "عربی میں فرضی یا ناممکن حالت کے لیے مخصوص لفظ 'لَوْ' (حرف شرط غیر جازم) استعمال ہوتا ہے۔ انگریزی میں اس فرق کو ظاہر کرنے کے لیے 'if' کے بعد فعل کے زمانے کو ماضی (Past Simple) میں تبدیل کیا جاتا ہے (مثال کے طور پر: If I had money = اگر میرے پاس پیسے ہوتے، جو کہ موجودہ فرضی حالت ہے)۔",
                    "انگریزی کے فرضی جملوں (Second Conditional) میں 'if' والے حصے میں ہمیشہ Past Simple اور دوسرے حصے میں 'would + verb' کا فارمولا لگایا جاتا ہے۔"
                ),
                examples = listOf(
                    BilingualExample("If you study hard, you will succeed.", "إذا درستَ بجدٍّ، فسوف تنجحُ.", "وضاحت: یہ ممکنہ اور حقیقی شرط ہے (First Conditional)۔ یہاں 'if' کے ساتھ فعل حال اور جواب میں 'will' استعمال ہوتا ہے۔"),
                    BilingualExample("If I were rich, I would travel the world.", "لو كنتُ غنياً، لسافرتُ حول العالم.", "وضاحت: یہ غیر حقیقی یا فرضی شرط ہے (Second Conditional)۔ عربی متبادل 'لو' ہے اور انگریزی میں 'were' اور 'would' استعمال ہوا ہے۔"),
                    BilingualExample("If it rains tomorrow, we will stay home.", "إنْ أمطرت غداً، سنبقى في البيت.", "وضاحت: مستقبل کا ممکنہ واقعہ۔")
                ),
                commonMistake = "اردو اور عربی بولنے والے اکثر شرطیہ جملوں کے 'if' والے حصے میں بھی غلطی سے 'will' لگا دیتے ہیں (جیسے: 'If it will rain tomorrow...')۔ یاد رکھیں، انگریزی قواعد کے مطابق 'if' کے فوراً بعد والے کلاز میں 'will' کا استعمال ممنوع ہے!",
                practiceQuestion = "اس جملے کا انگریزی ترجمہ کریں: 'لو كان لدي وقت، لزرتك اليوم' (اشارہ: فرضی جملہ ہے)۔",
                practiceAnswerHint = "درست ترجمہ ہوگا: 'If I had time, I would visit you today.' (فرضی حالت کے لیے 'had' اور 'would' استعمال کیا گیا ہے)۔"
            )
            "present_perfect_continuous" -> LessonContent(
                topicTitleEn = "Present Perfect Continuous (have/has been + -ing)",
                topicTitleAr = "فعلِ حال تمام جاری (Present Perfect Continuous)",
                englishRule = "The Present Perfect Continuous tense describes an action that started in the past and has continued up to the present moment, or finished very recently. Formula: Subject + have/has been + Verb-ing.",
                arabicConnection = "Likewise in Arabic, you have learned the continuous structure (ما زالَ / لا يزالُ + الفعل المضارع) or using (مُنْذُ + اسم الزمن) to express an action started in the past and still ongoing in the present.",
                similarities = listOf(
                    "Both languages emphasize that the action has a duration (استمرار) starting from the past to the present.",
                    "Both frequently use time indicators (English: for/since; Arabic: منذ/لمدة)."
                ),
                differences = listOf(
                    "English requires three auxiliary verb parts: 'have/has' + 'been' + 'verb-ing' (e.g. 'have been studying').",
                    "Arabic uses the auxiliary 'ما زالَ' followed directly by the present tense verb (e.g. 'ما زلتُ أدرسُ منذ ساعتين')."
                ),
                examples = listOf(
                    BilingualExample("I have been reading for two hours.", "أنا أقرأُ منذ ساعتينِ (ما زلتُ أقرأُ).", "Shows an action that began 2 hours ago and is still in progress."),
                    BilingualExample("She has been living in Cairo since 2020.", "هي تعيشُ في القاهرةِ منذ عامِ 2020.", "Highlights continuous residence up to the present moment."),
                    BilingualExample("They have been working all day.", "هم يعملون طوال اليوم.", "Emphasizes ongoing past-to-present continuous effort.")
                ),
                commonMistake = "Arabic speakers often incorrectly use Present Continuous ('I am living here since 2020') instead of Present Perfect Continuous. Always use 'have/has been + -ing' when 'for' or 'since' marks duration!",
                practiceQuestion = "Correct the sentence: 'He is sleeping for three hours.'",
                practiceAnswerHint = "The correct form is 'He has been sleeping for three hours.' because it connects past duration to the present."
            )
            "past_simple" -> LessonContent(
                topicTitleEn = "Past Simple Tense (Verb + -ed / Irregular)",
                topicTitleAr = "فعلِ ماضی سادہ (Past Simple / الماضي البسيط)",
                englishRule = "The Past Simple tense expresses an action completed at a definite time in the past. Regular verbs end in '-ed', while irregular verbs take specific past forms (e.g., go -> went, see -> saw).",
                arabicConnection = "Likewise in Arabic, you have learned (الفعل الماضي البسيط) such as (كَتَبَ، ذَهَبَ، أَنَهَى) which describes a finished event in the past.",
                similarities = listOf(
                    "Both languages use a direct past verb form to indicate completed past actions.",
                    "Both signify that the event has ended and has no ongoing effect in the present."
                ),
                differences = listOf(
                    "English has irregular verbs (went, ate, bought) that do not take '-ed' and must be memorized.",
                    "Negative and question forms in English Past Simple require the auxiliary 'did / did not' + base verb (e.g., 'did you go?', 'I didn't see')."
                ),
                examples = listOf(
                    BilingualExample("He visited Riyadh last week.", "زَارَ الرياضَ الأسبوعَ الماضي.", "Completed action in past time."),
                    BilingualExample("They did not attend the lecture.", "لَمْ يَحْضُرُوا المُحَاضَرَةَ.", "Negative past sentence using 'did not' + base verb."),
                    BilingualExample("Did you write the report?", "هَلْ كَتَبْتَ التَّقْرِيرَ؟", "Past simple question structure.")
                ),
                commonMistake = "Using double past forms in questions/negatives (e.g. 'I didn't went'). Always use the base verb after 'did / didn't'!",
                practiceQuestion = "Choose the correct option: 'Yesterday, I (see / saw / seen) a great movie.'",
                practiceAnswerHint = "The correct answer is 'saw' (the past simple form of 'see')."
            )
            "past_continuous" -> LessonContent(
                topicTitleEn = "Past Continuous (was/were + -ing)",
                topicTitleAr = "فعلِ ماضی استمراری / جاری (Past Continuous)",
                englishRule = "The Past Continuous tense describes an action that was ongoing at a specific time in the past, or was interrupted by another past action. Formula: Subject + was/were + Verb-ing.",
                arabicConnection = "Likewise in Arabic, you have learned the compound continuous past (كانَ + الفعل المضارع) like (كانَ يقرأُ) to describe continuous background past actions.",
                similarities = listOf(
                    "Both languages combine a past auxiliary (was/were in English, كان in Arabic) with a continuous verb form.",
                    "Both are ideal for setting background scenes in storytelling."
                ),
                differences = listOf(
                    "English uses 'was' for singular (I, he, she, it) and 'were' for plural (we, you, they) + verb-ing.",
                    "Arabic inflects 'كان' for pronoun gender and number, followed by the present tense verb."
                ),
                examples = listOf(
                    BilingualExample("I was reading when the phone rang.", "كُنْتُ أَقْرَأُ عِنْدَمَا رَنَّ الهَاتِفُ.", "Ongoing past continuous interrupted by past simple."),
                    BilingualExample("They were studying all night.", "كَانُوا يَدْرُسُونَ طِوَالَ اللَّيْلِ.", "Continuous duration during a specific past time."),
                    BilingualExample("She was not sleeping at 10 PM.", "لَمْ تَكُنْ تَنَامُ فِي السَّاعَةِ 10 مَسَاءً.", "Negative past continuous statement.")
                ),
                commonMistake = "Confusing Past Simple with Past Continuous. Use Past Continuous for the background ongoing action ('was cooking') and Past Simple for the interrupting action ('arrived').",
                practiceQuestion = "Fill in the blank: 'While we ___ (eat) dinner, it started raining.'",
                practiceAnswerHint = "The correct form is 'were eating'."
            )
            "past_perfect" -> LessonContent(
                topicTitleEn = "Past Perfect (had + past participle)",
                topicTitleAr = "فعلِ ماضی بعید / تمام (Past Perfect)",
                englishRule = "The Past Perfect tense expresses an action completed before another action or point in the past. Formula: Subject + had + Past Participle (V3).",
                arabicConnection = "Likewise in Arabic, you have learned the past completion structure (كانَ + قَدْ + الفعل الماضي) to mark an action that occurred prior to another past event.",
                similarities = listOf(
                    "Both languages use past completion auxiliaries (had in English, كان قد in Arabic) to establish clear order between two past events.",
                    "Both help build precise chronological timelines in narratives."
                ),
                differences = listOf(
                    "English uses the auxiliary 'had' for all subjects followed by past participle (V3).",
                    "Arabic inflects 'كان' according to the subject before adding 'قد' and the past verb."
                ),
                examples = listOf(
                    BilingualExample("The train had left before I arrived.", "كَانَ القِطَارُ قَدْ غَادَرَ قَبْلَ أَنْ أَصِلَ.", "Train leaving happened FIRST before arrival."),
                    BilingualExample("She had finished homework before dinner.", "كَانَتْ قَدْ أَنَهَتْ الوَاجِبَ قَبْلَ العَشَاءِ.", "Earlier completed past action."),
                    BilingualExample("They had never seen snow until that day.", "لَمْ يَكُونُوا قَدْ رَأَوْا الثَّلْجَ مِنْ قَبْلُ.", "Prior experience up to a past moment.")
                ),
                commonMistake = "Using Past Perfect when there is only a single isolated past event without sequence. Use Past Simple for single past actions!",
                practiceQuestion = "Correct: 'When we reached the airport, the plane already took off.'",
                practiceAnswerHint = "Correct: '...the plane had already taken off.' because taking off happened first."
            )
            "past_perfect_continuous" -> LessonContent(
                topicTitleEn = "Past Perfect Continuous (had been + -ing)",
                topicTitleAr = "فعلِ ماضی بعید جاری (Past Perfect Continuous)",
                englishRule = "The Past Perfect Continuous shows an action that started in the past and continued up until another point in the past, emphasizing duration. Formula: Subject + had been + Verb-ing.",
                arabicConnection = "Likewise in Arabic, you have learned structures like (كانَ + ظَلَّ / يواصلُ + الفعل المضارع) to emphasize prolonged continuous past effort leading to a past milestone.",
                similarities = listOf(
                    "Both languages focus on the cause, duration, and ongoing process preceding a past result."
                ),
                differences = listOf(
                    "English pairs three auxiliary forms: 'had' + 'been' + 'verb-ing'.",
                    "Arabic uses continuance past verbs like 'ظلّ' or 'استمرّ'."
                ),
                examples = listOf(
                    BilingualExample("He had been driving for 5 hours before stopping.", "كَانَ قَدْ ظَلَّ يَقُودُ 5 سَاعَاتٍ قَبْلَ التَّوَقُّفِ.", "Prolonged driving duration before stopping."),
                    BilingualExample("They were tired because they had been working.", "كَانُوا تَعْبَانِينَ لِأَنَّهُمْ كَانُوا يَعْمَلُونَ.", "Past continuous work causing past fatigue."),
                    BilingualExample("She had been studying for years before graduation.", "كَانَتْ تُوَاصِلُ الدِّرَاسَةَ لِسَنَوَاتٍ قَبْلَ التَّخَرُّجِ.", "Ongoing preparation before past milestone.")
                ),
                commonMistake = "Confusing 'had been doing' with simple 'was doing'. Use 'had been doing' when duration before a specific past event is highlighted.",
                practiceQuestion = "Complete: 'They ___ (wait) for 2 hours before the bus arrived.'",
                practiceAnswerHint = "The correct form is 'had been waiting'."
            )
            "future_simple" -> LessonContent(
                topicTitleEn = "Future Simple (Will / Be going to)",
                topicTitleAr = "فعلِ مستقبل سادہ (Future Simple)",
                englishRule = "The Future Simple tense expresses predictions, promises, spontaneous decisions ('will') or pre-planned intentions ('be going to'). Formula: Subject + will + Base Verb.",
                arabicConnection = "Likewise in Arabic, you have learned to attach the future particle (سَـ) for near future or (سَوْفَ) for planned future before (الفعل المضارع).",
                similarities = listOf(
                    "Both languages convert present verbs into future state using concise future particles ('will' / 'سـ / سوف').",
                    "Both distinguish between instant decisions and planned intentions."
                ),
                differences = listOf(
                    "English differentiates 'will' (spontaneous/promise) from 'going to' (planned intention).",
                    "Arabic uses 'سَـ' for immediate/near actions and 'سَوْفَ' for general future."
                ),
                examples = listOf(
                    BilingualExample("I will help you with English.", "سَأُسَاعِدُكَ فِي اللُّغَةِ الإِنْجِلِيزِيَّةِ.", "Spontaneous decision or promise."),
                    BilingualExample("She is going to study medicine next year.", "سَوْفَ تَدْرُسُ الطِّبَّ فِي العَامِ القَادِمِ.", "Pre-planned future goal."),
                    BilingualExample("We will arrive tomorrow morning.", "سَنَصِلُ غَدًا صَبَاحًا.", "Future fact or arrival expectation.")
                ),
                commonMistake = "Adding '-s' or '-ing' after 'will' (e.g. 'He will goes'). Always use the bare base verb after 'will'!",
                practiceQuestion = "Correct the error: 'I will visiting my friend tomorrow.'",
                practiceAnswerHint = "Correct: 'I will visit my friend tomorrow.' (Use base verb after 'will')."
            )
            "future_continuous" -> LessonContent(
                topicTitleEn = "Future Continuous (will be + -ing)",
                topicTitleAr = "فعلِ مستقبل جاری (Future Continuous)",
                englishRule = "The Future Continuous tense expresses an action that will be in progress at a specific time in the future. Formula: Subject + will be + Verb-ing.",
                arabicConnection = "Likewise in Arabic, you have learned the compound structure (سَيَكُونُ + الفعل المضارع) or (سَيَكُونُ + اسم الفاعل) to project ongoing future scenes.",
                similarities = listOf(
                    "Both languages pair a future auxiliary ('will be' in English, 'سيكون' in Arabic) with a continuous verb."
                ),
                differences = listOf(
                    "English formula requires 'will' + 'be' + 'verb-ing'.",
                    "Arabic inflects 'سيكون' for subject before the present verb."
                ),
                examples = listOf(
                    BilingualExample("At 9 PM tonight, I will be watching the match.", "فِي السَّاعَةِ 9 مَسَاءً، سَأَكُونُ أُشَاهِدُ المُبَارَاةَ.", "Ongoing action at a specific future moment."),
                    BilingualExample("They will be waiting for us at the airport.", "سَيَكُونُونَ فِي انْتِظَارِنَا فِي المَطَارِ.", "Continuous future expectation."),
                    BilingualExample("She will be working tomorrow afternoon.", "سَتَكُونُ تَعْمَلُ غَدًا بَعْدَ الظُّهْرِ.", "Scheduled ongoing future activity.")
                ),
                commonMistake = "Omitting 'be' (e.g., saying 'I will watching'). Remember to include both 'be' and '-ing'!",
                practiceQuestion = "Fill in the blank: 'Don't call at 8 AM, I ___ (sleep).' (Future Continuous)",
                practiceAnswerHint = "The correct answer is 'will be sleeping'."
            )
            "future_perfect" -> LessonContent(
                topicTitleEn = "Future Perfect (will have + past participle)",
                topicTitleAr = "فعلِ مستقبل تمام (Future Perfect)",
                englishRule = "The Future Perfect tense describes an action that will be finished before a specific deadline in the future. Formula: Subject + will have + Past Participle (V3).",
                arabicConnection = "Likewise in Arabic, you have learned the structure (سَيَكُونُ + قَدْ + الفعل الماضي) to show completion status by a specified future point.",
                similarities = listOf(
                    "Both languages view an action from a future point back to confirm its completion.",
                    "Both use deadline markers (English: by / by the time; Arabic: بحلول / قبل)."
                ),
                differences = listOf(
                    "English uses 'will have' for ALL subjects (I, he, they) + past participle.",
                    "Arabic inflects 'سيكون' according to subject before 'قد' and past verb."
                ),
                examples = listOf(
                    BilingualExample("By next year, I will have graduated.", "بِحُولِ العَامِ القَادِمِ، سَأَكُونُ قَدْ تَخَرَّجْتُ.", "Completed graduation before next year."),
                    BilingualExample("She will have finished the report by 5 PM.", "سَتَكُونُ قَدْ أَنَهَتِ التَّقْرِيرَ بِحُلُولِ 5 مَسَاءً.", "Completed task prior to deadline."),
                    BilingualExample("They will have built the hall by December.", "سَيَكُونُونَ قَدْ بَنَوْا القَاعَةَ بِحُلُولِ دِيسَمْبِرَ.", "Completed construction landmark.")
                ),
                commonMistake = "Saying 'He will has finished'. In Future Perfect, ALWAYS use 'will have' for all pronouns!",
                practiceQuestion = "Correct: 'She will has completed the assignment by tomorrow.'",
                practiceAnswerHint = "Correct: 'She will have completed...' ('will' is always followed by 'have')."
            )
            "future_perfect_continuous" -> LessonContent(
                topicTitleEn = "Future Perfect Continuous (will have been + -ing)",
                topicTitleAr = "فعلِ مستقبل تمام جاری (Future Perfect Continuous)",
                englishRule = "The Future Perfect Continuous describes an ongoing action that will continue up to a specific point in the future, highlighting duration. Formula: Subject + will have been + Verb-ing.",
                arabicConnection = "Likewise in Arabic, you have learned to express projected continuous duration using phrases like (سَيَكُونُ قَدْ أَمْضَى [مُدَّةً] وَهُوَ يَعْمَلُ...).",
                similarities = listOf(
                    "Both languages project ahead to measure accumulated continuous time spent on an activity."
                ),
                differences = listOf(
                    "English uses four verbal components: 'will' + 'have' + 'been' + 'verb-ing'.",
                    "Arabic uses duration phrases with present active structures."
                ),
                examples = listOf(
                    BilingualExample("By next month, I will have been working here for 5 years.", "بِحُلُولِ الشَّهْرِ القَادِمِ، سَأَكُونُ قَدْ أَمْضَيْتُ 5 سَنَوَاتٍ وَأَنَا أَعْمَلُ هُنَا.", "Accumulated 5 years duration projected next month."),
                    BilingualExample("She will have been studying for 3 hours when you arrive.", "سَتَكُونُ قَدْ ظَلَّتْ تَدْرُسُ 3 سَاعَاتٍ عِنْدَ وُصُولِكَ.", "Continuous duration up to a future event."),
                    BilingualExample("They will have been living in Dubai for ten years.", "سَيَكُونُونَ قَدْ أَتَمُّوا 10 سَنَوَاتٍ وَهُمْ يَعِيشُونَ فِي دُبَيّ.", "Duration milestone at a future date.")
                ),
                commonMistake = "Using Future Perfect for continuous duration. Use Future Perfect Continuous when 'for [duration]' is emphasized before a future date!",
                practiceQuestion = "Complete: 'In October, he ___ (teach) here for 10 years.' (Future Perfect Continuous)",
                practiceAnswerHint = "The correct answer is 'will have been teaching'."
            )
            else -> LessonContent(
                topicTitleEn = "Present Simple Tense",
                topicTitleAr = "فعل حال سادہ اور جملہ فعلیہ (Present Simple)",
                englishRule = "فعل حال سادہ (Present Simple) مستقل حقائق، عادات، اور روزمرہ کے معمولات کو بیان کرنے کے لیے استعمال ہوتا ہے۔ واحد غائب فاعل (He, She, It) کے ساتھ فعل کی پہلی فارم کے آخر میں '-s' یا '-es' کا اضافہ کیا جاتا ہے۔",
                arabicConnection = "عربی گرامر میں اس کے متبادل کے طور پر فعل مضارع معروف (Present Tense) استعمال ہوتا ہے، جو زمانہ حال میں کسی کام کے ہونے یا مستقل حقیقت کو ظاہر کرنے کے لیے شروع میں حروفِ اتین (ي، ت، أ، ن) کے ساتھ اعراب لیتا ہے۔",
                similarities = listOf(
                    "دونوں زبانوں میں یہ زمانہ عادات، روزمرہ کے معمولات اور کائناتی سچائیوں کو بیان کرنے کے لیے استعمال ہوتا ہے۔",
                    "دونوں زبانوں میں فاعل کے صیغے (واحد، جمع، غائب، مخاطب) کے لحاظ سے فعل کی شکل تبدیل ہوتی ہے۔"
                ),
                differences = listOf(
                    "انگریزی میں صرف واحد غائب (He/She/It یا کوئی واحد اسم) کے فعل کے آخر میں '-s' کا اضافہ ہوتا ہے، باقی تمام صیغے فعل کی اصل حالت ہی استعمال کرتے ہیں۔",
                    "عربی میں ہر ضمیر اور صیغے کے لیے فعل مضارع کی مکمل گردان ہوتی ہے اور اس کے شروع اور آخر میں تبدیلیاں آتی ہیں (جیسے: أكتب، تكتب، تكتبون)۔"
                ),
                examples = listOf(
                    BilingualExample("The sun rises in the east.", "تشرقُ الشمسُ من الشرق.", "وضاحت: کائناتی سچائی ہے۔ 'sun' چونکہ واحد غائب ہے، اس لیے فعل 'rise' کے ساتھ '-s' لگا۔"),
                    BilingualExample("Ahmed speaks Arabic fluently.", "يتحدثُ أحمدُ العربيةَ بطلاقةٍ.", "وضاحت: عادت/صلاحیت ہے۔ 'Ahmed' واحد غائب (He) ہے اس لیے 'speaks' استعمال ہوا۔"),
                    BilingualExample("They play football every week.", "هم يلعبون كرة القدم كل أسبوع.", "وضاحت: عادات و معمولات۔ جمع فاعل 'They' کے ساتھ فعل کے آخر میں '-s' نہیں لگایا جاتا۔")
                ),
                commonMistake = "طالب علم اکثر واحد غائب (He, She, It) کے ساتھ فعل کا '-s' لگانا بھول جاتے ہیں، جیسے 'He work in Lahore' کہہ دیتے ہیں۔ درست جملہ 'He works in Lahore' ہے۔",
                practiceQuestion = "جملے کی غلطی درست کریں: 'My sister go to school every morning.'",
                practiceAnswerHint = "درست جملہ ہوگا: 'My sister goes to school every morning.' کیونکہ 'My sister' واحد غائب (She) ہے جس کے فعل کے آخر میں '-es' کا اضافہ ضروری ہے۔"
            )
        }
    }
}
