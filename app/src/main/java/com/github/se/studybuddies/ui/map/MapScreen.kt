package com.github.se.studybuddies.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState


@Composable
fun MapScreen(navController: NavController) {
    val viewModel = viewModel<ToDoViewModel>()
    val tasks by viewModel.tasks.collectAsState()

    Scaffold(
        bottomBar = { BottomNavigationMenu(navController = navController) },
    ) { innerPadding ->
        GoogleMap(modifier = Modifier.fillMaxSize().padding(innerPadding).testTag("mapScreen")) {
            tasks.forEach { task ->
                if (task.location != "") {
                    val location =
                        LatLng(task.location.split(",")[1].toDouble(), task.location.split(",")[0].toDouble())
                    Marker(
                        state = rememberMarkerState(position = location),
                        title = task.name,
                        snippet = task.description)
                }
            }
        }
    }
}
