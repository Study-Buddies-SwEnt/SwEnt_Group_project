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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.viewModels.ChatViewModel
import com.github.se.studybuddies.viewModels.DirectMessageViewModel

@Composable
fun DirectMessageScreen(viewModel: DirectMessageViewModel, chatViewModel: ChatViewModel, navigationActions: NavigationActions) {
  val chats = viewModel.directMessages.collectAsState(initial = emptyList())
  Column {
    SecondaryTopBar(onClick = { navigationActions.goBack() }) {}

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