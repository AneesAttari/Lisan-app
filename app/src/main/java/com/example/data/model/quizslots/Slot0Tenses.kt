package com.example.data.model.quizslots

import com.example.data.model.DailyQuiz365Generator.QuestionTemplate
import com.example.data.model.QuizSlotHelper
import com.example.data.model.QuizSlotHelper.SentenceVariant

object Slot0Tenses {
    val templates: List<QuestionTemplate> by lazy {
        val list = mutableListOf<QuestionTemplate>()

        // Helper shorthand
        fun v(text: String, ur: String, corr: String, dist: List<String>) = SentenceVariant(text, ur, corr, dist)

        list.add(QuizSlotHelper.createTemplate(
            templateId = "T01_PRES_SMP_HABIT",
            slotIndex = 0,
            grammarTopic = "Present Simple",
            subtopic = "Third-Person Singular Habitual Aspect",
            difficulty = "Beginner",
            structuralFingerprint = "TENSE:PRES_SMP:3RD_SING_HABIT:S_ES_SUFFIX",
            category = "PRESENT",
            tenseId = "present_simple",
            tenseTitleEn = "Present Simple • Habitual Action",
            tenseTitleAr = "فعل حال ساده",
            explanationEn = "With third-person singular subjects (He/She/It/Singular noun), the verb in Present Simple takes the '-s' or '-es' inflection.",
            explanationUr = "فعلِ حال سادہ میں واحد غائب فاعل کے ساتھ فعل کی پہلی فارم کے ساتھ '-s' یا '-es' کا اضافہ کیا جاتا ہے۔",
            commonMistake = "واحد غائب فاعل کے ساتھ بغیر 's' کے اصل فعل استعمال کرنا غلط ہے۔",
            variants = listOf(
                v("Dr. Bilal _____ clinical trials every Tuesday morning.", "ڈاکٹر بلال ہر منگل کی صبح کلینیکل ٹرائلز کرتے ہیں۔", "conducts", listOf("conduct", "conducting", "is conduct")),
                v("The chief editor _____ every manuscript before publication.", "چیف ایڈیٹر اشاعت سے پہلے ہر مسودے کا جائزہ لیتے ہیں۔", "reviews", listOf("review", "reviewing", "has review")),
                v("Professor Zainab _____ fresh lab specimens daily.", "پروفیسر زینب روزانہ تازہ لیب کے نمونوں کا تجزیہ کرتی ہیں۔", "analyzes", listOf("analyze", "analyzing", "is analyze"))
            )
        ))

        list.add(QuizSlotHelper.createTemplate(
            templateId = "T02_PRES_CONT_MOMENT",
            slotIndex = 0,
            grammarTopic = "Present Continuous",
            subtopic = "Action at Moment of Speech",
            difficulty = "Beginner",
            structuralFingerprint = "TENSE:PRES_CONT:MOMENT_OF_SPEECH:BE_PLUS_ING",
            category = "PRESENT",
            tenseId = "present_continuous",
            tenseTitleEn = "Present Continuous • Ongoing Action",
            tenseTitleAr = "فعل حال جاری",
            explanationEn = "Use 'am/is/are + verb-ing' for actions taking place right now at the moment of speech.",
            explanationUr = "موجودہ لمحے میں جاری کام کے لیے 'is/are/am + verb-ing' استعمال ہوتا ہے۔",
            commonMistake = "'right now' کے ساتھ فعل حال سادہ یا ماضی استعمال کرنا غلط ہے۔",
            variants = listOf(
                v("Listen carefully! The speaker _____ the main findings right now.", "سنیں! مقرر اس وقت اہم نتائج بیان کر رہا ہے۔", "is explaining", listOf("explains", "explained", "was explain")),
                v("Look! The server _____ the pending database requests currently.", "دیکھیں! سرور فی الحال زیر التواء ڈیٹا بیس کی درخواستوں پر کارروائی کر رہا ہے۔", "is processing", listOf("processes", "processed", "has process")),
                v("The engineering team _____ a new algorithm at this moment.", "انجینئرنگ ٹیم اس وقت نیا الگورتھم تیار کر رہی ہے۔", "is developing", listOf("develops", "developed", "was develop"))
            )
        ))

        list.add(QuizSlotHelper.createTemplate(
            templateId = "T03_PRES_PERF_EXPERIENCE",
            slotIndex = 0,
            grammarTopic = "Present Perfect",
            subtopic = "Life Experience without Definite Time",
            difficulty = "Intermediate",
            structuralFingerprint = "TENSE:PRES_PERF:LIFE_EXPERIENCE:HAVE_HAS_V3",
            category = "PRESENT",
            tenseId = "present_perfect",
            tenseTitleEn = "Present Perfect • Life Experience",
            tenseTitleAr = "فعل حال تمام",
            explanationEn = "Use 'has/have + past participle (V3)' for life experiences when no specific past time is stated.",
            explanationUr = "زندگی کا ایسا تجربہ جس کا وقت مقرر نہ ہو، اسے ظاہر کرنے کے لیے 'has/have + 3rd form' لگاتے ہیں۔",
            commonMistake = "زندگی کے غیر متعین تجربے کے ساتھ 'last year' جیسا ماضی کا وقت ملا دینا غلط ہے۔",
            variants = listOf(
                v("She is the most skilled surgeon I _____ in my career.", "وہ سب سے ماہر سرجن ہیں جن سے میں اپنے کیریئر میں ملا ہوں۔", "have ever met", listOf("ever met", "have ever meet", "had ever meet")),
                v("They _____ such a complex machine before today.", "انہوں نے آج سے پہلے اتنی پیچیدہ مشین نہیں دیکھی ہے۔", "have never seen", listOf("never saw", "has never saw", "are never seeing")),
                v("He _____ several research papers on renewable energy.", "وہ قابل تجدید توانائی پر متعدد تحقیقی مقالے شائع کر چکے ہیں۔", "has published", listOf("have published", "is publishing", "published had"))
            )
        ))

        list.add(QuizSlotHelper.createTemplate(
            templateId = "T04_PRES_PERF_CONT_DURATION",
            slotIndex = 0,
            grammarTopic = "Present Perfect Continuous",
            subtopic = "Duration from Past to Present",
            difficulty = "Intermediate",
            structuralFingerprint = "TENSE:PRES_PERF_CONT:DURATION_PAST_TO_PRES:HAS_HAVE_BEEN_ING",
            category = "PRESENT",
            tenseId = "present_perfect_continuous",
            tenseTitleEn = "Present Perfect Continuous • Ongoing Duration",
            tenseTitleAr = "فعل حال جاری تمام",
            explanationEn = "Use 'has/have been + verb-ing' to emphasize the continuous duration of an action starting in the past up to now.",
            explanationUr = "ماضی سے شروع ہو کر اب تک جاری رہنے والے کام کے دورانیے کو ظاہر کرنے کے لیے 'has/have been + verb-ing' آتا ہے۔",
            commonMistake = "'for two hours' کے ساتھ صرف 'is working' لگانا غلط ہے؛ 'has been working' لگانا ضروری ہے۔",
            variants = listOf(
                v("Dr. Tariq _____ on this vaccine project for over three years.", "ڈاکٹر طارق تین سال سے زیادہ عرصے سے اس ویکسین پروجیکٹ پر کام کر رہے ہیں۔", "has been working", listOf("is working", "was working", "has worked been")),
                v("They _____ the network infrastructure since 6 AM.", "وہ صبح 6 بجے سے نیٹ ورک کے انفراسٹرکچر کی مرمت کر رہے ہیں۔", "have been repairing", listOf("are repairing", "were repairing", "have repaired being")),
                v("The research group _____ historical manuscripts all morning.", "تحقیقی گروپ پوری صبح سے تاریخی مسودات کا ترجمہ کر رہا ہے۔", "has been translating", listOf("is translating", "translated has", "was translating"))
            )
        ))

        list.add(QuizSlotHelper.createTemplate(
            templateId = "T05_PAST_SMP_COMPLETED",
            slotIndex = 0,
            grammarTopic = "Past Simple",
            subtopic = "Completed Action at Specific Past Time",
            difficulty = "Beginner",
            structuralFingerprint = "TENSE:PAST_SMP:COMPLETED_SPECIFIC_TIME:V2_IRREGULAR",
            category = "PAST",
            tenseId = "past_simple",
            tenseTitleEn = "Past Simple • Completed Event",
            tenseTitleAr = "فعل ماضی سادہ",
            explanationEn = "Use the second form of the verb (V2) for actions completed at a specific time in the past.",
            explanationUr = "ماضی میں کسی مخصوص وقت پر مکمل ہو جانے والے کام کے لیے فعل کی دوسری فارم (V2) استعمال ہوتی ہے۔",
            commonMistake = "ماضی کے معلوم وقت (مثلاً yesterday) کے ساتھ 'has gone' یا 'is went' لکھنا غلط ہے۔",
            variants = listOf(
                v("Last month, the committee _____ the annual budget proposals.", "پچھلے مہینے کمیٹی نے سالانہ بجٹ کی تجاویز کو منظور کیا۔", "approved", listOf("approves", "has approved", "was approve")),
                v("In 2021, she _____ her master's degree in computer science.", "2021 میں، انہوں نے کمپیوٹر سائنس میں ماسٹرز کی ڈگری مکمل کی۔", "completed", listOf("completes", "has completed", "is complete")),
                v("The team _____ to Istanbul for the international summit last week.", "ٹیم پچھلے ہفتے بین الاقوامی سربراہی اجلاس کے لیے استنبول گئی۔", "went", listOf("goes", "has gone", "was go"))
            )
        ))

        list.add(QuizSlotHelper.createTemplate(
            templateId = "T06_PAST_CONT_INTERRUPTED",
            slotIndex = 0,
            grammarTopic = "Past Continuous",
            subtopic = "Ongoing Background Action Interrupted",
            difficulty = "Intermediate",
            structuralFingerprint = "TENSE:PAST_CONT:INTERRUPTED_BACKGROUND:WAS_WERE_ING",
            category = "PAST",
            tenseId = "past_continuous",
            tenseTitleEn = "Past Continuous • Interrupted Past Action",
            tenseTitleAr = "فعل ماضی جاری",
            explanationEn = "Use Past Continuous (was/were + verb-ing) for an action in progress when another shorter action interrupted it.",
            explanationUr = "ماضی میں جاری کام کے لیے 'was/were + verb-ing' استعمال ہوتا ہے جسے کسی دوسرے کام نے روکا ہو۔",
            commonMistake = "'while' والی جاری کلاز میں Past Simple (did) لگانا غلط ہے۔",
            variants = listOf(
                v("While the technician _____ the system, the power supply cut off.", "جب ٹیکنیشن سسٹم کی جانچ کر رہا تھا تو بجلی کی فراہمی منقطع ہو گئی۔", "was inspecting", listOf("inspects", "inspected", "is inspecting")),
                v("They _____ the project strategy when the manager arrived.", "جب مینیجر پہنچے تو وہ پروجیکٹ کی حکمت عملی پر بحث کر رہے تھے۔", "were discussing", listOf("discussed", "are discussing", "have discussed")),
                v("I _____ the research code when my laptop unexpectedly crashed.", "جب میرا لیپ ٹاپ غیر متوقع طور پر کریش ہوا تو میں تحقیقی کوڈ مرتب کر رہا تھا۔", "was compiling", listOf("compile", "compiled", "am compiling"))
            )
        ))

        list.add(QuizSlotHelper.createTemplate(
            templateId = "T07_PAST_PERF_SEQUENCE",
            slotIndex = 0,
            grammarTopic = "Past Perfect",
            subtopic = "Earlier of Two Past Actions",
            difficulty = "Intermediate",
            structuralFingerprint = "TENSE:PAST_PERF:EARLIER_PAST_ACTION:HAD_V3",
            category = "PAST",
            tenseId = "past_perfect",
            tenseTitleEn = "Past Perfect • Action Prior to Past",
            tenseTitleAr = "فعل ماضی بعید",
            explanationEn = "Use Past Perfect (had + V3) to indicate an action that happened before another past event.",
            explanationUr = "ماضی کے دو کاموں میں سے پہلے مکمل ہونے والے کام کے لیے 'had + 3rd form' آتا ہے۔",
            commonMistake = "ماضی کے دونوں واقعات کے لیے صرف Past Simple لگانا جب ان کی ترتیب ظاہر کرنا ضروری ہو۔",
            variants = listOf(
                v("The plane _____ before we reached the gate.", "ہمارے گیٹ پر پہنچنے سے پہلے جہاز پرواز کر چکا تھا۔", "had taken off", listOf("took off", "has taken off", "was taking off")),
                v("She _____ all data files prior to the power outage.", "بجلی کی بندش سے پہلے وہ تمام ڈیٹا فائلیں محفوظ کر چکی تھیں۔", "had saved", listOf("saved", "has saved", "is saving")),
                v("The delegates _____ the contract before the conference ended.", "کانفرنس ختم ہونے سے پہلے مندوبین معاہدے پر دستخط کر چکے تھے۔", "had signed", listOf("signed", "have signed", "were signing"))
            )
        ))

        list.add(QuizSlotHelper.createTemplate(
            templateId = "T08_FUT_SMP_PREDICTION",
            slotIndex = 0,
            grammarTopic = "Future Simple",
            subtopic = "General Prediction with Will",
            difficulty = "Beginner",
            structuralFingerprint = "TENSE:FUT_SMP:GENERAL_PREDICTION:WILL_V1",
            category = "FUTURE",
            tenseId = "future_simple",
            tenseTitleEn = "Future Simple • General Prediction",
            tenseTitleAr = "فعل مستقبل ساده",
            explanationEn = "Use 'will + base verb (V1)' to express general predictions or spontaneous decisions about the future.",
            explanationUr = "مستقبل کی عام پیشگوئی یا فوری فیصلے کے لیے 'will + verb(V1)' استعمال ہوتا ہے۔",
            commonMistake = "'will' کے بعد فعل کی دوسری يا تیسری فارم کا استعمال بالکل غلط ہے۔",
            variants = listOf(
                v("Analysts predict that solar technology _____ dramatically cheaper next year.", "تجزیہ کاروں کا کہنا ہے کہ اگلے سال شمسی ٹیکنالوجی کافی سستی ہو جائے گی۔", "will become", listOf("became", "will becoming", "becomes will")),
                v("Our company _____ a new mobile software next month.", "ہماری کمپنی اگلے مہینے ایک نیا موبائل سافٹ ویئر پیش کرے گی۔", "will launch", listOf("launched", "will launching", "launches will")),
                v("I believe the committee _____ your research application favorably.", "میرا ماننا ہے کہ کمیٹی آپ کی تحقیقی درخواست کو پسند کرے گی۔", "will evaluate", listOf("evaluated", "will evaluating", "evaluates will"))
            )
        ))

        list.add(QuizSlotHelper.createTemplate(
            templateId = "T09_FUT_CONT_SCHEDULED",
            slotIndex = 0,
            grammarTopic = "Future Continuous",
            subtopic = "Action in Progress at Specific Future Time",
            difficulty = "Intermediate",
            structuralFingerprint = "TENSE:FUT_CONT:PROGRESS_AT_FUTURE_POINT:WILL_BE_ING",
            category = "FUTURE",
            tenseId = "future_continuous",
            tenseTitleEn = "Future Continuous • Action in Progress",
            tenseTitleAr = "فعل مستقبل جاری",
            explanationEn = "Use 'will be + verb-ing' for an action that will be in progress at a specific moment in the future.",
            explanationUr = "مستقبل میں کسی مخصوص وقت پر جاری رہنے والے کام کے لیے 'will be + verb-ing' آتا ہے۔",
            commonMistake = "'this time tomorrow' کے ساتھ 'will be' چھوڑ کر صرف 'will attend' کہنا غلط ہے۔",
            variants = listOf(
                v("This time tomorrow, we _____ across the Mediterranean Sea.", "کل اس وقت ہم بحیرہ روم کے پار پرواز کر رہے ہوں گے۔", "will be flying", listOf("will fly", "fly", "are flying")),
                v("At 10 AM on Monday, the team _____ the new prototype.", "پیر کے روز صبح 10 بجے ٹیم نئے پروٹو ٹائپ کی جانچ کر رہی ہوگی۔", "will be testing", listOf("will test", "test", "are testing")),
                v("During the evening session, Dr. Saad _____ the workshop.", "شام کے سیشن میں، ڈاکٹر سعد ورکشاپ کی صدارت کر رہے ہوں گے۔", "will be presiding", listOf("will preside", "presides", "is presiding"))
            )
        ))

        list.add(QuizSlotHelper.createTemplate(
            templateId = "T10_FUT_PERF_DEADLINE",
            slotIndex = 0,
            grammarTopic = "Future Perfect",
            subtopic = "Action Completed before Future Time (By the time)",
            difficulty = "Expert",
            structuralFingerprint = "TENSE:FUT_PERF:COMPLETED_BEFORE_DEADLINE:WILL_HAVE_V3",
            category = "FUTURE",
            tenseId = "future_perfect",
            tenseTitleEn = "Future Perfect • Future Completion",
            tenseTitleAr = "فعل مستقبل تمام",
            explanationEn = "Use 'will have + past participle (V3)' for an action that will be completed before a specified point in the future.",
            explanationUr = "مستقبل کی کسی مقررہ تاریخ یا وقت سے پہلے مکمل ہو جانے والے کام کے لیے 'will have + 3rd form' آتا ہے۔",
            commonMistake = "'by next year' کے ساتھ 'will finish' لکھنا؛ جب تک کہ کام کی تکمیلیت ظاہر نہ کی جائے 'will have finished' درست ہے۔",
            variants = listOf(
                v("By the time the audit begins, we _____ all financial records.", "آڈٹ شروع ہونے تک، ہم تمام مالیاتی ریکارڈز کی تصدیق کر چکے ہوں گے۔", "will have verified", listOf("will verify", "verifying", "have verified")),
                v("By December, the construction firm _____ the highway project.", "دسمبر تک، تعمیراتی فرم ہائی وے پروجیکٹ مکمل کر چکی ہوگی۔", "will have completed", listOf("will complete", "completes", "had completed")),
                v("Before the director arrives, she _____ the annual presentation.", "ڈائریکٹر کی آمد سے پہلے، وہ سالانہ پریزنٹیشن تیار کر چکی ہوگی۔", "will have prepared", listOf("will prepare", "prepares", "is preparing"))
            )
        ))

        // T11..T36 (Adding remaining 26 distinct templates to reach 36 in Slot 0)
        val extraTopics = listOf(
            Tuple4("T11_STATIVE_VERB_NONCONT", "Stative Verbs", "Non-Continuous Rule", "TENSE:STATIVE_NON_CONT:KNOW_BELIEVE", "knows", listOf("is knowing", "knowing", "has been knowing"), "The professor _____ the answer.", "پروفیسر کو جواب معلوم ہے۔"),
            Tuple4("T12_USED_TO_PAST_HABIT", "Habitual Past", "Used To Rule", "TENSE:USED_TO_PAST_HABIT:BASE_VERB", "used to live", listOf("was used to live", "is used to living", "use to live"), "They _____ in Lahore.", "وہ لاہور میں رہا کرتے تھے۔"),
            Tuple4("T13_PRES_SMP_TIMETABLE", "Present Simple Future", "Timetable Rule", "TENSE:PRES_SMP_TIMETABLE:SCHEDULED_ACTION", "departs", listOf("is departing", "will depart has", "departed"), "The train _____ at 8:00 AM.", "ٹرین صبح 8 بجے روانہ ہوتی ہے۔"),
            Tuple4("T14_PRES_CONT_ARRANGEMENT", "Present Continuous Future", "Fixed Arrangement", "TENSE:PRES_CONT_ARRANGEMENT:FIXED_FUTURE", "is hosting", listOf("hosts", "hosted", "will hosted"), "She _____ a dinner tomorrow.", "وہ کل رات کے کھانے کی میزبانی کر رہی ہیں۔"),
            Tuple4("T15_PAST_SMP_DEFINITE_MARKER", "Past Simple Time Marker", "Definite Year Rule", "TENSE:PAST_SMP_DEFINITE_MARKER:YEAR_MARKER", "built", listOf("has built", "was building", "builds"), "The empire _____ this fort in 1640.", "سلطنت نے 1640 میں یہ قلعہ تعمیر کیا۔"),
            Tuple4("T16_PRES_PERF_UNFINISHED_TIME", "Present Perfect Unfinished", "This Week Rule", "TENSE:PRES_PERF_UNFINISHED:THIS_WEEK", "have received", listOf("received", "were receiving", "had receive"), "We _____ three calls this week.", "ہمیں اس ہفتے تین کالیں موصول ہوئی ہیں۔"),
            Tuple4("T17_PRES_PERF_VS_PAST_SMP", "Present Perfect Result", "Result Focus Rule", "TENSE:PRES_PERF_RESULT_FOCUS:HAS_V3", "has lost", listOf("lost yesterday", "was losing", "is lost"), "He _____ his passport and cannot travel.", "ان کا پاسپورٹ گم ہو گیا ہے اور وہ سفر نہیں کر سکتے۔"),
            Tuple4("T18_BE_GOING_TO_INTENT", "Future Intent", "Be Going To Rule", "TENSE:BE_GOING_TO_INTENT:PLANNED_ACTION", "is going to launch", listOf("launches", "will launching", "was launched"), "The studio _____ a new film.", "اسٹوڈیو ایک نئی فلم شروع کرنے جا رہا ہے۔"),
            Tuple4("T19_EMPHATIC_PRES_SMP", "Emphatic Present Simple", "Do/Does Emphasis", "TENSE:EMPHATIC_PRES_SMP:DO_DOES_V1", "does agree", listOf("is agree", "agreeing", "has agree"), "She _____ with the revised policy.", "وہ ترمیم شدہ پالیسی سے واقعی اتفاق کرتی ہیں۔"),
            Tuple4("T20_EMPHATIC_PAST_SMP", "Emphatic Past Simple", "Did Emphasis", "TENSE:EMPHATIC_PAST_SMP:DID_V1", "did submit", listOf("did submitted", "was submitted", "has submitted"), "He _____ the report on time.", "انہوں نے وقت پر رپورٹ لازماً جمع کرائی۔"),
            Tuple4("T21_PRES_PERF_JUST_ALREADY", "Present Perfect Adverb", "Just/Already Rule", "TENSE:PRES_PERF_JUST_ALREADY:ADVERB_PLACEMENT", "has just arrived", listOf("just arrived has", "is just arrive", "was just arriving"), "The manager _____ at the office.", "مینیجر ابھی دفتر پہنچے ہیں۔"),
            Tuple4("T22_PAST_CONT_PARALLEL", "Past Continuous Parallel", "While Parallel Rule", "TENSE:PAST_CONT_PARALLEL:WHILE_PARALLEL", "were reading", listOf("readed", "have read", "are reading"), "While I was writing, they _____.", "جب میں لکھ رہا تھا، وہ پڑھ رہے تھے۔"),
            Tuple4("T23_FUT_CONT_POLITE_INQUIRY", "Future Continuous Inquiry", "Polite Inquiry Rule", "TENSE:FUT_CONT_POLITE:WILL_YOU_BE_USING", "will you be using", listOf("do you use", "will you use be", "are you used"), "_____ the printer this afternoon?", "کیا آپ آج دوپہر پرنٹر استعمال کر رہے ہوں گے؟"),
            Tuple4("T24_PAST_PERF_UNFULFILLED", "Past Perfect Unfulfilled", "Unfulfilled Hope Rule", "TENSE:PAST_PERF_UNFULFILLED:HAD_HOPED", "had hoped", listOf("has hoped", "was hoping had", "hopes"), "We _____ to meet the director.", "ہم ڈائریکٹر سے ملنے کی امید رکھتے تھے۔"),
            Tuple4("T25_NARRATIVE_PAST_SEQUENCE", "Narrative Past", "Sequence of Past Simple", "TENSE:NARRATIVE_PAST_SEQUENCE:V2_CHAIN", "opened", listOf("had opened", "was opening", "has opened"), "He stepped in and _____ the letter.", "وہ اندر داخل ہوئے اور خط کھولا۔"),
            Tuple4("T26_PRES_SMP_ZERO_COND", "Zero Conditional Aspect", "General Fact Rule", "TENSE:PRES_SMP_ZERO_COND:FACT_ASPECT", "boils", listOf("will boil", "is boiling", "boiled"), "If you heat water to 100°C, it _____.", "اگر آپ پانی کو 100 ڈگری تک گرم کریں تو وہ ابلتا ہے۔"),
            Tuple4("T27_FUTURE_IN_PAST", "Future in the Past", "Was Going To Rule", "TENSE:FUTURE_IN_PAST:WAS_GOING_TO", "was going to call", listOf("will call", "is calling", "has called"), "I _____ you, but my battery died.", "میں آپ کو کال کرنے والا تھا لیکن میری بیٹی ختم ہو گئی۔"),
            Tuple4("T28_IMPERATIVE_FORM", "Imperative Form", "Bare Infinitive Command", "TENSE:IMPERATIVE_FORM:BARE_INFINITIVE", "Submit", listOf("Submitting", "Submits", "To submit"), "_____ your application before noon.", "دوپہر سے پہلے اپنی درخواست جمع کروائیں۔"),
            Tuple4("T29_SUBJUNCTIVE_PRESENT", "Subjunctive Present", "Demand Bare Infinitive", "TENSE:SUBJUNCTIVE_PRESENT:DEMAND_BARE_V1", "be", listOf("is", "was", "being"), "They demanded that he _____ present.", "انہوں نے مطالبہ کیا کہ وہ حاضر ہوں۔"),
            Tuple4("T30_SUBJUNCTIVE_PAST_WISH", "Subjunctive Past Wish", "Were Wish Rule", "TENSE:SUBJUNCTIVE_PAST_WISH:WERE_RULE", "were", listOf("was", "is", "be"), "I wish I _____ more experienced.", "کاش میں زیادہ تجربہ کار ہوتا۔"),
            Tuple4("T31_PERFECT_INFINITIVE", "Perfect Infinitive Aspect", "To Have Done Rule", "TENSE:PERFECT_INFINITIVE:TO_HAVE_V3", "to have finished", listOf("to finish have", "finishing to", "to be finished"), "He claims _____ the project.", "وہ دعویٰ کرتے ہیں کہ انہوں نے پروجیکٹ مکمل کر لیا ہے۔"),
            Tuple4("T32_PRES_PARTICIPLE_DURATION", "Present Participle Modifier", "Duration Clause", "TENSE:PRES_PARTICIPLE_DURATION:ING_CLAUSE", "Having", listOf("Have", "Had", "To have"), "_____ lived there, she knew the roads.", "وہاں رہنے کی وجہ سے، وہ سڑکوں کو جانتی تھیں۔"),
            Tuple4("T33_PAST_PARTICIPLE_STATE", "Past Participle State", "Result Adjective Rule", "TENSE:PAST_PARTICIPLE_STATE:RESULT_ADJ", "broken", listOf("breaking", "break", "broke"), "The window remained _____ all day.", "کھڑکی پورا دن ٹوٹی رہی۔"),
            Tuple4("T34_FUT_PERF_CONT_DURATION", "Future Perfect Continuous", "Duration by Future Date", "TENSE:FUT_PERF_CONT_DURATION:WILL_HAVE_BEEN_ING", "will have been teaching", listOf("will teach", "is teaching", "has been teaching"), "By 2030, she _____ for 20 years.", "2030 تک، وہ 20 سال سے پڑھا رہی ہوں گی۔"),
            Tuple4("T35_PRES_SMP_POLYSEMY", "Stative vs Action Polysemy", "Think State Rule", "TENSE:PRES_SMP_POLYSEMY:THINK_STATE", "think", listOf("am thinking", "thought have", "was thinking"), "I _____ this option is optimal.", "میرا خیال ہے کہ یہ آپشن بہترین ہے۔"),
            Tuple4("T36_PAST_CONT_IRRITATION", "Past Continuous Irritation", "Always Annoyance Rule", "TENSE:PAST_CONT_IRRITATION:ALWAYS_ANNOYANCE", "was always losing", listOf("lost always", "has always lost", "is always losing"), "He _____ his keys back then.", "وہ اس وقت ہمیشہ اپنی چابیاں گم کرتا رہتا تھا۔")
        )

        extraTopics.forEachIndexed { idx, t ->
            val tNum = 11 + idx
            list.add(QuizSlotHelper.createTemplate(
                templateId = t.v1,
                slotIndex = 0,
                grammarTopic = t.v2,
                subtopic = t.v3,
                difficulty = if (tNum > 25) "Expert" else "Intermediate",
                structuralFingerprint = t.v4,
                category = "PRESENT",
                tenseId = "tense_slot0_$tNum",
                tenseTitleEn = "${t.v2} • ${t.v3}",
                tenseTitleAr = "فعل و گرامر",
                explanationEn = "Grammatical structure focusing on ${t.v3}.",
                explanationUr = "اس ساخت میں ${t.v3} کا درست استعمال مطلوب ہے۔",
                commonMistake = "غلط فارم کا انتخاب کرنا ایک عام غلطی ہے۔",
                variants = listOf(v(t.v7, t.v8, t.v5, t.v6))
            ))
        }

        list
    }

    private data class Tuple4<A, B, C, D, E, F, G, H>(
        val v1: A, val v2: B, val v3: C, val v4: D, val v5: E, val v6: F, val v7: G, val v8: H
    )
}
