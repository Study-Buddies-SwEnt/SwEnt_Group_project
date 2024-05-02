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

open class SharedTimerViewModel(private val groupId: String) : ViewModel() {
    private val databaseConnection = DatabaseConnection()
    private val timerRef = databaseConnection.getTimerReference(groupId)

    val timerData = MutableLiveData<TimerData>()
    var remainingTime = MutableLiveData<Long>()

    init {
        listenToTimerUpdates()
    }

    private fun listenToTimerUpdates() {
        timerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(TimerData::class.java) ?: TimerData()
                timerData.value = data
                updateRemainingTime(data)
            }

            override fun onCancelled(error: DatabaseError) {
                // Log or handle database read failures
            }
        })
    }

    private fun updateRemainingTime(data: TimerData) {
        if (data.isRunning) {
            val currentTime = System.currentTimeMillis()
            val elapsed = currentTime - (data.startTime ?: currentTime)
            data.elapsedTime = elapsed
            val newRemainingTime = (data.duration ?: 0L) - elapsed
            if (newRemainingTime <= 0) {
                pauseTimer()
                remainingTime.postValue(0)
            } else {
                remainingTime.postValue(newRemainingTime)
            }
        } else {
            remainingTime.postValue(data.duration)
        }
    }

    fun startTimer(duration: Long) {
        val startTime = System.currentTimeMillis()
        viewModelScope.launch(Dispatchers.IO) {
            timerRef.child("startTime").setValue(startTime)
            timerRef.child("duration").setValue(duration)
            timerRef.child("elapsedTime").setValue(0L)
            timerRef.child("isRunning").setValue(true)
            remainingTime.postValue(duration)
            startLocalCountdown(duration, startTime)
        }
    }

    private fun startLocalCountdown(duration: Long, startTime: Long) {
        viewModelScope.launch {
            var timeLeft = duration
            while (timeLeft > 0) {
                delay(1000)
                val elapsed = System.currentTimeMillis() - startTime
                timeLeft = duration - elapsed
                if (timeLeft <= 0) {
                    pauseTimer()
                    remainingTime.postValue(0)
                    break
                }
                remainingTime.postValue(timeLeft)
                timerRef.child("elapsedTime").setValue(elapsed)  // Update elapsed time in Firebase
            }
        }
    }

    fun pauseTimer() {
        viewModelScope.launch(Dispatchers.IO) {
            val currentElapsedTime = timerData.value?.elapsedTime ?: 0L
            timerRef.child("isRunning").setValue(false)
            timerRef.child("elapsedTime").setValue(currentElapsedTime)
        }
    }

    fun resetTimer() {
        viewModelScope.launch(Dispatchers.IO) {
            timerRef.child("startTime").setValue(null)
            timerRef.child("duration").setValue(null)
            timerRef.child("elapsedTime").setValue(0L)
            timerRef.child("isRunning").setValue(false)
            remainingTime.postValue(0)
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
        remainingTime.value?.let {
            val newTime = it + millisToAdd
            remainingTime.postValue(newTime)
            timerRef.child("duration").setValue(newTime + (timerData.value?.elapsedTime ?: 0L))
        }
    }
}
data class TimerData(
    var startTime: Long? = null,
    var duration: Long? = null,
    var isRunning: Boolean = false,
    var elapsedTime: Long = 0L  // Track elapsed time separately
)