package com.github.se.studybuddies.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.ItemArea
import com.github.se.studybuddies.data.Topic
import com.github.se.studybuddies.database.DbRepository
import com.github.se.studybuddies.database.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TopicViewModel(
    private val uid: String? = null,
    private val db: DbRepository = ServiceLocator.provideDatabase()
) : ViewModel() {
  private val _topic = MutableStateFlow<Topic>(Topic.empty())
  val topic: StateFlow<Topic> = _topic

  init {
    if (uid != null) {
      fetchTopicData(uid)
    }
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
      val task = db.getTopic(uid)
      task.sortItems()
      _topic.value = task
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

  fun createTopicFolder(name: String, area: ItemArea, parentUID: String) {
    if (uid == null) return
    db.createTopicFolder(name, parentUID) { folder ->
      when (area) {
        ItemArea.EXERCISES -> {
          if (parentUID.isBlank()) {
            db.addExercise(uid, folder)
          }
        }
        ItemArea.THEORY -> {
          if (parentUID.isBlank()) {
            db.addTheory(uid, folder)
          }
        }
      }
      fetchTopicData(uid)
    }
  }

  fun createTopicFile(name: String, area: ItemArea, parentUID: String) {
    if (uid == null) return
    db.createTopicFile(name, parentUID) { file ->
      when (area) {
        ItemArea.EXERCISES -> {
          if (parentUID.isBlank()) {
            db.addExercise(uid, file)
          }
        }
        ItemArea.THEORY -> {
          if (parentUID.isBlank()) {
            db.addTheory(uid, file)
          }
        }
      }
      fetchTopicData(uid)
    }
  }

  fun updateTopicName(name: String) {
    if (uid != null) {
      db.updateTopicName(uid, name)
      fetchTopicData(uid)
    }
  }
  fun deleteTopic(topicID: String, groupUID: String, callBack: () -> Unit) {
    viewModelScope.launch { db.deleteTopic(topicID, groupUID) { callBack() } }
  }

  fun deleteTopic(topicID: String, groupUID: String, callBack: () -> Unit) {
    viewModelScope.launch { db.deleteTopic(topicID, groupUID) { callBack() } }
  }
}
