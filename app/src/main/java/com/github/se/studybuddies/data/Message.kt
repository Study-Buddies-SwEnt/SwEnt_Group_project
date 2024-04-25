package com.github.se.studybuddies.data

import java.util.UUID

data class Message(
    val uid: String = UUID.randomUUID().toString(),
    val text: String,
    val sender: User,
    val timestamp: Long
) {
  companion object {
    fun empty(): Message {
      return Message(text = "", sender = User.empty(), timestamp = 0)
    }
  }

  fun getTime(): String {
    val date = java.util.Date(timestamp)
    val time = java.text.SimpleDateFormat("HH:mm").format(date)
    return time
  }

  fun getDate(): String {
    val date = java.util.Date(timestamp)
    val time = java.text.SimpleDateFormat("dd MMMM").format(date)
    return time
  }
}

object MessageVal {
  // name of the fields in the database
  const val TIMESTAMP = "timestamp"
  const val TEXT = "text"
  const val SENDER_UID = "senderId"
  // name of the path in the database
  const val GROUPS = "groups"
  const val DIRECT_MESSAGES = "direct_messages"
  const val MEMBERS = "members"
  const val MESSAGES = "messages"
}

enum class MessageType {
  PRIVATE,
  GROUP
}