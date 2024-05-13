package com.github.se.studybuddies.ui.chat

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.Contact
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.shared_elements.MainScreenScaffold
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.ChatViewModel
import com.github.se.studybuddies.viewModels.ContactsViewModel
import com.github.se.studybuddies.viewModels.DirectMessageViewModel
import com.github.se.studybuddies.viewModels.UsersViewModel
import kotlinx.coroutines.delay

@Composable
fun DirectMessageScreen(
    viewModel: DirectMessageViewModel,
    chatViewModel: ChatViewModel,
    usersViewModel: UsersViewModel,
    navigationActions: NavigationActions,
    contactsViewModel: ContactsViewModel
) {
  val showAddPrivateMessageList = remember { mutableStateOf(false) }
  val chats = viewModel.directMessages.collectAsState(initial = emptyList()).value
  var isLoading by remember { mutableStateOf(true) }

  LaunchedEffect(chats) {
    if (chats.isNotEmpty()) {
      isLoading = false
    } else {
      repeat(10) {
        delay(500)
        if (chats.isNotEmpty()) {
          isLoading = false
          return@repeat
        }
      }
      isLoading = false
    }
  }

  MainScreenScaffold(
      navigationActions = navigationActions,
      backRoute = Route.DIRECT_MESSAGE,
      content = { innerPadding ->
        if (isLoading) {
          Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
          }
        } else if (showAddPrivateMessageList.value) {
          ListAllUsers(showAddPrivateMessageList, viewModel, usersViewModel)
          //ListAllContacts(showAddPrivateMessageList, viewModel, contactsViewModel)
        } else {
          if (chats.isEmpty()) {
            Log.d("MyPrint", "DirectMessageScreen: chats is empty")
            Text(text = stringResource(R.string.direct_messages_empty))
          } else {
            Log.d("MyPrint", "DirectMessageScreen: chats is not empty")
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
            ) {
              LazyColumn(modifier = Modifier.fillMaxWidth().padding(innerPadding)) {
                items(chats) { chat ->
                  DirectMessageItem(chat) {
                    chatViewModel.setChat(chat)
                    navigationActions.navigateTo(Route.CHAT)
                  }
                }
              }
            }
          }
        }
        Box(
            contentAlignment = Alignment.BottomEnd, // Aligns the button to the bottom end (right)
            modifier =
                Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding())) {
              AddNewPrivateMessage(showAddPrivateMessageList)
            }
      },
      title =
          if (showAddPrivateMessageList.value) stringResource(R.string.start_direct_message_title)
          else stringResource(R.string.direct_messages_title),
      iconOptions = {})
}

@Composable
fun AddNewPrivateMessage(showAddPrivateMessageList: MutableState<Boolean>) {
  Row(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      verticalAlignment = Alignment.Bottom,
      horizontalArrangement = Arrangement.End) {
        Button(
            onClick = { showAddPrivateMessageList.value = !showAddPrivateMessageList.value },
            modifier = Modifier.width(64.dp).height(64.dp).clip(MaterialTheme.shapes.medium)) {
              if (showAddPrivateMessageList.value) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.create_a_task),
                    tint = White)
              } else {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.create_a_task),
                    tint = White)
              }
            }
      }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DirectMessageItem(chat: Chat, onClick: () -> Unit = {}) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth().padding(8.dp).combinedClickable(onClick = { onClick() })) {
        Image(
            painter = rememberAsyncImagePainter(chat.picture),
            contentDescription = stringResource(R.string.contentDescription_user_profile_picture),
            modifier =
                Modifier.padding(8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
                    .align(Alignment.CenterVertically)
                    .testTag("chat_user_profile_picture"),
            contentScale = ContentScale.Crop)
        Text(text = chat.name)
        Spacer(modifier = Modifier.weight(1f))
      }
}

@Composable
fun ListAllUsers(
    showAddPrivateMessageList: MutableState<Boolean>,
    viewModel: DirectMessageViewModel,
    usersViewModel: UsersViewModel,
) {
  val friendsData by usersViewModel.friends.collectAsState()
  var isLoading by remember { mutableStateOf(true) }

  LaunchedEffect(friendsData) {
    if (friendsData.isNotEmpty()) {
      isLoading = false
    } else {
      repeat(10) {
        delay(500)
        if (friendsData.isNotEmpty()) {
          isLoading = false
          return@repeat
        }
      }
      isLoading = false
    }
  }

  if (isLoading) {
    Box(modifier = Modifier.fillMaxSize()) {
      CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
  } else {
    if (friendsData.isEmpty()) {
      Text(text = stringResource(R.string.no_friends_found))
    } else {
      LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(friendsData) { friend -> UserItem(friend, viewModel, showAddPrivateMessageList) }
      }
    }
  }
}

@Composable
fun ListAllContacts(
    showAddPrivateMessageList: MutableState<Boolean>,
    viewModel: DirectMessageViewModel,
    contactsViewModel: ContactsViewModel
) {

  val contactsData by contactsViewModel.contacts.collectAsState()

  var isLoading by remember { mutableStateOf(true) }

  LaunchedEffect(contactsData) {
    if (contactsData.isNotEmpty()) {
      isLoading = false
    } else {
      repeat(10) {
        delay(500)
        if (contactsData.isNotEmpty()) {
          isLoading = false
          return@repeat
        }
      }
      isLoading = false
    }
  }

  if (isLoading) {
    Box(modifier = Modifier.fillMaxSize()) {
      CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
  } else {
    if (contactsData.isEmpty()) {
      Text(text = stringResource(R.string.no_friends_found))
    } else {
      LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(contactsData) { contact ->
          ContactItem(contact, viewModel, showAddPrivateMessageList)
        }
      }
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserItem(
    user: User,
    viewModel: DirectMessageViewModel,
    showAddPrivateMessageList: MutableState<Boolean>
) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          Modifier.fillMaxWidth()
              .padding(8.dp)
              .combinedClickable(
                  onClick = {
                    viewModel.startDirectMessage(user.uid)
                    showAddPrivateMessageList.value = false
                  })) {
        Image(
            painter = rememberAsyncImagePainter(user.photoUrl),
            contentDescription = stringResource(R.string.contentDescription_user_profile_picture),
            modifier =
                Modifier.padding(8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
                    .align(Alignment.CenterVertically)
                    .testTag("chat_user_profile_picture"),
            contentScale = ContentScale.Crop)
        Text(text = user.username)
      }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactItem(
    contact: Contact,
    viewModel: DirectMessageViewModel,
    showAddPrivateMessageList: MutableState<Boolean>
) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          Modifier.fillMaxWidth()
              .padding(8.dp)
              .combinedClickable(
                  onClick = {
                    viewModel.startDirectMessage(contact.uid)
                    showAddPrivateMessageList.value = false
                  })) {
        Image(
            painter = rememberAsyncImagePainter(contact.members.first),
            contentDescription = stringResource(R.string.contentDescription_user_profile_picture),
            modifier =
                Modifier.padding(8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
                    .align(Alignment.CenterVertically)
                    .testTag("chat_user_profile_picture"),
            contentScale = ContentScale.Crop)
        Text(text = contact.members.first)
      }
}
