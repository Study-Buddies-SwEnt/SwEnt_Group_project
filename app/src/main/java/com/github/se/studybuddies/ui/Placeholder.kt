package com.github.se.studybuddies.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.shared_elements.MainScreenScaffold

@Composable
fun Placeholder(navigationActions: NavigationActions) {
  MainScreenScaffold(
      navigationActions,
      Route.PLACEHOLDER,
      content = {
        Column(
            modifier = Modifier.fillMaxSize().testTag("GroupsHome"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
        ) {
          Spacer(modifier = Modifier.height(300.dp))
          Text(stringResource(R.string.feature_not_implemented_yet), textAlign = TextAlign.Center)
        }
      },
      title = "",
      iconOptions = {})
}
