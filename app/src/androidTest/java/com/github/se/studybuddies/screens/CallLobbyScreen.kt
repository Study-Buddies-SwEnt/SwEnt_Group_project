package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class CallLobbyScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CallLobbyScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("call_lobby") }) {
  val content: KNode = onNode { hasTestTag("content") }

  val topAppBar: KNode = content.child { hasTestTag("top_app_box") }
  val callIcon: KNode = content.child { hasTestTag("phone_icon") }
  val previewText: KNode = content.child { hasTestTag("preview_text") }
  val callLobby: KNode = content.child { hasTestTag("call_preview") }
  val joinCallButton: KNode = content.child { hasTestTag("join_call_button") }
}
