package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class DirectMessageScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<DirectMessageScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("direct_message_screen") }) {

  val addPrivateMessageButton: KNode = onNode { hasTestTag("add_private_message_button") }

  val addUser: KNode = onNode { hasTestTag("add_private_message") }
  val allUserList: KNode = addUser.child { hasTestTag("all_users_list") }
  val userItem: KNode = allUserList.child { hasTestTag("user_item") }
  val userItemName: KNode = userItem.child { hasTestTag("chat_name") }
  val userItemProfilePicture: KNode = userItem.child { hasTestTag("chat_user_profile_picture") }

  val directMessagesEmpty: KNode = onNode { hasTestTag("direct_messages_empty") }
  val directMessagesNotEmpty: KNode = onNode { hasTestTag("direct_messages_not_empty") }

  val directMessagesList: KNode =
      directMessagesNotEmpty.child { hasTestTag("direct_messages_list") }
  val directMessageItem: KNode = directMessagesList.child { hasTestTag("chat_item") }
  val directMessageItemName: KNode = directMessageItem.child { hasTestTag("chat_name") }
  val directMessageItemProfilePicture: KNode =
      directMessageItem.child { hasTestTag("chat_user_profile_picture") }
}
