package com.github.se.studybuddies.ui.todo

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.todo.ToDo
import com.github.se.studybuddies.data.todo.nextStatus
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.LightBlue
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.ToDoListViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SuppressLint("RestrictedApi")
@Composable
fun ToDoListScreen(toDoListViewModel: ToDoListViewModel, navigationActions: NavigationActions) {
  val todos by toDoListViewModel.todos.collectAsState()
  val (searchQuery, setSearchQuery) = remember { mutableStateOf("") }

  toDoListViewModel.fetchAllTodos()

  val todoList = remember { mutableStateOf(todos.getAllTasks()) }

  LaunchedEffect(todos) {
    todoList.value =
        if (searchQuery.isNotEmpty()) {
          todos.getFilteredTasks(searchQuery)
        } else {
          todos.getAllTasks()
        }
  }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("overviewScreen"),
      floatingActionButton = {
        FloatingActionButton(
            onClick = { navigationActions.navigateTo(Route.CREATETODO) },
            backgroundColor = Blue,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.testTag("createTodoButton")) {
              Icon(
                  painterResource(R.drawable.edit),
                  tint = Color.White,
                  contentDescription = null,
                  modifier = Modifier.size(32.dp))
            }
      },
      topBar = {
        TopNavigationBar(
            title = { Sub_title(title = "To do") },
            leftButton = {
              GoBackRouteButton(navigationActions = navigationActions)
            },
            rightButton = {
              CustomSearchBar(
                  searchQuery = searchQuery,
                  onSearchQueryChange = setSearchQuery,
                  onSearchAction = {
                    if (searchQuery.isNotEmpty()) {
                      todoList.value = todos.getFilteredTasks(searchQuery)
                    } else {
                      todoList.value = todos.getAllTasks()
                    }
                  },
                  onClearAction = { setSearchQuery("") },
                  noResultFound = todoList.value.isEmpty() && searchQuery.isNotEmpty())
            })
      },
      content = { innerPadding ->
        if (todoList.value.isEmpty()) {
          Text(
              text = "You have no tasks yet. Create one.",
              style = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
              modifier =
                  Modifier.padding(innerPadding)
                      .fillMaxSize()
                      .padding(4.dp)
                      .wrapContentHeight(Alignment.CenterVertically),
              textAlign = TextAlign.Center)
        } else {
          LazyColumn(
              modifier =
                  Modifier.padding(horizontal = 6.dp, vertical = 80.dp)
                      .fillMaxSize()
                      .background(LightBlue)
                      .testTag("todoList"),
              verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
              horizontalAlignment = Alignment.CenterHorizontally,
              content = {
                items(todoList.value) { todo ->
                  ToDoItem(todo, navigationActions, toDoListViewModel)
                }
              })
        }
      })
}

@Composable
fun ToDoItem(
    todo: ToDo,
    navigationActions: NavigationActions,
    toDoListViewModel: ToDoListViewModel
) {
  Box(
      modifier =
          Modifier.background(color = White, shape = RoundedCornerShape(size = 10.dp))
              .border(color = Blue, width = 2.dp, shape = RoundedCornerShape(size = 10.dp))
              .fillMaxWidth()
              .clickable {
                val todoUID = todo.uid
                navigationActions.navigateTo("${Route.EDITTODO}/$todoUID")
              }
              .testTag("todoListItem")) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically) {
              Column(
                  modifier =
                      Modifier.fillMaxHeight().fillMaxWidth(0.5F).padding(12.dp).clickable {
                        val todoUID = todo.uid
                        navigationActions.navigateTo("${Route.EDITTODO}/$todoUID")
                      },
              ) {
                Text(
                    text = formatDate(todo.dueDate),
                    style = TextStyle(fontSize = 12.sp),
                    lineHeight = 16.sp,
                    modifier = Modifier.align(Alignment.Start))
                Text(
                    text = todo.name,
                    style = TextStyle(fontSize = 16.sp),
                    lineHeight = 28.sp,
                    modifier = Modifier.align(Alignment.Start))
              }
              Text(
                  text = todo.status.name,
                  style = TextStyle(fontSize = 18.sp, color = statusColor(todo.status)),
              )
              Box(
                  modifier =
                      Modifier.size(60.dp)
                          .clickable {
                            nextStatus(todo)
                            toDoListViewModel.updateToDo(todo.uid, todo)
                            navigationActions.navigateTo(Route.TODOLIST)
                          }
                          .background(Color.Transparent)
                          .padding(8.dp),
                  contentAlignment = Alignment.Center) {
                    Button(
                        onClick = {},
                        colors =
                            ButtonColors(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Transparent,
                                Color.Transparent),
                        modifier =
                            Modifier.width(20.dp)
                                .height(20.dp)
                                .background(color = Color.Transparent, shape = CircleShape)
                                .border(BorderStroke(width = 4.dp, Blue), shape = CircleShape)
                                .padding(40.dp)) {}
                  }
            }
      }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CustomSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchAction: () -> Unit,
    onClearAction: () -> Unit,
    noResultFound: Boolean
) {
  val keyboard = LocalSoftwareKeyboardController.current

  TextField(
      value = searchQuery,
      onValueChange = { onSearchQueryChange(it) },
      placeholder = { Text("Search a Task", color = Blue, fontSize = 20.sp) },
      singleLine = true,
      modifier =
          Modifier.padding(start = 26.dp, top = 26.dp, end = 26.dp, bottom = 8.dp)
              .width(360.dp)
              .height(80.dp)
              .testTag("searchTodo"),
      shape = RoundedCornerShape(28.dp),
      colors =
          TextFieldDefaults.outlinedTextFieldColors(
              focusedBorderColor = Blue, unfocusedBorderColor = Blue, cursorColor = Blue),
      keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
      keyboardActions =
          KeyboardActions(
              onDone = {
                onSearchAction()
                keyboard?.hide()
              }),
      leadingIcon = {
        IconButton(onClick = onSearchAction) {
          Icon(
              painterResource(R.drawable.search),
              contentDescription = null,
              modifier = Modifier.padding(8.dp).size(52.dp))
        }
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { onClearAction() }) {
            Icon(
                Icons.Default.Clear,
                contentDescription = null,
                modifier = Modifier.padding(8.dp).size(30.dp))
          }
        }
      },
      supportingText = {
        if (noResultFound) {
          Text(
              text = "No result found",
              style =
                  TextStyle(
                      fontSize = 16.sp,
                  ),
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp),
          )
        }
      })
}

private fun formatDate(date: LocalDate): String {
  val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
  return date.format(formatter)
}
