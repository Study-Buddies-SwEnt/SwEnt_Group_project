package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class CreateAccountScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CreateAccountScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("create_account") }) {
  val content: KNode = onNode { hasTestTag("accountLazyColumn") }

  val usernameField: KNode = content.child { hasTestTag("username_field") }
  val profileButton: KNode = content.child { hasTestTag("set_picture_button") }
  val saveButton: KNode = content.child { hasTestTag("save_button_account") }
}
