package com.narvane.client.day.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.narvane.client.day.layout.buildDayPlacements
import com.narvane.client.day.model.DAY_MINUTES_PER_HOUR
import com.narvane.client.day.model.DAY_SLOT_STEP_MINUTES
import com.narvane.client.day.model.DayAction
import com.narvane.client.day.model.DayResizeEdge
import com.narvane.client.day.model.DayState
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
internal fun DayTimelineGrid(
    state: DayState,
    hourHeight: Dp,
    gridHeight: Dp,
    totalMinutes: Int,
    onAction: (DayAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var gridHeightPx by remember { mutableFloatStateOf(1f) }
    val pxPerMinute = (gridHeightPx / totalMinutes.toFloat()).coerceAtLeast(0.001f)
    val stepPx = (pxPerMinute * DAY_SLOT_STEP_MINUTES).coerceAtLeast(4f)

    fun minuteToOffset(minute: Int): Dp {
        val dayStart = state.startHour * DAY_MINUTES_PER_HOUR
        val dayEnd = state.endHour * DAY_MINUTES_PER_HOUR
        val clamped = minute.coerceIn(dayStart, dayEnd)
        val ratio = (clamped - dayStart).toFloat() / totalMinutes.toFloat()
        return gridHeight * ratio
    }

    fun yToMinute(y: Float): Int {
        val clampedY = y.coerceIn(0f, gridHeightPx)
        val minuteFromStart = (clampedY / gridHeightPx * totalMinutes).roundToInt()
        val stepped = (minuteFromStart / DAY_SLOT_STEP_MINUTES.toFloat()).roundToInt() * DAY_SLOT_STEP_MINUTES
        return (state.startHour * DAY_MINUTES_PER_HOUR + stepped)
            .coerceIn(
                state.startHour * DAY_MINUTES_PER_HOUR,
                state.endHour * DAY_MINUTES_PER_HOUR,
            )
    }

    BoxWithConstraints(
        modifier = modifier
            .height(gridHeight)
            .onSizeChanged { size ->
                gridHeightPx = size.height.toFloat().coerceAtLeast(1f)
            },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            for (index in 0 until (state.endHour - state.startHour)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(hourHeight)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        ),
                )
            }
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(state.startHour, state.endHour, stepPx) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val pointerId = down.id
                        val startMinute = yToMinute(down.position.y)
                        val dragThreshold = if (viewConfiguration.touchSlop < 6f) {
                            viewConfiguration.touchSlop
                        } else {
                            6f
                        }
                        var dragging = false

                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == pointerId } ?: break

                            if (change.changedToUpIgnoreConsumed()) {
                                if (dragging) {
                                    onAction(DayAction.FinishRangeSelection)
                                } else {
                                    onAction(DayAction.TapSlot(startMinute))
                                }
                                break
                            }

                            val dyFromStart = change.position.y - down.position.y
                            if (!dragging && abs(dyFromStart) >= dragThreshold) {
                                dragging = true
                                onAction(DayAction.StartRangeSelection(startMinute))
                                onAction(DayAction.UpdateRangeSelection(yToMinute(change.position.y)))
                                change.consume()
                            } else if (dragging) {
                                onAction(DayAction.UpdateRangeSelection(yToMinute(change.position.y)))
                                change.consume()
                            }
                        }
                    }
                },
        )

        val placements = remember(state.events) {
            buildDayPlacements(state.events)
        }

        placements.forEach { placement ->
            val event = placement.event
            key(event.id) {
                val top = minuteToOffset(event.startMinute)
                val bottom = minuteToOffset(event.endMinute)
                val height = maxOf(bottom - top, 22.dp)
                val borderColor = Color(0xFF5EA7F1)
                val laneCount = max(placement.laneCount, 1)
                val laneWidth = (maxWidth - 8.dp) / laneCount
                val laneX = 4.dp + laneWidth * placement.lane
                val itemWidth = (laneWidth - 4.dp).coerceAtLeast(22.dp)

                DayTimeBlock(
                    modifier = Modifier
                        .offset(x = laneX)
                        .offset(y = top)
                        .width(itemWidth)
                        .height(height)
                        .padding(vertical = 1.dp),
                    title = event.title,
                    startMinute = event.startMinute,
                    endMinute = event.endMinute,
                    containerColor = Color(0xFF8EC5FF).copy(alpha = 0.85f),
                    borderColor = borderColor,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    handleColor = borderColor,
                    stepPx = stepPx,
                    forceInteractionVisual = false,
                    onTap = { onAction(DayAction.OpenEventEditor(event.id)) },
                    onMoveByStep = { deltaMinutes ->
                        onAction(
                            DayAction.MoveEventByDelta(
                                eventId = event.id,
                                deltaMinutes = deltaMinutes,
                            ),
                        )
                    },
                    onResizeStart = { deltaMinutes ->
                        onAction(
                            DayAction.ResizeEventByDelta(
                                eventId = event.id,
                                edge = DayResizeEdge.Start,
                                deltaMinutes = deltaMinutes,
                            ),
                        )
                    },
                    onResizeEnd = { deltaMinutes ->
                        onAction(
                            DayAction.ResizeEventByDelta(
                                eventId = event.id,
                                edge = DayResizeEdge.End,
                                deltaMinutes = deltaMinutes,
                            ),
                        )
                    },
                )
            }
        }

        if (state.editingEventId == null) {
            state.selection?.let { selection ->
                val start = min(selection.startMinute, selection.endMinute)
                val end = max(selection.startMinute, selection.endMinute)
                val top = minuteToOffset(start)
                val bottom = minuteToOffset(end)
                val height = maxOf(bottom - top, 18.dp)

                key("draft-selection") {
                    DayTimeBlock(
                        modifier = Modifier
                            .offset(y = top)
                            .fillMaxWidth()
                            .height(height)
                            .padding(horizontal = 4.dp)
                            .zIndex(2f),
                        title = null,
                        startMinute = start,
                        endMinute = end,
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.17f),
                        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f),
                        textColor = MaterialTheme.colorScheme.onSurface,
                        handleColor = Color(0xFF8E61D9),
                        stepPx = stepPx,
                        forceInteractionVisual = state.editor == null,
                        onTap = null,
                        onMoveByStep = null,
                        onResizeStart = { deltaMinutes ->
                            onAction(
                                DayAction.ResizeDraftByDelta(
                                    edge = DayResizeEdge.Start,
                                    deltaMinutes = deltaMinutes,
                                ),
                            )
                        },
                        onResizeEnd = { deltaMinutes ->
                            onAction(
                                DayAction.ResizeDraftByDelta(
                                    edge = DayResizeEdge.End,
                                    deltaMinutes = deltaMinutes,
                                ),
                            )
                        },
                    )
                }
            }
        }
    }
}
