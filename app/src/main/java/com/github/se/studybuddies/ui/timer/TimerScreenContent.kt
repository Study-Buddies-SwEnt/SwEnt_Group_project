package com.github.se.studybuddies.ui.timer

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.studybuddies.viewModels.TimerViewModel

@Composable
fun TimerScreenContent(timerViewModel: TimerViewModel) {
    val timerValue by timerViewModel.timer.collectAsState()

    TimerScreen(
        timerValue = timerValue,
        onAddHours = { hours: Long -> timerViewModel.addHours(hours) },
        onAddMinutes = { minutes: Long -> timerViewModel.addMinutes(minutes) },
        onAddSeconds = timerViewModel::addSeconds,
        onStart = { timerViewModel.startTimer() },
        onPause = { timerViewModel.pauseTimer() },
        onReset = { timerViewModel.resetTimer() }
    )
}

@Composable
fun TimerScreen(
    timerValue: Long,
    onAddHours: (Long) -> Unit,
    onAddMinutes: (Long) -> Unit,
    onAddSeconds: (Long) -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = timerValue.formatTime(), fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onStart) { Text("Start") }
            Button(onClick = onPause) { Text("Pause") }
            Button(onClick = onReset) { Text("Reset") }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TimeAdjustSection("Hours", 1, onAddHours)
            TimeAdjustSection("Minutes", 1, onAddMinutes)
            TimeAdjustSection("Seconds", 10, onAddSeconds)
        }
    }
}

@Composable
fun TimeAdjustSection(label: String, amount: Long, onAdjust: (Long) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label)
        Row {
            Button(onClick = { onAdjust(amount) }) {
                Text("+")
            }
            Button(onClick = { onAdjust(-amount) }) {
                Text("-")
            }
        }
    }
}

fun Long.formatTime(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

@Preview(showBackground = true)
@Composable
fun PreviewTimerScreen() {
    val timerViewModel = TimerViewModel()
    TimerScreenContent(timerViewModel)
}