package com.github.se.studybuddies.viewModels

import android.app.ActivityManager
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.github.se.studybuddies.mapService.DefaultLocationClient
import com.github.se.studybuddies.mapService.LocationClient
import com.github.se.studybuddies.mapService.LocationService
import com.github.se.studybuddies.permissions.hasLocationPermission
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MapViewModel(private val context: Context) {
  private val locationManager =
      context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
  val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
  var locationClient: LocationClient? = null
  val locationFlow = MutableStateFlow<Location?>(null)

  private var _isLocationOn = MutableStateFlow(false)
  var isLocationOn = _isLocationOn.asStateFlow()

  init {
    if (context.hasLocationPermission() && isLocationEnabled) {
      val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
      locationClient = DefaultLocationClient(context, fusedLocationProviderClient)
    }
  }

  suspend fun startLocationUpdates() {
    locationClient?.getLocationUpdates(10000L)?.collect { location ->
      locationFlow.value = location
    }
    _isLocationOn.update { true }
  }

  suspend fun stopLocationUpdates() {
    _isLocationOn.update { false }
  }

  suspend fun getLocationUpdates() {
    locationClient?.getLocationUpdates(10000L)?.collect { location ->
      locationFlow.value = location
    }
  }

  private fun isLocationServiceRunning(context: Context): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
      if (LocationService::class.java.name == service.service.className) {
        return true
      }
    }
    return false
  }
}
