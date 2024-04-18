package com.github.se.studybuddies.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.ui.GoBackRouteButton
import com.github.se.studybuddies.ui.Main_title
import com.github.se.studybuddies.ui.TopNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(backRoute: String, navigationActions: NavigationActions) {
  Column {
    TopNavigationBar(title = { Main_title(title = "Settings") },
        navigationIcon = { GoBackRouteButton(navigationActions = navigationActions, backRoute = backRoute) }) {

    }
    Text("Settings")
  }
}
