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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EditorialBorder
import com.example.ui.theme.EditorialContainer
import com.example.ui.theme.EditorialPillBg
import com.example.ui.theme.EditorialPrimary
import com.example.ui.theme.EditorialTextPrimary

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
    val tabs = listOf(
        EditorialTabItem("Learn", Icons.AutoMirrored.Filled.MenuBook),
        EditorialTabItem("Tutor", Icons.Default.ChatBubbleOutline),
        EditorialTabItem("Analyze", Icons.Default.AutoAwesome),
        EditorialTabItem("Idioms", Icons.Default.Search),
        EditorialTabItem("Saved", Icons.Default.Bookmark)
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        color = EditorialContainer,
        border = BorderStroke(1.dp, EditorialBorder)
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
                Column(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onTabSelected(index) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .alpha(if (isSelected) 1f else 0.6f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (isSelected) {
                        Surface(
                            modifier = Modifier
                                .width(56.dp)
                                .height(32.dp),
                            shape = CircleShape,
                            color = EditorialPillBg
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = tabItem.icon,
                                    contentDescription = tabItem.title,
                                    tint = EditorialPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .width(56.dp)
                                .height(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = tabItem.icon,
                                contentDescription = tabItem.title,
                                tint = EditorialTextPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        text = tabItem.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = EditorialPrimary
                    )
                }
            }
        }
    }
}
