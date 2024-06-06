package com.github.se.studybuddies.ui.todo

// import com.github.se.studybuddies.viewModels.ToDoViewModel
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.todo.ToDo
import com.github.se.studybuddies.data.todo.ToDoStatus
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
import com.github.se.studybuddies.ui.shared_elements.SaveButton
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.ToDoListViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

@SuppressLint("SuspiciousIndentation", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateToDo(todoListViewModel: ToDoListViewModel, navigationActions: NavigationActions) {
  val titleState = remember { mutableStateOf("") }
  val descriptionState = remember { mutableStateOf("") }
  val selectedDate = remember { mutableStateOf(LocalDate.now()) }
  val isOpen = remember { mutableStateOf(false) }

  Scaffold(
      modifier = Modifier.fillMaxSize().background(White).testTag("create_todo_scaffold"),
      topBar = {
        TopNavigationBar(
            title = { Sub_title(stringResource(R.string.add_a_new_task)) },
            leftButton = { GoBackRouteButton(navigationActions = navigationActions) },
            rightButton = {})
      }) {
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .background(White)
                    .padding(horizontal = 20.dp, vertical = 80.dp)
                    .testTag("create_todo_column"),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              TodoFields(titleState, descriptionState, selectedDate, isOpen)
              SaveButton(titleState.value.isNotEmpty()) {
                val randomUID = UUID.randomUUID().toString()
                val newTodo =
                    ToDo(
                        uid = randomUID,
                        name = titleState.value,
                        description = descriptionState.value,
                        dueDate = selectedDate.value,
                        status = ToDoStatus.CREATED)
                Log.d("time", "CreateToDo ${newTodo.dueDate}")
                todoListViewModel.addToDo(newTodo)
                navigationActions.goBack()
              }
            }
      }
  if (isOpen.value) {
    CustomDatePickerDialog(
        onAccept = {
          isOpen.value = false
          if (it != null) {
            selectedDate.value = Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
          }
        },
        onCancel = { isOpen.value = false })
  }
}
