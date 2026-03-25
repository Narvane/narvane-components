package com.narvane.client.day.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
internal fun DayHoursColumn(
    startHour: Int,
    endHour: Int,
    labelWidth: Dp,
    gridHeight: Dp,
    hourHeight: Dp,
) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .width(labelWidth)
            .height(gridHeight),
    ) {
        for (hour in startHour until endHour) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(hourHeight),
                contentAlignment = Alignment.TopStart,
            ) {
                Text(
                    text = "%02d:00".format(hour),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
