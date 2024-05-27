package com.github.se.studybuddies.viewModels

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.DeviceStatus
import io.getstream.video.android.core.StreamVideo
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class VideoCallViewModel @Inject constructor(val uid: String, val call: Call) : ViewModel() {
  data class UiState(
      val isLoading: Boolean = true,
      val isCameraEnabled: Boolean =
          true, // enabled at start to allow direct display of camera in lobby
      val isMicrophoneEnabled: Boolean = false
  )

  val state: StateFlow<UiState> =
      flow {
            emit(VideoCallViewModel.UiState(isLoading = true))
            val isCameraEnabled = call.camera.status.first() is DeviceStatus.Enabled
            val isMicrophoneEnabled = call.microphone.status.first() is DeviceStatus.Enabled
            emit(
                VideoCallViewModel.UiState(
                    isLoading = false,
                    isCameraEnabled = isCameraEnabled,
                    isMicrophoneEnabled = isMicrophoneEnabled))
          }
          .stateIn(
              viewModelScope, SharingStarted.WhileSubscribed(5000), VideoCallViewModel.UiState())
  private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
  internal val isLoading: StateFlow<Boolean> = _isLoading

  @Composable
  fun JoinCall() {
    LaunchedEffect(call) {
      if (StreamVideo.instance().state.activeCall.value == call) {
        Log.d("MyPrint", "Active call is the same as the call we are trying to join")
      } else {
        try {
          call.join()
          Log.d("MyPrint", "Trying to join call")
        } catch (e: Exception) {
          Log.d("MyPrint", "Trying to join call got exception, leave call")
          call.leave()
          call.join()
        } finally {
          Log.d("MyPrint", "Keeping active call")
          keepActiveCall(call)
        }
      }
    }
  }

  fun enableCamera(enabled: Boolean) {
    call.camera.setEnabled(enabled)
  }

  fun enableMicrophone(enabled: Boolean) {
    call.microphone.setEnabled(enabled)
  }

  fun leaveCall() {
    Log.d("MyPrint", "Trying to leave call")
    call.leave()
  }

  private fun keepActiveCall(call: Call) {
    StreamVideo.instance().state.setActiveCall(call)
  }

  fun removeActiveCall() {
    StreamVideo.instance().state.removeActiveCall()
  }
}
