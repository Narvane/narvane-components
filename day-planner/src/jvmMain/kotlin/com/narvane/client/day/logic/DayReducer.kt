package com.narvane.client.day.logic

import com.narvane.client.day.model.DAY_DEFAULT_EVENT_MINUTES
import com.narvane.client.day.model.DAY_MINUTES_PER_HOUR
import com.narvane.client.day.model.DayAction
import com.narvane.client.day.model.DayEditor
import com.narvane.client.day.model.DayEvent
import com.narvane.client.day.model.DaySelection
import com.narvane.client.day.model.DayState
import kotlin.math.max
import kotlin.math.min

data class DayReduceResult(
    val state: DayState,
    val nextId: Int,
)

fun reduceDayState(
    state: DayState,
    nextId: Int,
    action: DayAction,
): DayReduceResult {
    var newState = state
    var newNextId = nextId
    val dayStart = state.startHour * DAY_MINUTES_PER_HOUR
    val dayEnd = state.endHour * DAY_MINUTES_PER_HOUR

    when (action) {
        is DayAction.TapSlot -> {
            val start = snap(action.minute).coerceIn(dayStart, dayEnd - DAY_DEFAULT_EVENT_MINUTES)
            val end = (start + DAY_DEFAULT_EVENT_MINUTES).coerceAtMost(dayEnd)
            val range = normalizeRange(start, end, dayStart, dayEnd)
            newState = state.copy(
                selection = range,
                editor = DayEditor(),
                editingEventId = null,
            )
        }

        is DayAction.StartRangeSelection -> {
            val minute = snap(action.minute).coerceIn(dayStart, dayEnd)
            newState = state.copy(
                selection = DaySelection(
                    startMinute = minute,
                    endMinute = minute,
                ),
                editor = null,
                editingEventId = null,
            )
        }

        is DayAction.UpdateRangeSelection -> {
            state.selection?.let { current ->
                newState = state.copy(
                    selection = current.copy(
                        endMinute = snap(action.minute).coerceIn(dayStart, dayEnd),
                    ),
                )
            }
        }

        DayAction.FinishRangeSelection -> {
            state.selection?.let { current ->
                val start = min(current.startMinute, current.endMinute)
                val end = max(current.startMinute, current.endMinute)
                val defaultEnd = if (end == start) start + DAY_DEFAULT_EVENT_MINUTES else end
                val normalized = normalizeRange(start, defaultEnd, dayStart, dayEnd)
                newState = state.copy(
                    selection = normalized,
                    editor = DayEditor(),
                    editingEventId = null,
                )
            }
        }

        is DayAction.OpenEventEditor -> {
            val event = state.events.firstOrNull { it.id == action.eventId }
            if (event != null) {
                newState = state.copy(
                    selection = DaySelection(event.startMinute, event.endMinute),
                    editor = DayEditor(title = event.title),
                    editingEventId = event.id,
                )
            }
        }

        is DayAction.MoveEventByDelta -> {
            val moved = moveEvent(
                events = state.events,
                eventId = action.eventId,
                deltaMinutes = action.deltaMinutes,
                dayStart = dayStart,
                dayEnd = dayEnd,
            )
            newState = state.copy(events = moved)

            if (state.editingEventId == action.eventId) {
                moved.firstOrNull { it.id == action.eventId }?.let { updated ->
                    newState = newState.copy(
                        selection = DaySelection(updated.startMinute, updated.endMinute),
                    )
                }
            }
        }

        is DayAction.ResizeDraftByDelta -> {
            state.selection?.let { current ->
                val resized = resizeSelection(
                    selection = normalizeRange(
                        min(current.startMinute, current.endMinute),
                        max(current.startMinute, current.endMinute),
                        dayStart,
                        dayEnd,
                    ),
                    edge = action.edge,
                    deltaMinutes = action.deltaMinutes,
                    dayStart = dayStart,
                    dayEnd = dayEnd,
                )
                newState = state.copy(selection = resized)
            }
        }

        is DayAction.ResizeEventByDelta -> {
            val updatedEvents = state.events.map { event ->
                if (event.id != action.eventId) {
                    event
                } else {
                    val resized = resizeSelection(
                        selection = DaySelection(event.startMinute, event.endMinute),
                        edge = action.edge,
                        deltaMinutes = action.deltaMinutes,
                        dayStart = dayStart,
                        dayEnd = dayEnd,
                    )
                    event.copy(
                        startMinute = resized.startMinute,
                        endMinute = resized.endMinute,
                    )
                }
            }.sortedBy { it.startMinute }

            newState = state.copy(events = updatedEvents)

            if (state.editingEventId == action.eventId) {
                updatedEvents.firstOrNull { it.id == action.eventId }?.let { updated ->
                    newState = newState.copy(
                        selection = DaySelection(updated.startMinute, updated.endMinute),
                    )
                }
            }
        }

        DayAction.DismissEditor -> {
            newState = state.copy(
                selection = null,
                editor = null,
                editingEventId = null,
            )
        }

        is DayAction.UpdateTitle -> {
            state.editor?.let { editor ->
                newState = state.copy(
                    editor = editor.copy(
                        title = action.value,
                        error = null,
                    ),
                )
            }
        }

        DayAction.SaveEvent -> {
            val selection = state.selection
            val editor = state.editor
            if (selection != null && editor != null) {
                val title = editor.title.trim()
                if (title.isNotEmpty()) {
                    val normalized = normalizeRange(
                        start = min(selection.startMinute, selection.endMinute),
                        end = max(selection.startMinute, selection.endMinute),
                        dayStart = dayStart,
                        dayEnd = dayEnd,
                    )

                    val editingId = state.editingEventId
                    val candidate = DayEvent(
                        id = editingId ?: newNextId.toString(),
                        title = title,
                        startMinute = normalized.startMinute,
                        endMinute = normalized.endMinute,
                    )
                    val otherEvents = if (editingId != null) {
                        state.events.filterNot { it.id == editingId }
                    } else {
                        state.events
                    }

                    if (wouldExceedCrossLimit(otherEvents + candidate, state.maxCrossEvents)) {
                        newState = state.copy(
                            editor = editor.copy(
                                error = "Limite de ${state.maxCrossEvents} eventos simultaneos atingido.",
                            ),
                        )
                    } else if (editingId != null) {
                        newState = state.copy(
                            events = (otherEvents + candidate).sortedBy { it.startMinute },
                            selection = null,
                            editor = null,
                            editingEventId = null,
                        )
                    } else {
                        val event = DayEvent(
                            id = newNextId.toString(),
                            title = title,
                            startMinute = normalized.startMinute,
                            endMinute = normalized.endMinute,
                        )
                        newNextId += 1

                        newState = state.copy(
                            events = (state.events + event).sortedBy { it.startMinute },
                            selection = null,
                            editor = null,
                            editingEventId = null,
                        )
                    }
                }
            }
        }

        DayAction.RemoveEvent -> {
            val editingId = state.editingEventId
            if (editingId != null) {
                newState = state.copy(
                    events = state.events.filterNot { it.id == editingId },
                    selection = null,
                    editor = null,
                    editingEventId = null,
                )
            }
        }
    }

    return DayReduceResult(
        state = newState,
        nextId = newNextId,
    )
}
