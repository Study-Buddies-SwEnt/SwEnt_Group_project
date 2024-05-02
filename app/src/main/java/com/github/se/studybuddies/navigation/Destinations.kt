package com.github.se.studybuddies.navigation

import com.github.se.studybuddies.R

data class Destination(val route: String, val icon: Int = 0, val textId: String)

val SETTINGS_DESTINATIONS =
    listOf(
        Destination(route = Route.SETTINGS, icon = R.drawable.settings, textId = "Settings"),
        Destination(route = Route.ACCOUNT, icon = R.drawable.user, textId = "Account"),
    )

val GROUPS_SETTINGS_DESTINATIONS =
    listOf(Destination(route = Route.GROUP, textId = "Modify a group"))

val BOTTOM_NAVIGATION_DESTINATIONS =
    // 4rth route is a placeholder
    listOf(
        Destination(
            route = Route.SOLOSTUDYHOME,
            icon = R.drawable.user_v2,
            textId = Resources.getSystem().getString(R.string.solo_study)),
        Destination(
            route = Route.GROUPSHOME,
            icon = R.drawable.groups,
            textId = Resources.getSystem().getString(R.string.groups)),
        Destination(
            route = Route.CHAT,
            icon = R.drawable.messages,
            textId = Resources.getSystem().getString(R.string.messages)),
        Destination(
            route = Route.TODOLIST,
            icon = R.drawable.map,
            textId = Resources.getSystem().getString(R.string.map)))
