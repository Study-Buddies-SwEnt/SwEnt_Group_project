package com.github.se.studybuddies.viewModels

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.ChatType
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.database.DatabaseConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MessageViewModel(val chat: Chat) : ViewModel() {

  private val db = DatabaseConnection()
  private val _messages = MutableStateFlow<List<Message>>(emptyList())
  private val _currentUser = MutableLiveData<User>()
  val currentUser = _currentUser

    private val _filterType = mutableStateOf<Class<out Message>?>(null)
    private val _searchQuery = MutableStateFlow<String>("")

//    val messages = _messages.map { messages ->
//        messages.filter { message ->
//            (_filterType.value == null || _filterType.value!!.isInstance(message)) &&
//                    (_searchQuery.value.isEmpty() || (message is Message.TextMessage && message.text.contains(_searchQuery.value, ignoreCase = true)))
//        }.sortedBy { it.timestamp }
//    }
val messages = _messages.combine(_searchQuery) { messages, query ->
    if (query.isEmpty()) {
        messages.sortedBy { it.timestamp }
    } else {
        messages.filter {
            when (it) {
                is Message.TextMessage -> it.text.contains(query, ignoreCase = true)
                is Message.LinkMessage -> it.linkName.contains(query, ignoreCase = true)
                is Message.FileMessage -> it.fileName.contains(query, ignoreCase = true)
                else -> false
            }
        }.sortedBy { it.timestamp }
    }
//    if (_filterType.value != null) {
//        Log.d("MessageViewModel", "messages: of type ${_filterType.value}")
//        messages.filter { _filterType.value!!.isInstance(it) }
//    } else {
//        messages
//    }
}


  init {
    getMessage()
    getCurrentUser()
  }

    fun setFilterType(type: Class<out Message>?) {
        Log.d("MessageViewModel", "setFilterType: $type")
        _filterType.value = type
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

  private fun getMessage() {
    db.getMessages(chat, _messages, Dispatchers.IO, Dispatchers.Main)
  }

  private fun getCurrentUser() {
    viewModelScope.launch { _currentUser.value = db.getCurrentUser() }
  }

  fun sendTextMessage(text: String) {
    val message =
        _currentUser.value?.let {
          Message.TextMessage(text = text, sender = it, timestamp = System.currentTimeMillis())
        }

    sendMessage(message!!)
  }

  fun sendLinkMessage(linkName: String, linkUri: Uri) {
    val message =
        _currentUser.value?.let {
          Message.LinkMessage(
              linkName = linkName,
              linkUri = linkUri,
              sender = it,
              timestamp = System.currentTimeMillis())
        }

    sendMessage(message!!)
  }

  fun sendPhotoMessage(photoUri: Uri) {
    val message =
        _currentUser.value?.let {
          Message.PhotoMessage(
              photoUri = photoUri, sender = it, timestamp = System.currentTimeMillis())
        }
    sendMessage(message!!)
  }

  fun sendFileMessage(fileName: String, fileUri: Uri) {
    val message =
        _currentUser.value?.let {
          Message.FileMessage(
              fileName = fileName,
              fileUri = fileUri,
              sender = it,
              timestamp = System.currentTimeMillis())
        }
    sendMessage(message!!)
  }

  private fun sendMessage(message: Message) {
    if (chat.type == ChatType.TOPIC)
        db.sendMessage(chat.uid, message, chat.type, chat.additionalUID)
    else db.sendMessage(chat.uid, message, chat.type)
  }

  fun deleteMessage(message: Message) {
    if (!isUserMessageSender(message)) return
    else {
      db.deleteMessage(chat.uid, message, chat.type)
      _messages.value = _messages.value.filter { it.uid != message.uid }
    }
  }

  fun editMessage(message: Message, newText: String) {
    if (!isUserMessageSender(message)) return
    // Check if the message UID exists in the list before proceeding.
    val messageExists = _messages.value.any { it.uid == message.uid }
    if (!messageExists) return

    // Update the message in the database
    db.editMessage(chat.uid, message, chat.type, newText)

    // Update the message in the local state
    _messages.value =
        _messages.value.map {
          if (it.uid == message.uid) {
            when (it) {
              is Message.TextMessage -> {
                it.copy(text = newText)
              }
              is Message.LinkMessage -> {
                it.copy(linkUri = Uri.parse(newText))
              }
              else -> {
                it
              }
            }
          } else it
        }
  }

  fun isUserMessageSender(message: Message): Boolean {
    return if (_currentUser.value != null) {
      message.sender.uid == _currentUser.value!!.uid
    } else {
      false
    }
  }
}
