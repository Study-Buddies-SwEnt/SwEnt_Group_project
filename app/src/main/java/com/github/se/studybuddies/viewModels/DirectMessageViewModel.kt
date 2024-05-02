package com.github.se.studybuddies.viewModels

import androidx.lifecycle.map
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.database.DatabaseConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class DirectMessageViewModel(val userUid: String) {
  private val db = DatabaseConnection()
  private val _directMessages = MutableStateFlow<List<Chat>>(emptyList())
  val directMessages =
      _directMessages.map { directMessages ->
        directMessages.sortedByDescending { it.messages.lastOrNull()?.timestamp ?: Long.MIN_VALUE }
      }

  init {
    getDirectMessagesList()
  }

  private fun getDirectMessagesList() {
    db.getPrivateChatsList(userUid, _directMessages)
  }
}
