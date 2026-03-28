package com.narvane.client.goal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.narvane.client.goal.layout.GoalLayout
import com.narvane.client.goal.logic.addGoal
import com.narvane.client.goal.logic.addHourOption
import com.narvane.client.goal.logic.appendRecentEmoji
import com.narvane.client.goal.logic.applyHourOptionPress
import com.narvane.client.goal.logic.emojisForCategory
import com.narvane.client.goal.logic.removeGoal
import com.narvane.client.goal.logic.sanitizeGoals
import com.narvane.client.goal.logic.updateGoalEmoji
import com.narvane.client.goal.logic.updateGoalTitle
import com.narvane.client.goal.model.GoalEmojiCategoryId
import com.narvane.client.goal.model.GoalEmojiCatalog
import com.narvane.client.goal.model.Goal

@Composable
fun GoalList(
    goals: List<Goal>,
    modifier: Modifier = Modifier,
    onChange: (List<Goal>) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    var localGoals by remember { mutableStateOf(sanitizeGoals(goals)) }
    var pickerGoalId by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf(GoalEmojiCategoryId.Recent) }
    var recentEmojiIds by rememberSaveable { mutableStateOf(emptyList<String>()) }

    LaunchedEffect(goals) {
        localGoals = sanitizeGoals(goals)
    }

    fun commit(reducer: (List<Goal>) -> List<Goal>) {
        val next = reducer(localGoals)
        localGoals = next
        onChange(next)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F8FC)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 760.dp)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            localGoals.forEach { goal ->
                GoalCard(
                    goal = goal,
                    onEmojiClick = {
                        focusManager.clearFocus(force = true)
                        pickerGoalId = goal.id
                        selectedCategory = GoalEmojiCategoryId.Recent
                    },
                    onTitleChange = { title ->
                        commit { current -> updateGoalTitle(current, goal.id, title) }
                    },
                    onAddHourOption = {
                        commit { current -> addHourOption(current, goal.id) }
                    },
                    onHourPress = { index, action ->
                        commit { current -> applyHourOptionPress(current, goal.id, index, action) }
                    },
                    onRemoveClick = {
                        commit { current -> removeGoal(current, goal.id) }
                        if (pickerGoalId == goal.id) {
                            pickerGoalId = null
                        }
                    },
                    onRemoveLongPress = {
                        commit { current -> removeGoal(current, goal.id) }
                        if (pickerGoalId == goal.id) {
                            pickerGoalId = null
                        }
                    },
                )
            }

            AddGoalCard(
                onClick = { commit(::addGoal) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        val openGoalId = pickerGoalId
        if (openGoalId != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.22f))
                    .clickable { pickerGoalId = null },
            )

            GoalEmojiPickerSheet(
                categories = GoalEmojiCatalog.categories,
                selectedCategoryId = selectedCategory,
                emojis = emojisForCategory(
                    categoryId = selectedCategory,
                    recentIds = recentEmojiIds,
                ),
                onCategorySelect = { selectedCategory = it },
                onEmojiSelect = { selectedEmoji ->
                    val emoji = selectedEmoji.symbol
                    commit { current -> updateGoalEmoji(current, openGoalId, emoji) }
                    recentEmojiIds = appendRecentEmoji(recentEmojiIds, selectedEmoji.id)
                    pickerGoalId = null
                },
                onClose = { pickerGoalId = null },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .widthIn(max = 760.dp)
                    .imePadding(),
            )
        }
    }
}

@Composable
private fun AddGoalCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .background(Color.White, GoalLayout.addGoalShape)
            .dashedBorder(Color(0xFFA7CDE7))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(16.dp)) {
            val stroke = 2.dp.toPx()
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val half = minOf(size.width, size.height) * 0.42f
            drawLine(
                color = Color(0xFF586A8A),
                start = androidx.compose.ui.geometry.Offset(centerX - half, centerY),
                end = androidx.compose.ui.geometry.Offset(centerX + half, centerY),
                strokeWidth = stroke,
            )
            drawLine(
                color = Color(0xFF586A8A),
                start = androidx.compose.ui.geometry.Offset(centerX, centerY - half),
                end = androidx.compose.ui.geometry.Offset(centerX, centerY + half),
                strokeWidth = stroke,
            )
        }
    }
}

private fun Modifier.dashedBorder(color: Color): Modifier {
    return this.then(
        Modifier.drawBehind {
            drawRoundRect(
                color = color,
                size = size,
                cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx()),
                style = Stroke(
                    width = 1.8.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(11f, 9f)),
                ),
            )
        },
    )
}
