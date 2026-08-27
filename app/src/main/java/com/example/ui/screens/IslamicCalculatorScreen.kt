package com.example.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.LisanViewModel
import com.example.utils.CityLocationHelper
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun IslamicCalculatorScreen(viewModel: LisanViewModel) {
    val context = LocalContext.current

    val currentTab by viewModel.islamicCalculatorTab.collectAsState()
    val silverRateState by viewModel.silverRateState.collectAsState()
    val goldRateState by viewModel.goldRateState.collectAsState()

    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val selectedCurrencySymbol by viewModel.selectedCurrencySymbol.collectAsState()
    val isLocationGpsDetected by viewModel.isLocationGpsDetected.collectAsState()
    val isDetectingLocation by viewModel.isDetectingLocation.collectAsState()

    var showLocationDialog by remember { mutableStateOf(false) }
    var hasRequestedPermissionOnLaunch by remember { mutableStateOf(false) }

    val isPakistan = selectedCountry.equals("Pakistan", ignoreCase = true) || selectedCountry.equals("PK", ignoreCase = true)

    // Foreground location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.detectLocationFromGps(context)
        } else {
            if (silverRateState is SilverRateUiState.Idle) {
                viewModel.fetchLiveBullionRates(forceRefresh = false)
            }
        }
    }

    // Auto-detect location flow on first launch
    LaunchedEffect(Unit) {
        if (!hasRequestedPermissionOnLaunch) {
            hasRequestedPermissionOnLaunch = true
            if (CityLocationHelper.hasLocationPermission(context)) {
                viewModel.detectLocationFromGps(context)
            } else {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        } else if (silverRateState is SilverRateUiState.Idle || goldRateState is GoldRateUiState.Idle) {
            viewModel.fetchLiveBullionRates(forceRefresh = false)
        }
    }

    val currencyFormatter = remember {
        NumberFormat.getNumberInstance(Locale.US).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 0
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Header Banner ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, EditorialBorder),
            shadowElevation = 3.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0F172A),
                                Color(0xFF1E293B),
                                Color(0xFF0F766E)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0xFF2DD4BF).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = null,
                                    tint = Color(0xFF2DD4BF),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (isPakistan) "Sarafa Market • لائیو صرافہ ریٹ" else "Global Bullion Index",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2DD4BF)
                                )
                            }
                        }

                        Surface(
                            color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "مستند شرعی حسابات",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFCD34D),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = "Islamic Calculator",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Text(
                        text = "اسلامی شرعی کیلکولیٹر: مہر، زکوٰۃ، فطرانہ اور عشر",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFE2E8F0)
                    )

                    Text(
                        text = "Precise local arithmetic & live market rates grounded for Pakistan and International markets.",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // --- 2. Four Unified Tabs Navigation ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, EditorialBorder),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IslamicCalculatorTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.setIslamicCalculatorTab(tab) },
                        color = if (isSelected) Color(0xFF0F766E) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = tab.iconEmoji,
                                fontSize = 18.sp
                            )
                            Text(
                                text = tab.titleEn,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = tab.titleUr,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Normal,
                                color = if (isSelected) Color(0xFF99F6E4) else EditorialTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // --- 3. Location & Market Bar ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, EditorialBorder),
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFF0FDFA), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF0F766E),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "$selectedCity, $selectedCountry",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isLocationGpsDetected) "📍 GPS Auto-Detected ($selectedCurrency)" else "⚙️ Selected Market ($selectedCurrency)",
                                fontSize = 11.sp,
                                color = if (isLocationGpsDetected) Color(0xFF059669) else EditorialTextSecondary
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = {
                                if (CityLocationHelper.hasLocationPermission(context)) {
                                    viewModel.detectLocationFromGps(context)
                                } else {
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            },
                            enabled = !isDetectingLocation,
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFF0FDFA), CircleShape)
                        ) {
                            if (isDetectingLocation) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFF0F766E)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.MyLocation,
                                    contentDescription = "Detect Location GPS",
                                    tint = Color(0xFF0F766E),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Button(
                            onClick = { showLocationDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.EditLocation, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Change City", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Quick Popular Cities Bar
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val quickCities = listOf(
                        Triple("Pakistan", "Multan", "ملتان"),
                        Triple("Pakistan", "Karachi", "کراچی"),
                        Triple("Pakistan", "Lahore", "لاہور"),
                        Triple("Pakistan", "Islamabad", "اسلام آباد"),
                        Triple("Saudi Arabia", "Riyadh", "الرياض"),
                        Triple("United Arab Emirates", "Dubai", "دبي"),
                        Triple("United Kingdom", "London", "London"),
                        Triple("United States", "New York", "New York")
                    )

                    quickCities.forEach { (country, city, urName) ->
                        val isSelected = selectedCountry.equals(country, ignoreCase = true) && selectedCity.equals(city, ignoreCase = true)
                        SuggestionChip(
                            onClick = {
                                viewModel.selectCountry(country)
                                viewModel.selectCity(city)
                            },
                            label = {
                                Text(
                                    text = "$city / $urName",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (isSelected) Color(0xFF0F766E) else Color(0xFFF1F5F9),
                                labelColor = if (isSelected) Color.White else Color(0xFF334155)
                            ),
                            border = null,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }
        }

        // --- 4. Render the Active Tab Content ---
        when (currentTab) {
            IslamicCalculatorTab.HAQ_MEHR -> {
                HaqMehrTabContent(
                    viewModel = viewModel,
                    currencyFormatter = currencyFormatter
                )
            }
            IslamicCalculatorTab.ZAKAT -> {
                ZakatTabContent(
                    viewModel = viewModel,
                    currencyFormatter = currencyFormatter
                )
            }
            IslamicCalculatorTab.FITRANA -> {
                FitranaTabContent(
                    viewModel = viewModel,
                    currencyFormatter = currencyFormatter
                )
            }
            IslamicCalculatorTab.USHR -> {
                UshrTabContent(
                    viewModel = viewModel,
                    currencyFormatter = currencyFormatter
                )
            }
        }
    }

    if (showLocationDialog) {
        LocationSelectionDialog(
            viewModel = viewModel,
            onDismiss = { showLocationDialog = false }
        )
    }
}

// -------------------------------------------------------------
// TAB 1: HAQ MEHR CALCULATOR (SIMPLIFIED SHARIAH FLOW)
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HaqMehrTabContent(
    viewModel: LisanViewModel,
    currencyFormatter: NumberFormat
) {
    val context = LocalContext.current

    val silverRateState by viewModel.silverRateState.collectAsState()
    val isCustomRateMode by viewModel.isCustomRateMode.collectAsState()
    val customRateInput by viewModel.customRateInput.collectAsState()

    val selectedCity by viewModel.selectedCity.collectAsState()
    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val selectedCurrencySymbol by viewModel.selectedCurrencySymbol.collectAsState()

    val presets = remember {
        listOf(
            HaqMehrPreset(
                id = "mehr_fatimi",
                nameEn = "Mehr-e-Fatimi (500 Dirhams)",
                nameUr = "مہرِ فاطمی",
                subtitleUr = "131.25 تولہ (1530.9 گرام)",
                weightInTola = 131.25,
                weightInGrams = 1530.875,
                descriptionUr = "سیدہ فاطمۃ الزہراء رضی اللہ عنہا کا مہر 500 درہم چاندی تھا۔"
            ),
            HaqMehrPreset(
                id = "min_shariah_mehr",
                nameEn = "Min. Shariah Mehr (10 Dirhams)",
                nameUr = "شرعی کم از کم مہر",
                subtitleUr = "2.625 تولہ (30.62 گرام)",
                weightInTola = 2.625,
                weightInGrams = 30.618,
                descriptionUr = "فقہ حنفی میں مہر کی کم از کم شرعی مقدار 10 درہم چاندی ہے۔"
            ),
            HaqMehrPreset(
                id = "custom_mehr",
                nameEn = "Custom Quantity",
                nameUr = "حسبِ منشاء مقدار",
                subtitleUr = "صوابدیدی وزن",
                weightInTola = 0.0,
                weightInGrams = 0.0,
                descriptionUr = "اپنی صوابدید کے مطابق چاندی کی مقدار درج کریں۔"
            )
        )
    }

    var selectedPresetId by remember { mutableStateOf("mehr_fatimi") }
    var customTolaWeightInput by remember { mutableStateOf("") }
    var calculationTriggered by remember { mutableStateOf(false) }
    var inputError by remember { mutableStateOf<String?>(null) }

    val activePreset = presets.find { it.id == selectedPresetId } ?: presets[0]

    val verifiedRate = (silverRateState as? SilverRateUiState.Success)?.rate
    val activeCurrency = verifiedRate?.currency ?: selectedCurrency
    val activeCurrencySymbol = verifiedRate?.currencySymbol ?: selectedCurrencySymbol

    // Parse the silver price per tola from customRateInput or live rate
    val silverPricePerTola: Double = customRateInput.replace(',', '.').trim().toDoubleOrNull()
        ?: verifiedRate?.pricePerTola
        ?: 0.0

    // Internal silver quantity resolution
    val requiredTola: Double = when (activePreset.id) {
        "mehr_fatimi" -> 131.25
        "min_shariah_mehr" -> 2.625
        else -> customTolaWeightInput.replace(',', '.').trim().toDoubleOrNull() ?: 0.0
    }
    val requiredGrams: Double = requiredTola * 11.6638125

    // Total Haq Mehr calculation
    val calculatedTotalValue: Double? = if (silverPricePerTola > 0.0 && requiredTola > 0.0) {
        requiredTola * silverPricePerTola
    } else {
        null
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // -------------------------------------------------------------
        // STEP 1: PRESET SELECTION
        // -------------------------------------------------------------
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, EditorialBorder),
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "1. Select Shariah Standard • مہر کا معیار منتخب کریں",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    presets.forEach { preset ->
                        val isSelected = selectedPresetId == preset.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedPresetId = preset.id
                                    inputError = null
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFFF0FDFA) else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) Color(0xFF0F766E) else EditorialBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = preset.nameUr,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) Color(0xFF0F766E) else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = preset.nameEn,
                                        fontSize = 11.sp,
                                        color = EditorialTextSecondary
                                    )
                                }
                                Surface(
                                    color = if (isSelected) Color(0xFF0F766E) else Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = preset.subtitleUr,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color(0xFF475569),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // If Custom is selected, show single simple tola weight input
                AnimatedVisibility(visible = selectedPresetId == "custom_mehr") {
                    OutlinedTextField(
                        value = customTolaWeightInput,
                        onValueChange = {
                            customTolaWeightInput = it
                            inputError = null
                        },
                        label = { Text("Silver Weight in Tola (چاندی کا وزن تولہ میں)") },
                        placeholder = { Text("e.g. 50 or 100") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("custom_tola_input")
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // STEP 2: LIVE SILVER PRICE RETRIEVAL & PRICE FIELD
        // -------------------------------------------------------------
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, EditorialBorder),
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "2. Silver Price • چاندی کی قیمت",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$selectedCity, $selectedCountry",
                        fontSize = 11.sp,
                        color = EditorialTextSecondary
                    )
                }

                // Silver Price Input Field
                OutlinedTextField(
                    value = customRateInput,
                    onValueChange = {
                        viewModel.setCustomRateInput(it)
                        viewModel.setCustomRateMode(true)
                        inputError = null
                    },
                    label = { Text("Silver Price (فی تولہ چاندی کی قیمت)") },
                    placeholder = { Text("e.g. 3350 per Tola") },
                    leadingIcon = {
                        Text(
                            text = "$activeCurrencySymbol ",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    },
                    trailingIcon = {
                        Text(
                            text = "per Tola",
                            fontSize = 12.sp,
                            color = EditorialTextSecondary,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("silver_price_input")
                )

                // Compact Row: Get Current Silver Price button + Rate Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            viewModel.fetchLiveSilverRate(forceRefresh = true)
                            inputError = null
                        },
                        enabled = silverRateState !is SilverRateUiState.Loading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0F766E),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("refresh_silver_price_button")
                    ) {
                        if (silverRateState is SilverRateUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Fetching...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Get Current Silver Price", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Rate metadata badge
                    when (val state = silverRateState) {
                        is SilverRateUiState.Success -> {
                            val rate = state.rate
                            val (badgeText, badgeBg, badgeTextColor) = when (rate.freshnessStatus) {
                                com.example.data.model.PriceFreshnessStatus.LIVE -> Triple("✓ Live Rate", Color(0xFFDCFCE7), Color(0xFF15803D))
                                com.example.data.model.PriceFreshnessStatus.CACHED -> Triple("Cached Rate", Color(0xFFFEF3C7), Color(0xFF92400E))
                                com.example.data.model.PriceFreshnessStatus.FALLBACK -> Triple("Reference Rate", Color(0xFFF1F5F9), Color(0xFF475569))
                            }
                            Surface(
                                color = badgeBg,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = badgeText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeTextColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        else -> {}
                    }
                }

                // Rate source info
                if (silverRateState is SilverRateUiState.Success) {
                    val rate = (silverRateState as SilverRateUiState.Success).rate
                    Text(
                        text = "Source: ${rate.sourceWebsite.ifBlank { "Sarafa Market" }}${if (rate.convertedFromInternational) " (Spot)" else ""} • ${rate.retrievedDateTime.ifBlank { "Live" }}",
                        fontSize = 10.sp,
                        color = EditorialTextSecondary
                    )
                } else if (silverRateState is SilverRateUiState.Error) {
                    val msg = (silverRateState as SilverRateUiState.Error).message
                    Text(
                        text = msg,
                        fontSize = 11.sp,
                        color = Color(0xFFDC2626)
                    )
                }

                if (inputError != null) {
                    Text(
                        text = inputError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Calculate Button
                Button(
                    onClick = {
                        if (silverPricePerTola <= 0.0) {
                            inputError = "Please get or enter the current silver price per tola."
                            calculationTriggered = false
                        } else if (requiredTola <= 0.0) {
                            inputError = "Please enter a valid silver weight."
                            calculationTriggered = false
                        } else {
                            inputError = null
                            calculationTriggered = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("calculate_haq_mehr_button")
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Calculate", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // -------------------------------------------------------------
        // STEP 3: RESULT & CALCULATION METHOD
        // -------------------------------------------------------------
        if (calculationTriggered && calculatedTotalValue != null) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF0F172A),
                shadowElevation = 3.dp
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "TOTAL HAQ MEHR AMOUNT • مجموعی مہر",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2DD4BF)
                    )

                    Text(
                        text = "$activeCurrencySymbol ${currencyFormatter.format(calculatedTotalValue)}",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFCD34D)
                    )

                    HorizontalDivider(color = Color(0xFF334155))

                    Text(
                        text = "Calculation Method • حساب کا تفصیلی طریقہ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Selected Mehr Standard:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text(activePreset.nameEn, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Required Silver Quantity:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text(
                                text = "${String.format(Locale.US, "%.3f", requiredTola)} Tola (${String.format(Locale.US, "%.2f", requiredGrams)}g)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2DD4BF)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Silver Rate Used:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text(
                                text = "$activeCurrencySymbol ${currencyFormatter.format(silverPricePerTola)} / Tola",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Calculation Formula:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text(
                                text = "${String.format(Locale.US, "%.3f", requiredTola)} Tola × $activeCurrencySymbol ${currencyFormatter.format(silverPricePerTola)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFCD34D)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val textToCopy = "💍 Haq Mehr Calculation:\nStandard: ${activePreset.nameEn} (${activePreset.nameUr})\nRequired Silver: ${String.format(Locale.US, "%.3f", requiredTola)} Tola (${String.format(Locale.US, "%.2f", requiredGrams)}g)\nSilver Rate: $activeCurrencySymbol ${currencyFormatter.format(silverPricePerTola)} / Tola\nCalculation: ${String.format(Locale.US, "%.3f", requiredTola)} × $activeCurrencySymbol ${currencyFormatter.format(silverPricePerTola)}\nTotal Haq Mehr: $activeCurrencySymbol ${currencyFormatter.format(calculatedTotalValue)}\nLocation: $selectedCity, $selectedCountry"
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Haq Mehr Calculation", textToCopy))
                                Toast.makeText(context, "Haq Mehr calculation copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val textToShare = "💍 Haq Mehr Calculation:\nStandard: ${activePreset.nameEn}\nRequired Silver: ${String.format(Locale.US, "%.3f", requiredTola)} Tola (${String.format(Locale.US, "%.2f", requiredGrams)}g)\nSilver Rate: $activeCurrencySymbol ${currencyFormatter.format(silverPricePerTola)} / Tola\nTotal Haq Mehr: $activeCurrencySymbol ${currencyFormatter.format(calculatedTotalValue)}"
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, textToShare)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Haq Mehr Calculation"))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 2: ZAKAT CALCULATOR
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ZakatTabContent(
    viewModel: LisanViewModel,
    currencyFormatter: NumberFormat
) {
    val context = LocalContext.current

    val silverRateState by viewModel.silverRateState.collectAsState()
    val goldRateState by viewModel.goldRateState.collectAsState()

    val zakatCashInput by viewModel.zakatCashInput.collectAsState()
    val zakatSilverAmountInput by viewModel.zakatSilverAmountInput.collectAsState()
    val zakatSilverUnit by viewModel.zakatSilverUnit.collectAsState()
    val zakatGoldAmountInput by viewModel.zakatGoldAmountInput.collectAsState()
    val zakatGoldUnit by viewModel.zakatGoldUnit.collectAsState()
    val zakatGoldPurity by viewModel.zakatGoldPurity.collectAsState()
    val zakatBusinessStockInput by viewModel.zakatBusinessStockInput.collectAsState()
    val zakatLiabilitiesInput by viewModel.zakatLiabilitiesInput.collectAsState()
    val zakatNisabStandard by viewModel.zakatNisabStandard.collectAsState()

    val isZakatCustomRateMode by viewModel.isZakatCustomRateMode.collectAsState()
    val zakatCustomGoldRate by viewModel.zakatCustomGoldRate.collectAsState()
    val zakatCustomSilverRate by viewModel.zakatCustomSilverRate.collectAsState()

    val selectedCity by viewModel.selectedCity.collectAsState()
    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val selectedCurrencySymbol by viewModel.selectedCurrencySymbol.collectAsState()

    var calculationTriggered by remember { mutableStateOf(false) }
    var zakatErrorMessage by remember { mutableStateOf<String?>(null) }

    val silverRate = (silverRateState as? SilverRateUiState.Success)?.rate
    val goldRate = (goldRateState as? GoldRateUiState.Success)?.rate

    val activeCurrency = silverRate?.currency ?: goldRate?.currency ?: selectedCurrency
    val activeCurrencySymbol = silverRate?.currencySymbol ?: goldRate?.currencySymbol ?: selectedCurrencySymbol

    val defaultSilverPricePerGram = silverRate?.pricePerGram ?: (silverRate?.pricePerTola?.let { it / 11.6638125 } ?: 0.0)
    val defaultGoldPricePerGram = goldRate?.pricePerGram ?: (goldRate?.pricePerTola?.let { it / 11.6638125 } ?: 0.0)

    val effectiveSilverPricePerGram = if (isZakatCustomRateMode) {
        val parsed = zakatCustomSilverRate.replace(',', '.').trim().toDoubleOrNull() ?: 0.0
        parsed / 11.6638125
    } else {
        defaultSilverPricePerGram
    }

    val effectiveGoldPricePerGram = if (isZakatCustomRateMode) {
        val parsed = zakatCustomGoldRate.replace(',', '.').trim().toDoubleOrNull() ?: 0.0
        parsed / 11.6638125
    } else {
        defaultGoldPricePerGram
    }

    // Local Arithmetic Calculations (Strict precision)
    val cashAmount = zakatCashInput.replace(',', '.').trim().toDoubleOrNull() ?: 0.0
    val stockAmount = zakatBusinessStockInput.replace(',', '.').trim().toDoubleOrNull() ?: 0.0
    val liabilitiesAmount = zakatLiabilitiesInput.replace(',', '.').trim().toDoubleOrNull() ?: 0.0

    val silverParsed = zakatSilverAmountInput.replace(',', '.').trim().toDoubleOrNull() ?: 0.0
    val silverGrams = when (zakatSilverUnit) {
        SilverUnit.TOLA -> silverParsed * 11.6638125
        SilverUnit.GRAMS -> silverParsed
        SilverUnit.TEN_GRAMS -> silverParsed * 10.0
        SilverUnit.TROY_OUNCE -> silverParsed * 31.1034768
        SilverUnit.KILOGRAM -> silverParsed * 1000.0
    }
    val silverMonetaryValue = silverGrams * effectiveSilverPricePerGram

    val goldParsed = zakatGoldAmountInput.replace(',', '.').trim().toDoubleOrNull() ?: 0.0
    val goldGrams = when (zakatGoldUnit) {
        GoldUnit.TOLA -> goldParsed * 11.6638125
        GoldUnit.GRAMS -> goldParsed
        GoldUnit.TEN_GRAMS -> goldParsed * 10.0
        GoldUnit.TROY_OUNCE -> goldParsed * 31.1034768
    }
    val goldMonetaryValue = goldGrams * effectiveGoldPricePerGram * zakatGoldPurity.purityFactor

    val grossZakatableWealth = cashAmount + silverMonetaryValue + goldMonetaryValue + stockAmount
    val netZakatableWealth = maxOf(0.0, grossZakatableWealth - liabilitiesAmount)

    // Nisab Threshold
    val nisabValueMonetary = if (zakatNisabStandard.isSilver) {
        zakatNisabStandard.weightGrams * effectiveSilverPricePerGram
    } else {
        zakatNisabStandard.weightGrams * effectiveGoldPricePerGram
    }

    val isNisabMet = netZakatableWealth >= nisabValueMonetary && nisabValueMonetary > 0.0
    val zakatPayable = if (isNisabMet) netZakatableWealth * 0.025 else 0.0

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Rates Banner for Zakat
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, EditorialBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Current Bullion Benchmark ($selectedCity)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { viewModel.setZakatCustomRateMode(!isZakatCustomRateMode) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isZakatCustomRateMode) Icons.Default.Tune else Icons.Outlined.Tune,
                                contentDescription = "Toggle Custom Bullion Rates",
                                tint = if (isZakatCustomRateMode) Color(0xFF0F766E) else EditorialTextSecondary
                            )
                        }

                        IconButton(
                            onClick = { viewModel.fetchLiveBullionRates(forceRefresh = true) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Bullion Rates",
                                tint = Color(0xFF0F766E)
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = isZakatCustomRateMode) {
                    Surface(
                        color = Color(0xFFFFFBEB),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Custom Gold & Silver Rates (per tola):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = zakatCustomGoldRate,
                                    onValueChange = { viewModel.setZakatCustomGoldRate(it) },
                                    label = { Text("Gold/Tola") },
                                    placeholder = { Text("285000") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = zakatCustomSilverRate,
                                    onValueChange = { viewModel.setZakatCustomSilverRate(it) },
                                    label = { Text("Silver/Tola") },
                                    placeholder = { Text("3350") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                if (silverRateState is SilverRateUiState.Error || goldRateState is GoldRateUiState.Error) {
                    val errorMsg = when {
                        silverRateState is SilverRateUiState.Error -> (silverRateState as SilverRateUiState.Error).message
                        goldRateState is GoldRateUiState.Error -> (goldRateState as GoldRateUiState.Error).message
                        else -> "Unable to update live bullion rates."
                    }
                    Surface(
                        color = Color(0xFFFEF2F2),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFFECACA)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                            Text(
                                text = errorMsg,
                                fontSize = 11.sp,
                                color = Color(0xFFB91C1C)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFF0FDFA),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFCCFBF1))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Silver 999 (چاندی)", fontSize = 11.sp, color = Color(0xFF0F766E), fontWeight = FontWeight.Bold)
                            Text(
                                text = if (effectiveSilverPricePerGram > 0.0) "$activeCurrencySymbol ${currencyFormatter.format(effectiveSilverPricePerGram * 11.6638125)} / tola" else "Updating...",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF134E4A)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFFEFCE8),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFFEF08A))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Gold 24K (خالص سونا)", fontSize = 11.sp, color = Color(0xFFB45309), fontWeight = FontWeight.Bold)
                            Text(
                                text = if (effectiveGoldPricePerGram > 0.0) "$activeCurrencySymbol ${currencyFormatter.format(effectiveGoldPricePerGram * 11.6638125)} / tola" else "Updating...",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF78350F)
                            )
                        }
                    }
                }
            }
        }

        // Nisab Standard Selector
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, EditorialBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Nisab Basis • معیار نصابِ زکوٰۃ:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ZakatNisabStandard.values().forEach { standard ->
                        val isSelected = zakatNisabStandard == standard
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.setZakatNisabStandard(standard) },
                            color = if (isSelected) Color(0xFF0F766E) else Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, if (isSelected) Color(0xFF0F766E) else EditorialBorder),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = standard.titleUr,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color(0xFF1E293B),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = standard.titleEn,
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color(0xFFCCFBF1) else EditorialTextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Text(
                    text = zakatNisabStandard.descriptionUr,
                    fontSize = 11.sp,
                    color = EditorialTextSecondary,
                    lineHeight = 16.sp
                )

                // Current Nisab Monetary Value Card
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Current Nisab Threshold (${zakatNisabStandard.weightTola} Tola):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155)
                        )
                        Text(
                            text = "$activeCurrencySymbol ${currencyFormatter.format(nisabValueMonetary)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F766E)
                        )
                    }
                }
            }
        }

        // Input Fields (Cash Primary, Gold, Silver, Trade, Debts)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, EditorialBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Zakatable Assets • قابلِ زکوٰۃ اثاثہ جات:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // 1. Cash / Money (PRIMARY)
                OutlinedTextField(
                    value = zakatCashInput,
                    onValueChange = {
                        viewModel.setZakatCashInput(it)
                        zakatErrorMessage = null
                    },
                    label = { Text("1. Cash in Hand & Bank Balances (نقد رقم و بینک بیلنس)") },
                    placeholder = { Text("e.g. 500000") },
                    leadingIcon = { Text(activeCurrencySymbol, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("zakat_cash_input")
                )

                // 2. Gold (Optional)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("2. Gold (سونا) - Optional:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = zakatGoldAmountInput,
                            onValueChange = {
                                viewModel.setZakatGoldAmountInput(it)
                                zakatErrorMessage = null
                            },
                            placeholder = { Text("Weight") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.2f)
                        )

                        var goldUnitDropdown by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedButton(
                                onClick = { goldUnitDropdown = true },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text(zakatGoldUnit.labelEn, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                            DropdownMenu(
                                expanded = goldUnitDropdown,
                                onDismissRequest = { goldUnitDropdown = false }
                            ) {
                                GoldUnit.values().forEach { unit ->
                                    DropdownMenuItem(
                                        text = { Text("${unit.labelEn} (${unit.labelUr})") },
                                        onClick = {
                                            viewModel.setZakatGoldUnit(unit)
                                            goldUnitDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Gold Purity Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        GoldPurity.values().forEach { purity ->
                            val isPuritySelected = zakatGoldPurity == purity
                            FilterChip(
                                selected = isPuritySelected,
                                onClick = { viewModel.setZakatGoldPurity(purity) },
                                label = { Text(purity.id.uppercase(), fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // 3. Silver (Optional)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("3. Silver (چاندی) - Optional:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = zakatSilverAmountInput,
                            onValueChange = {
                                viewModel.setZakatSilverAmountInput(it)
                                zakatErrorMessage = null
                            },
                            placeholder = { Text("Weight") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.2f)
                        )

                        var silverUnitDropdown by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedButton(
                                onClick = { silverUnitDropdown = true },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text(zakatSilverUnit.labelEn, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                            DropdownMenu(
                                expanded = silverUnitDropdown,
                                onDismissRequest = { silverUnitDropdown = false }
                            ) {
                                SilverUnit.values().forEach { unit ->
                                    DropdownMenuItem(
                                        text = { Text("${unit.labelEn} (${unit.labelUr})") },
                                        onClick = {
                                            viewModel.setZakatSilverUnit(unit)
                                            silverUnitDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Trade Goods & Merchandise
                OutlinedTextField(
                    value = zakatBusinessStockInput,
                    onValueChange = {
                        viewModel.setZakatBusinessStockInput(it)
                        zakatErrorMessage = null
                    },
                    label = { Text("4. Business Inventory / Trade Stock (مالِ تجارت)") },
                    placeholder = { Text("e.g. 200000") },
                    leadingIcon = { Text(activeCurrencySymbol, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // 5. Deductible Liabilities & Debts
                OutlinedTextField(
                    value = zakatLiabilitiesInput,
                    onValueChange = {
                        viewModel.setZakatLiabilitiesInput(it)
                        zakatErrorMessage = null
                    },
                    label = { Text("5. Short-term Deductible Debts (واجب الادا قرضے)") },
                    placeholder = { Text("e.g. 50000") },
                    leadingIcon = { Text(activeCurrencySymbol, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (zakatErrorMessage != null) {
                    Text(
                        text = zakatErrorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Action Buttons: Calculate Zakat & Clear
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.resetZakat()
                            calculationTriggered = false
                            zakatErrorMessage = null
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            if (grossZakatableWealth <= 0.0) {
                                zakatErrorMessage = "Please enter your cash or asset amount to calculate Zakat."
                                calculationTriggered = false
                            } else {
                                zakatErrorMessage = null
                                calculationTriggered = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(2f).height(50.dp).testTag("calculate_zakat_button")
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Calculate Zakat", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Zakat Results & Breakdown
        if (calculationTriggered && grossZakatableWealth > 0.0) {
            // Nisab Status Indicator Banner
            Surface(
                color = if (isNisabMet) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, if (isNisabMet) Color(0xFF86EFAC) else Color(0xFFFDE68A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = if (isNisabMet) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (isNisabMet) Color(0xFF15803D) else Color(0xFFB45309),
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = if (isNisabMet) "Above Nisab • زکوٰۃ فرض ہے (Nisab Met)" else "Below Nisab Threshold • نصاب سے کم ہے",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isNisabMet) Color(0xFF14532D) else Color(0xFF78350F)
                        )
                        Text(
                            text = if (isNisabMet)
                                "آپ کا خالص مال نصاب ($activeCurrencySymbol ${currencyFormatter.format(nisabValueMonetary)}) سے زیادہ ہے، 2.5% زکوٰۃ واجب الادا ہے۔"
                            else
                                "خالص مال نصاب ($activeCurrencySymbol ${currencyFormatter.format(nisabValueMonetary)}) سے کم ہے، فی الحال زکوٰۃ واجب نہیں۔",
                            fontSize = 11.sp,
                            color = if (isNisabMet) Color(0xFF166534) else Color(0xFF92400E)
                        )
                    }
                }
            }

            // Zakat Result Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF0F172A),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "ESTIMATED ZAKAT PAYABLE (2.5% / 1/40th) • قابلِ ادائیگی زکوٰۃ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2DD4BF)
                    )

                    Text(
                        text = "$activeCurrencySymbol ${currencyFormatter.format(zakatPayable)}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isNisabMet) Color(0xFFFCD34D) else Color(0xFF94A3B8)
                    )

                    HorizontalDivider(color = Color(0xFF334155))

                    // Breakdown list
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("💵 Cash / Bank Balances:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text("$activeCurrencySymbol ${currencyFormatter.format(cashAmount)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        if (goldMonetaryValue > 0.0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("🥇 Gold (${String.format(Locale.US, "%.2f", goldParsed)} ${zakatGoldUnit.labelEn} ${zakatGoldPurity.id.uppercase()}):", fontSize = 12.sp, color = Color(0xFF94A3B8))
                                Text("$activeCurrencySymbol ${currencyFormatter.format(goldMonetaryValue)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        if (silverMonetaryValue > 0.0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("🥈 Silver (${String.format(Locale.US, "%.2f", silverParsed)} ${zakatSilverUnit.labelEn}):", fontSize = 12.sp, color = Color(0xFF94A3B8))
                                Text("$activeCurrencySymbol ${currencyFormatter.format(silverMonetaryValue)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        if (stockAmount > 0.0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("📦 Trade Goods:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                                Text("$activeCurrencySymbol ${currencyFormatter.format(stockAmount)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        if (liabilitiesAmount > 0.0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("➖ Short-term Debts:", fontSize = 12.sp, color = Color(0xFFF87171))
                                Text("- $activeCurrencySymbol ${currencyFormatter.format(liabilitiesAmount)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF87171))
                            }
                        }
                        HorizontalDivider(color = Color(0xFF334155))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("💰 Net Zakatable Wealth:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2DD4BF))
                            Text("$activeCurrencySymbol ${currencyFormatter.format(netZakatableWealth)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2DD4BF))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("⚖️ Nisab Applied (${zakatNisabStandard.titleEn}):", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text("$activeCurrencySymbol ${currencyFormatter.format(nisabValueMonetary)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("🧮 Zakat Rate (2.5%):", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text(if (isNisabMet) "$activeCurrencySymbol ${currencyFormatter.format(netZakatableWealth)} × 2.5% = $activeCurrencySymbol ${currencyFormatter.format(zakatPayable)}" else "Below Nisab (Rs. 0)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFCD34D))
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val textToCopy = "💰 Zakat Calculation:\nCash: $activeCurrencySymbol ${currencyFormatter.format(cashAmount)}\nGold: $activeCurrencySymbol ${currencyFormatter.format(goldMonetaryValue)}\nSilver: $activeCurrencySymbol ${currencyFormatter.format(silverMonetaryValue)}\nNet Wealth: $activeCurrencySymbol ${currencyFormatter.format(netZakatableWealth)}\nNisab (${zakatNisabStandard.titleEn}): $activeCurrencySymbol ${currencyFormatter.format(nisabValueMonetary)}\nZakat Due (2.5%): $activeCurrencySymbol ${currencyFormatter.format(zakatPayable)}"
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Zakat Calculation", textToCopy))
                                Toast.makeText(context, "Zakat summary copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val textToShare = "💰 Zakat Calculation:\nNet Zakatable Wealth: $activeCurrencySymbol ${currencyFormatter.format(netZakatableWealth)}\nNisab: $activeCurrencySymbol ${currencyFormatter.format(nisabValueMonetary)}\nZakat Due (2.5%): $activeCurrencySymbol ${currencyFormatter.format(zakatPayable)}"
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, textToShare)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Zakat Calculation"))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 3: FITRANA CALCULATOR
// -------------------------------------------------------------
@Composable
private fun FitranaTabContent(
    viewModel: LisanViewModel,
    currencyFormatter: NumberFormat
) {
    val context = LocalContext.current

    val selectedCommodity by viewModel.fitranaCommodity.collectAsState()
    val personsCountInput by viewModel.fitranaPersonsCount.collectAsState()
    val customPriceInput by viewModel.fitranaCustomPricePerKg.collectAsState()
    val commodityPricesState by viewModel.commodityPricesState.collectAsState()

    val selectedCity by viewModel.selectedCity.collectAsState()
    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val selectedCurrencySymbol by viewModel.selectedCurrencySymbol.collectAsState()

    var calculationTriggered by remember { mutableStateOf(true) }
    var fitranaError by remember { mutableStateOf<String?>(null) }

    // Resolve commodity price from PriceRepository State
    val commodityPrices = (commodityPricesState as? CommodityPricesUiState.Success)?.prices
    val currentInfo = commodityPrices?.get(selectedCommodity)

    val repositoryPricePerKg = currentInfo?.pricePerKg ?: selectedCommodity.defaultPricePerKgPkr
    val effectivePricePerKg = customPriceInput.toDoubleOrNull() ?: repositoryPricePerKg
    val pricePerMaund = effectivePricePerKg * 40.0

    val priceSource = currentInfo?.sourceName ?: "Pakistan Mandi Commodity Bulletin"
    val freshnessStatus = currentInfo?.freshnessStatus ?: PriceFreshnessStatus.LIVE
    val formattedTimestamp = currentInfo?.formattedTimestamp ?: "Live"

    val personsCount = personsCountInput.toIntOrNull()?.coerceAtLeast(1) ?: 1
    val requiredKgPerPerson = selectedCommodity.requiredKgPerPerson
    val totalRequiredKg = requiredKgPerPerson * personsCount
    val fitranaPerPerson = requiredKgPerPerson * effectivePricePerKg
    val totalFitranaAmount = totalRequiredKg * effectivePricePerKg

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Commodity Selection
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, EditorialBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Select Fitrana Commodity • اجناس صدقۃ الفطر:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FitranaCommodity.values().forEach { commodity ->
                        val isSelected = selectedCommodity == commodity
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.setFitranaCommodity(commodity)
                                    viewModel.setFitranaCustomPricePerKg("")
                                    fitranaError = null
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFFF0FDFA) else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) Color(0xFF0F766E) else EditorialBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "${commodity.nameUr} (${commodity.nameEn})",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(0xFF0F766E) else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "شرعی مقدار: ${commodity.hadithMeasureUr}",
                                        fontSize = 11.sp,
                                        color = EditorialTextSecondary
                                    )
                                }
                                Surface(
                                    color = if (isSelected) Color(0xFF0F766E) else Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = commodity.quantityDescUr,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color(0xFF334155),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Commodity Price Info & Status Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, EditorialBorder)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Commodity Rate Benchmark",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = { viewModel.fetchLiveCommodityPrices(forceRefresh = true) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Price",
                            tint = Color(0xFF0F766E),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                if (commodityPricesState is CommodityPricesUiState.Error) {
                    val errorState = commodityPricesState as CommodityPricesUiState.Error
                    Surface(
                        color = Color(0xFFFEF2F2),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFFECACA)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                            Text(
                                text = errorState.message,
                                fontSize = 11.sp,
                                color = Color(0xFFB91C1C)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFF0FDFA),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFCCFBF1))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Price Per Kg (کلوگرام)", fontSize = 11.sp, color = Color(0xFF0F766E))
                            Text(
                                text = "$selectedCurrencySymbol ${currencyFormatter.format(effectivePricePerKg)} / kg",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF134E4A)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFF0FDFA),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFCCFBF1))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Price Per Maund (من 40kg)", fontSize = 11.sp, color = Color(0xFF0F766E))
                            Text(
                                text = "$selectedCurrencySymbol ${currencyFormatter.format(pricePerMaund)} / maund",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF134E4A)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Source: $priceSource • Updated: $formattedTimestamp",
                        fontSize = 10.sp,
                        color = EditorialTextSecondary
                    )

                    val (bgColor, textColor, label) = when (freshnessStatus) {
                        PriceFreshnessStatus.LIVE -> Triple(Color(0xFFDCFCE7), Color(0xFF15803D), "✓ LIVE Rate")
                        PriceFreshnessStatus.CACHED -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "🕒 CACHED Rate")
                        PriceFreshnessStatus.FALLBACK -> Triple(Color(0xFFF3F4F6), Color(0xFF4B5563), "ℹ️ FALLBACK Benchmark")
                    }

                    Surface(
                        color = bgColor,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Persons Count & Price/kg Adjuster
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, EditorialBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Persons & Local Rate • تعداد افراد و مقامی قیمت:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = personsCountInput,
                        onValueChange = {
                            viewModel.setFitranaPersonsCount(it)
                            fitranaError = null
                        },
                        label = { Text("Persons (افراد کی تعداد)") },
                        placeholder = { Text("1") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = customPriceInput.ifBlank { effectivePricePerKg.toString() },
                        onValueChange = {
                            viewModel.setFitranaCustomPricePerKg(it)
                            fitranaError = null
                        },
                        label = { Text("Price/Kg ($selectedCurrencySymbol)") },
                        placeholder = { Text(repositoryPricePerKg.toString()) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                if (fitranaError != null) {
                    Text(
                        text = fitranaError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Text(
                    text = "دارالعلوم کراچی و جامعہ بنوری ٹاؤن کے مستند فتاویٰ کے مطابق گندم کا فطرانہ نصف صاع (2.04 کلوگرام) اور جو/کھجور/کشمش کا فطرانہ ایک صاع (4.08 کلوگرام) مقرر ہے۔",
                    fontSize = 11.sp,
                    color = EditorialTextSecondary,
                    lineHeight = 16.sp
                )

                // Action Buttons: Calculate Fitrana & Clear
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.resetFitrana()
                            calculationTriggered = true
                            fitranaError = null
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            if (personsCount <= 0 || effectivePricePerKg <= 0.0) {
                                fitranaError = "Please enter valid number of persons and commodity price."
                                calculationTriggered = false
                            } else {
                                fitranaError = null
                                calculationTriggered = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(2f).height(50.dp).testTag("calculate_fitrana_button")
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Calculate Fitrana", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Fitrana Result Card
        if (calculationTriggered && totalFitranaAmount > 0.0) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF0F172A),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "TOTAL FITRANA PAYABLE • واجب الادا صدقۃ الفطر",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2DD4BF)
                    )

                    Text(
                        text = "$selectedCurrencySymbol ${currencyFormatter.format(totalFitranaAmount)}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFCD34D)
                    )

                    HorizontalDivider(color = Color(0xFF334155))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Commodity Selected:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text("${selectedCommodity.nameUr} (${selectedCommodity.nameEn})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Hadith Measure:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text(selectedCommodity.quantityDescUr, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2DD4BF))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Per Person Quantity:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text("${selectedCommodity.requiredKgPerPerson} kg", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Price Per Kg:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text("$selectedCurrencySymbol ${currencyFormatter.format(effectivePricePerKg)} / kg", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Per Person Fitrana Amount:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text("$selectedCurrencySymbol ${currencyFormatter.format(fitranaPerPerson)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFCD34D))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Family Quantity ($personsCount persons):", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text(String.format(Locale.US, "%.2f kg", totalRequiredKg), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2DD4BF))
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val textToCopy = "🌾 Fitrana Calculation:\nCommodity: ${selectedCommodity.nameEn} (${selectedCommodity.nameUr})\nPersons: $personsCount\nPer Person Rate: $selectedCurrencySymbol ${currencyFormatter.format(fitranaPerPerson)} (${selectedCommodity.requiredKgPerPerson} kg)\nTotal Family Fitrana: $selectedCurrencySymbol ${currencyFormatter.format(totalFitranaAmount)}"
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Fitrana Calculation", textToCopy))
                                Toast.makeText(context, "Fitrana calculation copied!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val textToShare = "🌾 Fitrana Calculation:\nCommodity: ${selectedCommodity.nameEn}\nPersons: $personsCount\nTotal Amount: $selectedCurrencySymbol ${currencyFormatter.format(totalFitranaAmount)}"
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, textToShare)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Fitrana Calculation"))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 4: USHR CALCULATOR (AGRICULTURAL PRODUCE)
// -------------------------------------------------------------
@Composable
private fun UshrTabContent(
    viewModel: LisanViewModel,
    currencyFormatter: NumberFormat
) {
    val context = LocalContext.current

    val produceAmountInput by viewModel.ushrProduceAmount.collectAsState()
    val produceUnit by viewModel.ushrProduceUnit.collectAsState()
    val irrigationType by viewModel.ushrIrrigationType.collectAsState()
    val marketPricePerUnitInput by viewModel.ushrMarketPricePerUnit.collectAsState()

    val selectedCurrencySymbol by viewModel.selectedCurrencySymbol.collectAsState()

    var calculationTriggered by remember { mutableStateOf(false) }
    var ushrError by remember { mutableStateOf<String?>(null) }

    val parsedProduceAmount = produceAmountInput.replace(',', '.').trim().toDoubleOrNull() ?: 0.0

    // Strict local unit conversions: 1 maund = 40 kg, 1 metric ton = 1,000 kg = 25 maunds
    val totalWeightKg = parsedProduceAmount * produceUnit.kgFactor
    val totalWeightMaunds = totalWeightKg / 40.0
    val totalWeightMetricTons = totalWeightKg / 1000.0

    val ushrRateFraction = irrigationType.ratePercent / 100.0
    val ushrDueKg = totalWeightKg * ushrRateFraction
    val ushrDueMaunds = totalWeightMaunds * ushrRateFraction
    val ushrDueMetricTons = totalWeightMetricTons * ushrRateFraction

    val parsedPricePerUnit = marketPricePerUnitInput.replace(',', '.').trim().toDoubleOrNull()
    val optionalMonetaryValue = if (parsedPricePerUnit != null && parsedPricePerUnit > 0.0) {
        val ushrDueInSelectedUnit = when (produceUnit) {
            UshrProduceUnit.MAUND -> ushrDueMaunds
            UshrProduceUnit.KILOGRAM -> ushrDueKg
            UshrProduceUnit.METRIC_TON -> ushrDueMetricTons
        }
        ushrDueInSelectedUnit * parsedPricePerUnit
    } else {
        null
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Irrigation Type Selector
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, EditorialBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Water / Irrigation Source • آبپاشی کا ذریعہ و شرعی شرح:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    UshrIrrigationType.values().forEach { type ->
                        val isSelected = irrigationType == type
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.setUshrIrrigationType(type)
                                    ushrError = null
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFFF0FDFA) else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) Color(0xFF0F766E) else EditorialBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(type.titleUr, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (isSelected) Color(0xFF0F766E) else MaterialTheme.colorScheme.onSurface)
                                    }
                                    Text(type.shariahBasisUr, fontSize = 11.sp, color = EditorialTextSecondary, lineHeight = 16.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = if (isSelected) Color(0xFF0F766E) else Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${type.ratePercent}% (${type.rateFractionUr})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color(0xFF334155),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Produce Input & Units (Maund, KG, Metric Ton)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, EditorialBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Agricultural Produce • زرعی پیداوار کی مقدار:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = produceAmountInput,
                        onValueChange = {
                            viewModel.setUshrProduceAmount(it)
                            ushrError = null
                        },
                        label = { Text("Produce Amount") },
                        placeholder = { Text("e.g. 50") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        isError = ushrError != null,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.2f).testTag("ushr_produce_input")
                    )

                    var unitDropdown by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { unitDropdown = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "${produceUnit.labelEn} (${produceUnit.labelUr})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                        DropdownMenu(
                            expanded = unitDropdown,
                            onDismissRequest = { unitDropdown = false }
                        ) {
                            UshrProduceUnit.values().forEach { unit ->
                                DropdownMenuItem(
                                    text = { Text("${unit.labelEn} (${unit.labelUr})") },
                                    onClick = {
                                        viewModel.setUshrProduceUnit(unit)
                                        unitDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                if (ushrError != null) {
                    Text(
                        text = ushrError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Standard conversion reference table
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, EditorialBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "معیاری اکائیاں: 1 من (Maund) = 40 کلوگرام | 1 میٹرک ٹن (Metric Ton) = 1,000 کلوگرام = 25 من",
                        fontSize = 11.sp,
                        color = Color(0xFF475569),
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                }

                // Optional Price per unit for cash equivalent
                OutlinedTextField(
                    value = marketPricePerUnitInput,
                    onValueChange = { viewModel.setUshrMarketPricePerUnit(it) },
                    label = { Text("Optional Price per ${produceUnit.labelEn} ($selectedCurrencySymbol)") },
                    placeholder = { Text("e.g. 3800 per maund") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Action Buttons: Calculate Ushr & Clear
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.resetUshr()
                            calculationTriggered = false
                            ushrError = null
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            if (parsedProduceAmount <= 0.0) {
                                ushrError = "Please enter a valid produce amount greater than 0."
                                calculationTriggered = false
                            } else {
                                ushrError = null
                                calculationTriggered = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(2f).height(50.dp).testTag("calculate_ushr_button")
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Calculate Ushr", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Ushr Result Card
        if (calculationTriggered && parsedProduceAmount > 0.0) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFF0F172A),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "TOTAL USHR DUE (IN PRODUCE) • واجب الادا عشر برائے فصل",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2DD4BF)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("In Maunds (من)", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                Text(
                                    text = String.format(Locale.US, "%.2f Maunds", ushrDueMaunds),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFCD34D)
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("In Kilograms (کلو)", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                Text(
                                    text = String.format(Locale.US, "%.2f kg", ushrDueKg),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFCD34D)
                                )
                            }
                        }
                    }

                    if (optionalMonetaryValue != null) {
                        Surface(
                            color = Color(0xFF134E4A),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Cash Equivalent (نقدی مالیت):", fontSize = 12.sp, color = Color(0xFFCCFBF1), fontWeight = FontWeight.SemiBold)
                                Text(
                                    "$selectedCurrencySymbol ${currencyFormatter.format(optionalMonetaryValue)}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFCD34D)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFF334155))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Produce Entered:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text("${String.format(Locale.US, "%.2f", parsedProduceAmount)} ${produceUnit.labelEn}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total in Maunds / KG:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text("${String.format(Locale.US, "%.2f", totalWeightMaunds)} Maunds (${String.format(Locale.US, "%.2f", totalWeightKg)} kg)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Irrigation Category:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text(irrigationType.titleEn, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Applicable Rate:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text("${irrigationType.ratePercent}% (${irrigationType.rateFractionUr})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2DD4BF))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Calculation Formula:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text("${String.format(Locale.US, "%.2f", totalWeightMaunds)} Maunds × ${irrigationType.ratePercent}% = ${String.format(Locale.US, "%.2f", ushrDueMaunds)} Maunds", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFCD34D))
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val textToCopy = "🌱 Ushr Calculation:\nProduce: ${String.format(Locale.US, "%.2f", parsedProduceAmount)} ${produceUnit.labelEn} (${String.format(Locale.US, "%.2f", totalWeightMaunds)} Maunds / ${String.format(Locale.US, "%.2f", totalWeightKg)} kg)\nIrrigation: ${irrigationType.titleEn} (${irrigationType.ratePercent}%)\nUshr Due: ${String.format(Locale.US, "%.2f", ushrDueMaunds)} Maunds (${String.format(Locale.US, "%.2f", ushrDueKg)} kg)"
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Ushr Calculation", textToCopy))
                                Toast.makeText(context, "Ushr calculation copied!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val textToShare = "🌱 Ushr Calculation:\nProduce: ${String.format(Locale.US, "%.2f", parsedProduceAmount)} ${produceUnit.labelEn}\nUshr Due: ${String.format(Locale.US, "%.2f", ushrDueMaunds)} Maunds / ${String.format(Locale.US, "%.2f", ushrDueKg)} kg"
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, textToShare)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Ushr Calculation"))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// LOCATION SELECTION DIALOG
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LocationSelectionDialog(
    viewModel: LisanViewModel,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var customCountryText by remember { mutableStateOf("") }
    var customCityText by remember { mutableStateOf("") }

    val countries = remember { CityLocationHelper.supportedCountries }
    val filteredCountries = remember(searchQuery) {
        if (searchQuery.isBlank()) countries else {
            countries.filter {
                it.nameEn.contains(searchQuery, ignoreCase = true) ||
                        it.nameUr.contains(searchQuery, ignoreCase = true) ||
                        it.majorCities.any { city -> city.contains(searchQuery, ignoreCase = true) }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Public, contentDescription = null, tint = Color(0xFF0F766E))
                Text("Select Location / مارکیٹ منتخب کریں", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search country or city...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Supported Bullion Countries & Cities:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EditorialTextSecondary
                )

                filteredCountries.forEach { countryInfo ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, EditorialBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${countryInfo.nameEn} (${countryInfo.nameUr})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F766E)
                                )
                                Text(
                                    text = countryInfo.defaultCurrency,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B)
                                )
                            }

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                countryInfo.majorCities.forEach { city ->
                                    AssistChip(
                                        onClick = {
                                            viewModel.selectCountry(countryInfo.nameEn)
                                            viewModel.selectCity(city)
                                            onDismiss()
                                        },
                                        label = { Text(city, fontSize = 11.sp) }
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = EditorialBorder)

                Text("Or enter custom location:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = customCountryText,
                        onValueChange = { customCountryText = it },
                        placeholder = { Text("Country") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = customCityText,
                        onValueChange = { customCityText = it },
                        placeholder = { Text("City") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (customCountryText.isNotBlank()) {
                        viewModel.selectCountry(customCountryText.trim())
                    }
                    if (customCityText.isNotBlank()) {
                        viewModel.selectCity(customCityText.trim())
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E))
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
