package com.github.se.studybuddies.ui.timer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.GoBackRouteButton
import com.github.se.studybuddies.ui.Sub_title
import com.github.se.studybuddies.ui.TopNavigationBar
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.TimerViewModel

@Composable
fun TimerScreenContent(timerViewModel: TimerViewModel,
                       navigationActions: NavigationActions) {
  val timerValue by timerViewModel.timer.collectAsState()
val  timerEnd by timerViewModel.timerEnd.collectAsState()

  TimerScreen(
      navigationActions = navigationActions,
      timerValue = timerValue,
      timerEnd= timerEnd,
      onAddHours = { hours: Long -> timerViewModel.addHours(hours) },
      onAddMinutes = { minutes: Long -> timerViewModel.addMinutes(minutes) },
      onAddSeconds = timerViewModel::addSeconds,
      onStart = { timerViewModel.startTimer() },
      onPause = { timerViewModel.pauseTimer() },
      onReset = { timerViewModel.resetTimer() })
}

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
  Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.CenterHorizontally) {
      TopNavigationBar(
          title = { Sub_title(title = "Timer") },
          navigationIcon = { GoBackRouteButton(navigationActions = navigationActions, Route.SOLOSTUDYHOME) }) {
      }
      Spacer(modifier = Modifier.height(120.dp))
      if(timerEnd){
          Card(
              shape = RoundedCornerShape(30.dp),
              modifier = Modifier
                  .padding(20.dp)
                  .width(400.dp)
                  .height(160.dp),
              colors = CardDefaults.cardColors(
                  containerColor = Color.Red)
          ) {
              Text(text = timerValue.formatTime(), fontSize = 80.sp,color = Blue, modifier = Modifier.padding(20.dp))
          }
      }else {
          Card(
              shape = RoundedCornerShape(30.dp),
              modifier = Modifier
                  .padding(20.dp)
                  .width(400.dp)
                  .height(160.dp),
              colors = CardDefaults.cardColors(
                  containerColor = White
              )
          ) {
              Text(
                  text = timerValue.formatTime(),
                  fontSize = 80.sp,
                  color = Blue,
                  modifier = Modifier.padding(20.dp)
              )
          }
      }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly) {
            TimerButton(onClick = onPause,"Pause")
            TimerButton(onClick =
            if(timerValue > 0){
                onStart
            } else {
                onReset
            },"Start")
            TimerButton(onClick = onReset, "Reset")
            }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly) {
              TimeAdjustSection("Hours", 1, onAddHours)
              TimeAdjustSection("Minutes", 1, onAddMinutes)
              TimeAdjustSection("Seconds", 10, onAddSeconds)
            }
      }
}

@Composable
fun TimeAdjustSection(label: String, amount: Long, onAdjust: (Long) -> Unit) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(text = label, fontSize = 25.sp)
    Row {
        OutlinedButton(onClick = { onAdjust(amount) },
            colors = ButtonDefaults.buttonColors(Blue),
            shape = RoundedCornerShape(0.dp),
            border = BorderStroke(1.dp, White),
            modifier = Modifier
                .padding(0.dp)
                .width(120.dp)
                .height(80.dp)){Text( "+", fontSize = 30.sp,color = White)}
        OutlinedButton(onClick = { onAdjust(-amount) },
            colors = ButtonDefaults.buttonColors(Blue),
            shape = RoundedCornerShape(0.dp),
            border = BorderStroke(1.dp, White),
            modifier = Modifier
                .padding(0.dp)
                .width(120.dp)
                .height(80.dp)){Text("-", fontSize = 30.sp,color= White)}
    }
  }
}

@Composable
fun TimerButton(
    onClick: () -> Unit,
    text: String
) {
  Button(
      onClick = onClick,
      modifier = Modifier
          .padding(0.dp)
          .width(150.dp)
          .height(60.dp),
      colors = ButtonDefaults.buttonColors(Blue),

      ) {
    Text(text,color = White, fontSize = 20.sp)
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
