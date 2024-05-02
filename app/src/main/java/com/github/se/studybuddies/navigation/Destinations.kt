package com.github.se.studybuddies.navigation

import android.content.Context
import com.github.se.studybuddies.R

data class Destination(val route: String, val icon: Int = 0, val textId: Int)

fun getLocalizedText(context: Context, resourceId: Int): String {
  return context.getString(resourceId)
}

val SETTINGS_DESTINATIONS =
    listOf(
        Destination(route = Route.SETTINGS, icon = R.drawable.settings, textId = R.string.settings),
        Destination(route = Route.ACCOUNT, icon = R.drawable.user, textId = R.string.account))

val GROUPS_SETTINGS_DESTINATIONS =
    listOf(Destination(route = Route.GROUP, textId = R.string.modify_a_group))

val BOTTOM_NAVIGATION_DESTINATIONS =
    listOf(
        Destination(
            route = Route.SOLOSTUDYHOME, icon = R.drawable.user_v2, textId = R.string.solo_study),
        Destination(route = Route.GROUPSHOME, icon = R.drawable.groups, textId = R.string.groups),
        Destination(route = Route.CHAT, icon = R.drawable.messages, textId = R.string.messages),
        Destination(route = Route.TODOLIST, icon = R.drawable.map, textId = R.string.map))
