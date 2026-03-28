package com.narvane.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.narvane.client.day.ui.DayPlanner
import com.narvane.client.goal.ui.GoalList
import com.narvane.sampledata.DayPlannerSampleData
import com.narvane.sampledata.GoalListSampleData

data class ComponentMenuItem(
    val title: String,
    val content: @Composable () -> Unit,
)

@Composable
fun ComponentsTesterApp() {
    val components = remember {
        listOf(
            ComponentMenuItem(
                title = "Day Planner",
                content = {
                    DayPlanner(
                        state = DayPlannerSampleData.defaultState(),
                        modifier = Modifier.fillMaxSize(),
                        onOutOfRangeEvents = { outOfRange ->
                            println("Out-of-range events detected (${outOfRange.size}):")
                            outOfRange.forEach { event ->
                                println(event)
                            }
                        },
                    )
                },
            ),
            ComponentMenuItem(
                title = "Goal List",
                content = {
                    var demoGoals by remember { mutableStateOf(GoalListSampleData.defaultGoals()) }
                    GoalList(
                        goals = demoGoals,
                        modifier = Modifier.fillMaxSize(),
                        onChange = { updated ->
                            demoGoals = updated
                            println("Goal list updated (${updated.size} goals)")
                        },
                    )
                },
            ),
        )
    }

    var selectedTitle by remember { mutableStateOf<String?>(null) }
    val selected = components.firstOrNull { it.title == selectedTitle }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            if (selected == null) {
                ComponentMenuList(
                    components = components,
                    onSelect = { selectedTitle = it },
                )
            } else {
                ComponentPreviewScreen(
                    title = selected.title,
                    onBack = { selectedTitle = null },
                    content = selected.content,
                )
            }
        }
    }
}
