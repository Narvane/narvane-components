package com.narvane.client.day.logic

import com.narvane.client.day.model.DAY_MIN_EVENT_MINUTES
import com.narvane.client.day.model.DAY_MINUTES_PER_HOUR
import com.narvane.client.day.model.DayEvent
import com.narvane.client.day.model.DayResizeEdge
import com.narvane.client.day.model.DaySelection
import kotlin.math.max
import kotlin.math.min

fun moveEvent(
    events: List<DayEvent>,
    eventId: String,
    deltaMinutes: Int,
    dayStart: Int,
    dayEnd: Int,
): List<DayEvent> {
    val snappedDelta = snapDelta(deltaMinutes)
    if (snappedDelta == 0) return events

    return events.map { event ->
        if (event.id != eventId) {
            event
        } else {
            val duration = event.endMinute - event.startMinute
            val newStart = (event.startMinute + snappedDelta).coerceIn(dayStart, dayEnd - duration)
            val newEnd = newStart + duration
            event.copy(
                startMinute = newStart,
                endMinute = newEnd,
            )
        }
    }.sortedBy { it.startMinute }
}

fun resizeSelection(
    selection: DaySelection,
    edge: DayResizeEdge,
    deltaMinutes: Int,
    dayStart: Int,
    dayEnd: Int,
): DaySelection {
    val snappedDelta = snapDelta(deltaMinutes)
    if (snappedDelta == 0) return selection

    val start = min(selection.startMinute, selection.endMinute)
    val end = max(selection.startMinute, selection.endMinute)

    return when (edge) {
        DayResizeEdge.Start -> {
            val newStart = (start + snappedDelta).coerceIn(dayStart, end - DAY_MIN_EVENT_MINUTES)
            normalizeRange(newStart, end, dayStart, dayEnd)
        }

        DayResizeEdge.End -> {
            val newEnd = (end + snappedDelta).coerceIn(start + DAY_MIN_EVENT_MINUTES, dayEnd)
            normalizeRange(start, newEnd, dayStart, dayEnd)
        }
    }
}

fun normalizeRange(
    start: Int,
    end: Int,
    dayStart: Int,
    dayEnd: Int,
): DaySelection {
    val snappedStart = snap(start).coerceIn(dayStart, dayEnd)
    val snappedEnd = snap(end).coerceIn(dayStart, dayEnd)

    val low = min(snappedStart, snappedEnd)
    val high = max(snappedStart, snappedEnd)
    val adjustedEnd = max(high, low + DAY_MIN_EVENT_MINUTES).coerceAtMost(dayEnd)
    val adjustedStart = min(low, adjustedEnd - DAY_MIN_EVENT_MINUTES).coerceAtLeast(dayStart)
    return DaySelection(adjustedStart, adjustedEnd)
}

fun wouldExceedCrossLimit(
    events: List<DayEvent>,
    maxCrossEvents: Int,
): Boolean {
    if (maxCrossEvents <= 0) return events.isNotEmpty()
    if (events.isEmpty()) return false

    val points = mutableListOf<Pair<Int, Int>>()
    events.forEach { event ->
        points += event.startMinute to 1
        points += event.endMinute to -1
    }

    points.sortWith(compareBy<Pair<Int, Int>>({ it.first }, { it.second }))

    var current = 0
    var peak = 0
    points.forEach { (_, delta) ->
        current += delta
        if (current > peak) peak = current
    }
    return peak > maxCrossEvents
}

fun collectOutOfRangeEvents(
    events: List<DayEvent>,
    startHour: Int,
    endHour: Int,
): List<DayEvent> {
    val dayStart = startHour * DAY_MINUTES_PER_HOUR
    val dayEnd = endHour * DAY_MINUTES_PER_HOUR
    return events.filter { it.startMinute < dayStart || it.endMinute > dayEnd }
}
