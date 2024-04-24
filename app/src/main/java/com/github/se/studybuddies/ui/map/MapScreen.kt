package com.github.se.studybuddies.ui.map

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
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
import com.github.se.studybuddies.R
import com.github.se.studybuddies.mapService.LocationService
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.DrawerMenu
import com.github.se.studybuddies.ui.Main_title
import com.github.se.studybuddies.viewModels.MapViewModel
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

@Composable
fun MapScreen(
    uid: String,
    mapViewModel: MapViewModel,
    navigationActions: NavigationActions,
    context: Context,
) {

  val location by mapViewModel.locationFlow.collectAsState(initial = null)
  // val isLocationOn by mapViewModel.isLocationOn.collectAsState()
  var isLocationOn = false

  var positionClient = LatLng(location?.latitude ?: -35.016, location?.longitude ?: 143.321)
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(positionClient, 3f)
  }
  var uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = true)) }

  DrawerMenu(
      navigationActions = navigationActions,
      backRoute = Route.MAP,
      content = {
        LaunchedEffect(location) {
          location?.let {
            positionClient = LatLng(it.latitude, it.longitude)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(positionClient, 15f)
          }
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize().testTag("mapScreen"),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings) {
              location?.let {
                val position = LatLng(it.latitude, it.longitude)
                Marker(
                    state = rememberMarkerState(position = position),
                    title = "Your Location",
                    snippet = "This is your current location")
              }
            }
      },
      title = { Main_title("Map") },
      iconOptions = {
        Icon(
            painter = painterResource(id = R.drawable.globe),
            modifier =
                Modifier.padding(26.dp).size(30.dp).clickable {
                  if (!isLocationOn) {
                    Intent(context, LocationService::class.java).apply {
                      action = LocationService.ACTION_START
                      context.startService(this)
                    }
                    CoroutineScope(Dispatchers.Main).launch { mapViewModel.startLocationUpdates() }
                    isLocationOn = true
                    Toast.makeText(context, "Location service started", Toast.LENGTH_SHORT).show()
                  } else if (isLocationOn) {
                    Intent(context, LocationService::class.java).apply {
                      action = LocationService.ACTION_STOP
                      context.startService(this)
                    }
                    CoroutineScope(Dispatchers.Main).launch { mapViewModel.stopLocationUpdates() }
                    isLocationOn = false
                    Toast.makeText(context, "Location service stopped", Toast.LENGTH_SHORT).show()
                  }
                },
            tint = Color.Red,
            contentDescription = "Map")
      },
  )
}
