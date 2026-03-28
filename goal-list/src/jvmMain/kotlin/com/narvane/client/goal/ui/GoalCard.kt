package com.narvane.client.goal.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.Canvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.narvane.client.goal.layout.GoalLayout
import com.narvane.client.goal.model.GOAL_MAX_HOUR_OPTIONS
import com.narvane.client.goal.model.Goal
import com.narvane.client.goal.model.GoalHourPressAction

@Composable
fun GoalCard(
    goal: Goal,
    onEmojiClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onAddHourOption: () -> Unit,
    onHourPress: (index: Int, action: GoalHourPressAction) -> Unit,
    onRemoveClick: () -> Unit,
    onRemoveLongPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val canAddHour = goal.hourOptions.size < GOAL_MAX_HOUR_OPTIONS
    val optionsScroll = rememberScrollState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp),
        shape = GoalLayout.cardShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            val leftColWidth = 40.dp
            val rightColWidth = 40.dp
            val middleWidthRaw = maxWidth - leftColWidth - rightColWidth - 16.dp
            val middleColWidth = if (middleWidthRaw > 120.dp) middleWidthRaw else 120.dp

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .pointerInput(goal.id) {
                        detectTapGestures(
                            onLongPress = { onRemoveLongPress() },
                        )
                    },
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .width(leftColWidth)
                        .height(60.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    EmojiTrigger(
                        value = goal.emoji,
                        onClick = onEmojiClick,
                    )
                }

                Column(
                    modifier = Modifier
                        .width(middleColWidth)
                        .height(60.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        BasicTextField(
                            value = goal.title,
                            onValueChange = onTitleChange,
                            singleLine = true,
                            textStyle = TextStyle(
                                color = Color(0xFF1B2335),
                                fontWeight = FontWeight.SemiBold,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { inner ->
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterStart,
                                ) {
                                    if (goal.title.isBlank()) {
                                        Text(text = "Add goal", color = Color(0xFF93A1BA))
                                    }
                                    inner()
                                }
                            },
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(26.dp)
                            .horizontalScroll(optionsScroll),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        goal.hourOptions.forEachIndexed { index, minutes ->
                            GoalHourPill(
                                minutes = minutes,
                                compact = true,
                                onAction = { action -> onHourPress(index, action) },
                            )
                        }

                        AddHourOptionButton(
                            enabled = canAddHour,
                            compact = true,
                            onClick = onAddHourOption,
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .width(rightColWidth)
                        .height(60.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    RemoveGoalButton(onClick = onRemoveClick)
                }
            }
        }
    }
}

@Composable
private fun EmojiTrigger(
    value: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.size(30.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FAFF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (value.isBlank()) "✨" else value,
                style = TextStyle(
                    color = Color(0xFF22314A),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }
    }
}

@Composable
private fun AddHourOptionButton(
    enabled: Boolean,
    compact: Boolean = false,
    onClick: () -> Unit,
) {
    val buttonSize = if (compact) 26.dp else 34.dp
    val iconSize = if (compact) 11.dp else 14.dp

    Card(
        modifier = Modifier
            .size(buttonSize)
            .then(
                if (enabled) Modifier else Modifier,
            ),
        shape = GoalLayout.addHourShape,
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) Color(0xFFEFF4FC) else Color(0xFFF5F7FB),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick,
        enabled = enabled,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(iconSize)) {
                val stroke = 2.dp.toPx()
                val centerX = size.width / 2f
                val centerY = size.height / 2f
                val half = size.width * 0.35f
                drawLine(
                    color = Color(0xFF5A6D90),
                    start = androidx.compose.ui.geometry.Offset(centerX - half, centerY),
                    end = androidx.compose.ui.geometry.Offset(centerX + half, centerY),
                    strokeWidth = stroke,
                )
                drawLine(
                    color = Color(0xFF5A6D90),
                    start = androidx.compose.ui.geometry.Offset(centerX, centerY - half),
                    end = androidx.compose.ui.geometry.Offset(centerX, centerY + half),
                    strokeWidth = stroke,
                )
            }
        }
    }
}

@Composable
private fun RemoveGoalButton(
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.size(32.dp),
        shape = GoalLayout.addHourShape,
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEFF2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(14.dp)) {
                val stroke = 2.dp.toPx()
                val centerX = size.width / 2f
                val centerY = size.height / 2f
                val half = size.width * 0.34f

                drawLine(
                    color = Color(0xFFB34A5A),
                    start = androidx.compose.ui.geometry.Offset(centerX - half, centerY - half),
                    end = androidx.compose.ui.geometry.Offset(centerX + half, centerY + half),
                    strokeWidth = stroke,
                )
                drawLine(
                    color = Color(0xFFB34A5A),
                    start = androidx.compose.ui.geometry.Offset(centerX - half, centerY + half),
                    end = androidx.compose.ui.geometry.Offset(centerX + half, centerY - half),
                    strokeWidth = stroke,
                )
            }
        }
    }
}

