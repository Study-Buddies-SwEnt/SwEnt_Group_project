package com.github.se.studybuddies.ui.chat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
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
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.ChatType
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.data.MessageVal
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.permissions.checkPermission
import com.github.se.studybuddies.permissions.getStoragePermission
import com.github.se.studybuddies.permissions.imagePermissionVersion
import com.github.se.studybuddies.ui.shared_elements.ChatTopBar
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
import com.github.se.studybuddies.ui.shared_elements.SaveButton
import com.github.se.studybuddies.ui.shared_elements.SetPicture
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.DarkBlue
import com.github.se.studybuddies.ui.theme.LightBlue
import com.github.se.studybuddies.utils.SaveType
import com.github.se.studybuddies.utils.saveToStorage
import com.github.se.studybuddies.viewModels.DirectMessagesViewModel
import com.github.se.studybuddies.viewModels.MessageViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(
    viewModel: MessageViewModel,
    navigationActions: NavigationActions,
) {
  val messages = viewModel.messages.collectAsState(initial = emptyList()).value
  val showOptionsDialog = remember { mutableStateOf(false) }
  val showEditDialog = remember { mutableStateOf(false) }
  val showIconsOptions = remember { mutableStateOf(false) }
  val showAddImage = remember { mutableStateOf(false) }
  val showAddLink = remember { mutableStateOf(false) }
  val showAddFile = remember { mutableStateOf(false) }
  var showSearchBar by remember { mutableStateOf(false) }
  var searchText by remember { mutableStateOf("") }

  var selectedMessage by remember { mutableStateOf<Message?>(null) }
  val listState = rememberLazyListState()

  LaunchedEffect(messages) {
    if (messages.isNotEmpty()) {
      listState.scrollToItem(messages.lastIndex)
    }
  }

  selectedMessage?.let {
    OptionsDialog(viewModel, it, showOptionsDialog, showEditDialog, navigationActions)
  }

  IconsOptionsList(viewModel, showIconsOptions, showAddImage, showAddLink, showAddFile)

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
        /*
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
        */
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
                  MessageBubble(
                      message,
                      displayName,
                  )
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
  Row(
      modifier = Modifier.padding(8.dp).fillMaxWidth().testTag("message_type_filter"),
      horizontalArrangement = Arrangement.SpaceEvenly) {
        MessageFilterType.entries.forEach { type ->
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
  FILE(R.string.file_message_type, Message.FileMessage::class.java)
}

@Composable
fun MessageBubble(message: Message, displayName: Boolean = false) {
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
fun MessageTextFields(
    onSend: (String) -> Unit,
    defaultText: String = "",
    showIconsOptions: MutableState<Boolean>,
) {
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
            modifier = Modifier.size(48.dp).padding(6.dp).testTag("icon_more_messages_types"),
            onClick = {
              showIconsOptions.value = !showIconsOptions.value
              Log.d("MyPrint", "Icon clicked, showIconsOptions.value: ${showIconsOptions.value}")
            }) {
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
                  imageVector = Icons.AutoMirrored.Outlined.Send,
                  contentDescription = stringResource(R.string.contentDescription_icon_send),
                  tint = Blue)
            }
      },
      placeholder = { Text(stringResource(R.string.type_a_message)) })
}

@Composable
fun OptionsDialog(
    viewModel: MessageViewModel,
    selectedMessage: Message,
    showOptionsDialog: MutableState<Boolean>,
    showEditDialog: MutableState<Boolean>,
    navigationActions: NavigationActions,
) {

  ShowAlertDialog(
      showDialog = showOptionsDialog,
      onDismiss = { showOptionsDialog.value = false },
      title = { Text(text = stringResource(R.string.options)) },
      content = {
        OptionDialogContent(
            viewModel = viewModel,
            selectedMessage = selectedMessage,
            showOptionsDialog = showOptionsDialog,
            showEditDialog = showEditDialog,
            navigationActions = navigationActions,
        )
      },
      button = {})
}

@Composable
fun OptionDialogContent(
    viewModel: MessageViewModel,
    selectedMessage: Message,
    showOptionsDialog: MutableState<Boolean>,
    showEditDialog: MutableState<Boolean>,
    navigationActions: NavigationActions,
) {

  Column(modifier = Modifier.testTag("option_dialog")) {
    CommonOptions(selectedMessage, showOptionsDialog)
    if (viewModel.isUserMessageSender(selectedMessage)) {
      UserMessageOptions(
          viewModel = viewModel,
          selectedMessage = selectedMessage,
          showOptionsDialog = showOptionsDialog,
          showEditDialog = showEditDialog)
    } else if (viewModel.chat.type != ChatType.PRIVATE) {
      NonUserMessageOptions(
          viewModel = viewModel,
          selectedMessage = selectedMessage,
          showOptionsDialog = showOptionsDialog,
          navigationActions = navigationActions)
    }
  }
}

@Composable
fun CommonOptions(
    selectedMessage: Message,
    showOptionsDialog: MutableState<Boolean>,
) {
  val context = LocalContext.current
  Text(text = selectedMessage.getDate())
  when (selectedMessage) {
    is Message.PhotoMessage -> {
      DownloadButton(permission = imagePermissionVersion(), context) {
        val name = selectedMessage.uid
        CoroutineScope(Dispatchers.Main).launch {
          saveToStorage(context, selectedMessage.photoUri, name, SaveType.Photo())
        }
        showOptionsDialog.value = false
      }
    }
    is Message.FileMessage -> {
      DownloadButton(permission = getStoragePermission(), context) {
        val name = selectedMessage.fileName
        CoroutineScope(Dispatchers.Main).launch {
          saveToStorage(context, selectedMessage.fileUri, name, SaveType.PDF())
        }
        showOptionsDialog.value = false
      }
    }
    else -> {}
  }
}

@Composable
fun DownloadButton(permission: String, context: Context, onClick: () -> Unit) {
  var hasPermission by remember { mutableStateOf(false) }
  val requestPermissionLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        hasPermission = isGranted
      }
  LaunchedEffect(key1 = Unit) {
    checkPermission(context, permission, requestPermissionLauncher) { hasPermission = true }
  }
  if (hasPermission) {
    Button(modifier = Modifier.testTag("option_dialog_download"), onClick = { onClick() }) {
      Text(
          text = stringResource(R.string.download),
          style = TextStyle(color = White),
      )
    }
  }
}

@Composable
fun UserMessageOptions(
    viewModel: MessageViewModel,
    selectedMessage: Message,
    showOptionsDialog: MutableState<Boolean>,
    showEditDialog: MutableState<Boolean>,
) {
  Spacer(modifier = Modifier.height(8.dp))
  when (selectedMessage) {
    is Message.TextMessage /*, is Message.LinkMessage*/ -> {
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
    }
    else -> {}
  }
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
}

@Composable
fun NonUserMessageOptions(
    viewModel: MessageViewModel,
    selectedMessage: Message,
    showOptionsDialog: MutableState<Boolean>,
    navigationActions: NavigationActions,
) {
  Spacer(modifier = Modifier.height(8.dp))
  Button(
      modifier = Modifier.testTag("option_dialog_start_direct_message"),
      onClick = {
        showOptionsDialog.value = false
        viewModel.currentUser.value
            ?.let { DirectMessagesViewModel(it.uid) }
            ?.startDirectMessage(selectedMessage.sender.uid)
        // TODO()
        navigationActions.navigateTo(Route.DIRECT_MESSAGE)
      }) {
        Text(
            text = stringResource(R.string.start_direct_message),
            style = TextStyle(color = White),
        )
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
              painter = painterResource(R.drawable.video_call),
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
              painter = painterResource(R.drawable.video_call),
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

@Composable
fun IconsOptionsList(
    viewModel: MessageViewModel,
    showIconsOptions: MutableState<Boolean>,
    showAddImage: MutableState<Boolean>,
    showAddLink: MutableState<Boolean>,
    showAddFile: MutableState<Boolean>,
) {
  SendPhotoMessage(viewModel, showAddImage)
  SendLinkMessage(viewModel, showAddLink)
  SendFileMessage(viewModel, showAddFile)
  ShowAlertDialog(
      modifier = Modifier.testTag("dialog_more_messages_types"),
      showDialog = showIconsOptions,
      onDismiss = { showIconsOptions.value = false },
      title = {},
      content = {
        LazyRow {
          items(3) {
            when (it) {
              0 ->
                  IconButtonOption(
                      modifier = Modifier.testTag("icon_send_image"),
                      onClickAction = {
                        showIconsOptions.value = false
                        showAddImage.value = true
                      },
                      painterResourceId = R.drawable.image_24px,
                      contentDescription = stringResource(R.string.app_name))
              1 ->
                  IconButtonOption(
                      modifier = Modifier.testTag("icon_send_link"),
                      onClickAction = {
                        showIconsOptions.value = false
                        showAddLink.value = true
                      },
                      painterResourceId = R.drawable.link_24px,
                      contentDescription = stringResource(R.string.app_name))
              2 ->
                  IconButtonOption(
                      modifier = Modifier.testTag("icon_send_file"),
                      onClickAction = {
                        showIconsOptions.value = false
                        showAddFile.value = true
                      },
                      painterResourceId = R.drawable.picture_as_pdf_24px,
                      contentDescription = stringResource(R.string.app_name))
            }
          }
        }
      },
      button = {})
}

@Composable
fun IconButtonOption(
    onClickAction: () -> Unit,
    painterResourceId: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tint: Color = Blue,
) {
  IconButton(onClick = onClickAction, modifier = modifier.padding(8.dp)) {
    Icon(
        painter = painterResource(id = painterResourceId),
        contentDescription = contentDescription,
        tint = tint)
  }
}

@Composable
fun SendPhotoMessage(messageViewModel: MessageViewModel, showAddImage: MutableState<Boolean>) {
  val photoState = remember { mutableStateOf(Uri.EMPTY) }
  val imageInput = "image/*"
  val permission = imagePermissionVersion()

  val getContent = setupGetContentLauncherPhoto(photoState)

  val requestPermissionLauncher = setupRequestPermissionLauncher(getContent, imageInput)

  ShowAlertDialog(
      modifier = Modifier.testTag("add_image_dialog"),
      showDialog = showAddImage,
      onDismiss = { showAddImage.value = false },
      title = {},
      content = {
        ImagePickerBox(
            photoState = photoState,
            permission = permission,
            getContent = getContent,
            requestPermissionLauncher = requestPermissionLauncher)
      },
      button = {
        SaveButton(photoState.value.toString().isNotBlank()) {
          messageViewModel.sendPhotoMessage(photoState.value)
          showAddImage.value = false
          photoState.value = Uri.EMPTY
        }
      })
}

@Composable
fun setupGetContentLauncherPhoto(
    uriState: MutableState<Uri>,
): ManagedActivityResultLauncher<String, Uri?> {
  return rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
    uri?.let { uriState.value = it }
  }
}

@Composable
fun ImagePickerBox(
    photoState: MutableState<Uri>,
    permission: String,
    getContent: ManagedActivityResultLauncher<String, Uri?>,
    requestPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
) {
  val context = LocalContext.current
  Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.padding(8.dp).fillMaxWidth().testTag("add_image_box")) {
        SetPicture(photoState) {
          checkPermission(context, permission, requestPermissionLauncher) {
            getContent.launch("image/*")
          }
        }
      }
}

@Composable
fun SendLinkMessage(messageViewModel: MessageViewModel, showAddLink: MutableState<Boolean>) {
  val linkState = remember { mutableStateOf("") }
  val linkName = remember { mutableStateOf("") }

  ShowAlertDialog(
      modifier = Modifier.testTag("add_link_dialog"),
      showDialog = showAddLink,
      onDismiss = { showAddLink.value = false },
      title = {},
      content = {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(8.dp).fillMaxWidth().testTag("add_link_box")) {
              OutlinedTextField(
                  value = linkState.value,
                  onValueChange = { linkState.value = it },
                  modifier = Modifier.fillMaxWidth().testTag("add_link_text_field"),
                  textStyle = TextStyle(color = Black),
                  singleLine = true,
                  placeholder = { Text(stringResource(R.string.enter_link)) },
              )
            }
      },
      button = {
        SaveButton(
            linkState.value.isNotBlank(),
        ) {
          val uriString = linkState.value.trim()
          val uri =
              if (!isValidUrl(uriString)) Uri.parse("https://$uriString") else Uri.parse(uriString)
          linkName.value = uriString.substringAfter("//")
          messageViewModel.sendLinkMessage(linkName.value, uri)
          showAddLink.value = false
          linkState.value = ""
          linkName.value = ""
        }
      })
}

fun isValidUrl(url: String): Boolean {
  return try {
    val uri = Uri.parse(url)
    uri.scheme == "http" || uri.scheme == "https"
  } catch (e: Exception) {
    false
  }
}

@Composable
fun SendFileMessage(messageViewModel: MessageViewModel, showAddFile: MutableState<Boolean>) {
  val fileState = remember { mutableStateOf(Uri.EMPTY) }
  val fileName = remember { mutableStateOf("") }
  val context = LocalContext.current
  val fileInput = MessageVal.FILE_TYPE
  val permission = imagePermissionVersion()

  val getContent = setupGetContentFile(fileState, fileName, context)
  val requestPermissionLauncher = setupRequestPermissionLauncher(getContent, fileInput)

  ShowAlertDialog(
      modifier = Modifier.testTag("add_file_dialog"),
      showDialog = showAddFile,
      onDismiss = { showAddFile.value = false },
      title = {},
      content = {
        FilePickerBox(
            fileState = fileState,
            fileName = fileName,
            permission = permission,
            getContent = getContent,
            requestPermissionLauncher = requestPermissionLauncher)
      },
      button = {
        SaveButton(fileState.value.toString().isNotBlank()) {
          messageViewModel.sendFileMessage(fileName.value, fileState.value)
          showAddFile.value = false
          fileState.value = Uri.EMPTY
          fileName.value = ""
        }
      })
}

@Composable
fun setupGetContentFile(
    fileState: MutableState<Uri>,
    fileName: MutableState<String>,
    context: Context,
): ManagedActivityResultLauncher<String, Uri?> {
  return rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
    uri?.let { fileUri ->
      fileState.value = fileUri
      context.contentResolver.query(fileUri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst() && nameIndex != -1) {
          fileName.value = cursor.getString(nameIndex)
        }
      }
    }
  }
}

@Composable
fun setupRequestPermissionLauncher(
    getContent: ManagedActivityResultLauncher<String, Uri?>,
    fileInput: String,
): ManagedActivityResultLauncher<String, Boolean> {
  return rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted,
    ->
    if (isGranted) {
      getContent.launch(fileInput)
    }
  }
}

@Composable
fun FilePickerBox(
    fileState: MutableState<Uri>,
    fileName: MutableState<String>,
    permission: String,
    getContent: ManagedActivityResultLauncher<String, Uri?>,
    requestPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
) {
  val context = LocalContext.current
  Box(
      contentAlignment = Alignment.Center,
      modifier =
          Modifier.padding(8.dp)
              .fillMaxWidth()
              .clickable {
                checkPermission(context, permission, requestPermissionLauncher) {
                  getContent.launch(MessageVal.FILE_TYPE)
                }
              }
              .testTag("add_file_box")) {
        if (fileState.value == Uri.EMPTY) {
          Text(
              text = stringResource(R.string.select_a_file),
              modifier = Modifier.testTag("select_file"))
        } else {
          Text(text = fileName.value, modifier = Modifier.testTag("select_file"))
        }
      }
}

@Composable
fun ShowAlertDialog(
    modifier: Modifier = Modifier,
    showDialog: MutableState<Boolean>,
    onDismiss: () -> Unit,
    title: @Composable () -> Unit,
    content: @Composable () -> Unit,
    button: @Composable () -> Unit = {},
) {
  if (showDialog.value) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        text = content,
        title = title,
        confirmButton = button)
  }
}
