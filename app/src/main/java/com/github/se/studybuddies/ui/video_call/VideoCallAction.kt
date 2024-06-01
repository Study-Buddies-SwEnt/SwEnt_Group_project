package com.github.se.studybuddies.ui.video_call

sealed interface VideoCallAction {
  data object LeaveCall : VideoCallAction

  data object JoinCall : VideoCallAction
}
