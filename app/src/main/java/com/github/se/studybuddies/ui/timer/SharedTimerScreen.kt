
package com.github.se.studybuddies.ui.timer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.GoBackRouteButton
import com.github.se.studybuddies.ui.TopNavigationBar
import com.github.se.studybuddies.viewModels.SharedTimerViewModel
import androidx.compose.runtime.livedata.observeAsState


@Composable
fun SharedTimerScreen(
    navigationActions: NavigationActions,
    sharedTimerViewModel: SharedTimerViewModel
) {
    val timerInfo by sharedTimerViewModel.timerInfo.observeAsState(SharedTimerViewModel.TimerInfo())

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("sharedtimer_scaffold"),
        topBar = {
            TopNavigationBar(
                title = { Text("Timer") },
                navigationIcon = {
                    GoBackRouteButton(navigationActions = navigationActions, Route.GROUPSHOME)
                },
                actions = {})


        }
    ) {contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier.padding(20.dp),
                colors = CardDefaults.cardColors(containerColor = if (!timerInfo.isActive) Color.Red else Color.White)
            ) {
                Text(
                    text = timerInfo.elapsedTime.formatTime(),
                    fontSize = if (!timerInfo.isActive) 80.sp else 40.sp,
                    color = Color.Blue,
                    modifier = Modifier.padding(10.dp),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(modifier = Modifier.testTag("Pause"),
                    onClick = { sharedTimerViewModel.pauseTimer() }) { Text("Pause") }
                Button(modifier = Modifier.testTag("Start"),
                    onClick = {
                    if (timerInfo.elapsedTime > 0) sharedTimerViewModel.startTimer() else sharedTimerViewModel.resetTimer()
                }) { Text(if (timerInfo.elapsedTime > 0) "Start" else "Reset") }
                Button(modifier = Modifier.height(20.dp).testTag("Reset"),
                    onClick = { sharedTimerViewModel.resetTimer() }) { Text("Reset")
                }

            }
            Row(
                Modifier.fillMaxWidth().testTag("timer_adjustment"),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                TimeAdjustButton("Hours", 1, { sharedTimerViewModel.addHours(it) })
                TimeAdjustButton("Minutes", 1, { sharedTimerViewModel.addMinutes(it) })
                TimeAdjustButton("Seconds", 10, { sharedTimerViewModel.addSeconds(it) })
            }
        }
    }


}
@Composable
fun TimeAdjustButton(label: String, amount: Long, onAdjust: (Long) -> Unit) {
    Button(onClick = { onAdjust(amount) }) {
        Text("+ $label")
    }
    Button(onClick = { onAdjust(-amount) }) {
        Text("- $label")
    }
}
