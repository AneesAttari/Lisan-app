package com.example.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class PriceFreshnessStatus(val labelEn: String, val labelUr: String) {
    LIVE("LIVE Real-Time API Rate", "لائیو مارکیٹ ریٹ"),
    GROUNDED_SEARCH("Grounded Web Search", "ویب سرچ ریٹ"),
    CACHED("CACHED Session Rate", "محفوظ شدہ مارکیٹ ریٹ"),
    FALLBACK("FALLBACK Market Benchmark", "مارکیٹ ریفرینس بینچ مارک")
}

@Serializable
data class CommodityPriceInfo(
    val commodityId: String,
    val commodityNameEn: String,
    val commodityNameUr: String,
    val pricePerKg: Double,
    val pricePerMaund: Double = pricePerKg * 40.0,
    val currency: String = "PKR",
    val currencySymbol: String = "Rs.",
    val sourceName: String = "Pakistan Mandi Commodity Bulletin",
    val freshnessStatus: PriceFreshnessStatus = PriceFreshnessStatus.LIVE,
    val timestampMs: Long = System.currentTimeMillis(),
    val formattedTimestamp: String = ""
)

sealed class CommodityPricesUiState {
    object Idle : CommodityPricesUiState()
    object Loading : CommodityPricesUiState()
    data class Success(
        val prices: Map<FitranaCommodity, CommodityPriceInfo>,
        val freshnessStatus: PriceFreshnessStatus,
        val sourceName: String,
        val formattedTimestamp: String
    ) : CommodityPricesUiState()
    data class Error(val message: String) : CommodityPricesUiState()
}

enum class SilverPriceType(val labelEn: String, val labelUr: String) {
    LOCAL_MARKET("Local Market Rate", "مقامی مارکیٹ ریٹ"),
    RETAIL("Retail / Jewellery Rate", "ریٹیل / صرافہ ریٹ"),
    SPOT("National Sarafa Benchmark", "قومی اسپاٹ ریٹ"),
    INTERNATIONAL_SPOT("International Silver Spot Price", "بین الاقوامی اسپاٹ ریٹ"),
    BENCHMARK("Reference Market Benchmark", "مارکیٹ بینچ مارک ریٹ")
}

@Serializable
data class LiveSilverRate(
    @SerialName("price_per_gram")
    val pricePerGram: Double? = null,
    @SerialName("price_per_tola")
    val pricePerTola: Double? = null,
    @SerialName("price_per_troy_oz")
    val pricePerTroyOz: Double? = null,
    @SerialName("price_per_10_gram")
    val pricePer10Gram: Double? = null,
    @SerialName("price_per_kg")
    val pricePerKg: Double? = null,
    @SerialName("currency")
    val currency: String = "PKR",
    @SerialName("currency_symbol")
    val currencySymbol: String = "Rs.",
    @SerialName("purity")
    val purity: String = "999 Fine Silver (24K)",
    @SerialName("purity_factor")
    val purityFactor: Double = 1.0,
    @SerialName("requested_city")
    val requestedCity: String = "Multan",
    @SerialName("requested_country")
    val requestedCountry: String = "Pakistan",
    @SerialName("applied_market_city")
    val appliedMarketCity: String = "Multan / Pakistan Sarafa Bazaar",
    @SerialName("is_city_specific")
    val isCitySpecific: Boolean = true,
    @SerialName("source_website")
    val sourceWebsite: String = "Sarafa.pk",
    @SerialName("source_url")
    val sourceUrl: String = "",
    @SerialName("retrieval_date_time")
    val retrievedDateTime: String = "",
    @SerialName("price_type")
    val priceType: String = "LOCAL_MARKET",
    @SerialName("is_verified")
    val isVerified: Boolean = true,
    @SerialName("city_rate_note")
    val cityRateNote: String = "",
    @SerialName("summary")
    val summary: String = "",
    @SerialName("converted_from_international")
    val convertedFromInternational: Boolean = false,
    @SerialName("original_source_currency")
    val originalSourceCurrency: String? = null,
    @SerialName("original_source_price")
    val originalSourcePrice: Double? = null,
    @SerialName("original_source_unit")
    val originalSourceUnit: String? = null,
    @SerialName("exchange_rate_note")
    val exchangeRateNote: String? = null,
    val freshnessStatus: PriceFreshnessStatus = PriceFreshnessStatus.LIVE,
    val timestampMs: Long = System.currentTimeMillis()
)

@Serializable
data class LiveGoldRate(
    @SerialName("price_per_gram")
    val pricePerGram: Double? = null,
    @SerialName("price_per_tola")
    val pricePerTola: Double? = null,
    @SerialName("price_per_troy_oz")
    val pricePerTroyOz: Double? = null,
    @SerialName("price_per_10_gram")
    val pricePer10Gram: Double? = null,
    @SerialName("currency")
    val currency: String = "PKR",
    @SerialName("currency_symbol")
    val currencySymbol: String = "Rs.",
    @SerialName("purity")
    val purity: String = "24K Fine Gold (99.9%)",
    @SerialName("purity_factor")
    val purityFactor: Double = 1.0,
    @SerialName("requested_city")
    val requestedCity: String = "Multan",
    @SerialName("requested_country")
    val requestedCountry: String = "Pakistan",
    @SerialName("applied_market_city")
    val appliedMarketCity: String = "Multan / Pakistan Sarafa Bazaar",
    @SerialName("is_city_specific")
    val isCitySpecific: Boolean = true,
    @SerialName("source_website")
    val sourceWebsite: String = "Sarafa.pk",
    @SerialName("source_url")
    val sourceUrl: String = "",
    @SerialName("retrieval_date_time")
    val retrievedDateTime: String = "",
    @SerialName("price_type")
    val priceType: String = "LOCAL_MARKET",
    @SerialName("is_verified")
    val isVerified: Boolean = true,
    @SerialName("city_rate_note")
    val cityRateNote: String = "",
    @SerialName("summary")
    val summary: String = "",
    val freshnessStatus: PriceFreshnessStatus = PriceFreshnessStatus.LIVE,
    val timestampMs: Long = System.currentTimeMillis()
)

enum class SilverUnit(val id: String, val labelEn: String, val labelUr: String, val gramsFactor: Double) {
    TOLA("tola", "Tola", "تولہ", 11.6638125),
    GRAMS("grams", "Grams", "گرام", 1.0),
    TEN_GRAMS("10_grams", "10 Grams", "10 گرام", 10.0),
    TROY_OUNCE("troy_oz", "Troy Ounce", "ٹرائے اونس", 31.1034768),
    KILOGRAM("kg", "Kilogram", "کلوگرام", 1000.0)
}

enum class GoldUnit(val id: String, val labelEn: String, val labelUr: String, val gramsFactor: Double) {
    TOLA("tola", "Tola", "تولہ", 11.6638125),
    GRAMS("grams", "Grams", "گرام", 1.0),
    TEN_GRAMS("10_grams", "10 Grams", "10 گرام", 10.0),
    TROY_OUNCE("troy_oz", "Troy Ounce", "ٹرائے اونس", 31.1034768)
}

enum class SilverPurity(val id: String, val labelEn: String, val labelUr: String, val purityFactor: Double) {
    FINE_999("999", "999 Fine Silver (24K / 99.9%)", "999 خالص چاندی (99.9%)", 1.0),
    STERLING_925("925", "925 Sterling Silver (92.5%)", "925 سٹرلنگ چاندی (92.5%)", 0.925),
    TRADITIONAL_900("900", "900 Traditional Silver (90.0%)", "900 روایتی چاندی (90.0%)", 0.900)
}

enum class GoldPurity(val id: String, val labelEn: String, val labelUr: String, val purityFactor: Double) {
    K24_FINE("24k", "24K Fine Gold (99.9%)", "24 قیراط خالص سونا (99.9%)", 1.0),
    K22_JEWELLERY("22k", "22K Jewellery Gold (91.6%)", "22 قیراط زیوراتی سونا (91.6%)", 22.0 / 24.0),
    K21_GULF("21k", "21K Gold (87.5%)", "21 قیراط سونا (87.5%)", 21.0 / 24.0),
    K18_ORNAMENT("18k", "18K Gold (75.0%)", "18 قیراط سونا (75.0%)", 18.0 / 24.0)
}

enum class IslamicCalculatorTab(val id: String, val titleEn: String, val titleUr: String, val iconEmoji: String) {
    HAQ_MEHR("haq_mehr", "Haq Mehr", "مہرِ شرعی", "💍"),
    ZAKAT("zakat", "Zakat", "زکوٰۃ", "💰"),
    FITRANA("fitrana", "Fitrana", "فطرانہ", "🌾"),
    USHR("ushr", "Ushr", "عشر", "🌱")
}

enum class HaqMehrMetalType(val id: String, val labelEn: String, val labelUr: String) {
    SILVER("silver", "Silver (چاندی)", "چاندی"),
    GOLD("gold", "Gold (سونا)", "سونا")
}

enum class UshrCropCategory(val id: String, val labelEn: String, val labelUr: String) {
    GRAINS("grains", "Grains & Cereals (Wheat, Rice, Corn)", "اناج و غلہ (گندم، چاول، مکئی)"),
    CASH_CROPS("cash_crops", "Cash Crops (Cotton, Sugarcane)", "نقد آور فصلیں (کپاس، کماد / گنا)"),
    FRUITS("fruits", "Fruits & Orchards (Mango, Citrus, Dates)", "پھل و باغات (آم، مالٹا، کھجور)"),
    VEGETABLES("vegetables", "Vegetables & Pulses", "سبزیاں و دالیں")
}

enum class ZakatNisabStandard(
    val id: String,
    val titleEn: String,
    val titleUr: String,
    val descriptionUr: String,
    val weightGrams: Double,
    val weightTola: Double,
    val isSilver: Boolean
) {
    SILVER(
        id = "silver",
        titleEn = "Silver Standard (52.5 Tola)",
        titleUr = "چاندی کا نصاب (52.5 تولہ)",
        descriptionUr = "چاندی کا نصاب 52.5 تولہ (612.35 گرام) ہے۔ جمہور حنفی فقہاء اور مفتیان کے نزدیک نقدی، سونے اور چاندی کے ملے جلے اموال میں غرباء کے مفاد کی خاطر چاندی کے نصاب کا اعتبار کیا جاتا ہے۔",
        weightGrams = 612.35,
        weightTola = 52.50,
        isSilver = true
    ),
    GOLD(
        id = "gold",
        titleEn = "Gold Standard (7.5 Tola)",
        titleUr = "سونے کا نصاب (7.5 تولہ)",
        descriptionUr = "سونے کا نصاب 7.5 تولہ (87.48 گرام خالص سونا) ہے۔ اگر کسی کے پاس صرف اور صرف سونا ہو اور کوئی نقد رقم، چاندی یا مالِ تجارت نہ ہو تو یہ نصاب دیکھا جاتا ہے۔",
        weightGrams = 87.48,
        weightTola = 7.50,
        isSilver = false
    )
}

enum class FitranaCommodity(
    val id: String,
    val nameEn: String,
    val nameUr: String,
    val quantityDescUr: String,
    val requiredKgPerPerson: Double,
    val defaultPricePerKgPkr: Double,
    val hadithMeasureUr: String
) {
    WHEAT(
        id = "wheat",
        nameEn = "Wheat / Flour",
        nameUr = "گندم / آٹا",
        quantityDescUr = "نصف صاع (2.04 کلوگرام)",
        requiredKgPerPerson = 2.04,
        defaultPricePerKgPkr = 150.0,
        hadithMeasureUr = "نصف صاع (2.04 کلوگرام شرعی وزن)"
    ),
    BARLEY(
        id = "barley",
        nameEn = "Barley",
        nameUr = "جو",
        quantityDescUr = "ایک صاع (4.08 کلوگرام)",
        requiredKgPerPerson = 4.08,
        defaultPricePerKgPkr = 280.0,
        hadithMeasureUr = "ایک صاع (4.08 کلوگرام شرعی وزن)"
    ),
    DATES(
        id = "dates",
        nameEn = "Dates",
        nameUr = "کھجور",
        quantityDescUr = "ایک صاع (4.08 کلوگرام)",
        requiredKgPerPerson = 4.08,
        defaultPricePerKgPkr = 650.0,
        hadithMeasureUr = "ایک صاع (4.08 کلوگرام شرعی وزن)"
    ),
    RAISINS(
        id = "raisins",
        nameEn = "Raisins",
        nameUr = "کشمش",
        quantityDescUr = "ایک صاع (4.08 کلوگرام)",
        requiredKgPerPerson = 4.08,
        defaultPricePerKgPkr = 1400.0,
        hadithMeasureUr = "ایک صاع (4.08 کلوگرام شرعی وزن)"
    ),
    AJWA_DATES(
        id = "ajwa_dates",
        nameEn = "Ajwa / Premium Dates",
        nameUr = "اعلیٰ کھجور / عجوہ",
        quantityDescUr = "ایک صاع (4.08 کلوگرام)",
        requiredKgPerPerson = 4.08,
        defaultPricePerKgPkr = 2500.0,
        hadithMeasureUr = "ایک صاع (4.08 کلوگرام شرعی وزن - اہل ثروت کے لیے مستحب)"
    )
}

enum class UshrIrrigationType(
    val id: String,
    val titleEn: String,
    val titleUr: String,
    val ratePercent: Double,
    val rateFractionUr: String,
    val shariahBasisUr: String
) {
    NATURAL(
        id = "natural",
        titleEn = "Rain / Natural Water / River (10%)",
        titleUr = "بارانی و قدرتی پانی (دریا، چشمہ، بارش)",
        ratePercent = 10.0,
        rateFractionUr = "دسواں حصہ (1/10)",
        shariahBasisUr = "حدیث شریف: «مَا سَقَتِ السَّمَاءُ وَالْعُيُونُ أَوْ كَانَ عَثَرِيًّا فَفِيهِ الْعُشْرُ» — قدرتی بارش یا دریا و چشمہ سے سیراب ہونے والی پیداوار پر 10 فیصد (دسواں حصہ) عشر واجب ہے۔"
    ),
    ARTIFICIAL(
        id = "artificial",
        titleEn = "Tube-well / Costly Irrigation (5%)",
        titleUr = "مصنوعی آبپاشی مع اخراجات (ٹیوب ویل، خریدا پانی)",
        ratePercent = 5.0,
        rateFractionUr = "بیسواں حصہ / نصف العشر (1/20)",
        shariahBasisUr = "حدیث شریف: «وَمَا سُقِيَ بِالنَّضْحِ فَفِيهِ نِصْفُ الْعُشْرِ» — جہاں پانی کے حصول پر اخراجات (ٹیوب ویل، بجلی، ڈیزل وغیرہ) آئیں وہاں 5 فیصد (نصف العشر) عشر واجب ہے۔"
    ),
    MIXED(
        id = "mixed",
        titleEn = "Mixed: 50% Natural + 50% Artificial (7.5%)",
        titleUr = "مخلوط آبپاشی (آدھا قدرتی و آدھا مصنوعی)",
        ratePercent = 7.5,
        rateFractionUr = "تین بٹا چالیس (3/40)",
        shariahBasisUr = "مفتیٰ بہ فقہی اصول: اگر فصل کو نصف مدت قدرتی اور نصف مدت مصنوعی طریقے سے پانی دیا گیا ہو تو اوسطاً 7.5% عشر واجب ہوتا ہے۔"
    )
}

enum class UshrProduceUnit(
    val id: String,
    val labelEn: String,
    val labelUr: String,
    val kgFactor: Double
) {
    MAUND("maund", "Maund (من)", "من", 40.0),
    KILOGRAM("kg", "Kilogram (کلوگرام)", "کلوگرام", 1.0),
    METRIC_TON("tonne", "Metric Ton (ٹن)", "میٹرک ٹن", 1000.0)
}

data class HaqMehrPreset(
    val id: String,
    val nameEn: String,
    val nameUr: String,
    val subtitleUr: String,
    val weightInTola: Double,
    val weightInGrams: Double,
    val descriptionUr: String,
    val metalType: HaqMehrMetalType = HaqMehrMetalType.SILVER,
    val hadithUr: String = ""
)

data class CountryLocationInfo(
    val code: String,
    val nameEn: String,
    val nameUr: String,
    val defaultCurrency: String,
    val currencySymbol: String,
    val majorCities: List<String>,
    val defaultUnit: SilverUnit = SilverUnit.TOLA
)

sealed class SilverRateUiState {
    object Idle : SilverRateUiState()
    object Loading : SilverRateUiState()
    data class Success(val rate: LiveSilverRate) : SilverRateUiState()
    data class Error(val message: String, val isConfigError: Boolean = false) : SilverRateUiState()
}

sealed class GoldRateUiState {
    object Idle : GoldRateUiState()
    object Loading : GoldRateUiState()
    data class Success(val rate: LiveGoldRate) : GoldRateUiState()
    data class Error(val message: String, val isConfigError: Boolean = false) : GoldRateUiState()
}

