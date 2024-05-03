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

  fun saveTopic(name: String) {
    uid?.let { updateTopicName(name) }
  }

  fun deleteTopic() {
    uid?.let { topicId -> viewModelScope.launch { db.deleteTopic(uid) } }
  }
  /*
  @SuppressLint("CoroutineCreationDuringComposition")
  fun applyDeletions(deletions: Set<String>, onComplete: @Composable () -> Unit) {
    viewModelScope.launch {
      try {
        deletions.forEach { itemId -> db.deleteTopicItem(topic.value.uid, itemId) }
        // Refresh or update the local topic data after deletions
        fetchTopicData(topic.value.uid)
      } catch (e: Exception) {
        // Handle exceptions that may occur during the deletion process
        Log.e("TopicViewModel", "Error deleting topic items: $e")
      }
    }
  }*/

  fun fetchTopicData(uid: String) {
    viewModelScope.launch {
      _topic.value = db.getTopic(uid)
      Log.d("MyPrint", "Topic data fetched")
    }
  }

  fun createTopic(name: String) {
    viewModelScope.launch { db.createTopic(name) }
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
