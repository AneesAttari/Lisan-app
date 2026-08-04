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
import com.example.ui.screens.SavedNotesScreen
import com.example.ui.screens.TutorChatScreen
import com.example.ui.theme.EditorialBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LisanViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LisanViewModel by viewModels {
        androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(dynamicColor = false) {
                LisanMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun LisanMainApp(viewModel: LisanViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val proficiencyLevel by viewModel.proficiencyLevel.collectAsState()

    var showLevelDialog by remember { mutableStateOf(false) }

    val tabTitles = listOf(
        "تقابلی اسباق (Lessons)",
        "اے آئی ٹیوٹر (AI Tutor)",
        "جملہ تجزیہ نگار (Analyzer)",
        "محاورے اور استعمال (Idioms)",
        "محفوظ گرامر نوٹس (Saved)"
    )

    if (showLevelDialog) {
        LevelSelectDialog(
            currentLevel = proficiencyLevel,
            onSelect = { viewModel.setProficiencyLevel(it) },
            onDismiss = { showLevelDialog = false }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(EditorialBackground),
        containerColor = EditorialBackground,
        topBar = {
            EditorialHeader(
                subtitle = "انگریزی اور عربی گرامر گائیڈ",
                title = tabTitles.getOrElse(selectedTab) { "Lisan Grammar" },
                levelBadge = proficiencyLevel.labelEn,
                onLevelClick = { showLevelDialog = true }
            )
        },
        bottomBar = {
            EditorialBottomNav(
                selectedTabIndex = selectedTab,
                onTabSelected = { viewModel.setSelectedTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> LessonScreen(viewModel = viewModel)
                1 -> TutorChatScreen(viewModel = viewModel)
                2 -> AnalyzerScreen(viewModel = viewModel)
                3 -> IdiomScreen(viewModel = viewModel)
                4 -> SavedNotesScreen(viewModel = viewModel)
                else -> LessonScreen(viewModel = viewModel)
            }
        }
    }
}
