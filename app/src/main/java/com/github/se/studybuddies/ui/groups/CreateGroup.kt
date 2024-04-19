package com.github.se.studybuddies.ui.groups

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.GoBackRouteButton
import com.github.se.studybuddies.ui.Sub_title
import com.github.se.studybuddies.ui.TopNavigationBar
import com.github.se.studybuddies.ui.permissions.checkPermission
import com.github.se.studybuddies.ui.settings.SetProfilePicture
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.GroupViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CreateGroup(groupViewModel: GroupViewModel, navigationActions: NavigationActions) {
  val nameState = remember { mutableStateOf("") }
  val photoState = remember { mutableStateOf(Uri.EMPTY) }
  val context = LocalContext.current

  LaunchedEffect(key1 = true) {
    val defaultProfilePictureUri =
        withContext(Dispatchers.IO) { groupViewModel.getDefaultPicture() }
    photoState.value = defaultProfilePictureUri
  }

  val getContent =
      rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { profilePictureUri -> photoState.value = profilePictureUri }
      }
  // val permissionGranted = checkPermission(context, "Manifest.permission.READ_EXTERNAL_STORAGE")

  val permissionState = rememberPermissionState("Manifest.permission.READ_EXTERNAL_STORAGE")

  val requestPermissionLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
          Toast.makeText(context as Activity, "Permission already granted", Toast.LENGTH_SHORT)
              .show()
        } else {
          // Handle permission denial
          Toast.makeText(context as Activity, "Permission refused", Toast.LENGTH_SHORT).show()
        }
      }
  Scaffold(
      modifier = Modifier.fillMaxSize().background(White).testTag("create_group"),
      topBar = {
        TopNavigationBar(
            title = { Sub_title("Create a group") },
            navigationIcon = {
              GoBackRouteButton(navigationActions = navigationActions, Route.GROUPSHOME)
            },
            actions = {})
      }) {
        Column(
            modifier = Modifier.fillMaxWidth().background(White).testTag("content"),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Spacer(modifier = Modifier.padding(20.dp))
              GroupFields(nameState)
              Spacer(modifier = Modifier.padding(20.dp))
              SetProfilePicture(photoState) {
                checkPermission(
                    context, "Manifest.permission.READ_EXTERNAL_STORAGE", requestPermissionLauncher)
                // permissionState.launchPermissionRequest()
                getContent.launch("image/*")
              }
              Spacer(modifier = Modifier.weight(1f))
              SaveButton(nameState) {
                groupViewModel.createGroup(nameState.value, photoState.value)
                navigationActions.navigateTo(Route.GROUPSHOME)
              }
            }
      }
}
