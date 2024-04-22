package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class SoloStudyScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SoloStudyScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("solo_study_home") }) {

  // val drawerScaffold: KNode = onNode { hasTestTag("drawer_scaffold") }
  // val soloStudyHome: KNode = onNode { hasTestTag("solo_study_home") }
  // val TimerButton : KNode = onNode { hasTestTag("Timer button") }
  val soloStudyButton: KNode = onNode { hasTestTag("Timer_button") }
  val soloStudyButtonText: KNode = onNode { hasTestTag("solo_study_button_text") }
}
