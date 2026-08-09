package com.example.data.repository

data class PictureVocabularyImageData(
    val word: String,
    val translation: String,
    val imageUrl: String,
    val imageSearchQuery: String,
    val alt: String
)

object PictureVocabularyImageMap {

    private val masterMap = mapOf(
        // Food and Drinks
        "milk" to PictureVocabularyImageData(
            word = "Milk",
            translation = "دودھ",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0e/Milk_glass.jpg/640px-Milk_glass.jpg",
            imageSearchQuery = "milk carton isolated white background",
            alt = "Picture of Milk"
        ),
        "bread" to PictureVocabularyImageData(
            word = "Bread",
            translation = "روٹی",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/Kaisersemmel-.jpg/640px-Kaisersemmel-.jpg",
            imageSearchQuery = "loaf of bread isolated white background",
            alt = "Picture of Bread"
        ),
        "water" to PictureVocabularyImageData(
            word = "Water",
            translation = "پانی",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/50/Glass-of-water.jpg/640px-Glass-of-water.jpg",
            imageSearchQuery = "glass of water isolated white background",
            alt = "Picture of Water"
        ),
        "rice" to PictureVocabularyImageData(
            word = "Rice",
            translation = "چاول",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2d/Basmati_Rice_Cooked.jpg/640px-Basmati_Rice_Cooked.jpg",
            imageSearchQuery = "bowl of rice isolated white background",
            alt = "Picture of Rice"
        ),
        "egg" to PictureVocabularyImageData(
            word = "Egg",
            translation = "انڈا",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5e/Chicken_egg_2009-06-04.jpg/640px-Chicken_egg_2009-06-04.jpg",
            imageSearchQuery = "boiled egg isolated white background",
            alt = "Picture of Egg"
        ),
        "cheese" to PictureVocabularyImageData(
            word = "Cheese",
            translation = "پنیر",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a8/Swiss_cheese_cubes.jpg/640px-Swiss_cheese_cubes.jpg",
            imageSearchQuery = "swiss cheese wedge isolated white background",
            alt = "Picture of Cheese"
        ),
        "tea" to PictureVocabularyImageData(
            word = "Tea",
            translation = "چائے",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/45/A_cup_of_tea.jpg/640px-A_cup_of_tea.jpg",
            imageSearchQuery = "cup of tea isolated white background",
            alt = "Picture of Tea"
        ),
        "coffee" to PictureVocabularyImageData(
            word = "Coffee",
            translation = "کافی",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/45/A_small_cup_of_coffee.jpg/640px-A_small_cup_of_coffee.jpg",
            imageSearchQuery = "mug of hot coffee isolated white background",
            alt = "Picture of Coffee"
        ),
        "butter" to PictureVocabularyImageData(
            word = "Butter",
            translation = "مکھن",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Butter_block_01.jpg/640px-Butter_block_01.jpg",
            imageSearchQuery = "stick of butter isolated white background",
            alt = "Picture of Butter"
        ),
        "honey" to PictureVocabularyImageData(
            word = "Honey",
            translation = "شہد",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f6/Honey_in_jar.jpg/640px-Honey_in_jar.jpg",
            imageSearchQuery = "honey jar isolated white background",
            alt = "Picture of Honey"
        ),
        "juice" to PictureVocabularyImageData(
            word = "Juice",
            translation = "جوس",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Orange_juice_one_glass.jpg/640px-Orange_juice_one_glass.jpg",
            imageSearchQuery = "glass of orange juice isolated white background",
            alt = "Picture of Juice"
        ),
        "soup" to PictureVocabularyImageData(
            word = "Soup",
            translation = "سوپ",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1e/Hot_and_sour_soup.jpg/640px-Hot_and_sour_soup.jpg",
            imageSearchQuery = "bowl of vegetable soup isolated white background",
            alt = "Picture of Soup"
        ),
        "meat" to PictureVocabularyImageData(
            word = "Meat",
            translation = "گوشت",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ae/Plate_of_cooked_meat.jpg/640px-Plate_of_cooked_meat.jpg",
            imageSearchQuery = "cooked steak meat isolated white background",
            alt = "Picture of Meat"
        ),
        "fish" to PictureVocabularyImageData(
            word = "Fish",
            translation = "مچھلی",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/23/Grilled_salmon_fillet.jpg/640px-Grilled_salmon_fillet.jpg",
            imageSearchQuery = "cooked fish fillet isolated white background",
            alt = "Picture of Fish"
        ),
        "salad" to PictureVocabularyImageData(
            word = "Salad",
            translation = "سلاد",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/94/Salad_platter.jpg/640px-Salad_platter.jpg",
            imageSearchQuery = "fresh green salad bowl isolated white background",
            alt = "Picture of Salad"
        ),
        "cake" to PictureVocabularyImageData(
            word = "Cake",
            translation = "کیک",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/04/Pound_cake_slice.jpg/640px-Pound_cake_slice.jpg",
            imageSearchQuery = "slice of birthday cake isolated white background",
            alt = "Picture of Cake"
        ),
        "cookie" to PictureVocabularyImageData(
            word = "Cookie",
            translation = "بسکٹ",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f1/220_Chocolate_Chip_Cookies.jpg/640px-220_Chocolate_Chip_Cookies.jpg",
            imageSearchQuery = "chocolate chip cookie isolated white background",
            alt = "Picture of Cookie"
        ),
        "biscuit" to PictureVocabularyImageData(
            word = "Biscuit",
            translation = "بسکٹ",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/30/Rich_Tea_biscuit.jpg/640px-Rich_Tea_biscuit.jpg",
            imageSearchQuery = "crispy biscuit isolated white background",
            alt = "Picture of Biscuit"
        ),
        "sugar" to PictureVocabularyImageData(
            word = "Sugar",
            translation = "چینی",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a1/White_sugar_cubes.jpg/640px-White_sugar_cubes.jpg",
            imageSearchQuery = "sugar cubes bowl isolated white background",
            alt = "Picture of Sugar"
        ),
        "salt" to PictureVocabularyImageData(
            word = "Salt",
            translation = "نمک",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/34/Glass_salt_shaker.jpg/640px-Glass_salt_shaker.jpg",
            imageSearchQuery = "salt shaker isolated white background",
            alt = "Picture of Salt"
        ),
        "yogurt" to PictureVocabularyImageData(
            word = "Yogurt",
            translation = "دہی",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Greek_Yogurt.jpg/640px-Greek_Yogurt.jpg",
            imageSearchQuery = "bowl of white yogurt isolated white background",
            alt = "Picture of Yogurt"
        ),
        "pizza" to PictureVocabularyImageData(
            word = "Pizza",
            translation = "پزا",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a3/Eq_it-na_pizza-margherita_sep2005_sml.jpg/640px-Eq_it-na_pizza-margherita_sep2005_sml.jpg",
            imageSearchQuery = "pepperoni pizza slice isolated white background",
            alt = "Picture of Pizza"
        ),
        "burger" to PictureVocabularyImageData(
            word = "Burger",
            translation = "برگر",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/RedDot_Burger.jpg/640px-RedDot_Burger.jpg",
            imageSearchQuery = "cheeseburger isolated white background",
            alt = "Picture of Burger"
        ),
        "pasta" to PictureVocabularyImageData(
            word = "Pasta",
            translation = "پاستا",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/03/Bowl_of_spaghetti.jpg/640px-Bowl_of_spaghetti.jpg",
            imageSearchQuery = "bowl of pasta isolated white background",
            alt = "Picture of Pasta"
        ),
        "ice cream" to PictureVocabularyImageData(
            word = "Ice Cream",
            translation = "آئس کریم",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2e/Ice_cream_cone_3.jpg/640px-Ice_cream_cone_3.jpg",
            imageSearchQuery = "vanilla ice cream cone isolated white background",
            alt = "Picture of Ice Cream"
        ),
        "chocolate" to PictureVocabularyImageData(
            word = "Chocolate",
            translation = "چاکلیٹ",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/70/Chocolate_bar_02.jpg/640px-Chocolate_bar_02.jpg",
            imageSearchQuery = "chocolate bar isolated white background",
            alt = "Picture of Chocolate"
        ),
        "noodles" to PictureVocabularyImageData(
            word = "Noodles",
            translation = "نوڈلز",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/73/Ramen_noodle_bowl.jpg/640px-Ramen_noodle_bowl.jpg",
            imageSearchQuery = "bowl of yellow noodles isolated white background",
            alt = "Picture of Noodles"
        ),
        "sandwich" to PictureVocabularyImageData(
            word = "Sandwich",
            translation = "سینڈوچ",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/Sandwich_combo.jpg/640px-Sandwich_combo.jpg",
            imageSearchQuery = "club sandwich isolated white background",
            alt = "Picture of Sandwich"
        ),

        // Fruits and Vegetables
        "apple" to PictureVocabularyImageData(
            word = "Apple",
            translation = "سیب",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/15/Red_Apple.jpg/640px-Red_Apple.jpg",
            imageSearchQuery = "red apple isolated white background",
            alt = "Picture of Apple"
        ),
        "banana" to PictureVocabularyImageData(
            word = "Banana",
            translation = "کیلا",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8a/Banana-Single.jpg/640px-Banana-Single.jpg",
            imageSearchQuery = "yellow banana isolated white background",
            alt = "Picture of Banana"
        ),
        "orange" to PictureVocabularyImageData(
            word = "Orange",
            translation = "مالٹا",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c4/Orange-Fruit-Pieces.jpg/640px-Orange-Fruit-Pieces.jpg",
            imageSearchQuery = "fresh orange fruit isolated white background",
            alt = "Picture of Orange"
        ),
        "mango" to PictureVocabularyImageData(
            word = "Mango",
            translation = "آم",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/9/90/Haden_mango_portrait.jpg/640px-Haden_mango_portrait.jpg",
            imageSearchQuery = "ripe yellow mango isolated white background",
            alt = "Picture of Mango"
        ),
        "grapes" to PictureVocabularyImageData(
            word = "Grapes",
            translation = "انگور",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bb/Table_grapes_on_white.jpg/640px-Table_grapes_on_white.jpg",
            imageSearchQuery = "bunch of green grapes isolated white background",
            alt = "Picture of Grapes"
        ),
        "watermelon" to PictureVocabularyImageData(
            word = "Watermelon",
            translation = "تربوز",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c9/Watermelon_slice.jpg/640px-Watermelon_slice.jpg",
            imageSearchQuery = "fresh watermelon slice isolated white background",
            alt = "Picture of Watermelon"
        ),
        "strawberry" to PictureVocabularyImageData(
            word = "Strawberry",
            translation = "اسٹرابیری",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e1/Strawberries.jpg/640px-Strawberries.jpg",
            imageSearchQuery = "red strawberry isolated white background",
            alt = "Picture of Strawberry"
        ),
        "lemon" to PictureVocabularyImageData(
            word = "Lemon",
            translation = "لیموں",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e4/Lemon.jpg/640px-Lemon.jpg",
            imageSearchQuery = "yellow lemon isolated white background",
            alt = "Picture of Lemon"
        ),
        "pineapple" to PictureVocabularyImageData(
            word = "Pineapple",
            translation = "انناس",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cb/Pineapple_and_cross_section.jpg/640px-Pineapple_and_cross_section.jpg",
            imageSearchQuery = "tropical pineapple isolated white background",
            alt = "Picture of Pineapple"
        ),
        "potato" to PictureVocabularyImageData(
            word = "Potato",
            translation = "آلو",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ab/Patatas.jpg/640px-Patatas.jpg",
            imageSearchQuery = "raw potato isolated white background",
            alt = "Picture of Potato"
        ),
        "tomato" to PictureVocabularyImageData(
            word = "Tomato",
            translation = "ٹماٹر",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/89/Tomato_je.jpg/640px-Tomato_je.jpg",
            imageSearchQuery = "red tomato isolated white background",
            alt = "Picture of Tomato"
        ),
        "onion" to PictureVocabularyImageData(
            word = "Onion",
            translation = "پیاز",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/25/Onion_on_White.jpg/640px-Onion_on_White.jpg",
            imageSearchQuery = "red onion isolated white background",
            alt = "Picture of Onion"
        ),
        "carrot" to PictureVocabularyImageData(
            word = "Carrot",
            translation = "گاجر",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bd/Carrots_at_Ljubljana_Central_Market.jpg/640px-Carrots_at_Ljubljana_Central_Market.jpg",
            imageSearchQuery = "orange carrot isolated white background",
            alt = "Picture of Carrot"
        ),
        "cucumber" to PictureVocabularyImageData(
            word = "Cucumber",
            translation = "کھیرا",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/87/Cucumbers%2C_whole_and_sliced.jpg/640px-Cucumbers%2C_whole_and_sliced.jpg",
            imageSearchQuery = "green cucumber isolated white background",
            alt = "Picture of Cucumber"
        ),

        // Animals
        "cat" to PictureVocabularyImageData(
            word = "Cat",
            translation = "بلی",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Cat03.jpg/640px-Cat03.jpg",
            imageSearchQuery = "cute cat sitting isolated white background",
            alt = "Picture of Cat"
        ),
        "dog" to PictureVocabularyImageData(
            word = "Dog",
            translation = "کتا",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/26/Golden_Retriever_standing_sample.jpg/640px-Golden_Retriever_standing_sample.jpg",
            imageSearchQuery = "happy dog standing isolated white background",
            alt = "Picture of Dog"
        ),
        "cow" to PictureVocabularyImageData(
            word = "Cow",
            translation = "گائے",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0c/Cow_female_black_white.jpg/640px-Cow_female_black_white.jpg",
            imageSearchQuery = "dairy cow isolated white background",
            alt = "Picture of Cow"
        ),
        "horse" to PictureVocabularyImageData(
            word = "Horse",
            translation = "گھوڑا",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/de/Nokota_Horses_cropped.jpg/640px-Nokota_Horses_cropped.jpg",
            imageSearchQuery = "brown horse isolated white background",
            alt = "Picture of Horse"
        ),
        "lion" to PictureVocabularyImageData(
            word = "Lion",
            translation = "شیر",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/73/Lion_waiting_in_Namibia.jpg/640px-Lion_waiting_in_Namibia.jpg",
            imageSearchQuery = "male lion isolated white background",
            alt = "Picture of Lion"
        ),
        "elephant" to PictureVocabularyImageData(
            word = "Elephant",
            translation = "ہاتھی",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/37/African_Bush_Elephant.jpg/640px-African_Bush_Elephant.jpg",
            imageSearchQuery = "african elephant isolated white background",
            alt = "Picture of Elephant"
        ),
        "rabbit" to PictureVocabularyImageData(
            word = "Rabbit",
            translation = "خرگوش",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1f/Oryctolagus_cuniculus_RcStGerd.jpg/640px-Oryctolagus_cuniculus_RcStGerd.jpg",
            imageSearchQuery = "white rabbit isolated white background",
            alt = "Picture of Rabbit"
        ),

        // Birds and Insects
        "bird" to PictureVocabularyImageData(
            word = "Bird",
            translation = "پرندہ",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/45/Eurasian_blue_tit_1.jpg/640px-Eurasian_blue_tit_1.jpg",
            imageSearchQuery = "colorful singing bird isolated white background",
            alt = "Picture of Bird"
        ),
        "butterfly" to PictureVocabularyImageData(
            word = "Butterfly",
            translation = "تتلی",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a4/Monarch_Butterfly_In_Monarch_Butterfly_Biosphere_Reserve_2.jpg/640px-Monarch_Butterfly_In_Monarch_Butterfly_Biosphere_Reserve_2.jpg",
            imageSearchQuery = "monarch butterfly isolated white background",
            alt = "Picture of Butterfly"
        ),

        // Home, School, Objects
        "book" to PictureVocabularyImageData(
            word = "Book",
            translation = "کتاب",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Gospel_Book_-_Open.jpg/640px-Gospel_Book_-_Open.jpg",
            imageSearchQuery = "open book isolated white background",
            alt = "Picture of Book"
        ),
        "pen" to PictureVocabularyImageData(
            word = "Pen",
            translation = "قلم",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4a/Ballpoint-pen-parts.jpg/640px-Ballpoint-pen-parts.jpg",
            imageSearchQuery = "blue ballpoint pen isolated white background",
            alt = "Picture of Pen"
        ),
        "chair" to PictureVocabularyImageData(
            word = "Chair",
            translation = "کرسی",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cf/Office_chair.jpg/640px-Office_chair.jpg",
            imageSearchQuery = "wooden chair isolated white background",
            alt = "Picture of Chair"
        ),
        "table" to PictureVocabularyImageData(
            word = "Table",
            translation = "میز",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b3/Dining_Table.jpg/640px-Dining_Table.jpg",
            imageSearchQuery = "dining table isolated white background",
            alt = "Picture of Table"
        ),
        "bed" to PictureVocabularyImageData(
            word = "Bed",
            translation = "بستر",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f9/Hotel_room_bed.jpg/640px-Hotel_room_bed.jpg",
            imageSearchQuery = "cozy wooden bed isolated white background",
            alt = "Picture of Bed"
        ),
        "house" to PictureVocabularyImageData(
            word = "House",
            translation = "گھر",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/39/House_in_Redmond_Washington.jpg/640px-House_in_Redmond_Washington.jpg",
            imageSearchQuery = "family house isolated white background",
            alt = "Picture of House"
        ),
        "car" to PictureVocabularyImageData(
            word = "Car",
            translation = "گاڑی",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/25/2015_Toyota_Corolla_%28ZRE172R%29_Ascent_sedan_%282015-08-23%29_01.jpg/640px-2015_Toyota_Corolla_%28ZRE172R%29_Ascent_sedan_%282015-08-23%29_01.jpg",
            imageSearchQuery = "red car isolated white background",
            alt = "Picture of Car"
        ),
        "bus" to PictureVocabularyImageData(
            word = "Bus",
            translation = "بس",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/63/LT_471_%28LTZ_1471%29_Arriva_London_New_Routemaster.jpg/640px-LT_471_%28LTZ_1471%29_Arriva_London_New_Routemaster.jpg",
            imageSearchQuery = "red bus isolated white background",
            alt = "Picture of Bus"
        ),
        "phone" to PictureVocabularyImageData(
            word = "Phone",
            translation = "فون",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c4/Smartphones.jpg/640px-Smartphones.jpg",
            imageSearchQuery = "modern smartphone isolated white background",
            alt = "Picture of Phone"
        ),
        "laptop" to PictureVocabularyImageData(
            word = "Laptop",
            translation = "لیپ ٹاپ",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/16/Open_laptop.jpg/640px-Open_laptop.jpg",
            imageSearchQuery = "open laptop computer isolated white background",
            alt = "Picture of Laptop"
        ),
        "clock" to PictureVocabularyImageData(
            word = "Clock",
            translation = "گھڑی",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/Alarm_clock.jpg/640px-Alarm_clock.jpg",
            imageSearchQuery = "round alarm clock isolated white background",
            alt = "Picture of Clock"
        ),
        "sun" to PictureVocabularyImageData(
            word = "Sun",
            translation = "سورج",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b4/The_Sun_by_the_Atmospheric_Imaging_Assembly_of_NASA%27s_Solar_Dynamics_Observatory_-_20100819.jpg/640px-The_Sun_by_the_Atmospheric_Imaging_Assembly_of_NASA%27s_Solar_Dynamics_Observatory_-_20100819.jpg",
            imageSearchQuery = "bright sun isolated white background",
            alt = "Picture of Sun"
        ),
        "flower" to PictureVocabularyImageData(
            word = "Flower",
            translation = "پھول",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Red_Rose.jpg/640px-Red_Rose.jpg",
            imageSearchQuery = "red rose flower isolated white background",
            alt = "Picture of Flower"
        ),
        "tree" to PictureVocabularyImageData(
            word = "Tree",
            translation = "درخت",
            imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/eb/Ash_Tree_-_geograph.org.uk_-_590710.jpg/640px-Ash_Tree_-_geograph.org.uk_-_590710.jpg",
            imageSearchQuery = "green tree isolated white background",
            alt = "Picture of Tree"
        )
    )

    fun getImageUrlForWord(wordEn: String, assetKey: String = ""): String {
        val clean = wordEn.lowercase().trim()
        val data = masterMap[clean]
        if (data != null) return data.imageUrl

        // Deterministic high-quality image fallback per word to ensure uniqueness across options
        val hash = kotlin.math.abs((clean + assetKey).hashCode())
        val seed = (hash % 1000) + 1
        return "https://picsum.photos/seed/$clean-$seed/600/400"
    }

    fun getSearchQueryForWord(wordEn: String): String {
        val clean = wordEn.lowercase().trim()
        val data = masterMap[clean]
        return data?.imageSearchQuery ?: "$clean isolated white background"
    }

    fun getAltForWord(wordEn: String): String {
        return "Picture of $wordEn"
    }

    fun getImageData(wordEn: String): PictureVocabularyImageData? {
        return masterMap[wordEn.lowercase().trim()]
    }
}
