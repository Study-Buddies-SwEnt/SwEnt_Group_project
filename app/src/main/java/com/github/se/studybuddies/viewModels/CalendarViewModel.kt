package com.github.se.studybuddies.viewModels

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
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

class CalendarViewModel(private val uid: String) : ViewModel() {

  private val dataSource by lazy { CalendarDataSource() }
  private val databaseConnection = DatabaseConnection()

  private val _dailyPlanners = mutableStateMapOf<String, MutableStateFlow<DailyPlanner>>()
  val dailyPlanners: Map<String, StateFlow<DailyPlanner>>
    get() = _dailyPlanners

  private val _uiState = MutableStateFlow(CalendarUiState.Init)
  val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
      syncDailyPlannersWithFirebase()
      _uiState.update { currentState ->
        currentState.copy(dates = dataSource.getDates(currentState.yearMonth))
      }
    }
  }

  private suspend fun syncDailyPlannersWithFirebase() {
    val user = databaseConnection.getUser(uid)
    val plannersMap = mutableMapOf<String, MutableStateFlow<DailyPlanner>>()
    user.dailyPlanners.forEach { planner -> plannersMap[planner.date] = MutableStateFlow(planner) }
    _dailyPlanners.clear()
    _dailyPlanners.putAll(plannersMap)
  }

  fun refreshDailyPlanners() {
    viewModelScope.launch { syncDailyPlannersWithFirebase() }
  }

  fun updateDailyPlanner(date: String, planner: DailyPlanner) {
    _dailyPlanners[date]?.value = planner

    viewModelScope.launch {
      val user = databaseConnection.getUser(uid)
      val updatedPlanners = user.dailyPlanners.toMutableList()
      updatedPlanners.removeAll { it.date == date }
      updatedPlanners.add(planner)
      databaseConnection.updateDailyPlanners(uid, updatedPlanners)
    }
  }

  fun getDailyPlanner(date: String): StateFlow<DailyPlanner> {
    return _dailyPlanners.getOrPut(date) { MutableStateFlow(DailyPlanner(date = date)) }
  }

  fun toNextMonth(nextMonth: YearMonth) {
    viewModelScope.launch {
      _uiState.update { currentState ->
        currentState.copy(yearMonth = nextMonth, dates = dataSource.getDates(nextMonth))
      }
    }
  }

  fun toPreviousMonth(prevMonth: YearMonth) {
    viewModelScope.launch {
      _uiState.update { currentState ->
        currentState.copy(yearMonth = prevMonth, dates = dataSource.getDates(prevMonth))
      }
    }
  }
}
