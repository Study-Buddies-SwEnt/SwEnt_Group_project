package com.github.se.studybuddies.viewModels

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.TimerState
import com.github.se.studybuddies.database.DatabaseConnection
import kotlinx.coroutines.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SharedTimerViewModel(
    private val groupUID: String,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {
  private val db = DatabaseConnection()
  private val _timerValue = MutableStateFlow(0L)
  val timerValue: StateFlow<Long> = _timerValue

  private val _timerEnd = MutableStateFlow(false)
  val timerEnd: StateFlow<Boolean> = _timerEnd

  private val _running_local = MutableStateFlow(false)
  val running_local: StateFlow<Boolean> = _running_local

  private val _isRunning = MutableStateFlow(false)
  val isRunning: StateFlow<Boolean> = _isRunning

  private var countDownTimer: CountDownTimer? = null

  init {

    startListeningForTimerUpdates()
  }

  private fun startListeningForTimerUpdates() {
    db.subscribeToGroupTimerUpdates(
        groupUID, _timerValue, _isRunning, ioDispatcher, mainDispatcher, ::onTimerStateChanged)
  }

  private suspend fun onTimerStateChanged(timerState: TimerState) {

    _timerValue.value = timerState.endTime

    _isRunning.value = timerState.isRunning

    if (_isRunning.value) {
      startTimer()
    } else {
      pauseTimer()
    }
  }

  fun setTimer(hours: Long = 0, minutes: Long = 0, seconds: Long = 0) {
    val newTime = (hours * 3600 + minutes * 60 + seconds) * 1000
    _timerValue.value = newTime

    viewModelScope.launch { db.updateGroupTimer(groupUID, TimerState(newTime, _isRunning.value)) }
  }

  fun startTimer() {
    if (isRunning.value) return // Timer is already running

    _running_local.value = true

    _isRunning.value = true
    viewModelScope.launch(ioDispatcher) {
      db.updateGroupTimer(groupUID, TimerState(_timerValue.value, true))
    }
    startLocalTimer()
    countDownTimer?.start()
  }

  private fun startLocalTimer() {

    countDownTimer?.cancel()
    countDownTimer =
        object : CountDownTimer(_timerValue.value, 1000) {
          override fun onTick(millisUntilFinished: Long) {
            if (isRunning.value == false) {
              cancel()
            }

            _timerValue.value = millisUntilFinished
          }

          override fun onFinish() {
            _timerValue.value = 0
          }
        }
  }

  fun pauseTimer() {
    if (!isRunning.value) return // Timer is already paused

    viewModelScope.launch(ioDispatcher) {
      _isRunning.value = false
      db.updateGroupTimer(groupUID, TimerState(_timerValue.value, _isRunning.value))
    }

    countDownTimer?.cancel()
    _running_local.value = false
  }

  fun resetTimer() {
    if (_timerValue.value == 0L) return // Timer is already rest

    countDownTimer?.cancel()
    _timerValue.value = 0

    _isRunning.value = false
    _running_local.value = false
    viewModelScope.launch(ioDispatcher) { db.updateGroupTimer(groupUID, TimerState(0L, false)) }
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

      if (_isRunning.value) {
        countDownTimer?.cancel()
        startLocalTimer()
        countDownTimer?.start()
      }

      viewModelScope.launch(ioDispatcher) {
        db.updateGroupTimer(groupUID, TimerState(_timerValue.value, _isRunning.value))
      }
    }
  }
}

object SharedTimerViewModelFactory {
  private val viewModelMap = mutableMapOf<String, SharedTimerViewModel>()

  fun getSharedTimerViewModel(
      groupUID: String,
      ioDispatcher: CoroutineDispatcher,
      mainDispatcher: CoroutineDispatcher
  ): SharedTimerViewModel {
    return viewModelMap.getOrPut(groupUID) {
      SharedTimerViewModel(groupUID, ioDispatcher, mainDispatcher)
    }
  }
}
