package com.example.ui.screens

import androidx.compose.runtime.Composable
import com.example.ui.viewmodel.LisanViewModel

/**
 * HaqMehrCalculatorScreen is now unified into [IslamicCalculatorScreen],
 * which provides Haq Mehr, Zakat, Fitrana, and Ushr calculators in a single page.
 */
@Composable
fun HaqMehrCalculatorScreen(viewModel: LisanViewModel) {
    IslamicCalculatorScreen(viewModel = viewModel)
}
