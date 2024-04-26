package com.github.se.studybuddies.ui.todo

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
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
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.viewModels.ToDoListViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ToDoListScreen(ToDoListViewModel: ToDoListViewModel, navigationActions: NavigationActions) {
  val todos by ToDoListViewModel.todos.collectAsState()
  val (searchQuery, setSearchQuery) = remember { mutableStateOf("") }

  ToDoListViewModel.fetchAllTodos()

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
            backgroundColor = Color.Blue,
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
              modifier = Modifier.padding(innerPadding).fillMaxSize().testTag("todoList"),
              verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
              horizontalAlignment = Alignment.Start,
              content = {
                items(todoList.value) { todo -> ToDoItem(todo, navigationActions) }
              })
        }
      })
}

@Composable
fun ToDoItem(todo: ToDo, navigationActions: NavigationActions) {
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .clickable {
                val todoUID = todo.uid
                Log.d("MyPrint", "Tapped on UID $todoUID")
                navigationActions.navigateTo("${Route.EDITTODO}/$todoUID")
              }
              .drawBehind {
                val strokeWidth = 1f
                val y = size.height - strokeWidth / 2
                drawLine(Color.LightGray, Offset(0f, y), Offset(size.width, y), strokeWidth)
              }
              .testTag("todoListItem")) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
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
        Row(modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
          Text(
              text = todo.status.name,
              style = TextStyle(fontSize = 11.sp, color = statusColor(todo.status)),
          )
          Spacer(Modifier.width(10.dp))
          Icon(
              painter = painterResource(R.drawable.arrow_right_24px),
              contentDescription = null,
              modifier = Modifier.size(28.dp))
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
      placeholder = { Text("Search a Task") },
      singleLine = true,
      modifier =
          Modifier.padding(start = 26.dp, top = 26.dp, end = 26.dp, bottom = 8.dp)
              .width(360.dp)
              .height(80.dp)
              .testTag("searchTodo"),
      shape = RoundedCornerShape(28.dp),
      colors =
          TextFieldDefaults.colors(
              focusedIndicatorColor = Color.Transparent,
              unfocusedIndicatorColor = Color.Transparent),
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
              modifier = Modifier.padding(8.dp).size(48.dp))
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
/*
@Preview(showBackground = true)
@Composable
fun OverviewPreview() {
    Overview()
}
 */
