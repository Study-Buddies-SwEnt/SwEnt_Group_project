package com.github.se.studybuddies.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.screens.SecondaryTopBar
import com.github.se.studybuddies.viewModels.ChatViewModel
import com.github.se.studybuddies.viewModels.DirectMessageViewModel
import com.github.se.studybuddies.viewModels.MessageViewModel
import com.github.se.studybuddies.viewModels.UsersViewModel

@Composable
fun DirectMessageScreen(
    viewModel: DirectMessageViewModel,
    chatViewModel: ChatViewModel,
    usersViewModel: UsersViewModel,
    navigationActions: NavigationActions
) {
  val showAddPrivateMessageList = remember { mutableStateOf(false) }
  val chats = viewModel.directMessages.collectAsState(initial = emptyList()).value

  Column {
    TopNavigationBar(
        title = {
          if (showAddPrivateMessageList.value) {
            Text(stringResource(R.string.start_direct_message_title))
          } else {
            Text(text = stringResource(R.string.direct_messages_title))
          }
        },
        navigationIcon = {
          GoBackRouteButton(navigationActions = navigationActions, backRoute = Route.GROUPSHOME)
        },
        actions = {
          IconButton(
              onClick = { showAddPrivateMessageList.value = !showAddPrivateMessageList.value }) {
                if (showAddPrivateMessageList.value) {
                  Icon(
                      Icons.Default.Close,
                      contentDescription = stringResource(R.string.new_private_message_icon))
                } else {
                  Icon(
                      Icons.Default.Add,
                      contentDescription = stringResource(R.string.new_private_message_icon))
                }
              }
        })

    if (showAddPrivateMessageList.value) {
      ListAllUsers(showAddPrivateMessageList, usersViewModel)
    } else {
      LazyColumn() {
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DirectMessageItem(chat: Chat, onClick: () -> Unit = {}) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth().padding(8.dp).combinedClickable(onClick = { onClick() })) {
        Image(
            painter = rememberImagePainter(chat.photoUrl),
            contentDescription = "User profile picture",
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
fun ListAllUsers(showAddPrivateMessageList: MutableState<Boolean>, usersViewModel: UsersViewModel) {
  val friendsData by usersViewModel.friends.collectAsState()

  LazyColumn(modifier = Modifier.fillMaxWidth()) {
    items(friendsData) { UserItem(it, showAddPrivateMessageList) }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserItem(user: User, showAddPrivateMessageList: MutableState<Boolean>) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
          Modifier.fillMaxWidth()
              .padding(8.dp)
              .combinedClickable(
                  onClick = {
                    val messageViewModel = MessageViewModel(Chat.empty())
                    messageViewModel.startDirectMessage(user.uid)
                    showAddPrivateMessageList.value = false
                  })) {
        Image(
            painter = rememberImagePainter(user.photoUrl),
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
