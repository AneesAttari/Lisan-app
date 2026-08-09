package com.example.ui.screens

import androidx.compose.runtime.Composable
import com.example.ui.components.VoiceTutorDialog
import com.example.ui.viewmodel.LisanViewModel

@Composable
fun VoiceTutorScreen(
    viewModel: LisanViewModel,
    onBack: () -> Unit
) {
    VoiceTutorDialog(
        viewModel = viewModel,
        onDismissRequest = onBack
    )
}

