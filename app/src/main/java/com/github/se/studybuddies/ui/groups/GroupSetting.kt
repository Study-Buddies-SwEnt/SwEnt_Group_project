package com.github.se.studybuddies.ui.groups

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.github.se.studybuddies.R
import com.github.se.studybuddies.database.DatabaseConnection
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.permissions.checkPermission
import com.github.se.studybuddies.permissions.imagePermissionVersion
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.utility.createGroupInviteLink
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val db = DatabaseConnection()

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GroupSetting(groupUID: String, navigationActions: NavigationActions) {

  val scope = rememberCoroutineScope()
  val nameState = remember { mutableStateOf("") }
  val photoState = remember { mutableStateOf(Uri.EMPTY) }
  val context = LocalContext.current

  val picture = remember { mutableStateOf(Uri.EMPTY) }
  val name = remember { mutableStateOf("") }
  val groupLink = remember { mutableStateOf("") }

  LaunchedEffect(key1 = true) {
    scope.launch {
      val group = db.getGroup(groupUID)

      picture.value = group.picture
      name.value = group.name

      groupLink.value = createGroupInviteLink(groupUID, name.value)
    }
  }
  photoState.value = picture.value

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
            title = { Sub_title(stringResource(R.string.group_settings)) },
            navigationIcon = {
              GoBackRouteButton(navigationActions = navigationActions, Route.GROUPSHOME)
            },
            actions = {})
      }) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().background(White).testTag("modify_group_column"),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              item { Spacer(modifier = Modifier.padding(20.dp)) }
              item { ModifyName(name.value, nameState) }
              item { Spacer(modifier = Modifier.padding(10.dp)) }
              item {
                ModifyProfilePicture(photoState) {
                  checkPermission(context, permission, requestPermissionLauncher) {
                    getContent.launch(imageInput)
                  }
                }
              }
              item { Spacer(modifier = Modifier.padding(20.dp)) }
              item { AddMemberButton(groupUID, db) }
              item { Spacer(modifier = Modifier.padding(0.dp)) }
              item { ShareLinkButton(groupLink.value) }
              item { Spacer(modifier = Modifier.padding(10.dp)) }
              item {
                SaveGroupButton {
                  if (nameState.value == "") {
                    db.updateGroup(groupUID, name.value, photoState.value)
                  } else {
                    db.updateGroup(groupUID, nameState.value, photoState.value)
                  }
                  navigationActions.navigateTo(Route.GROUPSHOME)
                }
              }
            }
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyName(name: String, nameState: MutableState<String>) {
  Spacer(Modifier.height(20.dp))
  OutlinedTextField(
      value = nameState.value,
      onValueChange = { nameState.value = it },
      label = { Text(name, color = Blue) },
      placeholder = { Text(stringResource(R.string.enter_a_new_group_name), color = Blue) },
      singleLine = true,
      modifier =
          Modifier.padding(0.dp)
              .width(300.dp)
              .height(65.dp)
              .clip(MaterialTheme.shapes.small)
              .testTag("group_name_field"),
      colors =
          OutlinedTextFieldDefaults.colors(
              cursorColor = Blue,
              focusedBorderColor = Blue,
              unfocusedBorderColor = Blue,
          ))
}

@Composable
fun ModifyProfilePicture(photoState: MutableState<Uri>, onClick: () -> Unit) {
  Image(
      painter = rememberImagePainter(photoState.value),
      contentDescription = "Profile Picture",
      modifier = Modifier.size(200.dp).border(1.dp, Blue, RoundedCornerShape(5.dp)),
      contentScale = ContentScale.Crop)
  Spacer(Modifier.height(20.dp))
  Text(
      text = stringResource(R.string.modify_the_profile_picture),
      modifier = Modifier.clickable { onClick() }.testTag("set_picture_button"))
}

@Composable
fun AddMemberButton(groupUID: String, db: DatabaseConnection) {
  var isTextFieldVisible by remember { mutableStateOf(false) }
  var text by remember { mutableStateOf("") }
  var showError by remember { mutableStateOf(false) }
  var showSucces by remember { mutableStateOf(false) }
  val scope = rememberCoroutineScope()

  Column {
    Button(
        onClick = { isTextFieldVisible = !isTextFieldVisible },
        shape = MaterialTheme.shapes.medium,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Blue,
            )) {
          Text(stringResource(R.string.add_members), color = Color.White)
        }
  }

  if (isTextFieldVisible) {
    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text(stringResource(R.string.enter_userID)) },
        singleLine = true,
        colors =
            TextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                unfocusedLabelColor = Blue,
                unfocusedIndicatorColor = Blue,
            ),
        keyboardActions =
            KeyboardActions(
                onDone = {
                  // add the user to the database
                  isTextFieldVisible = false
                  scope.launch {
                    if (text != "") {

                      val error = db.addUserToGroup(groupUID, text)
                      if (error == -1) {
                        showError = true
                        delay(3000L) // delay for 3 seconds
                        showError = false
                      } else {
                        showSucces = true
                        delay(3000L) // delay for 3 seconds
                        showSucces = false
                      }
                    }
                  }
                }))
  }
  if (showError) {
    Snackbar(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        action = { TextButton(onClick = { showError = false }) {} }) {
          Text(stringResource(R.string.can_t_find_a_member_with_this_uid))
        }
  }
  if (showSucces) {
    Snackbar(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        action = { TextButton(onClick = { showSucces = false }) {} }) {
          Text(stringResource(R.string.user_have_been_successfully_added_to_the_group))
        }
  }
}

@Composable
fun ShareLinkButton(groupLink: String) {
  var isTextVisible by remember { mutableStateOf(false) }
  var text by remember { mutableStateOf("") }

  Column {
    Button(
        onClick = {
          text = groupLink
          isTextVisible = !isTextVisible
        },
        shape = MaterialTheme.shapes.medium,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Blue,
            )) {
          Text(stringResource(R.string.share_link), color = Color.White)
        }
  }

  if (isTextVisible) {
    OutlinedTextField(
        value = text,
        onValueChange = {},
        readOnly = true,
        modifier = Modifier.padding(16.dp),
        colors =
            TextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                unfocusedLabelColor = Blue,
                unfocusedIndicatorColor = Blue,
            ))
  }
}

@Composable
fun SaveGroupButton(save: () -> Unit) {
  Button(
      onClick = save,
      modifier =
          Modifier.padding(20.dp)
              .width(300.dp)
              .height(50.dp)
              .background(color = Blue, shape = RoundedCornerShape(size = 10.dp))
              .testTag("save_group_button"),
      colors =
          ButtonDefaults.buttonColors(
              containerColor = Blue,
          )) {
        Text(
            stringResource(R.string.save),
            color = White,
            modifier = Modifier.testTag("save_group_button_text"))
      }
}
