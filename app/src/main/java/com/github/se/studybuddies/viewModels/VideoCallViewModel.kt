package com.github.se.studybuddies.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.Group
import com.github.se.studybuddies.database.DatabaseConnection
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.model.User
import kotlinx.coroutines.launch

class VideoCallViewModel(val groupUID: String, val uid: String) : ViewModel() {

  private val db = DatabaseConnection()
  private val _group = MutableLiveData(Group.empty())
  val group: LiveData<Group> = _group

  private val _userData = MutableLiveData<com.github.se.studybuddies.data.User>()
  val userData: LiveData<com.github.se.studybuddies.data.User> = _userData

  var username: String = ""

  val call = StreamVideo.instance().call("default", groupUID)

  init {
    if (uid != null) {
      fetchUserData(uid)
      viewModelScope.launch {
        _group.value = db.getGroup(groupUID)
        username = db.getCurrentUser().username
      }
    }
  }

  // Join a call, which type is `default` and id is `123`.
  fun joinCall(context: Context): Call {
    viewModelScope.launch {
      val result = call.join(create = true)
      result.onError { Toast.makeText(context, it.message, Toast.LENGTH_LONG).show() }
    }
    return call
  }

  fun leaveCall() {
    viewModelScope.launch { call.leave() }
  }

  fun fetchUserData(uid: String) {
    viewModelScope.launch { _userData.value = db.getUser(uid) }
  }
}
