package com.example.data.model.quizslots

import com.example.data.model.DailyQuiz365Generator.QuestionTemplate
import com.example.data.model.QuizSlotHelper
import com.example.data.model.QuizSlotHelper.SentenceVariant

object Slot3Conditionals {
    val templates: List<QuestionTemplate> by lazy {
        val list = mutableListOf<QuestionTemplate>()
        fun v(text: String, ur: String, corr: String, dist: List<String>) = SentenceVariant(text, ur, corr, dist)

        val topics = listOf(
            Tuple4("C01_ZERO_CONDITIONAL", "Conditionals", "Zero Conditional", "COND:ZERO:PRES_PRES", "melts", listOf("will melt", "melted", "is melting"), "If ice is exposed to heat, it _____.", "اگر برف کو گرمی دی جائے تو وہ پگھل جاتی ہے۔"),
            Tuple4("C02_FIRST_CONDITIONAL", "Conditionals", "First Conditional", "COND:FIRST:PRES_WILL", "will pass", listOf("passed", "would pass", "passes will"), "If she studies hard, she _____ the bar exam.", "اگر وہ محنت سے پڑھے گی تو بار کا امتحان پاس کر لے گی۔"),
            Tuple4("C03_SECOND_CONDITIONAL", "Conditionals", "Second Conditional", "COND:SECOND:PAST_WOULD", "would invest", listOf("will invest", "invested would", "invests"), "If I won the grant, I _____ in clean energy.", "اگر میں گرانٹ جیت جاتا، تو میں صاف توانائی میں سرمایہ کاری کرتا۔"),
            Tuple4("C04_THIRD_CONDITIONAL", "Conditionals", "Third Conditional", "COND:THIRD:PASTPERF_WOULDHAVE", "would have saved", listOf("would save", "will have saved", "saved would"), "If we had left earlier, we _____ time.", "اگر ہم جلدی نکلتے تو ہمارا وقت بچ جاتا۔"),
            Tuple4("C05_MIXED_COND_PAST_PRES", "Conditionals", "Mixed Past Cause Present Result", "COND:MIXED:PASTPERF_WOULD", "would be", listOf("would have been", "will be", "is"), "If he had taken the medicine, he _____ healthy today.", "اگر وہ دوا لے لیتا تو وہ آج صحت مند ہوتا۔"),
            Tuple4("C06_MIXED_COND_PRES_PAST", "Conditionals", "Mixed Present State Past Result", "COND:MIXED:PAST_WOULDHAVE", "would have accepted", listOf("would accept", "accepted would", "will accept"), "If I were braver, I _____ the offer last year.", "اگر میں زیادہ بہادر ہوتا تو پچھلے سال یہ پیشکش قبول کر لیتا۔"),
            Tuple4("C07_INVERSION_FIRST_COND", "Inversion Conditional", "Should First Conditional", "COND:INVERSION_FIRST:SHOULD_SUBJ", "Should you require", listOf("If you should required", "Do you require", "Were you require"), "_____ assistance, please contact support.", "اگر آپ کو مدد کی ضرورت ہو تو سپورٹ سے رابطہ کریں۔"),
            Tuple4("C08_INVERSION_SECOND_COND", "Inversion Conditional", "Were Second Conditional", "COND:INVERSION_SECOND:WERE_SUBJ", "Were I in", listOf("Was I in", "If I were in had", "Am I in"), "_____ your position, I would reconsider.", "اگر میں آپ کی جگہ ہوتا تو میں دوبارہ غور کرتا۔"),
            Tuple4("C09_INVERSION_THIRD_COND", "Inversion Conditional", "Had Third Conditional", "COND:INVERSION_THIRD:HAD_SUBJ_V3", "Had we known", listOf("If we had knew", "Have we known", "Did we know"), "_____ about the crisis, we would have helped.", "اگر ہمیں بحران کے بارے میں معلوم ہوتا تو ہم مدد کرتے۔"),
            Tuple4("C10_WISH_PRESENT_REGRET", "Wish Structure", "Wish + Past Simple", "COND:WISH:PAST_SIMPLE", "lived", listOf("lives", "has lived", "would live"), "I wish my family _____ closer to the city.", "کاش میرا خاندان شہر کے قریب رہتا۔"),
            Tuple4("C11_WISH_PAST_REGRET", "Wish Structure", "Wish + Past Perfect", "COND:WISH:PAST_PERFECT", "had studied", listOf("studied", "have studied", "would study"), "She wishes she _____ medicine instead of art.", "کاش انہوں نے آرٹ کے بجائے طب کی تعلیم حاصل کی ہوتی۔"),
            Tuple4("C12_WISH_WOULD_ANNOYANCE", "Wish Structure", "Wish + Would V1", "COND:WISH:WOULD_V1", "would turn down", listOf("turns down", "will turn down", "turned down"), "I wish he _____ the volume.", "کاش وہ آواز دھیمی کر دے۔"),
            Tuple4("C13_IF_ONLY_STRUCTURE", "Wish Structure", "If Only Realization", "COND:IF_ONLY:PAST_PERFECT", "If only we had", listOf("Only if we had", "If only we have", "If we only have"), "_____ acted sooner!", "کاش ہم نے پہلے اقدام کیا ہوتا!"),
            Tuple4("C14_AS_IF_SUBJUNCTIVE", "Subjunctive", "As If + Past Were", "COND:SUBJUNCTIVE:AS_IF_WERE", "as if he were", listOf("as if he is", "as though he was being", "like he is"), "He speaks _____ the company founder.", "وہ اس طرح بات کرتا ہے جیسے کمپنی کا بانی ہو۔"),
            Tuple4("C15_ITS_HIGH_TIME", "Subjunctive", "It's High Time + Past", "COND:SUBJUNCTIVE:HIGH_TIME_PAST", "It is high time we", listOf("It is high time we have", "It is high time we will", "High time is we"), "_____ renewed our contracts.", "اب وقت آ گیا ہے کہ ہم اپنے معاہدوں کی تجدید کریں۔"),
            Tuple4("C16_SUBJUNCTIVE_MANDATE", "Subjunctive", "Demands That Subject Bare V1", "COND:SUBJUNCTIVE:MANDATE_BARE_V1", "be appointed", listOf("is appointed", "was appointed", "being appointed"), "It is essential that a new leader _____.", "یہ ضروری ہے کہ ایک نئے قائد کا تقرر کیا جائے۔"),
            Tuple4("C17_PROVIDED_THAT", "Condition", "Provided That Rule", "COND:CONNECTOR:PROVIDED_THAT", "provided that you", listOf("provided you will", "providing you to", "provided that you had"), "You can join _____ pay the fee.", "آپ اس شرط پر شامل ہو سکتے ہیں کہ فیس ادا کریں۔"),
            Tuple4("C18_AS_LONG_AS", "Condition", "As Long As Rule", "COND:CONNECTOR:AS_LONG_AS", "as long as he", listOf("long as he will", "as long he", "so long he had"), "We will support him _____ remains honest.", "جب تک وہ ایماندار رہے گا ہم اس کی حمایت کریں گے۔"),
            Tuple4("C19_UNLESS_NEGATIVE_COND", "Condition", "Unless Rule", "COND:CONNECTOR:UNLESS_PRES", "unless you practice", listOf("unless you don't practice", "unless you won't practice", "unless practicing"), "You will not improve _____ daily.", "جب تک آپ روزانہ مشق نہیں کرتے آپ میں بہتری نہیں آئے گی۔"),
            Tuple4("C20_IN_CASE_PRECAUTION", "Condition", "In Case Precaution", "COND:CONNECTOR:IN_CASE_PRES", "in case it rains", listOf("in case it will rain", "in case of rain rain", "in case raining"), "Take an umbrella _____.", "چھتری ساتھ لے لیں مبادا بارش ہو جائے۔"),
            Tuple4("C21_OTHERWISE_OR_ELSE", "Condition", "Otherwise Result", "COND:CONNECTOR:OTHERWISE_WOULD", "otherwise you will", listOf("otherwise you would had", "or else you are", "otherwise you have"), "Hurry up, _____ miss the flight.", "جلدی کرو ورنہ پرواز چھوٹ جائے گی۔"),
            Tuple4("C22_BUT_FOR_NOUN", "Condition", "But For + Noun", "COND:CONNECTOR:BUT_FOR_NOUN", "But for your advice", listOf("But your advice", "For but your advice", "But for you advise"), "_____, I would have failed.", "آپ کے مشورے کے بغیر میں ناکام ہو جاتا۔"),
            Tuple4("C23_SUPPOSING_THAT", "Condition", "Supposing Hypothetical", "COND:CONNECTOR:SUPPOSING_PAST", "Supposing we lost", listOf("Supposing we lose will", "Suppose we are losing", "Supposing we have lost"), "_____, what would we do?", "فرض کریں کہ ہم ہار جائیں، ہم کیا کریں گے؟"),
            Tuple4("C24_ON_CONDITION_THAT", "Condition", "On Condition That", "COND:CONNECTOR:ON_CONDITION_THAT", "on condition that she", listOf("on condition she will", "on condition to", "on condition that she had"), "She agreed _____ manages the budget.", "وہ اس شرط پر راضی ہوئی کہ وہ بجٹ سنبھالے گی۔"),
            Tuple4("C25_FIRST_COND_IMPERATIVE", "Condition", "If Present Imperative", "COND:FIRST_IMPERATIVE:IF_PRES_IMP", "call the security", listOf("you will call security", "called security", "calling security"), "If you spot suspicious activity, _____.", "اگر آپ کو کوئی مشکوک سرگرمی نظر آئے تو سیکیورٹی کو کال کریں۔"),
            Tuple4("C26_SECOND_COND_WERE_TO", "Condition", "If Were To V1", "COND:SECOND_WERE_TO:HYPOTHETICAL", "were to resign", listOf("was to resign", "resigns were", "is to resign"), "If the minister _____, it would shock the nation.", "اگر وزیر مستعفی ہو جائیں تو قوم کو صدمہ پہنچے گا۔"),
            Tuple4("C27_POLITE_COND_WOULD", "Condition", "If You Would Be So Kind", "COND:POLITE:IF_YOU_WOULD", "If you would wait", listOf("If you will wait", "If you waited would", "If you had waited"), "_____, the manager will see you shortly.", "اگر آپ تھوڑا انتظار کریں تو مینیجر جلد آپ سے ملیں گے۔"),
            Tuple4("C28_EXCEPT_IF_EXCEPTION", "Condition", "Except If Rule", "COND:CONNECTOR:EXCEPT_IF", "except if required", listOf("except if requiring", "except if require", "except when requiring"), "Do not press the button _____ by staff.", "بٹن نہ دبائیں سوائے اس کے کہ عملہ کہے۔"),
            Tuple4("C29_WISH_COULD_ABILITY", "Wish Structure", "Wish Could Ability", "COND:WISH:COULD_ABILITY", "wishes she could travel", listOf("wishes she can travel", "wishes she would travel", "wishes traveling"), "She _____ around the world.", "کاش وہ دنیا بھر میں سفر کر سکتی۔"),
            Tuple4("C30_SUBJUNCTIVE_SUGGEST", "Subjunctive", "Suggest That Bare V1", "COND:SUBJUNCTIVE:SUGGEST_BARE_V1", "suggested that he take", listOf("suggested that he takes", "suggested that he took", "suggested that he taking"), "The doctor _____ a rest.", "ڈاکٹر نے مشورہ دیا کہ وہ آرام کرے۔"),
            Tuple4("C31_SUBJUNCTIVE_WOULD_RATHER", "Subjunctive", "Would Rather You Didn't", "COND:SUBJUNCTIVE:WOULD_RATHER_PAST", "would rather you didn't smoke", listOf("would rather you don't smoke", "would rather you not smoke", "would rather you didn't smoked"), "I _____ here.", "میں ترجیح دوں گا کہ آپ یہاں سگریٹ نہ پیئیں۔"),
            Tuple4("C32_SECOND_COND_MIGHT", "Condition", "If Past Might V1", "COND:SECOND_MIGHT:PAST_MIGHT", "might succeed", listOf("might succeeded", "will succeed", "succeeded might"), "If he attempted the puzzle, he _____.", "اگر وہ اس پہیلی کو حل کرنے کی کوشش کرتا تو شاید کامیاب ہو جاتا۔"),
            Tuple4("C33_THIRD_COND_COULD_HAVE", "Condition", "If Past Perfect Could Have V3", "COND:THIRD_COULD_HAVE:PASTPERF_COULD", "could have escaped", listOf("could escape", "can have escaped", "escaped could"), "If the door were unlocked, he _____.", "اگر دروازہ کھلا ہوتا تو وہ فرار ہو سکتا تھا۔"),
            Tuple4("C34_INVERTED_NEG_THIRD", "Inverted Condition", "Had It Not Been For", "COND:INVERTED_NEG:HAD_IT_NOT_BEEN", "Had it not been for", listOf("If it had not been", "Had not it been for", "Was it not for"), "_____ your support, we would be lost.", "اگر آپ کی مدد نہ ہوتی تو ہم بھٹک جاتے۔"),
            Tuple4("C35_WHETHER_OR_NOT", "Condition", "Whether Or Not", "COND:CONNECTOR:WHETHER_OR_NOT", "whether it rains or not", listOf("whether if it rains", "whether rain or no", "if or not it rains"), "We will hold the event _____.", "خواہ بارش ہو یا نہ ہو، ہم تقریب منعقد کریں گے۔"),
            Tuple4("C36_CONDITIONAL_REDUCTION", "Condition", "If Necessary Reduced", "COND:REDUCED:IF_NECESSARY", "If necessary", listOf("If it necessary", "If is necessary", "If necessitated"), "_____, adjust the parameters.", "اگر ضروری ہو تو پیرامیٹرز کو خود سے ایڈجسٹ کریں۔")
        )

        topics.forEachIndexed { idx, t ->
            val cNum = idx + 1
            list.add(QuizSlotHelper.createTemplate(
                templateId = t.v1,
                slotIndex = 3,
                grammarTopic = t.v2,
                subtopic = t.v3,
                difficulty = if (cNum > 25) "Expert" else "Intermediate",
                structuralFingerprint = t.v4,
                category = "FUTURE",
                tenseId = "conditional_slot3_$cNum",
                tenseTitleEn = "${t.v2} • ${t.v3}",
                tenseTitleAr = "شرطیہ جملے",
                explanationEn = "Conditional structure focusing on ${t.v3}.",
                explanationUr = "اس شرطیہ جملے میں ${t.v3} کے اصول دیے گئے ہیں۔",
                commonMistake = "شرط اور جوابِ شرط کے افعال کی غلط مطابقت۔",
                variants = listOf(v(t.v7, t.v8, t.v5, t.v6))
            ))
        }

        list
    }

    private data class Tuple4<A, B, C, D, E, F, G, H>(
        val v1: A, val v2: B, val v3: C, val v4: D, val v5: E, val v6: F, val v7: G, val v8: H
    )
}
