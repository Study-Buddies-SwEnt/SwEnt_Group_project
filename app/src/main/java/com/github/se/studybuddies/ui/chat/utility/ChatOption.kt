package com.github.se.studybuddies.ui.chat.utility

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.ChatType
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.permissions.checkPermission
import com.github.se.studybuddies.permissions.imagePermissionVersion
import com.github.se.studybuddies.ui.chat.EditDialog
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.utils.SaveType
import com.github.se.studybuddies.utils.saveToStorage
import com.github.se.studybuddies.viewModels.DirectMessageViewModel
import com.github.se.studybuddies.viewModels.MessageViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun IconsOptionsList(viewModel: MessageViewModel, showIconsOptions: MutableState<Boolean>) {
  val showAddImage = remember { mutableStateOf(false) }
  val showAddLink = remember { mutableStateOf(false) }
  val showAddFile = remember { mutableStateOf(false) }
  val showAddPoll = remember { mutableStateOf(false) }

  PickPicture(showAddImage) { viewModel.sendPhotoMessage(it) }
  SendLinkMessage(viewModel, showAddLink)
  SendFileMessage(viewModel, showAddFile)
  SendPollMessage(viewModel, showAddPoll)

  val iconButtonOptions =
      listOf(
          IconButtonOptionData(
              testTag = "icon_send_image",
              onClickAction = {
                showIconsOptions.value = false
                showAddImage.value = true
              },
              painterResourceId = R.drawable.image_24px,
              contentDescription = stringResource(R.string.app_name)),
          IconButtonOptionData(
              testTag = "icon_send_link",
              onClickAction = {
                showIconsOptions.value = false
                showAddLink.value = true
              },
              painterResourceId = R.drawable.link_24px,
              contentDescription = stringResource(R.string.app_name)),
          IconButtonOptionData(
              testTag = "icon_send_file",
              onClickAction = {
                showIconsOptions.value = false
                showAddFile.value = true
              },
              painterResourceId = R.drawable.picture_as_pdf_24px,
              contentDescription = stringResource(R.string.app_name)),
          IconButtonOptionData(
              testTag = "icon_send_poll",
              onClickAction = {
                showIconsOptions.value = false
                showAddPoll.value = true
              },
              painterResourceId = R.drawable.how_to_vote_24px,
              contentDescription = stringResource(R.string.app_name)))

  ShowAlertDialog(
      modifier = Modifier.testTag("dialog_more_messages_types"),
      showDialog = showIconsOptions,
      onDismiss = { showIconsOptions.value = false },
      title = {},
      content = {
        LazyRow {
          items(iconButtonOptions) { option ->
            IconButtonOption(
                modifier = Modifier.testTag(option.testTag),
                onClickAction = option.onClickAction,
                painterResourceId = option.painterResourceId,
                contentDescription = option.contentDescription)
          }
        }
      },
      button = {})
}

data class IconButtonOptionData(
    val testTag: String,
    val onClickAction: () -> Unit,
    val painterResourceId: Int,
    val contentDescription: String
)

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
fun OptionsDialog(
    viewModel: MessageViewModel,
    selectedMessage: Message,
    showOptionsDialog: MutableState<Boolean>,
    navigationActions: NavigationActions,
) {
  val showEditDialog = remember { mutableStateOf(false) }
  EditDialog(viewModel, selectedMessage, showEditDialog)

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
            navigationActions = navigationActions)
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
      DownloadButton(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE, context) {
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
          style = TextStyle(color = Color.White),
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
                style = TextStyle(color = Color.White),
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
            style = TextStyle(color = Color.White),
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
            ?.let { DirectMessageViewModel(it.uid) }
            ?.startDirectMessage(selectedMessage.sender.uid)
        navigationActions.navigateTo(Route.DIRECT_MESSAGE)
      }) {
        Text(
            text = stringResource(R.string.start_direct_message),
            style = TextStyle(color = Color.White),
        )
      }
}
