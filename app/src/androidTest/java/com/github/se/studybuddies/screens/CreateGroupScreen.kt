package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

class CreateGroupScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CreateGroupScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("create_group") }) {
  val content: KNode = onNode { hasTestTag("content") }
  val column: KNode = content.child { hasTestTag("CreateGroup") }

  val groupNameField: KNode = column.child { hasTestTag("group_name_field") }
  val profileButton: KNode = column.child { hasTestTag("set_picture_button") }
  val saveButton: KNode = column.child { hasTestTag("save_button") }
}
