package com.github.se.studybuddies.ui.todo

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.todo.ToDo
import com.github.se.studybuddies.data.todo.ToDoStatus
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.ToDoListViewModel
import java.time.Instant
import java.time.ZoneId

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditToDoScreen(
    todoUID: String,
    toDoListViewModel: ToDoListViewModel,
    navigationActions: NavigationActions
) {

  val todo by toDoListViewModel.todo.collectAsState()
  toDoListViewModel.fetchTodoByUID(todoUID)

  if (todo.uid.isEmpty()) {
    Text(
        text = "Loading task...",
        style = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
        modifier =
            Modifier.fillMaxSize().padding(16.dp).wrapContentHeight(Alignment.CenterVertically),
        textAlign = TextAlign.Center)
  } else {

    Log.d("MyPrint", "This viewModel is ${todo.name}")
    Log.d("MyPrint", "UID is: $todoUID")

    val titleState = remember { mutableStateOf(todo.name) }
    val descriptionState = remember { mutableStateOf(todo.description) }
    val selectedDate = remember { mutableStateOf(todo.dueDate) }
    val statusState = remember { mutableStateOf(todo.status) }

    val isOpen = remember { mutableStateOf(false) }
    val expanded = remember { mutableStateOf(false) }

    Scaffold(
          modifier = Modifier.fillMaxSize().background(White),
          topBar = {
              TopNavigationBar(
                  title = { Sub_title(stringResource(R.string.edit_task)) },
                  navigationIcon = {
                      GoBackRouteButton(navigationActions = navigationActions, Route.TODOLIST)
                  },
                  actions = {})
          }) {
      LazyColumn(
          modifier = Modifier.fillMaxSize().background(White)
              .padding(horizontal = 20.dp, vertical = 80.dp),
          verticalArrangement = Arrangement.spacedBy(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          content = {
            item {
              Column(
                  modifier = Modifier.fillMaxWidth(),
                  verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    TodoFields(titleState, descriptionState, selectedDate, isOpen)
                    TodoSaveButton(titleState) {
                      val updatedTodo =
                          ToDo(
                              uid = todoUID,
                              name = titleState.value,
                              description = descriptionState.value,
                              dueDate = selectedDate.value,
                              status = statusState.value)
                      toDoListViewModel.updateToDo(todoUID, updatedTodo)
                      navigationActions.goBack()
                    }
                    Button(
                        onClick = {
                          toDoListViewModel.deleteToDo(todoUID)
                          navigationActions.goBack()
                        },
                        modifier =
                            Modifier.padding(0.dp)
                                .width(300.dp)
                                .height(45.dp)
                                .background(Color.Transparent, shape = RoundedCornerShape(10.dp))
                                .testTag("todoDelete"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                          Icon(
                              painter = painterResource(R.drawable.delete),
                              contentDescription = null,
                              tint = Color.Red,
                              modifier = Modifier.size(36.dp))
                          Spacer(modifier = Modifier.width(8.dp))
                          Text("Delete", color = Color.Red)
                        }
                  }
            }
          })
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
}
