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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(val uid: String? = null) : ViewModel() {
  private val db = DatabaseConnection()
  private val _userData = MutableLiveData<User>()
  val userData: LiveData<User> = _userData

  init {
    if (uid != null) {
      fetchUserData(getCurrentUserUID())
      Log.d("MyPrint", "UserViewModel initialized with uid $uid")
    } else {
      Log.d("MyPrint", "UserViewModel initialized without uid")
    }
  }

  fun getCurrentUserUID(): String {
    return db.getCurrentUserUID()
  }

  fun fetchUserData(uid: String) {
    viewModelScope.launch { _userData.value = db.getUser(uid) }
  }

  suspend fun getDefaultProfilePicture(): Uri {
    return withContext(Dispatchers.IO) { db.getDefaultProfilePicture() }
  }

  fun createUser(uid: String, email: String, username: String, profilePictureUri: Uri) {
    db.createUser(uid, email, username, profilePictureUri)
  }

  fun updateUserData(uid: String, email: String, username: String, profilePictureUri: Uri) {
    db.updateUserData(uid, email, username, profilePictureUri)
  }
}
