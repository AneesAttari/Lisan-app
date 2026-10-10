package com.example.data.model.quizslots

import com.example.data.model.DailyQuiz365Generator.QuestionTemplate
import com.example.data.model.QuizSlotHelper
import com.example.data.model.QuizSlotHelper.SentenceVariant

object Slot2Voice {
    val templates: List<QuestionTemplate> by lazy {
        val list = mutableListOf<QuestionTemplate>()
        fun v(text: String, ur: String, corr: String, dist: List<String>) = SentenceVariant(text, ur, corr, dist)

        val topics = listOf(
            Tuple4("V01_PASSIVE_PRES_SMP", "Passive Voice", "Present Simple Passive", "PASSIVE:PRES_SMP:IS_ARE_V3", "are processed", listOf("is process", "are processing", "process"), "All applications _____ within 48 hours.", "تمام درخواستوں پر 48 گھنٹوں کے اندر کارروائی کی جاتی ہے۔"),
            Tuple4("V02_PASSIVE_PAST_SMP", "Passive Voice", "Past Simple Passive", "PASSIVE:PAST_SMP:WAS_WERE_V3", "was inaugurated", listOf("is inaugurated", "inaugurated was", "were inaugurate"), "The new medical center _____ by the governor yesterday.", "کل گورنر کے ہاتھوں نئے طبی مرکز کا افتتاح کیا گیا۔"),
            Tuple4("V03_PASSIVE_FUT_SMP", "Passive Voice", "Future Simple Passive", "PASSIVE:FUT_SMP:WILL_BE_V3", "will be announced", listOf("will announce", "will be announcing", "is announce"), "The election results _____ at midnight.", "انتخابی نتائج کا اعلان آدھی رات کو کیا جائے گا۔"),
            Tuple4("V04_PASSIVE_PRES_CONT", "Passive Voice", "Present Continuous Passive", "PASSIVE:PRES_CONT:IS_ARE_BEING_V3", "is being renovated", listOf("is renovating", "was renovated", "has renovated"), "The historical library _____ this month.", "اس مہینے تاریخی لائبریری کی تجدید کی جا رہی ہے۔"),
            Tuple4("V05_PASSIVE_PAST_CONT", "Passive Voice", "Past Continuous Passive", "PASSIVE:PAST_CONT:WAS_WERE_BEING_V3", "were being audited", listOf("were auditing", "have audited", "was audit"), "The company financial records _____ when the news broke.", "خبر پھیلنے پر کمپنی کے مالیاتی ریکارڈ کا آڈٹ کیا جا رہا تھا۔"),
            Tuple4("V06_PASSIVE_PRES_PERF", "Passive Voice", "Present Perfect Passive", "PASSIVE:PRES_PERF:HAS_HAVE_BEEN_V3", "has been approved", listOf("has approved", "is approved", "was approving"), "The research budget _____ by the board.", "بورڈ کی جانب سے تحقیقاتی بجٹ کو منظوری دے دی گئی ہے۔"),
            Tuple4("V07_PASSIVE_PAST_PERF", "Passive Voice", "Past Perfect Passive", "PASSIVE:PAST_PERF:HAD_BEEN_V3", "had been finalized", listOf("has been finalized", "was finalize", "had finalizing"), "The contract _____ before the partner backed out.", "شریک کے پیچھے ہٹنے سے پہلے معاہدے کو حتمی شکل دی جا چکی تھی۔"),
            Tuple4("V08_PASSIVE_FUT_PERF", "Passive Voice", "Future Perfect Passive", "PASSIVE:FUT_PERF:WILL_HAVE_BEEN_V3", "will have been constructed", listOf("will construct", "has constructed", "will be construct"), "By next autumn, the bridge _____.", "اگلی خزان تک، پل کی تعمیر مکمل ہو چکی ہوگی۔"),
            Tuple4("V09_CAUSATIVE_HAVE_PASSIVE", "Causative", "Have Something Done", "CAUSATIVE:HAVE_OBJ_V3", "had his car serviced", listOf("serviced his car had", "had serviced his car", "has his car service"), "Dr. Hamza _____ before the long trip.", "ڈاکٹر حمزہ نے طویل سفر سے پہلے اپنی گاڑی کی سروس کروائی۔"),
            Tuple4("V10_CAUSATIVE_GET_PASSIVE", "Causative", "Get Something Done", "CAUSATIVE:GET_OBJ_V3", "got her roof repaired", listOf("repaired her roof got", "got repair her roof", "gets her roof repairing"), "She _____ after the summer storm.", "انہوں نے گرمیوں کے طوفان کے بعد اپنی چھت کی مرمت کروائی۔"),
            Tuple4("V11_ACTIVE_CAUSATIVE_MAKE", "Causative", "Make Someone Do V1", "CAUSATIVE:MAKE_OBJ_V1", "made the students rewrite", listOf("made the students to rewrite", "made the students rewriting", "makes the students rewritten"), "The teacher _____ their essays.", "استاد نے طلباء سے اپنے مضامین دوبارہ لکھوائے۔"),
            Tuple4("V12_ACTIVE_CAUSATIVE_LET", "Causative", "Let Someone Do V1", "CAUSATIVE:LET_OBJ_V1", "let the intern handle", listOf("let the intern to handle", "lets the intern handling", "let the intern handled"), "The director _____ the client meeting.", "ڈائریکٹر نے انٹرن کو کلائنٹ میٹنگ سنبھالنے دی۔"),
            Tuple4("V13_ACTIVE_CAUSATIVE_HELP", "Causative", "Help Someone Do V1", "CAUSATIVE:HELP_OBJ_V1", "helped me organize", listOf("helped me organizing", "helps me organized", "helping me organize"), "She _____ the global seminar.", "انہوں نے عالمی سیمینار کے انعقاد میں میری مدد کی۔"),
            Tuple4("V14_IMPERSONAL_PASSIVE_IT", "Reporting Passive", "It Is Said That", "PASSIVE_REPORTING:IT_IS_V3_THAT", "It is believed that", listOf("He is believed that", "It believes that", "Is believed it that"), "_____ the discovery will revolutionize medicine.", "مانا جاتا ہے کہ یہ دریافت طب میں انقلاب برپا کر دے گی۔"),
            Tuple4("V15_PERSONAL_PASSIVE_PRES", "Reporting Passive", "He Is Believed To Be", "PASSIVE_REPORTING:HE_IS_V3_TO_BE", "is reported to be", listOf("reports to be", "is reporting being", "is reported being"), "The scientist _____ in Geneva.", "رپورٹ کے مطابق سائنسدان جنیوا میں ہیں۔"),
            Tuple4("V16_PERSONAL_PASSIVE_PAST", "Reporting Passive", "He Is Thought To Have Been", "PASSIVE_REPORTING:HE_IS_V3_TO_HAVE_BEEN", "is thought to have escaped", listOf("is thought escape", "thinks to have escaped", "is thinking escaped"), "The suspect _____ through the rear door.", "گمان ہے کہ ملزم پچھلے دروازے سے فرار ہو گیا تھا۔"),
            Tuple4("V17_PASSIVE_GERUND", "Passive Aspect", "Being V3 Gerund", "PASSIVE:GERUND:BEING_V3", "being criticized", listOf("to criticize", "criticizing being", "been criticized"), "He disdains _____ in public.", "وہ عوام کے سامنے تنقید کے نشانہ بننے سے نفرت کرتے ہیں۔"),
            Tuple4("V18_PASSIVE_INFINITIVE", "Passive Aspect", "To Be V3 Infinitive", "PASSIVE:INFINITIVE:TO_BE_V3", "to be evaluated", listOf("to evaluate", "being evaluated", "evaluated to be"), "The proposals are ready _____.", "تجاویز کی قدر پیمائی کی جانے کے لیے تیار ہیں۔"),
            Tuple4("V19_PASSIVE_PERFECT_INF", "Passive Aspect", "To Have Been V3", "PASSIVE:PERFECT_INF:TO_HAVE_BEEN_V3", "to have been written", listOf("to write", "having written", "to be writing"), "The ancient letter appears _____ in 500 BC.", "ایسا لگتا ہے کہ قدیم خط 500 قبل مسیح میں لکھا گیا تھا۔"),
            Tuple4("V20_PASSIVE_MODAL_PRESENT", "Modal Passive", "Must Be V3", "PASSIVE:MODAL_PRES:MUST_BE_V3", "must be signed", listOf("must sign", "must be sign", "is must signed"), "The agreement _____ by both parties.", "معاہدے پر دونوں فریقوں کے دستخط ہونا ضروری ہیں۔"),
            Tuple4("V21_PASSIVE_MODAL_PAST", "Modal Passive", "Should Have Been V3", "PASSIVE:MODAL_PAST:SHOULD_HAVE_BEEN_V3", "should have been notified", listOf("should notify", "must be notify", "ought notified"), "The team _____ in advance.", "ٹیم کو پہلے سے مطلع کیا جانا چاہیے تھا۔"),
            Tuple4("V22_PREPOSITIONAL_PASSIVE", "Prepositional Passive", "Verb Preposition Passive", "PASSIVE:PREPOSITIONAL:VERB_PREP_V3", "was operated on", listOf("was operated", "operated on was", "is operating on"), "The patient _____ by Dr. Fahad.", "ڈاکٹر فہد نے مریض کا آپریشن کیا۔"),
            Tuple4("V23_DOUBLE_OBJ_DIRECT", "Double Object Passive", "Direct Object Subject", "PASSIVE:DOUBLE_OBJ:DIRECT_SUBJECT", "was presented to", listOf("presented to was", "was presented for", "is presenting to"), "The trophy _____ the champion.", "فاتح کو ٹرافی پیش کی گئی۔"),
            Tuple4("V24_DOUBLE_OBJ_INDIRECT", "Double Object Passive", "Indirect Object Subject", "PASSIVE:DOUBLE_OBJ:INDIRECT_SUBJECT", "was awarded a medal", listOf("awarded a medal was", "was awarding a medal", "is award a medal"), "The officer _____ for bravery.", "افسر کو بہادری کے تمغے سے نوازا گیا۔"),
            Tuple4("V25_STATIVE_PASSIVE_PREP", "Stative Passive", "Interested In Rule", "PASSIVE:STATIVE_PREP:INTERESTED_IN", "is interested in", listOf("is interested with", "interests in", "interested on"), "She _____ artificial intelligence.", "وہ مصنوعی ذہانت میں دلچسپی رکھتی ہیں۔"),
            Tuple4("V26_PASSIVE_INSTRUMENTAL_WITH", "Passive Agent", "Instrumental With Rule", "PASSIVE:INSTRUMENTAL:WITH_TOOL", "was cut with", listOf("was cut by", "cutted with", "was cutting with"), "The rope _____ a sharp knife.", "رسی کو تیز چھری سے کاٹا گیا۔"),
            Tuple4("V27_PASSIVE_MODAL_CONT", "Modal Passive", "Must Be Being V3", "PASSIVE:MODAL_CONT:MUST_BE_BEING_V3", "must be being examined", listOf("must examine", "must be examine", "is must examined"), "The issue _____ right now.", "اس مسئلے کا اس وقت جائزہ لیا جا رہا ہوگا۔"),
            Tuple4("V28_PASSIVE_INF_EXPECT", "Passive Infinitive", "Expects To Be V3", "PASSIVE:INF_EXPECT:EXPECTS_TO_BE_V3", "expects to be promoted", listOf("expects promoting", "expects to promote", "expect to be promote"), "He _____ next year.", "وہ اگلے سال ترقی ملنے کی امید رکھتے ہیں۔"),
            Tuple4("V29_ACTIVE_CAUSATIVE_GET_TO", "Active Causative", "Get Someone To Do", "CAUSATIVE:GET_OBJ_TO_V1", "got the plumber to fix", listOf("got the plumber fix", "got the plumber fixing", "gets plumber to fixed"), "She _____ the broken pipe.", "انہوں نے پلمبر سے ٹوٹا ہوا پائپ ٹھیک کروایا۔"),
            Tuple4("V30_ACTIVE_CAUSATIVE_HAVE_BARE", "Active Causative", "Have Someone Do Bare", "CAUSATIVE:HAVE_OBJ_BARE_V1", "had the technician inspect", listOf("had the technician to inspect", "had the technician inspecting", "has technician inspected"), "We _____ the main generator.", "ہم نے ٹیکنیشن سے مین جنریٹر کا معائنہ کروایا۔"),
            Tuple4("V31_PASSIVE_IMPERATIVE", "Passive Command", "Let It Be V3", "PASSIVE:COMMAND:LET_IT_BE_V3", "Let it be done", listOf("Let it do", "Let it be do", "Let be done it"), "_____ immediately!", "یہ کام فوری طور پر ہو جانا چاہیے!"),
            Tuple4("V32_REDUCED_PASSIVE_REL", "Reduced Passive", "Past Participle Relative", "PASSIVE:REDUCED_REL:V3_CLAUSE", "written by the expert", listOf("that writing by expert", "was written by expert", "which write by expert"), "The manual _____ was clear.", "ماہر کے لکھے ہوئے دستی گائیڈ کی ہدایات واضح تھیں۔"),
            Tuple4("V33_REFLEXIVE_PASSIVE", "Reflexive Passive", "Get Oneself V3", "PASSIVE:REFLEXIVE:GET_ONESELF_V3", "got himself registered", listOf("got himself registering", "registered himself got", "gets himself register"), "He _____ for the conference.", "انہوں نے کانفرنس کے لیے اپنا اندراج کروایا۔"),
            Tuple4("V34_PASSIVE_PERCEPTUAL", "Perceptual Passive", "Was Seen To V1", "PASSIVE:PERCEPTUAL:WAS_SEEN_TO_V1", "was seen to enter", listOf("was seen enter", "saw to enter", "was seeing enter"), "The suspect _____ the building.", "مشتبہ شخص کو عمارت میں داخل ہوتے دیکھا گیا تھا۔"),
            Tuple4("V35_UNACCUSATIVE_ERGATIVE", "Ergative Verb", "Intransitive Result", "PASSIVE:ERGATIVE:INTRANSITIVE_RESULT", "opened automatically", listOf("was opened automatically by it", "is open automatically", "opening automatically"), "The heavy glass door _____.", "شیشے کا بھاری دروازہ خود بخود کھل گیا۔"),
            Tuple4("V36_PASSIVE_REPORT_SUPPOSED", "Reporting Passive", "Is Supposed To Be", "PASSIVE_REPORTING:IS_SUPPOSED_TO_BE", "is supposed to be completed", listOf("supposes to complete", "is supposed complete", "is supposing completed"), "The project _____ by Friday.", "پروجیکٹ جمعہ تک مکمل ہونا متوقع ہے۔")
        )

        topics.forEachIndexed { idx, t ->
            val vNum = idx + 1
            list.add(QuizSlotHelper.createTemplate(
                templateId = t.v1,
                slotIndex = 2,
                grammarTopic = t.v2,
                subtopic = t.v3,
                difficulty = if (vNum > 25) "Expert" else "Intermediate",
                structuralFingerprint = t.v4,
                category = "PAST",
                tenseId = "voice_slot2_$vNum",
                tenseTitleEn = "${t.v2} • ${t.v3}",
                tenseTitleAr = "فعلِ مجہول",
                explanationEn = "Passive voice structure focusing on ${t.v3}.",
                explanationUr = "اس مجہول ساخت میں ${t.v3} پر زور دیا گیا ہے۔",
                commonMistake = "معروف اور مجہول فعل کی تمیز نہ کرنا۔",
                variants = listOf(v(t.v7, t.v8, t.v5, t.v6))
            ))
        }

        list
    }

    private data class Tuple4<A, B, C, D, E, F, G, H>(
        val v1: A, val v2: B, val v3: C, val v4: D, val v5: E, val v6: F, val v7: G, val v8: H
    )
}
