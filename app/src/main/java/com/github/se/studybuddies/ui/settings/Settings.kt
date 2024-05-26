package com.github.se.studybuddies.ui.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(backRoute: String, navigationActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("settings_scaffold"),
      topBar = {
        TopNavigationBar(
            title = { Main_title(title = stringResource(R.string.settings)) },
            rightButton = {
              GoBackRouteButton(navigationActions = navigationActions, backRoute = backRoute)
            },
            leftButton = {})
      }) {
        Text("Settings", modifier = Modifier.testTag("settings_text"))
      }
}
