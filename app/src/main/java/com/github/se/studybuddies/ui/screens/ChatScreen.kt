package com.github.se.studybuddies.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.ChatType
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.LightBlue
import com.github.se.studybuddies.viewModels.MessageViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(viewModel: MessageViewModel, navigationActions: NavigationActions) {
  val messages = viewModel.messages.collectAsState(initial = emptyList()).value
  val showOptionsDialog = remember { mutableStateOf(false) }
  val showEditDialog = remember { mutableStateOf(false) }
  var selectedMessage by remember { mutableStateOf<Message?>(null) }
  val listState = rememberLazyListState()

  LaunchedEffect(messages) {
    if (messages.isNotEmpty()) {
      listState.scrollToItem(messages.lastIndex)
    }
  }

  OptionsDialog(viewModel, selectedMessage, showOptionsDialog, showEditDialog, navigationActions)
  EditDialog(viewModel, selectedMessage, showEditDialog)

  Column(
      modifier =
          Modifier.fillMaxSize()
              .background(LightBlue)
              .navigationBarsPadding()
              .testTag("chat_screen")) {
        SecondaryTopBar(onClick = { navigationActions.goBack() }) {
          when (viewModel.chat.type) {
            ChatType.GROUP,
            ChatType.TOPIC -> ChatGroupTitle(viewModel.chat)
            ChatType.PRIVATE -> PrivateChatTitle(viewModel.chat)
          }
        }
        LazyColumn(state = listState, modifier = Modifier.weight(1f).padding(8.dp)) {
          items(messages) { message ->
            Row(
                modifier =
                    Modifier.fillMaxWidth()
                        .padding(2.dp)
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                              selectedMessage = message
                              showOptionsDialog.value = true
                            })
                        .testTag("chat_message_row"),
                horizontalArrangement =
                    if (viewModel.isUserMessageSender(message)) {
                      Arrangement.End
                    } else {
                      Arrangement.Start
                    }) {
                  TextBubble(
                      message,
                      !viewModel.isUserMessageSender(message) &&
                          viewModel.chat.type != ChatType.PRIVATE)
                }
          }
        }
        MessageTextFields(onSend = { viewModel.sendMessage(it) })
      }
}

@Composable
fun TextBubble(message: Message, displayName: Boolean = false) {
  Row(modifier = Modifier.padding(1.dp).testTag("chat_text_bubble")) {
    if (displayName) {
      Image(
          painter = rememberImagePainter(message.sender.photoUrl.toString()),
          contentDescription = stringResource(R.string.contentDescription_user_profile_picture),
          modifier =
              Modifier.size(40.dp)
                  .clip(CircleShape)
                  .border(2.dp, Gray, CircleShape)
                  .align(Alignment.CenterVertically)
                  .testTag("chat_user_profile_picture"),
          contentScale = ContentScale.Crop)

      Spacer(modifier = Modifier.width(8.dp))
    }

    Box(
        modifier =
            Modifier.background(White, RoundedCornerShape(20.dp))
                .padding(1.dp)
                .testTag("chat_text_bubble_box")) {
          Column(modifier = Modifier.padding(8.dp)) {
            if (displayName) {
              Text(
                  text = message.sender.username,
                  fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                  style = TextStyle(color = Black),
                  modifier = Modifier.testTag("chat_message_sender_name"))
            }
            Text(
                text = message.text,
                style = TextStyle(color = Black),
                modifier = Modifier.testTag("chat_message_text"))
            Text(
                text = message.getTime(),
                style = TextStyle(color = Gray),
                modifier = Modifier.testTag("chat_message_time"))
          }
        }
  }
}

@Composable
fun MessageTextFields(onSend: (String) -> Unit, defaultText: String = "") {
  var textToSend by remember { mutableStateOf(defaultText) }
  OutlinedTextField(
      value = textToSend,
      onValueChange = { textToSend = it },
      modifier =
          Modifier.padding(8.dp)
              .fillMaxWidth()
              .background(White, RoundedCornerShape(20.dp))
              .testTag("chat_text_field"),
      shape = RoundedCornerShape(20.dp),
      textStyle = TextStyle(color = Black),
      keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
      keyboardActions =
          KeyboardActions(
              onSend = {
                if (textToSend.isNotBlank()) {
                  onSend(textToSend)
                  textToSend = ""
                }
              }),
      leadingIcon = {
        IconButton(
            modifier = Modifier.size(48.dp).padding(6.dp),
            onClick = { /*TODO add more message option as send photos*/}) {
              Icon(
                  Icons.Outlined.Add,
                  contentDescription = stringResource(R.string.contentDescription_icon_add),
                  tint = Blue)
            }
      },
      trailingIcon = {
        IconButton(
            modifier = Modifier.size(48.dp).padding(6.dp).testTag("chat_send_button"),
            onClick = {
              if (textToSend.isNotBlank()) {
                onSend(textToSend)
                textToSend = ""
              }
            }) {
              Icon(
                  Icons.Outlined.Send,
                  contentDescription = stringResource(R.string.contentDescription_icon_send),
                  tint = Blue)
            }
      },
      placeholder = { Text(stringResource(R.string.type_a_message)) })
}

@Composable
fun OptionsDialog(
    viewModel: MessageViewModel,
    selectedMessage: Message?,
    showOptionsDialog: MutableState<Boolean>,
    showEditDialog: MutableState<Boolean>,
    navigationActions: NavigationActions
) {
  if (showOptionsDialog.value) {
    AlertDialog(
        onDismissRequest = { showOptionsDialog.value = false },
        title = { Text(text = stringResource(R.string.options)) },
        text = {
          Column(modifier = Modifier.testTag("option_dialog")) {
            selectedMessage?.let { Text(text = it.getDate()) }
            if (viewModel.isUserMessageSender(selectedMessage!!)) {
              Spacer(modifier = Modifier.height(8.dp))
              Button(
                  modifier = Modifier.testTag("option_dialog_edit"),
                  onClick = {
                    showEditDialog.value = true
                    showOptionsDialog.value = false
                  }) {
                    Text(
                        text = stringResource(R.string.edit),
                        style = TextStyle(color = White),
                    )
                  }
              Spacer(modifier = Modifier.height(8.dp))
              Button(
                  modifier = Modifier.testTag("option_dialog_delete"),
                  onClick = {
                    viewModel.deleteMessage(selectedMessage)
                    showOptionsDialog.value = false
                  }) {
                    Text(
                        text = stringResource(R.string.delete),
                        style = TextStyle(color = White),
                    )
                  }
            } else {
              if (viewModel.chat.type == ChatType.GROUP || viewModel.chat.type == ChatType.TOPIC) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    modifier = Modifier.testTag("option_dialog_start_direct_message"),
                    onClick = {
                      showOptionsDialog.value = false
                      viewModel.startDirectMessage(selectedMessage.sender.uid)
                      navigationActions.navigateTo(Route.DIRECT_MESSAGE)
                    }) {
                      Text(
                          text = stringResource(R.string.start_direct_message),
                          style = TextStyle(color = White),
                      )
                    }
              }
            }
          }
        },
        confirmButton = {
          Button(
              modifier = Modifier.fillMaxWidth().testTag("option_dialog_cancel"),
              onClick = { showOptionsDialog.value = false }) {
                Text(text = stringResource(R.string.cancel), style = TextStyle(color = White))
              }
        })
  }
}

@Composable
fun EditDialog(
    viewModel: MessageViewModel,
    selectedMessage: Message?,
    showEditDialog: MutableState<Boolean>
) {
  if (showEditDialog.value) {
    AlertDialog(
        onDismissRequest = { showEditDialog.value = false },
        title = { Text(text = stringResource(R.string.edit)) },
        text = {
          Column(modifier = Modifier.testTag("edit_dialog")) {
            MessageTextFields(
                onSend = {
                  viewModel.editMessage(selectedMessage!!, it)
                  showEditDialog.value = false
                },
                defaultText = selectedMessage!!.text)
          }
        },
        confirmButton = {
          Button(
              modifier = Modifier.fillMaxWidth().testTag("edit_dialog_cancel"),
              onClick = { showEditDialog.value = false }) {
                Text(text = stringResource(R.string.cancel), style = TextStyle(color = White))
              }
        })
  }
}

@Composable
fun ChatGroupTitle(chat: Chat) {
  Image(
      painter = rememberAsyncImagePainter(chat.picture),
      contentDescription = stringResource(R.string.contentDescription_group_profile_picture),
      modifier = Modifier.size(40.dp).clip(CircleShape).testTag("group_title_profile_picture"),
      contentScale = ContentScale.Crop)

  Spacer(modifier = Modifier.width(8.dp))
  Column {
    Text(text = chat.name, maxLines = 1, modifier = Modifier.testTag("group_title_name"))
    Spacer(modifier = Modifier.width(8.dp))
    LazyRow(modifier = Modifier.testTag("group_title_members_row")) {
      items(chat.members) { member ->
        Text(
            text = member.username,
            modifier = Modifier.padding(end = 8.dp).testTag("group_title_member_name"),
            style = TextStyle(color = Gray),
            maxLines = 1)
      }
    }
  }
}

@Composable
fun PrivateChatTitle(chat: Chat) {
  Image(
      painter = rememberAsyncImagePainter(chat.picture),
      contentDescription = "User profile picture",
      modifier = Modifier.size(40.dp).clip(CircleShape).testTag("private_title_profile_picture"),
      contentScale = ContentScale.Crop)

  Spacer(modifier = Modifier.width(8.dp))
  Column { Text(text = chat.name, maxLines = 1, modifier = Modifier.testTag("private_title_name")) }
}
