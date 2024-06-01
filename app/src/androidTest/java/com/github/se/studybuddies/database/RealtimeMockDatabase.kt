package com.github.se.studybuddies.database

import android.util.Log
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.ChatType
import com.github.se.studybuddies.data.ChatVal
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.data.MessageVal
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

class RealtimeMockDatabase : RealtimeRepository {

  private val rtDb = mutableMapOf<String, Map<String, Any>>()

  override fun sendMessage(
      chatUID: String,
      message: Message,
      chatType: ChatType,
      additionalUID: String
  ) {
    when (message) {
      is Message.TextMessage -> {
        rtDb[chatUID] =
            mapOf(
                MessageVal.TEXT to message.text,
                MessageVal.SENDER_UID to message.sender,
                MessageVal.TIMESTAMP to message.timestamp)
      }
      is Message.LinkMessage -> {
        rtDb[chatUID] =
            mapOf(
                MessageVal.LINK to message.linkUri,
                MessageVal.SENDER_UID to message.sender,
                MessageVal.TIMESTAMP to message.timestamp)
      }
      is Message.PollMessage -> {
        rtDb[chatUID] =
            mapOf(
                MessageVal.POLL to message.question,
                MessageVal.SENDER_UID to message.sender,
                MessageVal.TIMESTAMP to message.timestamp)
      }
      else -> {
        Log.d("MyPrint", "Message type not recognized")
      }
    }
  }

  override fun saveMessage(path: String, data: Map<String, Any>) {
    rtDb[path] = data
  }

  override fun deleteMessage(groupUID: String, message: Message, chatType: ChatType) {
    rtDb.remove(groupUID)
  }

  override fun editMessage(
      groupUID: String,
      message: Message,
      chatType: ChatType,
      newText: String
  ) {
    when (message) {
      is Message.TextMessage -> {
        rtDb[groupUID] = mapOf(MessageVal.TEXT to newText)
      }
      is Message.LinkMessage -> {
        rtDb[groupUID] = mapOf(MessageVal.LINK to newText)
      }
      else -> {
        Log.d("MyPrint", "Message type not recognized")
      }
    }
  }

  override fun subscribeToPrivateChats(
      userUID: String,
      scope: CoroutineScope,
      ioDispatcher: CoroutineDispatcher,
      mainDispatcher: CoroutineDispatcher,
      onUpdate: (List<Chat>) -> Unit
  ) {
    // To do
  }

  override fun getMessages(
      chat: Chat,
      liveData: MutableStateFlow<List<Message>>,
      ioDispatcher: CoroutineDispatcher,
      mainDispatcher: CoroutineDispatcher
  ) {
    // TO DO
  }

  override fun votePollMessage(chat: Chat, message: Message.PollMessage) {
    TODO("Not yet implemented")
  }

  override fun checkForExistingChat(
      currentUserUID: String,
      otherUID: String,
      onResult: (Boolean, String?) -> Unit
  ) {
    val query =
        rtDb[ChatVal.DIRECT_MESSAGES]
            ?.filterValues { it is Map<*, *> }
            ?.mapValues { it.value as Map<*, *> }
            ?.filterValues { it[ChatVal.MEMBERS] is List<*> }
            ?.mapValues { it.value[ChatVal.MEMBERS] as List<*> }
            ?.filterValues { otherUID in it }

    object : ValueEventListener {
      override fun onDataChange(snapshot: DataSnapshot) {
        snapshot.children.forEach { chatSnapshot ->
          if (chatSnapshot.hasChild("${ChatVal.MEMBERS}/$otherUID")) {
            onResult(true, chatSnapshot.key)
            return
          }
        }
        onResult(false, null)
      }

      override fun onCancelled(databaseError: DatabaseError) {
        Log.w("DatabaseConnect", "Failed to check for existing chat", databaseError.toException())
        onResult(false, null)
      }
    }
  }

  override fun startDirectMessage(otherUID: String) {}

  override fun getTimerUpdates(groupUID: String, timerValue: MutableStateFlow<Long>): Boolean {

    return false
  }

  override suspend fun removeTopic(uid: String) {}
}
