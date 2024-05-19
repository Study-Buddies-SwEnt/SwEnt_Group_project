package com.github.se.studybuddies.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.database.DatabaseConnection
import com.github.se.studybuddies.database.DbRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class UsersViewModel(userUid: String = "", private val db: DbRepository = DatabaseConnection()) :
    ViewModel() {
  private val _userUid = MutableStateFlow(userUid)
  private val _friends = MutableStateFlow<List<User>>(emptyList())
  val friends = _friends.asStateFlow().map { friends -> friends.sortedBy { it.username } }
  val friends_old: StateFlow<List<User>> = _friends

  init {
    viewModelScope.launch {
      _userUid.collect { userUid ->
        if (userUid.isNotEmpty()) {
          fetchFriends(userUid)
        }
      }
    }
  }

  private suspend fun fetchFriends(userUid: String) {
    try {
      val friendsList = db.getAllFriends(userUid)
      _friends.value = friendsList
    } catch (e: Exception) {
      Log.d("UsersViewModel", "Failed to fetch friends: $e")
    }
  }

  fun fetchAllFriends(uid: String) {
    viewModelScope.launch {
      try {
        val users = db.getAllFriends(uid)
        _friends.value = users
      } catch (e: Exception) {
        Log.d("MyPrint", "In ViewModel, could not fetch friends with error $e")
      }
    }
  }

  fun setUserUID(userUID: String) {
    if (_userUid.value != userUID) {
      _userUid.value = userUID
    }
  }
}
