package com.github.se.studybuddies.mapService

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.widget.Toast
import com.github.se.studybuddies.ui.permissions.hasLocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

@Suppress("UNREACHABLE_CODE")
class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationClient {

  @SuppressLint("MissingPermission")
  override fun getLocationUpdates(interval: Long): Flow<Location> {
    return callbackFlow {
      if (!context.hasLocationPermission()) {
        throw LocationClient.LocationException("Missing location permission")
        Toast.makeText(context, "Missing location permission", Toast.LENGTH_SHORT).show()
      }

      val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
      val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
      val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
      if (!isGpsEnabled && !isNetworkEnabled) {
        throw LocationClient.LocationException("GPS is disabled")
        Toast.makeText(context, "GPS is disabled", Toast.LENGTH_SHORT).show()

      }

      val request = LocationRequest.create().setInterval(interval).setFastestInterval(interval)

      val locationCallback =
          object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
              super.onLocationResult(result)
              // The element of the list is the current location
              result.locations.lastOrNull()?.let { location -> launch { send(location) } }
            }
          }

      client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())

      awaitClose { client.removeLocationUpdates(locationCallback) }
    }
  }

}
