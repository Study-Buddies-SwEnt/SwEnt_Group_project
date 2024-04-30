package com.github.se.studybuddies.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.getstream.video.android.core.GEO
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.model.User
import kotlinx.coroutines.launch

class VideoCallViewModel(private val uid: String? = null, context: Context) : ViewModel() {

  val userToken = "REPLACE_WITH_TOKEN"
  val userId = "REPLACE_WITH_bitter-shape-1"
  val callId = "REPLACE_WITH_CALL_ID"
  val apiKey = "6epehk42qjnq"
  val email = "REPLACE_WITH_EMAIL"
  val username = "REPLACE_WITH_NAME"
  val photoUrl = "REPLACE_WITH_IMAGE_URL"

  // Initialize StreamVideo. For a production app we recommend adding the client to your Application
  // class or di module.
  private val client =
      StreamVideoBuilder(
              context = context,
              apiKey = apiKey, // demo API key
              geo = GEO.GlobalEdgeNetwork,
              user = User(id = userId, role = email, name = username, image = photoUrl),
              token = userToken,
          )
          .build()
  val call = client.call("default", callId)

  // Join a call, which type is `default` and id is `123`.
  fun joinCall() {
    viewModelScope.launch { call.join(create = true) }
  }
}
