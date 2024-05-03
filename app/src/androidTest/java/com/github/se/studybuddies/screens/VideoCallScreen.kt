package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class VideoCallScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<VideoCallScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("VideoCallScreen") }) {

  val content: KNode = onNode { hasTestTag("ControlActions") }

  val camera: KNode = content.child { hasTestTag("ToggleCameraAction") }
  val microphone: KNode = content.child { hasTestTag("ToggleMicrophoneAction") }
  val flipCamera: KNode = content.child { hasTestTag("FlipCameraAction") }
  val cancelCall: KNode = content.child { hasTestTag("CancelCallAction") }
}
