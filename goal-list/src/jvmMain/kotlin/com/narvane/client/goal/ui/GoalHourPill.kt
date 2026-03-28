package com.narvane.client.goal.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.narvane.client.goal.logic.formatHourOption
import com.narvane.client.goal.logic.resolveHourPressAction
import com.narvane.client.goal.model.GoalHourPressAction

@Composable
fun GoalHourPill(
    minutes: Int,
    compact: Boolean = false,
    onAction: (GoalHourPressAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.pointerInput(minutes) {
            detectTapGestures(
                onPress = {
                    val startMs = System.currentTimeMillis()
                    val released = tryAwaitRelease()
                    if (released) {
                        val elapsed = System.currentTimeMillis() - startMs
                        onAction(resolveHourPressAction(elapsed))
                    }
                },
            )
        },
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFE8F6FF),
        contentColor = Color(0xFF205A82),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        val textStyle = if (compact) {
            MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
        } else {
            MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
        }
        val minHeight = if (compact) 24.dp else 34.dp
        val horizontalPadding = if (compact) 9.dp else 14.dp
        val verticalPadding = if (compact) 3.dp else 7.dp

        Text(
            text = formatHourOption(minutes),
            style = textStyle,
            modifier = Modifier
                .defaultMinSize(minHeight = minHeight)
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        )
    }
}
