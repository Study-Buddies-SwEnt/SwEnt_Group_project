package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

/**
 * This class represents the CreateToDo Screen and the elements it contains.
 *
 * It is used to interact with the UI elements during UI tests, incl. grading! You can adapt the
 * test tags if necessary to suit your own implementation, but the class properties need to stay the
 * same.
 *
 * You can refer to Figma for the naming conventions.
 * https://www.figma.com/file/PHSAMl7fCpqEkkSHGeAV92/TO-DO-APP-Mockup?type=design&node-id=435%3A3350&mode=design&t=GjYE8drHL1ACkQnD-1
 */
class CreateToDoScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CreateToDoScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("create_todo_scaffold") }) {

  val screenTitle: KNode = onNode { hasTestTag("createTodoTitle") }

  val saveButton: KNode = onNode { hasTestTag("todoSave") }
  val topAppBox: KNode = onNode { hasTestTag("top_app_box") }
  val topAppBar: KNode = topAppBox.child { hasTestTag("top_app_bar") }
  val goBackButton: KNode = topAppBar.child { hasTestTag("go_back_button") }
  val divider: KNode = onNode { hasTestTag("divider") }

  val createTodoCol: KNode = child { hasTestTag("create_toDo_column") }
  val todoFields: KNode = createTodoCol.child { hasTestTag("toDo_name_field") }

  val inputTitle: KNode = onNode { hasTestTag("inputTodoTitle") }
  val inputDescription: KNode = onNode { hasTestTag("inputTodoDescription") }
  val inputDueDate: KNode = onNode { hasTestTag("inputTodoDate") }

  private val locationDropDownMenuBox = onNode { hasTestTag("locationDropDownMenuBox") }

  val inputLocation: KNode = locationDropDownMenuBox.child { hasTestTag("inputLocation") }
  private val locationDropDownMenu: KNode =
      locationDropDownMenuBox.child { hasTestTag("locationDropDownMenu") }
  val inputLocationProposal: KNode = locationDropDownMenu.child { hasClickAction() }
}
