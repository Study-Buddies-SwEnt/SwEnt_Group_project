package com.github.se.studybuddies.viewModels

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TimerViewModel private constructor() : ViewModel() {
  // Existing ViewModel code here

  companion object {
    private var INSTANCE: TimerViewModel? = null

    fun getInstance(): TimerViewModel {
      if (INSTANCE == null) {
        INSTANCE = TimerViewModel()
      }
      return INSTANCE!!
    }
  }

  private val _timerValue = MutableStateFlow(0L) // Holds the elapsed time in milliseconds
  val timerValue: StateFlow<Long> = _timerValue
  var isRunning = false

  private val _timerEnd = MutableStateFlow(false)
  val timerEnd: StateFlow<Boolean> = _timerEnd

  private var countDownTimer: CountDownTimer? = null

  private fun setupTimer(duration: StateFlow<Long>) {
    countDownTimer =
        object : CountDownTimer(_timerValue.value, 1000) {
          override fun onTick(millisUntilFinished: Long) {
            _timerValue.tryEmit(millisUntilFinished)
          }

          override fun onFinish() {
            _timerValue.value = 0
            _timerEnd.value = true
            resetTimer()
          }
        }
  }

  fun startTimer() {
    isRunning = true
    _timerEnd.value = false
    countDownTimer?.start()
  }

  fun pauseTimer() {
    isRunning = false
    countDownTimer?.cancel()
    setupTimer(_timerValue)
  }

  fun resetTimer() {
    isRunning = false
    countDownTimer?.cancel()
    _timerValue.value = 0
    _timerEnd.value = false
  }

  fun addHours(hours: Long) {
    val additionalTime = hours * 3600 * 1000 // Convert hours to milliseconds
    updateTimer(additionalTime)
  }

  fun addMinutes(minutes: Long) {
    val additionalTime = minutes * 60 * 1000 // Convert minutes to milliseconds
    updateTimer(additionalTime)
  }

  fun addSeconds(seconds: Long) {
    val additionalTime = seconds * 1000 // Convert seconds to milliseconds
    updateTimer(additionalTime)
  }

  private fun updateTimer(additionalTime: Long) {
    countDownTimer?.cancel()
    val newTime = _timerValue.value + additionalTime
    if (newTime >= 0) {
      _timerValue.value = newTime
      setupTimer(_timerValue)
      if (isRunning) {
        countDownTimer?.start()
      }
    }
  }
}
