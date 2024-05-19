package com.github.se.studybuddies.viewModels

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.database.DbRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedTimerViewModel
private constructor(private val groupUID: String, private val db: DbRepository) : ViewModel() {
  private val _timerValue = MutableStateFlow(0L) // Holds the current timer value in milliseconds
  val timerValue: StateFlow<Long> = _timerValue

  private val _timerEnd = MutableStateFlow(false) // Indicates whether the timer has ended
  val timerEnd: StateFlow<Boolean> = _timerEnd

  private var isRunning = false
  private var countDownTimer: CountDownTimer? = null

  companion object {
    private val instances = mutableMapOf<String, SharedTimerViewModel>()

    fun getInstance(groupUID: String, db: DbRepository): SharedTimerViewModel {

      return instances.getOrPut(groupUID) { SharedTimerViewModel(groupUID, db) }
    }
  }

  init {
    subscribeToTimerUpdates()
  }

  private fun subscribeToTimerUpdates() {
    isRunning = db.getTimerUpdates(groupUID, _timerValue)
    if (isRunning) {
      setupTimer(_timerValue.value)
    } else {
      pauseTimer()
    }
  }

  private fun setupTimer(timeRemaining: Long) {
    countDownTimer?.cancel()
    countDownTimer =
        object : CountDownTimer(timeRemaining, 1000) {
          override fun onTick(millisUntilFinished: Long) {
            _timerValue.value = millisUntilFinished
          }

          override fun onFinish() {
            _timerValue.value = 0
            _timerEnd.value = true
            isRunning = false
          }
        }

    if (isRunning) {

      countDownTimer?.start()
    }
  }

  fun startTimer() {
    val newEndTime = System.currentTimeMillis() + _timerValue.value
    isRunning = true
    setupTimer(_timerValue.value)
    isRunning = true
    groupUID?.let { uid -> viewModelScope.launch { db.updateGroupTimer(uid, newEndTime, true) } }
  }

  fun pauseTimer() {
    isRunning = false
    countDownTimer?.cancel()
    groupUID?.let { uid ->
      viewModelScope.launch() {
        db.updateGroupTimer(uid, System.currentTimeMillis() + _timerValue.value, false)
      }
    }
  }

  fun resetTimer() {
    isRunning = false
    _timerValue.value = 0
    _timerEnd.value = false
    countDownTimer?.cancel()
    groupUID?.let { uid -> viewModelScope.launch() { db.updateGroupTimer(uid, 0L, false) } }
  }

  // Adding time functions
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
    val newTime = _timerValue.value + additionalTime
    if (newTime >= 0) {
      _timerValue.value = newTime
      if (isRunning) {
        setupTimer(newTime)
      }
      groupUID?.let { uid ->
        val newEndTime = System.currentTimeMillis() + newTime
        viewModelScope.launch { db.updateGroupTimer(uid, newEndTime, isRunning) }
      }
    }
  }
}
