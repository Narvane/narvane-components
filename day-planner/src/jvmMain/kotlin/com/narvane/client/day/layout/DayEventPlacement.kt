package com.narvane.client.day.layout

import com.narvane.client.day.model.DayEvent
import kotlin.math.max

data class DayPlacement(
    val event: DayEvent,
    val lane: Int,
    val laneCount: Int,
)

private data class TempPlacement(
    val event: DayEvent,
    val lane: Int,
)

fun buildDayPlacements(events: List<DayEvent>): List<DayPlacement> {
    if (events.isEmpty()) return emptyList()

    val sorted = events.sortedWith(compareBy<DayEvent> { it.startMinute }.thenBy { it.endMinute })
    val result = mutableListOf<DayPlacement>()
    val active = mutableListOf<TempPlacement>()
    val cluster = mutableListOf<TempPlacement>()
    var clusterMax = 0

    fun finalizeCluster() {
        if (cluster.isEmpty()) return
        val laneCount = max(clusterMax, 1)
        cluster.forEach { temp ->
            result += DayPlacement(
                event = temp.event,
                lane = temp.lane,
                laneCount = laneCount,
            )
        }
        cluster.clear()
        clusterMax = 0
    }

    for (event in sorted) {
        active.removeAll { it.event.endMinute <= event.startMinute }

        if (active.isEmpty() && cluster.isNotEmpty()) {
            finalizeCluster()
        }

        val used = active.map { it.lane }.toSet()
        var lane = 0
        while (used.contains(lane)) {
            lane += 1
        }

        val placement = TempPlacement(event = event, lane = lane)
        active += placement
        cluster += placement
        clusterMax = max(clusterMax, active.size)
    }

    finalizeCluster()
    return result
}
