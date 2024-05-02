package com.github.se.studybuddies.viewModels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.Group
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.database.DatabaseConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupViewModel(uid: String? = null) : ViewModel() {
  private val db = DatabaseConnection()
  private val _group = MutableLiveData(Group.empty())
  private val _members = MutableLiveData<List<User>>(emptyList())
  val members: LiveData<List<User>> = _members
  val group: LiveData<Group> = _group

  init {
    if (uid != null) {
      fetchGroupData(uid)
      fetchUsers()
    }
  }

  fun fetchGroupData(uid: String) {
    viewModelScope.launch { _group.value = db.getGroup(uid) }
  }

  fun createGroup(name: String, photoUri: Uri) {
    viewModelScope.launch { db.createGroup(name, photoUri) }
  }

  suspend fun getDefaultPicture(): Uri {
    return withContext(Dispatchers.IO) { db.getDefaultPicture() }
  }

  fun fetchUsers() {
    viewModelScope.launch {
      _group.value?.members?.let { memberIds ->
        val users =
            memberIds.map { uid ->
              db.getUser(uid) // Assuming getUser is a suspend function
            }
        _members.postValue(users)
      }
    }
  }
}
