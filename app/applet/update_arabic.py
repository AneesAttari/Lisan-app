import re

with open("app/src/main/java/com/example/data/repository/PictureVocabularySeedData.kt", "r", encoding="utf-8") as f:
    pv_content = f.read()

raw_items = re.findall(r'RawItem\("([^"]+)",\s*"([^"]+)"', pv_content)

with open("app/src/main/java/com/example/utils/ArabicTranslator.kt", "r", encoding="utf-8") as f:
    at_content = f.read()

merged_map = {}
pairs = re.findall(r'"([^"]+)"\s*->\s*"([^"]+)"', at_content)
for eng, arab in pairs:
    merged_map[eng.lower().strip()] = arab

extra_dict = {
    "stapler": "كباسة", "paperclip": "مشبك ورق", "paper clip": "مشبك ورق", "folder": "مجلد أوراق",
    "envelope": "ظرف", "stamp": "طابع بريد", "notebook": "دفتر ملاحظات", "binder": "مجلد أوراق",
    "highlighter": "قلم تمييز", "sticky note": "ورقة ملاحظات", "calculator": "آلة حاسبة", "clipboard": "لوحة أوراق",
    "printer": "طابعة", "scanner": "ماسح ضوئي", "shredder": "آلة تقطيع الورق", "tape": "شريط لاصق",
    "glue stick": "قلم صمغ", "thumbtack": "دبوس ضغط", "stamp pad": "وسادة حبر", "desk organizer": "منظم مكتب",
    "calendar": "تقويم", "briefcase": "حقيبة أوراق", "ruler": "مسطرة", "pen stand": "حامل أقلام",
    "correction tape": "شريط تصحيح", "punch machine": "خرامة ورق", "badge": "شارة",
    "guitar": "قيثارة", "piano": "بيانو", "violin": "كمان", "drums": "طبول", "flute": "ناي",
    "trumpet": "بوق", "saxophone": "ساكسفون", "accordion": "أكورديون", "harp": "قيثارة هارب",
    "banjo": "بانجو", "cello": "تشيلو", "clarinet": "كلارينيت", "harmonica": "هارمونيكا",
    "xylophone": "زيلوفون", "tambourine": "دف", "microphone": "ميكروفون", "microphon": "ميكروفون",
    "speaker": "مكبر صوت", "headphones": "سماعات رأس", "bagpipes": "مزمار القربة", "organ": "أورغ",
    "ukulele": "أوكوليلي", "triangle": "مثلث موسيقي", "gong": "جونغ", "cymbals": "صنوج",
    "metronome": "مترونوم", "sheet music": "نوتة موسيقية",
    "money": "نقود", "coin": "عملة معدنية", "banknote": "ورقة نقدية", "wallet": "محفظة",
    "piggy bank": "حصالة توفير", "credit card": "بطاقة ائتمان", "atm": "صراف آلي", "bank": "بنك",
    "cheque": "شيك", "gold": "ذهب", "silver": "فضة", "safe": "خزنة", "receipt": "إيصال",
    "invoice": "فاتورة", "tax": "ضريبة", "investment": "استثمار", "dollar": "دولار",
    "vault": "خزينة بنك", "currency": "عملة", "interest": "فائدة", "loan": "قرض",
    "budget": "ميزانية", "exchange": "صرافة", "price tag": "بطاقة سعر", "cashier": "كاشير", "scale": "ميزان",
    "mountain": "جبل", "hill": "تلة", "valley": "وادي", "river": "نهر", "lake": "بحيرة",
    "ocean": "محيط", "sea": "بحر", "waterfall": "شلال", "island": "جزيرة", "volcano": "بركان",
    "cave": "كهف", "desert": "صحراء", "canyon": "أخدود", "glacier": "نهر جليدي", "plateau": "هضبة",
    "plain": "سهل", "swamp": "مستنقع", "oasis": "واحة", "peninsula": "شبه جزيرة", "reef": "شعاب مرجانية",
    "coast": "ساحل", "cliff": "جرف", "dunes": "كثبان رملية", "lagoon": "بحيرة شاطئية", "strait": "مضيق", "delta": "دلتا",
    "whisk": "خلاط يدوي", "rolling pin": "شوبك", "measuring cup": "كوب قياس", "apron": "مريلة",
    "chef hat": "قبعة طباخ", "oven mitt": "قفاز الفرن", "cutting board": "لوح تقطيع", "grater": "مبشرة",
    "peeler": "مقشرة", "ladle": "مغرفة", "strainer": "مصفاة", "mixer": "خلاط", "timer": "مؤقت",
    "pan": "مقلاة", "pot": "قدر", "bowl": "وعاء", "tray": "صينية", "muffin tin": "قالب كعك",
    "spatula": "ملعقة تقليب", "cookie cutter": "قالب بسكويت", "sifter": "منخل", "piping bag": "كيس تزيين",
    "recipe book": "كتاب وصفات", "blender": "خلاط", "oil bottle": "زجاجة زيت",
    "clock": "ساعة", "watch": "ساعة يد", "hourglass": "ساعة رملية", "alarm clock": "منبه",
    "sunrise": "شروق الشمس", "sunset": "غروب الشمس", "morning": "صباح", "noon": "ظهر",
    "afternoon": "بعد الظهر", "evening": "مساء", "night": "ليل", "midnight": "منتصف الليل",
    "day": "يوم", "week": "أسبوع", "month": "شهر", "year": "سنة", "century": "قرن",
    "schedule": "جدول مواعيد", "stopwatch": "ساعة إيقاف", "stopwatches": "ساعة إيقاف", "timeline": "خط زمني",
    "hour": "ساعة", "minute": "دقيقة", "second": "ثانية", "season": "موسم", "today": "اليوم",
    "watering can": "مرشة ماء", "shovel": "مجرفة", "rake": "مجرفة أوراق", "flowerpot": "أصيص زهور",
    "wheelbarrow": "عربة حدائق", "pruning shears": "مقص تقليم", "garden hose": "خرطوم حديقة", "seeds": "بذور",
    "soil": "تربة", "compost": "سماد", "greenhouse": "دفيئة زراعية", "lawn mower": "جزازة عشب",
    "gloves": "قفازات", "trowel": "مجرفة صغيرة", "fence": "سياج", "sprinkler": "مرشة ماء",
    "trellis": "تعريشة", "weed": "أعشاب ضارة", "sprout": "برعم", "cactus": "صبار",
    "sunflower": "عباد الشمس", "rose": "وردة", "tulip": "توليب", "orchid": "أوركيد",
    "bamboo": "خيزران", "palm tree": "نخلة",
    "fire extinguisher": "طفاءة حريق", "first aid kit": "حقيبة إسعافات أولية", "ambulance": "سيارة إسعاف",
    "fire truck": "سيارة إطفاء", "police car": "سيارة شرطة", "lifebuoy": "طوق نجاة", "life jacket": "سترة نجاة",
    "smoke detector": "كاشف دخان", "emergency exit": "مخرج طوارئ", "siren": "صفارة إنذار",
    "safety cone": "قمع سلامة", "helmet": "خوذة حماية", "flashlight": "كشاف", "barrier": "حاجز أمان",
    "stretcher": "حمالة مرضى", "defibrillator": "جهاز إنعاش القلب", "gas mask": "قناع گاز",
    "safety glasses": "نظارات حماية", "megaphone": "مكبر صوت يدوي", "hydrant": "صنبور حريق",
    "alarm button": "زر إنذار", "hazard sign": "علامة خطر", "whistle": "صفارة", "flare": "مشعل طوارئ",
    "radio walkie": "جهاز لاسلكي", "lifeboat": "قارب نجاة",
    "teddy bear": "دب لعبة", "doll": "دمية", "toy train": "قطار لعبة", "building blocks": "مكعبات بناء",
    "puzzle": "لغز", "yo-yo": "يويو", "kite": "طائرة ورقية", "board game": "لعبة لوحية",
    "chess": "شطرنج", "playing cards": "أوراق لعب", "rubik's cube": "مكعب روبيك", "toy car": "سيارة لعبة",
    "rocking horse": "حصان هزاز", "marble": "كُلّة", "spinning top": "بلبل", "water gun": "مسدس ماء",
    "puppet": "دمية متحركة", "sandbox": "صندوق رمال", "tricycle": "دراجة ثلاثية", "skateboard": "لوح تزلج",
    "jump rope": "حبل قفز", "hula hoop": "طوق هولا هوب", "slingshot": "نبالة", "video game": "لعبة فيديو",
    "jack in box": "لعبة الصندوق", "trampoline": "ترامبولين",
    "toothbrush": "فرشاة أسنان", "toothpaste": "معجون أسنان", "soap": "صابون", "shampoo": "شامبو",
    "towel": "منشفة", "comb": "مشط", "hairbrush": "فرشاة شعر", "hairdryer": "مجفف شعر",
    "mirror": "مرآة", "scissors": "مقص", "nail clipper": "قلامة أظافر", "perfume": "عطر",
    "lotion": "مرطب", "razor": "شفرة حلاقة", "shaving cream": "رغوة حلاقة", "tissue": "منديل ورقي",
    "cotton swab": "أعواد قطن", "deodorant": "مزيل عرق", "sunscreen": "واقي شمس", "face wash": "غسول وجه",
    "lip balm": "مرطب شفاه", "nail polish": "طلاء أظافر", "makeup brush": "فرشاة مكياج", "shower gel": "جل استحمام",
    "bathrobe": "روب حمام", "sponge": "إسفنجة",
    "donut": "دونات", "cupcake": "كعكة صغيرة", "ice cream": "أيس كريم", "cookie": "بسكويت",
    "chocolate": "شوكولاتة", "candy": "حلوى", "lollipop": "مصاصة", "pancake": "بان كيك",
    "waffle": "وافل", "pie": "فطيرة", "brownie": "براوني", "pudding": "بودينغ",
    "popcorn": "فشار", "french fries": "بطاطس مقلية", "burger": "برجر", "pizza": "بيتزا",
    "hot dog": "هوت دوغ", "taco": "تاكو", "nachos": "ناتشوز", "milkshake": "ميلك شيك",
    "churros": "شوروس", "croissant": "كرواسون", "muffin": "مافن", "pretzel": "بريتزل",
    "cotton candy": "غزل البنات", "sundae": "مثلجات صنداي",
    "skyscraper": "ناطحة سحاب", "castle": "قلعة", "mosque": "مسجد", "temple": "معبد",
    "church": "كنيسة", "pyramid": "أهرامات", "bridge": "جسر", "tower": "برج",
    "windmill": "طاحونة هواء", "lighthouse": "منارة", "stadium": "ملعب", "cottage": "كوخ",
    "cabin": "كوخ خشبي", "igloo": "منزل جليدي", "barn": "حظيرة", "factory": "مصنع",
    "museum": "متحف", "library": "مكتبة", "hospital": "مستشفى", "school": "مدرسة",
    "hotel": "فندق", "supermarket": "سوبرمارکت", "airport": "مطار", "train station": "محطة قطار",
    "monument": "نصب تذكاري",
    "microscope": "مجهر", "telescope": "دوربين فلكي", "beaker": "كأس مخبري", "test tube": "أنبوب اختبار",
    "flask": "قارورة مخبرية", "magnet": "مغناطيس", "bunsen burner": "موقد بنسن", "dropper": "قطارة",
    "thermometer": "ميزان حرارة", "safety goggles": "نظارات أمان", "lab coat": "معطف مختبر",
    "petri dish": "طبق بتري", "mortar pestle": "هاون ومدقة", "pipette": "ماصة", "syringe": "حقنة",
    "atom model": "نموذج ذرة", "dna helix": "حلزون DNA", "prism": "منشور زجاجي", "compass": "بوصلة",
    "globe": "كرة أرضية", "battery": "بطارية", "circuit": "دائرة كهربائية", "voltmeter": "فولتمتر",
    "funnel": "قمع", "spatula lab": "ملعقة مختبر", "safety shower": "دش أمان",
    "traffic light": "إشارة مرور", "stop sign": "إشارة توقف", "pedestrian crossing": "ممر مشاة",
    "speed limit": "حد السرعة", "traffic cone": "قمع مرور", "parking": "موقف سيارات",
    "no entry": "ممنوع الدخول", "highway": "طريق سريع", "roundabout": "دوار مرور", "tunnel": "نفق",
    "overpass": "جسر علوي", "underpass": "نفق سفلي", "bus stop": "موقف حافلات", "gas station": "محطة وقود",
    "speed bump": "مطب صناعي", "road barrier": "حاجز طريق", "toll plaza": "محطة رسوم",
    "street light": "عمود إضاءة", "sidewalk": "رصيف مشاة", "one way": "اتجاه واحد",
    "yield sign": "إشارة إعطاء الأولوية", "warning sign": "إشارة تحذير", "detour": "طريق بديل",
    "dead end": "طريق مسدود", "traffic police": "شرطة المرور",
    "paintbrush": "فرشاة رسم", "palette": "لوحة ألوان", "easel": "حامل لوحة", "canvas": "قماش رسم",
    "sculpture": "تمثال", "clay": "صلصال", "sketchbook": "دفتر رسم", "crayon": "أقلام شمعية",
    "marker": "قلم تخطيط", "origami": "أوريغامي", "pottery": "صناعة الفخار", "mosaic": "فسيفساء",
    "stencil": "قالب رسم", "glitter": "برق", "beads": "خرز", "yarn": "خيط صوف",
    "embroidery": "تطريز", "needle": "إبرة", "thread": "خيط", "ribbon": "شريط",
    "glue": "صمغ", "calligraphy": "خط عربي", "charcoal": "فحم رسم", "watercolor": "ألوان مائية",
    "frame": "إطار", "craft knife": "سكين حرفي",
    "sun": "شمس", "moon": "قمر", "star": "نجمة", "planet": "كوكب", "earth": "كوكب الأرض",
    "mars": "كوكب المريخ", "jupiter": "كوكب المشترى", "rocket": "صاروخ", "astronaut": "رائد فضاء",
    "galaxy": "مجرة", "comet": "مذنب", "meteor": "شهاب", "satellite": "قمر صناعي",
    "alien": "كائن فضائي", "ufo": "طبق طائر", "black hole": "ثقب أسود", "eclipse": "كسوف / خسوف",
    "orbit": "مدار", "milky way": "درب التبانة", "constellation": "كوكبة نَجْمِيَّة",
    "spacesuit": "بدلة فضاء", "rover": "مركبة استكشاف", "nebula": "سديم", "atmosphere": "غلاف جوي",
    "space station": "محطة فضاء",
    "shark": "قرش", "whale": "حوت", "dolphin": "دلفين", "octopus": "أخطبوط", "jellyfish": "قنديل البحر",
    "sea turtle": "سلحفاة بحرية", "crab": "سلطعون", "lobster": "جراد البحر", "starfish": "نجم البحر",
    "seahorse": "فرس البحر", "coral": "مرجان", "squid": "حبار", "seal": "فقمة", "walrus": "فرس البحر",
    "submarine": "غواصة", "anchor": "مرساة", "seaweed": "عشب بحري", "shell": "صدفة", "pearl": "لؤلؤة",
    "wave": "موجة", "iceberg": "جبل جليدي", "ocean floor": "قاع المحيط", "rays": "سمك الشفنين",
    "clam": "محار", "penguin": "بطريق", "lighthouse": "منارة"
}

for k, v in extra_dict.items():
    merged_map[k.lower().strip()] = v

for eng, urdu in raw_items:
    clean = eng.lower().strip()
    if clean not in merged_map:
        merged_map[clean] = eng

kotlin_code = """package com.example.utils

object ArabicTranslator {
    fun translate(word: String): String {
        return when (word.lowercase().trim()) {
"""

for eng in sorted(merged_map.keys()):
    arab = merged_map[eng]
    eng_escaped = eng.replace('"', '\\"')
    arab_escaped = arab.replace('"', '\\"')
    kotlin_code += f'            "{eng_escaped}" -> "{arab_escaped}"\n'

kotlin_code += """            else -> word
        }
    }
}
"""

with open("app/src/main/java/com/example/utils/ArabicTranslator.kt", "w", encoding="utf-8") as f:
    f.write(kotlin_code)

print("ArabicTranslator.kt updated successfully with", len(merged_map), "translations.")
