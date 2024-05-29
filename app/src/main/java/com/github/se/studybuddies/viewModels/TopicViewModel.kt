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

  fun fetchTopicData(uid: String, callBack: () -> Unit) {
    viewModelScope.launch {
      db.getTopic(uid) { topic ->
        topic.sortItems()
        _topic.value = topic
        callBack()
      }
      Log.d("MyPrint", "Topic data fetched")
    }
  }

  fun createTopic(name: String, groupUID: String, callBack: () -> Unit) {
    viewModelScope.launch {
      db.createTopic(name) { topicUID ->
        viewModelScope.launch { db.addTopicToGroup(topicUID, groupUID) }
      }
    }
    fetchTopicData(name) { callBack() }
  }

  fun createTopicFolder(name: String, area: ItemArea, parentUID: String, callBack: () -> Unit) {
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
      fetchTopicData(uid) { callBack() }
    }
  }

  fun createTopicFile(name: String, area: ItemArea, parentUID: String, callBack: () -> Unit) {
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
      fetchTopicData(uid) { callBack() }
    }
  }

  fun updateTopicName(name: String, callBack: () -> Unit) {
    if (uid != null) {
      db.updateTopicName(uid, name)
      fetchTopicData(uid) { callBack() }
    }
  }

  fun getIsUserStrong(fileID: String, callBack: (Boolean) -> Unit) {
    viewModelScope.launch { db.getIsUserStrong(fileID) { isUserStrong -> callBack(isUserStrong) } }
  }

  fun updateStrongUser(fileID: String, newValue: Boolean) {
    viewModelScope.launch { db.updateStrongUser(fileID, newValue) }
  }
}
