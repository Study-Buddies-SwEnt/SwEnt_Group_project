package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class AccountSettingsScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<AccountSettingsScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("account_settings") }) {
  val content: KNode = onNode { hasTestTag("content") }
  val signOutButton: KNode = content.child { hasTestTag("sign_out_button") }
}
