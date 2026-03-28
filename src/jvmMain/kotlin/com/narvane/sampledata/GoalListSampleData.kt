package com.narvane.sampledata

import com.narvane.client.goal.model.Goal

object GoalListSampleData {
    fun defaultGoals(): List<Goal> {
        return listOf(
            Goal(
                id = "1",
                emoji = "🧹",
                title = "Cleaning",
                hourOptions = listOf(30, 60),
            ),
            Goal(
                id = "2",
                emoji = "📚",
                title = "Study",
                hourOptions = listOf(60, 90),
            ),
        )
    }
}
