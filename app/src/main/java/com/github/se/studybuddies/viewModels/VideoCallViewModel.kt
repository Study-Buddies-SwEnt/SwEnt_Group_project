package com.github.se.studybuddies.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.StreamVideo
import kotlinx.coroutines.launch

class VideoCallViewModel(val call: Call, val uid: String) : ViewModel() {

  fun joinCall() {
    viewModelScope.launch {
      try {
        call.join()
        Log.d("MyPrint", "Trying to join call")
      } catch (e: Exception) {
        Log.d("MyPrint", "Trying to join call got exception")
        call.leave()
        call.join()
      }
      keepActiveCall()
    }
  }

  fun leaveCall() {
    StreamVideo.instance().state.removeActiveCall()
    Log.d("MyPrint", "Trying to leave call")
    call.leave()
  }

  private fun keepActiveCall() {
    StreamVideo.instance().state.setActiveCall(call)
  }
}
