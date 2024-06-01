package com.github.se.studybuddies.ui.chat

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberImagePainter
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.permissions.imagePermissionVersion
import com.github.se.studybuddies.ui.shared_elements.DeleteButton
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
import com.github.se.studybuddies.ui.shared_elements.SaveButton
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.ContactsViewModel
import com.github.se.studybuddies.viewModels.DirectMessagesViewModel
import com.github.se.studybuddies.viewModels.UserViewModel

@SuppressLint("SuspiciousIndentation")
@Composable
fun ContactScreen(
    contactID: String,
    contactsViewModel: ContactsViewModel,
    navigationActions: NavigationActions,
    userViewModel: UserViewModel,
    directMessagesViewModel: DirectMessagesViewModel
) {

  var isDeleteContactDialogVisible by remember { mutableStateOf(false) }

  val contactTestID = "Ha5sJPd71PFs1lYedpCy"
  val currentUserID = userViewModel.getCurrentUserUID()
  val otherUserID = contactsViewModel.getOtherUser(contactID, currentUserID)
  val contactData by contactsViewModel.contact.observeAsState()
  userViewModel.fetchUserData(otherUserID)
  val otherUserData by userViewModel.userData.observeAsState()

  val nameState = remember { mutableStateOf(otherUserData?.username ?: "") }
  val photoState = remember { mutableStateOf(otherUserData?.photoUrl ?: Uri.EMPTY) }

  val showOnMapState = remember { mutableStateOf(contactData?.showOnMap ?: false) }

  val context = LocalContext.current

  otherUserData?.let {
    nameState.value = it.username
    Log.d("contact", "otherUserData username is ${nameState.value}")
    photoState.value = it.photoUrl
  }

  contactData?.let { showOnMapState.value = it.showOnMap }

  val getContent =
      rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { profilePictureUris -> photoState.value = profilePictureUris }
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
      modifier = Modifier.fillMaxSize().background(White).testTag("modify_group_scaffold"),
      topBar = {
        TopNavigationBar(
            title = { Sub_title(nameState.value) },
            leftButton = { GoBackRouteButton(navigationActions = navigationActions) },
            rightButton = {
              IconButton(onClick = { navigationActions.navigateTo(Route.PLACEHOLDER) }) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.video_call),
                    contentDescription = "",
                    tint = Blue)
              }
            })
        Log.d("contact", "TopNav")
      }) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top) {
              LazyColumn(
                  modifier =
                      Modifier.fillMaxSize()
                          .padding(paddingValues)
                          .testTag("modify_contact_column"),
                  verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
                  horizontalAlignment = Alignment.CenterHorizontally) {
                    item { Spacer(modifier = Modifier.padding(10.dp)) }
                    item {
                      Image(
                          painter = rememberImagePainter(photoState.value),
                          contentDescription = "Profile Picture",
                          modifier =
                              Modifier.size(200.dp).border(1.dp, Blue, RoundedCornerShape(5.dp)),
                          contentScale = ContentScale.Crop)
                    }
                    item { Spacer(modifier = Modifier.padding(20.dp)) }
                    item { ToggleMapVisibilityButton(showOnMapState) }
                    item { Spacer(modifier = Modifier.padding(0.dp)) }
                    item {
                      SaveButton(nameState) {
                        contactsViewModel.updateContact(contactID, showOnMapState.value)
                        navigationActions.navigateTo(Route.CHAT)
                      }
                    }
                    item { DeleteButton { isDeleteContactDialogVisible = true } }
                  }
              Log.d("contact", "Lazycol")
              if (isDeleteContactDialogVisible) {
                DeleteContactDialog(
                    contactID = contactID,
                    onDelete = {
                      isDeleteContactDialogVisible = false
                      contactsViewModel.deleteContact(contactID)
                      directMessagesViewModel.deletePrivateChat(contactID)
                      navigationActions.navigateTo(Route.DIRECT_MESSAGE)
                    },
                    onCancel = { isDeleteContactDialogVisible = false })
              }
            }
      }
}

@Composable
private fun ToggleMapVisibilityButton(showOnMapState: MutableState<Boolean>) {

  if (showOnMapState.value) {
    Button(
        onClick = { showOnMapState.value = false },
        modifier = Modifier.width(300.dp).height(50.dp).clip(RoundedCornerShape(4.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Blue, contentColor = White)) {
          Text(text = stringResource(R.string.location_shared))
        }
  } else {
    Button(
        onClick = { showOnMapState.value = true },
        modifier = Modifier.width(300.dp).height(50.dp).clip(RoundedCornerShape(4.dp)),
        colors =
            ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = White)) {
          Text(text = stringResource(R.string.location_not_shared))
        }
  }
}

@Composable
private fun DeleteContactDialog(onDelete: () -> Unit, onCancel: () -> Unit, contactID: String) {
  Dialog(onDismissRequest = {}) {
    Box(
        modifier =
            Modifier.width(300.dp)
                .height(270.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .testTag(contactID + "_delete_box")) {
          Column(
              modifier = Modifier.padding(16.dp).testTag(contactID + "_delete_column"),
              verticalArrangement = Arrangement.Center,
              horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.warning_1_contact_deletion),
                    modifier = Modifier.testTag(contactID + "_delete_text1"),
                    color = Blue,
                    textAlign = TextAlign.Center)
                Text(
                    text = stringResource(R.string.warning_2_contact_deletion),
                    modifier = Modifier.testTag(contactID + "_delete_text2"),
                    color = Blue,
                    textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(20.dp))

                DeleteButton(onClick = { onDelete() })
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onCancel() },
                    modifier =
                        Modifier.clip(RoundedCornerShape(4.dp))
                            .width(100.dp)
                            .height(40.dp)
                            .testTag(contactID + "_delete_no_button"),
                    colors =
                        ButtonDefaults.buttonColors(containerColor = Blue, contentColor = White)) {
                      Text(
                          text = stringResource(R.string.cancel),
                          modifier = Modifier.testTag(contactID + "_delete_no_text"))
                    }
              }
        }
  }
}
