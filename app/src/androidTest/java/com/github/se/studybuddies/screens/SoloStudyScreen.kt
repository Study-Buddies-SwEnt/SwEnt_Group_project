package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class SoloStudyScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SoloStudyScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("solo_study_home") }) {

  val soloStudyButton: KNode = child { hasTestTag("solo_study_button") }
  val soloStudyButtonText: KNode = soloStudyButton.child { hasTestTag("solo_study_button_text") }
}
