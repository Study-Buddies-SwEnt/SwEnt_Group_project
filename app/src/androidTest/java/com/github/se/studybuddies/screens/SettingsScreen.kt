package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class SettingsScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SettingsScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("settings_scaffold") }) {

  val topAppBox: KNode = onNode { hasTestTag("top_app_box") }
  val topAppBar: KNode = onNode { hasTestTag("top_app_bar") }
  val goBackButton: KNode = topAppBar.child { hasTestTag("go_back_button") }
  val divider: KNode = onNode { hasTestTag("divider") }
  val mainTitle: KNode = onNode { hasTestTag("main_title") }

  val settingsText: KNode = child { hasTestTag("settings_text") }
}
