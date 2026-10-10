package com.example.data.model

object AdvancedGrammarMcqData {
    val passiveVoiceFoundationsMcqs = listOf(
        LessonMcq(
            id = "mcq_pvf_1",
            question = "In all Passive Voice sentences, the main verb is ALWAYS in the:",
            questionUr = "فعلِ مجہول (Passive Voice) کے تمام جملوں میں اصل فعل ہمیشہ کس فارم میں ہوتا ہے؟",
            options = listOf("1st Form (V1)", "2nd Form (V2)", "3rd Form (Past Participle - V3)", "ing Form"),
            correctOptionIndex = 2,
            explanationUr = "فعلِ مجہول (Passive Voice) کا بنیادی اور لازمی قانون یہ ہے کہ اصل فعل ہمیشہ اپنی تیسری فارم (V3 / Past Participle) میں آتا ہے۔",
            explanationEn = "In Passive Voice, the main verb is always in its past participle form (3rd form - V3)."
        ),
        LessonMcq(
            id = "mcq_pvf_2",
            question = "Passive Voice shifts the primary focus of the sentence to the:",
            questionUr = "Passive Voice میں جملے کی بنیادی توجہ کس پر منتقل ہو جاتی ہے؟",
            options = listOf("Subject (Doer)", "Object (Receiver of action)", "Time phrase", "Preposition"),
            correctOptionIndex = 1,
            explanationUr = "Passive Voice میں فاعل (Subject) کے بجائے مفعول (Object) یعنی جس پر کام واقع ہوا ہے، اسے بنیادی اہمیت دی جاتی ہے۔",
            explanationEn = "Passive Voice shifts focus from the doer (subject) to the receiver of the action (object)."
        ),
        LessonMcq(
            id = "mcq_pvf_3",
            question = "When the pronoun 'He' becomes the object in Passive Voice, it changes to:",
            questionUr = "جب فاعل 'He' مفعول کی جگہ جائے تو کس شکل میں تبدیل ہوگا؟",
            options = listOf("his", "him", "himself", "he"),
            correctOptionIndex = 1,
            explanationUr = "'He' کی مفعولی حالت (Objective form) 'him' ہے، اس لیے 'by him' بنتا ہے۔",
            explanationEn = "The objective case of pronoun 'He' is 'him' (e.g., 'by him')."
        ),
        LessonMcq(
            id = "mcq_pvf_4",
            question = "Which type of verbs CANNOT be converted into Passive Voice?",
            questionUr = "کس قسم کے افعال کا فعلِ مجہول (Passive Voice) نہیں بنایا جا سکتا؟",
            options = listOf("Transitive verbs (متعدی)", "Intransitive verbs (لازمی - بغیر مفعول کے)", "Regular verbs", "Irregular verbs"),
            correctOptionIndex = 1,
            explanationUr = "افعالِ لازمی (Intransitive verbs جیسے go, sleep, die, laugh) جن کا مفعول نہیں ہوتا، ان کا مجہول نہیں بن سکتا۔",
            explanationEn = "Intransitive verbs have no direct object and cannot be converted to Passive Voice."
        ),
        LessonMcq(
            id = "mcq_pvf_5",
            question = "Why can the sentence 'He sleeps in the room' NOT be made passive?",
            questionUr = "جملہ 'He sleeps in the room' کا مجہول کیوں نہیں بن سکتا؟",
            options = listOf(
                "Because 'sleeps' is in Present Simple.",
                "Because 'sleep' is an intransitive verb with no direct object.",
                "Because 'room' is a noun.",
                "Because 'He' is singular."
            ),
            correctOptionIndex = 1,
            explanationUr = "فعل 'sleep' ایک فعلِ لازمی (Intransitive verb) ہے اور 'in the room' جگہ کو ظاہر کرتا ہے، کوئی مفعول نہیں ہے اس لیے مجہول ناممکن ہے۔",
            explanationEn = "'Sleep' is intransitive; 'in the room' is a prepositional phrase, not a direct object."
        ),
        LessonMcq(
            id = "mcq_pvf_6",
            question = "When is the agent ('by someone') omitted in Passive Voice?",
            questionUr = "Passive Voice میں فاعل ('by ...') کو کب حذف کیا جاتا ہے؟",
            options = listOf(
                "When the agent is unknown, obvious, or unimportant.",
                "Only in questions.",
                "Only in past tense.",
                "Never."
            ),
            correctOptionIndex = 0,
            explanationUr = "جب کام کرنے والا نامعلوم ہو (Someone stole my bike -> My bike was stolen) یا واضح ہو، تو 'by' کو حذف کر دیا جاتا ہے۔",
            explanationEn = "The agent is omitted when it is unknown, obvious, or unimportant (e.g., 'My car was stolen')."
        ),
        LessonMcq(
            id = "mcq_pvf_7",
            question = "Active: 'Ali wrote a letter.' — The correct Passive form is:",
            questionUr = "جملہ 'Ali wrote a letter' کا درست Passive Voice کیا ہے؟",
            options = listOf(
                "A letter is written by Ali.",
                "A letter was written by Ali.",
                "A letter had written by Ali.",
                "A letter written by Ali."
            ),
            correctOptionIndex = 1,
            explanationUr = "چونکہ جملہ Past Simple میں ہے، اس لیے مفعول 'A letter' کے بعد 'was' اور V3 'written' لگے گا: 'A letter was written by Ali'۔",
            explanationEn = "Past simple active changes to 'was/were + V3': 'A letter was written by Ali'."
        ),
        LessonMcq(
            id = "mcq_pvf_8",
            question = "When the pronoun 'They' changes to the agent position, it becomes:",
            questionUr = "ضمیر 'They' مفعول بن کر کیا بن جاتی ہے؟",
            options = listOf("their", "theirs", "them", "themselves"),
            correctOptionIndex = 2,
            explanationUr = "'They' کی مفعولی حالت 'them' ہے (by them)۔",
            explanationEn = "The objective case for 'They' is 'them' ('by them')."
        ),
        LessonMcq(
            id = "mcq_pvf_9",
            question = "Active: 'People speak English worldwide.' — Choose the most natural Passive form:",
            questionUr = "سب سے موزوں اور قدرتی Passive Voice منتخب کریں:",
            options = listOf(
                "English is spoken worldwide.",
                "English is spoke worldwide by people.",
                "English was spoken by all peoples.",
                "English has spoken worldwide."
            ),
            correctOptionIndex = 0,
            explanationUr = "چونکہ 'people' ایک عام اور واضح فاعل ہے اس لیے 'by people' کے بغیر 'English is spoken worldwide' سب سے فصیح اور درست ہے۔",
            explanationEn = "'English is spoken worldwide' is the most natural passive form omitting the generic agent."
        ),
        LessonMcq(
            id = "mcq_pvf_10",
            question = "Which of the following is a Passive Voice sentence?",
            questionUr = "درج ذیل میں سے Passive Voice والا جملہ پہچانیں:",
            options = listOf(
                "The cat chased the mouse.",
                "The chef cooked a delicious meal.",
                "The bridge was built in 1995.",
                "Students read books every day."
            ),
            correctOptionIndex = 2,
            explanationUr = "'The bridge was built in 1995' میں مفعول 'bridge' پہلے ہے اور 'was + V3 (built)' استعمال ہوا ہے، اس لیے یہ مجہول ہے۔",
            explanationEn = "'The bridge was built in 1995' contains 'was + V3', which is the passive voice structure."
        )
    )

    val passiveVoiceConversionMcqs = listOf(
        LessonMcq(
            id = "mcq_pvc_1",
            question = "Active: 'She is cooking the dinner.' — Passive:",
            questionUr = "Present Continuous کے جملے کو Passive میں تبدیل کریں:",
            options = listOf(
                "The dinner is cooked by her.",
                "The dinner is being cooked by her.",
                "The dinner was being cooked by her.",
                "The dinner has been cooked by her."
            ),
            correctOptionIndex = 1,
            explanationUr = "Present Continuous کے مجہول میں 'is/am/are + being + V3' کی ساخت استعمال ہوتی ہے، اس لیے 'is being cooked' درست ہے۔",
            explanationEn = "Present Continuous passive requires 'is/are + being + V3': 'is being cooked by her'."
        ),
        LessonMcq(
            id = "mcq_pvc_2",
            question = "Active: 'They have completed the building.' — Passive:",
            questionUr = "Present Perfect کے جملے کا مجہول بنائیں:",
            options = listOf(
                "The building has been completed by them.",
                "The building have been completed by them.",
                "The building was completed by them.",
                "The building is being completed by them."
            ),
            correctOptionIndex = 0,
            explanationUr = "مفعول 'The building' واحد ہے، اس لیے 'has been completed' آئے گا۔",
            explanationEn = "'The building' is singular and takes 'has been completed by them'."
        ),
        LessonMcq(
            id = "mcq_pvc_3",
            question = "Active: 'Who wrote this famous book?' — Passive:",
            questionUr = "سوالیہ لفظ 'Who' والے جملے کا مجہول کیسے بنے گا؟",
            options = listOf(
                "By whom this famous book was written?",
                "By whom was this famous book written?",
                "Who was this famous book written by?",
                "Whom wrote this famous book?"
            ),
            correctOptionIndex = 1,
            explanationUr = "'Who' کے مجہول میں شروع میں 'By whom' آتا ہے اور پھر امدادی فعل فاعل سے پہلے لگتا ہے: 'By whom was this famous book written?'۔",
            explanationEn = "'Who' becomes 'By whom + auxiliary + subject + V3': 'By whom was this famous book written?'."
        ),
        LessonMcq(
            id = "mcq_pvc_4",
            question = "Active: 'Close the window.' (Command/Order) — Passive:",
            questionUr = "حکمیہ جملے (Imperative Order) کا مجہول بنائیں:",
            options = listOf(
                "Let the window be closed.",
                "You close the window.",
                "The window should closed.",
                "Let the window closed."
            ),
            correctOptionIndex = 0,
            explanationUr = "حکمیہ جملوں کا مجہول بنانے کا فارمولا 'Let + Object + be + V3' ہے، اس لیے 'Let the window be closed' درست ہے۔",
            explanationEn = "Imperative commands use 'Let + object + be + V3': 'Let the window be closed'."
        ),
        LessonMcq(
            id = "mcq_pvc_5",
            question = "Active: 'You must submit the assignment today.' — Passive:",
            questionUr = "Modal فعل 'must' کے جملے کا مجہول منتخب کریں:",
            options = listOf(
                "The assignment must be submitted today.",
                "The assignment must submitted today.",
                "The assignment must been submitted today.",
                "The assignment is submitted today."
            ),
            correctOptionIndex = 0,
            explanationUr = "Modal افعال کے مجہول کا فارمولا 'Modal + be + V3' ہے: 'must be submitted'۔",
            explanationEn = "Modal verbs in passive voice follow 'Modal + be + V3': 'must be submitted'."
        ),
        LessonMcq(
            id = "mcq_pvc_6",
            question = "Active: 'Did the police catch the thief?' — Passive:",
            questionUr = "ماضی کے سوالیہ جملے کو مجہول میں تبدیل کریں:",
            options = listOf(
                "Was the thief caught by the police?",
                "Did the thief caught by the police?",
                "Was the thief catch by the police?",
                "Has the thief caught by the police?"
            ),
            correctOptionIndex = 0,
            explanationUr = "Past Simple کے سوالیہ مجہول کا آغاز 'Was/Were + Object + V3' سے ہوتا ہے: 'Was the thief caught...?'۔",
            explanationEn = "Past Simple question converts to 'Was/Were + Object + V3?': 'Was the thief caught?'."
        ),
        LessonMcq(
            id = "mcq_pvc_7",
            question = "Active: 'She was writing a letter.' — Passive:",
            questionUr = "Past Continuous کے جملے کا مجہول منتخب کریں:",
            options = listOf(
                "A letter was written by her.",
                "A letter was being written by her.",
                "A letter had been written by her.",
                "A letter is being written by her."
            ),
            correctOptionIndex = 1,
            explanationUr = "Past Continuous کے مجہول میں 'was/were + being + V3' آتا ہے: 'A letter was being written by her'۔",
            explanationEn = "Past Continuous passive requires 'was/were + being + V3': 'was being written'."
        ),
        LessonMcq(
            id = "mcq_pvc_8",
            question = "Active: 'They will announce the results tomorrow.' — Passive:",
            questionUr = "Future Simple کے جملے کا مجہول بنائیں:",
            options = listOf(
                "The results will be announced tomorrow.",
                "The results will been announced tomorrow.",
                "The results are announced tomorrow.",
                "The results will announce tomorrow."
            ),
            correctOptionIndex = 0,
            explanationUr = "Future Simple کا مجہول 'will be + V3' سے بنتا ہے: 'The results will be announced tomorrow'۔",
            explanationEn = "Future Simple passive structure is 'will be + V3': 'will be announced'."
        ),
        LessonMcq(
            id = "mcq_pvc_9",
            question = "Active: 'Please help me.' (Request) — Passive:",
            questionUr = "التجائیہ جملے کا مناسب ترین مجہول کیا ہوگا؟",
            options = listOf(
                "Let me be helped.",
                "You are requested to help me.",
                "I should be helped.",
                "Please be helped."
            ),
            correctOptionIndex = 1,
            explanationUr = "التجا اور درخواست والے جملوں میں 'You are requested to + V1' کا اسلوب سب سے شائستہ اور درست ہوتا ہے۔",
            explanationEn = "Polite requests are best converted using 'You are requested to + base verb'."
        ),
        LessonMcq(
            id = "mcq_pvc_10",
            question = "Identify the grammatically correct passive conversion:",
            questionUr = "درست تبدیل شدہ مجہول جملہ منتخب کریں:",
            options = listOf(
                "The room is cleaned yesterday.",
                "The room was being cleaned when I arrived.",
                "The book was stole by someone.",
                "The letter has wrote by him."
            ),
            correctOptionIndex = 1,
            explanationUr = "'The room was being cleaned when I arrived' بالکل درست ہے کیونکہ Past Continuous کے مجہول میں 'was being + V3' آیا ہے۔",
            explanationEn = "'The room was being cleaned when I arrived' correctly applies 'was being + V3'."
        )
    )

    val directIndirectFoundationsMcqs = listOf(
        LessonMcq(
            id = "mcq_dif_1",
            question = "In Direct and Indirect Speech, the 'SON' rule for pronoun changes means:",
            questionUr = "ضمائر کی تبدیلی میں 'SON' اصول سے کیا مراد ہے؟",
            options = listOf(
                "1st Person -> Subject, 2nd Person -> Object, 3rd Person -> No Change",
                "1st Person -> Object, 2nd Person -> Subject, 3rd Person -> No Change",
                "Singular, Object, Noun",
                "Simple, Objective, Neutral"
            ),
            correctOptionIndex = 0,
            explanationUr = "SON اصول کے مطابق: 1st person (I, We) فاعل کے مطابق، 2nd person (You) مفعول کے مطابق، اور 3rd person (He, She, They) میں کوئی تبدیلی نہیں ہوتی۔",
            explanationEn = "SON rule: 1st person pronouns change per Subject, 2nd person per Object, and 3rd person pronouns have No change."
        ),
        LessonMcq(
            id = "mcq_dif_2",
            question = "If the Reporting Verb is in Present or Future tense (e.g., 'He says'), what happens to the tense of the Reported Speech?",
            questionUr = "اگر واوین سے باہر والا فعل حال یا مستقبل میں ہو (مثلاً He says)، تو اندر والے زمانے کا کیا بنتا ہے؟",
            options = listOf(
                "Tense backshifts to Past.",
                "Tense does NOT change at all.",
                "Tense changes to Future.",
                "Tense changes to Continuous."
            ),
            correctOptionIndex = 1,
            explanationUr = "اہم استثناء: اگر Reporting Verb حال یا مستقبل میں ہو (He says / She will say) تو اندرونی جملے کا زمانہ بالکل نہیں بدلتا!",
            explanationEn = "When the reporting verb is in present or future tense, the tense inside reported speech does NOT change."
        ),
        LessonMcq(
            id = "mcq_dif_3",
            question = "Direct: The teacher said, 'The sun rises in the east.' — Indirect:",
            questionUr = "آفاقی سچائی (Universal Truth) والے جملے کا Indirect کیا ہوگا؟",
            options = listOf(
                "The teacher said that the sun rose in the east.",
                "The teacher said that the sun rises in the east.",
                "The teacher said that the sun had risen in the east.",
                "The teacher asked if the sun rises in the east."
            ),
            correctOptionIndex = 1,
            explanationUr = "آفاقی سچائی اور سائنسی حقائق کا زمانہ کبھی ماضی میں تبدیل نہیں ہوتا، اس لیے 'the sun rises in the east' برقرار رہے گا۔",
            explanationEn = "Universal facts and scientific truths retain their present tense in reported speech."
        ),
        LessonMcq(
            id = "mcq_dif_4",
            question = "When the Reporting Verb is in Past ('said'), 'Present Simple' changes into:",
            questionUr = "ماضی کے Reporting Verb کی موجودگی میں Present Simple کس زمانے میں بدلتا ہے؟",
            options = listOf("Past Simple", "Past Continuous", "Past Perfect", "Future Simple"),
            correctOptionIndex = 0,
            explanationUr = "زمانوں کے پیچھے ہٹنے (Backshift) کے قانون کے تحت Present Simple فعل 'Past Simple' میں تبدیل ہو جاتا ہے۔",
            explanationEn = "Under backshift rules, Present Simple shifts to Past Simple (am/is/are -> was/were, V1 -> V2)."
        ),
        LessonMcq(
            id = "mcq_dif_5",
            question = "In Indirect Speech, the time word 'today' changes to:",
            questionUr = "Indirect Speech بناتے وقت لفظ 'today' کس میں تبدیل ہوتا ہے؟",
            options = listOf("the next day", "that day", "yesterday", "the previous day"),
            correctOptionIndex = 1,
            explanationUr = "وقت کے الفاظ میں 'today' تبدیل ہو کر 'that day' بن جاتا ہے۔",
            explanationEn = "'Today' changes to 'that day' in reported speech."
        ),
        LessonMcq(
            id = "mcq_dif_6",
            question = "In Indirect Speech, the time word 'tomorrow' changes to:",
            questionUr = "لفظ 'tomorrow' کی تبدیلی کس میں ہوتی ہے؟",
            options = listOf("the previous day", "that day", "the next day / following day", "yesterday"),
            correctOptionIndex = 2,
            explanationUr = "'tomorrow' تبدیل ہو کر 'the next day' یا 'the following day' بن جاتا ہے۔",
            explanationEn = "'Tomorrow' converts to 'the next day' or 'the following day'."
        ),
        LessonMcq(
            id = "mcq_dif_7",
            question = "In Indirect Speech, the modal verb 'will' changes to:",
            questionUr = "Reporting Verb ماضی میں ہونے پر 'will' کس میں بدلتا ہے؟",
            options = listOf("shall", "would", "could", "might"),
            correctOptionIndex = 1,
            explanationUr = "'will' ماضی کی حالت 'would' میں تبدیل ہو جاتا ہے۔",
            explanationEn = "'Will' backshifts to 'would' in reported speech."
        ),
        LessonMcq(
            id = "mcq_dif_8",
            question = "Direct: He said, 'I am reading a book.' — Indirect:",
            questionUr = "جملہ 'He said, I am reading a book' کا درست Indirect منتخب کریں:",
            options = listOf(
                "He said that he is reading a book.",
                "He said that he was reading a book.",
                "He said that I was reading a book.",
                "He said that he had read a book."
            ),
            correctOptionIndex = 1,
            explanationUr = "فاعل 'I' بدل کر 'he' بنے گا اور Present Continuous (am reading) بدل کر Past Continuous (was reading) بن جائے گا۔",
            explanationEn = "'I' changes to 'he' and 'am reading' backshifts to 'was reading'."
        ),
        LessonMcq(
            id = "mcq_dif_9",
            question = "In Indirect Speech, the word 'here' changes to:",
            questionUr = "مقام کا لفظ 'here' کس میں تبدیل ہوتا ہے؟",
            options = listOf("there", "this", "that", "now"),
            correctOptionIndex = 0,
            explanationUr = "مقام کی دوری کو ظاہر کرنے کے لیے 'here' کو 'there' میں بدلا جاتا ہے۔",
            explanationEn = "'Here' converts to 'there' in reported speech."
        ),
        LessonMcq(
            id = "mcq_dif_10",
            question = "Direct: She says, 'I love English.' — Indirect:",
            questionUr = "جملہ 'She says, I love English' کا درست Indirect چنیں:",
            options = listOf(
                "She says that she loved English.",
                "She says that she loves English.",
                "She said that she loved English.",
                "She says that I love English."
            ),
            correctOptionIndex = 1,
            explanationUr = "چونکہ Reporting Verb 'says' حال میں ہے اس لیے اندرونی زمانہ نہیں بدلے گا، صرف فاعل 'I' سے 'she' بنے گا: 'She says that she loves English'۔",
            explanationEn = "Because the reporting verb 'says' is present, the tense does not backshift: 'She says that she loves English'."
        )
    )

    val directIndirectSentencesMcqs = listOf(
        LessonMcq(
            id = "mcq_dis_1",
            question = "Direct: Ali said to me, 'Are you coming to the seminar?' — Indirect:",
            questionUr = "ہاں/نہ والے سوالیہ جملے (Yes/No Question) کا درست Indirect چنیں:",
            options = listOf(
                "Ali asked me that I was coming to the seminar.",
                "Ali asked me if I was coming to the seminar.",
                "Ali told me if I was coming to the seminar.",
                "Ali asked me was I coming to the seminar."
            ),
            correctOptionIndex = 1,
            explanationUr = "Yes/No سوالیہ جملوں میں ربطی لفظ 'if' یا 'whether' لگتا ہے اور جملہ بیانی حالت (Subject + Verb) میں آتا ہے (if I was coming)۔ 'that' کبھی نہیں لگتا۔",
            explanationEn = "Yes/No questions use 'asked + if/whether' and statement word order (Subject before Verb)."
        ),
        LessonMcq(
            id = "mcq_dis_2",
            question = "Direct: She said to him, 'Where do you live?' — Indirect:",
            questionUr = "WH-سوالیہ جملے کا درست Indirect کیا ہوگا؟",
            options = listOf(
                "She asked him that where he lived.",
                "She asked him where he lived.",
                "She asked him where did he live.",
                "She told him where he lives."
            ),
            correctOptionIndex = 1,
            explanationUr = "WH-سوالیہ جملوں میں خود WH-word ہی کنیکٹر بنتا ہے، 'that' نہیں لگایا جاتا اور فاعل فعل سے پہلے آتا ہے: 'where he lived'۔",
            explanationEn = "WH-questions retain the question word ('where') as connector with assertive word order ('he lived')."
        ),
        LessonMcq(
            id = "mcq_dis_3",
            question = "Direct: The teacher said to the students, 'Open your books.' — Indirect:",
            questionUr = "حکمیہ جملے (Imperative Command) کا درست Indirect منتخب کریں:",
            options = listOf(
                "The teacher ordered the students to open their books.",
                "The teacher ordered the students that open their books.",
                "The teacher said to students to open books.",
                "The teacher asked students that they opened books."
            ),
            correctOptionIndex = 0,
            explanationUr = "حکمیہ جملوں میں 'ordered + to + V1' کا ڈھانچہ استعمال ہوتا ہے: 'ordered the students to open their books'۔",
            explanationEn = "Imperative commands use 'ordered/commanded + to + base verb'."
        ),
        LessonMcq(
            id = "mcq_dis_4",
            question = "Direct: He said, 'Please lend me your pen.' — Indirect:",
            questionUr = "التجائیہ جملے (Request) کا Indirect کیسے بنے گا؟",
            options = listOf(
                "He told to lend him pen.",
                "He requested me to lend him my pen.",
                "He requested that please lend him pen.",
                "He ordered me to lend him pen."
            ),
            correctOptionIndex = 1,
            explanationUr = "درخواست کے لیے 'requested + to + V1' لگتا ہے اور لفظ 'please' حذف کر دیا جاتا ہے۔",
            explanationEn = "Requests use 'requested + to + base verb', omitting the word 'please'."
        ),
        LessonMcq(
            id = "mcq_dis_5",
            question = "Direct: Father said to his son, 'Do not waste your time.' — Indirect:",
            questionUr = "منفی نصیحت/حکم (Negative Imperative) کا درست Indirect چنیں:",
            options = listOf(
                "Father advised his son not to waste his time.",
                "Father advised his son to not waste his time.",
                "Father told his son that do not waste time.",
                "Father asked his son not wasting time."
            ),
            correctOptionIndex = 0,
            explanationUr = "منفی حکمیہ یا نصیحت والے جملوں میں 'not to + V1' استعمال ہوتا ہے: 'advised his son not to waste his time'۔",
            explanationEn = "Negative imperatives connect using 'not to + base verb'."
        ),
        LessonMcq(
            id = "mcq_dis_6",
            question = "Direct: They said, 'Hurrah! We won the grand prize.' — Indirect:",
            questionUr = "خوشی کے استعجابیہ جملے (Exclamatory Sentence) کا درست Indirect کیا ہوگا؟",
            options = listOf(
                "They exclaimed with joy that they had won the grand prize.",
                "They said happily that we won the grand prize.",
                "They told that they won the prize.",
                "They exclaimed with sorrow that they won the grand prize."
            ),
            correctOptionIndex = 0,
            explanationUr = "خوشی کے استعجاب میں 'exclaimed with joy that...' استعمال ہوتا ہے اور Past Simple بدل کر Past Perfect (had won) بن جاتا ہے۔",
            explanationEn = "Exclamations of joy use 'exclaimed with joy that + past perfect'."
        ),
        LessonMcq(
            id = "mcq_dis_7",
            question = "Direct: The old woman said to the boy, 'May you live long!' — Indirect:",
            questionUr = "دعائیہ جملے (Optative Sentence) کا Indirect منتخب کریں:",
            options = listOf(
                "The old woman prayed that the boy might live long.",
                "The old woman asked the boy to live long.",
                "The old woman said that may the boy live long.",
                "The old woman wished to the boy living long."
            ),
            correctOptionIndex = 0,
            explanationUr = "دعائیہ جملوں میں Reporting Verb 'prayed / wished' بنتا ہے اور 'may' بدل کر 'might' ہو جاتا ہے۔",
            explanationEn = "Optative prayers use 'prayed that + subject + might + base verb'."
        ),
        LessonMcq(
            id = "mcq_dis_8",
            question = "Direct: Ali said, 'Let us go for a walk.' — Indirect:",
            questionUr = "تجویز والے جملے ('Let us...') کا درست Indirect چنیں:",
            options = listOf(
                "Ali ordered that they should go for a walk.",
                "Ali suggested that they should go for a walk.",
                "Ali told to go for a walk.",
                "Ali asked that let us go for a walk."
            ),
            correctOptionIndex = 1,
            explanationUr = "'Let us' والے جملوں میں 'suggested / proposed that + they should + V1' کا ڈھانچہ آتا ہے۔",
            explanationEn = "Proposals with 'Let's' convert to 'suggested that + subject + should + base verb'."
        ),
        LessonMcq(
            id = "mcq_dis_9",
            question = "Direct: Doctor said to patient, 'Take this medicine regularly.' — Indirect:",
            questionUr = "ڈاکٹر کی نصیحت کا درست Indirect کیا ہوگا؟",
            options = listOf(
                "Doctor advised the patient to take that medicine regularly.",
                "Doctor told that patient take medicine.",
                "Doctor ordered to take this medicine.",
                "Doctor asked if patient took medicine."
            ),
            correctOptionIndex = 0,
            explanationUr = "طبی نصیحت میں 'advised + to + V1' لگتا ہے اور 'this' تبدیل ہو کر 'that' بن جاتا ہے۔",
            explanationEn = "Medical advice uses 'advised + to + V1', converting 'this' to 'that'."
        ),
        LessonMcq(
            id = "mcq_dis_10",
            question = "Identify the grammatically INCORRECT indirect conversion:",
            questionUr = "درج ذیل میں سے غلط Indirect Speech والا جملہ پہچانیں:",
            options = listOf(
                "He asked me where I was going.",
                "He asked me that where was I going.",
                "He inquired of me if I was ready.",
                "He requested me to help him."
            ),
            correctOptionIndex = 1,
            explanationUr = "'He asked me that where was I going' دو وجوہات سے غلط ہے: (۱) سوالیہ جملے میں 'that' نہیں لگتا، (۲) فاعل سے پہلے 'was' سوالیہ ترتیب میں نہیں آنا چاہیے تھا۔",
            explanationEn = "'He asked me that where was I going' is doubly wrong: it incorrectly uses 'that' before 'where' and maintains inverted question order."
        )
    )
}
