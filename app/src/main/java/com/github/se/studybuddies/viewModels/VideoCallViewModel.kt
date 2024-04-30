package com.github.se.studybuddies.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.getstream.video.android.core.GEO
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.model.User
import kotlinx.coroutines.launch

class VideoCallViewModel(private val uid: String? = null) : ViewModel() {

  val userToken = "REPLACE_WITH_TOKEN"
  val userId = "REPLACE_WITH_bitter-shape-1"
  val callId = "REPLACE_WITH_CALL_ID"

  // Create a user.
  val user =
      User(
          id = userId, // any string
          name = "Tutorial" // name and image are used in the UI
          )

  // Initialize StreamVideo. For a production app we recommend adding the client to your Application
  // class or di module.
  val client =
      StreamVideoBuilder(
              context = ,
              apiKey = "6epehk42qjnq", // demo API key
              geo = GEO.GlobalEdgeNetwork,
              user = user,
              token = userToken,
          )
          .build()

  // Join a call, which type is `default` and id is `123`.
  fun joinCall() {
    val call = client.call("default", callId)
    viewModelScope.launch { call.join(create = true) }
  }
}
