package com.github.se.studybuddies.ui.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import coil.compose.rememberAsyncImagePainter
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.ChatType
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.data.MessageVal
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.chat.utility.IconsOptionsList
import com.github.se.studybuddies.ui.chat.utility.MessageTextFields
import com.github.se.studybuddies.ui.chat.utility.OptionsDialog
import com.github.se.studybuddies.ui.chat.utility.ShowAlertDialog
import com.github.se.studybuddies.ui.shared_elements.ChatTopBar
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.DarkBlue
import com.github.se.studybuddies.ui.theme.LightBlue
import com.github.se.studybuddies.viewModels.MessageViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(
    viewModel: MessageViewModel,
    navigationActions: NavigationActions,
) {
  val messages by viewModel.messages.collectAsState(initial = emptyList())
  val showOptionsDialog = remember { mutableStateOf(false) }
  val showIconsOptions = remember { mutableStateOf(false) }

  var selectedMessage by remember { mutableStateOf<Message?>(null) }
  val listState = rememberLazyListState()

  LaunchedEffect(messages) {
    if (messages.isNotEmpty()) {
      listState.scrollToItem(messages.lastIndex)
    }
  }

  selectedMessage?.let { OptionsDialog(viewModel, it, showOptionsDialog, navigationActions) }

  IconsOptionsList(viewModel, showIconsOptions)

  Column(
      modifier =
          Modifier.fillMaxSize()
              .background(LightBlue)
              .navigationBarsPadding()
              .testTag("chat_screen")) {
        when (viewModel.chat.type) {
          ChatType.GROUP,
          ChatType.TOPIC, -> GroupChatTopBar(viewModel.chat, navigationActions)
          ChatType.PRIVATE -> PrivateChatTopBar(viewModel.chat, navigationActions)
        }
        LazyColumn(state = listState, modifier = Modifier.weight(1f).padding(8.dp)) {
          items(messages) { message ->
            val isCurrentUserMessageSender = viewModel.isUserMessageSender(message)
            val displayName = viewModel.chat.type != ChatType.PRIVATE && !isCurrentUserMessageSender
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
                    if (isCurrentUserMessageSender) {
                      Arrangement.End
                    } else {
                      Arrangement.Start
                    }) {
                  MessageBubble(message, displayName, viewModel)
                }
          }
        }
        MessageTextFields(
            onSend = { viewModel.sendTextMessage(it) }, showIconsOptions = showIconsOptions)
      }
}

@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onClearSearch: () -> Unit
) {
  Column {
    TextField(
        value = searchText,
        onValueChange = onSearchTextChanged,
        modifier = Modifier.fillMaxWidth().testTag("search_bar_text_field"),
        placeholder = { Text(stringResource(R.string.search)) },
        singleLine = true,
        colors =
            TextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
            ),
        trailingIcon = {
          IconButton(onClick = onClearSearch) {
            if (searchText.isNotEmpty())
                Icon(
                    Icons.Default.Clear,
                    contentDescription = stringResource(R.string.content_description_clear_search))
          }
        },
    )
  }
}

@Composable
fun MessageTypeFilter(viewModel: MessageViewModel) {
  val filterType = viewModel.filterType.collectAsState().value
  LazyRow(
      modifier = Modifier.padding(8.dp).fillMaxWidth().testTag("message_type_filter"),
      horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(MessageFilterType.entries) { type ->
          val backgroundColor = if (filterType == type.messageType) DarkBlue else Blue
          Button(
              modifier = Modifier.testTag("message_type_filter_button"),
              onClick = { viewModel.setFilterType(type.messageType) },
              colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
          ) {
            Text(stringResource(type.displayNameRes), style = TextStyle(color = White))
          }
        }
      }
}

enum class MessageFilterType(
    @StringRes val displayNameRes: Int,
    val messageType: Class<out Message>?
) {
  ALL(R.string.all_message_type, null),
  TEXT(R.string.test_message_type, Message.TextMessage::class.java),
  PHOTO(R.string.photo_message_type, Message.PhotoMessage::class.java),
  LINK(R.string.link_message_type, Message.LinkMessage::class.java),
  FILE(R.string.file_message_type, Message.FileMessage::class.java),
  POLL(R.string.poll_message_type, Message.PollMessage::class.java)
}

@Composable
fun MessageBubble(message: Message, displayName: Boolean = false, viewModel: MessageViewModel) {
  val browserLauncher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.StartActivityForResult()) {}

  Row(modifier = Modifier.padding(1.dp).testTag("chat_text_bubble")) {
    if (displayName) {
      Image(
          painter = rememberAsyncImagePainter(message.sender.photoUrl.toString()),
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
            when (message) {
              is Message.TextMessage -> {
                Text(
                    text = message.text,
                    style = TextStyle(color = Black),
                    modifier = Modifier.testTag("chat_message_text"))
              }
              is Message.PhotoMessage -> {
                Image(
                    painter = rememberAsyncImagePainter(message.photoUri.toString()),
                    contentDescription = stringResource(R.string.contentDescription_photo),
                    modifier =
                        Modifier.size(200.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .testTag("chat_message_image"),
                    contentScale = ContentScale.Crop)
              }
              is Message.LinkMessage -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                      painter = painterResource(id = R.drawable.link_24px),
                      contentDescription = stringResource(R.string.app_name),
                      tint = Blue)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                      text = message.linkName,
                      style = TextStyle(color = Blue),
                      modifier =
                          Modifier.clickable {
                                val intent = Intent(Intent.ACTION_VIEW, message.linkUri)
                                browserLauncher.launch(intent)
                              }
                              .testTag("chat_message_link"))
                }
              }
              is Message.FileMessage -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                      painter = painterResource(id = R.drawable.picture_as_pdf_24px),
                      contentDescription = stringResource(R.string.app_name),
                      tint = Blue)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                      text = message.fileName,
                      style = TextStyle(color = Blue),
                      modifier =
                          Modifier.clickable {
                                val intent =
                                    Intent().apply {
                                      action = Intent.ACTION_VIEW
                                      setDataAndType(message.fileUri, MessageVal.FILE_TYPE)
                                      flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    }
                                browserLauncher.launch(
                                    Intent.createChooser(
                                        intent,
                                        "Open with") // I tried to extract the string resource but
                                    // it didn't work
                                    )
                              }
                              .testTag("chat_message_file"))
                }
              }
              is Message.PollMessage -> {
                Column {
                  Text(
                      text = message.question,
                      style = TextStyle(color = Black),
                      modifier = Modifier.testTag("chat_message_poll_question"))
                  message.options.forEach { option ->
                    val isSelected =
                        message.votes[option]?.any { it.uid == viewModel.currentUser.value?.uid } ==
                            true
                    val voteNumber = message.votes[option]?.size ?: 0
                    PollButton(
                        text = option,
                        isSelected = isSelected,
                        voteNumber = voteNumber,
                        singleChoice = message.singleChoice) {
                          viewModel.votePollMessage(message, option)
                        }
                  }
                }
              }
            }
            Text(
                text = message.getTime(),
                style = TextStyle(color = Gray),
                modifier = Modifier.testTag("chat_message_time"))
          }
        }
  }
}

@Composable
fun PollButton(
    text: String,
    isSelected: Boolean,
    voteNumber: Int,
    singleChoice: Boolean,
    onItemSelected: (String) -> Unit
) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceEvenly,
      modifier = Modifier.clickable { onItemSelected(text) }) {
        if (singleChoice) {
          RadioButton(selected = isSelected, onClick = { onItemSelected(text) })
        } else {
          Checkbox(checked = isSelected, onCheckedChange = { onItemSelected(text) })
        }
        Text(
            text = text,
            style = TextStyle(color = Black),
            modifier = Modifier.testTag("chat_message_poll_option"))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = voteNumber.toString(), style = TextStyle(color = Gray))
      }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun EditDialog(
    viewModel: MessageViewModel,
    selectedMessage: Message,
    showEditDialog: MutableState<Boolean>,
) {

  val selectedMessageText =
      when (selectedMessage) {
        is Message.TextMessage -> selectedMessage.text
        is Message.LinkMessage -> selectedMessage.linkUri.toString()
        else -> ""
      }
  ShowAlertDialog(
      showDialog = showEditDialog,
      onDismiss = { showEditDialog.value = false },
      title = { Text(text = stringResource(R.string.edit)) },
      content = {
        Column(modifier = Modifier.testTag("edit_dialog")) {
          MessageTextFields(
              onSend = {
                viewModel.editMessage(selectedMessage, it)
                showEditDialog.value = false
              },
              defaultText = selectedMessageText,
              showIconsOptions = mutableStateOf(false))
        }
      },
      button = {})
}

@Composable
fun GroupChatTopBar(chat: Chat, navigationActions: NavigationActions) {
  ChatTopBar(
      leftButton = { GoBackRouteButton(navigationActions = navigationActions, Route.GROUPSHOME) },
      rightButton = {
        IconButton(onClick = { navigationActions.navigateTo(Route.PLACEHOLDER) }) {
          Icon(
              modifier = Modifier.size(20.dp),
              painter = painterResource(R.drawable.active_call),
              contentDescription = "",
              tint = Blue)
        }
      }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.85F).fillMaxHeight().padding(4.dp)) {
              Image(
                  painter = rememberAsyncImagePainter(chat.picture),
                  contentDescription =
                      stringResource(R.string.contentDescription_group_profile_picture),
                  modifier =
                      Modifier.size(40.dp).clip(CircleShape).testTag("group_title_profile_picture"),
                  contentScale = ContentScale.Crop)

              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                    text = chat.name, maxLines = 1, modifier = Modifier.testTag("group_title_name"))
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
      }
}



@Composable
fun PrivateChatTopBar(chat: Chat, navigationActions: NavigationActions) {

  ChatTopBar(
      leftButton = {
        GoBackRouteButton(navigationActions = navigationActions, Route.DIRECT_MESSAGE)
      },
      rightButton = {
        IconButton(onClick = { navigationActions.navigateTo(Route.PLACEHOLDER) }) {
          Icon(
              modifier = Modifier.size(20.dp),
              painter = painterResource(R.drawable.active_call),
              contentDescription = "",
              tint = Blue)
        }
      }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier.fillMaxWidth(0.85F).fillMaxHeight().padding(4.dp).clickable {
                  navigationActions.navigateTo("${Route.CONTACT_SETTINGS}/${chat.uid}")
                }) {
              Image(
                  painter = rememberAsyncImagePainter(chat.picture),
                  contentDescription = "User profile picture",
                  modifier =
                      Modifier.size(40.dp)
                          .clip(CircleShape)
                          .testTag("private_title_profile_picture"),
                  contentScale = ContentScale.Crop)
              Spacer(modifier = Modifier.width(8.dp))
              Column() {
                Text(
                    text = chat.name,
                    maxLines = 1,
                    modifier = Modifier.testTag("private_title_name"))
              }
            }
      }
}

//TODO reimplement this correctly
@Composable
fun SearchButton(viewModel: MessageViewModel){
    var showSearchBar by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    Spacer(Modifier)
    Icon(
        Icons.Default.Search,
        contentDescription = "Search",
        modifier =
        Modifier.clickable { showSearchBar = !showSearchBar }.testTag("search_button"))
    if (showSearchBar) {
        Column {
            SearchBar(
                searchText,
                onSearchTextChanged = {
                    searchText = it
                    viewModel.setSearchQuery(it)
                    Log.d("MyPrint", "Search query: $it")
                },
                onClearSearch = {
                    searchText = ""
                    viewModel.setSearchQuery("")
                })
            MessageTypeFilter(viewModel = viewModel)
        }
    }
}
