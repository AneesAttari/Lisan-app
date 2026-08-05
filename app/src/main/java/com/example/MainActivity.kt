package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.example.ui.theme.EditorialBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LisanViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.ui.theme.*
import com.example.ui.screens.DailyChallengeScreen
import com.example.ui.screens.RevisionPlannerScreen
import com.example.ui.screens.AchievementsScreen
import kotlinx.coroutines.launch

import com.example.ui.screens.UnifiedLibraryScreen

class MainActivity : ComponentActivity() {

    private val viewModel: LisanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            MyApplicationTheme(darkTheme = isDarkTheme, dynamicColor = false) {
                LisanMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun LisanMainApp(viewModel: LisanViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val proficiencyLevel by viewModel.proficiencyLevel.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val isMoreMenuOpen by viewModel.isMoreMenuOpen.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showLevelDialog by remember { mutableStateOf(false) }

    val tabTitles = mapOf(
        0 to "Lessons",
        1 to "Sentence Analyzer",
        2 to "AI Tutor",
        3 to "Idioms & Expressions",
        4 to "My Library",
        5 to "Daily Challenge",
        6 to "Quizzes",
        7 to "Revision Planner",
        8 to "Achievements",
        9 to "Progress",
        10 to "Settings",
        11 to "Help & Feedback"
    )

    if (showLevelDialog) {
        LevelSelectDialog(
            currentLevel = proficiencyLevel,
            onSelect = { viewModel.setProficiencyLevel(it) },
            onDismiss = { showLevelDialog = false }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
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
                        Triple("Daily Challenge", Icons.Default.Whatshot, 5),
                        Triple("Quizzes", Icons.Default.CheckCircle, 6),
                        Triple("Revision Planner", Icons.Default.EventNote, 7),
                        Triple("Achievements", Icons.Default.EmojiEvents, 8),
                        Triple("Progress", Icons.Default.BarChart, 9),
                        Triple("Settings", Icons.Default.Settings, 10),
                        Triple("Help & Feedback", Icons.Default.HelpOutline, 11)
                    )

                    menuItems.forEach { (title, icon, index) ->
                        NavigationDrawerItem(
                            label = { Text(title, fontWeight = FontWeight.SemiBold) },
                            selected = selectedTab == index,
                            onClick = {
                                viewModel.setSelectedTab(index)
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
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
            topBar = {
                EditorialHeader(
                    subtitle = "انگریزی اور عربی گرامر گائیڈ (English & Arabic Grammar Guide)",
                    title = tabTitles[selectedTab] ?: "Lisan Grammar",
                    levelBadge = proficiencyLevel.labelEn,
                    onLevelClick = { showLevelDialog = true },
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { viewModel.toggleTheme() },
                    onMoreClick = { scope.launch { drawerState.open() } }
                )
            },
            bottomBar = {
                if (selectedTab in 0..4) {
                    EditorialBottomNav(
                        selectedTabIndex = selectedTab,
                        onTabSelected = { viewModel.setSelectedTab(it) }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    0 -> LessonScreen(viewModel = viewModel)
                    1 -> AnalyzerScreen(viewModel = viewModel)
                    2 -> TutorChatScreen(viewModel = viewModel)
                    3 -> IdiomScreen(viewModel = viewModel)
                    4 -> UnifiedLibraryScreen(viewModel = viewModel)
                    5 -> DailyChallengeScreen(viewModel = viewModel)
                    6 -> QuizScreen(viewModel = viewModel)
                    7 -> RevisionPlannerScreen(viewModel = viewModel)
                    8 -> AchievementsScreen(viewModel = viewModel)
                    9 -> ProgressDashboardScreen(viewModel = viewModel)
                    10 -> SettingsScreen(viewModel = viewModel)
                    11 -> SettingsScreen(viewModel = viewModel)
                    else -> LessonScreen(viewModel = viewModel)
                }
            }
        }
    }
}
