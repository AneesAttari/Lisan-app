package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EditorialTheme

@Composable
fun VisualStepCard(
    stepBadge: String,
    titleEn: String,
    titleUrdu: String,
    mockContent: @Composable () -> Unit
) {
    val colors = EditorialTheme.colors
    Card(
        colors = CardDefaults.cardColors(containerColor = colors.container),
        border = BorderStroke(1.dp, colors.borderSoft),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(
                color = colors.accentPurple.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stepBadge,
                    color = colors.accentPurple,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            
            Text(
                text = titleEn,
                fontSize = 13.sp,
                color = colors.textPrimary,
                lineHeight = 18.sp
            )
            
            Text(
                text = titleUrdu,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = colors.primary,
                lineHeight = 22.sp
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.background, RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                mockContent()
            }
        }
    }
}

@Composable
fun ApiKeyGuideCardInline(
    onOpenGuide: () -> Unit,
    onSaveKey: (String) -> Unit
) {
    val colors = EditorialTheme.colors
    Card(
        colors = CardDefaults.cardColors(containerColor = colors.accentPurple.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, colors.accentPurple.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "⚠️ API Limit Exceeded / Quota Reached",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = colors.primary
            )
            Text(
                text = "To keep talking with your tutor without any limit, please add your own free Gemini API key.",
                fontSize = 12.sp,
                color = colors.textPrimary,
                lineHeight = 16.sp
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onOpenGuide,
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accentPurple),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Get Free Key Guide", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color.White)
                }
            }
        }
    }
}
