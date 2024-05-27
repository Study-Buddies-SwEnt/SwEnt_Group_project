package com.github.se.studybuddies.ui.account

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.github.se.studybuddies.permissions.checkPermission
import com.github.se.studybuddies.permissions.imagePermissionVersion
import com.github.se.studybuddies.ui.shared_elements.AccountFields
import com.github.se.studybuddies.ui.shared_elements.SaveButton
import com.github.se.studybuddies.ui.shared_elements.SetProfilePicture
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun CreateAccount(userViewModel: UserViewModel, navigationActions: NavigationActions) {
  val coroutineScope = rememberCoroutineScope()

  var uid = userViewModel.uid
  if (uid == null) {
    uid = userViewModel.getCurrentUserUID()
  }
  userViewModel.fetchUserData(uid)
  val user by userViewModel.userData.observeAsState()
  val email =
      if (userViewModel.isFakeDatabase()) {
        "test@gmail.com"
      } else {
        FirebaseAuth.getInstance().currentUser?.email ?: ""
      }
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

  Scaffold(
      modifier = Modifier.fillMaxSize().background(White).testTag("create_account"),
      topBar = {
        TopNavigationBar(
            title = { Sub_title(title = stringResource(R.string.create_account)) },
            navigationIcon = {
              Icon(
                  imageVector = Icons.Default.ArrowBack,
                  contentDescription = "Go back",
                  modifier =
                      Modifier.clickable {
                            if (userViewModel.isFakeDatabase()) {
                              userViewModel.signOut()
                              navigationActions.navigateTo(Route.LOGIN)
                            } else {
                              FirebaseAuth.getInstance().signOut()
                              navigationActions.navigateTo(Route.LOGIN)
                            }
                          }
                          .testTag("go_back_button"))
            },
            actions = {})
      }) { paddingValue ->
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top) {
              LazyColumn(
                  modifier = Modifier.fillMaxWidth().padding(paddingValue).testTag("content"),
                  verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.Top),
                  horizontalAlignment = Alignment.CenterHorizontally) {
                    item { Spacer(modifier = Modifier.size(20.dp)) }
                    item {
                      Text(
                          stringResource(R.string.you_have_signed_in_with_email, email),
                          modifier = Modifier.width(300.dp))
                    }
                    item { Spacer(modifier = Modifier.padding(5.dp)) }
                    item { AccountFields(usernameState) }
                    item { Spacer(modifier = Modifier.padding(5.dp)) }
                    item {
                      SetProfilePicture(photoState) {
                        checkPermission(context, permission, requestPermissionLauncher) {
                          getContent.launch(imageInput)
                        }
                      }
                    }
                    item { Spacer(modifier = Modifier.size(5.dp)) }
                    item {
                      SaveButton(usernameState, testTag = "save_button_account") {
                        userViewModel.createUser(uid, email, usernameState.value, photoState.value)
                        navigationActions.navigateTo(Route.SOLOSTUDYHOME)
                      }
                    }
                  }
            }
      }
}
