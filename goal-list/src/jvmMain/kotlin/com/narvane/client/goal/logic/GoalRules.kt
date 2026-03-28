package com.narvane.client.goal.logic

import com.narvane.client.goal.model.GOAL_DECREMENT_PRESS_MS
import com.narvane.client.goal.model.GOAL_MAX_HOUR_OPTIONS
import com.narvane.client.goal.model.GOAL_MIN_HOUR_OPTION
import com.narvane.client.goal.model.GOAL_REMOVE_PRESS_MS
import com.narvane.client.goal.model.GOAL_STEP_MINUTES
import com.narvane.client.goal.model.Goal
import com.narvane.client.goal.model.GoalHourPressAction
import kotlin.math.roundToInt
import kotlin.random.Random

fun sanitizeGoals(goals: List<Goal>): List<Goal> {
    return goals.map { goal ->
        goal.copy(
            emoji = goal.emoji,
            title = goal.title,
            hourOptions = goal.hourOptions
                .take(GOAL_MAX_HOUR_OPTIONS)
                .map(::normalizeHourOption),
        )
    }
}

fun addGoal(goals: List<Goal>): List<Goal> {
    return sanitizeGoals(goals) + Goal(id = buildGoalId())
}

fun removeGoal(goals: List<Goal>, goalId: String): List<Goal> {
    return sanitizeGoals(goals).filterNot { it.id == goalId }
}

fun updateGoalEmoji(goals: List<Goal>, goalId: String, value: String): List<Goal> {
    return sanitizeGoals(goals).map { goal ->
        if (goal.id == goalId) goal.copy(emoji = value) else goal
    }
}

fun updateGoalTitle(goals: List<Goal>, goalId: String, value: String): List<Goal> {
    return sanitizeGoals(goals).map { goal ->
        if (goal.id == goalId) goal.copy(title = value) else goal
    }
}

fun addHourOption(goals: List<Goal>, goalId: String): List<Goal> {
    return sanitizeGoals(goals).map { goal ->
        if (goal.id != goalId || goal.hourOptions.size >= GOAL_MAX_HOUR_OPTIONS) {
            goal
        } else {
            goal.copy(hourOptions = goal.hourOptions + GOAL_MIN_HOUR_OPTION)
        }
    }
}

fun applyHourOptionPress(
    goals: List<Goal>,
    goalId: String,
    index: Int,
    action: GoalHourPressAction,
): List<Goal> {
    return sanitizeGoals(goals).map { goal ->
        if (goal.id != goalId || index !in goal.hourOptions.indices) {
            goal
        } else {
            val options = goal.hourOptions.toMutableList()
            when (action) {
                GoalHourPressAction.Increment -> {
                    options[index] = normalizeHourOption(options[index] + GOAL_STEP_MINUTES)
                }
                GoalHourPressAction.Decrement -> {
                    options[index] = normalizeHourOption(options[index] - GOAL_STEP_MINUTES)
                }
                GoalHourPressAction.Remove -> {
                    options.removeAt(index)
                }
            }
            goal.copy(hourOptions = options)
        }
    }
}

fun resolveHourPressAction(pressDurationMs: Long): GoalHourPressAction {
    return when {
        pressDurationMs >= GOAL_REMOVE_PRESS_MS -> GoalHourPressAction.Remove
        pressDurationMs >= GOAL_DECREMENT_PRESS_MS -> GoalHourPressAction.Decrement
        else -> GoalHourPressAction.Increment
    }
}

fun formatHourOption(minutes: Int): String {
    val safeMinutes = normalizeHourOption(minutes)
    val hours = safeMinutes / 60
    val rest = safeMinutes % 60
    return when {
        hours == 0 -> "${safeMinutes}m"
        rest == 0 -> "${hours}h"
        else -> "${hours}h ${rest}m"
    }
}

private fun normalizeHourOption(minutes: Int): Int {
    if (minutes <= 0) return GOAL_MIN_HOUR_OPTION
    val stepped = (minutes.toDouble() / GOAL_STEP_MINUTES).roundToInt() * GOAL_STEP_MINUTES
    return maxOf(GOAL_MIN_HOUR_OPTION, stepped)
}

private fun buildGoalId(): String {
    val randomSuffix = Random.nextInt(100000, 999999)
    return "goal-$randomSuffix"
}
