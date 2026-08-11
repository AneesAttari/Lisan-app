package com.example.data.model

object DailyQuiz365Generator {

    private val subjects = listOf(
        Pair("Ahmed", "احمد"),
        Pair("Fatima", "فاطمہ"),
        Pair("The engineer", "انجینئر"),
        Pair("My teacher", "میرے استاد"),
        Pair("Zayed", "زید"),
        Pair("The doctor", "ڈاکٹر"),
        Pair("Mariam", "مریم"),
        Pair("The students", "طلباء"),
        Pair("The pilot", "پائلٹ"),
        Pair("Our family", "ہمارا خاندان")
    )

    private val places = listOf(
        Pair("in Riyadh", "ریاض میں"),
        Pair("at Cairo University", "قاہرہ یونیورسٹی میں"),
        Pair("in Dubai", "دبئی میں"),
        Pair("in Medina", "مدینہ منورہ میں"),
        Pair("at the main library", "مرکزی لائبریری میں"),
        Pair("in Istanbul", "استنبول میں"),
        Pair("at the hospital", "ہسپتال میں"),
        Pair("in London", "لندن میں")
    )

    fun getQuestionsForDay(dayNumber: Int): List<QuizQuestion> {
        val safeDay = ((dayNumber - 1) % 365) + 1
        val questions = mutableListOf<QuizQuestion>()

        val subjIndex = (safeDay - 1) % subjects.size
        val placeIndex = (safeDay - 1) % places.size
        val (subjEn, subjUr) = subjects[subjIndex]
        val (placeEn, placeUr) = places[placeIndex]

        // Question 1: Present Simple Habit
        questions.add(
            QuizQuestion(
                id = "d${safeDay}_q1",
                tenseId = "present_simple",
                tenseTitleEn = "Day $safeDay • Present Simple",
                tenseTitleAr = "فعل حال سادہ (یوم $safeDay)",
                category = "PRESENT",
                level = "Day $safeDay",
                questionText = "$subjEn always _____ English lessons $placeEn.",
                arabicTranslation = "$subjUr ہمیشہ $placeUr انگریزی کے اسباق پڑھتا/پڑھتی ہے۔",
                options = listOf("studies", "study", "studying", "is study"),
                correctOptionIndex = 0,
                explanationEn = "With third-person singular subjects (He/She/It/$subjEn), add -es to verbs ending in consonant + y (study -> studies).",
                explanationUr = "واحد غائب فاعل ($subjEn) کے ساتھ فعل حال سادہ میں فعل کے ساتھ '-es' کا اضافہ کرتے ہیں، جیسے 'studies'۔",
                commonMistake = "واحد فاعل کے ساتھ اصل فعل بغیر اس (es) کے استعمال نہ کریں۔"
            )
        )

        // Question 2: Present Continuous Action
        questions.add(
            QuizQuestion(
                id = "d${safeDay}_q2",
                tenseId = "present_continuous",
                tenseTitleEn = "Day $safeDay • Present Continuous",
                tenseTitleAr = "فعل حال جاری (یوم $safeDay)",
                category = "PRESENT",
                level = "Day $safeDay",
                questionText = "Look! $subjEn _____ a comprehensive grammar report right now.",
                arabicTranslation = "دیکھیں! $subjUr اس وقت گرامر کی ایک مفصل رپورٹ لکھ رہا/رہی ہے۔",
                options = listOf("is writing", "writes", "was writing", "wrote"),
                correctOptionIndex = 0,
                explanationEn = "Use 'is + verb-ing' for an action happening at the moment of speaking.",
                explanationUr = "موجودہ لمحے میں جاری کام کو ظاہر کرنے کے لیے 'is + V-ing' (is writing) کا استعمال کیا جاتا ہے۔",
                commonMistake = "'Look!' جیسے اشارے کے بعد فعل حال سادہ استعمال کرنا غلط ہے۔"
            )
        )

        // Question 3: Past Simple Completed Event
        questions.add(
            QuizQuestion(
                id = "d${safeDay}_q3",
                tenseId = "past_simple",
                tenseTitleEn = "Day $safeDay • Past Simple",
                tenseTitleAr = "فعل ماضی سادہ (یوم $safeDay)",
                category = "PAST",
                level = "Day $safeDay",
                questionText = "Yesterday, $subjEn _____ a beautiful speech $placeEn.",
                arabicTranslation = "کل $subjUr نے $placeUr ایک خوبصورت تقریر پیش کی۔",
                options = listOf("delivered", "delivers", "was deliver", "delivering"),
                correctOptionIndex = 0,
                explanationEn = "Use the second form of the verb (V2) for completed actions at a specific time in the past.",
                explanationUr = "ماضی کے مکمل کام کے لیے فعل کی دوسری فارم (delivered) استعمال ہوتی ہے۔",
                commonMistake = "'Yesterday' موجود ہوتے ہوئے حال کی فارم استعمال نہ کریں۔"
            )
        )

        // Question 4: Present Perfect Experience
        questions.add(
            QuizQuestion(
                id = "d${safeDay}_q4",
                tenseId = "present_perfect",
                tenseTitleEn = "Day $safeDay • Present Perfect",
                tenseTitleAr = "فعل حال مکمل (یوم $safeDay)",
                category = "PRESENT",
                level = "Day $safeDay",
                questionText = "$subjEn _____ already completed three advanced grammar modules.",
                arabicTranslation = "$subjUr پہلے ہی گرامر کے تین اعلیٰ ماڈیولز مکمل کر چکا/چکی ہے۔",
                options = listOf("has", "have", "had", "is"),
                correctOptionIndex = 0,
                explanationEn = "Use 'has + past participle (V3)' with singular subjects for completed experiences with present relevance.",
                explanationUr = "واحد فاعل کے ساتھ فعل حال مکمل میں 'has + V3' لگاتے ہیں۔",
                commonMistake = "واحد کے ساتھ 'have' یا 'is' مت لگائیں۔"
            )
        )

        // Question 5: Future Simple Intention
        questions.add(
            QuizQuestion(
                id = "d${safeDay}_q5",
                tenseId = "future_simple",
                tenseTitleEn = "Day $safeDay • Future Simple",
                tenseTitleAr = "فعل مستقبل سادہ (یوم $safeDay)",
                category = "FUTURE",
                level = "Day $safeDay",
                questionText = "Tomorrow morning, $subjEn _____ visit the historic center $placeEn.",
                arabicTranslation = "کل صبح $subjUr $placeUr تاریخی مرکز کی زیارت کرے گا۔/گی۔",
                options = listOf("will", "is", "would", "had"),
                correctOptionIndex = 0,
                explanationEn = "Use 'will + base verb' to express future plans or predictions.",
                explanationUr = "مستقبل میں انجام پانے والے کام کے لیے 'will + base verb' استعمال ہوتا ہے۔",
                commonMistake = "'will' کے بعد فعل کی دوسری یا تیسری فارم مت لگائیں۔"
            )
        )

        // Question 6: Passive Voice Transformation
        questions.add(
            QuizQuestion(
                id = "d${safeDay}_q6",
                tenseId = "passive_voice",
                tenseTitleEn = "Day $safeDay • Active & Passive Voice",
                tenseTitleAr = "فعل معروف و مجہول (یوم $safeDay)",
                category = "PAST",
                level = "Day $safeDay",
                questionText = "The Arabic grammar handbook was written by $subjEn.",
                arabicTranslation = "عربی گرامر کی یہ گائیڈ بک $subjUr کی طرف سے لکھی گئی تھی۔",
                options = listOf("was written", "wrote", "is writing", "has wrote"),
                correctOptionIndex = 0,
                explanationEn = "In passive voice (Past Simple), use 'was/were + Past Participle (V3)'.",
                explanationUr = "فعل مجہول (Passive Voice) میں 'was/were + 3rd form' (was written) لائی جاتی ہے۔",
                commonMistake = "مجہول جملے میں فعل کی پہلی یا دوسری فارم لگانا غلط ہے۔"
            )
        )

        // Question 7: Direct & Indirect Speech
        questions.add(
            QuizQuestion(
                id = "d${safeDay}_q7",
                tenseId = "direct_indirect",
                tenseTitleEn = "Day $safeDay • Direct & Indirect Speech",
                tenseTitleAr = "روایتِ لفظی و معنوی (یوم $safeDay)",
                category = "PAST",
                level = "Day $safeDay",
                questionText = "$subjEn said that he _____ working hard for the exam.",
                arabicTranslation = "$subjUr نے کہا کہ وہ امتحان کے لیے سخت محنت کر رہا تھا۔",
                options = listOf("was", "is", "will be", "has been"),
                correctOptionIndex = 0,
                explanationEn = "When the reporting verb is in the past ('said'), present tense 'is' shifts to past tense 'was' in indirect speech.",
                explanationUr = "غیر بلاواسطہ گفتگو (Indirect Speech) میں اگر reporting verb ماضی (said) میں ہو تو 'is' بدل کر 'was' بن جاتا ہے۔",
                commonMistake = "ماضی کے بیاں (said) کے بعد 'is' کو ویسے ہی رکھنا غیر صحیح ہے۔"
            )
        )

        // Question 8: First Conditional Clause
        questions.add(
            QuizQuestion(
                id = "d${safeDay}_q8",
                tenseId = "conditionals",
                tenseTitleEn = "Day $safeDay • Conditionals",
                tenseTitleAr = "جملہ شرطیہ (یوم $safeDay)",
                category = "PRESENT",
                level = "Day $safeDay",
                questionText = "If $subjEn practices every day, he _____ master English quickly.",
                arabicTranslation = "اگر $subjUr روزانہ مشق کرے تو وہ جلد انگریزی سیکھ جائے گا۔",
                options = listOf("will", "would", "had", "did"),
                correctOptionIndex = 0,
                explanationEn = "In the First Conditional (real future condition): If + Present Simple, Main Clause + Will + Base Verb.",
                explanationUr = "پہلے شرطیہ اسلوب (First Conditional) میں شرطیہ کلاز میں Present Simple اور جزا میں 'will + base verb' آتا ہے۔",
                commonMistake = "شرط کے پہلے حصے میں ہی 'will' لگانا جائس نہیں۔"
            )
        )

        // Question 9: Articles (A, An, The)
        questions.add(
            QuizQuestion(
                id = "d${safeDay}_q9",
                tenseId = "articles",
                tenseTitleEn = "Day $safeDay • Articles",
                tenseTitleAr = "حروفِ معرفہ و تنکیر (یوم $safeDay)",
                category = "PRESENT",
                level = "Day $safeDay",
                questionText = "$subjEn gave me _____ extremely useful dictionary.",
                arabicTranslation = "$subjUr نے مجھے ایک انتہا درجہ مفید لغت تحفہ دیے۔",
                options = listOf("an", "a", "the", "some"),
                correctOptionIndex = 0,
                explanationEn = "Use 'an' before adjectives or nouns beginning with a vowel sound ('extremely' starts with /ɪ/ sound).",
                explanationUr = "ایسے صفت یا اسم جن کی شروعات وول آواز سے ہو ان سے قبل 'an' استعمال ہوتا ہے۔",
                commonMistake = "'an extremely' کی جگہ 'a extremely' لکھنا املا کی غلطی ہے۔"
            )
        )

        // Question 10: Flexible Phrasal Verb
        questions.add(
            QuizQuestion(
                id = "d${safeDay}_q10",
                tenseId = "phrasal_verbs",
                tenseTitleEn = "Day $safeDay • Flexible Phrasal Verbs",
                tenseTitleAr = "افعالِ مرکبہ (یوم $safeDay)",
                category = "PRESENT",
                level = "Day $safeDay",
                questionText = "Before starting the test, $subjEn must _____ the instructions carefully.",
                arabicTranslation = "امتحان شروع کرنے سے پہلے $subjUr کو تمام ہدایات غور سے دیکھنا/پڑھنا چاہئیں۔",
                options = listOf("look over", "look after", "look up to", "look into"),
                correctOptionIndex = 0,
                explanationEn = "'Look over' means to review or inspect documents/instructions quickly and thoroughly.",
                explanationUr = "فعلِ مرکب 'look over' کا مطلب ہے کسی دستاویز یا ہدایات کی اچھی طرح دہرائی کرنا۔",
                commonMistake = "'look after' کا مطلب دیکھ بھال کرنا ہے، ہدایات پڑھنا نہیں۔"
            )
        )

        return questions
    }
}
