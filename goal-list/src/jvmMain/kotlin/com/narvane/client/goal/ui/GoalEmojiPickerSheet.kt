package com.narvane.client.goal.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.narvane.client.goal.model.GoalEmojiCategory
import com.narvane.client.goal.model.GoalEmojiCategoryId
import com.narvane.client.goal.model.GoalEmojiItem

@Composable
fun GoalEmojiPickerSheet(
    categories: List<GoalEmojiCategory>,
    selectedCategoryId: GoalEmojiCategoryId,
    emojis: List<GoalEmojiItem>,
    onCategorySelect: (GoalEmojiCategoryId) -> Unit,
    onEmojiSelect: (GoalEmojiItem) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 280.dp, max = 420.dp),
        shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Choose emoji",
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF24314A),
                )
                Card(
                    shape = RoundedCornerShape(999.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF2F6FC)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    onClick = onClose,
                ) {
                    Text(
                        text = "Close",
                        color = Color(0xFF5F6F8A),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                categories.forEach { category ->
                    val selected = selectedCategoryId == category.id
                    Card(
                        shape = RoundedCornerShape(999.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected) Color(0xFFE6F3FF) else Color(0xFFF4F6FB),
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        onClick = { onCategorySelect(category.id) },
                    ) {
                        Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)) {
                            Text(
                                text = category.label,
                                color = if (selected) Color(0xFF215B83) else Color(0xFF64748D),
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                            )
                        }
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 52.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(emojis, key = { it.id }) { emoji ->
                    EmojiPickerCell(
                        emoji = emoji,
                        onClick = { onEmojiSelect(emoji) },
                    )
                }
            }
        }
    }
}

@Composable
private fun EmojiPickerCell(
    emoji: GoalEmojiItem,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.size(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FAFF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = emoji.symbol,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
