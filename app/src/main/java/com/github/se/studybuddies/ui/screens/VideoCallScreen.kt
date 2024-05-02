package com.github.se.studybuddies.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.viewModels.VideoCallViewModel
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.call.CallAppBar
import io.getstream.video.android.compose.ui.components.call.activecall.CallContent
import io.getstream.video.android.compose.ui.components.call.controls.ControlActions
import io.getstream.video.android.compose.ui.components.call.controls.actions.FlipCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.LeaveCallAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleMicrophoneAction
import io.getstream.video.android.compose.ui.components.call.renderer.ParticipantVideo

// Design UI elements using Jetpack Compose
@Composable
fun VideoCallScreen(videoCallViewModel: VideoCallViewModel, navigationActions: NavigationActions) {
  val call = videoCallViewModel.joinCall(context = LocalContext.current)
  val isCameraEnabled by call.camera.isEnabled.collectAsState()
  val isMicrophoneEnabled by call.microphone.isEnabled.collectAsState()

  VideoTheme {
    CallContent(
        modifier = Modifier.fillMaxSize(),
        call = call,
        onBackPressed = {
          Log.d("MyPrint", "Trying to leave the call from call content")
          videoCallViewModel.leaveCall()
          navigationActions.goBack()
        },
        appBarContent = {
          CallAppBar(
              call = call,
              onBackPressed = {
                Log.d("MyPrint", "Trying to leave the call from app bar")
                videoCallViewModel.leaveCall()
                navigationActions.goBack()
              })
        },
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
                      {
                        LeaveCallAction(
                            modifier =
                                Modifier.size(
                                    VideoTheme.dimens.controlActionsButtonSize,
                                ),
                            onCallAction = {
                              Log.d("MyPrint", "Trying to leave the call from bottom controls bar")
                              videoCallViewModel.leaveCall()
                              Log.d("MyPrint", "Successfully left the call")
                              navigationActions.goBack()
                            },
                        )
                      },
                  ))
        },
        videoContent = {
          val participants by call.state.participants.collectAsState()

          LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            items(participants, key = { it.sessionId }) { participant ->
              ParticipantVideo(
                  modifier = Modifier.fillMaxWidth().height(220.dp),
                  call = call,
                  participant = participant)
            }
          }
        })
  }
}
