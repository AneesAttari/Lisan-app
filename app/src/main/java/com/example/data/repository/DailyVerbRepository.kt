package com.example.data.repository

import com.example.data.local.DailyVerbAssignmentDao
import com.example.data.model.DailyVerb
import com.example.data.model.DailyVerbAssignment
import com.example.data.model.DailyVerbExample
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DailyVerbRepository(private val dailyDaos: DailyVerbAssignmentDao) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val allVerbs: List<DailyVerb> = listOf(
        DailyVerb(
            id = "verb_1_learn",
            baseForm = "learn",
            thirdPerson = "learns",
            presentParticiple = "learning",
            pastSimple = "learned / learnt",
            pastParticiple = "learned / learnt",
            urduMeaning = "سیکھنا",
            examples = listOf(
                DailyVerbExample("I learn English every day.", "میں ہر روز انگریزی سیکھتا ہوں।"),
                DailyVerbExample("She learns English every day.", "وہ ہر روز انگریزی سیکھتی ہے۔"),
                DailyVerbExample("I am learning English now.", "میں ابھی انگریزی سیکھ رہا ہوں۔"),
                DailyVerbExample("I learned English yesterday.", "میں نے کل انگریزی سیکھی।"),
                DailyVerbExample("I have learned this lesson.", "میں نے یہ سبق سیکھ لیا ہے۔")
            ),
            usageNoteEn = "Use \"learn\" with I, you, we, and they.\nUse \"learns\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “learn” استعمال ہوتا ہے۔\n“He, she, it” کے ساتھ “learns” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_2_go",
            baseForm = "go",
            thirdPerson = "goes",
            presentParticiple = "going",
            pastSimple = "went",
            pastParticiple = "gone",
            urduMeaning = "جانا",
            examples = listOf(
                DailyVerbExample("I go to school every day.", "میں روزانہ اسکول جاتا ہوں۔"),
                DailyVerbExample("He goes to the market.", "وہ بازار جاتا ہے۔"),
                DailyVerbExample("They are going home.", "وہ گھر جا رہے ہیں۔"),
                DailyVerbExample("She went to Lahore.", "وہ لاہور گئی۔"),
                DailyVerbExample("I have gone there before.", "میں پہلے وہاں جا چکا ہوں۔")
            ),
            usageNoteEn = "Use \"go\" with I, you, we, and they.\nUse \"goes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “go” استعمال ہوتا ہے۔\n“He, she, it” کے ساتھ “goes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_3_be",
            baseForm = "be",
            thirdPerson = "is / am / are",
            presentParticiple = "being",
            pastSimple = "was / were",
            pastParticiple = "been",
            urduMeaning = "ہونا",
            examples = listOf(
                DailyVerbExample("I am a student.", "میں ایک طالب علم ہوں۔"),
                DailyVerbExample("She is happy.", "وہ خوش ہے۔"),
                DailyVerbExample("They are my friends.", "وہ میرے دوست ہیں۔"),
                DailyVerbExample("He was at home.", "وہ گھر پر تھا۔"),
                DailyVerbExample("We have been there.", "ہم وہاں رہ چکے ہیں۔")
            ),
            usageNoteEn = "\"Be\" changes completely depending on the subject and tense.",
            usageNoteUr = "“Be” کی شکل فائل (subject) اور زمانے کے لحاظ سے مکمل تبدیل ہوتی ہے۔"
        ),
        DailyVerb(
            id = "verb_4_eat",
            baseForm = "eat",
            thirdPerson = "eats",
            presentParticiple = "eating",
            pastSimple = "ate",
            pastParticiple = "eaten",
            urduMeaning = "کھانا",
            examples = listOf(
                DailyVerbExample("I eat an apple.", "میں ایک سیب کھاتا ہوں۔"),
                DailyVerbExample("He eats lunch.", "وہ دوپہر کا کھانا کھاتا ہے۔"),
                DailyVerbExample("We are eating dinner.", "ہم رات کا کھانا کھا رہے ہیں۔"),
                DailyVerbExample("She ate a sandwich.", "اس نے سینڈوچ کھایا۔"),
                DailyVerbExample("I have eaten my food.", "میں اپنا کھانا کھا چکا ہوں۔")
            ),
            usageNoteEn = "Use \"eat\" with I, you, we, and they.\nUse \"eats\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “eat” استعمال ہوتا ہے۔\n“He, she, it” کے ساتھ “eats” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_5_speak",
            baseForm = "speak",
            thirdPerson = "speaks",
            presentParticiple = "speaking",
            pastSimple = "spoke",
            pastParticiple = "spoken",
            urduMeaning = "بولنا",
            examples = listOf(
                DailyVerbExample("I speak English and Urdu.", "میں انگریزی اور اردو بولتا ہوں۔"),
                DailyVerbExample("He speaks very clearly.", "وہ بہت واضح بولتا ہے۔"),
                DailyVerbExample("She is speaking with her teacher.", "وہ اپنے استاد سے بات کر رہی ہے۔"),
                DailyVerbExample("They spoke to me yesterday.", "انہوں نے کل مجھ سے بات کی۔"),
                DailyVerbExample("He has spoken the truth.", "اس نے سچ بولا ہے۔")
            ),
            usageNoteEn = "Use \"speak\" for languages or general conversation.",
            usageNoteUr = "زبانوں یا عام گفتگو کے لیے “speak” استعمال کریں۔"
        ),
        DailyVerb(
            id = "verb_6_write",
            baseForm = "write",
            thirdPerson = "writes",
            presentParticiple = "writing",
            pastSimple = "wrote",
            pastParticiple = "written",
            urduMeaning = "لکھنا",
            examples = listOf(
                DailyVerbExample("I write a letter.", "میں ایک خط لکھتا ہوں۔"),
                DailyVerbExample("She writes beautiful stories.", "وہ خوبصورت کہانیاں لکھتی ہے۔"),
                DailyVerbExample("He is writing an essay.", "وہ ایک مضمون لکھ رہا ہے۔"),
                DailyVerbExample("I wrote a note yesterday.", "میں نے کل ایک نوٹ لکھا۔"),
                DailyVerbExample("They have written a book.", "انہوں نے ایک کتاب لکھی ہے۔")
            ),
            usageNoteEn = "\"Write\" is irregular: write -> wrote -> written.",
            usageNoteUr = "“Write” ایک غیر باقاعدہ فعل ہے: write -> wrote -> written۔"
        ),
        DailyVerb(
            id = "verb_7_read",
            baseForm = "read",
            thirdPerson = "reads",
            presentParticiple = "reading",
            pastSimple = "read (red)",
            pastParticiple = "read (red)",
            urduMeaning = "پڑھنا",
            examples = listOf(
                DailyVerbExample("I read books every night.", "میں ہر رات کتابیں پڑھتا ہوں۔"),
                DailyVerbExample("He reads the newspaper.", "وہ اخبار پڑھتا ہے۔"),
                DailyVerbExample("She is reading a new novel.", "وہ ایک نیا ناول پڑھ رہی ہے۔"),
                DailyVerbExample("I read that book last week.", "میں نے پچھلے ہفتے وہ کتاب پڑھی۔"),
                DailyVerbExample("We have read all the lessons.", "ہم تمام اسباق پڑھ چکے ہیں۔")
            ),
            usageNoteEn = "Past simple is spelled \"read\" but pronounced like \"red\".",
            usageNoteUr = "ماضی کی شکل کا ہجائی “read” ہی ہے لیکن تلفظ “red” جیسا ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_8_make",
            baseForm = "make",
            thirdPerson = "makes",
            presentParticiple = "making",
            pastSimple = "made",
            pastParticiple = "made",
            urduMeaning = "بنانا",
            examples = listOf(
                DailyVerbExample("I make tea in the morning.", "میں صبح چائے بناتا ہوں۔"),
                DailyVerbExample("She makes delicious cake.", "وہ مزیدار کیک بناتی ہے۔"),
                DailyVerbExample("They are making a plan.", "وہ ایک منصوبہ بنا رہے ہیں۔"),
                DailyVerbExample("He made a mistake.", "اس نے ایک غلطی کی۔"),
                DailyVerbExample("We have made progress.", "ہم نے پیش رفت کی ہے۔")
            ),
            usageNoteEn = "Use \"make\" when creating or producing something.",
            usageNoteUr = "جب کوئی چیز بنائی یا تیار کی جائے تو “make” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_9_take",
            baseForm = "take",
            thirdPerson = "takes",
            presentParticiple = "taking",
            pastSimple = "took",
            pastParticiple = "taken",
            urduMeaning = "لینا",
            examples = listOf(
                DailyVerbExample("I take medicine on time.", "میں وقت پر دوا لیتا ہوں۔"),
                DailyVerbExample("She takes the bus to work.", "وہ کام پر جانے کے لیے بس لیتی ہے۔"),
                DailyVerbExample("He is taking a test.", "وہ ایک ٹیسٹ دے رہا ہے۔"),
                DailyVerbExample("We took many photos.", "ہم نے بہت سی تصاویر لیں۔"),
                DailyVerbExample("Have you taken your breakfast?", "کیا آپ نے ناشتہ کر لیا ہے؟")
            ),
            usageNoteEn = "\"Take\" is used for time, transport, medicine, and physical items.",
            usageNoteUr = "“Take” وقت، سواری، ادویات اور اشیاء لینے کے لیے استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_10_give",
            baseForm = "give",
            thirdPerson = "gives",
            presentParticiple = "giving",
            pastSimple = "gave",
            pastParticiple = "given",
            urduMeaning = "دینا",
            examples = listOf(
                DailyVerbExample("I give my best in everything.", "میں ہر کام میں اپنی پوری کوشش کرتا ہوں۔"),
                DailyVerbExample("She gives good advice.", "وہ اچھی نصیحت دیتی ہے۔"),
                DailyVerbExample("He is giving a presentation.", "وہ ایک پریزنٹیشن دے رہا ہے۔"),
                DailyVerbExample("They gave me a gift.", "انہوں نے مجھے ایک تحفہ دیا۔"),
                DailyVerbExample("He has given his opinion.", "اس نے اپنی رائے دی ہے۔")
            ),
            usageNoteEn = "\"Give\" goes: give -> gave -> given.",
            usageNoteUr = "“Give” کی شکلیں: give -> gave -> given۔"
        ),
        DailyVerb(
            id = "verb_11_walk",
            baseForm = "walk",
            thirdPerson = "walks",
            presentParticiple = "walking",
            pastSimple = "walked",
            pastParticiple = "walked",
            urduMeaning = "پیدل چلنا",
            examples = listOf(
                DailyVerbExample("I walk in the morning.", "میں صبح پیدل چلتا ہوں۔"),
                DailyVerbExample("He walks every day.", "وہ ہر روز پیدل چلنا کرتا ہے۔"),
                DailyVerbExample("They are walking now.", "وہ ابھی پیدل چلنا کر رہے ہیں۔"),
                DailyVerbExample("We walked yesterday.", "ہم نے کل پیدل چلنا کیا۔"),
                DailyVerbExample("She has walked.", "وہ پیدل چلنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"walk\" with I, you, we, and they. Use \"walks\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “walk” اور “He, she, it” کے ساتھ “walks” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_12_run",
            baseForm = "run",
            thirdPerson = "runs",
            presentParticiple = "running",
            pastSimple = "ran",
            pastParticiple = "run",
            urduMeaning = "دوڑنا",
            examples = listOf(
                DailyVerbExample("He runs fast.", "وہ تیز دوڑتا ہے۔"),
                DailyVerbExample("He runs every day.", "وہ ہر روز دوڑنا کرتا ہے۔"),
                DailyVerbExample("They are running now.", "وہ ابھی دوڑنا کر رہے ہیں۔"),
                DailyVerbExample("We ran yesterday.", "ہم نے کل دوڑنا کیا۔"),
                DailyVerbExample("She has run.", "وہ دوڑنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"run\" with I, you, we, and they. Use \"runs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “run” اور “He, she, it” کے ساتھ “runs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_13_fly",
            baseForm = "fly",
            thirdPerson = "flies",
            presentParticiple = "flying",
            pastSimple = "flew",
            pastParticiple = "flown",
            urduMeaning = "اڑنا",
            examples = listOf(
                DailyVerbExample("Birds fly in the sky.", "پرندے آسمان میں اڑتے ہیں۔"),
                DailyVerbExample("He flies every day.", "وہ ہر روز اڑنا کرتا ہے۔"),
                DailyVerbExample("They are flying now.", "وہ ابھی اڑنا کر رہے ہیں۔"),
                DailyVerbExample("We flew yesterday.", "ہم نے کل اڑنا کیا۔"),
                DailyVerbExample("She has flown.", "وہ اڑنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"fly\" with I, you, we, and they. Use \"flies\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “fly” اور “He, she, it” کے ساتھ “flies” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_14_swim",
            baseForm = "swim",
            thirdPerson = "swims",
            presentParticiple = "swimming",
            pastSimple = "swam",
            pastParticiple = "swum",
            urduMeaning = "تیرنا",
            examples = listOf(
                DailyVerbExample("She swims every morning.", "وہ ہر صبح تیرتی ہے۔"),
                DailyVerbExample("He swims every day.", "وہ ہر روز تیرنا کرتا ہے۔"),
                DailyVerbExample("They are swimming now.", "وہ ابھی تیرنا کر رہے ہیں۔"),
                DailyVerbExample("We swam yesterday.", "ہم نے کل تیرنا کیا۔"),
                DailyVerbExample("She has swum.", "وہ تیرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"swim\" with I, you, we, and they. Use \"swims\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “swim” اور “He, she, it” کے ساتھ “swims” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_15_sleep",
            baseForm = "sleep",
            thirdPerson = "sleeps",
            presentParticiple = "sleeping",
            pastSimple = "slept",
            pastParticiple = "slept",
            urduMeaning = "سونا",
            examples = listOf(
                DailyVerbExample("I sleep at 10 PM.", "میں رات ۱۰ بجے سوتا ہوں۔"),
                DailyVerbExample("He sleeps every day.", "وہ ہر روز سونا کرتا ہے۔"),
                DailyVerbExample("They are sleeping now.", "وہ ابھی سونا کر رہے ہیں۔"),
                DailyVerbExample("We slept yesterday.", "ہم نے کل سونا کیا۔"),
                DailyVerbExample("She has slept.", "وہ سونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"sleep\" with I, you, we, and they. Use \"sleeps\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “sleep” اور “He, she, it” کے ساتھ “sleeps” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_16_drive",
            baseForm = "drive",
            thirdPerson = "drives",
            presentParticiple = "driving",
            pastSimple = "drove",
            pastParticiple = "driven",
            urduMeaning = "گاڑی چلانا",
            examples = listOf(
                DailyVerbExample("He drives carefully.", "وہ احتیاط سے گاڑی چلاتا ہے۔"),
                DailyVerbExample("He drives every day.", "وہ ہر روز گاڑی چلانا کرتا ہے۔"),
                DailyVerbExample("They are driving now.", "وہ ابھی گاڑی چلانا کر رہے ہیں۔"),
                DailyVerbExample("We drove yesterday.", "ہم نے کل گاڑی چلانا کیا۔"),
                DailyVerbExample("She has driven.", "وہ گاڑی چلانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"drive\" with I, you, we, and they. Use \"drives\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “drive” اور “He, she, it” کے ساتھ “drives” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_17_teach",
            baseForm = "teach",
            thirdPerson = "teaches",
            presentParticiple = "teaching",
            pastSimple = "taught",
            pastParticiple = "taught",
            urduMeaning = "پڑھانا",
            examples = listOf(
                DailyVerbExample("She teaches mathematics.", "وہ ریاضی پڑھاتی ہے۔"),
                DailyVerbExample("He teaches every day.", "وہ ہر روز پڑھانا کرتا ہے۔"),
                DailyVerbExample("They are teaching now.", "وہ ابھی پڑھانا کر رہے ہیں۔"),
                DailyVerbExample("We taught yesterday.", "ہم نے کل پڑھانا کیا۔"),
                DailyVerbExample("She has taught.", "وہ پڑھانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"teach\" with I, you, we, and they. Use \"teaches\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “teach” اور “He, she, it” کے ساتھ “teaches” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_18_sing",
            baseForm = "sing",
            thirdPerson = "sings",
            presentParticiple = "singing",
            pastSimple = "sang",
            pastParticiple = "sung",
            urduMeaning = "گانا",
            examples = listOf(
                DailyVerbExample("They sing nicely.", "وہ اچھا گاتے ہیں۔"),
                DailyVerbExample("He sings every day.", "وہ ہر روز گانا کرتا ہے۔"),
                DailyVerbExample("They are singing now.", "وہ ابھی گانا کر رہے ہیں۔"),
                DailyVerbExample("We sang yesterday.", "ہم نے کل گانا کیا۔"),
                DailyVerbExample("She has sung.", "وہ گانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"sing\" with I, you, we, and they. Use \"sings\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “sing” اور “He, she, it” کے ساتھ “sings” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_19_build",
            baseForm = "build",
            thirdPerson = "builds",
            presentParticiple = "building",
            pastSimple = "built",
            pastParticiple = "built",
            urduMeaning = "تعمیر کرنا",
            examples = listOf(
                DailyVerbExample("Engineers build bridges.", "انجینئرز پل تعمیر کرتے ہیں۔"),
                DailyVerbExample("He builds every day.", "وہ ہر روز تعمیر کرنا کرتا ہے۔"),
                DailyVerbExample("They are building now.", "وہ ابھی تعمیر کرنا کر رہے ہیں۔"),
                DailyVerbExample("We built yesterday.", "ہم نے کل تعمیر کرنا کیا۔"),
                DailyVerbExample("She has built.", "وہ تعمیر کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"build\" with I, you, we, and they. Use \"builds\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “build” اور “He, she, it” کے ساتھ “builds” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_20_call",
            baseForm = "call",
            thirdPerson = "calls",
            presentParticiple = "calling",
            pastSimple = "called",
            pastParticiple = "called",
            urduMeaning = "بلانا",
            examples = listOf(
                DailyVerbExample("I call my parents daily.", "میں روزانہ اپنے والدین کو فون کرتا ہوں۔"),
                DailyVerbExample("He calls every day.", "وہ ہر روز بلانا کرتا ہے۔"),
                DailyVerbExample("They are calling now.", "وہ ابھی بلانا کر رہے ہیں۔"),
                DailyVerbExample("We called yesterday.", "ہم نے کل بلانا کیا۔"),
                DailyVerbExample("She has called.", "وہ بلانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"call\" with I, you, we, and they. Use \"calls\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “call” اور “He, she, it” کے ساتھ “calls” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_21_help",
            baseForm = "help",
            thirdPerson = "helps",
            presentParticiple = "helping",
            pastSimple = "helped",
            pastParticiple = "helped",
            urduMeaning = "مدد کرنا",
            examples = listOf(
                DailyVerbExample("Always help others.", "ہمیشہ دوسروں کی مدد کریں۔"),
                DailyVerbExample("He helps every day.", "وہ ہر روز مدد کرنا کرتا ہے۔"),
                DailyVerbExample("They are helping now.", "وہ ابھی مدد کرنا کر رہے ہیں۔"),
                DailyVerbExample("We helped yesterday.", "ہم نے کل مدد کرنا کیا۔"),
                DailyVerbExample("She has helped.", "وہ مدد کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"help\" with I, you, we, and they. Use \"helps\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “help” اور “He, she, it” کے ساتھ “helps” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_22_play",
            baseForm = "play",
            thirdPerson = "plays",
            presentParticiple = "playing",
            pastSimple = "played",
            pastParticiple = "played",
            urduMeaning = "کھیلنا",
            examples = listOf(
                DailyVerbExample("We play cricket.", "ہم کرکٹ کھیلتے ہیں۔"),
                DailyVerbExample("He plays every day.", "وہ ہر روز کھیلنا کرتا ہے۔"),
                DailyVerbExample("They are playing now.", "وہ ابھی کھیلنا کر رہے ہیں۔"),
                DailyVerbExample("We played yesterday.", "ہم نے کل کھیلنا کیا۔"),
                DailyVerbExample("She has played.", "وہ کھیلنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"play\" with I, you, we, and they. Use \"plays\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “play” اور “He, she, it” کے ساتھ “plays” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_23_watch",
            baseForm = "watch",
            thirdPerson = "watches",
            presentParticiple = "watching",
            pastSimple = "watched",
            pastParticiple = "watched",
            urduMeaning = "دیکھنا",
            examples = listOf(
                DailyVerbExample("They watch a movie.", "وہ فلم دیکھتے ہیں۔"),
                DailyVerbExample("He watches every day.", "وہ ہر روز دیکھنا کرتا ہے۔"),
                DailyVerbExample("They are watching now.", "وہ ابھی دیکھنا کر رہے ہیں۔"),
                DailyVerbExample("We watched yesterday.", "ہم نے کل دیکھنا کیا۔"),
                DailyVerbExample("She has watched.", "وہ دیکھنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"watch\" with I, you, we, and they. Use \"watches\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “watch” اور “He, she, it” کے ساتھ “watches” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_24_open",
            baseForm = "open",
            thirdPerson = "opens",
            presentParticiple = "opening",
            pastSimple = "opened",
            pastParticiple = "opened",
            urduMeaning = "کھولنا",
            examples = listOf(
                DailyVerbExample("Please open the door.", "براہ کرم دروازہ کھولیں۔"),
                DailyVerbExample("He opens every day.", "وہ ہر روز کھولنا کرتا ہے۔"),
                DailyVerbExample("They are opening now.", "وہ ابھی کھولنا کر رہے ہیں۔"),
                DailyVerbExample("We opened yesterday.", "ہم نے کل کھولنا کیا۔"),
                DailyVerbExample("She has opened.", "وہ کھولنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"open\" with I, you, we, and they. Use \"opens\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “open” اور “He, she, it” کے ساتھ “opens” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_25_close",
            baseForm = "close",
            thirdPerson = "closes",
            presentParticiple = "closing",
            pastSimple = "closed",
            pastParticiple = "closed",
            urduMeaning = "بند کرنا",
            examples = listOf(
                DailyVerbExample("Close the window.", "کھڑکی بند کریں۔"),
                DailyVerbExample("He closes every day.", "وہ ہر روز بند کرنا کرتا ہے۔"),
                DailyVerbExample("They are closing now.", "وہ ابھی بند کرنا کر رہے ہیں۔"),
                DailyVerbExample("We closed yesterday.", "ہم نے کل بند کرنا کیا۔"),
                DailyVerbExample("She has closed.", "وہ بند کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"close\" with I, you, we, and they. Use \"closes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “close” اور “He, she, it” کے ساتھ “closes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_26_bring",
            baseForm = "bring",
            thirdPerson = "brings",
            presentParticiple = "bringing",
            pastSimple = "brought",
            pastParticiple = "brought",
            urduMeaning = "لانا",
            examples = listOf(
                DailyVerbExample("Bring some water.", "تھوڑا پانی لائیں۔"),
                DailyVerbExample("He brings every day.", "وہ ہر روز لانا کرتا ہے۔"),
                DailyVerbExample("They are bringing now.", "وہ ابھی لانا کر رہے ہیں۔"),
                DailyVerbExample("We brought yesterday.", "ہم نے کل لانا کیا۔"),
                DailyVerbExample("She has brought.", "وہ لانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"bring\" with I, you, we, and they. Use \"brings\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “bring” اور “He, she, it” کے ساتھ “brings” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_27_send",
            baseForm = "send",
            thirdPerson = "sends",
            presentParticiple = "sending",
            pastSimple = "sent",
            pastParticiple = "sent",
            urduMeaning = "بھیجنا",
            examples = listOf(
                DailyVerbExample("I sent an email.", "میں نے ای میل بھیجی۔"),
                DailyVerbExample("He sends every day.", "وہ ہر روز بھیجنا کرتا ہے۔"),
                DailyVerbExample("They are sending now.", "وہ ابھی بھیجنا کر رہے ہیں۔"),
                DailyVerbExample("We sent yesterday.", "ہم نے کل بھیجنا کیا۔"),
                DailyVerbExample("She has sent.", "وہ بھیجنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"send\" with I, you, we, and they. Use \"sends\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “send” اور “He, she, it” کے ساتھ “sends” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_28_pay",
            baseForm = "pay",
            thirdPerson = "pays",
            presentParticiple = "paying",
            pastSimple = "paid",
            pastParticiple = "paid",
            urduMeaning = "ادائیگی کرنا",
            examples = listOf(
                DailyVerbExample("She paid the bill.", "اس نے بل ادا کیا۔"),
                DailyVerbExample("He pays every day.", "وہ ہر روز ادائیگی کرنا کرتا ہے۔"),
                DailyVerbExample("They are paying now.", "وہ ابھی ادائیگی کرنا کر رہے ہیں۔"),
                DailyVerbExample("We paid yesterday.", "ہم نے کل ادائیگی کرنا کیا۔"),
                DailyVerbExample("She has paid.", "وہ ادائیگی کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"pay\" with I, you, we, and they. Use \"pays\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “pay” اور “He, she, it” کے ساتھ “pays” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_29_choose",
            baseForm = "choose",
            thirdPerson = "chooses",
            presentParticiple = "choosing",
            pastSimple = "chose",
            pastParticiple = "chosen",
            urduMeaning = "منتخب کرنا",
            examples = listOf(
                DailyVerbExample("Choose the right path.", "صحیح راستہ منتخب کریں۔"),
                DailyVerbExample("He chooses every day.", "وہ ہر روز منتخب کرنا کرتا ہے۔"),
                DailyVerbExample("They are choosing now.", "وہ ابھی منتخب کرنا کر رہے ہیں۔"),
                DailyVerbExample("We chose yesterday.", "ہم نے کل منتخب کرنا کیا۔"),
                DailyVerbExample("She has chosen.", "وہ منتخب کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"choose\" with I, you, we, and they. Use \"chooses\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “choose” اور “He, she, it” کے ساتھ “chooses” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_30_find",
            baseForm = "find",
            thirdPerson = "finds",
            presentParticiple = "finding",
            pastSimple = "found",
            pastParticiple = "found",
            urduMeaning = "تلاش کرنا",
            examples = listOf(
                DailyVerbExample("I found my lost keys.", "مجھے میری گمشدہ چابیاں مل گئیں۔"),
                DailyVerbExample("He finds every day.", "وہ ہر روز تلاش کرنا کرتا ہے۔"),
                DailyVerbExample("They are finding now.", "وہ ابھی تلاش کرنا کر رہے ہیں۔"),
                DailyVerbExample("We found yesterday.", "ہم نے کل تلاش کرنا کیا۔"),
                DailyVerbExample("She has found.", "وہ تلاش کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"find\" with I, you, we, and they. Use \"finds\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “find” اور “He, she, it” کے ساتھ “finds” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_31_tell",
            baseForm = "tell",
            thirdPerson = "tells",
            presentParticiple = "telling",
            pastSimple = "told",
            pastParticiple = "told",
            urduMeaning = "بتانا",
            examples = listOf(
                DailyVerbExample("Tell me the truth.", "مجھے سچ بتاؤ۔"),
                DailyVerbExample("He tells every day.", "وہ ہر روز بتانا کرتا ہے۔"),
                DailyVerbExample("They are telling now.", "وہ ابھی بتانا کر رہے ہیں۔"),
                DailyVerbExample("We told yesterday.", "ہم نے کل بتانا کیا۔"),
                DailyVerbExample("She has told.", "وہ بتانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"tell\" with I, you, we, and they. Use \"tells\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “tell” اور “He, she, it” کے ساتھ “tells” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_32_leave",
            baseForm = "leave",
            thirdPerson = "leaves",
            presentParticiple = "leaving",
            pastSimple = "left",
            pastParticiple = "left",
            urduMeaning = "چھوڑنا",
            examples = listOf(
                DailyVerbExample("The train left at 8.", "ٹرین ۸ بجے روانہ ہوئی۔"),
                DailyVerbExample("He leaves every day.", "وہ ہر روز چھوڑنا کرتا ہے۔"),
                DailyVerbExample("They are leaving now.", "وہ ابھی چھوڑنا کر رہے ہیں۔"),
                DailyVerbExample("We left yesterday.", "ہم نے کل چھوڑنا کیا۔"),
                DailyVerbExample("She has left.", "وہ چھوڑنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"leave\" with I, you, we, and they. Use \"leaves\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “leave” اور “He, she, it” کے ساتھ “leaves” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_33_meet",
            baseForm = "meet",
            thirdPerson = "meets",
            presentParticiple = "meeting",
            pastSimple = "met",
            pastParticiple = "met",
            urduMeaning = "ملنا",
            examples = listOf(
                DailyVerbExample("Nice to meet you.", "آپ سے مل کر خوشی ہوئی۔"),
                DailyVerbExample("He meets every day.", "وہ ہر روز ملنا کرتا ہے۔"),
                DailyVerbExample("They are meeting now.", "وہ ابھی ملنا کر رہے ہیں۔"),
                DailyVerbExample("We met yesterday.", "ہم نے کل ملنا کیا۔"),
                DailyVerbExample("She has met.", "وہ ملنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"meet\" with I, you, we, and they. Use \"meets\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “meet” اور “He, she, it” کے ساتھ “meets” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_34_keep",
            baseForm = "keep",
            thirdPerson = "keeps",
            presentParticiple = "keeping",
            pastSimple = "kept",
            pastParticiple = "kept",
            urduMeaning = "رکھنا",
            examples = listOf(
                DailyVerbExample("Keep your room clean.", "اپنا کمرہ صاف رکھیں۔"),
                DailyVerbExample("He keeps every day.", "وہ ہر روز رکھنا کرتا ہے۔"),
                DailyVerbExample("They are keeping now.", "وہ ابھی رکھنا کر رہے ہیں۔"),
                DailyVerbExample("We kept yesterday.", "ہم نے کل رکھنا کیا۔"),
                DailyVerbExample("She has kept.", "وہ رکھنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"keep\" with I, you, we, and they. Use \"keeps\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “keep” اور “He, she, it” کے ساتھ “keeps” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_35_hold",
            baseForm = "hold",
            thirdPerson = "holds",
            presentParticiple = "holding",
            pastSimple = "held",
            pastParticiple = "held",
            urduMeaning = "پکڑنا",
            examples = listOf(
                DailyVerbExample("Hold my hand.", "میرا ہاتھ پکڑو۔"),
                DailyVerbExample("He holds every day.", "وہ ہر روز پکڑنا کرتا ہے۔"),
                DailyVerbExample("They are holding now.", "وہ ابھی پکڑنا کر رہے ہیں۔"),
                DailyVerbExample("We held yesterday.", "ہم نے کل پکڑنا کیا۔"),
                DailyVerbExample("She has held.", "وہ پکڑنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"hold\" with I, you, we, and they. Use \"holds\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “hold” اور “He, she, it” کے ساتھ “holds” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_36_start",
            baseForm = "start",
            thirdPerson = "starts",
            presentParticiple = "starting",
            pastSimple = "started",
            pastParticiple = "started",
            urduMeaning = "شروع کرنا",
            examples = listOf(
                DailyVerbExample("Let us start the game.", "آؤ کھیل شروع کریں۔"),
                DailyVerbExample("He starts every day.", "وہ ہر روز شروع کرنا کرتا ہے۔"),
                DailyVerbExample("They are starting now.", "وہ ابھی شروع کرنا کر رہے ہیں۔"),
                DailyVerbExample("We started yesterday.", "ہم نے کل شروع کرنا کیا۔"),
                DailyVerbExample("She has started.", "وہ شروع کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"start\" with I, you, we, and they. Use \"starts\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “start” اور “He, she, it” کے ساتھ “starts” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_37_show",
            baseForm = "show",
            thirdPerson = "shows",
            presentParticiple = "showing",
            pastSimple = "showed",
            pastParticiple = "shown",
            urduMeaning = "دکھانا",
            examples = listOf(
                DailyVerbExample("Show me your work.", "مجھے اپنا کام دکھاؤ۔"),
                DailyVerbExample("He shows every day.", "وہ ہر روز دکھانا کرتا ہے۔"),
                DailyVerbExample("They are showing now.", "وہ ابھی دکھانا کر رہے ہیں۔"),
                DailyVerbExample("We showed yesterday.", "ہم نے کل دکھانا کیا۔"),
                DailyVerbExample("She has shown.", "وہ دکھانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"show\" with I, you, we, and they. Use \"shows\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “show” اور “He, she, it” کے ساتھ “shows” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_38_hear",
            baseForm = "hear",
            thirdPerson = "hears",
            presentParticiple = "hearing",
            pastSimple = "heard",
            pastParticiple = "heard",
            urduMeaning = "سننا",
            examples = listOf(
                DailyVerbExample("I hear a sound.", "مجھے ایک آواز سنائی دیتی ہے۔"),
                DailyVerbExample("He hears every day.", "وہ ہر روز سننا کرتا ہے۔"),
                DailyVerbExample("They are hearing now.", "وہ ابھی سننا کر رہے ہیں۔"),
                DailyVerbExample("We heard yesterday.", "ہم نے کل سننا کیا۔"),
                DailyVerbExample("She has heard.", "وہ سننا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"hear\" with I, you, we, and they. Use \"hears\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “hear” اور “He, she, it” کے ساتھ “hears” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_39_listen",
            baseForm = "listen",
            thirdPerson = "listens",
            presentParticiple = "listening",
            pastSimple = "listened",
            pastParticiple = "listened",
            urduMeaning = "توجہ سے سننا",
            examples = listOf(
                DailyVerbExample("Listen to your teacher.", "اپنے استاد کی بات توجہ سے سنیں۔"),
                DailyVerbExample("He listens every day.", "وہ ہر روز توجہ سے سننا کرتا ہے۔"),
                DailyVerbExample("They are listening now.", "وہ ابھی توجہ سے سننا کر رہے ہیں۔"),
                DailyVerbExample("We listened yesterday.", "ہم نے کل توجہ سے سننا کیا۔"),
                DailyVerbExample("She has listened.", "وہ توجہ سے سننا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"listen\" with I, you, we, and they. Use \"listens\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “listen” اور “He, she, it” کے ساتھ “listens” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_40_move",
            baseForm = "move",
            thirdPerson = "moves",
            presentParticiple = "moving",
            pastSimple = "moved",
            pastParticiple = "moved",
            urduMeaning = "حرکت کرنا",
            examples = listOf(
                DailyVerbExample("We moved to a new house.", "ہم نئے گھر میں منتقل ہو گئے۔"),
                DailyVerbExample("He moves every day.", "وہ ہر روز حرکت کرنا کرتا ہے۔"),
                DailyVerbExample("They are moving now.", "وہ ابھی حرکت کرنا کر رہے ہیں۔"),
                DailyVerbExample("We moved yesterday.", "ہم نے کل حرکت کرنا کیا۔"),
                DailyVerbExample("She has moved.", "وہ حرکت کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"move\" with I, you, we, and they. Use \"moves\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “move” اور “He, she, it” کے ساتھ “moves” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_41_like",
            baseForm = "like",
            thirdPerson = "likes",
            presentParticiple = "liking",
            pastSimple = "liked",
            pastParticiple = "liked",
            urduMeaning = "پسند کرنا",
            examples = listOf(
                DailyVerbExample("I like apples.", "مجھے سیب پسند ہیں۔"),
                DailyVerbExample("He likes every day.", "وہ ہر روز پسند کرنا کرتا ہے۔"),
                DailyVerbExample("They are liking now.", "وہ ابھی پسند کرنا کر رہے ہیں۔"),
                DailyVerbExample("We liked yesterday.", "ہم نے کل پسند کرنا کیا۔"),
                DailyVerbExample("She has liked.", "وہ پسند کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"like\" with I, you, we, and they. Use \"likes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “like” اور “He, she, it” کے ساتھ “likes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_42_love",
            baseForm = "love",
            thirdPerson = "loves",
            presentParticiple = "loving",
            pastSimple = "loved",
            pastParticiple = "loved",
            urduMeaning = "محبت کرنا",
            examples = listOf(
                DailyVerbExample("Children love ice cream.", "بچے آئس کریم سے محبت کرتے ہیں۔"),
                DailyVerbExample("He loves every day.", "وہ ہر روز محبت کرنا کرتا ہے۔"),
                DailyVerbExample("They are loving now.", "وہ ابھی محبت کرنا کر رہے ہیں۔"),
                DailyVerbExample("We loved yesterday.", "ہم نے کل محبت کرنا کیا۔"),
                DailyVerbExample("She has loved.", "وہ محبت کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"love\" with I, you, we, and they. Use \"loves\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “love” اور “He, she, it” کے ساتھ “loves” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_43_live",
            baseForm = "live",
            thirdPerson = "lives",
            presentParticiple = "living",
            pastSimple = "lived",
            pastParticiple = "lived",
            urduMeaning = "رہنا",
            examples = listOf(
                DailyVerbExample("I live in Islamabad.", "میں اسلام آباد میں رہتا ہوں۔"),
                DailyVerbExample("He lives every day.", "وہ ہر روز رہنا کرتا ہے۔"),
                DailyVerbExample("They are living now.", "وہ ابھی رہنا کر رہے ہیں۔"),
                DailyVerbExample("We lived yesterday.", "ہم نے کل رہنا کیا۔"),
                DailyVerbExample("She has lived.", "وہ رہنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"live\" with I, you, we, and they. Use \"lives\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “live” اور “He, she, it” کے ساتھ “lives” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_44_believe",
            baseForm = "believe",
            thirdPerson = "believes",
            presentParticiple = "believing",
            pastSimple = "believed",
            pastParticiple = "believed",
            urduMeaning = "یقین کرنا",
            examples = listOf(
                DailyVerbExample("Believe in yourself.", "اپنے آپ پر یقین رکھیں۔"),
                DailyVerbExample("He believes every day.", "وہ ہر روز یقین کرنا کرتا ہے۔"),
                DailyVerbExample("They are believing now.", "وہ ابھی یقین کرنا کر رہے ہیں۔"),
                DailyVerbExample("We believed yesterday.", "ہم نے کل یقین کرنا کیا۔"),
                DailyVerbExample("She has believed.", "وہ یقین کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"believe\" with I, you, we, and they. Use \"believes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “believe” اور “He, she, it” کے ساتھ “believes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_45_happen",
            baseForm = "happen",
            thirdPerson = "happens",
            presentParticiple = "happening",
            pastSimple = "happened",
            pastParticiple = "happened",
            urduMeaning = "واقع ہونا",
            examples = listOf(
                DailyVerbExample("Accidents happen.", "حادثات ہوتے ہیں۔"),
                DailyVerbExample("He happens every day.", "وہ ہر روز واقع ہونا کرتا ہے۔"),
                DailyVerbExample("They are happening now.", "وہ ابھی واقع ہونا کر رہے ہیں۔"),
                DailyVerbExample("We happened yesterday.", "ہم نے کل واقع ہونا کیا۔"),
                DailyVerbExample("She has happened.", "وہ واقع ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"happen\" with I, you, we, and they. Use \"happens\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “happen” اور “He, she, it” کے ساتھ “happens” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_46_provide",
            baseForm = "provide",
            thirdPerson = "provides",
            presentParticiple = "providing",
            pastSimple = "provided",
            pastParticiple = "provided",
            urduMeaning = "مہیا کرنا",
            examples = listOf(
                DailyVerbExample("Education provides opportunities.", "تعلیم مواقع فراہم کرتی ہے۔"),
                DailyVerbExample("He provides every day.", "وہ ہر روز مہیا کرنا کرتا ہے۔"),
                DailyVerbExample("They are providing now.", "وہ ابھی مہیا کرنا کر رہے ہیں۔"),
                DailyVerbExample("We provided yesterday.", "ہم نے کل مہیا کرنا کیا۔"),
                DailyVerbExample("She has provided.", "وہ مہیا کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"provide\" with I, you, we, and they. Use \"provides\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “provide” اور “He, she, it” کے ساتھ “provides” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_47_sit",
            baseForm = "sit",
            thirdPerson = "sits",
            presentParticiple = "sitting",
            pastSimple = "sat",
            pastParticiple = "sat",
            urduMeaning = "بیٹھنا",
            examples = listOf(
                DailyVerbExample("Sit down please.", "براہ کرم بیٹھ جائیں۔"),
                DailyVerbExample("He sits every day.", "وہ ہر روز بیٹھنا کرتا ہے۔"),
                DailyVerbExample("They are sitting now.", "وہ ابھی بیٹھنا کر رہے ہیں۔"),
                DailyVerbExample("We sat yesterday.", "ہم نے کل بیٹھنا کیا۔"),
                DailyVerbExample("She has sat.", "وہ بیٹھنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"sit\" with I, you, we, and they. Use \"sits\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “sit” اور “He, she, it” کے ساتھ “sits” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_48_stand",
            baseForm = "stand",
            thirdPerson = "stands",
            presentParticiple = "standing",
            pastSimple = "stood",
            pastParticiple = "stood",
            urduMeaning = "کھڑے ہونا",
            examples = listOf(
                DailyVerbExample("Stand up straight.", "سیدھے کھڑے رہیں۔"),
                DailyVerbExample("He stands every day.", "وہ ہر روز کھڑے ہونا کرتا ہے۔"),
                DailyVerbExample("They are standing now.", "وہ ابھی کھڑے ہونا کر رہے ہیں۔"),
                DailyVerbExample("We stood yesterday.", "ہم نے کل کھڑے ہونا کیا۔"),
                DailyVerbExample("She has stood.", "وہ کھڑے ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"stand\" with I, you, we, and they. Use \"stands\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “stand” اور “He, she, it” کے ساتھ “stands” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_49_lose",
            baseForm = "lose",
            thirdPerson = "loses",
            presentParticiple = "losing",
            pastSimple = "lost",
            pastParticiple = "lost",
            urduMeaning = "کھونا",
            examples = listOf(
                DailyVerbExample("Don't lose hope.", "امید نہ ہاریں۔"),
                DailyVerbExample("He loses every day.", "وہ ہر روز کھونا کرتا ہے۔"),
                DailyVerbExample("They are losing now.", "وہ ابھی کھونا کر رہے ہیں۔"),
                DailyVerbExample("We lost yesterday.", "ہم نے کل کھونا کیا۔"),
                DailyVerbExample("She has lost.", "وہ کھونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"lose\" with I, you, we, and they. Use \"loses\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “lose” اور “He, she, it” کے ساتھ “loses” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_50_include",
            baseForm = "include",
            thirdPerson = "includes",
            presentParticiple = "including",
            pastSimple = "included",
            pastParticiple = "included",
            urduMeaning = "شامل کرنا",
            examples = listOf(
                DailyVerbExample("Price includes tax.", "قیمت میں ٹیکس شامل ہے۔"),
                DailyVerbExample("He includes every day.", "وہ ہر روز شامل کرنا کرتا ہے۔"),
                DailyVerbExample("They are including now.", "وہ ابھی شامل کرنا کر رہے ہیں۔"),
                DailyVerbExample("We included yesterday.", "ہم نے کل شامل کرنا کیا۔"),
                DailyVerbExample("She has included.", "وہ شامل کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"include\" with I, you, we, and they. Use \"includes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “include” اور “He, she, it” کے ساتھ “includes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_51_continue",
            baseForm = "continue",
            thirdPerson = "continues",
            presentParticiple = "continuing",
            pastSimple = "continued",
            pastParticiple = "continued",
            urduMeaning = "جاری رکھنا",
            examples = listOf(
                DailyVerbExample("Continue reading.", "پڑھنا جاری رکھیں۔"),
                DailyVerbExample("He continues every day.", "وہ ہر روز جاری رکھنا کرتا ہے۔"),
                DailyVerbExample("They are continuing now.", "وہ ابھی جاری رکھنا کر رہے ہیں۔"),
                DailyVerbExample("We continued yesterday.", "ہم نے کل جاری رکھنا کیا۔"),
                DailyVerbExample("She has continued.", "وہ جاری رکھنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"continue\" with I, you, we, and they. Use \"continues\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “continue” اور “He, she, it” کے ساتھ “continues” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_52_set",
            baseForm = "set",
            thirdPerson = "sets",
            presentParticiple = "setting",
            pastSimple = "set",
            pastParticiple = "set",
            urduMeaning = "قائم کرنا",
            examples = listOf(
                DailyVerbExample("Set the alarm.", "الارم لگائیں۔"),
                DailyVerbExample("He sets every day.", "وہ ہر روز قائم کرنا کرتا ہے۔"),
                DailyVerbExample("They are setting now.", "وہ ابھی قائم کرنا کر رہے ہیں۔"),
                DailyVerbExample("We set yesterday.", "ہم نے کل قائم کرنا کیا۔"),
                DailyVerbExample("She has set.", "وہ قائم کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"set\" with I, you, we, and they. Use \"sets\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “set” اور “He, she, it” کے ساتھ “sets” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_53_change",
            baseForm = "change",
            thirdPerson = "changes",
            presentParticiple = "changing",
            pastSimple = "changed",
            pastParticiple = "changed",
            urduMeaning = "تبدیل کرنا",
            examples = listOf(
                DailyVerbExample("Change your mind.", "اپنی سوچ بدلیں۔"),
                DailyVerbExample("He changes every day.", "وہ ہر روز تبدیل کرنا کرتا ہے۔"),
                DailyVerbExample("They are changing now.", "وہ ابھی تبدیل کرنا کر رہے ہیں۔"),
                DailyVerbExample("We changed yesterday.", "ہم نے کل تبدیل کرنا کیا۔"),
                DailyVerbExample("She has changed.", "وہ تبدیل کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"change\" with I, you, we, and they. Use \"changes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “change” اور “He, she, it” کے ساتھ “changes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_54_lead",
            baseForm = "lead",
            thirdPerson = "leads",
            presentParticiple = "leading",
            pastSimple = "led",
            pastParticiple = "led",
            urduMeaning = "رہنمائی کرنا",
            examples = listOf(
                DailyVerbExample("He leads the team.", "وہ ٹیم کی رہنمائی کرتا ہے۔"),
                DailyVerbExample("He leads every day.", "وہ ہر روز رہنمائی کرنا کرتا ہے۔"),
                DailyVerbExample("They are leading now.", "وہ ابھی رہنمائی کرنا کر رہے ہیں۔"),
                DailyVerbExample("We led yesterday.", "ہم نے کل رہنمائی کرنا کیا۔"),
                DailyVerbExample("She has led.", "وہ رہنمائی کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"lead\" with I, you, we, and they. Use \"leads\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “lead” اور “He, she, it” کے ساتھ “leads” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_55_follow",
            baseForm = "follow",
            thirdPerson = "follows",
            presentParticiple = "following",
            pastSimple = "followed",
            pastParticiple = "followed",
            urduMeaning = "پیروی کرنا",
            examples = listOf(
                DailyVerbExample("Follow the rules.", "قواعد کی پیروی کریں۔"),
                DailyVerbExample("He follows every day.", "وہ ہر روز پیروی کرنا کرتا ہے۔"),
                DailyVerbExample("They are following now.", "وہ ابھی پیروی کرنا کر رہے ہیں۔"),
                DailyVerbExample("We followed yesterday.", "ہم نے کل پیروی کرنا کیا۔"),
                DailyVerbExample("She has followed.", "وہ پیروی کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"follow\" with I, you, we, and they. Use \"follows\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “follow” اور “He, she, it” کے ساتھ “follows” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_56_stop",
            baseForm = "stop",
            thirdPerson = "stops",
            presentParticiple = "stopping",
            pastSimple = "stopped",
            pastParticiple = "stopped",
            urduMeaning = "روکنا",
            examples = listOf(
                DailyVerbExample("Stop the car.", "گاڑی روکیں۔"),
                DailyVerbExample("He stops every day.", "وہ ہر روز روکنا کرتا ہے۔"),
                DailyVerbExample("They are stopping now.", "وہ ابھی روکنا کر رہے ہیں۔"),
                DailyVerbExample("We stopped yesterday.", "ہم نے کل روکنا کیا۔"),
                DailyVerbExample("She has stopped.", "وہ روکنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"stop\" with I, you, we, and they. Use \"stops\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “stop” اور “He, she, it” کے ساتھ “stops” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_57_create",
            baseForm = "create",
            thirdPerson = "creates",
            presentParticiple = "creating",
            pastSimple = "created",
            pastParticiple = "created",
            urduMeaning = "تخلیق کرنا",
            examples = listOf(
                DailyVerbExample("Artists create art.", "فنکار آرٹ تخلیق کرتے ہیں۔"),
                DailyVerbExample("He creates every day.", "وہ ہر روز تخلیق کرنا کرتا ہے۔"),
                DailyVerbExample("They are creating now.", "وہ ابھی تخلیق کرنا کر رہے ہیں۔"),
                DailyVerbExample("We created yesterday.", "ہم نے کل تخلیق کرنا کیا۔"),
                DailyVerbExample("She has created.", "وہ تخلیق کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"create\" with I, you, we, and they. Use \"creates\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “create” اور “He, she, it” کے ساتھ “creates” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_58_allow",
            baseForm = "allow",
            thirdPerson = "allows",
            presentParticiple = "allowing",
            pastSimple = "allowed",
            pastParticiple = "allowed",
            urduMeaning = "اجازت دینا",
            examples = listOf(
                DailyVerbExample("Smoking is not allowed.", "سیگریٹ نوشی کی اجازت نہیں ہے۔"),
                DailyVerbExample("He allows every day.", "وہ ہر روز اجازت دینا کرتا ہے۔"),
                DailyVerbExample("They are allowing now.", "وہ ابھی اجازت دینا کر رہے ہیں۔"),
                DailyVerbExample("We allowed yesterday.", "ہم نے کل اجازت دینا کیا۔"),
                DailyVerbExample("She has allowed.", "وہ اجازت دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"allow\" with I, you, we, and they. Use \"allows\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “allow” اور “He, she, it” کے ساتھ “allows” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_59_add",
            baseForm = "add",
            thirdPerson = "adds",
            presentParticiple = "adding",
            pastSimple = "added",
            pastParticiple = "added",
            urduMeaning = "شامل کرنا",
            examples = listOf(
                DailyVerbExample("Add some sugar.", "تھوڑی چینی شامل کریں۔"),
                DailyVerbExample("He adds every day.", "وہ ہر روز شامل کرنا کرتا ہے۔"),
                DailyVerbExample("They are adding now.", "وہ ابھی شامل کرنا کر رہے ہیں۔"),
                DailyVerbExample("We added yesterday.", "ہم نے کل شامل کرنا کیا۔"),
                DailyVerbExample("She has added.", "وہ شامل کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"add\" with I, you, we, and they. Use \"adds\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “add” اور “He, she, it” کے ساتھ “adds” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_60_spend",
            baseForm = "spend",
            thirdPerson = "spends",
            presentParticiple = "spending",
            pastSimple = "spent",
            pastParticiple = "spent",
            urduMeaning = "خرچ کرنا",
            examples = listOf(
                DailyVerbExample("I spend time reading.", "میں وقت مطالعہ میں گزارتا ہوں۔"),
                DailyVerbExample("He spends every day.", "وہ ہر روز خرچ کرنا کرتا ہے۔"),
                DailyVerbExample("They are spending now.", "وہ ابھی خرچ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We spent yesterday.", "ہم نے کل خرچ کرنا کیا۔"),
                DailyVerbExample("She has spent.", "وہ خرچ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"spend\" with I, you, we, and they. Use \"spends\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “spend” اور “He, she, it” کے ساتھ “spends” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_61_grow",
            baseForm = "grow",
            thirdPerson = "grows",
            presentParticiple = "growing",
            pastSimple = "grew",
            pastParticiple = "grown",
            urduMeaning = "اگنا",
            examples = listOf(
                DailyVerbExample("Plants grow fast.", "پودے تیزی سے بڑھتے ہیں۔"),
                DailyVerbExample("He grows every day.", "وہ ہر روز اگنا کرتا ہے۔"),
                DailyVerbExample("They are growing now.", "وہ ابھی اگنا کر رہے ہیں۔"),
                DailyVerbExample("We grew yesterday.", "ہم نے کل اگنا کیا۔"),
                DailyVerbExample("She has grown.", "وہ اگنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"grow\" with I, you, we, and they. Use \"grows\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “grow” اور “He, she, it” کے ساتھ “grows” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_62_win",
            baseForm = "win",
            thirdPerson = "wins",
            presentParticiple = "winning",
            pastSimple = "won",
            pastParticiple = "won",
            urduMeaning = "جیتنا",
            examples = listOf(
                DailyVerbExample("We won the match.", "ہم میچ جیت گئے۔"),
                DailyVerbExample("He wins every day.", "وہ ہر روز جیتنا کرتا ہے۔"),
                DailyVerbExample("They are winning now.", "وہ ابھی جیتنا کر رہے ہیں۔"),
                DailyVerbExample("We won yesterday.", "ہم نے کل جیتنا کیا۔"),
                DailyVerbExample("She has won.", "وہ جیتنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"win\" with I, you, we, and they. Use \"wins\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “win” اور “He, she, it” کے ساتھ “wins” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_63_offer",
            baseForm = "offer",
            thirdPerson = "offers",
            presentParticiple = "offering",
            pastSimple = "offered",
            pastParticiple = "offered",
            urduMeaning = "پیشکش کرنا",
            examples = listOf(
                DailyVerbExample("He offered help.", "اس نے مدد کی پیشکش کی۔"),
                DailyVerbExample("He offers every day.", "وہ ہر روز پیشکش کرنا کرتا ہے۔"),
                DailyVerbExample("They are offering now.", "وہ ابھی پیشکش کرنا کر رہے ہیں۔"),
                DailyVerbExample("We offered yesterday.", "ہم نے کل پیشکش کرنا کیا۔"),
                DailyVerbExample("She has offered.", "وہ پیشکش کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"offer\" with I, you, we, and they. Use \"offers\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “offer” اور “He, she, it” کے ساتھ “offers” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_64_remember",
            baseForm = "remember",
            thirdPerson = "remembers",
            presentParticiple = "remembering",
            pastSimple = "remembered",
            pastParticiple = "remembered",
            urduMeaning = "یاد رکھنا",
            examples = listOf(
                DailyVerbExample("I remember you.", "مجھے آپ یاد ہیں۔"),
                DailyVerbExample("He remembers every day.", "وہ ہر روز یاد رکھنا کرتا ہے۔"),
                DailyVerbExample("They are remembering now.", "وہ ابھی یاد رکھنا کر رہے ہیں۔"),
                DailyVerbExample("We remembered yesterday.", "ہم نے کل یاد رکھنا کیا۔"),
                DailyVerbExample("She has remembered.", "وہ یاد رکھنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"remember\" with I, you, we, and they. Use \"remembers\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “remember” اور “He, she, it” کے ساتھ “remembers” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_65_wait",
            baseForm = "wait",
            thirdPerson = "waits",
            presentParticiple = "waiting",
            pastSimple = "waited",
            pastParticiple = "waited",
            urduMeaning = "انتظار کرنا",
            examples = listOf(
                DailyVerbExample("Wait for me.", "میرا انتظار کریں۔"),
                DailyVerbExample("He waits every day.", "وہ ہر روز انتظار کرنا کرتا ہے۔"),
                DailyVerbExample("They are waiting now.", "وہ ابھی انتظار کرنا کر رہے ہیں۔"),
                DailyVerbExample("We waited yesterday.", "ہم نے کل انتظار کرنا کیا۔"),
                DailyVerbExample("She has waited.", "وہ انتظار کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"wait\" with I, you, we, and they. Use \"waits\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “wait” اور “He, she, it” کے ساتھ “waits” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_66_serve",
            baseForm = "serve",
            thirdPerson = "serves",
            presentParticiple = "serving",
            pastSimple = "served",
            pastParticiple = "served",
            urduMeaning = "خدمت کرنا",
            examples = listOf(
                DailyVerbExample("Serve food hot.", "کھانا گرم پیش کریں۔"),
                DailyVerbExample("He serves every day.", "وہ ہر روز خدمت کرنا کرتا ہے۔"),
                DailyVerbExample("They are serving now.", "وہ ابھی خدمت کرنا کر رہے ہیں۔"),
                DailyVerbExample("We served yesterday.", "ہم نے کل خدمت کرنا کیا۔"),
                DailyVerbExample("She has served.", "وہ خدمت کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"serve\" with I, you, we, and they. Use \"serves\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “serve” اور “He, she, it” کے ساتھ “serves” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_67_die",
            baseForm = "die",
            thirdPerson = "dies",
            presentParticiple = "dying",
            pastSimple = "died",
            pastParticiple = "died",
            urduMeaning = "مرنا",
            examples = listOf(
                DailyVerbExample("Flowers die without water.", "پانی کے بغیر پھول مر جاتے ہیں۔"),
                DailyVerbExample("He dies every day.", "وہ ہر روز مرنا کرتا ہے۔"),
                DailyVerbExample("They are dying now.", "وہ ابھی مرنا کر رہے ہیں۔"),
                DailyVerbExample("We died yesterday.", "ہم نے کل مرنا کیا۔"),
                DailyVerbExample("She has died.", "وہ مرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"die\" with I, you, we, and they. Use \"dies\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “die” اور “He, she, it” کے ساتھ “dies” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_68_expect",
            baseForm = "expect",
            thirdPerson = "expects",
            presentParticiple = "expecting",
            pastSimple = "expected",
            pastParticiple = "expected",
            urduMeaning = "توقع رکھنا",
            examples = listOf(
                DailyVerbExample("We expect good results.", "ہم اچھے نتائج کی توقع رکھتے ہیں۔"),
                DailyVerbExample("He expects every day.", "وہ ہر روز توقع رکھنا کرتا ہے۔"),
                DailyVerbExample("They are expecting now.", "وہ ابھی توقع رکھنا کر رہے ہیں۔"),
                DailyVerbExample("We expected yesterday.", "ہم نے کل توقع رکھنا کیا۔"),
                DailyVerbExample("She has expected.", "وہ توقع رکھنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"expect\" with I, you, we, and they. Use \"expects\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “expect” اور “He, she, it” کے ساتھ “expects” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_69_stay",
            baseForm = "stay",
            thirdPerson = "stays",
            presentParticiple = "staying",
            pastSimple = "stayed",
            pastParticiple = "stayed",
            urduMeaning = "قیام کرنا",
            examples = listOf(
                DailyVerbExample("Stay safe.", "محفوظ رہیں۔"),
                DailyVerbExample("He stays every day.", "وہ ہر روز قیام کرنا کرتا ہے۔"),
                DailyVerbExample("They are staying now.", "وہ ابھی قیام کرنا کر رہے ہیں۔"),
                DailyVerbExample("We stayed yesterday.", "ہم نے کل قیام کرنا کیا۔"),
                DailyVerbExample("She has stayed.", "وہ قیام کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"stay\" with I, you, we, and they. Use \"stays\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “stay” اور “He, she, it” کے ساتھ “stays” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_70_fall",
            baseForm = "fall",
            thirdPerson = "falls",
            presentParticiple = "falling",
            pastSimple = "fell",
            pastParticiple = "fallen",
            urduMeaning = "گرنا",
            examples = listOf(
                DailyVerbExample("Rain falls from clouds.", "بادلوں سے بارش گرتی ہے۔"),
                DailyVerbExample("He falls every day.", "وہ ہر روز گرنا کرتا ہے۔"),
                DailyVerbExample("They are falling now.", "وہ ابھی گرنا کر رہے ہیں۔"),
                DailyVerbExample("We fell yesterday.", "ہم نے کل گرنا کیا۔"),
                DailyVerbExample("She has fallen.", "وہ گرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"fall\" with I, you, we, and they. Use \"falls\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “fall” اور “He, she, it” کے ساتھ “falls” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_71_cut",
            baseForm = "cut",
            thirdPerson = "cuts",
            presentParticiple = "cutting",
            pastSimple = "cut",
            pastParticiple = "cut",
            urduMeaning = "کاٹنا",
            examples = listOf(
                DailyVerbExample("Cut the fruit.", "پھل کاٹیں۔"),
                DailyVerbExample("He cuts every day.", "وہ ہر روز کاٹنا کرتا ہے۔"),
                DailyVerbExample("They are cutting now.", "وہ ابھی کاٹنا کر رہے ہیں۔"),
                DailyVerbExample("We cut yesterday.", "ہم نے کل کاٹنا کیا۔"),
                DailyVerbExample("She has cut.", "وہ کاٹنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"cut\" with I, you, we, and they. Use \"cuts\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “cut” اور “He, she, it” کے ساتھ “cuts” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_72_reach",
            baseForm = "reach",
            thirdPerson = "reaches",
            presentParticiple = "reaching",
            pastSimple = "reached",
            pastParticiple = "reached",
            urduMeaning = "پہنچنا",
            examples = listOf(
                DailyVerbExample("Reach home safely.", "خیریت سے گھر پہنچیں۔"),
                DailyVerbExample("He reaches every day.", "وہ ہر روز پہنچنا کرتا ہے۔"),
                DailyVerbExample("They are reaching now.", "وہ ابھی پہنچنا کر رہے ہیں۔"),
                DailyVerbExample("We reached yesterday.", "ہم نے کل پہنچنا کیا۔"),
                DailyVerbExample("She has reached.", "وہ پہنچنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"reach\" with I, you, we, and they. Use \"reaches\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “reach” اور “He, she, it” کے ساتھ “reaches” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_73_kill",
            baseForm = "kill",
            thirdPerson = "kills",
            presentParticiple = "killing",
            pastSimple = "killed",
            pastParticiple = "killed",
            urduMeaning = "مارنا",
            examples = listOf(
                DailyVerbExample("Pesticides kill pests.", "کیڑے مار ادویات کیڑوں کو مارتی ہیں۔"),
                DailyVerbExample("He kills every day.", "وہ ہر روز مارنا کرتا ہے۔"),
                DailyVerbExample("They are killing now.", "وہ ابھی مارنا کر رہے ہیں۔"),
                DailyVerbExample("We killed yesterday.", "ہم نے کل مارنا کیا۔"),
                DailyVerbExample("She has killed.", "وہ مارنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"kill\" with I, you, we, and they. Use \"kills\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “kill” اور “He, she, it” کے ساتھ “kills” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_74_remain",
            baseForm = "remain",
            thirdPerson = "remains",
            presentParticiple = "remaining",
            pastSimple = "remained",
            pastParticiple = "remained",
            urduMeaning = "باقی رہنا",
            examples = listOf(
                DailyVerbExample("Remain calm.", "پرامید اور پرسکون رہیں۔"),
                DailyVerbExample("He remains every day.", "وہ ہر روز باقی رہنا کرتا ہے۔"),
                DailyVerbExample("They are remaining now.", "وہ ابھی باقی رہنا کر رہے ہیں۔"),
                DailyVerbExample("We remained yesterday.", "ہم نے کل باقی رہنا کیا۔"),
                DailyVerbExample("She has remained.", "وہ باقی رہنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"remain\" with I, you, we, and they. Use \"remains\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “remain” اور “He, she, it” کے ساتھ “remains” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_75_suggest",
            baseForm = "suggest",
            thirdPerson = "suggests",
            presentParticiple = "suggesting",
            pastSimple = "suggested",
            pastParticiple = "suggested",
            urduMeaning = "تجویز کرنا",
            examples = listOf(
                DailyVerbExample("I suggest a break.", "میں وقفے کی تجویز دیتا ہوں۔"),
                DailyVerbExample("He suggests every day.", "وہ ہر روز تجویز کرنا کرتا ہے۔"),
                DailyVerbExample("They are suggesting now.", "وہ ابھی تجویز کرنا کر رہے ہیں۔"),
                DailyVerbExample("We suggested yesterday.", "ہم نے کل تجویز کرنا کیا۔"),
                DailyVerbExample("She has suggested.", "وہ تجویز کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"suggest\" with I, you, we, and they. Use \"suggests\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “suggest” اور “He, she, it” کے ساتھ “suggests” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_76_raise",
            baseForm = "raise",
            thirdPerson = "raises",
            presentParticiple = "raising",
            pastSimple = "raised",
            pastParticiple = "raised",
            urduMeaning = "اٹھانا",
            examples = listOf(
                DailyVerbExample("Raise your hand.", "اپنا ہاتھ اٹھائیں۔"),
                DailyVerbExample("He raises every day.", "وہ ہر روز اٹھانا کرتا ہے۔"),
                DailyVerbExample("They are raising now.", "وہ ابھی اٹھانا کر رہے ہیں۔"),
                DailyVerbExample("We raised yesterday.", "ہم نے کل اٹھانا کیا۔"),
                DailyVerbExample("She has raised.", "وہ اٹھانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"raise\" with I, you, we, and they. Use \"raises\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “raise” اور “He, she, it” کے ساتھ “raises” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_77_pass",
            baseForm = "pass",
            thirdPerson = "passes",
            presentParticiple = "passing",
            pastSimple = "passed",
            pastParticiple = "passed",
            urduMeaning = "گزرنا",
            examples = listOf(
                DailyVerbExample("He passed the test.", "اس نے ٹیسٹ پاس کر لیا۔"),
                DailyVerbExample("He passes every day.", "وہ ہر روز گزرنا کرتا ہے۔"),
                DailyVerbExample("They are passing now.", "وہ ابھی گزرنا کر رہے ہیں۔"),
                DailyVerbExample("We passed yesterday.", "ہم نے کل گزرنا کیا۔"),
                DailyVerbExample("She has passed.", "وہ گزرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"pass\" with I, you, we, and they. Use \"passes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “pass” اور “He, she, it” کے ساتھ “passes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_78_sell",
            baseForm = "sell",
            thirdPerson = "sells",
            presentParticiple = "selling",
            pastSimple = "sold",
            pastParticiple = "sold",
            urduMeaning = "بیچنا",
            examples = listOf(
                DailyVerbExample("They sell fresh bread.", "وہ تازہ روٹی بیچتے ہیں۔"),
                DailyVerbExample("He sells every day.", "وہ ہر روز بیچنا کرتا ہے۔"),
                DailyVerbExample("They are selling now.", "وہ ابھی بیچنا کر رہے ہیں۔"),
                DailyVerbExample("We sold yesterday.", "ہم نے کل بیچنا کیا۔"),
                DailyVerbExample("She has sold.", "وہ بیچنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"sell\" with I, you, we, and they. Use \"sells\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “sell” اور “He, she, it” کے ساتھ “sells” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_79_require",
            baseForm = "require",
            thirdPerson = "requires",
            presentParticiple = "requiring",
            pastSimple = "required",
            pastParticiple = "required",
            urduMeaning = "تقاضا کرنا",
            examples = listOf(
                DailyVerbExample("It requires attention.", "اس پر توجہ کی ضرورت ہے۔"),
                DailyVerbExample("He requires every day.", "وہ ہر روز تقاضا کرنا کرتا ہے۔"),
                DailyVerbExample("They are requiring now.", "وہ ابھی تقاضا کرنا کر رہے ہیں۔"),
                DailyVerbExample("We required yesterday.", "ہم نے کل تقاضا کرنا کیا۔"),
                DailyVerbExample("She has required.", "وہ تقاضا کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"require\" with I, you, we, and they. Use \"requires\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “require” اور “He, she, it” کے ساتھ “requires” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_80_report",
            baseForm = "report",
            thirdPerson = "reports",
            presentParticiple = "reporting",
            pastSimple = "reported",
            pastParticiple = "reported",
            urduMeaning = "رپورٹ کرنا",
            examples = listOf(
                DailyVerbExample("Report any problem.", "کسی بھی مسئلے کی رپورٹ کریں۔"),
                DailyVerbExample("He reports every day.", "وہ ہر روز رپورٹ کرنا کرتا ہے۔"),
                DailyVerbExample("They are reporting now.", "وہ ابھی رپورٹ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We reported yesterday.", "ہم نے کل رپورٹ کرنا کیا۔"),
                DailyVerbExample("She has reported.", "وہ رپورٹ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"report\" with I, you, we, and they. Use \"reports\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “report” اور “He, she, it” کے ساتھ “reports” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_81_decide",
            baseForm = "decide",
            thirdPerson = "decides",
            presentParticiple = "deciding",
            pastSimple = "decided",
            pastParticiple = "decided",
            urduMeaning = "فیصلہ کرنا",
            examples = listOf(
                DailyVerbExample("Decide quickly.", "جلدی فیصلہ کریں۔"),
                DailyVerbExample("He decides every day.", "وہ ہر روز فیصلہ کرنا کرتا ہے۔"),
                DailyVerbExample("They are deciding now.", "وہ ابھی فیصلہ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We decided yesterday.", "ہم نے کل فیصلہ کرنا کیا۔"),
                DailyVerbExample("She has decided.", "وہ فیصلہ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"decide\" with I, you, we, and they. Use \"decides\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “decide” اور “He, she, it” کے ساتھ “decides” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_82_pull",
            baseForm = "pull",
            thirdPerson = "pulls",
            presentParticiple = "pulling",
            pastSimple = "pulled",
            pastParticiple = "pulled",
            urduMeaning = "کھینچنا",
            examples = listOf(
                DailyVerbExample("Pull the handle.", "ہینڈل کھینچیں۔"),
                DailyVerbExample("He pulls every day.", "وہ ہر روز کھینچنا کرتا ہے۔"),
                DailyVerbExample("They are pulling now.", "وہ ابھی کھینچنا کر رہے ہیں۔"),
                DailyVerbExample("We pulled yesterday.", "ہم نے کل کھینچنا کیا۔"),
                DailyVerbExample("She has pulled.", "وہ کھینچنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"pull\" with I, you, we, and they. Use \"pulls\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “pull” اور “He, she, it” کے ساتھ “pulls” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_83_push",
            baseForm = "push",
            thirdPerson = "pushes",
            presentParticiple = "pushing",
            pastSimple = "pushed",
            pastParticiple = "pushed",
            urduMeaning = "دھکا دینا",
            examples = listOf(
                DailyVerbExample("Push the button.", "بٹن دبائیں۔"),
                DailyVerbExample("He pushes every day.", "وہ ہر روز دھکا دینا کرتا ہے۔"),
                DailyVerbExample("They are pushing now.", "وہ ابھی دھکا دینا کر رہے ہیں۔"),
                DailyVerbExample("We pushed yesterday.", "ہم نے کل دھکا دینا کیا۔"),
                DailyVerbExample("She has pushed.", "وہ دھکا دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"push\" with I, you, we, and they. Use \"pushes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “push” اور “He, she, it” کے ساتھ “pushes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_84_carry",
            baseForm = "carry",
            thirdPerson = "carries",
            presentParticiple = "carrying",
            pastSimple = "carried",
            pastParticiple = "carried",
            urduMeaning = "لے جانا",
            examples = listOf(
                DailyVerbExample("Carry this bag.", "یہ بیگ اٹھاؤ۔"),
                DailyVerbExample("He carries every day.", "وہ ہر روز لے جانا کرتا ہے۔"),
                DailyVerbExample("They are carrying now.", "وہ ابھی لے جانا کر رہے ہیں۔"),
                DailyVerbExample("We carried yesterday.", "ہم نے کل لے جانا کیا۔"),
                DailyVerbExample("She has carried.", "وہ لے جانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"carry\" with I, you, we, and they. Use \"carries\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “carry” اور “He, she, it” کے ساتھ “carries” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_85_talk",
            baseForm = "talk",
            thirdPerson = "talks",
            presentParticiple = "talking",
            pastSimple = "talked",
            pastParticiple = "talked",
            urduMeaning = "بات کرنا",
            examples = listOf(
                DailyVerbExample("Let us talk.", "آؤ بات کریں۔"),
                DailyVerbExample("He talks every day.", "وہ ہر روز بات کرنا کرتا ہے۔"),
                DailyVerbExample("They are talking now.", "وہ ابھی بات کرنا کر رہے ہیں۔"),
                DailyVerbExample("We talked yesterday.", "ہم نے کل بات کرنا کیا۔"),
                DailyVerbExample("She has talked.", "وہ بات کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"talk\" with I, you, we, and they. Use \"talks\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “talk” اور “He, she, it” کے ساتھ “talks” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_86_appear",
            baseForm = "appear",
            thirdPerson = "appears",
            presentParticiple = "appearing",
            pastSimple = "appeared",
            pastParticiple = "appeared",
            urduMeaning = "ظاہر ہونا",
            examples = listOf(
                DailyVerbExample("Stars appear at night.", "رات کو ستارے ظاہر ہوتے ہیں۔"),
                DailyVerbExample("He appears every day.", "وہ ہر روز ظاہر ہونا کرتا ہے۔"),
                DailyVerbExample("They are appearing now.", "وہ ابھی ظاہر ہونا کر رہے ہیں۔"),
                DailyVerbExample("We appeared yesterday.", "ہم نے کل ظاہر ہونا کیا۔"),
                DailyVerbExample("She has appeared.", "وہ ظاہر ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"appear\" with I, you, we, and they. Use \"appears\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “appear” اور “He, she, it” کے ساتھ “appears” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_87_explain",
            baseForm = "explain",
            thirdPerson = "explains",
            presentParticiple = "explaining",
            pastSimple = "explained",
            pastParticiple = "explained",
            urduMeaning = "وضاحت کرنا",
            examples = listOf(
                DailyVerbExample("Explain the answer.", "جواب کی وضاحت کریں۔"),
                DailyVerbExample("He explains every day.", "وہ ہر روز وضاحت کرنا کرتا ہے۔"),
                DailyVerbExample("They are explaining now.", "وہ ابھی وضاحت کرنا کر رہے ہیں۔"),
                DailyVerbExample("We explained yesterday.", "ہم نے کل وضاحت کرنا کیا۔"),
                DailyVerbExample("She has explained.", "وہ وضاحت کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"explain\" with I, you, we, and they. Use \"explains\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “explain” اور “He, she, it” کے ساتھ “explains” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_88_hope",
            baseForm = "hope",
            thirdPerson = "hopes",
            presentParticiple = "hoping",
            pastSimple = "hoped",
            pastParticiple = "hoped",
            urduMeaning = "امید رکھنا",
            examples = listOf(
                DailyVerbExample("I hope so.", "مجھے ایسی ہی امید ہے۔"),
                DailyVerbExample("He hopes every day.", "وہ ہر روز امید رکھنا کرتا ہے۔"),
                DailyVerbExample("They are hoping now.", "وہ ابھی امید رکھنا کر رہے ہیں۔"),
                DailyVerbExample("We hoped yesterday.", "ہم نے کل امید رکھنا کیا۔"),
                DailyVerbExample("She has hoped.", "وہ امید رکھنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"hope\" with I, you, we, and they. Use \"hopes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “hope” اور “He, she, it” کے ساتھ “hopes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_89_develop",
            baseForm = "develop",
            thirdPerson = "develops",
            presentParticiple = "developing",
            pastSimple = "developed",
            pastParticiple = "developed",
            urduMeaning = "ترقی دینا",
            examples = listOf(
                DailyVerbExample("Develop your skills.", "اپنی مہارتیں تیار کریں۔"),
                DailyVerbExample("He develops every day.", "وہ ہر روز ترقی دینا کرتا ہے۔"),
                DailyVerbExample("They are developing now.", "وہ ابھی ترقی دینا کر رہے ہیں۔"),
                DailyVerbExample("We developed yesterday.", "ہم نے کل ترقی دینا کیا۔"),
                DailyVerbExample("She has developed.", "وہ ترقی دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"develop\" with I, you, we, and they. Use \"develops\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “develop” اور “He, she, it” کے ساتھ “develops” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_90_agree",
            baseForm = "agree",
            thirdPerson = "agrees",
            presentParticiple = "agreeing",
            pastSimple = "agreed",
            pastParticiple = "agreed",
            urduMeaning = "متفق ہونا",
            examples = listOf(
                DailyVerbExample("I agree with you.", "میں آپ سے متفق ہوں۔"),
                DailyVerbExample("He agrees every day.", "وہ ہر روز متفق ہونا کرتا ہے۔"),
                DailyVerbExample("They are agreeing now.", "وہ ابھی متفق ہونا کر رہے ہیں۔"),
                DailyVerbExample("We agreed yesterday.", "ہم نے کل متفق ہونا کیا۔"),
                DailyVerbExample("She has agreed.", "وہ متفق ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"agree\" with I, you, we, and they. Use \"agrees\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “agree” اور “He, she, it” کے ساتھ “agrees” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_91_disagree",
            baseForm = "disagree",
            thirdPerson = "disagrees",
            presentParticiple = "disagreeing",
            pastSimple = "disagreed",
            pastParticiple = "disagreed",
            urduMeaning = "اختلاف کرنا",
            examples = listOf(
                DailyVerbExample("We disagree politely.", "ہم شائستگی سے اختلاف کرتے ہیں۔"),
                DailyVerbExample("He disagrees every day.", "وہ ہر روز اختلاف کرنا کرتا ہے۔"),
                DailyVerbExample("They are disagreeing now.", "وہ ابھی اختلاف کرنا کر رہے ہیں۔"),
                DailyVerbExample("We disagreed yesterday.", "ہم نے کل اختلاف کرنا کیا۔"),
                DailyVerbExample("She has disagreed.", "وہ اختلاف کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"disagree\" with I, you, we, and they. Use \"disagrees\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “disagree” اور “He, she, it” کے ساتھ “disagrees” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_92_accept",
            baseForm = "accept",
            thirdPerson = "accepts",
            presentParticiple = "accepting",
            pastSimple = "accepted",
            pastParticiple = "accepted",
            urduMeaning = "قبول کرنا",
            examples = listOf(
                DailyVerbExample("Accept the gift.", "تحفہ قبول کریں۔"),
                DailyVerbExample("He accepts every day.", "وہ ہر روز قبول کرنا کرتا ہے۔"),
                DailyVerbExample("They are accepting now.", "وہ ابھی قبول کرنا کر رہے ہیں۔"),
                DailyVerbExample("We accepted yesterday.", "ہم نے کل قبول کرنا کیا۔"),
                DailyVerbExample("She has accepted.", "وہ قبول کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"accept\" with I, you, we, and they. Use \"accepts\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “accept” اور “He, she, it” کے ساتھ “accepts” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_93_refuse",
            baseForm = "refuse",
            thirdPerson = "refuses",
            presentParticiple = "refusing",
            pastSimple = "refused",
            pastParticiple = "refused",
            urduMeaning = "انکار کرنا",
            examples = listOf(
                DailyVerbExample("He refused to go.", "اس نے جانے سے انکار کر دیا۔"),
                DailyVerbExample("He refuses every day.", "وہ ہر روز انکار کرنا کرتا ہے۔"),
                DailyVerbExample("They are refusing now.", "وہ ابھی انکار کرنا کر رہے ہیں۔"),
                DailyVerbExample("We refused yesterday.", "ہم نے کل انکار کرنا کیا۔"),
                DailyVerbExample("She has refused.", "وہ انکار کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"refuse\" with I, you, we, and they. Use \"refuses\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “refuse” اور “He, she, it” کے ساتھ “refuses” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_94_clean",
            baseForm = "clean",
            thirdPerson = "cleans",
            presentParticiple = "cleaning",
            pastSimple = "cleaned",
            pastParticiple = "cleaned",
            urduMeaning = "صاف کرنا",
            examples = listOf(
                DailyVerbExample("Clean your hands.", "اپنے ہاتھ صاف کریں۔"),
                DailyVerbExample("He cleans every day.", "وہ ہر روز صاف کرنا کرتا ہے۔"),
                DailyVerbExample("They are cleaning now.", "وہ ابھی صاف کرنا کر رہے ہیں۔"),
                DailyVerbExample("We cleaned yesterday.", "ہم نے کل صاف کرنا کیا۔"),
                DailyVerbExample("She has cleaned.", "وہ صاف کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"clean\" with I, you, we, and they. Use \"cleans\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “clean” اور “He, she, it” کے ساتھ “cleans” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_95_wash",
            baseForm = "wash",
            thirdPerson = "washes",
            presentParticiple = "washing",
            pastSimple = "washed",
            pastParticiple = "washed",
            urduMeaning = "دھونا",
            examples = listOf(
                DailyVerbExample("Wash the dishes.", "برتن دھوئیں۔"),
                DailyVerbExample("He washes every day.", "وہ ہر روز دھونا کرتا ہے۔"),
                DailyVerbExample("They are washing now.", "وہ ابھی دھونا کر رہے ہیں۔"),
                DailyVerbExample("We washed yesterday.", "ہم نے کل دھونا کیا۔"),
                DailyVerbExample("She has washed.", "وہ دھونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"wash\" with I, you, we, and they. Use \"washes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “wash” اور “He, she, it” کے ساتھ “washes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_96_cook",
            baseForm = "cook",
            thirdPerson = "cooks",
            presentParticiple = "cooking",
            pastSimple = "cooked",
            pastParticiple = "cooked",
            urduMeaning = "پکانا",
            examples = listOf(
                DailyVerbExample("She cooks dinner.", "وہ رات کا کھانا پکاتی ہے۔"),
                DailyVerbExample("He cooks every day.", "وہ ہر روز پکانا کرتا ہے۔"),
                DailyVerbExample("They are cooking now.", "وہ ابھی پکانا کر رہے ہیں۔"),
                DailyVerbExample("We cooked yesterday.", "ہم نے کل پکانا کیا۔"),
                DailyVerbExample("She has cooked.", "وہ پکانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"cook\" with I, you, we, and they. Use \"cooks\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “cook” اور “He, she, it” کے ساتھ “cooks” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_97_bake",
            baseForm = "bake",
            thirdPerson = "bakes",
            presentParticiple = "baking",
            pastSimple = "baked",
            pastParticiple = "baked",
            urduMeaning = "بیک کرنا",
            examples = listOf(
                DailyVerbExample("Bake a fresh cake.", "تازہ کیک بیک کریں۔"),
                DailyVerbExample("He bakes every day.", "وہ ہر روز بیک کرنا کرتا ہے۔"),
                DailyVerbExample("They are baking now.", "وہ ابھی بیک کرنا کر رہے ہیں۔"),
                DailyVerbExample("We baked yesterday.", "ہم نے کل بیک کرنا کیا۔"),
                DailyVerbExample("She has baked.", "وہ بیک کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"bake\" with I, you, we, and they. Use \"bakes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “bake” اور “He, she, it” کے ساتھ “bakes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_98_drink",
            baseForm = "drink",
            thirdPerson = "drinks",
            presentParticiple = "drinking",
            pastSimple = "drank",
            pastParticiple = "drunk",
            urduMeaning = "پینا",
            examples = listOf(
                DailyVerbExample("Drink warm milk.", "گرم دودھ پیئیں۔"),
                DailyVerbExample("He drinks every day.", "وہ ہر روز پینا کرتا ہے۔"),
                DailyVerbExample("They are drinking now.", "وہ ابھی پینا کر رہے ہیں۔"),
                DailyVerbExample("We drank yesterday.", "ہم نے کل پینا کیا۔"),
                DailyVerbExample("She has drunk.", "وہ پینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"drink\" with I, you, we, and they. Use \"drinks\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “drink” اور “He, she, it” کے ساتھ “drinks” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_99_enjoy",
            baseForm = "enjoy",
            thirdPerson = "enjoys",
            presentParticiple = "enjoying",
            pastSimple = "enjoyed",
            pastParticiple = "enjoyed",
            urduMeaning = "لطف اندوز ہونا",
            examples = listOf(
                DailyVerbExample("Enjoy your trip.", "اپنے سفر سے لطف اندوز ہوں۔"),
                DailyVerbExample("He enjoys every day.", "وہ ہر روز لطف اندوز ہونا کرتا ہے۔"),
                DailyVerbExample("They are enjoying now.", "وہ ابھی لطف اندوز ہونا کر رہے ہیں۔"),
                DailyVerbExample("We enjoyed yesterday.", "ہم نے کل لطف اندوز ہونا کیا۔"),
                DailyVerbExample("She has enjoyed.", "وہ لطف اندوز ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"enjoy\" with I, you, we, and they. Use \"enjoys\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “enjoy” اور “He, she, it” کے ساتھ “enjoys” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_100_laugh",
            baseForm = "laugh",
            thirdPerson = "laughs",
            presentParticiple = "laughing",
            pastSimple = "laughed",
            pastParticiple = "laughed",
            urduMeaning = "ہنسنا",
            examples = listOf(
                DailyVerbExample("They laugh softly.", "وہ دھیمے سے ہنستے ہیں۔"),
                DailyVerbExample("He laughs every day.", "وہ ہر روز ہنسنا کرتا ہے۔"),
                DailyVerbExample("They are laughing now.", "وہ ابھی ہنسنا کر رہے ہیں۔"),
                DailyVerbExample("We laughed yesterday.", "ہم نے کل ہنسنا کیا۔"),
                DailyVerbExample("She has laughed.", "وہ ہنسنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"laugh\" with I, you, we, and they. Use \"laughs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “laugh” اور “He, she, it” کے ساتھ “laughs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_101_cry",
            baseForm = "cry",
            thirdPerson = "cries",
            presentParticiple = "crying",
            pastSimple = "cried",
            pastParticiple = "cried",
            urduMeaning = "رونا",
            examples = listOf(
                DailyVerbExample("Don't cry.", "روؤ مت۔"),
                DailyVerbExample("He cries every day.", "وہ ہر روز رونا کرتا ہے۔"),
                DailyVerbExample("They are crying now.", "وہ ابھی رونا کر رہے ہیں۔"),
                DailyVerbExample("We cried yesterday.", "ہم نے کل رونا کیا۔"),
                DailyVerbExample("She has cried.", "وہ رونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"cry\" with I, you, we, and they. Use \"cries\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “cry” اور “He, she, it” کے ساتھ “cries” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_102_smile",
            baseForm = "smile",
            thirdPerson = "smiles",
            presentParticiple = "smiling",
            pastSimple = "smiled",
            pastParticiple = "smiled",
            urduMeaning = "مسکرانا",
            examples = listOf(
                DailyVerbExample("Smile always.", "ہمیشہ مسکراتے رہیں۔"),
                DailyVerbExample("He smiles every day.", "وہ ہر روز مسکرانا کرتا ہے۔"),
                DailyVerbExample("They are smiling now.", "وہ ابھی مسکرانا کر رہے ہیں۔"),
                DailyVerbExample("We smiled yesterday.", "ہم نے کل مسکرانا کیا۔"),
                DailyVerbExample("She has smiled.", "وہ مسکرانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"smile\" with I, you, we, and they. Use \"smiles\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “smile” اور “He, she, it” کے ساتھ “smiles” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_103_fight",
            baseForm = "fight",
            thirdPerson = "fights",
            presentParticiple = "fighting",
            pastSimple = "fought",
            pastParticiple = "fought",
            urduMeaning = "لڑنا",
            examples = listOf(
                DailyVerbExample("Don't fight with others.", "دوسروں سے مت لڑو۔"),
                DailyVerbExample("He fights every day.", "وہ ہر روز لڑنا کرتا ہے۔"),
                DailyVerbExample("They are fighting now.", "وہ ابھی لڑنا کر رہے ہیں۔"),
                DailyVerbExample("We fought yesterday.", "ہم نے کل لڑنا کیا۔"),
                DailyVerbExample("She has fought.", "وہ لڑنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"fight\" with I, you, we, and they. Use \"fights\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “fight” اور “He, she, it” کے ساتھ “fights” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_104_dance",
            baseForm = "dance",
            thirdPerson = "dances",
            presentParticiple = "dancing",
            pastSimple = "danced",
            pastParticiple = "danced",
            urduMeaning = "رقص کرنا",
            examples = listOf(
                DailyVerbExample("Children dance with joy.", "بچے خوشی سے رقص کرتے ہیں۔"),
                DailyVerbExample("He dances every day.", "وہ ہر روز رقص کرنا کرتا ہے۔"),
                DailyVerbExample("They are dancing now.", "وہ ابھی رقص کرنا کر رہے ہیں۔"),
                DailyVerbExample("We danced yesterday.", "ہم نے کل رقص کرنا کیا۔"),
                DailyVerbExample("She has danced.", "وہ رقص کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"dance\" with I, you, we, and they. Use \"dances\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “dance” اور “He, she, it” کے ساتھ “dances” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_105_climb",
            baseForm = "climb",
            thirdPerson = "climbs",
            presentParticiple = "climbing",
            pastSimple = "climbed",
            pastParticiple = "climbed",
            urduMeaning = "چڑھنا",
            examples = listOf(
                DailyVerbExample("Climb the stairs.", "سیڑھیاں چڑھیں۔"),
                DailyVerbExample("He climbs every day.", "وہ ہر روز چڑھنا کرتا ہے۔"),
                DailyVerbExample("They are climbing now.", "وہ ابھی چڑھنا کر رہے ہیں۔"),
                DailyVerbExample("We climbed yesterday.", "ہم نے کل چڑھنا کیا۔"),
                DailyVerbExample("She has climbed.", "وہ چڑھنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"climb\" with I, you, we, and they. Use \"climbs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “climb” اور “He, she, it” کے ساتھ “climbs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_106_jump",
            baseForm = "jump",
            thirdPerson = "jumps",
            presentParticiple = "jumping",
            pastSimple = "jumped",
            pastParticiple = "jumped",
            urduMeaning = "چھلانگ لگانا",
            examples = listOf(
                DailyVerbExample("Jump high.", "اونچی چھلانگ لگائیں۔"),
                DailyVerbExample("He jumps every day.", "وہ ہر روز چھلانگ لگانا کرتا ہے۔"),
                DailyVerbExample("They are jumping now.", "وہ ابھی چھلانگ لگانا کر رہے ہیں۔"),
                DailyVerbExample("We jumped yesterday.", "ہم نے کل چھلانگ لگانا کیا۔"),
                DailyVerbExample("She has jumped.", "وہ چھلانگ لگانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"jump\" with I, you, we, and they. Use \"jumps\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “jump” اور “He, she, it” کے ساتھ “jumps” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_107_throw",
            baseForm = "throw",
            thirdPerson = "throws",
            presentParticiple = "throwing",
            pastSimple = "threw",
            pastParticiple = "thrown",
            urduMeaning = "پھینکنا",
            examples = listOf(
                DailyVerbExample("Throw the ball.", "گیند پھینکیں۔"),
                DailyVerbExample("He throws every day.", "وہ ہر روز پھینکنا کرتا ہے۔"),
                DailyVerbExample("They are throwing now.", "وہ ابھی پھینکنا کر رہے ہیں۔"),
                DailyVerbExample("We threw yesterday.", "ہم نے کل پھینکنا کیا۔"),
                DailyVerbExample("She has thrown.", "وہ پھینکنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"throw\" with I, you, we, and they. Use \"throws\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “throw” اور “He, she, it” کے ساتھ “throws” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_108_catch",
            baseForm = "catch",
            thirdPerson = "catches",
            presentParticiple = "catching",
            pastSimple = "caught",
            pastParticiple = "caught",
            urduMeaning = "پکڑنا",
            examples = listOf(
                DailyVerbExample("Catch the ball.", "گیند پکڑو۔"),
                DailyVerbExample("He catches every day.", "وہ ہر روز پکڑنا کرتا ہے۔"),
                DailyVerbExample("They are catching now.", "وہ ابھی پکڑنا کر رہے ہیں۔"),
                DailyVerbExample("We caught yesterday.", "ہم نے کل پکڑنا کیا۔"),
                DailyVerbExample("She has caught.", "وہ پکڑنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"catch\" with I, you, we, and they. Use \"catches\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “catch” اور “He, she, it” کے ساتھ “catches” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_109_count",
            baseForm = "count",
            thirdPerson = "counts",
            presentParticiple = "counting",
            pastSimple = "counted",
            pastParticiple = "counted",
            urduMeaning = "گننا",
            examples = listOf(
                DailyVerbExample("Count from one to ten.", "ایک سے دس تک گنو۔"),
                DailyVerbExample("He counts every day.", "وہ ہر روز گننا کرتا ہے۔"),
                DailyVerbExample("They are counting now.", "وہ ابھی گننا کر رہے ہیں۔"),
                DailyVerbExample("We counted yesterday.", "ہم نے کل گننا کیا۔"),
                DailyVerbExample("She has counted.", "وہ گننا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"count\" with I, you, we, and they. Use \"counts\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “count” اور “He, she, it” کے ساتھ “counts” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_110_touch",
            baseForm = "touch",
            thirdPerson = "touches",
            presentParticiple = "touching",
            pastSimple = "touched",
            pastParticiple = "touched",
            urduMeaning = "چھونا",
            examples = listOf(
                DailyVerbExample("Don't touch hot iron.", "گرم استری کو مت چھوئیں۔"),
                DailyVerbExample("He touches every day.", "وہ ہر روز چھونا کرتا ہے۔"),
                DailyVerbExample("They are touching now.", "وہ ابھی چھونا کر رہے ہیں۔"),
                DailyVerbExample("We touched yesterday.", "ہم نے کل چھونا کیا۔"),
                DailyVerbExample("She has touched.", "وہ چھونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"touch\" with I, you, we, and they. Use \"touches\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “touch” اور “He, she, it” کے ساتھ “touches” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_111_smell",
            baseForm = "smell",
            thirdPerson = "smells",
            presentParticiple = "smelling",
            pastSimple = "smelt / smelled",
            pastParticiple = "smelt / smelled",
            urduMeaning = "سونگھنا",
            examples = listOf(
                DailyVerbExample("Roses smell sweet.", "گلاب کی خوشبو میٹھی ہوتی ہے۔"),
                DailyVerbExample("He smells every day.", "وہ ہر روز سونگھنا کرتا ہے۔"),
                DailyVerbExample("They are smelling now.", "وہ ابھی سونگھنا کر رہے ہیں۔"),
                DailyVerbExample("We smelt / smelled yesterday.", "ہم نے کل سونگھنا کیا۔"),
                DailyVerbExample("She has smelt / smelled.", "وہ سونگھنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"smell\" with I, you, we, and they. Use \"smells\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “smell” اور “He, she, it” کے ساتھ “smells” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_112_taste",
            baseForm = "taste",
            thirdPerson = "tastes",
            presentParticiple = "tasting",
            pastSimple = "tasted",
            pastParticiple = "tasted",
            urduMeaning = "چکھنا",
            examples = listOf(
                DailyVerbExample("Taste the soup.", "سوپ چکھیں۔"),
                DailyVerbExample("He tastes every day.", "وہ ہر روز چکھنا کرتا ہے۔"),
                DailyVerbExample("They are tasting now.", "وہ ابھی چکھنا کر رہے ہیں۔"),
                DailyVerbExample("We tasted yesterday.", "ہم نے کل چکھنا کیا۔"),
                DailyVerbExample("She has tasted.", "وہ چکھنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"taste\" with I, you, we, and they. Use \"tastes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “taste” اور “He, she, it” کے ساتھ “tastes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_113_wear",
            baseForm = "wear",
            thirdPerson = "wears",
            presentParticiple = "wearing",
            pastSimple = "wore",
            pastParticiple = "worn",
            urduMeaning = "پہننا",
            examples = listOf(
                DailyVerbExample("Wear a warm coat.", "گرم کوٹ پہنیں۔"),
                DailyVerbExample("He wears every day.", "وہ ہر روز پہننا کرتا ہے۔"),
                DailyVerbExample("They are wearing now.", "وہ ابھی پہننا کر رہے ہیں۔"),
                DailyVerbExample("We wore yesterday.", "ہم نے کل پہننا کیا۔"),
                DailyVerbExample("She has worn.", "وہ پہننا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"wear\" with I, you, we, and they. Use \"wears\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “wear” اور “He, she, it” کے ساتھ “wears” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_114_forget",
            baseForm = "forget",
            thirdPerson = "forgets",
            presentParticiple = "forgetting",
            pastSimple = "forgot",
            pastParticiple = "forgotten",
            urduMeaning = "بھولنا",
            examples = listOf(
                DailyVerbExample("Don't forget your hat.", "اپنی ٹوپی مت بھولیں۔"),
                DailyVerbExample("He forgets every day.", "وہ ہر روز بھولنا کرتا ہے۔"),
                DailyVerbExample("They are forgetting now.", "وہ ابھی بھولنا کر رہے ہیں۔"),
                DailyVerbExample("We forgot yesterday.", "ہم نے کل بھولنا کیا۔"),
                DailyVerbExample("She has forgotten.", "وہ بھولنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"forget\" with I, you, we, and they. Use \"forgets\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “forget” اور “He, she, it” کے ساتھ “forgets” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_115_lend",
            baseForm = "lend",
            thirdPerson = "lends",
            presentParticiple = "lending",
            pastSimple = "lent",
            pastParticiple = "lent",
            urduMeaning = "ادھار دینا",
            examples = listOf(
                DailyVerbExample("Lend me your book.", "مجھے اپنی کتاب ادھار دیں۔"),
                DailyVerbExample("He lends every day.", "وہ ہر روز ادھار دینا کرتا ہے۔"),
                DailyVerbExample("They are lending now.", "وہ ابھی ادھار دینا کر رہے ہیں۔"),
                DailyVerbExample("We lent yesterday.", "ہم نے کل ادھار دینا کیا۔"),
                DailyVerbExample("She has lent.", "وہ ادھار دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"lend\" with I, you, we, and they. Use \"lends\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “lend” اور “He, she, it” کے ساتھ “lends” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_116_borrow",
            baseForm = "borrow",
            thirdPerson = "borrows",
            presentParticiple = "borrowing",
            pastSimple = "borrowed",
            pastParticiple = "borrowed",
            urduMeaning = "ادھار لینا",
            examples = listOf(
                DailyVerbExample("Borrow a pencil.", "پنسل ادھار لیں۔"),
                DailyVerbExample("He borrows every day.", "وہ ہر روز ادھار لینا کرتا ہے۔"),
                DailyVerbExample("They are borrowing now.", "وہ ابھی ادھار لینا کر رہے ہیں۔"),
                DailyVerbExample("We borrowed yesterday.", "ہم نے کل ادھار لینا کیا۔"),
                DailyVerbExample("She has borrowed.", "وہ ادھار لینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"borrow\" with I, you, we, and they. Use \"borrows\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “borrow” اور “He, she, it” کے ساتھ “borrows” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_117_save",
            baseForm = "save",
            thirdPerson = "saves",
            presentParticiple = "saving",
            pastSimple = "saved",
            pastParticiple = "saved",
            urduMeaning = "محفوظ کرنا",
            examples = listOf(
                DailyVerbExample("Save electricity.", "بجلی بچائیں۔"),
                DailyVerbExample("He saves every day.", "وہ ہر روز محفوظ کرنا کرتا ہے۔"),
                DailyVerbExample("They are saving now.", "وہ ابھی محفوظ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We saved yesterday.", "ہم نے کل محفوظ کرنا کیا۔"),
                DailyVerbExample("She has saved.", "وہ محفوظ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"save\" with I, you, we, and they. Use \"saves\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “save” اور “He, she, it” کے ساتھ “saves” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_118_destroy",
            baseForm = "destroy",
            thirdPerson = "destroys",
            presentParticiple = "destroying",
            pastSimple = "destroyed",
            pastParticiple = "destroyed",
            urduMeaning = "تباہ کرنا",
            examples = listOf(
                DailyVerbExample("Fire destroyed trees.", "آگ نے درخت تباہ کر دیے۔"),
                DailyVerbExample("He destroys every day.", "وہ ہر روز تباہ کرنا کرتا ہے۔"),
                DailyVerbExample("They are destroying now.", "وہ ابھی تباہ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We destroyed yesterday.", "ہم نے کل تباہ کرنا کیا۔"),
                DailyVerbExample("She has destroyed.", "وہ تباہ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"destroy\" with I, you, we, and they. Use \"destroys\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “destroy” اور “He, she, it” کے ساتھ “destroys” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_119_protect",
            baseForm = "protect",
            thirdPerson = "protects",
            presentParticiple = "protecting",
            pastSimple = "protected",
            pastParticiple = "protected",
            urduMeaning = "حفاظت کرنا",
            examples = listOf(
                DailyVerbExample("Protect nature.", "قدرت کی حفاظت کریں۔"),
                DailyVerbExample("He protects every day.", "وہ ہر روز حفاظت کرنا کرتا ہے۔"),
                DailyVerbExample("They are protecting now.", "وہ ابھی حفاظت کرنا کر رہے ہیں۔"),
                DailyVerbExample("We protected yesterday.", "ہم نے کل حفاظت کرنا کیا۔"),
                DailyVerbExample("She has protected.", "وہ حفاظت کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"protect\" with I, you, we, and they. Use \"protects\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “protect” اور “He, she, it” کے ساتھ “protects” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_120_attack",
            baseForm = "attack",
            thirdPerson = "attacks",
            presentParticiple = "attacking",
            pastSimple = "attacked",
            pastParticiple = "attacked",
            urduMeaning = "حملہ کرنا",
            examples = listOf(
                DailyVerbExample("Eagles attack prey.", "عقاب شکار پر حملہ کرتے ہیں۔"),
                DailyVerbExample("He attacks every day.", "وہ ہر روز حملہ کرنا کرتا ہے۔"),
                DailyVerbExample("They are attacking now.", "وہ ابھی حملہ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We attacked yesterday.", "ہم نے کل حملہ کرنا کیا۔"),
                DailyVerbExample("She has attacked.", "وہ حملہ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"attack\" with I, you, we, and they. Use \"attacks\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “attack” اور “He, she, it” کے ساتھ “attacks” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_121_defend",
            baseForm = "defend",
            thirdPerson = "defends",
            presentParticiple = "defending",
            pastSimple = "defended",
            pastParticiple = "defended",
            urduMeaning = "دفاع کرنا",
            examples = listOf(
                DailyVerbExample("Defend your right.", "اپنے حق کا دفاع کریں۔"),
                DailyVerbExample("He defends every day.", "وہ ہر روز دفاع کرنا کرتا ہے۔"),
                DailyVerbExample("They are defending now.", "وہ ابھی دفاع کرنا کر رہے ہیں۔"),
                DailyVerbExample("We defended yesterday.", "ہم نے کل دفاع کرنا کیا۔"),
                DailyVerbExample("She has defended.", "وہ دفاع کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"defend\" with I, you, we, and they. Use \"defends\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “defend” اور “He, she, it” کے ساتھ “defends” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_122_paint",
            baseForm = "paint",
            thirdPerson = "paints",
            presentParticiple = "painting",
            pastSimple = "painted",
            pastParticiple = "painted",
            urduMeaning = "رنگنا",
            examples = listOf(
                DailyVerbExample("Paint the wall green.", "دیوار کو سبز رنگیں۔"),
                DailyVerbExample("He paints every day.", "وہ ہر روز رنگنا کرتا ہے۔"),
                DailyVerbExample("They are painting now.", "وہ ابھی رنگنا کر رہے ہیں۔"),
                DailyVerbExample("We painted yesterday.", "ہم نے کل رنگنا کیا۔"),
                DailyVerbExample("She has painted.", "وہ رنگنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"paint\" with I, you, we, and they. Use \"paints\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “paint” اور “He, she, it” کے ساتھ “paints” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_123_draw",
            baseForm = "draw",
            thirdPerson = "draws",
            presentParticiple = "drawing",
            pastSimple = "drew",
            pastParticiple = "drawn",
            urduMeaning = "خاکہ بنانا",
            examples = listOf(
                DailyVerbExample("Draw a beautiful flower.", "ایک خوبصورت پھول کا خاکہ بنائیں۔"),
                DailyVerbExample("He draws every day.", "وہ ہر روز خاکہ بنانا کرتا ہے۔"),
                DailyVerbExample("They are drawing now.", "وہ ابھی خاکہ بنانا کر رہے ہیں۔"),
                DailyVerbExample("We drew yesterday.", "ہم نے کل خاکہ بنانا کیا۔"),
                DailyVerbExample("She has drawn.", "وہ خاکہ بنانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"draw\" with I, you, we, and they. Use \"draws\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “draw” اور “He, she, it” کے ساتھ “draws” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_124_fill",
            baseForm = "fill",
            thirdPerson = "fills",
            presentParticiple = "filling",
            pastSimple = "filled",
            pastParticiple = "filled",
            urduMeaning = "بھرنا",
            examples = listOf(
                DailyVerbExample("Fill the bottle with water.", "بوتل کو پانی سے بھریں۔"),
                DailyVerbExample("He fills every day.", "وہ ہر روز بھرنا کرتا ہے۔"),
                DailyVerbExample("They are filling now.", "وہ ابھی بھرنا کر رہے ہیں۔"),
                DailyVerbExample("We filled yesterday.", "ہم نے کل بھرنا کیا۔"),
                DailyVerbExample("She has filled.", "وہ بھرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"fill\" with I, you, we, and they. Use \"fills\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “fill” اور “He, she, it” کے ساتھ “fills” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_125_empty",
            baseForm = "empty",
            thirdPerson = "empties",
            presentParticiple = "emptying",
            pastSimple = "emptied",
            pastParticiple = "emptied",
            urduMeaning = "خالی کرنا",
            examples = listOf(
                DailyVerbExample("Empty the dustbin.", "کوڑے دان کو خالی کریں۔"),
                DailyVerbExample("He empties every day.", "وہ ہر روز خالی کرنا کرتا ہے۔"),
                DailyVerbExample("They are emptying now.", "وہ ابھی خالی کرنا کر رہے ہیں۔"),
                DailyVerbExample("We emptied yesterday.", "ہم نے کل خالی کرنا کیا۔"),
                DailyVerbExample("She has emptied.", "وہ خالی کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"empty\" with I, you, we, and they. Use \"empties\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “empty” اور “He, she, it” کے ساتھ “empties” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_126_shake",
            baseForm = "shake",
            thirdPerson = "shakes",
            presentParticiple = "shaking",
            pastSimple = "shook",
            pastParticiple = "shaken",
            urduMeaning = "ہلانا",
            examples = listOf(
                DailyVerbExample("Shake well before use.", "استعمال سے پہلے اچھی طرح ہلائیں۔"),
                DailyVerbExample("He shakes every day.", "وہ ہر روز ہلانا کرتا ہے۔"),
                DailyVerbExample("They are shaking now.", "وہ ابھی ہلانا کر رہے ہیں۔"),
                DailyVerbExample("We shook yesterday.", "ہم نے کل ہلانا کیا۔"),
                DailyVerbExample("She has shaken.", "وہ ہلانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"shake\" with I, you, we, and they. Use \"shakes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “shake” اور “He, she, it” کے ساتھ “shakes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_127_drop",
            baseForm = "drop",
            thirdPerson = "drops",
            presentParticiple = "dropping",
            pastSimple = "dropped",
            pastParticiple = "dropped",
            urduMeaning = "گرانا",
            examples = listOf(
                DailyVerbExample("Don't drop the glass.", "شیشہ مت گرائیں۔"),
                DailyVerbExample("He drops every day.", "وہ ہر روز گرانا کرتا ہے۔"),
                DailyVerbExample("They are dropping now.", "وہ ابھی گرانا کر رہے ہیں۔"),
                DailyVerbExample("We dropped yesterday.", "ہم نے کل گرانا کیا۔"),
                DailyVerbExample("She has dropped.", "وہ گرانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"drop\" with I, you, we, and they. Use \"drops\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “drop” اور “He, she, it” کے ساتھ “drops” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_128_lift",
            baseForm = "lift",
            thirdPerson = "lifts",
            presentParticiple = "lifting",
            pastSimple = "lifted",
            pastParticiple = "lifted",
            urduMeaning = "اٹھانا",
            examples = listOf(
                DailyVerbExample("Lift the heavy box.", "بھاری ڈبہ اٹھائیں۔"),
                DailyVerbExample("He lifts every day.", "وہ ہر روز اٹھانا کرتا ہے۔"),
                DailyVerbExample("They are lifting now.", "وہ ابھی اٹھانا کر رہے ہیں۔"),
                DailyVerbExample("We lifted yesterday.", "ہم نے کل اٹھانا کیا۔"),
                DailyVerbExample("She has lifted.", "وہ اٹھانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"lift\" with I, you, we, and they. Use \"lifts\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “lift” اور “He, she, it” کے ساتھ “lifts” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_129_fix",
            baseForm = "fix",
            thirdPerson = "fixes",
            presentParticiple = "fixing",
            pastSimple = "fixed",
            pastParticiple = "fixed",
            urduMeaning = "ٹھیک کرنا",
            examples = listOf(
                DailyVerbExample("Fix the broken chair.", "ٹوٹی ہوئی کرسی ٹھیک کریں۔"),
                DailyVerbExample("He fixes every day.", "وہ ہر روز ٹھیک کرنا کرتا ہے۔"),
                DailyVerbExample("They are fixing now.", "وہ ابھی ٹھیک کرنا کر رہے ہیں۔"),
                DailyVerbExample("We fixed yesterday.", "ہم نے کل ٹھیک کرنا کیا۔"),
                DailyVerbExample("She has fixed.", "وہ ٹھیک کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"fix\" with I, you, we, and they. Use \"fixes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “fix” اور “He, she, it” کے ساتھ “fixes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_130_repair",
            baseForm = "repair",
            thirdPerson = "repairs",
            presentParticiple = "repairing",
            pastSimple = "repaired",
            pastParticiple = "repaired",
            urduMeaning = "مرمت کرنا",
            examples = listOf(
                DailyVerbExample("Repair the bicycle.", "سائیکل کی مرمت کریں۔"),
                DailyVerbExample("He repairs every day.", "وہ ہر روز مرمت کرنا کرتا ہے۔"),
                DailyVerbExample("They are repairing now.", "وہ ابھی مرمت کرنا کر رہے ہیں۔"),
                DailyVerbExample("We repaired yesterday.", "ہم نے کل مرمت کرنا کیا۔"),
                DailyVerbExample("She has repaired.", "وہ مرمت کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"repair\" with I, you, we, and they. Use \"repairs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “repair” اور “He, she, it” کے ساتھ “repairs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_131_break",
            baseForm = "break",
            thirdPerson = "breaks",
            presentParticiple = "breaking",
            pastSimple = "broke",
            pastParticiple = "broken",
            urduMeaning = "توڑنا",
            examples = listOf(
                DailyVerbExample("Careful, don't break it.", "احتیاط کریں، اسے مت توڑیں۔"),
                DailyVerbExample("He breaks every day.", "وہ ہر روز توڑنا کرتا ہے۔"),
                DailyVerbExample("They are breaking now.", "وہ ابھی توڑنا کر رہے ہیں۔"),
                DailyVerbExample("We broke yesterday.", "ہم نے کل توڑنا کیا۔"),
                DailyVerbExample("She has broken.", "وہ توڑنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"break\" with I, you, we, and they. Use \"breaks\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “break” اور “He, she, it” کے ساتھ “breaks” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_132_melt",
            baseForm = "melt",
            thirdPerson = "melts",
            presentParticiple = "melting",
            pastSimple = "melted",
            pastParticiple = "melted",
            urduMeaning = "پگھلنا",
            examples = listOf(
                DailyVerbExample("Ice melts in heat.", "گرمی میں برف پگھلتی ہے۔"),
                DailyVerbExample("He melts every day.", "وہ ہر روز پگھلنا کرتا ہے۔"),
                DailyVerbExample("They are melting now.", "وہ ابھی پگھلنا کر رہے ہیں۔"),
                DailyVerbExample("We melted yesterday.", "ہم نے کل پگھلنا کیا۔"),
                DailyVerbExample("She has melted.", "وہ پگھلنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"melt\" with I, you, we, and they. Use \"melts\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “melt” اور “He, she, it” کے ساتھ “melts” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_133_freeze",
            baseForm = "freeze",
            thirdPerson = "freezes",
            presentParticiple = "freezing",
            pastSimple = "froze",
            pastParticiple = "frozen",
            urduMeaning = "جمنا",
            examples = listOf(
                DailyVerbExample("Water freezes at 0°C.", "پانی صفر ڈگری پر جمتا ہے۔"),
                DailyVerbExample("He freezes every day.", "وہ ہر روز جمنا کرتا ہے۔"),
                DailyVerbExample("They are freezing now.", "وہ ابھی جمنا کر رہے ہیں۔"),
                DailyVerbExample("We froze yesterday.", "ہم نے کل جمنا کیا۔"),
                DailyVerbExample("She has frozen.", "وہ جمنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"freeze\" with I, you, we, and they. Use \"freezes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “freeze” اور “He, she, it” کے ساتھ “freezes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_134_boil",
            baseForm = "boil",
            thirdPerson = "boils",
            presentParticiple = "boiling",
            pastSimple = "boiled",
            pastParticiple = "boiled",
            urduMeaning = "ابالنا",
            examples = listOf(
                DailyVerbExample("Boil the water first.", "پہلے پانی ابالیں۔"),
                DailyVerbExample("He boils every day.", "وہ ہر روز ابالنا کرتا ہے۔"),
                DailyVerbExample("They are boiling now.", "وہ ابھی ابالنا کر رہے ہیں۔"),
                DailyVerbExample("We boiled yesterday.", "ہم نے کل ابالنا کیا۔"),
                DailyVerbExample("She has boiled.", "وہ ابالنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"boil\" with I, you, we, and they. Use \"boils\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “boil” اور “He, she, it” کے ساتھ “boils” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_135_burn",
            baseForm = "burn",
            thirdPerson = "burns",
            presentParticiple = "burning",
            pastSimple = "burnt / burned",
            pastParticiple = "burnt / burned",
            urduMeaning = "جلنا",
            examples = listOf(
                DailyVerbExample("Sunlight burns skin.", "سورج کی روشنی جلد کو جلاتی ہے۔"),
                DailyVerbExample("He burns every day.", "وہ ہر روز جلنا کرتا ہے۔"),
                DailyVerbExample("They are burning now.", "وہ ابھی جلنا کر رہے ہیں۔"),
                DailyVerbExample("We burnt / burned yesterday.", "ہم نے کل جلنا کیا۔"),
                DailyVerbExample("She has burnt / burned.", "وہ جلنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"burn\" with I, you, we, and they. Use \"burns\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “burn” اور “He, she, it” کے ساتھ “burns” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_136_shine",
            baseForm = "shine",
            thirdPerson = "shines",
            presentParticiple = "shining",
            pastSimple = "shone",
            pastParticiple = "shone",
            urduMeaning = "چمکنا",
            examples = listOf(
                DailyVerbExample("The sun shines bright.", "سورج تیز چمکتا ہے۔"),
                DailyVerbExample("He shines every day.", "وہ ہر روز چمکنا کرتا ہے۔"),
                DailyVerbExample("They are shining now.", "وہ ابھی چمکنا کر رہے ہیں۔"),
                DailyVerbExample("We shone yesterday.", "ہم نے کل چمکنا کیا۔"),
                DailyVerbExample("She has shone.", "وہ چمکنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"shine\" with I, you, we, and they. Use \"shines\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “shine” اور “He, she, it” کے ساتھ “shines” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_137_blow",
            baseForm = "blow",
            thirdPerson = "blows",
            presentParticiple = "blowing",
            pastSimple = "blew",
            pastParticiple = "blown",
            urduMeaning = "ہوا چلنا",
            examples = listOf(
                DailyVerbExample("Wind blows gently.", "ہوا دھیمے سے چلتی ہے۔"),
                DailyVerbExample("He blows every day.", "وہ ہر روز ہوا چلنا کرتا ہے۔"),
                DailyVerbExample("They are blowing now.", "وہ ابھی ہوا چلنا کر رہے ہیں۔"),
                DailyVerbExample("We blew yesterday.", "ہم نے کل ہوا چلنا کیا۔"),
                DailyVerbExample("She has blown.", "وہ ہوا چلنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"blow\" with I, you, we, and they. Use \"blows\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “blow” اور “He, she, it” کے ساتھ “blows” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_138_flow",
            baseForm = "flow",
            thirdPerson = "flows",
            presentParticiple = "flowing",
            pastSimple = "flowed",
            pastParticiple = "flowed",
            urduMeaning = "بہنا",
            examples = listOf(
                DailyVerbExample("Rivers flow to the sea.", "دریا سمندر کی طرف بہتے ہیں۔"),
                DailyVerbExample("He flows every day.", "وہ ہر روز بہنا کرتا ہے۔"),
                DailyVerbExample("They are flowing now.", "وہ ابھی بہنا کر رہے ہیں۔"),
                DailyVerbExample("We flowed yesterday.", "ہم نے کل بہنا کیا۔"),
                DailyVerbExample("She has flowed.", "وہ بہنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"flow\" with I, you, we, and they. Use \"flows\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “flow” اور “He, she, it” کے ساتھ “flows” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_139_plant",
            baseForm = "plant",
            thirdPerson = "plants",
            presentParticiple = "planting",
            pastSimple = "planted",
            pastParticiple = "planted",
            urduMeaning = "پودا لگانا",
            examples = listOf(
                DailyVerbExample("Plant more trees.", "مزید پودے لگائیں۔"),
                DailyVerbExample("He plants every day.", "وہ ہر روز پودا لگانا کرتا ہے۔"),
                DailyVerbExample("They are planting now.", "وہ ابھی پودا لگانا کر رہے ہیں۔"),
                DailyVerbExample("We planted yesterday.", "ہم نے کل پودا لگانا کیا۔"),
                DailyVerbExample("She has planted.", "وہ پودا لگانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"plant\" with I, you, we, and they. Use \"plants\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “plant” اور “He, she, it” کے ساتھ “plants” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_140_harvest",
            baseForm = "harvest",
            thirdPerson = "harvests",
            presentParticiple = "harvesting",
            pastSimple = "harvested",
            pastParticiple = "harvested",
            urduMeaning = "فصل کاٹنا",
            examples = listOf(
                DailyVerbExample("Farmers harvest wheat.", "کسان گندم کاٹتے ہیں۔"),
                DailyVerbExample("He harvests every day.", "وہ ہر روز فصل کاٹنا کرتا ہے۔"),
                DailyVerbExample("They are harvesting now.", "وہ ابھی فصل کاٹنا کر رہے ہیں۔"),
                DailyVerbExample("We harvested yesterday.", "ہم نے کل فصل کاٹنا کیا۔"),
                DailyVerbExample("She has harvested.", "وہ فصل کاٹنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"harvest\" with I, you, we, and they. Use \"harvests\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “harvest” اور “He, she, it” کے ساتھ “harvests” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_141_share",
            baseForm = "share",
            thirdPerson = "shares",
            presentParticiple = "sharing",
            pastSimple = "shared",
            pastParticiple = "shared",
            urduMeaning = "بانٹنا",
            examples = listOf(
                DailyVerbExample("Share your food.", "اپنا کھانا بانٹیں۔"),
                DailyVerbExample("He shares every day.", "وہ ہر روز بانٹنا کرتا ہے۔"),
                DailyVerbExample("They are sharing now.", "وہ ابھی بانٹنا کر رہے ہیں۔"),
                DailyVerbExample("We shared yesterday.", "ہم نے کل بانٹنا کیا۔"),
                DailyVerbExample("She has shared.", "وہ بانٹنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"share\" with I, you, we, and they. Use \"shares\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “share” اور “He, she, it” کے ساتھ “shares” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_142_care",
            baseForm = "care",
            thirdPerson = "cares",
            presentParticiple = "caring",
            pastSimple = "cared",
            pastParticiple = "cared",
            urduMeaning = "دیکھ بھال کرنا",
            examples = listOf(
                DailyVerbExample("Care for animals.", "جانوروں کی دیکھ بھال کریں۔"),
                DailyVerbExample("He cares every day.", "وہ ہر روز دیکھ بھال کرنا کرتا ہے۔"),
                DailyVerbExample("They are caring now.", "وہ ابھی دیکھ بھال کرنا کر رہے ہیں۔"),
                DailyVerbExample("We cared yesterday.", "ہم نے کل دیکھ بھال کرنا کیا۔"),
                DailyVerbExample("She has cared.", "وہ دیکھ بھال کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"care\" with I, you, we, and they. Use \"cares\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “care” اور “He, she, it” کے ساتھ “cares” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_143_trust",
            baseForm = "trust",
            thirdPerson = "trusts",
            presentParticiple = "trusting",
            pastSimple = "trusted",
            pastParticiple = "trusted",
            urduMeaning = "بھروسہ کرنا",
            examples = listOf(
                DailyVerbExample("Trust honest people.", "دیانت دار لوگوں پر بھروسہ کریں۔"),
                DailyVerbExample("He trusts every day.", "وہ ہر روز بھروسہ کرنا کرتا ہے۔"),
                DailyVerbExample("They are trusting now.", "وہ ابھی بھروسہ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We trusted yesterday.", "ہم نے کل بھروسہ کرنا کیا۔"),
                DailyVerbExample("She has trusted.", "وہ بھروسہ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"trust\" with I, you, we, and they. Use \"trusts\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “trust” اور “He, she, it” کے ساتھ “trusts” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_144_respect",
            baseForm = "respect",
            thirdPerson = "respects",
            presentParticiple = "respecting",
            pastSimple = "respected",
            pastParticiple = "respected",
            urduMeaning = "احترام کرنا",
            examples = listOf(
                DailyVerbExample("Respect your elders.", "اپنے بڑوں کا احترام کریں۔"),
                DailyVerbExample("He respects every day.", "وہ ہر روز احترام کرنا کرتا ہے۔"),
                DailyVerbExample("They are respecting now.", "وہ ابھی احترام کرنا کر رہے ہیں۔"),
                DailyVerbExample("We respected yesterday.", "ہم نے کل احترام کرنا کیا۔"),
                DailyVerbExample("She has respected.", "وہ احترام کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"respect\" with I, you, we, and they. Use \"respects\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “respect” اور “He, she, it” کے ساتھ “respects” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_145_honor",
            baseForm = "honor",
            thirdPerson = "honors",
            presentParticiple = "honoring",
            pastSimple = "honored",
            pastParticiple = "honored",
            urduMeaning = "عزت دینا",
            examples = listOf(
                DailyVerbExample("Honor your word.", "اپنے وعدے کی پاسداری کریں۔"),
                DailyVerbExample("He honors every day.", "وہ ہر روز عزت دینا کرتا ہے۔"),
                DailyVerbExample("They are honoring now.", "وہ ابھی عزت دینا کر رہے ہیں۔"),
                DailyVerbExample("We honored yesterday.", "ہم نے کل عزت دینا کیا۔"),
                DailyVerbExample("She has honored.", "وہ عزت دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"honor\" with I, you, we, and they. Use \"honors\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “honor” اور “He, she, it” کے ساتھ “honors” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_146_praise",
            baseForm = "praise",
            thirdPerson = "praises",
            presentParticiple = "praising",
            pastSimple = "praised",
            pastParticiple = "praised",
            urduMeaning = "تعریف کرنا",
            examples = listOf(
                DailyVerbExample("Praise good deeds.", "اچھے کاموں کی تعریف کریں۔"),
                DailyVerbExample("He praises every day.", "وہ ہر روز تعریف کرنا کرتا ہے۔"),
                DailyVerbExample("They are praising now.", "وہ ابھی تعریف کرنا کر رہے ہیں۔"),
                DailyVerbExample("We praised yesterday.", "ہم نے کل تعریف کرنا کیا۔"),
                DailyVerbExample("She has praised.", "وہ تعریف کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"praise\" with I, you, we, and they. Use \"praises\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “praise” اور “He, she, it” کے ساتھ “praises” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_147_thank",
            baseForm = "thank",
            thirdPerson = "thanks",
            presentParticiple = "thanking",
            pastSimple = "thanked",
            pastParticiple = "thanked",
            urduMeaning = "شکریہ ادا کرنا",
            examples = listOf(
                DailyVerbExample("Thank God daily.", "روزانہ اللہ کا شکریہ ادا کریں۔"),
                DailyVerbExample("He thanks every day.", "وہ ہر روز شکریہ ادا کرنا کرتا ہے۔"),
                DailyVerbExample("They are thanking now.", "وہ ابھی شکریہ ادا کرنا کر رہے ہیں۔"),
                DailyVerbExample("We thanked yesterday.", "ہم نے کل شکریہ ادا کرنا کیا۔"),
                DailyVerbExample("She has thanked.", "وہ شکریہ ادا کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"thank\" with I, you, we, and they. Use \"thanks\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “thank” اور “He, she, it” کے ساتھ “thanks” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_148_welcome",
            baseForm = "welcome",
            thirdPerson = "welcomes",
            presentParticiple = "welcoming",
            pastSimple = "welcomed",
            pastParticiple = "welcomed",
            urduMeaning = "خوش آمدید کہنا",
            examples = listOf(
                DailyVerbExample("Welcome the guests.", "مہمانوں کا خوش آمدید کہیں۔"),
                DailyVerbExample("He welcomes every day.", "وہ ہر روز خوش آمدید کہنا کرتا ہے۔"),
                DailyVerbExample("They are welcoming now.", "وہ ابھی خوش آمدید کہنا کر رہے ہیں۔"),
                DailyVerbExample("We welcomed yesterday.", "ہم نے کل خوش آمدید کہنا کیا۔"),
                DailyVerbExample("She has welcomed.", "وہ خوش آمدید کہنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"welcome\" with I, you, we, and they. Use \"welcomes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “welcome” اور “He, she, it” کے ساتھ “welcomes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_149_invite",
            baseForm = "invite",
            thirdPerson = "invites",
            presentParticiple = "inviting",
            pastSimple = "invited",
            pastParticiple = "invited",
            urduMeaning = "دعوت دینا",
            examples = listOf(
                DailyVerbExample("Invite your friends.", "اپنے دوستوں کو دعوت دیں۔"),
                DailyVerbExample("He invites every day.", "وہ ہر روز دعوت دینا کرتا ہے۔"),
                DailyVerbExample("They are inviting now.", "وہ ابھی دعوت دینا کر رہے ہیں۔"),
                DailyVerbExample("We invited yesterday.", "ہم نے کل دعوت دینا کیا۔"),
                DailyVerbExample("She has invited.", "وہ دعوت دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"invite\" with I, you, we, and they. Use \"invites\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “invite” اور “He, she, it” کے ساتھ “invites” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_150_visit",
            baseForm = "visit",
            thirdPerson = "visits",
            presentParticiple = "visiting",
            pastSimple = "visited",
            pastParticiple = "visited",
            urduMeaning = "ملاقات کرنا",
            examples = listOf(
                DailyVerbExample("Visit your family.", "اپنے خاندان سے ملاقات کریں۔"),
                DailyVerbExample("He visits every day.", "وہ ہر روز ملاقات کرنا کرتا ہے۔"),
                DailyVerbExample("They are visiting now.", "وہ ابھی ملاقات کرنا کر رہے ہیں۔"),
                DailyVerbExample("We visited yesterday.", "ہم نے کل ملاقات کرنا کیا۔"),
                DailyVerbExample("She has visited.", "وہ ملاقات کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"visit\" with I, you, we, and they. Use \"visits\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “visit” اور “He, she, it” کے ساتھ “visits” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_151_travel",
            baseForm = "travel",
            thirdPerson = "travels",
            presentParticiple = "travelling",
            pastSimple = "travelled",
            pastParticiple = "travelled",
            urduMeaning = "سفر کرنا",
            examples = listOf(
                DailyVerbExample("Travel expands mind.", "سفر ذہن کو وسیع کرتا ہے۔"),
                DailyVerbExample("He travels every day.", "وہ ہر روز سفر کرنا کرتا ہے۔"),
                DailyVerbExample("They are travelling now.", "وہ ابھی سفر کرنا کر رہے ہیں۔"),
                DailyVerbExample("We travelled yesterday.", "ہم نے کل سفر کرنا کیا۔"),
                DailyVerbExample("She has travelled.", "وہ سفر کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"travel\" with I, you, we, and they. Use \"travels\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “travel” اور “He, she, it” کے ساتھ “travels” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_152_arrive",
            baseForm = "arrive",
            thirdPerson = "arrives",
            presentParticiple = "arriving",
            pastSimple = "arrived",
            pastParticiple = "arrived",
            urduMeaning = "پہنچنا",
            examples = listOf(
                DailyVerbExample("The bus arrived early.", "بس جلدی پہنچ گئی۔"),
                DailyVerbExample("He arrives every day.", "وہ ہر روز پہنچنا کرتا ہے۔"),
                DailyVerbExample("They are arriving now.", "وہ ابھی پہنچنا کر رہے ہیں۔"),
                DailyVerbExample("We arrived yesterday.", "ہم نے کل پہنچنا کیا۔"),
                DailyVerbExample("She has arrived.", "وہ پہنچنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"arrive\" with I, you, we, and they. Use \"arrives\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “arrive” اور “He, she, it” کے ساتھ “arrives” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_153_depart",
            baseForm = "depart",
            thirdPerson = "departs",
            presentParticiple = "departing",
            pastSimple = "departed",
            pastParticiple = "departed",
            urduMeaning = "روانہ ہونا",
            examples = listOf(
                DailyVerbExample("Plane departs at noon.", "جہاز دوپہر کو روانہ ہوتا ہے۔"),
                DailyVerbExample("He departs every day.", "وہ ہر روز روانہ ہونا کرتا ہے۔"),
                DailyVerbExample("They are departing now.", "وہ ابھی روانہ ہونا کر رہے ہیں۔"),
                DailyVerbExample("We departed yesterday.", "ہم نے کل روانہ ہونا کیا۔"),
                DailyVerbExample("She has departed.", "وہ روانہ ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"depart\" with I, you, we, and they. Use \"departs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “depart” اور “He, she, it” کے ساتھ “departs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_154_return",
            baseForm = "return",
            thirdPerson = "returns",
            presentParticiple = "returning",
            pastSimple = "returned",
            pastParticiple = "returned",
            urduMeaning = "واپس آنا",
            examples = listOf(
                DailyVerbExample("Return safely.", "خیریت سے واپس آئیں۔"),
                DailyVerbExample("He returns every day.", "وہ ہر روز واپس آنا کرتا ہے۔"),
                DailyVerbExample("They are returning now.", "وہ ابھی واپس آنا کر رہے ہیں۔"),
                DailyVerbExample("We returned yesterday.", "ہم نے کل واپس آنا کیا۔"),
                DailyVerbExample("She has returned.", "وہ واپس آنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"return\" with I, you, we, and they. Use \"returns\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “return” اور “He, she, it” کے ساتھ “returns” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_155_remind",
            baseForm = "remind",
            thirdPerson = "reminds",
            presentParticiple = "reminding",
            pastSimple = "reminded",
            pastParticiple = "reminded",
            urduMeaning = "یاد دلانا",
            examples = listOf(
                DailyVerbExample("Remind me tomorrow.", "مجھے کل یاد دلانا۔"),
                DailyVerbExample("He reminds every day.", "وہ ہر روز یاد دلانا کرتا ہے۔"),
                DailyVerbExample("They are reminding now.", "وہ ابھی یاد دلانا کر رہے ہیں۔"),
                DailyVerbExample("We reminded yesterday.", "ہم نے کل یاد دلانا کیا۔"),
                DailyVerbExample("She has reminded.", "وہ یاد دلانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"remind\" with I, you, we, and they. Use \"reminds\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “remind” اور “He, she, it” کے ساتھ “reminds” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_156_achieve",
            baseForm = "achieve",
            thirdPerson = "achieves",
            presentParticiple = "achieving",
            pastSimple = "achieved",
            pastParticiple = "achieved",
            urduMeaning = "حاصل کرنا",
            examples = listOf(
                DailyVerbExample("She achieved her goals.", "اس نے اپنے مقاصد حاصل کر لیے۔"),
                DailyVerbExample("He achieves every day.", "وہ ہر روز حاصل کرنا کرتا ہے۔"),
                DailyVerbExample("They are achieving now.", "وہ ابھی حاصل کرنا کر رہے ہیں۔"),
                DailyVerbExample("We achieved yesterday.", "ہم نے کل حاصل کرنا کیا۔"),
                DailyVerbExample("She has achieved.", "وہ حاصل کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"achieve\" with I, you, we, and they. Use \"achieves\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “achieve” اور “He, she, it” کے ساتھ “achieves” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_157_admire",
            baseForm = "admire",
            thirdPerson = "admires",
            presentParticiple = "admiring",
            pastSimple = "admired",
            pastParticiple = "admired",
            urduMeaning = "تعریف کرنا",
            examples = listOf(
                DailyVerbExample("We admire his honesty.", "ہم اس کی دیانت داری کی تعریف کرتے ہیں۔"),
                DailyVerbExample("He admires every day.", "وہ ہر روز تعریف کرنا کرتا ہے۔"),
                DailyVerbExample("They are admiring now.", "وہ ابھی تعریف کرنا کر رہے ہیں۔"),
                DailyVerbExample("We admired yesterday.", "ہم نے کل تعریف کرنا کیا۔"),
                DailyVerbExample("She has admired.", "وہ تعریف کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"admire\" with I, you, we, and they. Use \"admires\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “admire” اور “He, she, it” کے ساتھ “admires” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_158_admit",
            baseForm = "admit",
            thirdPerson = "admits",
            presentParticiple = "admitting",
            pastSimple = "admitted",
            pastParticiple = "admitted",
            urduMeaning = "اعتراف کرنا",
            examples = listOf(
                DailyVerbExample("He admitted his mistake.", "اس نے اپنی غلطی کا اعتراف کیا۔"),
                DailyVerbExample("He admits every day.", "وہ ہر روز اعتراف کرنا کرتا ہے۔"),
                DailyVerbExample("They are admitting now.", "وہ ابھی اعتراف کرنا کر رہے ہیں۔"),
                DailyVerbExample("We admitted yesterday.", "ہم نے کل اعتراف کرنا کیا۔"),
                DailyVerbExample("She has admitted.", "وہ اعتراف کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"admit\" with I, you, we, and they. Use \"admits\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “admit” اور “He, she, it” کے ساتھ “admits” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_159_advise",
            baseForm = "advise",
            thirdPerson = "advises",
            presentParticiple = "advising",
            pastSimple = "advised",
            pastParticiple = "advised",
            urduMeaning = "نصیحت کرنا",
            examples = listOf(
                DailyVerbExample("Teachers advise students.", "اساتذہ طلباء کو نصیحت کرتے ہیں۔"),
                DailyVerbExample("He advises every day.", "وہ ہر روز نصیحت کرنا کرتا ہے۔"),
                DailyVerbExample("They are advising now.", "وہ ابھی نصیحت کرنا کر رہے ہیں۔"),
                DailyVerbExample("We advised yesterday.", "ہم نے کل نصیحت کرنا کیا۔"),
                DailyVerbExample("She has advised.", "وہ نصیحت کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"advise\" with I, you, we, and they. Use \"advises\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “advise” اور “He, she, it” کے ساتھ “advises” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_160_afford",
            baseForm = "afford",
            thirdPerson = "affords",
            presentParticiple = "affording",
            pastSimple = "afforded",
            pastParticiple = "afforded",
            urduMeaning = "استطاعت رکھنا",
            examples = listOf(
                DailyVerbExample("I can afford this book.", "میں اس کتاب کی استطاعت رکھتا ہوں۔"),
                DailyVerbExample("He affords every day.", "وہ ہر روز استطاعت رکھنا کرتا ہے۔"),
                DailyVerbExample("They are affording now.", "وہ ابھی استطاعت رکھنا کر رہے ہیں۔"),
                DailyVerbExample("We afforded yesterday.", "ہم نے کل استطاعت رکھنا کیا۔"),
                DailyVerbExample("She has afforded.", "وہ استطاعت رکھنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"afford\" with I, you, we, and they. Use \"affords\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “afford” اور “He, she, it” کے ساتھ “affords” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_161_announce",
            baseForm = "announce",
            thirdPerson = "announces",
            presentParticiple = "announcing",
            pastSimple = "announced",
            pastParticiple = "announced",
            urduMeaning = "اعلان کرنا",
            examples = listOf(
                DailyVerbExample("They announced the winner.", "انہوں نے فاتح کا اعلان کیا۔"),
                DailyVerbExample("He announces every day.", "وہ ہر روز اعلان کرنا کرتا ہے۔"),
                DailyVerbExample("They are announcing now.", "وہ ابھی اعلان کرنا کر رہے ہیں۔"),
                DailyVerbExample("We announced yesterday.", "ہم نے کل اعلان کرنا کیا۔"),
                DailyVerbExample("She has announced.", "وہ اعلان کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"announce\" with I, you, we, and they. Use \"announces\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “announce” اور “He, she, it” کے ساتھ “announces” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_162_answer",
            baseForm = "answer",
            thirdPerson = "answers",
            presentParticiple = "answering",
            pastSimple = "answered",
            pastParticiple = "answered",
            urduMeaning = "جواب دینا",
            examples = listOf(
                DailyVerbExample("Answer the question.", "سوال کا جواب دیں۔"),
                DailyVerbExample("He answers every day.", "وہ ہر روز جواب دینا کرتا ہے۔"),
                DailyVerbExample("They are answering now.", "وہ ابھی جواب دینا کر رہے ہیں۔"),
                DailyVerbExample("We answered yesterday.", "ہم نے کل جواب دینا کیا۔"),
                DailyVerbExample("She has answered.", "وہ جواب دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"answer\" with I, you, we, and they. Use \"answers\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “answer” اور “He, she, it” کے ساتھ “answers” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_163_apologize",
            baseForm = "apologize",
            thirdPerson = "apologizes",
            presentParticiple = "apologizing",
            pastSimple = "apologized",
            pastParticiple = "apologized",
            urduMeaning = "معافی مانگنا",
            examples = listOf(
                DailyVerbExample("Apologize for being late.", "دیر سے آنے پر معافی مانگیں۔"),
                DailyVerbExample("He apologizes every day.", "وہ ہر روز معافی مانگنا کرتا ہے۔"),
                DailyVerbExample("They are apologizing now.", "وہ ابھی معافی مانگنا کر رہے ہیں۔"),
                DailyVerbExample("We apologized yesterday.", "ہم نے کل معافی مانگنا کیا۔"),
                DailyVerbExample("She has apologized.", "وہ معافی مانگنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"apologize\" with I, you, we, and they. Use \"apologizes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “apologize” اور “He, she, it” کے ساتھ “apologizes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_164_apply",
            baseForm = "apply",
            thirdPerson = "applies",
            presentParticiple = "applying",
            pastSimple = "applied",
            pastParticiple = "applied",
            urduMeaning = "درخواست دینا",
            examples = listOf(
                DailyVerbExample("Apply for the job.", "نوکری کے لیے درخواست دیں۔"),
                DailyVerbExample("He applies every day.", "وہ ہر روز درخواست دینا کرتا ہے۔"),
                DailyVerbExample("They are applying now.", "وہ ابھی درخواست دینا کر رہے ہیں۔"),
                DailyVerbExample("We applied yesterday.", "ہم نے کل درخواست دینا کیا۔"),
                DailyVerbExample("She has applied.", "وہ درخواست دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"apply\" with I, you, we, and they. Use \"applies\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “apply” اور “He, she, it” کے ساتھ “applies” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_165_appreciate",
            baseForm = "appreciate",
            thirdPerson = "appreciates",
            presentParticiple = "appreciating",
            pastSimple = "appreciated",
            pastParticiple = "appreciated",
            urduMeaning = "قدر کرنا",
            examples = listOf(
                DailyVerbExample("We appreciate your help.", "ہم آپ کی مدد کی قدر کرتے ہیں۔"),
                DailyVerbExample("He appreciates every day.", "وہ ہر روز قدر کرنا کرتا ہے۔"),
                DailyVerbExample("They are appreciating now.", "وہ ابھی قدر کرنا کر رہے ہیں۔"),
                DailyVerbExample("We appreciated yesterday.", "ہم نے کل قدر کرنا کیا۔"),
                DailyVerbExample("She has appreciated.", "وہ قدر کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"appreciate\" with I, you, we, and they. Use \"appreciates\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “appreciate” اور “He, she, it” کے ساتھ “appreciates” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_166_approve",
            baseForm = "approve",
            thirdPerson = "approves",
            presentParticiple = "approving",
            pastSimple = "approved",
            pastParticiple = "approved",
            urduMeaning = "منظوری دینا",
            examples = listOf(
                DailyVerbExample("The principal approved.", "پرنسپل نے منظوری دے دی۔"),
                DailyVerbExample("He approves every day.", "وہ ہر روز منظوری دینا کرتا ہے۔"),
                DailyVerbExample("They are approving now.", "وہ ابھی منظوری دینا کر رہے ہیں۔"),
                DailyVerbExample("We approved yesterday.", "ہم نے کل منظوری دینا کیا۔"),
                DailyVerbExample("She has approved.", "وہ منظوری دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"approve\" with I, you, we, and they. Use \"approves\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “approve” اور “He, she, it” کے ساتھ “approves” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_167_arrange",
            baseForm = "arrange",
            thirdPerson = "arranges",
            presentParticiple = "arranging",
            pastSimple = "arranged",
            pastParticiple = "arranged",
            urduMeaning = "انتظام کرنا",
            examples = listOf(
                DailyVerbExample("Arrange the chairs.", "کرسیوں کا انتظام کریں۔"),
                DailyVerbExample("He arranges every day.", "وہ ہر روز انتظام کرنا کرتا ہے۔"),
                DailyVerbExample("They are arranging now.", "وہ ابھی انتظام کرنا کر رہے ہیں۔"),
                DailyVerbExample("We arranged yesterday.", "ہم نے کل انتظام کرنا کیا۔"),
                DailyVerbExample("She has arranged.", "وہ انتظام کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"arrange\" with I, you, we, and they. Use \"arranges\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “arrange” اور “He, she, it” کے ساتھ “arranges” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_168_arrest",
            baseForm = "arrest",
            thirdPerson = "arrests",
            presentParticiple = "arresting",
            pastSimple = "arrested",
            pastParticiple = "arrested",
            urduMeaning = "گرفتار کرنا",
            examples = listOf(
                DailyVerbExample("Police arrested the thief.", "پولیس نے چور کو گرفتار کر لیا۔"),
                DailyVerbExample("He arrests every day.", "وہ ہر روز گرفتار کرنا کرتا ہے۔"),
                DailyVerbExample("They are arresting now.", "وہ ابھی گرفتار کرنا کر رہے ہیں۔"),
                DailyVerbExample("We arrested yesterday.", "ہم نے کل گرفتار کرنا کیا۔"),
                DailyVerbExample("She has arrested.", "وہ گرفتار کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"arrest\" with I, you, we, and they. Use \"arrests\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “arrest” اور “He, she, it” کے ساتھ “arrests” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_169_assist",
            baseForm = "assist",
            thirdPerson = "assists",
            presentParticiple = "assisting",
            pastSimple = "assisted",
            pastParticiple = "assisted",
            urduMeaning = "مدد کرنا",
            examples = listOf(
                DailyVerbExample("Assist the old lady.", "معمر خاتون کی مدد کریں۔"),
                DailyVerbExample("He assists every day.", "وہ ہر روز مدد کرنا کرتا ہے۔"),
                DailyVerbExample("They are assisting now.", "وہ ابھی مدد کرنا کر رہے ہیں۔"),
                DailyVerbExample("We assisted yesterday.", "ہم نے کل مدد کرنا کیا۔"),
                DailyVerbExample("She has assisted.", "وہ مدد کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"assist\" with I, you, we, and they. Use \"assists\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “assist” اور “He, she, it” کے ساتھ “assists” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_170_attach",
            baseForm = "attach",
            thirdPerson = "attaches",
            presentParticiple = "attaching",
            pastSimple = "attached",
            pastParticiple = "attached",
            urduMeaning = "منسلک کرنا",
            examples = listOf(
                DailyVerbExample("Attach the file.", "فائل منسلک کریں۔"),
                DailyVerbExample("He attaches every day.", "وہ ہر روز منسلک کرنا کرتا ہے۔"),
                DailyVerbExample("They are attaching now.", "وہ ابھی منسلک کرنا کر رہے ہیں۔"),
                DailyVerbExample("We attached yesterday.", "ہم نے کل منسلک کرنا کیا۔"),
                DailyVerbExample("She has attached.", "وہ منسلک کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"attach\" with I, you, we, and they. Use \"attaches\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “attach” اور “He, she, it” کے ساتھ “attaches” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_171_attend",
            baseForm = "attend",
            thirdPerson = "attends",
            presentParticiple = "attending",
            pastSimple = "attended",
            pastParticiple = "attended",
            urduMeaning = "حاضری دینا",
            examples = listOf(
                DailyVerbExample("Attend the lecture.", "لیکچر میں شرکت کریں۔"),
                DailyVerbExample("He attends every day.", "وہ ہر روز حاضری دینا کرتا ہے۔"),
                DailyVerbExample("They are attending now.", "وہ ابھی حاضری دینا کر رہے ہیں۔"),
                DailyVerbExample("We attended yesterday.", "ہم نے کل حاضری دینا کیا۔"),
                DailyVerbExample("She has attended.", "وہ حاضری دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"attend\" with I, you, we, and they. Use \"attends\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “attend” اور “He, she, it” کے ساتھ “attends” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_172_attract",
            baseForm = "attract",
            thirdPerson = "attracts",
            presentParticiple = "attracting",
            pastSimple = "attracted",
            pastParticiple = "attracted",
            urduMeaning = "متوجہ کرنا",
            examples = listOf(
                DailyVerbExample("Flowers attract bees.", "پھول شہد کی مکھیوں کو متوجہ کرتے ہیں۔"),
                DailyVerbExample("He attracts every day.", "وہ ہر روز متوجہ کرنا کرتا ہے۔"),
                DailyVerbExample("They are attracting now.", "وہ ابھی متوجہ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We attracted yesterday.", "ہم نے کل متوجہ کرنا کیا۔"),
                DailyVerbExample("She has attracted.", "وہ متوجہ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"attract\" with I, you, we, and they. Use \"attracts\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “attract” اور “He, she, it” کے ساتھ “attracts” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_173_avoid",
            baseForm = "avoid",
            thirdPerson = "avoids",
            presentParticiple = "avoiding",
            pastSimple = "avoided",
            pastParticiple = "avoided",
            urduMeaning = "بچنا",
            examples = listOf(
                DailyVerbExample("Avoid bad habits.", "بری عادتوں سے بچیں۔"),
                DailyVerbExample("He avoids every day.", "وہ ہر روز بچنا کرتا ہے۔"),
                DailyVerbExample("They are avoiding now.", "وہ ابھی بچنا کر رہے ہیں۔"),
                DailyVerbExample("We avoided yesterday.", "ہم نے کل بچنا کیا۔"),
                DailyVerbExample("She has avoided.", "وہ بچنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"avoid\" with I, you, we, and they. Use \"avoids\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “avoid” اور “He, she, it” کے ساتھ “avoids” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_174_awake",
            baseForm = "awake",
            thirdPerson = "awakes",
            presentParticiple = "awaking",
            pastSimple = "awoke",
            pastParticiple = "awoken",
            urduMeaning = "جاگنا",
            examples = listOf(
                DailyVerbExample("Awake early.", "صبح سویرے جاگیں۔"),
                DailyVerbExample("He awakes every day.", "وہ ہر روز جاگنا کرتا ہے۔"),
                DailyVerbExample("They are awaking now.", "وہ ابھی جاگنا کر رہے ہیں۔"),
                DailyVerbExample("We awoke yesterday.", "ہم نے کل جاگنا کیا۔"),
                DailyVerbExample("She has awoken.", "وہ جاگنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"awake\" with I, you, we, and they. Use \"awakes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “awake” اور “He, she, it” کے ساتھ “awakes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_175_belong",
            baseForm = "belong",
            thirdPerson = "belongs",
            presentParticiple = "belonging",
            pastSimple = "belonged",
            pastParticiple = "belonged",
            urduMeaning = "تعلق رکھنا",
            examples = listOf(
                DailyVerbExample("This belongs to me.", "یہ مجھ سے تعلق رکھتا ہے۔"),
                DailyVerbExample("He belongs every day.", "وہ ہر روز تعلق رکھنا کرتا ہے۔"),
                DailyVerbExample("They are belonging now.", "وہ ابھی تعلق رکھنا کر رہے ہیں۔"),
                DailyVerbExample("We belonged yesterday.", "ہم نے کل تعلق رکھنا کیا۔"),
                DailyVerbExample("She has belonged.", "وہ تعلق رکھنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"belong\" with I, you, we, and they. Use \"belongs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “belong” اور “He, she, it” کے ساتھ “belongs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_176_bend",
            baseForm = "bend",
            thirdPerson = "bends",
            presentParticiple = "bending",
            pastSimple = "bent",
            pastParticiple = "bent",
            urduMeaning = "جھکنا",
            examples = listOf(
                DailyVerbExample("Bend down carefully.", "احتیاط سے نیچے جھکیں۔"),
                DailyVerbExample("He bends every day.", "وہ ہر روز جھکنا کرتا ہے۔"),
                DailyVerbExample("They are bending now.", "وہ ابھی جھکنا کر رہے ہیں۔"),
                DailyVerbExample("We bent yesterday.", "ہم نے کل جھکنا کیا۔"),
                DailyVerbExample("She has bent.", "وہ جھکنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"bend\" with I, you, we, and they. Use \"bends\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “bend” اور “He, she, it” کے ساتھ “bends” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_177_bite",
            baseForm = "bite",
            thirdPerson = "bites",
            presentParticiple = "biting",
            pastSimple = "bit",
            pastParticiple = "bitten",
            urduMeaning = "کاٹنا",
            examples = listOf(
                DailyVerbExample("Mosquitoes bite.", "مچھر کاٹتے ہیں۔"),
                DailyVerbExample("He bites every day.", "وہ ہر روز کاٹنا کرتا ہے۔"),
                DailyVerbExample("They are biting now.", "وہ ابھی کاٹنا کر رہے ہیں۔"),
                DailyVerbExample("We bit yesterday.", "ہم نے کل کاٹنا کیا۔"),
                DailyVerbExample("She has bitten.", "وہ کاٹنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"bite\" with I, you, we, and they. Use \"bites\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “bite” اور “He, she, it” کے ساتھ “bites” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_178_bleed",
            baseForm = "bleed",
            thirdPerson = "bleeds",
            presentParticiple = "bleeding",
            pastSimple = "bled",
            pastParticiple = "bled",
            urduMeaning = "خون بہنا",
            examples = listOf(
                DailyVerbExample("The wound bled.", "زخم سے خون بہا۔"),
                DailyVerbExample("He bleeds every day.", "وہ ہر روز خون بہنا کرتا ہے۔"),
                DailyVerbExample("They are bleeding now.", "وہ ابھی خون بہنا کر رہے ہیں۔"),
                DailyVerbExample("We bled yesterday.", "ہم نے کل خون بہنا کیا۔"),
                DailyVerbExample("She has bled.", "وہ خون بہنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"bleed\" with I, you, we, and they. Use \"bleeds\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “bleed” اور “He, she, it” کے ساتھ “bleeds” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_179_bless",
            baseForm = "bless",
            thirdPerson = "blesses",
            presentParticiple = "blessing",
            pastSimple = "blessed",
            pastParticiple = "blessed",
            urduMeaning = "دعا دینا",
            examples = listOf(
                DailyVerbExample("God bless you.", "اللہ آپ کو برکت دے۔"),
                DailyVerbExample("He blesses every day.", "وہ ہر روز دعا دینا کرتا ہے۔"),
                DailyVerbExample("They are blessing now.", "وہ ابھی دعا دینا کر رہے ہیں۔"),
                DailyVerbExample("We blessed yesterday.", "ہم نے کل دعا دینا کیا۔"),
                DailyVerbExample("She has blessed.", "وہ دعا دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"bless\" with I, you, we, and they. Use \"blesses\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “bless” اور “He, she, it” کے ساتھ “blesses” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_180_blink",
            baseForm = "blink",
            thirdPerson = "blinks",
            presentParticiple = "blinking",
            pastSimple = "blinked",
            pastParticiple = "blinked",
            urduMeaning = "پلکیں جھپکنا",
            examples = listOf(
                DailyVerbExample("Blink your eyes.", "اپنی پلکیں جھپکائیں۔"),
                DailyVerbExample("He blinks every day.", "وہ ہر روز پلکیں جھپکنا کرتا ہے۔"),
                DailyVerbExample("They are blinking now.", "وہ ابھی پلکیں جھپکنا کر رہے ہیں۔"),
                DailyVerbExample("We blinked yesterday.", "ہم نے کل پلکیں جھپکنا کیا۔"),
                DailyVerbExample("She has blinked.", "وہ پلکیں جھپکنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"blink\" with I, you, we, and they. Use \"blinks\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “blink” اور “He, she, it” کے ساتھ “blinks” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_181_breathe",
            baseForm = "breathe",
            thirdPerson = "breathes",
            presentParticiple = "breathing",
            pastSimple = "breathed",
            pastParticiple = "breathed",
            urduMeaning = "سانس لینا",
            examples = listOf(
                DailyVerbExample("Breathe deeply.", "گہری سانس لیں۔"),
                DailyVerbExample("He breathes every day.", "وہ ہر روز سانس لینا کرتا ہے۔"),
                DailyVerbExample("They are breathing now.", "وہ ابھی سانس لینا کر رہے ہیں۔"),
                DailyVerbExample("We breathed yesterday.", "ہم نے کل سانس لینا کیا۔"),
                DailyVerbExample("She has breathed.", "وہ سانس لینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"breathe\" with I, you, we, and they. Use \"breathes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “breathe” اور “He, she, it” کے ساتھ “breathes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_182_brush",
            baseForm = "brush",
            thirdPerson = "brushes",
            presentParticiple = "brushing",
            pastSimple = "brushed",
            pastParticiple = "brushed",
            urduMeaning = "برش کرنا",
            examples = listOf(
                DailyVerbExample("Brush your teeth.", "اپنے دانت صاف کریں۔"),
                DailyVerbExample("He brushes every day.", "وہ ہر روز برش کرنا کرتا ہے۔"),
                DailyVerbExample("They are brushing now.", "وہ ابھی برش کرنا کر رہے ہیں۔"),
                DailyVerbExample("We brushed yesterday.", "ہم نے کل برش کرنا کیا۔"),
                DailyVerbExample("She has brushed.", "وہ برش کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"brush\" with I, you, we, and they. Use \"brushes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “brush” اور “He, she, it” کے ساتھ “brushes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_183_calculate",
            baseForm = "calculate",
            thirdPerson = "calculates",
            presentParticiple = "calculating",
            pastSimple = "calculated",
            pastParticiple = "calculated",
            urduMeaning = "حساب لگانا",
            examples = listOf(
                DailyVerbExample("Calculate the sum.", "مجموعے کا حساب لگائیں۔"),
                DailyVerbExample("He calculates every day.", "وہ ہر روز حساب لگانا کرتا ہے۔"),
                DailyVerbExample("They are calculating now.", "وہ ابھی حساب لگانا کر رہے ہیں۔"),
                DailyVerbExample("We calculated yesterday.", "ہم نے کل حساب لگانا کیا۔"),
                DailyVerbExample("She has calculated.", "وہ حساب لگانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"calculate\" with I, you, we, and they. Use \"calculates\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “calculate” اور “He, she, it” کے ساتھ “calculates” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_184_cancel",
            baseForm = "cancel",
            thirdPerson = "cancels",
            presentParticiple = "canceling",
            pastSimple = "canceled",
            pastParticiple = "canceled",
            urduMeaning = "منسوخ کرنا",
            examples = listOf(
                DailyVerbExample("Cancel the order.", "آرڈر منسوخ کریں۔"),
                DailyVerbExample("He cancels every day.", "وہ ہر روز منسوخ کرنا کرتا ہے۔"),
                DailyVerbExample("They are canceling now.", "وہ ابھی منسوخ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We canceled yesterday.", "ہم نے کل منسوخ کرنا کیا۔"),
                DailyVerbExample("She has canceled.", "وہ منسوخ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"cancel\" with I, you, we, and they. Use \"cancels\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “cancel” اور “He, she, it” کے ساتھ “cancels” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_185_capture",
            baseForm = "capture",
            thirdPerson = "captures",
            presentParticiple = "capturing",
            pastSimple = "captured",
            pastParticiple = "captured",
            urduMeaning = "قبضہ کرنا",
            examples = listOf(
                DailyVerbExample("Capture the image.", "تصویر محفوظ کریں۔"),
                DailyVerbExample("He captures every day.", "وہ ہر روز قبضہ کرنا کرتا ہے۔"),
                DailyVerbExample("They are capturing now.", "وہ ابھی قبضہ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We captured yesterday.", "ہم نے کل قبضہ کرنا کیا۔"),
                DailyVerbExample("She has captured.", "وہ قبضہ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"capture\" with I, you, we, and they. Use \"captures\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “capture” اور “He, she, it” کے ساتھ “captures” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_186_challenge",
            baseForm = "challenge",
            thirdPerson = "challenges",
            presentParticiple = "challenging",
            pastSimple = "challenged",
            pastParticiple = "challenged",
            urduMeaning = "چیلنج کرنا",
            examples = listOf(
                DailyVerbExample("Challenge yourself.", "خود کو چیلنج کریں۔"),
                DailyVerbExample("He challenges every day.", "وہ ہر روز چیلنج کرنا کرتا ہے۔"),
                DailyVerbExample("They are challenging now.", "وہ ابھی چیلنج کرنا کر رہے ہیں۔"),
                DailyVerbExample("We challenged yesterday.", "ہم نے کل چیلنج کرنا کیا۔"),
                DailyVerbExample("She has challenged.", "وہ چیلنج کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"challenge\" with I, you, we, and they. Use \"challenges\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “challenge” اور “He, she, it” کے ساتھ “challenges” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_187_charge",
            baseForm = "charge",
            thirdPerson = "charges",
            presentParticiple = "charging",
            pastSimple = "charged",
            pastParticiple = "charged",
            urduMeaning = "چارج کرنا",
            examples = listOf(
                DailyVerbExample("Charge your battery.", "اپنی بیٹری چارج کریں۔"),
                DailyVerbExample("He charges every day.", "وہ ہر روز چارج کرنا کرتا ہے۔"),
                DailyVerbExample("They are charging now.", "وہ ابھی چارج کرنا کر رہے ہیں۔"),
                DailyVerbExample("We charged yesterday.", "ہم نے کل چارج کرنا کیا۔"),
                DailyVerbExample("She has charged.", "وہ چارج کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"charge\" with I, you, we, and they. Use \"charges\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “charge” اور “He, she, it” کے ساتھ “charges” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_188_chase",
            baseForm = "chase",
            thirdPerson = "chases",
            presentParticiple = "chasing",
            pastSimple = "chased",
            pastParticiple = "chased",
            urduMeaning = "تعاقب کرنا",
            examples = listOf(
                DailyVerbExample("Cats chase mice.", "بلیاں چوہوں کا تعاقب کرتی ہیں۔"),
                DailyVerbExample("He chases every day.", "وہ ہر روز تعاقب کرنا کرتا ہے۔"),
                DailyVerbExample("They are chasing now.", "وہ ابھی تعاقب کرنا کر رہے ہیں۔"),
                DailyVerbExample("We chased yesterday.", "ہم نے کل تعاقب کرنا کیا۔"),
                DailyVerbExample("She has chased.", "وہ تعاقب کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"chase\" with I, you, we, and they. Use \"chases\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “chase” اور “He, she, it” کے ساتھ “chases” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_189_cheat",
            baseForm = "cheat",
            thirdPerson = "cheats",
            presentParticiple = "cheating",
            pastSimple = "cheated",
            pastParticiple = "cheated",
            urduMeaning = "دھوکہ دینا",
            examples = listOf(
                DailyVerbExample("Never cheat others.", "کبھی دوسروں کو دھوکہ نہ دیں۔"),
                DailyVerbExample("He cheats every day.", "وہ ہر روز دھوکہ دینا کرتا ہے۔"),
                DailyVerbExample("They are cheating now.", "وہ ابھی دھوکہ دینا کر رہے ہیں۔"),
                DailyVerbExample("We cheated yesterday.", "ہم نے کل دھوکہ دینا کیا۔"),
                DailyVerbExample("She has cheated.", "وہ دھوکہ دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"cheat\" with I, you, we, and they. Use \"cheats\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “cheat” اور “He, she, it” کے ساتھ “cheats” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_190_cheer",
            baseForm = "cheer",
            thirdPerson = "cheers",
            presentParticiple = "cheering",
            pastSimple = "cheered",
            pastParticiple = "cheered",
            urduMeaning = "حوصلہ افزائی کرنا",
            examples = listOf(
                DailyVerbExample("Fans cheer loudly.", "شائقین نے پرجوش نعرے لگائے۔"),
                DailyVerbExample("He cheers every day.", "وہ ہر روز حوصلہ افزائی کرنا کرتا ہے۔"),
                DailyVerbExample("They are cheering now.", "وہ ابھی حوصلہ افزائی کرنا کر رہے ہیں۔"),
                DailyVerbExample("We cheered yesterday.", "ہم نے کل حوصلہ افزائی کرنا کیا۔"),
                DailyVerbExample("She has cheered.", "وہ حوصلہ افزائی کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"cheer\" with I, you, we, and they. Use \"cheers\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “cheer” اور “He, she, it” کے ساتھ “cheers” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_191_collect",
            baseForm = "collect",
            thirdPerson = "collects",
            presentParticiple = "collecting",
            pastSimple = "collected",
            pastParticiple = "collected",
            urduMeaning = "جمع کرنا",
            examples = listOf(
                DailyVerbExample("Collect coins.", "سکے جمع کریں۔"),
                DailyVerbExample("He collects every day.", "وہ ہر روز جمع کرنا کرتا ہے۔"),
                DailyVerbExample("They are collecting now.", "وہ ابھی جمع کرنا کر رہے ہیں۔"),
                DailyVerbExample("We collected yesterday.", "ہم نے کل جمع کرنا کیا۔"),
                DailyVerbExample("She has collected.", "وہ جمع کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"collect\" with I, you, we, and they. Use \"collects\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “collect” اور “He, she, it” کے ساتھ “collects” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_192_combine",
            baseForm = "combine",
            thirdPerson = "combines",
            presentParticiple = "combining",
            pastSimple = "combined",
            pastParticiple = "combined",
            urduMeaning = "ملانا",
            examples = listOf(
                DailyVerbExample("Combine the colors.", "رنگوں کو ملائیں۔"),
                DailyVerbExample("He combines every day.", "وہ ہر روز ملانا کرتا ہے۔"),
                DailyVerbExample("They are combining now.", "وہ ابھی ملانا کر رہے ہیں۔"),
                DailyVerbExample("We combined yesterday.", "ہم نے کل ملانا کیا۔"),
                DailyVerbExample("She has combined.", "وہ ملانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"combine\" with I, you, we, and they. Use \"combines\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “combine” اور “He, she, it” کے ساتھ “combines” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_193_command",
            baseForm = "command",
            thirdPerson = "commands",
            presentParticiple = "commanding",
            pastSimple = "commanded",
            pastParticiple = "commanded",
            urduMeaning = "حکم دینا",
            examples = listOf(
                DailyVerbExample("Officers command.", "افسران حکم دیتے ہیں۔"),
                DailyVerbExample("He commands every day.", "وہ ہر روز حکم دینا کرتا ہے۔"),
                DailyVerbExample("They are commanding now.", "وہ ابھی حکم دینا کر رہے ہیں۔"),
                DailyVerbExample("We commanded yesterday.", "ہم نے کل حکم دینا کیا۔"),
                DailyVerbExample("She has commanded.", "وہ حکم دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"command\" with I, you, we, and they. Use \"commands\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “command” اور “He, she, it” کے ساتھ “commands” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_194_communicate",
            baseForm = "communicate",
            thirdPerson = "communicates",
            presentParticiple = "communicating",
            pastSimple = "communicated",
            pastParticiple = "communicated",
            urduMeaning = "رابطہ کرنا",
            examples = listOf(
                DailyVerbExample("Communicate clearly.", "واضح رابطہ کریں۔"),
                DailyVerbExample("He communicates every day.", "وہ ہر روز رابطہ کرنا کرتا ہے۔"),
                DailyVerbExample("They are communicating now.", "وہ ابھی رابطہ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We communicated yesterday.", "ہم نے کل رابطہ کرنا کیا۔"),
                DailyVerbExample("She has communicated.", "وہ رابطہ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"communicate\" with I, you, we, and they. Use \"communicates\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “communicate” اور “He, she, it” کے ساتھ “communicates” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_195_compare",
            baseForm = "compare",
            thirdPerson = "compares",
            presentParticiple = "comparing",
            pastSimple = "compared",
            pastParticiple = "compared",
            urduMeaning = "موازنہ کرنا",
            examples = listOf(
                DailyVerbExample("Compare the prices.", "قیمتوں کا موازنہ کریں۔"),
                DailyVerbExample("He compares every day.", "وہ ہر روز موازنہ کرنا کرتا ہے۔"),
                DailyVerbExample("They are comparing now.", "وہ ابھی موازنہ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We compared yesterday.", "ہم نے کل موازنہ کرنا کیا۔"),
                DailyVerbExample("She has compared.", "وہ موازنہ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"compare\" with I, you, we, and they. Use \"compares\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “compare” اور “He, she, it” کے ساتھ “compares” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_196_compete",
            baseForm = "compete",
            thirdPerson = "competes",
            presentParticiple = "competing",
            pastSimple = "competed",
            pastParticiple = "competed",
            urduMeaning = "مقابلہ کرنا",
            examples = listOf(
                DailyVerbExample("Compete fairly.", "منصفانہ مقابلہ کریں۔"),
                DailyVerbExample("He competes every day.", "وہ ہر روز مقابلہ کرنا کرتا ہے۔"),
                DailyVerbExample("They are competing now.", "وہ ابھی مقابلہ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We competed yesterday.", "ہم نے کل مقابلہ کرنا کیا۔"),
                DailyVerbExample("She has competed.", "وہ مقابلہ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"compete\" with I, you, we, and they. Use \"competes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “compete” اور “He, she, it” کے ساتھ “competes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_197_complain",
            baseForm = "complain",
            thirdPerson = "complains",
            presentParticiple = "complaining",
            pastSimple = "complained",
            pastParticiple = "complained",
            urduMeaning = "شکایت کرنا",
            examples = listOf(
                DailyVerbExample("Don't complain.", "شکایت نہ کریں۔"),
                DailyVerbExample("He complains every day.", "وہ ہر روز شکایت کرنا کرتا ہے۔"),
                DailyVerbExample("They are complaining now.", "وہ ابھی شکایت کرنا کر رہے ہیں۔"),
                DailyVerbExample("We complained yesterday.", "ہم نے کل شکایت کرنا کیا۔"),
                DailyVerbExample("She has complained.", "وہ شکایت کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"complain\" with I, you, we, and they. Use \"complains\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “complain” اور “He, she, it” کے ساتھ “complains” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_198_complete",
            baseForm = "complete",
            thirdPerson = "completes",
            presentParticiple = "completing",
            pastSimple = "completed",
            pastParticiple = "completed",
            urduMeaning = "مکمل کرنا",
            examples = listOf(
                DailyVerbExample("Complete the task.", "کام مکمل کریں۔"),
                DailyVerbExample("He completes every day.", "وہ ہر روز مکمل کرنا کرتا ہے۔"),
                DailyVerbExample("They are completing now.", "وہ ابھی مکمل کرنا کر رہے ہیں۔"),
                DailyVerbExample("We completed yesterday.", "ہم نے کل مکمل کرنا کیا۔"),
                DailyVerbExample("She has completed.", "وہ مکمل کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"complete\" with I, you, we, and they. Use \"completes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “complete” اور “He, she, it” کے ساتھ “completes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_199_concentrate",
            baseForm = "concentrate",
            thirdPerson = "concentrates",
            presentParticiple = "concentrating",
            pastSimple = "concentrated",
            pastParticiple = "concentrated",
            urduMeaning = "توجہ دینا",
            examples = listOf(
                DailyVerbExample("Concentrate on studies.", "پڑھائی پر توجہ دیں۔"),
                DailyVerbExample("He concentrates every day.", "وہ ہر روز توجہ دینا کرتا ہے۔"),
                DailyVerbExample("They are concentrating now.", "وہ ابھی توجہ دینا کر رہے ہیں۔"),
                DailyVerbExample("We concentrated yesterday.", "ہم نے کل توجہ دینا کیا۔"),
                DailyVerbExample("She has concentrated.", "وہ توجہ دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"concentrate\" with I, you, we, and they. Use \"concentrates\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “concentrate” اور “He, she, it” کے ساتھ “concentrates” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_200_confirm",
            baseForm = "confirm",
            thirdPerson = "confirms",
            presentParticiple = "confirming",
            pastSimple = "confirmed",
            pastParticiple = "confirmed",
            urduMeaning = "تصدیق کرنا",
            examples = listOf(
                DailyVerbExample("Confirm the date.", "تاریخ کی تصدیق کریں۔"),
                DailyVerbExample("He confirms every day.", "وہ ہر روز تصدیق کرنا کرتا ہے۔"),
                DailyVerbExample("They are confirming now.", "وہ ابھی تصدیق کرنا کر رہے ہیں۔"),
                DailyVerbExample("We confirmed yesterday.", "ہم نے کل تصدیق کرنا کیا۔"),
                DailyVerbExample("She has confirmed.", "وہ تصدیق کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"confirm\" with I, you, we, and they. Use \"confirms\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “confirm” اور “He, she, it” کے ساتھ “confirms” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_201_connect",
            baseForm = "connect",
            thirdPerson = "connects",
            presentParticiple = "connecting",
            pastSimple = "connected",
            pastParticiple = "connected",
            urduMeaning = "جوڑنا",
            examples = listOf(
                DailyVerbExample("Connect the cable.", "کیبل جوڑیں۔"),
                DailyVerbExample("He connects every day.", "وہ ہر روز جوڑنا کرتا ہے۔"),
                DailyVerbExample("They are connecting now.", "وہ ابھی جوڑنا کر رہے ہیں۔"),
                DailyVerbExample("We connected yesterday.", "ہم نے کل جوڑنا کیا۔"),
                DailyVerbExample("She has connected.", "وہ جوڑنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"connect\" with I, you, we, and they. Use \"connects\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “connect” اور “He, she, it” کے ساتھ “connects” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_202_consider",
            baseForm = "consider",
            thirdPerson = "considers",
            presentParticiple = "considering",
            pastSimple = "considered",
            pastParticiple = "considered",
            urduMeaning = "غور کرنا",
            examples = listOf(
                DailyVerbExample("Consider the options.", "اختیارات پر غور کریں۔"),
                DailyVerbExample("He considers every day.", "وہ ہر روز غور کرنا کرتا ہے۔"),
                DailyVerbExample("They are considering now.", "وہ ابھی غور کرنا کر رہے ہیں۔"),
                DailyVerbExample("We considered yesterday.", "ہم نے کل غور کرنا کیا۔"),
                DailyVerbExample("She has considered.", "وہ غور کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"consider\" with I, you, we, and they. Use \"considers\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “consider” اور “He, she, it” کے ساتھ “considers” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_203_construct",
            baseForm = "construct",
            thirdPerson = "constructs",
            presentParticiple = "constructing",
            pastSimple = "constructed",
            pastParticiple = "constructed",
            urduMeaning = "تعمیر کرنا",
            examples = listOf(
                DailyVerbExample("Construct a road.", "سڑک تعمیر کریں۔"),
                DailyVerbExample("He constructs every day.", "وہ ہر روز تعمیر کرنا کرتا ہے۔"),
                DailyVerbExample("They are constructing now.", "وہ ابھی تعمیر کرنا کر رہے ہیں۔"),
                DailyVerbExample("We constructed yesterday.", "ہم نے کل تعمیر کرنا کیا۔"),
                DailyVerbExample("She has constructed.", "وہ تعمیر کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"construct\" with I, you, we, and they. Use \"constructs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “construct” اور “He, she, it” کے ساتھ “constructs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_204_contain",
            baseForm = "contain",
            thirdPerson = "contains",
            presentParticiple = "containing",
            pastSimple = "contained",
            pastParticiple = "contained",
            urduMeaning = "مشتمل ہونا",
            examples = listOf(
                DailyVerbExample("Milk contains calcium.", "دودھ کیلشیم پر مشتمل ہوتا ہے۔"),
                DailyVerbExample("He contains every day.", "وہ ہر روز مشتمل ہونا کرتا ہے۔"),
                DailyVerbExample("They are containing now.", "وہ ابھی مشتمل ہونا کر رہے ہیں۔"),
                DailyVerbExample("We contained yesterday.", "ہم نے کل مشتمل ہونا کیا۔"),
                DailyVerbExample("She has contained.", "وہ مشتمل ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"contain\" with I, you, we, and they. Use \"contains\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “contain” اور “He, she, it” کے ساتھ “contains” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_205_control",
            baseForm = "control",
            thirdPerson = "controls",
            presentParticiple = "controlling",
            pastSimple = "controlled",
            pastParticiple = "controlled",
            urduMeaning = "قابو پانا",
            examples = listOf(
                DailyVerbExample("Control your anger.", "اپنے غصے پر قابو پائیں۔"),
                DailyVerbExample("He controls every day.", "وہ ہر روز قابو پانا کرتا ہے۔"),
                DailyVerbExample("They are controlling now.", "وہ ابھی قابو پانا کر رہے ہیں۔"),
                DailyVerbExample("We controlled yesterday.", "ہم نے کل قابو پانا کیا۔"),
                DailyVerbExample("She has controlled.", "وہ قابو پانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"control\" with I, you, we, and they. Use \"controls\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “control” اور “He, she, it” کے ساتھ “controls” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_206_convert",
            baseForm = "convert",
            thirdPerson = "converts",
            presentParticiple = "converting",
            pastSimple = "converted",
            pastParticiple = "converted",
            urduMeaning = "تبدیل کرنا",
            examples = listOf(
                DailyVerbExample("Convert currency.", "کرنسی تبدیل کریں۔"),
                DailyVerbExample("He converts every day.", "وہ ہر روز تبدیل کرنا کرتا ہے۔"),
                DailyVerbExample("They are converting now.", "وہ ابھی تبدیل کرنا کر رہے ہیں۔"),
                DailyVerbExample("We converted yesterday.", "ہم نے کل تبدیل کرنا کیا۔"),
                DailyVerbExample("She has converted.", "وہ تبدیل کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"convert\" with I, you, we, and they. Use \"converts\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “convert” اور “He, she, it” کے ساتھ “converts” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_207_convince",
            baseForm = "convince",
            thirdPerson = "convinces",
            presentParticiple = "convincing",
            pastSimple = "convinced",
            pastParticiple = "convinced",
            urduMeaning = "قائل کرنا",
            examples = listOf(
                DailyVerbExample("Convince the judge.", "جج کو قائل کریں۔"),
                DailyVerbExample("He convinces every day.", "وہ ہر روز قائل کرنا کرتا ہے۔"),
                DailyVerbExample("They are convincing now.", "وہ ابھی قائل کرنا کر رہے ہیں۔"),
                DailyVerbExample("We convinced yesterday.", "ہم نے کل قائل کرنا کیا۔"),
                DailyVerbExample("She has convinced.", "وہ قائل کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"convince\" with I, you, we, and they. Use \"convinces\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “convince” اور “He, she, it” کے ساتھ “convinces” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_208_copy",
            baseForm = "copy",
            thirdPerson = "copies",
            presentParticiple = "copying",
            pastSimple = "copied",
            pastParticiple = "copied",
            urduMeaning = "نقل کرنا",
            examples = listOf(
                DailyVerbExample("Copy the notes.", "نوٹس کی نقل کریں۔"),
                DailyVerbExample("He copies every day.", "وہ ہر روز نقل کرنا کرتا ہے۔"),
                DailyVerbExample("They are copying now.", "وہ ابھی نقل کرنا کر رہے ہیں۔"),
                DailyVerbExample("We copied yesterday.", "ہم نے کل نقل کرنا کیا۔"),
                DailyVerbExample("She has copied.", "وہ نقل کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"copy\" with I, you, we, and they. Use \"copies\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “copy” اور “He, she, it” کے ساتھ “copies” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_209_correct",
            baseForm = "correct",
            thirdPerson = "corrects",
            presentParticiple = "correcting",
            pastSimple = "corrected",
            pastParticiple = "corrected",
            urduMeaning = "درست کرنا",
            examples = listOf(
                DailyVerbExample("Correct the mistakes.", "غلطیاں درست کریں۔"),
                DailyVerbExample("He corrects every day.", "وہ ہر روز درست کرنا کرتا ہے۔"),
                DailyVerbExample("They are correcting now.", "وہ ابھی درست کرنا کر رہے ہیں۔"),
                DailyVerbExample("We corrected yesterday.", "ہم نے کل درست کرنا کیا۔"),
                DailyVerbExample("She has corrected.", "وہ درست کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"correct\" with I, you, we, and they. Use \"corrects\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “correct” اور “He, she, it” کے ساتھ “corrects” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_210_cover",
            baseForm = "cover",
            thirdPerson = "covers",
            presentParticiple = "covering",
            pastSimple = "covered",
            pastParticiple = "covered",
            urduMeaning = "ڈھانپنا",
            examples = listOf(
                DailyVerbExample("Cover the food.", "کھانے کو ڈھانپیں۔"),
                DailyVerbExample("He covers every day.", "وہ ہر روز ڈھانپنا کرتا ہے۔"),
                DailyVerbExample("They are covering now.", "وہ ابھی ڈھانپنا کر رہے ہیں۔"),
                DailyVerbExample("We covered yesterday.", "ہم نے کل ڈھانپنا کیا۔"),
                DailyVerbExample("She has covered.", "وہ ڈھانپنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"cover\" with I, you, we, and they. Use \"covers\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “cover” اور “He, she, it” کے ساتھ “covers” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_211_crash",
            baseForm = "crash",
            thirdPerson = "crashes",
            presentParticiple = "crashing",
            pastSimple = "crashed",
            pastParticiple = "crashed",
            urduMeaning = "ٹکرانا",
            examples = listOf(
                DailyVerbExample("Cars crash.", "گاڑیاں ٹکرا جاتی ہیں۔"),
                DailyVerbExample("He crashes every day.", "وہ ہر روز ٹکرانا کرتا ہے۔"),
                DailyVerbExample("They are crashing now.", "وہ ابھی ٹکرانا کر رہے ہیں۔"),
                DailyVerbExample("We crashed yesterday.", "ہم نے کل ٹکرانا کیا۔"),
                DailyVerbExample("She has crashed.", "وہ ٹکرانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"crash\" with I, you, we, and they. Use \"crashes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “crash” اور “He, she, it” کے ساتھ “crashes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_212_cross",
            baseForm = "cross",
            thirdPerson = "crosses",
            presentParticiple = "crossing",
            pastSimple = "crossed",
            pastParticiple = "crossed",
            urduMeaning = "پار کرنا",
            examples = listOf(
                DailyVerbExample("Cross the street.", "سڑک پار کریں۔"),
                DailyVerbExample("He crosses every day.", "وہ ہر روز پار کرنا کرتا ہے۔"),
                DailyVerbExample("They are crossing now.", "وہ ابھی پار کرنا کر رہے ہیں۔"),
                DailyVerbExample("We crossed yesterday.", "ہم نے کل پار کرنا کیا۔"),
                DailyVerbExample("She has crossed.", "وہ پار کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"cross\" with I, you, we, and they. Use \"crosses\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “cross” اور “He, she, it” کے ساتھ “crosses” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_213_damage",
            baseForm = "damage",
            thirdPerson = "damages",
            presentParticiple = "damaging",
            pastSimple = "damaged",
            pastParticiple = "damaged",
            urduMeaning = "نقصان پہنچانا",
            examples = listOf(
                DailyVerbExample("Storm damaged homes.", "طوفان نے گھروں کو نقصان پہنچایا۔"),
                DailyVerbExample("He damages every day.", "وہ ہر روز نقصان پہنچانا کرتا ہے۔"),
                DailyVerbExample("They are damaging now.", "وہ ابھی نقصان پہنچانا کر رہے ہیں۔"),
                DailyVerbExample("We damaged yesterday.", "ہم نے کل نقصان پہنچانا کیا۔"),
                DailyVerbExample("She has damaged.", "وہ نقصان پہنچانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"damage\" with I, you, we, and they. Use \"damages\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “damage” اور “He, she, it” کے ساتھ “damages” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_214_dare",
            baseForm = "dare",
            thirdPerson = "dares",
            presentParticiple = "daring",
            pastSimple = "dared",
            pastParticiple = "dared",
            urduMeaning = "ہمت کرنا",
            examples = listOf(
                DailyVerbExample("How dare you!", "آپ کی اتنی ہمت!"),
                DailyVerbExample("He dares every day.", "وہ ہر روز ہمت کرنا کرتا ہے۔"),
                DailyVerbExample("They are daring now.", "وہ ابھی ہمت کرنا کر رہے ہیں۔"),
                DailyVerbExample("We dared yesterday.", "ہم نے کل ہمت کرنا کیا۔"),
                DailyVerbExample("She has dared.", "وہ ہمت کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"dare\" with I, you, we, and they. Use \"dares\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “dare” اور “He, she, it” کے ساتھ “dares” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_215_deal",
            baseForm = "deal",
            thirdPerson = "deals",
            presentParticiple = "dealing",
            pastSimple = "dealt",
            pastParticiple = "dealt",
            urduMeaning = "معاملہ کرنا",
            examples = listOf(
                DailyVerbExample("Deal with patience.", "صبر سے معاملہ کریں۔"),
                DailyVerbExample("He deals every day.", "وہ ہر روز معاملہ کرنا کرتا ہے۔"),
                DailyVerbExample("They are dealing now.", "وہ ابھی معاملہ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We dealt yesterday.", "ہم نے کل معاملہ کرنا کیا۔"),
                DailyVerbExample("She has dealt.", "وہ معاملہ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"deal\" with I, you, we, and they. Use \"deals\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “deal” اور “He, she, it” کے ساتھ “deals” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_216_debate",
            baseForm = "debate",
            thirdPerson = "debates",
            presentParticiple = "debating",
            pastSimple = "debated",
            pastParticiple = "debated",
            urduMeaning = "بحث کرنا",
            examples = listOf(
                DailyVerbExample("Debate respectfully.", "احترام کے ساتھ بحث کریں۔"),
                DailyVerbExample("He debates every day.", "وہ ہر روز بحث کرنا کرتا ہے۔"),
                DailyVerbExample("They are debating now.", "وہ ابھی بحث کرنا کر رہے ہیں۔"),
                DailyVerbExample("We debated yesterday.", "ہم نے کل بحث کرنا کیا۔"),
                DailyVerbExample("She has debated.", "وہ بحث کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"debate\" with I, you, we, and they. Use \"debates\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “debate” اور “He, she, it” کے ساتھ “debates” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_217_declare",
            baseForm = "declare",
            thirdPerson = "declares",
            presentParticiple = "declaring",
            pastSimple = "declared",
            pastParticiple = "declared",
            urduMeaning = "اعلان کرنا",
            examples = listOf(
                DailyVerbExample("Declare results.", "نتائج کا اعلان کریں۔"),
                DailyVerbExample("He declares every day.", "وہ ہر روز اعلان کرنا کرتا ہے۔"),
                DailyVerbExample("They are declaring now.", "وہ ابھی اعلان کرنا کر رہے ہیں۔"),
                DailyVerbExample("We declared yesterday.", "ہم نے کل اعلان کرنا کیا۔"),
                DailyVerbExample("She has declared.", "وہ اعلان کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"declare\" with I, you, we, and they. Use \"declares\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “declare” اور “He, she, it” کے ساتھ “declares” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_218_decorate",
            baseForm = "decorate",
            thirdPerson = "decorates",
            presentParticiple = "decorating",
            pastSimple = "decorated",
            pastParticiple = "decorated",
            urduMeaning = "سجانا",
            examples = listOf(
                DailyVerbExample("Decorate the room.", "کمرہ سجائیں۔"),
                DailyVerbExample("He decorates every day.", "وہ ہر روز سجانا کرتا ہے۔"),
                DailyVerbExample("They are decorating now.", "وہ ابھی سجانا کر رہے ہیں۔"),
                DailyVerbExample("We decorated yesterday.", "ہم نے کل سجانا کیا۔"),
                DailyVerbExample("She has decorated.", "وہ سجانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"decorate\" with I, you, we, and they. Use \"decorates\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “decorate” اور “He, she, it” کے ساتھ “decorates” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_219_delay",
            baseForm = "delay",
            thirdPerson = "delays",
            presentParticiple = "delaying",
            pastSimple = "delayed",
            pastParticiple = "delayed",
            urduMeaning = "تاخیر کرنا",
            examples = listOf(
                DailyVerbExample("Don't delay.", "تاخیر نہ کریں۔"),
                DailyVerbExample("He delays every day.", "وہ ہر روز تاخیر کرنا کرتا ہے۔"),
                DailyVerbExample("They are delaying now.", "وہ ابھی تاخیر کرنا کر رہے ہیں۔"),
                DailyVerbExample("We delayed yesterday.", "ہم نے کل تاخیر کرنا کیا۔"),
                DailyVerbExample("She has delayed.", "وہ تاخیر کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"delay\" with I, you, we, and they. Use \"delays\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “delay” اور “He, she, it” کے ساتھ “delays” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_220_deliver",
            baseForm = "deliver",
            thirdPerson = "delivers",
            presentParticiple = "delivering",
            pastSimple = "delivered",
            pastParticiple = "delivered",
            urduMeaning = "پہنچانا",
            examples = listOf(
                DailyVerbExample("Deliver the post.", "ڈاک پہنچائیں۔"),
                DailyVerbExample("He delivers every day.", "وہ ہر روز پہنچانا کرتا ہے۔"),
                DailyVerbExample("They are delivering now.", "وہ ابھی پہنچانا کر رہے ہیں۔"),
                DailyVerbExample("We delivered yesterday.", "ہم نے کل پہنچانا کیا۔"),
                DailyVerbExample("She has delivered.", "وہ پہنچانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"deliver\" with I, you, we, and they. Use \"delivers\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “deliver” اور “He, she, it” کے ساتھ “delivers” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_221_demand",
            baseForm = "demand",
            thirdPerson = "demands",
            presentParticiple = "demanding",
            pastSimple = "demanded",
            pastParticiple = "demanded",
            urduMeaning = "مطالبہ کرنا",
            examples = listOf(
                DailyVerbExample("Demand justice.", "انصاف کا مطالبہ کریں۔"),
                DailyVerbExample("He demands every day.", "وہ ہر روز مطالبہ کرنا کرتا ہے۔"),
                DailyVerbExample("They are demanding now.", "وہ ابھی مطالبہ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We demanded yesterday.", "ہم نے کل مطالبہ کرنا کیا۔"),
                DailyVerbExample("She has demanded.", "وہ مطالبہ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"demand\" with I, you, we, and they. Use \"demands\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “demand” اور “He, she, it” کے ساتھ “demands” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_222_deny",
            baseForm = "deny",
            thirdPerson = "denies",
            presentParticiple = "denying",
            pastSimple = "denied",
            pastParticiple = "denied",
            urduMeaning = "انکار کرنا",
            examples = listOf(
                DailyVerbExample("He denied rumors.", "اس نے افواہوں سے انکار کیا۔"),
                DailyVerbExample("He denies every day.", "وہ ہر روز انکار کرنا کرتا ہے۔"),
                DailyVerbExample("They are denying now.", "وہ ابھی انکار کرنا کر رہے ہیں۔"),
                DailyVerbExample("We denied yesterday.", "ہم نے کل انکار کرنا کیا۔"),
                DailyVerbExample("She has denied.", "وہ انکار کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"deny\" with I, you, we, and they. Use \"denies\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “deny” اور “He, she, it” کے ساتھ “denies” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_223_depend",
            baseForm = "depend",
            thirdPerson = "depends",
            presentParticiple = "depending",
            pastSimple = "depende",
            pastParticiple = "depended",
            urduMeaning = "انحصار کرنا",
            examples = listOf(
                DailyVerbExample("Depend on hard work.", "محنت پر انحصار کریں۔"),
                DailyVerbExample("He depends every day.", "وہ ہر روز انحصار کرنا کرتا ہے۔"),
                DailyVerbExample("They are depending now.", "وہ ابھی انحصار کرنا کر رہے ہیں۔"),
                DailyVerbExample("We depende yesterday.", "ہم نے کل انحصار کرنا کیا۔"),
                DailyVerbExample("She has depended.", "وہ انحصار کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"depend\" with I, you, we, and they. Use \"depends\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “depend” اور “He, she, it” کے ساتھ “depends” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_224_describe",
            baseForm = "describe",
            thirdPerson = "describes",
            presentParticiple = "describing",
            pastSimple = "described",
            pastParticiple = "described",
            urduMeaning = "بیان کرنا",
            examples = listOf(
                DailyVerbExample("Describe the scene.", "منظر بیان کریں۔"),
                DailyVerbExample("He describes every day.", "وہ ہر روز بیان کرنا کرتا ہے۔"),
                DailyVerbExample("They are describing now.", "وہ ابھی بیان کرنا کر رہے ہیں۔"),
                DailyVerbExample("We described yesterday.", "ہم نے کل بیان کرنا کیا۔"),
                DailyVerbExample("She has described.", "وہ بیان کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"describe\" with I, you, we, and they. Use \"describes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “describe” اور “He, she, it” کے ساتھ “describes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_225_design",
            baseForm = "design",
            thirdPerson = "designs",
            presentParticiple = "designing",
            pastSimple = "designed",
            pastParticiple = "designed",
            urduMeaning = "ڈیزائن کرنا",
            examples = listOf(
                DailyVerbExample("Design a building.", "عمارت ڈیزائن کریں۔"),
                DailyVerbExample("He designs every day.", "وہ ہر روز ڈیزائن کرنا کرتا ہے۔"),
                DailyVerbExample("They are designing now.", "وہ ابھی ڈیزائن کرنا کر رہے ہیں۔"),
                DailyVerbExample("We designed yesterday.", "ہم نے کل ڈیزائن کرنا کیا۔"),
                DailyVerbExample("She has designed.", "وہ ڈیزائن کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"design\" with I, you, we, and they. Use \"designs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “design” اور “He, she, it” کے ساتھ “designs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_226_detect",
            baseForm = "detect",
            thirdPerson = "detects",
            presentParticiple = "detecting",
            pastSimple = "detected",
            pastParticiple = "detected",
            urduMeaning = "کھوج لگانا",
            examples = listOf(
                DailyVerbExample("Detect errors.", "غلطیوں کا کھوج لگائیں۔"),
                DailyVerbExample("He detects every day.", "وہ ہر روز کھوج لگانا کرتا ہے۔"),
                DailyVerbExample("They are detecting now.", "وہ ابھی کھوج لگانا کر رہے ہیں۔"),
                DailyVerbExample("We detected yesterday.", "ہم نے کل کھوج لگانا کیا۔"),
                DailyVerbExample("She has detected.", "وہ کھوج لگانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"detect\" with I, you, we, and they. Use \"detects\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “detect” اور “He, she, it” کے ساتھ “detects” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_227_determine",
            baseForm = "determine",
            thirdPerson = "determines",
            presentParticiple = "determining",
            pastSimple = "determined",
            pastParticiple = "determined",
            urduMeaning = "عزم کرنا",
            examples = listOf(
                DailyVerbExample("Determine the cause.", "وجہ کا عزم کریں۔"),
                DailyVerbExample("He determines every day.", "وہ ہر روز عزم کرنا کرتا ہے۔"),
                DailyVerbExample("They are determining now.", "وہ ابھی عزم کرنا کر رہے ہیں۔"),
                DailyVerbExample("We determined yesterday.", "ہم نے کل عزم کرنا کیا۔"),
                DailyVerbExample("She has determined.", "وہ عزم کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"determine\" with I, you, we, and they. Use \"determines\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “determine” اور “He, she, it” کے ساتھ “determines” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_228_dig",
            baseForm = "dig",
            thirdPerson = "digs",
            presentParticiple = "digging",
            pastSimple = "dug",
            pastParticiple = "dug",
            urduMeaning = "کھودنا",
            examples = listOf(
                DailyVerbExample("Dig a well.", "کنواں کھودیں۔"),
                DailyVerbExample("He digs every day.", "وہ ہر روز کھودنا کرتا ہے۔"),
                DailyVerbExample("They are digging now.", "وہ ابھی کھودنا کر رہے ہیں۔"),
                DailyVerbExample("We dug yesterday.", "ہم نے کل کھودنا کیا۔"),
                DailyVerbExample("She has dug.", "وہ کھودنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"dig\" with I, you, we, and they. Use \"digs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “dig” اور “He, she, it” کے ساتھ “digs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_229_discuss",
            baseForm = "discuss",
            thirdPerson = "discusses",
            presentParticiple = "discussing",
            pastSimple = "discussed",
            pastParticiple = "discussed",
            urduMeaning = "تبادلہ خیال کرنا",
            examples = listOf(
                DailyVerbExample("Discuss the plan.", "منصوبے پر تبادلہ خیال کریں۔"),
                DailyVerbExample("He discusses every day.", "وہ ہر روز تبادلہ خیال کرنا کرتا ہے۔"),
                DailyVerbExample("They are discussing now.", "وہ ابھی تبادلہ خیال کرنا کر رہے ہیں۔"),
                DailyVerbExample("We discussed yesterday.", "ہم نے کل تبادلہ خیال کرنا کیا۔"),
                DailyVerbExample("She has discussed.", "وہ تبادلہ خیال کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"discuss\" with I, you, we, and they. Use \"discusses\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “discuss” اور “He, she, it” کے ساتھ “discusses” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_230_dislike",
            baseForm = "dislike",
            thirdPerson = "dislikes",
            presentParticiple = "disliking",
            pastSimple = "disliked",
            pastParticiple = "disliked",
            urduMeaning = "ناپسند کرنا",
            examples = listOf(
                DailyVerbExample("I dislike noise.", "مجھے شور ناپسند ہے۔"),
                DailyVerbExample("He dislikes every day.", "وہ ہر روز ناپسند کرنا کرتا ہے۔"),
                DailyVerbExample("They are disliking now.", "وہ ابھی ناپسند کرنا کر رہے ہیں۔"),
                DailyVerbExample("We disliked yesterday.", "ہم نے کل ناپسند کرنا کیا۔"),
                DailyVerbExample("She has disliked.", "وہ ناپسند کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"dislike\" with I, you, we, and they. Use \"dislikes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “dislike” اور “He, she, it” کے ساتھ “dislikes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_231_divide",
            baseForm = "divide",
            thirdPerson = "divides",
            presentParticiple = "dividing",
            pastSimple = "divided",
            pastParticiple = "divided",
            urduMeaning = "تقسیم کرنا",
            examples = listOf(
                DailyVerbExample("Divide the sweets.", "مٹھائیاں تقسیم کریں۔"),
                DailyVerbExample("He divides every day.", "وہ ہر روز تقسیم کرنا کرتا ہے۔"),
                DailyVerbExample("They are dividing now.", "وہ ابھی تقسیم کرنا کر رہے ہیں۔"),
                DailyVerbExample("We divided yesterday.", "ہم نے کل تقسیم کرنا کیا۔"),
                DailyVerbExample("She has divided.", "وہ تقسیم کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"divide\" with I, you, we, and they. Use \"divides\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “divide” اور “He, she, it” کے ساتھ “divides” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_232_doubt",
            baseForm = "doubt",
            thirdPerson = "doubts",
            presentParticiple = "doubting",
            pastSimple = "doubted",
            pastParticiple = "doubted",
            urduMeaning = "شک کرنا",
            examples = listOf(
                DailyVerbExample("Don't doubt yourself.", "خود پر شک نہ کریں۔"),
                DailyVerbExample("He doubts every day.", "وہ ہر روز شک کرنا کرتا ہے۔"),
                DailyVerbExample("They are doubting now.", "وہ ابھی شک کرنا کر رہے ہیں۔"),
                DailyVerbExample("We doubted yesterday.", "ہم نے کل شک کرنا کیا۔"),
                DailyVerbExample("She has doubted.", "وہ شک کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"doubt\" with I, you, we, and they. Use \"doubts\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “doubt” اور “He, she, it” کے ساتھ “doubts” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_233_drag",
            baseForm = "drag",
            thirdPerson = "drags",
            presentParticiple = "dragging",
            pastSimple = "dragged",
            pastParticiple = "dragged",
            urduMeaning = "گھسیٹنا",
            examples = listOf(
                DailyVerbExample("Drag the heavy box.", "بھاری ڈبے کو گھسیٹیں۔"),
                DailyVerbExample("He drags every day.", "وہ ہر روز گھسیٹنا کرتا ہے۔"),
                DailyVerbExample("They are dragging now.", "وہ ابھی گھسیٹنا کر رہے ہیں۔"),
                DailyVerbExample("We dragged yesterday.", "ہم نے کل گھسیٹنا کیا۔"),
                DailyVerbExample("She has dragged.", "وہ گھسیٹنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"drag\" with I, you, we, and they. Use \"drags\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “drag” اور “He, she, it” کے ساتھ “drags” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_234_dream",
            baseForm = "dream",
            thirdPerson = "dreams",
            presentParticiple = "dreaming",
            pastSimple = "dreamt / dreamed",
            pastParticiple = "dreamt / dreamed",
            urduMeaning = "خواب دیکھنا",
            examples = listOf(
                DailyVerbExample("Dream big.", "بڑے خواب دیکھیں۔"),
                DailyVerbExample("He dreams every day.", "وہ ہر روز خواب دیکھنا کرتا ہے۔"),
                DailyVerbExample("They are dreaming now.", "وہ ابھی خواب دیکھنا کر رہے ہیں۔"),
                DailyVerbExample("We dreamt / dreamed yesterday.", "ہم نے کل خواب دیکھنا کیا۔"),
                DailyVerbExample("She has dreamt / dreamed.", "وہ خواب دیکھنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"dream\" with I, you, we, and they. Use \"dreams\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “dream” اور “He, she, it” کے ساتھ “dreams” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_235_dress",
            baseForm = "dress",
            thirdPerson = "dresses",
            presentParticiple = "dressing",
            pastSimple = "dressed",
            pastParticiple = "dressed",
            urduMeaning = "لباس پہننا",
            examples = listOf(
                DailyVerbExample("Dress neatly.", "صاف ستھرا لباس پہنیں۔"),
                DailyVerbExample("He dresses every day.", "وہ ہر روز لباس پہننا کرتا ہے۔"),
                DailyVerbExample("They are dressing now.", "وہ ابھی لباس پہننا کر رہے ہیں۔"),
                DailyVerbExample("We dressed yesterday.", "ہم نے کل لباس پہننا کیا۔"),
                DailyVerbExample("She has dressed.", "وہ لباس پہننا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"dress\" with I, you, we, and they. Use \"dresses\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “dress” اور “He, she, it” کے ساتھ “dresses” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_236_earn",
            baseForm = "earn",
            thirdPerson = "earns",
            presentParticiple = "earning",
            pastSimple = "earned",
            pastParticiple = "earned",
            urduMeaning = "کمانا",
            examples = listOf(
                DailyVerbExample("Earn a living.", "روزگار کمائیں۔"),
                DailyVerbExample("He earns every day.", "وہ ہر روز کمانا کرتا ہے۔"),
                DailyVerbExample("They are earning now.", "وہ ابھی کمانا کر رہے ہیں۔"),
                DailyVerbExample("We earned yesterday.", "ہم نے کل کمانا کیا۔"),
                DailyVerbExample("She has earned.", "وہ کمانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"earn\" with I, you, we, and they. Use \"earns\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “earn” اور “He, she, it” کے ساتھ “earns” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_237_educate",
            baseForm = "educate",
            thirdPerson = "educates",
            presentParticiple = "educating",
            pastSimple = "educated",
            pastParticiple = "educated",
            urduMeaning = "تعلیم دینا",
            examples = listOf(
                DailyVerbExample("Educate children.", "بچوں کو تعلیم دیں۔"),
                DailyVerbExample("He educates every day.", "وہ ہر روز تعلیم دینا کرتا ہے۔"),
                DailyVerbExample("They are educating now.", "وہ ابھی تعلیم دینا کر رہے ہیں۔"),
                DailyVerbExample("We educated yesterday.", "ہم نے کل تعلیم دینا کیا۔"),
                DailyVerbExample("She has educated.", "وہ تعلیم دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"educate\" with I, you, we, and they. Use \"educates\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “educate” اور “He, she, it” کے ساتھ “educates” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_238_elect",
            baseForm = "elect",
            thirdPerson = "elects",
            presentParticiple = "electing",
            pastSimple = "elected",
            pastParticiple = "elected",
            urduMeaning = "منتخب کرنا",
            examples = listOf(
                DailyVerbExample("Elect a leader.", "رہنما کا انتخاب کریں۔"),
                DailyVerbExample("He elects every day.", "وہ ہر روز منتخب کرنا کرتا ہے۔"),
                DailyVerbExample("They are electing now.", "وہ ابھی منتخب کرنا کر رہے ہیں۔"),
                DailyVerbExample("We elected yesterday.", "ہم نے کل منتخب کرنا کیا۔"),
                DailyVerbExample("She has elected.", "وہ منتخب کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"elect\" with I, you, we, and they. Use \"elects\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “elect” اور “He, she, it” کے ساتھ “elects” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_239_employ",
            baseForm = "employ",
            thirdPerson = "employs",
            presentParticiple = "employing",
            pastSimple = "employed",
            pastParticiple = "employed",
            urduMeaning = "ملازمت دینا",
            examples = listOf(
                DailyVerbExample("Company employs staff.", "کمپنی عملے کو ملازمت دیتی ہے۔"),
                DailyVerbExample("He employs every day.", "وہ ہر روز ملازمت دینا کرتا ہے۔"),
                DailyVerbExample("They are employing now.", "وہ ابھی ملازمت دینا کر رہے ہیں۔"),
                DailyVerbExample("We employed yesterday.", "ہم نے کل ملازمت دینا کیا۔"),
                DailyVerbExample("She has employed.", "وہ ملازمت دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"employ\" with I, you, we, and they. Use \"employs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “employ” اور “He, she, it” کے ساتھ “employs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_240_encourage",
            baseForm = "encourage",
            thirdPerson = "encourages",
            presentParticiple = "encouraging",
            pastSimple = "encouraged",
            pastParticiple = "encouraged",
            urduMeaning = "حوصلہ افزائی کرنا",
            examples = listOf(
                DailyVerbExample("Encourage others.", "دوسروں کی حوصلہ افزائی کریں۔"),
                DailyVerbExample("He encourages every day.", "وہ ہر روز حوصلہ افزائی کرنا کرتا ہے۔"),
                DailyVerbExample("They are encouraging now.", "وہ ابھی حوصلہ افزائی کرنا کر رہے ہیں۔"),
                DailyVerbExample("We encouraged yesterday.", "ہم نے کل حوصلہ افزائی کرنا کیا۔"),
                DailyVerbExample("She has encouraged.", "وہ حوصلہ افزائی کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"encourage\" with I, you, we, and they. Use \"encourages\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “encourage” اور “He, she, it” کے ساتھ “encourages” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_241_enter",
            baseForm = "enter",
            thirdPerson = "enters",
            presentParticiple = "entering",
            pastSimple = "entered",
            pastParticiple = "entered",
            urduMeaning = "داخل ہونا",
            examples = listOf(
                DailyVerbExample("Enter the hall.", "ہال میں داخل ہوں۔"),
                DailyVerbExample("He enters every day.", "وہ ہر روز داخل ہونا کرتا ہے۔"),
                DailyVerbExample("They are entering now.", "وہ ابھی داخل ہونا کر رہے ہیں۔"),
                DailyVerbExample("We entered yesterday.", "ہم نے کل داخل ہونا کیا۔"),
                DailyVerbExample("She has entered.", "وہ داخل ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"enter\" with I, you, we, and they. Use \"enters\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “enter” اور “He, she, it” کے ساتھ “enters” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_242_escape",
            baseForm = "escape",
            thirdPerson = "escapes",
            presentParticiple = "escaping",
            pastSimple = "escaped",
            pastParticiple = "escaped",
            urduMeaning = "بچ نکلنا",
            examples = listOf(
                DailyVerbExample("Escape danger.", "خطرناک صورتحال سے بچ نکلیں۔"),
                DailyVerbExample("He escapes every day.", "وہ ہر روز بچ نکلنا کرتا ہے۔"),
                DailyVerbExample("They are escaping now.", "وہ ابھی بچ نکلنا کر رہے ہیں۔"),
                DailyVerbExample("We escaped yesterday.", "ہم نے کل بچ نکلنا کیا۔"),
                DailyVerbExample("She has escaped.", "وہ بچ نکلنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"escape\" with I, you, we, and they. Use \"escapes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “escape” اور “He, she, it” کے ساتھ “escapes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_243_examine",
            baseForm = "examine",
            thirdPerson = "examines",
            presentParticiple = "examining",
            pastSimple = "examined",
            pastParticiple = "examined",
            urduMeaning = "معائنہ کرنا",
            examples = listOf(
                DailyVerbExample("Doctor examined patient.", "ڈاکٹر نے مریض کا معائنہ کیا۔"),
                DailyVerbExample("He examines every day.", "وہ ہر روز معائنہ کرنا کرتا ہے۔"),
                DailyVerbExample("They are examining now.", "وہ ابھی معائنہ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We examined yesterday.", "ہم نے کل معائنہ کرنا کیا۔"),
                DailyVerbExample("She has examined.", "وہ معائنہ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"examine\" with I, you, we, and they. Use \"examines\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “examine” اور “He, she, it” کے ساتھ “examines” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_244_excuse",
            baseForm = "excuse",
            thirdPerson = "excuses",
            presentParticiple = "excusing",
            pastSimple = "excused",
            pastParticiple = "excused",
            urduMeaning = "معاف کرنا",
            examples = listOf(
                DailyVerbExample("Excuse my mistake.", "میری غلطی معاف کریں۔"),
                DailyVerbExample("He excuses every day.", "وہ ہر روز معاف کرنا کرتا ہے۔"),
                DailyVerbExample("They are excusing now.", "وہ ابھی معاف کرنا کر رہے ہیں۔"),
                DailyVerbExample("We excused yesterday.", "ہم نے کل معاف کرنا کیا۔"),
                DailyVerbExample("She has excused.", "وہ معاف کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"excuse\" with I, you, we, and they. Use \"excuses\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “excuse” اور “He, she, it” کے ساتھ “excuses” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_245_exist",
            baseForm = "exist",
            thirdPerson = "exists",
            presentParticiple = "existing",
            pastSimple = "existed",
            pastParticiple = "existed",
            urduMeaning = "موجود ہونا",
            examples = listOf(
                DailyVerbExample("Stars exist in space.", "ستارے خلا میں موجود ہیں۔"),
                DailyVerbExample("He exists every day.", "وہ ہر روز موجود ہونا کرتا ہے۔"),
                DailyVerbExample("They are existing now.", "وہ ابھی موجود ہونا کر رہے ہیں۔"),
                DailyVerbExample("We existed yesterday.", "ہم نے کل موجود ہونا کیا۔"),
                DailyVerbExample("She has existed.", "وہ موجود ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"exist\" with I, you, we, and they. Use \"exists\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “exist” اور “He, she, it” کے ساتھ “exists” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_246_expand",
            baseForm = "expand",
            thirdPerson = "expands",
            presentParticiple = "expanding",
            pastSimple = "expanded",
            pastParticiple = "expanded",
            urduMeaning = "پھیلنا",
            examples = listOf(
                DailyVerbExample("Metals expand in heat.", "دھاتیں گرمی میں پھیلتی ہیں۔"),
                DailyVerbExample("He expands every day.", "وہ ہر روز پھیلنا کرتا ہے۔"),
                DailyVerbExample("They are expanding now.", "وہ ابھی پھیلنا کر رہے ہیں۔"),
                DailyVerbExample("We expanded yesterday.", "ہم نے کل پھیلنا کیا۔"),
                DailyVerbExample("She has expanded.", "وہ پھیلنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"expand\" with I, you, we, and they. Use \"expands\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “expand” اور “He, she, it” کے ساتھ “expands” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_247_experience",
            baseForm = "experience",
            thirdPerson = "experiences",
            presentParticiple = "experiencing",
            pastSimple = "experienced",
            pastParticiple = "experienced",
            urduMeaning = "تجرہ حاصل کرنا",
            examples = listOf(
                DailyVerbExample("Gain experience.", "تجرہ حاصل کریں۔"),
                DailyVerbExample("He experiences every day.", "وہ ہر روز تجرہ حاصل کرنا کرتا ہے۔"),
                DailyVerbExample("They are experiencing now.", "وہ ابھی تجرہ حاصل کرنا کر رہے ہیں۔"),
                DailyVerbExample("We experienced yesterday.", "ہم نے کل تجرہ حاصل کرنا کیا۔"),
                DailyVerbExample("She has experienced.", "وہ تجرہ حاصل کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"experience\" with I, you, we, and they. Use \"experiences\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “experience” اور “He, she, it” کے ساتھ “experiences” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_248_explore",
            baseForm = "explore",
            thirdPerson = "explores",
            presentParticiple = "exploring",
            pastSimple = "explored",
            pastParticiple = "explored",
            urduMeaning = "دریافت کرنا",
            examples = listOf(
                DailyVerbExample("Explore new places.", "نئی جگہیں دریافت کریں۔"),
                DailyVerbExample("He explores every day.", "وہ ہر روز دریافت کرنا کرتا ہے۔"),
                DailyVerbExample("They are exploring now.", "وہ ابھی دریافت کرنا کر رہے ہیں۔"),
                DailyVerbExample("We explored yesterday.", "ہم نے کل دریافت کرنا کیا۔"),
                DailyVerbExample("She has explored.", "وہ دریافت کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"explore\" with I, you, we, and they. Use \"explores\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “explore” اور “He, she, it” کے ساتھ “explores” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_249_express",
            baseForm = "express",
            thirdPerson = "expresses",
            presentParticiple = "expressing",
            pastSimple = "expressed",
            pastParticiple = "expressed",
            urduMeaning = "اظہار کرنا",
            examples = listOf(
                DailyVerbExample("Express your thoughts.", "اپنے خیالات کا اظہار کریں۔"),
                DailyVerbExample("He expresses every day.", "وہ ہر روز اظہار کرنا کرتا ہے۔"),
                DailyVerbExample("They are expressing now.", "وہ ابھی اظہار کرنا کر رہے ہیں۔"),
                DailyVerbExample("We expressed yesterday.", "ہم نے کل اظہار کرنا کیا۔"),
                DailyVerbExample("She has expressed.", "وہ اظہار کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"express\" with I, you, we, and they. Use \"expresses\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “express” اور “He, she, it” کے ساتھ “expresses” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_250_extend",
            baseForm = "extend",
            thirdPerson = "extends",
            presentParticiple = "extending",
            pastSimple = "extended",
            pastParticiple = "extended",
            urduMeaning = "توسیع کرنا",
            examples = listOf(
                DailyVerbExample("Extend deadline.", "مدت میں توسیع کریں۔"),
                DailyVerbExample("He extends every day.", "وہ ہر روز توسیع کرنا کرتا ہے۔"),
                DailyVerbExample("They are extending now.", "وہ ابھی توسیع کرنا کر رہے ہیں۔"),
                DailyVerbExample("We extended yesterday.", "ہم نے کل توسیع کرنا کیا۔"),
                DailyVerbExample("She has extended.", "وہ توسیع کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"extend\" with I, you, we, and they. Use \"extends\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “extend” اور “He, she, it” کے ساتھ “extends” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_251_face",
            baseForm = "face",
            thirdPerson = "faces",
            presentParticiple = "facing",
            pastSimple = "faced",
            pastParticiple = "faced",
            urduMeaning = "سامنا کرنا",
            examples = listOf(
                DailyVerbExample("Face challenges.", "چیلنجز کا سامنا کریں۔"),
                DailyVerbExample("He faces every day.", "وہ ہر روز سامنا کرنا کرتا ہے۔"),
                DailyVerbExample("They are facing now.", "وہ ابھی سامنا کرنا کر رہے ہیں۔"),
                DailyVerbExample("We faced yesterday.", "ہم نے کل سامنا کرنا کیا۔"),
                DailyVerbExample("She has faced.", "وہ سامنا کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"face\" with I, you, we, and they. Use \"faces\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “face” اور “He, she, it” کے ساتھ “faces” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_252_fail",
            baseForm = "fail",
            thirdPerson = "fails",
            presentParticiple = "failing",
            pastSimple = "failed",
            pastParticiple = "failed",
            urduMeaning = "ناکام ہونا",
            examples = listOf(
                DailyVerbExample("Never fear to fail.", "ناکام ہونے سے کبھی مت ڈریں۔"),
                DailyVerbExample("He fails every day.", "وہ ہر روز ناکام ہونا کرتا ہے۔"),
                DailyVerbExample("They are failing now.", "وہ ابھی ناکام ہونا کر رہے ہیں۔"),
                DailyVerbExample("We failed yesterday.", "ہم نے کل ناکام ہونا کیا۔"),
                DailyVerbExample("She has failed.", "وہ ناکام ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"fail\" with I, you, we, and they. Use \"fails\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “fail” اور “He, she, it” کے ساتھ “fails” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_253_fear",
            baseForm = "fear",
            thirdPerson = "fears",
            presentParticiple = "fearing",
            pastSimple = "feared",
            pastParticiple = "feared",
            urduMeaning = "ڈرنا",
            examples = listOf(
                DailyVerbExample("Fear nothing.", "کسی چیز سے نہ ڈریں۔"),
                DailyVerbExample("He fears every day.", "وہ ہر روز ڈرنا کرتا ہے۔"),
                DailyVerbExample("They are fearing now.", "وہ ابھی ڈرنا کر رہے ہیں۔"),
                DailyVerbExample("We feared yesterday.", "ہم نے کل ڈرنا کیا۔"),
                DailyVerbExample("She has feared.", "وہ ڈرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"fear\" with I, you, we, and they. Use \"fears\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “fear” اور “He, she, it” کے ساتھ “fears” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_254_feed",
            baseForm = "feed",
            thirdPerson = "feeds",
            presentParticiple = "feeding",
            pastSimple = "fed",
            pastParticiple = "fed",
            urduMeaning = "کھلانا",
            examples = listOf(
                DailyVerbExample("Feed the hungry.", "بھوکوں کو کھانا کھلائیں۔"),
                DailyVerbExample("He feeds every day.", "وہ ہر روز کھلانا کرتا ہے۔"),
                DailyVerbExample("They are feeding now.", "وہ ابھی کھلانا کر رہے ہیں۔"),
                DailyVerbExample("We fed yesterday.", "ہم نے کل کھلانا کیا۔"),
                DailyVerbExample("She has fed.", "وہ کھلانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"feed\" with I, you, we, and they. Use \"feeds\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “feed” اور “He, she, it” کے ساتھ “feeds” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_255_finish",
            baseForm = "finish",
            thirdPerson = "finishes",
            presentParticiple = "finishing",
            pastSimple = "finished",
            pastParticiple = "finished",
            urduMeaning = "ختم کرنا",
            examples = listOf(
                DailyVerbExample("Finish your food.", "اپنا کھانا ختم کریں۔"),
                DailyVerbExample("He finishes every day.", "وہ ہر روز ختم کرنا کرتا ہے۔"),
                DailyVerbExample("They are finishing now.", "وہ ابھی ختم کرنا کر رہے ہیں۔"),
                DailyVerbExample("We finished yesterday.", "ہم نے کل ختم کرنا کیا۔"),
                DailyVerbExample("She has finished.", "وہ ختم کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"finish\" with I, you, we, and they. Use \"finishes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “finish” اور “He, she, it” کے ساتھ “finishes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_256_fit",
            baseForm = "fit",
            thirdPerson = "fits",
            presentParticiple = "fitting",
            pastSimple = "fit",
            pastParticiple = "fit",
            urduMeaning = "پورا آنا",
            examples = listOf(
                DailyVerbExample("Shoes fit well.", "جوتے بالکل پورے آ رہے ہیں۔"),
                DailyVerbExample("He fits every day.", "وہ ہر روز پورا آنا کرتا ہے۔"),
                DailyVerbExample("They are fitting now.", "وہ ابھی پورا آنا کر رہے ہیں۔"),
                DailyVerbExample("We fit yesterday.", "ہم نے کل پورا آنا کیا۔"),
                DailyVerbExample("She has fit.", "وہ پورا آنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"fit\" with I, you, we, and they. Use \"fits\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “fit” اور “He, she, it” کے ساتھ “fits” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_257_fold",
            baseForm = "fold",
            thirdPerson = "folds",
            presentParticiple = "folding",
            pastSimple = "folded",
            pastParticiple = "folded",
            urduMeaning = "تہہ کرنا",
            examples = listOf(
                DailyVerbExample("Fold the clothes.", "کپڑوں کو تہہ کریں۔"),
                DailyVerbExample("He folds every day.", "وہ ہر روز تہہ کرنا کرتا ہے۔"),
                DailyVerbExample("They are folding now.", "وہ ابھی تہہ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We folded yesterday.", "ہم نے کل تہہ کرنا کیا۔"),
                DailyVerbExample("She has folded.", "وہ تہہ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"fold\" with I, you, we, and they. Use \"folds\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “fold” اور “He, she, it” کے ساتھ “folds” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_258_forgive",
            baseForm = "forgive",
            thirdPerson = "forgives",
            presentParticiple = "forgiving",
            pastSimple = "forgave",
            pastParticiple = "forgiven",
            urduMeaning = "معاف کرنا",
            examples = listOf(
                DailyVerbExample("Forgive others.", "دوسروں کو معاف کریں۔"),
                DailyVerbExample("He forgives every day.", "وہ ہر روز معاف کرنا کرتا ہے۔"),
                DailyVerbExample("They are forgiving now.", "وہ ابھی معاف کرنا کر رہے ہیں۔"),
                DailyVerbExample("We forgave yesterday.", "ہم نے کل معاف کرنا کیا۔"),
                DailyVerbExample("She has forgiven.", "وہ معاف کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"forgive\" with I, you, we, and they. Use \"forgives\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “forgive” اور “He, she, it” کے ساتھ “forgives” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_259_frighten",
            baseForm = "frighten",
            thirdPerson = "frightens",
            presentParticiple = "frightening",
            pastSimple = "frightened",
            pastParticiple = "frightened",
            urduMeaning = "ڈرانا",
            examples = listOf(
                DailyVerbExample("Loud sound frightened him.", "تیز آواز نے اسے ڈرا دیا۔"),
                DailyVerbExample("He frightens every day.", "وہ ہر روز ڈرانا کرتا ہے۔"),
                DailyVerbExample("They are frightening now.", "وہ ابھی ڈرانا کر رہے ہیں۔"),
                DailyVerbExample("We frightened yesterday.", "ہم نے کل ڈرانا کیا۔"),
                DailyVerbExample("She has frightened.", "وہ ڈرانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"frighten\" with I, you, we, and they. Use \"frightens\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “frighten” اور “He, she, it” کے ساتھ “frightens” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_260_gather",
            baseForm = "gather",
            thirdPerson = "gathers",
            presentParticiple = "gathering",
            pastSimple = "gathered",
            pastParticiple = "gathered",
            urduMeaning = "اکٹھا کرنا",
            examples = listOf(
                DailyVerbExample("Gather information.", "معلومات اکٹھی کریں۔"),
                DailyVerbExample("He gathers every day.", "وہ ہر روز اکٹھا کرنا کرتا ہے۔"),
                DailyVerbExample("They are gathering now.", "وہ ابھی اکٹھا کرنا کر رہے ہیں۔"),
                DailyVerbExample("We gathered yesterday.", "ہم نے کل اکٹھا کرنا کیا۔"),
                DailyVerbExample("She has gathered.", "وہ اکٹھا کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"gather\" with I, you, we, and they. Use \"gathers\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “gather” اور “He, she, it” کے ساتھ “gathers” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_261_gaze",
            baseForm = "gaze",
            thirdPerson = "gazes",
            presentParticiple = "gazing",
            pastSimple = "gazed",
            pastParticiple = "gazed",
            urduMeaning = "گھورنا",
            examples = listOf(
                DailyVerbExample("Gaze at stars.", "ستاروں کو غور سے دیکھیں۔"),
                DailyVerbExample("He gazes every day.", "وہ ہر روز گھورنا کرتا ہے۔"),
                DailyVerbExample("They are gazing now.", "وہ ابھی گھورنا کر رہے ہیں۔"),
                DailyVerbExample("We gazed yesterday.", "ہم نے کل گھورنا کیا۔"),
                DailyVerbExample("She has gazed.", "وہ گھورنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"gaze\" with I, you, we, and they. Use \"gazes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “gaze” اور “He, she, it” کے ساتھ “gazes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_262_govern",
            baseForm = "govern",
            thirdPerson = "governs",
            presentParticiple = "governing",
            pastSimple = "governed",
            pastParticiple = "governed",
            urduMeaning = "حکومت کرنا",
            examples = listOf(
                DailyVerbExample("Govern wisely.", "عقلمندی سے حکومت کریں۔"),
                DailyVerbExample("He governs every day.", "وہ ہر روز حکومت کرنا کرتا ہے۔"),
                DailyVerbExample("They are governing now.", "وہ ابھی حکومت کرنا کر رہے ہیں۔"),
                DailyVerbExample("We governed yesterday.", "ہم نے کل حکومت کرنا کیا۔"),
                DailyVerbExample("She has governed.", "وہ حکومت کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"govern\" with I, you, we, and they. Use \"governs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “govern” اور “He, she, it” کے ساتھ “governs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_263_grab",
            baseForm = "grab",
            thirdPerson = "grabs",
            presentParticiple = "grabbing",
            pastSimple = "grabbed",
            pastParticiple = "grabbed",
            urduMeaning = "لپکنا",
            examples = listOf(
                DailyVerbExample("Grab the chance.", "موقع پر لپکیں۔"),
                DailyVerbExample("He grabs every day.", "وہ ہر روز لپکنا کرتا ہے۔"),
                DailyVerbExample("They are grabbing now.", "وہ ابھی لپکنا کر رہے ہیں۔"),
                DailyVerbExample("We grabbed yesterday.", "ہم نے کل لپکنا کیا۔"),
                DailyVerbExample("She has grabbed.", "وہ لپکنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"grab\" with I, you, we, and they. Use \"grabs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “grab” اور “He, she, it” کے ساتھ “grabs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_264_grant",
            baseForm = "grant",
            thirdPerson = "grants",
            presentParticiple = "granting",
            pastSimple = "granted",
            pastParticiple = "granted",
            urduMeaning = "عطا کرنا",
            examples = listOf(
                DailyVerbExample("Grant permission.", "اجازت عطا کریں۔"),
                DailyVerbExample("He grants every day.", "وہ ہر روز عطا کرنا کرتا ہے۔"),
                DailyVerbExample("They are granting now.", "وہ ابھی عطا کرنا کر رہے ہیں۔"),
                DailyVerbExample("We granted yesterday.", "ہم نے کل عطا کرنا کیا۔"),
                DailyVerbExample("She has granted.", "وہ عطا کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"grant\" with I, you, we, and they. Use \"grants\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “grant” اور “He, she, it” کے ساتھ “grants” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_265_greet",
            baseForm = "greet",
            thirdPerson = "greets",
            presentParticiple = "greeting",
            pastSimple = "greeted",
            pastParticiple = "greeted",
            urduMeaning = "سلام کرنا",
            examples = listOf(
                DailyVerbExample("Greet everyone.", "سب کو سلام کریں۔"),
                DailyVerbExample("He greets every day.", "وہ ہر روز سلام کرنا کرتا ہے۔"),
                DailyVerbExample("They are greeting now.", "وہ ابھی سلام کرنا کر رہے ہیں۔"),
                DailyVerbExample("We greeted yesterday.", "ہم نے کل سلام کرنا کیا۔"),
                DailyVerbExample("She has greeted.", "وہ سلام کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"greet\" with I, you, we, and they. Use \"greets\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “greet” اور “He, she, it” کے ساتھ “greets” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_266_guard",
            baseForm = "guard",
            thirdPerson = "guards",
            presentParticiple = "guarding",
            pastSimple = "guarded",
            pastParticiple = "guarded",
            urduMeaning = "حفاظت کرنا",
            examples = listOf(
                DailyVerbExample("Guard the gate.", "گیٹ کی حفاظت کریں۔"),
                DailyVerbExample("He guards every day.", "وہ ہر روز حفاظت کرنا کرتا ہے۔"),
                DailyVerbExample("They are guarding now.", "وہ ابھی حفاظت کرنا کر رہے ہیں۔"),
                DailyVerbExample("We guarded yesterday.", "ہم نے کل حفاظت کرنا کیا۔"),
                DailyVerbExample("She has guarded.", "وہ حفاظت کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"guard\" with I, you, we, and they. Use \"guards\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “guard” اور “He, she, it” کے ساتھ “guards” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_267_guess",
            baseForm = "guess",
            thirdPerson = "guesses",
            presentParticiple = "guessing",
            pastSimple = "guessed",
            pastParticiple = "guessed",
            urduMeaning = "اندازہ لگانا",
            examples = listOf(
                DailyVerbExample("Guess the number.", "نمبر کا اندازہ لگائیں۔"),
                DailyVerbExample("He guesses every day.", "وہ ہر روز اندازہ لگانا کرتا ہے۔"),
                DailyVerbExample("They are guessing now.", "وہ ابھی اندازہ لگانا کر رہے ہیں۔"),
                DailyVerbExample("We guessed yesterday.", "ہم نے کل اندازہ لگانا کیا۔"),
                DailyVerbExample("She has guessed.", "وہ اندازہ لگانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"guess\" with I, you, we, and they. Use \"guesses\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “guess” اور “He, she, it” کے ساتھ “guesses” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_268_guide",
            baseForm = "guide",
            thirdPerson = "guides",
            presentParticiple = "guiding",
            pastSimple = "guided",
            pastParticiple = "guided",
            urduMeaning = "رہنمائی کرنا",
            examples = listOf(
                DailyVerbExample("Guide the visitors.", "سیاحوں کی رہنمائی کریں۔"),
                DailyVerbExample("He guides every day.", "وہ ہر روز رہنمائی کرنا کرتا ہے۔"),
                DailyVerbExample("They are guiding now.", "وہ ابھی رہنمائی کرنا کر رہے ہیں۔"),
                DailyVerbExample("We guided yesterday.", "ہم نے کل رہنمائی کرنا کیا۔"),
                DailyVerbExample("She has guided.", "وہ رہنمائی کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"guide\" with I, you, we, and they. Use \"guides\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “guide” اور “He, she, it” کے ساتھ “guides” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_269_handle",
            baseForm = "handle",
            thirdPerson = "handles",
            presentParticiple = "handling",
            pastSimple = "handled",
            pastParticiple = "handled",
            urduMeaning = "سنبھالنا",
            examples = listOf(
                DailyVerbExample("Handle with care.", "احتیاط سے سنبھالیں۔"),
                DailyVerbExample("He handles every day.", "وہ ہر روز سنبھالنا کرتا ہے۔"),
                DailyVerbExample("They are handling now.", "وہ ابھی سنبھالنا کر رہے ہیں۔"),
                DailyVerbExample("We handled yesterday.", "ہم نے کل سنبھالنا کیا۔"),
                DailyVerbExample("She has handled.", "وہ سنبھالنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"handle\" with I, you, we, and they. Use \"handles\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “handle” اور “He, she, it” کے ساتھ “handles” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_270_hang",
            baseForm = "hang",
            thirdPerson = "hangs",
            presentParticiple = "hanging",
            pastSimple = "hung",
            pastParticiple = "hung",
            urduMeaning = "لٹکانا",
            examples = listOf(
                DailyVerbExample("Hang the coat.", "کوٹ لٹکائیں۔"),
                DailyVerbExample("He hangs every day.", "وہ ہر روز لٹکانا کرتا ہے۔"),
                DailyVerbExample("They are hanging now.", "وہ ابھی لٹکانا کر رہے ہیں۔"),
                DailyVerbExample("We hung yesterday.", "ہم نے کل لٹکانا کیا۔"),
                DailyVerbExample("She has hung.", "وہ لٹکانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"hang\" with I, you, we, and they. Use \"hangs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “hang” اور “He, she, it” کے ساتھ “hangs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_271_hate",
            baseForm = "hate",
            thirdPerson = "hates",
            presentParticiple = "hating",
            pastSimple = "hated",
            pastParticiple = "hated",
            urduMeaning = "نفرت کرنا",
            examples = listOf(
                DailyVerbExample("Hate evil deeds.", "برے کاموں سے نفرت کریں۔"),
                DailyVerbExample("He hates every day.", "وہ ہر روز نفرت کرنا کرتا ہے۔"),
                DailyVerbExample("They are hating now.", "وہ ابھی نفرت کرنا کر رہے ہیں۔"),
                DailyVerbExample("We hated yesterday.", "ہم نے کل نفرت کرنا کیا۔"),
                DailyVerbExample("She has hated.", "وہ نفرت کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"hate\" with I, you, we, and they. Use \"hates\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “hate” اور “He, she, it” کے ساتھ “hates” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_272_heal",
            baseForm = "heal",
            thirdPerson = "heals",
            presentParticiple = "healing",
            pastSimple = "healed",
            pastParticiple = "healed",
            urduMeaning = "شفاء پانا",
            examples = listOf(
                DailyVerbExample("Time heals wounds.", "وقت زخموں کو بھر دیتا ہے۔"),
                DailyVerbExample("He heals every day.", "وہ ہر روز شفاء پانا کرتا ہے۔"),
                DailyVerbExample("They are healing now.", "وہ ابھی شفاء پانا کر رہے ہیں۔"),
                DailyVerbExample("We healed yesterday.", "ہم نے کل شفاء پانا کیا۔"),
                DailyVerbExample("She has healed.", "وہ شفاء پانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"heal\" with I, you, we, and they. Use \"heals\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “heal” اور “He, she, it” کے ساتھ “heals” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_273_hide",
            baseForm = "hide",
            thirdPerson = "hides",
            presentParticiple = "hiding",
            pastSimple = "hid",
            pastParticiple = "hidden",
            urduMeaning = "چھپانا",
            examples = listOf(
                DailyVerbExample("Hide the treasure.", "خزانہ چھپائیں۔"),
                DailyVerbExample("He hides every day.", "وہ ہر روز چھپانا کرتا ہے۔"),
                DailyVerbExample("They are hiding now.", "وہ ابھی چھپانا کر رہے ہیں۔"),
                DailyVerbExample("We hid yesterday.", "ہم نے کل چھپانا کیا۔"),
                DailyVerbExample("She has hidden.", "وہ چھپانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"hide\" with I, you, we, and they. Use \"hides\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “hide” اور “He, she, it” کے ساتھ “hides” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_274_hire",
            baseForm = "hire",
            thirdPerson = "hires",
            presentParticiple = "hiring",
            pastSimple = "hired",
            pastParticiple = "hired",
            urduMeaning = "کرائے پر لینا",
            examples = listOf(
                DailyVerbExample("Hire a taxi.", "ٹیکسی کرائے پر لیں۔"),
                DailyVerbExample("He hires every day.", "وہ ہر روز کرائے پر لینا کرتا ہے۔"),
                DailyVerbExample("They are hiring now.", "وہ ابھی کرائے پر لینا کر رہے ہیں۔"),
                DailyVerbExample("We hired yesterday.", "ہم نے کل کرائے پر لینا کیا۔"),
                DailyVerbExample("She has hired.", "وہ کرائے پر لینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"hire\" with I, you, we, and they. Use \"hires\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “hire” اور “He, she, it” کے ساتھ “hires” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_275_hit",
            baseForm = "hit",
            thirdPerson = "hits",
            presentParticiple = "hitting",
            pastSimple = "hit",
            pastParticiple = "hit",
            urduMeaning = "مارنا",
            examples = listOf(
                DailyVerbExample("Hit the ball.", "گیند کو ماریں۔"),
                DailyVerbExample("He hits every day.", "وہ ہر روز مارنا کرتا ہے۔"),
                DailyVerbExample("They are hitting now.", "وہ ابھی مارنا کر رہے ہیں۔"),
                DailyVerbExample("We hit yesterday.", "ہم نے کل مارنا کیا۔"),
                DailyVerbExample("She has hit.", "وہ مارنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"hit\" with I, you, we, and they. Use \"hits\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “hit” اور “He, she, it” کے ساتھ “hits” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_276_hug",
            baseForm = "hug",
            thirdPerson = "hugs",
            presentParticiple = "hugging",
            pastSimple = "hugged",
            pastParticiple = "hugged",
            urduMeaning = "گلے لگانا",
            examples = listOf(
                DailyVerbExample("Hug your brother.", "اپنے بھائی کو گلے لگائیں۔"),
                DailyVerbExample("He hugs every day.", "وہ ہر روز گلے لگانا کرتا ہے۔"),
                DailyVerbExample("They are hugging now.", "وہ ابھی گلے لگانا کر رہے ہیں۔"),
                DailyVerbExample("We hugged yesterday.", "ہم نے کل گلے لگانا کیا۔"),
                DailyVerbExample("She has hugged.", "وہ گلے لگانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"hug\" with I, you, we, and they. Use \"hugs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “hug” اور “He, she, it” کے ساتھ “hugs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_277_hunt",
            baseForm = "hunt",
            thirdPerson = "hunts",
            presentParticiple = "hunting",
            pastSimple = "hunted",
            pastParticiple = "hunted",
            urduMeaning = "شکار کرنا",
            examples = listOf(
                DailyVerbExample("Lions hunt deer.", "شیر ہرن کا شکار کرتے ہیں۔"),
                DailyVerbExample("He hunts every day.", "وہ ہر روز شکار کرنا کرتا ہے۔"),
                DailyVerbExample("They are hunting now.", "وہ ابھی شکار کرنا کر رہے ہیں۔"),
                DailyVerbExample("We hunted yesterday.", "ہم نے کل شکار کرنا کیا۔"),
                DailyVerbExample("She has hunted.", "وہ شکار کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"hunt\" with I, you, we, and they. Use \"hunts\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “hunt” اور “He, she, it” کے ساتھ “hunts” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_278_hurry",
            baseForm = "hurry",
            thirdPerson = "hurries",
            presentParticiple = "hurrying",
            pastSimple = "hurried",
            pastParticiple = "hurried",
            urduMeaning = "جلدی کرنا",
            examples = listOf(
                DailyVerbExample("Hurry up!", "جلدی کریں!"),
                DailyVerbExample("He hurries every day.", "وہ ہر روز جلدی کرنا کرتا ہے۔"),
                DailyVerbExample("They are hurrying now.", "وہ ابھی جلدی کرنا کر رہے ہیں۔"),
                DailyVerbExample("We hurried yesterday.", "ہم نے کل جلدی کرنا کیا۔"),
                DailyVerbExample("She has hurried.", "وہ جلدی کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"hurry\" with I, you, we, and they. Use \"hurries\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “hurry” اور “He, she, it” کے ساتھ “hurries” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_279_hurt",
            baseForm = "hurt",
            thirdPerson = "hurts",
            presentParticiple = "hurting",
            pastSimple = "hurt",
            pastParticiple = "hurt",
            urduMeaning = "چوٹ پہنچانا",
            examples = listOf(
                DailyVerbExample("Don't hurt feelings.", "احساسات کو چوٹ نہ پہنچائیں۔"),
                DailyVerbExample("He hurts every day.", "وہ ہر روز چوٹ پہنچانا کرتا ہے۔"),
                DailyVerbExample("They are hurting now.", "وہ ابھی چوٹ پہنچانا کر رہے ہیں۔"),
                DailyVerbExample("We hurt yesterday.", "ہم نے کل چوٹ پہنچانا کیا۔"),
                DailyVerbExample("She has hurt.", "وہ چوٹ پہنچانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"hurt\" with I, you, we, and they. Use \"hurts\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “hurt” اور “He, she, it” کے ساتھ “hurts” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_280_identify",
            baseForm = "identify",
            thirdPerson = "identifies",
            presentParticiple = "identifying",
            pastSimple = "identified",
            pastParticiple = "identified",
            urduMeaning = "شناخت کرنا",
            examples = listOf(
                DailyVerbExample("Identify the cause.", "وجہ کی شناخت کریں۔"),
                DailyVerbExample("He identifies every day.", "وہ ہر روز شناخت کرنا کرتا ہے۔"),
                DailyVerbExample("They are identifying now.", "وہ ابھی شناخت کرنا کر رہے ہیں۔"),
                DailyVerbExample("We identified yesterday.", "ہم نے کل شناخت کرنا کیا۔"),
                DailyVerbExample("She has identified.", "وہ شناخت کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"identify\" with I, you, we, and they. Use \"identifies\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “identify” اور “He, she, it” کے ساتھ “identifies” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_281_ignore",
            baseForm = "ignore",
            thirdPerson = "ignores",
            presentParticiple = "ignoring",
            pastSimple = "ignored",
            pastParticiple = "ignored",
            urduMeaning = "نظر انداز کرنا",
            examples = listOf(
                DailyVerbExample("Ignore bad talk.", "بری باتوں کو نظر انداز کریں۔"),
                DailyVerbExample("He ignores every day.", "وہ ہر روز نظر انداز کرنا کرتا ہے۔"),
                DailyVerbExample("They are ignoring now.", "وہ ابھی نظر انداز کرنا کر رہے ہیں۔"),
                DailyVerbExample("We ignored yesterday.", "ہم نے کل نظر انداز کرنا کیا۔"),
                DailyVerbExample("She has ignored.", "وہ نظر انداز کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"ignore\" with I, you, we, and they. Use \"ignores\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “ignore” اور “He, she, it” کے ساتھ “ignores” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_282_imagine",
            baseForm = "imagine",
            thirdPerson = "imagines",
            presentParticiple = "imagining",
            pastSimple = "imagined",
            pastParticiple = "imagined",
            urduMeaning = "تصور کرنا",
            examples = listOf(
                DailyVerbExample("Imagine peace.", "امن کا تصور کریں۔"),
                DailyVerbExample("He imagines every day.", "وہ ہر روز تصور کرنا کرتا ہے۔"),
                DailyVerbExample("They are imagining now.", "وہ ابھی تصور کرنا کر رہے ہیں۔"),
                DailyVerbExample("We imagined yesterday.", "ہم نے کل تصور کرنا کیا۔"),
                DailyVerbExample("She has imagined.", "وہ تصور کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"imagine\" with I, you, we, and they. Use \"imagines\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “imagine” اور “He, she, it” کے ساتھ “imagines” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_283_impress",
            baseForm = "impress",
            thirdPerson = "impresses",
            presentParticiple = "impressing",
            pastSimple = "impressed",
            pastParticiple = "impressed",
            urduMeaning = "متاثر کرنا",
            examples = listOf(
                DailyVerbExample("Impress the audience.", "سامعین کو متاثر کریں۔"),
                DailyVerbExample("He impresses every day.", "وہ ہر روز متاثر کرنا کرتا ہے۔"),
                DailyVerbExample("They are impressing now.", "وہ ابھی متاثر کرنا کر رہے ہیں۔"),
                DailyVerbExample("We impressed yesterday.", "ہم نے کل متاثر کرنا کیا۔"),
                DailyVerbExample("She has impressed.", "وہ متاثر کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"impress\" with I, you, we, and they. Use \"impresses\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “impress” اور “He, she, it” کے ساتھ “impresses” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_284_improve",
            baseForm = "improve",
            thirdPerson = "improves",
            presentParticiple = "improving",
            pastSimple = "improved",
            pastParticiple = "improved",
            urduMeaning = "بہتر بنانا",
            examples = listOf(
                DailyVerbExample("Improve your handwriting.", "اپنی تحریر بہتر بنائیں۔"),
                DailyVerbExample("He improves every day.", "وہ ہر روز بہتر بنانا کرتا ہے۔"),
                DailyVerbExample("They are improving now.", "وہ ابھی بہتر بنانا کر رہے ہیں۔"),
                DailyVerbExample("We improved yesterday.", "ہم نے کل بہتر بنانا کیا۔"),
                DailyVerbExample("She has improved.", "وہ بہتر بنانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"improve\" with I, you, we, and they. Use \"improves\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “improve” اور “He, she, it” کے ساتھ “improves” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_285_increase",
            baseForm = "increase",
            thirdPerson = "increases",
            presentParticiple = "increasing",
            pastSimple = "increased",
            pastParticiple = "increased",
            urduMeaning = "بڑھانا",
            examples = listOf(
                DailyVerbExample("Increase knowledge.", "علم میں اضافہ کریں۔"),
                DailyVerbExample("He increases every day.", "وہ ہر روز بڑھانا کرتا ہے۔"),
                DailyVerbExample("They are increasing now.", "وہ ابھی بڑھانا کر رہے ہیں۔"),
                DailyVerbExample("We increased yesterday.", "ہم نے کل بڑھانا کیا۔"),
                DailyVerbExample("She has increased.", "وہ بڑھانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"increase\" with I, you, we, and they. Use \"increases\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “increase” اور “He, she, it” کے ساتھ “increases” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_286_inform",
            baseForm = "inform",
            thirdPerson = "informs",
            presentParticiple = "informing",
            pastSimple = "informed",
            pastParticiple = "informed",
            urduMeaning = "اطلاع دینا",
            examples = listOf(
                DailyVerbExample("Inform the office.", "دفتر کو اطلاع دیں۔"),
                DailyVerbExample("He informs every day.", "وہ ہر روز اطلاع دینا کرتا ہے۔"),
                DailyVerbExample("They are informing now.", "وہ ابھی اطلاع دینا کر رہے ہیں۔"),
                DailyVerbExample("We informed yesterday.", "ہم نے کل اطلاع دینا کیا۔"),
                DailyVerbExample("She has informed.", "وہ اطلاع دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"inform\" with I, you, we, and they. Use \"informs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “inform” اور “He, she, it” کے ساتھ “informs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_287_inject",
            baseForm = "inject",
            thirdPerson = "injects",
            presentParticiple = "injecting",
            pastSimple = "injected",
            pastParticiple = "injected",
            urduMeaning = "ٹیپ لگانا",
            examples = listOf(
                DailyVerbExample("Inject medicine.", "دوا کا انجیکشن لگائیں۔"),
                DailyVerbExample("He injects every day.", "وہ ہر روز ٹیپ لگانا کرتا ہے۔"),
                DailyVerbExample("They are injecting now.", "وہ ابھی ٹیپ لگانا کر رہے ہیں۔"),
                DailyVerbExample("We injected yesterday.", "ہم نے کل ٹیپ لگانا کیا۔"),
                DailyVerbExample("She has injected.", "وہ ٹیپ لگانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"inject\" with I, you, we, and they. Use \"injects\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “inject” اور “He, she, it” کے ساتھ “injects” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_288_injure",
            baseForm = "injure",
            thirdPerson = "injures",
            presentParticiple = "injuring",
            pastSimple = "injured",
            pastParticiple = "injured",
            urduMeaning = "زخمی کرنا",
            examples = listOf(
                DailyVerbExample("Don't injure yourself.", "خود کو زخمی نہ کریں۔"),
                DailyVerbExample("He injures every day.", "وہ ہر روز زخمی کرنا کرتا ہے۔"),
                DailyVerbExample("They are injuring now.", "وہ ابھی زخمی کرنا کر رہے ہیں۔"),
                DailyVerbExample("We injured yesterday.", "ہم نے کل زخمی کرنا کیا۔"),
                DailyVerbExample("She has injured.", "وہ زخمی کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"injure\" with I, you, we, and they. Use \"injures\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “injure” اور “He, she, it” کے ساتھ “injures” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_289_insert",
            baseForm = "insert",
            thirdPerson = "inserts",
            presentParticiple = "inserting",
            pastSimple = "inserted",
            pastParticiple = "inserted",
            urduMeaning = "داخل کرنا",
            examples = listOf(
                DailyVerbExample("Insert coin.", "سکہ داخل کریں۔"),
                DailyVerbExample("He inserts every day.", "وہ ہر روز داخل کرنا کرتا ہے۔"),
                DailyVerbExample("They are inserting now.", "وہ ابھی داخل کرنا کر رہے ہیں۔"),
                DailyVerbExample("We inserted yesterday.", "ہم نے کل داخل کرنا کیا۔"),
                DailyVerbExample("She has inserted.", "وہ داخل کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"insert\" with I, you, we, and they. Use \"inserts\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “insert” اور “He, she, it” کے ساتھ “inserts” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_290_insist",
            baseForm = "insist",
            thirdPerson = "insists",
            presentParticiple = "insisting",
            pastSimple = "insisted",
            pastParticiple = "insisted",
            urduMeaning = "اصرار کرنا",
            examples = listOf(
                DailyVerbExample("He insisted on going.", "اس نے جانے پر اصرار کیا۔"),
                DailyVerbExample("He insists every day.", "وہ ہر روز اصرار کرنا کرتا ہے۔"),
                DailyVerbExample("They are insisting now.", "وہ ابھی اصرار کرنا کر رہے ہیں۔"),
                DailyVerbExample("We insisted yesterday.", "ہم نے کل اصرار کرنا کیا۔"),
                DailyVerbExample("She has insisted.", "وہ اصرار کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"insist\" with I, you, we, and they. Use \"insists\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “insist” اور “He, she, it” کے ساتھ “insists” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_291_inspect",
            baseForm = "inspect",
            thirdPerson = "inspects",
            presentParticiple = "inspecting",
            pastSimple = "inspected",
            pastParticiple = "inspected",
            urduMeaning = "معائنہ کرنا",
            examples = listOf(
                DailyVerbExample("Inspect the vehicle.", "گاڑی کا معائنہ کریں۔"),
                DailyVerbExample("He inspects every day.", "وہ ہر روز معائنہ کرنا کرتا ہے۔"),
                DailyVerbExample("They are inspecting now.", "وہ ابھی معائنہ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We inspected yesterday.", "ہم نے کل معائنہ کرنا کیا۔"),
                DailyVerbExample("She has inspected.", "وہ معائنہ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"inspect\" with I, you, we, and they. Use \"inspects\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “inspect” اور “He, she, it” کے ساتھ “inspects” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_292_inspire",
            baseForm = "inspire",
            thirdPerson = "inspires",
            presentParticiple = "inspiring",
            pastSimple = "inspired",
            pastParticiple = "inspired",
            urduMeaning = "متاثر کرنا",
            examples = listOf(
                DailyVerbExample("Inspire others.", "دوسروں کو متاثر کریں۔"),
                DailyVerbExample("He inspires every day.", "وہ ہر روز متاثر کرنا کرتا ہے۔"),
                DailyVerbExample("They are inspiring now.", "وہ ابھی متاثر کرنا کر رہے ہیں۔"),
                DailyVerbExample("We inspired yesterday.", "ہم نے کل متاثر کرنا کیا۔"),
                DailyVerbExample("She has inspired.", "وہ متاثر کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"inspire\" with I, you, we, and they. Use \"inspires\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “inspire” اور “He, she, it” کے ساتھ “inspires” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_293_install",
            baseForm = "install",
            thirdPerson = "installs",
            presentParticiple = "installing",
            pastSimple = "installed",
            pastParticiple = "installed",
            urduMeaning = "نصب کرنا",
            examples = listOf(
                DailyVerbExample("Install software.", "سافٹ ویئر نصب کریں۔"),
                DailyVerbExample("He installs every day.", "وہ ہر روز نصب کرنا کرتا ہے۔"),
                DailyVerbExample("They are installing now.", "وہ ابھی نصب کرنا کر رہے ہیں۔"),
                DailyVerbExample("We installed yesterday.", "ہم نے کل نصب کرنا کیا۔"),
                DailyVerbExample("She has installed.", "وہ نصب کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"install\" with I, you, we, and they. Use \"installs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “install” اور “He, she, it” کے ساتھ “installs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_294_instruct",
            baseForm = "instruct",
            thirdPerson = "instructs",
            presentParticiple = "instructing",
            pastSimple = "instructed",
            pastParticiple = "instructed",
            urduMeaning = "ہدایت دینا",
            examples = listOf(
                DailyVerbExample("Instruct clearly.", "واضح ہدایت دیں۔"),
                DailyVerbExample("He instructs every day.", "وہ ہر روز ہدایت دینا کرتا ہے۔"),
                DailyVerbExample("They are instructing now.", "وہ ابھی ہدایت دینا کر رہے ہیں۔"),
                DailyVerbExample("We instructed yesterday.", "ہم نے کل ہدایت دینا کیا۔"),
                DailyVerbExample("She has instructed.", "وہ ہدایت دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"instruct\" with I, you, we, and they. Use \"instructs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “instruct” اور “He, she, it” کے ساتھ “instructs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_295_intend",
            baseForm = "intend",
            thirdPerson = "intends",
            presentParticiple = "intending",
            pastSimple = "intended",
            pastParticiple = "intended",
            urduMeaning = "ارادہ رکھنا",
            examples = listOf(
                DailyVerbExample("I intend to travel.", "میرا سفر کا ارادہ ہے۔"),
                DailyVerbExample("He intends every day.", "وہ ہر روز ارادہ رکھنا کرتا ہے۔"),
                DailyVerbExample("They are intending now.", "وہ ابھی ارادہ رکھنا کر رہے ہیں۔"),
                DailyVerbExample("We intended yesterday.", "ہم نے کل ارادہ رکھنا کیا۔"),
                DailyVerbExample("She has intended.", "وہ ارادہ رکھنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"intend\" with I, you, we, and they. Use \"intends\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “intend” اور “He, she, it” کے ساتھ “intends” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_296_introduce",
            baseForm = "introduce",
            thirdPerson = "introduces",
            presentParticiple = "introducing",
            pastSimple = "introduced",
            pastParticiple = "introduced",
            urduMeaning = "تعارف کروانا",
            examples = listOf(
                DailyVerbExample("Introduce yourself.", "اپنا تعارف کروائیں۔"),
                DailyVerbExample("He introduces every day.", "وہ ہر روز تعارف کروانا کرتا ہے۔"),
                DailyVerbExample("They are introducing now.", "وہ ابھی تعارف کروانا کر رہے ہیں۔"),
                DailyVerbExample("We introduced yesterday.", "ہم نے کل تعارف کروانا کیا۔"),
                DailyVerbExample("She has introduced.", "وہ تعارف کروانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"introduce\" with I, you, we, and they. Use \"introduces\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “introduce” اور “He, she, it” کے ساتھ “introduces” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_297_invent",
            baseForm = "invent",
            thirdPerson = "invents",
            presentParticiple = "inventing",
            pastSimple = "invented",
            pastParticiple = "invented",
            urduMeaning = "ایجاد کرنا",
            examples = listOf(
                DailyVerbExample("Edison invented light bulb.", "ایڈیسن نے بلب ایجاد کیا۔"),
                DailyVerbExample("He invents every day.", "وہ ہر روز ایجاد کرنا کرتا ہے۔"),
                DailyVerbExample("They are inventing now.", "وہ ابھی ایجاد کرنا کر رہے ہیں۔"),
                DailyVerbExample("We invented yesterday.", "ہم نے کل ایجاد کرنا کیا۔"),
                DailyVerbExample("She has invented.", "وہ ایجاد کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"invent\" with I, you, we, and they. Use \"invents\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “invent” اور “He, she, it” کے ساتھ “invents” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_298_invest",
            baseForm = "invest",
            thirdPerson = "invests",
            presentParticiple = "investing",
            pastSimple = "invested",
            pastParticiple = "invested",
            urduMeaning = "سرمایہ کاری کرنا",
            examples = listOf(
                DailyVerbExample("Invest wisely.", "عقلمندی سے سرمایہ کاری کریں۔"),
                DailyVerbExample("He invests every day.", "وہ ہر روز سرمایہ کاری کرنا کرتا ہے۔"),
                DailyVerbExample("They are investing now.", "وہ ابھی سرمایہ کاری کرنا کر رہے ہیں۔"),
                DailyVerbExample("We invested yesterday.", "ہم نے کل سرمایہ کاری کرنا کیا۔"),
                DailyVerbExample("She has invested.", "وہ سرمایہ کاری کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"invest\" with I, you, we, and they. Use \"invests\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “invest” اور “He, she, it” کے ساتھ “invests” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_299_iron",
            baseForm = "iron",
            thirdPerson = "irons",
            presentParticiple = "ironing",
            pastSimple = "ironed",
            pastParticiple = "ironed",
            urduMeaning = "استری کرنا",
            examples = listOf(
                DailyVerbExample("Iron the clothes.", "کپڑے استری کریں۔"),
                DailyVerbExample("He irons every day.", "وہ ہر روز استری کرنا کرتا ہے۔"),
                DailyVerbExample("They are ironing now.", "وہ ابھی استری کرنا کر رہے ہیں۔"),
                DailyVerbExample("We ironed yesterday.", "ہم نے کل استری کرنا کیا۔"),
                DailyVerbExample("She has ironed.", "وہ استری کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"iron\" with I, you, we, and they. Use \"irons\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “iron” اور “He, she, it” کے ساتھ “irons” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_300_issue",
            baseForm = "issue",
            thirdPerson = "issues",
            presentParticiple = "issuing",
            pastSimple = "issued",
            pastParticiple = "issued",
            urduMeaning = "جاری کرنا",
            examples = listOf(
                DailyVerbExample("Issue a receipt.", "رسید جاری کریں۔"),
                DailyVerbExample("He issues every day.", "وہ ہر روز جاری کرنا کرتا ہے۔"),
                DailyVerbExample("They are issuing now.", "وہ ابھی جاری کرنا کر رہے ہیں۔"),
                DailyVerbExample("We issued yesterday.", "ہم نے کل جاری کرنا کیا۔"),
                DailyVerbExample("She has issued.", "وہ جاری کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"issue\" with I, you, we, and they. Use \"issues\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “issue” اور “He, she, it” کے ساتھ “issues” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_301_join",
            baseForm = "join",
            thirdPerson = "joins",
            presentParticiple = "joining",
            pastSimple = "joined",
            pastParticiple = "joined",
            urduMeaning = "شامل ہونا",
            examples = listOf(
                DailyVerbExample("Join our group.", "ہمارے گروپ میں شامل ہوں۔"),
                DailyVerbExample("He joins every day.", "وہ ہر روز شامل ہونا کرتا ہے۔"),
                DailyVerbExample("They are joining now.", "وہ ابھی شامل ہونا کر رہے ہیں۔"),
                DailyVerbExample("We joined yesterday.", "ہم نے کل شامل ہونا کیا۔"),
                DailyVerbExample("She has joined.", "وہ شامل ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"join\" with I, you, we, and they. Use \"joins\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “join” اور “He, she, it” کے ساتھ “joins” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_302_joke",
            baseForm = "joke",
            thirdPerson = "jokes",
            presentParticiple = "joking",
            pastSimple = "joked",
            pastParticiple = "joked",
            urduMeaning = "مذاق کرنا",
            examples = listOf(
                DailyVerbExample("Don't joke here.", "یہاں مذاق نہ کریں۔"),
                DailyVerbExample("He jokes every day.", "وہ ہر روز مذاق کرنا کرتا ہے۔"),
                DailyVerbExample("They are joking now.", "وہ ابھی مذاق کرنا کر رہے ہیں۔"),
                DailyVerbExample("We joked yesterday.", "ہم نے کل مذاق کرنا کیا۔"),
                DailyVerbExample("She has joked.", "وہ مذاق کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"joke\" with I, you, we, and they. Use \"jokes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “joke” اور “He, she, it” کے ساتھ “jokes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_303_judge",
            baseForm = "judge",
            thirdPerson = "judges",
            presentParticiple = "judging",
            pastSimple = "judged",
            pastParticiple = "judged",
            urduMeaning = "فیصلہ کرنا",
            examples = listOf(
                DailyVerbExample("Don't judge books by cover.", "ظاہر سے اندازہ نہ لگائیں۔"),
                DailyVerbExample("He judges every day.", "وہ ہر روز فیصلہ کرنا کرتا ہے۔"),
                DailyVerbExample("They are judging now.", "وہ ابھی فیصلہ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We judged yesterday.", "ہم نے کل فیصلہ کرنا کیا۔"),
                DailyVerbExample("She has judged.", "وہ فیصلہ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"judge\" with I, you, we, and they. Use \"judges\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “judge” اور “He, she, it” کے ساتھ “judges” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_304_kick",
            baseForm = "kick",
            thirdPerson = "kicks",
            presentParticiple = "kicking",
            pastSimple = "kicked",
            pastParticiple = "kicked",
            urduMeaning = "ٹانگ مارنا",
            examples = listOf(
                DailyVerbExample("Kick the football.", "فٹ بال کو کک ماریں۔"),
                DailyVerbExample("He kicks every day.", "وہ ہر روز ٹانگ مارنا کرتا ہے۔"),
                DailyVerbExample("They are kicking now.", "وہ ابھی ٹانگ مارنا کر رہے ہیں۔"),
                DailyVerbExample("We kicked yesterday.", "ہم نے کل ٹانگ مارنا کیا۔"),
                DailyVerbExample("She has kicked.", "وہ ٹانگ مارنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"kick\" with I, you, we, and they. Use \"kicks\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “kick” اور “He, she, it” کے ساتھ “kicks” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_305_kiss",
            baseForm = "kiss",
            thirdPerson = "kisses",
            presentParticiple = "kissing",
            pastSimple = "kissed",
            pastParticiple = "kissed",
            urduMeaning = "بوسہ دینا",
            examples = listOf(
                DailyVerbExample("Kiss child's forehead.", "بچے کی پیشانی پر بوسہ دیں۔"),
                DailyVerbExample("He kisses every day.", "وہ ہر روز بوسہ دینا کرتا ہے۔"),
                DailyVerbExample("They are kissing now.", "وہ ابھی بوسہ دینا کر رہے ہیں۔"),
                DailyVerbExample("We kissed yesterday.", "ہم نے کل بوسہ دینا کیا۔"),
                DailyVerbExample("She has kissed.", "وہ بوسہ دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"kiss\" with I, you, we, and they. Use \"kisses\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “kiss” اور “He, she, it” کے ساتھ “kisses” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_306_knock",
            baseForm = "knock",
            thirdPerson = "knocks",
            presentParticiple = "knocking",
            pastSimple = "knocked",
            pastParticiple = "knocked",
            urduMeaning = "دستک دینا",
            examples = listOf(
                DailyVerbExample("Knock the door.", "دروازے پر دستک دیں۔"),
                DailyVerbExample("He knocks every day.", "وہ ہر روز دستک دینا کرتا ہے۔"),
                DailyVerbExample("They are knocking now.", "وہ ابھی دستک دینا کر رہے ہیں۔"),
                DailyVerbExample("We knocked yesterday.", "ہم نے کل دستک دینا کیا۔"),
                DailyVerbExample("She has knocked.", "وہ دستک دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"knock\" with I, you, we, and they. Use \"knocks\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “knock” اور “He, she, it” کے ساتھ “knocks” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_307_label",
            baseForm = "label",
            thirdPerson = "labels",
            presentParticiple = "labeling",
            pastSimple = "labeled",
            pastParticiple = "labeled",
            urduMeaning = "لیبل لگانا",
            examples = listOf(
                DailyVerbExample("Label the box.", "ڈبے پر لیبل لگائیں۔"),
                DailyVerbExample("He labels every day.", "وہ ہر روز لیبل لگانا کرتا ہے۔"),
                DailyVerbExample("They are labeling now.", "وہ ابھی لیبل لگانا کر رہے ہیں۔"),
                DailyVerbExample("We labeled yesterday.", "ہم نے کل لیبل لگانا کیا۔"),
                DailyVerbExample("She has labeled.", "وہ لیبل لگانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"label\" with I, you, we, and they. Use \"labels\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “label” اور “He, she, it” کے ساتھ “labels” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_308_lack",
            baseForm = "lack",
            thirdPerson = "lacks",
            presentParticiple = "lacking",
            pastSimple = "lacked",
            pastParticiple = "lacked",
            urduMeaning = "کمی ہونا",
            examples = listOf(
                DailyVerbExample("Plant lacks water.", "پودے کو پانی کی کمی ہے۔"),
                DailyVerbExample("He lacks every day.", "وہ ہر روز کمی ہونا کرتا ہے۔"),
                DailyVerbExample("They are lacking now.", "وہ ابھی کمی ہونا کر رہے ہیں۔"),
                DailyVerbExample("We lacked yesterday.", "ہم نے کل کمی ہونا کیا۔"),
                DailyVerbExample("She has lacked.", "وہ کمی ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"lack\" with I, you, we, and they. Use \"lacks\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “lack” اور “He, she, it” کے ساتھ “lacks” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_309_land",
            baseForm = "land",
            thirdPerson = "lands",
            presentParticiple = "landing",
            pastSimple = "landed",
            pastParticiple = "landed",
            urduMeaning = "اتارنا",
            examples = listOf(
                DailyVerbExample("Plane landed safely.", "جہاز خیریت سے اتر گیا۔"),
                DailyVerbExample("He lands every day.", "وہ ہر روز اتارنا کرتا ہے۔"),
                DailyVerbExample("They are landing now.", "وہ ابھی اتارنا کر رہے ہیں۔"),
                DailyVerbExample("We landed yesterday.", "ہم نے کل اتارنا کیا۔"),
                DailyVerbExample("She has landed.", "وہ اتارنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"land\" with I, you, we, and they. Use \"lands\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “land” اور “He, she, it” کے ساتھ “lands” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_310_launch",
            baseForm = "launch",
            thirdPerson = "launches",
            presentParticiple = "launching",
            pastSimple = "launched",
            pastParticiple = "launched",
            urduMeaning = "شروع کرنا",
            examples = listOf(
                DailyVerbExample("Launch product.", "پروڈکٹ کا آغاز کریں۔"),
                DailyVerbExample("He launches every day.", "وہ ہر روز شروع کرنا کرتا ہے۔"),
                DailyVerbExample("They are launching now.", "وہ ابھی شروع کرنا کر رہے ہیں۔"),
                DailyVerbExample("We launched yesterday.", "ہم نے کل شروع کرنا کیا۔"),
                DailyVerbExample("She has launched.", "وہ شروع کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"launch\" with I, you, we, and they. Use \"launches\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “launch” اور “He, she, it” کے ساتھ “launches” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_311_lean",
            baseForm = "lean",
            thirdPerson = "leans",
            presentParticiple = "leaning",
            pastSimple = "leant / leaned",
            pastParticiple = "leant / leaned",
            urduMeaning = "ٹیک لگانا",
            examples = listOf(
                DailyVerbExample("Don't lean on glass.", "شیشے پر ٹیک نہ لگائیں۔"),
                DailyVerbExample("He leans every day.", "وہ ہر روز ٹیک لگانا کرتا ہے۔"),
                DailyVerbExample("They are leaning now.", "وہ ابھی ٹیک لگانا کر رہے ہیں۔"),
                DailyVerbExample("We leant / leaned yesterday.", "ہم نے کل ٹیک لگانا کیا۔"),
                DailyVerbExample("She has leant / leaned.", "وہ ٹیک لگانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"lean\" with I, you, we, and they. Use \"leans\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “lean” اور “He, she, it” کے ساتھ “leans” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_312_leap",
            baseForm = "leap",
            thirdPerson = "leaps",
            presentParticiple = "leaping",
            pastSimple = "leapt / leaped",
            pastParticiple = "leapt / leaped",
            urduMeaning = "چھلانگ لگانا",
            examples = listOf(
                DailyVerbExample("Leap over puddle.", "پانی کے گڑھے کے اوپر چھلانگ لگائیں۔"),
                DailyVerbExample("He leaps every day.", "وہ ہر روز چھلانگ لگانا کرتا ہے۔"),
                DailyVerbExample("They are leaping now.", "وہ ابھی چھلانگ لگانا کر رہے ہیں۔"),
                DailyVerbExample("We leapt / leaped yesterday.", "ہم نے کل چھلانگ لگانا کیا۔"),
                DailyVerbExample("She has leapt / leaped.", "وہ چھلانگ لگانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"leap\" with I, you, we, and they. Use \"leaps\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “leap” اور “He, she, it” کے ساتھ “leaps” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_313_limit",
            baseForm = "limit",
            thirdPerson = "limits",
            presentParticiple = "limiting",
            pastSimple = "limited",
            pastParticiple = "limited",
            urduMeaning = "محدود کرنا",
            examples = listOf(
                DailyVerbExample("Limit speed.", "رفتار محدود رکھیں۔"),
                DailyVerbExample("He limits every day.", "وہ ہر روز محدود کرنا کرتا ہے۔"),
                DailyVerbExample("They are limiting now.", "وہ ابھی محدود کرنا کر رہے ہیں۔"),
                DailyVerbExample("We limited yesterday.", "ہم نے کل محدود کرنا کیا۔"),
                DailyVerbExample("She has limited.", "وہ محدود کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"limit\" with I, you, we, and they. Use \"limits\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “limit” اور “He, she, it” کے ساتھ “limits” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_314_link",
            baseForm = "link",
            thirdPerson = "links",
            presentParticiple = "linking",
            pastSimple = "linked",
            pastParticiple = "linked",
            urduMeaning = "ربط دینا",
            examples = listOf(
                DailyVerbExample("Link accounts.", "اکاؤنٹس کو منسلک کریں۔"),
                DailyVerbExample("He links every day.", "وہ ہر روز ربط دینا کرتا ہے۔"),
                DailyVerbExample("They are linking now.", "وہ ابھی ربط دینا کر رہے ہیں۔"),
                DailyVerbExample("We linked yesterday.", "ہم نے کل ربط دینا کیا۔"),
                DailyVerbExample("She has linked.", "وہ ربط دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"link\" with I, you, we, and they. Use \"links\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “link” اور “He, she, it” کے ساتھ “links” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_315_lock",
            baseForm = "lock",
            thirdPerson = "locks",
            presentParticiple = "locking",
            pastSimple = "locked",
            pastParticiple = "locked",
            urduMeaning = "قفل لگانا",
            examples = listOf(
                DailyVerbExample("Lock the door.", "دروازے کو تالا لگائیں۔"),
                DailyVerbExample("He locks every day.", "وہ ہر روز قفل لگانا کرتا ہے۔"),
                DailyVerbExample("They are locking now.", "وہ ابھی قفل لگانا کر رہے ہیں۔"),
                DailyVerbExample("We locked yesterday.", "ہم نے کل قفل لگانا کیا۔"),
                DailyVerbExample("She has locked.", "وہ قفل لگانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"lock\" with I, you, we, and they. Use \"locks\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “lock” اور “He, she, it” کے ساتھ “locks” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_316_manage",
            baseForm = "manage",
            thirdPerson = "manages",
            presentParticiple = "managing",
            pastSimple = "managed",
            pastParticiple = "managed",
            urduMeaning = "بندوبست کرنا",
            examples = listOf(
                DailyVerbExample("Manage time well.", "وقت کا اچھا بندوبست کریں۔"),
                DailyVerbExample("He manages every day.", "وہ ہر روز بندوبست کرنا کرتا ہے۔"),
                DailyVerbExample("They are managing now.", "وہ ابھی بندوبست کرنا کر رہے ہیں۔"),
                DailyVerbExample("We managed yesterday.", "ہم نے کل بندوبست کرنا کیا۔"),
                DailyVerbExample("She has managed.", "وہ بندوبست کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"manage\" with I, you, we, and they. Use \"manages\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “manage” اور “He, she, it” کے ساتھ “manages” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_317_march",
            baseForm = "march",
            thirdPerson = "marches",
            presentParticiple = "marching",
            pastSimple = "marched",
            pastParticiple = "marched",
            urduMeaning = "مارچ کرنا",
            examples = listOf(
                DailyVerbExample("Soldiers march.", "سپاہی مارچ کرتے ہیں۔"),
                DailyVerbExample("He marches every day.", "وہ ہر روز مارچ کرنا کرتا ہے۔"),
                DailyVerbExample("They are marching now.", "وہ ابھی مارچ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We marched yesterday.", "ہم نے کل مارچ کرنا کیا۔"),
                DailyVerbExample("She has marched.", "وہ مارچ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"march\" with I, you, we, and they. Use \"marches\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “march” اور “He, she, it” کے ساتھ “marches” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_318_mark",
            baseForm = "mark",
            thirdPerson = "marks",
            presentParticiple = "marking",
            pastSimple = "marked",
            pastParticiple = "marked",
            urduMeaning = "نشان لگانا",
            examples = listOf(
                DailyVerbExample("Mark correct answer.", "صحیح جواب پر نشان لگائیں۔"),
                DailyVerbExample("He marks every day.", "وہ ہر روز نشان لگانا کرتا ہے۔"),
                DailyVerbExample("They are marking now.", "وہ ابھی نشان لگانا کر رہے ہیں۔"),
                DailyVerbExample("We marked yesterday.", "ہم نے کل نشان لگانا کیا۔"),
                DailyVerbExample("She has marked.", "وہ نشان لگانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"mark\" with I, you, we, and they. Use \"marks\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “mark” اور “He, she, it” کے ساتھ “marks” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_319_marry",
            baseForm = "marry",
            thirdPerson = "marries",
            presentParticiple = "marrying",
            pastSimple = "married",
            pastParticiple = "married",
            urduMeaning = "شادی کرنا",
            examples = listOf(
                DailyVerbExample("They married happily.", "انہوں نے خوشی سے شادی کی۔"),
                DailyVerbExample("He marries every day.", "وہ ہر روز شادی کرنا کرتا ہے۔"),
                DailyVerbExample("They are marrying now.", "وہ ابھی شادی کرنا کر رہے ہیں۔"),
                DailyVerbExample("We married yesterday.", "ہم نے کل شادی کرنا کیا۔"),
                DailyVerbExample("She has married.", "وہ شادی کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"marry\" with I, you, we, and they. Use \"marries\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “marry” اور “He, she, it” کے ساتھ “marries” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_320_match",
            baseForm = "match",
            thirdPerson = "matches",
            presentParticiple = "matching",
            pastSimple = "matched",
            pastParticiple = "matched",
            urduMeaning = "ملانا",
            examples = listOf(
                DailyVerbExample("Match the words.", "الفاظ کو ملائیں۔"),
                DailyVerbExample("He matches every day.", "وہ ہر روز ملانا کرتا ہے۔"),
                DailyVerbExample("They are matching now.", "وہ ابھی ملانا کر رہے ہیں۔"),
                DailyVerbExample("We matched yesterday.", "ہم نے کل ملانا کیا۔"),
                DailyVerbExample("She has matched.", "وہ ملانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"match\" with I, you, we, and they. Use \"matches\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “match” اور “He, she, it” کے ساتھ “matches” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_321_measure",
            baseForm = "measure",
            thirdPerson = "measures",
            presentParticiple = "measuring",
            pastSimple = "measured",
            pastParticiple = "measured",
            urduMeaning = "پیمائش کرنا",
            examples = listOf(
                DailyVerbExample("Measure length.", "لمبائی کی پیمائش کریں۔"),
                DailyVerbExample("He measures every day.", "وہ ہر روز پیمائش کرنا کرتا ہے۔"),
                DailyVerbExample("They are measuring now.", "وہ ابھی پیمائش کرنا کر رہے ہیں۔"),
                DailyVerbExample("We measured yesterday.", "ہم نے کل پیمائش کرنا کیا۔"),
                DailyVerbExample("She has measured.", "وہ پیمائش کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"measure\" with I, you, we, and they. Use \"measures\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “measure” اور “He, she, it” کے ساتھ “measures” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_322_mention",
            baseForm = "mention",
            thirdPerson = "mentions",
            presentParticiple = "mentioning",
            pastSimple = "mentioned",
            pastParticiple = "mentioned",
            urduMeaning = "ذکر کرنا",
            examples = listOf(
                DailyVerbExample("Don't mention it.", "کوئی بات نہیں (ذکر نہ کریں)۔"),
                DailyVerbExample("He mentions every day.", "وہ ہر روز ذکر کرنا کرتا ہے۔"),
                DailyVerbExample("They are mentioning now.", "وہ ابھی ذکر کرنا کر رہے ہیں۔"),
                DailyVerbExample("We mentioned yesterday.", "ہم نے کل ذکر کرنا کیا۔"),
                DailyVerbExample("She has mentioned.", "وہ ذکر کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"mention\" with I, you, we, and they. Use \"mentions\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “mention” اور “He, she, it” کے ساتھ “mentions” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_323_mind",
            baseForm = "mind",
            thirdPerson = "minds",
            presentParticiple = "minding",
            pastSimple = "minded",
            pastParticiple = "minded",
            urduMeaning = "برا ماننا",
            examples = listOf(
                DailyVerbExample("Do you mind waiting?", "کیا آپ کو انتظار کرنے میں اعتراض ہے؟"),
                DailyVerbExample("He minds every day.", "وہ ہر روز برا ماننا کرتا ہے۔"),
                DailyVerbExample("They are minding now.", "وہ ابھی برا ماننا کر رہے ہیں۔"),
                DailyVerbExample("We minded yesterday.", "ہم نے کل برا ماننا کیا۔"),
                DailyVerbExample("She has minded.", "وہ برا ماننا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"mind\" with I, you, we, and they. Use \"minds\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “mind” اور “He, she, it” کے ساتھ “minds” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_324_miss",
            baseForm = "miss",
            thirdPerson = "misses",
            presentParticiple = "missing",
            pastSimple = "missed",
            pastParticiple = "missed",
            urduMeaning = "مس کرنا",
            examples = listOf(
                DailyVerbExample("I miss my family.", "مجھے اپنے خاندان کی یاد آتی ہے۔"),
                DailyVerbExample("He misses every day.", "وہ ہر روز مس کرنا کرتا ہے۔"),
                DailyVerbExample("They are missing now.", "وہ ابھی مس کرنا کر رہے ہیں۔"),
                DailyVerbExample("We missed yesterday.", "ہم نے کل مس کرنا کیا۔"),
                DailyVerbExample("She has missed.", "وہ مس کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"miss\" with I, you, we, and they. Use \"misses\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “miss” اور “He, she, it” کے ساتھ “misses” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_325_mix",
            baseForm = "mix",
            thirdPerson = "mixes",
            presentParticiple = "mixing",
            pastSimple = "mixed",
            pastParticiple = "mixed",
            urduMeaning = "مکس کرنا",
            examples = listOf(
                DailyVerbExample("Mix ingredients.", "اجزاء کو مکس کریں۔"),
                DailyVerbExample("He mixes every day.", "وہ ہر روز مکس کرنا کرتا ہے۔"),
                DailyVerbExample("They are mixing now.", "وہ ابھی مکس کرنا کر رہے ہیں۔"),
                DailyVerbExample("We mixed yesterday.", "ہم نے کل مکس کرنا کیا۔"),
                DailyVerbExample("She has mixed.", "وہ مکس کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"mix\" with I, you, we, and they. Use \"mixes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “mix” اور “He, she, it” کے ساتھ “mixes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_326_modify",
            baseForm = "modify",
            thirdPerson = "modifies",
            presentParticiple = "modifying",
            pastSimple = "modified",
            pastParticiple = "modified",
            urduMeaning = "ترمیم کرنا",
            examples = listOf(
                DailyVerbExample("Modify the text.", "متن میں ترمیم کریں۔"),
                DailyVerbExample("He modifies every day.", "وہ ہر روز ترمیم کرنا کرتا ہے۔"),
                DailyVerbExample("They are modifying now.", "وہ ابھی ترمیم کرنا کر رہے ہیں۔"),
                DailyVerbExample("We modified yesterday.", "ہم نے کل ترمیم کرنا کیا۔"),
                DailyVerbExample("She has modified.", "وہ ترمیم کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"modify\" with I, you, we, and they. Use \"modifies\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “modify” اور “He, she, it” کے ساتھ “modifies” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_327_multiply",
            baseForm = "multiply",
            thirdPerson = "multiplies",
            presentParticiple = "multiplying",
            pastSimple = "multiplied",
            pastParticiple = "multiplied",
            urduMeaning = "ضرب دینا",
            examples = listOf(
                DailyVerbExample("Multiply numbers.", "اعداد کو ضرب دیں۔"),
                DailyVerbExample("He multiplies every day.", "وہ ہر روز ضرب دینا کرتا ہے۔"),
                DailyVerbExample("They are multiplying now.", "وہ ابھی ضرب دینا کر رہے ہیں۔"),
                DailyVerbExample("We multiplied yesterday.", "ہم نے کل ضرب دینا کیا۔"),
                DailyVerbExample("She has multiplied.", "وہ ضرب دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"multiply\" with I, you, we, and they. Use \"multiplies\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “multiply” اور “He, she, it” کے ساتھ “multiplies” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_328_name",
            baseForm = "name",
            thirdPerson = "names",
            presentParticiple = "naming",
            pastSimple = "named",
            pastParticiple = "named",
            urduMeaning = "نام رکھنا",
            examples = listOf(
                DailyVerbExample("Name the baby.", "بچے کا نام رکھیں۔"),
                DailyVerbExample("He names every day.", "وہ ہر روز نام رکھنا کرتا ہے۔"),
                DailyVerbExample("They are naming now.", "وہ ابھی نام رکھنا کر رہے ہیں۔"),
                DailyVerbExample("We named yesterday.", "ہم نے کل نام رکھنا کیا۔"),
                DailyVerbExample("She has named.", "وہ نام رکھنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"name\" with I, you, we, and they. Use \"names\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “name” اور “He, she, it” کے ساتھ “names” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_329_nod",
            baseForm = "nod",
            thirdPerson = "nods",
            presentParticiple = "nodding",
            pastSimple = "nodded",
            pastParticiple = "nodded",
            urduMeaning = "سر ہلا کر ہاں کہنا",
            examples = listOf(
                DailyVerbExample("He nodded agreement.", "اس نے ہاں میں سر ہلایا۔"),
                DailyVerbExample("He nods every day.", "وہ ہر روز سر ہلا کر ہاں کہنا کرتا ہے۔"),
                DailyVerbExample("They are nodding now.", "وہ ابھی سر ہلا کر ہاں کہنا کر رہے ہیں۔"),
                DailyVerbExample("We nodded yesterday.", "ہم نے کل سر ہلا کر ہاں کہنا کیا۔"),
                DailyVerbExample("She has nodded.", "وہ سر ہلا کر ہاں کہنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"nod\" with I, you, we, and they. Use \"nods\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “nod” اور “He, she, it” کے ساتھ “nods” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_330_notice",
            baseForm = "notice",
            thirdPerson = "notices",
            presentParticiple = "noticing",
            pastSimple = "noticed",
            pastParticiple = "noticed",
            urduMeaning = "نوٹس کرنا",
            examples = listOf(
                DailyVerbExample("I noticed a change.", "میں نے ایک تبدیلی نوٹس کی۔"),
                DailyVerbExample("He notices every day.", "وہ ہر روز نوٹس کرنا کرتا ہے۔"),
                DailyVerbExample("They are noticing now.", "وہ ابھی نوٹس کرنا کر رہے ہیں۔"),
                DailyVerbExample("We noticed yesterday.", "ہم نے کل نوٹس کرنا کیا۔"),
                DailyVerbExample("She has noticed.", "وہ نوٹس کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"notice\" with I, you, we, and they. Use \"notices\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “notice” اور “He, she, it” کے ساتھ “notices” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_331_obey",
            baseForm = "obey",
            thirdPerson = "obeys",
            presentParticiple = "obeying",
            pastSimple = "obeyed",
            pastParticiple = "obeyed",
            urduMeaning = "فرمانبرداری کرنا",
            examples = listOf(
                DailyVerbExample("Obey laws.", "قوانین کی پاسداری کریں۔"),
                DailyVerbExample("He obeys every day.", "وہ ہر روز فرمانبرداری کرنا کرتا ہے۔"),
                DailyVerbExample("They are obeying now.", "وہ ابھی فرمانبرداری کرنا کر رہے ہیں۔"),
                DailyVerbExample("We obeyed yesterday.", "ہم نے کل فرمانبرداری کرنا کیا۔"),
                DailyVerbExample("She has obeyed.", "وہ فرمانبرداری کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"obey\" with I, you, we, and they. Use \"obeys\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “obey” اور “He, she, it” کے ساتھ “obeys” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_332_observe",
            baseForm = "observe",
            thirdPerson = "observes",
            presentParticiple = "observing",
            pastSimple = "observed",
            pastParticiple = "observed",
            urduMeaning = "مشاہدہ کرنا",
            examples = listOf(
                DailyVerbExample("Observe nature.", "قدرت کا مشاہدہ کریں۔"),
                DailyVerbExample("He observes every day.", "وہ ہر روز مشاہدہ کرنا کرتا ہے۔"),
                DailyVerbExample("They are observing now.", "وہ ابھی مشاہدہ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We observed yesterday.", "ہم نے کل مشاہدہ کرنا کیا۔"),
                DailyVerbExample("She has observed.", "وہ مشاہدہ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"observe\" with I, you, we, and they. Use \"observes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “observe” اور “He, she, it” کے ساتھ “observes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_333_obtain",
            baseForm = "obtain",
            thirdPerson = "obtains",
            presentParticiple = "obtaining",
            pastSimple = "obtained",
            pastParticiple = "obtained",
            urduMeaning = "حاصل کرنا",
            examples = listOf(
                DailyVerbExample("Obtain permit.", "اجازت نامہ حاصل کریں۔"),
                DailyVerbExample("He obtains every day.", "وہ ہر روز حاصل کرنا کرتا ہے۔"),
                DailyVerbExample("They are obtaining now.", "وہ ابھی حاصل کرنا کر رہے ہیں۔"),
                DailyVerbExample("We obtained yesterday.", "ہم نے کل حاصل کرنا کیا۔"),
                DailyVerbExample("She has obtained.", "وہ حاصل کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"obtain\" with I, you, we, and they. Use \"obtains\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “obtain” اور “He, she, it” کے ساتھ “obtains” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_334_occur",
            baseForm = "occur",
            thirdPerson = "occurs",
            presentParticiple = "occurring",
            pastSimple = "occurred",
            pastParticiple = "occurred",
            urduMeaning = "واقع ہونا",
            examples = listOf(
                DailyVerbExample("Events occur daily.", "واقعات روزانہ ہوتے ہیں۔"),
                DailyVerbExample("He occurs every day.", "وہ ہر روز واقع ہونا کرتا ہے۔"),
                DailyVerbExample("They are occurring now.", "وہ ابھی واقع ہونا کر رہے ہیں۔"),
                DailyVerbExample("We occurred yesterday.", "ہم نے کل واقع ہونا کیا۔"),
                DailyVerbExample("She has occurred.", "وہ واقع ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"occur\" with I, you, we, and they. Use \"occurs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “occur” اور “He, she, it” کے ساتھ “occurs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_335_operate",
            baseForm = "operate",
            thirdPerson = "operates",
            presentParticiple = "operating",
            pastSimple = "operated",
            pastParticiple = "operated",
            urduMeaning = "چلانا",
            examples = listOf(
                DailyVerbExample("Operate machinery.", "مشینری چلائیں۔"),
                DailyVerbExample("He operates every day.", "وہ ہر روز چلانا کرتا ہے۔"),
                DailyVerbExample("They are operating now.", "وہ ابھی چلانا کر رہے ہیں۔"),
                DailyVerbExample("We operated yesterday.", "ہم نے کل چلانا کیا۔"),
                DailyVerbExample("She has operated.", "وہ چلانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"operate\" with I, you, we, and they. Use \"operates\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “operate” اور “He, she, it” کے ساتھ “operates” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_336_order",
            baseForm = "order",
            thirdPerson = "orders",
            presentParticiple = "ordering",
            pastSimple = "ordered",
            pastParticiple = "ordered",
            urduMeaning = "حکم دینا",
            examples = listOf(
                DailyVerbExample("Order food online.", "آن لائن کھانا آرڈر کریں۔"),
                DailyVerbExample("He orders every day.", "وہ ہر روز حکم دینا کرتا ہے۔"),
                DailyVerbExample("They are ordering now.", "وہ ابھی حکم دینا کر رہے ہیں۔"),
                DailyVerbExample("We ordered yesterday.", "ہم نے کل حکم دینا کیا۔"),
                DailyVerbExample("She has ordered.", "وہ حکم دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"order\" with I, you, we, and they. Use \"orders\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “order” اور “He, she, it” کے ساتھ “orders” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_337_organize",
            baseForm = "organize",
            thirdPerson = "organizes",
            presentParticiple = "organizing",
            pastSimple = "organized",
            pastParticiple = "organized",
            urduMeaning = "تنظیم کرنا",
            examples = listOf(
                DailyVerbExample("Organize event.", "تقریب کی تنظیم کریں۔"),
                DailyVerbExample("He organizes every day.", "وہ ہر روز تنظیم کرنا کرتا ہے۔"),
                DailyVerbExample("They are organizing now.", "وہ ابھی تنظیم کرنا کر رہے ہیں۔"),
                DailyVerbExample("We organized yesterday.", "ہم نے کل تنظیم کرنا کیا۔"),
                DailyVerbExample("She has organized.", "وہ تنظیم کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"organize\" with I, you, we, and they. Use \"organizes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “organize” اور “He, she, it” کے ساتھ “organizes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_338_overcome",
            baseForm = "overcome",
            thirdPerson = "overcomes",
            presentParticiple = "overcoming",
            pastSimple = "overcame",
            pastParticiple = "overcome",
            urduMeaning = "قابو پانا",
            examples = listOf(
                DailyVerbExample("Overcome fear.", "خوف پر قابو پائیں۔"),
                DailyVerbExample("He overcomes every day.", "وہ ہر روز قابو پانا کرتا ہے۔"),
                DailyVerbExample("They are overcoming now.", "وہ ابھی قابو پانا کر رہے ہیں۔"),
                DailyVerbExample("We overcame yesterday.", "ہم نے کل قابو پانا کیا۔"),
                DailyVerbExample("She has overcome.", "وہ قابو پانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"overcome\" with I, you, we, and they. Use \"overcomes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “overcome” اور “He, she, it” کے ساتھ “overcomes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_339_owe",
            baseForm = "owe",
            thirdPerson = "owes",
            presentParticiple = "owing",
            pastSimple = "owed",
            pastParticiple = "owed",
            urduMeaning = "مقروض ہونا",
            examples = listOf(
                DailyVerbExample("I owe you thanks.", "میں آپ کا شکر گزار ہوں۔"),
                DailyVerbExample("He owes every day.", "وہ ہر روز مقروض ہونا کرتا ہے۔"),
                DailyVerbExample("They are owing now.", "وہ ابھی مقروض ہونا کر رہے ہیں۔"),
                DailyVerbExample("We owed yesterday.", "ہم نے کل مقروض ہونا کیا۔"),
                DailyVerbExample("She has owed.", "وہ مقروض ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"owe\" with I, you, we, and they. Use \"owes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “owe” اور “He, she, it” کے ساتھ “owes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_340_own",
            baseForm = "own",
            thirdPerson = "owns",
            presentParticiple = "owning",
            pastSimple = "owned",
            pastParticiple = "owned",
            urduMeaning = "مالک ہونا",
            examples = listOf(
                DailyVerbExample("He owns a house.", "وہ ایک گھر کا مالک ہے۔"),
                DailyVerbExample("He owns every day.", "وہ ہر روز مالک ہونا کرتا ہے۔"),
                DailyVerbExample("They are owning now.", "وہ ابھی مالک ہونا کر رہے ہیں۔"),
                DailyVerbExample("We owned yesterday.", "ہم نے کل مالک ہونا کیا۔"),
                DailyVerbExample("She has owned.", "وہ مالک ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"own\" with I, you, we, and they. Use \"owns\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “own” اور “He, she, it” کے ساتھ “owns” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_341_pack",
            baseForm = "pack",
            thirdPerson = "packs",
            presentParticiple = "packing",
            pastSimple = "packed",
            pastParticiple = "packed",
            urduMeaning = "پیک کرنا",
            examples = listOf(
                DailyVerbExample("Pack your bags.", "اپنے بیگ پیک کریں۔"),
                DailyVerbExample("He packs every day.", "وہ ہر روز پیک کرنا کرتا ہے۔"),
                DailyVerbExample("They are packing now.", "وہ ابھی پیک کرنا کر رہے ہیں۔"),
                DailyVerbExample("We packed yesterday.", "ہم نے کل پیک کرنا کیا۔"),
                DailyVerbExample("She has packed.", "وہ پیک کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"pack\" with I, you, we, and they. Use \"packs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “pack” اور “He, she, it” کے ساتھ “packs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_342_park",
            baseForm = "park",
            thirdPerson = "parks",
            presentParticiple = "parking",
            pastSimple = "parked",
            pastParticiple = "parked",
            urduMeaning = "پارک کرنا",
            examples = listOf(
                DailyVerbExample("Park the car.", "گاڑی پارک کریں۔"),
                DailyVerbExample("He parks every day.", "وہ ہر روز پارک کرنا کرتا ہے۔"),
                DailyVerbExample("They are parking now.", "وہ ابھی پارک کرنا کر رہے ہیں۔"),
                DailyVerbExample("We parked yesterday.", "ہم نے کل پارک کرنا کیا۔"),
                DailyVerbExample("She has parked.", "وہ پارک کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"park\" with I, you, we, and they. Use \"parks\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “park” اور “He, she, it” کے ساتھ “parks” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_343_perform",
            baseForm = "perform",
            thirdPerson = "performs",
            presentParticiple = "performing",
            pastSimple = "performed",
            pastParticiple = "performed",
            urduMeaning = "کارکردگی دکھانا",
            examples = listOf(
                DailyVerbExample("Perform well.", "اچھی کارکردگی دکھائیں۔"),
                DailyVerbExample("He performs every day.", "وہ ہر روز کارکردگی دکھانا کرتا ہے۔"),
                DailyVerbExample("They are performing now.", "وہ ابھی کارکردگی دکھانا کر رہے ہیں۔"),
                DailyVerbExample("We performed yesterday.", "ہم نے کل کارکردگی دکھانا کیا۔"),
                DailyVerbExample("She has performed.", "وہ کارکردگی دکھانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"perform\" with I, you, we, and they. Use \"performs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “perform” اور “He, she, it” کے ساتھ “performs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_344_permit",
            baseForm = "permit",
            thirdPerson = "permits",
            presentParticiple = "permitting",
            pastSimple = "permitted",
            pastParticiple = "permitted",
            urduMeaning = "اجازت دینا",
            examples = listOf(
                DailyVerbExample("Entry permitted.", "داخلے کی اجازت ہے۔"),
                DailyVerbExample("He permits every day.", "وہ ہر روز اجازت دینا کرتا ہے۔"),
                DailyVerbExample("They are permitting now.", "وہ ابھی اجازت دینا کر رہے ہیں۔"),
                DailyVerbExample("We permitted yesterday.", "ہم نے کل اجازت دینا کیا۔"),
                DailyVerbExample("She has permitted.", "وہ اجازت دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"permit\" with I, you, we, and they. Use \"permits\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “permit” اور “He, she, it” کے ساتھ “permits” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_345_persuade",
            baseForm = "persuade",
            thirdPerson = "persuades",
            presentParticiple = "persuading",
            pastSimple = "persuaded",
            pastParticiple = "persuaded",
            urduMeaning = "قائل کرنا",
            examples = listOf(
                DailyVerbExample("Persuade him.", "اسے قائل کریں۔"),
                DailyVerbExample("He persuades every day.", "وہ ہر روز قائل کرنا کرتا ہے۔"),
                DailyVerbExample("They are persuading now.", "وہ ابھی قائل کرنا کر رہے ہیں۔"),
                DailyVerbExample("We persuaded yesterday.", "ہم نے کل قائل کرنا کیا۔"),
                DailyVerbExample("She has persuaded.", "وہ قائل کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"persuade\" with I, you, we, and they. Use \"persuades\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “persuade” اور “He, she, it” کے ساتھ “persuades” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_346_pick",
            baseForm = "pick",
            thirdPerson = "picks",
            presentParticiple = "picking",
            pastSimple = "picked",
            pastParticiple = "picked",
            urduMeaning = "چننا",
            examples = listOf(
                DailyVerbExample("Pick fresh flowers.", "تازہ پھول چنائیں۔"),
                DailyVerbExample("He picks every day.", "وہ ہر روز چننا کرتا ہے۔"),
                DailyVerbExample("They are picking now.", "وہ ابھی چننا کر رہے ہیں۔"),
                DailyVerbExample("We picked yesterday.", "ہم نے کل چننا کیا۔"),
                DailyVerbExample("She has picked.", "وہ چننا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"pick\" with I, you, we, and they. Use \"picks\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “pick” اور “He, she, it” کے ساتھ “picks” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_347_plan",
            baseForm = "plan",
            thirdPerson = "plans",
            presentParticiple = "planning",
            pastSimple = "planned",
            pastParticiple = "planned",
            urduMeaning = "منصوبہ بنانا",
            examples = listOf(
                DailyVerbExample("Plan your future.", "اپنے مستقبل کا منصوبہ بنائیں۔"),
                DailyVerbExample("He plans every day.", "وہ ہر روز منصوبہ بنانا کرتا ہے۔"),
                DailyVerbExample("They are planning now.", "وہ ابھی منصوبہ بنانا کر رہے ہیں۔"),
                DailyVerbExample("We planned yesterday.", "ہم نے کل منصوبہ بنانا کیا۔"),
                DailyVerbExample("She has planned.", "وہ منصوبہ بنانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"plan\" with I, you, we, and they. Use \"plans\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “plan” اور “He, she, it” کے ساتھ “plans” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_348_point",
            baseForm = "point",
            thirdPerson = "points",
            presentParticiple = "pointing",
            pastSimple = "pointed",
            pastParticiple = "pointed",
            urduMeaning = "اشارہ کرنا",
            examples = listOf(
                DailyVerbExample("Point at target.", "ہدف کی طرف اشارہ کریں۔"),
                DailyVerbExample("He points every day.", "وہ ہر روز اشارہ کرنا کرتا ہے۔"),
                DailyVerbExample("They are pointing now.", "وہ ابھی اشارہ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We pointed yesterday.", "ہم نے کل اشارہ کرنا کیا۔"),
                DailyVerbExample("She has pointed.", "وہ اشارہ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"point\" with I, you, we, and they. Use \"points\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “point” اور “He, she, it” کے ساتھ “points” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_349_polish",
            baseForm = "polish",
            thirdPerson = "polishes",
            presentParticiple = "polishing",
            pastSimple = "polished",
            pastParticiple = "polished",
            urduMeaning = "پالش کرنا",
            examples = listOf(
                DailyVerbExample("Polish shoes.", "جوتے پالش کریں۔"),
                DailyVerbExample("He polishes every day.", "وہ ہر روز پالش کرنا کرتا ہے۔"),
                DailyVerbExample("They are polishing now.", "وہ ابھی پالش کرنا کر رہے ہیں۔"),
                DailyVerbExample("We polished yesterday.", "ہم نے کل پالش کرنا کیا۔"),
                DailyVerbExample("She has polished.", "وہ پالش کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"polish\" with I, you, we, and they. Use \"polishes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “polish” اور “He, she, it” کے ساتھ “polishes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_350_pour",
            baseForm = "pour",
            thirdPerson = "pours",
            presentParticiple = "pouring",
            pastSimple = "poured",
            pastParticiple = "poured",
            urduMeaning = "انڈیلنا",
            examples = listOf(
                DailyVerbExample("Pour tea.", "چائے انڈیلیں۔"),
                DailyVerbExample("He pours every day.", "وہ ہر روز انڈیلنا کرتا ہے۔"),
                DailyVerbExample("They are pouring now.", "وہ ابھی انڈیلنا کر رہے ہیں۔"),
                DailyVerbExample("We poured yesterday.", "ہم نے کل انڈیلنا کیا۔"),
                DailyVerbExample("She has poured.", "وہ انڈیلنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"pour\" with I, you, we, and they. Use \"pours\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “pour” اور “He, she, it” کے ساتھ “pours” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_351_pray",
            baseForm = "pray",
            thirdPerson = "prays",
            presentParticiple = "praying",
            pastSimple = "prayed",
            pastParticiple = "prayed",
            urduMeaning = "دعا کرنا",
            examples = listOf(
                DailyVerbExample("Pray for peace.", "امن کی دعا کریں۔"),
                DailyVerbExample("He prays every day.", "وہ ہر روز دعا کرنا کرتا ہے۔"),
                DailyVerbExample("They are praying now.", "وہ ابھی دعا کرنا کر رہے ہیں۔"),
                DailyVerbExample("We prayed yesterday.", "ہم نے کل دعا کرنا کیا۔"),
                DailyVerbExample("She has prayed.", "وہ دعا کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"pray\" with I, you, we, and they. Use \"prays\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “pray” اور “He, she, it” کے ساتھ “prays” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_352_predict",
            baseForm = "predict",
            thirdPerson = "predicts",
            presentParticiple = "predicting",
            pastSimple = "predicted",
            pastParticiple = "predicted",
            urduMeaning = "پیشین گوئی کرنا",
            examples = listOf(
                DailyVerbExample("Predict weather.", "موسم کی پیشین گوئی کریں۔"),
                DailyVerbExample("He predicts every day.", "وہ ہر روز پیشین گوئی کرنا کرتا ہے۔"),
                DailyVerbExample("They are predicting now.", "وہ ابھی پیشین گوئی کرنا کر رہے ہیں۔"),
                DailyVerbExample("We predicted yesterday.", "ہم نے کل پیشین گوئی کرنا کیا۔"),
                DailyVerbExample("She has predicted.", "وہ پیشین گوئی کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"predict\" with I, you, we, and they. Use \"predicts\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “predict” اور “He, she, it” کے ساتھ “predicts” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_353_prefer",
            baseForm = "prefer",
            thirdPerson = "prefers",
            presentParticiple = "preferring",
            pastSimple = "preferred",
            pastParticiple = "preferred",
            urduMeaning = "ترجیح دینا",
            examples = listOf(
                DailyVerbExample("Prefer tea to coffee.", "کافی پر چائے کو ترجیح دیں۔"),
                DailyVerbExample("He prefers every day.", "وہ ہر روز ترجیح دینا کرتا ہے۔"),
                DailyVerbExample("They are preferring now.", "وہ ابھی ترجیح دینا کر رہے ہیں۔"),
                DailyVerbExample("We preferred yesterday.", "ہم نے کل ترجیح دینا کیا۔"),
                DailyVerbExample("She has preferred.", "وہ ترجیح دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"prefer\" with I, you, we, and they. Use \"prefers\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “prefer” اور “He, she, it” کے ساتھ “prefers” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_354_prepare",
            baseForm = "prepare",
            thirdPerson = "prepares",
            presentParticiple = "preparing",
            pastSimple = "prepared",
            pastParticiple = "prepared",
            urduMeaning = "تیاری کرنا",
            examples = listOf(
                DailyVerbExample("Prepare exam.", "امتحان کی تیاری کریں۔"),
                DailyVerbExample("He prepares every day.", "وہ ہر روز تیاری کرنا کرتا ہے۔"),
                DailyVerbExample("They are preparing now.", "وہ ابھی تیاری کرنا کر رہے ہیں۔"),
                DailyVerbExample("We prepared yesterday.", "ہم نے کل تیاری کرنا کیا۔"),
                DailyVerbExample("She has prepared.", "وہ تیاری کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"prepare\" with I, you, we, and they. Use \"prepares\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “prepare” اور “He, she, it” کے ساتھ “prepares” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_355_preserve",
            baseForm = "preserve",
            thirdPerson = "preserves",
            presentParticiple = "preserving",
            pastSimple = "preserved",
            pastParticiple = "preserved",
            urduMeaning = "محفوظ رکھنا",
            examples = listOf(
                DailyVerbExample("Preserve history.", "تاریخ کو محفوظ رکھیں۔"),
                DailyVerbExample("He preserves every day.", "وہ ہر روز محفوظ رکھنا کرتا ہے۔"),
                DailyVerbExample("They are preserving now.", "وہ ابھی محفوظ رکھنا کر رہے ہیں۔"),
                DailyVerbExample("We preserved yesterday.", "ہم نے کل محفوظ رکھنا کیا۔"),
                DailyVerbExample("She has preserved.", "وہ محفوظ رکھنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"preserve\" with I, you, we, and they. Use \"preserves\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “preserve” اور “He, she, it” کے ساتھ “preserves” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_356_press",
            baseForm = "press",
            thirdPerson = "presses",
            presentParticiple = "pressing",
            pastSimple = "pressed",
            pastParticiple = "pressed",
            urduMeaning = "دبانا",
            examples = listOf(
                DailyVerbExample("Press button.", "بٹن دبائیں۔"),
                DailyVerbExample("He presses every day.", "وہ ہر روز دبانا کرتا ہے۔"),
                DailyVerbExample("They are pressing now.", "وہ ابھی دبانا کر رہے ہیں۔"),
                DailyVerbExample("We pressed yesterday.", "ہم نے کل دبانا کیا۔"),
                DailyVerbExample("She has pressed.", "وہ دبانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"press\" with I, you, we, and they. Use \"presses\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “press” اور “He, she, it” کے ساتھ “presses” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_357_pretend",
            baseForm = "pretend",
            thirdPerson = "pretends",
            presentParticiple = "pretending",
            pastSimple = "pretended",
            pastParticiple = "pretended",
            urduMeaning = "ڈھونگ رچانا",
            examples = listOf(
                DailyVerbExample("Don't pretend.", "ڈھونگ نہ رچائیں۔"),
                DailyVerbExample("He pretends every day.", "وہ ہر روز ڈھونگ رچانا کرتا ہے۔"),
                DailyVerbExample("They are pretending now.", "وہ ابھی ڈھونگ رچانا کر رہے ہیں۔"),
                DailyVerbExample("We pretended yesterday.", "ہم نے کل ڈھونگ رچانا کیا۔"),
                DailyVerbExample("She has pretended.", "وہ ڈھونگ رچانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"pretend\" with I, you, we, and they. Use \"pretends\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “pretend” اور “He, she, it” کے ساتھ “pretends” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_358_prevent",
            baseForm = "prevent",
            thirdPerson = "prevents",
            presentParticiple = "preventing",
            pastSimple = "prevented",
            pastParticiple = "prevented",
            urduMeaning = "روکنا",
            examples = listOf(
                DailyVerbExample("Prevent diseases.", "بیماریوں کو روکیں۔"),
                DailyVerbExample("He prevents every day.", "وہ ہر روز روکنا کرتا ہے۔"),
                DailyVerbExample("They are preventing now.", "وہ ابھی روکنا کر رہے ہیں۔"),
                DailyVerbExample("We prevented yesterday.", "ہم نے کل روکنا کیا۔"),
                DailyVerbExample("She has prevented.", "وہ روکنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"prevent\" with I, you, we, and they. Use \"prevents\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “prevent” اور “He, she, it” کے ساتھ “prevents” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_359_print",
            baseForm = "print",
            thirdPerson = "prints",
            presentParticiple = "printing",
            pastSimple = "printed",
            pastParticiple = "printed",
            urduMeaning = "چھاپنا",
            examples = listOf(
                DailyVerbExample("Print document.", "دستاویز چھاپیں۔"),
                DailyVerbExample("He prints every day.", "وہ ہر روز چھاپنا کرتا ہے۔"),
                DailyVerbExample("They are printing now.", "وہ ابھی چھاپنا کر رہے ہیں۔"),
                DailyVerbExample("We printed yesterday.", "ہم نے کل چھاپنا کیا۔"),
                DailyVerbExample("She has printed.", "وہ چھاپنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"print\" with I, you, we, and they. Use \"prints\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “print” اور “He, she, it” کے ساتھ “prints” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_360_produce",
            baseForm = "produce",
            thirdPerson = "produces",
            presentParticiple = "producing",
            pastSimple = "produced",
            pastParticiple = "produced",
            urduMeaning = "پیدا کرنا",
            examples = listOf(
                DailyVerbExample("Farms produce food.", "کھیت خوراک پیدا کرتے ہیں۔"),
                DailyVerbExample("He produces every day.", "وہ ہر روز پیدا کرنا کرتا ہے۔"),
                DailyVerbExample("They are producing now.", "وہ ابھی پیدا کرنا کر رہے ہیں۔"),
                DailyVerbExample("We produced yesterday.", "ہم نے کل پیدا کرنا کیا۔"),
                DailyVerbExample("She has produced.", "وہ پیدا کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"produce\" with I, you, we, and they. Use \"produces\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “produce” اور “He, she, it” کے ساتھ “produces” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_361_promise",
            baseForm = "promise",
            thirdPerson = "promises",
            presentParticiple = "promising",
            pastSimple = "promised",
            pastParticiple = "promised",
            urduMeaning = "وعدہ کرنا",
            examples = listOf(
                DailyVerbExample("Keep your promise.", "اپنا وعدہ پورا کریں۔"),
                DailyVerbExample("He promises every day.", "وہ ہر روز وعدہ کرنا کرتا ہے۔"),
                DailyVerbExample("They are promising now.", "وہ ابھی وعدہ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We promised yesterday.", "ہم نے کل وعدہ کرنا کیا۔"),
                DailyVerbExample("She has promised.", "وہ وعدہ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"promise\" with I, you, we, and they. Use \"promises\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “promise” اور “He, she, it” کے ساتھ “promises” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_362_protest",
            baseForm = "protest",
            thirdPerson = "protests",
            presentParticiple = "protesting",
            pastSimple = "protested",
            pastParticiple = "protested",
            urduMeaning = "احتجاج کرنا",
            examples = listOf(
                DailyVerbExample("Protest peacefully.", "پرامن احتجاج کریں۔"),
                DailyVerbExample("He protests every day.", "وہ ہر روز احتجاج کرنا کرتا ہے۔"),
                DailyVerbExample("They are protesting now.", "وہ ابھی احتجاج کرنا کر رہے ہیں۔"),
                DailyVerbExample("We protested yesterday.", "ہم نے کل احتجاج کرنا کیا۔"),
                DailyVerbExample("She has protested.", "وہ احتجاج کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"protest\" with I, you, we, and they. Use \"protests\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “protest” اور “He, she, it” کے ساتھ “protests” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_363_prove",
            baseForm = "prove",
            thirdPerson = "proves",
            presentParticiple = "proving",
            pastSimple = "proved",
            pastParticiple = "proven",
            urduMeaning = "ثابت کرنا",
            examples = listOf(
                DailyVerbExample("Prove true.", "سچ ثابت کریں۔"),
                DailyVerbExample("He proves every day.", "وہ ہر روز ثابت کرنا کرتا ہے۔"),
                DailyVerbExample("They are proving now.", "وہ ابھی ثابت کرنا کر رہے ہیں۔"),
                DailyVerbExample("We proved yesterday.", "ہم نے کل ثابت کرنا کیا۔"),
                DailyVerbExample("She has proven.", "وہ ثابت کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"prove\" with I, you, we, and they. Use \"proves\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “prove” اور “He, she, it” کے ساتھ “proves” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_364_publish",
            baseForm = "publish",
            thirdPerson = "publishes",
            presentParticiple = "publishing",
            pastSimple = "published",
            pastParticiple = "published",
            urduMeaning = "شائع کرنا",
            examples = listOf(
                DailyVerbExample("Publish book.", "کتاب شائع کریں۔"),
                DailyVerbExample("He publishes every day.", "وہ ہر روز شائع کرنا کرتا ہے۔"),
                DailyVerbExample("They are publishing now.", "وہ ابھی شائع کرنا کر رہے ہیں۔"),
                DailyVerbExample("We published yesterday.", "ہم نے کل شائع کرنا کیا۔"),
                DailyVerbExample("She has published.", "وہ شائع کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"publish\" with I, you, we, and they. Use \"publishes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “publish” اور “He, she, it” کے ساتھ “publishes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_365_punish",
            baseForm = "punish",
            thirdPerson = "punishes",
            presentParticiple = "punishing",
            pastSimple = "punished",
            pastParticiple = "punished",
            urduMeaning = "سزا دینا",
            examples = listOf(
                DailyVerbExample("Punish crime.", "جرم کی سزا دیں۔"),
                DailyVerbExample("He punishes every day.", "وہ ہر روز سزا دینا کرتا ہے۔"),
                DailyVerbExample("They are punishing now.", "وہ ابھی سزا دینا کر رہے ہیں۔"),
                DailyVerbExample("We punished yesterday.", "ہم نے کل سزا دینا کیا۔"),
                DailyVerbExample("She has punished.", "وہ سزا دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"punish\" with I, you, we, and they. Use \"punishes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “punish” اور “He, she, it” کے ساتھ “punishes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_366_pursue",
            baseForm = "pursue",
            thirdPerson = "pursues",
            presentParticiple = "pursuing",
            pastSimple = "pursued",
            pastParticiple = "pursued",
            urduMeaning = "تعاقب کرنا",
            examples = listOf(
                DailyVerbExample("Pursue dreams.", "خوابوں کا تعاقب کریں۔"),
                DailyVerbExample("He pursues every day.", "وہ ہر روز تعاقب کرنا کرتا ہے۔"),
                DailyVerbExample("They are pursuing now.", "وہ ابھی تعاقب کرنا کر رہے ہیں۔"),
                DailyVerbExample("We pursued yesterday.", "ہم نے کل تعاقب کرنا کیا۔"),
                DailyVerbExample("She has pursued.", "وہ تعاقب کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"pursue\" with I, you, we, and they. Use \"pursues\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “pursue” اور “He, she, it” کے ساتھ “pursues” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_367_quit",
            baseForm = "quit",
            thirdPerson = "quits",
            presentParticiple = "quitting",
            pastSimple = "quit",
            pastParticiple = "quit",
            urduMeaning = "چھوڑنا",
            examples = listOf(
                DailyVerbExample("Quit smoking.", "تمباکو نوشی چھوڑ دیں۔"),
                DailyVerbExample("He quits every day.", "وہ ہر روز چھوڑنا کرتا ہے۔"),
                DailyVerbExample("They are quitting now.", "وہ ابھی چھوڑنا کر رہے ہیں۔"),
                DailyVerbExample("We quit yesterday.", "ہم نے کل چھوڑنا کیا۔"),
                DailyVerbExample("She has quit.", "وہ چھوڑنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"quit\" with I, you, we, and they. Use \"quits\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “quit” اور “He, she, it” کے ساتھ “quits” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_368_quote",
            baseForm = "quote",
            thirdPerson = "quotes",
            presentParticiple = "quoting",
            pastSimple = "quoted",
            pastParticiple = "quoted",
            urduMeaning = "حوالہ دینا",
            examples = listOf(
                DailyVerbExample("Quote verse.", "آیت کا حوالہ دیں۔"),
                DailyVerbExample("He quotes every day.", "وہ ہر روز حوالہ دینا کرتا ہے۔"),
                DailyVerbExample("They are quoting now.", "وہ ابھی حوالہ دینا کر رہے ہیں۔"),
                DailyVerbExample("We quoted yesterday.", "ہم نے کل حوالہ دینا کیا۔"),
                DailyVerbExample("She has quoted.", "وہ حوالہ دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"quote\" with I, you, we, and they. Use \"quotes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “quote” اور “He, she, it” کے ساتھ “quotes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_369_react",
            baseForm = "react",
            thirdPerson = "reacts",
            presentParticiple = "reacting",
            pastSimple = "reacted",
            pastParticiple = "reacted",
            urduMeaning = "ردعمل دینا",
            examples = listOf(
                DailyVerbExample("React calmly.", "پرسکون ردعمل دیں۔"),
                DailyVerbExample("He reacts every day.", "وہ ہر روز ردعمل دینا کرتا ہے۔"),
                DailyVerbExample("They are reacting now.", "وہ ابھی ردعمل دینا کر رہے ہیں۔"),
                DailyVerbExample("We reacted yesterday.", "ہم نے کل ردعمل دینا کیا۔"),
                DailyVerbExample("She has reacted.", "وہ ردعمل دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"react\" with I, you, we, and they. Use \"reacts\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “react” اور “He, she, it” کے ساتھ “reacts” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_370_receive",
            baseForm = "receive",
            thirdPerson = "receives",
            presentParticiple = "receiving",
            pastSimple = "received",
            pastParticiple = "received",
            urduMeaning = "وصول کرنا",
            examples = listOf(
                DailyVerbExample("Receive gift.", "تحفہ وصول کریں۔"),
                DailyVerbExample("He receives every day.", "وہ ہر روز وصول کرنا کرتا ہے۔"),
                DailyVerbExample("They are receiving now.", "وہ ابھی وصول کرنا کر رہے ہیں۔"),
                DailyVerbExample("We received yesterday.", "ہم نے کل وصول کرنا کیا۔"),
                DailyVerbExample("She has received.", "وہ وصول کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"receive\" with I, you, we, and they. Use \"receives\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “receive” اور “He, she, it” کے ساتھ “receives” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_371_recognize",
            baseForm = "recognize",
            thirdPerson = "recognizes",
            presentParticiple = "recognizing",
            pastSimple = "recognized",
            pastParticiple = "recognized",
            urduMeaning = "پہچاننا",
            examples = listOf(
                DailyVerbExample("Recognize face.", "چہرہ پہچانیں۔"),
                DailyVerbExample("He recognizes every day.", "وہ ہر روز پہچاننا کرتا ہے۔"),
                DailyVerbExample("They are recognizing now.", "وہ ابھی پہچاننا کر رہے ہیں۔"),
                DailyVerbExample("We recognized yesterday.", "ہم نے کل پہچاننا کیا۔"),
                DailyVerbExample("She has recognized.", "وہ پہچاننا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"recognize\" with I, you, we, and they. Use \"recognizes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “recognize” اور “He, she, it” کے ساتھ “recognizes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_372_recommend",
            baseForm = "recommend",
            thirdPerson = "recommends",
            presentParticiple = "recommending",
            pastSimple = "recommended",
            pastParticiple = "recommended",
            urduMeaning = "سفارش کرنا",
            examples = listOf(
                DailyVerbExample("Recommend book.", "کتاب کی سفارش کریں۔"),
                DailyVerbExample("He recommends every day.", "وہ ہر روز سفارش کرنا کرتا ہے۔"),
                DailyVerbExample("They are recommending now.", "وہ ابھی سفارش کرنا کر رہے ہیں۔"),
                DailyVerbExample("We recommended yesterday.", "ہم نے کل سفارش کرنا کیا۔"),
                DailyVerbExample("She has recommended.", "وہ سفارش کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"recommend\" with I, you, we, and they. Use \"recommends\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “recommend” اور “He, she, it” کے ساتھ “recommends” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_373_record",
            baseForm = "record",
            thirdPerson = "records",
            presentParticiple = "recording",
            pastSimple = "recorded",
            pastParticiple = "recorded",
            urduMeaning = "ریکارڈ کرنا",
            examples = listOf(
                DailyVerbExample("Record audio.", "آڈیو ریکارڈ کریں۔"),
                DailyVerbExample("He records every day.", "وہ ہر روز ریکارڈ کرنا کرتا ہے۔"),
                DailyVerbExample("They are recording now.", "وہ ابھی ریکارڈ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We recorded yesterday.", "ہم نے کل ریکارڈ کرنا کیا۔"),
                DailyVerbExample("She has recorded.", "وہ ریکارڈ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"record\" with I, you, we, and they. Use \"records\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “record” اور “He, she, it” کے ساتھ “records” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_374_recover",
            baseForm = "recover",
            thirdPerson = "recovers",
            presentParticiple = "recovering",
            pastSimple = "recovered",
            pastParticiple = "recovered",
            urduMeaning = "صحت یاب ہونا",
            examples = listOf(
                DailyVerbExample("Recover soon.", "جلد صحت یاب ہوں۔"),
                DailyVerbExample("He recovers every day.", "وہ ہر روز صحت یاب ہونا کرتا ہے۔"),
                DailyVerbExample("They are recovering now.", "وہ ابھی صحت یاب ہونا کر رہے ہیں۔"),
                DailyVerbExample("We recovered yesterday.", "ہم نے کل صحت یاب ہونا کیا۔"),
                DailyVerbExample("She has recovered.", "وہ صحت یاب ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"recover\" with I, you, we, and they. Use \"recovers\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “recover” اور “He, she, it” کے ساتھ “recovers” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_375_reduce",
            baseForm = "reduce",
            thirdPerson = "reduces",
            presentParticiple = "reducing",
            pastSimple = "reduced",
            pastParticiple = "reduced",
            urduMeaning = "کم کرنا",
            examples = listOf(
                DailyVerbExample("Reduce stress.", "تناؤ کم کریں۔"),
                DailyVerbExample("He reduces every day.", "وہ ہر روز کم کرنا کرتا ہے۔"),
                DailyVerbExample("They are reducing now.", "وہ ابھی کم کرنا کر رہے ہیں۔"),
                DailyVerbExample("We reduced yesterday.", "ہم نے کل کم کرنا کیا۔"),
                DailyVerbExample("She has reduced.", "وہ کم کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"reduce\" with I, you, we, and they. Use \"reduces\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “reduce” اور “He, she, it” کے ساتھ “reduces” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_376_reflect",
            baseForm = "reflect",
            thirdPerson = "reflects",
            presentParticiple = "reflecting",
            pastSimple = "reflected",
            pastParticiple = "reflected",
            urduMeaning = "عکاسی کرنا",
            examples = listOf(
                DailyVerbExample("Mirror reflects light.", "آئینہ روشنی کی عکاسی کرتا ہے۔"),
                DailyVerbExample("He reflects every day.", "وہ ہر روز عکاسی کرنا کرتا ہے۔"),
                DailyVerbExample("They are reflecting now.", "وہ ابھی عکاسی کرنا کر رہے ہیں۔"),
                DailyVerbExample("We reflected yesterday.", "ہم نے کل عکاسی کرنا کیا۔"),
                DailyVerbExample("She has reflected.", "وہ عکاسی کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"reflect\" with I, you, we, and they. Use \"reflects\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “reflect” اور “He, she, it” کے ساتھ “reflects” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_377_regret",
            baseForm = "regret",
            thirdPerson = "regrets",
            presentParticiple = "regretting",
            pastSimple = "regretted",
            pastParticiple = "regretted",
            urduMeaning = "پچھتانا",
            examples = listOf(
                DailyVerbExample("Don't regret past.", "ماضی پر مت پچھتائیں۔"),
                DailyVerbExample("He regrets every day.", "وہ ہر روز پچھتانا کرتا ہے۔"),
                DailyVerbExample("They are regretting now.", "وہ ابھی پچھتانا کر رہے ہیں۔"),
                DailyVerbExample("We regretted yesterday.", "ہم نے کل پچھتانا کیا۔"),
                DailyVerbExample("She has regretted.", "وہ پچھتانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"regret\" with I, you, we, and they. Use \"regrets\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “regret” اور “He, she, it” کے ساتھ “regrets” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_378_reject",
            baseForm = "reject",
            thirdPerson = "rejects",
            presentParticiple = "rejecting",
            pastSimple = "rejected",
            pastParticiple = "rejected",
            urduMeaning = "مسترد کرنا",
            examples = listOf(
                DailyVerbExample("Reject offer.", "پیشکش مسترد کریں۔"),
                DailyVerbExample("He rejects every day.", "وہ ہر روز مسترد کرنا کرتا ہے۔"),
                DailyVerbExample("They are rejecting now.", "وہ ابھی مسترد کرنا کر رہے ہیں۔"),
                DailyVerbExample("We rejected yesterday.", "ہم نے کل مسترد کرنا کیا۔"),
                DailyVerbExample("She has rejected.", "وہ مسترد کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"reject\" with I, you, we, and they. Use \"rejects\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “reject” اور “He, she, it” کے ساتھ “rejects” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_379_relax",
            baseForm = "relax",
            thirdPerson = "relaxes",
            presentParticiple = "relaxing",
            pastSimple = "relaxed",
            pastParticiple = "relaxed",
            urduMeaning = "پرآرام ہونا",
            examples = listOf(
                DailyVerbExample("Relax at home.", "گھر پر آرام کریں۔"),
                DailyVerbExample("He relaxes every day.", "وہ ہر روز پرآرام ہونا کرتا ہے۔"),
                DailyVerbExample("They are relaxing now.", "وہ ابھی پرآرام ہونا کر رہے ہیں۔"),
                DailyVerbExample("We relaxed yesterday.", "ہم نے کل پرآرام ہونا کیا۔"),
                DailyVerbExample("She has relaxed.", "وہ پرآرام ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"relax\" with I, you, we, and they. Use \"relaxes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “relax” اور “He, she, it” کے ساتھ “relaxes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_380_release",
            baseForm = "release",
            thirdPerson = "releases",
            presentParticiple = "releasing",
            pastSimple = "released",
            pastParticiple = "released",
            urduMeaning = "آزاد کرنا",
            examples = listOf(
                DailyVerbExample("Release birds.", "پرندوں کو آزاد کریں۔"),
                DailyVerbExample("He releases every day.", "وہ ہر روز آزاد کرنا کرتا ہے۔"),
                DailyVerbExample("They are releasing now.", "وہ ابھی آزاد کرنا کر رہے ہیں۔"),
                DailyVerbExample("We released yesterday.", "ہم نے کل آزاد کرنا کیا۔"),
                DailyVerbExample("She has released.", "وہ آزاد کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"release\" with I, you, we, and they. Use \"releases\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “release” اور “He, she, it” کے ساتھ “releases” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_381_rely",
            baseForm = "rely",
            thirdPerson = "relies",
            presentParticiple = "relying",
            pastSimple = "relied",
            pastParticiple = "relied",
            urduMeaning = "انحصار کرنا",
            examples = listOf(
                DailyVerbExample("Rely on truth.", "سچ پر انحصار کریں۔"),
                DailyVerbExample("He relies every day.", "وہ ہر روز انحصار کرنا کرتا ہے۔"),
                DailyVerbExample("They are relying now.", "وہ ابھی انحصار کرنا کر رہے ہیں۔"),
                DailyVerbExample("We relied yesterday.", "ہم نے کل انحصار کرنا کیا۔"),
                DailyVerbExample("She has relied.", "وہ انحصار کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"rely\" with I, you, we, and they. Use \"relies\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “rely” اور “He, she, it” کے ساتھ “relies” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_382_remove",
            baseForm = "remove",
            thirdPerson = "removes",
            presentParticiple = "removing",
            pastSimple = "removed",
            pastParticiple = "removed",
            urduMeaning = "ہٹانا",
            examples = listOf(
                DailyVerbExample("Remove shoes.", "جوتے اتاریں۔"),
                DailyVerbExample("He removes every day.", "وہ ہر روز ہٹانا کرتا ہے۔"),
                DailyVerbExample("They are removing now.", "وہ ابھی ہٹانا کر رہے ہیں۔"),
                DailyVerbExample("We removed yesterday.", "ہم نے کل ہٹانا کیا۔"),
                DailyVerbExample("She has removed.", "وہ ہٹانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"remove\" with I, you, we, and they. Use \"removes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “remove” اور “He, she, it” کے ساتھ “removes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_383_renew",
            baseForm = "renew",
            thirdPerson = "renews",
            presentParticiple = "renewing",
            pastSimple = "renewed",
            pastParticiple = "renewed",
            urduMeaning = "تجدید کرنا",
            examples = listOf(
                DailyVerbExample("Renew passport.", "پاسپورٹ کی تجدید کروائیں۔"),
                DailyVerbExample("He renews every day.", "وہ ہر روز تجدید کرنا کرتا ہے۔"),
                DailyVerbExample("They are renewing now.", "وہ ابھی تجدید کرنا کر رہے ہیں۔"),
                DailyVerbExample("We renewed yesterday.", "ہم نے کل تجدید کرنا کیا۔"),
                DailyVerbExample("She has renewed.", "وہ تجدید کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"renew\" with I, you, we, and they. Use \"renews\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “renew” اور “He, she, it” کے ساتھ “renews” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_384_repeat",
            baseForm = "repeat",
            thirdPerson = "repeats",
            presentParticiple = "repeating",
            pastSimple = "repeated",
            pastParticiple = "repeated",
            urduMeaning = "دہرانا",
            examples = listOf(
                DailyVerbExample("Repeat words.", "الفاظ دہرائیں۔"),
                DailyVerbExample("He repeats every day.", "وہ ہر روز دہرانا کرتا ہے۔"),
                DailyVerbExample("They are repeating now.", "وہ ابھی دہرانا کر رہے ہیں۔"),
                DailyVerbExample("We repeated yesterday.", "ہم نے کل دہرانا کیا۔"),
                DailyVerbExample("She has repeated.", "وہ دہرانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"repeat\" with I, you, we, and they. Use \"repeats\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “repeat” اور “He, she, it” کے ساتھ “repeats” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_385_replace",
            baseForm = "replace",
            thirdPerson = "replaces",
            presentParticiple = "replacing",
            pastSimple = "replaced",
            pastParticiple = "replaced",
            urduMeaning = "بدلنا",
            examples = listOf(
                DailyVerbExample("Replace part.", "حصہ تبدیل کریں۔"),
                DailyVerbExample("He replaces every day.", "وہ ہر روز بدلنا کرتا ہے۔"),
                DailyVerbExample("They are replacing now.", "وہ ابھی بدلنا کر رہے ہیں۔"),
                DailyVerbExample("We replaced yesterday.", "ہم نے کل بدلنا کیا۔"),
                DailyVerbExample("She has replaced.", "وہ بدلنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"replace\" with I, you, we, and they. Use \"replaces\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “replace” اور “He, she, it” کے ساتھ “replaces” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_386_reply",
            baseForm = "reply",
            thirdPerson = "replies",
            presentParticiple = "replying",
            pastSimple = "replied",
            pastParticiple = "replied",
            urduMeaning = "جواب دینا",
            examples = listOf(
                DailyVerbExample("Reply promptly.", "فوری جواب دیں۔"),
                DailyVerbExample("He replies every day.", "وہ ہر روز جواب دینا کرتا ہے۔"),
                DailyVerbExample("They are replying now.", "وہ ابھی جواب دینا کر رہے ہیں۔"),
                DailyVerbExample("We replied yesterday.", "ہم نے کل جواب دینا کیا۔"),
                DailyVerbExample("She has replied.", "وہ جواب دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"reply\" with I, you, we, and they. Use \"replies\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “reply” اور “He, she, it” کے ساتھ “replies” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_387_rescue",
            baseForm = "rescue",
            thirdPerson = "rescues",
            presentParticiple = "rescuing",
            pastSimple = "rescued",
            pastParticiple = "rescued",
            urduMeaning = "بچانا",
            examples = listOf(
                DailyVerbExample("Rescue animals.", "جانوروں کو بچائیں۔"),
                DailyVerbExample("He rescues every day.", "وہ ہر روز بچانا کرتا ہے۔"),
                DailyVerbExample("They are rescuing now.", "وہ ابھی بچانا کر رہے ہیں۔"),
                DailyVerbExample("We rescued yesterday.", "ہم نے کل بچانا کیا۔"),
                DailyVerbExample("She has rescued.", "وہ بچانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"rescue\" with I, you, we, and they. Use \"rescues\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “rescue” اور “He, she, it” کے ساتھ “rescues” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_388_research",
            baseForm = "research",
            thirdPerson = "researches",
            presentParticiple = "researching",
            pastSimple = "researched",
            pastParticiple = "researched",
            urduMeaning = "تحقیق کرنا",
            examples = listOf(
                DailyVerbExample("Research topic.", "موضوع پر تحقیق کریں۔"),
                DailyVerbExample("He researches every day.", "وہ ہر روز تحقیق کرنا کرتا ہے۔"),
                DailyVerbExample("They are researching now.", "وہ ابھی تحقیق کرنا کر رہے ہیں۔"),
                DailyVerbExample("We researched yesterday.", "ہم نے کل تحقیق کرنا کیا۔"),
                DailyVerbExample("She has researched.", "وہ تحقیق کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"research\" with I, you, we, and they. Use \"researches\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “research” اور “He, she, it” کے ساتھ “researches” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_389_resist",
            baseForm = "resist",
            thirdPerson = "resists",
            presentParticiple = "resisting",
            pastSimple = "resisted",
            pastParticiple = "resisted",
            urduMeaning = "مزاحمت کرنا",
            examples = listOf(
                DailyVerbExample("Resist temptation.", "ترغیب کی مزاحمت کریں۔"),
                DailyVerbExample("He resists every day.", "وہ ہر روز مزاحمت کرنا کرتا ہے۔"),
                DailyVerbExample("They are resisting now.", "وہ ابھی مزاحمت کرنا کر رہے ہیں۔"),
                DailyVerbExample("We resisted yesterday.", "ہم نے کل مزاحمت کرنا کیا۔"),
                DailyVerbExample("She has resisted.", "وہ مزاحمت کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"resist\" with I, you, we, and they. Use \"resists\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “resist” اور “He, she, it” کے ساتھ “resists” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_390_respond",
            baseForm = "respond",
            thirdPerson = "responds",
            presentParticiple = "responding",
            pastSimple = "responded",
            pastParticiple = "responded",
            urduMeaning = "ردعمل دینا",
            examples = listOf(
                DailyVerbExample("Respond politely.", "شائستگی سے جواب دیں۔"),
                DailyVerbExample("He responds every day.", "وہ ہر روز ردعمل دینا کرتا ہے۔"),
                DailyVerbExample("They are responding now.", "وہ ابھی ردعمل دینا کر رہے ہیں۔"),
                DailyVerbExample("We responded yesterday.", "ہم نے کل ردعمل دینا کیا۔"),
                DailyVerbExample("She has responded.", "وہ ردعمل دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"respond\" with I, you, we, and they. Use \"responds\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “respond” اور “He, she, it” کے ساتھ “responds” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_391_restore",
            baseForm = "restore",
            thirdPerson = "restores",
            presentParticiple = "restoring",
            pastSimple = "restored",
            pastParticiple = "restored",
            urduMeaning = "بحال کرنا",
            examples = listOf(
                DailyVerbExample("Restore peace.", "امن بحال کریں۔"),
                DailyVerbExample("He restores every day.", "وہ ہر روز بحال کرنا کرتا ہے۔"),
                DailyVerbExample("They are restoring now.", "وہ ابھی بحال کرنا کر رہے ہیں۔"),
                DailyVerbExample("We restored yesterday.", "ہم نے کل بحال کرنا کیا۔"),
                DailyVerbExample("She has restored.", "وہ بحال کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"restore\" with I, you, we, and they. Use \"restores\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “restore” اور “He, she, it” کے ساتھ “restores” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_392_retire",
            baseForm = "retire",
            thirdPerson = "retires",
            presentParticiple = "retiring",
            pastSimple = "retired",
            pastParticiple = "retired",
            urduMeaning = "ریٹائر ہونا",
            examples = listOf(
                DailyVerbExample("Retire happily.", "خوشی سے ریٹائر ہوں۔"),
                DailyVerbExample("He retires every day.", "وہ ہر روز ریٹائر ہونا کرتا ہے۔"),
                DailyVerbExample("They are retiring now.", "وہ ابھی ریٹائر ہونا کر رہے ہیں۔"),
                DailyVerbExample("We retired yesterday.", "ہم نے کل ریٹائر ہونا کیا۔"),
                DailyVerbExample("She has retired.", "وہ ریٹائر ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"retire\" with I, you, we, and they. Use \"retires\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “retire” اور “He, she, it” کے ساتھ “retires” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_393_reveal",
            baseForm = "reveal",
            thirdPerson = "reveals",
            presentParticiple = "revealing",
            pastSimple = "revealed",
            pastParticiple = "revealed",
            urduMeaning = "افشا کرنا",
            examples = listOf(
                DailyVerbExample("Reveal truth.", "سچ افشا کریں۔"),
                DailyVerbExample("He reveals every day.", "وہ ہر روز افشا کرنا کرتا ہے۔"),
                DailyVerbExample("They are revealing now.", "وہ ابھی افشا کرنا کر رہے ہیں۔"),
                DailyVerbExample("We revealed yesterday.", "ہم نے کل افشا کرنا کیا۔"),
                DailyVerbExample("She has revealed.", "وہ افشا کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"reveal\" with I, you, we, and they. Use \"reveals\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “reveal” اور “He, she, it” کے ساتھ “reveals” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_394_review",
            baseForm = "review",
            thirdPerson = "reviews",
            presentParticiple = "reviewing",
            pastSimple = "reviewed",
            pastParticiple = "reviewed",
            urduMeaning = "نظر ثانی کرنا",
            examples = listOf(
                DailyVerbExample("Review notes.", "نوٹس پر نظر ثانی کریں۔"),
                DailyVerbExample("He reviews every day.", "وہ ہر روز نظر ثانی کرنا کرتا ہے۔"),
                DailyVerbExample("They are reviewing now.", "وہ ابھی نظر ثانی کرنا کر رہے ہیں۔"),
                DailyVerbExample("We reviewed yesterday.", "ہم نے کل نظر ثانی کرنا کیا۔"),
                DailyVerbExample("She has reviewed.", "وہ نظر ثانی کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"review\" with I, you, we, and they. Use \"reviews\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “review” اور “He, she, it” کے ساتھ “reviews” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_395_reward",
            baseForm = "reward",
            thirdPerson = "rewards",
            presentParticiple = "rewarding",
            pastSimple = "rewarded",
            pastParticiple = "rewarded",
            urduMeaning = "انعام دینا",
            examples = listOf(
                DailyVerbExample("Reward virtue.", "نیکی کا انعام دیں۔"),
                DailyVerbExample("He rewards every day.", "وہ ہر روز انعام دینا کرتا ہے۔"),
                DailyVerbExample("They are rewarding now.", "وہ ابھی انعام دینا کر رہے ہیں۔"),
                DailyVerbExample("We rewarded yesterday.", "ہم نے کل انعام دینا کیا۔"),
                DailyVerbExample("She has rewarded.", "وہ انعام دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"reward\" with I, you, we, and they. Use \"rewards\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “reward” اور “He, she, it” کے ساتھ “rewards” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_396_ride",
            baseForm = "ride",
            thirdPerson = "rides",
            presentParticiple = "riding",
            pastSimple = "rode",
            pastParticiple = "ridden",
            urduMeaning = "سواری کرنا",
            examples = listOf(
                DailyVerbExample("Ride horse.", "گھوڑے کی سواری کریں۔"),
                DailyVerbExample("He rides every day.", "وہ ہر روز سواری کرنا کرتا ہے۔"),
                DailyVerbExample("They are riding now.", "وہ ابھی سواری کرنا کر رہے ہیں۔"),
                DailyVerbExample("We rode yesterday.", "ہم نے کل سواری کرنا کیا۔"),
                DailyVerbExample("She has ridden.", "وہ سواری کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"ride\" with I, you, we, and they. Use \"rides\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “ride” اور “He, she, it” کے ساتھ “rides” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_397_ring",
            baseForm = "ring",
            thirdPerson = "rings",
            presentParticiple = "ringing",
            pastSimple = "rang",
            pastParticiple = "rung",
            urduMeaning = "بجنا",
            examples = listOf(
                DailyVerbExample("Bell rings.", "گھنٹی بجتی ہے۔"),
                DailyVerbExample("He rings every day.", "وہ ہر روز بجنا کرتا ہے۔"),
                DailyVerbExample("They are ringing now.", "وہ ابھی بجنا کر رہے ہیں۔"),
                DailyVerbExample("We rang yesterday.", "ہم نے کل بجنا کیا۔"),
                DailyVerbExample("She has rung.", "وہ بجنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"ring\" with I, you, we, and they. Use \"rings\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “ring” اور “He, she, it” کے ساتھ “rings” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_398_rise",
            baseForm = "rise",
            thirdPerson = "rises",
            presentParticiple = "rising",
            pastSimple = "rose",
            pastParticiple = "risen",
            urduMeaning = "طلوع ہونا",
            examples = listOf(
                DailyVerbExample("Sun rises east.", "سورج مشرق سے طلوع ہوتا ہے۔"),
                DailyVerbExample("He rises every day.", "وہ ہر روز طلوع ہونا کرتا ہے۔"),
                DailyVerbExample("They are rising now.", "وہ ابھی طلوع ہونا کر رہے ہیں۔"),
                DailyVerbExample("We rose yesterday.", "ہم نے کل طلوع ہونا کیا۔"),
                DailyVerbExample("She has risen.", "وہ طلوع ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"rise\" with I, you, we, and they. Use \"rises\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “rise” اور “He, she, it” کے ساتھ “rises” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_399_risk",
            baseForm = "risk",
            thirdPerson = "risks",
            presentParticiple = "risking",
            pastSimple = "risked",
            pastParticiple = "risked",
            urduMeaning = "خاطرہ مول لینا",
            examples = listOf(
                DailyVerbExample("Don't risk health.", "صحت کا خطرہ نہ لیں۔"),
                DailyVerbExample("He risks every day.", "وہ ہر روز خاطرہ مول لینا کرتا ہے۔"),
                DailyVerbExample("They are risking now.", "وہ ابھی خاطرہ مول لینا کر رہے ہیں۔"),
                DailyVerbExample("We risked yesterday.", "ہم نے کل خاطرہ مول لینا کیا۔"),
                DailyVerbExample("She has risked.", "وہ خاطرہ مول لینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"risk\" with I, you, we, and they. Use \"risks\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “risk” اور “He, she, it” کے ساتھ “risks” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_400_roll",
            baseForm = "roll",
            thirdPerson = "rolls",
            presentParticiple = "rolling",
            pastSimple = "rolled",
            pastParticiple = "rolled",
            urduMeaning = "رول کرنا",
            examples = listOf(
                DailyVerbExample("Roll the ball.", "گیند رول کریں۔"),
                DailyVerbExample("He rolls every day.", "وہ ہر روز رول کرنا کرتا ہے۔"),
                DailyVerbExample("They are rolling now.", "وہ ابھی رول کرنا کر رہے ہیں۔"),
                DailyVerbExample("We rolled yesterday.", "ہم نے کل رول کرنا کیا۔"),
                DailyVerbExample("She has rolled.", "وہ رول کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"roll\" with I, you, we, and they. Use \"rolls\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “roll” اور “He, she, it” کے ساتھ “rolls” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_401_rub",
            baseForm = "rub",
            thirdPerson = "rubs",
            presentParticiple = "rubbing",
            pastSimple = "rubbed",
            pastParticiple = "rubbed",
            urduMeaning = "ملنا",
            examples = listOf(
                DailyVerbExample("Rub hands.", "ہاتھ ملیں۔"),
                DailyVerbExample("He rubs every day.", "وہ ہر روز ملنا کرتا ہے۔"),
                DailyVerbExample("They are rubbing now.", "وہ ابھی ملنا کر رہے ہیں۔"),
                DailyVerbExample("We rubbed yesterday.", "ہم نے کل ملنا کیا۔"),
                DailyVerbExample("She has rubbed.", "وہ ملنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"rub\" with I, you, we, and they. Use \"rubs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “rub” اور “He, she, it” کے ساتھ “rubs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_402_rush",
            baseForm = "rush",
            thirdPerson = "rushes",
            presentParticiple = "rushing",
            pastSimple = "rushed",
            pastParticiple = "rushed",
            urduMeaning = "تیزی سے جانا",
            examples = listOf(
                DailyVerbExample("Rush to work.", "کام کے لیے تیزی سے جائیں۔"),
                DailyVerbExample("He rushes every day.", "وہ ہر روز تیزی سے جانا کرتا ہے۔"),
                DailyVerbExample("They are rushing now.", "وہ ابھی تیزی سے جانا کر رہے ہیں۔"),
                DailyVerbExample("We rushed yesterday.", "ہم نے کل تیزی سے جانا کیا۔"),
                DailyVerbExample("She has rushed.", "وہ تیزی سے جانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"rush\" with I, you, we, and they. Use \"rushes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “rush” اور “He, she, it” کے ساتھ “rushes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_403_sail",
            baseForm = "sail",
            thirdPerson = "sails",
            presentParticiple = "sailing",
            pastSimple = "sailed",
            pastParticiple = "sailed",
            urduMeaning = "بادبانی کرنا",
            examples = listOf(
                DailyVerbExample("Sail the sea.", "سمندر پر بادبانی کریں۔"),
                DailyVerbExample("He sails every day.", "وہ ہر روز بادبانی کرنا کرتا ہے۔"),
                DailyVerbExample("They are sailing now.", "وہ ابھی بادبانی کرنا کر رہے ہیں۔"),
                DailyVerbExample("We sailed yesterday.", "ہم نے کل بادبانی کرنا کیا۔"),
                DailyVerbExample("She has sailed.", "وہ بادبانی کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"sail\" with I, you, we, and they. Use \"sails\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “sail” اور “He, she, it” کے ساتھ “sails” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_404_satisfy",
            baseForm = "satisfy",
            thirdPerson = "satisfies",
            presentParticiple = "satisfying",
            pastSimple = "satisfied",
            pastParticiple = "satisfied",
            urduMeaning = "مطمئن کرنا",
            examples = listOf(
                DailyVerbExample("Satisfy customer.", "گاہک کو مطمئن کریں۔"),
                DailyVerbExample("He satisfies every day.", "وہ ہر روز مطمئن کرنا کرتا ہے۔"),
                DailyVerbExample("They are satisfying now.", "وہ ابھی مطمئن کرنا کر رہے ہیں۔"),
                DailyVerbExample("We satisfied yesterday.", "ہم نے کل مطمئن کرنا کیا۔"),
                DailyVerbExample("She has satisfied.", "وہ مطمئن کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"satisfy\" with I, you, we, and they. Use \"satisfies\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “satisfy” اور “He, she, it” کے ساتھ “satisfies” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_405_scare",
            baseForm = "scare",
            thirdPerson = "scares",
            presentParticiple = "scaring",
            pastSimple = "scared",
            pastParticiple = "scared",
            urduMeaning = "ڈرا دینا",
            examples = listOf(
                DailyVerbExample("Ghost story scared.", "بھوت کی کہانی نے ڈرا دیا۔"),
                DailyVerbExample("He scares every day.", "وہ ہر روز ڈرا دینا کرتا ہے۔"),
                DailyVerbExample("They are scaring now.", "وہ ابھی ڈرا دینا کر رہے ہیں۔"),
                DailyVerbExample("We scared yesterday.", "ہم نے کل ڈرا دینا کیا۔"),
                DailyVerbExample("She has scared.", "وہ ڈرا دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"scare\" with I, you, we, and they. Use \"scares\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “scare” اور “He, she, it” کے ساتھ “scares” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_406_schedule",
            baseForm = "schedule",
            thirdPerson = "schedules",
            presentParticiple = "scheduling",
            pastSimple = "scheduled",
            pastParticiple = "scheduled",
            urduMeaning = "شیڈول بنانا",
            examples = listOf(
                DailyVerbExample("Schedule meeting.", "میٹنگ شیڈول کریں۔"),
                DailyVerbExample("He schedules every day.", "وہ ہر روز شیڈول بنانا کرتا ہے۔"),
                DailyVerbExample("They are scheduling now.", "وہ ابھی شیڈول بنانا کر رہے ہیں۔"),
                DailyVerbExample("We scheduled yesterday.", "ہم نے کل شیڈول بنانا کیا۔"),
                DailyVerbExample("She has scheduled.", "وہ شیڈول بنانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"schedule\" with I, you, we, and they. Use \"schedules\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “schedule” اور “He, she, it” کے ساتھ “schedules” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_407_scream",
            baseForm = "scream",
            thirdPerson = "screams",
            presentParticiple = "screaming",
            pastSimple = "screamed",
            pastParticiple = "screamed",
            urduMeaning = "چیخنا",
            examples = listOf(
                DailyVerbExample("Don't scream.", "چیخیں مت۔"),
                DailyVerbExample("He screams every day.", "وہ ہر روز چیخنا کرتا ہے۔"),
                DailyVerbExample("They are screaming now.", "وہ ابھی چیخنا کر رہے ہیں۔"),
                DailyVerbExample("We screamed yesterday.", "ہم نے کل چیخنا کیا۔"),
                DailyVerbExample("She has screamed.", "وہ چیخنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"scream\" with I, you, we, and they. Use \"screams\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “scream” اور “He, she, it” کے ساتھ “screams” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_408_search",
            baseForm = "search",
            thirdPerson = "searches",
            presentParticiple = "searching",
            pastSimple = "searched",
            pastParticiple = "searched",
            urduMeaning = "تلاش کرنا",
            examples = listOf(
                DailyVerbExample("Search online.", "آن لائن تلاش کریں۔"),
                DailyVerbExample("He searches every day.", "وہ ہر روز تلاش کرنا کرتا ہے۔"),
                DailyVerbExample("They are searching now.", "وہ ابھی تلاش کرنا کر رہے ہیں۔"),
                DailyVerbExample("We searched yesterday.", "ہم نے کل تلاش کرنا کیا۔"),
                DailyVerbExample("She has searched.", "وہ تلاش کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"search\" with I, you, we, and they. Use \"searches\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “search” اور “He, she, it” کے ساتھ “searches” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_409_select",
            baseForm = "select",
            thirdPerson = "selects",
            presentParticiple = "selecting",
            pastSimple = "selected",
            pastParticiple = "selected",
            urduMeaning = "منتخب کرنا",
            examples = listOf(
                DailyVerbExample("Select option.", "آپشن منتخب کریں۔"),
                DailyVerbExample("He selects every day.", "وہ ہر روز منتخب کرنا کرتا ہے۔"),
                DailyVerbExample("They are selecting now.", "وہ ابھی منتخب کرنا کر رہے ہیں۔"),
                DailyVerbExample("We selected yesterday.", "ہم نے کل منتخب کرنا کیا۔"),
                DailyVerbExample("She has selected.", "وہ منتخب کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"select\" with I, you, we, and they. Use \"selects\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “select” اور “He, she, it” کے ساتھ “selects” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_410_separate",
            baseForm = "separate",
            thirdPerson = "separates",
            presentParticiple = "separating",
            pastSimple = "separated",
            pastParticiple = "separated",
            urduMeaning = "الگ کرنا",
            examples = listOf(
                DailyVerbExample("Separate items.", "اشیاء الگ کریں۔"),
                DailyVerbExample("He separates every day.", "وہ ہر روز الگ کرنا کرتا ہے۔"),
                DailyVerbExample("They are separating now.", "وہ ابھی الگ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We separated yesterday.", "ہم نے کل الگ کرنا کیا۔"),
                DailyVerbExample("She has separated.", "وہ الگ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"separate\" with I, you, we, and they. Use \"separates\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “separate” اور “He, she, it” کے ساتھ “separates” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_411_settle",
            baseForm = "settle",
            thirdPerson = "settles",
            presentParticiple = "settling",
            pastSimple = "settled",
            pastParticiple = "settled",
            urduMeaning = "آباد ہونا",
            examples = listOf(
                DailyVerbExample("Settle down.", "آباد ہو جائیں۔"),
                DailyVerbExample("He settles every day.", "وہ ہر روز آباد ہونا کرتا ہے۔"),
                DailyVerbExample("They are settling now.", "وہ ابھی آباد ہونا کر رہے ہیں۔"),
                DailyVerbExample("We settled yesterday.", "ہم نے کل آباد ہونا کیا۔"),
                DailyVerbExample("She has settled.", "وہ آباد ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"settle\" with I, you, we, and they. Use \"settles\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “settle” اور “He, she, it” کے ساتھ “settles” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_412_shock",
            baseForm = "shock",
            thirdPerson = "shocks",
            presentParticiple = "shocking",
            pastSimple = "shocked",
            pastParticiple = "shocked",
            urduMeaning = "صدمہ پہنچنا",
            examples = listOf(
                DailyVerbExample("News shocked all.", "خبر نے سب کو صدمہ پہنچایا۔"),
                DailyVerbExample("He shocks every day.", "وہ ہر روز صدمہ پہنچنا کرتا ہے۔"),
                DailyVerbExample("They are shocking now.", "وہ ابھی صدمہ پہنچنا کر رہے ہیں۔"),
                DailyVerbExample("We shocked yesterday.", "ہم نے کل صدمہ پہنچنا کیا۔"),
                DailyVerbExample("She has shocked.", "وہ صدمہ پہنچنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"shock\" with I, you, we, and they. Use \"shocks\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “shock” اور “He, she, it” کے ساتھ “shocks” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_413_shoot",
            baseForm = "shoot",
            thirdPerson = "shoots",
            presentParticiple = "shooting",
            pastSimple = "shot",
            pastParticiple = "shot",
            urduMeaning = "گولی مارنا",
            examples = listOf(
                DailyVerbExample("Shoot target.", "ہدف پر نشانہ لگائیں۔"),
                DailyVerbExample("He shoots every day.", "وہ ہر روز گولی مارنا کرتا ہے۔"),
                DailyVerbExample("They are shooting now.", "وہ ابھی گولی مارنا کر رہے ہیں۔"),
                DailyVerbExample("We shot yesterday.", "ہم نے کل گولی مارنا کیا۔"),
                DailyVerbExample("She has shot.", "وہ گولی مارنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"shoot\" with I, you, we, and they. Use \"shoots\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “shoot” اور “He, she, it” کے ساتھ “shoots” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_414_shout",
            baseForm = "shout",
            thirdPerson = "shouts",
            presentParticiple = "shouting",
            pastSimple = "shouted",
            pastParticiple = "shouted",
            urduMeaning = "چلا کر کہنا",
            examples = listOf(
                DailyVerbExample("Don't shout.", "چلائیں نہیں۔"),
                DailyVerbExample("He shouts every day.", "وہ ہر روز چلا کر کہنا کرتا ہے۔"),
                DailyVerbExample("They are shouting now.", "وہ ابھی چلا کر کہنا کر رہے ہیں۔"),
                DailyVerbExample("We shouted yesterday.", "ہم نے کل چلا کر کہنا کیا۔"),
                DailyVerbExample("She has shouted.", "وہ چلا کر کہنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"shout\" with I, you, we, and they. Use \"shouts\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “shout” اور “He, she, it” کے ساتھ “shouts” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_415_shrink",
            baseForm = "shrink",
            thirdPerson = "shrinks",
            presentParticiple = "shrinking",
            pastSimple = "shrank",
            pastParticiple = "shrunk",
            urduMeaning = "سکڑنا",
            examples = listOf(
                DailyVerbExample("Cotton shrinks.", "سوتی کپڑا سکڑتا ہے۔"),
                DailyVerbExample("He shrinks every day.", "وہ ہر روز سکڑنا کرتا ہے۔"),
                DailyVerbExample("They are shrinking now.", "وہ ابھی سکڑنا کر رہے ہیں۔"),
                DailyVerbExample("We shrank yesterday.", "ہم نے کل سکڑنا کیا۔"),
                DailyVerbExample("She has shrunk.", "وہ سکڑنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"shrink\" with I, you, we, and they. Use \"shrinks\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “shrink” اور “He, she, it” کے ساتھ “shrinks” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_416_shut",
            baseForm = "shut",
            thirdPerson = "shuts",
            presentParticiple = "shutting",
            pastSimple = "shut",
            pastParticiple = "shut",
            urduMeaning = "بند کرنا",
            examples = listOf(
                DailyVerbExample("Shut the door.", "دروازہ بند کریں۔"),
                DailyVerbExample("He shuts every day.", "وہ ہر روز بند کرنا کرتا ہے۔"),
                DailyVerbExample("They are shutting now.", "وہ ابھی بند کرنا کر رہے ہیں۔"),
                DailyVerbExample("We shut yesterday.", "ہم نے کل بند کرنا کیا۔"),
                DailyVerbExample("She has shut.", "وہ بند کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"shut\" with I, you, we, and they. Use \"shuts\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “shut” اور “He, she, it” کے ساتھ “shuts” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_417_sigh",
            baseForm = "sigh",
            thirdPerson = "sighs",
            presentParticiple = "sighing",
            pastSimple = "sighed",
            pastParticiple = "sighed",
            urduMeaning = "آہ بھرنا",
            examples = listOf(
                DailyVerbExample("He sighed softly.", "اس نے دھیمی آہ بھری۔"),
                DailyVerbExample("He sighs every day.", "وہ ہر روز آہ بھرنا کرتا ہے۔"),
                DailyVerbExample("They are sighing now.", "وہ ابھی آہ بھرنا کر رہے ہیں۔"),
                DailyVerbExample("We sighed yesterday.", "ہم نے کل آہ بھرنا کیا۔"),
                DailyVerbExample("She has sighed.", "وہ آہ بھرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"sigh\" with I, you, we, and they. Use \"sighs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “sigh” اور “He, she, it” کے ساتھ “sighs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_418_sign",
            baseForm = "sign",
            thirdPerson = "signs",
            presentParticiple = "signing",
            pastSimple = "signed",
            pastParticiple = "signed",
            urduMeaning = "دستخط کرنا",
            examples = listOf(
                DailyVerbExample("Sign document.", "دستاویز پر دستخط کریں۔"),
                DailyVerbExample("He signs every day.", "وہ ہر روز دستخط کرنا کرتا ہے۔"),
                DailyVerbExample("They are signing now.", "وہ ابھی دستخط کرنا کر رہے ہیں۔"),
                DailyVerbExample("We signed yesterday.", "ہم نے کل دستخط کرنا کیا۔"),
                DailyVerbExample("She has signed.", "وہ دستخط کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"sign\" with I, you, we, and they. Use \"signs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “sign” اور “He, she, it” کے ساتھ “signs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_419_slide",
            baseForm = "slide",
            thirdPerson = "slides",
            presentParticiple = "sliding",
            pastSimple = "slid",
            pastParticiple = "slid",
            urduMeaning = "پھسلنا",
            examples = listOf(
                DailyVerbExample("Slide down hill.", "پہاڑی سے نیچے پھسلیں۔"),
                DailyVerbExample("He slides every day.", "وہ ہر روز پھسلنا کرتا ہے۔"),
                DailyVerbExample("They are sliding now.", "وہ ابھی پھسلنا کر رہے ہیں۔"),
                DailyVerbExample("We slid yesterday.", "ہم نے کل پھسلنا کیا۔"),
                DailyVerbExample("She has slid.", "وہ پھسلنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"slide\" with I, you, we, and they. Use \"slides\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “slide” اور “He, she, it” کے ساتھ “slides” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_420_slip",
            baseForm = "slip",
            thirdPerson = "slips",
            presentParticiple = "slipping",
            pastSimple = "slipped",
            pastParticiple = "slipped",
            urduMeaning = "پھسل جانا",
            examples = listOf(
                DailyVerbExample("Slip on ice.", "برف پر پھسلنا۔"),
                DailyVerbExample("He slips every day.", "وہ ہر روز پھسل جانا کرتا ہے۔"),
                DailyVerbExample("They are slipping now.", "وہ ابھی پھسل جانا کر رہے ہیں۔"),
                DailyVerbExample("We slipped yesterday.", "ہم نے کل پھسل جانا کیا۔"),
                DailyVerbExample("She has slipped.", "وہ پھسل جانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"slip\" with I, you, we, and they. Use \"slips\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “slip” اور “He, she, it” کے ساتھ “slips” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_421_solve",
            baseForm = "solve",
            thirdPerson = "solves",
            presentParticiple = "solving",
            pastSimple = "solved",
            pastParticiple = "solved",
            urduMeaning = "حل کرنا",
            examples = listOf(
                DailyVerbExample("Solve math puzzle.", "ریاضی کی پہیلی حل کریں۔"),
                DailyVerbExample("He solves every day.", "وہ ہر روز حل کرنا کرتا ہے۔"),
                DailyVerbExample("They are solving now.", "وہ ابھی حل کرنا کر رہے ہیں۔"),
                DailyVerbExample("We solved yesterday.", "ہم نے کل حل کرنا کیا۔"),
                DailyVerbExample("She has solved.", "وہ حل کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"solve\" with I, you, we, and they. Use \"solves\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “solve” اور “He, she, it” کے ساتھ “solves” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_422_spell",
            baseForm = "spell",
            thirdPerson = "spells",
            presentParticiple = "spelling",
            pastSimple = "spelt / spelled",
            pastParticiple = "spelt / spelled",
            urduMeaning = "ہجے کرنا",
            examples = listOf(
                DailyVerbExample("Spell your name.", "اپنے نام کے ہجے کریں۔"),
                DailyVerbExample("He spells every day.", "وہ ہر روز ہجے کرنا کرتا ہے۔"),
                DailyVerbExample("They are spelling now.", "وہ ابھی ہجے کرنا کر رہے ہیں۔"),
                DailyVerbExample("We spelt / spelled yesterday.", "ہم نے کل ہجے کرنا کیا۔"),
                DailyVerbExample("She has spelt / spelled.", "وہ ہجے کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"spell\" with I, you, we, and they. Use \"spells\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “spell” اور “He, she, it” کے ساتھ “spells” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_423_spill",
            baseForm = "spill",
            thirdPerson = "spills",
            presentParticiple = "spilling",
            pastSimple = "spilt / spilled",
            pastParticiple = "spilt / spilled",
            urduMeaning = "چھلکنا",
            examples = listOf(
                DailyVerbExample("Don't spill milk.", "دودھ مت چھلکائیں۔"),
                DailyVerbExample("He spills every day.", "وہ ہر روز چھلکنا کرتا ہے۔"),
                DailyVerbExample("They are spilling now.", "وہ ابھی چھلکنا کر رہے ہیں۔"),
                DailyVerbExample("We spilt / spilled yesterday.", "ہم نے کل چھلکنا کیا۔"),
                DailyVerbExample("She has spilt / spilled.", "وہ چھلکنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"spill\" with I, you, we, and they. Use \"spills\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “spill” اور “He, she, it” کے ساتھ “spills” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_424_spoil",
            baseForm = "spoil",
            thirdPerson = "spoils",
            presentParticiple = "spoiling",
            pastSimple = "spoilt / spoiled",
            pastParticiple = "spoilt / spoiled",
            urduMeaning = "خراب کرنا",
            examples = listOf(
                DailyVerbExample("Food spoils fast.", "کھانا جلدی خراب ہو جاتا ہے۔"),
                DailyVerbExample("He spoils every day.", "وہ ہر روز خراب کرنا کرتا ہے۔"),
                DailyVerbExample("They are spoiling now.", "وہ ابھی خراب کرنا کر رہے ہیں۔"),
                DailyVerbExample("We spoilt / spoiled yesterday.", "ہم نے کل خراب کرنا کیا۔"),
                DailyVerbExample("She has spoilt / spoiled.", "وہ خراب کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"spoil\" with I, you, we, and they. Use \"spoils\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “spoil” اور “He, she, it” کے ساتھ “spoils” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_425_spread",
            baseForm = "spread",
            thirdPerson = "spreads",
            presentParticiple = "spreading",
            pastSimple = "spread",
            pastParticiple = "spread",
            urduMeaning = "پھیلانا",
            examples = listOf(
                DailyVerbExample("Spread butter.", "مکھن پھیلائیں۔"),
                DailyVerbExample("He spreads every day.", "وہ ہر روز پھیلانا کرتا ہے۔"),
                DailyVerbExample("They are spreading now.", "وہ ابھی پھیلانا کر رہے ہیں۔"),
                DailyVerbExample("We spread yesterday.", "ہم نے کل پھیلانا کیا۔"),
                DailyVerbExample("She has spread.", "وہ پھیلانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"spread\" with I, you, we, and they. Use \"spreads\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “spread” اور “He, she, it” کے ساتھ “spreads” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_426_squeeze",
            baseForm = "squeeze",
            thirdPerson = "squeezes",
            presentParticiple = "squeezing",
            pastSimple = "squeezed",
            pastParticiple = "squeezed",
            urduMeaning = "نچوڑنا",
            examples = listOf(
                DailyVerbExample("Squeeze lemon.", "لیمو نچوڑیں۔"),
                DailyVerbExample("He squeezes every day.", "وہ ہر روز نچوڑنا کرتا ہے۔"),
                DailyVerbExample("They are squeezing now.", "وہ ابھی نچوڑنا کر رہے ہیں۔"),
                DailyVerbExample("We squeezed yesterday.", "ہم نے کل نچوڑنا کیا۔"),
                DailyVerbExample("She has squeezed.", "وہ نچوڑنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"squeeze\" with I, you, we, and they. Use \"squeezes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “squeeze” اور “He, she, it” کے ساتھ “squeezes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_427_stare",
            baseForm = "stare",
            thirdPerson = "stares",
            presentParticiple = "staring",
            pastSimple = "stared",
            pastParticiple = "stared",
            urduMeaning = "گھورنا",
            examples = listOf(
                DailyVerbExample("Don't stare.", "گھوریں نہیں۔"),
                DailyVerbExample("He stares every day.", "وہ ہر روز گھورنا کرتا ہے۔"),
                DailyVerbExample("They are staring now.", "وہ ابھی گھورنا کر رہے ہیں۔"),
                DailyVerbExample("We stared yesterday.", "ہم نے کل گھورنا کیا۔"),
                DailyVerbExample("She has stared.", "وہ گھورنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"stare\" with I, you, we, and they. Use \"stares\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “stare” اور “He, she, it” کے ساتھ “stares” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_428_steal",
            baseForm = "steal",
            thirdPerson = "steals",
            presentParticiple = "stealing",
            pastSimple = "stole",
            pastParticiple = "stolen",
            urduMeaning = "چوری کرنا",
            examples = listOf(
                DailyVerbExample("Never steal.", "کبھی چوری نہ کریں۔"),
                DailyVerbExample("He steals every day.", "وہ ہر روز چوری کرنا کرتا ہے۔"),
                DailyVerbExample("They are stealing now.", "وہ ابھی چوری کرنا کر رہے ہیں۔"),
                DailyVerbExample("We stole yesterday.", "ہم نے کل چوری کرنا کیا۔"),
                DailyVerbExample("She has stolen.", "وہ چوری کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"steal\" with I, you, we, and they. Use \"steals\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “steal” اور “He, she, it” کے ساتھ “steals” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_429_steer",
            baseForm = "steer",
            thirdPerson = "steers",
            presentParticiple = "steering",
            pastSimple = "steered",
            pastParticiple = "steered",
            urduMeaning = "گاڑی موڑنا",
            examples = listOf(
                DailyVerbExample("Steer wheel.", "اسٹیئرنگ موڑیں۔"),
                DailyVerbExample("He steers every day.", "وہ ہر روز گاڑی موڑنا کرتا ہے۔"),
                DailyVerbExample("They are steering now.", "وہ ابھی گاڑی موڑنا کر رہے ہیں۔"),
                DailyVerbExample("We steered yesterday.", "ہم نے کل گاڑی موڑنا کیا۔"),
                DailyVerbExample("She has steered.", "وہ گاڑی موڑنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"steer\" with I, you, we, and they. Use \"steers\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “steer” اور “He, she, it” کے ساتھ “steers” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_430_stick",
            baseForm = "stick",
            thirdPerson = "sticks",
            presentParticiple = "sticking",
            pastSimple = "stuck",
            pastParticiple = "stuck",
            urduMeaning = "چپکنا",
            examples = listOf(
                DailyVerbExample("Stamp sticks paper.", "ٹکٹ کاغذ سے چپکتی ہے۔"),
                DailyVerbExample("He sticks every day.", "وہ ہر روز چپکنا کرتا ہے۔"),
                DailyVerbExample("They are sticking now.", "وہ ابھی چپکنا کر رہے ہیں۔"),
                DailyVerbExample("We stuck yesterday.", "ہم نے کل چپکنا کیا۔"),
                DailyVerbExample("She has stuck.", "وہ چپکنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"stick\" with I, you, we, and they. Use \"sticks\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “stick” اور “He, she, it” کے ساتھ “sticks” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_431_stitch",
            baseForm = "stitch",
            thirdPerson = "stitches",
            presentParticiple = "stitching",
            pastSimple = "stitched",
            pastParticiple = "stitched",
            urduMeaning = "سلائی کرنا",
            examples = listOf(
                DailyVerbExample("Stitch clothes.", "کپڑے سلائی کریں۔"),
                DailyVerbExample("He stitches every day.", "وہ ہر روز سلائی کرنا کرتا ہے۔"),
                DailyVerbExample("They are stitching now.", "وہ ابھی سلائی کرنا کر رہے ہیں۔"),
                DailyVerbExample("We stitched yesterday.", "ہم نے کل سلائی کرنا کیا۔"),
                DailyVerbExample("She has stitched.", "وہ سلائی کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"stitch\" with I, you, we, and they. Use \"stitches\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “stitch” اور “He, she, it” کے ساتھ “stitches” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_432_store",
            baseForm = "store",
            thirdPerson = "stores",
            presentParticiple = "storing",
            pastSimple = "stored",
            pastParticiple = "stored",
            urduMeaning = "ذخیرہ کرنا",
            examples = listOf(
                DailyVerbExample("Store data.", "ڈیٹا ذخیرہ کریں۔"),
                DailyVerbExample("He stores every day.", "وہ ہر روز ذخیرہ کرنا کرتا ہے۔"),
                DailyVerbExample("They are storing now.", "وہ ابھی ذخیرہ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We stored yesterday.", "ہم نے کل ذخیرہ کرنا کیا۔"),
                DailyVerbExample("She has stored.", "وہ ذخیرہ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"store\" with I, you, we, and they. Use \"stores\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “store” اور “He, she, it” کے ساتھ “stores” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_433_stretch",
            baseForm = "stretch",
            thirdPerson = "stretches",
            presentParticiple = "stretching",
            pastSimple = "stretched",
            pastParticiple = "stretched",
            urduMeaning = "پھیلنا",
            examples = listOf(
                DailyVerbExample("Stretch muscle.", "پٹھوں کو پھیلائیں۔"),
                DailyVerbExample("He stretches every day.", "وہ ہر روز پھیلنا کرتا ہے۔"),
                DailyVerbExample("They are stretching now.", "وہ ابھی پھیلنا کر رہے ہیں۔"),
                DailyVerbExample("We stretched yesterday.", "ہم نے کل پھیلنا کیا۔"),
                DailyVerbExample("She has stretched.", "وہ پھیلنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"stretch\" with I, you, we, and they. Use \"stretches\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “stretch” اور “He, she, it” کے ساتھ “stretches” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_434_strike",
            baseForm = "strike",
            thirdPerson = "strikes",
            presentParticiple = "striking",
            pastSimple = "struck",
            pastParticiple = "struck",
            urduMeaning = "مارنا",
            examples = listOf(
                DailyVerbExample("Clock strikes twelve.", "گھڑی پر بارہ بجے۔"),
                DailyVerbExample("He strikes every day.", "وہ ہر روز مارنا کرتا ہے۔"),
                DailyVerbExample("They are striking now.", "وہ ابھی مارنا کر رہے ہیں۔"),
                DailyVerbExample("We struck yesterday.", "ہم نے کل مارنا کیا۔"),
                DailyVerbExample("She has struck.", "وہ مارنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"strike\" with I, you, we, and they. Use \"strikes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “strike” اور “He, she, it” کے ساتھ “strikes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_435_struggle",
            baseForm = "struggle",
            thirdPerson = "struggles",
            presentParticiple = "struggling",
            pastSimple = "struggled",
            pastParticiple = "struggled",
            urduMeaning = "جدوجہد کرنا",
            examples = listOf(
                DailyVerbExample("Struggle for success.", "کامیابی کے لیے جدوجہد کریں۔"),
                DailyVerbExample("He struggles every day.", "وہ ہر روز جدوجہد کرنا کرتا ہے۔"),
                DailyVerbExample("They are struggling now.", "وہ ابھی جدوجہد کرنا کر رہے ہیں۔"),
                DailyVerbExample("We struggled yesterday.", "ہم نے کل جدوجہد کرنا کیا۔"),
                DailyVerbExample("She has struggled.", "وہ جدوجہد کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"struggle\" with I, you, we, and they. Use \"struggles\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “struggle” اور “He, she, it” کے ساتھ “struggles” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_436_study",
            baseForm = "study",
            thirdPerson = "studies",
            presentParticiple = "studying",
            pastSimple = "studied",
            pastParticiple = "studied",
            urduMeaning = "مطالعہ کرنا",
            examples = listOf(
                DailyVerbExample("Study daily.", "روزانہ مطالعہ کریں۔"),
                DailyVerbExample("He studies every day.", "وہ ہر روز مطالعہ کرنا کرتا ہے۔"),
                DailyVerbExample("They are studying now.", "وہ ابھی مطالعہ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We studied yesterday.", "ہم نے کل مطالعہ کرنا کیا۔"),
                DailyVerbExample("She has studied.", "وہ مطالعہ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"study\" with I, you, we, and they. Use \"studies\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “study” اور “He, she, it” کے ساتھ “studies” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_437_submit",
            baseForm = "submit",
            thirdPerson = "submits",
            presentParticiple = "submitting",
            pastSimple = "submitted",
            pastParticiple = "submitted",
            urduMeaning = "جمع کروانا",
            examples = listOf(
                DailyVerbExample("Submit report.", "رپورٹ جمع کروائیں۔"),
                DailyVerbExample("He submits every day.", "وہ ہر روز جمع کروانا کرتا ہے۔"),
                DailyVerbExample("They are submitting now.", "وہ ابھی جمع کروانا کر رہے ہیں۔"),
                DailyVerbExample("We submitted yesterday.", "ہم نے کل جمع کروانا کیا۔"),
                DailyVerbExample("She has submitted.", "وہ جمع کروانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"submit\" with I, you, we, and they. Use \"submits\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “submit” اور “He, she, it” کے ساتھ “submits” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_438_succeed",
            baseForm = "succeed",
            thirdPerson = "succeeds",
            presentParticiple = "succeeding",
            pastSimple = "succeeded",
            pastParticiple = "succeeded",
            urduMeaning = "کامیاب ہونا",
            examples = listOf(
                DailyVerbExample("Work hard to succeed.", "کامیابی کے لیے محنت کریں۔"),
                DailyVerbExample("He succeeds every day.", "وہ ہر روز کامیاب ہونا کرتا ہے۔"),
                DailyVerbExample("They are succeeding now.", "وہ ابھی کامیاب ہونا کر رہے ہیں۔"),
                DailyVerbExample("We succeeded yesterday.", "ہم نے کل کامیاب ہونا کیا۔"),
                DailyVerbExample("She has succeeded.", "وہ کامیاب ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"succeed\" with I, you, we, and they. Use \"succeeds\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “succeed” اور “He, she, it” کے ساتھ “succeeds” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_439_suffer",
            baseForm = "suffer",
            thirdPerson = "suffers",
            presentParticiple = "suffering",
            pastSimple = "suffered",
            pastParticiple = "suffered",
            urduMeaning = "مبتلا ہونا",
            examples = listOf(
                DailyVerbExample("Don't suffer quietly.", "خاموشی سے تکلیف نہ سہیں۔"),
                DailyVerbExample("He suffers every day.", "وہ ہر روز مبتلا ہونا کرتا ہے۔"),
                DailyVerbExample("They are suffering now.", "وہ ابھی مبتلا ہونا کر رہے ہیں۔"),
                DailyVerbExample("We suffered yesterday.", "ہم نے کل مبتلا ہونا کیا۔"),
                DailyVerbExample("She has suffered.", "وہ مبتلا ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"suffer\" with I, you, we, and they. Use \"suffers\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “suffer” اور “He, she, it” کے ساتھ “suffers” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_440_suit",
            baseForm = "suit",
            thirdPerson = "suits",
            presentParticiple = "suiting",
            pastSimple = "suited",
            pastParticiple = "suited",
            urduMeaning = "مناسب ہونا",
            examples = listOf(
                DailyVerbExample("Color suits you.", "رنگ آپ پر مناسب لگتا ہے۔"),
                DailyVerbExample("He suits every day.", "وہ ہر روز مناسب ہونا کرتا ہے۔"),
                DailyVerbExample("They are suiting now.", "وہ ابھی مناسب ہونا کر رہے ہیں۔"),
                DailyVerbExample("We suited yesterday.", "ہم نے کل مناسب ہونا کیا۔"),
                DailyVerbExample("She has suited.", "وہ مناسب ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"suit\" with I, you, we, and they. Use \"suits\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “suit” اور “He, she, it” کے ساتھ “suits” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_441_supply",
            baseForm = "supply",
            thirdPerson = "supplies",
            presentParticiple = "supplying",
            pastSimple = "supplied",
            pastParticiple = "supplied",
            urduMeaning = "فراہم کرنا",
            examples = listOf(
                DailyVerbExample("Supply clean water.", "صاف پانی فراہم کریں۔"),
                DailyVerbExample("He supplies every day.", "وہ ہر روز فراہم کرنا کرتا ہے۔"),
                DailyVerbExample("They are supplying now.", "وہ ابھی فراہم کرنا کر رہے ہیں۔"),
                DailyVerbExample("We supplied yesterday.", "ہم نے کل فراہم کرنا کیا۔"),
                DailyVerbExample("She has supplied.", "وہ فراہم کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"supply\" with I, you, we, and they. Use \"supplies\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “supply” اور “He, she, it” کے ساتھ “supplies” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_442_support",
            baseForm = "support",
            thirdPerson = "supports",
            presentParticiple = "supporting",
            pastSimple = "supported",
            pastParticiple = "supported",
            urduMeaning = "سہارا دینا",
            examples = listOf(
                DailyVerbExample("Support charity.", "خیرات کا ساتھ دیں۔"),
                DailyVerbExample("He supports every day.", "وہ ہر روز سہارا دینا کرتا ہے۔"),
                DailyVerbExample("They are supporting now.", "وہ ابھی سہارا دینا کر رہے ہیں۔"),
                DailyVerbExample("We supported yesterday.", "ہم نے کل سہارا دینا کیا۔"),
                DailyVerbExample("She has supported.", "وہ سہارا دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"support\" with I, you, we, and they. Use \"supports\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “support” اور “He, she, it” کے ساتھ “supports” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_443_suppose",
            baseForm = "suppose",
            thirdPerson = "supposes",
            presentParticiple = "supposing",
            pastSimple = "supposed",
            pastParticiple = "supposed",
            urduMeaning = "فرض کرنا",
            examples = listOf(
                DailyVerbExample("Suppose it rains.", "فرض کریں بارش ہوتی ہے۔"),
                DailyVerbExample("He supposes every day.", "وہ ہر روز فرض کرنا کرتا ہے۔"),
                DailyVerbExample("They are supposing now.", "وہ ابھی فرض کرنا کر رہے ہیں۔"),
                DailyVerbExample("We supposed yesterday.", "ہم نے کل فرض کرنا کیا۔"),
                DailyVerbExample("She has supposed.", "وہ فرض کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"suppose\" with I, you, we, and they. Use \"supposes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “suppose” اور “He, she, it” کے ساتھ “supposes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_444_surprise",
            baseForm = "surprise",
            thirdPerson = "surprises",
            presentParticiple = "surprising",
            pastSimple = "surprised",
            pastParticiple = "surprised",
            urduMeaning = "حیران کرنا",
            examples = listOf(
                DailyVerbExample("Gift surprised her.", "تحفے نے اسے حیران کر دیا۔"),
                DailyVerbExample("He surprises every day.", "وہ ہر روز حیران کرنا کرتا ہے۔"),
                DailyVerbExample("They are surprising now.", "وہ ابھی حیران کرنا کر رہے ہیں۔"),
                DailyVerbExample("We surprised yesterday.", "ہم نے کل حیران کرنا کیا۔"),
                DailyVerbExample("She has surprised.", "وہ حیران کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"surprise\" with I, you, we, and they. Use \"surprises\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “surprise” اور “He, she, it” کے ساتھ “surprises” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_445_survive",
            baseForm = "survive",
            thirdPerson = "survives",
            presentParticiple = "surviving",
            pastSimple = "survived",
            pastParticiple = "survived",
            urduMeaning = "بچ جانا",
            examples = listOf(
                DailyVerbExample("Plants survive drought.", "پودے خشک سالی میں بچ جاتے ہیں۔"),
                DailyVerbExample("He survives every day.", "وہ ہر روز بچ جانا کرتا ہے۔"),
                DailyVerbExample("They are surviving now.", "وہ ابھی بچ جانا کر رہے ہیں۔"),
                DailyVerbExample("We survived yesterday.", "ہم نے کل بچ جانا کیا۔"),
                DailyVerbExample("She has survived.", "وہ بچ جانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"survive\" with I, you, we, and they. Use \"survives\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “survive” اور “He, she, it” کے ساتھ “survives” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_446_suspect",
            baseForm = "suspect",
            thirdPerson = "suspects",
            presentParticiple = "suspecting",
            pastSimple = "suspected",
            pastParticiple = "suspected",
            urduMeaning = "شک کرنا",
            examples = listOf(
                DailyVerbExample("Police suspect foul play.", "پولیس کو شک ہے۔"),
                DailyVerbExample("He suspects every day.", "وہ ہر روز شک کرنا کرتا ہے۔"),
                DailyVerbExample("They are suspecting now.", "وہ ابھی شک کرنا کر رہے ہیں۔"),
                DailyVerbExample("We suspected yesterday.", "ہم نے کل شک کرنا کیا۔"),
                DailyVerbExample("She has suspected.", "وہ شک کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"suspect\" with I, you, we, and they. Use \"suspects\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “suspect” اور “He, she, it” کے ساتھ “suspects” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_447_swallow",
            baseForm = "swallow",
            thirdPerson = "swallows",
            presentParticiple = "swallowing",
            pastSimple = "swallowed",
            pastParticiple = "swallowed",
            urduMeaning = "نگلنا",
            examples = listOf(
                DailyVerbExample("Swallow medicine.", "دوا نگلیں۔"),
                DailyVerbExample("He swallows every day.", "وہ ہر روز نگلنا کرتا ہے۔"),
                DailyVerbExample("They are swallowing now.", "وہ ابھی نگلنا کر رہے ہیں۔"),
                DailyVerbExample("We swallowed yesterday.", "ہم نے کل نگلنا کیا۔"),
                DailyVerbExample("She has swallowed.", "وہ نگلنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"swallow\" with I, you, we, and they. Use \"swallows\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “swallow” اور “He, she, it” کے ساتھ “swallows” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_448_swear",
            baseForm = "swear",
            thirdPerson = "swears",
            presentParticiple = "swearing",
            pastSimple = "swore",
            pastParticiple = "sworn",
            urduMeaning = "قسم کھانا",
            examples = listOf(
                DailyVerbExample("Swear by truth.", "سچ کی قسم کھائیں۔"),
                DailyVerbExample("He swears every day.", "وہ ہر روز قسم کھانا کرتا ہے۔"),
                DailyVerbExample("They are swearing now.", "وہ ابھی قسم کھانا کر رہے ہیں۔"),
                DailyVerbExample("We swore yesterday.", "ہم نے کل قسم کھانا کیا۔"),
                DailyVerbExample("She has sworn.", "وہ قسم کھانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"swear\" with I, you, we, and they. Use \"swears\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “swear” اور “He, she, it” کے ساتھ “swears” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_449_sweep",
            baseForm = "sweep",
            thirdPerson = "sweeps",
            presentParticiple = "sweeping",
            pastSimple = "swept",
            pastParticiple = "swept",
            urduMeaning = "جھاڑو دینا",
            examples = listOf(
                DailyVerbExample("Sweep floor.", "فرش پر جھاڑو دیں۔"),
                DailyVerbExample("He sweeps every day.", "وہ ہر روز جھاڑو دینا کرتا ہے۔"),
                DailyVerbExample("They are sweeping now.", "وہ ابھی جھاڑو دینا کر رہے ہیں۔"),
                DailyVerbExample("We swept yesterday.", "ہم نے کل جھاڑو دینا کیا۔"),
                DailyVerbExample("She has swept.", "وہ جھاڑو دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"sweep\" with I, you, we, and they. Use \"sweeps\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “sweep” اور “He, she, it” کے ساتھ “sweeps” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_450_switch",
            baseForm = "switch",
            thirdPerson = "switches",
            presentParticiple = "switching",
            pastSimple = "switched",
            pastParticiple = "switched",
            urduMeaning = "سوئچ کرنا",
            examples = listOf(
                DailyVerbExample("Switch off light.", "روشنی بند کریں۔"),
                DailyVerbExample("He switches every day.", "وہ ہر روز سوئچ کرنا کرتا ہے۔"),
                DailyVerbExample("They are switching now.", "وہ ابھی سوئچ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We switched yesterday.", "ہم نے کل سوئچ کرنا کیا۔"),
                DailyVerbExample("She has switched.", "وہ سوئچ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"switch\" with I, you, we, and they. Use \"switches\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “switch” اور “He, she, it” کے ساتھ “switches” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_451_tear",
            baseForm = "tear",
            thirdPerson = "tears",
            presentParticiple = "tearing",
            pastSimple = "tore",
            pastParticiple = "torn",
            urduMeaning = "پھاڑنا",
            examples = listOf(
                DailyVerbExample("Don't tear paper.", "کاغذ مت پھاڑیں۔"),
                DailyVerbExample("He tears every day.", "وہ ہر روز پھاڑنا کرتا ہے۔"),
                DailyVerbExample("They are tearing now.", "وہ ابھی پھاڑنا کر رہے ہیں۔"),
                DailyVerbExample("We tore yesterday.", "ہم نے کل پھاڑنا کیا۔"),
                DailyVerbExample("She has torn.", "وہ پھاڑنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"tear\" with I, you, we, and they. Use \"tears\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “tear” اور “He, she, it” کے ساتھ “tears” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_452_test",
            baseForm = "test",
            thirdPerson = "tests",
            presentParticiple = "testing",
            pastSimple = "tested",
            pastParticiple = "tested",
            urduMeaning = "ٹیسٹ کرنا",
            examples = listOf(
                DailyVerbExample("Test the engine.", "انجن کا ٹیسٹ کریں۔"),
                DailyVerbExample("He tests every day.", "وہ ہر روز ٹیسٹ کرنا کرتا ہے۔"),
                DailyVerbExample("They are testing now.", "وہ ابھی ٹیسٹ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We tested yesterday.", "ہم نے کل ٹیسٹ کرنا کیا۔"),
                DailyVerbExample("She has tested.", "وہ ٹیسٹ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"test\" with I, you, we, and they. Use \"tests\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “test” اور “He, she, it” کے ساتھ “tests” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_453_threaten",
            baseForm = "threaten",
            thirdPerson = "threatens",
            presentParticiple = "threatening",
            pastSimple = "threatened",
            pastParticiple = "threatened",
            urduMeaning = "دھمکی دینا",
            examples = listOf(
                DailyVerbExample("Don't threaten.", "دھمکی نہ دیں۔"),
                DailyVerbExample("He threatens every day.", "وہ ہر روز دھمکی دینا کرتا ہے۔"),
                DailyVerbExample("They are threatening now.", "وہ ابھی دھمکی دینا کر رہے ہیں۔"),
                DailyVerbExample("We threatened yesterday.", "ہم نے کل دھمکی دینا کیا۔"),
                DailyVerbExample("She has threatened.", "وہ دھمکی دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"threaten\" with I, you, we, and they. Use \"threatens\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “threaten” اور “He, she, it” کے ساتھ “threatens” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_454_tie",
            baseForm = "tie",
            thirdPerson = "ties",
            presentParticiple = "tying",
            pastSimple = "tied",
            pastParticiple = "tied",
            urduMeaning = "باندھنا",
            examples = listOf(
                DailyVerbExample("Tie shoes.", "تسمے باندھیں۔"),
                DailyVerbExample("He ties every day.", "وہ ہر روز باندھنا کرتا ہے۔"),
                DailyVerbExample("They are tying now.", "وہ ابھی باندھنا کر رہے ہیں۔"),
                DailyVerbExample("We tied yesterday.", "ہم نے کل باندھنا کیا۔"),
                DailyVerbExample("She has tied.", "وہ باندھنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"tie\" with I, you, we, and they. Use \"ties\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “tie” اور “He, she, it” کے ساتھ “ties” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_455_train",
            baseForm = "train",
            thirdPerson = "trains",
            presentParticiple = "training",
            pastSimple = "trained",
            pastParticiple = "trained",
            urduMeaning = "تربیت دینا",
            examples = listOf(
                DailyVerbExample("Train staff.", "عملے کو تربیت دیں۔"),
                DailyVerbExample("He trains every day.", "وہ ہر روز تربیت دینا کرتا ہے۔"),
                DailyVerbExample("They are training now.", "وہ ابھی تربیت دینا کر رہے ہیں۔"),
                DailyVerbExample("We trained yesterday.", "ہم نے کل تربیت دینا کیا۔"),
                DailyVerbExample("She has trained.", "وہ تربیت دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"train\" with I, you, we, and they. Use \"trains\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “train” اور “He, she, it” کے ساتھ “trains” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_456_translate",
            baseForm = "translate",
            thirdPerson = "translates",
            presentParticiple = "translating",
            pastSimple = "translated",
            pastParticiple = "translated",
            urduMeaning = "ترجمہ کرنا",
            examples = listOf(
                DailyVerbExample("Translate sentence.", "جملے کا ترجمہ کریں۔"),
                DailyVerbExample("He translates every day.", "وہ ہر روز ترجمہ کرنا کرتا ہے۔"),
                DailyVerbExample("They are translating now.", "وہ ابھی ترجمہ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We translated yesterday.", "ہم نے کل ترجمہ کرنا کیا۔"),
                DailyVerbExample("She has translated.", "وہ ترجمہ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"translate\" with I, you, we, and they. Use \"translates\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “translate” اور “He, she, it” کے ساتھ “translates” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_457_treat",
            baseForm = "treat",
            thirdPerson = "treats",
            presentParticiple = "treating",
            pastSimple = "treated",
            pastParticiple = "treated",
            urduMeaning = "علاج کرنا",
            examples = listOf(
                DailyVerbExample("Treat patients.", "مریضوں کا علاج کریں۔"),
                DailyVerbExample("He treats every day.", "وہ ہر روز علاج کرنا کرتا ہے۔"),
                DailyVerbExample("They are treating now.", "وہ ابھی علاج کرنا کر رہے ہیں۔"),
                DailyVerbExample("We treated yesterday.", "ہم نے کل علاج کرنا کیا۔"),
                DailyVerbExample("She has treated.", "وہ علاج کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"treat\" with I, you, we, and they. Use \"treats\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “treat” اور “He, she, it” کے ساتھ “treats” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_458_trick",
            baseForm = "trick",
            thirdPerson = "tricks",
            presentParticiple = "tricking",
            pastSimple = "tricked",
            pastParticiple = "tricked",
            urduMeaning = "دھوکہ دینا",
            examples = listOf(
                DailyVerbExample("Magic trick amused.", "جادو کے شعبدے نے محظوظ کیا۔"),
                DailyVerbExample("He tricks every day.", "وہ ہر روز دھوکہ دینا کرتا ہے۔"),
                DailyVerbExample("They are tricking now.", "وہ ابھی دھوکہ دینا کر رہے ہیں۔"),
                DailyVerbExample("We tricked yesterday.", "ہم نے کل دھوکہ دینا کیا۔"),
                DailyVerbExample("She has tricked.", "وہ دھوکہ دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"trick\" with I, you, we, and they. Use \"tricks\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “trick” اور “He, she, it” کے ساتھ “tricks” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_459_turn",
            baseForm = "turn",
            thirdPerson = "turns",
            presentParticiple = "turning",
            pastSimple = "turned",
            pastParticiple = "turned",
            urduMeaning = "مڑنا",
            examples = listOf(
                DailyVerbExample("Turn right.", "دائیں مڑیں۔"),
                DailyVerbExample("He turns every day.", "وہ ہر روز مڑنا کرتا ہے۔"),
                DailyVerbExample("They are turning now.", "وہ ابھی مڑنا کر رہے ہیں۔"),
                DailyVerbExample("We turned yesterday.", "ہم نے کل مڑنا کیا۔"),
                DailyVerbExample("She has turned.", "وہ مڑنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"turn\" with I, you, we, and they. Use \"turns\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “turn” اور “He, she, it” کے ساتھ “turns” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_460_twist",
            baseForm = "twist",
            thirdPerson = "twists",
            presentParticiple = "twisting",
            pastSimple = "twisted",
            pastParticiple = "twisted",
            urduMeaning = "موڑنا",
            examples = listOf(
                DailyVerbExample("Twist cap.", "ڈھکن موڑیں۔"),
                DailyVerbExample("He twists every day.", "وہ ہر روز موڑنا کرتا ہے۔"),
                DailyVerbExample("They are twisting now.", "وہ ابھی موڑنا کر رہے ہیں۔"),
                DailyVerbExample("We twisted yesterday.", "ہم نے کل موڑنا کیا۔"),
                DailyVerbExample("She has twisted.", "وہ موڑنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"twist\" with I, you, we, and they. Use \"twists\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “twist” اور “He, she, it” کے ساتھ “twists” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_461_type",
            baseForm = "type",
            thirdPerson = "types",
            presentParticiple = "typing",
            pastSimple = "typed",
            pastParticiple = "typed",
            urduMeaning = "ٹائپ کرنا",
            examples = listOf(
                DailyVerbExample("Type message.", "پیغام ٹائپ کریں۔"),
                DailyVerbExample("He types every day.", "وہ ہر روز ٹائپ کرنا کرتا ہے۔"),
                DailyVerbExample("They are typing now.", "وہ ابھی ٹائپ کرنا کر رہے ہیں۔"),
                DailyVerbExample("We typed yesterday.", "ہم نے کل ٹائپ کرنا کیا۔"),
                DailyVerbExample("She has typed.", "وہ ٹائپ کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"type\" with I, you, we, and they. Use \"types\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “type” اور “He, she, it” کے ساتھ “types” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_462_unite",
            baseForm = "unite",
            thirdPerson = "unites",
            presentParticiple = "uniting",
            pastSimple = "united",
            pastParticiple = "united",
            urduMeaning = "متحد ہونا",
            examples = listOf(
                DailyVerbExample("Unite for peace.", "امن کے لیے متحد ہوں۔"),
                DailyVerbExample("He unites every day.", "وہ ہر روز متحد ہونا کرتا ہے۔"),
                DailyVerbExample("They are uniting now.", "وہ ابھی متحد ہونا کر رہے ہیں۔"),
                DailyVerbExample("We united yesterday.", "ہم نے کل متحد ہونا کیا۔"),
                DailyVerbExample("She has united.", "وہ متحد ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"unite\" with I, you, we, and they. Use \"unites\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “unite” اور “He, she, it” کے ساتھ “unites” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_463_unlock",
            baseForm = "unlock",
            thirdPerson = "unlocks",
            presentParticiple = "unlocking",
            pastSimple = "unlocked",
            pastParticiple = "unlocked",
            urduMeaning = "تالا کھولنا",
            examples = listOf(
                DailyVerbExample("Unlock door.", "دروازے کا تالا کھولیں۔"),
                DailyVerbExample("He unlocks every day.", "وہ ہر روز تالا کھولنا کرتا ہے۔"),
                DailyVerbExample("They are unlocking now.", "وہ ابھی تالا کھولنا کر رہے ہیں۔"),
                DailyVerbExample("We unlocked yesterday.", "ہم نے کل تالا کھولنا کیا۔"),
                DailyVerbExample("She has unlocked.", "وہ تالا کھولنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"unlock\" with I, you, we, and they. Use \"unlocks\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “unlock” اور “He, she, it” کے ساتھ “unlocks” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_464_upset",
            baseForm = "upset",
            thirdPerson = "upsets",
            presentParticiple = "upsetting",
            pastSimple = "upset",
            pastParticiple = "upset",
            urduMeaning = "پریشان کرنا",
            examples = listOf(
                DailyVerbExample("Don't upset her.", "اسے پریشان نہ کریں۔"),
                DailyVerbExample("He upsets every day.", "وہ ہر روز پریشان کرنا کرتا ہے۔"),
                DailyVerbExample("They are upsetting now.", "وہ ابھی پریشان کرنا کر رہے ہیں۔"),
                DailyVerbExample("We upset yesterday.", "ہم نے کل پریشان کرنا کیا۔"),
                DailyVerbExample("She has upset.", "وہ پریشان کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"upset\" with I, you, we, and they. Use \"upsets\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “upset” اور “He, she, it” کے ساتھ “upsets” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_465_urge",
            baseForm = "urge",
            thirdPerson = "urges",
            presentParticiple = "urging",
            pastSimple = "urged",
            pastParticiple = "urged",
            urduMeaning = "ترغیب دینا",
            examples = listOf(
                DailyVerbExample("Urge action.", "ایکشن کی ترغیب دیں۔"),
                DailyVerbExample("He urges every day.", "وہ ہر روز ترغیب دینا کرتا ہے۔"),
                DailyVerbExample("They are urging now.", "وہ ابھی ترغیب دینا کر رہے ہیں۔"),
                DailyVerbExample("We urged yesterday.", "ہم نے کل ترغیب دینا کیا۔"),
                DailyVerbExample("She has urged.", "وہ ترغیب دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"urge\" with I, you, we, and they. Use \"urges\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “urge” اور “He, she, it” کے ساتھ “urges” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_466_use",
            baseForm = "use",
            thirdPerson = "uses",
            presentParticiple = "using",
            pastSimple = "used",
            pastParticiple = "used",
            urduMeaning = "استعمال کرنا",
            examples = listOf(
                DailyVerbExample("Use wisely.", "عقلمندی سے استعمال کریں۔"),
                DailyVerbExample("He uses every day.", "وہ ہر روز استعمال کرنا کرتا ہے۔"),
                DailyVerbExample("They are using now.", "وہ ابھی استعمال کرنا کر رہے ہیں۔"),
                DailyVerbExample("We used yesterday.", "ہم نے کل استعمال کرنا کیا۔"),
                DailyVerbExample("She has used.", "وہ استعمال کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"use\" with I, you, we, and they. Use \"uses\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “use” اور “He, she, it” کے ساتھ “uses” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_467_value",
            baseForm = "value",
            thirdPerson = "values",
            presentParticiple = "valuing",
            pastSimple = "valued",
            pastParticiple = "valued",
            urduMeaning = "قدر کرنا",
            examples = listOf(
                DailyVerbExample("Value time.", "وقت کی قدر کریں۔"),
                DailyVerbExample("He values every day.", "وہ ہر روز قدر کرنا کرتا ہے۔"),
                DailyVerbExample("They are valuing now.", "وہ ابھی قدر کرنا کر رہے ہیں۔"),
                DailyVerbExample("We valued yesterday.", "ہم نے کل قدر کرنا کیا۔"),
                DailyVerbExample("She has valued.", "وہ قدر کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"value\" with I, you, we, and they. Use \"values\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “value” اور “He, she, it” کے ساتھ “values” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_468_vanish",
            baseForm = "vanish",
            thirdPerson = "vanishes",
            presentParticiple = "vanishing",
            pastSimple = "vanished",
            pastParticiple = "vanished",
            urduMeaning = "غائب ہونا",
            examples = listOf(
                DailyVerbExample("Mist vanished.", "دھند غائب ہو گئی۔"),
                DailyVerbExample("He vanishes every day.", "وہ ہر روز غائب ہونا کرتا ہے۔"),
                DailyVerbExample("They are vanishing now.", "وہ ابھی غائب ہونا کر رہے ہیں۔"),
                DailyVerbExample("We vanished yesterday.", "ہم نے کل غائب ہونا کیا۔"),
                DailyVerbExample("She has vanished.", "وہ غائب ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"vanish\" with I, you, we, and they. Use \"vanishes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “vanish” اور “He, she, it” کے ساتھ “vanishes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_469_vote",
            baseForm = "vote",
            thirdPerson = "votes",
            presentParticiple = "voting",
            pastSimple = "voted",
            pastParticiple = "voted",
            urduMeaning = "ووٹ دینا",
            examples = listOf(
                DailyVerbExample("Vote in election.", "انتخابات میں ووٹ دیں۔"),
                DailyVerbExample("He votes every day.", "وہ ہر روز ووٹ دینا کرتا ہے۔"),
                DailyVerbExample("They are voting now.", "وہ ابھی ووٹ دینا کر رہے ہیں۔"),
                DailyVerbExample("We voted yesterday.", "ہم نے کل ووٹ دینا کیا۔"),
                DailyVerbExample("She has voted.", "وہ ووٹ دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"vote\" with I, you, we, and they. Use \"votes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “vote” اور “He, she, it” کے ساتھ “votes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_470_wake",
            baseForm = "wake",
            thirdPerson = "wakes",
            presentParticiple = "waking",
            pastSimple = "woke",
            pastParticiple = "woken",
            urduMeaning = "جاگنا",
            examples = listOf(
                DailyVerbExample("Wake up early.", "صبح سویرے جاگیں۔"),
                DailyVerbExample("He wakes every day.", "وہ ہر روز جاگنا کرتا ہے۔"),
                DailyVerbExample("They are waking now.", "وہ ابھی جاگنا کر رہے ہیں۔"),
                DailyVerbExample("We woke yesterday.", "ہم نے کل جاگنا کیا۔"),
                DailyVerbExample("She has woken.", "وہ جاگنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"wake\" with I, you, we, and they. Use \"wakes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “wake” اور “He, she, it” کے ساتھ “wakes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_471_warn",
            baseForm = "warn",
            thirdPerson = "warns",
            presentParticiple = "warning",
            pastSimple = "warned",
            pastParticiple = "warned",
            urduMeaning = "خبردار کرنا",
            examples = listOf(
                DailyVerbExample("Warn danger.", "خطرناک بات سے خبردار کریں۔"),
                DailyVerbExample("He warns every day.", "وہ ہر روز خبردار کرنا کرتا ہے۔"),
                DailyVerbExample("They are warning now.", "وہ ابھی خبردار کرنا کر رہے ہیں۔"),
                DailyVerbExample("We warned yesterday.", "ہم نے کل خبردار کرنا کیا۔"),
                DailyVerbExample("She has warned.", "وہ خبردار کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"warn\" with I, you, we, and they. Use \"warns\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “warn” اور “He, she, it” کے ساتھ “warns” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_472_waste",
            baseForm = "waste",
            thirdPerson = "wastes",
            presentParticiple = "wasting",
            pastSimple = "wasted",
            pastParticiple = "wasted",
            urduMeaning = "ضائع کرنا",
            examples = listOf(
                DailyVerbExample("Don't waste water.", "پانی ضائع نہ کریں۔"),
                DailyVerbExample("He wastes every day.", "وہ ہر روز ضائع کرنا کرتا ہے۔"),
                DailyVerbExample("They are wasting now.", "وہ ابھی ضائع کرنا کر رہے ہیں۔"),
                DailyVerbExample("We wasted yesterday.", "ہم نے کل ضائع کرنا کیا۔"),
                DailyVerbExample("She has wasted.", "وہ ضائع کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"waste\" with I, you, we, and they. Use \"wastes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “waste” اور “He, she, it” کے ساتھ “wastes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_473_wave",
            baseForm = "wave",
            thirdPerson = "waves",
            presentParticiple = "waving",
            pastSimple = "waved",
            pastParticiple = "waved",
            urduMeaning = "ہاتھ ہلانا",
            examples = listOf(
                DailyVerbExample("Wave goodbye.", "الوداعی ہاتھ ہلائیں۔"),
                DailyVerbExample("He waves every day.", "وہ ہر روز ہاتھ ہلانا کرتا ہے۔"),
                DailyVerbExample("They are waving now.", "وہ ابھی ہاتھ ہلانا کر رہے ہیں۔"),
                DailyVerbExample("We waved yesterday.", "ہم نے کل ہاتھ ہلانا کیا۔"),
                DailyVerbExample("She has waved.", "وہ ہاتھ ہلانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"wave\" with I, you, we, and they. Use \"waves\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “wave” اور “He, she, it” کے ساتھ “waves” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_474_weigh",
            baseForm = "weigh",
            thirdPerson = "weighs",
            presentParticiple = "weighing",
            pastSimple = "weighed",
            pastParticiple = "weighed",
            urduMeaning = "وزن کرنا",
            examples = listOf(
                DailyVerbExample("Weigh luggage.", "سامان کا وزن کریں۔"),
                DailyVerbExample("He weighs every day.", "وہ ہر روز وزن کرنا کرتا ہے۔"),
                DailyVerbExample("They are weighing now.", "وہ ابھی وزن کرنا کر رہے ہیں۔"),
                DailyVerbExample("We weighed yesterday.", "ہم نے کل وزن کرنا کیا۔"),
                DailyVerbExample("She has weighed.", "وہ وزن کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"weigh\" with I, you, we, and they. Use \"weighs\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “weigh” اور “He, she, it” کے ساتھ “weighs” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_475_whisper",
            baseForm = "whisper",
            thirdPerson = "whispers",
            presentParticiple = "whispering",
            pastSimple = "whispered",
            pastParticiple = "whispered",
            urduMeaning = "سرگوشی کرنا",
            examples = listOf(
                DailyVerbExample("Whisper softly.", "دھیمی سرگوشی کریں۔"),
                DailyVerbExample("He whispers every day.", "وہ ہر روز سرگوشی کرنا کرتا ہے۔"),
                DailyVerbExample("They are whispering now.", "وہ ابھی سرگوشی کرنا کر رہے ہیں۔"),
                DailyVerbExample("We whispered yesterday.", "ہم نے کل سرگوشی کرنا کیا۔"),
                DailyVerbExample("She has whispered.", "وہ سرگوشی کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"whisper\" with I, you, we, and they. Use \"whispers\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “whisper” اور “He, she, it” کے ساتھ “whispers” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_476_whistle",
            baseForm = "whistle",
            thirdPerson = "whistles",
            presentParticiple = "whistling",
            pastSimple = "whistled",
            pastParticiple = "whistled",
            urduMeaning = "سیٹی بجانا",
            examples = listOf(
                DailyVerbExample("Whistle tune.", "دھن پر سیٹی بجائیں۔"),
                DailyVerbExample("He whistles every day.", "وہ ہر روز سیٹی بجانا کرتا ہے۔"),
                DailyVerbExample("They are whistling now.", "وہ ابھی سیٹی بجانا کر رہے ہیں۔"),
                DailyVerbExample("We whistled yesterday.", "ہم نے کل سیٹی بجانا کیا۔"),
                DailyVerbExample("She has whistled.", "وہ سیٹی بجانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"whistle\" with I, you, we, and they. Use \"whistles\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “whistle” اور “He, she, it” کے ساتھ “whistles” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_477_wipe",
            baseForm = "wipe",
            thirdPerson = "wipes",
            presentParticiple = "wiping",
            pastSimple = "wiped",
            pastParticiple = "wiped",
            urduMeaning = "پونچھنا",
            examples = listOf(
                DailyVerbExample("Wipe table.", "میز پونچھیں۔"),
                DailyVerbExample("He wipes every day.", "وہ ہر روز پونچھنا کرتا ہے۔"),
                DailyVerbExample("They are wiping now.", "وہ ابھی پونچھنا کر رہے ہیں۔"),
                DailyVerbExample("We wiped yesterday.", "ہم نے کل پونچھنا کیا۔"),
                DailyVerbExample("She has wiped.", "وہ پونچھنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"wipe\" with I, you, we, and they. Use \"wipes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “wipe” اور “He, she, it” کے ساتھ “wipes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_478_wish",
            baseForm = "wish",
            thirdPerson = "wishes",
            presentParticiple = "wishing",
            pastSimple = "wished",
            pastParticiple = "wished",
            urduMeaning = "خواہش کرنا",
            examples = listOf(
                DailyVerbExample("Wish good luck.", "نیک تمناؤں کا اظہار کریں۔"),
                DailyVerbExample("He wishes every day.", "وہ ہر روز خواہش کرنا کرتا ہے۔"),
                DailyVerbExample("They are wishing now.", "وہ ابھی خواہش کرنا کر رہے ہیں۔"),
                DailyVerbExample("We wished yesterday.", "ہم نے کل خواہش کرنا کیا۔"),
                DailyVerbExample("She has wished.", "وہ خواہش کرنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"wish\" with I, you, we, and they. Use \"wishes\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “wish” اور “He, she, it” کے ساتھ “wishes” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_479_wonder",
            baseForm = "wonder",
            thirdPerson = "wonders",
            presentParticiple = "wondering",
            pastSimple = "wondered",
            pastParticiple = "wondered",
            urduMeaning = "حیران ہونا",
            examples = listOf(
                DailyVerbExample("I wonder why.", "مجھے حیرت ہے کہ کیوں۔"),
                DailyVerbExample("He wonders every day.", "وہ ہر روز حیران ہونا کرتا ہے۔"),
                DailyVerbExample("They are wondering now.", "وہ ابھی حیران ہونا کر رہے ہیں۔"),
                DailyVerbExample("We wondered yesterday.", "ہم نے کل حیران ہونا کیا۔"),
                DailyVerbExample("She has wondered.", "وہ حیران ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"wonder\" with I, you, we, and they. Use \"wonders\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “wonder” اور “He, she, it” کے ساتھ “wonders” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_480_worry",
            baseForm = "worry",
            thirdPerson = "worries",
            presentParticiple = "worrying",
            pastSimple = "worried",
            pastParticiple = "worried",
            urduMeaning = "پریشان ہونا",
            examples = listOf(
                DailyVerbExample("Don't worry.", "پریشان نہ ہوں۔"),
                DailyVerbExample("He worries every day.", "وہ ہر روز پریشان ہونا کرتا ہے۔"),
                DailyVerbExample("They are worrying now.", "وہ ابھی پریشان ہونا کر رہے ہیں۔"),
                DailyVerbExample("We worried yesterday.", "ہم نے کل پریشان ہونا کیا۔"),
                DailyVerbExample("She has worried.", "وہ پریشان ہونا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"worry\" with I, you, we, and they. Use \"worries\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “worry” اور “He, she, it” کے ساتھ “worries” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_481_wrap",
            baseForm = "wrap",
            thirdPerson = "wraps",
            presentParticiple = "wrapping",
            pastSimple = "wrapped",
            pastParticiple = "wrapped",
            urduMeaning = "لپیٹنا",
            examples = listOf(
                DailyVerbExample("Wrap gift.", "تحفہ لپیٹیں۔"),
                DailyVerbExample("He wraps every day.", "وہ ہر روز لپیٹنا کرتا ہے۔"),
                DailyVerbExample("They are wrapping now.", "وہ ابھی لپیٹنا کر رہے ہیں۔"),
                DailyVerbExample("We wrapped yesterday.", "ہم نے کل لپیٹنا کیا۔"),
                DailyVerbExample("She has wrapped.", "وہ لپیٹنا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"wrap\" with I, you, we, and they. Use \"wraps\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “wrap” اور “He, she, it” کے ساتھ “wraps” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_482_yell",
            baseForm = "yell",
            thirdPerson = "yells",
            presentParticiple = "yelling",
            pastSimple = "yelled",
            pastParticiple = "yelled",
            urduMeaning = "چلانا",
            examples = listOf(
                DailyVerbExample("Don't yell.", "چلائیں نہیں۔"),
                DailyVerbExample("He yells every day.", "وہ ہر روز چلانا کرتا ہے۔"),
                DailyVerbExample("They are yelling now.", "وہ ابھی چلانا کر رہے ہیں۔"),
                DailyVerbExample("We yelled yesterday.", "ہم نے کل چلانا کیا۔"),
                DailyVerbExample("She has yelled.", "وہ چلانا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"yell\" with I, you, we, and they. Use \"yells\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “yell” اور “He, she, it” کے ساتھ “yells” استعمال ہوتا ہے۔"
        ),
        DailyVerb(
            id = "verb_483_yield",
            baseForm = "yield",
            thirdPerson = "yields",
            presentParticiple = "yielding",
            pastSimple = "yielded",
            pastParticiple = "yielded",
            urduMeaning = "پیداوار دینا",
            examples = listOf(
                DailyVerbExample("Crop yields well.", "فصل اچھی پیداوار دیتی ہے۔"),
                DailyVerbExample("He yields every day.", "وہ ہر روز پیداوار دینا کرتا ہے۔"),
                DailyVerbExample("They are yielding now.", "وہ ابھی پیداوار دینا کر رہے ہیں۔"),
                DailyVerbExample("We yielded yesterday.", "ہم نے کل پیداوار دینا کیا۔"),
                DailyVerbExample("She has yielded.", "وہ پیداوار دینا کر چکی ہے۔")
            ),
            usageNoteEn = "Use \"yield\" with I, you, we, and they. Use \"yields\" with he, she, and it.",
            usageNoteUr = "“I, you, we, they” کے ساتھ “yield” اور “He, she, it” کے ساتھ “yields” استعمال ہوتا ہے۔"
        )
    )

    fun getDayIndex(dateKey: String): Int {
        return try {
            val date = dateFormat.parse(dateKey) ?: Date()
            val epoch = dateFormat.parse("2025-01-01") ?: Date()
            val diffMs = date.time - epoch.time
            val days = (diffMs / (1000 * 60 * 60 * 24)).toInt()
            kotlin.math.abs(days)
        } catch (e: Exception) {
            kotlin.math.abs(dateKey.hashCode())
        }
    }

    suspend fun getTodayVerbAssignment(userId: String = "user_default", dateKey: String): DailyVerbAssignment {
        val existing = dailyDaos.getAssignmentByDateSync(userId, dateKey)
        if (existing != null) return existing

        val dayIndex = getDayIndex(dateKey)
        val selectedVerb = getVerbForDay(dateKey, dayIndex)

        val newAssignment = DailyVerbAssignment(userId = userId, verbId = selectedVerb.id, assignedDate = dateKey)
        dailyDaos.insertAssignment(newAssignment)
        return dailyDaos.getAssignmentByDateSync(userId, dateKey) ?: newAssignment
    }

    fun getVerbForDay(dateKey: String, dayIndex: Int): DailyVerb {
        val baseVerb = allVerbs[dayIndex % allVerbs.size]
        val cycle = dayIndex / allVerbs.size
        if (cycle == 0) return baseVerb

        // Procedural variation for subsequent 6-month cycles
        val seed = kotlin.math.abs(dateKey.hashCode())
        val subject = when (seed % 3) {
            0 -> "They"
            1 -> "We"
            else -> "You"
        }
        val customEx = DailyVerbExample("$subject ${baseVerb.baseForm} every single day.", "$subject ہر روز ${baseVerb.urduMeaning} کرتے ہیں۔")
        return baseVerb.copy(
            id = "${baseVerb.id}_cycle_$cycle",
            examples = listOf(customEx) + baseVerb.examples.take(4)
        )
    }

    fun getVerbById(id: String): DailyVerb? {
        val cleanId = id.substringBefore("_cycle_")
        return allVerbs.find { it.id == cleanId || it.id == id }
    }
}
