package com.github.se.studybuddies.viewModels

import android.app.ActivityManager
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.github.se.studybuddies.mapService.DefaultLocationClient
import com.github.se.studybuddies.mapService.LocationClient
import com.github.se.studybuddies.mapService.LocationService
import com.github.se.studybuddies.ui.permissions.hasLocationPermission
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MapViewModel(private val context: Context) {
  private val locationManager =
      context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
  val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
  val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

  var locationClient = MutableStateFlow<LocationClient?>(null)
  val locationFlow = MutableStateFlow<Location?>(null)

  init {
    if(!isGpsEnabled || !isNetworkEnabled) {
      throw LocationClient.LocationException("GPS is disabled")
    }else if(!context.hasLocationPermission()) {
      throw LocationClient.LocationException("Missing location permission")
    } else {
      val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
      locationClient.update { DefaultLocationClient(context, fusedLocationProviderClient) }
    }
  }

  suspend fun startLocationUpdates() {
    /*locationClient.value?.let {
      it.getLocationUpdates(10000L).collect { location -> locationFlow.value = location }
    }*/
    /*
    locationClient?.getLocationUpdates(10000L)?.collect { location ->
        locationFlow.value = location
    }*/
  }

  suspend fun stopLocationUpdates() {
    //locationClient.update { null }
    //locationFlow.value = null
  }

  fun startLocationPossible(): Boolean {
    return context.hasLocationPermission() && isGpsEnabled && isNetworkEnabled && !isLocationServiceRunning(context)
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
