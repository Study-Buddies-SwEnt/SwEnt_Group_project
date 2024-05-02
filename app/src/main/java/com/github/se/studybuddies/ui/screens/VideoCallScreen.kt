package com.github.se.studybuddies.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.viewModels.VideoCallViewModel
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.call.activecall.CallContent
import io.getstream.video.android.compose.ui.components.call.controls.ControlActions
import io.getstream.video.android.compose.ui.components.call.controls.actions.CancelCallAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.FlipCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleMicrophoneAction
import io.getstream.video.android.compose.ui.components.call.renderer.ParticipantVideo
import io.getstream.video.android.compose.ui.components.call.renderer.ParticipantsLayout
import io.getstream.video.android.compose.ui.components.call.renderer.RegularVideoRendererStyle

// Design UI elements using Jetpack Compose
@Composable
fun VideoCallScreen(videoCallViewModel: VideoCallViewModel, navigationActions: NavigationActions) {
  val call = remember { videoCallViewModel.call }
  val isCameraEnabled by call.camera.isEnabled.collectAsState()
  val isMicrophoneEnabled by call.microphone.isEnabled.collectAsState()

  VideoTheme {
    videoCallViewModel.joinCall()
    CallContent(
        modifier =
            Modifier.fillMaxSize()
                .background(color = VideoTheme.colors.appBackground)
                .testTag("VideoCallScreen"),
        call = call,
        onBackPressed = { navigationActions.goBack() },
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
                            onCallAction = {
                              videoCallViewModel.leaveCall()
                              navigationActions.goBack()
                            },
                        )
                      },
                  ))
        },
    )
  }
}
