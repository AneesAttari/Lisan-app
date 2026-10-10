package com.example.data.model

object PastTensesMcqData {
    val pastSimpleMcqs = listOf(
        LessonMcq(
            id = "mcq_pst_1",
            question = "I _____ a letter to my mentor yesterday.",
            questionUr = "ماضی کے مثبت جملے میں فعل 'write' کی دوسری فارم (V2) چنیں۔",
            options = listOf("write", "wrote", "written", "writing"),
            correctOptionIndex = 1,
            explanationUr = "Past Simple کے مثبت جملے میں فعل کی دوسری فارم (V2) استعمال ہوتی ہے۔ 'write' کی دوسری فارم 'wrote' ہے۔",
            explanationEn = "In affirmative Past Simple sentences, use the simple past form (V2: 'wrote')."
        ),
        LessonMcq(
            id = "mcq_pst_2",
            question = "He did not _____ the lecture last Monday.",
            questionUr = "'did not' کے بعد فعل 'attend' کی درست فارم منتخب کریں۔",
            options = listOf("attended", "attend", "attending", "attends"),
            correctOptionIndex = 1,
            explanationUr = "اہم ترین اصول: 'did' یا 'did not' کے بعد ہمیشہ فعل کی پہلی بنیادی فارم (V1) آتی ہے، لہٰذا 'attend' درست ہے۔",
            explanationEn = "CRITICAL RULE: After 'did' or 'did not', the main verb must always revert to its base form (V1: 'attend')."
        ),
        LessonMcq(
            id = "mcq_pst_3",
            question = "_____ you visit the historical museum last summer?",
            questionUr = "ماضی کے سوالیہ جملے کا آغاز کس امدادی فعل سے ہوگا؟",
            options = listOf("Do", "Did", "Have", "Were"),
            correctOptionIndex = 1,
            explanationUr = "Past Simple کے سوالیہ جملے تمام فاعلوں کے ساتھ 'Did' سے شروع ہوتے ہیں۔",
            explanationEn = "Past Simple questions use the auxiliary verb 'Did' for all subjects."
        ),
        LessonMcq(
            id = "mcq_pst_4",
            question = "They _____ to Turkey for vacation two years ago.",
            questionUr = "فعل 'go' کی Past Simple میں مثبت شکل چنیں۔",
            options = listOf("go", "went", "gone", "going"),
            correctOptionIndex = 1,
            explanationUr = "ماضی کے مثبت جملے کے لیے 'go' کی دوسری فارم 'went' استعمال کی جائے گی۔",
            explanationEn = "The past simple (V2) form of the irregular verb 'go' is 'went'."
        ),
        LessonMcq(
            id = "mcq_pst_5",
            question = "Where did she _____ that antique watch?",
            questionUr = "'Where did she' کے بعد فعل 'find' کی درست فارم لگائیں۔",
            options = listOf("found", "find", "finding", "finds"),
            correctOptionIndex = 1,
            explanationUr = "سوالیہ جملے میں 'did' موجود ہونے کی وجہ سے فعل کی پہلی فارم 'find' استعمال ہوگی، نہ کہ 'found'۔",
            explanationEn = "Because 'did' is already present in the question, the verb remains in base form: 'find'."
        ),
        LessonMcq(
            id = "mcq_pst_6",
            question = "We did not _____ any sound during the night.",
            questionUr = "منفی جملے میں فعل 'hear' کی درست شکل کا انتخاب کریں۔",
            options = listOf("heard", "hear", "hearing", "hears"),
            correctOptionIndex = 1,
            explanationUr = "'did not' کے بعد فعل کی پہلی فارم 'hear' آتی ہے۔",
            explanationEn = "Use base verb 'hear' after the negative auxiliary 'did not'."
        ),
        LessonMcq(
            id = "mcq_pst_7",
            question = "The train arrived at the station five minutes _____.",
            questionUr = "ماضی کے وقت کے اشارے کے لیے مناسب لفظ چنیں۔",
            options = listOf("since", "ago", "before", "for"),
            correctOptionIndex = 1,
            explanationUr = "حال سے ماضی کے وقت کے فاصلے کے لیے 'ago' استعمال ہوتا ہے، جیسے 'five minutes ago'۔",
            explanationEn = "'Ago' is used to measure past time back from the present moment in Past Simple."
        ),
        LessonMcq(
            id = "mcq_pst_8",
            question = "Why did the manager _____ the urgent meeting?",
            questionUr = "سوالیہ جملے میں 'did' کے بعد فعل 'cancel' کی شکل چنیں۔",
            options = listOf("cancelled", "cancel", "cancelling", "cancels"),
            correctOptionIndex = 1,
            explanationUr = "'Did' کے ساتھ ہمیشہ فعل کی پہلی شکل 'cancel' استعمال ہوگی۔",
            explanationEn = "The auxiliary 'did' takes the base infinitive 'cancel'."
        ),
        LessonMcq(
            id = "mcq_pst_9",
            question = "She _____ a delicious cake for her sister's birthday.",
            questionUr = "فعل 'bake' کی دوسری فارم چنیں۔",
            options = listOf("bakes", "baked", "baking", "bake"),
            correctOptionIndex = 1,
            explanationUr = "ماضی کے مثبت جملے میں باقاعدہ فعل کے ساتھ -ed لگتا ہے: 'baked'۔",
            explanationEn = "Regular verbs add -ed in the affirmative Past Simple: 'baked'."
        ),
        LessonMcq(
            id = "mcq_pst_10",
            question = "Choose the sentence that is grammatically INCORRECT:",
            questionUr = "درج ذیل میں سے گرامر کی رو سے غلط جملہ تلاش کریں:",
            options = listOf(
                "Did you see the match yesterday?",
                "She did not go to the clinic.",
                "Did he went to school yesterday?",
                "I bought a new car last month."
            ),
            correctOptionIndex = 2,
            explanationUr = "'Did he went' غلط ہے کیونکہ 'Did' کے بعد دوسری فارم 'went' نہیں بلکہ پہلی فارم 'go' ہونی چاہیے (Did he go)۔",
            explanationEn = "'Did he went' is incorrect because 'did' must be followed by base form 'go', not 'went'."
        )
    )

    val pastContinuousMcqs = listOf(
        LessonMcq(
            id = "mcq_pcon_1",
            question = "I _____ sleeping peacefully when the phone rang loudly.",
            questionUr = "واحد متکلم فاعل 'I' کے ساتھ Past Continuous کا امدادی فعل چنیں۔",
            options = listOf("were", "was", "is", "am"),
            correctOptionIndex = 1,
            explanationUr = "فاعل 'I', 'He', 'She', 'It' اور واحد اسماء کے ساتھ 'was' استعمال ہوتا ہے: 'I was sleeping'۔",
            explanationEn = "First-person and third-person singular subjects take 'was' in Past Continuous."
        ),
        LessonMcq(
            id = "mcq_pcon_2",
            question = "They _____ playing cricket when it suddenly started to rain.",
            questionUr = "جمع فاعل 'They' کے ساتھ مناسب امدادی فعل منتخب کریں۔",
            options = listOf("was", "were", "are", "have"),
            correctOptionIndex = 1,
            explanationUr = "جمع فاعل 'They', 'We', 'You' کے ساتھ 'were' لگایا جاتا ہے: 'They were playing'۔",
            explanationEn = "Plural subjects take 'were' in Past Continuous: 'They were playing'."
        ),
        LessonMcq(
            id = "mcq_pcon_3",
            question = "What _____ you doing at 9:00 PM last night?",
            questionUr = "فاعل 'you' والے سوالیہ جملے میں 'What' کے بعد کون سا فعل آئے گا؟",
            options = listOf("was", "were", "did", "are"),
            correctOptionIndex = 1,
            explanationUr = "فاعل 'you' کے سوالیہ جملے میں 'What were you doing...' کی ترتیب ہوتی ہے۔",
            explanationEn = "With 'you', the past continuous question begins with 'What were you doing?'."
        ),
        LessonMcq(
            id = "mcq_pcon_4",
            question = "She was not _____ attention to the speaker's instructions.",
            questionUr = "'was not' کے بعد فعل 'pay' کی ing والی شکل چنیں۔",
            options = listOf("paid", "paying", "pays", "pay"),
            correctOptionIndex = 1,
            explanationUr = "Past Continuous میں ہمیشہ فعل کی ing والی شکل استعمال ہوتی ہے: 'paying'۔",
            explanationEn = "Past Continuous requires the -ing present participle: 'paying'."
        ),
        LessonMcq(
            id = "mcq_pcon_5",
            question = "While I was driving, Ali _____ listening to the radio.",
            questionUr = "ایک ساتھ جاری دو کاموں کے لیے فاعل 'Ali' کے ساتھ درست فعل چنیں۔",
            options = listOf("were", "was", "is", "did"),
            correctOptionIndex = 1,
            explanationUr = "واحد اسم 'Ali' کے ساتھ 'was listening' آئے گا۔ جب دو کام بیک وقت ماضی میں ہو رہے ہوں تو دونوں میں Past Continuous آتا ہے۔",
            explanationEn = "Singular subject 'Ali' takes 'was listening' to show parallel ongoing past actions."
        ),
        LessonMcq(
            id = "mcq_pcon_6",
            question = "_____ he preparing dinner when you arrived?",
            questionUr = "فاعل 'he' کے سوالیہ جملے کا آغاز کس فعل سے ہوگا؟",
            options = listOf("Were", "Was", "Did", "Is"),
            correctOptionIndex = 1,
            explanationUr = "واحد فاعل 'he' کے لیے سوالیہ جملہ 'Was he preparing...' سے شروع ہوگا۔",
            explanationEn = "Questions with third-person singular 'he' start with 'Was'."
        ),
        LessonMcq(
            id = "mcq_pcon_7",
            question = "The children _____ making a lot of noise in the classroom.",
            questionUr = "جمع اسم 'children' کے ساتھ درست امدادی فعل چنیں۔",
            options = listOf("was", "were", "is", "did"),
            correctOptionIndex = 1,
            explanationUr = "'Children' جمع ہے، اس لیے اس کے ساتھ 'were' لگے گا۔",
            explanationEn = "'Children' is an irregular plural noun and requires 'were'."
        ),
        LessonMcq(
            id = "mcq_pcon_8",
            question = "I was reading a novel _____ the lights suddenly went out.",
            questionUr = "جاری عمل کے دوران اچانک ہونے والے واقعے کے لیے مناسب ربطی لفظ چنیں۔",
            options = listOf("while", "when", "during", "since"),
            correctOptionIndex = 1,
            explanationUr = "جب کوئی جاری کام (Past Continuous) کسی مختصر واقعے (Past Simple) سے منقطع ہو، تو 'when' کا استعمال ہوتا ہے۔",
            explanationEn = "Use 'when' before the interrupting past simple clause."
        ),
        LessonMcq(
            id = "mcq_pcon_9",
            question = "Why was she _____ so loudly in the hallway?",
            questionUr = "'was she' کے بعد فعل 'cry' کی درست شکل منتخب کریں۔",
            options = listOf("cried", "crying", "cries", "cry"),
            correctOptionIndex = 1,
            explanationUr = "Past Continuous سوالیہ میں فعل کی ing فارم 'crying' آئے گی۔",
            explanationEn = "Past Continuous interrogative requires the present participle 'crying'."
        ),
        LessonMcq(
            id = "mcq_pcon_10",
            question = "Identify the grammatically correct sentence:",
            questionUr = "درست گرامر والا جملہ منتخب کریں:",
            options = listOf(
                "He were studying when I called him.",
                "He was studying when I called him.",
                "He was study when I called him.",
                "He was studied when I called him."
            ),
            correctOptionIndex = 1,
            explanationUr = "'He was studying when I called him' بالکل درست ہے کیونکہ فاعل 'He' کے ساتھ 'was' اور فعل کی 'ing' فارم لگتی ہے۔",
            explanationEn = "'He was studying when I called him' correctly pairs 'He' with 'was studying'."
        )
    )

    val pastPerfectMcqs = listOf(
        LessonMcq(
            id = "mcq_ppf_1",
            question = "The train _____ already left before we reached the station.",
            questionUr = "پہلے واقع ہونے والے کام (Earlier Past) کے لیے درست ساخت چنیں۔",
            options = listOf("has", "had", "was", "did"),
            correctOptionIndex = 1,
            explanationUr = "ماضی کے دو کاموں میں سے پہلے مکمل ہونے والے کام کے لیے Past Perfect (had + V3) استعمال ہوتا ہے۔ اس لیے 'had already left' درست ہے۔",
            explanationEn = "The earlier of two past actions uses Past Perfect ('had left')."
        ),
        LessonMcq(
            id = "mcq_ppf_2",
            question = "She had not _____ the novel before the exam began.",
            questionUr = "'had not' کے بعد فعل 'read' کی تیسری فارم (V3) چنیں۔",
            options = listOf("reading", "read", "reads", "reader"),
            correctOptionIndex = 1,
            explanationUr = "Past Perfect میں ہمیشہ V3 لگتی ہے۔ فعل 'read' کی تیسری فارم کی ہجے 'read' ہی رہتی ہے (تلفظ 'ریڈ' ہوتا ہے)۔",
            explanationEn = "Past Perfect requires the past participle (V3); the spelling of 'read' remains 'read'."
        ),
        LessonMcq(
            id = "mcq_ppf_3",
            question = "_____ they finished the project before the deadline yesterday?",
            questionUr = "Past Perfect کے سوالیہ جملے کا آغاز کس لفظ سے ہوگا؟",
            options = listOf("Did", "Had", "Have", "Were"),
            correctOptionIndex = 1,
            explanationUr = "Past Perfect کے سوالیہ جملے تمام فاعلوں کے ساتھ 'Had' سے شروع ہوتے ہیں۔",
            explanationEn = "Past Perfect questions begin with the auxiliary verb 'Had'."
        ),
        LessonMcq(
            id = "mcq_ppf_4",
            question = "By the time the doctor arrived, the patient had _____.",
            questionUr = "'had' کے بعد فعل 'pass away' کی درست تیسری فارم چنیں۔",
            options = listOf("pass away", "passed away", "passing away", "passes away"),
            correctOptionIndex = 1,
            explanationUr = "امدادی فعل 'had' کے بعد فعل کی تیسری فارم 'passed away' آئے گی۔",
            explanationEn = "The auxiliary 'had' must be followed by the past participle 'passed away'."
        ),
        LessonMcq(
            id = "mcq_ppf_5",
            question = "I had never _____ such a breathtaking view before that trip.",
            questionUr = "'had never' کے بعد فعل 'see' کی تیسری فارم منتخب کریں۔",
            options = listOf("saw", "seen", "seeing", "sees"),
            correctOptionIndex = 1,
            explanationUr = "فعل 'see' کی تیسری فارم (V3) 'seen' ہے: 'I had never seen...'۔",
            explanationEn = "The past participle (V3) of 'see' is 'seen'."
        ),
        LessonMcq(
            id = "mcq_ppf_6",
            question = "After he _____ his prayers, he went to the market.",
            questionUr = "پہلے مکمل ہونے والے کام کے لیے درست فعل چنیں۔",
            options = listOf("offered", "had offered", "has offered", "offers"),
            correctOptionIndex = 1,
            explanationUr = "لفظ 'After' کے بعد پہلے مکمل ہونے والا عمل Past Perfect (had offered) میں آتا ہے۔",
            explanationEn = "Actions that occurred prior to another past event use Past Perfect ('had offered')."
        ),
        LessonMcq(
            id = "mcq_ppf_7",
            question = "Had she _____ to the manager before submitting her resignation?",
            questionUr = "سوالیہ جملے میں 'Had she' کے بعد فعل 'speak' کی تیسری فارم چنیں۔",
            options = listOf("spoke", "spoken", "speaking", "speaks"),
            correctOptionIndex = 1,
            explanationUr = "'speak' کی تیسری فارم 'spoken' ہے: 'Had she spoken...'۔",
            explanationEn = "The past participle of 'speak' is 'spoken'."
        ),
        LessonMcq(
            id = "mcq_ppf_8",
            question = "We were hungry because we had not _____ anything all day.",
            questionUr = "'had not' کے بعد فعل 'eat' کی تیسری فارم چنیں۔",
            options = listOf("ate", "eaten", "eating", "eats"),
            correctOptionIndex = 1,
            explanationUr = "'eat' کی تیسری فارم 'eaten' ہے: 'had not eaten'۔",
            explanationEn = "The past participle of 'eat' is 'eaten'."
        ),
        LessonMcq(
            id = "mcq_ppf_9",
            question = "Why had they _____ the match before the scheduled time?",
            questionUr = "WH-سوال میں 'had they' کے بعد فعل 'cancel' کی تیسری فارم چنیں۔",
            options = listOf("cancel", "cancelled", "cancelling", "cancels"),
            correctOptionIndex = 1,
            explanationUr = "Past Perfect میں 3rd Form 'cancelled' آئے گی۔",
            explanationEn = "Past participle 'cancelled' completes the Past Perfect question."
        ),
        LessonMcq(
            id = "mcq_ppf_10",
            question = "Identify the sentence that correctly follows the Double Past Rule:",
            questionUr = "ماضی کے دوہرے قانون (Double Past Rule) کے عین مطابق جملہ منتخب کریں:",
            options = listOf(
                "When I reached the station, the bus already left.",
                "When I reached the station, the bus had already left.",
                "When I had reached the station, the bus left.",
                "When I reached the station, the bus has left."
            ),
            correctOptionIndex = 1,
            explanationUr = "پہلے بس جا چکی تھی (had left) اور بعد میں میں پہنچا (reached)، اس لیے 'had already left' والا جملہ بالکل درست ہے۔",
            explanationEn = "The earlier event ('bus had left') must be in Past Perfect and the subsequent event ('reached') in Past Simple."
        )
    )

    val pastPerfectContinuousMcqs = listOf(
        LessonMcq(
            id = "mcq_ppfc_1",
            question = "He was tired because he had been _____ for six hours.",
            questionUr = "'had been' کے بعد فعل 'drive' کی ing فارم چنیں۔",
            options = listOf("driven", "driving", "drove", "drives"),
            correctOptionIndex = 1,
            explanationUr = "Past Perfect Continuous کی ساخت 'had been + V1-ing' ہوتی ہے، اس لیے 'driving' درست ہے۔",
            explanationEn = "Past Perfect Continuous requires 'had been + V-ing': 'had been driving'."
        ),
        LessonMcq(
            id = "mcq_ppfc_2",
            question = "They had been living in that village _____ 2010 before moving to the city.",
            questionUr = "ابتدائی سال کے لیے مناسب لفظ چنیں۔",
            options = listOf("for", "since", "from", "at"),
            correctOptionIndex = 1,
            explanationUr = "شروعاتی سال کے لیے 'since' استعمال ہوتا ہے: 'since 2010'۔",
            explanationEn = "Use 'since' with a specific starting year like 'since 2010'."
        ),
        LessonMcq(
            id = "mcq_ppfc_3",
            question = "She had been studying for three hours _____ she took a break.",
            questionUr = "دوسرے ماضی کے واقعے کے لیے مناسب ربطی لفظ چنیں۔",
            options = listOf("before", "since", "during", "while"),
            correctOptionIndex = 0,
            explanationUr = "کسی دوسرے ماضی کے کام سے پہلے تک کی مدت کو ظاہر کرنے کے لیے 'before' استعمال ہوتا ہے۔",
            explanationEn = "Use 'before' to connect the continuous past duration with the concluding event."
        ),
        LessonMcq(
            id = "mcq_ppfc_4",
            question = "Had they _____ practicing for the tournament since last month?",
            questionUr = "سوالیہ جملے میں 'Had they' کے بعد کون سا امدادی لفظ آئے گا؟",
            options = listOf("be", "being", "been", "was"),
            correctOptionIndex = 2,
            explanationUr = "Past Perfect Continuous سوالیہ جملے میں 'Had + Subject + been + V1-ing' کی ترتیب ہوتی ہے، اس لیے 'been' درست ہے۔",
            explanationEn = "In Past Perfect Continuous questions, use 'been' after the subject."
        ),
        LessonMcq(
            id = "mcq_ppfc_5",
            question = "We had not been _____ for long when the bus arrived.",
            questionUr = "'had not been' کے بعد فعل 'wait' کی درست شکل منتخب کریں۔",
            options = listOf("waited", "waiting", "waits", "wait"),
            correctOptionIndex = 1,
            explanationUr = "منفی جملے میں فعل کی 'ing' فارم 'waiting' آئے گی۔",
            explanationEn = "Use the -ing participle 'waiting' in negative continuous constructions."
        ),
        LessonMcq(
            id = "mcq_ppfc_6",
            question = "How long had he _____ working on that research paper?",
            questionUr = "WH-سوالیہ جملے میں خالی جگہ پر کریں۔",
            options = listOf("be", "being", "been", "is"),
            correctOptionIndex = 2,
            explanationUr = "سوالیہ ترتیب 'How long had he been working...' ہوتی ہے۔",
            explanationEn = "The interrogative formula is 'How long had + subject + been + V-ing'."
        ),
        LessonMcq(
            id = "mcq_ppfc_7",
            question = "The ground was wet because it had been _____ all night.",
            questionUr = "'had been' کے بعد فعل 'rain' کی ing فارم لگائیں۔",
            options = listOf("rained", "raining", "rains", "rain"),
            correctOptionIndex = 1,
            explanationUr = "رات بھر بارش ہونے کے تسلسل کو ظاہر کرنے کے لیے 'had been raining' درست ہے۔",
            explanationEn = "'Had been raining' indicates ongoing precipitation that produced the wet ground."
        ),
        LessonMcq(
            id = "mcq_ppfc_8",
            question = "They had been walking _____ four hours before finding shelter.",
            questionUr = "چار گھنٹے کی مدت کے لیے درست لفظ چنیں۔",
            options = listOf("since", "for", "during", "at"),
            correctOptionIndex = 1,
            explanationUr = "چار گھنٹے کی مدت (duration) کے لیے 'for' استعمال کیا جائے گا۔",
            explanationEn = "'Four hours' is a period of time, so use 'for'."
        ),
        LessonMcq(
            id = "mcq_ppfc_9",
            question = "I was breathless because I had been _____ fast.",
            questionUr = "'had been' کے بعد فعل 'run' کی ing والی فارم چنیں۔",
            options = listOf("run", "ran", "running", "runs"),
            correctOptionIndex = 2,
            explanationUr = "'run' کی ing فارم 'running' ہے (n کو دوہرا کیا جاتا ہے)۔",
            explanationEn = "The present participle of 'run' is 'running'."
        ),
        LessonMcq(
            id = "mcq_ppfc_10",
            question = "Choose the sentence that correctly expresses past duration up to an event:",
            questionUr = "ماضی کے واقعے تک جاری مدت کا درست جملہ منتخب کریں:",
            options = listOf(
                "He has been waiting for two hours before the doctor came.",
                "He had been waiting for two hours before the doctor came.",
                "He was waiting for two hours before the doctor had come.",
                "He is waiting for two hours before the doctor came."
            ),
            correctOptionIndex = 1,
            explanationUr = "ڈاکٹر کے آنے (ماضی) سے پہلے ۲ گھنٹے سے جاری کام کے لیے Past Perfect Continuous (had been waiting) درست ترین ہے۔",
            explanationEn = "Past duration preceding a past simple milestone requires 'had been waiting'."
        )
    )
}
