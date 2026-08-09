package com.example.data.repository

import com.example.data.model.RevisionQuestionEntity

object QuestionBankSeeder {

    fun generateAllQuestions(): List<RevisionQuestionEntity> {
        val allQuestions = mutableListOf<RevisionQuestionEntity>()
        allQuestions.addAll(generateVerbToBeQuestions())
        allQuestions.addAll(generateArticlesQuestions())
        allQuestions.addAll(generatePresentSimpleQuestions())
        allQuestions.addAll(generatePresentContinuousQuestions())
        return allQuestions
    }

    private fun generateVerbToBeQuestions(): List<RevisionQuestionEntity> {
        val questions = mutableListOf<RevisionQuestionEntity>()
        
        val singularSubjects = listOf(
            Pair("Ahmed", "احمد"),
            Pair("Fatima", "فاطمہ"),
            Pair("The teacher", "استاد"),
            Pair("My brother", "میرا بھائی"),
            Pair("The doctor", "ڈاکٹر"),
            Pair("Zayed", "زید"),
            Pair("Aisha", "عائشہ"),
            Pair("The cat", "بلی"),
            Pair("My father", "میرے والد"),
            Pair("The child", "بچہ")
        )

        val pluralSubjects = listOf(
            Pair("The students", "طلباء"),
            Pair("My parents", "میرے والدین"),
            Pair("They", "وہ سب"),
            Pair("We", "ہم"),
            Pair("You", "آپ"),
            Pair("The doctors", "ڈاکٹروں"),
            Pair("The players", "کھلاڑی"),
            Pair("The children", "بچے"),
            Pair("Ahmed and Ali", "احمد اور علی"),
            Pair("The teachers", "اساتذہ")
        )

        val singularPredicates = listOf(
            Triple("at the clinic in Cairo", "قاہرہ کے کلینک میں", "في العيادة في القاهرة"),
            Triple("very happy today", "آج بہت خوش", "سعيدةٌ اليوم"),
            Triple("busy with work", "کام میں مصروف", "مشغولٌ بالعمل"),
            Triple("a qualified teacher", "ایک قابل استاد", "معلمٌ مؤهل"),
            Triple("at school right now", "اس وقت اسکول میں", "في المدرسة الآن"),
            Triple("from Saudi Arabia", "سعودی عرب سے", "من المملكة العربية السعودية"),
            Triple("ready for the exam", "امتحان کے لیے تیار", "مستعدٌ للامتحان"),
            Triple("kind to everyone", "سب کے ساتھ مہربان", "لطيفٌ مع الجميع"),
            Triple("tired after a long day", "طویل دن کے بعد تھکا ہوا", "تعبانٌ بعد يوم طويل"),
            Triple("at the national airport", "قومی ہوائی اڈے پر", "في المطار الوطني")
        )

        val pluralPredicates = listOf(
            Triple("in the classroom", "کلاس روم میں", "في الصف الدراسي"),
            Triple("very helpful", "بہت مددگار", "متعاونون جداً"),
            Triple("learning English grammar", "انگریزی گرامر سیکھ رہے", "يتعلمون قواعد الإنجليزية"),
            Triple("famous in the city", "شہر میں مشہور", "مشهورون في المدينة"),
            Triple("at the hospital", "ہسپتال میں", "في المستشفى"),
            Triple("from Pakistan", "پاکستان سے", "من باكستان"),
            Triple("excited about the trip", "سفر کے لیے پرجوش", "متحمسون للرحلة"),
            Triple("late for the meeting", "میٹنگ کے لیے لیٹ", "متأخرون عن الاجتماع"),
            Triple("drinking fresh water", "تازہ پانی پی رہے", "يشربون الماء العذب"),
            Triple("very friendly", "بہت ملنسار", "ودودون جداً")
        )

        // Generate 100 questions
        for (i in 1..100) {
            val qId = "rev_verb_to_be_$i"
            val topicId = "verb_to_be"
            
            val (subjectEn, subjectUr) = if (i % 2 == 0) {
                singularSubjects[(i / 2) % singularSubjects.size]
            } else {
                pluralSubjects[(i / 2) % pluralSubjects.size]
            }

            val isPlural = (i % 2 != 0)
            val isI = (!isPlural && subjectEn == "The child" && i % 4 == 0) // Treat some as 'I'
            
            val actualSubjectEn = if (isI) "I" else subjectEn
            val actualSubjectUr = if (isI) "میں" else subjectUr

            val (predicateEn, predicateUr, predicateAr) = if (isPlural) {
                pluralPredicates[(i * 3) % pluralPredicates.size]
            } else {
                singularPredicates[(i * 3) % singularPredicates.size]
            }

            val correctOption = when {
                isI -> "am"
                isPlural -> "are"
                else -> "is"
            }

            val correctIndex = (i % 3) // Deterministic correct option index (0, 1, or 2)
            val options = mutableListOf("is", "am", "are", "be")
            options.remove(correctOption)
            options.add(correctIndex, correctOption)

            val sentenceEn = "$actualSubjectEn _____ $predicateEn."
            val sentenceUr = "$actualSubjectUr _____ $predicateUr۔ (درست فعلِ ربط منتخب کریں)"
            
            val arabicMatch = when {
                isI -> "أنا $predicateAr"
                isPlural -> "هم $predicateAr"
                else -> "هو $predicateAr"
            }

            val explanationEn = "In English, every complete sentence needs a verb. Here, the subject is '$actualSubjectEn', which takes the verb to be '$correctOption'."
            val explanationUr = "انگریزی گرامر میں ہر جملے میں فعل (Verb) کا ہونا لازمی ہے۔ یہاں فاعل '$actualSubjectEn' ہے، اس لیے اس کے ساتھ فعلِ ربط '$correctOption' استعمال ہوگا۔ عربی میں اس کے متبادل اسمیہ جملہ '$arabicMatch' ہوتا ہے جس میں حال کے زمانے میں فعلِ ربط کی ضرورت نہیں ہوتی۔"
            val explanationAr = "في اللغة العربية، الجملة الاسمية لا تحتاج إلى فعل ربط في زمن المضارع (مثل: '$arabicMatch'). ولكن في اللغة الإنجليزية، يجب استخدام verb to be مناسب للفاعل."

            questions.add(
                RevisionQuestionEntity(
                    id = qId,
                    topicId = topicId,
                    subtopicId = "verb_to_be_basic",
                    difficulty = if (i > 70) "Intermediate" else "Beginner",
                    type = "fill_in_the_blank",
                    stem = sentenceEn,
                    context = sentenceUr,
                    optionsString = options.joinToString("||"),
                    correctOptionIndex = correctIndex,
                    explanationEnglish = explanationEn,
                    explanationUrdu = explanationUr,
                    explanationArabic = explanationAr,
                    tagsString = "verb_to_be,foundations,essential",
                    normalizedFingerprint = "verb_to_be_${actualSubjectEn.lowercase()}_$correctOption",
                    contentVersion = 1,
                    isActive = true
                )
            )
        }
        return questions
    }

    private fun generateArticlesQuestions(): List<RevisionQuestionEntity> {
        val questions = mutableListOf<RevisionQuestionEntity>()

        val consonantNouns = listOf(
            Triple("book", "کتاب", "كتابٌ"),
            Triple("pen", "قلم", "قلمٌ"),
            Triple("car", "گاڑی", "سيارةٌ"),
            Triple("table", "میز", "طاولةٌ"),
            Triple("house", "گھر", "بيتٌ"),
            Triple("computer", "کمپیوٹر", "حاسوبٌ"),
            Triple("phone", "فون", "هاتفٌ"),
            Triple("key", "چابی", "مفتاحٌ"),
            Triple("door", "دروازہ", "بابٌ"),
            Triple("bag", "بیگ", "حقيبةٌ")
        )

        val vowelNouns = listOf(
            Triple("apple", "سیب", "تفاحةٌ"),
            Triple("elephant", "ہاتھی", "فيلٌ"),
            Triple("orange", "مالٹا", "برتقالةٌ"),
            Triple("umbrella", "چھتری", "مظلةٌ"),
            Triple("egg", "انڈا", "بيضةٌ"),
            Triple("onion", "پیاز", "بصلةٌ"),
            Triple("engineer", "انجینئر", "مهندسٌ"),
            Triple("artist", "فنکار", "فنانٌ"),
            Triple("architect", "آرکیٹیکٹ", "مهندس معماري"),
            Triple("uncle", "چچا", "عمٌ")
        )

        val uniqueNouns = listOf(
            Triple("sun", "سورج", "الشمسُ"),
            Triple("moon", "چاند", "القمرُ"),
            Triple("sky", "آسمان", "السماءُ"),
            Triple("earth", "زمین", "الأرضُ"),
            Triple("internet", "انٹرنیٹ", "الإنترنت"),
            Triple("Quran", "قرآن", "القرآنُ"),
            Triple("Kaaba", "کعبہ", "الكعبةُ"),
            Triple("pyramids", "اہرامِ مصر", "الأهرامات"),
            Triple("world", "دنیا", "العالمُ"),
            Triple("President", "صدر", "الرئيسُ")
        )

        for (i in 1..100) {
            val qId = "rev_articles_$i"
            val topicId = "articles_a_an_the"

            val mode = i % 3 // 0: a, 1: an, 2: the
            val correctOption: String
            val stem: String
            val context: String
            val explanationEn: String
            val explanationUr: String
            val explanationAr: String
            val fingerprint: String

            when (mode) {
                0 -> {
                    val (nounEn, nounUr, nounAr) = consonantNouns[(i / 3) % consonantNouns.size]
                    correctOption = "a"
                    stem = "I have _____ $nounEn on my study desk."
                    context = "میرے مطالعے کی میز پر ایک $nounUr ہے۔ (درست آرٹیکل منتخب کریں)"
                    fingerprint = "consonant_$nounEn"
                    explanationEn = "Use 'a' before singular countable nouns that start with a consonant sound."
                    explanationUr = "انگریزی میں ایسے اسمائے نکرہ (countable nouns) جو کونسوننٹ (consonant) کی آواز سے شروع ہوں ان سے پہلے 'a' لگایا جاتا ہے۔ عربی میں اسم نکرہ کے آخر میں تنوین (ـٌ) آتی ہے، جیسے '$nounAr'۔"
                    explanationAr = "في اللغة العربية، يتم التعبير عن التنكير بالتنوين (مثل: '$nounAr'). أما في الإنجليزية، نستخدم 'a' قبل الأسماء المفردة التي تبدأ بصوت ساكن."
                }
                1 -> {
                    val (nounEn, nounUr, nounAr) = vowelNouns[(i / 3) % vowelNouns.size]
                    correctOption = "an"
                    stem = "She wants to see _____ $nounEn at the zoo."
                    context = "وہ چڑیا گھر میں ایک $nounUr دیکھنا چاہتی ہے۔ (درست آرٹیکل منتخب کریں)"
                    fingerprint = "vowel_$nounEn"
                    explanationEn = "Use 'an' before singular countable nouns that start with a vowel sound (a, e, i, o, u)."
                    explanationUr = "انگریزی میں ایسے اسمائے نکرہ جو واول (vowel) کی آواز سے شروع ہوں ان سے پہلے 'an' لگایا جاتا ہے۔ عربی میں اسم نکرہ کے آخر میں تنوین آتی ہے، جیسے '$nounAr'۔"
                    explanationAr = "في اللغة الإنجليزية، نستخدم 'an' قبل الأسماء النكرة المفردة التي تبدأ بصوت متحرك (a, e, i, o, u) لتسهيل النطق."
                }
                else -> {
                    val (nounEn, nounUr, nounAr) = uniqueNouns[(i / 3) % uniqueNouns.size]
                    correctOption = "the"
                    stem = "Look at _____ $nounEn, it is beautiful!"
                    context = "اس $nounUr کی طرف دیکھو، یہ خوبصورت ہے! (درست آرٹیکل منتخب کریں)"
                    fingerprint = "unique_$nounEn"
                    explanationEn = "Use 'the' for unique, specific things that are one of a kind."
                    explanationUr = "دنیا کی منفرد اور مخصوص چیزوں کے ساتھ ہمیشہ ڈیفینٹ آرٹیکل 'the' (معرفہ) استعمال ہوتا ہے۔ عربی میں اس کے لیے 'أل التعريف' کا استعمال ہوتا ہے، جیسے '$nounAr'۔"
                    explanationAr = "نستخدم أداة التعريف 'the' للإشارة إلى الأشياء الفريدة من نوعها أو المحددة. يقابلها في اللغة العربية 'أل التعريف' (مثل: '$nounAr')."
                }
            }

            val correctIndex = (i % 3)
            val options = mutableListOf("a", "an", "the", "some")
            options.remove(correctOption)
            options.add(correctIndex, correctOption)

            questions.add(
                RevisionQuestionEntity(
                    id = qId,
                    topicId = topicId,
                    subtopicId = "articles_essential",
                    difficulty = "Beginner",
                    type = "fill_in_the_blank",
                    stem = stem,
                    context = context,
                    optionsString = options.joinToString("||"),
                    correctOptionIndex = correctIndex,
                    explanationEnglish = explanationEn,
                    explanationUrdu = explanationUr,
                    explanationArabic = explanationAr,
                    tagsString = "articles,foundations,grammar_rules",
                    normalizedFingerprint = fingerprint,
                    contentVersion = 1,
                    isActive = true
                )
            )
        }
        return questions
    }

    private fun generatePresentSimpleQuestions(): List<RevisionQuestionEntity> {
        val questions = mutableListOf<RevisionQuestionEntity>()

        val singularVerbs = listOf(
            Triple("plays", "play", "کھیلتا ہے"),
            Triple("works", "work", "کام کرتا ہے"),
            Triple("writes", "write", "لکھتا ہے"),
            Triple("reads", "read", "پڑھتا ہے"),
            Triple("drinks", "drink", "پیتا ہے"),
            Triple("speaks", "speak", "بولتا ہے"),
            Triple("teaches", "teach", "پڑھاتا ہے"),
            Triple("runs", "run", "دوڑتا ہے"),
            Triple("likes", "like", "پسند کرتا ہے"),
            Triple("helps", "help", "مدد کرتا ہے")
        )

        val pluralVerbs = listOf(
            Triple("play", "plays", "کھیلتے ہیں"),
            Triple("work", "works", "کام کرتے ہیں"),
            Triple("write", "writes", "لکھتے ہیں"),
            Triple("read", "reads", "پڑھتے ہیں"),
            Triple("drink", "drinks", "پیتے ہیں"),
            Triple("speak", "speaks", "بولتے ہیں"),
            Triple("teach", "teaches", "پڑھاتے ہیں"),
            Triple("run", "runs", "دوڑتے ہیں"),
            Triple("like", "likes", "پسند کرتے ہیں"),
            Triple("help", "helps", "مدد کرتے ہیں")
        )

        for (i in 1..100) {
            val qId = "rev_present_simple_$i"
            val topicId = "present_simple"

            val isSingular = (i % 2 == 0)
            val correctOption: String
            val wrongOption: String
            val stem: String
            val context: String
            val explanationEn: String
            val explanationUr: String
            val explanationAr: String
            val fingerprint: String

            if (isSingular) {
                val (vS, vPl, actionUr) = singularVerbs[(i / 2) % singularVerbs.size]
                correctOption = vS
                wrongOption = vPl
                val subject = if (i % 4 == 0) "He" else "Fatima"
                val subjectUr = if (i % 4 == 0) "وہ" else "فاطمہ"
                stem = "$subject _____ tennis every Sunday morning."
                context = "$subjectUr ہر اتوار کی صبح ٹینس _____۔ (درست فعل منتخب کریں)"
                fingerprint = "singular_${subject.lowercase()}_$vS"
                explanationEn = "In Present Simple, singular subjects (He, She, It, or singular names) require the verb to end in '-s' or '-es'."
                explanationUr = "فعلِ حال سادہ (Present Simple) میں واحد غائب فاعل (जैसे He, She, Fatima) کے ساتھ فعل کے آخر میں 's' یا 'es' کا اضافہ کیا جاتا ہے۔ عربی میں اس کے متبادل علامتِ مضارع 'يـ' یا 'تـ' استعمال ہوتی ہے، جیسے 'تَلْعَبُ فَاطِمَةُ'۔"
                explanationAr = "في زمن المضارع البسيط، إذا كان الفاعل مفرداً غائباً (مثل: He, She)، نضيف حرف 's' أو 'es' لنهاية الفعل. يقابل ذلك في العربية تغيير حرف المضارعة (مثل: 'تَلْعَبُ' للمؤنث الغائب)."
            } else {
                val (vPl, vS, actionUr) = pluralVerbs[(i / 2) % pluralVerbs.size]
                correctOption = vPl
                wrongOption = vS
                val subject = if (i % 4 == 1) "They" else "The students"
                val subjectUr = if (i % 4 == 1) "وہ سب" else "طلباء"
                stem = "$subject _____ their lessons with great interest."
                context = "$subjectUr اپنے اسباق بہت دلچسپی سے _____۔ (درست فعل منتخب کریں)"
                fingerprint = "plural_${subject.lowercase().replace(" ", "_")}_$vPl"
                explanationEn = "In Present Simple, plural subjects (They, We, You, and I) take the base form of the verb without '-s'."
                explanationUr = "فعلِ حال سادہ (Present Simple) میں جمع فاعل (جیسے They, Students) کے ساتھ فعل کی پہلی شکل بغیر کسی اضافے کے استعمال ہوتی ہے۔ عربی میں فاعل جمع ہونے پر فعل بھی جمع کی علامت کے ساتھ آتا ہے، جیسے 'يَدْرُسُونَ'۔"
                explanationAr = "في المضارع البسيط، إذا كان الفاعل جمعاً (مثل: They)، نستخدم الشكل الأساسي للفعل بدون أي إضافات."
            }

            val correctIndex = (i % 3)
            val options = mutableListOf(correctOption, wrongOption, "${correctOption}ing", "will $correctOption")
            options.remove(correctOption)
            // Ensure no duplicate options
            val uniqueOptions = options.distinct().toMutableList()
            while (uniqueOptions.size < 4) {
                uniqueOptions.add("has $correctOption")
            }
            uniqueOptions.add(correctIndex, correctOption)

            questions.add(
                RevisionQuestionEntity(
                    id = qId,
                    topicId = topicId,
                    subtopicId = "present_simple_verbs",
                    difficulty = if (i > 70) "Intermediate" else "Beginner",
                    type = "fill_in_the_blank",
                    stem = stem,
                    context = context,
                    optionsString = uniqueOptions.take(4).joinToString("||"),
                    correctOptionIndex = correctIndex,
                    explanationEnglish = explanationEn,
                    explanationUrdu = explanationUr,
                    explanationArabic = explanationAr,
                    tagsString = "present_simple,tenses,verbs",
                    normalizedFingerprint = fingerprint,
                    contentVersion = 1,
                    isActive = true
                )
            )
        }
        return questions
    }

    private fun generatePresentContinuousQuestions(): List<RevisionQuestionEntity> {
        val questions = mutableListOf<RevisionQuestionEntity>()

        val actions = listOf(
            Triple("sleeping", "is sleeping", "سو رہا ہے"),
            Triple("reading", "is reading", "پڑھ رہا ہے"),
            Triple("studying", "is studying", "مطالعہ کر رہا ہے"),
            Triple("playing", "are playing", "کھیل رہے ہیں"),
            Triple("working", "are working", "کام کر رہے ہیں"),
            Triple("cooking", "is cooking", "کھانا پکا رہا ہے"),
            Triple("running", "is running", "دوڑ رہا ہے"),
            Triple("writing", "am writing", "لکھ رہا ہوں"),
            Triple("learning", "are learning", "سیکھ رہے ہیں"),
            Triple("drinking", "is drinking", "پی رہا ہے")
        )

        for (i in 1..100) {
            val qId = "rev_present_continuous_$i"
            val topicId = "present_continuous"

            val mode = i % 3 // 0: Singular (is), 1: Plural (are), 2: First Person (am)
            val stem: String
            val context: String
            val correctOption: String
            val wrongOption1: String
            val wrongOption2: String
            val wrongOption3: String
            val fingerprint: String
            val explanationUr: String
            val explanationAr: String

            val (verbIng, helperVerb, actionUr) = actions[(i / 3) % actions.size]

            when (mode) {
                0 -> {
                    val subject = if (i % 2 == 0) "Ali" else "She"
                    val subjectUr = if (i % 2 == 0) "علی" else "وہ"
                    stem = "Listen! $subject _____ a book right now."
                    context = "سنو! $subject اس وقت ایک کتاب _____۔ (درست امدادی فعل اور فعلِ جاری منتخب کریں)"
                    correctOption = "is $verbIng"
                    wrongOption1 = "are $verbIng"
                    wrongOption2 = "am $verbIng"
                    wrongOption3 = verbIng
                    fingerprint = "continuous_singular_$subject"
                    explanationUr = "فعلِ حال جاری (Present Continuous) میں واحد غائب فاعل (Ali, She) کے ساتھ 'is + verb-ing' استعمال ہوتا ہے۔ عربی میں جاری حالت کو ظاہر کرنے کے لیے فعلِ مضارع کے ساتھ 'الآنَ' (اب) استعمال کیا جاتا ہے، جیسے 'يَقْرَأُ الآنَ'۔"
                    explanationAr = "في زمن المضارع المستمر، نستخدم الفعل المساعد 'is' متبوعاً بالفعل مضافاً إليه 'ing' مع الفاعل المفرد الغائب (He, She, It)."
                }
                1 -> {
                    val subject = if (i % 2 == 0) "The children" else "We"
                    val subjectUr = if (i % 2 == 0) "بچے" else "ہم"
                    stem = "Look! $subject _____ in the beautiful park."
                    context = "دیکھو! $subject خوبصورت پارک میں _____۔ (درست امدادی فعل اور فعلِ جاری منتخب کریں)"
                    correctOption = "are $verbIng"
                    wrongOption1 = "is $verbIng"
                    wrongOption2 = "am $verbIng"
                    wrongOption3 = verbIng
                    fingerprint = "continuous_plural_${subject.replace(" ", "_")}"
                    explanationUr = "فعلِ حال جاری (Present Continuous) میں جمع فاعل (Children, We) کے ساتھ ہمیشہ 'are + verb-ing' استعمال ہوتا ہے۔ عربی میں اس کے لیے 'يَلْعَبُونَ الآنَ' جیسا اسلوب لایا جاتا ہے۔"
                    explanationAr = "نستخدم الفعل المساعد 'are' مع ضمائر الجمع والفاعل الجمع (They, We, You) للدلالة على حدث مستمر يحدث الآن."
                }
                else -> {
                    stem = "I _____ my English lessons at the moment."
                    context = "میں اس وقت اپنے انگریزی کے اسباق _____۔ (درست امدادی فعل اور فعلِ جاری منتخب کریں)"
                    correctOption = "am $verbIng"
                    wrongOption1 = "is $verbIng"
                    wrongOption2 = "are $verbIng"
                    wrongOption3 = verbIng
                    fingerprint = "continuous_i"
                    explanationUr = "انگریزی گرامر میں متکلم 'I' کے ساتھ حال جاری میں ہمیشہ 'am + verb-ing' لگایا جاتا ہے۔ عربی میں اس کے لیے 'أَدْرُسُ الآنَ' (میں اب مطالعہ کر رہا ہوں) بولا جائے گا۔"
                    explanationAr = "مع ضمير المتكلم المفرد (I)، نستخدم الفعل المساعد 'am' متبوعاً بالفعل ومعه 'ing' للتعبير عن الحدث الجاري."
                }
            }

            val correctIndex = (i % 3)
            val options = mutableListOf(wrongOption1, wrongOption2, wrongOption3)
            val uniqueOptions = options.distinct().toMutableList()
            while (uniqueOptions.size < 3) {
                uniqueOptions.add("will $verbIng")
            }
            uniqueOptions.add(correctIndex, correctOption)

            questions.add(
                RevisionQuestionEntity(
                    id = qId,
                    topicId = topicId,
                    subtopicId = "present_continuous_essential",
                    difficulty = "Intermediate",
                    type = "fill_in_the_blank",
                    stem = stem,
                    context = context,
                    optionsString = uniqueOptions.take(4).joinToString("||"),
                    correctOptionIndex = correctIndex,
                    explanationEnglish = "Present Continuous describes an action in progress right now. It is formed with 'am/is/are' + verb ending in '-ing'.",
                    explanationUrdu = explanationUr,
                    explanationArabic = explanationAr,
                    tagsString = "present_continuous,tenses,ongoing",
                    normalizedFingerprint = fingerprint,
                    contentVersion = 1,
                    isActive = true
                )
            )
        }
        return questions
    }
}
