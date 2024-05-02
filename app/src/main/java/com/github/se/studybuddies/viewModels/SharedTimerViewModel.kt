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
                timerData.postValue(TimerData(System.currentTimeMillis() , timerValue, 0L))

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun startTimer() {

        val elapsedTime = System.currentTimeMillis() - (timerData.value?.startTime ?: 0L)
        val duration = timerData.value?.duration ?: 0L


        viewModelScope.launch(Dispatchers.IO) {
            timerData.postValue(TimerData(System.currentTimeMillis(), duration, elapsedTime))
            remainingTime.postValue(duration)
            startLocalCountdown(duration, System.currentTimeMillis(),)
        }

    }

    private fun startLocalCountdown(duration: Long, startTime: Long) {
        if (timerJob == null || timerJob?.isCompleted == true) {
            timerJob =

                viewModelScope.launch(Dispatchers.IO) {
                    while (isActive) {


                        timerData.value?.duration= duration - (timerData.value?.elapsedTime ?: 0L)

                        while (timerData.value?.duration!! > 0 ) {
                            delay(3000)
                            timerData.value?.duration = timerData.value?.duration!! - 1000 // Decrement timeLeft by 1 second
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
        viewModelScope.launch {
            timerData.value?.duration?.let { databaseConnection.updateGroupTimer(groupId, it) }
        }
    }

        /*
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



    fun resetTimer() {
        timerJob?.cancel()



            timerData.postValue(
                TimerData(
                    startTime = null,
                    duration = 0L,
                    elapsedTime = 0L
                )
            )



        viewModelScope.launch {
            databaseConnection.updateGroupTimer(groupId, 0)
        }

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

    private  fun addTimeMillis(millisToAdd: Long) {

        if (millisToAdd > 0) {


            val currentTimerData = timerData.value ?: TimerData()

            val newDuration = (currentTimerData.duration ?: 0L) + millisToAdd

            // Update TimerData
            currentTimerData.duration = newDuration
            timerData.postValue(currentTimerData)

            // Update remaining time assuming the timer is counting down
            val newRemainingTime = (remainingTime.value ?: 0L) + millisToAdd
            remainingTime.postValue(newRemainingTime)

            viewModelScope.launch {
                databaseConnection.updateGroupTimer(groupId,newDuration)
            }

        }

    }

}
data class  TimerData(
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
