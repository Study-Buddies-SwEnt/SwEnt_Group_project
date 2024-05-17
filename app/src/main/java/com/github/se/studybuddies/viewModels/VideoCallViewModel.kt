package com.github.se.studybuddies.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.StreamVideo
import kotlinx.coroutines.launch

class VideoCallViewModel(val call: Call, val uid: String) : ViewModel() {

  fun joinCall() {
    viewModelScope.launch {
      StreamVideo.instance().state.activeCall.value?.leave()
      call.join(create = true)
    }
  }

  fun leaveCall() {
    viewModelScope.launch { call.leave() }
  }
}
