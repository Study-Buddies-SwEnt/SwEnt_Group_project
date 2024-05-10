package com.github.se.studybuddies.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.ChatType
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.database.DatabaseConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MessageViewModel(val chat: Chat) : ViewModel() {

  private val db = DatabaseConnection()
  private val _messages = MutableStateFlow<List<Message>>(emptyList())
  val messages = _messages.map { messages -> messages.sortedBy { it.timestamp } }
  private val _currentUser = MutableLiveData<User>()
  val currentUser = _currentUser

  init {
    getMessage()
    getCurrentUser()
  }

  private fun getMessage() {
    db.getMessages(chat, _messages)
  }

  private fun getCurrentUser() {
    viewModelScope.launch { _currentUser.value = db.getCurrentUser() }
  }

  fun sendTextMessage(text: String) {
    val message =
        _currentUser.value?.let {
          Message.TextMessage(text = text, sender = it, timestamp = System.currentTimeMillis())
        }

    if (message != null) {
      if (chat.type == ChatType.TOPIC)
          db.sendMessage(chat.uid, message, chat.type, chat.additionalUID)
      else db.sendMessage(chat.uid, message, chat.type)
    } else Log.d("MyPrint", "message is null, could not retrieve")
  }

  fun sendPhotoMessage(photoUri: Uri) {
    val message =
        _currentUser.value?.let {
          Message.PhotoMessage(
              photoUri = photoUri, sender = it, timestamp = System.currentTimeMillis())
        }

    if (message != null) {
      if (chat.type == ChatType.TOPIC)
          db.sendMessage(chat.uid, message, chat.type, chat.additionalUID)
      else db.sendMessage(chat.uid, message, chat.type)
    } else Log.d("MyPrint", "message is null, could not retrieve")
  }

  fun deleteMessage(message: Message) {
    if (!isUserMessageSender(message)) return
    else {
      db.deleteMessage(chat.uid, message, chat.type)
      _messages.value = _messages.value.filter { it.uid != message.uid }
    }
  }

  fun editMessage(message: Message.TextMessage, newText: String) {
    if (!isUserMessageSender(message)) return
    // Check if the message UID exists in the list before proceeding.
    val messageExists = _messages.value.any { it.uid == message.uid }
    if (!messageExists) return

    // Update the message in the database
    db.editMessage(chat.uid, message, chat.type, newText)

    // Update the message in the local state
    _messages.value =
        _messages.value.map {
          if (it.uid == message.uid) (it as Message.TextMessage).copy(text = newText) else it
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
