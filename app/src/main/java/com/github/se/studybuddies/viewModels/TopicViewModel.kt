package com.github.se.studybuddies.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.ItemArea
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
    viewModelScope.launch { db.createTopic(name) }
  }

  fun createTopicFolder(name: String, area: ItemArea) {
    val folder = db.createTopicFolder(name)
    when (area) {
      ItemArea.EXERCISES -> {
        val updatedExercises = _topic.value?.exercises?.toMutableList()?.apply { add(folder) }
        if (uid != null && updatedExercises != null) {
          _topic.value?.let { db.updateTopicData(uid, it.name, updatedExercises, it.theory) }
        }
      }
      ItemArea.THEORY -> {
        val updatedTheory = _topic.value?.theory?.toMutableList()?.apply { add(folder) }
        if (uid != null && updatedTheory != null) {
          _topic.value?.let { db.updateTopicData(uid, it.name, it.exercises, updatedTheory) }
        }
      }
    }
  }

  fun createTopicFile(name: String, area: ItemArea) {
    val file = db.createTopicFile(name)
    when (area) {
      ItemArea.EXERCISES -> {
        val updatedExercises = _topic.value?.exercises?.toMutableList()?.apply { add(file) }
        if (uid != null && updatedExercises != null) {
          _topic.value?.let { db.updateTopicData(uid, it.name, updatedExercises, it.theory) }
        }
      }
      ItemArea.THEORY -> {
        val updatedTheory = _topic.value?.theory?.toMutableList()?.apply { add(file) }
        if (uid != null && updatedTheory != null) {
          _topic.value?.let { db.updateTopicData(uid, it.name, it.exercises, updatedTheory) }
        }
      }
    }
  }

  fun updateTopicName(name: String) {
    if (uid != null) {
      _topic.value?.let { db.updateTopicData(uid, name, it.exercises, it.theory) }
    }
  }
}
