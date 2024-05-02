package com.github.se.studybuddies.navigation

import androidx.navigation.NavHostController

open class NavigationActions(private val navController: NavHostController) {

  open fun navigateTo(route: String) {
    navController.navigate(route) {
      // popUpTo(navController.graph.findStartDestination().id) { saveState = false }
      launchSingleTop = true
      restoreState = true
    }
  }

  open fun goBack() {
    navController.popBackStack()
  }
}
