package com.github.se.studybuddies.ui.groups

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.Sub_title
import com.github.se.studybuddies.ui.permissions.checkPermission
import com.github.se.studybuddies.ui.settings.SetProfilePicture
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.GroupViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@OptIn(ExperimentalPermissionsApi::class)
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

  Surface(color = White, modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally) {
          item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)) {
                  TopAppBar(
                      title = { Sub_title("Create a group") },
                      navigationIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back",
                            modifier =
                                Modifier.clickable {
                                  navigationActions.navigateTo(Route.GROUPSHOME)
                                })
                      })
                  Divider(color = Blue, thickness = 4.dp)
                  Spacer(modifier = Modifier.padding(20.dp))
                  GroupFields(nameState)
                  Spacer(modifier = Modifier.padding(20.dp))
                  SetProfilePicture(photoState) {
                    checkPermission(
                        context,
                        "Manifest.permission.READ_EXTERNAL_STORAGE",
                        requestPermissionLauncher)
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
  }
}

        /*
            LazyColumn(
              modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 20.dp, vertical = 20.dp),
              verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top),
              horizontalAlignment = Alignment.CenterHorizontally) {
                item {
                  Column(
                      modifier = Modifier.fillMaxWidth(),
                      verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        SecondaryTopBar { navigationActions.navigateTo(Route.GROUPSHOME) }
                        Text("Create a group")
                        Spacer(modifier = Modifier.padding(20.dp))
                        GroupFields(nameState)
                        Spacer(modifier = Modifier.padding(20.dp))
                        SetProfilePicture(photoState) { getContent.launch("image/*") }
                        SaveButton(nameState) {
                          groupViewModel.createGroup(nameState.value, photoState.value)
                          navigationActions.navigateTo(Route.GROUPSHOME)
                        }
                      }
                }
              }
        }

               */

               */
