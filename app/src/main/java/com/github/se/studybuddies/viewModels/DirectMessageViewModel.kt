package com.github.se.studybuddies.viewModels

import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.database.DatabaseConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class DirectMessageViewModel(val userUid: String) {
  private val db = DatabaseConnection()
  private val _directMessages = MutableStateFlow<List<Chat>>(emptyList())
  val directMessages = _directMessages.map { directMessages -> directMessages.sortedBy { it.name } }

  init {
    getDirectMessagesList()
  }

  private fun getDirectMessagesList() {
    db.getPrivateChatsList(userUid, _directMessages)
  }

  fun startDirectMessage(messageUserUID: String) {
    db.startDirectMessage(messageUserUID)
  }
}
