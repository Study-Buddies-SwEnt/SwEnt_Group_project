package com.github.se.studybuddies.ui.map

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.mapService.LocationClient
import com.github.se.studybuddies.mapService.LocationService
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.DrawerMenu
import com.github.se.studybuddies.ui.Main_title
import com.github.se.studybuddies.viewModels.UserViewModel
import com.google.maps.android.compose.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.maps.MapView
import com.google.maps.android.ktx.awaitMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun MapScreen(
    uid: String,
    userViewModel: UserViewModel,
    navigationActions: NavigationActions,
    context: Context,
    locationClient: LocationClient,
    ) {
    userViewModel.fetchUserData(uid)
    val userData by userViewModel.userData.observeAsState()
    val location by locationClient.getLocationUpdates(10000L).collectAsState(initial = null)



    val mapView = rememberMapViewWithLifecycle()


    LaunchedEffect(location) {
        location?.let { currentLocation ->
            val position = LatLng(currentLocation.latitude, currentLocation.longitude)
            val googleMap = mapView.awaitMap()

            // Calculate zoom level based on the marker
            val zoomLevel = 15f

            // Move camera to the marker with zoom level
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(position, zoomLevel)
            )
        }
    }

    DrawerMenu(
        navigationActions = navigationActions,
        backRoute = Route.MAP,
        content = {
            AndroidView({ mapView}) { mapView ->
                CoroutineScope(Dispatchers.Main).launch {
                    val map = mapView.awaitMap()
                    map.uiSettings.isZoomControlsEnabled = true

                    location?.let {
                        val position = LatLng(it.latitude, it.longitude)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 6f))
                        val markerOptions = MarkerOptions()
                            .title("Sydney Opera House")
                            .position(position)
                        map.addMarker(markerOptions)
                    }
                }
            }
            
            /*GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("mapScreen"),

            ) {
                location?.let {
                    val position = LatLng(it.latitude, it.longitude)
                    Marker(
                        state = rememberMarkerState(position = position),
                        title = "Your Location",
                        snippet = "This is your current location"
                    )
                }

                /*
                tasks.forEach { task ->
                    if (task.location != "") {
                        val location =
                            LatLng(
                                task.location.split(",")[1].toDouble(),
                                task.location.split(",")[0].toDouble()
                            )
                        Marker(
                            state = rememberMarkerState(position = location),
                            title = task.name,
                            snippet = task.description
                        )
                    }
                }*/
            }*/

        },
        title = { Main_title("Map") },
        iconOptions = {
            Icon(
                painter = painterResource(id = R.drawable.globe),
                modifier = Modifier.padding(26.dp).size(30.dp).clickable {
                    Intent(context, LocationService::class.java).apply {
                        action = LocationService.ACTION_START
                        context.startService(this)
                    }
                },
                tint = Color.Red,
                contentDescription = "Map"
            )
        },
    )

}


@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context)
    }

    // Makes MapView follow the lifecycle of this composable
    val lifecycleObserver = rememberMapLifecycleObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return mapView
}


@Composable
fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> throw IllegalStateException()
            }
        }
    }