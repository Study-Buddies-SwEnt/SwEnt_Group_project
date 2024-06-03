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
import com.github.se.studybuddies.viewModels.DirectMessagesViewModel
import com.github.se.studybuddies.viewModels.MessageViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Displays a row of icon options for different message types, using AlertDialog to facilitate
 * selection.
 *
 * @param viewModel The ViewModel associated with message operations.
 * @param showIconsOptions State controlling visibility of the icons options list.
 */
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
/**
 * Data class representing properties of an IconButton in the UI.
 *
 * @param testTag Testing tag for the component.
 * @param onClickAction Action performed when the icon button is clicked.
 * @param painterResourceId Resource ID of the icon's image.
 * @param contentDescription Text description of the icon for accessibility.
 */
data class IconButtonOptionData(
    val testTag: String,
    val onClickAction: () -> Unit,
    val painterResourceId: Int,
    val contentDescription: String
)

/**
 * Composable that renders an IconButton with customizable options.
 *
 * @param onClickAction Action to perform on button click.
 * @param painterResourceId Resource ID for the icon image.
 * @param contentDescription Accessibility description of the icon.
 * @param modifier Modifier for styling and optional configuration.
 * @param tint Color tint for the icon.
 */
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

/**
 * Composable to display message options, allowing editing or deletion based on message type and
 * user permissions.
 *
 * @param viewModel The ViewModel for handling message operations.
 * @param selectedMessage The message selected by the user for options.
 * @param showOptionsDialog State controlling the visibility of the options dialog.
 * @param navigationActions Provides navigation actions throughout the application.
 */
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

/**
 * Composable to display content specific to the selected message's options dialog.
 *
 * @param viewModel The ViewModel for handling message operations.
 * @param selectedMessage The message selected by the user for options.
 * @param showOptionsDialog State controlling the visibility of the options dialog.
 * @param showEditDialog State for showing the edit message dialog.
 * @param navigationActions Provides navigation actions throughout the application.
 */
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

/**
 * Composable to display download options for messages that can be downloaded.
 *
 * @param selectedMessage The message selected by the user.
 * @param showOptionsDialog State controlling the visibility of the options dialog.
 */
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

/**
 * Composable to create a button for downloading content, ensuring permissions are checked and
 * requested as necessary.
 *
 * @param permission Permission required for the action.
 * @param context Android context, necessary for checking permissions.
 * @param onClick Action performed when the button is clicked.
 */
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

/**
 * Displays options for user own messages, allowing editing or deletion based on message type and
 * permissions.
 *
 * @param viewModel The ViewModel for handling message operations.
 * @param selectedMessage The message selected by the user.
 * @param showOptionsDialog State controlling the visibility of the options dialog.
 * @param showEditDialog State for showing the edit message dialog.
 */
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

/**
 * Composable to show options for messages from other users in non-private chat contexts, like
 * starting a direct message.
 *
 * @param viewModel The ViewModel for handling message operations.
 * @param selectedMessage The message being interacted with.
 * @param showOptionsDialog State controlling the visibility of the options dialog.
 * @param navigationActions Navigation actions for moving across different screens or contexts.
 */
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
        navigationActions.navigateTo(Route.DIRECT_MESSAGE)
      }) {
        Text(
            text = stringResource(R.string.start_direct_message),
            style = TextStyle(color = Color.White),
        )
      }
}
