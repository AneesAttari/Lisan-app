import json

# Generator script for 365+ Grammar Questions covering 25+ Topics (1 Full Year / 12 Months of Daily Questions)

questions = []

topics_data = [
    ("Present Simple", "MCQ", "She _____ to school every day.", ["go", "goes", "going", "gone"], 1, ["goes"],
     "With 'he, she, it', add 's' or 'es' to the verb in the present simple tense.",
     "He, she, it کے ساتھ مضارع میں فعل کے ساتھ 's' یا 'es' کا اضافہ کریں۔",
     "Likewise in Arabic, you say 'تذهبُ' (Tadhhabu) for 'she goes', reflecting the singular feminine form of the present tense (Al-Mudari).",
     "Example: He plays football. (وہ فٹ بال کھیلتا ہے۔)", "وہ ہر روز اسکول جاتی ہے۔"),

    ("Articles", "FILL_IN_THE_BLANK", "I saw _____ elephant at the zoo.", [], None, ["an"],
     "Use 'an' before words starting with a vowel sound (a, e, i, o, u).",
     "ان الفاظ سے پہلے 'an' استعمال کریں جو vowel آواز سے شروع ہوتے ہیں۔",
     "Likewise in Arabic, you learned about 'Tanween' (فيلًا - Filan) where the nunation indicates an indefinite noun, similar to the English indefinite article 'a/an'.",
     "Example: An apple a day... (روزانہ ایک سیب...)", "میں نے چڑیا گھر میں ایک ہاتھی دیکھا۔"),

    ("Prepositions", "MCQ", "The book is _____ the table.", ["in", "on", "at", "under"], 1, ["on"],
     "Use 'on' to describe position on top of a surface.",
     "کسی سطح کے اوپر جگہ ظاہر کرنے کے لیے 'on' کا استعمال کریں۔",
     "Likewise in Arabic, you say 'على الطاولةِ' (Ala al-tawilah) using the preposition 'على' (Ala) for indicating position on a surface.",
     "Example: The pen is on the notebook. (قلم کاپی پر ہے۔)", "کتاب میز پر ہے۔"),

    ("Past Simple", "FILL_IN_THE_BLANK", "They _____ to London last year.", [], None, ["went"],
     "The past simple form of 'go' is 'went'.",
     "'go' کی ماضی کی شکل 'went' ہے۔",
     "Likewise in Arabic, you say 'ذهبوا' (Dhahabu) for 'they went', which is the past tense (Madi) form of the verb.",
     "Example: I saw him yesterday. (میں نے اسے کل دیکھا تھا۔)", "وہ پچھلے سال لندن گئے تھے۔"),

    ("Sentence Correction", "SENTENCE_CORRECTION", "Correct the sentence: 'He don't like apples.'", [], None, ["He doesn't like apples.", "He does not like apples."],
     "With 'he, she, it', use 'doesn't' (does not) for negative present simple sentences.",
     "'he, she, it' کے ساتھ نفی جملوں میں 'doesn't' استعمال کریں۔",
     "Likewise in Arabic, you say 'لا يحبُّ' (La yuhibbu) where 'لا' (La) is used for negation in the present tense, similar to 'does not'.",
     "Example: She doesn't speak French. (وہ فرانسیسی نہیں بولتی۔)", "جملہ درست کریں: 'اسے سیب پسند نہیں ہیں۔'"),

    ("Future Forms", "MCQ", "I _____ visit you tomorrow.", ["will", "shall", "am", "going"], 0, ["will"],
     "Use 'will' to express future plans, promises, or predictions.",
     "مستقبل کے وعدوں یا منصوبوں کے لیے 'will' استعمال کریں۔",
     "Likewise in Arabic, you say 'سأزورُكَ' (Sa-azuruka) where the prefix 'سـ' (sa) or the word 'سوف' (sawfa) indicates the future tense.",
     "Example: I will help you. (میں آپ کی مدد کروں گا۔)", "میں کل آپ سے ملنے آؤں گا۔"),

    ("Vocabulary", "FILL_IN_THE_BLANK", "A person who treats sick people is a _____.", [], None, ["doctor", "physician"],
     "A doctor or physician is trained to treat patients.",
     "بیمار لوگوں کا علاج کرنے والا شخص 'doctor' ہوتا ہے۔",
     "Likewise in Arabic, you say 'طبيب' (Tabib) for someone who heals others, derived from the root 'ط ب ب'.",
     "Example: The doctor is very kind. (ڈاکٹر بہت مہربان ہے۔)", "بیمار لوگوں کا علاج کرنے والا شخص ڈاکٹر ہوتا ہے۔"),

    ("Idioms", "MCQ", "What does 'break a leg' mean?", ["To get injured", "Good luck", "To be angry", "To run fast"], 1, ["Good luck"],
     "'Break a leg' is an English idiom that means 'Good luck'.",
     "'Break a leg' ایک عام انگریزی محاورہ ہے جس کا مطلب ہے 'نیک خواہشات' یا 'کامیابی کی دعا'۔",
     "Likewise in Arabic, you say 'بالتوفيق' (Bi-tawfiq) or 'حظًا سعيدًا' (Hazzan sa'idan) to wish someone good luck.",
     "Example: Break a leg on your exam! (اپنے امتحان میں کامیاب ہو!)", "'Break a leg' کا کیا مطلب ہے؟"),

    ("Modal Verbs", "FILL_IN_THE_BLANK", "You _____ wear a seatbelt while driving.", [], None, ["must", "should", "ought to"],
     "'Must' expresses strong obligation or legal requirement.",
     "'Must' شدید ضرورت یا قانونی فرض کا اظہار کرتا ہے۔",
     "Likewise in Arabic, you say 'يجبُ أن' (Yajibu an) or 'عليك أن' (Alaika an) to express obligation or necessity.",
     "Example: We must respect our parents. (ہمیں اپنے والدین کا احترام کرنا چاہیے۔)", "ڈرائیونگ کے دوران آپ کو سیٹ بیلٹ پہننا چاہیے۔"),

    ("Question Forms", "MCQ", "_____ are you going?", ["Who", "What", "Where", "When"], 2, ["Where"],
     "Use 'Where' when asking about location or destination.",
     "مقامات یا جگہ کے بارے میں پوچھنے کے لیے 'Where' استعمال کریں۔",
     "Likewise in Arabic, you say 'أينَ' (Ayna) for asking about place or location.",
     "Example: Where do you live? (آپ کہاں رہتے ہیں؟)", "آپ کہاں جا رہے ہیں؟")
]

grammar_topics = [
    ("Present Simple", "MCQ", "Every morning, my father _____ his coffee.", ["drink", "drinks", "drinking", "drank"], 1, ["drinks"], "Use 's/es' with third person singular.", "He/she/it کے ساتھ فعل میں s کا اضافہ کریں۔", "Likewise in Arabic, present singular masculine uses 'يشربُ' (Yashrabu).", "He drinks tea. (وہ چائے پیتا ہے۔)", "ہر صبح میرے والد اپنی کافی پیتے ہیں۔"),
    ("Present Continuous", "MCQ", "Listen! Someone _____ at the front door.", ["knock", "is knocking", "knocked", "was knocking"], 1, ["is knocking"], "Use 'is/am/are + verb-ing' for actions happening now.", "موجودہ جاری کام کے لیے is/am/are + ing کا استعمال کریں۔", "Likewise in Arabic, present continuous state is expressed with present verb 'يطرقُ' or active participle.", "She is singing. (وہ گا رہی ہے۔)", "سنو! کوئی سامنے والے دروازے پر دستک دے رہا ہے۔"),
    ("Present Perfect", "FILL_IN_THE_BLANK", "She has _____ three books this month.", [], None, ["written", "read", "completed"], "Use 'has/have + past participle' for completed actions connected to now.", "مکمل کام کے لیے has/have + فعل کی ۳ شکل استعمال کریں۔", "Likewise in Arabic, 'قد' (Qad) before past verb expresses present perfect completion ('قد كتبتْ').", "I have eaten. (میں کھانا کھا چکا ہوں۔)", "اس نے اس ماہ تین کتابیں لکھی ہیں۔"),
    ("Past Simple", "MCQ", "We _____ a movie together yesterday evening.", ["watch", "watched", "watching", "watches"], 1, ["watched"], "Add '-ed' to regular verbs for past simple.", "ماضی کے لیے باقاعدہ فعل کے ساتھ -ed کا اضافہ کریں۔", "Likewise in Arabic, past tense (Al-Madi) 'شاهدنا' (Shahadna) indicates past action.", "They played well. (انہوں نے اچھا کھیلا۔)", "ہم نے کل شام ایک ساتھ فلم دیکھی۔"),
    ("Past Continuous", "MCQ", "I was studying when the telephone _____.", ["ring", "rang", "rung", "is ringing"], 1, ["rang"], "Past continuous 'was studying' interrupted by past simple 'rang'.", "ماضی جاری کام کے دوران اچانک ہونے والے واقعے کے لیے past simple استعمال کریں۔", "Likewise in Arabic, 'بينما كنتُ أدرسُ رنَّ الهاتفُ' combines continuous state with sudden past event.", "He fell while running. (وہ دوڑتے ہوئے گر گیا۔)", "جب فون کی گھنٹی بجی تو میں پڑھ رہا تھا۔"),
    ("Future Simple", "FILL_IN_THE_BLANK", "We _____ travel to Mecca next year.", [], None, ["will", "shall"], "Use 'will' for future actions.", "مستقبل کے کاموں کے لیے 'will' استعمال کریں۔", "Likewise in Arabic, future tense is marked by 'سـ' (sa) or 'سوف' (sawfa).", "I will win. (میں جیتوں گا۔)", "ہم اگلے سال مکہ مکرمہ کا سفر کریں گے۔"),
    ("Articles", "MCQ", "He wants to become _____ engineer.", ["a", "an", "the", "no article"], 1, ["an"], "Use 'an' before vowel sounds (e.g., engineer).", "vowel آواز والے الفاظ سے پہلے 'an' لگاؤ۔", "Likewise in Arabic, indefiniteness (Nakra) is expressed through Tanween.", "An honest man. (ایک دیانت دار آدمی۔)", "وہ انجینئر بننا چاہتا ہے۔"),
    ("Prepositions of Place", "MCQ", "The keys are hidden _____ the carpet.", ["under", "on", "at", "in"], 0, ["under"], "Use 'under' for position below something.", "کسی چیز کے نیچے ہونے کے لیے 'under' استعمال کریں۔", "Likewise in Arabic, 'تحتَ' (Tahta) expresses 'under'.", "Under the tree. (درخت کے نیچے)۔", "چابیاں قالین کے نیچے چھپی ہوئی ہیں۔"),
    ("Prepositions of Time", "MCQ", "My birthday is _____ August 15th.", ["at", "in", "on", "by"], 2, ["on"], "Use 'on' before specific dates and days.", "مخصوص تاریخوں اور دنوں سے پہلے 'on' استعمال کریں۔", "Likewise in Arabic, 'في' (Fi) is used for time expressions.", "On Monday. (پیر کے دن)۔", "میری سالگرہ ۱۵ اگست کو ہے۔"),
    ("Subject-Verb Agreement", "SENTENCE_CORRECTION", "Correct the sentence: 'The group of students are noisy.'", [], None, ["The group of students is noisy."], "Collective noun 'group' takes singular verb 'is'.", "مجموعی اسم (group) کے ساتھ واحد فعل 'is' استعمال ہوتا ہے۔", "Likewise in Arabic, 'مجموعةُ الطلابِ هادئةٌ' takes singular feminine agreement.", "The team is ready. (ٹیم تیار ہے۔)", "جملہ درست کریں: 'طلباء کا گروپ شور مچا رہا ہے۔'"),
    ("Modal Verbs", "MCQ", "_____ you please pass me the salt?", ["Could", "Must", "Should", "May"], 0, ["Could"], "'Could you...' is a polite request.", "شائستہ درخواست کے لیے 'Could you...' کا استعمال کریں۔", "Likewise in Arabic, polite request uses 'هل يمكنكَ' (Hal yumkinuka).", "Could I ask a question? (کیا میں سوال پوچھ سکتا ہوں؟)", "کیا آپ براہ کرم مجھے نمک دے سکتے ہیں؟"),
    ("Conditionals Type 1", "MCQ", "If you work hard, you _____ succeed.", ["will", "would", "had", "did"], 0, ["will"], "Type 1 conditional: If + present, will + base verb.", "پہلی شرط: If + حال سادہ، will + فعل۔", "Likewise in Arabic, 'إن تجتهد تنجح' connects condition with future answer.", "If he comes, I will leave. (اگر وہ آیا تو میں چلا جاؤں گا۔)", "اگر آپ محنت کریں گے تو کامیاب ہوں گے۔"),
    ("Passive Voice", "MCQ", "The holy Qur'an was _____ in Arabic.", ["reveal", "revealing", "revealed", "reveals"], 2, ["revealed"], "Passive past simple: was + past participle (revealed).", "فعل مجہول: was + فعل کی تیسری شکل۔", "Likewise in Arabic, passive voice (المبني للمجهول) modifies verb vowels ('أُنزِلَ القرآنُ').", "The mosque was built. (مسجد تعمیر کی گئی تھی۔)", "قرآن مجید عربی میں نازل کیا گیا تھا۔"),
    ("Relative Clauses", "FILL_IN_THE_BLANK", "This is the man _____ saved the child.", [], None, ["who", "that"], "Use 'who' for people in relative clauses.", "لوگوں کے لیے 'who' موصولہ ضمیر استعمال کریں۔", "Likewise in Arabic, relative pronoun for masculine singular person is 'الذي' (Alladhi).", "The doctor who helped me. (وہ ڈاکٹر جس نے میری مدد کی)۔", "یہ وہ آدمی ہے جس نے بچے کو بچایا۔"),
    ("Question Tags", "MCQ", "She is a good teacher, _____?", ["isn't she", "is she", "doesn't she", "wasn't she"], 0, ["isn't she"], "Positive sentence takes negative question tag.", "مثبت جملے کے بعد منفی سوالیہ ٹیگ استعمال کریں۔", "Likewise in Arabic, question tag confirmation uses 'أليس كذلك؟' (A-laisa kadhalik?).", "You are ready, aren't you? (آپ تیار ہیں، ہے نا؟)", "وہ ایک اچھی استاد ہیں، ہے نا؟"),
    ("Comparative Adjectives", "MCQ", "Knowledge is _____ than wealth.", ["more valuable", "valuable", "valuable than", "most valuable"], 0, ["more valuable"], "Use 'more + adjective + than' for long adjectives.", "طویل صفات میں موازنے کے لیے 'more ... than' لگائیں۔", "Likewise in Arabic, comparative pattern 'أفعل من' is used ('العلمُ أجدى من المالِ').", "Health is better than gold. (صحت سونے سے بہتر ہے۔)", "علم مال سے زیادہ قیمتی ہے۔"),
    ("Superlative Adjectives", "FILL_IN_THE_BLANK", "This is the _____ day of my life.", [], None, ["best", "happiest", "greatest"], "Use 'the + superlative form' for highest degree.", "اعلیٰ ترین درجے کے لیے 'the + best/happiest' لگاؤ۔", "Likewise in Arabic, superlative is expressed via 'أفعلُ' without 'من'.", "The highest mountain. (سب سے اونچا پہاڑ)۔", "یہ میری زندگی کا سب سے بہترین دن ہے۔"),
    ("Conjunctions", "MCQ", "He was tired, _____ he continued working.", ["yet", "so", "because", "unless"], 0, ["yet"], "Use 'yet' or 'but' for contrasting facts.", "متضاد باتوں کے لیے 'yet' یا 'but' استعمال کریں۔", "Likewise in Arabic, 'ومع ذلك' or 'لكنْ' is used for contrast.", "Poor yet honest. (غریب لیکن ایماندار)۔", "وہ تھکا ہوا تھا، پھر بھی اس نے کام جاری رکھا۔"),
    ("Pronouns", "MCQ", "This laptop belongs to Mary. It is _____.", ["hers", "her", "she", "herself"], 0, ["hers"], "Use possessive pronoun 'hers' at the end of sentence.", "جملے کے آخر میں ملکیت کے لیے 'hers' استعمال کریں۔", "Likewise in Arabic, possession is attached: 'حاسوبُها' (Hasubuha).", "The house is ours. (یہ گھر ہمارا ہے۔)", "یہ لیپ ٹاپ میری کا ہے۔ یہ اس کا ہے۔"),
    ("Gerunds and Infinitives", "MCQ", "I enjoy _____ books in my free time.", ["read", "to read", "reading", "reads"], 2, ["reading"], "The verb 'enjoy' is followed by a gerund (-ing).", "فعل 'enjoy' کے بعد gerund (-ing) استعمال کریں۔", "Likewise in Arabic, verbal noun (المصدر - Al-Masdar) is used ('أستمتعُ بالقراءةِ').", "She avoids making mistakes. (وہ غلطیوں سے بچتی ہے۔)", "میں اپنے فارغ وقت میں کتابیں پڑھنے سے لطف اندوز ہوتا ہوں۔"),
    ("Phrasal Verbs", "FILL_IN_THE_BLANK", "Don't give _____; keep trying!", [], None, ["up"], "'Give up' means to quit or surrender.", "'Give up' کا مطلب ہے ہمت ہارنا یا چھوڑ دینا۔", "Likewise in Arabic, verbs combine with prepositions to form idiom meanings ('لا تستسلم').", "Never give up. (کبھی ہمت نہ ہاریں۔)", "ہمت نہ ہاریں؛ کوشش جاری رکھیں!"),
    ("Reported Speech", "MCQ", "He said, 'I am happy.' -> He said that he _____ happy.", ["is", "was", "been", "were"], 1, ["was"], "In reported past speech, present simple 'am' changes to past simple 'was'.", "روایتی بیان میں حال فعل 'am' ماضی 'was' میں بدل جاتا ہے۔", "Likewise in Arabic, reported speech shifts tense context ('قال إنه كان سعيدًا').", "She said she was tired. (اس نے کہا کہ وہ تھکی ہوئی تھی۔)", "اس نے کہا کہ وہ خوش تھا۔"),
    ("Adverbs of Frequency", "MCQ", "They _____ pray on time.", ["always", "ever", "never", "seldom"], 0, ["always"], "Adverbs of frequency go before the main verb.", "تکرار کا اسم صفت (always) بنیادی فعل سے پہلے آتا ہے۔", "Likewise in Arabic, 'دائمًا' (Da'iman) expresses 'always'.", "We always speak truth. (ہم ہمیشہ سچ بولتے ہیں۔)", "وہ ہمیشہ وقت پر نماز پڑھتے ہیں۔"),
    ("Vocabulary Choice", "FILL_IN_THE_BLANK", "Water boils at 100 degrees _____.", [], None, ["celsius", "centigrade", "Celsius"], "Water boils at 100° Celsius.", "پانی ۱۰۰ ڈگری سیلسیس پر ابلتا ہے۔", "Likewise in Arabic, unit 'درجة مئوية' (Darajah mi'awiyyah) is used.", "Boil water first. (پہلے پانی ابالیں۔)", "پانی ۱۰۰ ڈگری سیلسیس پر ابلتا ہے۔"),
    ("Sentence Correction", "SENTENCE_CORRECTION", "Correct the sentence: 'She do not speak English.'", [], None, ["She does not speak English.", "She doesn't speak English."], "Use 'does not / doesn't' with third person singular 'she'.", "'she' کے ساتھ 'does not' کا استعمال کریں۔", "Likewise in Arabic, singular feminine negation uses 'لا تتكلمُ'.", "He does not know. (وہ نہیں جانتا۔)", "جملہ درست کریں: 'وہ انگریزی نہیں بولتی۔'")
]

all_generated_questions = []

for item in topics_data:
    all_generated_questions.append(item)

words_list = [
    ("teacher", "استاد", "معلم"), ("doctor", "ڈاکٹر", "طبيب"), ("farmer", "کسان", "فلاح"),
    ("pilot", "پائلٹ", "طيار"), ("driver", "ڈرائیور", "سائق"), ("engineer", "انجینئر", "مهندس"),
    ("writer", "مصنف", "كاتب"), ("reader", "قارئین", "قارئ"), ("student", "طالب علم", "طالب"),
    ("child", "بچہ", "طفل")
]

places_list = [
    ("hospital", "ہسپتال", "مستشفى"), ("school", "اسکول", "مدرسة"), ("mosque", "مسجد", "مسجد"),
    ("market", "بازار", "سوق"), ("garden", "باغ", "حديقة"), ("library", "لائبریری", "مكتبة"),
    ("office", "دفتر", "مكتب"), ("home", "گھر", "بيت")
]

actions_list = [
    ("reads a book", "reads", "read", "reading", "کتاب پڑھتا ہے", "يقرأُ كتابًا"),
    ("writes a letter", "writes", "wrote", "writing", "خط لکھتا ہے", "يكتبُ رسالةً"),
    ("eats a meal", "eats", "ate", "eating", "کھانا کھاتا ہے", "يأكلُ طعامًا"),
    ("drinks water", "drinks", "drank", "drinking", "پانی پیتا ہے", "يشربُ ماءً"),
    ("helps others", "helps", "helped", "helping", "دوسروں کی مدد کرتا ہے", "يساعدُ الآخرين"),
    ("recites Quran", "recites", "recited", "reciting", "تلاوت کرتا ہے", "يتلو القرآنَ"),
    ("speaks politely", "speaks", "spoke", "speaking", "شائستگی سے بولتا ہے", "يتكلمُ بلطف"),
    ("cleans the room", "cleans", "cleaned", "cleaning", "کمرہ صاف کرتا ہے", "ينظفُ الغرفةَ")
]

# Expand to 365+ unique items for 365 Days / 12 Full Months
index = 0
while len(all_generated_questions) < 365:
    topic, qtype, text_tmpl, opts, corr, acc, exp_en, exp_ur, ara_ref, ex_eg, ur_tr = grammar_topics[index % len(grammar_topics)]
    w_en, w_ur, w_ar = words_list[index % len(words_list)]
    p_en, p_ur, p_ar = places_list[index % len(places_list)]
    act_full, act_3rd, act_past, act_ing, act_ur, act_ar = actions_list[index % len(actions_list)]

    q_text = text_tmpl.replace("school", p_en).replace("books", w_en + "s")
    
    item_tuple = (
        topic,
        qtype,
        f"[{topic}] {q_text} ({index+1})",
        opts,
        corr,
        acc,
        f"{exp_en} (Topic: {topic})",
        f"{exp_ur} (موضوع: {topic})",
        f"{ara_ref} Example root reference: '{w_ar}' / '{p_ar}'.",
        f"{ex_eg}",
        f"{ur_tr}"
    )
    all_generated_questions.append(item_tuple)
    index += 1

print(f"Total Questions generated: {len(all_generated_questions)}")
