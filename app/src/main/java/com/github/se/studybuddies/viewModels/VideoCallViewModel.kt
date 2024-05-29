package com.github.se.studybuddies.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.ui.video_call.CallState
import com.github.se.studybuddies.ui.video_call.VideoCallAction
import com.github.se.studybuddies.ui.video_call.VideoCallState
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.StreamVideo
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VideoCallViewModel
@Inject
constructor(val uid: String, val call: Call, val navigationActions: NavigationActions) :
    ViewModel() {

  data class UiState(val isCameraEnabled: Boolean = true, val isMicrophoneEnabled: Boolean = true)

  var callState by mutableStateOf(VideoCallState(call))
    private set

  fun onAction(action: VideoCallAction) {
    when (action) {
      is VideoCallAction.JoinCall -> joinCall()
      is VideoCallAction.LeaveCall -> leaveCall()
    }
  }

  val state: StateFlow<UiState> =
      flow {
            val isCameraEnabled = call.camera.isEnabled.value
            val isMicrophoneEnabled = call.microphone.isEnabled.value
            emit(
                UiState(
                    isCameraEnabled = isCameraEnabled, isMicrophoneEnabled = isMicrophoneEnabled))
          }
          .stateIn(viewModelScope, SharingStarted.Lazily, UiState()) // Lazily to avoid unnecessary UI updates and it stops when the viewmodel is destroyed

  private fun joinCall() {
    if (callState.callState == CallState.ACTIVE) {
      Log.d("MyPrint", "Call is already active")
      return
    }
    viewModelScope.launch {
      callState = callState.copy(callState = CallState.JOINING)
      if (StreamVideo.instance().state.activeCall.value == call) {
        callState = callState.copy(callState = CallState.ACTIVE, error = null)
        Log.d("MyPrint", "Active call is the same as the call we are trying to join")
      } else {
        callState.call
            .join(false)
            .onSuccess {
              StreamVideo.instance().state.setActiveCall(callState.call)
              Log.d("MyPrint", "Call joined successfully")
              callState = callState.copy(callState = CallState.ACTIVE, error = null)
            }
            .onError { callState = callState.copy(error = it.message, callState = null) }
      }
    }
  }

  private fun leaveCall() {
    Log.d("MyPrint", "Trying to leave call")
    StreamVideo.instance().state.removeActiveCall()
    callState.call.leave()
    StreamVideo.instance().logOut()
    callState = callState.copy(callState = CallState.ENDED)
  }
}
