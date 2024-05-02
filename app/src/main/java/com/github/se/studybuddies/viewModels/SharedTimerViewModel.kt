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

class SharedTimerViewModel(private val groupId: String) : ViewModel() {
    private val databaseConnection = DatabaseConnection()
    private val timerRef = databaseConnection.getTimerReference(groupId)

    val timerData = MutableLiveData<TimerData>()
    var remainingTime = MutableLiveData<Long?>()

    init {
        listenToTimerUpdates()
    }

    private fun listenToTimerUpdates() {
        timerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val time = snapshot.getValue(Long::class.java) ?: 0L
                remainingTime.postValue(time)
            }

            override fun onCancelled(error: DatabaseError) {
                // Log or handle database read failures
            }
        })
    }

    fun startTimer(duration: Long) {
        val startTime = System.currentTimeMillis()
        viewModelScope.launch(Dispatchers.IO) {
            timerData.postValue(TimerData(startTime, duration, true, 0L))
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
            }
            // Here, update Firebase with the remaining time
            timerRef.setValue(timeLeft)
        }
    }

    fun pauseTimer() {
        viewModelScope.launch(Dispatchers.IO) {
            timerData.value?.let { timer ->
                val defaultDuration = timer.duration ?: 0L
                val elapsedTime = defaultDuration - (remainingTime.value ?: defaultDuration)
                timer.isRunning = false
                timer.elapsedTime = elapsedTime
                timerRef.setValue(remainingTime.value ?: 0L)
            }
        }
    }

    fun resetTimer() {
        viewModelScope.launch(Dispatchers.IO) {
            timerData.postValue(TimerData())
            remainingTime.postValue(0)
            timerRef.setValue(0L)
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
            timerRef.setValue(newTime)
        }
    }
}

data class TimerData(
    var startTime: Long? = null,
    var duration: Long? = null,
    var isRunning: Boolean = false,
    var elapsedTime: Long = 0L
)