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
import com.example.data.model.AuthState

class MainActivity : ComponentActivity() {

    private val viewModel: LisanViewModel by viewModels()
    private val pictureVocabularyViewModel: com.example.ui.viewmodel.PictureVocabularyViewModel by viewModels()
    private val lisanJourneyViewModel: LisanJourneyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
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
                LisanMainApp(
                    viewModel = viewModel,
                    pictureVocabularyViewModel = pictureVocabularyViewModel,
                    lisanJourneyViewModel = lisanJourneyViewModel
                )
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
    var showSplashScreen by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1600)
        showSplashScreen = false
    }

    val currentRoute by HashRouter.currentRoute.collectAsState()
    val selectedTab = remember(currentRoute) { HashRouter.routeToTab(currentRoute) }
    val proficiencyLevel by viewModel.proficiencyLevel.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
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
        12 to "Past Conversations",
        13 to "Pronunciation Practice",
        14 to "Learn with Pictures"
    )

    if (showLevelDialog) {
        LevelSelectDialog(
            currentLevel = proficiencyLevel,
            onSelect = { viewModel.setProficiencyLevel(it) },
            onDismiss = { showLevelDialog = false }
        )
    }

    BackHandler(enabled = selectedTab != 0 || drawerState.isOpen || showAuthDialog || showProfileDialog || showLevelDialog) {
        if (showAuthDialog) {
            showAuthDialog = false
        } else if (showProfileDialog) {
            showProfileDialog = false
        } else if (showLevelDialog) {
            showLevelDialog = false
        } else if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else if (selectedTab != 0) {
            if (!HashRouter.goBack()) {
                HashRouter.navigate("#/lessons")
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    ModalDrawerSheet(
                        modifier = Modifier.width(300.dp),
                        drawerContainerColor = EditorialBackground,
                        drawerTonalElevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "More Options",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EditorialPrimary
                                )
                                IconButton(onClick = { scope.launch { drawerState.close() } }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = EditorialPrimary)
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            val menuItems = listOf(
                                Triple("Learn with Pictures", Icons.Default.Image, 14),
                                Triple("Lisan Daily", Icons.Default.CalendarToday, 5),
                                Triple("Pronunciation Practice", Icons.Default.Hearing, 13),
                                Triple("Quizzes", Icons.Default.CheckCircle, 6),
                                Triple("Revision Planner", Icons.AutoMirrored.Filled.EventNote, 7),
                                Triple("Lisan Journey", Icons.Default.EmojiEvents, 8),
                                Triple("Progress", Icons.Default.BarChart, 9),
                                Triple("Past Conversations", Icons.Default.History, 12),
                                Triple("Settings", Icons.Default.Settings, 10),
                                Triple("Help & Feedback", Icons.AutoMirrored.Filled.HelpOutline, 11)
                            )

                            menuItems.forEach { (title, icon, index) ->
                                NavigationDrawerItem(
                                    label = { Text(title, fontWeight = FontWeight.SemiBold) },
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
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
                    topBar = {
                        EditorialHeader(
                            subtitle = "(English & Arabic Grammar Guide)",
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
                            onMoreClick = { scope.launch { drawerState.open() } },
                            userPhotoPath = currentUser?.profilePhotoPath,
                            userDisplayName = currentUser?.displayName
                        )
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
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
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
                                12 -> PastConversationsScreen(viewModel = viewModel)
                                13 -> PronunciationPracticeScreen(viewModel = viewModel)
                                14 -> PictureVocabularyRootScreen(viewModel = pictureVocabularyViewModel)
                                else -> LessonScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSplashScreen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E0040)),
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
}
