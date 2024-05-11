package com.github.se.studybuddies.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.DeviceStatus
import io.getstream.video.android.core.StreamVideo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CallLobbyViewModel(val uid: String) : ViewModel() {

  val call: Call by lazy {
    val streamVideo = StreamVideo.instance()
    val call = streamVideo.call("default", uid)

    viewModelScope.launch {
      // create the call if it doesn't exist - this will also load the settings for the call,
      // this way the lobby screen can already display the right mic/camera settings
      // This also starts listening to the call events to get the participant count
      val callGetOrCreateResult = call.create()
      if (callGetOrCreateResult.isFailure) {
        Log.e(
            "CallLobbyViewModel",
            "Failed to create the call ${callGetOrCreateResult.errorOrNull()}",
        )
        event.emit(CallLobbyEvent.JoinFailed(callGetOrCreateResult.errorOrNull()?.message))
      }
    }
    call
  }

  init {
    call.microphone.setEnabled(false)
    call.camera.setEnabled(true)

    viewModelScope.launch {
      // wait for settings (this will not block the UI) and then update the camera
      // based on it
      val settings = call.state.settings.first { it != null }

      val enabled =
          when (call.camera.status.first()) {
            is DeviceStatus.NotSelected -> {
              settings?.video?.cameraDefaultOn ?: false
            }
            is DeviceStatus.Enabled -> {
              true
            }
            is DeviceStatus.Disabled -> {
              false
            }
          }

      // enable/disable camera capture (no preview would be visible otherwise)
      call.camera.setEnabled(enabled)
    }
  }

  private val event: MutableSharedFlow<CallLobbyEvent> = MutableSharedFlow()

  fun enableCamera(enabled: Boolean) {
    call.camera.setEnabled(enabled)
  }

  fun enableMicrophone(enabled: Boolean) {
    call.microphone.setEnabled(enabled)
  }

  fun signOut() {
    viewModelScope.launch {
      StreamVideo.instance().logOut()
      StreamVideo.removeClient()
    }
  }
}

sealed interface CallLobbyEvent {

  data object JoinCall : CallLobbyEvent

  data class JoinFailed(val reason: String?) : CallLobbyEvent
}
