package com.github.se.studybuddies.viewModels

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.data.MessageVal
import com.github.se.studybuddies.database.DatabaseConnection
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class MessageViewModel(val groupUID: String) : ViewModel() {

  val db = DatabaseConnection()
  private val dbRef = db.rt_db.getReference(db.getGroupMessagesPath(groupUID))
  private val _messages = MutableStateFlow<List<Message>>(emptyList())
  val messages = _messages.map { messages -> messages.sortedBy { it.timestamp } }

  init {
    listenToMessages()
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
            handler.postDelayed(runnable!!, 500) // Delay the execution
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
