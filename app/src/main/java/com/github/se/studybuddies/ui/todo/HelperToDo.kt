package com.github.se.studybuddies.ui.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.todo.ToDoStatus
import com.github.se.studybuddies.navigation.NavigationActions
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TodoTopBar(navigationActions: NavigationActions, title: String) {
  val tag = if (title == "Create a new task") "createTodoTitle" else "editTodoTitle"
  TopAppBar(
      modifier = Modifier.width(412.dp).height(90.dp).padding(bottom = 2.dp),
      contentColor = Color.Transparent,
      backgroundColor = Color.Transparent,
      elevation = 0.dp) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start,
        ) {
          IconButton(
              onClick = { navigationActions.goBack() },
              modifier = Modifier.testTag("goBackButton")) {
                Icon(
                    painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp))
              }
          Text(
              modifier = Modifier.width(360.dp).height(32.dp).padding(start = 16.dp).testTag(tag),
              text = title,
              style =
                  TextStyle(
                      fontSize = 24.sp,
                      lineHeight = 32.sp,
                      fontWeight = FontWeight(400),
                  ))
        }
      }
}

@Composable
fun TodoFields(
    titleState: MutableState<String>,
    descriptionState: MutableState<String>,
    selectedDate: MutableState<LocalDate>,
    isOpen: MutableState<Boolean>
) {
  OutlinedTextField(
      value = titleState.value,
      onValueChange = { titleState.value = it },
      label = { Text("Title") },
      placeholder = { Text("Name the task") },
      singleLine = true,
      modifier =
          Modifier.padding(0.dp)
              .width(300.dp)
              .height(65.dp)
              .testTag("inputTodoTitle")
              .testTag("toDo_name_field"))
  OutlinedTextField(
      value = descriptionState.value,
      onValueChange = { descriptionState.value = it },
      label = { Text("Description") },
      placeholder = { Text("Describe the task") },
      modifier =
          Modifier.padding(0.dp).width(300.dp).height(150.dp).testTag("inputTodoDescription"))
  Box() {
    OutlinedTextField(
        readOnly = true,
        value = selectedDate.value.format(DateTimeFormatter.ISO_DATE),
        label = { Text("Due date") },
        onValueChange = {},
        modifier = Modifier.width(300.dp).height(65.dp).testTag("inputTodoDate"),
        leadingIcon = {
          Icon(
              painterResource(R.drawable.calendar),
              contentDescription = null,
              modifier = Modifier.size(20.dp))
        })
    Box(modifier = Modifier.matchParentSize().alpha(0f).clickable { isOpen.value = true })
  }
}

@Composable
fun TodoSaveButton(titleState: MutableState<String>, save: () -> Unit) {
  val enabled = titleState.value.isNotEmpty()
  Button(
      onClick = save,
      enabled = enabled,
      modifier =
          Modifier.padding(0.dp)
              .width(300.dp)
              .height(50.dp)
              .background(color = Color.Transparent, shape = RoundedCornerShape(size = 10.dp))
              .testTag("todoSave"),
      colors =
          ButtonDefaults.buttonColors(
              containerColor = Color.Blue,
          )) {
        Text("Save")
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(onAccept: (Long?) -> Unit, onCancel: () -> Unit) {
  val state = rememberDatePickerState()
  DatePickerDialog(
      onDismissRequest = {},
      confirmButton = {
        Button(
            onClick = { onAccept(state.selectedDateMillis) },
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                )) {
              Text("Confirm")
            }
      },
      dismissButton = {
        Button(
            onClick = onCancel,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                )) {
              Text("Cancel")
            }
      }) {
        DatePicker(state = state)
      }
}

@Composable
fun statusColor(status: ToDoStatus): Color {
  return when (status) {
    ToDoStatus.CREATED -> Color(0xFF9BC5C5)
    ToDoStatus.STARTED -> Color(0xFFFB9905)
    ToDoStatus.ENDED -> Color(0xFF1FC959)
    ToDoStatus.ARCHIVED -> Color(0xFF808080)
  }
}
