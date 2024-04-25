package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class ChatScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ChatScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("chat_screen") }) {

  val text_field: KNode = onNode { hasTestTag("chat_text_field") }
  val sendButton: KNode = onNode { hasTestTag("chat_send_button") }

  val ownTextBubble: KNode = onNode { hasTestTag("chat_own_text_bubble") }
  val ownMsg: KNode = onNode { hasTestTag("chat_message_text") }
}
