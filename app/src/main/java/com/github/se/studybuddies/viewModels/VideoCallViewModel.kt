package com.github.se.studybuddies.viewModels

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.Group
import com.github.se.studybuddies.database.DatabaseConnection
import io.getstream.video.android.core.GEO
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.model.User
import kotlinx.coroutines.launch
import java.net.URI

class VideoCallViewModel(val groupUID: String? = null, val uid: String, videoContext: Context) : ViewModel() {

    val apiKey = "x52wgjq8qyfc"
  private val db = DatabaseConnection()
  private val _group = MutableLiveData<Group>(Group.empty())
  val group: LiveData<Group> = _group

    private val _userData = MutableLiveData<com.github.se.studybuddies.data.User>()
    val userData: LiveData<com.github.se.studybuddies.data.User> = _userData
  init {
      if (groupUID != null) {
          viewModelScope.launch { _group.value = db.getGroup(groupUID) }
          fetchUserData(db.getCurrentUserUID())
      }
  }

    val userToken = userData.value!!.uid
    val userId = userData.value!!.username
    val callId = group.value.callId


  // Initialize StreamVideo. For a production app we recommend adding the client to your Application
  // class or di module.
  private val client =
      StreamVideoBuilder(
              context = videoContext,
              apiKey = apiKey, // demo API key
              geo = GEO.GlobalEdgeNetwork,
              user = User(id = userId),
              token = userToken,
          )
          .build()


  // Join a call, which type is `default` and id is `123`.
  fun joinCall(context: Context, callType: String) {
      val call = client.call(callType, callId)
      viewModelScope.launch {
        val result = call.join(create = true)
        result.onError {
            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
        }
    }
  }

    fun fetchUserData(uid: String) {
        viewModelScope.launch { _userData.value = db.getUser(uid) }
    }
}
