package com.github.se.studybuddies.viewModels

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.database.DatabaseConnection
import com.github.se.studybuddies.database.DbRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class SharedTimerViewModel(private val groupUID: String) : ViewModel() {
  private val db = DbRepository = DatabaseConnection()
  private val _timerValue = MutableStateFlow(0L)
  val timerValue: StateFlow<Long> = _timerValue

  private val _timerEnd = MutableStateFlow(false)
  val timerEnd: StateFlow<Boolean> = _timerEnd

  private var isRunning = false
  private var countDownTimer: CountDownTimer? = null

  init {
    viewModelScope.launch {
      syncTimerWithFirebase()
      startPeriodicSync()
    }
  }

  private fun startPeriodicSync() {
    viewModelScope.launch {
      while (isActive) {
        syncTimerWithFirebase()
        delay(2000) // Appeler toutes les 2 secondes
      }
    }
  }

  private suspend fun syncTimerWithFirebase() {
    val group = db.getGroup(groupUID)
    val timerState = group.timerState
    val remainingTime = timerState.endTime - System.currentTimeMillis()
    if (remainingTime > 0) {
      _timerValue.value = remainingTime
      isRunning = timerState.isRunning
      if (isRunning) {
        setupTimer(remainingTime)
      } else {
        pauseTimer()
      }
    } else {
      resetTimerValues()
    }
  }

  private fun setupTimer(timeRemaining: Long) {
    countDownTimer?.cancel()
    countDownTimer =
        object : CountDownTimer(timeRemaining, 1000) {
          override fun onTick(millisUntilFinished: Long) {
            _timerValue.value = millisUntilFinished
            viewModelScope.launch {
              databaseConnection.updateGroupTimer(
                  groupUID, _timerValue.value + System.currentTimeMillis(), isRunning)
            }
          }

          override fun onFinish() {
            _timerValue.value = 0
            _timerEnd.value = true
            isRunning = false
            viewModelScope.launch { databaseConnection.updateGroupTimer(groupUID, 0L, false) }
          }
        }

    if (isRunning) {
      countDownTimer?.start()
    }
  }

  fun startTimer() {

    if (isRunning) return
    if (_timerValue.value > 0) {
      val newEndTime = System.currentTimeMillis() + _timerValue.value
      isRunning = true
      setupTimer(_timerValue.value)
    }
  }

  fun pauseTimer() {
    isRunning = false
    countDownTimer?.cancel()

    viewModelScope.launch {
      databaseConnection.updateGroupTimer(
          groupUID, System.currentTimeMillis() + _timerValue.value, false)
    }
  }

  fun resetTimer() {
    isRunning = false
    resetTimerValues()
    viewModelScope.launch { databaseConnection.updateGroupTimer(groupUID, 0L, false) }
  }

  private fun resetTimerValues() {
    _timerValue.value = 0
    _timerEnd.value = false
    countDownTimer?.cancel()

  }

  fun addHours(hours: Long) {
    updateTimer(hours * 3600 * 1000) // Convert hours to milliseconds
  }

  fun addMinutes(minutes: Long) {
    updateTimer(minutes * 60 * 1000) // Convert minutes to milliseconds
  }

  fun addSeconds(seconds: Long) {
    updateTimer(seconds * 1000) // Convert seconds to milliseconds
  }

  private fun updateTimer(additionalTime: Long) {
    val newTime = _timerValue.value + additionalTime
    if (newTime >= 0) {
      _timerValue.value = newTime
      if (isRunning) {
        setupTimer(newTime)
      }
      val newEndTime = System.currentTimeMillis() + newTime
      viewModelScope.launch { databaseConnection.updateGroupTimer(groupUID, newEndTime, isRunning) }

    }
  }
}
