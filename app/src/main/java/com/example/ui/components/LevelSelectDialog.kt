package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ProficiencyLevel
import com.example.ui.theme.EditorialAccentPurple
import com.example.ui.theme.EditorialBorder
import com.example.ui.theme.EditorialContainer
import com.example.ui.theme.EditorialPrimary
import com.example.ui.theme.EditorialSurface
import com.example.ui.theme.EditorialTextMuted
import com.example.ui.theme.EditorialTextPrimary

@Composable
fun LevelSelectDialog(
    currentLevel: ProficiencyLevel,
    onSelect: (ProficiencyLevel) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = EditorialSurface,
            border = BorderStroke(1.dp, EditorialBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        text = "STUDENT LEVEL • اپنا تعلیمی لیول منتخب کریں",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialTextMuted,
                        letterSpacing = 1.4.sp
                    )
                    Text(
                        text = "Proficiency & Depth",
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                }

                EditorialDivider()

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ProficiencyLevel.entries.forEach { level ->
                        val isSelected = level == currentLevel
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    onSelect(level)
                                    onDismiss()
                                },
                            color = if (isSelected) EditorialContainer else EditorialSurface,
                            border = BorderStroke(1.dp, if (isSelected) EditorialAccentPurple else EditorialBorder),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = level.labelEn,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EditorialPrimary
                                    )
                                    Text(
                                        text = level.labelAr,
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Serif,
                                        fontStyle = FontStyle.Italic,
                                        color = EditorialAccentPurple
                                    )
                                }

                                if (isSelected) {
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = EditorialPrimary
                                    ) {
                                        Text(
                                            text = "ACTIVE",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EditorialSurface,
                                            letterSpacing = 1.0.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
