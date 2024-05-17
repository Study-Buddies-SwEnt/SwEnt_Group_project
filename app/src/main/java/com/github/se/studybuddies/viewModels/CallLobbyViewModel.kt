package com.github.se.studybuddies.viewModels

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

class CallLobbyViewModel @Inject constructor(val uid: String, val callType: String) : ViewModel() {

  data class UiState(
      val isLoading: Boolean = true,
      val isCameraEnabled: Boolean = false,
      val isMicrophoneEnabled: Boolean = false
  )

  val call: Call by lazy {
    val streamVideo = StreamVideo.instance()
    streamVideo.call(callType, uid)
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
