package com.github.se.studybuddies.navigation

import com.github.se.studybuddies.R

data class Destination(val route: String, val icon: Int = 0, val textId: String)

val SETTINGS_DESTINATIONS =
    listOf(
        Destination(route = Route.SETTINGS, icon = R.drawable.settings, textId = "Settings"),
        Destination(route = Route.ACCOUNT, icon = R.drawable.user, textId = "Account"))

val GROUPS_SETTINGS_DESTINATIONS =
    listOf(
        Destination(route = Route.GROUPSETTING, textId = "Modify group"),
        Destination(route = Route.GROUPMEMBERS, textId = "Members"),
        Destination(route = Route.LEAVEGROUP, textId = "Leave group"),
        Destination(route = Route.DELETEGROUP, textId = "Delete group"))

val BOTTOM_NAVIGATION_DESTINATIONS =
    listOf(
        Destination(route = Route.SOLOSTUDYHOME, icon = R.drawable.user_v2, textId = "Solo study"),
        Destination(route = Route.GROUPSHOME, icon = R.drawable.groups, textId = "Groups"),
        Destination(route = Route.DIRECT_MESSAGE, icon = R.drawable.messages, textId = "Messages"),
        Destination(route = Route.MAP, icon = R.drawable.map, textId = "Map"))

val GROUPS_BOTTOM_NAVIGATION_DESTINATIONS =
    listOf(
        Destination(route = Route.CALLLOBBY, icon = R.drawable.video_call, textId = "Video Call"),
        Destination(
            route = "${Route.SHAREDTIMER}/{groupUID}",
            icon = R.drawable.messages,
            textId = "Timer"))
