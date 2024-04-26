package com.github.se.studybuddies.ui
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChannelScreen() {
    var channelName by remember { mutableStateOf("") }
    var channelDescription by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Channels") }) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Input fields for channel name and description
            OutlinedTextField(
                value = channelName,
                onValueChange = { channelName = it },
                label = { Text("Channel Name") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = channelDescription,
                onValueChange = { channelDescription = it },
                label = { Text("Channel Description") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Button to create channel
            Button(onClick = { /* Implement logic to create channel */ }) {
                Text("Create Channel")
            }
        }
    }
}