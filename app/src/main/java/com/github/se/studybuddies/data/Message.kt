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
