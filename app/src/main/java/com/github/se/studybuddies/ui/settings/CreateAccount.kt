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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.viewModels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CreateAccount(userViewModel: UserViewModel, navigationActions: NavigationActions) {
  val uid = userViewModel.getCurrentUserUID()

  val email = FirebaseAuth.getInstance().currentUser?.email ?: ""
  val usernameState = remember { mutableStateOf("") }
  val photoState = remember { mutableStateOf(Uri.EMPTY) }

  LaunchedEffect(key1 = true) {
    val defaultProfilePictureUri =
        withContext(Dispatchers.IO) { userViewModel.getDefaultProfilePicture() }
    photoState.value = defaultProfilePictureUri
  }

  val getContent =
      rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { profilePictureUri -> photoState.value = profilePictureUri }
      }

  Column(modifier = Modifier.fillMaxSize().testTag("create_account")) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally) {
          item {
            Column(
                modifier = Modifier.fillMaxWidth().testTag("content"),
                verticalArrangement = Arrangement.spacedBy(20.dp)) {
                  Text("You signed in with the email address $email")
                  Spacer(modifier = Modifier.padding(20.dp))
                  AccountFields(usernameState)
                  Spacer(modifier = Modifier.padding(20.dp))
                  SetProfilePicture(photoState) { getContent.launch("image/*") }
                  SaveButton(usernameState) {
                    userViewModel.createUser(uid, email, usernameState.value, photoState.value)
                    navigationActions.navigateTo(Route.GROUPSHOME)
                  }
                }
          }
        }
  }
}
