package com.github.se.studybuddies.ui.Timer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimeViewModel : ViewModel() {
    private var totalTimeInSeconds = 0L
    private val _timeLeft = MutableLiveData<Long>()
    val timeLeft: LiveData<Long> = _timeLeft

    private var timerJob: Job? = null

    fun setTimer(hours: Int, minutes: Int, seconds: Int) {
        totalTimeInSeconds = (hours * 3600 + minutes * 60 + seconds).toLong()
        _timeLeft.value = totalTimeInSeconds
    }

    fun startTimer() {
        timerJob?.cancel() // Cancel any existing job
        timerJob = viewModelScope.launch {
            var secondsCount = totalTimeInSeconds
            while (secondsCount > 0) {
                _timeLeft.postValue(secondsCount)
                delay(1000)
                secondsCount--
            }
            _timeLeft.postValue(0)
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
    }

    fun stopTimer() {
        timerJob?.cancel()
        _timeLeft.postValue(totalTimeInSeconds) // Reset to initial state
    }
}
