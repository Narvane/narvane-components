package com.narvane.client.goal.model

enum class GoalEmojiCategoryId {
    Recent,
    Work,
    Home,
    Health,
    Study,
    Personal,
}

data class GoalEmojiCategory(
    val id: GoalEmojiCategoryId,
    val label: String,
)

data class GoalEmojiItem(
    val id: String,
    val symbol: String,
    val categoryId: GoalEmojiCategoryId,
    val assetKey: String,
    val tags: List<String> = emptyList(),
)

object GoalEmojiCatalog {
    val categories = listOf(
        GoalEmojiCategory(GoalEmojiCategoryId.Recent, "Recent"),
        GoalEmojiCategory(GoalEmojiCategoryId.Work, "Work"),
        GoalEmojiCategory(GoalEmojiCategoryId.Home, "Home"),
        GoalEmojiCategory(GoalEmojiCategoryId.Health, "Health"),
        GoalEmojiCategory(GoalEmojiCategoryId.Study, "Study"),
        GoalEmojiCategory(GoalEmojiCategoryId.Personal, "Personal"),
    )

    val items = listOf(
        GoalEmojiItem("work_cleaning", "🧹", GoalEmojiCategoryId.Work, "openmoji_work_cleaning", listOf("cleaning", "chores")),
        GoalEmojiItem("work_meeting", "📅", GoalEmojiCategoryId.Work, "openmoji_work_meeting", listOf("meeting", "calendar")),
        GoalEmojiItem("work_focus", "💻", GoalEmojiCategoryId.Work, "openmoji_work_focus", listOf("focus", "coding")),
        GoalEmojiItem("work_finance", "💼", GoalEmojiCategoryId.Work, "openmoji_work_finance", listOf("business", "finance")),
        GoalEmojiItem("work_calls", "📞", GoalEmojiCategoryId.Work, "openmoji_work_calls", listOf("call", "sales")),

        GoalEmojiItem("home_cooking", "🍳", GoalEmojiCategoryId.Home, "openmoji_home_cooking", listOf("cook", "food")),
        GoalEmojiItem("home_groceries", "🛒", GoalEmojiCategoryId.Home, "openmoji_home_groceries", listOf("market", "shopping")),
        GoalEmojiItem("home_laundry", "🧺", GoalEmojiCategoryId.Home, "openmoji_home_laundry", listOf("laundry", "clothes")),
        GoalEmojiItem("home_repair", "🔧", GoalEmojiCategoryId.Home, "openmoji_home_repair", listOf("repair", "tool")),
        GoalEmojiItem("home_plants", "🪴", GoalEmojiCategoryId.Home, "openmoji_home_plants", listOf("plants", "garden")),

        GoalEmojiItem("health_workout", "🏋️", GoalEmojiCategoryId.Health, "openmoji_health_workout", listOf("gym", "fitness")),
        GoalEmojiItem("health_run", "🏃", GoalEmojiCategoryId.Health, "openmoji_health_run", listOf("run", "cardio")),
        GoalEmojiItem("health_sleep", "😴", GoalEmojiCategoryId.Health, "openmoji_health_sleep", listOf("sleep", "rest")),
        GoalEmojiItem("health_water", "💧", GoalEmojiCategoryId.Health, "openmoji_health_water", listOf("water", "hydration")),
        GoalEmojiItem("health_meditate", "🧘", GoalEmojiCategoryId.Health, "openmoji_health_meditate", listOf("meditate", "mind")),

        GoalEmojiItem("study_reading", "📚", GoalEmojiCategoryId.Study, "openmoji_study_reading", listOf("study", "reading")),
        GoalEmojiItem("study_notes", "📝", GoalEmojiCategoryId.Study, "openmoji_study_notes", listOf("notes", "writing")),
        GoalEmojiItem("study_language", "🗣️", GoalEmojiCategoryId.Study, "openmoji_study_language", listOf("language", "speak")),
        GoalEmojiItem("study_laptop", "💡", GoalEmojiCategoryId.Study, "openmoji_study_idea", listOf("idea", "learn")),
        GoalEmojiItem("study_exam", "✅", GoalEmojiCategoryId.Study, "openmoji_study_exam", listOf("exam", "review")),

        GoalEmojiItem("personal_family", "👨‍👩‍👧", GoalEmojiCategoryId.Personal, "openmoji_personal_family", listOf("family", "kids")),
        GoalEmojiItem("personal_friend", "🤝", GoalEmojiCategoryId.Personal, "openmoji_personal_friend", listOf("friend", "social")),
        GoalEmojiItem("personal_hobby", "🎨", GoalEmojiCategoryId.Personal, "openmoji_personal_hobby", listOf("hobby", "art")),
        GoalEmojiItem("personal_music", "🎵", GoalEmojiCategoryId.Personal, "openmoji_personal_music", listOf("music", "fun")),
        GoalEmojiItem("personal_travel", "✈️", GoalEmojiCategoryId.Personal, "openmoji_personal_travel", listOf("travel", "trip")),
    )
}
