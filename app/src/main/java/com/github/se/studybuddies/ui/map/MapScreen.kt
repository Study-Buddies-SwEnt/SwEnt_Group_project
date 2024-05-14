package com.github.se.studybuddies.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.mapService.LocationService
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.permissions.hasLocationPermission
import com.github.se.studybuddies.ui.shared_elements.MainScreenScaffold
import com.github.se.studybuddies.ui.theme.Red
import com.github.se.studybuddies.viewModels.UserViewModel
import com.github.se.studybuddies.viewModels.UsersViewModel
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.delay

@SuppressLint("InlinedApi", "CoroutineCreationDuringComposition")
@Composable
fun MapScreen(
    uid: String, // This will be useful for later versions when we'll store the user location in the
    userViewModel: UserViewModel,
    usersViewModel: UsersViewModel,
    navigationActions: NavigationActions,
    context: Context,
) {
  if (uid.isEmpty()) return
  userViewModel.fetchUserData(uid)

  val friendsData = remember { mutableStateOf(emptyList<User>()) }

  var location by remember { mutableStateOf<Location?>(null) }

  val isTrackingOn = remember { mutableStateOf(false) }
  var isZooming = remember { mutableStateOf(true) }

  val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

  // Set it the initial values
  var positionClient = LatLng(location?.latitude ?: -35.016, location?.longitude ?: 143.321)
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(positionClient, 3f)
  }
  val uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = true)) }

  // Update the friends location every 10 sec as long as the user location tracking is off
  // Indeed if the tracking is on, the screen will automatically rerun every 10 sec
  LaunchedEffect(key1 = true) {
    // This coroutine will run indefinitely until the composable is disposed
    while (!isTrackingOn.value) {
      // Fetch friends data from Firebase
      usersViewModel.fetchAllFriends(uid)
      // Delay for 10 seconds
      delay(10001)
    }
  }

  // Create a BroadcastReceiver to receive the location updates from the LocationService
  val locationReceiver: BroadcastReceiver =
      object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
          location = intent.getParcelableExtra<Location>("location")
          // Use the location here
          location?.let {
            positionClient = LatLng(it.latitude, it.longitude)
            userViewModel.updateLocation(uid, "${it.latitude},${it.longitude}")
            if (isZooming.value) {
              cameraPositionState.position = CameraPosition.fromLatLngZoom(positionClient, 15f)
              isZooming.value = false
            }
          }
          // When the user location trakcing is on, the disposal effect will refresh the data every
          // 10 sec
          // We remove the Launched effect (conflict btw Disposal + Launched) and thus the update of
          // the friends location must have to be done here now
          usersViewModel.fetchAllFriends(uid)
        }
      }
  // Use DisposableEffect to "clean up" the current state of the screen when the Location tracking
  // is off or changing
  // Contrary to LaunchedEffect, DisposableEffect is called when the intent come from another
  // screen/file
  DisposableEffect(key1 = true) {
    val filter = IntentFilter("LocationUpdates")
    LocalBroadcastManager.getInstance(context).registerReceiver(locationReceiver, filter)
    onDispose { LocalBroadcastManager.getInstance(context).unregisterReceiver(locationReceiver) }
  }

  MainScreenScaffold(
      navigationActions = navigationActions,
      backRoute = Route.MAP,
      content = {
        GoogleMap(
            modifier = Modifier.fillMaxSize().testTag("mapScreen"),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings) {
              if (isTrackingOn.value) {
                location?.let {
                  val position = LatLng(it.latitude, it.longitude)
                  Marker(
                      state = rememberMarkerState(position = position),
                      title = R.string.your_location.toString(),
                      snippet = R.string.this_is_your_current_location.toString())
                }
              }
              friendsData.value.forEach { friend ->
                if (friend.location != "offline" && friend.location != "") {
                  val position =
                      LatLng(
                          friend.location.split(",")[0].toDouble(),
                          friend.location.split(",")[1].toDouble())
                  Marker(
                      state = rememberMarkerState(position = position),
                      title = friend.username,
                      icon = scaleBitmap(context, R.drawable.friends_location, 40, 40))
                }
              }
            }
      },
      title = "Map",
      iconOptions = {
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.padding(8.dp)) {
          FriendsLocationButton(context, isTrackingOn, usersViewModel, friendsData)
          UserLocationButton(uid, context, locationManager, isTrackingOn, isZooming)
        }
      })
}

@Composable
fun UserLocationButton(
    uid: String,
    context: Context,
    locationManager: LocationManager,
    isTrackingOn: MutableState<Boolean>,
    isZooming: MutableState<Boolean>,
) {
  val requestPermissionLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
              Log.d("MapScreen", R.string.permission_granted.toString())
            } else {
              Toast.makeText(context, R.string.location_permission_not_granted, Toast.LENGTH_SHORT)
                  .show()
            }
          }

  val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
  val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

  Icon(
      painter = painterResource(id = R.drawable.get_location),
      modifier =
          Modifier.testTag("mapIcon").padding(24.dp).size(30.dp).clickable {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION))
            if (!isGpsEnabled) {
              Toast.makeText(context, "GPS not enable", Toast.LENGTH_SHORT).show()
            } else if (!isNetworkEnabled) {
              Toast.makeText(context, "Network not enabled", Toast.LENGTH_SHORT).show()
            } else if (!context.hasLocationPermission()) {
              Log.d("MapScreen", R.string.location_permission_not_granted.toString())
            } else if (isTrackingOn.value) {
              Intent(context, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                context.startService(this)
              }
              isTrackingOn.value = false
              Toast.makeText(context, R.string.location_service_stopped, Toast.LENGTH_SHORT).show()
              UserViewModel().updateLocation(uid, "offline")
              // In the case where nothing is wrong, we start the LocationService
            } else {
              Intent(context, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                context.startService(this)
              }
              isTrackingOn.value = true
              isZooming.value = true
              Toast.makeText(context, R.string.location_service_started, Toast.LENGTH_SHORT).show()
            }
          },
      tint = if (isTrackingOn.value) Color.Red else Color.Gray,
      contentDescription = "Map")
}

@Composable
fun FriendsLocationButton(
    context: Context,
    isTrackingOn: MutableState<Boolean>,
    usersViewModel: UsersViewModel,
    friendsData: MutableState<List<User>>,
) {
  val friends by usersViewModel.friends_old.collectAsState()
  friendsData.value = friends

  val isLoading = remember { mutableStateOf(true) }

  if (isLoading.value) {
    val handler = android.os.Handler()
    val runnable =
        object : Runnable {
          override fun run() {

            if (friends.isNotEmpty()) {
              isLoading.value = false // Stop loading as chats are not empty
            } else {
              handler.postDelayed(this, 1000) // Continue checking every second
            }
          }
        }
    handler.post(runnable) // Start the checking process
    handler.postDelayed(
        {
          isLoading.value = false // Ensure isLoading is set to false after the original delay
          handler.removeCallbacks(runnable) // Stop any further checks if time expires
        },
        10000)
  }
  if (isLoading.value) {
    CircularProgressIndicator(modifier = Modifier.padding(24.dp).size(30.dp).testTag("loading"))
  } else {
    if (friends.isEmpty()) {
      Text(
          text = stringResource(id = R.string.no_friends_found),
          modifier = Modifier.width(250.dp).height(80.dp).padding(25.dp).size(20.dp),
          color = Red)
      // When the location tracking is on, we shouldn't show this toast as the "screen" is
      // refreshing every 10 sec
    } else if (!isTrackingOn.value) {
      Toast.makeText(context, R.string.friends_location_found, Toast.LENGTH_SHORT).show()
    }
  }
}

@Composable
fun scaleBitmap(context: Context, drawableId: Int, width: Int, height: Int): BitmapDescriptor {
  val imageBitmap = BitmapFactory.decodeResource(context.resources, drawableId)
  val scaledBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false)
  return BitmapDescriptorFactory.fromBitmap(scaledBitmap)
}
