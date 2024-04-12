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
}

object MessageVal {
  // name of the fields in the database
  const val TIMESTAMP = "timestamp"
  const val TEXT = "text"
  const val SENDER_UID = "senderId"
  // name of the path in the database
  const val GROUPS = "groups"
  const val MESSAGES = "messages"
}
