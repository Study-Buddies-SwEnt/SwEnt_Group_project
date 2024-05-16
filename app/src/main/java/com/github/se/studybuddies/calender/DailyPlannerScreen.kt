package com.github.se.studybuddies.calender

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.DailyPlanner
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
import com.github.se.studybuddies.ui.shared_elements.SaveButton
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.CalendarViewModel
import kotlin.math.*

@Composable
fun DailyPlannerScreen(
    date: String,
    viewModel: CalendarViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    navigationActions: NavigationActions
) {
  LaunchedEffect(date) { viewModel.refreshDailyPlanners() }

  val planner by viewModel.getDailyPlanner(date).collectAsState()

  var goals by remember { mutableStateOf(planner.goals.toMutableList()) }
  var appointments by remember { mutableStateOf(planner.appointments.toMutableMap().toSortedMap()) }
  var notes by remember { mutableStateOf(planner.notes.toMutableList()) }

  LaunchedEffect(planner) {
    goals = planner.goals.toMutableList()
    appointments = planner.appointments.toMutableMap().toSortedMap()
    notes = planner.notes.toMutableList()
  }

  var showAddGoalDialog by remember { mutableStateOf(false) }
  var showAddNoteDialog by remember { mutableStateOf(false) }
  var showAddAppointmentDialog by remember { mutableStateOf(false) }
  var newGoalText by remember { mutableStateOf("") }
  var newNoteText by remember { mutableStateOf("") }
  var newAppointmentHour by remember { mutableStateOf("") }
  var newAppointmentMinute by remember { mutableStateOf("") }
  var newAppointmentText by remember { mutableStateOf("") }

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopNavigationBar(
            title = { Sub_title(stringResource(id = R.string.daily_planner_title)) },
            navigationIcon = { GoBackRouteButton(navigationActions, Route.CALENDAR) },
            actions = {})
      },
      floatingActionButton = {
        SaveButton(enabled = true) {
          val updatedPlanner =
              DailyPlanner(date = date, goals = goals, appointments = appointments, notes = notes)
          viewModel.updateDailyPlanner(date, updatedPlanner)
          navigationActions.navigateTo("${Route.DAILYPLANNER}/$date")
        }
      }) { padding ->
        Column(modifier = Modifier.padding(10.dp).padding(padding)) {
          Spacer(modifier = Modifier.height(5.dp))

          Row {
            Column(modifier = Modifier.weight(1f).padding(end = 3.dp)) {
              PlannerSection(
                  title = stringResource(id = R.string.todays_goals),
                  items = goals,
                  onItemChange = { index, value -> goals[index] = value },
                  onAddItemClick = { showAddGoalDialog = true })
              Spacer(modifier = Modifier.height(16.dp))
              PlannerSection(
                  title = stringResource(id = R.string.notes),
                  items = notes,
                  onItemChange = { index, value -> notes[index] = value },
                  onAddItemClick = { showAddNoteDialog = true })
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
              AppointmentsSection(
                  appointments = appointments,
                  onAppointmentChange = { time, value -> appointments[time] = value },
                  onAddAppointmentClick = { showAddAppointmentDialog = true })
            }
          }
        }
      }

  if (showAddGoalDialog) {
    AlertDialog(
        onDismissRequest = { showAddGoalDialog = false },
        title = { Text(stringResource(id = R.string.add_goal), color = Blue) },
        text = {
          TextField(
              value = newGoalText,
              onValueChange = { newGoalText = it },
              label = { Text(stringResource(id = R.string.goal), color = Blue) },
              colors =
                  TextFieldDefaults.textFieldColors(
                      textColor = Blue,
                      backgroundColor = White,
                      focusedIndicatorColor = Blue,
                      unfocusedIndicatorColor = Blue))
        },
        confirmButton = {
          Button(
              onClick = {
                if (newGoalText.isNotEmpty()) {
                  goals.add(newGoalText)
                  newGoalText = ""
                }
                showAddGoalDialog = false
              },
              colors = ButtonDefaults.buttonColors(backgroundColor = Blue)) {
                Text(stringResource(id = R.string.add), color = White)
              }
        },
        dismissButton = {
          Button(
              onClick = { showAddGoalDialog = false },
              colors = ButtonDefaults.buttonColors(backgroundColor = Blue)) {
                Text(stringResource(id = R.string.cancel), color = White)
              }
        })
  }

  if (showAddNoteDialog) {
    AlertDialog(
        onDismissRequest = { showAddNoteDialog = false },
        title = { Text(stringResource(id = R.string.add_note), color = Blue) },
        text = {
          TextField(
              value = newNoteText,
              onValueChange = { newNoteText = it },
              label = { Text(stringResource(id = R.string.note), color = Blue) },
              colors =
                  TextFieldDefaults.textFieldColors(
                      textColor = Blue,
                      backgroundColor = White,
                      focusedIndicatorColor = Blue,
                      unfocusedIndicatorColor = Blue))
        },
        confirmButton = {
          Button(
              onClick = {
                if (newNoteText.isNotEmpty()) {
                  notes.add(newNoteText)
                  newNoteText = ""
                }
                showAddNoteDialog = false
              },
              colors = ButtonDefaults.buttonColors(backgroundColor = Blue)) {
                Text(stringResource(id = R.string.add), color = White)
              }
        },
        dismissButton = {
          Button(
              onClick = { showAddNoteDialog = false },
              colors = ButtonDefaults.buttonColors(backgroundColor = Blue)) {
                Text(stringResource(id = R.string.cancel), color = White)
              }
        })
  }

  if (showAddAppointmentDialog) {
    AlertDialog(
        onDismissRequest = { showAddAppointmentDialog = false },
        title = { Text(stringResource(id = R.string.add_appointment), color = Blue) },
        text = {
          Column {
            TextField(
                value = newAppointmentText,
                onValueChange = { newAppointmentText = it },
                label = { Text(stringResource(id = R.string.appointment_title), color = Blue) },
                colors =
                    TextFieldDefaults.textFieldColors(
                        textColor = Blue,
                        backgroundColor = White,
                        focusedIndicatorColor = Blue,
                        unfocusedIndicatorColor = Blue))
            Spacer(modifier = Modifier.height(16.dp))
            Row {
              TextField(
                  value = newAppointmentHour,
                  onValueChange = {
                    newAppointmentHour = it.filter { char -> char.isDigit() }.take(2)
                  },
                  label = { Text(stringResource(id = R.string.hour), color = Blue) },
                  modifier = Modifier.weight(1f),
                  colors =
                      TextFieldDefaults.textFieldColors(
                          textColor = Blue,
                          backgroundColor = White,
                          focusedIndicatorColor = Blue,
                          unfocusedIndicatorColor = Blue),
                  isError = newAppointmentHour.toIntOrNull()?.let { it !in 0..23 } == true)
              Spacer(modifier = Modifier.width(8.dp))
              TextField(
                  value = newAppointmentMinute,
                  onValueChange = {
                    newAppointmentMinute = it.filter { char -> char.isDigit() }.take(2)
                  },
                  label = { Text(stringResource(id = R.string.minute), color = Blue) },
                  modifier = Modifier.weight(1f),
                  colors =
                      TextFieldDefaults.textFieldColors(
                          textColor = Blue,
                          backgroundColor = White,
                          focusedIndicatorColor = Blue,
                          unfocusedIndicatorColor = Blue),
                  isError = newAppointmentMinute.toIntOrNull()?.let { it !in 0..59 } == true)
            }
          }
        },
        confirmButton = {
          Button(
              onClick = {
                val hour = newAppointmentHour.toIntOrNull()
                val minute = newAppointmentMinute.toIntOrNull()
                if (newAppointmentText.isNotEmpty() &&
                    hour != null &&
                    minute != null &&
                    hour in 0..23 &&
                    minute in 0..59) {
                  val time = String.format("%02d:%02d", hour, minute)
                  appointments[time] = newAppointmentText
                  appointments = appointments.toSortedMap()
                  newAppointmentText = ""
                  newAppointmentHour = ""
                  newAppointmentMinute = ""
                }
                showAddAppointmentDialog = false
              },
              colors = ButtonDefaults.buttonColors(backgroundColor = Blue)) {
                Text(stringResource(id = R.string.add), color = White)
              }
        },
        dismissButton = {
          Button(
              onClick = { showAddAppointmentDialog = false },
              colors = ButtonDefaults.buttonColors(backgroundColor = Blue)) {
                Text(stringResource(id = R.string.cancel), color = White)
              }
        })
  }
}

@Composable
fun PlannerSection(
    title: String,
    items: List<String>,
    onItemChange: (Int, String) -> Unit,
    onAddItemClick: () -> Unit
) {
  Column {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
          text = title,
          color = Blue,
          fontFamily = FontFamily(Font(R.font.coolvetica_regular)),
          fontSize = 18.sp)
      Spacer(modifier = Modifier.width(8.dp))
      IconButton(onClick = onAddItemClick) {
        Icon(Icons.Default.Add, contentDescription = "Add", tint = Blue)
      }
    }
    Spacer(modifier = Modifier.height(8.dp))
    items.forEachIndexed { index, item ->
      TextFieldWithLines(
          value = item,
          onValueChange = { onItemChange(index, it) },
          singleLine = false // Allow multi-line input
          )
      Spacer(modifier = Modifier.height(8.dp))
    }
    if (items.isEmpty()) {
      repeat(3) {
        TextFieldWithLines(value = "", onValueChange = {}, singleLine = false)
        Spacer(modifier = Modifier.height(8.dp))
      }
    }
  }
}

@Composable
fun TextFieldWithLines(
    value: String,
    onValueChange: (String) -> Unit,
    lineCount: Int = 1,
    singleLine: Boolean = true
) {
  Column {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
        singleLine = singleLine,
        colors =
            TextFieldDefaults.textFieldColors(
                textColor = Blue,
                backgroundColor = White,
                unfocusedIndicatorColor = Blue,
                focusedIndicatorColor = Blue))
    Divider(color = Blue, thickness = 1.dp)
  }
}

@Composable
fun AppointmentsSection(
    appointments: Map<String, String>,
    onAppointmentChange: (String, String) -> Unit,
    onAddAppointmentClick: () -> Unit
) {
  Column {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
          text = stringResource(id = R.string.appointments),
          color = Blue,
          fontFamily = FontFamily(Font(R.font.coolvetica_regular)),
          fontSize = 18.sp)
      Spacer(modifier = Modifier.width(8.dp))
      IconButton(onClick = onAddAppointmentClick) {
        Icon(Icons.Default.Add, contentDescription = "Add", tint = Blue)
      }
    }
    Spacer(modifier = Modifier.height(8.dp))
    if (appointments.isEmpty()) {
      Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        TextFieldWithLines(
            value = "",
            onValueChange = {
              onAppointmentChange(it.toString(), it.toString())
            } // Dummy function to force display of lines
            )
      }
      Spacer(modifier = Modifier.height(8.dp))
    } else {
      appointments.forEach { (time, appointment) ->
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
          Text(time, color = Blue, modifier = Modifier.width(80.dp))
          TextFieldWithLines(
              value = appointment,
              onValueChange = { onAppointmentChange(time, it) },
              singleLine = false)
        }
        Spacer(modifier = Modifier.height(8.dp))
      }
    }
  }
}
