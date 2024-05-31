package com.github.se.studybuddies.ui.video_call

import io.getstream.video.android.core.Call

data class VideoCallState(
    val call: Call,
    val callState: CallState? = null,
    val error: String? = null
)

enum class CallState {
  JOINING,
  ACTIVE,
  ENDED,
}

data class ConnectState(
    val call: Call,
    val isConnected: Boolean = false,
    val errorMessage: String? = null
)
