package com.example.data.provider

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

private const val TAG = "SilverPriceProvider"

/**
 * Normalized silver price result returned by providers.
 */
data class ProviderSilverPrice(
    val pricePerTola: Double,
    val pricePerGram: Double,
    val currency: String = "PKR",
    val currencySymbol: String = "Rs.",
    val sourceName: String,
    val sourceUrl: String,
    val checkedTime: String,
    val city: String? = null,
    val isCitySpecific: Boolean = false
)

interface SilverPriceProvider {
    val name: String
    suspend fun fetchSilverPrice(city: String): ProviderSilverPrice
}

object SilverProviderHttpClient {
    val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(12, TimeUnit.SECONDS)
            .writeTimeout(12, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .build()
    }

    fun createRequest(url: String): Request {
        return Request.Builder()
            .url(url)
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
            .header("Accept-Language", "en-US,en;q=0.9,ur;q=0.8")
            .header("Cache-Control", "no-cache")
            .header("Pragma", "no-cache")
            .header("Sec-Fetch-Dest", "document")
            .header("Sec-Fetch-Mode", "navigate")
            .header("Sec-Fetch-Site", "none")
            .header("Upgrade-Insecure-Requests", "1")
            .build()
    }
}

object SilverHtmlParser {
    const val MIN_SILVER_TOLA_PKR = 1500.0
    const val MAX_SILVER_TOLA_PKR = 15000.0

    /**
     * Extracts a verified Silver Tola price from raw HTML or formatted web text.
     * Tolerates multi-line layouts, whitespace variations, nested markup, and Urdu labels.
     */
    fun extractTolaPrice(html: String, cityKeyword: String? = null): Double? {
        if (html.isBlank()) return null

        // 1. Isolate the silver/چاندی context to avoid picking up gold prices or stock indices
        val lowerHtml = html.lowercase()
        val silverIdx = lowerHtml.indexOf("silver").takeIf { it != -1 }
            ?: lowerHtml.indexOf("چاندی").takeIf { it != -1 }

        val targetSnippet = if (silverIdx != null) {
            val start = (silverIdx - 500).coerceAtLeast(0)
            val end = (silverIdx + 12000).coerceAtMost(html.length)
            html.substring(start, end)
        } else {
            html
        }

        // 2. Direct multi-line Regex matching on raw HTML (handles <td>1 Tola</td>\n<td>Rs. 3,350</td>)
        val multiLinePatterns = listOf(
            Regex("""(?:1\s*tola|tola|تولہ)[^0-9]{1,150}?(?:rs\.?|pkr|روپے)?\s*(\d{1,3}(?:,\d{3})+(?:\.\d+)?|\d{4,5}(?:\.\d+)?)""", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)),
            Regex("""(?:rs\.?|pkr|روپے)?\s*(\d{1,3}(?:,\d{3})+(?:\.\d+)?|\d{4,5}(?:\.\d+)?)\s*(?:per|\/|فی)\s*(?:1\s*)?(?:tola|تولہ)""", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)),
            Regex("""(?:silver|چاندی)[^0-9]{1,200}?(?:1\s*tola|tola|تولہ)[^0-9]{1,100}?(\d{1,3}(?:,\d{3})+(?:\.\d+)?|\d{4,5}(?:\.\d+)?)""", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
        )

        for (pattern in multiLinePatterns) {
            val matches = pattern.findAll(targetSnippet)
            for (m in matches) {
                val rawNum = m.groupValues[1]
                val cleaned = cleanNumber(rawNum)
                if (isValidSilverPrice(cleaned)) {
                    return cleaned
                }
            }
        }

        // 3. Tag-stripped plain-text scanning (handles split tags, nested spans, table cells)
        val plainText = stripHtmlTags(targetSnippet)
        val textPatterns = listOf(
            Regex("""(?:1\s*Tola|Tola|تولہ)\s*(?:Silver|چاندی)?\s*(?:Price|Rate)?\s*[:=\-]?\s*(?:Rs\.?|PKR|روپے)?\s*(\d{1,3}(?:,\d{3})*(?:\.\d+)?)""", RegexOption.IGNORE_CASE),
            Regex("""(?:Rs\.?|PKR|روپے)\s*(\d{1,3}(?:,\d{3})*(?:\.\d+)?)\s*(?:per|\/|فی)\s*(?:1\s*)?(?:Tola|تولہ)""", RegexOption.IGNORE_CASE),
            Regex("""(\d{1,3}(?:,\d{3})*(?:\.\d+)?)\s*(?:Rs\.?|PKR|روپے)?\s*(?:per|\/|فی)\s*(?:1\s*)?(?:Tola|تولہ)""", RegexOption.IGNORE_CASE),
            Regex("""(?:Silver|چاندی)\s*(?:Rate|Price)?\s*[:=\-]?\s*(?:Rs\.?|PKR|روپے)?\s*(\d{1,3}(?:,\d{3})*(?:\.\d+)?)""", RegexOption.IGNORE_CASE),
            Regex("""(?:Silver|چاندی)\s*(?:1\s*Tola|Tola|تولہ)\s*(\d{1,3}(?:,\d{3})*(?:\.\d+)?)""", RegexOption.IGNORE_CASE)
        )

        for (pattern in textPatterns) {
            val matches = pattern.findAll(plainText)
            for (m in matches) {
                val rawNum = m.groupValues[1]
                val cleaned = cleanNumber(rawNum)
                if (isValidSilverPrice(cleaned)) {
                    return cleaned
                }
            }
        }

        // 4. Broad numeric fallback within the isolated silver context
        val broadMatches = Regex("""(\d{1,3}(?:,\d{3})*(?:\.\d+)?)""").findAll(plainText)
        for (m in broadMatches) {
            val cleaned = cleanNumber(m.groupValues[1])
            if (isValidSilverPrice(cleaned)) {
                return cleaned
            }
        }

        return null
    }

    fun isValidSilverPrice(price: Double?): Boolean {
        return price != null && price >= MIN_SILVER_TOLA_PKR && price <= MAX_SILVER_TOLA_PKR
    }

    fun cleanNumber(raw: String): Double? {
        return raw.replace(",", "").replace(" ", "").toDoubleOrNull()
    }

    fun stripHtmlTags(html: String): String {
        return html
            .replace(Regex("""<(?:br|p|div|tr|td|th|li|h[1-6])[^>]*>""", RegexOption.IGNORE_CASE), " ")
            .replace(Regex("""<[^>]*>"""), " ")
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&gt;", ">")
            .replace("&lt;", "<")
            .replace(Regex("""\s+"""), " ")
            .trim()
    }
}

class HamariwebProvider : SilverPriceProvider {
    override val name = "Hamariweb"

    override suspend fun fetchSilverPrice(city: String): ProviderSilverPrice {
        val url = "https://www.hamariweb.com/finance/silver-rate-in-pakistan.aspx"
        val request = SilverProviderHttpClient.createRequest(url)

        SilverProviderHttpClient.client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("HTTP Error ${response.code} from Hamariweb")
            }
            val html = response.body?.string() ?: throw Exception("Empty response from Hamariweb")

            val price = SilverHtmlParser.extractTolaPrice(html)
                ?: throw Exception("Failed to extract valid silver price from Hamariweb HTML structure.")
            
            val sdf = SimpleDateFormat("hh:mm a", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("Asia/Karachi")
            }

            return ProviderSilverPrice(
                pricePerTola = price,
                pricePerGram = price / 11.6638125,
                sourceName = "Hamariweb",
                sourceUrl = url,
                checkedTime = sdf.format(Date()),
                city = "Pakistan National Rate",
                isCitySpecific = false
            )
        }
    }
}

class SarafaProvider : SilverPriceProvider {
    override val name = "Sarafa.pk"

    override suspend fun fetchSilverPrice(city: String): ProviderSilverPrice {
        val citySlugs = mapOf(
            "karachi" to "karachi",
            "lahore" to "lahore",
            "islamabad" to "islamabad",
            "rawalpindi" to "rawalpindi",
            "multan" to "multan",
            "faisalabad" to "faisalabad",
            "peshawar" to "peshawar",
            "quetta" to "quetta"
        )
        val cleanCity = city.trim().lowercase()
        val slug = citySlugs[cleanCity]
        
        val url = if (slug != null) {
            "https://www.sarafa.pk/en/silver-rate/$slug"
        } else {
            "https://www.sarafa.pk/silver-rate/"
        }

        val request = SilverProviderHttpClient.createRequest(url)

        SilverProviderHttpClient.client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("HTTP Error ${response.code} from Sarafa.pk")
            }
            val html = response.body?.string() ?: throw Exception("Empty response from Sarafa.pk")

            val price = SilverHtmlParser.extractTolaPrice(html, city)
                ?: throw Exception("Failed to extract valid silver price from Sarafa.pk HTML.")
            
            val sdf = SimpleDateFormat("hh:mm a", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("Asia/Karachi")
            }

            return ProviderSilverPrice(
                pricePerTola = price,
                pricePerGram = price / 11.6638125,
                sourceName = "Sarafa.pk",
                sourceUrl = url,
                checkedTime = sdf.format(Date()),
                city = if (slug != null) city else "Pakistan National Rate",
                isCitySpecific = slug != null
            )
        }
    }
}

class SarmaayaProvider : SilverPriceProvider {
    override val name = "Sarmaaya.pk"

    override suspend fun fetchSilverPrice(city: String): ProviderSilverPrice {
        val url = "https://sarmaaya.pk/commodities/silver"
        val request = SilverProviderHttpClient.createRequest(url)

        SilverProviderHttpClient.client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("HTTP Error ${response.code} from Sarmaaya.pk")
            }
            val html = response.body?.string() ?: throw Exception("Empty response from Sarmaaya.pk")

            val price = SilverHtmlParser.extractTolaPrice(html)
                ?: throw Exception("Failed to extract valid silver price from Sarmaaya.pk HTML.")

            val sdf = SimpleDateFormat("hh:mm a", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("Asia/Karachi")
            }

            return ProviderSilverPrice(
                pricePerTola = price,
                pricePerGram = price / 11.6638125,
                sourceName = "Sarmaaya.pk",
                sourceUrl = url,
                checkedTime = sdf.format(Date()),
                city = "Pakistan National Rate",
                isCitySpecific = false
            )
        }
    }
}

object SilverPriceScrapingEngine {
    private val providers = listOf(
        HamariwebProvider(),
        SarafaProvider(),
        SarmaayaProvider()
    )

    suspend fun fetchGroundedPrice(city: String): ProviderSilverPrice {
        val errors = mutableListOf<String>()
        for (provider in providers) {
            try {
                Log.i("SCRAPER_ENGINE", "Attempting fetch from ${provider.name} for city: $city...")
                val result = provider.fetchSilverPrice(city)
                Log.i("SCRAPER_ENGINE", "Success! Fetched price from ${provider.name}: Rs. ${result.pricePerTola} per Tola")
                return result
            } catch (e: Exception) {
                Log.w("SCRAPER_ENGINE", "${provider.name} failed: ${e.message}")
                errors.add("${provider.name}: ${e.message}")
            }
        }
        throw Exception("All local providers failed: ${errors.joinToString("; ")}")
    }
}
