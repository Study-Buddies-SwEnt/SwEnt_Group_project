package com.github.se.studybuddies.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.navigation.NavigationActions
import io.getstream.video.android.compose.ui.components.call.activecall.CallContent
import io.getstream.video.android.compose.ui.components.call.controls.ControlActions
import io.getstream.video.android.compose.ui.components.call.controls.actions.FlipCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleMicrophoneAction
import io.getstream.video.android.core.Call

// Design UI elements using Jetpack Compose
@Composable
fun VideoCallScreen(call: Call, navigationActions: NavigationActions) {

  val isCameraEnabled by call.camera.isEnabled.collectAsState()
  val isMicrophoneEnabled by call.microphone.isEnabled.collectAsState()

  CallContent(
      modifier = Modifier.fillMaxSize(),
      call = call,
      onBackPressed = { /* Handle back press */},
      controlsContent = {
        ControlActions(
            call = call,
            actions =
                listOf(
                    {
                      ToggleCameraAction(
                          modifier = Modifier.size(52.dp),
                          isCameraEnabled = isCameraEnabled,
                          onCallAction = { call.camera.setEnabled(it.isEnabled) })
                    },
                    {
                      ToggleMicrophoneAction(
                          modifier = Modifier.size(52.dp),
                          isMicrophoneEnabled = isMicrophoneEnabled,
                          onCallAction = { call.microphone.setEnabled(it.isEnabled) })
                    },
                    {
                      FlipCameraAction(
                          modifier = Modifier.size(52.dp), onCallAction = { call.camera.flip() })
                    },
                ))
      })
}
