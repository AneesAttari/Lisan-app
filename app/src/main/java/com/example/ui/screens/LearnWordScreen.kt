package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PictureVocabularyItemEntity
import com.example.ui.components.PictureCardThumbnail
import com.example.ui.navigation.HashRouter
import com.example.ui.theme.EditorialPrimary
import com.example.ui.theme.EditorialTheme
import com.example.ui.viewmodel.PictureVocabularyViewModel

@Composable
fun LearnWordScreen(categoryId: String, itemId: String, viewModel: PictureVocabularyViewModel) {
    val colors = EditorialTheme.colors
    var item by remember { mutableStateOf<PictureVocabularyItemEntity?>(null) }

    LaunchedEffect(itemId) {
        item = viewModel.getItem(itemId)
        item?.let {
            viewModel.recordWordLearned(it.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { HashRouter.navigate("#/picture-vocabulary/category/$categoryId") }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Learn Word",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        item?.let { vocab ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colors.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Illustration Thumbnail
                    PictureCardThumbnail(
                        assetKey = vocab.imageAssetKey,
                        imageUrl = vocab.getEffectiveImageUrl(),
                        word = vocab.wordEnglish,
                        alt = vocab.getEffectiveAlt(),
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(20.dp))
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = vocab.wordEnglish,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        IconButton(
                            onClick = { viewModel.speakWord(vocab.wordEnglish) },
                            modifier = Modifier
                                .size(48.dp)
                                .background(EditorialPrimary, CircleShape)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Listen", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = vocab.wordArabic,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.accentPurple
                    )

                    vocab.ipa?.let { ipa ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = ipa, fontSize = 14.sp, color = colors.textSecondary)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = colors.border)
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Example Sentence:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = vocab.exampleSentenceEnglish,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.textPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                item?.let {
                    HashRouter.navigate("#/picture-vocabulary/category/$categoryId")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary)
        ) {
            Text(text = "Back to Category Words", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
