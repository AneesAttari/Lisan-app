package com.example.data.model

object FoundationsMcqData {
    val verbToBeMcqs = listOf(
        LessonMcq(
            id = "mcq_vtb_1",
            question = "I _____ a dedicated student of English grammar.",
            questionUr = "خالی جگہ پر کریں: فاعل 'I' کے لیے درست فعلِ ربط چنیں۔",
            options = listOf("am", "is", "are", "be"),
            correctOptionIndex = 0,
            explanationUr = "فاعل 'I' (متکلم واحد) کے ساتھ حال کے زمانے میں ہمیشہ 'am' استعمال ہوتا ہے۔ جیسے عربی میں: 'أنا طالبٌ'۔",
            explanationEn = "With the first-person singular pronoun 'I', always use 'am' in the present tense."
        ),
        LessonMcq(
            id = "mcq_vtb_2",
            question = "He _____ very happy with the examination results today.",
            questionUr = "واحد غائب فاعل 'He' کے ساتھ درست فعل چنیں۔",
            options = listOf("are", "is", "am", "were"),
            correctOptionIndex = 1,
            explanationUr = "واحد غائب فاعل (He/She/It/واحد اسم) کے ساتھ حال میں 'is' استعمال ہوتا ہے۔",
            explanationEn = "Use 'is' with third-person singular subjects (He, She, It) in present tense."
        ),
        LessonMcq(
            id = "mcq_vtb_3",
            question = "They _____ ready to start the lesson now.",
            questionUr = "جمع فاعل 'They' کے ساتھ درست فعلِ ربط منتخب کریں۔",
            options = listOf("is", "am", "are", "be"),
            correctOptionIndex = 2,
            explanationUr = "جمع فاعل (They/We/You/جمع اسماء) کے ساتھ ہمیشہ 'are' استعمال کیا جاتا ہے۔",
            explanationEn = "Use 'are' with plural subject pronouns like They, We, and You in the present tense."
        ),
        LessonMcq(
            id = "mcq_vtb_4",
            question = "She _____ not afraid of difficult challenges.",
            questionUr = "منفی جملے میں فاعل 'She' کے ساتھ درست فعل چنیں۔",
            options = listOf("are", "am", "is", "be"),
            correctOptionIndex = 2,
            explanationUr = "واحد فاعل کے منفی جملے کی ساخت 'Subject + is + not' ہوتی ہے، اس لیے 'is' درست ہے۔",
            explanationEn = "The negative structure for singular subjects is 'Subject + is + not'."
        ),
        LessonMcq(
            id = "mcq_vtb_5",
            question = "_____ you ready for the final presentation?",
            questionUr = "فاعل 'you' والے سوالیہ جملے کا آغاز کس فعل سے ہوگا؟",
            options = listOf("Is", "Are", "Am", "Do"),
            correctOptionIndex = 1,
            explanationUr = "فاعل 'You' کے ساتھ سوالیہ جملہ بنانے کے لیے شروع میں 'Are' لایا جاتا ہے۔",
            explanationEn = "To form questions with the subject 'you', place 'Are' at the beginning of the sentence."
        ),
        LessonMcq(
            id = "mcq_vtb_6",
            question = "The weather _____ very pleasant this evening.",
            questionUr = "غیر شمار کنندہ واحد اسم 'The weather' کے ساتھ درست فعل منتخب کریں۔",
            options = listOf("are", "is", "am", "were"),
            correctOptionIndex = 1,
            explanationUr = "لفظ 'weather' واحد اسم ہے، اس لیے اس کے ساتھ 'is' لگے گا۔",
            explanationEn = "'Weather' is an uncountable singular noun and requires the singular verb 'is'."
        ),
        LessonMcq(
            id = "mcq_vtb_7",
            question = "Where _____ my keys? I cannot find them.",
            questionUr = "جمع اسم 'keys' کے لیے WH-سوالیہ جملے میں درست فعل منتخب کریں۔",
            options = listOf("is", "are", "am", "be"),
            correctOptionIndex = 1,
            explanationUr = "چونکہ 'keys' جمع ہے، اس لیے سوالیہ لفظ 'Where' کے بعد جمع کا فعل 'are' آئے گا۔",
            explanationEn = "Since 'keys' is plural, the question requires the plural auxiliary verb 'are'."
        ),
        LessonMcq(
            id = "mcq_vtb_8",
            question = "We _____ excited to visit the historical library.",
            questionUr = "جمع متکلم فاعل 'We' کے ساتھ درست فعل کا انتخاب کریں۔",
            options = listOf("am", "is", "are", "be"),
            correctOptionIndex = 2,
            explanationUr = "فاعل 'We' کے ساتھ حال میں 'are' استعمال ہوتا ہے: 'We are excited'۔",
            explanationEn = "With the first-person plural pronoun 'We', the correct present tense verb is 'are'."
        ),
        LessonMcq(
            id = "mcq_vtb_9",
            question = "Why _____ she absent from class today?",
            questionUr = "واحد فاعل 'she' کے ساتھ سوالیہ جملے میں مناسب فعل منتخب کریں۔",
            options = listOf("are", "am", "is", "do"),
            correctOptionIndex = 2,
            explanationUr = "واحد غائب مؤنث 'she' کے ساتھ سوالیہ جملے میں 'Why' کے فوراً بعد 'is' آئے گا۔",
            explanationEn = "In a WH-question with the singular subject 'she', use 'is' immediately after the question word."
        ),
        LessonMcq(
            id = "mcq_vtb_10",
            question = "Identify the grammatically INCORRECT sentence:",
            questionUr = "درج ذیل میں سے گرامر کی رو سے غلط جملہ پہچانیں:",
            options = listOf(
                "I am a doctor.",
                "He is my brother.",
                "They is ready.",
                "You are welcome."
            ),
            correctOptionIndex = 2,
            explanationUr = "جملہ 'They is ready' غلط ہے کیونکہ جمع فاعل 'They' کے ساتھ 'are' آنا چاہیے (They are ready)۔",
            explanationEn = "'They is ready' is incorrect because the plural pronoun 'They' must agree with 'are'."
        )
    )

    val articlesMcqs = listOf(
        LessonMcq(
            id = "mcq_art_1",
            question = "I bought _____ new laptop yesterday.",
            questionUr = "کونسونینٹ آواز والے اسم 'new laptop' کے لیے درست آرٹیکل چنیں۔",
            options = listOf("a", "an", "the", "no article"),
            correctOptionIndex = 0,
            explanationUr = "لفظ 'new' کونسونینٹ (حرف صحیح) کی آواز سے شروع ہوتا ہے، اس لیے ایک عام نئی چیز کے لیے 'a' استعمال ہوگا۔",
            explanationEn = "Use 'a' before singular countable nouns beginning with a consonant sound."
        ),
        LessonMcq(
            id = "mcq_art_2",
            question = "She gave me _____ apple from the basket.",
            questionUr = "واول (vowel) آواز والے اسم 'apple' سے پہلے درست آرٹیکل چنیں۔",
            options = listOf("a", "an", "the", "some"),
            correctOptionIndex = 1,
            explanationUr = "لفظ 'apple' واول کی آواز سے شروع ہوتا ہے، اس لیے نکرہ اسم کے لیے 'an' لگایا جاتا ہے۔",
            explanationEn = "Use 'an' before singular count nouns starting with a vowel sound (a, e, i, o, u)."
        ),
        LessonMcq(
            id = "mcq_art_3",
            question = "_____ sun rises in the east and sets in the west.",
            questionUr = "کائنات کی واحد و یکتا چیز 'sun' کے ساتھ کون سا آرٹیکل آتا ہے؟",
            options = listOf("A", "An", "The", "No article"),
            correctOptionIndex = 2,
            explanationUr = "سورج کائنات کی ایک منفرد اور یکتا چیز ہے، اس لیے اس کے ساتھ معرفہ آرٹیکل 'The' لازمی ہے۔",
            explanationEn = "Use the definite article 'the' for unique celestial and natural objects like 'the sun'."
        ),
        LessonMcq(
            id = "mcq_art_4",
            question = "He waited for half _____ hour at the bus station.",
            questionUr = "خاموش 'h' والے لفظ 'hour' سے پہلے کون سا آرٹیکل آئے گا؟",
            options = listOf("a", "an", "the", "one"),
            correctOptionIndex = 1,
            explanationUr = "لفظ 'hour' میں 'h' خاموش (silent) ہے اور تلفظ واول 'او' سے شروع ہوتا ہے، اس لیے 'an hour' درست ہے۔",
            explanationEn = "In 'hour', the initial 'h' is silent and starts with a vowel sound, so it takes 'an'."
        ),
        LessonMcq(
            id = "mcq_art_5",
            question = "He is studying at _____ university in London.",
            questionUr = "لفظ 'university' کی شروعاتی آواز کے مطابق درست آرٹیکل کا انتخاب کریں۔",
            options = listOf("an", "a", "the", "no article"),
            correctOptionIndex = 1,
            explanationUr = "لفظ 'university' کا آغاز اگرچہ حرف 'u' سے ہے مگر اس کی آواز 'یو' (consonant 'y' sound) ہے، اس لیے 'a university' درست ہے۔",
            explanationEn = "'University' begins with a consonant 'y' sound (/juː/), so it correctly takes 'a', not 'an'."
        ),
        LessonMcq(
            id = "mcq_art_6",
            question = "I saw a movie last night. _____ movie was truly inspiring.",
            questionUr = "جب کوئی اسم دوسری بار ذکر ہو کر معلوم بن جائے تو کون سا آرٹیکل لگاتے ہیں؟",
            options = listOf("A", "An", "The", "That"),
            correctOptionIndex = 2,
            explanationUr = "جب کسی اسم کا پہلی بار ذکر ہو تو نکرہ (a movie) ہوتا ہے، لیکن دوسری بار وہ معلوم اور خاص بن جاتا ہے اس لیے 'The movie' استعمال ہوتا ہے۔",
            explanationEn = "When a noun is mentioned a second time in context, it becomes definite and takes 'the'."
        ),
        LessonMcq(
            id = "mcq_art_7",
            question = "Ali is _____ honest police officer.",
            questionUr = "لفظ 'honest' کے ساتھ درست آرٹیکل کیا ہے؟",
            options = listOf("a", "an", "the", "no article"),
            correctOptionIndex = 1,
            explanationUr = "لفظ 'honest' میں 'h' سائلنٹ ہے اور تلفظ واول سے شروع ہوتا ہے، اس لیے 'an honest officer' درست ہے۔",
            explanationEn = "'Honest' has a silent 'h' and begins with a vowel sound, requiring 'an'."
        ),
        LessonMcq(
            id = "mcq_art_8",
            question = "_____ Holy Quran is the final divine book.",
            questionUr = "مقدس کتابوں کے نام سے پہلے کون سا آرٹیکل لگایا جاتا ہے؟",
            options = listOf("A", "An", "The", "No article"),
            correctOptionIndex = 2,
            explanationUr = "مقدس کتب، مشہور دریاؤں اور تاریخی عمارتوں کے ناموں سے پہلے 'The' لگانا ضروری ہے۔",
            explanationEn = "Sacred scriptures and well-known historical works take the definite article 'the'."
        ),
        LessonMcq(
            id = "mcq_art_9",
            question = "She wants to buy _____ umbrella before it starts raining.",
            questionUr = "لفظ 'umbrella' کے لیے درست آرٹیکل کا انتخاب کریں۔",
            options = listOf("a", "an", "the", "no article"),
            correctOptionIndex = 1,
            explanationUr = "لفظ 'umbrella' واول کی آواز سے شروع ہوتا ہے اس لیے 'an umbrella' درست ہے۔",
            explanationEn = "'Umbrella' begins with a short vowel sound (/ʌ/), so it takes 'an'."
        ),
        LessonMcq(
            id = "mcq_art_10",
            question = "Identify the sentence that correctly uses articles:",
            questionUr = "آرٹیکلز کے درست استعمال والا جملہ منتخب کریں:",
            options = listOf(
                "She is a European scientist.",
                "She is an European scientist.",
                "She is the European scientist with no context.",
                "She is an unique teacher."
            ),
            correctOptionIndex = 0,
            explanationUr = "لفظ 'European' کونسونینٹ کی آواز 'یو' سے بولا جاتا ہے اس لیے 'a European' بالکل درست ہے۔",
            explanationEn = "'European' begins with the consonant sound /j/, so 'a European' is grammatically correct."
        )
    )
}
