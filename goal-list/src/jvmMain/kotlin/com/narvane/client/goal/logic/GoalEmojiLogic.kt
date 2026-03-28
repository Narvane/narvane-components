package com.narvane.client.goal.logic

import com.narvane.client.goal.model.GoalEmojiCategoryId
import com.narvane.client.goal.model.GoalEmojiCatalog
import com.narvane.client.goal.model.GoalEmojiItem

private const val GOAL_EMOJI_RECENTS_LIMIT = 12

fun appendRecentEmoji(
    recents: List<String>,
    emojiId: String,
): List<String> {
    return (listOf(emojiId) + recents.filterNot { it == emojiId })
        .take(GOAL_EMOJI_RECENTS_LIMIT)
}

fun emojisForCategory(
    categoryId: GoalEmojiCategoryId,
    recentIds: List<String>,
): List<GoalEmojiItem> {
    return if (categoryId == GoalEmojiCategoryId.Recent) {
        recentIds.mapNotNull(::emojiById)
    } else {
        GoalEmojiCatalog.items.filter { it.categoryId == categoryId }
    }
}

fun emojiById(id: String): GoalEmojiItem? {
    return GoalEmojiCatalog.items.firstOrNull { it.id == id }
}

fun normalizeSelectedEmoji(value: String): String {
    return value.trim()
}
