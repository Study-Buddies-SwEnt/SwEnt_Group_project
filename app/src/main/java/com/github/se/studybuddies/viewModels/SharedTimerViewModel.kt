package com.github.se.studybuddies.viewModels


import android.util.Log
import kotlinx.coroutines.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.database.DatabaseConnection
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.FirebaseDatabase
import kotlin.time.Duration

class SharedTimerViewModel(private val groupId: String) : ViewModel() {
    private val databaseConnection = DatabaseConnection()
    private val timerRef = databaseConnection.getTimerReference(groupId)

    val timerData: MutableLiveData<TimerData> = MutableLiveData()
    var remainingTime: MutableLiveData<Long?> = MutableLiveData()
    private var timerJob: Job? = null


    init {
        listenToTimerUpdates()
    }

    private fun listenToTimerUpdates() {
        timerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val timerValue = snapshot.getValue(Long::class.java) ?: 0L
                remainingTime.postValue(timerValue)
                timerData.postValue(TimerData(System.currentTimeMillis() , timerValue, true, 0L))

            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database read failures
            }
        })
    }

    fun startTimer() {

        val elapsedTime = System.currentTimeMillis() - (timerData.value?.startTime ?: 0L)
        val duration = timerData.value?.duration ?: 0L

        viewModelScope.launch(Dispatchers.IO) {
            timerData.postValue(TimerData(System.currentTimeMillis(), duration, true, elapsedTime))
            remainingTime.postValue(duration)
            startLocalCountdown(duration, System.currentTimeMillis(),)
        }
    }

    private fun startLocalCountdown(duration: Long, startTime: Long) {
        if (timerJob == null || timerJob?.isCompleted == true) {
            timerJob =
                viewModelScope.launch {
                    var timeLeft = duration - (timerData.value?.elapsedTime ?: 0L)

                    while (timeLeft > 0 && timerData.value?.isRunning == true) {
                        delay(1000)
                        timeLeft -= 1000 // Decrement timeLeft by 1 second
                        remainingTime.postValue(timeLeft)
                    }


                    remainingTime.postValue(0L)


                    if (timeLeft <= 0) {
                        resetTimer()
                    }
                }
        }
    }

    fun pauseTimer() {
        viewModelScope.launch(Dispatchers.IO) {
            timerData.value?.let { timer ->

                if (timer.isRunning) {
                    timerJob?.cancel()


                    timer.isRunning = false
                    val currentTime = System.currentTimeMillis()
                    val startTime = timer.startTime ?: currentTime
                    val elapsedTime = currentTime - startTime
                    timer.isRunning = false

                    timer.elapsedTime = elapsedTime

                    // Calculate the remaining time properly
                    val remainingTimeCalc = (timer.duration ?: 0L) - elapsedTime
                    remainingTime.postValue(remainingTimeCalc)


                    timerRef.setValue(remainingTimeCalc)

                    // Update the local timer data
                    timerData.postValue(timer)
                }
            }
        }
    }

    fun resetTimer() {
        viewModelScope.launch(Dispatchers.IO) {
            // Reset the TimerData locally
            timerData.postValue(
                TimerData(
                    startTime = null,
                    duration = 0L,
                    isRunning = false,
                    elapsedTime = 0L
                )
            )

            // Reset the timer data in Firebase
            timerRef.setValue(null)

            // Post the reset remaining time
            remainingTime.postValue(0L)
        }
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

        if (millisToAdd > 0) {


            val currentTimerData = timerData.value ?: TimerData()

            val newDuration = (currentTimerData.duration ?: 0L) + millisToAdd

            // Update TimerData
            currentTimerData.duration = newDuration
            timerData.postValue(currentTimerData)

            // Update remaining time assuming the timer is counting down
            val newRemainingTime = (remainingTime.value ?: 0L) + millisToAdd
            remainingTime.postValue(newRemainingTime)

            // Update Firebase with the new duration
            timerRef.setValue(newDuration)
        }

    }

}

data class TimerData(
    var startTime: Long? = null,
    var duration: Long? = null,
    var isRunning: Boolean = false,
    var elapsedTime: Long = 0L
)