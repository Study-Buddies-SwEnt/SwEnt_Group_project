package com.github.se.studybuddies.viewModels


import android.util.Log
import kotlinx.coroutines.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.util.Timer
import java.util.TimerTask
import kotlin.coroutines.CoroutineContext

class SharedTimerViewModel(private val groupId: String) : ViewModel() {
    private var databaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("timers/$groupId")

    val timerLiveData = MutableLiveData<String>()

    init {
        // Attach ValueEventListener to listen for changes in elapsedTime
        databaseRef.child("elapsedTime").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val elapsed = dataSnapshot.getValue(Long::class.java) ?: 0L
                timerLiveData.postValue(formatElapsedTime(elapsed))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Log error or handle Firebase database access error
                println("Database error: ${databaseError.toException()}")
            }
        })
    }

    fun startTimer() {
        val startTime = System.currentTimeMillis()
        databaseRef.child("startTime").setValue(startTime)
        databaseRef.child("isRunning").setValue(true)
    }

    fun stopTimer() {
        databaseRef.child("isRunning").setValue(false)
    }

    fun pauseTimer() {
        databaseRef.child("isRunning").setValue(false)
    }

    fun resetTimer() {
        databaseRef.child("elapsedTime").setValue(0L)
        databaseRef.child("isRunning").setValue(false)
    }

    fun addHours(hours: Long) {
        adjustTime(hours * 3600 * 1000)
    }

    fun addMinutes(minutes: Long) {
        adjustTime(minutes * 60 * 1000)
    }

    fun addSeconds(seconds: Long) {
        adjustTime(seconds * 1000)
    }

    private fun adjustTime(additionalMillis: Long) {
        databaseRef.child("elapsedTime").get().addOnSuccessListener { snapshot ->
            val currentElapsed = snapshot.getValue(Long::class.java) ?: 0L
            databaseRef.child("elapsedTime").setValue(currentElapsed + additionalMillis)
        }
    }

    private fun formatElapsedTime(elapsedMillis: Long): String {
        val seconds = (elapsedMillis / 1000) % 60
        val minutes = (elapsedMillis / (1000 * 60)) % 60
        val hours = (elapsedMillis / (1000 * 60 * 60))
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}