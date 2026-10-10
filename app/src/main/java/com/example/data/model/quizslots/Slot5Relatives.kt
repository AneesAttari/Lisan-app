package com.example.data.model.quizslots

import com.example.data.model.DailyQuiz365Generator.QuestionTemplate
import com.example.data.model.QuizSlotHelper
import com.example.data.model.QuizSlotHelper.SentenceVariant

object Slot5Relatives {
    val templates: List<QuestionTemplate> by lazy {
        val list = mutableListOf<QuestionTemplate>()
        fun v(text: String, ur: String, corr: String, dist: List<String>) = SentenceVariant(text, ur, corr, dist)

        val topics = listOf(
            Tuple4("K01_DEFINING_REL_HUMAN", "Relative Clauses", "Defining Relative Subject Human", "REL:DEFINING:SUBJECT_HUMAN_WHO", "who developed", listOf("which developed", "whose developed", "whom developed"), "The researcher _____ the new vaccine received an award.", "جس محقق نے نئی ویکسین تیار کی، اسے ایوارڈ ملا۔"),
            Tuple4("K02_DEFINING_REL_NONHUMAN", "Relative Clauses", "Defining Relative Subject Non-Human", "REL:DEFINING:SUBJECT_NONHUMAN_WHICH", "which powers", listOf("who powers", "whom powers", "whose powers"), "The solar engine _____ the entire facility is zero-emission.", "پورے مرکز کو توانائی فراہم کرنے والا شمسی انجن صفر اخراج پر مبنی ہے۔"),
            Tuple4("K03_DEFINING_REL_OBJECT", "Relative Clauses", "Defining Relative Object Contact Clause", "REL:DEFINING:OBJECT_OMISSION", "we interviewed yesterday", listOf("whom we interviewed yesterday him", "which we interviewed yesterday", "whose we interviewed yesterday"), "The applicant _____ has accepted the offer.", "جس امیدوار کا ہم نے کل انٹرویو لیا تھا اس نے پیشکش قبول کر لی ہے۔"),
            Tuple4("K04_DEFINING_REL_POSSESSION", "Relative Clauses", "Defining Relative Possession Whose", "REL:DEFINING:POSSESSION_WHOSE", "whose research", listOf("who research", "which research", "whom research"), "The scientist _____ won the grant gave a lecture.", "جس سائنسدان کی تحقیق کو گرانٹ ملی اس نے لیکچر دیا۔"),
            Tuple4("K05_NON_DEFINING_REL", "Relative Clauses", "Non-Defining Relative Clause Comma", "REL:NON_DEFINING:COMMA_WHICH", ", which was built in 1920,", listOf("that was built in 1920", "which was built in 1920", "what was built in 1920"), "The central bank _____ undergoes restoration.", "مرکزی بینک، جو 1920 میں تعمیر ہوا تھا، کی مرمت کی جا رہی ہے۔"),
            Tuple4("K06_FORMAL_REL_PREP", "Relative Clauses", "Formal Preposition + Relative Pronoun", "REL:FORMAL_PREP:IN_WHICH", "in which", listOf("which in", "where in", "that in"), "This is the environment _____ rare species thrive.", "یہ وہ ماحول ہے جس میں نایاب انواع پروان چڑھتی ہیں۔"),
            Tuple4("K07_INFORMAL_REL_PREP", "Relative Clauses", "Informal Preposition at Clause End", "REL:INFORMAL_PREP:END_PREP", "which we were talking about", listOf("about which we were talking about", "that about we were talking", "whom we talked about it"), "The policy _____ was revised today.", "جس پالیسی کے بارے میں ہم بات کر رہے تھے اس میں آج ترمیم کی گئی۔"),
            Tuple4("K08_REL_TIME_WHEN", "Relative Adverbs", "Relative Adverb Time When", "REL:ADVERB:TIME_WHEN", "when the treaty was signed", listOf("where the treaty was signed", "which the treaty was signed", "why the treaty was signed"), "1945 was the year _____.", "1945 وہ سال تھا جب معاہدے پر دستخط ہوئے۔"),
            Tuple4("K09_REL_PLACE_WHERE", "Relative Adverbs", "Relative Adverb Place Where", "REL:ADVERB:PLACE_WHERE", "where the summit took place", listOf("which the summit took place", "when the summit took place", "that the summit took place in"), "Geneva is the city _____.", "جنیوا وہ شہر ہے جہاں سربراہی اجلاس منعقد ہوا۔"),
            Tuple4("K10_REL_REASON_WHY", "Relative Adverbs", "Relative Adverb Reason Why", "REL:ADVERB:REASON_WHY", "why the project was delayed", listOf("which the project was delayed", "where the project was delayed", "when the project delayed"), "That is the main reason _____.", "یہ وہ بنیادی وجہ ہے کہ پروجیکٹ میں تاخیر کیوں ہوئی۔"),
            Tuple4("K11_SENTENTIAL_RELATIVE", "Relative Clauses", "Sentential Relative Clause Which", "REL:SENTENTIAL:WHICH_CLAUSE", ", which pleased the entire board", listOf("that pleased the entire board", "what pleased the entire board", "who pleased the entire board"), "The team completed the target early _____", "ٹیم نے ہدف وقت سے پہلے پورا کر لیا، جس سے پورا بورڈ خوش ہوا۔"),
            Tuple4("K12_REDUCED_REL_ACTIVE", "Participle Clauses", "Reduced Relative Active Present Participle", "REL:REDUCED_ACTIVE:ING_PARTICIPLE", "standing by the reception", listOf("who standing by reception", "stood by reception", "is standing by reception"), "The engineer _____ is our chief architect.", "ریسیپشن کے پاس کھڑے انجینئر ہمارے چیف آرکیٹیکٹ ہیں۔"),
            Tuple4("K13_REDUCED_REL_PASSIVE", "Participle Clauses", "Reduced Relative Passive Past Participle", "REL:REDUCED_PASSIVE:V3_PARTICIPLE", "published in the journal", listOf("which published in journal", "was published in journal", "publishing in journal"), "The study _____ gained global attention.", "جرنل میں شائع ہونے والے مطالعے نے عالمی توجہ حاصل کی۔"),
            Tuple4("K14_QUANTIFIER_RELATIVE", "Relative Clauses", "Quantifier Relative Some Of Which", "REL:QUANTIFIER:SOME_OF_WHICH", "some of which were outdated", listOf("some of that were outdated", "some of them were outdated", "some of whom were outdated"), "He presented ten articles, _____.", "انہوں نے دس مضامین پیش کیے، جن میں سے کچھ پرانے تھے۔"),
            Tuple4("K15_NOMINAL_RELATIVE_WHAT", "Relative Clauses", "Nominal Relative What", "REL:NOMINAL:WHAT_CLAUSE", "What surprised us most", listOf("That surprised us most", "Which surprised us most", "The thing what surprised us"), "_____ was his calm composure.", "جس چیز نے ہمیں سب سے زیادہ حیران کیا وہ ان کا سکون تھا۔"),
            Tuple4("K16_COMPOUND_REL_WHOEVER", "Relative Pronouns", "Compound Relative Whoever", "REL:COMPOUND:WHOEVER_SUBJ", "Whoever completes the quiz first", listOf("Whomever completes quiz first", "Who completes quiz first", "Anyone completes quiz first"), "_____ will receive a distinction certificate.", "جو بھی کوئز پہلے مکمل کرے گا اسے امتیازی سند ملے گی۔"),
            Tuple4("K17_PARTICIPLE_TIME_SEQ", "Participle Clauses", "Participle Clause Time Sequence", "PARTICIPLE:TIME_SEQ:HAVING_V3", "Having locked the office", listOf("Locking office", "Locked office", "After lock office"), "_____, the director went home.", "دفتر کو تالا لگانے کے بعد ڈائریکٹر گھر چلے گئے۔"),
            Tuple4("K18_PARTICIPLE_REASON_CAUSE", "Participle Clauses", "Participle Clause Reason Cause", "PARTICIPLE:REASON:ING_CAUSE", "Feeling unwell", listOf("Felt unwell", "Having feel unwell", "On feeling unwell"), "_____, she took a short leave.", "طبیعت ناساز محسوس ہونے پر، انہوں نے مختصر رخصت لی۔"),
            Tuple4("K19_PARTICIPLE_SIMULTANEOUS", "Participle Clauses", "Participle Clause Simultaneous Action", "PARTICIPLE:SIMULTANEOUS:ING_ACTION", "whistling a cheerful tune", listOf("whistled a cheerful tune", "and whistle cheerful tune", "having whistled cheerful tune"), "He walked into the room _____.", "وہ خوشگوار دھن گنگناتے ہوئے کمرے میں داخل ہوا۔"),
            Tuple4("K20_PASSIVE_PARTICIPLE_CAUSE", "Participle Clauses", "Passive Participle Cause V3", "PARTICIPLE:PASSIVE_CAUSE:V3_PART", "Accompanied by security guards", listOf("Accompanying security guards", "Having accompanied security guards", "Accompanied security guards"), "_____, the convoy safely reached the bank.", "سیکیورٹی گارڈز کی معیت میں، قافلہ خیریت سے بینک پہنچ گیا۔"),
            Tuple4("K21_PERFECT_PART_ACTIVE", "Participle Clauses", "Perfect Participle Active Having V3", "PARTICIPLE:PERFECT_ACTIVE:HAVING_V3", "Having verified the credentials", listOf("Verifying credentials", "Verified credentials", "Having verify credentials"), "_____, the system granted access.", "کریڈنشلز کی تصدیق کے بعد سسٹم نے رسائی دے دی۔"),
            Tuple4("K22_PERFECT_PART_PASSIVE", "Participle Clauses", "Perfect Participle Passive Having Been V3", "PARTICIPLE:PERFECT_PASSIVE:HAVING_BEEN_V3", "Having been warned twice", listOf("Having warned twice", "Warning twice", "Been warned twice"), "_____, the driver slowed down.", "دو بار خبردار کیے جانے پر ڈرائیور نے گاڑی آہستہ کر لی۔"),
            Tuple4("K23_DANGLING_MOD_AVOIDANCE", "Participle Modifiers", "Dangling Modifier Correction", "PARTICIPLE:DANGLING_CORRECTION:SUBJECT_ALIGN", "While walking to the lab, Dr. Zoya dropped", listOf("While walking to the lab, the beaker dropped by Dr. Zoya", "Walking to lab, beaker dropped", "While walking to lab, it dropped by Dr. Zoya"), "_____ the chemical beaker.", "لیب کی طرف جاتے ہوئے ڈاکٹر زویا سے بیکر گر گیا۔"),
            Tuple4("K24_REL_CLAUSE_NOMINAL_FACT", "Relative Clauses", "The Fact That Nominal Clause", "REL:NOMINAL_FACT:THE_FACT_THAT", "The fact that he admitted error", listOf("The fact he admitted error", "Fact that he admitted error", "The fact which he admitted error"), "_____ impressed the jury.", "اس حقیقت نے کہ اس نے غلطی کا اعتراف کیا، جیوری کو متاثر کیا۔"),
            Tuple4("K25_INDEFINITE_PRONOUN_REL", "Relative Clauses", "Indefinite Pronoun Anyone Who", "REL:INDEFINITE:ANYONE_WHO", "anyone who violates", listOf("anyone which violates", "anyone whom violates", "anyone whose violates"), "The system automatically blocks _____ security rules.", "سسٹم سیکیورٹی قوانین کی خلاف ورزی کرنے والے کسی بھی شخص کو خود بخود بلاک کر دیتا ہے۔"),
            Tuple4("K26_REL_CLAUSE_PREP_END", "Relative Clauses", "Preposition End Focus Relative", "REL:PREP_END:WHICH_RELIED_UPON", "which the software relied upon", listOf("upon which software relied upon", "that software relied upon it", "where software relied upon"), "The core module _____ was updated.", "جس کور ماڈیول پر سافٹ ویئر انحصار کرتا تھا اسے اپ ڈیٹ کر دیا گیا۔"),
            Tuple4("K27_INFINITIVE_CLAUSE_REDUCE", "Infinitive Clauses", "The First Person To Infinitive", "INFINITIVE:REDUCED:FIRST_PERSON_TO_V1", "the first delegate to arrive", listOf("the first delegate arriving", "the first delegate who arriving", "the first delegate arrived"), "She was _____ at the venue.", "وہ مقام پر پہنچنے والی پہلی مندوب تھیں۔"),
            Tuple4("K28_CLEFT_SENTENCE_IT", "Cleft Sentences", "It Is/Was... Who/That Cleft", "CLEFT:IT_WAS_WHO_THAT", "It was Dr. Tariq who", listOf("Dr. Tariq was it who", "It was Dr. Tariq whom", "It Dr. Tariq who"), "_____ published the breakthrough discovery.", "وہ ڈاکٹر طارق ہی تھے جنہوں نے اس شاندار دریافت کو شائع کیا۔"),
            Tuple4("K29_PSEUDO_CLEFT_WHAT", "Cleft Sentences", "What... Is/Was Pseudo Cleft", "CLEFT:WHAT_IS_WAS_PSEUDO", "What we need most is", listOf("That we need most is", "Which we need most is", "What we need most are"), "_____ clear strategic direction.", "جس چیز کی ہمیں سب سے زیادہ ضرورت ہے وہ ہے واضح حکمت عملی۔"),
            Tuple4("K30_REL_COMPOUND_PREP_RESULT", "Relative Clauses", "As A Result Of Which Compound Prep", "REL:COMPOUND_PREP:AS_A_RESULT_OF_WHICH", "as a result of which the event was cancelled", listOf("as a result of that event was cancelled", "which result event cancelled", "as result where event cancelled"), "Torrential rain fell all morning, _____.", "صبح بھر مصلادھار بارش ہوتی رہی، جس کے نتیجے میں تقریب منسوخ کر دی گئی۔"),
            Tuple4("K31_REL_SUBJECT_AGREEMENT", "Relative Clauses", "One Of The Nouns Who Plural Verb", "REL:SUBJECT_AGREEMENT:ONE_OF_PLURAL_WHO", "one of the officers who were commended", listOf("one of the officers who was commended", "one of the officer who were commended", "one of the officers which were commended"), "Captain Bilal is _____ for valor.", "کپتان بلال ان افسران میں سے ایک ہیں جنہیں بہادری پر سراہا گیا۔"),
            Tuple4("K32_NON_RESTRICTIVE_WHOSE", "Relative Clauses", "Non-Restrictive Whose Possession", "REL:NON_RESTRICTIVE:WHOSE_POSSESSION", ", whose innovative work", listOf("whose innovative work", "that innovative work", "which innovative work"), "Dr. Sara _____ earned international praise, gave a talk.", "ڈاکٹر سارہ، جن کے اختراعی کام نے بین الاقوامی سطح پر پذیرائی حاصل کی، نے لیکچر دیا۔"),
            Tuple4("K33_ABSOLUTE_PARTICIPLE_CLAUSE", "Absolute Clauses", "Absolute Participle Weather Permitting", "ABSOLUTE:CLAUSE:WEATHER_PERMITTING", "Weather permitting", listOf("If weather permitting", "Weather permits", "Weather permitted"), "_____, the ceremony will be held outdoors.", "موسم کی اجازت سے، تقریب کھلے میں منعقد ہوگی۔"),
            Tuple4("K34_REDUCED_PARTICIPLE_CONJ", "Participle Clauses", "Although + Participle Reduced", "PARTICIPLE:CONJUNCTION:ALTHOUGH_ING", "Although feeling exhausted", listOf("Although felt exhausted", "Although feels exhausted", "Although having feel exhausted"), "_____, she completed the remaining shift.", "تھکن محسوس کرنے کے باوجود، اس نے بقیہ شفٹ مکمل کی۔"),
            Tuple4("K35_REDUCED_PARTICIPLE_COND", "Participle Clauses", "Unless + Past Participle Reduced", "PARTICIPLE:CONJUNCTION:UNLESS_V3", "Unless stated otherwise", listOf("Unless stating otherwise", "Unless state otherwise", "Unless states otherwise"), "_____, all meetings start at 9 AM.", "اگر بصورتِ دیگر بیان نہ کیا گیا ہو، تمام میٹنگز صبح 9 بجے شروع ہوتی ہیں۔"),
            Tuple4("K36_RELATIVE_PRONOUN_EVER", "Relative Pronouns", "Whichever Choice Compound Relative", "REL:COMPOUND:WHICHEVER_CHOICE", "Whichever option you choose", listOf("Whatever option you choose", "Whichever option do you choose", "Whichever option you will choose"), "_____, ensure it aligns with security guidelines.", "آپ جو بھی آپشن منتخب کریں، یقینی بنائیں کہ وہ سیکیورٹی رہنما خطوط کے مطابق ہو۔")
        )

        topics.forEachIndexed { idx, t ->
            val kNum = idx + 1
            list.add(QuizSlotHelper.createTemplate(
                templateId = t.v1,
                slotIndex = 5,
                grammarTopic = t.v2,
                subtopic = t.v3,
                difficulty = if (kNum > 25) "Expert" else "Intermediate",
                structuralFingerprint = t.v4,
                category = "PRESENT",
                tenseId = "relatives_slot5_$kNum",
                tenseTitleEn = "${t.v2} • ${t.v3}",
                tenseTitleAr = "موصول جملے",
                explanationEn = "Relative structure focusing on ${t.v3}.",
                explanationUr = "اس ساخت میں اسمِ موصول اور اس کی اقسام کی وضاحت کی گئی ہے۔",
                commonMistake = "موصول اسم اور فعل کے جوڑ میں غلطی۔",
                variants = listOf(v(t.v7, t.v8, t.v5, t.v6))
            ))
        }

        list
    }

    private data class Tuple4<A, B, C, D, E, F, G, H>(
        val v1: A, val v2: B, val v3: C, val v4: D, val v5: E, val v6: F, val v7: G, val v8: H
    )
}
