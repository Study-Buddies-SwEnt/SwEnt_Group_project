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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.database.DbRepository
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.permissions.checkPermission
import com.github.se.studybuddies.permissions.imagePermissionVersion
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
import com.github.se.studybuddies.ui.shared_elements.SaveButton
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.GroupViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GroupSetting(
    groupUID: String,
    groupViewModel: GroupViewModel,
    navigationActions: NavigationActions,
    db: DbRepository
) {

  if (groupUID.isEmpty()) return
  groupViewModel.fetchGroupData(groupUID)
  val groupData by groupViewModel.group.observeAsState()
  val isBoxVisible = remember { mutableStateOf(false) }

  val nameState = remember { mutableStateOf(groupData?.name ?: "") }
  val photoState = remember { mutableStateOf(groupData?.picture ?: Uri.EMPTY) }
  val groupLink = remember { mutableStateOf("") }

  val context = LocalContext.current

  groupData?.let {
    nameState.value = it.name
    photoState.value = it.picture
    groupLink.value = groupViewModel.createGroupInviteLink(groupUID, it.name)
  }

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
            actions = { GroupsSettingsButton(groupUID, navigationActions, db) })
      }) { paddingValues ->
        if (isBoxVisible.value) {
          ShowContact(groupUID, groupViewModel, isBoxVisible)
        } else {
          Column(
              modifier = Modifier.fillMaxSize(),
              horizontalAlignment = Alignment.Start,
              verticalArrangement = Arrangement.Top) {
                LazyColumn(
                    modifier =
                        Modifier.fillMaxSize()
                            .padding(paddingValues)
                            .testTag("modify_group_column"),
                    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                      item { Spacer(modifier = Modifier.padding(10.dp)) }
                      item { ModifyName(nameState) }
                      item { Spacer(modifier = Modifier.padding(10.dp)) }
                      item {
                        ModifyProfilePicture(photoState) {
                          checkPermission(context, permission, requestPermissionLauncher) {
                            getContent.launch(imageInput)
                          }
                        }
                      }
                      item { Spacer(modifier = Modifier.padding(10.dp)) }
                      item { AddMemberButtonUID(groupUID, groupViewModel) }
                      item { AddMemberButtonList(isBoxVisible) }
                      item { ShareLinkButton(groupLink.value) }
                      item { Spacer(modifier = Modifier.padding(10.dp)) }
                      item {
                        SaveButton(nameState) {
                          groupViewModel.updateGroup(groupUID, nameState.value, photoState.value)
                          navigationActions.navigateTo(Route.GROUPSHOME)
                        }
                      }
                    }
              }
        }
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyName(nameState: MutableState<String>) {
  Spacer(Modifier.height(20.dp))
  OutlinedTextField(
      value = nameState.value,
      onValueChange = { nameState.value = it },
      singleLine = true,
      modifier =
          Modifier.padding(0.dp).clip(MaterialTheme.shapes.small).testTag("group_name_field"),
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
      painter = rememberAsyncImagePainter(photoState.value),
      contentDescription = "Profile Picture",
      modifier = Modifier.size(200.dp).border(1.dp, Blue, RoundedCornerShape(5.dp)),
      contentScale = ContentScale.Crop)
  Spacer(Modifier.height(20.dp))
  Text(
      text = stringResource(R.string.modify_the_profile_picture),
      modifier = Modifier.clickable { onClick() }.testTag("set_picture_button"))
}

@Composable
fun AddMemberButtonUID(groupUID: String, groupViewModel: GroupViewModel) {
  var isTextFieldVisible by remember { mutableStateOf(false) }
  var text by remember { mutableStateOf("") }
  var showError by remember { mutableStateOf(false) }
  var showSucces by remember { mutableStateOf(false) }

  Column {
    Button(
        onClick = {
          isTextFieldVisible = !isTextFieldVisible
          showSucces = false
          showError = false
        },
        shape = MaterialTheme.shapes.medium,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Blue,
            )) {
          Text("Add member with UID", color = Color.White)
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
                  if (text == "") text = "Error"
                  groupViewModel.addUserToGroup(groupUID, text) { isError ->
                    if (isError) {
                      showError = true
                      if (text == "Error") text = ""
                    } else {
                      showSucces = true
                      text = ""
                    }
                  }
                }))
  }
  if (showError) {
    Snackbar(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        action = {
          TextButton(modifier = Modifier.fillMaxWidth(), onClick = { showError = false }) {}
        }) {
          Text(stringResource(R.string.can_t_find_a_member_with_this_uid))
        }
  }
  if (showSucces) {
    Snackbar(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        action = {
          TextButton(modifier = Modifier.fillMaxWidth(), onClick = { showSucces = false }) {}
        }) {
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
fun AddMemberButtonList(isBoxVisible: MutableState<Boolean>) {

  Column {
    Button(
        onClick = { isBoxVisible.value = true },
        shape = MaterialTheme.shapes.medium,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Blue,
            )) {
          Text("Add member from list", color = Color.White)
        }
  }
}

@Composable
fun ShowContact(
    groupUID: String,
    groupViewModel: GroupViewModel,
    isBoxVisible: MutableState<Boolean>
) {
  groupViewModel.getAllFriendsGroup(groupViewModel.getCurrentUser())
  val members by groupViewModel.membersGroup.observeAsState()
  members?.let {
    Box(modifier = Modifier.fillMaxSize()) {
      LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { Spacer(modifier = Modifier.height(64.dp)) }
        item {
          Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                onClick = { isBoxVisible.value = false },
                modifier = Modifier.align(Alignment.TopEnd)) {
                  Icon(
                      imageVector = Icons.Default.Close,
                      modifier = Modifier.size(40.dp),
                      contentDescription = "Close friends List",
                  )
                }
          }
        }
        item { Spacer(modifier = Modifier.height(4.dp)) }
        items(members!!) { member -> ShowOneUser(member, groupViewModel, groupUID, isBoxVisible) }
      }
    }
  }
}

@Composable
fun ShowOneUser(
    user: User,
    groupViewModel: GroupViewModel,
    groupUID: String,
    isBoxVisible: MutableState<Boolean>
) {
  Box(
      modifier =
          Modifier.clickable {
                groupViewModel.addUserToGroup(groupUID, user.uid) {}
                isBoxVisible.value = false
              }
              .fillMaxWidth()
              .background(Color.White)
              .drawBehind {
                val strokeWidth = 4f
                val y = size.height - strokeWidth / 2
                drawLine(Color.LightGray, Offset(0f, y), Offset(size.width, y), strokeWidth)
              }) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
          Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(Color.Transparent)) {
            Image(
                painter = rememberAsyncImagePainter(user.photoUrl),
                contentDescription = stringResource(id = R.string.user_picture),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop)
          }
          Spacer(modifier = Modifier.size(16.dp))
          Text(
              text = user.username,
              modifier = Modifier.align(Alignment.CenterVertically),
              style = TextStyle(fontSize = 20.sp),
              lineHeight = 28.sp)
        }
      }
}
