package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import androidx.compose.material.icons.filled.ChatBubbleOutline

data class EditorialTabItem(
    val title: String,
    val icon: ImageVector
)

@Composable
fun EditorialBottomNav(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = EditorialTheme.colors
    val tabs = listOf(
        EditorialTabItem("Learn", Icons.AutoMirrored.Filled.MenuBook),
        EditorialTabItem("Analyze", Icons.Default.AutoAwesome),
        EditorialTabItem("Tutor", Icons.Default.ChatBubbleOutline),
        EditorialTabItem("Idioms", Icons.Default.Bookmark),
        EditorialTabItem("Library", Icons.Default.LibraryBooks)
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(84.dp),
        color = colors.container,
        border = BorderStroke(1.dp, colors.border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tabItem ->
                val isSelected = selectedTabIndex == index
                val isCenter = index == 2 // Middle item "Tutor"

                if (isCenter) {
                    Column(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .clip(CircleShape)
                            .clickable { onTabSelected(index) }
                            .padding(horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(56.dp)
                                .shadow(8.dp, CircleShape),
                            shape = CircleShape,
                            color = if (isSelected) colors.accentPurple else colors.surface,
                            border = BorderStroke(2.dp, colors.accentPurple)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = tabItem.icon,
                                    contentDescription = tabItem.title,
                                    tint = if (isSelected) Color.White else colors.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Text(
                            text = tabItem.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onTabSelected(index) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .alpha(if (isSelected) 1f else 0.7f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = tabItem.icon,
                            contentDescription = tabItem.title,
                            tint = if (isSelected) colors.accentPurple else colors.textSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = tabItem.title,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) colors.accentPurple else colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}
