package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class VideoCallScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<VideoCallScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("video_call_screen") }) {

  val callContent = onNode { hasTestTag("call_content") }
  val controls: KNode = callContent.child { hasTestTag("control_actions") }
}
