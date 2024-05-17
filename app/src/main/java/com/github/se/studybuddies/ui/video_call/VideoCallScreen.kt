package com.github.se.studybuddies.ui.video_call

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.viewModels.VideoCallViewModel
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

// Design UI elements using Jetpack Compose
@Composable
fun VideoCallScreen(videoVM: VideoCallViewModel, onCallDisconnected: () -> Unit = {}) {
  val isCameraEnabled by videoVM.call.camera.isEnabled.collectAsState()
  val isMicrophoneEnabled by videoVM.call.microphone.isEnabled.collectAsState()
  val layout by remember { mutableStateOf(LayoutType.DYNAMIC) }

  LockScreenOrientation(orientation = Configuration.ORIENTATION_PORTRAIT)
  videoVM.joinCall()

  VideoTheme {
    Box(modifier = Modifier.fillMaxSize().testTag("video_call_screen")) {
      CallContent(
          modifier =
              Modifier.fillMaxSize()
                  .background(color = VideoTheme.colors.appBackground)
                  .testTag("call_content"),
          call = videoVM.call,
          enableInPictureInPicture = true,
          layout = layout,
          onBackPressed = {
            videoVM.keepActiveCall()
            onCallDisconnected.invoke()
          },
          videoContent = {
            ParticipantsLayout(
                call = videoVM.call,
                modifier =
                    Modifier.fillMaxSize()
                        .weight(1f)
                        .padding(6.dp)
                        .testTag("participant_video_screen"),
                style = RegularVideoRendererStyle(),
                videoRenderer = { modifier, _, participant, style ->
                  ParticipantVideo(
                      modifier = modifier.padding(4.dp).clip(RoundedCornerShape(8.dp)),
                      call = videoVM.call,
                      participant = participant,
                      style = style,
                  )
                },
            )
          },
          controlsContent = {
            ControlActions(
                call = videoVM.call,
                modifier = Modifier.testTag("control_actions"),
                actions =
                    listOf(
                        {
                          ToggleCameraAction(
                              modifier = Modifier.size(52.dp),
                              isCameraEnabled = isCameraEnabled,
                              onCallAction = { videoVM.call.camera.setEnabled(it.isEnabled) })
                        },
                        {
                          ToggleMicrophoneAction(
                              modifier = Modifier.size(52.dp),
                              isMicrophoneEnabled = isMicrophoneEnabled,
                              onCallAction = { videoVM.call.microphone.setEnabled(it.isEnabled) })
                        },
                        {
                          FlipCameraAction(
                              modifier = Modifier.size(52.dp),
                              onCallAction = { videoVM.call.camera.flip() })
                        },
                        {
                          CancelCallAction(
                              modifier =
                                  Modifier.size(
                                      VideoTheme.dimens.controlActionsButtonSize,
                                  ),
                              onCallAction = {
                                videoVM.leaveCall()
                                onCallDisconnected.invoke()
                              },
                          )
                        },
                    ))
          },
      )
    }
  }
}
