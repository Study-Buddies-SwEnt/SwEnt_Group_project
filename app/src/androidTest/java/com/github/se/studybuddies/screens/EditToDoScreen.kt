package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class EditToDoScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<EditToDoScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("edit_todo_scaffold") }) {

    val topAppBox: KNode = onNode { hasTestTag("top_app_box") }
    val topAppBar: KNode = topAppBox.child { hasTestTag("top_app_bar") }
    val goBackButton: KNode = topAppBar.child { hasTestTag("go_back_button") }
    val divider: KNode = onNode { hasTestTag("divider") }

    val editTodoCol: KNode = onNode { hasTestTag("edit_todo_column") }
    val todoNameField: KNode = editTodoCol.child { hasTestTag("todo_name_field") }
    val todoDescriptionField: KNode = editTodoCol.child { hasTestTag("todo_description_field") }
    val todoDateField: KNode = editTodoCol.child { hasTestTag("todo_date_field") }
    val saveButton: KNode = editTodoCol.child { hasTestTag("save_button") }
    val deleteButton : KNode = editTodoCol.child { hasTestTag("todo_delete") }

    val datePicker: KNode = onNode { hasTestTag("date_picker") }
    val dateConfirmButton: KNode = onNode { hasTestTag("date_confirm_button") }
    val dateDismissButton: KNode = onNode { hasTestTag("date_dismiss_button") }
}
