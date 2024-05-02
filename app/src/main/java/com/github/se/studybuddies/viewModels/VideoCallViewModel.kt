package com.github.se.studybuddies.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.getstream.video.android.core.StreamVideo
import kotlinx.coroutines.launch

class VideoCallViewModel(val groupUID: String, val uid: String) : ViewModel() {

  private val streamVideo = StreamVideo.instance()
  val call = streamVideo.call("default", groupUID)

  fun joinCall() {
    viewModelScope.launch { call.join(create = true) }
  }

  fun leaveCall() {
    viewModelScope.launch { call.leave() }
  }
}
