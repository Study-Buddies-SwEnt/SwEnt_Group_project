package com.github.se.studybuddies.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.TopicFile
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.database.DbRepository
import com.github.se.studybuddies.database.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TopicFileViewModel(
    private val fileID: String,
    private val db: DbRepository = ServiceLocator.provideDatabase()
) : ViewModel() {
  private val _topicFile = MutableStateFlow<TopicFile>(TopicFile.empty())
  val topicFile: StateFlow<TopicFile> = _topicFile

  init {
    fetchTopicFile(fileID)
  }

  fun fetchTopicFile(id: String) {
    viewModelScope.launch {
      // val task = db.getTopicFile(id)
      // _topicFile.value = task
    }
  }

  fun getStrongUsers(userIDs: List<String>, callBack: (List<User>) -> Unit) {
    viewModelScope.launch {
      val users = mutableListOf<User>()
      userIDs.forEach { userID ->
        val user = db.getUser(userID)!!
        users.add(user)
      }
      callBack(users)
    }
  }
}
