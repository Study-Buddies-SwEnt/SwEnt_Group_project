package com.github.se.studybuddies.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.DeviceStatus
import io.getstream.video.android.core.StreamVideo
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CallLobbyViewModel @Inject constructor(val uid: String, val callType: String) : ViewModel() {

  data class UiState(
      val isLoading: Boolean = true,
      val isCameraEnabled: Boolean = false,
      val isMicrophoneEnabled: Boolean = false
  )

  val call: Call by lazy {
    val streamVideo = StreamVideo.instance()
    val call = streamVideo.call(callType, uid)

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

  val state: StateFlow<UiState> =
      flow {
            emit(UiState(isLoading = true))
            val isCameraEnabled = call.camera.status.first() is DeviceStatus.Enabled
            val isMicrophoneEnabled = call.microphone.status.first() is DeviceStatus.Enabled
            emit(
                UiState(
                    isLoading = false,
                    isCameraEnabled = isCameraEnabled,
                    isMicrophoneEnabled = isMicrophoneEnabled))
          }
          .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState())

  private val event: MutableSharedFlow<CallLobbyEvent> = MutableSharedFlow()

  fun enableCamera(enabled: Boolean) {
    call.camera.setEnabled(enabled)
  }

  fun enableMicrophone(enabled: Boolean) {
    call.microphone.setEnabled(enabled)
  }

  sealed interface CallLobbyEvent {

    data class JoinFailed(val reason: String?) : CallLobbyEvent
  }
}
