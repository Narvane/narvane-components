package com.narvane.sampledata

import com.narvane.client.day.model.DAY_DEFAULT_EVENT_MINUTES
import com.narvane.client.day.model.DAY_MINUTES_PER_HOUR
import com.narvane.client.day.model.DayEvent
import com.narvane.client.day.model.DayState

object DayPlannerSampleData {
    fun defaultState(): DayState {
        return DayState(
            startHour = 6,
            endHour = 20,
            maxCrossEvents = 3,
            events = listOf(
                DayEvent(
                    id = "1",
                    title = "Trabalho",
                    startMinute = 8 * DAY_MINUTES_PER_HOUR,
                    endMinute = 10 * DAY_MINUTES_PER_HOUR,
                ),
                DayEvent(
                    id = "2",
                    title = "Estudo",
                    startMinute = 15 * DAY_MINUTES_PER_HOUR,
                    endMinute = 16 * DAY_MINUTES_PER_HOUR + DAY_DEFAULT_EVENT_MINUTES / 2,
                ),
            ),
        )
    }
}
