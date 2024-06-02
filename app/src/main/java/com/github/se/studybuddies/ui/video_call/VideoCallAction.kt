package com.github.se.studybuddies.ui.video_call

/** Actions that can be performed in the video call screen */
sealed interface VideoCallAction {
  data object LeaveCall : VideoCallAction

  data object JoinCall : VideoCallAction
}
