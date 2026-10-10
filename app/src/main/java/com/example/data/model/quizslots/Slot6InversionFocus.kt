package com.example.data.model.quizslots

import com.example.data.model.DailyQuiz365Generator.QuestionTemplate
import com.example.data.model.QuizSlotHelper
import com.example.data.model.QuizSlotHelper.SentenceVariant

object Slot6InversionFocus {
    val templates: List<QuestionTemplate> by lazy {
        val list = mutableListOf<QuestionTemplate>()
        fun v(text: String, ur: String, corr: String, dist: List<String>) = SentenceVariant(text, ur, corr, dist)

        val topics = listOf(
            Tuple4("I01_HARDLY_HAD_WHEN", "Inversion", "Hardly Had... When Structure", "INV:HARDLY_HAD:WHEN", "Hardly had the meeting started when", listOf("Hardly the meeting had started when", "Hardly had the meeting started than", "Hardly did the meeting start when"), "_____ the fire alarm sounded.", "ابھی میٹنگ شروع ہی ہوئی تھی کہ فائر الارم بج اٹھا۔"),
            Tuple4("I02_NO_SOONER_THAN", "Inversion", "No Sooner Had... Than Structure", "INV:NO_SOONER_HAD:THAN", "No sooner had the plane landed than", listOf("No sooner the plane landed when", "No sooner had the plane landed when", "No sooner did the plane landed than"), "_____ passengers unbuckled their seatbelts.", "جونہی جہاز اترے ہی، مسافروں نے اپنی سیٹ بیلٹ کھول لیں۔"),
            Tuple4("I03_SCARCELY_HAD_WHEN", "Inversion", "Scarcely Had... When Structure", "INV:SCARCELY_HAD:WHEN", "Scarcely had we arrived when", listOf("Scarcely we had arrived when", "Scarcely had we arrived than", "Scarcely did we arrived when"), "_____ the downpour began.", "ہم مشکل سے ہی پہنچے تھے کہ مصلادھار بارش شروع ہو گئی۔"),
            Tuple4("I04_NEVER_HAVE_I_SEEN", "Inversion", "Never Have I Seen Structure", "INV:NEVER_HAVE_I:V3", "Never have I witnessed", listOf("Never I have witnessed", "Never I witnessed have", "Never did I witnessed"), "_____ such dedication in a research team.", "میں نے کسی تحقیقی ٹیم میں اتنی لگن کبھی نہیں دیکھی۔"),
            Tuple4("I05_SELDOM_DO_WE_FIND", "Inversion", "Seldom Do We Find Present Simple", "INV:SELDOM_DO_WE:V1", "Seldom do we encounter", listOf("Seldom we encounter", "Seldom we do encounter", "Seldom encountered we"), "_____ such pristine geological formations.", "ہمیں شاذ و نادر ہی اس طرح کی خالص ارضیاتی ساخت ملتی ہے۔"),
            Tuple4("I06_LITTLE_DID_HE_KNOW", "Inversion", "Little Did He Know Structure", "INV:LITTLE_DID_HE:KNOW", "Little did he suspect", listOf("Little he suspected", "Little did he suspected", "Little suspect did he"), "_____ that the strategy would fail.", "اسے ذرا بھی شک نہیں تھا کہ حکمت عملی ناکام ہو جائے گی۔"),
            Tuple4("I07_UNDER_NO_CIRCUMSTANCES", "Inversion", "Under No Circumstances Modal Inversion", "INV:UNDER_NO_CIRCUMSTANCES:MODAL", "Under no circumstances should you share", listOf("Under no circumstances you should share", "Under no circumstances should you to share", "Under no circumstances share you should"), "_____ your security pass key.", "کسی بھی صورت میں آپ کو اپنا سیکیورٹی پاس کسی سے شیئر نہیں کرنا چاہیے۔"),
            Tuple4("I08_ON_NO_ACCOUNT", "Inversion", "On No Account Must Inversion", "INV:ON_NO_ACCOUNT:MUST", "On no account must visitors touch", listOf("On no account visitors must touch", "On no account must visitors to touch", "On no account touch visitors must"), "_____ the lab exhibits.", "کسی حالت میں بھی مہمانوں کو لیبارٹری کی نمائش کو نہیں چھونا چاہیے۔"),
            Tuple4("I09_NOT_ONLY_BUT_ALSO", "Inversion", "Not Only Inverted Auxiliary", "INV:NOT_ONLY:DID_SUBJ_V1", "Not only did he complete", listOf("Not only he completed", "Not only did he completed", "Not only completed he"), "_____ the task, but he also mentored others.", "اس نے نہ صرف کام مکمل کیا بلکہ دوسروں کی رہنمائی بھی کی۔"),
            Tuple4("I10_ONLY_AFTER_DID", "Inversion", "Only After Clause Inversion", "INV:ONLY_AFTER:DID_SUBJ_V1", "did he comprehend", listOf("he comprehended", "did he comprehended", "comprehended he"), "Only after reading the audit report _____ the deficit.", "آڈٹ رپورٹ پڑھنے کے بعد ہی وہ خسارے کو سمجھ پایا۔"),
            Tuple4("I11_ONLY_BY_CAN", "Inversion", "Only By Instrument Modal Inversion", "INV:ONLY_BY:CAN_SUBJ_V1", "can we overcome", listOf("we can overcome", "can we to overcome", "overcome we can"), "Only by rigorous training _____ these challenges.", "صرف سخت تربیتی عمل کے ذریعے ہی ہم ان چیلنجوں پر قابو پا سکتے ہیں۔"),
            Tuple4("I12_NOT_UNTIL_DID", "Inversion", "Not Until Clause Inversion", "INV:NOT_UNTIL:DID_SUBJ_V1", "did she notice", listOf("she noticed", "did she noticed", "noticed she"), "Not until midnight _____ the missing document.", "آدھی رات تک اس نے مفقود دستاویز پر توجہ نہیں دی تھی۔"),
            Tuple4("I13_INVERSION_SO_NEITHER", "Inversion", "Neither / Nor Inversion", "INV:SO_NEITHER:NEITHER_HAVE_I", "neither have I", listOf("neither I have", "so haven't I", "nor I have"), "They haven't received the grant, and _____.", "انہیں گرانٹ نہیں ملی ہے، اور نہ ہی مجھے ملی ہے۔"),
            Tuple4("I14_SO_SEVERE_WAS_THAT", "Inversion", "So Adjective Was The Noun That", "INV:SO_ADJ_WAS:THAT", "So intense was the heat that", listOf("So intense the heat was that", "So intense was heat which", "Such intense was heat that"), "_____ metal structures began to warp.", "گرمی اتنی شدید تھی کہ دھاتی ڈھانچے مڑنے لگے۔"),
            Tuple4("I15_SUCH_WAS_THE_FORCE", "Inversion", "Such Was The Force That", "INV:SUCH_WAS:THAT", "Such was the dedication of the team that", listOf("Such the dedication of the team was that", "Such was team dedication which", "So was dedication of team that"), "_____ they worked 24 hours straight.", "ٹیم کی لگن ایسی تھی کہ انہوں نے مسلسل 24 گھنٹے کام کیا۔"),
            Tuple4("I16_DIRECTIONAL_INVERSION", "Inversion", "Directional Prepositional Inversion", "INV:DIRECTIONAL:DOWN_CAME", "Down came the pouring rain", listOf("Down the pouring rain came", "Down came pouring rain it", "Down did come pouring rain"), "_____ on the thirsty crops.", "پیاسی فصلوں پر مصلادھار بارش برس پڑی۔"),
            Tuple4("I17_LOCATIVE_INVERSION", "Inversion", "Locative Adverbial Verb Subject", "INV:LOCATIVE:ON_THE_HILL_STOOD", "On the summit stood a majestic fortress", listOf("On the summit a majestic fortress stood", "On the summit stood majestic fortress it", "On summit did stand fortress"), "_____ overlooking the green valley.", "پہاڑ کی چوٹی پر ایک شاندار قلعہ ایستادہ تھا جو سبز وادی کو دیکھتا تھا۔"),
            Tuple4("I18_COMPARATIVE_INVERSION", "Inversion", "Comparative Inversion Than Did Subject", "INV:COMPARATIVE:THAN_DID_SUBJ", "than did any of her predecessors", listOf("than any of her predecessors did", "than did predecessors of her", "than her predecessors did had"), "She published more papers _____.", "اس نے اپنے پیشروؤں کے مقابلے میں زیادہ مقالے شائع کیے۔"),
            Tuple4("I19_IN_NO_WAY_CAN", "Inversion", "In No Way Modal Inversion", "INV:IN_NO_WAY:CAN_SUBJ", "In no way can this error be excused", listOf("In no way this error can be excused", "In no way can this error excused", "In no way error can be excuse"), "_____ by management.", "انتظامیہ کے ذریعے کسی صورت میں اس غلطی کی معافی نہیں مل سکتی۔"),
            Tuple4("I20_FRONTED_ADJ_EMPHASIS", "Focus", "Fronted Adjective Verb Subject", "INV:FRONTED_ADJ:BITTER_WAS", "Bitter was the disappointment", listOf("Bitter the disappointment was", "Bitter was disappointment it", "Bitter did disappointment be"), "_____ among the defeated candidates.", "شکست خوردہ امیدواروں میں مایوسی گہری تھی۔"),
            Tuple4("I21_FRONTED_PARTICIPLE_EMPHASIS", "Focus", "Fronted Participle Verb Subject", "INV:FRONTED_PARTICIPLE:STANDING_WAS", "Standing near the doorway was the chief guard", listOf("Standing near doorway chief guard was", "Standing near doorway was guard chief", "Stood near doorway was chief guard"), "_____ keeping close vigil.", "دروازے کے پاس ہی چیف گارڈ کھڑا تھا جو کڑی نگرانی کر رہا تھا۔"),
            Tuple4("I22_EMPHATIC_DO_POSITIVE", "Emphasis", "Emphatic Auxiliary Do/Does/Did", "EMPHASIS:AUXILIARY_DO:DOES_V1", "does contain", listOf("is contain", "containing does", "do contain"), "Contrary to rumors, the report _____ verified data.", "افواہوں کے برعکس، اس رپورٹ میں واقعی تصدیق شدہ اعداد و شمار شامل ہیں۔"),
            Tuple4("I23_CLEFT_OBJECT_EMPHASIS", "Cleft", "It Is Object That Emphasis", "CLEFT:IT_IS_OBJECT_THAT", "It is total transparency that we require", listOf("Total transparency is what we require that", "It total transparency that we require", "It is transparency that we requires"), "_____ in all public transactions.", "تمام عوامی معاملات میں جس چیز کا ہم تقاضا کرتے ہیں وہ مکمل شفافیت ہے۔"),
            Tuple4("I24_CLEFT_ADVERBIAL_EMPHASIS", "Cleft", "It Was In Year That Emphasis", "CLEFT:IT_WAS_ADVERBIAL_THAT", "It was in 2015 that the research center opened", listOf("In 2015 was when research center opened", "It was 2015 when research center opened", "It in 2015 that research center opened"), "_____ in Islamabad.", "اسلام آباد میں تحقیقی مرکز کا قیام 2015 ہی میں عمل میں آیا تھا۔"),
            Tuple4("I25_PSEUDO_CLEFT_VERB", "Cleft", "What Subject Did Was Bare Infinitive", "CLEFT:WHAT_SUBJ_DID_WAS_V1", "What the team did was completely redesign", listOf("What team did was completely redesigning", "That team did was redesign", "What team done was redesign"), "_____ the software architecture.", "ٹیم نے جو کام کیا وہ سافٹ ویئر کے فنِ تعمیر کو مکمل طور پر ازسرنو ترتیب دینا تھا۔"),
            Tuple4("I26_AT_NO_TIME_DID", "Inversion", "At No Time Did Inversion", "INV:AT_NO_TIME:DID_SUBJ", "At no time did the nuclear reactor exceed", listOf("At no time the nuclear reactor exceeded", "At no time did nuclear reactor exceeded", "At no time reactor exceeded did"), "_____ safe operational levels.", "ایٹمی ری ایکٹر کی گرمی نے کسی بھی وقت محفوظ حد سے تجاوز نہیں کیا۔"),
            Tuple4("I27_INVERSION_AS_THAN", "Inversion", "As / Than Inversion Subject Verb", "INV:AS_THAN:AS_DOES_SUBJ", "as does her senior colleague", listOf("as her senior colleague does", "as do her senior colleague", "as is her senior colleague"), "She values meticulous research, _____.", "وہ باریک بین تحقیق کی قدر کرتی ہیں، جیسے کہ ان کے سینئر ساتھی کرتے ہیں۔"),
            Tuple4("I28_IN_FEW_CASES_HAVE", "Inversion", "In Few Cases Have Inversion", "INV:IN_FEW_CASES:HAVE_SUBJ", "In few instances have scientists seen", listOf("In few instances scientists have seen", "In few instances have scientists saw", "In few instances seen scientists have"), "_____ such rapid genetic mutation.", "شاذ و نادر ہی سائنسدانوں نے اتنی تیز جینیاتی تبدیلی دیکھی ہو۔"),
            Tuple4("I29_ONLY_WHEN_DID", "Inversion", "Only When Clause Auxiliary Inversion", "INV:ONLY_WHEN:DID_SUBJ", "did the warning bell ring", listOf("the warning bell rang", "did warning bell rang", "rang the warning bell"), "Only when the power dropped below 10% _____.", "جب بجلی 10 فیصد سے کم ہوئی تب ہی وارننگ کی گھنٹی بجی۔"),
            Tuple4("I30_ONLY_WITH_EFFORT_CAN", "Inversion", "Only With Phrase Modal Inversion", "INV:ONLY_WITH:CAN_SUBJ", "Only with steady persistence can one master", listOf("Only with steady persistence one can master", "Only with steady persistence can one to master", "Only with persistence master can one"), "_____ a foreign language.", "صرف مستقل مزاجی ہی سے کوئی غیر ملکی زبان سیکھ سکتا ہے۔"),
            Tuple4("I31_FRONTED_BARE_INF_TRY", "Focus", "Try As Subject Might Bare Inf", "INV:FRONTED_INF:TRY_AS_HE_MIGHT", "Try as he might, he could not crack", listOf("Trying as he might, he could not crack", "Try as he could, he crack not", "As he try, he could not crack"), "_____ the encrypted code.", "اس نے کتنی ہی کوشش کی لیکن وہ انکرپٹڈ کوڈ توڑ نہ سکا۔"),
            Tuple4("I32_FRONTED_THOUGH_ADJ", "Focus", "Adjective Though Subject Verb", "INV:FRONTED_ADJ:EXHAUSTED_THOUGH_SHE_WAS", "Exhausted though she was, she presented", listOf("Though exhausted she was, she presented", "Exhausted as was she, she presented", "Exhausted although she was, presented she"), "_____ the final report eloquently.", "اگرچہ وہ انتہائی تھکی ہوئی تھی، لیکن اس نے حتمی رپورٹ کو فصاحت کے ساتھ پیش کیا۔"),
            Tuple4("I33_REDUPLICATIVE_FOCUS_DAY", "Emphasis", "Day After Day Did Subject Verb", "EMPHASIS:REDUPLICATIVE:DAY_AFTER_DAY", "Day after day did they toil", listOf("Day after day they toiled", "Day after day did they toiled", "Day after day toiled they"), "_____ in the deep coal mines.", "دن بہ دن انہوں نے کوئلے کی گہری کانوں میں سخت محنت کی۔"),
            Tuple4("I34_INVERTED_NEG_COND_WERE", "Inversion", "Were It Not For Subject", "INV:NEG_COND:WERE_IT_NOT_FOR", "Were it not for your timely intervention", listOf("If were it not for your intervention", "Was it not for your intervention", "Were not it for your intervention"), "_____, the project would have failed.", "اگر آپ کی بروقت مداخلت نہ ہوتی تو پروجیکٹ ناکام ہو جاتا۔"),
            Tuple4("I35_FRONTED_PREP_IN_NO_CASE", "Inversion", "In No Case Modal Inversion", "INV:IN_NO_CASE:MUST_SUBJ", "In no case must unauthorized personnel enter", listOf("In no case unauthorized personnel must enter", "In no case must unauthorized personnel to enter", "In no case enter must unauthorized personnel"), "_____ the cleanroom facility.", "کسی بھی صورت میں غیر مجاز افراد کلین روم سہولت میں داخل نہیں ہونے چاہئیں۔"),
            Tuple4("I36_NOT_A_SINGLE_WORD_DID", "Inversion", "Not A Word Did Subject Verb", "INV:NOT_A_WORD:DID_SUBJ", "Not a single word did he utter", listOf("Not a single word he uttered", "Not a single word did he uttered", "Not a word uttered he"), "_____ during the entire tribunal hearing.", "پورے ٹربیونل کی سماعت کے دوران اس نے ایک لفظ بھی نہ کہا۔")
        )

        topics.forEachIndexed { idx, t ->
            val iNum = idx + 1
            list.add(QuizSlotHelper.createTemplate(
                templateId = t.v1,
                slotIndex = 6,
                grammarTopic = t.v2,
                subtopic = t.v3,
                difficulty = if (iNum > 20) "Expert" else "Intermediate",
                structuralFingerprint = t.v4,
                category = "PAST",
                tenseId = "inversion_slot6_$iNum",
                tenseTitleEn = "${t.v2} • ${t.v3}",
                tenseTitleAr = "ترتیبِ الٹ",
                explanationEn = "Inversion structure focusing on ${t.v3}.",
                explanationUr = "اس ساخت میں کلام میں تاکید کے لیے جملے کی الٹ ترتیب پر زور دیا گیا ہے۔",
                commonMistake = "امدادی فعل کو فاعل سے پہلے لانا بھول جانا۔",
                variants = listOf(v(t.v7, t.v8, t.v5, t.v6))
            ))
        }

        list
    }

    private data class Tuple4<A, B, C, D, E, F, G, H>(
        val v1: A, val v2: B, val v3: C, val v4: D, val v5: E, val v6: F, val v7: G, val v8: H
    )
}
