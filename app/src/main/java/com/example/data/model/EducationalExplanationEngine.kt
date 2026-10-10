package com.example.data.model

/**
 * EducationalExplanationEngine
 *
 * Implements authoritative, deep, option-specific educational feedback for English grammar MCQs.
 * Follows the mandatory structure:
 * - WHY: Option-specific analysis (why correct, or the precise failure for distractors)
 * - RULE: Authoritative grammar rule
 * - HOW TO SOLVE: Step-by-step repeatable method matching the grammatical concept
 * - EXAMPLE: Clear illustrative sentence
 * - COMMON MISTAKE: Frequent pitfall to avoid
 * - ARABIC COMPARISON: Contextual comparison with classical Arabic grammar
 * - NON-REVEALING HINT: Guiding prompt that stimulates active thinking without giving away the answer (AGENTS.md compliance).
 */
object EducationalExplanationEngine {

    enum class ConceptPillar {
        TENSES,
        MODALS,
        PASSIVE_VOICE,
        CONDITIONALS,
        REPORTED_SPEECH,
        GERUNDS_INFINITIVES,
        ARTICLES_QUANTIFIERS,
        PREPOSITIONS,
        SUBJECT_VERB_AGREEMENT,
        CONNECTORS_SYNTAX,
        GENERAL
    }

    /**
     * Determines the concept pillar for a question based on its category, tenseId, and text.
     */
    fun detectConceptPillar(question: QuizQuestion): ConceptPillar {
        val lowerId = (question.tenseId + "_" + question.category + "_" + question.questionText).lowercase()
        return when {
            lowerId.contains("modal") || lowerId.contains("auxiliary") || lowerId.contains("should") || lowerId.contains("must") || lowerId.contains("could") -> ConceptPillar.MODALS
            lowerId.contains("voice") || lowerId.contains("passive") || lowerId.contains("by the") -> ConceptPillar.PASSIVE_VOICE
            lowerId.contains("conditional") || lowerId.contains("if ") || lowerId.contains("hypothe") || lowerId.contains("unless") -> ConceptPillar.CONDITIONALS
            lowerId.contains("reported") || lowerId.contains("indirect") || lowerId.contains("said that") || lowerId.contains("asked") -> ConceptPillar.REPORTED_SPEECH
            lowerId.contains("gerund") || lowerId.contains("infinitive") || lowerId.contains("participle") || lowerId.contains("ing") -> ConceptPillar.GERUNDS_INFINITIVES
            lowerId.contains("article") || lowerId.contains("quantifier") || lowerId.contains("a_an") || lowerId.contains("determiner") -> ConceptPillar.ARTICLES_QUANTIFIERS
            lowerId.contains("preposition") || lowerId.contains("phrasal") || lowerId.contains("in_on_at") -> ConceptPillar.PREPOSITIONS
            lowerId.contains("agreement") || lowerId.contains("pronoun") || lowerId.contains("subject") || lowerId.contains("relative") || lowerId.contains("who_which") -> ConceptPillar.SUBJECT_VERB_AGREEMENT
            lowerId.contains("connector") || lowerId.contains("conjunction") || lowerId.contains("transformation") || lowerId.contains("inversion") || lowerId.contains("although") -> ConceptPillar.CONNECTORS_SYNTAX
            lowerId.contains("tense") || lowerId.contains("present") || lowerId.contains("past") || lowerId.contains("future") || lowerId.contains("perfect") || lowerId.contains("continuous") -> ConceptPillar.TENSES
            else -> ConceptPillar.GENERAL
        }
    }

    /**
     * Validates that a QuizQuestion meets all structural requirements before presentation.
     */
    fun validateQuestion(question: QuizQuestion): Boolean {
        if (question.options.size != 4) return false
        if (question.correctOptionIndex !in 0..3) return false
        if (question.questionText.isBlank()) return false
        if (question.options.any { it.isBlank() }) return false
        return true
    }

    /**
     * Generates a step-by-step repeatable solving method tailored to the grammatical pillar.
     */
    fun getConceptSolvingMethod(question: QuizQuestion): String {
        if (question.howToSolveUr.isNotBlank()) return question.howToSolveUr

        return when (detectConceptPillar(question)) {
            ConceptPillar.TENSES ->
                """
                1. جملے میں وقت ظاہر کرنے والے اشارے (Time markers جیسے yesterday, already, tomorrow, currently) تلاش کریں۔
                2. فاعل (Subject) کی تعداد (واحد یا جمع) اور شخص (1st, 2nd, 3rd person) کی شناخت کریں۔
                3. متعلقہ زمانے کے امدادی فعل (Auxiliary verb) کا درست انتخاب کریں۔
                4. اصل فعل کی ساخت (Base form, V2, V3 یا V+ing) کی مکمل مطابقت کی تصدیق کریں۔
                """.trimIndent()

            ConceptPillar.MODALS ->
                """
                1. جملے کے بنیادی مفہوم کا تعین کریں (کیا یہ قابلیت، اجازت، فرض، قیاس یا مشورہ ہے؟)۔
                2. اس مفہوم کے مطابق موزوں ترین موڈل ورب (Modal Auxiliary) منتخب کریں۔
                3. تصدیق کریں کہ موڈل کے فوراً بعد فعل کی پہلی بنیادی حالت (Bare Infinitive - بغیر 'to' اور بغیر '-s/-ed/-ing') استعمال ہوئی ہو۔
                """.trimIndent()

            ConceptPillar.PASSIVE_VOICE ->
                """
                1. دیکھیں کہ جملے کا فاعل (Subject) کام کرنے والا (Agent) ہے یا کام وصول کرنے والا (Patient/Receiver)۔
                2. اگر کام وصول کرنے والے پر زور ہے تو زمانے کے مطابق امدادی فعل 'to be' (is, are, was, were, been, being) کا انتخاب کریں۔
                3. امدادی فعل کے بعد ہمیشہ لازمی طور پر اصل فعل کی تیسری حالت (Past Participle - V3) لگائیں۔
                """.trimIndent()

            ConceptPillar.CONDITIONALS ->
                """
                1. سب سے پہلے 'If' کلاز (شرطیہ فقرے) میں استعمال شدہ زمانے اور فعل کی حالت کا مشاہدہ کریں۔
                2. شرط کی قسم متعین کریں: Zero (سائنسی سچائی)، First (ممکنہ مستقبل)، Second (فرضی حال)، Third (ماضی کا پچھتاوا)۔
                3. مین کلاز کے فارمولے کو جوڑیں:
                   • Type 1: If + Present Simple ➔ will + Base Verb
                   • Type 2: If + Past Simple ➔ would + Base Verb
                   • Type 3: If + Past Perfect (had + V3) ➔ would have + V3.
                """.trimIndent()

            ConceptPillar.REPORTED_SPEECH ->
                """
                1. رپورٹنگ ورب (Reporting verb جیسے said, asked, explained) کا زمانہ دیکھیں۔
                2. اگر رپورٹنگ ورب ماضی میں ہو تو بالواسطہ بات کے فعل کو ایک درجہ ماضی میں (Tense Backshift) تبدیل کریں۔
                3. ضمائر (Pronouns) اور وقت و مکان کے اشاروں (now ➔ then, tomorrow ➔ next day) کو نئے بولنے والے کے مطابق ایڈجسٹ کریں۔
                """.trimIndent()

            ConceptPillar.GERUNDS_INFINITIVES ->
                """
                1. خالی جگہ سے پہلے آنے والے اہم فعل، صفت یا حرفِ جار (Preposition) کو دیکھیں۔
                2. قواعد یاد رکھیں: تمام Prepositions کے بعد لازمی Gerund (V+ing) آتا ہے۔
                3. مخصوص افعال کے پیٹرن کی جانچ کریں (کچھ افعال صرف Gerund لیتے ہیں جیسے enjoy, avoid؛ اور کچھ صرف Infinitive لیتے ہیں جیسے decide, plan, hope)۔
                """.trimIndent()

            ConceptPillar.ARTICLES_QUANTIFIERS ->
                """
                1. اسم (Noun) کی نوعیت دیکھیں: کیا وہ قابلِ شمار (Countable) ہے یا ناقابلِ شمار (Uncountable)؟
                2. دیکھیں کہ کیا یہ اسم سامع کے لیے معلوم و مخصوص (Specific/Definite) ہے یا پہلی بار ذکر ہو رہا ہے (General/Indefinite)۔
                3. اگر نکرہ اور مفرد ہو تو صوتی آواز (Vowel/Consonant sound) کے مطابق 'a' یا 'an' چنیں؛ اگر مخصوص ہو تو 'the' لگائیں۔
                """.trimIndent()

            ConceptPillar.PREPOSITIONS ->
                """
                1. جملے میں بنیادی تعلق کی نوعیت پہچانیں (وقت Time، مقام Place، سمت Direction یا مخصوص لفظی جوڑ Collocation)۔
                2. سیاق کے مطابق صحیح Preposition منتخب کریں (مثلاً: مخصوص تاریخ کے لیے 'on'، گھڑی کے وقت کے لیے 'at'، مہینوں و سالوں کے لیے 'in')۔
                3. مکمل فقرے کو روانی کے ساتھ پڑھ کر تسلی کریں کہ محاوراتی جوڑ بالکل درست ہے۔
                """.trimIndent()

            ConceptPillar.SUBJECT_VERB_AGREEMENT ->
                """
                1. جملے کا اصل بنیادی فاعل (Head Noun) تلاش کریں؛ درمیانی وضاحتی الفاظ (جیسے 'of the students') کو نظر انداز کریں۔
                2. فاعل کا واحد (Singular) یا جمع (Plural) ہونا طے کریں۔
                3. واحد فاعل کے ساتھ فعل کی واحد حالت (Present میں -s/-es) اور جمع فاعل کے ساتھ جمع فعل کا انتخاب کریں۔
                """.trimIndent()

            ConceptPillar.CONNECTORS_SYNTAX ->
                """
                1. دونوں ذیلی جملوں کے درمیان منطقی تعلق کو سمجھیں (تضاد Contrast، وجہ Reason، نتیجہ Result یا شرط Condition)۔
                2. رابطہ پیدا کرنے والے لفظ کی گرامری حیثیت دیکھیں (کیا اس کے بعد مکمل کلاز آئے گی یا صرف اسم/فریج)۔
                3. رموزِ اوقاف اور معنی کے لحاظ سے درست کنیکٹر (Connector) منتخب کریں۔
                """.trimIndent()

            ConceptPillar.GENERAL ->
                """
                1. جملے کے تمام حصوں (فاعل، فعل، مفعول اور وقت کے اشارے) کا باریک بینی سے جائزہ لیں۔
                2. چاروں آپشنز کا موازنہ کریں اور واضح گرامری غلطیوں والے آپشنز کو خارج (Eliminate) کریں۔
                3. بچے ہوئے درست آپشن کو جملے کے سیاق و سباق میں رکھ کر قواعد کی رو سے جانچیں۔
                """.trimIndent()
        }
    }

    /**
     * Generates or returns the core grammatical rule.
     */
    fun getGrammarRule(question: QuizQuestion): String {
        if (question.ruleUr.isNotBlank()) return question.ruleUr

        val correctText = question.options.getOrElse(question.correctOptionIndex) { "" }
        val pillar = detectConceptPillar(question)

        return when (pillar) {
            ConceptPillar.TENSES ->
                "قاعدہ: جملے کے زمانے اور فاعل کے مطابق فعل کی درست حالت درکار ہے۔ یہاں درست جواب \"$correctText\" ہے جو فاعل اور جملے کے سیاق کے عین مطابق ہے۔"
            ConceptPillar.MODALS ->
                "قاعدہ: موڈل افعال (Modal Auxiliaries) کے بعد ہمیشہ فعل کی پہلی بنیادی حالت (Bare Infinitive) بغیر کسی اضافے کے استعمال ہوتی ہے۔"
            ConceptPillar.PASSIVE_VOICE ->
                "قاعدہ: مجہول جملوں (Passive Voice) میں فاعل پر کام واقع ہوتا ہے اور ساخت ہمیشہ 'Auxiliary be + Past Participle (V3)' ہوتی ہے۔"
            ConceptPillar.CONDITIONALS ->
                "قاعدہ: شرطیہ جملوں میں 'If' کلاز اور مین کلاز کے درمیان زمانے کی باقاعدہ مطابقت لازمی ہے۔"
            ConceptPillar.REPORTED_SPEECH ->
                "قاعدہ: جب رپورٹنگ ورب ماضی میں ہو تو بالواسطہ گفتگو میں فعل کے زمانے کو ماضی کے متوازی درجے پر منتقل کیا جاتا ہے۔"
            ConceptPillar.GERUNDS_INFINITIVES ->
                "قاعدہ: انگریزی میں بعض افعال کے بعد اسم مصدر (Gerund - V+ing) اور بعض کے بعد (Infinitive - to + V) کا استعمال لازمی ہوتا ہے۔"
            ConceptPillar.ARTICLES_QUANTIFIERS ->
                "قاعدہ: معرفہ اسم کے لیے 'the'، نکرہ مفرد اسم کے لیے آواز کے اعتبار سے 'a/an'، اور مقدار کے لیے درست Quantifier ضروری ہے۔"
            ConceptPillar.PREPOSITIONS ->
                "قاعدہ: انگریزی میں افعال اور اسمائے صفت کے ساتھ مخصوص حروفِ جار (Fixed Prepositions) کا استعمال محاوراتی اصولوں کے تحت ہوتا ہے۔"
            ConceptPillar.SUBJECT_VERB_AGREEMENT ->
                "قاعدہ: فعل ہمیشہ اپنے اصل فاعل کے ساتھ تعداد (واحد/جمع) اور شخص میں مکمل مطابقت رکھتا ہے۔"
            ConceptPillar.CONNECTORS_SYNTAX ->
                "قاعدہ: جملوں کو باہم جوڑنے والے روابط (Conjunctions/Connectors) معنی کے ربط اور نحوی ساخت کے لحاظ سے متعین ہوتے ہیں۔"
            ConceptPillar.GENERAL ->
                "قاعدہ: جملے میں فاعل، فعل اور نحوی ترتیب کی باہمی درست مطابقت لازمی ہے۔"
        }
    }

    /**
     * Generates rich, option-specific feedback for each of the 4 options.
     */
    fun getOptionSpecificFeedback(question: QuizQuestion, optionIndex: Int): String {
        // If explicit custom option explanations exist, use them
        if (question.optionExplanations.size > optionIndex && question.optionExplanations[optionIndex].isNotBlank()) {
            return question.optionExplanations[optionIndex]
        }

        val optionText = question.options.getOrElse(optionIndex) { "" }
        val isCorrect = optionIndex == question.correctOptionIndex
        val correctText = question.options.getOrElse(question.correctOptionIndex) { "" }
        val pillar = detectConceptPillar(question)

        if (isCorrect) {
            return when (pillar) {
                ConceptPillar.TENSES ->
                    "✓ درست انتخاب: \"$optionText\" فاعل اور جملے کے وقتی سیاق (Tense & Aspect) کی تمام ضروریات کو گرامر کے مطابق مکمل کرتا ہے۔"
                ConceptPillar.MODALS ->
                    "✓ درست انتخاب: \"$optionText\" موڈل ورب کے قواعد کے عین مطابق درست بنیادی ساخت (Base form) فراہم کرتا ہے۔"
                ConceptPillar.PASSIVE_VOICE ->
                    "✓ درست انتخاب: \"$optionText\" مجہول ساخت (Passive Voice) کے لیے مطلوبہ 'be + V3' کو درست طور پر تشکیل دیتا ہے۔"
                ConceptPillar.CONDITIONALS ->
                    "✓ درست انتخاب: \"$optionText\" شرطیہ کلاز (Conditional Clause) کے فارمولے کے مطابق جوابِ شرط کو درست شکل دیتا ہے۔"
                ConceptPillar.REPORTED_SPEECH ->
                    "✓ درست انتخاب: \"$optionText\" بالواسطہ کلام (Indirect Speech) کے Tense Backshift اصول کو درست طور پر نافذ کرتا ہے۔"
                ConceptPillar.GERUNDS_INFINITIVES ->
                    "✓ درست انتخاب: \"$optionText\" متعلقہ فعل/حرفِ جار کے بعد مطلوبہ ساخت (Gerund/Infinitive) کے بالکل موافق ہے۔"
                ConceptPillar.ARTICLES_QUANTIFIERS ->
                    "✓ درست انتخاب: \"$optionText\" اسم کی معین/غیر معین کیفیت اور صوتی اصولوں کے عین مطابق ہے۔"
                ConceptPillar.PREPOSITIONS ->
                    "✓ درست انتخاب: \"$optionText\" اس فقرے اور سیاق کے لیے درست محاوراتی حرفِ جار (Preposition) ہے۔"
                ConceptPillar.SUBJECT_VERB_AGREEMENT ->
                    "✓ درست انتخاب: \"$optionText\" فاعل کی تعداد (Singular/Plural) اور صیغے کے ساتھ مکمل ہم آہنگ ہے۔"
                ConceptPillar.CONNECTORS_SYNTAX ->
                    "✓ درست انتخاب: \"$optionText\" دونوں کلازز کے درمیان منطقی ربط اور نحوی اصول کو درست بناتا ہے۔"
                ConceptPillar.GENERAL ->
                    "✓ درست انتخاب: \"$optionText\" جملے کی درست نحوی اور گرامری ضرورت کو پورا کرتا ہے۔"
            }
        }

        // Option is a distractor -> derive exact reason why this specific option fails
        val lowerOption = optionText.lowercase().trim()
        val lowerCorrect = correctText.lowercase().trim()

        return when {
            pillar == ConceptPillar.TENSES && (lowerOption.endsWith("ed") || lowerOption.contains("was") || lowerOption.contains("had")) && (lowerCorrect.contains("is") || lowerCorrect.endsWith("s")) ->
                "✗ غلط آپشن: \"$optionText\" ماضی (Past) کا صیغہ ہے جبکہ جملے کا سیاق حال (Present) کے فعل کا تقاضا کر رہا ہے۔"

            pillar == ConceptPillar.TENSES && (lowerOption.endsWith("s") || lowerOption.endsWith("es")) && !lowerCorrect.endsWith("s") ->
                "✗ غلط آپشن: \"$optionText\" میں واحد فاعل کا اضافہ (-s/-es) موجود ہے جو یہاں فاعل کی مطابقت کے لحاظ سے غلط ہے۔"

            pillar == ConceptPillar.MODALS && (lowerOption.startsWith("to ") || lowerOption.endsWith("ing") || lowerOption.endsWith("ed")) ->
                "✗ غلط آپشن: \"$optionText\" موڈل ورب کے بعد نہیں آ سکتا کیونکہ موڈل کے بعد ہمیشہ بغیر 'to' کے بنیادی فعل (Bare Infinitive) آتا ہے۔"

            pillar == ConceptPillar.PASSIVE_VOICE && !lowerOption.contains("been") && !lowerOption.contains("being") && !lowerOption.endsWith("ed") && !lowerOption.endsWith("en") ->
                "✗ غلط آپشن: \"$optionText\" معروف (Active) ساخت ظاہر کر رہا ہے جبکہ یہاں کام وصول کنندہ کے لیے مجہول (Passive) صیغہ درکار ہے۔"

            pillar == ConceptPillar.CONDITIONALS && (lowerOption.contains("will") && lowerCorrect.contains("would")) ->
                "✗ غلط آپشن: \"$optionText\" میں 'will' کا استعمال ہے جو فرضی شرط (Type 2/3 Conditional) کے جواب میں غلط ہے؛ یہاں 'would' درکار تھا۔"

            pillar == ConceptPillar.GERUNDS_INFINITIVES && lowerOption.startsWith("to ") && lowerCorrect.endsWith("ing") ->
                "✗ غلط آپشن: \"$optionText\" Infinitive ہے جبکہ متعلقہ فعل/حرفِ جار کے بعد اسم مصدر (Gerund یعنی -ing) آنا ضروری ہے۔"

            pillar == ConceptPillar.GERUNDS_INFINITIVES && lowerOption.endsWith("ing") && lowerCorrect.startsWith("to ") ->
                "✗ غلط آپشن: \"$optionText\" Gerund ہے جبکہ اس فعل کے بعد 'to + Base Verb' (Infinitive) مطلوب ہے۔"

            pillar == ConceptPillar.ARTICLES_QUANTIFIERS && lowerOption == "a" && (lowerCorrect == "an" || lowerCorrect == "the") ->
                "✗ غلط آپشن: \"$optionText\" وول (Vowel) آواز سے شروع ہونے والے اسم کے ساتھ نہیں لگایا جا سکتا۔"

            pillar == ConceptPillar.PREPOSITIONS ->
                "✗ غلط آپشن: \"$optionText\" کا استعمال اس سیاق یا مخصوص ترکیب (Collocation) کے ساتھ قواعد کی رو سے غلط ہے۔"

            pillar == ConceptPillar.SUBJECT_VERB_AGREEMENT ->
                "✗ غلط آپشن: \"$optionText\" فاعل کی تعداد (Singular/Plural) کے ساتھ مطابقت نہیں رکھتا۔"

            else ->
                "✗ غلط آپشن: \"$optionText\" جملے کے نحوی تقاضے اور فاعل و فعل کی مطلوبہ ساخت کے مطابق نہیں ہے۔"
        }
    }

    /**
     * Generates a pedagogical, non-revealing hint in natural Urdu (AGENTS.md compliant).
     */
    fun getPedagogicalHint(question: QuizQuestion): String {
        if (question.hintUr.isNotBlank()) return question.hintUr

        return when (detectConceptPillar(question)) {
            ConceptPillar.TENSES ->
                "اشارہ: جملے میں وقت کے اشارے کو دیکھیں اور فاعل کے واحد یا جمع ہونے پر غور کریں۔"
            ConceptPillar.MODALS ->
                "اشارہ: موڈل ورب کے فوراً بعد آنے والے فعل کی بنیادی حالت کے اصول کو یاد کریں۔"
            ConceptPillar.PASSIVE_VOICE ->
                "اشارہ: غور کریں کہ جملے کا فاعل کام کر رہا ہے یا اس پر کام واقع ہو رہا ہے؟"
            ConceptPillar.CONDITIONALS ->
                "اشارہ: 'If' والے حصے کے زمانے پر غور کریں اور اس کے مطابق جوابِ شرط کا فارمولا لگائیں۔"
            ConceptPillar.REPORTED_SPEECH ->
                "اشارہ: رپورٹنگ ورب کے زمانے کو دیکھیں اور وقت کے الفاظ کی تبدیلی کا خیال رکھیں۔"
            ConceptPillar.GERUNDS_INFINITIVES ->
                "اشارہ: خالی جگہ سے پہلے والے لفظ پر غور کریں کہ کیا وہ Preposition ہے یا مخصوص فعل؟"
            ConceptPillar.ARTICLES_QUANTIFIERS ->
                "اشارہ: اسم کے قابل شمار ہونے اور اس کی پہلی آواز (حرفِ علت یا حرفِ صحیح) کو پرکھیں۔"
            ConceptPillar.PREPOSITIONS ->
                "اشارہ: وقت، مقام یا فعل کے ساتھ مستعمل مخصوص محاوراتی جوڑ (Collocation) پر دھیان دیں۔"
            ConceptPillar.SUBJECT_VERB_AGREEMENT ->
                "اشارہ: جملے کے اصل اور بنیادی فاعل کو پہچانیں اور درمیانی وضاحتی الفاظ سے دھوکا نہ کھائیں۔"
            ConceptPillar.CONNECTORS_SYNTAX ->
                "اشارہ: دونوں حصوں کے درمیان معنوی تعلق (تضاد ہے یا وجہ) کا بغور جائزہ لیں۔"
            ConceptPillar.GENERAL ->
                "اشارہ: جملے کے سیاق اور فاعل و فعل کی باہمی مطابقت پر توجہ مرکوز کریں۔"
        }
    }

    /**
     * Generates an Arabic grammar comparison where pedagogically meaningful.
     */
    fun getArabicGrammarComparison(question: QuizQuestion): String {
        if (!question.arabicReference.isNullOrBlank()) return question.arabicReference
        if (question.explanationUr.contains("عربی")) {
            val lines = question.explanationUr.lines().filter { it.contains("عربی") }
            if (lines.isNotEmpty()) return lines.joinToString("\n")
        }

        return when (detectConceptPillar(question)) {
            ConceptPillar.TENSES ->
                "عربی گرامر سے موازنہ: عربی میں افعال ماضی اور مضارع میں منقسم ہیں، جبکہ جاری کیفیت کو 'اسم الفاعل' یا 'كان + المضارع' کے ذریعے ظاہر کیا جاتا ہے۔"
            ConceptPillar.MODALS ->
                "عربی گرامر سے موازنہ: موڈل افعال عربی کے ادواتِ رجاء واستطاعت (يَجِبُ أَنْ، يُمْكِنُ أَنْ) کے مانند ہیں جن کے بعد فعل مضارع آتا ہے۔"
            ConceptPillar.PASSIVE_VOICE ->
                "عربی گرامر سے موازنہ: یہ عربی کے 'الفعل المبني للمجهول' کے مماثل ہے، جہاں مفعول فاعل کی جگہ لے کر 'نائب الفاعل' بن جاتا ہے (جیسے: كُتِبَ الدَّرْسُ)۔"
            ConceptPillar.CONDITIONALS ->
                "عربی گرامر سے موازنہ: یہ عربی کے 'اسلوبِ شرط' (جملة الشرط وجواب الشرط) کے مشابہ ہے، جس میں 'إِنْ / لَوْ / إِذَا' کے ذریعے ربط قائم کیا جاتا ہے۔"
            ConceptPillar.REPORTED_SPEECH ->
                "عربی گرامر سے موازنہ: عربی میں نقلِ قول (حكاية القول) کے وقت ضمائر بولنے والے کے تناظر کے مطابق ایڈجسٹ کیے جاتے ہیں۔"
            ConceptPillar.GERUNDS_INFINITIVES ->
                "عربی گرامر سے موازنہ: Gerund عربی کے 'المصدر الصريح' اور Infinitive 'المصدر المؤول' (أَنْ + الفعل) کے ہم معنی ہے۔"
            ConceptPillar.ARTICLES_QUANTIFIERS ->
                "عربی گرامر سے موازنہ: 'the' عربی کے لامِ تعریف (الـ) اور 'a/an' نکرہ کی تنوین کے مترادف ہے۔"
            ConceptPillar.PREPOSITIONS ->
                "عربی گرامر سے موازنہ: Prepositions عربی کے 'حروفِ الجر' (في، على، مِنْ، إِلَى، بِـ) کے قائم مقام ہیں۔"
            ConceptPillar.SUBJECT_VERB_AGREEMENT ->
                "عربی گرامر سے موازنہ: یہ عربی کے 'تطابق الفعل مع الفاعل' (افراد، تثنیہ، جمع اور تذکیر و تانیث میں مطابقت) کا عین اصول ہے۔"
            ConceptPillar.CONNECTORS_SYNTAX ->
                "عربی گرامر سے موازنہ: یہ عربی کے 'حروفِ العطف وأدوات الوصل' (لٰكِنْ، لِذٰلِكَ، بِالرَّغْمِ مِنْ) کے متوازی ہیں۔"
            ConceptPillar.GENERAL ->
                "عربی گرامر سے موازنہ: دونوں زبانوں میں جملے کے ارکان (فاعل، فعل، متعلقات) کے درمیان معنوی اور نحوی مطابقت بنیادی اہمیت رکھتی ہے۔"
        }
    }

    /**
     * Fully enriches a QuizQuestion with all 4 option-specific explanations, rules, and solving steps.
     */
    fun enrichQuestion(question: QuizQuestion): QuizQuestion {
        val pillar = detectConceptPillar(question)
        val optionExplanations = (0 until 4).map { idx ->
            getOptionSpecificFeedback(question, idx)
        }
        val rule = getGrammarRule(question)
        val howToSolve = getConceptSolvingMethod(question)
        val hint = getPedagogicalHint(question)
        val arabicComp = getArabicGrammarComparison(question)

        return question.copy(
            optionExplanations = optionExplanations,
            ruleUr = rule,
            howToSolveUr = howToSolve,
            conceptType = pillar.name,
            hintUr = hint,
            arabicReference = arabicComp
        )
    }
}
