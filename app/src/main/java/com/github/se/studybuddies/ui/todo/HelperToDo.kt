package com.github.se.studybuddies.ui.todo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.todo.ToDoStatus
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.White
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
      modifier = Modifier.padding(0.dp).width(300.dp).height(65.dp).testTag("todo_name_field"))
  OutlinedTextField(
      value = descriptionState.value,
      onValueChange = { descriptionState.value = it },
      label = { Text("Description") },
      placeholder = { Text("Describe the task") },
      modifier =
          Modifier.padding(0.dp).width(300.dp).height(150.dp).testTag("todo_description_field"))
  Box() {
    OutlinedTextField(
        readOnly = true,
        value = selectedDate.value.format(DateTimeFormatter.ISO_DATE),
        label = { Text("Due date") },
        onValueChange = {},
        modifier = Modifier.width(300.dp).height(65.dp).testTag("todo_date_field"),
        leadingIcon = {
          Icon(
              painterResource(R.drawable.calendar),
              contentDescription = null,
              modifier = Modifier.size(20.dp))
        })
    Box(modifier = Modifier.matchParentSize().alpha(0f).clickable { isOpen.value = true })
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(onAccept: (Long?) -> Unit, onCancel: () -> Unit) {
  val state = rememberDatePickerState()
  DatePickerDialog(
      modifier = Modifier.testTag("date_picker"),
      onDismissRequest = {},
      confirmButton = {
        Button(
            modifier = Modifier.testTag("date_confirm_button"),
            onClick = { onAccept(state.selectedDateMillis) },
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Blue,
                )) {
              Text("Confirm", color = White)
            }
      },
      dismissButton = {
        Button(
            modifier = Modifier.testTag("date_dismiss_button"),
            onClick = onCancel,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Blue,
                )) {
              Text("Cancel", color = White)
            }
      }) {
        DatePicker(state = state)
      }
}

@Composable
fun statusColor(status: ToDoStatus): Color {
  return when (status) {
    ToDoStatus.CREATED -> Color(0xFFFFFFFF)
    ToDoStatus.STARTED -> Color(0xFFFB9905)
    ToDoStatus.DONE -> Color(0xFF1FC959)
  }
}
