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
import com.example.ui.screens.RevisionPlannerScreen
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

data class DrawerItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val index: Int,
    val badge: String? = null
)

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
        7 to "Revision Planner",
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
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    ModalDrawerSheet(
                        modifier = Modifier.width(320.dp),
                        drawerContainerColor = EditorialBackground,
                        drawerTonalElevation = 6.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(20.dp)
                        ) {
                            // Header Card
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, EditorialBorder),
                                shadowElevation = 2.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(Color(0xFF2563EB), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MenuBook,
                                                contentDescription = null,
                                                tint = Color.White
                                            )
                                        }
                                        Column {
                                            Text(
                                                text = "Lisan Navigation",
                                                fontSize = 17.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EditorialPrimary
                                            )
                                            Text(
                                                text = "لسان گرامر مینو",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF2563EB)
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = { scope.launch { drawerState.close() } },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Close",
                                            tint = EditorialPrimary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Section 1: Special Features
                            Text(
                                text = "SPECIAL TOOLS • اہم خصوصیات",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB),
                                modifier = Modifier.padding(start = 8.dp, bottom = 6.dp)
                            )

                            val specialItems = listOf(
                                DrawerItem("Islamic Calculator", Icons.Default.Calculate, 18, "شرعی کیلکولیٹر"),
                                DrawerItem("Implement Your API Key", Icons.Default.Key, 15, "مفت Key"),
                                DrawerItem("Learn with Pictures", Icons.Default.Image, 14, "جدید")
                            )

                            specialItems.forEach { item ->
                                val (title, icon, index, badge) = item
                                NavigationDrawerItem(
                                    label = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Surface(
                                                color = if (index == 15) Color(0xFFDC2626) else Color(0xFF2563EB),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = badge ?: "",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    },
                                    selected = selectedTab == index,
                                    onClick = {
                                        HashRouter.navigate(HashRouter.tabToRoute(index))
                                        scope.launch { drawerState.close() }
                                    },
                                    icon = { Icon(icon, contentDescription = null, tint = if (selectedTab == index) Color(0xFF2563EB) else EditorialTextSecondary) },
                                    colors = NavigationDrawerItemDefaults.colors(
                                        selectedContainerColor = Color(0xFFEFF6FF),
                                        selectedIconColor = Color(0xFF2563EB),
                                        selectedTextColor = Color(0xFF1E3A8A),
                                        unselectedIconColor = EditorialTextSecondary,
                                        unselectedTextColor = EditorialTextPrimary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.padding(vertical = 3.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = EditorialBorder)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Section 2: Learning & Practice
                            Text(
                                text = "LEARNING & PRACTICE • تعلیم و مشق",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialTextSecondary,
                                modifier = Modifier.padding(start = 8.dp, bottom = 6.dp)
                            )

                            val learningItems = listOf(
                                Triple("Lisan Daily", Icons.Default.CalendarToday, 5),
                                Triple("Pronunciation Practice", Icons.Default.Hearing, 13),
                                Triple("Quizzes", Icons.Default.CheckCircle, 6),
                                Triple("Revision Planner", Icons.AutoMirrored.Filled.EventNote, 7),
                                Triple("Lisan Journey", Icons.Default.EmojiEvents, 8),
                                Triple("Progress Dashboard", Icons.Default.BarChart, 9)
                            )

                            learningItems.forEach { (title, icon, index) ->
                                NavigationDrawerItem(
                                    label = { Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                                    selected = selectedTab == index,
                                    onClick = {
                                        HashRouter.navigate(HashRouter.tabToRoute(index))
                                        scope.launch { drawerState.close() }
                                    },
                                    icon = { Icon(icon, contentDescription = null) },
                                    colors = NavigationDrawerItemDefaults.colors(
                                        selectedContainerColor = EditorialPillBg,
                                        selectedIconColor = EditorialPrimary,
                                        selectedTextColor = EditorialPrimary,
                                        unselectedIconColor = EditorialTextSecondary,
                                        unselectedTextColor = EditorialTextPrimary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = EditorialBorder)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Section 3: App Settings & Help
                            Text(
                                text = "SYSTEM • سیٹنگز و مدد",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialTextSecondary,
                                modifier = Modifier.padding(start = 8.dp, bottom = 6.dp)
                            )

                            val systemItems = listOf(
                                Triple("Settings", Icons.Default.Settings, 10),
                                Triple("Help & Feedback", Icons.AutoMirrored.Filled.HelpOutline, 11)
                            )

                            systemItems.forEach { (title, icon, index) ->
                                NavigationDrawerItem(
                                    label = { Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                                    selected = selectedTab == index,
                                    onClick = {
                                        HashRouter.navigate(HashRouter.tabToRoute(index))
                                        scope.launch { drawerState.close() }
                                    },
                                    icon = { Icon(icon, contentDescription = null) },
                                    colors = NavigationDrawerItemDefaults.colors(
                                        selectedContainerColor = EditorialPillBg,
                                        selectedIconColor = EditorialPrimary,
                                        selectedTextColor = EditorialPrimary,
                                        unselectedIconColor = EditorialTextSecondary,
                                        unselectedTextColor = EditorialTextPrimary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.padding(vertical = 2.dp)
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
                                7 -> RevisionPlannerScreen(viewModel = viewModel)
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
