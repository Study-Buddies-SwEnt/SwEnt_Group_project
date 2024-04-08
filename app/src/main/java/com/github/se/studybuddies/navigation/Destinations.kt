package com.github.se.studybuddies.navigation

import com.github.se.studybuddies.R

data class Destination(val route: String, val icon: Int = 0, val textId: String)

val SETTINGS_DESTINATIONS =
    listOf(
        Destination(route = Route.SETTINGS, icon = R.drawable.settings, textId = "Settings"),
        Destination(route = Route.ACCOUNT, icon = R.drawable.user, textId = "Account"))

val GROUPS_SETTINGS_DESTINATIONS =
    listOf(Destination(route = Route.CREATEGROUP, textId = "Create a group"))

val BOTTOM_NAVIGATION_DESTINATIONS =
    listOf(
        Destination(route = Route.GROUPSHOME, icon = R.drawable.user_v2, textId = "Solo study"),
        Destination(route = Route.CREATEGROUP, icon = R.drawable.groups, textId = "Groups"),
        Destination(route = Route.CREATEGROUP, icon = R.drawable.messages, textId = "Messages"),
        Destination(route = Route.CREATEGROUP, icon = R.drawable.map, textId = "Map"))
