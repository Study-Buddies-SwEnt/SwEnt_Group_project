package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class CreateGroupScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CreateGroupScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("CreateGroup") }) {

  // Structural elements of the UI
  val groupTitle: KNode = child { hasTestTag("CreateGroupTitle") }
}
