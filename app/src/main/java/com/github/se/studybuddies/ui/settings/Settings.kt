package com.github.se.studybuddies.ui.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
import com.github.se.studybuddies.ui.shared_elements.Main_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar

/**
 * This composable is used to display the settings
 *
 * @param backRoute The route to go back to.
 * @param navigationActions The navigation actions.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Settings(backRoute: String, navigationActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("settings_scaffold"),
      topBar = {
        TopNavigationBar(
            title = { Main_title(title = stringResource(R.string.settings)) },
            navigationIcon = {
              GoBackRouteButton(navigationActions = navigationActions, backRoute = backRoute)
            },
            actions = {})
      }) {
        Text("Settings", modifier = Modifier.testTag("settings_text"))
      }
}
