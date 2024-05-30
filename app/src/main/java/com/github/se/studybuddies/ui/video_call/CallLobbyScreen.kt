package com.github.se.studybuddies.ui.video_call

import android.annotation.SuppressLint
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun CallLobbyScreen(
    state: ConnectState,
    callLobbyViewModel: CallLobbyViewModel,
    onAction: (ConnectAction) -> Unit,
    navigationActions: NavigationActions,
) {
  val isLoading by callLobbyViewModel.isLoading.collectAsState()
  val isCameraEnabled by state.call.camera.isEnabled.collectAsState()
  val isMicrophoneEnabled by state.call.microphone.isEnabled.collectAsState()
  val context = LocalContext.current
  val groupUID = state.call.cid
    var joinCallText = stringResource(R.string.join_call)
    if(state.call.state.participants.value.isEmpty()) {
        joinCallText = stringResource(R.string.start_call)
    }

  LaunchCallPermissions(
      call = state.call,
      onPermissionsResult = {
        if (it.values.contains(false)) {
          Toast.makeText(
                  context,
                  context.getString(R.string.permissions_not_granted_call),
                  Toast.LENGTH_LONG,
              )
              .show()
          navigationActions.navigateTo("${Route.GROUP}/$groupUID")
        }
      })

  VideoTheme {
    Box(modifier = Modifier
        .fillMaxSize()
        .testTag("call_lobby")) {
      if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = Blue,
        )
      }
      Column(
          modifier = Modifier
              .fillMaxSize()
              .testTag("content"),
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        TopNavigationBar(
            title = { Sub_title(stringResource(R.string.call_lobby)) },
            navigationIcon = { GoBackRouteButton(navigationActions, "${Route.GROUP}/$groupUID") },
            actions = {})
          HorizontalDivider(modifier = Modifier.size(36.dp))
        Icon(
            modifier = Modifier
                .size(40.dp)
                .testTag("phone_icon"),
            imageVector = Icons.Default.Phone,
            contentDescription = stringResource(R.string.phone_icon),
        )
          HorizontalDivider(modifier = Modifier.size(20.dp))
        Text(
            text = stringResource(R.string.preview_of_your_call_setup),
            modifier = Modifier.testTag("preview_text"),
        )
          HorizontalDivider(modifier = Modifier.size(36.dp))
        CallLobby(
            call = state.call,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("call_preview"),
            isCameraEnabled = isCameraEnabled,
            isMicrophoneEnabled = isMicrophoneEnabled)
        FloatingActionButton(
            modifier = Modifier
                .size(60.dp)
                .testTag("join_call_button"),
            containerColor = Blue,
            onClick = { onAction(ConnectAction.OnConnectClick) }) {
              Text(joinCallText)
            }
      }
    }
  }
}
