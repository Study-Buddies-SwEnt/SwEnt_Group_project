package com.github.se.studybuddies.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.Topic
import com.github.se.studybuddies.database.DatabaseConnection
import kotlinx.coroutines.launch

class TopicViewModel(private val uid: String? = null) : ViewModel() {
  private val db = DatabaseConnection()
  private val _topic = MutableLiveData<Topic>(Topic.empty())
  val topic: LiveData<Topic> = _topic

  init {
    if (uid != null) {
      fetchTopicData(uid)
    }
  }

  fun fetchTopicData(uid: String) {
    viewModelScope.launch { _topic.value = db.getTopic(uid) }
  }

  fun createTopic(name: String) {
    viewModelScope.launch { db.createTopic(name, emptyList(), emptyList()) }
  }
}
