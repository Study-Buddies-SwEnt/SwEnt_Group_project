package com.github.se.studybuddies.ui.video_call

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.theme.Blue
import io.getstream.video.android.compose.permission.rememberCallPermissionsState
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

// Design UI elements using Jetpack Compose
@Composable
fun VideoCallScreen(
    callId: String,
    state: VideoCallState,
    onAction: (VideoCallAction) -> Unit,
    navigationActions: NavigationActions
) {

  VideoTheme {
    when {
      state.error != null -> {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text(text = state.error, color = MaterialTheme.colorScheme.error)
        }
      }
      state.callState == CallState.JOINING -> {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              CircularProgressIndicator(color = Blue)
              Text(text = stringResource(R.string.joining))
            }
      }
      else -> {
        val layout by remember { mutableStateOf(LayoutType.DYNAMIC) }
        val speakingWhileMuted by state.call.state.speakingWhileMuted.collectAsStateWithLifecycle()
        val context = LocalContext.current
        if (speakingWhileMuted && state.callState == CallState.ACTIVE) {
          Toast.makeText(LocalContext.current, R.string.speaking_while_muted, Toast.LENGTH_SHORT)
              .show()
        }
        val isCameraEnabled by state.call.camera.isEnabled.collectAsState()
        val isMicrophoneEnabled by state.call.microphone.isEnabled.collectAsState()
        Column(modifier = Modifier
            .fillMaxSize()
            .testTag("video_call_screen")) {
          CallContent(
              modifier = Modifier
                  .fillMaxSize()
                  .background(color = Blue)
                  .testTag("call_content"),
              call = state.call,
              permissions = // Request camera and microphone permissions, shouldn't be called ever
                  // since always passes through callLobby first
                  rememberCallPermissionsState(
                      call = state.call,
                      permissions =
                          listOf(
                              android.Manifest.permission.CAMERA,
                              android.Manifest.permission.RECORD_AUDIO),
                      onPermissionsResult = { permissions ->
                        if (permissions.values.contains(false)) {
                          Toast.makeText(
                                  context,
                                  context.getString(R.string.permissions_not_granted_call),
                                  Toast.LENGTH_LONG)
                              .show()
                          navigationActions.navigateTo("${Route.GROUP}/$callId")
                        } else {
                          onAction(VideoCallAction.JoinCall)
                        }
                      }),
              enableInPictureInPicture = true,
              isShowingOverlayAppBar = false,
              layout = layout,
              videoContent = {
                ParticipantsLayout(
                    call = state.call,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    videoRenderer = { modifier, _, participant, style ->
                      ParticipantVideo(
                          call = state.call,
                          participant = participant,
                          style = style,
                          modifier = modifier
                              .padding(4.dp)
                              .clip(RoundedCornerShape(8.dp)))
                    },
                )
              },
              controlsContent = {
                ControlActions(
                    call = state.call,
                    modifier = Modifier.testTag("control_actions"),
                    actions =
                        listOf(
                            {
                              ToggleCameraAction(
                                  modifier = Modifier.size(52.dp),
                                  isCameraEnabled = isCameraEnabled,
                                  onCallAction = { state.call.camera.setEnabled(it.isEnabled) })
                            },
                            {
                              ToggleMicrophoneAction(
                                  modifier = Modifier.size(52.dp),
                                  isMicrophoneEnabled = isMicrophoneEnabled,
                                  onCallAction = { state.call.microphone.setEnabled(it.isEnabled) })
                            },
                            {
                              FlipCameraAction(
                                  modifier = Modifier.size(52.dp),
                                  onCallAction = { state.call.camera.flip() })
                            },
                            {
                              LeaveCallAction(
                                  modifier = Modifier.size(52.dp),
                                  onCallAction = { onAction(VideoCallAction.LeaveCall) })
                            }))
                BackHandler { navigationActions.navigateTo("${Route.GROUP}/${callId}") }
              },
          )
        }
      }
    }
  }
}
