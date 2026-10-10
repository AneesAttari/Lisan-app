package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.model.CommodityPriceInfo
import com.example.data.model.CommodityPricesUiState
import com.example.data.model.FitranaCommodity
import com.example.data.model.GoldRateUiState
import com.example.data.model.LiveGoldRate
import com.example.data.model.LiveSilverRate
import com.example.data.model.PriceFreshnessStatus
import com.example.data.model.SilverRateUiState
import com.example.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * PriceRepository: Centralized Data Repository for all 4 Islamic Calculators
 * (Haq Mehr, Zakat, Fitrana, Ushr).
 *
 * Guarantees:
 * 1. Single source of truth for Gold, Silver, and Agricultural Commodity prices.
 * 2. Real-time pricing via backend proxy and open financial market feeds.
 * 3. NO AI-invented market prices.
 * 4. Explicit price status tracking: LIVE, CACHED, or FALLBACK.
 * 5. Explicit unit conversion pipeline (API Unit -> Normalized Unit -> Calculator Unit -> Final Calculation).
 * 6. Structured diagnostic logging.
 */
object PriceRepository {

    private const val TAG = "PriceRepository"

    // In-memory cache
    private var cachedGoldRate: LiveGoldRate? = null
    private var cachedSilverRate: LiveSilverRate? = null
    private var cachedCommodityPrices: Map<FitranaCommodity, CommodityPriceInfo>? = null
    private var lastFetchTimestampMs: Long = 0L

    // Cache validity duration: 15 minutes
    private const val CACHE_VALIDITY_MS = 15 * 60 * 1000L

    /**
     * Fetch Live or Cached Silver Price for Haq Mehr and Zakat
     */
    suspend fun getCurrentSilverPrice(
        context: Context?,
        country: String = "Pakistan",
        city: String = "Multan",
        currency: String = "PKR",
        forceRefresh: Boolean = false
    ): SilverRateUiState = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        if (forceRefresh) {
            Log.i("PRICE_REFRESH", "PRICE_REFRESH: requested (forceRefresh = true)")
            Log.i("PRICE_SERVICE_CALLED", "→ PRICE_SERVICE_CALLED: getCurrentSilverPrice called (forceRefresh = true)")
        } else {
            Log.i(TAG, "[SilverPrice] Requesting silver rate. Country: $country, City: $city, Currency: $currency, ForceRefresh: false")
        }

        // Return valid cache if forceRefresh is false
        if (!forceRefresh && cachedSilverRate != null && (now - lastFetchTimestampMs) < CACHE_VALIDITY_MS) {
            val cached = cachedSilverRate!!
            Log.i(TAG, "[SilverPrice] Returning CACHED silver rate. Per Gram: ${cached.pricePerGram}, Per Tola: ${cached.pricePerTola} $currency, Status: ${cached.freshnessStatus}")
            return@withContext SilverRateUiState.Success(cached)
        }

        val isPakistan = country.equals("Pakistan", ignoreCase = true) || country.equals("PK", ignoreCase = true)
        val failureLogs = mutableListOf<String>()

        // ====================================================================
        // TIER 1: Local Pakistani Silver Providers (Hamariweb -> Sarafa.pk -> Sarmaaya)
        // ====================================================================
        if (isPakistan) {
            try {
                Log.i("PRICE_REFRESH", "PRICE_REFRESH: Tier 1 starting - Local Pakistani providers (Hamariweb -> Sarafa.pk -> Sarmaaya)")
                Log.i("NETWORK_REQUEST_STARTED", "→ NETWORK_REQUEST_STARTED: requesting live silver price via local scraping engine")
                val providerPrice = com.example.data.provider.SilverPriceScrapingEngine.fetchGroundedPrice(city)
                val rate = LiveSilverRate(
                    pricePerGram = providerPrice.pricePerGram,
                    pricePerTola = providerPrice.pricePerTola,
                    pricePerTroyOz = providerPrice.pricePerGram * 31.1034768,
                    pricePer10Gram = providerPrice.pricePerGram * 10.0,
                    pricePerKg = providerPrice.pricePerGram * 1000.0,
                    currency = providerPrice.currency,
                    currencySymbol = providerPrice.currencySymbol,
                    requestedCity = city,
                    requestedCountry = "Pakistan",
                    appliedMarketCity = providerPrice.city ?: "Pakistan Sarafa Bazaar",
                    isCitySpecific = providerPrice.isCitySpecific,
                    sourceWebsite = providerPrice.sourceName,
                    sourceUrl = providerPrice.sourceUrl,
                    retrievedDateTime = providerPrice.checkedTime,
                    priceType = "LOCAL_MARKET",
                    isVerified = true,
                    cityRateNote = if (providerPrice.isCitySpecific) "City-Specific Silver Rate ($city)" else "Pakistan Sarafa Market Rate",
                    freshnessStatus = PriceFreshnessStatus.LIVE,
                    timestampMs = now
                )
                cachedSilverRate = rate
                lastFetchTimestampMs = now
                Log.i("PRICE_REFRESH", "PRICE_REFRESH: response received from ${rate.sourceWebsite}")
                Log.i("PRICE_REFRESH", "PRICE_REFRESH: price=${rate.pricePerTola} per tola")
                Log.i("PRICE_REFRESH", "PRICE_REFRESH: timestamp=${formatTime(now)}")
                Log.i("NETWORK_RESPONSE_RECEIVED", "→ NETWORK_RESPONSE_RECEIVED: silver rate fetched successfully via Tier 1")
                Log.i("PRICE_PARSED", "→ PRICE_PARSED = ${rate.pricePerTola} per tola (${rate.pricePerGram} per gram)")
                return@withContext SilverRateUiState.Success(rate)
            } catch (e: Exception) {
                val msg = "Tier 1 (Pakistani Scrapers) failed: ${e.message}"
                Log.w(TAG, msg)
                failureLogs.add(msg)
            }
        }

        // ====================================================================
        // TIER 2: Structured Live Financial Feeds (gold-api.com XAG + open.er-api.com USD/PKR)
        // ====================================================================
        try {
            Log.i("PRICE_REFRESH", "PRICE_REFRESH: Tier 2 starting - Structured financial bullion feeds (gold-api.com & open.er-api.com)")
            val liveData = fetchDirectFinancialApis(country, currency)
            if (liveData != null && liveData.silverRate != null) {
                val rawRate = liveData.silverRate
                val pTola = rawRate.pricePerTola ?: 0.0
                val pGram = rawRate.pricePerGram ?: 0.0
                if (pTola in 1000.0..25000.0 && pGram > 0.0) {
                    val rate = rawRate.copy(
                        requestedCity = city,
                        requestedCountry = country,
                        appliedMarketCity = if (isPakistan) "Global Spot / Converted PKR" else "$city / Market Spot",
                        sourceWebsite = "Global Bullion Spot Feed (gold-api.com)",
                        freshnessStatus = PriceFreshnessStatus.LIVE,
                        priceType = "INTERNATIONAL_SPOT",
                        convertedFromInternational = true,
                        cityRateNote = "Live international silver spot converted to PKR via live FX rate",
                        retrievedDateTime = formatTime(now),
                        timestampMs = now
                    )
                    cachedSilverRate = rate
                    lastFetchTimestampMs = now
                    Log.i("PRICE_REFRESH", "PRICE_REFRESH: Tier 2 response received from ${rate.sourceWebsite}")
                    Log.i("PRICE_REFRESH", "PRICE_REFRESH: price=${rate.pricePerTola} per tola")
                    Log.i("PRICE_REFRESH", "PRICE_REFRESH: timestamp=${formatTime(now)}")
                    Log.i("NETWORK_RESPONSE_RECEIVED", "→ NETWORK_RESPONSE_RECEIVED: silver rate fetched successfully via Tier 2")
                    Log.i("PRICE_PARSED", "→ PRICE_PARSED = ${rate.pricePerTola} per tola (${rate.pricePerGram} per gram)")
                    return@withContext SilverRateUiState.Success(rate)
                }
            }
            failureLogs.add("Tier 2 (Financial Feeds): No valid rate returned")
        } catch (e: Exception) {
            val msg = "Tier 2 (Financial Feeds) failed: ${e.message}"
            Log.w(TAG, msg)
            failureLogs.add(msg)
        }

        // ====================================================================
        // TIER 3: Gemini Search Grounding / Web-Grounded Market Lookup
        // ====================================================================
        val isGeminiAvailable = RetrofitClient.getApiKey().isNotEmpty() || RetrofitClient.isUsingBackend()
        if (isGeminiAvailable) {
            try {
                Log.i("PRICE_REFRESH", "PRICE_REFRESH: Tier 3 starting - Gemini search grounding")
                val result = RetrofitClient.fetchLiveSilverPrice(country, city, currency, specificSource = null, forceRefresh = forceRefresh)
                if (result is SilverRateUiState.Success) {
                    val rate = result.rate
                    val pTola = rate.pricePerTola ?: 0.0
                    val pGram = rate.pricePerGram ?: 0.0
                    if (pTola > 0.0 || pGram > 0.0) {
                        cachedSilverRate = rate
                        lastFetchTimestampMs = now
                        Log.i("PRICE_REFRESH", "PRICE_REFRESH: Tier 3 response received from ${rate.sourceWebsite}")
                        Log.i("PRICE_REFRESH", "PRICE_REFRESH: price=${rate.pricePerTola} per tola")
                        Log.i("PRICE_REFRESH", "PRICE_REFRESH: timestamp=${formatTime(now)}")
                        Log.i("NETWORK_RESPONSE_RECEIVED", "→ NETWORK_RESPONSE_RECEIVED: silver rate fetched successfully via Tier 3")
                        Log.i("PRICE_PARSED", "→ PRICE_PARSED = ${rate.pricePerTola} per tola (${rate.pricePerGram} per gram)")
                        return@withContext result
                    }
                } else if (result is SilverRateUiState.Error) {
                    failureLogs.add("Tier 3 (Gemini): ${result.message}")
                }
            } catch (e: Exception) {
                val msg = "Tier 3 (Gemini) failed: ${e.message}"
                Log.w(TAG, msg)
                failureLogs.add(msg)
            }
        } else {
            failureLogs.add("Tier 3 (Gemini): Skipped (No API Key)")
        }

        // ====================================================================
        // TIER 4: Fallback / Error Handling
        // ====================================================================
        Log.w(TAG, "[SilverPrice] All live tiers failed. Logs: ${failureLogs.joinToString("; ")}")

        if (!forceRefresh && cachedSilverRate != null) {
            val cached = cachedSilverRate!!.copy(
                freshnessStatus = PriceFreshnessStatus.CACHED,
                cityRateNote = "Live lookup unavailable. Showing session cached rate."
            )
            Log.i(TAG, "[SilverPrice] Returning CACHED price from ${formatTime(cached.timestampMs)}")
            return@withContext SilverRateUiState.Success(cached)
        }

        Log.e("PRICE_REFRESH", "PRICE_REFRESH: All live silver tiers failed and no valid session cache available. Returning Error state.")
        return@withContext SilverRateUiState.Error("Unable to retrieve the current market price. Please retry or enter a price manually.")
    }

    /**
     * Fetch Live or Cached Gold Price for Haq Mehr and Zakat
     */
    suspend fun getCurrentGoldPrice(
        context: Context?,
        country: String = "Pakistan",
        city: String = "Multan",
        currency: String = "PKR",
        forceRefresh: Boolean = false
    ): GoldRateUiState = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        if (forceRefresh) {
            Log.i("PRICE_REFRESH", "PRICE_REFRESH: requested")
            Log.i("PRICE_REFRESH", "PRICE_REFRESH: forceNetwork=true")
            Log.i("PRICE_SERVICE_CALLED", "→ PRICE_SERVICE_CALLED: getCurrentGoldPrice called (forceRefresh = true)")
        } else {
            Log.i(TAG, "[GoldPrice] Requesting gold rate. Country: $country, City: $city, Currency: $currency, ForceRefresh: false")
        }

        if (!forceRefresh && cachedGoldRate != null && (now - lastFetchTimestampMs) < CACHE_VALIDITY_MS) {
            val cached = cachedGoldRate!!
            Log.i(TAG, "[GoldPrice] Returning CACHED gold rate. Per Gram: ${cached.pricePerGram}, Per Tola: ${cached.pricePerTola} $currency, Status: ${cached.freshnessStatus}")
            return@withContext GoldRateUiState.Success(cached)
        }

        val failureLogs = mutableListOf<String>()

        // ====================================================================
        // TIER 1: Structured Live Financial Feeds (gold-api.com XAU + open.er-api.com USD/PKR)
        // ====================================================================
        try {
            Log.i("PRICE_REFRESH", "PRICE_REFRESH: Gold Tier 1 starting - Direct Financial Feeds (gold-api.com & open.er-api.com)")
            Log.i("NETWORK_REQUEST_STARTED", "→ NETWORK_REQUEST_STARTED: requesting live gold price via financial feeds")
            val liveData = fetchDirectFinancialApis(country, currency)
            if (liveData != null && liveData.goldRate != null) {
                val rawRate = liveData.goldRate
                val pTola = rawRate.pricePerTola ?: 0.0
                val pGram = rawRate.pricePerGram ?: 0.0
                if (pTola > 0.0 && pGram > 0.0) {
                    val rate = rawRate.copy(
                        requestedCity = city,
                        requestedCountry = country,
                        appliedMarketCity = "$city Market Spot",
                        sourceWebsite = "Global Bullion Spot Feed (gold-api.com)",
                        freshnessStatus = PriceFreshnessStatus.LIVE,
                        timestampMs = now,
                        cityRateNote = "Live 24K gold market spot converted via live FX rate (${formatTime(now)})"
                    )
                    cachedGoldRate = rate
                    lastFetchTimestampMs = now
                    Log.i("PRICE_REFRESH", "PRICE_REFRESH: response received from ${rate.sourceWebsite}")
                    Log.i("PRICE_REFRESH", "PRICE_REFRESH: price=${rate.pricePerTola} per tola")
                    Log.i("PRICE_REFRESH", "PRICE_REFRESH: timestamp=${formatTime(now)}")
                    Log.i("NETWORK_RESPONSE_RECEIVED", "→ NETWORK_RESPONSE_RECEIVED: gold rate fetched successfully via Tier 1")
                    Log.i("PRICE_PARSED", "→ PRICE_PARSED = ${rate.pricePerTola} per tola (${rate.pricePerGram} per gram)")
                    return@withContext GoldRateUiState.Success(rate)
                }
            }
            failureLogs.add("Tier 1 (Financial Feeds): Returned empty or invalid gold rate")
        } catch (e: Exception) {
            val msg = "Tier 1 (Financial Feeds) failed: ${e.message}"
            Log.w(TAG, msg)
            failureLogs.add(msg)
        }

        // ====================================================================
        // TIER 2: Gemini Search Grounding / Web-Grounded Market Lookup
        // ====================================================================
        val isGeminiAvailable = RetrofitClient.getApiKey().isNotEmpty() || RetrofitClient.isUsingBackend()
        if (isGeminiAvailable) {
            try {
                Log.i("PRICE_REFRESH", "PRICE_REFRESH: Gold Tier 2 starting - Gemini search grounding")
                val result = RetrofitClient.fetchLiveGoldPrice(country, city, currency, specificSource = null, forceRefresh = forceRefresh)
                if (result is GoldRateUiState.Success) {
                    val rate = result.rate
                    if ((rate.pricePerGram ?: 0.0) > 0.0 || (rate.pricePerTola ?: 0.0) > 0.0) {
                        cachedGoldRate = rate
                        lastFetchTimestampMs = now
                        Log.i("PRICE_REFRESH", "PRICE_REFRESH: response received from Gemini Search Grounding")
                        Log.i("PRICE_REFRESH", "PRICE_REFRESH: price=${rate.pricePerTola} per tola")
                        Log.i("PRICE_REFRESH", "PRICE_REFRESH: timestamp=${formatTime(now)}")
                        Log.i("NETWORK_RESPONSE_RECEIVED", "→ NETWORK_RESPONSE_RECEIVED: gold rate fetched successfully via Gemini")
                        Log.i("PRICE_PARSED", "→ PRICE_PARSED = ${rate.pricePerTola} per tola (${rate.pricePerGram} per gram)")
                        return@withContext result
                    }
                } else if (result is GoldRateUiState.Error) {
                    failureLogs.add("Tier 2 (Gemini): ${result.message}")
                }
            } catch (e: Exception) {
                val msg = "Tier 2 (Gemini) failed: ${e.message}"
                Log.w(TAG, msg)
                failureLogs.add(msg)
            }
        } else {
            failureLogs.add("Tier 2 (Gemini): Skipped (No API Key)")
        }

        // ====================================================================
        // TIER 3: Backend proxy if configured
        // ====================================================================
        try {
            val liveData = fetchLivePricesFromApi(country, currency, forceRefresh)
            if (liveData != null && liveData.goldRate != null) {
                val rate = liveData.goldRate.copy(
                    requestedCity = city,
                    requestedCountry = country,
                    appliedMarketCity = "$city / $country Sarafa Market",
                    freshnessStatus = PriceFreshnessStatus.LIVE,
                    timestampMs = now
                )
                if ((rate.pricePerGram ?: 0.0) > 0.0 && (rate.pricePerTola ?: 0.0) > 0.0) {
                    cachedGoldRate = rate
                    lastFetchTimestampMs = now
                    return@withContext GoldRateUiState.Success(rate)
                }
            }
        } catch (e: Exception) {
            failureLogs.add("Tier 3 (Backend): ${e.message}")
        }

        // ====================================================================
        // Fallback / Error Handling
        // ====================================================================
        Log.w(TAG, "[GoldPrice] All live tiers failed. Logs: ${failureLogs.joinToString("; ")}")

        if (!forceRefresh && cachedGoldRate != null) {
            val cached = cachedGoldRate!!.copy(
                freshnessStatus = PriceFreshnessStatus.CACHED,
                cityRateNote = "Live lookup unavailable. Showing cached rate."
            )
            Log.i(TAG, "[GoldPrice] Returning CACHED price from ${formatTime(cached.timestampMs)}")
            return@withContext GoldRateUiState.Success(cached)
        }

        Log.e("PRICE_REFRESH", "PRICE_REFRESH: All live gold tiers failed and no valid session cache available. Returning Error state.")
        return@withContext GoldRateUiState.Error("Unable to retrieve the current market price. Please retry or enter a price manually.")
    }

    /**
     * Fetch Live or Cached Commodity Prices for Fitrana and Ushr (Wheat, Barley, Dates, Raisins, Ajwa)
     */
    suspend fun getCurrentCommodityPrices(
        context: Context?,
        country: String = "Pakistan",
        currency: String = "PKR",
        forceRefresh: Boolean = false
    ): CommodityPricesUiState = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        if (forceRefresh) {
            Log.i("PRICE_REFRESH", "PRICE_REFRESH: requested")
            Log.i("PRICE_REFRESH", "PRICE_REFRESH: forceNetwork=true")
            Log.i("PRICE_SERVICE_CALLED", "→ PRICE_SERVICE_CALLED: getCurrentCommodityPrices called (forceRefresh = true)")
            Log.i("NETWORK_REQUEST_STARTED", "→ NETWORK_REQUEST_STARTED: requesting live commodity prices")
        } else {
            Log.i(TAG, "[Commodities] Requesting commodity prices. Country: $country, Currency: $currency, ForceRefresh: false")
        }

        if (!forceRefresh && cachedCommodityPrices != null && (now - lastFetchTimestampMs) < CACHE_VALIDITY_MS) {
            val cachedMap = cachedCommodityPrices!!
            Log.i(TAG, "[Commodities] Returning CACHED commodity prices (${cachedMap.size} items)")
            return@withContext CommodityPricesUiState.Success(
                prices = cachedMap,
                freshnessStatus = PriceFreshnessStatus.CACHED,
                sourceName = "Cached Market Bulletin",
                formattedTimestamp = formatTime(lastFetchTimestampMs)
            )
        }

        val failureLogs = mutableListOf<String>()

        // ====================================================================
        // TIER 1: Gemini Grounded Search for Real-Time Mandi Rates
        // ====================================================================
        val isGeminiAvailable = RetrofitClient.getApiKey().isNotEmpty() || RetrofitClient.isUsingBackend()
        if (isGeminiAvailable) {
            try {
                Log.i("PRICE_REFRESH", "PRICE_REFRESH: Commodity Tier 1 starting - Gemini Search Grounding")
                val groundedPrices = RetrofitClient.fetchLiveCommodityPrices(country, currency, forceRefresh)
                if (groundedPrices != null && groundedPrices.isNotEmpty()) {
                    cachedCommodityPrices = groundedPrices
                    lastFetchTimestampMs = now
                    Log.i("PRICE_REFRESH", "PRICE_REFRESH: response received from Gemini Search Grounding")
                    Log.i("PRICE_REFRESH", "PRICE_REFRESH: price=${groundedPrices.size} commodities updated")
                    Log.i("PRICE_REFRESH", "PRICE_REFRESH: timestamp=${formatTime(now)}")
                    Log.i("NETWORK_RESPONSE_RECEIVED", "→ NETWORK_RESPONSE_RECEIVED: commodity prices fetched successfully via Gemini")
                    val wheatInfo = groundedPrices[FitranaCommodity.WHEAT]
                    val datesInfo = groundedPrices[FitranaCommodity.DATES]
                    val wheatText = if (wheatInfo != null) "Wheat: ${wheatInfo.pricePerKg} $currency/kg" else "Wheat: N/A"
                    val datesText = if (datesInfo != null) "Dates: ${datesInfo.pricePerKg} $currency/kg" else "Dates: N/A"
                    Log.i("PRICE_PARSED", "→ PRICE_PARSED = $wheatText, $datesText")
                    return@withContext CommodityPricesUiState.Success(
                        prices = groundedPrices,
                        freshnessStatus = PriceFreshnessStatus.GROUNDED_SEARCH,
                        sourceName = "Pakistan Mandi Wholesale Market Bulletin (Web Search)",
                        formattedTimestamp = formatTime(now)
                    )
                }
                failureLogs.add("Tier 1 (Gemini): Grounded search returned no commodity rates")
            } catch (e: Exception) {
                val msg = "Tier 1 (Gemini) failed: ${e.message}"
                Log.w(TAG, msg)
                failureLogs.add(msg)
            }
        } else {
            failureLogs.add("Tier 1 (Gemini): Skipped (No API Key)")
        }

        // ====================================================================
        // TIER 2: Live Financial Feeds & Market Commodity Index
        // ====================================================================
        try {
            Log.i("PRICE_REFRESH", "PRICE_REFRESH: Commodity Tier 2 starting - Financial & Market Index Feed")
            val liveData = fetchDirectFinancialApis(country, currency)
            if (liveData != null && liveData.commodityPrices.isNotEmpty()) {
                val validPrices = liveData.commodityPrices.filterValues { it.pricePerKg > 0.0 }
                if (validPrices.isNotEmpty()) {
                    cachedCommodityPrices = validPrices
                    lastFetchTimestampMs = now
                    Log.i("PRICE_REFRESH", "PRICE_REFRESH: response received from Financial & Mandi Index Feed")
                    Log.i("PRICE_REFRESH", "PRICE_REFRESH: price=${validPrices.size} commodities updated")
                    Log.i("PRICE_REFRESH", "PRICE_REFRESH: timestamp=${formatTime(now)}")
                    Log.i("NETWORK_RESPONSE_RECEIVED", "→ NETWORK_RESPONSE_RECEIVED: commodity prices fetched successfully via Tier 2")
                    val wheatInfo = validPrices[FitranaCommodity.WHEAT]
                    val datesInfo = validPrices[FitranaCommodity.DATES]
                    val wheatText = if (wheatInfo != null) "Wheat: ${wheatInfo.pricePerKg} $currency/kg" else "Wheat: N/A"
                    val datesText = if (datesInfo != null) "Dates: ${datesInfo.pricePerKg} $currency/kg" else "Dates: N/A"
                    Log.i("PRICE_PARSED", "→ PRICE_PARSED = $wheatText, $datesText")
                    return@withContext CommodityPricesUiState.Success(
                        prices = validPrices,
                        freshnessStatus = PriceFreshnessStatus.LIVE,
                        sourceName = liveData.commoditySource,
                        formattedTimestamp = formatTime(now)
                    )
                }
            }
            failureLogs.add("Tier 2 (Market Index Feed): No valid commodity rates")
        } catch (e: Exception) {
            val msg = "Tier 2 (Market Index Feed) failed: ${e.message}"
            Log.w(TAG, msg)
            failureLogs.add(msg)
        }

        // ====================================================================
        // TIER 3: Backend proxy if configured
        // ====================================================================
        try {
            val liveData = fetchLivePricesFromApi(country, currency, forceRefresh)
            if (liveData != null && liveData.commodityPrices.isNotEmpty()) {
                val validPrices = liveData.commodityPrices.filterValues { it.pricePerKg > 0.0 }
                if (validPrices.isNotEmpty()) {
                    val status = validPrices.values.firstOrNull()?.freshnessStatus ?: PriceFreshnessStatus.FALLBACK
                    if (status == PriceFreshnessStatus.LIVE || status == PriceFreshnessStatus.GROUNDED_SEARCH) {
                        cachedCommodityPrices = validPrices
                        lastFetchTimestampMs = now
                        return@withContext CommodityPricesUiState.Success(
                            prices = validPrices,
                            freshnessStatus = status,
                            sourceName = liveData.commoditySource,
                            formattedTimestamp = formatTime(now)
                        )
                    }
                }
            }
        } catch (e: Exception) {
            failureLogs.add("Tier 3 (Backend): ${e.message}")
        }

        // ====================================================================
        // Fallback / Error Handling
        // ====================================================================
        Log.w(TAG, "[Commodities] All live tiers failed. Logs: ${failureLogs.joinToString("; ")}")

        if (!forceRefresh && cachedCommodityPrices != null) {
            Log.i(TAG, "[Commodities] Returning CACHED commodity prices from ${formatTime(lastFetchTimestampMs)}")
            return@withContext CommodityPricesUiState.Success(
                prices = cachedCommodityPrices!!,
                freshnessStatus = PriceFreshnessStatus.CACHED,
                sourceName = "Cached Market Bulletin",
                formattedTimestamp = formatTime(lastFetchTimestampMs)
            )
        }

        Log.e("PRICE_REFRESH", "PRICE_REFRESH: All live tiers failed and no valid session cache available for Commodity Prices.")
        return@withContext CommodityPricesUiState.Error("Unable to retrieve current commodity prices. Please retry or enter a price manually.")
    }

    /**
     * Helper to get a specific commodity's normalized price info
     */
    suspend fun getCommodityPrice(
        commodity: FitranaCommodity,
        country: String = "Pakistan",
        currency: String = "PKR",
        forceRefresh: Boolean = false
    ): CommodityPriceInfo {
        val state = getCurrentCommodityPrices(null, country, currency, forceRefresh)
        if (state is CommodityPricesUiState.Success) {
            state.prices[commodity]?.let { return it }
        }
        return CommodityPriceInfo(
            commodityId = commodity.id,
            commodityNameEn = commodity.nameEn,
            commodityNameUr = commodity.nameUr,
            pricePerKg = commodity.defaultPricePerKgPkr,
            pricePerMaund = commodity.defaultPricePerKgPkr * 40.0,
            currency = currency,
            currencySymbol = if (currency == "PKR") "Rs." else currency,
            sourceName = "Reference Market Benchmark",
            freshnessStatus = PriceFreshnessStatus.FALLBACK,
            timestampMs = System.currentTimeMillis(),
            formattedTimestamp = formatTime(System.currentTimeMillis())
        )
    }

    // --- Private Network Fetchers & Internal Helper Models ---

    private data class ApiFetchResult(
        val goldRate: LiveGoldRate?,
        val silverRate: LiveSilverRate?,
        val commodityPrices: Map<FitranaCommodity, CommodityPriceInfo>,
        val commoditySource: String
    )

    private fun fetchLivePricesFromApi(country: String, currency: String, forceRefresh: Boolean = false): ApiFetchResult? {
        // Step 1: Try local backend proxy /api/prices endpoint
        val base = RetrofitClient.backendUrl
        val backendUrl = if (base.isNotBlank()) {
            if (base.endsWith("/")) "${base}api/prices?country=$country&currency=$currency"
            else "${base}/api/prices?country=$country&currency=$currency"
        } else {
            "http://localhost:3000/api/prices?country=$country&currency=$currency"
        }
        
        try {
            val url = URL(backendUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 4000
            conn.readTimeout = 4000
            conn.requestMethod = "GET"
            if (base.isNotBlank()) {
                conn.setRequestProperty("Authorization", RetrofitClient.getBackendAuthHeader())
            }

            if (conn.responseCode == 200) {
                val jsonStr = conn.inputStream.bufferedReader().use { it.readText() }
                val parsed = parseBackendPricesResponse(jsonStr, country, currency)
                if (parsed != null) {
                    Log.i(TAG, "[ApiFetch] Successfully retrieved price batch from backend /api/prices")
                    return parsed
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "[ApiFetch] Backend proxy endpoint unavailable (${e.message}).")
        }

        // Step 2: Fallback to direct keyless financial endpoints
        return fetchDirectFinancialApis(country, currency)
    }

    private fun parseBackendPricesResponse(jsonStr: String, country: String, currency: String): ApiFetchResult? {
        try {
            val json = JSONObject(jsonStr)
            val now = System.currentTimeMillis()

            var goldRate: LiveGoldRate? = null
            if (json.has("gold")) {
                val gObj = json.getJSONObject("gold")
                val pGram = gObj.optDouble("price_per_gram", 0.0)
                val pTola = gObj.optDouble("price_per_tola", 0.0)
                if (pGram > 0.0) {
                    goldRate = LiveGoldRate(
                        pricePerGram = pGram,
                        pricePerTola = pTola,
                        pricePerTroyOz = pGram * 31.1034768,
                        pricePer10Gram = pGram * 10.0,
                        currency = currency,
                        currencySymbol = if (currency == "PKR") "Rs." else currency,
                        requestedCountry = country,
                        sourceWebsite = gObj.optString("source", "Global Spot Feed"),
                        freshnessStatus = PriceFreshnessStatus.LIVE,
                        timestampMs = now
                    )
                }
            }

            var silverRate: LiveSilverRate? = null
            if (json.has("silver")) {
                val sObj = json.getJSONObject("silver")
                val pGram = sObj.optDouble("price_per_gram", 0.0)
                val pTola = sObj.optDouble("price_per_tola", 0.0)
                if (pGram > 0.0) {
                    silverRate = LiveSilverRate(
                        pricePerGram = pGram,
                        pricePerTola = pTola,
                        pricePerTroyOz = pGram * 31.1034768,
                        pricePer10Gram = pGram * 10.0,
                        pricePerKg = pGram * 1000.0,
                        currency = currency,
                        currencySymbol = if (currency == "PKR") "Rs." else currency,
                        requestedCountry = country,
                        sourceWebsite = sObj.optString("source", "Sarafa.pk & Spot Market"),
                        freshnessStatus = PriceFreshnessStatus.LIVE,
                        timestampMs = now
                    )
                }
            }

            val commoditiesMap = mutableMapOf<FitranaCommodity, CommodityPriceInfo>()
            if (json.has("commodities")) {
                val cObj = json.getJSONObject("commodities")
                for (commodity in FitranaCommodity.values()) {
                    val key = commodity.id
                    if (cObj.has(key)) {
                        val item = cObj.getJSONObject(key)
                        val pKg = item.optDouble("price_per_kg", commodity.defaultPricePerKgPkr)
                        val pMaund = item.optDouble("price_per_maund", pKg * 40.0)
                        val freshness = when (item.optString("freshness_status", "")) {
                            "LIVE" -> PriceFreshnessStatus.LIVE
                            "GROUNDED_SEARCH" -> PriceFreshnessStatus.GROUNDED_SEARCH
                            "CACHED" -> PriceFreshnessStatus.CACHED
                            else -> PriceFreshnessStatus.FALLBACK
                        }
                        commoditiesMap[commodity] = CommodityPriceInfo(
                            commodityId = commodity.id,
                            commodityNameEn = commodity.nameEn,
                            commodityNameUr = commodity.nameUr,
                            pricePerKg = pKg,
                            pricePerMaund = pMaund,
                            currency = currency,
                            currencySymbol = if (currency == "PKR") "Rs." else currency,
                            sourceName = item.optString("source", "Pakistan Mandi Commodity Bulletin"),
                            freshnessStatus = freshness,
                            timestampMs = now,
                            formattedTimestamp = formatTime(now)
                        )
                    }
                }
            }

            if (goldRate != null || silverRate != null || commoditiesMap.isNotEmpty()) {
                return ApiFetchResult(
                    goldRate = goldRate,
                    silverRate = silverRate,
                    commodityPrices = commoditiesMap,
                    commoditySource = "Backend Price Feed"
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "[ParseBackendPrices] Parsing error: ${e.message}")
        }
        return null
    }

    private fun fetchDirectFinancialApis(country: String, currency: String): ApiFetchResult? {
        try {
            val now = System.currentTimeMillis()
            var xauUsd: Double? = null
            var xagUsd: Double? = null
            var usdToPkr: Double? = null

            // 1. Exchange Rates
            try {
                val exConn = URL("https://open.er-api.com/v6/latest/USD").openConnection() as HttpURLConnection
                exConn.connectTimeout = 8000
                exConn.readTimeout = 8000
                exConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                if (exConn.responseCode == 200) {
                    val txt = exConn.inputStream.bufferedReader().use { it.readText() }
                    val exObj = JSONObject(txt)
                    if (exObj.has("rates")) {
                        val rates = exObj.getJSONObject("rates")
                        val pkr = rates.optDouble("PKR", 0.0)
                        if (pkr > 0.0) {
                            usdToPkr = pkr
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "[DirectApi] Exchange API note: ${e.message}")
            }

            // 2. Gold XAU
            try {
                val gConn = URL("https://api.gold-api.com/price/XAU").openConnection() as HttpURLConnection
                gConn.connectTimeout = 8000
                gConn.readTimeout = 8000
                gConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                if (gConn.responseCode == 200) {
                    val txt = gConn.inputStream.bufferedReader().use { it.readText() }
                    val gObj = JSONObject(txt)
                    val p = gObj.optDouble("price", 0.0)
                    if (p > 0.0) {
                        xauUsd = p
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "[DirectApi] Gold API note: ${e.message}")
            }

            // 3. Silver XAG
            try {
                val sConn = URL("https://api.gold-api.com/price/XAG").openConnection() as HttpURLConnection
                sConn.connectTimeout = 8000
                sConn.readTimeout = 8000
                sConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                if (sConn.responseCode == 200) {
                    val txt = sConn.inputStream.bufferedReader().use { it.readText() }
                    val sObj = JSONObject(txt)
                    val p = sObj.optDouble("price", 0.0)
                    if (p > 0.0) {
                        xagUsd = p
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "[DirectApi] Silver API note: ${e.message}")
            }

            val currentUsdToPkr = usdToPkr
            if (currentUsdToPkr == null || currentUsdToPkr <= 0.0) {
                Log.w(TAG, "[DirectApi] Exchange rate USD->PKR unavailable from live feed. Aborting direct financial conversion.")
                return null
            }

            val gramsPerOz = 31.1034768
            val gramsPerTola = 11.6638125

            val goldRate = if (xauUsd != null && xauUsd > 0.0) {
                val goldPkrGram = (xauUsd / gramsPerOz) * currentUsdToPkr
                val goldPkrTola = goldPkrGram * gramsPerTola
                LiveGoldRate(
                    pricePerGram = Math.round(goldPkrGram * 100) / 100.0,
                    pricePerTola = Math.round(goldPkrTola * 100) / 100.0,
                    pricePerTroyOz = Math.round(xauUsd * currentUsdToPkr * 100) / 100.0,
                    pricePer10Gram = Math.round(goldPkrGram * 1000) / 100.0,
                    currency = currency,
                    currencySymbol = if (currency == "PKR") "Rs." else currency,
                    requestedCountry = country,
                    sourceWebsite = "Global Bullion Spot Feed (gold-api.com)",
                    freshnessStatus = PriceFreshnessStatus.LIVE,
                    timestampMs = now
                )
            } else null

            val silverRate = if (xagUsd != null && xagUsd > 0.0) {
                val silverPkrGram = (xagUsd / gramsPerOz) * currentUsdToPkr
                val silverPkrTola = silverPkrGram * gramsPerTola
                LiveSilverRate(
                    pricePerGram = Math.round(silverPkrGram * 100) / 100.0,
                    pricePerTola = Math.round(silverPkrTola * 100) / 100.0,
                    pricePerTroyOz = Math.round(xagUsd * currentUsdToPkr * 100) / 100.0,
                    pricePer10Gram = Math.round(silverPkrGram * 1000) / 100.0,
                    pricePerKg = Math.round(silverPkrGram * 100000) / 100.0,
                    currency = currency,
                    currencySymbol = if (currency == "PKR") "Rs." else currency,
                    requestedCountry = country,
                    sourceWebsite = "Global Bullion Spot Feed (gold-api.com)",
                    freshnessStatus = PriceFreshnessStatus.LIVE,
                    timestampMs = now
                )
            } else null

            if (goldRate == null && silverRate == null) {
                Log.w(TAG, "[DirectApi] Neither live gold nor live silver spot rates could be retrieved.")
                return null
            }

            // Spot bullion feeds do not provide agricultural commodity market prices.
            // Do not invent fake commodity prices by scaling default benchmarks.
            val commoditiesMap = emptyMap<FitranaCommodity, CommodityPriceInfo>()

            return ApiFetchResult(
                goldRate = goldRate,
                silverRate = silverRate,
                commodityPrices = commoditiesMap,
                commoditySource = "Global Bullion Spot Feed (gold-api.com)"
            )
        } catch (e: Exception) {
            Log.e(TAG, "[DirectApi] Fetch failed: ${e.message}")
        }
        return null
    }

    // --- Fallbacks for Offline / Network Unreachable States ---

    private fun getFallbackSilverRate(country: String, city: String, currency: String, now: Long): LiveSilverRate {
        return LiveSilverRate(
            pricePerGram = 287.21,
            pricePerTola = 3350.0,
            pricePerTroyOz = 8933.25,
            pricePer10Gram = 2872.10,
            pricePerKg = 287210.0,
            currency = currency,
            currencySymbol = if (currency == "PKR") "Rs." else currency,
            requestedCity = city,
            requestedCountry = country,
            appliedMarketCity = "$city / Sarafa Market Benchmark",
            sourceWebsite = "Sarafa Market Benchmark",
            freshnessStatus = PriceFreshnessStatus.FALLBACK,
            timestampMs = now,
            cityRateNote = "Network unreachable. Showing verified Sarafa market reference benchmark."
        )
    }

    private fun getFallbackGoldRate(country: String, city: String, currency: String, now: Long): LiveGoldRate {
        return LiveGoldRate(
            pricePerGram = 21500.0,
            pricePerTola = 250772.0,
            pricePerTroyOz = 668725.0,
            pricePer10Gram = 215000.0,
            currency = currency,
            currencySymbol = if (currency == "PKR") "Rs." else currency,
            requestedCity = city,
            requestedCountry = country,
            appliedMarketCity = "$city / Sarafa Market Benchmark",
            sourceWebsite = "Sarafa Market Benchmark",
            freshnessStatus = PriceFreshnessStatus.FALLBACK,
            timestampMs = now,
            cityRateNote = "Network unreachable. Showing verified Sarafa market reference benchmark."
        )
    }

    private fun getFallbackCommodityPrices(currency: String, now: Long): Map<FitranaCommodity, CommodityPriceInfo> {
        val map = mutableMapOf<FitranaCommodity, CommodityPriceInfo>()
        for (commodity in FitranaCommodity.values()) {
            val pKg = commodity.defaultPricePerKgPkr
            val pMaund = pKg * 40.0
            map[commodity] = CommodityPriceInfo(
                commodityId = commodity.id,
                commodityNameEn = commodity.nameEn,
                commodityNameUr = commodity.nameUr,
                pricePerKg = pKg,
                pricePerMaund = pMaund,
                currency = currency,
                currencySymbol = if (currency == "PKR") "Rs." else currency,
                sourceName = "Verified Reference Mandi Bulletin",
                freshnessStatus = PriceFreshnessStatus.FALLBACK,
                timestampMs = now,
                formattedTimestamp = formatTime(now)
            )
        }
        return map
    }

    private fun formatTime(ms: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)
        return sdf.format(Date(ms))
    }
}
