package com.github.se.studybuddies.ui.calender

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
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
    viewModel: CalendarViewModel,
    navigationActions: NavigationActions
) {


    LaunchedEffect(date) { viewModel.refreshDailyPlanners() }

    val planner by viewModel.getDailyPlanner(date).collectAsState()

    var goals by remember { mutableStateOf(listOf<String>()) }
    var appointments by remember { mutableStateOf(mapOf<String, String>().toSortedMap()) }
    var notes by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(planner) {
        goals = planner.goals
        appointments = planner.appointments.toSortedMap()
        notes = planner.notes
    }

    var dialogState by remember { mutableStateOf<DialogState?>(null) }
    var deleteMode by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopNavigationBar(
                title = { Sub_title(stringResource(id = R.string.daily_planner_title)) },
                navigationIcon = { GoBackRouteButton(navigationActions, Route.CALENDAR) },
                actions = {
                    IconButton(onClick = { deleteMode = !deleteMode }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Mode", tint = Blue)
                    }
                })
        }) { padding ->
        Column(modifier = Modifier.padding(10.dp).padding(padding)) {
            Spacer(modifier = Modifier.height(5.dp))

            Row {
                Column(modifier = Modifier.weight(1f).padding(end = 3.dp)) {
                    PlannerSection(
                        title = stringResource(id = R.string.todays_goals),
                        items = goals,
                        onAddItemClick = { dialogState = DialogState.AddGoal },
                        onDeleteItemClick = { goal ->
                            viewModel.deleteGoal(date, goal)
                            goals = goals.filter { it != goal }
                        },
                        deleteMode = deleteMode)
                    Spacer(modifier = Modifier.height(16.dp))
                    PlannerSection(
                        title = stringResource(id = R.string.notes),
                        items = notes,
                        onAddItemClick = { dialogState = DialogState.AddNote },
                        onDeleteItemClick = { note ->
                            viewModel.deleteNote(date, note)
                            notes = notes.filter { it != note }
                        },
                        deleteMode = deleteMode)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                    AppointmentsSection(
                        appointments = appointments,
                        onAddAppointmentClick = { dialogState = DialogState.AddAppointment },
                        onDeleteAppointmentClick = { time ->
                            viewModel.deleteAppointment(date, time)
                            appointments = appointments.filterKeys { it != time }.toSortedMap()
                        },
                        deleteMode = deleteMode)
                }
            }
        }
    }

    when (dialogState) {
        DialogState.AddGoal ->
            AddItemDialog(
                title = stringResource(id = R.string.add_goal),
                label = stringResource(id = R.string.goal),
                onAddItem = { newItem ->
                    goals = goals + newItem
                    viewModel.updateDailyPlanner(date, planner.copy(goals = goals))
                },
                onDismiss = { dialogState = null })
        DialogState.AddNote ->
            AddItemDialog(
                title = stringResource(id = R.string.add_note),
                label = stringResource(id = R.string.note),
                onAddItem = { newItem ->
                    notes = notes + newItem
                    viewModel.updateDailyPlanner(date, planner.copy(notes = notes))
                },
                onDismiss = { dialogState = null })
        DialogState.AddAppointment ->
            AddAppointmentDialog(
                onAddAppointment = { time, text ->
                    appointments = (appointments + (time to text)).toSortedMap()
                    viewModel.updateDailyPlanner(date, planner.copy(appointments = appointments))
                },
                onDismiss = { dialogState = null })
        null -> {}
    }
}

@Composable
fun PlannerSection(
    title: String,
    items: List<String>,
    onAddItemClick: () -> Unit,
    onDeleteItemClick: (String) -> Unit,
    deleteMode: Boolean
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
        items.forEachIndexed { _, item ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                TextFieldWithLines(
                    value = item,
                    singleLine = false, // Allow multi-line input
                    modifier = Modifier.weight(1f))
                if (deleteMode) {
                    IconButton(onClick = { onDeleteItemClick(item) }) {
                        Icon(Icons.Default.Close, contentDescription = "Delete", tint = Blue)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun AppointmentsSection(
    appointments: Map<String, String>,
    onAddAppointmentClick: () -> Unit,
    onDeleteAppointmentClick: (String) -> Unit,
    deleteMode: Boolean
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

        appointments.forEach { (time, appointment) ->
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(time, color = Blue, modifier = Modifier.width(80.dp))
                TextFieldWithLines(value = appointment, singleLine = false, modifier = Modifier.weight(1f))
                if (deleteMode) {
                    IconButton(onClick = { onDeleteAppointmentClick(time) }) {
                        Icon(Icons.Default.Close, contentDescription = "Delete", tint = Blue)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TextFieldWithLines(value: String, singleLine: Boolean = true, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = value,
            color = Blue,
            modifier = Modifier.fillMaxWidth().heightIn(min = 20.dp),
            maxLines = if (singleLine) 1 else Int.MAX_VALUE)
        Divider(color = Blue, thickness = 1.dp)
    }
}

@Composable
fun AddItemDialog(
    title: String,
    label: String,
    onAddItem: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newItemText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, color = Blue) },
        text = {
            TextField(
                value = newItemText,
                onValueChange = { newItemText = it },
                label = { Text(label, color = Blue) },
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
                    if (newItemText.isNotEmpty()) {
                        onAddItem(newItemText)
                        newItemText = ""
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Blue)) {
                Text(stringResource(id = R.string.add), color = White)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(backgroundColor = Blue)) {
                Text(stringResource(id = R.string.cancel), color = White)
            }
        })
}

@Composable
fun AddAppointmentDialog(onAddAppointment: (String, String) -> Unit, onDismiss: () -> Unit) {
    var newAppointmentText by remember { mutableStateOf("") }
    var newAppointmentHour by remember { mutableStateOf("") }
    var newAppointmentMinute by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
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
                        unfocusedIndicatorColor = Blue)
                )
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
                        isError = newAppointmentHour.toIntOrNull()?.let { it !in 0..23 } == true
                    )
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
                        isError = newAppointmentMinute.toIntOrNull()?.let { it !in 0..59 } == true
                    )
                }
                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        style = TextStyle(fontSize = 14.sp)
                    )
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
                        minute in 0..59
                    ) {
                        val time = String.format("%02d:%02d", hour, minute)
                        onAddAppointment(time, newAppointmentText)
                        newAppointmentText = ""
                        newAppointmentHour = ""
                        newAppointmentMinute = ""
                        errorMessage = ""
                        onDismiss()
                    } else {
                        errorMessage = "Incorrect time format"
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Blue)
            ) {
                Text(stringResource(id = R.string.add), color = White)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(backgroundColor = Blue)) {
                Text(stringResource(id = R.string.cancel), color = White)
            }
        }
    )
}

enum class DialogState {
    AddGoal,
    AddNote,
    AddAppointment
}


