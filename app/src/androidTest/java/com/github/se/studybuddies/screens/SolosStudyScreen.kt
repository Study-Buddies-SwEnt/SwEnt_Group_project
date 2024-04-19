package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class SolosStudyScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SolosStudyScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("Drawer") }) {

  // val content: KNode = onNode { hasTestTag("SoloStudyButton") }
  val drawer_itmer = onNode { hasTestTag("Drawer") }
  val drawer_scaffold = onNode { hasTestTag("DrawerScaffold") }
  // val soloStudyHome = drawer_scaffold.child { hasTestTag("SoloStudyHome") }
  val soloStudyButton: KNode = child { hasTestTag("SoloStudyButton") }
}
