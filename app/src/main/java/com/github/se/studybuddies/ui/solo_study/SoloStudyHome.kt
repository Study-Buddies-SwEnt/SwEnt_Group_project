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
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.DrawerMenu

@Composable
fun SoloStudyHome(navigationActions: NavigationActions) {
  DrawerMenu(
      navigationActions,
      Route.SOLOSTUDYHOME,
      content = {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceEvenly) {
                    solo_study_buttons(option = FLASH_CARD)
                    solo_study_buttons(option = TODO_LIST)
                  }
              Spacer(modifier = Modifier.height(200.dp))
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceEvenly) {
                    solo_study_buttons(option = FOCUS_MODE)
                    solo_study_buttons(option = TIMER)
                  }
            }
      },
      iconOption = {})
}
