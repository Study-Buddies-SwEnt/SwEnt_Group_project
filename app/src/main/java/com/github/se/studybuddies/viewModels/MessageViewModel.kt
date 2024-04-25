package com.github.se.studybuddies.viewModels

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.se.studybuddies.data.Group
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.data.MessageVal
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.database.DatabaseConnection
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MessageViewModel(val groupUID: String) : ViewModel() {

  val db = DatabaseConnection()
  private val dbRef = db.rt_db.getReference(db.getGroupMessagesPath(groupUID))
  private val _messages = MutableStateFlow<List<Message>>(emptyList())
  val messages = _messages.map { messages -> messages.sortedBy { it.timestamp } }
  private val _currentUserUID = MutableLiveData<String>()
  private val _currentUser = MutableLiveData<User>()

  init {
    getCurrentUserUID()
    Log.d("MyPrint", "current user id is ${_currentUserUID.value}")
    if (_currentUserUID.value != null) {
      if (_currentUserUID.value!!.isNotBlank()) {
        Log.d("MyPrint", "User exists with uid ${_currentUserUID.value}")
        listenToMessages()
        getCurrentUser()
      } else Log.d("MyPrint", "User not defined yet")
    } else Log.d("MyPrint", "User ID is null")
  }

  private fun listenToMessages() {
    dbRef.addValueEventListener(
        object : ValueEventListener {
          private var handler = Handler(Looper.getMainLooper())
          private var runnable: Runnable? = null

          override fun onDataChange(snapshot: DataSnapshot) {
            runnable?.let { handler.removeCallbacks(it) }
            runnable = Runnable {
              val newMessages = mutableListOf<Message>()
              snapshot.children.forEach { postSnapshot ->
                viewModelScope.launch {
                  val message =
                      Message(
                          postSnapshot.key.toString(),
                          postSnapshot.child(MessageVal.TEXT).value.toString(),
                          db.getUser(postSnapshot.child(MessageVal.SENDER_UID).value.toString()),
                          postSnapshot.child(MessageVal.TIMESTAMP).value.toString().toLong())
                  newMessages.add(message)
                }
              }
              // Now update _messages.value with the new list
              _messages.value = newMessages
            }
            handler.postDelayed(runnable!!, 500) // Delay the execution
          }

          override fun onCancelled(error: DatabaseError) {
            Log.w("MessageViewModel", "Failed to read value.", error.toException())
          }
        })
  }

  fun getGroup(): Group {
    viewModelScope.launch {
      return db.getGroup(groupUID)
    }
  }

  private fun getCurrentUserUID() {
    viewModelScope.launch {
      val currentUserUID = db.getCurrentUserUID()
      _currentUserUID.value = currentUserUID
    }
  }

  private fun getCurrentUser() {
    viewModelScope.launch {
      val currentUser = db.getCurrentUser()
      _currentUser.value = currentUser
    }

  }

  fun sendMessage(text: String) {
    val message =
        _currentUser.value?.let {
          Message(text = text, sender = it, timestamp = System.currentTimeMillis())
        }

    if (message != null) {
      db.sendGroupMessage(groupUID, message)
    } else Log.d("MyPrint", "message is null, could not retrieve")
  }

  fun deleteMessage(message: Message) {
    if (!isUserMessageSender(message)) return
    else {
      db.deleteMessage(groupUID, message)
      _messages.value = _messages.value.filter { it.uid != message.uid }
    }
  }

  fun editMessage(message: Message, newText: String) {
    if (!isUserMessageSender(message)) return
    else {
      db.editMessage(groupUID, message, newText)
      _messages.value =
          _messages.value.map {
            if (it.uid == message.uid) {
              it.copy(text = newText)
            } else {
              it
            }
          }
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
