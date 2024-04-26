package com.github.se.studybuddies.ui.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.viewModels.SharedTimerViewModel
@Composable
fun TimerScreen(groupId: String, viewModel: SharedTimerViewModel) {
    val timerData = viewModel.timerData.observeAsState(initial = emptyMap())
    val timerInfo = timerData.value[groupId] ?: TimerInfo()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Timer for Group $groupId") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TimerDisplay(timerInfo = timerInfo)
            Spacer(Modifier.height(20.dp))
            TimeAdjustmentButtons(groupId, viewModel)
            Spacer(Modifier.height(20.dp))
            TimerControlButtons(groupId, viewModel)
        }
    }
}
@Composable
fun TimerDisplay(timerInfo: TimerInfo) {
    Text(
        text = "Current Time: ${timerInfo.duration.formateTime()}",
        style = MaterialTheme.typography.h4,
        modifier = Modifier.padding(8.dp)
    )
    Text(
        text = if (timerInfo.isActive) "Timer is running" else "Timer is paused",
        style = MaterialTheme.typography.body1,
        color = if (timerInfo.isActive) Color.Green else Color.Red,
        modifier = Modifier.padding(8.dp)
    )
}
@Composable
fun TimeAdjustmentButtons(groupId: String, viewModel: SharedTimerViewModel) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        TimeAdjustButton("+1 Hour", 3600000, groupId, viewModel)
        TimeAdjustButton("+1 Min", 60000, groupId, viewModel)
        TimeAdjustButton("+10 Sec", 10000, groupId, viewModel)
        TimeAdjustButton("-1 Hour", -3600000, groupId, viewModel)
        TimeAdjustButton("-1 Min", -60000, groupId, viewModel)
        TimeAdjustButton("-10 Sec", -10000, groupId, viewModel)
    }
}

@Composable
fun TimeAdjustButton(label: String, adjustment: Long, groupId: String, viewModel: SharedTimerViewModel) {
    Button(
        onClick = { viewModel.adjustTime(groupId, adjustment) },
        modifier = Modifier.padding(4.dp)
    ) {
        Text(label)
    }
}
@Composable
fun TimerControlButtons(groupId: String, viewModel: SharedTimerViewModel) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = { viewModel.startTimer(groupId) },
            modifier = Modifier.padding(4.dp)
        ) {
            Text("Start")
        }
        Button(
            onClick = { viewModel.pauseTimer(groupId) },
            modifier = Modifier.padding(4.dp)
        ) {
            Text("Pause")
        }
        Button(
            onClick = { viewModel.resetTimer(groupId) },
            modifier = Modifier.padding(4.dp)
        ) {
            Text("Reset")
        }
    }
}
fun Long.formateTime(): String {
    val hours = this / 3600000
    val minutes = (this % 3600000) / 60000
    val seconds = (this % 60000) / 1000
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}

