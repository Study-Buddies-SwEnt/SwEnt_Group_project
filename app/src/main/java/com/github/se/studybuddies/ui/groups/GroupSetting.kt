package com.github.se.studybuddies.ui.groups

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.github.se.studybuddies.database.DatabaseConnection
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.GoBackRouteButton
import com.github.se.studybuddies.ui.Sub_title
import com.github.se.studybuddies.ui.TopNavigationBar
import com.github.se.studybuddies.ui.permissions.checkPermission
import com.github.se.studybuddies.ui.permissions.imagePermissionVersion
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.utility.createGroupInviteLink
import com.github.se.studybuddies.viewModels.GroupViewModel
import kotlinx.coroutines.launch

private val db = DatabaseConnection()

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GroupSetting(
    groupUID: String,
    groupViewModel: GroupViewModel,
    navigationActions: NavigationActions
) {

  val scope = rememberCoroutineScope()
  val nameState = remember { mutableStateOf("") }
  val photoState = remember { mutableStateOf(Uri.EMPTY) }
  val context = LocalContext.current

  val picture = remember { mutableStateOf(Uri.EMPTY) }
  val name = remember { mutableStateOf("") }
  // val member = remember { mutableStateOf(???) }
  val groupLink = remember { mutableStateOf("") }

  LaunchedEffect(key1 = true) {
    scope.launch {
      val group = db.getGroup(groupUID)

      picture.value = group.picture
      name.value = group.name
      // todo member.value = group.members

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
            title = { Sub_title("Group settings") },
            navigationIcon = {
              GoBackRouteButton(navigationActions = navigationActions, Route.GROUPSHOME)
            },
            actions = {})
      }) {
        Column(
            modifier = Modifier.fillMaxWidth().background(White).testTag("modify_group_column"),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Spacer(modifier = Modifier.padding(20.dp))
              ModifyName(name.value, nameState)
              Spacer(modifier = Modifier.padding(10.dp))
              ModifyProfilePicture(photoState) {
                checkPermission(context, permission, requestPermissionLauncher) {
                  getContent.launch(imageInput)
                }
              }
              Spacer(modifier = Modifier.padding(20.dp))
              AddMemberButton(db)
              Spacer(modifier = Modifier.padding(0.dp))
              ShareLinkButton(groupLink.value)
              Spacer(modifier = Modifier.padding(0.dp))
              // todo draw all member
              Spacer(modifier = Modifier.padding(10.dp))
              SaveButton(nameState) {
                // todo replace by update group
                // groupViewModel.createGroup(nameState.value, photoState.value)
                navigationActions.navigateTo(Route.GROUPSHOME)
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
      placeholder = { Text("Enter a new group name", color = Blue) },
      singleLine = true,
      modifier =
          Modifier.padding(0.dp)
              .width(300.dp)
              .height(65.dp)
              .clip(MaterialTheme.shapes.small)
              .testTag("group_name_field"),
      colors =
          TextFieldDefaults.outlinedTextFieldColors(
              focusedBorderColor = Blue, unfocusedBorderColor = Blue, cursorColor = Blue))
}

@Composable
fun ModifyProfilePicture(photoState: MutableState<Uri>, onClick: () -> Unit) {
  Image(
      painter = rememberImagePainter(photoState.value),
      contentDescription = "Profile Picture",
      modifier = Modifier.size(200.dp),
      contentScale = ContentScale.Crop)
  Spacer(Modifier.height(20.dp))
  Text(
      text = "Modify the profile picture",
      modifier = Modifier.clickable { onClick() }.testTag("set_picture_button"))
}

@Composable
fun AddMemberButton(db: DatabaseConnection) {
  var isTextFieldVisible by remember { mutableStateOf(false) }
  var text by remember { mutableStateOf("") }
  val scope = rememberCoroutineScope()

  Column {
    Button(
        onClick = { isTextFieldVisible = !isTextFieldVisible },
        shape = MaterialTheme.shapes.medium,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Blue,
            )) {
          Text("Add Members", color = Color.White)
        }

    if (isTextFieldVisible) {
      OutlinedTextField(
          value = text,
          onValueChange = { text = it },
          label = { Text("Enter UserID") },
          singleLine = true
          // todo add the user to the database
          // copy from GroupHome for the error and call of updateGroup
          // use function updateGroup, by adding an entry,
          )
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
          Text("Share Link", color = Color.White)
        }

    if (isTextVisible) {
      TextField(
          value = text, onValueChange = {}, readOnly = true, modifier = Modifier.padding(16.dp))
    }
  }
}
