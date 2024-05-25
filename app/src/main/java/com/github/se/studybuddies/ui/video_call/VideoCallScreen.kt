package com.github.se.studybuddies.ui.video_call

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.call.activecall.CallContent
import io.getstream.video.android.compose.ui.components.call.controls.ControlActions
import io.getstream.video.android.compose.ui.components.call.controls.actions.FlipCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.LeaveCallAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleMicrophoneAction
import io.getstream.video.android.compose.ui.components.call.renderer.LayoutType
import io.getstream.video.android.compose.ui.components.call.renderer.ParticipantVideo
import io.getstream.video.android.compose.ui.components.call.renderer.ParticipantsLayout
import io.getstream.video.android.compose.ui.components.call.renderer.RegularVideoRendererStyle
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.StreamVideo

// Design UI elements using Jetpack Compose
@Composable
fun VideoCallScreen(
    call: Call,
    keepCallConnected: () -> Unit = {},
    onCallDisconnected: () -> Unit = {}
) {
  val isCameraEnabled by call.camera.isEnabled.collectAsState()
  val isMicrophoneEnabled by call.microphone.isEnabled.collectAsState()
  val layout by remember { mutableStateOf(LayoutType.DYNAMIC) }

  JoinCall(call)

  VideoTheme {
    Box(modifier = Modifier.fillMaxSize().testTag("video_call_screen")) {
      CallContent(
          modifier =
              Modifier.fillMaxSize()
                  .background(color = VideoTheme.colors.appBackground)
                  .testTag("call_content"),
          call = call,
          enableInPictureInPicture = true,
          layout = layout,
          onBackPressed = { keepCallConnected.invoke() },
          videoContent = {
            ParticipantsLayout(
                call = call,
                modifier =
                    Modifier.fillMaxSize()
                        .weight(1f)
                        .padding(6.dp)
                        .testTag("participant_video_screen"),
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
                modifier = Modifier.testTag("control_actions"),
                actions =
                    listOf(
                        {
                          ToggleCameraAction(
                              modifier = Modifier.size(52.dp),
                              isCameraEnabled = isCameraEnabled,
                              onCallAction = { toggleCamera(call, it.isEnabled) })
                        },
                        {
                          ToggleMicrophoneAction(
                              modifier = Modifier.size(52.dp),
                              isMicrophoneEnabled = isMicrophoneEnabled,
                              onCallAction = { toggleMicrophone(call, it.isEnabled) })
                        },
                        {
                          FlipCameraAction(
                              modifier = Modifier.size(52.dp),
                              onCallAction = { call.camera.flip() })
                        },
                        {
                          LeaveCallAction(
                              modifier =
                                  Modifier.size(
                                      VideoTheme.dimens.controlActionsButtonSize,
                                  ),
                              onCallAction = {
                                removeActiveCall()
                                leaveCall(call)
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

@Composable
private fun JoinCall(call: Call) {
  LaunchedEffect(call) {
    if (StreamVideo.instance().state.activeCall.value == call) {
      Log.d("MyPrint", "Active call is the same as the call we are trying to join")
    } else {
      try {
        call.join()
        Log.d("MyPrint", "Trying to join call")
      } catch (e: Exception) {
        Log.d("MyPrint", "Trying to join call got exception, leave call")
        call.leave()
        call.join()
      } finally {
        Log.d("MyPrint", "Keeping active call")
        keepActiveCall(call)
      }
    }
  }
}

fun leaveCall(call: Call) {
  Log.d("MyPrint", "Trying to leave call")
  call.leave()
}

fun keepActiveCall(call: Call) {
  StreamVideo.instance().state.setActiveCall(call)
}

fun removeActiveCall() {
  StreamVideo.instance().state.removeActiveCall()
}

fun toggleCamera(call: Call, enable: Boolean) {
  call.camera.setEnabled(enable)
}

fun toggleMicrophone(call: Call, enable: Boolean) {
  call.microphone.setEnabled(enable)
}
