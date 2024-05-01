package com.github.se.studybuddies.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.TopicList
import com.github.se.studybuddies.database.DatabaseConnection
import kotlinx.coroutines.launch

class TopicsHomeViewModel(private val uid: String? = null) : ViewModel() {
  private val db = DatabaseConnection()
  private val _topics = MutableLiveData(TopicList(emptyList()))
  val topics: LiveData<TopicList> = _topics

  init {
    if (uid != null) {
      fetchTopics(uid)
    }
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
}
