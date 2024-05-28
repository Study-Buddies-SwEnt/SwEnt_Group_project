package com.github.se.studybuddies.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.ui.video_call.ConnectAction
import com.github.se.studybuddies.ui.video_call.ConnectState
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.DeviceStatus
import io.getstream.video.android.core.StreamVideo
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CallLobbyViewModel @Inject constructor(val uid: String, val callType: String) : ViewModel() {

  data class UiState(
      val isLoading: Boolean = true,
      val isCameraEnabled: Boolean =
          true, // enabled at start to allow direct display of camera in lobby
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

  var callState by mutableStateOf(ConnectState(call))
    private set

  fun onAction(action: ConnectAction) {
    when (action) {
      ConnectAction.OnConnectClick -> {
        callState = callState.copy(isConnected = true)
      }
    }
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

  private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
  internal val isLoading: StateFlow<Boolean> = _isLoading

  private val _event: MutableSharedFlow<CallLobbyEvent> = MutableSharedFlow()
}

sealed interface CallLobbyEvent {

  data object JoinCall : CallLobbyEvent

  data class JoinFailed(val reason: String?) : CallLobbyEvent
}
