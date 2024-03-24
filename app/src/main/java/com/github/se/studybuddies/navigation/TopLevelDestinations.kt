package com.github.se.studybuddies.navigation

import com.github.se.studybuddies.R

data class TopLevelDestination(val route: String, val icon: Int, val textId: String)

val TOP_LEVEL_DESTINATIONS =
    /*
    listOf(
        TopLevelDestination(route = Route.OVERVIEW, icon = R.drawable.menu, textId = "Overview"),
        TopLevelDestination(route = Route.MAP, icon = R.drawable.globe, textId = "Map"))
     */
    listOf(
        TopLevelDestination(route = Route.SIGNIN, icon = R.drawable.menu, textId = "Sign In"),
        TopLevelDestination(route = Route.HOME, icon = R.drawable.menu, textId = "Home"),
    )
