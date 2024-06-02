package com.github.se.studybuddies.viewModels

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.CalendarDataSource
import com.github.se.studybuddies.data.CalendarUiState
import com.github.se.studybuddies.data.DailyPlanner
import com.github.se.studybuddies.database.DatabaseConnection
import java.time.YearMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalendarGroupViewModel(val groupuid: String) : ViewModel() {

  private val value_date by lazy { CalendarDataSource() }
  private val databaseConnection = DatabaseConnection()

  private val _dailyPlanners = mutableStateMapOf<String, MutableStateFlow<DailyPlanner>>()
  val dailyPlanners: Map<String, StateFlow<DailyPlanner>>
    get() = _dailyPlanners

  private val _uiState = MutableStateFlow(CalendarUiState.Init)
  val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
      syncDailyPlannersWithFirebaseGroup()
      _uiState.update { currentState ->
        currentState.copy(dates = value_date.getDates(currentState.yearMonth))
      }
    }
  }

  fun refreshDailyPlanners() {
    viewModelScope.launch { syncDailyPlannersWithFirebaseGroup() }
  }

  private suspend fun syncDailyPlannersWithFirebaseGroup() {
    val dailyPlannersGroup = databaseConnection.getAllGroupDailyPlanners(groupuid)
    val plannersMap = mutableMapOf<String, MutableStateFlow<DailyPlanner>>()
    dailyPlannersGroup.forEach { planner -> plannersMap[planner.date] = MutableStateFlow(planner) }
    _dailyPlanners.clear()
    _dailyPlanners.putAll(plannersMap)
  }

  fun updateDailyGroupPlanner(date: String, planner: DailyPlanner) {
    _dailyPlanners[date]?.value = planner

    viewModelScope.launch {
      val dailyPlanners = _dailyPlanners.values.map { it.value }.toList()
      databaseConnection.updateGroupPlanners(groupuid, dailyPlanners)
    }
  }

  fun getDailyPlannerGroup(date: String): StateFlow<DailyPlanner> {
    return _dailyPlanners.getOrPut(date) {
      MutableStateFlow(DailyPlanner(date)).also { fetchAndUpdatePlanner(date) }
    }
  }

  private fun fetchAndUpdatePlanner(date: String) {
    viewModelScope.launch {
      val planner = databaseConnection.getDailyGroupPlanner(groupuid, date)
      _dailyPlanners[date]?.value = planner
    }
  }

  fun to_Next_Month(nextMonth: YearMonth) {
    viewModelScope.launch {
      _uiState.update { currentState ->
        currentState.copy(yearMonth = nextMonth, dates = value_date.getDates(nextMonth))
      }
    }
  }

  fun to_Previous_Month(prevMonth: YearMonth) {
    viewModelScope.launch {
      _uiState.update { currentState ->
        currentState.copy(yearMonth = prevMonth, dates = value_date.getDates(prevMonth))
      }
    }
  }

  fun deleteGoal(date: String, goal: String) {
    val planner = _dailyPlanners[date]?.value ?: return
    val updatedGoals = planner.goals.toMutableList().apply { remove(goal) }
    updateDailyGroupPlanner(date, planner.copy(goals = updatedGoals))
  }

  fun deleteNote(date: String, note: String) {
    val planner = _dailyPlanners[date]?.value ?: return
    val updatedNotes = planner.notes.toMutableList().apply { remove(note) }
    updateDailyGroupPlanner(date, planner.copy(notes = updatedNotes))
  }

  fun deleteAppointment(date: String, time: String) {
    val planner = _dailyPlanners[date]?.value ?: return
    val updatedAppointments = planner.appointments.toMutableMap().apply { remove(time) }
    updateDailyGroupPlanner(date, planner.copy(appointments = updatedAppointments))
  }
}

class CalendarGroupViewModelFactory(private val uid: String) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(CalendarGroupViewModel::class.java)) {
      return CalendarGroupViewModel(uid) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}
