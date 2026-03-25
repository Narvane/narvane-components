package com.narvane.client.day.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.narvane.client.day.model.DAY_SLOT_STEP_MINUTES

@Composable
internal fun DayResizeHandle(
    modifier: Modifier,
    handleColor: Color,
    stepPx: Float,
    onInteractionStart: () -> Unit,
    onInteractionEnd: () -> Unit,
    onStep: (Int) -> Unit,
) {
    val onStepState = rememberUpdatedState(onStep)

    Box(
        modifier = modifier
            .zIndex(4f)
            .width(24.dp)
            .height(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .width(16.dp)
                .height(16.dp)
                .background(handleColor, CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                .pointerInput(stepPx) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        down.consume()
                        onInteractionStart()
                        val pointerId = down.id
                        var carry = 0f

                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == pointerId } ?: break

                            if (change.changedToUpIgnoreConsumed()) {
                                onInteractionEnd()
                                break
                            }

                            val dy = change.positionChange().y
                            carry += dy
                            while (carry >= stepPx) {
                                onStepState.value(DAY_SLOT_STEP_MINUTES)
                                carry -= stepPx
                            }
                            while (carry <= -stepPx) {
                                onStepState.value(-DAY_SLOT_STEP_MINUTES)
                                carry += stepPx
                            }
                            change.consume()
                        }
                        onInteractionEnd()
                    }
                },
        )
    }
}
