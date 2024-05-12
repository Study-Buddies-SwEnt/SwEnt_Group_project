package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class SoloStudyScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SoloStudyScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("Solo Study_menu") }) {

    val drawerScaffold: KNode = onNode { hasTestTag("Solo Study_drawer_scaffold") }
    val soloStudyScreen: KNode = drawerScaffold.child { hasTestTag("solo_study_home") }
    val row1 : KNode = soloStudyScreen.child { hasTestTag("solo_study_row1") }
    val row2 : KNode = soloStudyScreen.child { hasTestTag("solo_study_row2") }
    val flashCardButton: KNode = row1.child { hasTestTag("Flash Card_button") }
    val todoListButton: KNode = row1.child { hasTestTag("ToDo List_button") }
    val timerButton: KNode = row2.child { hasTestTag("Timer_button") }
    val calendarButton: KNode = row2.child { hasTestTag("Calendar_button") }
  //val soloStudyButton: KNode = onNode { hasTestTag("solo_study_button") }
  //sval soloStudyButtonText: KNode = onNode { hasTestTag("solo_study_button_text") }
}
