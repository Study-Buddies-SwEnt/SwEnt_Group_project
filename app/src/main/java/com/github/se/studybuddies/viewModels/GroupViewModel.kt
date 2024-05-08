package com.github.se.studybuddies.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.Group
import com.github.se.studybuddies.data.TopicList
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
  private val _topics = MutableLiveData(TopicList(emptyList()))
  val topics: LiveData<TopicList> = _topics

  init {
    if (uid != null) {
      fetchGroupData(uid)
      fetchUsers()
      fetchTopics(uid)
    }
  }

  fun fetchGroupData(uid: String) {
    viewModelScope.launch { _group.value = db.getGroup(uid) }
  }

  fun fetchTopics(uid: String) {
    viewModelScope.launch {
      try {
        val topics = db.getALlTopics(uid)
        _topics.value = topics
      } catch (e: Exception) {
        Log.d("MyPrint", "In ViewModel, could not fetch topics with error ", e)
      }
    }
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
        val users = memberIds.map { uid -> db.getUser(uid) }
        _members.postValue(users)
      }
    }
  }

  fun leaveGroup(groupUID: String, userUID: String = "") {
    viewModelScope.launch { db.removeUserFromGroup(groupUID, userUID) }
  }
}
