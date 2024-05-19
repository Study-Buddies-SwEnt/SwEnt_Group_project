package com.github.se.studybuddies.ui.video_call

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.viewModels.CallLobbyViewModel
import io.getstream.video.android.compose.permission.LaunchCallPermissions
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
  val isLoading by callLobbyViewModel.isLoading.collectAsState()
  val isCameraEnabled by call.camera.isEnabled.collectAsState()
  val isMicrophoneEnabled by call.microphone.isEnabled.collectAsState()
  val context = LocalContext.current

  LaunchCallPermissions(
      call = call,
      onPermissionsResult = {
        if (it.values.contains(false)) {
          Toast.makeText(
                  context,
                  "Call permissions are required to join the call",
                  Toast.LENGTH_LONG,
              )
              .show()
          navigationActions.navigateTo("${Route.GROUP}/$groupUID")
        }
      })

  VideoTheme {
    Box(modifier = Modifier.fillMaxSize().testTag("call_lobby")) {
      Column(
          modifier = Modifier.fillMaxSize().testTag("content"),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        TopNavigationBar(
            title = { Sub_title(stringResource(R.string.call_lobby)) },
            navigationIcon = { GoBackRouteButton(navigationActions, "${Route.GROUP}/$groupUID") },
            actions = {})
        Icon(
            modifier = Modifier.size(36.dp).testTag("phone_icon"),
            imageVector = Icons.Default.Phone,
            contentDescription = stringResource(R.string.phone_icon),
        )
        Text(
            text = stringResource(R.string.preview_of_your_call_setup),
            modifier = Modifier.testTag("preview_text"),
        )
        CallLobby(
            call = call,
            modifier = Modifier.fillMaxWidth().testTag("call_preview"),
            isCameraEnabled = isCameraEnabled,
            isMicrophoneEnabled = isMicrophoneEnabled,
            onCallAction = { action ->
              when (action) {
                is ToggleCamera -> callLobbyViewModel.enableCamera(action.isEnabled)
                is ToggleMicrophone -> callLobbyViewModel.enableMicrophone(action.isEnabled)
                else -> Unit
              }
            })
        FloatingActionButton(
            modifier = Modifier.size(60.dp).testTag("join_call_button"),
            onClick = { navigationActions.navigateTo("${Route.VIDEOCALL}/$groupUID") }) {
              Text(stringResource(R.string.join_call))
            }
      }
      if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = Blue,
        )
      }
    }
  }
}
