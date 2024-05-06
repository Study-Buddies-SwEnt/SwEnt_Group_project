package com.github.se.studybuddies.viewModels

import android.annotation.SuppressLint
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI.getApplicationContext
import com.github.se.studybuddies.ui.timer.DataHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TimerViewModel (): ViewModel() {
  @SuppressLint("RestrictedApi")
  val applicationContext = getApplicationContext()

  val dataHelper = DataHelper(applicationContext)
  private val _timerValue = MutableStateFlow(0L)  // Holds the elapsed time in milliseconds
  val timerValue: StateFlow<Long> = _timerValue

  private val _timerEnd = MutableStateFlow(false)
  val timerEnd: StateFlow<Boolean> = _timerEnd

  private var countDownTimer: CountDownTimer? = null

  init {
    setupTimer(0)
  }

  private fun setupTimer(duration: StateFlow<Long>) {
    countDownTimer = object : CountDownTimer(duration, 1000) {  // Update every second
      override fun onTick(millisUntilFinished: Long) {
        _timerValue.tryEmit(millisUntilFinished)
      }

      override fun onFinish() {
        _timerValue.tryEmit(0)  // Ensure timer shows 0 at the end
        _timerEnd.tryEmit(true)  // Signal that the timer has ended
      }
    }
    if (dataHelper.timerCounting()) {
      countDownTimer?.start()  // Automatically start timer if it was previously running
    }
  }


  fun startTimer() {
    dataHelper.setTimerCounting(true)
    countDownTimer?.start()

  }

  fun pauseTimer() {
    countDownTimer?.cancel()
    dataHelper.setTimerCounting(false)
  }

  fun resetTimer() {
    countDownTimer?.cancel()  // Stop current timer
    setupTimer(timerValue)  // Set up a new timer with the initial duration
    _timerValue.tryEmit(timerValue)  // Reset to full duration
    dataHelper.setTimerCounting(false)
    _timerEnd.tryEmit(false)
  }



  fun addHours(hours: Long) {
    val additionalTime = hours * 3600  // Convert hours to milliseconds
    updateTimer(additionalTime)
  }

  fun addMinutes(minutes: Long) {
    val additionalTime = minutes * 60   // Convert minutes to milliseconds
    updateTimer(additionalTime)
  }

  fun addSeconds(seconds: Long) {
    val additionalTime = seconds  // Convert seconds to milliseconds
    updateTimer(additionalTime)
  }

  private fun updateTimer(additionalTime: Long) {
    val newTime = _timerValue.value + additionalTime
    if (newTime > 0) {
      _timerValue.tryEmit(newTime)
    }
  }

  override fun onCleared() {
    super.onCleared()
    timer?.cancel()
  }
}



  /*
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
/*

   */