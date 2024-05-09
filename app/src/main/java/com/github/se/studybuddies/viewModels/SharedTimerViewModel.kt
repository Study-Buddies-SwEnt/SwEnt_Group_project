package com.github.se.studybuddies.viewModels

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.TimerState
import com.github.se.studybuddies.database.DatabaseConnection
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedTimerViewModel private constructor(private val groupUID: String) : ViewModel() {
  private val databaseConnection = DatabaseConnection()
  private val _timerValue = MutableStateFlow(0L) // Holds the current timer value in milliseconds
  val timerValue: StateFlow<Long> = _timerValue

  private val _timerEnd = MutableStateFlow(false) // Indicates whether the timer has ended
  val timerEnd: StateFlow<Boolean> = _timerEnd

  private var isRunning = false
  private var countDownTimer: CountDownTimer? = null

  companion object {
    private val instances = mutableMapOf<String, SharedTimerViewModel>()

    fun getInstance(groupUID: String): SharedTimerViewModel {
      return instances.getOrPut(groupUID) { SharedTimerViewModel(groupUID) }
    }
  }

  private fun subscribeToTimerUpdates() {
    groupUID?.let { uid ->
      val timerRef = databaseConnection.getTimerReference(uid)
      timerRef.addValueEventListener(
          object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
              snapshot.getValue(TimerState::class.java)?.let { timerState ->
                _timerValue.value = timerState.endTime - System.currentTimeMillis()
                isRunning = timerState.isRunning
                if (isRunning) {
                  setupTimer(_timerValue.value)
                } else {
                  pauseTimer()
                }
              }
            }

            override fun onCancelled(error: DatabaseError) {
              Log.e("TimerViewModel", "Failed to read timer", error.toException())
            }
          })
    } ?: error("Group UID is not set. Call setup() with valid Group UID.")
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
    groupUID?.let { uid ->
      viewModelScope.launch { databaseConnection.updateGroupTimer(uid, newEndTime, true) }
    }
  }

  fun pauseTimer() {
    isRunning = false
    countDownTimer?.cancel()
    groupUID?.let { uid ->
      viewModelScope.launch() {
        databaseConnection.updateGroupTimer(
            uid, System.currentTimeMillis() + _timerValue.value, false)
      }
    }
  }

  fun resetTimer() {
    isRunning = false
    _timerValue.value = 0
    _timerEnd.value = false
    countDownTimer?.cancel()
    groupUID?.let { uid ->
      viewModelScope.launch() { databaseConnection.updateGroupTimer(uid, 0L, false) }
    }
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
        viewModelScope.launch { databaseConnection.updateGroupTimer(uid, newEndTime, isRunning) }
      }
    }
  }
}
