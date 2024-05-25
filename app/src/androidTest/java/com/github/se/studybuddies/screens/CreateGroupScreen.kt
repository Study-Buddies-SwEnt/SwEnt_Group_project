package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasTestTag
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class CreateGroupScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CreateGroupScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("create_group_scaffold") }) {

  // Structural elements of the UI
  val topAppBox: KNode = child { hasTestTag("top_app_box") }
  val topAppBar: KNode = topAppBox.child { hasTestTag("top_app_bar") }
  val goBackButton: KNode = topAppBar.child { hasTestTag("go_back_button") }
  val divider: KNode = onNode { hasTestTag("divider") }

  val createGroupCol: KNode = child { hasTestTag("create_group_column") }
  val groupNameField: KNode = createGroupCol.child { hasTestTag("group_name_field") }
  val saveButton: KNode = createGroupCol.child { hasTestTag("save_button") }
  val profileButton: KNode = createGroupCol.child { hasTestTag("set_picture_button") }

}
