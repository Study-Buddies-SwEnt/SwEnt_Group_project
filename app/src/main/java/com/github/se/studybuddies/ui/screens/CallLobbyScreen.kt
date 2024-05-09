package com.github.se.studybuddies.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.utility.LockScreenOrientation
import com.github.se.studybuddies.viewModels.CallLobbyViewModel
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.call.lobby.CallLobby
import io.getstream.video.android.core.call.state.AcceptCall

@Composable
fun CallLobbyScreen(
    groupUID: String,
    callLobbyViewModel: CallLobbyViewModel,
    navigationActions: NavigationActions
) {
  LockScreenOrientation(orientation = Configuration.ORIENTATION_PORTRAIT)
  val call by remember { mutableStateOf(callLobbyViewModel.call) }
  val isLoading by callLobbyViewModel.isLoading.collectAsState()
  val isCameraEnabled by call.camera.isEnabled.collectAsState()
  val isMicrophoneEnabled by call.microphone.isEnabled.collectAsState()

  VideoTheme {
    TopNavigationBar(
        title = { Sub_title("Call Lobby") },
        navigationIcon = { GoBackRouteButton(navigationActions, "${Route.GROUP}/$groupUID") },
        actions = {})
    CallLobby(
        call = call,
        modifier = Modifier.fillMaxWidth(),
        isCameraEnabled = isCameraEnabled,
        isMicrophoneEnabled = isMicrophoneEnabled,
        onCallAction = { action ->
          when (action) {
            is AcceptCall -> navigationActions.navigateTo("${Route.VIDEOCALL}/$groupUID")
            else -> Unit
          }
        })
  }
}
