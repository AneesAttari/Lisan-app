package com.example.data.model.quizslots

import com.example.data.model.DailyQuiz365Generator.QuestionTemplate
import com.example.data.model.QuizSlotHelper
import com.example.data.model.QuizSlotHelper.SentenceVariant

object Slot8ArticlesPrepositions {
    val templates: List<QuestionTemplate> by lazy {
        val list = mutableListOf<QuestionTemplate>()
        fun v(text: String, ur: String, corr: String, dist: List<String>) = SentenceVariant(text, ur, corr, dist)

        val topics = listOf(
            Tuple4("P01_ARTICLE_DEFINITE_SPECIFIC", "Articles", "Definite Article Specific Mention", "ARTICLE:DEFINITE:THE_SPECIFIC", "the manuscript", listOf("a manuscript", "an manuscript", "manuscript"), "The professor carefully reviewed _____ submitted by Dr. Bilal.", "پروفیسر نے ڈاکٹر بلال کی جمع کرائی گئی مسودے کا بغور جائزہ لیا۔"),
            Tuple4("P02_ARTICLE_INDEFINITE_CONSONANT", "Articles", "Indefinite Article Consonant Sound", "ARTICLE:INDEFINITE:A_CONSONANT", "a university", listOf("an university", "the university", "university"), "She was accepted into _____ in the United Kingdom.", "انھیں برطانیہ کی ایک یونیورسٹی میں داخلہ مل گیا۔"),
            Tuple4("P03_ARTICLE_INDEFINITE_VOWEL_SOUND", "Articles", "Indefinite Article Vowel Sound", "ARTICLE:INDEFINITE:AN_VOWEL_SOUND", "an honest", listOf("a honest", "the honest", "honest"), "He is recognized as _____ civil officer.", "انہیں ایک ایماندار سول آفیسر کے طور پر جانا جاتا ہے۔"),
            Tuple4("P04_ARTICLE_ZERO_ABSTRACT", "Articles", "Zero Article Abstract Noun", "ARTICLE:ZERO:ABSTRACT_NOUN", "Patience is", listOf("The patience is", "A patience is", "An patience is"), "_____ a virtue required in scientific research.", "صبر سائنسی تحقیق میں مطلوب ایک خوبی ہے۔"),
            Tuple4("P05_ARTICLE_ZERO_PLURAL", "Articles", "Zero Article Plural General Category", "ARTICLE:ZERO:PLURAL_GENERAL", "Cheetahs run", listOf("The cheetahs run", "A cheetahs run", "An cheetahs run"), "_____ faster than most mammals.", "چیتے اکثر ممالیہ جانوروں سے زیادہ تیز بھاگتے ہیں۔"),
            Tuple4("P06_ARTICLE_UNIQUE_OBJECTS", "Articles", "Definite Article Unique Natural Objects", "ARTICLE:DEFINITE:THE_UNIQUE", "the sun", listOf("a sun", "an sun", "sun"), "Earth orbits _____ at an average distance of 93 million miles.", "زمین 93 ملین میل کے اوسط فاصلے پر سورج کے گرد گھومتی ہے۔"),
            Tuple4("P07_ARTICLE_GEOGRAPHICAL", "Articles", "Definite Article Mountains Oceans", "ARTICLE:DEFINITE:THE_GEO", "the Himalayas", listOf("Himalayas", "a Himalayas", "an Himalayas"), "The expedition scaled _____ in harsh winter weather.", "مہم جوئوں نے سخت سردی کے موسم میں ہمالیہ کو سر کیا۔"),
            Tuple4("P08_PREP_TIME_IN", "Prepositions", "Preposition of Time In Months Years", "PREPOSITION:TIME:IN_YEAR_MONTH", "in 2022", listOf("on 2022", "at 2022", "by 2022 in"), "The new medical laboratory was commissioned _____.", "نئی میڈیکل لیبارٹری کو 2022 میں فعال کیا گیا تھا۔"),
            Tuple4("P09_PREP_TIME_ON", "Prepositions", "Preposition of Time On Specific Days", "PREPOSITION:TIME:ON_SPECIFIC_DAY", "on Monday morning", listOf("in Monday morning", "at Monday morning", "by Monday morning"), "The briefing is scheduled _____ at 9 AM.", "برشنگ کا وقت پیر کی صبح 9 بجے طے ہے۔"),
            Tuple4("P10_PREP_TIME_AT", "Prepositions", "Preposition of Time At Clock Time", "PREPOSITION:TIME:AT_CLOCK_TIME", "at midnight", listOf("in midnight", "on midnight", "during midnight"), "The system performs an automatic data backup _____.", "سسٹم آدھی رات کو خودکار ڈیٹا بیک اپ کرتا ہے۔"),
            Tuple4("P11_PREP_MOVEMENT_INTO", "Prepositions", "Preposition Movement Into Enclosure", "PREPOSITION:MOVEMENT:INTO", "into the cleanroom", listOf("in the cleanroom", "onto the cleanroom", "at the cleanroom"), "The technicians stepped _____ wearing protective suits.", "ٹیکنیشنز حفاظتی لباس پہن کر کلین روم کے اندر داخل ہوئے۔"),
            Tuple4("P12_PREP_ADJECTIVE_INTERESTED", "Prepositions", "Dependent Preposition Interested In", "PREPOSITION:DEPENDENT_ADJ:INTERESTED_IN", "interested in", listOf("interested with", "interested on", "interested for"), "She is deeply _____ quantum computing research.", "وہ کوانٹم کمپیوٹنگ کی تحقیق میں گہری دلچسپی رکھتی ہیں۔"),
            Tuple4("P13_PREP_VERB_RELY_ON", "Prepositions", "Dependent Preposition Rely On", "PREPOSITION:DEPENDENT_VERB:RELY_ON", "rely on", listOf("rely with", "rely in", "rely at"), "Modern navies _____ satellite positioning data.", "جدید بحریہ سیٹلائٹ پوزیشننگ کے اعداد و شمار پر انحصار کرتی ہے۔"),
            Tuple4("P14_PREP_NOUN_SOLUTION_TO", "Prepositions", "Dependent Preposition Solution To", "PREPOSITION:DEPENDENT_NOUN:SOLUTION_TO", "solution to", listOf("solution of", "solution for to", "solution with"), "Engineers found an innovative _____ the structural flaw.", "انجینئروں نے ساخت کی خرابی کا ایک اختراعی حل دریافت کیا۔"),
            Tuple4("P15_QUANTIFIER_FEW_VS_AFEW", "Quantifiers", "Quantifier Few Scarcity Countable", "QUANTIFIER:COUNTABLE:FEW_SCARCITY", "few delegates", listOf("a few delegates", "little delegates", "a little delegates"), "Regrettably, _____ attended the early morning workshop.", "افسوس کی بات ہے کہ صبح سویرے کی ورکشاپ میں بہت کم مندوبین نے شرکت کی۔"),
            Tuple4("P16_QUANTIFIER_LITTLE_UNCOUNTABLE", "Quantifiers", "Quantifier Little Scarcity Uncountable", "QUANTIFIER:UNCOUNTABLE:LITTLE_SCARCITY", "little evidence", listOf("a little evidence", "few evidence", "a few evidence"), "There is _____ to support the claim.", "اس دعوے کی حمایت کے لیے بہت کم ثبوت موجود ہیں۔"),
            Tuple4("P17_QUANTIFIER_MUCH_VS_MANY", "Quantifiers", "Quantifier Much Negative Uncountable", "QUANTIFIER:UNCOUNTABLE:MUCH", "much progress", listOf("many progress", "few progress", "a few progress"), "We didn't observe _____ during the first trial.", "ہم نے پہلے ٹرائل کے دوران زیادہ پیش رفت کا مشاہدہ نہیں کیا۔"),
            Tuple4("P18_QUANTIFIER_EACH_VS_EVERY", "Quantifiers", "Quantifier Each Individual Focus", "QUANTIFIER:COUNTABLE:EACH_INDIVIDUAL", "Each applicant", listOf("Every applicants", "All applicant", "Whole applicant"), "_____ must present valid identification documents.", "ہر امیدوار کو درست شناختی دستاویزات پیش کرنا ہوں گی۔"),
            Tuple4("P19_QUANTIFIER_EITHER_NEITHER", "Quantifiers", "Quantifier Neither Singular Verb", "QUANTIFIER:BINARY:NEITHER_SINGULAR", "Neither option is", listOf("Neither option are", "Either option are", "Both option is"), "_____ suitable for our current system requirements.", "موجودہ سسٹم کے تقاضوں کے لیے دونوں میں سے کوئی بھی آپشن مناسب نہیں ہے۔"),
            Tuple4("P20_PREP_DURING_VS_WHILE", "Prepositions", "Preposition During + Noun", "PREPOSITION:DURATION:DURING_NOUN", "during the conference", listOf("while the conference", "for the conference duration", "in while conference"), "He presented two research papers _____.", "انہوں نے کانفرنس کے دوران دو تحقیقی مقالے پیش کیے۔"),
            Tuple4("P21_PREP_BY_VS_UNTIL", "Prepositions", "Preposition By Deadline", "PREPOSITION:DEADLINE:BY_TIME", "by 5 PM", listOf("until 5 PM", "till 5 PM", "on 5 PM"), "All budget proposals must be submitted _____.", "بجٹ کی تمام تجاویز شام 5 بجے تک جمع ہو جانی چاہئیں۔"),
            Tuple4("P22_PREP_BETWEEN_AMONG", "Prepositions", "Preposition Between Two Entities", "PREPOSITION:RELATION:BETWEEN_TWO", "between the two countries", listOf("among the two countries", "amidst the two countries", "with the two countries"), "The bilateral treaty was signed _____.", "دو طرفہ معاہدے پر دونوں ممالک کے درمیان دستخط ہوئے۔"),
            Tuple4("P23_PREP_BESIDE_BESIDES", "Prepositions", "Preposition Besides In Addition To", "PREPOSITION:ADDITION:BESIDES_NOUN", "Besides English", listOf("Beside English", "Excepting English", "Beside of English"), "_____, she speaks fluent German and Arabic.", "انگریزی کے علاوہ، وہ جرمن اور عربی میں روانی سے بولتی ہیں۔"),
            Tuple4("P24_PREP_DESPITE_NOUN", "Prepositions", "Preposition Despite + Noun Phrase", "PREPOSITION:CONCESSION:DESPITE_NOUN", "Despite the heavy rain", listOf("Despite of the heavy rain", "In spite the heavy rain", "Although the heavy rain"), "_____, the match continued.", "تیز بارش کے باوجود میچ جاری رہا۔"),
            Tuple4("P25_PREP_OWING_TO", "Prepositions", "Preposition Owing To Reason", "PREPOSITION:REASON:OWING_TO", "owing to technical difficulties", listOf("owing of technical difficulties", "due on technical difficulties", "because technical difficulties"), "The broadcast was delayed _____.", "تکنیکی مشکلات کی وجہ سے نشریات میں تاخیر ہوئی۔"),
            Tuple4("P26_PREP_ACCORDING_TO", "Prepositions", "Preposition According To Source", "PREPOSITION:SOURCE:ACCORDING_TO", "according to the latest survey", listOf("according with latest survey", "according by latest survey", "according latest survey"), "_____ 80% of users favor the update.", "تازہ ترین سروے کے مطابق 80 فیصد صارفین اپ ڈیٹ کی حمایت کرتے ہیں۔"),
            Tuple4("P27_PREP_IN_TERMS_OF", "Prepositions", "Complex Preposition In Terms Of", "PREPOSITION:COMPLEX:IN_TERMS_OF", "in terms of efficiency", listOf("in terms with efficiency", "in terms for efficiency", "in term of efficiency"), "The new engine outperforms the old model _____.", "نیا انجن کارکردگی کے لحاظ سے پرانے ماڈل کو پیچھے چھوڑ دیتا ہے۔"),
            Tuple4("P28_BOTH_AND_CORRELATIVE", "Correlative", "Both... And Pair", "CORRELATIVE:PAIRED:BOTH_AND", "both accuracy and speed", listOf("both accuracy or speed", "both accuracy as speed", "either accuracy and speed"), "The algorithm achieves _____.", "الگورتھم درستگی اور رفتار دونوں حاصل کرتا ہے۔"),
            Tuple4("P29_NEITHER_NOR_CORRELATIVE", "Correlative", "Neither... Nor Pair Subject Verb Agreement", "CORRELATIVE:PAIRED:NEITHER_NOR", "neither the manager nor the employees were", listOf("neither the manager nor the employees was", "neither manager or employees were", "neither the manager nor employees has"), "_____ satisfied with the decision.", "نہ مینیجر اور نہ ہی ملازمین اس فیصلے سے مطمئن تھے۔"),
            Tuple4("P30_ARTICLE_SUPERLATIVE", "Articles", "Definite Article Superlative", "ARTICLE:SUPERLATIVE:THE_MOST", "the most sophisticated", listOf("a most sophisticated", "most sophisticated", "an most sophisticated"), "This lab houses _____ spectrometer in the region.", "اس لیبارٹری میں خطے کا سب سے جدید سپیکٹرو میٹر موجود ہے۔"),
            Tuple4("P31_PREP_AMONG_THREE_OR_MORE", "Prepositions", "Preposition Among Multiple Entities", "PREPOSITION:RELATION:AMONG_MULTIPLE", "among the team members", listOf("between the team members", "amidst of team members", "between team member"), "The tasks were distributed fairly _____.", "ٹیم کے ارکان کے درمیان کام کو منصفانہ طور پر تقسیم کیا گیا۔"),
            Tuple4("P32_ARTICLE_INSTRUMENT", "Articles", "Definite Article Musical Instrument", "ARTICLE:INSTRUMENT:THE_VIOLIN", "plays the violin", listOf("plays a violin", "plays violin", "plays an violin"), "She _____ gracefully in the symphony orchestra.", "وہ سمفنی آرکسٹرا میں خوبصورتی سے وائلن بجاتی ہیں۔"),
            Tuple4("P33_PREP_IN_SPITE_OF", "Prepositions", "Preposition In Spite Of", "PREPOSITION:CONCESSION:IN_SPITE_OF", "in spite of facing severe hurdles", listOf("in spite facing severe hurdles", "in spite off facing hurdles", "despite of facing hurdles"), "They reached their goal _____.", "شدید مشکلات کا سامنا کرنے کے باوجود وہ اپنے ہدف تک پہنچ گئے۔"),
            Tuple4("P34_QUANTIFIER_A_GREAT_DEAL", "Quantifiers", "Quantifier A Great Deal Of Uncountable", "QUANTIFIER:UNCOUNTABLE:A_GREAT_DEAL", "a great deal of expertise", listOf("a great deal expertise", "a great deal of expertises", "great deal of expertise"), "The project requires _____ in civil engineering.", "اس پروجیکٹ کے لیے سول انجینئرنگ میں کافی مہارت کی ضرورت ہے۔"),
            Tuple4("P35_PREP_WITH_RESPECT_TO", "Prepositions", "Complex Preposition With Respect To", "PREPOSITION:COMPLEX:WITH_RESPECT_TO", "with respect to safety protocols", listOf("with respect of safety protocols", "with respect for safety protocols", "in respect to safety protocols"), "The facility complies fully _____.", "یہ سہولت سیکیورٹی پروٹوکول کے سلسلے میں مکمل مطابقت رکھتی ہے۔"),
            Tuple4("P36_ARTICLE_THE_SAME_AS", "Articles", "Definite Article The Same As", "ARTICLE:COMPARISON:THE_SAME_AS", "the same result as", listOf("a same result as", "same result as", "the same result than"), "The second trial yielded _____ the initial test.", "دوسرے ٹرائل نے بھی پہلے ٹیسٹ جیسا ہی نتیجہ فراہم کیا۔")
        )

        topics.forEachIndexed { idx, t ->
            val pNum = idx + 1
            list.add(QuizSlotHelper.createTemplate(
                templateId = t.v1,
                slotIndex = 8,
                grammarTopic = t.v2,
                subtopic = t.v3,
                difficulty = if (pNum > 24) "Expert" else "Intermediate",
                structuralFingerprint = t.v4,
                category = "PRESENT",
                tenseId = "articles_slot8_$pNum",
                tenseTitleEn = "${t.v2} • ${t.v3}",
                tenseTitleAr = "حروفِ جار و تنکیر",
                explanationEn = "Article or preposition structure focusing on ${t.v3}.",
                explanationUr = "اس ساخت میں حروفِ جار اور حروفِ تنکیر کے استعمال کے قواعد بیان کیے گئے ہیں۔",
                commonMistake = "حرفِ جار (Preposition) يا حرفِ تنکیر (Article) کا غلط انتخاب۔",
                variants = listOf(v(t.v7, t.v8, t.v5, t.v6))
            ))
        }

        list
    }

    private data class Tuple4<A, B, C, D, E, F, G, H>(
        val v1: A, val v2: B, val v3: C, val v4: D, val v5: E, val v6: F, val v7: G, val v8: H
    )
}
