package com.github.se.studybuddies.data

data class Chat(
    val uid: String,
    val name: String,
    val photoUrl: String,
    val members: List<User>,
    val messages: List<Message>
) {
    companion object {
        fun empty(): Chat {
            return Chat(
                uid = "",
                name = "",
                photoUrl = "",
                members = emptyList(),
                messages = emptyList()
            )
        }
    }
}

enum class ChatType {
    PRIVATE,
    GROUP
}

object ChatVal {
    const val GROUPS = "groups"
    const val DIRECT_MESSAGES = "direct_messages"
    const val MEMBERS = "members"
    const val MESSAGES = "messages"
}