package com.example.data.model

object FutureTensesMcqData {
    val futureSimpleMcqs = listOf(
        LessonMcq(
            id = "mcq_fs_1",
            question = "I _____ call you as soon as I reach the office tomorrow.",
            questionUr = "فوری فیصلے یا مستقبل کے وعدے کے لیے درست امدادی فعل چنیں۔",
            options = listOf("will", "would", "am", "have"),
            correctOptionIndex = 0,
            explanationUr = "مستقبل کے وعدے اور فیصلوں کے لیے Future Simple میں 'will' استعمال ہوتا ہے۔ جیسے عربی میں 'سـ / سوف'۔",
            explanationEn = "Use 'will' + base verb for future promises, intentions, and spontaneous decisions."
        ),
        LessonMcq(
            id = "mcq_fs_2",
            question = "She will not _____ the final examination next week.",
            questionUr = "'will not' کے بعد فعل 'take' کی درست بنیادی شکل چنیں۔",
            options = listOf("takes", "take", "taking", "took"),
            correctOptionIndex = 1,
            explanationUr = "'will' یا 'will not' کے بعد ہمیشہ فعل کی پہلی بنیادی فارم (Base form / V1) آتی ہے۔",
            explanationEn = "Modal auxiliary 'will' must always be followed by the base form of the verb (V1: 'take')."
        ),
        LessonMcq(
            id = "mcq_fs_3",
            question = "_____ you help me carry these heavy luggage bags?",
            questionUr = "مستقبل میں درخواست یا سوال کے لیے جملے کا آغاز کس لفظ سے ہوگا؟",
            options = listOf("Will", "Are", "Do", "Have"),
            correctOptionIndex = 0,
            explanationUr = "مدد کی درخواست یا مستقبل کے استفسار کے لیے 'Will you help...' کی ترتیب ہوتی ہے۔",
            explanationEn = "Use 'Will you + base verb' for polite requests and future offers."
        ),
        LessonMcq(
            id = "mcq_fs_4",
            question = "The weather forecast says it _____ rain tomorrow afternoon.",
            questionUr = "مستقبل کی پیشگوئی (Prediction) کے لیے درست فعل چنیں۔",
            options = listOf("will", "is", "was", "has"),
            correctOptionIndex = 0,
            explanationUr = "موسم کی پیشگوئی کے لیے Future Simple 'it will rain' استعمال ہوتا ہے۔",
            explanationEn = "Future predictions based on opinion or forecasts use 'will rain'."
        ),
        LessonMcq(
            id = "mcq_fs_5",
            question = "They _____ travel to Madinah next month insha'Allah.",
            questionUr = "مستقبل کے منصوبے کے لیے درست امدادی فعل منتخب کریں۔",
            options = listOf("will", "are", "have", "were"),
            correctOptionIndex = 0,
            explanationUr = "مستقبل کے مثبت جملے میں 'will' اور فعل کی پہلی فارم آتی ہے: 'They will travel'۔",
            explanationEn = "Positive future events are formed with 'will + V1': 'They will travel'."
        ),
        LessonMcq(
            id = "mcq_fs_6",
            question = "Where will you _____ during the winter holidays?",
            questionUr = "'Where will you' کے بعد فعل 'stay' کی درست فارم چنیں۔",
            options = listOf("stayed", "staying", "stay", "stays"),
            correctOptionIndex = 2,
            explanationUr = "'will' کے ساتھ سوالیہ جملے میں پہلی فارم 'stay' آئے گی۔",
            explanationEn = "In a WH-question with 'will', the verb remains in its base form 'stay'."
        ),
        LessonMcq(
            id = "mcq_fs_7",
            question = "He promises that he _____ never lie again.",
            questionUr = "وعدے کے لیے درست امدادی فعل کا انتخاب کریں۔",
            options = listOf("will", "is", "has", "does"),
            correctOptionIndex = 0,
            explanationUr = "وعدہ ظاہر کرنے کے لیے Future Simple 'will' استعمال ہوتا ہے۔",
            explanationEn = "Promises are expressed with 'will': 'he will never lie again'."
        ),
        LessonMcq(
            id = "mcq_fs_8",
            question = "The abbreviation 'won't' stands for:",
            questionUr = "مختصر لفظ 'won't' اصل میں کن دو الفاظ کا مخفف ہے؟",
            options = listOf("would not", "will not", "was not", "were not"),
            correctOptionIndex = 1,
            explanationUr = "'won't' دراصل 'will not' کی مختصر شکل (contraction) ہے۔",
            explanationEn = "'Won't' is the standard contraction for 'will not'."
        ),
        LessonMcq(
            id = "mcq_fs_9",
            question = "Why will they not _____ the invitation to the ceremony?",
            questionUr = "'will they not' کے بعد فعل 'accept' کی درست شکل چنیں۔",
            options = listOf("accepted", "accept", "accepting", "accepts"),
            correctOptionIndex = 1,
            explanationUr = "منفی سوالیہ میں 'will' کے بعد پہلی بنیادی فارم 'accept' آتی ہے۔",
            explanationEn = "Negative interrogatives with 'will' take the base verb 'accept'."
        ),
        LessonMcq(
            id = "mcq_fs_10",
            question = "Identify the grammatically INCORRECT sentence:",
            questionUr = "درج ذیل میں سے غلط جملہ منتخب کریں:",
            options = listOf(
                "I will go to the market tomorrow.",
                "She will to go to the market tomorrow.",
                "They will not attend the lecture.",
                "Will you come with us?"
            ),
            correctOptionIndex = 1,
            explanationUr = "'She will to go' غلط ہے کیونکہ 'will' کے بعد 'to' کبھی نہیں لگایا جاتا (درست: 'She will go')۔",
            explanationEn = "'She will to go' is wrong because modal verbs like 'will' are followed by bare infinitives without 'to'."
        )
    )

    val futureContinuousMcqs = listOf(
        LessonMcq(
            id = "mcq_fc_1",
            question = "At 10:00 AM tomorrow, I _____ taking my mathematics exam.",
            questionUr = "مستقبل میں جاری عمل کے لیے درست امدادی ترکیب چنیں۔",
            options = listOf("will", "will be", "am", "shall"),
            correctOptionIndex = 1,
            explanationUr = "مستقبل کے کسی مقررہ وقت پر جاری کام کو بیان کرنے کے لیے Future Continuous (will be + V1-ing) استعمال ہوتا ہے۔",
            explanationEn = "Actions in progress at a specific future moment use Future Continuous: 'will be taking'."
        ),
        LessonMcq(
            id = "mcq_fc_2",
            question = "They will not _____ traveling during the heavy storm.",
            questionUr = "منفی جملے میں 'will not' کے بعد کون سا امدادی لفظ آئے گا؟",
            options = listOf("be", "being", "been", "is"),
            correctOptionIndex = 0,
            explanationUr = "Future Continuous کی منفی ساخت 'will not + be + V1-ing' ہوتی ہے، اس لیے 'be' درست ہے۔",
            explanationEn = "Negative Future Continuous uses 'will not be + V-ing': 'will not be traveling'."
        ),
        LessonMcq(
            id = "mcq_fc_3",
            question = "What will you be _____ at this exact time tomorrow?",
            questionUr = "'will you be' کے بعد فعل 'do' کی ing شکل چنیں۔",
            options = listOf("did", "done", "doing", "does"),
            correctOptionIndex = 2,
            explanationUr = "Future Continuous کے سوالیہ جملے میں فعل کی ing والی فارم 'doing' آئے گی۔",
            explanationEn = "Future Continuous questions take the present participle: 'will you be doing'."
        ),
        LessonMcq(
            id = "mcq_fc_4",
            question = "She _____ waiting for you at the airport arrivals gate.",
            questionUr = "مستقبل کے جاری عمل کے لیے مناسب امدادی فعل منتخب کریں۔",
            options = listOf("will be", "will", "is being", "has been"),
            correctOptionIndex = 0,
            explanationUr = "تمام فاعلوں کے ساتھ Future Continuous میں 'will be' لگایا جاتا ہے۔",
            explanationEn = "Use 'will be' with all subjects in Future Continuous: 'She will be waiting'."
        ),
        LessonMcq(
            id = "mcq_fc_5",
            question = "Will he be _____ the medical conference in Dubai next week?",
            questionUr = "'Will he be' کے بعد فعل 'attend' کی درست شکل لگائیں۔",
            options = listOf("attended", "attends", "attending", "attend"),
            correctOptionIndex = 2,
            explanationUr = "'Will he be' کے بعد ing فارم 'attending' آئے گی۔",
            explanationEn = "Future Continuous interrogative requires the present participle 'attending'."
        ),
        LessonMcq(
            id = "mcq_fc_6",
            question = "Don't call him at 2 PM because he will be _____ a nap.",
            questionUr = "'will be' کے بعد فعل 'take' کی ing شکل چنیں۔",
            options = listOf("take", "taking", "taken", "took"),
            correctOptionIndex = 1,
            explanationUr = "مستقبل کے اس وقت سونے کے جاری عمل کے لیے 'will be taking' درست ہے۔",
            explanationEn = "Ongoing future activity uses 'will be taking a nap'."
        ),
        LessonMcq(
            id = "mcq_fc_7",
            question = "We will be _____ on our new project all evening.",
            questionUr = "'will be' کے بعد فعل 'work' کی ing والی شکل منتخب کریں۔",
            options = listOf("worked", "working", "works", "work"),
            correctOptionIndex = 1,
            explanationUr = "فعل کی ing فارم 'working' آئے گی: 'will be working'۔",
            explanationEn = "Use the -ing participle 'working' after 'will be'."
        ),
        LessonMcq(
            id = "mcq_fc_8",
            question = "Will they _____ staying at the hotel or at our house?",
            questionUr = "سوالیہ جملے میں 'Will they' کے بعد خالی جگہ پر کریں۔",
            options = listOf("be", "been", "being", "are"),
            correctOptionIndex = 0,
            explanationUr = "سوالیہ جملے میں 'Will + Subject + be + V1-ing' کی ترتیب ہوتی ہے، اس لیے 'be' درست ہے۔",
            explanationEn = "The interrogative structure places 'be' after the subject: 'Will they be staying'."
        ),
        LessonMcq(
            id = "mcq_fc_9",
            question = "Why will she not be _____ in the school debate?",
            questionUr = "منفی سوال میں فعل 'participate' کی درست شکل چنیں۔",
            options = listOf("participated", "participating", "participates", "participate"),
            correctOptionIndex = 1,
            explanationUr = "منفی سوال میں ing والی فارم 'participating' استعمال ہوگی۔",
            explanationEn = "The -ing participle 'participating' is required in negative questions."
        ),
        LessonMcq(
            id = "mcq_fc_10",
            question = "Identify the grammatically correct sentence:",
            questionUr = "درست گرامر کا حامل جملہ منتخب کریں:",
            options = listOf(
                "I will studying at 8 PM tonight.",
                "I will be studying at 8 PM tonight.",
                "I will been studying at 8 PM tonight.",
                "I will be study at 8 PM tonight."
            ),
            correctOptionIndex = 1,
            explanationUr = "'I will be studying at 8 PM tonight' بالکل درست ہے کیونکہ Future Continuous میں 'will be' کے بعد V1-ing آتا ہے۔",
            explanationEn = "'I will be studying at 8 PM tonight' correctly follows 'will be + V-ing'."
        )
    )

    val futurePerfectMcqs = listOf(
        LessonMcq(
            id = "mcq_fp_1",
            question = "By next Friday, I _____ finished the entire course.",
            questionUr = "مستقبل کے وقت تک مکمل ہونے والے کام کے لیے درست ساخت چنیں۔",
            options = listOf("will have", "will has", "will had", "have"),
            correctOptionIndex = 0,
            explanationUr = "مستقبل کی کسی تاریخ تک مکمل ہونے والے کام کے لیے Future Perfect (will have + V3) استعمال ہوتا ہے۔ 'will has' کبھی نہیں ہوتا۔",
            explanationEn = "Use 'will have + V3' for actions completed by a future deadline. 'Will has' does not exist."
        ),
        LessonMcq(
            id = "mcq_fp_2",
            question = "She will have _____ her degree by July 2027.",
            questionUr = "'will have' کے بعد فعل 'complete' کی تیسری فارم (V3) چنیں۔",
            options = listOf("complete", "completing", "completed", "completes"),
            correctOptionIndex = 2,
            explanationUr = "Future Perfect میں ہمیشہ فعل کی تیسری فارم (V3) 'completed' استعمال ہوتی ہے۔",
            explanationEn = "Future Perfect requires the past participle (V3: 'completed')."
        ),
        LessonMcq(
            id = "mcq_fp_3",
            question = "They will not have _____ the bridge by the end of this year.",
            questionUr = "'will not have' کے بعد فعل 'build' کی تیسری فارم چنیں۔",
            options = listOf("built", "build", "building", "builds"),
            correctOptionIndex = 0,
            explanationUr = "فعل 'build' کی تیسری فارم 'built' ہے: 'will not have built'۔",
            explanationEn = "The past participle (V3) of 'build' is 'built'."
        ),
        LessonMcq(
            id = "mcq_fp_4",
            question = "_____ you have arrived in Karachi by 8:00 PM tonight?",
            questionUr = "Future Perfect کے سوالیہ جملے کا آغاز کس لفظ سے ہوگا؟",
            options = listOf("Will", "Have", "Are", "Did"),
            correctOptionIndex = 0,
            explanationUr = "Future Perfect کا سوالیہ جملہ 'Will + Subject + have + V3' سے بنتا ہے، اس لیے 'Will' سے شروع ہوگا۔",
            explanationEn = "Future Perfect questions start with 'Will': 'Will you have arrived?'."
        ),
        LessonMcq(
            id = "mcq_fp_5",
            question = "The keyword 'by the time...' most frequently indicates which tense?",
            questionUr = "الفاظ 'by the time / by next year' عام طور پر کس زمانے کی علامت ہیں؟",
            options = listOf("Present Continuous", "Future Perfect", "Past Simple", "Present Simple"),
            correctOptionIndex = 1,
            explanationUr = "'by' کے ساتھ مستقبل کے وقت کا ذکر Future Perfect Tense کی واضح علامت ہے۔",
            explanationEn = "Time phrases with 'by' (e.g., 'by next year') typically signal Future Perfect."
        ),
        LessonMcq(
            id = "mcq_fp_6",
            question = "He will have _____ all his debts before retiring.",
            questionUr = "'will have' کے بعد فعل 'pay' کی تیسری فارم (V3) منتخب کریں۔",
            options = listOf("paid", "payed", "paying", "pays"),
            correctOptionIndex = 0,
            explanationUr = "فعل 'pay' کی تیسری فارم 'paid' ہے: 'will have paid'۔",
            explanationEn = "The past participle of 'pay' is 'paid'."
        ),
        LessonMcq(
            id = "mcq_fp_7",
            question = "By the time the guests arrive, we will have _____ dinner.",
            questionUr = "'will have' کے بعد فعل 'cook' کی تیسری فارم چنیں۔",
            options = listOf("cooked", "cook", "cooking", "cooks"),
            correctOptionIndex = 0,
            explanationUr = "فعل کی تیسری فارم 'cooked' آئے گی: 'will have cooked dinner'۔",
            explanationEn = "Use past participle 'cooked' in Future Perfect."
        ),
        LessonMcq(
            id = "mcq_fp_8",
            question = "Will she _____ left the office by 6:00 PM?",
            questionUr = "سوالیہ جملے میں 'Will she' کے بعد کون سا امدادی فعل آئے گا؟",
            options = listOf("has", "have", "had", "be"),
            correctOptionIndex = 1,
            explanationUr = "تمام فاعلوں (چاہے He ہو یا She) کے ساتھ 'will' کے بعد ہمیشہ 'have' ہی آتا ہے، 'has' کبھی نہیں لگتا۔",
            explanationEn = "Always use 'have' after modal 'will', regardless of subject: 'Will she have left?'."
        ),
        LessonMcq(
            id = "mcq_fp_9",
            question = "What will they have _____ by the end of their scientific mission?",
            questionUr = "'will they have' کے بعد فعل 'discover' کی تیسری فارم چنیں۔",
            options = listOf("discovered", "discover", "discovering", "discovers"),
            correctOptionIndex = 0,
            explanationUr = "Future Perfect میں 3rd form 'discovered' آئے گی۔",
            explanationEn = "Past participle 'discovered' completes the question."
        ),
        LessonMcq(
            id = "mcq_fp_10",
            question = "Identify the INCORRECT sentence regarding Future Perfect:",
            questionUr = "Future Perfect کے غلط استعمال والا جملہ پہچانیں:",
            options = listOf(
                "He will have finished his work by noon.",
                "He will has finished his work by noon.",
                "She will have reached Lahore by tomorrow.",
                "They will have sold the house by next month."
            ),
            correctOptionIndex = 1,
            explanationUr = "'He will has' غلط ہے کیونکہ 'will' کے بعد کبھی 'has' نہیں آتا، ہمیشہ 'will have' ہی درست ہے۔",
            explanationEn = "'He will has' is grammatically incorrect; 'will' is always followed by base form 'have'."
        )
    )

    val futurePerfectContinuousMcqs = listOf(
        LessonMcq(
            id = "mcq_fpc_1",
            question = "By next month, I will have been working here _____ five years.",
            questionUr = "مستقبل کی پانچ سالہ مدت کے لیے درست حرفِ ربط چنیں۔",
            options = listOf("for", "since", "during", "from"),
            correctOptionIndex = 0,
            explanationUr = "مدتِ وقت (duration) کو ظاہر کرنے کے لیے 'for' استعمال ہوتا ہے: 'for five years'۔",
            explanationEn = "Use 'for' with duration of time: 'for five years'."
        ),
        LessonMcq(
            id = "mcq_fpc_2",
            question = "By December, she will have _____ living in this house for a decade.",
            questionUr = "'will have' کے بعد Future Perfect Continuous کا امدادی لفظ چنیں۔",
            options = listOf("be", "been", "being", "was"),
            correctOptionIndex = 1,
            explanationUr = "Future Perfect Continuous کی ساخت 'will have been + V1-ing' ہوتی ہے، اس لیے 'been' درست ہے۔",
            explanationEn = "The formula requires 'will have been + V-ing'."
        ),
        LessonMcq(
            id = "mcq_fpc_3",
            question = "By 8 PM, they will have been _____ for four hours.",
            questionUr = "'will have been' کے بعد فعل 'study' کی ing فارم لگائیں۔",
            options = listOf("studied", "studying", "studies", "study"),
            correctOptionIndex = 1,
            explanationUr = "فعل کی پہلی فارم کے ساتھ ing کا اضافہ ہوگا: 'studying'۔",
            explanationEn = "Present participle 'studying' follows 'will have been'."
        ),
        LessonMcq(
            id = "mcq_fpc_4",
            question = "Will you have been _____ English for two years by next summer?",
            questionUr = "سوالیہ جملے میں فعل 'learn' کی درست شکل منتخب کریں۔",
            options = listOf("learned", "learning", "learns", "learn"),
            correctOptionIndex = 1,
            explanationUr = "Future Perfect Continuous سوالیہ میں 'learning' آئے گا۔",
            explanationEn = "The continuous aspect uses the -ing participle 'learning'."
        ),
        LessonMcq(
            id = "mcq_fpc_5",
            question = "She will not have been _____ long before the supervisor arrives.",
            questionUr = "منفی جملے میں فعل 'wait' کی درست شکل چنیں۔",
            options = listOf("waited", "waiting", "waits", "wait"),
            correctOptionIndex = 1,
            explanationUr = "منفی جملے میں ing فارم 'waiting' استعمال ہوگی۔",
            explanationEn = "Negative construction uses 'waiting'."
        ),
        LessonMcq(
            id = "mcq_fpc_6",
            question = "How long will he have been _____ by the time he retires?",
            questionUr = "WH-سوال میں فعل 'teach' کی درست شکل منتخب کریں۔",
            options = listOf("taught", "teaching", "teaches", "teach"),
            correctOptionIndex = 1,
            explanationUr = "'How long will he have been teaching...' کی ترتیب درست ہے۔",
            explanationEn = "The question uses 'teaching'."
        ),
        LessonMcq(
            id = "mcq_fpc_7",
            question = "By 2030, we will have been living in this city _____ 2010.",
            questionUr = "مخصوص ابتدائی سال کے لیے مناسب لفظ چنیں۔",
            options = listOf("for", "since", "in", "from"),
            correctOptionIndex = 1,
            explanationUr = "ابتدائی سال (point of time) کے لیے 'since 2010' درست ہے۔",
            explanationEn = "Starting point in time takes 'since 2010'."
        ),
        LessonMcq(
            id = "mcq_fpc_8",
            question = "The full helping verb string for Future Perfect Continuous is:",
            questionUr = "Future Perfect Continuous کے امدادی افعال کا درست مجموعہ کیا ہے؟",
            options = listOf("will been have", "will have been", "will be having", "shall had been"),
            correctOptionIndex = 1,
            explanationUr = "درست ترتیب ہمیشہ 'will + have + been' ہوتی ہے۔",
            explanationEn = "The correct auxiliary order is 'will have been'."
        ),
        LessonMcq(
            id = "mcq_fpc_9",
            question = "By 5 PM, the players will have been _____ in the sun for three hours.",
            questionUr = "'will have been' کے بعد فعل 'run' کی ing فارم چنیں۔",
            options = listOf("ran", "run", "running", "runs"),
            correctOptionIndex = 2,
            explanationUr = "'run' کی ing والی شکل 'running' ہے: 'will have been running'۔",
            explanationEn = "Present participle is 'running'."
        ),
        LessonMcq(
            id = "mcq_fpc_10",
            question = "Identify the grammatically correct sentence:",
            questionUr = "درست گرامر کا جملہ منتخب کریں:",
            options = listOf(
                "By next year, I will have been working here for 2 years.",
                "By next year, I will has been working here for 2 years.",
                "By next year, I will have being working here for 2 years.",
                "By next year, I will had been working here for 2 years."
            ),
            correctOptionIndex = 0,
            explanationUr = "'will have been working' گرامر کے تمام اصولوں کے عین مطابق ہے۔",
            explanationEn = "'I will have been working here for 2 years' is the only grammatically correct option."
        )
    )
}
