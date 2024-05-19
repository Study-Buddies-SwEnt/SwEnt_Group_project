package com.github.se.studybuddies.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.DeviceStatus
import io.getstream.video.android.core.StreamVideo
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.shareIn
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
        // in demo we can ignore this. The lobby screen will just display default camera/video,
        // but we will show an error
        Log.e(
            "CallLobbyViewModel",
            "Failed to create the call ${callGetOrCreateResult.errorOrNull()}",
        )
        _event.emit(CallLobbyEvent.JoinFailed(callGetOrCreateResult.errorOrNull()?.message))
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

  /*
  init {
    // for demo we set the default state for mic and camera to be on
    // and then we wait for call settings and we update the default state accordingly
    call.microphone.setEnabled(true)
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

    }
  }

   */

  private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
  internal val isLoading: StateFlow<Boolean> = _isLoading

  private val _event: MutableSharedFlow<CallLobbyEvent> = MutableSharedFlow()
  @OptIn(ExperimentalCoroutinesApi::class)
  internal val uiState: SharedFlow<CallLobbyUiState> =
      _event
          .flatMapLatest { event ->
            when (event) {
              is CallLobbyEvent.JoinCall -> {
                flowOf(CallLobbyUiState.JoinCompleted)
              }
              is CallLobbyEvent.JoinFailed -> {
                flowOf(CallLobbyUiState.JoinFailed(event.reason))
              }
            }
          }
          .onCompletion { _isLoading.value = false }
          .shareIn(viewModelScope, SharingStarted.Lazily, 0)

  fun handleUiEvent(event: CallLobbyEvent) {
    viewModelScope.launch { this@CallLobbyViewModel._event.emit(event) }
  }

  fun enableCamera(enabled: Boolean) {
    call.camera.setEnabled(enabled)
  }

  fun enableMicrophone(enabled: Boolean) {
    call.microphone.setEnabled(enabled)
  }
}

sealed interface CallLobbyUiState {
  data object Nothing : CallLobbyUiState

  data object JoinCompleted : CallLobbyUiState

  data class JoinFailed(val reason: String?) : CallLobbyUiState
}

sealed interface CallLobbyEvent {

  data object JoinCall : CallLobbyEvent

  data class JoinFailed(val reason: String?) : CallLobbyEvent
}
