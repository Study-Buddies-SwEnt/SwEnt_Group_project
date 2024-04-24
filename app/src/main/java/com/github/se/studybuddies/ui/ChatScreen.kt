package com.github.se.studybuddies.ui

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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.LightBlue
import com.github.se.studybuddies.viewModels.MessageViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(viewModel: MessageViewModel, navigationActions: NavigationActions) {
  val messages = viewModel.messages.collectAsState(initial = emptyList()).value
  var textToSend by remember { mutableStateOf("") }
  var showOptionsDialog by remember { mutableStateOf(false) }
  var selectedMessage by remember { mutableStateOf<Message?>(null) }

  if (showOptionsDialog) {
    AlertDialog(
        modifier = Modifier.testTag("alert_dialog"),
        onDismissRequest = { showOptionsDialog = false },
        title = { Text(text = stringResource(R.string.options)) },
        text = {
          Column {
            selectedMessage?.let { Text(text = it.getDate()) }
              Spacer(modifier = Modifier.height(8.dp))
            Button(
                modifier = Modifier.testTag("alert_dialog_delete"),
                onClick = {
                  viewModel.deleteMessage(selectedMessage!!)
                  showOptionsDialog = false
                }) {
                  Text(text = stringResource(R.string.delete))
                }
          }
        },
        confirmButton = {
          Button(modifier = Modifier.fillMaxWidth().testTag("alert_dialog_cancel"), onClick = { showOptionsDialog = false }) { Text(text = stringResource(R.string.cancel)) }
        })
  }

  // TODO issue when open keyboard, the list of messages goes up
  Column(
      modifier =
      Modifier
          .fillMaxSize()
          .background(LightBlue)
          .navigationBarsPadding()
          .testTag("chat_screen")) {
        SecondaryTopBar { navigationActions.goBack() }
        LazyColumn(
            Modifier
                .weight(1f)
                .padding(8.dp)) {
          items(messages) { message ->
            Row(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            selectedMessage = message
                            showOptionsDialog = true
                        }).testTag("chat_message_row"),
                horizontalArrangement =
                    if (viewModel.isUserMessageSender(message)) {
                      Arrangement.End
                    } else {
                      Arrangement.Start
                    }) {
                  TextBubble(message, !viewModel.isUserMessageSender(message))
                }
          }
        }

        OutlinedTextField(
            value = textToSend,
            onValueChange = { textToSend = it },
            modifier =
            Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(20.dp))
                .testTag("chat_text_field"),
            shape = RoundedCornerShape(20.dp),
            textStyle = TextStyle(color = Black),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
            keyboardActions =
                KeyboardActions(
                    onSend = {
                      if (textToSend.isNotBlank()) {
                        viewModel.sendMessage(textToSend)
                        textToSend = ""
                      }
                    }),
            leadingIcon = {
              IconButton(
                  modifier = Modifier
                      .size(48.dp)
                      .padding(6.dp),
                  onClick = { /*TODO add more message option as send photos*/}) {
                    Icon(Icons.Outlined.Add, contentDescription = "Icon", tint = Blue)
                  }
            },
            trailingIcon = {
              IconButton(
                  modifier = Modifier
                      .size(48.dp)
                      .padding(6.dp)
                      .testTag("chat_send_button"),
                  onClick = {
                    if (textToSend.isNotBlank()) {
                      viewModel.sendMessage(textToSend)
                      textToSend = ""
                    }
                  }) {
                    Icon(Icons.Outlined.Send, contentDescription = "Icon", tint = Blue)
                  }
            },
            placeholder = { Text(stringResource(R.string.type_a_message)) })
      }
}

@Composable
fun TextBubble(message: Message, displayName: Boolean = false) {
  Row(modifier = Modifier.padding(1.dp)) {
    if (displayName) {
      // add user profile picture
      Image(
          painter = rememberImagePainter(message.sender.photoUrl.toString()),
          contentDescription = "User profile picture",
          modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .border(2.dp, Gray, CircleShape)
              .align(Alignment.CenterVertically).testTag("chat_user_profile_picture"),
          contentScale = ContentScale.Crop)

      Spacer(modifier = Modifier.width(8.dp))
    }

    Box(
        modifier =
        Modifier
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(1.dp)
            .testTag("chat_text_bubble")) {
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

@Preview
@Composable
fun ChatScreenPreview() {
  val groupUID = "groupUID_test_1"
  ChatScreen(MessageViewModel(groupUID), NavigationActions(rememberNavController()))
}
