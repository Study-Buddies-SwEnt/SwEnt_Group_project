package com.github.se.studybuddies.viewModels

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.Group
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.data.MessageVal
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.database.DatabaseConnection
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MessageViewModel(val groupUID: String) : ViewModel() {

  val db = DatabaseConnection()
  private val dbRef = db.rt_db.getReference(db.getGroupMessagesPath(groupUID))
  private val _messages = MutableStateFlow<List<Message>>(emptyList())
  val messages = _messages.map { messages -> messages.sortedBy { it.timestamp } }
  private val _currentUser = MutableLiveData<User>()
  private val _currentGroup = MutableLiveData<Group>()
  val group =
      if (_currentGroup.value != null) _currentGroup.value!!
      else
          Group(
              uid = groupUID,
              name = "Default group name",
              picture =
                  Uri.parse("https://images.pexels.com/photos/6031345/pexels-photo-6031345.jpeg"),
              listOf(
                  "user1",
                  "user2",
                  "user3",
                  "user4",
                  "user5",
                  "user6",
                  "user7",
                  "user8",
                  "user9",
                  "user10"))

  init {
    listenToMessages()
    getCurrentUser()
    getCurrentGroup()
  }

  private fun listenToMessages() {
    dbRef.addValueEventListener(
        object : ValueEventListener {
          private var handler = Handler(Looper.getMainLooper())
          private var runnable: Runnable? = null

          override fun onDataChange(snapshot: DataSnapshot) {
            runnable?.let { handler.removeCallbacks(it) }
            runnable = Runnable {
              viewModelScope.launch {
                val newMessages = mutableListOf<Message>()
                // Collect deferred results for all messages
                val deferredMessages =
                    snapshot.children.map { postSnapshot ->
                      async {
                        Message(
                            postSnapshot.key.toString(),
                            postSnapshot.child(MessageVal.TEXT).value.toString(),
                            db.getUser(postSnapshot.child(MessageVal.SENDER_UID).value.toString()),
                            postSnapshot.child(MessageVal.TIMESTAMP).value.toString().toLong())
                      }
                    }
                // Await all deferred results and add them to the list
                deferredMessages.forEach {
                  val message = it.await() // This ensures all messages are fetched before adding
                  newMessages.add(message)
                }
                // Update LiveData post all messages being fetched and processed
                _messages.value = newMessages
              }
            }
            handler.postDelayed(
                runnable!!, 500) // Delay the execution to allow for batch processing, if needed
          }

          override fun onCancelled(error: DatabaseError) {
            Log.w("MessageViewModel", "Failed to read value.", error.toException())
          }
        })
  }

  private fun getCurrentGroup() {
    viewModelScope.launch {
      val group = db.getGroup(groupUID)
      _currentGroup.value = group
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
