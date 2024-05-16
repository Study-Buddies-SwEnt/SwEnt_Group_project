package com.github.se.studybuddies.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.database.DatabaseConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(val uid: String? = null,  private val db : DatabaseConnection) : ViewModel() {
  private val _userData = MutableLiveData<User>()
  val userData: LiveData<User> = _userData

  init {
    if (uid != null) {
      fetchUserData(uid)
      Log.d("MyPrint", "UserViewModel initialized with uid $uid")
    } else {
      Log.d("MyPrint", "UserViewModel initialized without uid")
    }
  }

  fun getCurrentUserUID(): String {
    val currentUserUID = db.getCurrentUserUID()
    Log.d("MyPrint", "Current UID fetched from UserViewModel is $currentUserUID")
    return currentUserUID
  }

  fun fetchUserData(uid: String) {
    viewModelScope.launch { _userData.value = db.getUser(uid) }
  }

  suspend fun getDefaultProfilePicture(): Uri {
    return withContext(Dispatchers.IO) { db.getDefaultProfilePicture() }
  }

  fun createUser(
      uid: String,
      email: String,
      username: String,
      profilePictureUri: Uri,
      location: String = "offline"
  ) {
    viewModelScope.launch { db.createUser(uid, email, username, profilePictureUri, location) }
  }

  fun updateUserData(uid: String, email: String, username: String, profilePictureUri: Uri) {
    db.updateUserData(
        uid, email, username, profilePictureUri, userData.value?.location ?: "offline")
  }

  fun updateLocation(uid: String, location: String) {
    db.updateLocation(uid, location)
  }

  fun signOut() {
    viewModelScope.cancel()
  }
}
