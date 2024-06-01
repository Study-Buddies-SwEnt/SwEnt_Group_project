package com.github.se.studybuddies.ui.groups

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.permissions.checkPermission
import com.github.se.studybuddies.permissions.imagePermissionVersion
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteToLastPageButton
import com.github.se.studybuddies.ui.shared_elements.SaveButton
import com.github.se.studybuddies.ui.shared_elements.SetProfilePicture
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.GroupViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
  val imageInput = "image/*"

  val requestPermissionLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
          getContent.launch(imageInput)
        }
      }
  var permission = imagePermissionVersion()
  // Check if the Android version is lower than TIRAMISU API 33
  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
    // For older Android versions, use READ_EXTERNAL_STORAGE permission
    permission = "android.permission.READ_EXTERNAL_STORAGE"
  }
  Scaffold(
      modifier = Modifier.fillMaxSize().background(White).testTag("create_group_scaffold"),
      topBar = {
        TopNavigationBar(
            title = { Sub_title(stringResource(R.string.create_group)) },
            navigationIcon = { GoBackRouteToLastPageButton(navigationActions = navigationActions) },
            actions = {})
      }) {
        Column(
            modifier = Modifier.fillMaxSize().background(White).testTag("create_group_column"),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Spacer(modifier = Modifier.padding(20.dp))
              GroupFields(nameState)
              Spacer(modifier = Modifier.padding(10.dp))
              SetProfilePicture(photoState) {
                checkPermission(context, permission, requestPermissionLauncher) {
                  getContent.launch(imageInput)
                }
              }

              Spacer(modifier = Modifier.padding(10.dp))
              SaveButton(nameState.value.isNotBlank()) {
                groupViewModel.createGroup(nameState.value, photoState.value)
                navigationActions.goBack()
              }
            }
      }
}
