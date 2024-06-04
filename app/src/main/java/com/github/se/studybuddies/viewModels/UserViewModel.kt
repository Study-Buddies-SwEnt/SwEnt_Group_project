package com.github.se.studybuddies.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.database.DbRepository
import com.github.se.studybuddies.database.ServiceLocator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(
    val uid: String? = null,
    private val db: DbRepository = ServiceLocator.provideDatabase()
) : ViewModel() {
  private val _userData = MutableLiveData<User>()
  val userData: LiveData<User> = _userData
  private val _userUid = MutableStateFlow<String>(uid ?: "")
  val userID: StateFlow<String> = _userUid

  init {
    if (uid != null) {
      fetchUserData(uid)
      Log.d("UserVM", "UserViewModel initialized with uid $uid")
    } else {
      Log.d("UserVM", "UserViewModel initialized without uid")
    }
  }

  fun setUserUID(userUID: String) {
    if (_userUid.value != userUID) {
      _userUid.value = userUID
    }
  }

  fun getCurrentUserUID(): String {
    val currentUserUID = db.getCurrentUserUID()
    Log.d("UserVM", "Current UID fetched from UserViewModel is $currentUserUID")
    return currentUserUID
  }

  fun fetchUserData(uid: String) {
    Log.d("UserVM", "fetched user data for id $uid")
    viewModelScope.launch { _userData.value = db.getUser(uid) }
    Log.d("UserVM", "userData.value is ${_userData.value}")
  }

  suspend fun getDefaultProfilePicture(): Uri {
    if (db.isFakeDatabase()) {
      return db.getDefaultPicture()
    } else return withContext(Dispatchers.IO) { db.getDefaultProfilePicture() }
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

  fun isFakeDatabase(): Boolean {
    return db.isFakeDatabase()
  }

  fun getUser(userID: String): User {
    var user = User.empty()
    viewModelScope.launch { user = db.getUser(userID)!! }
    return user
  }
}
