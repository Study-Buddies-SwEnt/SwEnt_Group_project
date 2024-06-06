package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class PlaceHolderScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<PlaceHolderScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("placeholder_scaffold") }) {

  val topAppBox: KNode = onNode { hasTestTag("top_app_box") }
  val topAppBar: KNode = topAppBox.child { hasTestTag("top_app_bar") }
  val goBackButton: KNode = topAppBar.child { hasTestTag("go_back_button") }
  val divider: KNode = onNode { hasTestTag("divider") }

  // Structural elements of the UI
  val placeholderCol: KNode = onNode { hasTestTag("placeholder_column") }
  val placeHolderText: KNode = placeholderCol.child { hasTestTag("placeholder_text") }
}
