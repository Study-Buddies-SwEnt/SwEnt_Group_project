package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class GroupsHomeScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<GroupsHomeScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("GroupsHomeScreen") }) {

  // Structural elements of the UI
  val loginTitle: KNode = child { hasTestTag("LoginTitle") }
  val loginButton: KNode = child { hasTestTag("LoginButton") }

  val textDialogues: KNode = onNode { hasTestTag("LeaveGroupDialogText") }
  val textDialoguesYes: KNode = onNode { hasTestTag("LeaveGroupDialogYesButton") }
  val textDialoguesNo: KNode = onNode { hasTestTag("LeaveGroupDialogNoButton") }
}
