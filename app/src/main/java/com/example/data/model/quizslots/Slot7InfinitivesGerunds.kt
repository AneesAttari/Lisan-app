package com.example.data.model.quizslots

import com.example.data.model.DailyQuiz365Generator.QuestionTemplate
import com.example.data.model.QuizSlotHelper
import com.example.data.model.QuizSlotHelper.SentenceVariant

object Slot7InfinitivesGerunds {
    val templates: List<QuestionTemplate> by lazy {
        val list = mutableListOf<QuestionTemplate>()
        fun v(text: String, ur: String, corr: String, dist: List<String>) = SentenceVariant(text, ur, corr, dist)

        val topics = listOf(
            Tuple4("G01_VERB_GERUND_ONLY", "Gerunds", "Verb Followed Only By Gerund", "GERUND:VERB_ONLY:ENJOY_ING", "avoided revealing", listOf("avoided to reveal", "avoided reveal", "avoided for revealing"), "The executive _____ sensitive corporate data.", "ایگزیکٹو نے حسّاس کارپوریٹ ڈیٹا کو ظاہر کرنے سے گریز کیا۔"),
            Tuple4("G02_VERB_INF_ONLY", "Infinitives", "Verb Followed Only By To Infinitive", "INFINITIVE:VERB_ONLY:DECIDE_TO_V1", "decided to expand", listOf("decided expanding", "decided expand", "decided for expanding"), "The board _____ their European operations.", "بورڈ نے اپنے یورپی کام کاج میں توسیع کا فیصلہ کیا۔"),
            Tuple4("G03_VERB_OBJ_BARE_INF", "Infinitives", "Perception Verb Object Bare Infinitive", "INFINITIVE:PERCEPTION:BARE_INF", "saw the intruder enter", listOf("saw the intruder to enter", "saw the intruder entered", "saw intruder entering to"), "The security camera _____ through the basement window.", "سیکیورٹی کیمرے نے گھس بیٹھیے کو تہہ خانے کی کھڑکی سے داخل ہوتے دیکھا۔"),
            Tuple4("G04_VERB_OBJ_TO_INF", "Infinitives", "Verb Object To Infinitive", "INFINITIVE:OBJ_TO_INF:PERSUADE_TO", "persuaded him to accept", listOf("persuaded him accepting", "persuaded him accept", "persuaded to him accept"), "The committee _____ the leadership role.", "کمیٹی نے انہیں قیادت کا کردار قبول کرنے پر آمادہ کیا۔"),
            Tuple4("G05_REMEMBER_ING_VS_TO", "Gerunds vs Infinitives", "Remember + Gerund Past Memory", "VERB_PATTERN:REMEMBER:GERUND_PAST", "remember locking", listOf("remember to lock", "remember lock", "remember for locking"), "I clearly _____ the front gate before leaving.", "مجھے اچھی طرح یاد ہے کہ میں نے نکلنے سے پہلے مرکزی گیٹ کو تالا لگایا تھا۔"),
            Tuple4("G06_FORGET_TO_VS_ING", "Gerunds vs Infinitives", "Forget + To Infinitive Duty", "VERB_PATTERN:FORGET:TO_INF_DUTY", "forgot to attach", listOf("forgot attaching", "forgot attach", "forgot for attaching"), "She _____ the project document to her email.", "وہ ای میل کے ساتھ پروجیکٹ کی دستاویز منسلک کرنا بھول گئی۔"),
            Tuple4("G07_STOP_ING_VS_TO", "Gerunds vs Infinitives", "Stop + Gerund Cessation", "VERB_PATTERN:STOP:GERUND_CESSATION", "stopped smoking", listOf("stopped to smoke", "stopped smoke", "stopped for smoking"), "He _____ three years ago for health reasons.", "اس نے صحت کی وجوہات کی بناء پر تین سال پہلے سگریٹ نوشی ترک کر دی۔"),
            Tuple4("G08_TRY_ING_VS_TO", "Gerunds vs Infinitives", "Try + Gerund Experiment", "VERB_PATTERN:TRY:GERUND_EXPERIMENT", "tried restarting", listOf("tried to restart", "tried restart", "tried for restarting"), "When the router lagged, I _____ it.", "جب روٹر سست ہوا، میں نے اسے ری اسٹارٹ کر کے دیکھا۔"),
            Tuple4("G09_REGRET_TO_VS_ING", "Gerunds vs Infinitives", "Regret + To Infinitive Formal Announcement", "VERB_PATTERN:REGRET:TO_INF_FORMAL", "regret to inform", listOf("regret informing", "regret inform", "regret for informing"), "We _____ you that your application was unsuccessful.", "ہمیں آپ کو مطلع کرتے ہوئے افسوس ہو رہا ہے کہ آپ کی درخواست ناکام رہی۔"),
            Tuple4("G10_PREPOSITION_GERUND", "Gerunds", "Preposition Followed By Gerund", "GERUND:PREPOSITION:LOOK_FORWARD_TO", "looks forward to receiving", listOf("looks forward to receive", "looks forward receiving", "looks forward for receive"), "The candidate _____ your response.", "امیدوار آپ کے جواب کا منتظر ہے۔"),
            Tuple4("G11_ADJECTIVE_TO_INF", "Infinitives", "Adjective Followed By To Infinitive", "INFINITIVE:ADJECTIVE:EAGER_TO_V1", "eager to participate", listOf("eager participating", "eager for participate", "eager to participating"), "Students are _____ in the hackathon.", "طلباء ہیکاتھون میں حصہ لینے کے لیے پرجوش ہیں۔"),
            Tuple4("G12_NOUN_TO_INF", "Infinitives", "Noun Followed By To Infinitive", "INFINITIVE:NOUN:DECISION_TO_V1", "opportunity to present", listOf("opportunity for present", "opportunity presenting", "opportunity to presenting"), "He gained an _____ at the conference.", "اسے کانفرنس میں پیشکش کرنے کا موقع ملا۔"),
            Tuple4("G13_TOO_ADJ_TO_INF", "Infinitives", "Too Adjective To Infinitive", "INFINITIVE:TOO_ADJ:TOO_COMPLEX_TO", "too complex to solve", listOf("too complex solving", "too complex to solving", "so complex to solve"), "The algorithm was _____ in one afternoon.", "الگورتھم اتنا پیچیدہ تھا کہ ایک دوپہر میں حل نہیں ہو سکتا تھا۔"),
            Tuple4("G14_ADJ_ENOUGH_TO_INF", "Infinitives", "Adjective Enough To Infinitive", "INFINITIVE:ENOUGH:QUALIFIED_ENOUGH_TO", "qualified enough to lead", listOf("enough qualified to lead", "qualified enough leading", "qualified enough for leading"), "She is _____ the engineering division.", "وہ انجینئرنگ ڈویژن کی قیادت کرنے کے لیے کافی اہل ہیں۔"),
            Tuple4("G15_PERFECT_GERUND", "Gerunds", "Perfect Gerund Having V3", "GERUND:PERFECT:HAVING_V3", "having completed", listOf("to have completed", "having complete", "have completing"), "He admitted _____ the audit prior to the deadline.", "اس نے ڈیڈ لائن سے پہلے آڈٹ مکمل کرنے کا اعتراف کیا۔"),
            Tuple4("G16_PASSIVE_GERUND", "Gerunds", "Passive Gerund Being V3", "GERUND:PASSIVE:BEING_V3", "being interrupted", listOf("to be interrupted", "being interrupt", "been interrupted"), "The lecturer disliked _____ during his speech.", "لیکچرر کو اپنی تقریر کے دوران مداخلت کا نشانہ بننا ناپسند تھا۔"),
            Tuple4("G17_PERFECT_INFINITIVE", "Infinitives", "Perfect Infinitive To Have V3", "INFINITIVE:PERFECT:TO_HAVE_V3", "to have written", listOf("to write", "having written", "to have write"), "The medieval poet is believed _____ over fifty sonnets.", "مانا جاتا ہے کہ قرون وسطی کے شاعر نے پچاس سے زیادہ غزلیں لکھیں۔"),
            Tuple4("G18_PASSIVE_INFINITIVE", "Infinitives", "Passive Infinitive To Be V3", "INFINITIVE:PASSIVE:TO_BE_V3", "to be signed", listOf("to sign", "being signed", "to be sign"), "The contracts are ready _____ by both parties.", "معاہدے دونوں فریقوں کی جانب سے دستخط کیے جانے کے لیے تیار ہیں۔"),
            Tuple4("G19_GERUND_AS_SUBJECT", "Gerunds", "Gerund Clause As Subject", "GERUND:SUBJECT:ING_CLAUSE", "Mastering a foreign language", listOf("Master a foreign language", "To master foreign language is", "Mastered a foreign language"), "_____ requires consistent practice.", "غیر ملکی زبان پر عبور حاصل کرنے کے لیے مسلسل مشق کی ضرورت ہوتی ہے۔"),
            Tuple4("G20_IT_IS_NO_USE_ING", "Gerunds", "It Is No Use Gerund", "GERUND:IDIOMATIC:IT_IS_NO_USE_ING", "It is no use complaining", listOf("It is no use to complain", "It is no use complain", "It is useless to complaining"), "_____ about spilled milk.", "اب پچھتائے کیا ہوت جب چڑیاں چگ گئیں کھیت۔"),
            Tuple4("G21_WORTH_GERUND", "Gerunds", "Worth + Gerund Structure", "GERUND:ADJECTIVE:WORTH_ING", "worth visiting", listOf("worth to visit", "worth visit", "worth for visiting"), "The national museum is definitely _____.", "قومی عجائب گھر یقیناً دیکھنے کے قابل ہے۔"),
            Tuple4("G22_BUSY_GERUND", "Gerunds", "Busy + Gerund Structure", "GERUND:ADJECTIVE:BUSY_ING", "busy preparing", listOf("busy to prepare", "busy prepare", "busy for preparing"), "The research team is _____ for the upcoming launch.", "تحقیقی ٹیم آنے والی پیشکش کی تیاری میں مصروف ہے۔"),
            Tuple4("G23_CANT_HELP_GERUND", "Gerunds", "Can't Help + Gerund", "GERUND:IDIOMATIC:CANT_HELP_ING", "cannot help admiring", listOf("cannot help to admire", "cannot help admire", "cannot help for admire"), "I _____ her resilience.", "میں ان کی ثابت قدمی کی تعریف کیے بغیر نہیں رہ سکتا۔"),
            Tuple4("G24_CANT_STAND_GERUND", "Gerunds", "Can't Stand + Gerund", "GERUND:IDIOMATIC:CANT_STAND_ING", "cannot stand waiting", listOf("cannot stand to wait", "cannot stand wait", "cannot stand for waiting"), "He _____ in long queues.", "وہ لمبی قطاروں میں انتظار کرنا برداشت نہیں کر سکتا۔"),
            Tuple4("G25_USED_TO_GERUND_STATE", "Gerunds", "Be Used To + Gerund State", "GERUND:SEMI_MODAL:BE_USED_TO_ING", "used to working", listOf("used to work", "use to working", "used working"), "She is _____ long shifts at the hospital.", "وہ ہسپتال میں لمبی شفٹیں کرنے کی عادی ہیں۔"),
            Tuple4("G26_PREFER_ING_TO_ING", "Gerunds", "Prefer Gerund To Gerund", "GERUND:PREFERENCE:PREFER_ING_TO_ING", "prefers reading to watching", listOf("prefers reading than watching", "prefers to read to watch", "prefers read to watch"), "He _____ television.", "وہ ٹیلی ویژن دیکھنے کے مقابلے میں پڑھنا پسند کرتے ہیں۔"),
            Tuple4("G27_PURPOSE_IN_ORDER_TO", "Infinitives", "In Order To Infinitive of Purpose", "INFINITIVE:PURPOSE:IN_ORDER_TO", "in order to meet", listOf("in order for meeting", "in order meeting", "in order to meeting"), "They streamlined the workflow _____ the deadline.", "انہوں نے ڈیڈ لائن پر پورا اترنے کے لیے ورک فلو کو ہموار کیا۔"),
            Tuple4("G28_NEG_PURPOSE_SO_AS_NOT_TO", "Infinitives", "So As Not To Negative Purpose", "INFINITIVE:PURPOSE:SO_AS_NOT_TO", "so as not to disturb", listOf("so as to not disturb", "so not to disturb", "so as to disturb not"), "He walked quietly _____ the sleeping child.", "وہ خاموشی سے چلا تاکہ سوئے ہوئے بچے کو خلل نہ پڑے۔"),
            Tuple4("G29_SPEND_TIME_GERUND", "Gerunds", "Spend Time Gerund", "GERUND:SPEND_TIME:ING", "spent three hours editing", listOf("spent three hours to edit", "spent three hours edit", "spent three hours for editing"), "She _____ the research paper.", "اس نے تحقیقی مقالے کی تدوین میں تین گھنٹے صرف کیے۔"),
            Tuple4("G30_HAVE_DIFFICULTY_GERUND", "Gerunds", "Have Difficulty Gerund", "GERUND:HAVE_DIFFICULTY:ING", "had difficulty solving", listOf("had difficulty to solve", "had difficulty solve", "had difficulty for solving"), "The students _____ the differential equation.", "طلباء کو تفریقی مساوات حل کرنے میں دشواری کا سامنا کرنا پڑا۔"),
            Tuple4("G31_PERCEPTION_PASSIVE_TO_INF", "Infinitives", "Perception Passive Was Seen To Infinitive", "INFINITIVE:PASSIVE_PERCEPTION:WAS_SEEN_TO", "was seen to enter", listOf("was seen enter", "was seen entering to", "saw to enter"), "The suspect _____ the abandoned building.", "مشتبہ شخص کو ویران عمارت میں داخل ہوتے دیکھا گیا تھا۔"),
            Tuple4("G32_HABITUAL_PAST_USED_TO", "Infinitives", "Used To Bare Infinitive", "INFINITIVE:PAST_HABIT:USED_TO_V1", "used to reside", listOf("was used to reside", "used to residing", "use to reside"), "They _____ in the coastal village.", "وہ ساحلی گاؤں میں رہا کرتے تھے۔"),
            Tuple4("G33_IT_IS_IMPORTANT_TO", "Infinitives", "Dummy It Is Adjective To Infinitive", "INFINITIVE:DUMMY_IT:IT_IS_ADJ_TO_V1", "It is vital to double-check", listOf("It is vital double-checking", "It is vital to double-checking", "Is vital to double-check"), "_____ all numerical calculations.", "تمام عددی حساب کتاب کی دوبارہ تصدیق کرنا انتہائی اہم ہے۔"),
            Tuple4("G34_SPLIT_INFINITIVE_ADVERB", "Infinitives", "Infinitive Adverb Placement", "INFINITIVE:ADVERB_PLACEMENT:TO_FULLY_V1", "to fully understand", listOf("to understand fully it", "fully to understand", "for fully understanding"), "In order _____ the quantum theory, one must study math.", "کوانٹم تھیوری کو مکمل طور پر سمجھنے کے لیے ریاضی پڑھنا ضروری ہے۔"),
            Tuple4("G35_GERUND_AFTER_FEEL_LIKE", "Gerunds", "Feel Like + Gerund", "GERUND:IDIOMATIC:FEEL_LIKE_ING", "feels like taking", listOf("feels like to take", "feels like take", "feels for taking"), "He _____ a brief stroll after lunch.", "دوپہر کے کھانے کے بعد اس کا دل کا نکتہ سیر کا کر رہا ہے۔"),
            Tuple4("G36_THERE_IS_NO_POINT_ING", "Gerunds", "There Is No Point In + Gerund", "GERUND:IDIOMATIC:THERE_IS_NO_POINT_ING", "There is no point in arguing", listOf("There is no point to argue", "There is no point argue", "There is no point for arguing"), "_____ about matters beyond our control.", "ان معاملات پر بحث کرنے کا کوئی فائدہ نہیں جو ہمارے کنٹرول سے باہر ہیں۔")
        )

        topics.forEachIndexed { idx, t ->
            val gNum = idx + 1
            list.add(QuizSlotHelper.createTemplate(
                templateId = t.v1,
                slotIndex = 7,
                grammarTopic = t.v2,
                subtopic = t.v3,
                difficulty = if (gNum > 22) "Expert" else "Intermediate",
                structuralFingerprint = t.v4,
                category = "PRESENT",
                tenseId = "gerunds_slot7_$gNum",
                tenseTitleEn = "${t.v2} • ${t.v3}",
                tenseTitleAr = "اسمِ مصدر و مفعول",
                explanationEn = "Verb pattern structure focusing on ${t.v3}.",
                explanationUr = "اس ساخت میں اسمِ مصدر (Gerund) اور اسمِ مفعول (Infinitive) کے قواعد بیان کیے گئے ہیں۔",
                commonMistake = "Gerund اور Infinitive کا غلط جگہ استعمال۔",
                variants = listOf(v(t.v7, t.v8, t.v5, t.v6))
            ))
        }

        list
    }

    private data class Tuple4<A, B, C, D, E, F, G, H>(
        val v1: A, val v2: B, val v3: C, val v4: D, val v5: E, val v6: F, val v7: G, val v8: H
    )
}
