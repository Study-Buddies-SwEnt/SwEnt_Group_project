package com.github.se.studybuddies.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.ItemArea
import com.github.se.studybuddies.data.Topic
import com.github.se.studybuddies.database.DatabaseConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TopicViewModel(private val uid: String? = null) : ViewModel() {
  private val db = DatabaseConnection()
  private val _topic = MutableStateFlow<Topic>(Topic.empty())
  val topic: StateFlow<Topic> = _topic

  init {
    if (uid != null) {
      fetchTopicData(uid)
    }
  }

  fun fetchTopicData(uid: String) {
    viewModelScope.launch {
      _topic.value = db.getTopic(uid)
      Log.d("MyPrint", "Topic data fetched")
    }
  }

  fun createTopic(name: String, groupUID: String) {
    viewModelScope.launch {
      db.createTopic(name) { topicUID ->
        viewModelScope.launch { db.addTopicToGroup(topicUID, groupUID) }
      }
    }
    fetchTopicData(name)
  }

  fun createTopicFolder(name: String, area: ItemArea) {
    db.createTopicFolder(name) { folder ->
      when (area) {
        ItemArea.EXERCISES -> {
          if (uid != null) {
            db.addExercise(uid, folder)
            fetchTopicData(uid)
          }
        }
        ItemArea.THEORY -> {
          if (uid != null) {
            db.addTheory(uid, folder)
            fetchTopicData(uid)
          }
        }
      }
    }
  }

  fun createTopicFile(name: String, area: ItemArea) {
    db.createTopicFile(name) { file ->
      when (area) {
        ItemArea.EXERCISES -> {
          if (uid != null) {
            db.addExercise(uid, file)
            fetchTopicData(uid)
          }
        }
        ItemArea.THEORY -> {
          val updatedTheory = _topic.value.theory.toMutableList().apply { add(file) }
          if (uid != null) {
            db.addTheory(uid, file)
            fetchTopicData(uid)
          }
        }
      }
    }
  }

  fun updateTopicName(name: String) {
    if (uid != null) {
      db.updateTopicName(uid, name)
      fetchTopicData(uid)
    }
  }
}
