package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class GroupSettingScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<GroupSettingScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("groupSettingScaffold") }) {

  val topAppBox: KNode = child { hasTestTag("top_app_box") }
  val topAppBar: KNode = topAppBox.child { hasTestTag("top_app_bar") }
  val goBackButton: KNode = topAppBar.child { hasTestTag("go_back_button") }
  val divider: KNode = onNode { hasTestTag("divider") }
}
