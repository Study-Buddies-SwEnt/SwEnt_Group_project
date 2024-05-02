package com.github.se.studybuddies.data

data class Chat(
    val uid: String,
    var name: String,
    var photoUrl: String,
    val type: ChatType,
    var members: List<User>,
//    var messages: List<Message>
) {
  companion object {
    fun empty(): Chat {
      return Chat(
          uid = "",
          name = "",
          photoUrl = "",
          type = ChatType.GROUP,
          members = emptyList(),
//          messages = emptyList()
      )
    }

    fun withId(uid: String, type: ChatType): Chat {
      return Chat(
          uid = uid,
          name = "",
          photoUrl = "",
          type = type,
          members = emptyList(),
//          messages = emptyList()
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
