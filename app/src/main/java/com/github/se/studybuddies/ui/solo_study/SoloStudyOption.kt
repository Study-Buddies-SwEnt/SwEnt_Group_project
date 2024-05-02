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

data class SoloStudyOption(val name: String, val icon_id: Int, val route: String) {}

class SoloStudyOptionList(private val SoloStudyOptions: List<SoloStudyOption>) {
  fun getAllOptions(): List<SoloStudyOption> {
    return SoloStudyOptions
  }
}

val FLASH_CARD = SoloStudyOption("Flash Card", R.drawable.flash_cards, Route.TIMER)
val TODO_LIST = SoloStudyOption("ToDo List", R.drawable.to_do_list, Route.TIMER)
val FOCUS_MODE = SoloStudyOption("Focus Mode", R.drawable.flash_cards, Route.TIMER)
val TIMER = SoloStudyOption("Timer", R.drawable.timer, Route.TIMER)
val CALENDAR = SoloStudyOption("Calendar", R.drawable.calendar, Route.TIMER)

@Composable
fun Solo_study_buttons(navigationActions: NavigationActions, option: SoloStudyOption) {
  Button(
      onClick = { navigationActions.navigateTo(option.route) },
      modifier = Modifier.height(120.dp).width(160.dp).testTag("solo_study_button"),
      colors = ButtonDefaults.buttonColors(White),
      shape = RoundedCornerShape(12.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
              painter = painterResource(id = option.icon_id),
              contentDescription = option.name,
              modifier = Modifier.height(80.dp).width(100.dp).testTag("solo_study_icon"))
          Text(
              option.name,
              color = Blue,
              fontSize = 20.sp,
              modifier = Modifier.testTag("solo_study_button_text"))
        }
      }
}
