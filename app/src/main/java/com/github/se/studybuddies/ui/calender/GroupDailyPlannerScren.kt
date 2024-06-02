package com.github.se.studybuddies.ui.calender

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
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
  var deleteMode by remember { mutableStateOf(false) }

  LaunchedEffect(planner) {
    goals = planner.goals
    appointments = planner.appointments.toSortedMap()
    notes = planner.notes
  }

  PlannerScreen(
      date = date,
      title = stringResource(id = R.string.daily_planner_title),
      navigationIcon = { GoBackRouteButton(navigationActions, "${Route.GROUPCALENDAR}/$groupUID") },
      onDeleteModeToggle = { deleteMode = !deleteMode },
      isDeleteMode = deleteMode,
      goals = goals,
      notes = notes,
      appointments = appointments,
      onGoalAdd = { newItem ->
        goals = goals + newItem
        viewModel.updateDailyPlanner(date, planner.copy(goals = goals))
      },
      onNoteAdd = { newItem ->
        notes = notes + newItem
        viewModel.updateDailyPlanner(date, planner.copy(notes = notes))
      },
      onAppointmentAdd = { time, text ->
        appointments = (appointments + (time to text)).toSortedMap()
        viewModel.updateDailyPlanner(date, planner.copy(appointments = appointments))
      },
      onGoalDelete = { goal ->
        viewModel.deleteGoal(date, goal)
        goals = goals.filter { it != goal }
      },
      onNoteDelete = { note ->
        viewModel.deleteNote(date, note)
        notes = notes.filter { it != note }
      },
      onAppointmentDelete = { time ->
        viewModel.deleteAppointment(date, time)
        appointments = appointments.filterKeys { it != time }.toSortedMap()
      })
}
