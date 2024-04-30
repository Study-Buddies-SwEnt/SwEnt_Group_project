package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class MapScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<MapScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("Map_menu") }) {

  val drawerScaffold: KNode = onNode { hasTestTag("Map_drawer_scaffold") }
  val mapScreen: KNode = drawerScaffold.child { hasTestTag("mapScreen") }
  // Structural elements of the UI

  val mapIcon: KNode = onNode { hasTestTag("mapIcon") }
}
