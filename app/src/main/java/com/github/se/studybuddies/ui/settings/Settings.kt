package com.github.se.studybuddies.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.ui.SecondaryTopBar

@Composable
fun Settings(backRoute: String, navigationActions: NavigationActions) {
    Column {
        SecondaryTopBar {
            navigationActions.navigateTo(backRoute)
        }
        Text("Settings")
    }
}