package com.github.se.studybuddies.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.github.se.studybuddies.MainActivity
import com.github.se.studybuddies.R
import com.github.se.studybuddies.mapService.LocationService
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.DrawerMenu
import com.github.se.studybuddies.ui.Main_title
import com.github.se.studybuddies.ui.permissions.hasLocationPermission
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("InlinedApi")
@Composable
fun MapScreen(
    uid: String, //This will be useful for later versions when
    navigationActions: NavigationActions,
    context: Context,
) {
    var location by remember { mutableStateOf<Location?>(null) }

    var isTrackingOn by remember { mutableStateOf(false) }
    var isZooming by remember { mutableStateOf(true) }

    val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


    //Set it the initial values
  var positionClient = LatLng(location?.latitude ?: -35.016, location?.longitude ?: 143.321)
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(positionClient, 3f)
  }
  val uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = true)) }

    // Create a BroadcastReceiver to receive the location updates from the LocationService
    val locationReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            location = intent.getParcelableExtra<Location>("location")
            // Use the location here
            location?.let {
                positionClient = LatLng(it.latitude, it.longitude)
                if(isZooming) {
                    cameraPositionState.position =
                        CameraPosition.fromLatLngZoom(positionClient, 15f)
                    isZooming = false
                }
            }
        }
    }
    //Use DisposableEffect to "clean up" the current state of the screen when the Location tracking is off or changing
    // Contrary to LaunchedEffect, DisposableEffect is called when the intent come from another screen/file
    DisposableEffect(key1 = true) {
        val filter = IntentFilter("LocationUpdates")
        context.registerReceiver(locationReceiver, filter, Context.RECEIVER_NOT_EXPORTED)

        // Unregister the receiver when the composable is disposed
        onDispose {
            context.unregisterReceiver(locationReceiver)
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            Log.d("MapScreen", "Permission granted")
        } else {
            Log.d("MapScreen", "Permission denied")
        }

    }


    /*ActivityCompat.requestPermissions(
        (context as MainActivity),
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ),
        0)*/
  DrawerMenu(
      navigationActions = navigationActions,
      backRoute = Route.MAP,
      content = {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .testTag("mapScreen"),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings) {
                if(isTrackingOn){
                    location?.let {
                        val position = LatLng(it.latitude, it.longitude)
                        Marker(
                            state = rememberMarkerState(position = position),
                            title = "Your Location",
                            snippet = "This is your current location")
                    }
                }
            }
      },
      title = { Main_title("Map") },
      iconOptions = {
        Icon(
            painter = painterResource(id = R.drawable.get_location),
            modifier =
            Modifier
                .testTag("mapIcon")
                .padding(26.dp)
                .size(30.dp)
                .clickable {
                    requestPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                    if (!isGpsEnabled) {
                        Toast
                            .makeText(context, "GPS not enable", Toast.LENGTH_SHORT)
                            .show()
                    } else if (!context.hasLocationPermission()) {
                        Toast
                            .makeText(
                                context,
                                "Location permission not granted",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    } else if (!isNetworkEnabled) {
                        Toast
                            .makeText(context, "Network not enabled", Toast.LENGTH_SHORT)
                            .show()
                    } else if (isTrackingOn) {
                        Intent(context, LocationService::class.java).apply {
                            action = LocationService.ACTION_STOP
                            context.startService(this)
                        }
                        isTrackingOn = false
                        Toast
                            .makeText(context, "Location service stopped", Toast.LENGTH_SHORT)
                            .show()
                    //In the case where nothing is wrong, we start the LocationService
                    } else {
                        Intent(context, LocationService::class.java).apply {
                            action = LocationService.ACTION_START
                            context.startService(this)
                        }
                        Log.d("MapScreen", positionClient.toString())
                        isTrackingOn = true
                        isZooming = true
                        Toast
                            .makeText(context, "Location service started", Toast.LENGTH_SHORT)
                            .show()

                    }
                },
            tint = if(isTrackingOn) Color.Red else Color.Gray,
            contentDescription = "Map")
      },
  )
}
