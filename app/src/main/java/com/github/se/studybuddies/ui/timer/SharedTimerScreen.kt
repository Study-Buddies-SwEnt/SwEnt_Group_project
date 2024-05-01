
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
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.White


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
                title = { Text("Group Shared Timer") },
                navigationIcon = {
                    IconButton(onClick = { navigationActions.navigateTo(Route.GROUPSHOME) }) { // Adjust as per your navigation implementation
                        Icon(Icons.Default.ArrowBack, contentDescription = "Go Back")
                    }
                }
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { sharedTimerViewModel.startTimer() }) {
                    Text("Start")
                }
                Button(onClick = { sharedTimerViewModel.pauseTimer() }) {
                    Text("Pause")
                }
                Button(onClick = { sharedTimerViewModel.resetTimer() }) {
                    Text("Reset")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Time adjustment buttons
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TimeAdjustButton("Add 1 Hour", 1, sharedTimerViewModel::addHours)
                TimeAdjustButton("Add 1 Minute", 1, sharedTimerViewModel::addMinutes)
                TimeAdjustButton("Add 10 Seconds", 10, sharedTimerViewModel::addSeconds)
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
