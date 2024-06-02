package com.github.se.studybuddies.ui.solo_study

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.White

/**
 * Data class to represent the options available for solo study.
 * @param name: The name of the option.
 * @param iconId: The id of the icon for the option.
 * @param route: The route to navigate to when the option is selected.
 */
data class SoloStudyOption(val name: String, val iconId: Int, val route: String)

val FLASH_CARD = SoloStudyOption("Flash Card", R.drawable.flash_cards, Route.PLACEHOLDER)
val TODO_LIST = SoloStudyOption("ToDo List", R.drawable.to_do_list, Route.TODOLIST)
val TIMER = SoloStudyOption("Timer", R.drawable.timer, Route.TIMER)

val CALENDAR = SoloStudyOption("Calendar", R.drawable.calendar, Route.CALENDAR)

/**
 * Composable function to display the buttons for the solo study options.
 */
@Composable
fun Solo_study_buttons(navigationActions: NavigationActions, option: SoloStudyOption) {
  Button(
      onClick = { navigationActions.navigateTo(option.route) },
      modifier = Modifier.height(120.dp).width(160.dp).testTag(option.name + "_button"),
      colors = ButtonDefaults.buttonColors(White),
      shape = RoundedCornerShape(12.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.testTag(option.name + "_column")) {
              Icon(
                  painter = painterResource(id = option.iconId),
                  contentDescription = option.name,
                  tint = Blue,
                  modifier = Modifier.height(80.dp).width(100.dp).testTag(option.name + "_icon"))
              Text(
                  option.name,
                  color = Blue,
                  fontSize = 20.sp,
                  modifier = Modifier.testTag(option.name + "_text"))
            }
      }
}
