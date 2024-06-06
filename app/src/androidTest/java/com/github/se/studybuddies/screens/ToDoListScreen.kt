package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class ToDoListScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ToDoListScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("todo_list_scaffold") }) {

  val topAppBox: KNode = onNode { hasTestTag("top_app_box") }
  val topAppBar: KNode = topAppBox.child { hasTestTag("top_app_bar") }
  val goBackButton: KNode = topAppBar.child { hasTestTag("go_back_button") }
  val divider: KNode = onNode { hasTestTag("divider") }

  val addToDoButton: KNode = onNode { hasTestTag("add_todo_button") }
  val noTaskText: KNode = onNode { hasTestTag("no_task_text") }
  val todoListColumn: KNode = onNode { hasTestTag("todo_list_col") }

  val testTodo1Box: KNode = todoListColumn.child { hasTestTag("testTodo1_box") }
  val testTodo1Row: KNode = testTodo1Box.child { hasTestTag("testTodo1_row") }
  val testTodo1Column: KNode = testTodo1Row.child { hasTestTag("testTodo1_column") }
  val testTodo1Date: KNode = testTodo1Column.child { hasTestTag("testTodo1_date") }
  val testToDo1Name: KNode = testTodo1Column.child { hasTestTag("testTodo1_name") }
  val testTodo1StatusText: KNode = testTodo1Row.child { hasTestTag("testTodo1_status_text") }
  val testTodo1StatusBox: KNode = testTodo1Row.child { hasTestTag("testTodo1_status_box") }
  val testTodo1StatusButton: KNode =
      testTodo1StatusBox.child { hasTestTag("testTodo1_status_button") }

  val customSearchBar: KNode = topAppBar.child { hasTestTag("custom_search_bar") }
  val searchBarIcon: KNode = customSearchBar.child { hasTestTag("custom_search_bar_icon") }
  val searchBarCLear: KNode = customSearchBar.child { hasTestTag("custom_search_bar_clear") }
}
