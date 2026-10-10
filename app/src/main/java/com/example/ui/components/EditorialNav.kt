package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class EditorialTabItem(
    val title: String,
    val subtitleUr: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun EditorialBottomNav(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = EditorialTheme.colors
    val tabs = listOf(
        EditorialTabItem("Learn", "سبق", Icons.AutoMirrored.Filled.MenuBook, Icons.AutoMirrored.Filled.MenuBook),
        EditorialTabItem("Analyze", "تجزیہ", Icons.Default.AutoAwesome, Icons.Default.AutoAwesome),
        EditorialTabItem("AI Tutor", "استاد", Icons.Default.SmartToy, Icons.Default.ChatBubble),
        EditorialTabItem("Idioms", "محاورات", Icons.Default.Bookmark, Icons.Outlined.BookmarkBorder),
        EditorialTabItem("Library", "کتب خانہ", Icons.Default.LibraryBooks, Icons.Outlined.LocalLibrary)
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.container,
        tonalElevation = 6.dp,
        border = BorderStroke(1.dp, colors.borderSoft)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(72.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tabItem ->
                val isSelected = selectedTabIndex == index
                val isCenter = index == 2 // Center item: AI Tutor

                if (isCenter) {
                    // Elevated Center AI Tutor Floating Action Item
                    Column(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = false, radius = 28.dp)
                            ) { onTabSelected(index) }
                            .padding(horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(50.dp)
                                .shadow(6.dp, CircleShape),
                            shape = CircleShape,
                            color = Color.Transparent,
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.5.dp,
                                brush = if (isSelected) {
                                    Brush.linearGradient(listOf(Color(0xFF818CF8), Color(0xFFC084FC)))
                                } else {
                                    Brush.linearGradient(listOf(colors.accentPurple.copy(alpha = 0.5f), colors.accentPurple))
                                }
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.linearGradient(
                                            if (isSelected) {
                                                listOf(Color(0xFF4F46E5), Color(0xFF9333EA))
                                            } else {
                                                listOf(Color(0xFF6366F1), Color(0xFF7C3AED))
                                            }
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.SmartToy else Icons.Default.ChatBubble,
                                    contentDescription = tabItem.title,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Text(
                            text = tabItem.title,
                            fontSize = 10.5.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                            color = if (isSelected) colors.accentPurple else colors.primary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                } else {
                    // Standard Tab Item with Active Pill Container
                    val iconTint by animateColorAsState(
                        targetValue = if (isSelected) colors.accentPurple else colors.textSecondary,
                        label = "tabIconTint"
                    )

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true)
                            ) { onTabSelected(index) }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) colors.accentPurple.copy(alpha = 0.12f) else Color.Transparent,
                            border = if (isSelected) BorderStroke(1.dp, colors.accentPurple.copy(alpha = 0.25f)) else null,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSelected) tabItem.selectedIcon else tabItem.unselectedIcon,
                                    contentDescription = tabItem.title,
                                    tint = iconTint,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Text(
                            text = tabItem.title,
                            fontSize = 10.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) colors.accentPurple else colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}
