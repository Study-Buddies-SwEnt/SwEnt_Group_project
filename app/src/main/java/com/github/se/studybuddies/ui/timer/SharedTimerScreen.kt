package com.github.se.studybuddies.ui.timer

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.SharedTimerViewModel
import kotlin.text.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SharedTimerScreen(
    navigationActions: NavigationActions,
    sharedTimerViewModel: SharedTimerViewModel,
    groupUID: String
) {
  val timerData by sharedTimerViewModel.timerValue.collectAsState()
  val remainingTime by sharedTimerViewModel.timerEnd.collectAsState()

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("sharedtimer_scaffold"),
      topBar = {
        TopNavigationBar(
            title = { Sub_title(title = "Shared Timer") },
            leftButton = {
              GoBackRouteButton(navigationActions = navigationActions, "${Route.GROUP}/$groupUID")
            },
            rightButton = {})
      },
  ) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("timer_column"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
          Card(
              shape = RoundedCornerShape(30.dp),
              modifier = Modifier.padding(20.dp).fillMaxWidth().height(160.dp),
              colors = CardDefaults.cardColors(containerColor = White)) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Text(
                      text = formatDuration(timerData),
                      fontSize = 40.sp,
                      color = Blue,
                      textAlign = TextAlign.Center)
                }
              }

          Spacer(modifier = Modifier.height(20.dp))

          // Timer control buttons
          Row(
              modifier = Modifier.fillMaxWidth().padding(16.dp),
              horizontalArrangement = Arrangement.SpaceEvenly) {
                TimerButton(
                    onClick = {
                      if (!sharedTimerViewModel.isRunning.value) sharedTimerViewModel.startTimer()
                    },
                    text = "Start")
                TimerButton(onClick = { sharedTimerViewModel.pauseTimer() }, text = "Pause")
                TimerButton(onClick = { sharedTimerViewModel.resetTimer() }, text = "Reset")
              }

          Spacer(modifier = Modifier.height(20.dp).testTag("timer_spacer"))

          // Time adjustment buttons
          Row(
              modifier = Modifier.fillMaxWidth().padding(16.dp),
              horizontalArrangement = Arrangement.SpaceAround) {
                TimeAdjustSection("Hours", 1, sharedTimerViewModel::addHours)
                TimeAdjustSection("Minutes", 1, sharedTimerViewModel::addMinutes)
                TimeAdjustSection("Seconds", 1, sharedTimerViewModel::addSeconds)
              }
        }
  }
}

fun formatDuration(millis: Long?): String {
  val hours = millis?.div(3600000)
  val minutes = (millis?.rem(3600000))?.div(60000)
  val seconds = (millis?.rem(60000))?.div(1000)
  return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

@Composable
fun TimeAdjustButton(label: String, amount: Long, onAdjust: (Long) -> Unit) {
  Button(onClick = { onAdjust(amount) }) { Text(label) }
}
