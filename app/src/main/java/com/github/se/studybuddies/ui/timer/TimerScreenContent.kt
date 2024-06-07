@file:Suppress("UNUSED_EXPRESSION")

package com.github.se.studybuddies.ui.timer

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.TimerViewModel

@SuppressLint("SuspiciousIndentation")
@Composable
fun TimerScreenContent(timerViewModel: TimerViewModel, navigationActions: NavigationActions) {
  val timerValue = timerViewModel.timerValue.collectAsState()
  val timerEnd by timerViewModel.timerEnd.collectAsState()
  timerValue?.let {
    timerEnd?.let { it1 ->
      TimerScreen(
          navigationActions = navigationActions,
          timerValue = it.value,
          timerEnd = it1,
          onAddHours = { hours: Long -> timerViewModel.addHours(hours) },
          onAddMinutes = { minutes: Long -> timerViewModel.addMinutes(minutes) },
          onAddSeconds = timerViewModel::addSeconds,
          onStart = { timerViewModel.startTimer() },
          onPause = { timerViewModel.pauseTimer() },
          onReset = { timerViewModel.resetTimer() })
    }
  }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    navigationActions: NavigationActions,
    timerValue: Long,
    timerEnd: Boolean,
    onAddHours: (Long) -> Unit,
    onAddMinutes: (Long) -> Unit,
    onAddSeconds: (Long) -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit
) {
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("timer_scaffold"),
      topBar = {
        TopNavigationBar(
            title = { Sub_title(title = "Timer") },
            rightButton = { GoBackRouteButton(navigationActions = navigationActions) },
            leftButton = {})
      },
  ) {
    Column(
        modifier = Modifier.fillMaxSize().testTag("timer_column"),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally) {
          Spacer(modifier = Modifier.height(120.dp))
          if (timerEnd) {
            Card(
                shape = RoundedCornerShape(30.dp),
                modifier =
                    Modifier.padding(20.dp).width(300.dp).height(100.dp).testTag("timer_red_card"),
                colors = CardDefaults.cardColors(containerColor = Color.Red)) {
                  Text(
                      text = (timerValue / 1000).formatTime(),
                      fontSize = 50.sp,
                      color = Blue,
                      modifier = Modifier.padding(10.dp))
                }
          } else {
            Card(
                shape = RoundedCornerShape(30.dp),
                modifier =
                    Modifier.padding(30.dp).width(220.dp).height(80.dp).testTag("timer_card"),
                colors = CardDefaults.cardColors(containerColor = White)) {
                  Text(
                      text = (timerValue / 1000).formatTime(),
                      fontSize = 40.sp,
                      color = Blue,
                      textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                      modifier = Modifier.padding(10.dp))
                }
          }
          Spacer(modifier = Modifier.height(16.dp).testTag("timer_spacer"))
          Row(
              modifier = Modifier.fillMaxWidth().padding(16.dp),
              horizontalArrangement = Arrangement.SpaceEvenly) {
                TimerButton(
                    onClick =
                        if (timerValue > 0) {
                          onStart
                        } else {
                          onReset
                        },
                    "Start")
                TimerButton(onClick = onPause, "Pause")
                TimerButton(onClick = onReset, "Reset")
              }
          Row(
              modifier = Modifier.fillMaxWidth().padding(16.dp),
              horizontalArrangement = Arrangement.SpaceAround) {
                TimeAdjustSection("Hours", 1, onAddHours)
                TimeAdjustSection("Minutes", 1, onAddMinutes)
                TimeAdjustSection("Seconds", 1, onAddSeconds)
              }
        }
  }
}

@Composable
fun TimeAdjustSection(label: String, amount: Long, onAdjust: (Long) -> Unit) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.testTag("timer_adjustment")) {
        Text(text = label, fontSize = 20.sp)
        Column {
          TimerAdjustButton(label = "+$label", amount, onAdjust)
          TimerAdjustButton(label = "-$label", -amount, onAdjust)
        }
      }
}

@Composable
fun TimerAdjustButton(label: String, amount: Long, onAdjust: (Long) -> Unit) {
  OutlinedButton(
      onClick = { onAdjust(amount) },
      colors = ButtonDefaults.buttonColors(Blue),
      shape = RoundedCornerShape(0.dp),
      border = BorderStroke(1.dp, White),
      modifier = Modifier.padding(0.dp).width(60.dp).height(40.dp).testTag(label + "_button")) {
        Text(label[0].toString(), fontSize = 15.sp, color = White)
      }
}

@Composable
fun TimerButton(onClick: () -> Unit, text: String) {
  Button(
      onClick = onClick,
      modifier = Modifier.padding(0.dp).width(100.dp).height(50.dp).testTag(text + "_timer_button"),
      colors = ButtonDefaults.buttonColors(Blue),
  ) {
    Text(text, color = White, fontSize = 15.sp)
  }
}

fun Long.formatTime(): String {
  val hours = this / 3600
  val minutes = (this % 3600) / 60
  val seconds = this % 60
  return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

/*
@Preview(showBackground = true)
@Composable
fun PreviewTimerScreen() {
  val timerViewModel = TimerViewModel()
  TimerScreenContent(timerViewModel)
}*/
