package com.github.se.studybuddies.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.studybuddies.data.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MessageViewModel(val groupUID: String) : ViewModel() {

  val db = DatabaseConnection()
  private val dbRef = db.rt_db.getReference("groups/${groupUID}/messages/")
  private val _messages = MutableStateFlow<List<Message>>(emptyList())
  // TODO : sort messages by timestamp
  val messages = _messages.asStateFlow()

  init {
    listenToMessages()
  }

  private fun listenToMessages() {
    dbRef.addValueEventListener(
        object : ValueEventListener {
          override fun onDataChange(snapshot: DataSnapshot) {
            for (postSnapshot in snapshot.children) {
              val message =
                  Message(
                      // TODO replace hardcoded strings with constants
                      postSnapshot.key.toString(),
                      postSnapshot.child("text").value.toString(),
                      db.getUser(postSnapshot.child("senderId").value.toString()),
                      // TODO : crash when sending message
                      //
                      // postSnapshot.child("timestamp").value.toString().toLong()
                      1234567890)
              // Only add the message if it is not already in the list
              if (!_messages.value.contains(message)) {
                _messages.value += message
              }
            }
          }

          override fun onCancelled(error: DatabaseError) {
            Log.w("MessageViewModel", "Failed to read value.", error.toException())
          }
        })
  }

  fun sendMessage(text: String) {
    val message =
        Message(text = text, sender = db.getCurrentUser(), timestamp = System.currentTimeMillis())
    db.sendGroupMessage(groupUID, message)
  }

  fun isUserMessageSender(message: Message): Boolean {
    return message.sender.uid == db.getCurrentUser().uid
  }
}
