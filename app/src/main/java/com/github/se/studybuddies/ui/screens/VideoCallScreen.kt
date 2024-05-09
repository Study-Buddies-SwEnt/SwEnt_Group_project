package com.github.se.studybuddies.ui.screens

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.se.studybuddies.utility.LockScreenOrientation
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.call.activecall.CallContent
import io.getstream.video.android.compose.ui.components.call.controls.ControlActions
import io.getstream.video.android.compose.ui.components.call.controls.actions.CancelCallAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.FlipCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleMicrophoneAction
import io.getstream.video.android.compose.ui.components.call.renderer.LayoutType
import io.getstream.video.android.compose.ui.components.call.renderer.ParticipantVideo
import io.getstream.video.android.compose.ui.components.call.renderer.ParticipantsLayout
import io.getstream.video.android.compose.ui.components.call.renderer.RegularVideoRendererStyle
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.RealtimeConnection

// Design UI elements using Jetpack Compose
@Composable
fun VideoCallScreen(
    call: Call,
    onCallDisconnected: () -> Unit = {},
    onUserLeaveCall: () -> Unit = {},
) {
  val context = LocalContext.current
  val isCameraEnabled by call.camera.isEnabled.collectAsState()
  val isMicrophoneEnabled by call.microphone.isEnabled.collectAsState()
  var layout by remember { mutableStateOf(LayoutType.DYNAMIC) }
  var showParticipants by remember { mutableStateOf(false) }
  val orientation = LocalConfiguration.current.orientation
  val participantsSize by call.state.participants.collectAsStateWithLifecycle()
  val scope = rememberCoroutineScope()
  val connection by call.state.connection.collectAsStateWithLifecycle()
  val me by call.state.me.collectAsState()

  LaunchedEffect(key1 = connection) {
    if (connection == RealtimeConnection.Disconnected) {
      onCallDisconnected.invoke()
    } else if (connection is RealtimeConnection.Failed) {
      Toast.makeText(
              context,
              "Call connection failed (${(connection as RealtimeConnection.Failed).error}",
              Toast.LENGTH_LONG,
          )
          .show()
      onCallDisconnected.invoke()
    }
  }
  LockScreenOrientation(orientation = Configuration.ORIENTATION_PORTRAIT)

  VideoTheme {
    CallContent(
        modifier =
            Modifier.fillMaxSize()
                .background(color = VideoTheme.colors.appBackground)
                .testTag("VideoCallScreen"),
        call = call,
        enableInPictureInPicture = true,
        layout = layout,
        onBackPressed = { onUserLeaveCall.invoke() },
        videoContent = {
          ParticipantsLayout(
              call = call,
              modifier = Modifier.fillMaxSize().weight(1f).padding(6.dp),
              style = RegularVideoRendererStyle(),
              videoRenderer = { modifier, _, participant, style ->
                ParticipantVideo(
                    modifier = modifier.padding(4.dp).clip(RoundedCornerShape(8.dp)),
                    call = call,
                    participant = participant,
                    style = style,
                )
              },
          )
        },
        controlsContent = {
          ControlActions(
              call = call,
              modifier = Modifier.testTag("ControlActions"),
              actions =
                  listOf(
                      {
                        ToggleCameraAction(
                            modifier = Modifier.size(52.dp).testTag("ToggleCameraAction"),
                            isCameraEnabled = isCameraEnabled,
                            onCallAction = { call.camera.setEnabled(it.isEnabled) })
                      },
                      {
                        ToggleMicrophoneAction(
                            modifier = Modifier.size(52.dp).testTag("ToggleMicrophoneAction"),
                            isMicrophoneEnabled = isMicrophoneEnabled,
                            onCallAction = { call.microphone.setEnabled(it.isEnabled) })
                      },
                      {
                        FlipCameraAction(
                            modifier = Modifier.size(52.dp).testTag("FlipCameraAction"),
                            onCallAction = { call.camera.flip() })
                      },
                      {
                        CancelCallAction(
                            modifier =
                                Modifier.size(
                                        VideoTheme.dimens.controlActionsButtonSize,
                                    )
                                    .testTag("CancelCallAction"),
                            onCallAction = { onCallDisconnected.invoke() },
                        )
                      },
                  ))
        },
    )
  }
}
