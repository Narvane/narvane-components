package com.narvane.client.goal.model

const val GOAL_STEP_MINUTES = 30
const val GOAL_MIN_HOUR_OPTION = 30
const val GOAL_MAX_HOUR_OPTIONS = 3
const val GOAL_DECREMENT_PRESS_MS = 500L
const val GOAL_REMOVE_PRESS_MS = 1000L

typealias HourOption = Int

data class Goal(
    val id: String,
    val emoji: String = "",
    val title: String = "",
    val hourOptions: List<HourOption> = emptyList(),
)

enum class GoalHourPressAction {
    Increment,
    Decrement,
    Remove,
}
