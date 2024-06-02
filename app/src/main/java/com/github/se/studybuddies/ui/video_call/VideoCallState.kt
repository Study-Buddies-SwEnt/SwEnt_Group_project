package com.github.se.studybuddies.ui.video_call

import io.getstream.video.android.core.Call

/** Overall state for the video call */
data class VideoCallState(
    val call: Call,
    val callState: CallState? = null,
    val error: String? = null
)

/** Call states */
enum class CallState {
  JOINING,
  ACTIVE,
  ENDED,
}

/** Connectivity states for the video call */
data class ConnectState(
    val call: Call,
    val isConnected: Boolean = false,
    val errorMessage: String? = null
)
