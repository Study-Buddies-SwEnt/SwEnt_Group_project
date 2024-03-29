package com.github.se.studybuddies.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

class NavigationActions(private val navController: NavHostController) {

    fun navigateTo(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = false }
            launchSingleTop = true
            restoreState = true
        }
    }
}
