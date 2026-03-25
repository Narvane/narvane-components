package com.narvane.client.day.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.narvane.client.day.logic.formatMinute
import com.narvane.client.day.model.DAY_SLOT_STEP_MINUTES
import kotlin.math.abs

@Composable
internal fun DayTimeBlock(
    modifier: Modifier,
    title: String?,
    startMinute: Int,
    endMinute: Int,
    containerColor: Color,
    borderColor: Color,
    textColor: Color,
    handleColor: Color,
    stepPx: Float,
    forceInteractionVisual: Boolean,
    onTap: (() -> Unit)?,
    onMoveByStep: ((Int) -> Unit)?,
    onResizeStart: (Int) -> Unit,
    onResizeEnd: (Int) -> Unit,
) {
    val onTapState = rememberUpdatedState(onTap)
    val onMoveByStepState = rememberUpdatedState(onMoveByStep)
    var isInteracting by remember { mutableStateOf(false) }
    val showOutsideTimes = forceInteractionVisual || isInteracting
    val rangeText = "${formatMinute(startMinute)} - ${formatMinute(endMinute)}"

    Box(
        modifier = modifier
            .background(containerColor, RoundedCornerShape(10.dp))
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .let { base ->
                if (onTap != null || onMoveByStep != null) {
                    base.pointerInput(stepPx) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = true)
                            val pointerId = down.id
                            val dragThreshold = if (viewConfiguration.touchSlop < 6f) {
                                viewConfiguration.touchSlop
                            } else {
                                6f
                            }
                            var dragging = false
                            var carry = 0f

                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull { it.id == pointerId } ?: break

                                if (change.changedToUpIgnoreConsumed()) {
                                    if (dragging) {
                                        isInteracting = false
                                    }
                                    if (!dragging) {
                                        onTapState.value?.invoke()
                                    }
                                    break
                                }

                                val dyFromStart = change.position.y - down.position.y
                                if (!dragging && onMoveByStepState.value != null && abs(dyFromStart) >= dragThreshold) {
                                    dragging = true
                                    isInteracting = true
                                    change.consume()
                                }

                                if (dragging && onMoveByStepState.value != null) {
                                    val dy = change.positionChange().y
                                    carry += dy
                                    while (carry >= stepPx) {
                                        onMoveByStepState.value?.invoke(DAY_SLOT_STEP_MINUTES)
                                        carry -= stepPx
                                    }
                                    while (carry <= -stepPx) {
                                        onMoveByStepState.value?.invoke(-DAY_SLOT_STEP_MINUTES)
                                        carry += stepPx
                                    }
                                    change.consume()
                                }
                            }
                        }
                    }
                } else {
                    base
                }
            },
    ) {
        if (title != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                )
                if (!showOutsideTimes) {
                    Text(
                        text = rangeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor.copy(alpha = 0.85f),
                    )
                }
            }
        } else if (!showOutsideTimes) {
            Text(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                text = rangeText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (showOutsideTimes) {
            Text(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-38).dp, y = (-2).dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerLowest,
                        shape = RoundedCornerShape(4.dp),
                    )
                    .padding(horizontal = 3.dp, vertical = 1.dp),
                text = formatMinute(startMinute),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-38).dp, y = 2.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerLowest,
                        shape = RoundedCornerShape(4.dp),
                    )
                    .padding(horizontal = 3.dp, vertical = 1.dp),
                text = formatMinute(endMinute),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        DayResizeHandle(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 18.dp, y = (-12).dp),
            handleColor = handleColor,
            stepPx = stepPx,
            onInteractionStart = { isInteracting = true },
            onInteractionEnd = { isInteracting = false },
            onStep = onResizeStart,
        )
        DayResizeHandle(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-12).dp, y = 12.dp),
            handleColor = handleColor,
            stepPx = stepPx,
            onInteractionStart = { isInteracting = true },
            onInteractionEnd = { isInteracting = false },
            onStep = onResizeEnd,
        )
    }
}
