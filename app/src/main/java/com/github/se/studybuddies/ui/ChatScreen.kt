package com.github.se.studybuddies.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.theme.background_blue
import com.github.se.studybuddies.ui.theme.blue
import com.github.se.studybuddies.viewModels.MessageViewModel

@Composable
fun ChatScreen(viewModel: MessageViewModel, navigationActions: NavigationActions) {
  val messages = viewModel.messages.collectAsState(initial = emptyList()).value
  var text_to_send by remember { mutableStateOf("") }

  // TODO issue when open keyboard, the list of messages goes up
  Column(modifier = Modifier.fillMaxSize().background(background_blue).navigationBarsPadding()) {
    SecondaryTopBar { navigationActions.navigateTo(Route.GROUPSHOME) }
    LazyColumn(Modifier.weight(1f).padding(8.dp)) {
      items(messages) { message ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(2.dp),
            horizontalArrangement =
                if (viewModel.isUserMessageSender(message)) {
                  Arrangement.End
                } else {
                  Arrangement.Start
                }) {
              if (viewModel.isUserMessageSender(message)) {
                OwnTextBubble(message)
              } else {
                OtherTextBubble(message)
              }
            }
      }
    }

    OutlinedTextField(
        value = text_to_send,
        onValueChange = { text_to_send = it },
        modifier =
            Modifier.padding(8.dp)
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
        keyboardActions =
            KeyboardActions(
                onSend = {
                  if (text_to_send.isNotBlank()) {
                    viewModel.sendMessage(text_to_send)
                    text_to_send = ""
                  }
                }),
        leadingIcon = {
          IconButton(
              modifier = Modifier.size(48.dp).padding(6.dp),
              onClick = { /*TODO add more message option as send photos*/}) {
                Icon(
                    Icons.Outlined.Add, // Replace with your icon painter
                    contentDescription = "Icon",
                    tint = blue)
              }
        },
        placeholder = { Text("Type a message") })
  }
}

@Composable
fun OwnTextBubble(message: Message) {
  BoxWithConstraints(
      modifier = Modifier.background(Color.White, RoundedCornerShape(20.dp)).padding(1.dp)) {
        Text(text = message.text, modifier = Modifier.padding(8.dp))
      }
}

@Composable
fun OtherTextBubble(message: Message) {
  Box(modifier = Modifier.background(Color.White, RoundedCornerShape(20.dp)).padding(1.dp)) {
    Column(modifier = Modifier.padding(8.dp)) {
      Text(
          text = message.sender.username,
          fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
      Text(text = message.text, modifier = Modifier.padding(0.dp))
    }
  }
}

@Preview
@Composable
fun ChatScreenPreview() {
  val groupUID = "groupUID_test_1"
  ChatScreen(MessageViewModel(groupUID), NavigationActions(rememberNavController()))
}
