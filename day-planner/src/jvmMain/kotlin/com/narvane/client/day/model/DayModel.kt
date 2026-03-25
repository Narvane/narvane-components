package com.narvane.client.day.model

const val DAY_MINUTES_PER_HOUR = 60
const val DAY_SLOT_STEP_MINUTES = 15
const val DAY_MIN_EVENT_MINUTES = 30
const val DAY_DEFAULT_EVENT_MINUTES = 60

enum class DayResizeEdge {
    Start,
    End,
}

data class DayEvent(
    val id: String,
    val title: String,
    val startMinute: Int,
    val endMinute: Int,
)

data class DaySelection(
    val startMinute: Int,
    val endMinute: Int,
)

data class DayEditor(
    val title: String = "",
    val error: String? = null,
)

data class DayState(
    val startHour: Int,
    val endHour: Int,
    val events: List<DayEvent>,
    val selection: DaySelection? = null,
    val editor: DayEditor? = null,
    val editingEventId: String? = null,
    val maxCrossEvents: Int = 3,
)

sealed interface DayAction {
    data class TapSlot(val minute: Int) : DayAction
    data class StartRangeSelection(val minute: Int) : DayAction
    data class UpdateRangeSelection(val minute: Int) : DayAction
    data object FinishRangeSelection : DayAction

    data class OpenEventEditor(val eventId: String) : DayAction
    data class MoveEventByDelta(val eventId: String, val deltaMinutes: Int) : DayAction

    data class ResizeDraftByDelta(val edge: DayResizeEdge, val deltaMinutes: Int) : DayAction
    data class ResizeEventByDelta(
        val eventId: String,
        val edge: DayResizeEdge,
        val deltaMinutes: Int,
    ) : DayAction

    data object DismissEditor : DayAction
    data class UpdateTitle(val value: String) : DayAction
    data object SaveEvent : DayAction
    data object RemoveEvent : DayAction
}
