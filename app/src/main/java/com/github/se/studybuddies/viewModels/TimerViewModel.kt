package com.github.se.studybuddies.viewModels

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TimerViewModel private constructor() : ViewModel() {
  // Existing ViewModel code here

  companion object {
    private var INSTANCE: TimerViewModel? = null

    fun getInstance(): TimerViewModel {
      if (INSTANCE == null) {
        INSTANCE = TimerViewModel()
      }
      return INSTANCE!!
    }
  }

  private val _timerValue = MutableStateFlow(0L) // Holds the elapsed time in milliseconds
  val timerValue: StateFlow<Long> = _timerValue
  var isRunning = false

  private val _timerEnd = MutableStateFlow(false)
  val timerEnd: StateFlow<Boolean> = _timerEnd

  private var countDownTimer: CountDownTimer? = null

  private fun setupTimer(duration: StateFlow<Long>) {
    countDownTimer =
        object : CountDownTimer(_timerValue.value, 1000) {
          override fun onTick(millisUntilFinished: Long) {
            _timerValue.tryEmit(millisUntilFinished)
          }

          override fun onFinish() {
            _timerValue.value = 0
            _timerEnd.value = true
          }
        }
  }

  fun startTimer() {
    isRunning = true
    _timerEnd.value = false
    countDownTimer?.start()
  }

  fun pauseTimer() {
    isRunning = false
    countDownTimer?.cancel()
  }

  fun resetTimer() {
    isRunning = false
    countDownTimer?.cancel()
    _timerValue.value = 0 // Reset to full duration
    _timerEnd.value = false
  }

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
    countDownTimer?.cancel()
    val newTime = _timerValue.value + additionalTime
    if (newTime >= 0) {
      _timerValue.value = newTime
      setupTimer(_timerValue)
      if (isRunning) {
        countDownTimer?.start()
      }
    }
  }
}

/*

  private val _timerValue = MutableStateFlow<Long?>(0L)
  val timerValue = _timerValue.asStateFlow()


  private val _timerEnd = MutableStateFlow<Boolean?>(false)
  val timerEnd = _timerEnd.asStateFlow()
  private val  serviceIntent :Intent = Intent(applicationContext, TimerService::class.java);


  private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      val time = intent.getLongExtra(TimerService.TIMER_UPDATED, 0)
      _timerValue.tryEmit(time)
    }
  }


    private val filter = IntentFilter(TimerService.TIMER_UPDATED)

    val c= applicationContext.registerReceiver(updateTime, filter, RECEIVER_NOT_EXPORTED)


  fun startTimer() {

      serviceIntent.putExtra(TimerService.TIMER_UPDATED, timerValue.value ?: 0)
      applicationContext.startService(serviceIntent)

  }

  fun pauseTimer() {

    applicationContext.stopService(serviceIntent)
  }

  fun resetTimer() {

    applicationContext.stopService(serviceIntent)
    _timerValue.tryEmit(0)
  }

  fun addTime(time: Long) {
    _timerValue.value?.let { currentTime ->
      val newTime = currentTime + time
      _timerValue.tryEmit(newTime)
      serviceIntent.putExtra(TimerService.TIMER_UPDATED, newTime)
    }
  }

  fun addHours(hours: Long) = addTime(hours * 3600)
  fun addMinutes(minutes: Long) = addTime(minutes * 60)
  fun addSeconds(seconds: Long) = addTime(seconds)

  private fun getTimeStringFromDouble(time: Double): String {
    val resultInt = time.roundToInt()
    val hours = resultInt % 86400 / 3600
    val minutes = resultInt % 3600 / 60
    val seconds = resultInt % 60
    return makeTimeString(hours, minutes, seconds)
  }

  private fun makeTimeString(hour: Int, min: Int, sec: Int): String =
    String.format("%02d:%02d:%02d", hour, min, sec)

  override fun onCleared() {
    applicationContext.unregisterReceiver(updateTime)
    super.onCleared()
  }
}
/*
  private val serviceIntent = Intent(applicationContext, TimerService::class.java)
  private val _timerValue = MutableStateFlow<Long?>(null)
  val timerValue = _timerValue.asStateFlow()
   val _timerEnd = MutableStateFlow<Boolean?>(false)
   val timerEnd =_timerEnd.asStateFlow()
  private lateinit var broadcastManager: LocalBroadcastManager
  broadcastManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

  private val updateTime = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      val time = intent.getLongExtra(TimerService.timer, 0L)
      _timerValue.tryEmit(time)

    }
  }

    // Using LocalBroadcastManager to register receiver
    LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
      updateTime,
      IntentFilter(TimerService.timer.toString())
    )


  fun startTimer() {
    serviceIntent.putExtra(TimerService.timer.toString(), timerValue.value)
    applicationContext.startService(serviceIntent)
    if (_timerValue.value!! <=0 )
      _timerEnd.value =true

  }

  fun pauseTimer() {
    applicationContext.stopService(serviceIntent)
  }

  fun resetTimer() {
    pauseTimer()
    _timerValue.tryEmit(0L)
  }

  fun addTime(time: Long) {
    _timerValue.value?.let { currentTime ->
      val newTime = currentTime + time
      _timerValue.tryEmit(newTime)
      startTimer()
    }
  }

  fun addHours(hours: Long) = addTime(hours * 3600)
  fun addMinutes(minutes: Long) = addTime(minutes * 60)
  fun addSeconds(seconds: Long) = addTime(seconds)

  override fun onCleared() {
    super.onCleared()
    LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(updateTime)
  }
}

*/
/*
  private lateinit var service: TimerService
  private var bound = false
  private val _timerEnd = MutableStateFlow(false)
  val timerEnd: StateFlow<Boolean> = _timerEnd
  private val _timerValue = MutableStateFlow(0L)  // Holds the elapsed time in milliseconds
  val timerValue: StateFlow<Long> = _timerValue

  private var serviceConnection: ServiceConnection? = null
init{
  bindService(context)
}

  fun bindService(context: Context) {
    serviceConnection = object : ServiceConnection {
      override fun onServiceConnected(className: ComponentName, service: IBinder) {
        val binder = service as TimerService.LocalBinder
        this@TimerViewModel.service = binder.getService()
        bound = true
      }

      override fun onServiceDisconnected(arg0: ComponentName) {
        bound = false
      }
    }
    Intent(context, TimerService::class.java).also { intent ->
      context.bindService(intent, serviceConnection!!, Context.BIND_AUTO_CREATE)
    }
  }

  fun unbindService(context: Context) {
    if (bound && serviceConnection != null) {
      context.unbindService(serviceConnection!!)
      bound = false
      serviceConnection = null
    }
  }

  fun startTimer() {
    if (bound) {
      service.startTimer(_timerValue.value)
      _timerEnd.value = false
    }
  }

  fun pauseTimer() {
    if (bound) service.pauseTimer()
  }

  fun resetTimer() {
    if (bound) service.resetTimer(0)
  }

  fun addTime(time: Long) {
    val newTime = _timerValue.value + time
    _timerValue.tryEmit(newTime)
  }

  fun addHours(hours: Long) = addTime(hours * 3600 * 1000)
  fun addMinutes(minutes: Long) = addTime(minutes * 60 * 1000)
  fun addSeconds(seconds: Long) = addTime(seconds * 1000)

  override fun onCleared() {
    super.onCleared()
    // Cleanup logic like unbinding service should typically not be in ViewModel.
  }
}

/*
*/
*/
   */

/*
  @SuppressLint("RestrictedApi")


  private val dataHelper = DataHelper(context)
  private val _timerValue = MutableStateFlow(0L)  // Holds the elapsed time in milliseconds
  val timerValue: StateFlow<Long> = _timerValue

  private val _timerEnd = MutableStateFlow(false)
  val timerEnd: StateFlow<Boolean> = _timerEnd

  private var countDownTimer: CountDownTimer? = null


  init {
    setupTimer(timerValue)
    if(dataHelper.timerCounting())
    {
      startTimer()
    }
    else
    {
      pauseTimer()
      if(dataHelper.startTime() != null && dataHelper.stopTime() != null)
      {
        val time = Date().time - calcRestartTime().time
        _timerValue.tryEmit(time)


      }
    }

  }



  private fun calcRestartTime(): Date
  {
    val diff = dataHelper.startTime()!!.time - dataHelper.stopTime()!!.time
    return Date(System.currentTimeMillis() + diff)
  }

  private fun setupTimer(duration: StateFlow<Long>) {
    countDownTimer = object : CountDownTimer(duration.value, 1000) {  // Update every second
      override fun onTick(millisUntilFinished: Long) {
        _timerValue.tryEmit(millisUntilFinished)
      }

      override fun onFinish() {
        _timerValue.tryEmit(0)  // Ensure timer shows 0 at the end
        _timerEnd.tryEmit(true)  // Signal that the timer has ended
      }
    }
    if (dataHelper.timerCounting()) {
      countDownTimer?.start()  // Automatically start timer if it was previously running
    }
  }


  fun startTimer() {
    dataHelper.setTimerCounting(true)
    countDownTimer?.start()

  }

  fun pauseTimer() {
    countDownTimer?.cancel()
    dataHelper.setTimerCounting(false)
  }

  fun resetTimer() {
    countDownTimer?.cancel()  // Stop current timer
    setupTimer(MutableStateFlow(0L) )  // Set up a new timer with the initial duration
    _timerValue.tryEmit(0)  // Reset to full duration
    dataHelper.setTimerCounting(false)
    _timerEnd.tryEmit(false)
  }



  fun addHours(hours: Long) {
    val additionalTime = hours * 3600*1000  // Convert hours to milliseconds
    updateTimer(additionalTime)
  }

  fun addMinutes(minutes: Long) {
    val additionalTime = minutes * 60  *1000  // Convert minutes to milliseconds
    updateTimer(additionalTime)
  }

  fun addSeconds(seconds: Long) {
    val additionalTime = seconds*1000  // Convert seconds to milliseconds
    updateTimer(additionalTime)
  }

  private fun updateTimer(additionalTime: Long) {
    val newTime = _timerValue.value + additionalTime
    if (newTime > 0) {
      _timerValue.tryEmit(newTime)
      setupTimer(_timerValue)

    }
  }

  override fun onCleared() {
    super.onCleared()
    countDownTimer?.cancel()
  }
}



  /*
  private val _timer = MutableStateFlow(0L) // Timer value in seconds
  val timer = _timer.asStateFlow()

  private val _timerEnd = MutableStateFlow(false)
  val timerEnd = _timerEnd.asStateFlow()

  private var timerJob: Job? = null

  fun addHours(hours: Long) {
    if (_timer.value + hours > 0) {
      _timer.value += hours * 3600
    }
  }

  fun addMinutes(minutes: Long) {
    if (_timer.value + minutes > 0) {
      _timer.value += minutes * 60
    }
  }

  fun addSeconds(seconds: Long) {
    if (_timer.value + seconds > 0) {
      _timer.value += seconds
    }
  }

  fun startTimer() {
    if (timerJob == null || timerJob?.isCompleted == true) {
      timerJob =
          viewModelScope1.launch {
            while (isActive) {
              delay(1000)
              if (_timer.value > 0) _timer.value-- else _timerEnd.value = true
              delay(1000)
              _timerEnd.value = false
            }
          }
    }
  }

  fun pauseTimer() {
    timerJob?.cancel()
  }

  fun resetTimer() {
    pauseTimer()
    _timer.value = 0
  }
}
/*

   */
   */

*/
