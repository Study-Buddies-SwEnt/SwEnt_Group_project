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

  /*
    private val databaseConnection = DatabaseConnection()
    private var timerRef: DatabaseReference = databaseConnection.getTimerReference(groupId)
    val timerData: MutableLiveData<TimerData> = MutableLiveData()
    var remainingTime: MutableLiveData<Long?> = MutableLiveData()

    private var timerJob: Job? = null
    private var timerEventListener: ValueEventListener? = null

    init {
      listenToTimerUpdates()
    }

    private fun listenToTimerUpdates() {
      timerEventListener =
          object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
              val timerValue = snapshot.getValue(Long::class.java) ?: 0L
              remainingTime.postValue(timerValue)
              timerData.postValue(TimerData(System.currentTimeMillis(), timerValue, 0L))
              synchro()
              pauseTimer()
            }

            override fun onCancelled(error: DatabaseError) {
              // Optionally handle database read/write operation cancellations
              Log.e(
                  "SharedTimerViewModel",
                  "Listening to timer updates was cancelled: ${error.message}")
            }
          }
      timerRef.addValueEventListener(timerEventListener!!)
    }
    /*



    private fun listenToTimerUpdates() {
      timerRef.addValueEventListener(
          object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
              val timerValue = snapshot.getValue(Long::class.java) ?: 0L
              remainingTime.postValue(timerValue)
              timerData.postValue(TimerData(System.currentTimeMillis(), timerValue, 0L))
              synchro()
              pauseTimer()
            }

            override fun onCancelled(error: DatabaseError) {}
          })
    }*/

    fun synchro() {
      val elapsedTime = System.currentTimeMillis() - (timerData.value?.startTime ?: 0L)
      timerData.value?.elapsedTime = elapsedTime
    }

    fun startTimer() {

      val duration = timerData.value?.duration ?: 0L

      viewModelScope.launch(Dispatchers.IO) {
        timerData.postValue(TimerData(System.currentTimeMillis(), duration))
        remainingTime.postValue(duration)
        startLocalCountdown(
            duration,
            System.currentTimeMillis(),
        )
      }
    }

    private fun startLocalCountdown(duration: Long, startTime: Long) {

      if (timerJob == null || timerJob?.isCompleted == true) {

        timerJob =
            viewModelScope.launch(Dispatchers.IO) {
              while (isActive) {

                timerData.value?.duration = duration - (timerData.value?.elapsedTime ?: 0L)

                while (timerData.value?.duration!! > 0) {
                  delay(1000)
                  timerData.value?.duration =
                      timerData.value?.duration!! - 1000 // Decrement timeLeft by 1 second
                  remainingTime.postValue(timerData.value?.duration)
                }

                remainingTime.postValue(0L)

                if (timerData.value?.duration!! <= 0) {
                  resetTimer()
                }
              }
            }
      }
    }

    fun pauseTimer() {
      timerJob?.cancel() // Cancel the ongoing timer job
      /*
        viewModelScope.launch {
          timerData.value?.duration?.let { databaseConnection.updateGroupTimer(groupId, it) }
        }
      }


                  timerData.value?.let { timer ->

                          val currentTime = System.currentTimeMillis()
                          val startTime = timer.startTime ?: currentTime
                          val elapsedTime = currentTime - startTime

                          // Update the timer data

                          timer.elapsedTime += elapsedTime

                          val remainingTimeCalc = (timer.duration ?: 0L) - timer.elapsedTime
                          remainingTime.postValue(remainingTimeCalc)


                          viewModelScope.launch {
                              databaseConnection.updateGroupTimer(groupId, remainingTimeCalc)
                          }
                          // Update the local timer data

                      }
                  }

      */
    }

    fun resetTimer() {
      timerJob?.cancel()

      timerData.postValue(TimerData(startTime = null, duration = 0L, elapsedTime = 0L))
      /*

      viewModelScope.launch { databaseConnection.updateGroupTimer(groupId, 0) }
      */

      remainingTime.postValue(0L)
    }

    fun addHours(hours: Long) {
      addTimeMillis(hours * 3600 * 1000)
    }

    fun addMinutes(minutes: Long) {
      addTimeMillis(minutes * 60 * 1000)
    }

    fun addSeconds(seconds: Long) {
      addTimeMillis(seconds * 1000)
    }

    private fun addTimeMillis(millisToAdd: Long) {

      val currentTimerData = timerData.value ?: TimerData()

      val newDuration = (currentTimerData.duration ?: 0L) + millisToAdd

      // Update TimerData
      currentTimerData.duration = newDuration

      timerData.postValue(currentTimerData)

      // Update remaining time assuming the timer is counting down
      val newRemainingTime = (remainingTime.value ?: 0L) + millisToAdd
      if (newRemainingTime >= 0) {
        remainingTime.postValue(newRemainingTime)

        viewModelScope.launch { databaseConnection.updateGroupTimer(groupId, newDuration) }
      }
    }
  }

  data class TimerData(
      var startTime: Long? = null,
      var duration: Long? = null,
      var elapsedTime: Long = 0L
  )

  /*
  data class TimerData(
      var startTime: Long? = null,
      var duration: Long? = null,
      var elapsedTime: Long = 0L
  )
  */
  */
