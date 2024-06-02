package com.github.se.studybuddies.ui.video_call

/** Actions that can be performed in the lobby screen */
sealed interface ConnectAction {
  data object OnConnectClick : ConnectAction
}
