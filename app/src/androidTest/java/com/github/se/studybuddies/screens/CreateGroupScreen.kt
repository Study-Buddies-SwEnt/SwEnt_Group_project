package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class CreateGroupScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CreateGroupScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("create_group") }) {
  val content: KNode = onNode { hasTestTag("content") }

  val groupNameField: KNode = content.child { hasTestTag("group_name_field") }
  val profileButton: KNode = content.child { hasTestTag("set_picture_button") }
  val saveButton: KNode = content.child { hasTestTag("save_button") }
}
