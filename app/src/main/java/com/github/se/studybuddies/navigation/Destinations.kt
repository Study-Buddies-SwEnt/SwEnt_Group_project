package com.github.se.studybuddies.navigation

import com.github.se.studybuddies.R

data class Destination(val route: String, val icon: Int = 0, val textId: String)

val SETTINGS_DESTINATIONS =
    listOf(
        Destination(route = Route.SETTINGS, icon = R.drawable.settings, textId = "Settings"),
        Destination(route = Route.ACCOUNT, icon = R.drawable.user, textId = "Account")
    )

val GROUPS_SETTINGS_DESTINATIONS = listOf(
    Destination(route = Route.CREATEGROUP, textId = "Create a group")
)