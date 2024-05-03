package com.github.se.studybuddies.ui

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.ChatType
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.viewModels.ChatViewModel
import com.github.se.studybuddies.viewModels.DirectMessageViewModel
import com.github.se.studybuddies.viewModels.MessageViewModel

@Composable
fun DirectMessageScreen(
    viewModel: DirectMessageViewModel,
    chatViewModel: ChatViewModel,
    navigationActions: NavigationActions
) {
  val showAddPrivateMessageList = remember { mutableStateOf(false) }
  val chats = viewModel.directMessages.collectAsState(initial = emptyList())

  if (showAddPrivateMessageList.value) {
    chatViewModel
        .getChat()
        ?.let { MessageViewModel(it) }
        ?.let { ListAllUsers(it, showAddPrivateMessageList) }
  }

  Column {
    //    SecondaryTopBar(onClick = { navigationActions.goBack() }) {
    //        Text(text = stringResource(R.string.direct_messages_title), modifier =
    // Modifier.padding(8.dp), fontSize = 20.sp)
    //    }
    TopNavigationBar(
        title = { Text(text = stringResource(R.string.direct_messages_title)) },
        navigationIcon = {
          GoBackRouteButton(navigationActions = navigationActions, backRoute = Route.GROUPSHOME)
        },
        actions = {
          IconButton(
              onClick = {
                  Log.d("MyPrint", "Add private message button clicked")
                showAddPrivateMessageList.value = true
              }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.new_private_message_icon))
              }
        })

    LazyColumn() {
      items(chats.value) { chat ->
        DirectMessageItem(chat) {
          chatViewModel.setChat(chat)
          navigationActions.navigateTo(Route.CHAT)
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

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ListAllUsers(viewModel: MessageViewModel, showAddPrivateMessageList: MutableState<Boolean>) {
  LazyColumn {
    // TODO: Replace with actual users from the database
    items(10) {
      UserItem(
          User("uid", "https://www.example.com", "username", Uri.EMPTY),
          viewModel,
          showAddPrivateMessageList)
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserItem(
    user: User,
    viewModel: MessageViewModel,
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

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
fun ListAllUsersPreview() {
  val viewModel = MessageViewModel(Chat("uid", "name", "photoUrl", ChatType.PRIVATE, emptyList()))
  ListAllUsers(viewModel, mutableStateOf(true))
}
