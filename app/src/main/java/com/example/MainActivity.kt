package com.example

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.components.EditorialBottomNav
import com.example.ui.components.EditorialHeader
import com.example.ui.components.LevelSelectDialog
import com.example.ui.screens.AnalyzerScreen
import com.example.ui.screens.IdiomScreen
import com.example.ui.screens.LessonScreen
import com.example.ui.screens.ProgressDashboardScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.SavedNotesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TutorChatScreen
import com.example.ui.screens.HelpAndFeedbackScreen
import com.example.ui.screens.PictureVocabularyRootScreen
import com.example.ui.screens.ImplementApiKeyScreen
import com.example.ui.screens.ChatThemeSelectionScreen
import com.example.ui.screens.HaqMehrCalculatorScreen
import com.example.ui.theme.EditorialBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LisanViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.ui.theme.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import com.example.ui.screens.DailyChallengeScreen
import com.example.ui.screens.LisanDailyScreen
import com.example.ui.screens.LisanJourneyScreen
import com.example.ui.screens.PronunciationPracticeScreen
import com.example.ui.viewmodel.LisanJourneyViewModel
import com.example.ui.navigation.HashRouter
import com.example.ui.components.HashRouterBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.launch

import com.example.ui.screens.UnifiedLibraryScreen
import com.example.ui.screens.PastConversationsScreen
import com.example.ui.components.AuthDialog
import com.example.ui.components.ProfileDialog
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.data.model.AuthState

class MainActivity : ComponentActivity() {

    private val viewModel: LisanViewModel by viewModels()
    private val pictureVocabularyViewModel: com.example.ui.viewmodel.PictureVocabularyViewModel by viewModels()
    private val lisanJourneyViewModel: LisanJourneyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle app open event for launcher icon rotation & last open timestamp
        lifecycleScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            com.example.utils.IconRotationManager.onAppOpened(
                applicationContext,
                viewModel.settingsRepository
            )
        }

        setContent {
            var showSplashScreen by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000L)
                showSplashScreen = false
            }

            val userSettings by viewModel.userSettings.collectAsState()
            val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()
            val darkTheme = when (userSettings.themeMode) {
                "Dark" -> true
                "Light" -> false
                else -> isSystemDark
            }
            MyApplicationTheme(
                darkTheme = darkTheme,
                dynamicColor = userSettings.dynamicColor,
                fontSizeSetting = userSettings.fontSize
            ) {
                if (showSplashScreen) {
                    SplashScreen()
                } else {
                    LisanMainApp(
                        viewModel = viewModel,
                        pictureVocabularyViewModel = pictureVocabularyViewModel,
                        lisanJourneyViewModel = lisanJourneyViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0C20)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.lisan_ai_splash_screen_1786018639638),
            contentDescription = "Lisan AI Splash Screen",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

data class LisanDrawerItem(
    val title: String,
    val subtitleUr: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val index: Int,
    val iconBgColor: Color,
    val iconBorderColor: Color,
    val iconTintColor: Color,
    val badge: String? = null,
    val badgeBgColor: Color = Color(0xFF2563EB)
)

@Composable
fun LisanDrawerItemRow(
    item: LisanDrawerItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = EditorialTheme.colors
    val itemBg = if (isSelected) colors.pillBg else Color.Transparent
    val borderStroke = if (isSelected) BorderStroke(1.dp, colors.accentPurple.copy(alpha = 0.35f)) else null

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = itemBg,
        border = borderStroke,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon Container
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = item.iconBgColor,
                border = BorderStroke(1.dp, item.iconBorderColor),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = item.iconTintColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Title & Subtitle
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 13.5.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isSelected) colors.primary else colors.textPrimary
                )
                Text(
                    text = item.subtitleUr,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) colors.accentPurple else colors.textSecondary
                )
            }

            // Right-aligned Badge or Indicator
            if (item.badge != null) {
                Surface(
                    color = item.badgeBgColor,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = item.badge,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                    )
                }
            } else if (isSelected) {
                Surface(
                    color = colors.accentPurple,
                    shape = CircleShape,
                    modifier = Modifier.size(8.dp)
                ) {}
            }
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun LisanMainApp(
    viewModel: LisanViewModel,
    pictureVocabularyViewModel: com.example.ui.viewmodel.PictureVocabularyViewModel,
    lisanJourneyViewModel: LisanJourneyViewModel
) {
    val currentRoute by HashRouter.currentRoute.collectAsState()
    val selectedTab = remember(currentRoute) { HashRouter.routeToTab(currentRoute) }
    val proficiencyLevel by viewModel.proficiencyLevel.collectAsState()
    val isTutorChatActive by viewModel.isTutorChatActive.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()

    LaunchedEffect(userSettings.defaultLearningTab) {
        HashRouter.initializeStartupRoute(userSettings.defaultLearningTab)
    }

    val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()
    val isDarkTheme = when (userSettings.themeMode) {
        "Dark" -> true
        "Light" -> false
        else -> isSystemDark
    }
    val isMoreMenuOpen by viewModel.isMoreMenuOpen.collectAsState()

    // Sync back to ViewModel to keep its internal state in sync
    LaunchedEffect(selectedTab) {
        if (viewModel.selectedTab.value != selectedTab) {
            viewModel.setSelectedTab(selectedTab)
        }
    }

    LaunchedEffect(currentRoute) {
        val topicId = com.example.ui.navigation.HashRouter.extractTopicId(currentRoute)
        if (topicId != null) {
            viewModel.selectTopic(topicId)
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val authState by viewModel.authState.collectAsState()
    var showAuthDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    var showLevelDialog by remember { mutableStateOf(false) }

    val currentUser = when (val state = authState) {
        is AuthState.LoggedIn -> state.user
        is AuthState.Guest -> state.user
        is AuthState.LoggedOut -> null
    }

    if (showAuthDialog) {
        AuthDialog(viewModel = viewModel, onDismiss = { showAuthDialog = false })
    }

    if (showProfileDialog) {
        if (currentUser != null) {
            ProfileDialog(
                viewModel = viewModel,
                user = currentUser,
                onDismiss = { showProfileDialog = false },
                onOpenAuth = { showAuthDialog = true }
            )
        }
    }

    val tabTitles = mapOf(
        0 to "Lessons",
        1 to "Sentence Analyzer",
        2 to "AI Tutor",
        3 to "Idioms & Expressions",
        4 to "My Library",
        5 to "Lisan Daily",
        6 to "Quizzes",
        8 to "Lisan Journey",
        9 to "Progress",
        10 to "Settings",
        11 to "Help & Feedback",
        13 to "Pronunciation Practice",
        14 to "Learn with Pictures",
        15 to "Implement Your API Key",
        16 to "Chat Theme",
        17 to "Create Image",
        18 to "Islamic Calculator"
    )

    if (showLevelDialog) {
        LevelSelectDialog(
            currentLevel = proficiencyLevel,
            onSelect = { viewModel.setProficiencyLevel(it) },
            onDismiss = { showLevelDialog = false }
        )
    }

    val defaultTab = remember(userSettings.defaultLearningTab) {
        HashRouter.routeToTab(HashRouter.tabNameToRoute(userSettings.defaultLearningTab))
    }

    BackHandler(enabled = selectedTab != defaultTab || drawerState.isOpen || showAuthDialog || showProfileDialog || showLevelDialog) {
        if (showAuthDialog) {
            showAuthDialog = false
        } else if (showProfileDialog) {
            showProfileDialog = false
        } else if (showLevelDialog) {
            showLevelDialog = false
        } else if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else if (selectedTab != defaultTab) {
            if (!HashRouter.goBack()) {
                HashRouter.navigate(HashRouter.tabNameToRoute(userSettings.defaultLearningTab))
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                val colors = EditorialTheme.colors
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    ModalDrawerSheet(
                        modifier = Modifier.width(330.dp),
                        drawerContainerColor = colors.background,
                        drawerTonalElevation = 6.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .statusBarsPadding()
                                .navigationBarsPadding()
                                .padding(horizontal = 18.dp, vertical = 14.dp)
                        ) {
                            // Header Card
                            Surface(
                                color = colors.surface,
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, colors.borderSoft),
                                shadowElevation = 2.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .background(
                                                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                                            listOf(Color(0xFF4F46E5), Color(0xFF9333EA))
                                                        ),
                                                        shape = RoundedCornerShape(12.dp)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                            Column {
                                                Text(
                                                    text = "Lisan AI",
                                                    fontSize = 17.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = FontFamily.Serif,
                                                    color = colors.primary
                                                )
                                                Text(
                                                    text = "لسان گرامر مینو",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = colors.accentPurple
                                                )
                                            }
                                        }
                                        IconButton(
                                            onClick = { scope.launch { drawerState.close() } },
                                            modifier = Modifier
                                                .size(34.dp)
                                                .background(colors.container, CircleShape)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Close",
                                                tint = colors.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider(color = colors.borderSoft)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.School,
                                                contentDescription = null,
                                                tint = colors.accentPurple,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Text(
                                                text = "Level: ${proficiencyLevel.labelEn}",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = colors.textSecondary
                                            )
                                        }
                                        Surface(
                                            color = colors.accentPurple.copy(alpha = 0.12f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = proficiencyLevel.labelUr,
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = colors.accentPurple,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Section 1: Special Features
                            Text(
                                text = "SPECIAL TOOLS • اہم خصوصیات",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.accentPurple,
                                modifier = Modifier.padding(start = 6.dp, bottom = 6.dp)
                            )

                            val specialItems = listOf(
                                LisanDrawerItem(
                                    title = "Islamic Calculator",
                                    subtitleUr = "شرعی کیلکولیٹر و مہر",
                                    icon = Icons.Default.Calculate,
                                    index = 18,
                                    iconBgColor = Color(0xFFE8F5E9),
                                    iconBorderColor = Color(0xFFA5D6A7),
                                    iconTintColor = Color(0xFF1B5E20),
                                    badge = "شرعی",
                                    badgeBgColor = Color(0xFF16A34A)
                                ),
                                LisanDrawerItem(
                                    title = "Implement Your API Key",
                                    subtitleUr = "مفت Gemini Key شامل کریں",
                                    icon = Icons.Default.Key,
                                    index = 15,
                                    iconBgColor = Color(0xFFFFF1F2),
                                    iconBorderColor = Color(0xFFFECDD3),
                                    iconTintColor = Color(0xFFE11D48),
                                    badge = "مفت Key",
                                    badgeBgColor = Color(0xFFDC2626)
                                ),
                                LisanDrawerItem(
                                    title = "Learn with Pictures",
                                    subtitleUr = "تصاویر کے ساتھ بصری الفاظ",
                                    icon = Icons.Default.PhotoLibrary,
                                    index = 14,
                                    iconBgColor = Color(0xFFEEF2FF),
                                    iconBorderColor = Color(0xFFC7D2FE),
                                    iconTintColor = Color(0xFF4F46E5),
                                    badge = "جدید",
                                    badgeBgColor = Color(0xFF4F46E5)
                                )
                            )

                            specialItems.forEach { item ->
                                LisanDrawerItemRow(
                                    item = item,
                                    isSelected = selectedTab == item.index,
                                    onClick = {
                                        HashRouter.navigate(HashRouter.tabToRoute(item.index))
                                        scope.launch { drawerState.close() }
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = colors.borderSoft)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Section 2: Learning & Practice
                            Text(
                                text = "LEARNING & PRACTICE • تعلیم و مشق",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textSecondary,
                                modifier = Modifier.padding(start = 6.dp, bottom = 6.dp)
                            )

                            val learningItems = listOf(
                                LisanDrawerItem(
                                    title = "Lisan Daily",
                                    subtitleUr = "روزانہ کا سنہری سبق و محاورہ",
                                    icon = Icons.Default.CalendarToday,
                                    index = 5,
                                    iconBgColor = Color(0xFFEFF6FF),
                                    iconBorderColor = Color(0xFFBFDBFE),
                                    iconTintColor = Color(0xFF2563EB)
                                ),
                                LisanDrawerItem(
                                    title = "Pronunciation Practice",
                                    subtitleUr = "صحیح انگریزی تلفظ کی مشق",
                                    icon = Icons.Default.RecordVoiceOver,
                                    index = 13,
                                    iconBgColor = Color(0xFFFDF4FF),
                                    iconBorderColor = Color(0xFFF5D0FE),
                                    iconTintColor = Color(0xFFC026D3)
                                ),
                                LisanDrawerItem(
                                    title = "Quizzes",
                                    subtitleUr = "گرامر اور الفاظ کے ٹیسٹ",
                                    icon = Icons.Default.TaskAlt,
                                    index = 6,
                                    iconBgColor = Color(0xFFF5F3FF),
                                    iconBorderColor = Color(0xFFDDD6FE),
                                    iconTintColor = Color(0xFF7C3AED)
                                ),
                                LisanDrawerItem(
                                    title = "Lisan Journey",
                                    subtitleUr = "تعلیمی سفر اور انعامات",
                                    icon = Icons.Default.EmojiEvents,
                                    index = 8,
                                    iconBgColor = Color(0xFFFFFBEB),
                                    iconBorderColor = Color(0xFFFDE68A),
                                    iconTintColor = Color(0xFFD97706)
                                ),
                                LisanDrawerItem(
                                    title = "Progress Dashboard",
                                    subtitleUr = "کارکردگی کا جامع جائزہ",
                                    icon = Icons.Default.BarChart,
                                    index = 9,
                                    iconBgColor = Color(0xFFECFEFF),
                                    iconBorderColor = Color(0xFFA5F3FC),
                                    iconTintColor = Color(0xFF0891B2)
                                )
                            )

                            learningItems.forEach { item ->
                                LisanDrawerItemRow(
                                    item = item,
                                    isSelected = selectedTab == item.index,
                                    onClick = {
                                        HashRouter.navigate(HashRouter.tabToRoute(item.index))
                                        scope.launch { drawerState.close() }
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = colors.borderSoft)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Section 3: App Settings & Help
                            Text(
                                text = "SYSTEM • سیٹنگز و مدد",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textSecondary,
                                modifier = Modifier.padding(start = 6.dp, bottom = 6.dp)
                            )

                            val systemItems = listOf(
                                LisanDrawerItem(
                                    title = "Settings",
                                    subtitleUr = "ایپ کی ترتیبات و آواز",
                                    icon = Icons.Default.Settings,
                                    index = 10,
                                    iconBgColor = Color(0xFFF1F5F9),
                                    iconBorderColor = Color(0xFFCBD5E1),
                                    iconTintColor = Color(0xFF475569)
                                ),
                                LisanDrawerItem(
                                    title = "Help & Feedback",
                                    subtitleUr = "رہنمائی اور فیڈ بیک",
                                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                                    index = 11,
                                    iconBgColor = Color(0xFFF0FDF4),
                                    iconBorderColor = Color(0xFFBBF7D0),
                                    iconTintColor = Color(0xFF16A34A)
                                )
                            )

                            systemItems.forEach { item ->
                                LisanDrawerItemRow(
                                    item = item,
                                    isSelected = selectedTab == item.index,
                                    onClick = {
                                        HashRouter.navigate(HashRouter.tabToRoute(item.index))
                                        scope.launch { drawerState.close() }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
                val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
                LaunchedEffect(drawerState.isOpen) {
                    if (drawerState.isOpen) {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
                    topBar = {
                        if (selectedTab != 2 || !isTutorChatActive) {
                            EditorialHeader(
                                subtitle = "",
                                title = tabTitles[selectedTab] ?: "Lisan Grammar",
                                levelBadge = proficiencyLevel.labelEn,
                                onLevelClick = { showLevelDialog = true },
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { viewModel.toggleTheme() },
                                onProfileClick = {
                                    when (authState) {
                                        is AuthState.LoggedIn -> showProfileDialog = true
                                        is AuthState.Guest -> showProfileDialog = true
                                        is AuthState.LoggedOut -> showAuthDialog = true
                                    }
                                },
                                onMoreClick = {
                                    keyboardController?.hide()
                                    scope.launch { drawerState.open() }
                                },
                                userPhotoPath = currentUser?.profilePhotoPath,
                                userDisplayName = currentUser?.displayName
                            )
                        }
                    },
                    bottomBar = {
                        if (!WindowInsets.isImeVisible) {
                            EditorialBottomNav(
                                selectedTabIndex = selectedTab,
                                onTabSelected = { HashRouter.navigate(HashRouter.tabToRoute(it)) }
                            )
                        }
                    }
                ) { innerPadding ->
                    val isImeVisible = WindowInsets.isImeVisible
                    val bottomPadding = if (isImeVisible) {
                        WindowInsets.ime.asPaddingValues().calculateBottomPadding()
                    } else {
                        innerPadding.calculateBottomPadding()
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                                top = innerPadding.calculateTopPadding(),
                                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                                bottom = bottomPadding
                            )
                    ) {
                        // HashRouterBar removed
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            when (selectedTab) {
                                0 -> LessonScreen(viewModel = viewModel)
                                1 -> AnalyzerScreen(viewModel = viewModel)
                                2 -> TutorChatScreen(viewModel = viewModel)
                                3 -> IdiomScreen(viewModel = viewModel)
                                4 -> UnifiedLibraryScreen(viewModel = viewModel)
                                5 -> LisanDailyScreen(viewModel = viewModel)
                                6 -> QuizScreen(viewModel = viewModel)
                                8 -> LisanJourneyScreen(viewModel = lisanJourneyViewModel)
                                9 -> ProgressDashboardScreen(viewModel = viewModel)
                                10 -> SettingsScreen(viewModel = viewModel)
                                11 -> HelpAndFeedbackScreen(viewModel = viewModel)
                                13 -> PronunciationPracticeScreen(viewModel = viewModel)
                                14 -> PictureVocabularyRootScreen(viewModel = pictureVocabularyViewModel)
                                15 -> ImplementApiKeyScreen(viewModel = viewModel)
                                16 -> ChatThemeSelectionScreen(viewModel = viewModel)
                                18 -> HaqMehrCalculatorScreen(viewModel = viewModel)
                                else -> LessonScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
