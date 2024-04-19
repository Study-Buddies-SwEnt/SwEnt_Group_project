package com.github.se.studybuddies.ui.settings

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.firebase.ui.auth.AuthUI
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.GoBackRouteButton
import com.github.se.studybuddies.ui.Sub_title
import com.github.se.studybuddies.ui.permissions.checkPermission
import com.github.se.studybuddies.viewModels.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettings(
    uid: String,
    userViewModel: UserViewModel,
    backRoute: String,
    navigationActions: NavigationActions
) {
  userViewModel.fetchUserData(uid)
  val userData by userViewModel.userData.observeAsState()

  val emailState = remember { mutableStateOf(userData?.email ?: "") }
  val usernameState = remember { mutableStateOf(userData?.username ?: "") }
  val photoState = remember { mutableStateOf(userData?.photoUrl ?: Uri.EMPTY) }

  val context = LocalContext.current

  userData?.let {
    emailState.value = it.email
    usernameState.value = it.username
    photoState.value = it.photoUrl
  }

  val getContent =
      rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { profilePictureUri ->
          photoState.value = profilePictureUri
          userViewModel.updateUserData(
              userViewModel.getCurrentUserUID(),
              emailState.value,
              usernameState.value,
              photoState.value)
        }
      }

  val requestPermissionLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
          getContent.launch("image/*")
        }
      }
  var permission = "android.permission.READ_MEDIA_IMAGES"
  // Check if the Android version is lower than TIRAMISU API 33
  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
    // For older Android versions, use READ_EXTERNAL_STORAGE permission
    permission = "android.permission.READ_EXTERNAL_STORAGE"
  }

  Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Top) {
        CenterAlignedTopAppBar(
            title = { Sub_title(title = "Profile setting") },
            navigationIcon = {
              GoBackRouteButton(navigationActions = navigationActions, backRoute)
            })
        Spacer(Modifier.height(150.dp))
        SetProfilePicture(photoState) {
          checkPermission(context, permission, requestPermissionLauncher)
        }
        Spacer(Modifier.height(60.dp))
        SignOutButton(navigationActions)
      }
}

@Composable
private fun SignOutButton(navigationActions: NavigationActions) {
  val context = LocalContext.current // Get the context here
  Button(
      onClick = {
        AuthUI.getInstance().signOut(context).addOnCompleteListener {
          if (it.isSuccessful) {
            navigationActions.navigateTo(Route.LOGIN)
          }
        }
      },
      colors =
          ButtonDefaults.buttonColors(
              containerColor = Color.White,
          ),
      modifier =
          Modifier.border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(50))
              .background(color = Color.Transparent, shape = RoundedCornerShape(50))
              .width(250.dp)
              .height(50.dp)
              .testTag("LoginButton"),
      shape = RoundedCornerShape(50)) {
        Text("Sign out", color = Color.Black)
      }
}
