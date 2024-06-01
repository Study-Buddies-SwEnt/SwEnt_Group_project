package com.github.se.studybuddies.ui.account

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.firebase.ui.auth.AuthUI
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.permissions.checkPermission
import com.github.se.studybuddies.permissions.imagePermissionVersion
import com.github.se.studybuddies.ui.shared_elements.AccountFields
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
import com.github.se.studybuddies.ui.shared_elements.SaveButton
import com.github.se.studybuddies.ui.shared_elements.SetProfilePicture
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.UserViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AccountSettings(
    uid: String,
    userViewModel: UserViewModel,
    backRoute: String,
    navigationActions: NavigationActions
) {
  if (uid.isEmpty()) return
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
          userViewModel.updateUserData(uid, emailState.value, usernameState.value, photoState.value)
        }
      }
  val imageInput = "image/*"

  val requestPermissionLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
          getContent.launch(imageInput)
        }
      }
  val permission = imagePermissionVersion()

  Scaffold(
      modifier = Modifier.fillMaxSize().background(White).testTag("account_settings"),
      topBar = {
        TopNavigationBar(
            title = { Sub_title(title = stringResource(R.string.profile_setting)) },
            leftButton = { GoBackRouteButton(navigationActions = navigationActions, backRoute) },
            rightButton = {})
      }) { paddingValue ->
        Column(
            modifier = Modifier.fillMaxSize().testTag("account_settings_column"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              Spacer(modifier = Modifier.size(20.dp).testTag("account_settings_spacer1"))
              AccountFields(usernameState)
              Spacer(modifier = Modifier.size(10.dp).testTag("account_settings_spacer2"))
              SetProfilePicture(photoState) {
                checkPermission(context, permission, requestPermissionLauncher) {
                  getContent.launch(imageInput)
                }
              }
              Spacer(modifier = Modifier.size(10.dp).testTag("account_settings_spacer3"))
              SaveButton(usernameState) {
                userViewModel.updateUserData(
                    uid, emailState.value, usernameState.value, photoState.value)
                navigationActions.navigateTo(Route.SOLOSTUDYHOME)
              }
              SignOutButton(navigationActions, userViewModel)
            }
      }
}

@Composable
private fun SignOutButton(navigationActions: NavigationActions, userViewModel: UserViewModel) {
  val context = LocalContext.current // Get the context here
  Button(
      onClick = {
        AuthUI.getInstance().signOut(context).addOnCompleteListener {
          if (it.isSuccessful) {
            userViewModel.signOut()
            navigationActions.navigateTo(Route.LOGIN)
          } else {
            Log.d("SignOut", "Failed to sign out or MockDatabase is being used.")
            userViewModel.signOut()
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
              .testTag("sign_out_button"),
      shape = RoundedCornerShape(50)) {
        Text(text = stringResource(R.string.sign_out), color = Color.Black)
      }
}
