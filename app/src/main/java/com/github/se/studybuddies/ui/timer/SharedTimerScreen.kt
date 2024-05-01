
package com.github.se.studybuddies.ui.timer

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.github.se.studybuddies.ui.Sub_title
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.White

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

@Composable
fun SharedTimerScreen(
    navigationActions: NavigationActions,
    sharedTimerViewModel: SharedTimerViewModel
) {
    val timerInfo by sharedTimerViewModel.timerInfo.observeAsState(SharedTimerViewModel.TimerInfo())


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("sharedtimer_scaffold"),
        topBar = {
            TopNavigationBar(
                title = { Sub_title(title = "Timer") },
                navigationIcon = {
                    GoBackRouteButton(navigationActions = navigationActions, Route.SOLOSTUDYHOME)
                },
                actions = {})
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .height(160.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = timerInfo.elapsedTime.formatTime(),
                        fontSize = 40.sp,
                        color = Color.Blue,
                        textAlign = TextAlign.Center
                    )
                    if (timerInfo.isActive) {
                        Text("Timer is active", color = Color.Green, textAlign = TextAlign.Center)
                    } else {
                        Text("Timer is paused", color = Color.Red, textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Timer control buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TimerButton(onClick = { sharedTimerViewModel.startTimer() }, text = "Start" )
                TimerButton(onClick = { sharedTimerViewModel.pauseTimer() }, text = "Pause" )
                TimerButton(onClick = {  sharedTimerViewModel.resetTimer()  }, text = "Reset" )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Time adjustment buttons
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround) {
                TimeAdjustSection("Hours", 1, sharedTimerViewModel::addHours)
                TimeAdjustSection("Minutes", 1, sharedTimerViewModel::addMinutes)
                TimeAdjustSection("Seconds", 10, sharedTimerViewModel::addSeconds)
            }
        }
    }
}

@Composable
fun TimeAdjustButton(label: String, amount: Long, onAdjust: (Long) -> Unit) {
    Button(onClick = { onAdjust(amount) }) {
        Text(label)
    }
}