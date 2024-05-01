
package com.github.se.studybuddies.ui.timer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedTimerScreen(
    navigationActions: NavigationActions,
    sharedTimerViewModel: SharedTimerViewModel
) {
    val timerInfo by sharedTimerViewModel.timerInfo.observeAsState(SharedTimerViewModel.TimerInfo())

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("sharedtimer_scaffold"),
        topBar = {
            TopAppBar(
                title = { Text("Shared Timer") },
                navigationIcon = {
                    IconButton(onClick = { navigationActions.navigateTo(Route.GROUPSHOME) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Go Back")
                    }
                },
                actions = {}
            )
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding) // Apply the padding provided by Scaffold here
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .padding(20.dp)
                    .width(if (timerInfo.isActive) 220.dp else 300.dp)
                    .height(if (timerInfo.isActive) 80.dp else 160.dp)
                    .testTag(if (timerInfo.isActive) "timer_card" else "timer_red_card"),
                colors = CardDefaults.cardColors(containerColor = if (timerInfo.isActive) Color.White else Color.Red)
            ) {
                Text(
                    text = timerInfo.elapsedTime.formatTime(),
                    fontSize = if (timerInfo.isActive) 40.sp else 80.sp,
                    color = Color.Blue,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp).testTag("timer_spacer"))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { sharedTimerViewModel.pauseTimer() },
                    modifier = Modifier.testTag("Pause")
                ) { Text("Pause") }
                Button(
                    onClick = {
                        if (timerInfo.elapsedTime > 0) sharedTimerViewModel.startTimer() else sharedTimerViewModel.resetTimer()
                    },
                    modifier = Modifier.testTag("Start/Reset")
                ) { Text(if (timerInfo.elapsedTime > 0) "Start" else "Reset") }
            }
            Row(
                Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TimeAdjustButton("Hours", 1, sharedTimerViewModel::addHours)
                TimeAdjustButton("Minutes", 1, sharedTimerViewModel::addMinutes)
                TimeAdjustButton("Seconds", 10, sharedTimerViewModel::addSeconds)
            }
        }
    }
}

@Composable
fun TimeAdjustButton(label: String, amount: Long, onAdjust: (Long) -> Unit) {
    Row {
        Button(onClick = { onAdjust(amount) }) {
            Text("+ $label")
        }
        Button(onClick = { onAdjust(-amount) }) {
            Text("- $label")
        }
    }
}
