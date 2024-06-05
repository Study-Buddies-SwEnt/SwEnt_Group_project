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
    val todoColumn: KNode=onNode { hasTestTag("todo_column") }

    val customSearchBar: KNode = topAppBar.child { hasTestTag("custom_search_bar") }
    val searchBarIcon: KNode = customSearchBar.child { hasTestTag("custom_search_bar_icon") }
    val searchBarCLear: KNode = customSearchBar.child { hasTestTag("custom_search_bar_clear") }
    val noResultText: KNode = customSearchBar.child { hasTestTag("no_result_text") }
}