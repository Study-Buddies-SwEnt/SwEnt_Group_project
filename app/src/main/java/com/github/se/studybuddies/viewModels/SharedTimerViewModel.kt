package com.github.se.studybuddies.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.ui.timer.TimerInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import com.google.firebase.database.ktx.getValue
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.launch

class SharedTimerViewModel : ViewModel() {
    private val db = FirebaseDatabase.getInstance().reference
    private val _timerData = MutableLiveData<Map<String, TimerInfo>>()
    val timerData: LiveData<Map<String, TimerInfo>> = _timerData

    init {
        listenToTimerUpdates()
    }

    private fun listenToTimerUpdates() {
        db.child("timers").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedTimers = mutableMapOf<String, TimerInfo>()
                snapshot.children.forEach { child ->
                    val key = child.key ?: return
                    val timerInfo: TimerInfo? = child.getValue(TimerInfo::class.java)
                    if (timerInfo != null) {
                        updatedTimers[key] = timerInfo
                    }
                }
                _timerData.postValue(updatedTimers)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SharedTimerViewModel", "Failed to listen to timer updates", error.toException())
            }
        })
    }

    fun startTimer(groupId: String) {
        viewModelScope.launch {
            val currentTimer = _timerData.value?.get(groupId) ?: TimerInfo()
            if (!currentTimer.isActive) {
                currentTimer.isActive = true
                currentTimer.lastStartTime = System.currentTimeMillis()
                updateTimer(groupId, currentTimer)
            }
        }
    }

    fun pauseTimer(groupId: String) {
        viewModelScope.launch {
            _timerData.value?.get(groupId)?.let { currentTimer ->
                if (currentTimer.isActive) {
                    val elapsedTime = System.currentTimeMillis() - currentTimer.lastStartTime
                    currentTimer.duration += elapsedTime
                    currentTimer.isActive = false
                    updateTimer(groupId, currentTimer)
                }
            }
        }
    }

    fun resetTimer(groupId: String) {
        viewModelScope.launch {
            updateTimer(groupId, TimerInfo())
        }
    }

    fun adjustTime(groupId: String, timeAdjustment: Long) {
        viewModelScope.launch {
            _timerData.value?.get(groupId)?.let { currentTimer ->
                if (currentTimer.isActive) {
                    val elapsedTime = System.currentTimeMillis() - currentTimer.lastStartTime
                    currentTimer.duration += elapsedTime + timeAdjustment
                    currentTimer.lastStartTime = System.currentTimeMillis()
                } else {
                    currentTimer.duration += timeAdjustment
                }
                updateTimer(groupId, currentTimer)
            }
        }
    }

    private fun updateTimer(groupId: String, timerInfo: TimerInfo) {
        db.child("timers").child(groupId).setValue(timerInfo)
            .addOnFailureListener { exception ->
                Log.e("SharedTimerViewModel", "Failed to update timer", exception)
            }
    }
}
