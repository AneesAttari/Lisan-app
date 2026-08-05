package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EditorialCard
import com.example.ui.components.EditorialDivider
import com.example.ui.components.EditorialSectionTitle
import com.example.ui.components.EditorialMixedText
import com.example.ui.theme.EditorialAccentPurple
import com.example.ui.theme.EditorialBorderAccent
import com.example.ui.theme.EditorialContainer
import com.example.ui.theme.EditorialPrimary
import com.example.ui.theme.EditorialTextMuted
import com.example.ui.theme.EditorialTextPrimary
import com.example.ui.theme.EditorialTextSecondary
import com.example.ui.viewmodel.LisanViewModel

@Composable
fun SavedNotesScreen(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    val notes by viewModel.savedNotes.collectAsState()
    val savedIdiomIds by viewModel.savedIdiomIds.collectAsState()
    val allIdioms = com.example.data.repository.IdiomRepository.idioms
    val savedIdioms = allIdioms.filter { savedIdiomIds.contains(it.id) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Grammar Notes Section
            item {
                EditorialSectionTitle("Saved Grammar Notes")
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (notes.isEmpty()) {
                item {
                    EditorialCard {
                        Text(
                            text = "No grammar notes saved yet.",
                            modifier = Modifier.padding(16.dp),
                            color = EditorialTextMuted
                        )
                    }
                }
            } else {
                items(notes, key = { it.id }) { note ->
                    EditorialCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = note.category.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EditorialTextMuted,
                                    letterSpacing = 1.2.sp
                                )
                                Text(
                                    text = note.titleEn,
                                    fontSize = 18.sp,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Italic,
                                    color = EditorialPrimary
                                )
                                Text(
                                    text = note.titleAr,
                                    fontSize = 13.sp,
                                    color = EditorialAccentPurple
                                )
                            }

                            Surface(
                                color = EditorialContainer,
                                shape = CircleShape,
                                border = BorderStroke(1.dp, EditorialBorderAccent),
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable { viewModel.deleteSavedNote(note.id) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete",
                                    tint = EditorialAccentPurple,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .size(18.dp)
                                )
                            }
                        }

                        EditorialDivider()

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            EditorialMixedText(
                                text = note.englishText,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                color = EditorialTextPrimary
                            )
                            EditorialMixedText(
                                text = note.arabicComparisonText,
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                color = EditorialPrimary,
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
            }

            // Saved Idioms Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                EditorialSectionTitle("Saved Idioms")
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (savedIdioms.isEmpty()) {
                item {
                    EditorialCard {
                        Text(
                            text = "No idioms saved yet.",
                            modifier = Modifier.padding(16.dp),
                            color = EditorialTextMuted
                        )
                    }
                }
            } else {
                items(savedIdioms, key = { it.id }) { idiom ->
                    IdiomCard(idiom, viewModel)
                }
            }
        }
    }
}
