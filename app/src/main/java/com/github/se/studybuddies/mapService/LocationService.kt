package com.github.se.studybuddies.mapService

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.se.studybuddies.R
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationService : Service() {

  private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
  private lateinit var locationClient: LocationClient

  override fun onBind(p0: Intent?): IBinder? {
    return null
  }

  override fun onCreate() {
    super.onCreate()
    locationClient =
        DefaultLocationClient(
            applicationContext, LocationServices.getFusedLocationProviderClient(applicationContext))
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    when (intent?.action) {
      ACTION_START -> start()
      ACTION_STOP -> stop()
    }
    return super.onStartCommand(intent, flags, startId)
  }

  private fun start() {
    // Set up a notification that inform the user that we're tracking its location
    val notification =
        NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location...")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.map) // Set the small icon here
            .setOngoing(true)

    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    locationClient
        .getLocationUpdates(10000L)
        .catch { e -> e.printStackTrace() }
        .onEach { location ->
          val lat = location.latitude.toString()
          val long = location.longitude.toString()
          val updatedNotification = notification.setContentText("Location: ($lat, $long)")
          notificationManager.notify(1, updatedNotification.build())

          val intent = Intent("LocationUpdates")
          // val intent = Intent(LocationService.ACTION_LOCATION_UPDATES)
          intent.putExtra("location", location)
          // val intent = Intent("com.github.se.studybuddies.LOCATION_UPDATES")
          // intent.setPackage("com.github.se.studybuddies.mapService")
          // sendBroadcast(intent)
          // sendBroadcast(intent, "com.github.se.studybuddies.mapService.LocationService")
          LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
          // sendBroadcast(intent)
        }
        .launchIn(serviceScope)

    startForeground(1, notification.build())
  }

  private fun stop() {
    stopForeground(true)
    stopSelf()
  }

<<<<<<< HEAD
  override fun onDestroy() {
    super.onDestroy()
    serviceScope.cancel()
  }

  companion object {
    const val ACTION_LOCATION_UPDATES = "com.github.se.studybuddies.LOCATION_UPDATES"

    const val ACTION_START = "ACTION_START"
    const val ACTION_STOP = "ACTION_STOP"
  }
}
=======


    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}
>>>>>>> a9b2e6f (Add zoom option and MapViewModel)
