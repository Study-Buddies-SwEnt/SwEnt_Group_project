package com.github.se.studybuddies.ui.calender

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.viewModels.CalendarGroupViewModel

@Composable
fun GroupDailyPlannerScreen(
    date: String,
    groupUID: String,
    viewModel: CalendarGroupViewModel,
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
            navigationIcon = {
              GoBackRouteButton(navigationActions, "${Route.GROUPCALENDAR}/$groupUID")
            },
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
