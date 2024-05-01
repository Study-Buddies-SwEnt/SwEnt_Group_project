package com.github.se.studybuddies.ui.screens

import android.net.Uri
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.viewModels.VideoCallViewModel
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.call.activecall.CallContent
import io.getstream.video.android.compose.ui.components.call.controls.ControlActions
import io.getstream.video.android.compose.ui.components.call.controls.actions.CancelCallAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.FlipCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleMicrophoneAction
import io.getstream.video.android.compose.ui.components.call.renderer.ParticipantVideo

// Design UI elements using Jetpack Compose
@Composable
fun VideoCallScreen(
    groupUID: String,
    videoCallViewModel: VideoCallViewModel,
    navigationActions: NavigationActions
) {
  val group by videoCallViewModel.group.observeAsState()
  val nameState = remember { mutableStateOf(group?.name ?: "") }
  val pictureState = remember { mutableStateOf(group?.picture ?: Uri.EMPTY) }
  val membersState = remember { mutableStateOf(group?.members ?: emptyList()) }
  val call = videoCallViewModel.call
  val isCameraEnabled by call.camera.isEnabled.collectAsState()
  val isMicrophoneEnabled by call.microphone.isEnabled.collectAsState()

  VideoTheme {
    TopNavigationBar(
        title = { Sub_title(nameState.value) },
        navigationIcon = {
          GoBackRouteButton(navigationActions = navigationActions, "${Route.GROUP}/$groupUID")
        },
        actions = {})
    CallContent(
        modifier = Modifier.fillMaxSize(),
        call = call,
        // onBackPressed = { finish()},
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
                        CancelCallAction(
                            modifier =
                                Modifier.size(
                                    VideoTheme.dimens.controlActionsButtonSize,
                                ),
                            onCallAction = {
                              call.leave()
                              // finish()
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
