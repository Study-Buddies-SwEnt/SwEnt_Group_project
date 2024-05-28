package com.github.se.studybuddies.ui.video_call

sealed interface ConnectAction {
  data object OnConnectClick : ConnectAction
}
