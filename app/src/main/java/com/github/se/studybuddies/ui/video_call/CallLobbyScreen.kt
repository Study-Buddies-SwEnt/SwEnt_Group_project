package com.github.se.studybuddies.ui.video_call

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
import com.github.se.studybuddies.viewModels.CallLobbyViewModel
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.call.lobby.CallLobby
import io.getstream.video.android.core.call.state.ToggleCamera
import io.getstream.video.android.core.call.state.ToggleMicrophone

@Composable
fun CallLobbyScreen(
    groupUID: String,
    callLobbyViewModel: CallLobbyViewModel,
    navigationActions: NavigationActions
) {
  LockScreenOrientation(orientation = Configuration.ORIENTATION_PORTRAIT)
  val call by remember { mutableStateOf(callLobbyViewModel.call) }
  val isCameraEnabled by call.camera.isEnabled.collectAsState()
  val isMicrophoneEnabled by call.microphone.isEnabled.collectAsState()

  VideoTheme {
    Box(modifier = Modifier.fillMaxSize()) {
      Column(
          modifier = Modifier.fillMaxSize().testTag("call_lobby"),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        TopNavigationBar(
            title = { Sub_title("Call Lobby") },
            navigationIcon = { GoBackRouteButton(navigationActions, "${Route.GROUP}/$groupUID") },
            actions = {})
        Icon(
            modifier = Modifier.size(36.dp),
            imageVector = Icons.Default.Phone,
            contentDescription = "",
        )
        Text(
            text = "Set up your test call",
        )
        CallLobby(
            call = call,
            modifier = Modifier.fillMaxWidth(),
            isCameraEnabled = isCameraEnabled,
            isMicrophoneEnabled = isMicrophoneEnabled,
            onCallAction = { action ->
              when (action) {
                is ToggleCamera -> callLobbyViewModel.enableCamera(!isCameraEnabled)
                is ToggleMicrophone -> callLobbyViewModel.enableMicrophone(!isMicrophoneEnabled)
                else -> Unit
              }
            })
        FloatingActionButton(
            onClick = { navigationActions.navigateTo("${Route.VIDEOCALL}/$groupUID") }) {
              Text("Join call")
            }
      }
    }
  }
}
