package com.github.se.studybuddies.ui.solo_study

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.shared_elements.MainScreenScaffold

/**
 * Solo study home screen.
 *
 * @param navigationActions Navigation actions to allow user to navigate to other screens.
 */
@Composable
fun SoloStudyHome(navigationActions: NavigationActions) {
  MainScreenScaffold(
      navigationActions,
      Route.SOLOSTUDYHOME,
      content = {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("solo_study_home"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Row(
                  modifier = Modifier
                      .fillMaxWidth()
                      .testTag("solo_study_row1"),
                  horizontalArrangement = Arrangement.SpaceEvenly) {
                    Solo_study_buttons(navigationActions, option = FLASH_CARD)
                    Solo_study_buttons(navigationActions, option = TODO_LIST)
                  }

              Spacer(modifier = Modifier.height(100.dp))
              Row(
                  modifier = Modifier
                      .fillMaxWidth()
                      .testTag("solo_study_row2"),
                  horizontalArrangement = Arrangement.SpaceEvenly) {
                    Solo_study_buttons(navigationActions, option = CALENDAR)
                    Solo_study_buttons(navigationActions, option = TIMER)
                  }
            }
      },
      title = stringResource(id = R.string.solo_study),
      iconOptions = {})
}
