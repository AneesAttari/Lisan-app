package com.example.data.model.quizslots

import com.example.data.model.DailyQuiz365Generator.QuestionTemplate
import com.example.data.model.QuizSlotHelper
import com.example.data.model.QuizSlotHelper.SentenceVariant

object Slot4ReportedSpeech {
    val templates: List<QuestionTemplate> by lazy {
        val list = mutableListOf<QuestionTemplate>()
        fun v(text: String, ur: String, corr: String, dist: List<String>) = SentenceVariant(text, ur, corr, dist)

        val topics = listOf(
            Tuple4("R01_REPORTED_PRES_PAST", "Reported Speech", "Tense Backshift Present to Past", "REPORTED:BACKSHIFT:PRES_TO_PAST", "she was working", listOf("she is working", "she works", "was she working"), "She said that _____ on a new project.", "انہوں نے کہا کہ وہ ایک نئے پروجیکٹ پر کام کر رہی ہیں۔"),
            Tuple4("R02_REPORTED_PAST_PASTPERF", "Reported Speech", "Tense Backshift Past to Past Perfect", "REPORTED:BACKSHIFT:PAST_TO_PASTPERF", "they had finished", listOf("they finished", "they have finished", "had they finished"), "He stated that _____ the audit prior to noon.", "انہوں نے بتایا کہ وہ دوپہر سے پہلے آڈٹ مکمل کر چکے تھے۔"),
            Tuple4("R03_REPORTED_TIME_SHIFT", "Reported Speech", "Time Expression Shift Today to That Day", "REPORTED:TIME_SHIFT:TODAY_THAT_DAY", "that day", listOf("today", "this day", "the today"), "The manager said he would sign the papers _____.", "مینیجر نے کہا کہ وہ اس دن کاغذات پر دستخط کریں گے۔"),
            Tuple4("R04_REPORTED_PLACE_SHIFT", "Reported Speech", "Place Expression Shift Here to There", "REPORTED:PLACE_SHIFT:HERE_TO_THERE", "there", listOf("here", "at here", "in here"), "She told us to wait _____ until the meeting started.", "انہوں نے ہمیں میٹنگ شروع ہونے تک وہاں انتظار کرنے کو کہا۔"),
            Tuple4("R05_REPORTED_PRONOUN_SHIFT", "Reported Speech", "Pronoun Shift First to Third Person", "REPORTED:PRONOUN_SHIFT:FIRST_TO_THIRD", "he", listOf("I", "myself", "me"), "Ali said that _____ would lead the team.", "علی نے کہا کہ وہ ٹیم کی قیادت کریں گے۔"),
            Tuple4("R06_REPORTED_YES_NO_IF", "Reported Speech", "Reported Yes/No Question with If", "REPORTED:YES_NO_QUESTION:IF_WHETHER", "if they had completed", listOf("did they complete", "if had they completed", "whether did they complete"), "The director asked _____ the survey.", "ڈائریکٹر نے پوچھا کہ کیا انہوں نے سروے مکمل کر لیا ہے۔"),
            Tuple4("R07_REPORTED_WH_WORD_ORDER", "Reported Speech", "Reported Wh- Question Word Order", "REPORTED:WH_QUESTION:WORD_ORDER", "where the main library was", listOf("where was the main library", "where is the main library", "where main library was"), "The tourist inquired _____ located.", "سیاح نے پوچھا کہ مرکزی لائبریری کہاں واقع ہے۔"),
            Tuple4("R08_INDIRECT_QUESTION_POLITE", "Indirect Questions", "Polite Intro Could You Tell Me", "INDIRECT:POLITE_INTRO:COULD_YOU_TELL", "where the office is", listOf("where is the office", "where was the office", "the office is where"), "Could you please tell me _____?", "کیا آپ مجھے بتا سکتے ہیں کہ دفتر کہاں ہے؟"),
            Tuple4("R09_INDIRECT_QUESTION_SUBJECT", "Indirect Questions", "Subject Focus Embedded Question", "INDIRECT:SUBJECT_FOCUS:WHO_LEFT", "who left the key", listOf("who did leave the key", "who was leaving key", "whom left the key"), "Do you know _____ on the table?", "کیا آپ کو معلوم ہے کہ میز پر چابی کس نے چھوڑی؟"),
            Tuple4("R10_REPORTED_COMMAND_TO_INF", "Reported Speech", "Reported Command To Infinitive", "REPORTED:COMMAND:TO_INFINITIVE", "to submit the report", listOf("that submit report", "submitting report", "to submitted report"), "The supervisor told the intern _____ immediately.", "سپروائزر نے انٹرن سے کہا کہ وہ فوری طور پر رپورٹ جمع کروائے۔"),
            Tuple4("R11_REPORTED_NEG_COMMAND", "Reported Speech", "Reported Negative Command Not To", "REPORTED:COMMAND:NOT_TO_INFINITIVE", "warned us not to touch", listOf("warned us to not touch", "warned us don't touch", "warned us not touching"), "The guard _____ the high-voltage wire.", "محافظ نے ہمیں ہائی وولٹیج تار کو نہ چھونے کی تنبیہ کی۔"),
            Tuple4("R12_REPORTED_VERB_PREP_GERUND", "Reporting Verbs", "Apologized For Gerund", "REPORTING_VERB:PREP_GERUND:APOLOGIZED_FOR", "apologized for being late", listOf("apologized to be late", "apologized that late", "apologized for late"), "The speaker _____ for the presentation.", "مقرر نے پریزنٹیشن میں تاخیر پر معذرت کی۔"),
            Tuple4("R13_REPORTED_VERB_THAT_CLAUSE", "Reporting Verbs", "Explained That Clause", "REPORTING_VERB:THAT_CLAUSE:EXPLAINED", "explained that the server was down", listOf("explained the server down", "explained server is down", "explained about server down"), "The technician _____.", "ٹیکنیشن نے وضاحت کی کہ سرور کام نہیں کر رہا تھا۔"),
            Tuple4("R14_REPORTED_VERB_OBJ_INF", "Reporting Verbs", "Advised Object To Infinitive", "REPORTING_VERB:OBJ_INF:ADVISED_TO", "advised her to consult", listOf("advised her consulting", "advised that she consults", "advised to her consult"), "The mentor _____ a doctor.", "مینٹور نے انہیں ڈاکٹر سے مشورہ کرنے کا مشورہ دیا۔"),
            Tuple4("R15_REPORTED_VERB_GERUND", "Reporting Verbs", "Suggested Gerund", "REPORTING_VERB:GERUND:SUGGESTED_ING", "suggested launching", listOf("suggested to launch", "suggested launch", "suggested that launching"), "The strategist _____ a new promotional campaign.", "حکمت عملی ساز نے ایک نئی پروموشنل مہم شروع کرنے کی تجویز پیش کی۔"),
            Tuple4("R16_REPORTED_OFFER_TO_INF", "Reporting Speech", "Offered To Infinitive", "REPORTED:OFFER:TO_INFINITIVE", "offered to carry", listOf("offered carrying", "offered that carry", "offered for carrying"), "He _____ the heavy luggage for the guest.", "انہوں نے مہمان کا بھاری سامان اٹھانے کی پیشکش کی۔"),
            Tuple4("R17_REPORTED_PROMISE_TO_INF", "Reporting Speech", "Promised To Infinitive", "REPORTED:PROMISE:TO_INFINITIVE", "promised to return", listOf("promised returning", "promised that return", "promised for returning"), "She _____ the borrowed book by Friday.", "انہوں نے جمعہ تک ادھار لی گئی کتاب واپس کرنے کا وعدہ کیا۔"),
            Tuple4("R18_REPORTED_THREAT_TO_INF", "Reporting Speech", "Threatened To Infinitive", "REPORTED:THREAT:TO_INFINITIVE", "threatened to sue", listOf("threatened suing", "threatened that sue", "threatened for suing"), "The firm _____ the competitor for patent violation.", "فرم نے پیٹنٹ کی خلاف ورزی پر حریف پر مقدمہ کرنے کی دھمکی دی۔"),
            Tuple4("R19_REPORTED_REFUSAL_TO_INF", "Reporting Speech", "Refused To Infinitive", "REPORTED:REFUSAL:TO_INFINITIVE", "refused to sign", listOf("refused signing", "refused that sign", "refused for signing"), "The executive _____ the disputed document.", "ایگزیکٹو نے متنازعہ دستاویز پر دستخط کرنے سے انکار کر دیا۔"),
            Tuple4("R20_REPORTED_PERMANENT_TRUTH", "Reported Speech", "No Backshift Universal Truth", "REPORTED:NO_BACKSHIFT:PERMANENT_TRUTH", "revolves around the sun", listOf("revolved around the sun", "was revolving around sun", "is revolved around sun"), "The teacher explained that the Earth _____.", "استاد نے بتایا کہ زمین سورج کے گرد گھومتی ہے۔"),
            Tuple4("R21_REPORTED_PRES_MATRIX_VERB", "Reported Speech", "No Backshift Present Reporting Verb", "REPORTED:NO_BACKSHIFT:PRES_MATRIX_VERB", "he is coming", listOf("he was coming", "he came", "was he coming"), "He says that _____ to the workshop today.", "ان کا کہنا ہے کہ وہ آج ورکشاپ میں آ رہے ہیں۔"),
            Tuple4("R22_REPORTED_MODAL_SHIFTS", "Reported Speech", "Modal Backshift Can to Could", "REPORTED:MODAL_SHIFT:CAN_TO_COULD", "she could code in Python", listOf("she can code in Python", "she is coding in Python", "could she code in Python"), "She confirmed that _____.", "انہوں نے تصدیق کی کہ وہ پائتھن میں کوڈنگ کر سکتی ہیں۔"),
            Tuple4("R23_REPORTED_MODAL_UNCHANGED", "Reported Speech", "Modal Unchanged Should/Would", "REPORTED:MODAL_UNCHANGED:SHOULD_WOULD", "they should inspect", listOf("they shall inspect", "they had to inspect", "should they inspect"), "The consultant advised that _____ the facility.", "مشیر نے مشورہ دیا کہ انہیں سہولت کا معائنہ کرنا چاہیے۔"),
            Tuple4("R24_INDIRECT_EMBEDDED_CLAUSE", "Indirect Questions", "I Wonder Embedded Clause", "INDIRECT:EMBEDDED:I_WONDER_WHY", "why the bus was late", listOf("why was the bus late", "why is the bus late", "why bus late was"), "I wonder _____ yesterday.", "مجھے حیرت ہے کہ کل بس کو دیر کیوں ہوئی۔"),
            Tuple4("R25_INDIRECT_EMBEDDED_CHOICE", "Indirect Questions", "Whether To Infinitive Choice", "INDIRECT:EMBEDDED:WHETHER_TO_INF", "whether to accept", listOf("whether accepting", "if to accept", "whether accept"), "She was unsure _____ the job offer.", "وہ اس بات پر غیر یقینی کا شکار تھیں کہ نوکری کی پیشکش قبول کریں یا نہیں۔"),
            Tuple4("R26_REPORTED_AUX_INVERSION_REMOVAL", "Reported Questions", "Removal of Do/Did Inversion", "REPORTED:QUESTION:AUX_INVERSION_REMOVED", "what time the flight departed", listOf("what time did the flight depart", "what time departed the flight", "what time was flight departing"), "He asked _____.", "اس نے پوچھا کہ فلائٹ کس وقت روانہ ہوئی۔"),
            Tuple4("R27_REPORTED_EXCLAMATION", "Reported Speech", "Exclaimed With Joy That", "REPORTED:EXCLAMATION:EXCLAIMED_THAT", "exclaimed that the team had won", listOf("exclaimed that team wins", "exclaimed team winning", "exclaimed to win team"), "The fans _____ the championship.", "شائقین نے خوشی سے نعرہ لگایا کہ ٹیم چیمپئن شپ جیت گئی ہے۔"),
            Tuple4("R28_REPORTED_SUBJUNCTIVE_SUGGEST", "Reported Speech", "Suggested That He Be Subjunctive", "REPORTED:SUBJUNCTIVE:SUGGESTED_THAT_BARE", "suggested that he be promoted", listOf("suggested that he is promoted", "suggested him to promoted", "suggested that he was promoted"), "The panel _____.", "پینل نے تجویز پیش کی کہ انہیں ترقی دی جائے۔"),
            Tuple4("R29_REPORTED_DENIAL_GERUND", "Reporting Verbs", "Denied Gerund Action", "REPORTING_VERB:DENIAL:DENIED_ING", "denied leaking the documents", listOf("denied to leak documents", "denied that leak documents", "denied for leaking documents"), "The spokesperson _____ to the press.", "ترجمان نے پریس کو دستاویزات لیک کرنے کی تردید کی۔"),
            Tuple4("R30_REPORTED_ADMISSION_PERF_GERUND", "Reporting Verbs", "Admitted Having Past Action", "REPORTING_VERB:ADMISSION:HAVING_V3", "admitted having committed", listOf("admitted to commit", "admitted that commit", "admitted for commit"), "The suspect _____ the accounting error.", "ملزم نے محاسبے میں غلطی کا اعتراف کیا۔"),
            Tuple4("R31_REPORTED_ACCUSATION_OF_GERUND", "Reporting Verbs", "Accused Of Gerund", "REPORTING_VERB:ACCUSATION:ACCUSED_OF_ING", "accused him of tampering", listOf("accused him to tamper", "accused him for tampering", "accused him that tampering"), "The auditor _____ with the financial ledgers.", "آڈیٹر نے ان پر مالیاتی کھاتوں میں ہیرا پھیری کا الزام لگایا۔"),
            Tuple4("R32_REPORTED_REMINDER_OBJ_INF", "Reporting Verbs", "Reminded Object To Infinitive", "REPORTING_VERB:REMINDER:REMINDED_TO", "reminded me to back up", listOf("reminded me backing up", "reminded me that back up", "reminded to me back up"), "The IT manager _____ my files.", "آئی ٹی مینیجر نے مجھے اپنی فائلوں کا بیک اپ لینے کی یاد دہانی کرائی۔"),
            Tuple4("R33_REPORTED_ENCOURAGE_OBJ_INF", "Reporting Verbs", "Encouraged Object To Infinitive", "REPORTING_VERB:ENCOURAGE:ENCOURAGED_TO", "encouraged us to apply", listOf("encouraged us applying", "encouraged that we apply", "encouraged to us applying"), "The mentor _____ for the international grant.", "مینٹور نے ہمیں بین الاقوامی گرانٹ کے لیے درخواست دینے کی ترغیب دی۔"),
            Tuple4("R34_REPORTED_REGRET_NOT_HAVING", "Reporting Verbs", "Regretted Not Having V3", "REPORTING_VERB:REGRET:NOT_HAVING_V3", "regretted not having double-checked", listOf("regretted to not double-check", "regretted not to check", "regretted for not check"), "He _____ the numerical data.", "انہیں عددی اعداد و شمار کی دوبارہ تصدیق نہ کرنے پر افسوس ہوا۔"),
            Tuple4("R35_REPORTED_COMPLEX_COND_BACKSHIFT", "Reported Speech", "Reported Conditional Shift", "REPORTED:CONDITIONAL:PASTPERF_WOULDHAVE", "would have helped if we had known", listOf("will help if we know", "would help if we knew", "helped would if had known"), "They told us that they _____.", "انہوں نے ہمیں بتایا کہ اگر انہیں معلوم ہوتا تو وہ مدد کرتے۔"),
            Tuple4("R36_REPORTED_PASSIVE_MATRIX", "Reporting Passive", "It Was Reported That", "REPORTING_PASSIVE:IT_WAS_REPORTED_THAT", "It was reported that the road was closed", listOf("It reported road closed", "It was report that road closed", "Road reported to closed"), "_____ due to snow.", "رپورٹ کیا گیا تھا کہ برف باری کی وجہ سے سڑک بند تھی۔")
        )

        topics.forEachIndexed { idx, t ->
            val rNum = idx + 1
            list.add(QuizSlotHelper.createTemplate(
                templateId = t.v1,
                slotIndex = 4,
                grammarTopic = t.v2,
                subtopic = t.v3,
                difficulty = if (rNum > 25) "Expert" else "Intermediate",
                structuralFingerprint = t.v4,
                category = "PAST",
                tenseId = "reported_slot4_$rNum",
                tenseTitleEn = "${t.v2} • ${t.v3}",
                tenseTitleAr = "بالواسطہ گفتگو",
                explanationEn = "Reported speech rule focusing on ${t.v3}.",
                explanationUr = "اس ساخت میں بالواسطہ گفتگو کے اصول بیان کیے گئے ہیں۔",
                commonMistake = "زمانے اور الفاظ کی تبدیلی نہ کرنا۔",
                variants = listOf(v(t.v7, t.v8, t.v5, t.v6))
            ))
        }

        list
    }

    private data class Tuple4<A, B, C, D, E, F, G, H>(
        val v1: A, val v2: B, val v3: C, val v4: D, val v5: E, val v6: F, val v7: G, val v8: H
    )
}
