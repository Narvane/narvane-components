package com.narvane.client.day.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.narvane.client.day.logic.DayReduceResult
import com.narvane.client.day.logic.collectOutOfRangeEvents
import com.narvane.client.day.logic.reduceDayState
import com.narvane.client.day.model.DAY_MINUTES_PER_HOUR
import com.narvane.client.day.model.DayAction
import com.narvane.client.day.model.DayEvent
import com.narvane.client.day.model.DayState

@Composable
fun DayPlanner(
    state: DayState,
    modifier: Modifier = Modifier,
    onOutOfRangeEvents: (List<DayEvent>) -> Unit = {},
) {
    var plannerState by remember { mutableStateOf(state) }
    var nextId by remember { mutableIntStateOf(deriveNextId(state.events)) }

    // Keep component pluggable: host can replace full state when needed.
    LaunchedEffect(state) {
        plannerState = state
        nextId = deriveNextId(state.events)
    }

    LaunchedEffect(plannerState.events, plannerState.startHour, plannerState.endHour) {
        val outOfRange = collectOutOfRangeEvents(
            events = plannerState.events,
            startHour = plannerState.startHour,
            endHour = plannerState.endHour,
        )
        if (outOfRange.isNotEmpty()) {
            onOutOfRangeEvents(outOfRange)
        }
    }

    val onAction: (DayAction) -> Unit = { action ->
        val result: DayReduceResult = reduceDayState(
            state = plannerState,
            nextId = nextId,
            action = action,
        )
        plannerState = result.state
        nextId = result.nextId
    }

    DayPlannerContent(
        state = plannerState,
        onAction = onAction,
        modifier = modifier,
    )
}

private fun deriveNextId(events: List<DayEvent>): Int {
    val maxNumericId = events.mapNotNull { it.id.toIntOrNull() }.maxOrNull() ?: 0
    return maxNumericId + 1
}

@Composable
private fun DayPlannerContent(
    state: DayState,
    onAction: (DayAction) -> Unit,
    modifier: Modifier,
) {
    val totalMinutes = remember(state.startHour, state.endHour) {
        (state.endHour - state.startHour) * DAY_MINUTES_PER_HOUR
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val compact = maxWidth < 700.dp
        val labelWidth = if (compact) 48.dp else 64.dp
        val hourHeight = if (compact) 72.dp else 84.dp
        val gridHeight = hourHeight * (state.endHour - state.startHour)
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(if (compact) 8.dp else 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Day planner",
                style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest, RoundedCornerShape(16.dp))
                    .padding(10.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                ) {
                    DayHoursColumn(
                        startHour = state.startHour,
                        endHour = state.endHour,
                        labelWidth = labelWidth,
                        gridHeight = gridHeight,
                        hourHeight = hourHeight,
                    )
                    DayTimelineGrid(
                        state = state,
                        hourHeight = hourHeight,
                        gridHeight = gridHeight,
                        totalMinutes = totalMinutes,
                        onAction = onAction,
                        modifier = Modifier
                            .weight(1f)
                            .height(gridHeight),
                    )
                }
            }
        }

        if (state.editor != null && state.selection != null) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
                    .fillMaxWidth()
                    .widthIn(max = 640.dp),
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
                DayEditorSheet(
                    editor = state.editor,
                    selection = state.selection,
                    isEditingExisting = state.editingEventId != null,
                    onAction = onAction,
                )
            }
        }
    }
}
