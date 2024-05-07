package com.github.se.studybuddies.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.viewModels.CallLobbyViewModel
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.call.lobby.CallLobby
import io.getstream.video.android.core.call.state.AcceptCall
import io.getstream.video.android.core.call.state.ToggleCamera
import io.getstream.video.android.core.call.state.ToggleMicrophone

@Composable
fun CallLobbyScreen(groupUID: String, callLobbyViewModel: CallLobbyViewModel, navigationActions: NavigationActions) {
    val call = remember { callLobbyViewModel.call }
    val isCameraEnabled by call.camera.isEnabled.collectAsState()
    val isMicrophoneEnabled by call.microphone.isEnabled.collectAsState()

    VideoTheme {
        TopNavigationBar(
            title = { Sub_title("Call Lobby") },
            navigationIcon = { GoBackRouteButton(navigationActions, "${Route.GROUP}/$groupUID") },
            actions = {})
        CallLobby(
            call = call,
            modifier = Modifier.fillMaxWidth(),
            isCameraEnabled = isCameraEnabled,
            isMicrophoneEnabled = isMicrophoneEnabled,
            onCallAction = { action ->
                when (action) {
                    is AcceptCall -> navigationActions.navigateTo("${Route.VIDEOCALL}/$groupUID")
                    else -> Unit
                }
            }

        )
    }

}