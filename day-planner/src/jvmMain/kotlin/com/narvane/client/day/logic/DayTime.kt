package com.narvane.client.day.logic

import com.narvane.client.day.model.DAY_MINUTES_PER_HOUR
import com.narvane.client.day.model.DAY_SLOT_STEP_MINUTES
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

fun formatMinute(total: Int): String {
    val safe = total.coerceAtLeast(0)
    val hour = safe / DAY_MINUTES_PER_HOUR
    val minute = safe % DAY_MINUTES_PER_HOUR
    return "%02d:%02d".format(hour, minute)
}

fun snap(value: Int): Int {
    return (value / DAY_SLOT_STEP_MINUTES.toFloat()).roundToInt() * DAY_SLOT_STEP_MINUTES
}

fun snapDelta(value: Int): Int {
    if (value == 0) return 0
    val snapped = (value / DAY_SLOT_STEP_MINUTES.toFloat()).roundToInt() * DAY_SLOT_STEP_MINUTES
    return when {
        snapped > 0 -> max(snapped, DAY_SLOT_STEP_MINUTES)
        snapped < 0 -> min(snapped, -DAY_SLOT_STEP_MINUTES)
        else -> 0
    }
}
