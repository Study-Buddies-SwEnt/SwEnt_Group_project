package com.github.se.studybuddies.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope as viewModelScope1
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
  private val _timer = MutableStateFlow(0L) // Timer value in seconds
  val timer = _timer.asStateFlow()

  private val _timerEnd = MutableStateFlow(false)
  val timerEnd = _timerEnd.asStateFlow()

  private var timerJob: Job? = null

  fun addHours(hours: Long) {
    if (_timer.value + hours > 0) {
      _timer.value += hours * 3600
    }
  }

  fun addMinutes(minutes: Long) {
    if (_timer.value + minutes > 0) {
      _timer.value += minutes * 60
    }
  }

  fun addSeconds(seconds: Long) {
    if (_timer.value + seconds > 0) {
      _timer.value += seconds
    }
  }

  fun startTimer() {
    if (timerJob == null || timerJob?.isCompleted == true) {
      timerJob =
          viewModelScope1.launch {
            while (isActive) {
              delay(1000)
              if (_timer.value > 0) _timer.value-- else _timerEnd.value = true
              delay(1000)
              _timerEnd.value = false
            }
          }
    }
  }

  fun pauseTimer() {
    timerJob?.cancel()
  }

  fun resetTimer() {
    pauseTimer()
    _timer.value = 0
  }
}
