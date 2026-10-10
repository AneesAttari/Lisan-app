package com.example.data.model.quizslots

import com.example.data.model.DailyQuiz365Generator.QuestionTemplate
import com.example.data.model.QuizSlotHelper
import com.example.data.model.QuizSlotHelper.SentenceVariant

object Slot1Modals {
    val templates: List<QuestionTemplate> by lazy {
        val list = mutableListOf<QuestionTemplate>()
        fun v(text: String, ur: String, corr: String, dist: List<String>) = SentenceVariant(text, ur, corr, dist)

        val topics = listOf(
            Tuple4("M01_PAST_MODAL_REGRET", "Past Modal", "Regret / Criticism", "MODAL:PAST_REGRET:SHOULD_HAVE_V3", "should have submitted", listOf("should submit", "must have submit", "ought submit"), "He _____ the report yesterday.", "انھیں کل رپورٹ جمع کرانی چاہیے تھی۔"),
            Tuple4("M02_PAST_MODAL_DEDUCTION", "Past Modal", "Certain Deduction", "MODAL:PAST_DEDUCTION:MUST_HAVE_V3", "must have missed", listOf("can have missed", "should miss", "must miss"), "The streets are wet; it _____ heavy rain.", "سڑکیں گیلی ہیں؛ یقیناً تیز بارش ہوئی ہوگی۔"),
            Tuple4("M03_PAST_MODAL_IMPOSSIBILITY", "Past Modal", "Impossibility", "MODAL:PAST_IMPOSSIBILITY:CANNOT_HAVE_V3", "cannot have stolen", listOf("must not steal", "should not steal", "might not stole"), "He _____ the document; he was abroad.", "وہ دستاویز چوری نہیں کر سکتا؛ وہ بیرون ملک تھا۔"),
            Tuple4("M04_PRES_MODAL_CERTAINTY", "Present Modal", "Logical Certainty", "MODAL:PRES_CERTAINTY:MUST_BE", "must be", listOf("can be", "should being", "is must"), "She has studied all night; she _____ exhausted.", "انہوں نے پوری رات پڑھائی کی ہے؛ وہ یقیناً تھک چکی ہوں گی۔"),
            Tuple4("M05_PRES_MODAL_NEG_CERTAINTY", "Present Modal", "Negative Certainty", "MODAL:PRES_NEG_CERTAINTY:CANNOT_BE", "cannot be", listOf("must not be", "should not be", "is not can"), "That claim _____ true based on facts.", "حقائق کی بنیاد پر وہ دعویٰ سچا نہیں ہو سکتا۔"),
            Tuple4("M06_OBLIGATION_MUST_VS_HAVETO", "Obligation", "External Necessity", "MODAL:OBLIGATION:HAVE_TO", "has to comply", listOf("must to comply", "complying has", "is comply"), "Every citizen _____ with state laws.", "ہر شہری کو ریاستی قوانین کی پابندی کرنی ہوگی۔"),
            Tuple4("M07_LACK_OF_OBLIGATION", "Lack of Obligation", "Don't Have To", "MODAL:LACK_OBLIGATION:DONT_HAVE_TO", "don't have to pay", listOf("must not pay", "should not paid", "haven't to pay"), "Entry is free; you _____ any fee.", "داخلہ مفت ہے؛ آپ کو کوئی فیس ادا کرنے کی ضرورت نہیں ہے۔"),
            Tuple4("M08_PROHIBITION_MUST_NOT", "Prohibition", "Must Not Rule", "MODAL:PROHIBITION:MUST_NOT", "must not park", listOf("don't have to park", "need not park", "aren't parking"), "Drivers _____ in front of emergency gates.", "ڈرائیوروں کو ہنگامی دروازوں کے سامنے گاڑی پارک نہیں کرنی چاہیے۔"),
            Tuple4("M09_FORMAL_PERMISSION", "Permission", "May Formal Rule", "MODAL:PERMISSION:MAY_FORMAL", "may leave", listOf("can to leave", "might left", "would leave"), "Candidates _____ the hall after two hours.", "امیدوار دو گھنٹے کے بعد ہال چھوڑ سکتے ہیں۔"),
            Tuple4("M10_ABILITY_BE_ABLE_TO", "Ability", "Be Able To Future", "MODAL:ABILITY:WILL_BE_ABLE_TO", "will be able to access", listOf("can access will", "could to access", "is able access"), "Users _____ the dashboard tomorrow.", "صارفین کل سے ڈیش بورڈ تک رسائی حاصل کر سکیں گے۔"),
            Tuple4("M11_PAST_SPECIFIC_ABILITY", "Past Ability", "Was Able To Rule", "MODAL:PAST_ABILITY:WAS_ABLE_TO", "was able to solve", listOf("could solve had", "can solve", "would solve"), "Despite the difficulty, he _____ the riddle.", "مشکل کے باوجود، وہ پہیلی حل کرنے میں کامیاب رہا۔"),
            Tuple4("M12_STRONG_RECOMMENDATION", "Advice", "Had Better Rule", "MODAL:ADVICE:HAD_BETTER_V1", "had better leave", listOf("had better to leave", "would better leave", "has better left"), "You _____ now or you will miss the train.", "بہتر ہوگا کہ آپ ابھی نکل جائیں ورنہ ٹرین چھوٹ جائے گی۔"),
            Tuple4("M13_POLITE_REQUEST_MIND", "Polite Request", "Would You Mind + Ing", "MODAL:REQUEST:WOULD_YOU_MIND_ING", "would you mind opening", listOf("would you mind to open", "do you mind open", "could you mind opening"), "_____ the window, please?", "کیا آپ برائے مہربانی کھڑکی کھول دیں گے؟"),
            Tuple4("M14_PREFERENCE_WOULD_RATHER", "Preference", "Would Rather Bare V1", "MODAL:PREFERENCE:WOULD_RATHER_V1", "would rather stay", listOf("would rather to stay", "would prefer stay", "had rather stayed"), "I _____ at home tonight than go out.", "میں آج رات باہر جانے کے بجائے گھر پر رہنا پسند کروں گا۔"),
            Tuple4("M15_PREFERENCE_WOULD_PREFER", "Preference", "Would Prefer To Infinitive", "MODAL:PREFERENCE:WOULD_PREFER_TO_V1", "would prefer to study", listOf("would prefer study", "would rather to study", "had prefer study"), "She _____ in a quiet library.", "وہ خاموش لائبریری میں پڑھنا زیادہ پسند کرے گی۔"),
            Tuple4("M16_PAST_LOW_CERTAINTY", "Past Speculation", "Might Have V3", "MODAL:PAST_SPECULATION:MIGHT_HAVE_V3", "might have forgotten", listOf("may forget", "must forget", "should forgot"), "He is late; he _____ the appointment time.", "انھیں دیر ہو گئی ہے؛ شاید وہ ملاقات کا وقت بھول گئے ہوں۔"),
            Tuple4("M17_PAST_UNFULFILLED_ABILITY", "Past Ability", "Could Have V3", "MODAL:PAST_ABILITY:COULD_HAVE_V3", "could have won", listOf("can win", "must have win", "should win"), "They _____ the game, but they made errors.", "وہ گیم جیت سکتے تھے، لیکن انہوں نے غلطیاں کیں۔"),
            Tuple4("M18_PAST_NEEDNT_HAVE", "Past Necessity", "Needn't Have V3", "MODAL:PAST_NECESSITY:NEEDNT_HAVE_V3", "needn't have brought", listOf("didn't need bring", "must not bring", "shouldn't bring"), "You _____ extra food; lunch was provided.", "آپ کو اضافی کھانا لانے کی ضرورت نہیں تھی؛ دوپہر کا کھانا فراہم کیا گیا تھا۔"),
            Tuple4("M19_BE_USED_TO_GERUND", "Semi-Modal", "Be Used To + Ing", "MODAL:SEMI_MODAL:BE_USED_TO_ING", "is used to working", listOf("is used to work", "used to work", "uses to working"), "Dr. Ali _____ under high pressure.", "ڈاکٹر علی شدید دباؤ میں کام کرنے کے عادی ہیں۔"),
            Tuple4("M20_DARE_NEED_MODAL", "Semi-Modal", "Need Not Bare Infinitive", "MODAL:SEMI_MODAL:NEED_NOT_BARE_V1", "need not worry", listOf("need not to worry", "don't need worry", "needs not worry"), "You _____ about the exam results.", "آپ کو امتحانی نتائج کی فکر کرنے کی ضرورت نہیں ہے۔"),
            Tuple4("M21_MODAL_PASSIVE_PRESENT", "Modal Passive", "Can Be V3", "MODAL:PASSIVE:CAN_BE_V3", "can be repaired", listOf("can repair", "can be repair", "is can repaired"), "This device _____ by a specialist.", "یہ آلہ کسی ماہر کے ذریعے ہی درست کیا جا سکتا ہے۔"),
            Tuple4("M22_MODAL_PASSIVE_PAST", "Modal Passive", "Should Have Been V3", "MODAL:PASSIVE:SHOULD_HAVE_BEEN_V3", "should have been inspected", listOf("should be inspect", "must have inspect", "ought be inspected"), "The machinery _____ before operation.", "چالو کرنے سے پہلے مشینری کی تفتش ہونی چاہیے تھی۔"),
            Tuple4("M23_WISH_COULD_ABILITY", "Wish Structure", "Wish + Could V1", "MODAL:WISH:COULD_V1", "could speak", listOf("can speak", "would speak have", "was able speak"), "I wish I _____ fluent Arabic.", "کاش میں روانی سے عربی بول سکتا۔"),
            Tuple4("M24_WISH_WOULD_HABIT", "Wish Structure", "Wish + Would V1", "MODAL:WISH:WOULD_V1", "would stop", listOf("will stop", "could stopped", "stops"), "I wish it _____ raining so we can walk.", "کاش بارش رک جائے تاکہ ہم پیدل چل سکیں۔"),
            Tuple4("M25_BE_SUPPOSED_TO", "Expectation", "Be Supposed To V1", "MODAL:EXPECTATION:BE_SUPPOSED_TO", "are supposed to wear", listOf("suppose to wear", "are supposed wearing", "must to wear"), "Visitors _____ identification badges.", "مہمانوں سے شناختی کارڈ پہننے کی توقع کی جاتی ہے۔"),
            Tuple4("M26_PAST_OBLIGATION_HAD_TO", "Past Obligation", "Had To V1", "MODAL:PAST_OBLIGATION:HAD_TO_V1", "had to cancel", listOf("must cancel", "have to cancel", "musted cancel"), "They _____ the flight due to bad weather.", "خراب موسم کی وجہ سے انہیں پرواز منسوخ کرنی پڑی۔"),
            Tuple4("M27_POLITE_SUGGESTION_SHALL", "Suggestion", "Shall We V1", "MODAL:SUGGESTION:SHALL_WE_V1", "Shall we begin", listOf("Will we begin", "Must we begin", "Should we to begin"), "_____ the presentation now?", "کیا ہم اب پریزنٹیشن شروع کریں؟"),
            Tuple4("M28_PAST_HABIT_WOULD", "Past Habit", "Would V1 Repeated", "MODAL:PAST_HABIT:WOULD_V1", "would sit", listOf("used to sitting", "will sit", "was sitting would"), "Every evening, my grandfather _____ in the garden.", "ہر شام، میرے دادا باغ میں بیٹھا کرتے تھے۔"),
            Tuple4("M29_PAST_CRITICISM_MIGHT_HAVE", "Past Criticism", "Might Have V3", "MODAL:CRITICISM:MIGHT_HAVE_V3", "might have warned", listOf("might warn", "should warn had", "may warned"), "You _____ us about the road closure!", "آپ کو سڑک کی بندش کے بارے میں ہمیں خبردار کر دینا چاہیے تھا!"),
            Tuple4("M30_DEGREE_BOUND_TO", "Certainty", "Bound To V1", "MODAL:CERTAINTY:BOUND_TO_V1", "is bound to succeed", listOf("is bound succeed", "bounds to succeed", "will bound to succeed"), "With his effort, he _____ in the test.", "اپنی محنت کی بدولت، وہ امتحان میں ضرور کامیاب ہوگا۔"),
            Tuple4("M31_MODAL_CONTINUOUS", "Modal Continuous", "Must Be Ing", "MODAL:CONTINUOUS:MUST_BE_ING", "must be sleeping", listOf("must sleep", "must been sleeping", "is must sleeping"), "The light is off; he _____ right now.", "بتی بند ہے؛ وہ اس وقت سو رہا ہوگا۔"),
            Tuple4("M32_MODAL_PERFECT_CONT", "Modal Perfect Continuous", "Must Have Been Ing", "MODAL:PERFECT_CONT:MUST_HAVE_BEEN_ING", "must have been driving", listOf("must be driving", "must drove", "should driving"), "He looks tired; he _____ all night.", "وہ تھکا ہوا لگتا ہے؛ وہ رات بھر گاڑی چلاتا رہا ہوگا۔"),
            Tuple4("M33_PAST_REFUSAL_WOULD_NOT", "Past Refusal", "Would Not V1", "MODAL:REFUSAL:WOULD_NOT_V1", "would not start", listOf("will not start", "did not started", "could not starting"), "The engine _____ despite several attempts.", "کئی کوششوں کے باوجود انجن چالو نہیں ہو رہا تھا۔"),
            Tuple4("M34_CONDITIONAL_RESULT_WOULD", "Conditional Result", "Would V1 Result", "MODAL:COND_RESULT:WOULD_V1", "would accept", listOf("will accept", "accepted would", "accepts"), "If offered the role, she _____ it.", "اگر اسے یہ عہدہ دیا گیا تو وہ اسے قبول کر لے گی۔"),
            Tuple4("M35_CONDITIONAL_RESULT_PAST", "Conditional Result Past", "Would Have V3 Result", "MODAL:COND_RESULT_PAST:WOULD_HAVE_V3", "would have completed", listOf("would complete", "will have completed", "completed would"), "Had he known, he _____ the task.", "اگر اسے معلوم ہوتا، تو وہ کام مکمل کر چکا ہوتا۔"),
            Tuple4("M36_HAD_BETTER_NOT", "Advice Warning", "Had Better Not V1", "MODAL:ADVICE_WARNING:HAD_BETTER_NOT", "had better not touch", listOf("had not better touch", "would better not touch", "had better don't touch"), "You _____ those exposed electrical wires.", "آپ کا ان ننگے برقی تاروں کو نہ چھونا ہی بہتر ہے۔")
        )

        topics.forEachIndexed { idx, t ->
            val mNum = idx + 1
            list.add(QuizSlotHelper.createTemplate(
                templateId = t.v1,
                slotIndex = 1,
                grammarTopic = t.v2,
                subtopic = t.v3,
                difficulty = if (mNum > 25) "Expert" else "Intermediate",
                structuralFingerprint = t.v4,
                category = "PRESENT",
                tenseId = "modal_slot1_$mNum",
                tenseTitleEn = "${t.v2} • ${t.v3}",
                tenseTitleAr = "امدادی افعال",
                explanationEn = "Modal structure focusing on ${t.v3}.",
                explanationUr = "اس ساخت میں ${t.v3} کے قواعد پر توجہ دی گئی ہے۔",
                commonMistake = "درست امدادی فعل یا فارم کا انتخاب نہ کرنا۔",
                variants = listOf(v(t.v7, t.v8, t.v5, t.v6))
            ))
        }

        list
    }

    private data class Tuple4<A, B, C, D, E, F, G, H>(
        val v1: A, val v2: B, val v3: C, val v4: D, val v5: E, val v6: F, val v7: G, val v8: H
    )
}
