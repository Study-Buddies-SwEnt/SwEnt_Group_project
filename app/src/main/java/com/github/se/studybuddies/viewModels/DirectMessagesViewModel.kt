package com.github.se.studybuddies.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.database.DbRepository
import com.github.se.studybuddies.database.ServiceLocator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DirectMessagesViewModel(
    private val userUid: String = "",
    private val db: DbRepository = ServiceLocator.provideDatabase()
) : ViewModel() {

  private val _userUid = MutableStateFlow(userUid)
  private val _directMessages = MutableStateFlow<List<Chat>>(emptyList())
  val directMessages =
      _directMessages.asStateFlow().map { directMessages -> directMessages.sortedBy { it.name } }

  init {
    viewModelScope.launch {
      _userUid.collect { userUid ->
        if (userUid.isNotEmpty()) {
          db.subscribeToPrivateChats(userUid, viewModelScope, Dispatchers.IO, Dispatchers.Main) {
              chats ->
            _directMessages.value = chats
          }
        }
      }
    }
  }

  fun setUserUID(userUID: String) {
    if (_userUid.value != userUID) {
      _userUid.value = userUID
    }
  }

  fun startDirectMessage(messageUserUID: String): String {
    var contactID = ""
    viewModelScope.launch { contactID = db.startDirectMessage(messageUserUID) }
    return contactID
  }
}
