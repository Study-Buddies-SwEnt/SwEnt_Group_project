package com.github.se.studybuddies.viewModels

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
    if (_currentUserUID.value != null) {
      listenToMessages()
      getCurrentUser()
    }
  }

  private fun listenToMessages() {
    dbRef.addValueEventListener(
        object : ValueEventListener {
          private var handler = Handler(Looper.getMainLooper())
          private var runnable: Runnable? = null

          override fun onDataChange(snapshot: DataSnapshot) {
            runnable?.let { handler.removeCallbacks(it) }
            runnable = Runnable {
              snapshot.children.forEach { postSnapshot ->
                viewModelScope.launch {
                  val message =
                      Message(
                          postSnapshot.key.toString(),
                          postSnapshot.child(MessageVal.TEXT).value.toString(),
                          db.getUser(postSnapshot.child(MessageVal.SENDER_UID).value.toString()),
                          postSnapshot.child(MessageVal.TIMESTAMP).value.toString().toLong())
                  if (!_messages.value.contains(message)) {
                    _messages.value += message
                  }
                }
              }
            }
            handler.postDelayed(runnable!!, 500) // Delay the execution
          }

          override fun onCancelled(error: DatabaseError) {
            Log.w("MessageViewModel", "Failed to read value.", error.toException())
          }
        })
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

  fun isUserMessageSender(message: Message): Boolean {
    return if (_currentUser.value != null) {
      message.sender.uid == _currentUser.value!!.uid
    } else {
      false
    }
  }
}
