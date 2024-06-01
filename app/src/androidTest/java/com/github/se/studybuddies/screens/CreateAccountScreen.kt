package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class CreateAccountScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CreateAccountScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("create_account") }) {
  val createAccountColumn: KNode = onNode { hasTestTag("create_account_column") }

  val usernameField: KNode = createAccountColumn.child { hasTestTag("username_field") }
  val profileButton: KNode = createAccountColumn.child { hasTestTag("set_picture_button") }
  val saveButton: KNode = createAccountColumn.child { hasTestTag("save_button_account") }
}
