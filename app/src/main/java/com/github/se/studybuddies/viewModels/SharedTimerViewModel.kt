package com.github.se.studybuddies.viewModels

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.TimerState
import com.github.se.studybuddies.database.DatabaseConnection
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedTimerViewModel(private val groupUID: String) : ViewModel() {
  private val databaseConnection = DatabaseConnection()
  private val _timerValue = MutableStateFlow(0L)
  val timerValue: StateFlow<Long> = _timerValue

  private val _timerEnd = MutableStateFlow(false)
  val timerEnd: StateFlow<Boolean> = _timerEnd

  private var isRunning = false
  private var countDownTimer: CountDownTimer? = null

  init {
    viewModelScope.launch {
      try {
        val group = databaseConnection.getGroup(groupUID)
        syncTimerWithFirebase(group.timerState)
      } catch (e: Exception) {
        Log.e("SharedTimerViewModel", "Failed to initialize: ${e.message}")
      }
      //
    }
  }
  /*
   private fun observeTimerChanges() {
     databaseConnection.observeTimerChanges(
         groupUID, viewModelScope, Dispatchers.IO, Dispatchers.Main) { timerState ->
           viewModelScope.launch { syncTimerWithFirebase(timerState) }
         }
   }

  */

  private suspend fun syncTimerWithFirebase(timerState: TimerState) {
    val remainingTime = timerState.endTime - System.currentTimeMillis()
    if (remainingTime > 0) {
      _timerValue.value = remainingTime
      val wasRunning = isRunning
      isRunning = timerState.isRunning
      if (isRunning && !wasRunning) {
        setupTimer(remainingTime)
      } else if (!isRunning && wasRunning) {
        cancelCurrentTimer()
      }
    } else {
      resetTimerValues()
    }
  }

  private fun setupTimer(timeRemaining: Long) {
    cancelCurrentTimer()
    countDownTimer = createCountDownTimer(timeRemaining)
    if (isRunning) {
      countDownTimer?.start()
    }
  }

  private fun createCountDownTimer(timeRemaining: Long): CountDownTimer {
    return object : CountDownTimer(timeRemaining, 1000) {
      override fun onTick(millisUntilFinished: Long) {
        _timerValue.value = millisUntilFinished
        if (isRunning) {
          viewModelScope.launch {
            try {
              databaseConnection.updateGroupTimer(
                  groupUID, System.currentTimeMillis() + millisUntilFinished, isRunning)
            } catch (e: Exception) {
              Log.e("SharedTimerViewModel", "Failed to update timer: ${e.message}")
            }
          }
        }
      }

      override fun onFinish() {
        _timerValue.value = 0
        _timerEnd.value = true
        isRunning = false
        viewModelScope.launch {
          try {
            databaseConnection.updateGroupTimer(groupUID, 0L, false)
          } catch (e: Exception) {
            Log.e("SharedTimerViewModel", "Failed to finish timer: ${e.message}")
          }
        }
      }
    }
  }

  private fun cancelCurrentTimer() {
    countDownTimer?.cancel()
    countDownTimer = null
  }

  fun startTimer() {
    if (isRunning || _timerValue.value <= 0) return
    isRunning = true
    setupTimer(_timerValue.value)
    viewModelScope.launch {
      try {
        databaseConnection.updateGroupTimer(
            groupUID, System.currentTimeMillis() + _timerValue.value, true)
      } catch (e: Exception) {
        Log.e("SharedTimerViewModel", "Failed to start timer: ${e.message}")
      }
    }
  }

  fun pauseTimer() {
    if (!isRunning) return
    isRunning = false
    cancelCurrentTimer()
    viewModelScope.launch {
      try {
        databaseConnection.updateGroupTimer(
            groupUID, System.currentTimeMillis() + _timerValue.value, false)
      } catch (e: Exception) {
        Log.e("SharedTimerViewModel", "Failed to pause timer: ${e.message}")
      }
    }
  }

  fun resetTimer() {
    isRunning = false
    resetTimerValues()
    viewModelScope.launch {
      try {
        databaseConnection.updateGroupTimer(groupUID, 0L, false)
      } catch (e: Exception) {
        Log.e("SharedTimerViewModel", "Failed to reset timer: ${e.message}")
      }
    }
  }

  private fun resetTimerValues() {
    _timerValue.value = 0
    _timerEnd.value = false
    cancelCurrentTimer()
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
      viewModelScope.launch {
        try {
          databaseConnection.updateGroupTimer(
              groupUID, System.currentTimeMillis() + newTime, isRunning)
        } catch (e: Exception) {
          Log.e("SharedTimerViewModel", "Failed to update timer: ${e.message}")
        }
      }
    }
  }
}
