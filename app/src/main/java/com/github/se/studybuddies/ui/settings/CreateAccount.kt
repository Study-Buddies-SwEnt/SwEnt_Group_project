package com.github.se.studybuddies.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.permissions.checkPermission
import com.github.se.studybuddies.ui.permissions.imagePermissionVersion
import com.github.se.studybuddies.viewModels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun CreateAccount(
    userViewModel: UserViewModel,
    navigationActions: NavigationActions,
    signInSuccessful: MutableState<Boolean>
) {
  val coroutineScope = rememberCoroutineScope()

  var uid = userViewModel.uid
  if (uid == null) {
    uid = userViewModel.getCurrentUserUID()
  }
  userViewModel.fetchUserData(uid)
  val user by userViewModel.userData.observeAsState()
  val email = FirebaseAuth.getInstance().currentUser?.email ?: ""
  val usernameState = remember { mutableStateOf("") }
  val photoState = remember { mutableStateOf(Uri.EMPTY) }
  val context = LocalContext.current

  user?.let {
    coroutineScope.launch {
      val defaultProfilePictureUri = userViewModel.getDefaultProfilePicture()
      photoState.value = defaultProfilePictureUri
    }
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
  val permission = imagePermissionVersion()

  Column(modifier = Modifier.fillMaxSize().testTag("create_account")) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally) {
          item {
            Column(
                modifier = Modifier.fillMaxWidth().testTag("content"),
                verticalArrangement = Arrangement.spacedBy(20.dp)) {
                  Text(stringResource(R.string.you_have_signed_in_with_email, email))
                  Spacer(modifier = Modifier.padding(20.dp))
                  AccountFields(usernameState)
                  Spacer(modifier = Modifier.padding(20.dp))
                  SetProfilePicture(photoState) {
                    checkPermission(context, permission, requestPermissionLauncher) {
                      getContent.launch(imageInput)
                    }
                  }
                  SaveButton(usernameState) {
                    userViewModel.createUser(uid, email, usernameState.value, photoState.value)
                    navigationActions.navigateTo(Route.GROUPSHOME)
                    signInSuccessful.value = true
                  }
                }
          }
        }
  }
}
