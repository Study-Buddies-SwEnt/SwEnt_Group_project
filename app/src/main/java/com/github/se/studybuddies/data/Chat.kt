package com.github.se.studybuddies.data

import android.net.Uri

data class Chat(
    val uid: String,
    var name: String,
    var picture: Uri,
    val type: ChatType,
    var members: List<User>,
    val additionalUID: String = "",
    val contactID: String
    //    var messages: List<Message>
) {
  companion object {
    fun empty(): Chat {
      return Chat(
          uid = "",
          name = "",
          picture = Uri.EMPTY,
          type = ChatType.GROUP,
          members = emptyList(),
          contactID = ""
      )
    }
  }
}

enum class ChatType {
  PRIVATE,
  GROUP,
  TOPIC
}

object ChatVal {
  const val GROUPS = "groups"
  const val DIRECT_MESSAGES = "direct_messages"
  const val MEMBERS = "members"
  const val MESSAGES = "messages"
  const val TOPICS = "topics"
}
