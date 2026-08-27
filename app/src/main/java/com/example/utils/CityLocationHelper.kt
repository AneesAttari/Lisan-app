package com.example.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.data.model.CountryLocationInfo
import com.example.data.model.SilverUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

data class LocationDetectionResult(
    val country: String,
    val city: String,
    val countryCode: String,
    val currency: String,
    val currencySymbol: String,
    val isGpsAccurate: Boolean = true
)

object CityLocationHelper {
    private const val TAG = "CityLocationHelper"

    val supportedCountries: List<CountryLocationInfo> = listOf(
        CountryLocationInfo(
            code = "PK",
            nameEn = "Pakistan",
            nameUr = "پاکستان",
            defaultCurrency = "PKR",
            currencySymbol = "Rs.",
            majorCities = listOf(
                "Multan", "Karachi", "Lahore", "Islamabad", "Rawalpindi",
                "Faisalabad", "Peshawar", "Quetta", "Gujranwala", "Sialkot",
                "Hyderabad", "Bahawalpur", "Sargodha", "Abbottabad", "Sukkur",
                "Mardan", "Muzaffarabad", "Mirpur"
            ),
            defaultUnit = SilverUnit.TOLA
        ),
        CountryLocationInfo(
            code = "SA",
            nameEn = "Saudi Arabia",
            nameUr = "سعودی عرب",
            defaultCurrency = "SAR",
            currencySymbol = "SAR",
            majorCities = listOf("Riyadh", "Jeddah", "Makkah", "Madinah", "Dammam", "Khobar", "Tabuk", "Taif", "Abha"),
            defaultUnit = SilverUnit.GRAMS
        ),
        CountryLocationInfo(
            code = "AE",
            nameEn = "United Arab Emirates",
            nameUr = "متحدہ عرب امارات",
            defaultCurrency = "AED",
            currencySymbol = "AED",
            majorCities = listOf("Dubai", "Abu Dhabi", "Sharjah", "Ajman", "Ras Al Khaimah", "Al Ain", "Fujairah"),
            defaultUnit = SilverUnit.GRAMS
        ),
        CountryLocationInfo(
            code = "GB",
            nameEn = "United Kingdom",
            nameUr = "برطانیہ",
            defaultCurrency = "GBP",
            currencySymbol = "£",
            majorCities = listOf("London", "Birmingham", "Manchester", "Leeds", "Glasgow", "Bradford", "Leicester"),
            defaultUnit = SilverUnit.TROY_OUNCE
        ),
        CountryLocationInfo(
            code = "US",
            nameEn = "United States",
            nameUr = "امریکہ",
            defaultCurrency = "USD",
            currencySymbol = "$",
            majorCities = listOf("New York", "Chicago", "Houston", "Los Angeles", "Dallas", "Washington D.C.", "San Francisco"),
            defaultUnit = SilverUnit.TROY_OUNCE
        ),
        CountryLocationInfo(
            code = "CA",
            nameEn = "Canada",
            nameUr = "کینیڈا",
            defaultCurrency = "CAD",
            currencySymbol = "C$",
            majorCities = listOf("Toronto", "Vancouver", "Montreal", "Calgary", "Ottawa", "Edmonton", "Mississauga"),
            defaultUnit = SilverUnit.TROY_OUNCE
        ),
        CountryLocationInfo(
            code = "IN",
            nameEn = "India",
            nameUr = "بھارت",
            defaultCurrency = "INR",
            currencySymbol = "₹",
            majorCities = listOf("Mumbai", "Delhi", "Hyderabad", "Bangalore", "Chennai", "Kolkata", "Ahmedabad", "Lucknow"),
            defaultUnit = SilverUnit.TOLA
        ),
        CountryLocationInfo(
            code = "QA",
            nameEn = "Qatar",
            nameUr = "قطر",
            defaultCurrency = "QAR",
            currencySymbol = "QAR",
            majorCities = listOf("Doha", "Al Rayyan", "Al Wakrah", "Al Khor"),
            defaultUnit = SilverUnit.GRAMS
        ),
        CountryLocationInfo(
            code = "KW",
            nameEn = "Kuwait",
            nameUr = "کویت",
            defaultCurrency = "KWD",
            currencySymbol = "KWD",
            majorCities = listOf("Kuwait City", "Hawalli", "Salmiya", "Farwaniya"),
            defaultUnit = SilverUnit.GRAMS
        ),
        CountryLocationInfo(
            code = "BH",
            nameEn = "Bahrain",
            nameUr = "بحرین",
            defaultCurrency = "BHD",
            currencySymbol = "BHD",
            majorCities = listOf("Manama", "Riffa", "Muharraq", "Hamad Town"),
            defaultUnit = SilverUnit.GRAMS
        ),
        CountryLocationInfo(
            code = "OM",
            nameEn = "Oman",
            nameUr = "عمان",
            defaultCurrency = "OMR",
            currencySymbol = "OMR",
            majorCities = listOf("Muscat", "Salalah", "Sohar", "Nizwa"),
            defaultUnit = SilverUnit.GRAMS
        ),
        CountryLocationInfo(
            code = "TR",
            nameEn = "Turkey",
            nameUr = "ترکیہ",
            defaultCurrency = "TRY",
            currencySymbol = "₺",
            majorCities = listOf("Istanbul", "Ankara", "Izmir", "Bursa", "Antalya", "Konya"),
            defaultUnit = SilverUnit.GRAMS
        ),
        CountryLocationInfo(
            code = "EG",
            nameEn = "Egypt",
            nameUr = "مصر",
            defaultCurrency = "EGP",
            currencySymbol = "EGP",
            majorCities = listOf("Cairo", "Alexandria", "Giza", "Shubra El Kheima", "Port Said"),
            defaultUnit = SilverUnit.GRAMS
        ),
        CountryLocationInfo(
            code = "MY",
            nameEn = "Malaysia",
            nameUr = "ملائیشیا",
            defaultCurrency = "MYR",
            currencySymbol = "RM",
            majorCities = listOf("Kuala Lumpur", "George Town", "Johor Bahru", "Ipoh", "Shah Alam"),
            defaultUnit = SilverUnit.GRAMS
        ),
        CountryLocationInfo(
            code = "AU",
            nameEn = "Australia",
            nameUr = "آسٹریلیا",
            defaultCurrency = "AUD",
            currencySymbol = "A$",
            majorCities = listOf("Sydney", "Melbourne", "Brisbane", "Perth", "Adelaide"),
            defaultUnit = SilverUnit.TROY_OUNCE
        ),
        CountryLocationInfo(
            code = "DE",
            nameEn = "Germany",
            nameUr = "جرمنی",
            defaultCurrency = "EUR",
            currencySymbol = "€",
            majorCities = listOf("Berlin", "Frankfurt", "Munich", "Hamburg", "Cologne", "Düsseldorf"),
            defaultUnit = SilverUnit.GRAMS
        )
    )

    fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    suspend fun detectLocation(context: Context): LocationDetectionResult? = withContext(Dispatchers.IO) {
        if (!hasLocationPermission(context)) {
            Log.d(TAG, "Location permission not granted.")
            return@withContext null
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return@withContext null

        try {
            var bestLocation: Location? = null

            val providers = listOf(
                LocationManager.NETWORK_PROVIDER,
                LocationManager.GPS_PROVIDER,
                LocationManager.PASSIVE_PROVIDER
            )

            for (provider in providers) {
                try {
                    if (locationManager.isProviderEnabled(provider)) {
                        val loc = locationManager.getLastKnownLocation(provider)
                        if (loc != null) {
                            if (bestLocation == null || loc.accuracy < bestLocation.accuracy || loc.time > bestLocation.time) {
                                bestLocation = loc
                            }
                        }
                    }
                } catch (e: SecurityException) {
                    Log.w(TAG, "SecurityException on provider $provider: ${e.message}")
                }
            }

            if (bestLocation != null) {
                val detected = getLocationFromCoordinates(context, bestLocation.latitude, bestLocation.longitude)
                if (detected != null) {
                    Log.d(TAG, "Location detected via GPS: ${detected.city}, ${detected.country} (${detected.countryCode})")
                    return@withContext detected
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error detecting location: ${e.message}", e)
        }

        return@withContext null
    }

    private fun getLocationFromCoordinates(context: Context, latitude: Double, longitude: Double): LocationDetectionResult? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val detectedCountryName = address.countryName ?: "Pakistan"
                val countryCode = address.countryCode?.uppercase(Locale.US) ?: "PK"
                val rawCity = address.locality
                    ?: address.subAdminArea
                    ?: address.adminArea
                    ?: address.featureName
                    ?: "Multan"

                // Find matching country profile or create a smart fallback
                val matchedCountry = supportedCountries.firstOrNull {
                    it.code.equals(countryCode, ignoreCase = true) ||
                            it.nameEn.equals(detectedCountryName, ignoreCase = true)
                }

                val finalCountry = matchedCountry?.nameEn ?: detectedCountryName
                val finalCurrency = matchedCountry?.defaultCurrency ?: getCurrencyForCountryCode(countryCode)
                val finalCurrencySymbol = matchedCountry?.currencySymbol ?: finalCurrency

                val cleanedCity = if (matchedCountry != null) {
                    val matchingCity = matchedCountry.majorCities.firstOrNull {
                        rawCity.contains(it, ignoreCase = true)
                    }
                    matchingCity ?: rawCity.trim()
                } else {
                    rawCity.trim()
                }

                LocationDetectionResult(
                    country = finalCountry,
                    city = cleanedCity,
                    countryCode = countryCode,
                    currency = finalCurrency,
                    currencySymbol = finalCurrencySymbol,
                    isGpsAccurate = true
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Geocoder lookup failed: ${e.message}", e)
            null
        }
    }

    fun getCurrencyForCountryCode(code: String): String {
        return when (code.uppercase(Locale.US)) {
            "PK" -> "PKR"
            "SA" -> "SAR"
            "AE" -> "AED"
            "GB" -> "GBP"
            "US" -> "USD"
            "CA" -> "CAD"
            "IN" -> "INR"
            "QA" -> "QAR"
            "KW" -> "KWD"
            "BH" -> "BHD"
            "OM" -> "OMR"
            "TR" -> "TRY"
            "EG" -> "EGP"
            "MY" -> "MYR"
            "AU" -> "AUD"
            "DE", "FR", "IT", "ES", "NL", "BE", "AT" -> "EUR"
            else -> "USD"
        }
    }

    fun getCountryInfo(countryNameOrCode: String): CountryLocationInfo? {
        val trimmed = countryNameOrCode.trim()
        return supportedCountries.firstOrNull {
            it.code.equals(trimmed, ignoreCase = true) ||
                    it.nameEn.equals(trimmed, ignoreCase = true) ||
                    it.nameUr.equals(trimmed, ignoreCase = true)
        }
    }
}
