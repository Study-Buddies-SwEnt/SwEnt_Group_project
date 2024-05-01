package com.github.se.studybuddies.viewModels


import android.util.Log
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.Firebase
import com.google.firebase.database.database

class SharedTimerViewModel(private val groupId: String) : ViewModel() {
    var dbRef = Firebase.database.reference.child("timers").child(groupId)

    private val _timerInfo = MutableLiveData<TimerInfo>()
    val timerInfo: LiveData<TimerInfo> = _timerInfo

    init {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _timerInfo.value = snapshot.getValue(TimerInfo::class.java) ?: TimerInfo()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SharedTimerVM", "Failed to fetch timer data", error.toException())
            }
        })
    }

    fun addHours(hours: Long) {
        _timerInfo.value?.let {
            val additionalSeconds = hours * 3600
            updateTimer(it.elapsedTime + additionalSeconds)
        }
    }

    fun addMinutes(minutes: Long) {
        _timerInfo.value?.let {
            val additionalSeconds = minutes * 60
            updateTimer(it.elapsedTime + additionalSeconds)
        }
    }

    fun addSeconds(seconds: Long) {
        _timerInfo.value?.let {
            updateTimer(it.elapsedTime + seconds)
        }
    }

    private fun updateTimer(newElapsedTime: Long) {
        if (newElapsedTime > 0) {
            val updatedTimerInfo = _timerInfo.value?.copy(elapsedTime = newElapsedTime) ?: TimerInfo(elapsedTime = newElapsedTime)
            dbRef.setValue(updatedTimerInfo)
        }
    }

    fun startTimer() {
        _timerInfo.value?.let {
            if (!it.isActive) {
                val newTimer = it.copy(isActive = true, startTime = System.currentTimeMillis())
                dbRef.setValue(newTimer)
            }
        }
    }

    fun pauseTimer() {
        _timerInfo.value?.let {
            if (it.isActive) {
                val elapsed = System.currentTimeMillis() - it.startTime
                val newTimer = it.copy(isActive = false, elapsedTime = it.elapsedTime + elapsed)
                dbRef.setValue(newTimer)
            }
        }
    }

    fun resetTimer() {
        dbRef.setValue(TimerInfo())
    }

    data class TimerInfo(
        val isActive: Boolean = false,
        val startTime: Long = 0L,
        val elapsedTime: Long = 0L
    )
}


