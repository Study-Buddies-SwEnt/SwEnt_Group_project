import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.studybuddies.ui.Timer.TimeViewModel

@Composable
fun TimerSetupScreen(viewModel: TimeViewModel = viewModel()) {
    var hours by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("") }
    var seconds by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = hours,
            onValueChange = { hours = it },
            label = { Text("Hours") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = minutes,
            onValueChange = { minutes = it },
            label = { Text("Minutes") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = seconds,
            onValueChange = { seconds = it },
            label = { Text("Seconds ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Button(onClick = {
            viewModel.setTimer(
                hours.toIntOrNull() ?: 0,
                minutes.toIntOrNull() ?: 0,
                seconds.toIntOrNull() ?: 0
            )
        }) {
            Text("Set Timer")
        }
        Button(onClick = { viewModel.startTimer() }) {
            Text("Start")
        }
        Button(onClick = { viewModel.pauseTimer() }) {
            Text("Pause")
        }
        Button(onClick = { viewModel.stopTimer() }) {
            Text("Stop")
        }
        val timeLeftInSeconds by viewModel.timeLeft.observeAsState(0L)
        Text(text = "Time Left: ${formatTime(timeLeftInSeconds)}", style = MaterialTheme.typography.h4)
    }
}

fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}
