import json

new_islamic_idioms = [
    {
        "id": "inshallah_god_willing",
        "expression": "God willing (Insha'Allah)",
        "simpleMeaning": "Expressing hope or intention that something will happen with God's permission.",
        "urduMeaning": "اگر اللہ نے چاہا (ان شاء اللہ)",
        "arabicMeaning": "إن شاء الله / إعراب عن الأمل والتوفيق الإلهي",
        "arabicType": "MEANING",
        "example": "We will pass the examination tomorrow, Insha'Allah.",
        "level": "BEGINNER",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "mashallah_as_god_willed",
        "expression": "As God has willed (Masha'Allah)",
        "simpleMeaning": "Used to express appreciation, joy, praise, or thankfulness for an event or person.",
        "urduMeaning": "جو اللہ نے چاہا / سبحان اللہ، تعریف اور شکر گزاری کا اظہار",
        "arabicMeaning": "ماشاء الله / للثناء والإعجاب بالنعمة",
        "arabicType": "MEANING",
        "example": "Masha'Allah, your child reads the Quran so beautifully.",
        "level": "BEGINNER",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "alhamdulillah_praise_be_to_god",
        "expression": "Praise be to God (Alhamdulillah)",
        "simpleMeaning": "Used to express gratitude to God in all circumstances.",
        "urduMeaning": "تمام تعریفیں اللہ کے لیے ہیں (الحمد للہ)",
        "arabicMeaning": "الحمد لله / إظهار الشكر والحمد لله تعالى",
        "arabicType": "MEANING",
        "example": "Alhamdulillah, we arrived safely after a long journey.",
        "level": "BEGINNER",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "subhanallah_glory_be_to_god",
        "expression": "Glory be to God (Subhan'Allah)",
        "simpleMeaning": "Expressing awe and wonder at God's creation or majesty.",
        "urduMeaning": "اللہ پاک اور بے عیب ہے (سبحان اللہ)",
        "arabicMeaning": "سبحان الله / تنزيه الله والتعجب من قدرته",
        "arabicType": "MEANING",
        "example": "Look at the beauty of the sunset, Subhan'Allah!",
        "level": "BEGINNER",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "jazakallah_may_god_reward_you",
        "expression": "May God reward you with goodness (JazakAllahu Khair)",
        "simpleMeaning": "An Islamic phrase used to express deep gratitude to someone.",
        "urduMeaning": "اللہ آپ کو بہترین جزا دے (جزاک اللہ خیر)",
        "arabicMeaning": "جزاك الله خيراً / دعاء بالخير والتعبير عن الشكر العميق",
        "arabicType": "MEANING",
        "example": "JazakAllahu Khair for helping me with my lesson today.",
        "level": "BEGINNER",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "bismillah_in_the_name_of_god",
        "expression": "In the name of God (Bismillah)",
        "simpleMeaning": "Said before starting any action to invoke God's blessing.",
        "urduMeaning": "اللہ کے نام سے شروع (بسم اللہ)",
        "arabicMeaning": "بسم الله / التبرك باسم الله عند البدء بكل عمل",
        "arabicType": "MEANING",
        "example": "Always say Bismillah before taking a meal or starting work.",
        "level": "BEGINNER",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "barakallahu_feek_may_god_bless_you",
        "expression": "May God bless you (BarakAllahu Feek)",
        "simpleMeaning": "Praying for God's blessings upon another person.",
        "urduMeaning": "اللہ آپ کے کام اور عمل میں برکت دے (بارک اللہ فیک)",
        "arabicMeaning": "بارك الله فيك / الدعاء بالبركة والخير للآخرين",
        "arabicType": "MEANING",
        "example": "BarakAllahu Feek for your kindness and respectful manners.",
        "level": "BEGINNER",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "astaghfirullah_i_seek_forgiveness",
        "expression": "I seek forgiveness from God (Astaghfirullah)",
        "simpleMeaning": "Uttered to seek God's forgiveness or express regret.",
        "urduMeaning": "میں اللہ سے معافی مانگتا ہوں (استغفر اللہ)",
        "arabicMeaning": "أستغفر الله / طلب المغفرة والتوبة إلى الله",
        "arabicType": "MEANING",
        "example": "Astaghfirullah, I made a mistake and wish to correct it immediately.",
        "level": "BEGINNER",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "la_hawla_no_power_except_god",
        "expression": "No power or strength except with God (La hawla wa la quwwata illa billah)",
        "simpleMeaning": "A declaration of reliance on God's divine guidance and power during difficulty.",
        "urduMeaning": "اللہ کے بغیر نہ نیکی کی طاقت ہے نہ برائی سے بچنے کی (لا حول ولا قوة إلا بالله)",
        "arabicMeaning": "لا حول ولا قوة إلا بالله / التبرؤ من الحول والقوة والاستعانة بالله",
        "arabicType": "MEANING",
        "example": "In times of hardship, recite La hawla wa la quwwata illa billah for patience.",
        "level": "INTERMEDIATE",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "in_the_blink_of_an_eye_gods_will",
        "expression": "In the twinkling of an eye",
        "simpleMeaning": "Describing how swiftly God can alter circumstances.",
        "urduMeaning": "آنکھ جھپکنے میں / اللہ کے حکم سے پل بھر میں",
        "arabicMeaning": "بين عشية وضحاها / في لمحة عين",
        "arabicType": "MEANING",
        "example": "Things can change in the twinkling of an eye by divine decree.",
        "level": "INTERMEDIATE",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "honor_thy_parents",
        "expression": "To honor one's parents",
        "simpleMeaning": "Treating parents with ultimate respect, obedience, and kindness (Birr al-Walidayn).",
        "urduMeaning": "والدین کا احترام اور خدمت کرنا (والدین کے ساتھ حسنِ سلوک)",
        "arabicMeaning": "بر الوالدين / إكرامهما وإحسان المعاملة لهما",
        "arabicType": "MEANING",
        "example": "Honoring one's parents brings eternal blessings into a person's life.",
        "level": "BEGINNER",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "speak_good_or_remain_silent",
        "expression": "Speak good or remain silent",
        "simpleMeaning": "Encouraging constructive speech and avoiding harmful gossip or hurtful words.",
        "urduMeaning": "اچھی بات کہو یا خاموش رہو (گفتار کی تمیز)",
        "arabicMeaning": "قل خيراً أو اصمت / الحث على الكلمة الطيبة وحفظ اللسان",
        "arabicType": "MEANING",
        "example": "When tempers flare, remember the noble rule: speak good or remain silent.",
        "level": "INTERMEDIATE",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "a_smiling_face_is_charity",
        "expression": "A warm smile is charity",
        "simpleMeaning": "Showing a pleasant and friendly disposition to others is an act of kindness.",
        "urduMeaning": "مسکرانا بھی صدقہ ہے (خوش اخلاقی)",
        "arabicMeaning": "التبسم في وجه أخيك صدقة / إفشاء البشاشة والود",
        "arabicType": "MEANING",
        "example": "Greeting your brothers with a warm smile is charity in Islam.",
        "level": "BEGINNER",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "seek_knowledge_from_cradle_to_grave",
        "expression": "Seek knowledge from the cradle to the grave",
        "simpleMeaning": "Continuous lifelong dedication to acquiring wisdom and education.",
        "urduMeaning": "گود سے گور تک علم حاصل کرو (مسلسل تعلم)",
        "arabicMeaning": "اطلبوا العلم من المهد إلى اللحد / التعلم المستمر مدى الحياة",
        "arabicType": "MEANING",
        "example": "Age is no barrier when you seek knowledge from the cradle to the grave.",
        "level": "ADVANCED",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "tie_your_camel_and_trust_god",
        "expression": "Tie your camel and put trust in God",
        "simpleMeaning": "Take all proper practical precautions first, then place trust in God (Tawakkul).",
        "urduMeaning": "اونٹ باندھو اور اللہ پر توکل کرو (تدبیر کے ساتھ توکل)",
        "arabicMeaning": "اعقلها وتوكل / الأخذ بالأسباب مع الاعتماد على الله",
        "arabicType": "MEANING",
        "example": "Prepare well for your examination, tie your camel, and then put your trust in God.",
        "level": "INTERMEDIATE",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "lower_ones_gaze",
        "expression": "To lower one's gaze",
        "simpleMeaning": "Practicing modesty and avoiding inappropriate or lustful looks.",
        "urduMeaning": "اپنی نگاہیں نیچی رکھنا (حیا اور پاکدامنی)",
        "arabicMeaning": "غض البصر / الالتزام بالحياء والحشمة",
        "arabicType": "MEANING",
        "example": "Lowering one's gaze reflects true nobility and inner modesty.",
        "level": "INTERMEDIATE",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "keep_the_ties_of_kinship",
        "expression": "To maintain the ties of kinship",
        "simpleMeaning": "Visiting, respecting, and staying closely connected with relatives (Silat ar-Rahim).",
        "urduMeaning": "صلہ رحمی کرنا / رشتوں کو جوڑ کر رکھنا",
        "arabicMeaning": "صلة الرحم / التواصل مع الأقارب وإكرامهم",
        "arabicType": "MEANING",
        "example": "Maintaining the ties of kinship lengthens life and brings prosperity.",
        "level": "INTERMEDIATE",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "the_upper_hand_is_better",
        "expression": "The upper hand is better than the lower hand",
        "simpleMeaning": "The giving and charitable hand is superior to the receiving hand.",
        "urduMeaning": "دینے والا ہاتھ لینے والے ہاتھ سے بہتر ہے (سخاوت)",
        "arabicMeaning": "اليد العليا خير من اليد السفلى / العطاء والإنفاق أفضل من الأخذ",
        "arabicType": "MEANING",
        "example": "Always work hard to give charity, for the upper hand is better than the lower hand.",
        "level": "ADVANCED",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "walk_humbly_upon_the_earth",
        "expression": "Walk humbly upon the earth",
        "simpleMeaning": "Moving through life without arrogance, pride, or boasting.",
        "urduMeaning": "زمین پر عاجزی کے ساتھ چلنا (انکساری)",
        "arabicMeaning": "المشي على الأرض هونا / التواضع وعدم الكبرياء",
        "arabicType": "MEANING",
        "example": "True greatness lies in walking humbly upon the earth.",
        "level": "ADVANCED",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "patience_is_the_key_to_relief",
        "expression": "Patience is the key to relief",
        "simpleMeaning": "Remaining steadfast (Sabr) during hardship will eventually lead to ease.",
        "urduMeaning": "صبر کشائش کی کنجی ہے (صبر و تحمل)",
        "arabicMeaning": "الصبر مفتاح الفرج / التماس الفرج بالصبر والثبات",
        "arabicType": "MEANING",
        "example": "Do not lose heart; remember that patience is the key to relief.",
        "level": "INTERMEDIATE",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "purity_is_half_of_faith",
        "expression": "Purity is half of faith",
        "simpleMeaning": "Personal cleanliness, hygiene, and spiritual purity are fundamental.",
        "urduMeaning": "پاکیزگی نصف ایمان ہے (طہارت اور صفائی)",
        "arabicMeaning": "الطهور شطر الإيمان / أهمية النظافة والطهارة",
        "arabicType": "MEANING",
        "example": "Wash your hands and keep your environment clean, for purity is half of faith.",
        "level": "BEGINNER",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "actions_are_judged_by_intentions",
        "expression": "Actions are judged by intentions",
        "simpleMeaning": "The value and reward of every action depend on the sincerity of purpose (Niyyah).",
        "urduMeaning": "اعمال کا دارومدار نیتوں پر ہے (اخلاص نیت)",
        "arabicMeaning": "إنما الأعمال بالنيات / تقييم العمل بحسب القصد والإخلاص",
        "arabicType": "MEANING",
        "example": "Before helping others, ensure your heart is sincere, because actions are judged by intentions.",
        "level": "INTERMEDIATE",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "spread_peace_among_yourselves",
        "expression": "Spread peace among yourselves",
        "simpleMeaning": "Actively greeting others with Assalamu Alaikum and promoting tranquility.",
        "urduMeaning": "آپس میں سلام کو عام کرو (سلام اور سلامتی پھیلانا)",
        "arabicMeaning": "أفشوا السلام بينكم / نشر السلام والتحية بين الناس",
        "arabicType": "MEANING",
        "example": "To build a compassionate community, spread peace among yourselves wherever you go.",
        "level": "BEGINNER",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "best_among_you_are_best_in_character",
        "expression": "The best among you are best in character",
        "simpleMeaning": "True excellence is measured by moral character and polite conduct.",
        "urduMeaning": "تم میں سے بہترین وہ ہے جس کے اخلاق بہترین ہوں (حسنِ اخلاق)",
        "arabicMeaning": "خياركم أحاسنكم أخلاقاً / معيار الفضل بحسن الخلق",
        "arabicType": "MEANING",
        "example": "Focus on truthfulness and politeness, for the best among you are best in character.",
        "level": "INTERMEDIATE",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "a_good_word_is_charity",
        "expression": "A good word is charity",
        "simpleMeaning": "Speaking kindly, offering encouragement, or giving good counsel is an act of charity.",
        "urduMeaning": "اچھی بات کہنا بھی صدقہ ہے (شیریں کلامی)",
        "arabicMeaning": "الكلمة الطيبة صدقة / الكلام النافع والطيب",
        "arabicType": "MEANING",
        "example": "Comfort a friend in distress; remember that a good word is charity.",
        "level": "BEGINNER",
        "category": "ISLAMIC_CONTEXT"
    },
    {
        "id": "innalillahi_to_god_we_belong",
        "expression": "To God we belong and to Him we return (Inna Lillahi wa inna ilayhi raji'un)",
        "simpleMeaning": "Recited upon hearing news of loss, calamity, or death to express submission to God's decree.",
        "urduMeaning": "ہم اللہ کے ہیں اور اسی کی طرف لوٹ کر جانے والے ہیں (صبر اور تسلیم)",
        "arabicMeaning": "إنا لله وإنا إليه راجعون / الاسترجاع عند المصيبة والتسليم بقضاء الله",
        "arabicType": "MEANING",
        "example": "Upon hearing the sad news of his passing, we recited Inna Lillahi wa inna ilayhi raji'un.",
        "level": "INTERMEDIATE",
        "category": "ISLAMIC_CONTEXT"
    }
]

file_path = "app/src/main/assets/idioms.json"
with open(file_path, "r", encoding="utf-8") as f:
    existing_idioms = json.load(f)

existing_ids = set(item["id"] for item in existing_idioms)
added_count = 0
for item in new_islamic_idioms:
    if item["id"] not in existing_ids:
        existing_idioms.append(item)
        added_count += 1

with open(file_path, "w", encoding="utf-8") as f:
    json.dump(existing_idioms, f, ensure_ascii=False, indent=2)

print(f"Successfully added {added_count} Islamic idioms. Total idioms in file: {len(existing_idioms)}")
