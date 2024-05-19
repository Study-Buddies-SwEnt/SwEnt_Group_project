package com.github.se.studybuddies.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.GroupList
import com.github.se.studybuddies.database.DatabaseConnection
import com.github.se.studybuddies.database.DbRepository
import kotlinx.coroutines.launch

class GroupsHomeViewModel(private val uid: String? = null, private val db: DbRepository = DatabaseConnection()) :
    ViewModel() {
  private val _groups = MutableLiveData(GroupList(emptyList()))
  val groups: LiveData<GroupList> = _groups

  init {
    if (uid != null) {
      fetchGroups(uid)
    }
  }

  fun fetchGroups(uid: String) {
    viewModelScope.launch {
      try {
        val groups = db.getAllGroups(uid)
        _groups.value = groups
      } catch (e: Exception) {
        Log.d("MyPrint", "In ViewModel, could not fetch groups with error: $e")
      }
    }
  }
}
