package com.github.se.studybuddies.ui.todo

// import com.github.se.studybuddies.viewModels.ToDoViewModel

import androidx.compose.runtime.getValue


/*

@Composable
fun EditToDoScreen(todoUID: String, todoViewModel: ToDoViewModel, navigationActions: NavigationActions) {
  val todo by todoViewModel.todo.collectAsState()

  todoViewModel.fetchTodoByUID(todoUID)

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

    Column(modifier = Modifier.fillMaxSize().testTag("editScreen")) {
      TodoTopBar(navigationActions, "Edit task")
      LazyColumn(
          modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
          verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top),
          horizontalAlignment = Alignment.CenterHorizontally,
          content = {
            item {
              Column(
                  modifier = Modifier.fillMaxWidth(),
                  verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    TodoFields(
                        titleState,
                        descriptionState,
                        selectedDate,
                        isOpen)
                    Button(
                        onClick = { expanded.value = true },
                        modifier =
                            Modifier.padding(0.dp)
                                .width(300.dp)
                                .height(45.dp)
                                .background(Color.Transparent, shape = RoundedCornerShape(10.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        border = BorderStroke(1.dp, statusColor(statusState.value))) {
                          Text(
                              text = statusState.value.name, color = statusColor(statusState.value))
                        }
                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false },
                        modifier =
                            Modifier.width(300.dp)
                                .padding(horizontal = 16.dp)
                                .background(
                                    color = Color.Transparent, shape = RoundedCornerShape(10.dp))) {
                          for (status in ToDoStatus.entries) {
                            DropdownMenuItem(
                                text = {
                                  Text(
                                      status.name,
                                      textAlign = TextAlign.Center,
                                      modifier = Modifier.fillMaxWidth())
                                },
                                onClick = {
                                  statusState.value = status
                                  expanded.value = false
                                })
                          }
                        }
                    TodoSaveButton(titleState) {
                      val updatedTodo =
                          ToDo(
                              uid = todoUID,
                              name = titleState.value,
                              description = descriptionState.value,
                              dueDate = selectedDate.value,
                              status = statusState.value)
                      todoViewModel.updateTodo(todoUID, updatedTodo)
                      navigationActions.goBack()
                    }
                    Button(
                        onClick = {
                          todoViewModel.deleteTodo(todoUID)
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

 */
