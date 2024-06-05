package com.github.se.studybuddies.ui.chat.utility

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.MessageVal
import com.github.se.studybuddies.permissions.imagePermissionVersion
import com.github.se.studybuddies.ui.shared_elements.SaveButton
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.MessageViewModel

/**
 * Composable function to create a text input field with additional functionalities for sending
 * messages.
 *
 * @param onSend Function to call when the message is sent.
 * @param defaultText Initial text to display in the text field.
 * @param showIconsOptions Mutable state to control the visibility of icon options.
 */
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
              .background(Color.White, RoundedCornerShape(20.dp))
              .testTag("chat_text_field"),
      shape = RoundedCornerShape(20.dp),
      textStyle = TextStyle(color = Color.Black),
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

/**
 * Composable function to send a poll message. It includes input fields for the question and
 * options.
 *
 * @param messageViewModel ViewModel that handles sending messages.
 * @param showAddPoll Mutable state to control the visibility of the poll creation interface.
 */
@Composable
fun SendPollMessage(messageViewModel: MessageViewModel, showAddPoll: MutableState<Boolean>) {
  val question = remember { mutableStateOf("") }
  val options = remember { mutableStateOf(listOf("")) }
  val singleChoice = remember { mutableStateOf(true) }

  ShowAlertDialog(
      modifier = Modifier.testTag("add_poll_dialog"),
      showDialog = showAddPoll,
      onDismiss = { showAddPoll.value = false },
      title = {},
      content = {
        Column {
          OutlinedTextField(
              label = { Text(stringResource(R.string.poll_question)) },
              value = question.value,
              onValueChange = { question.value = it },
              modifier = Modifier.fillMaxWidth().testTag("add_poll_question_text_field"),
              textStyle = TextStyle(color = Color.Black),
              singleLine = true,
              placeholder = { Text(stringResource(R.string.enter_poll_question)) },
          )
          Spacer(modifier = Modifier.height(16.dp))

          Text(text = stringResource(R.string.poll_options))

          LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)) {
            items(options.value) { option ->
              OutlinedTextField(
                  value = option,
                  onValueChange = {
                    options.value =
                        options.value.toMutableList().apply {
                          set(indexOf(option), it)
                          removeAll { option -> option.isBlank() }
                          if (lastOrNull()?.isNotBlank() == true) add("")
                        }
                  },
                  modifier = Modifier.fillMaxWidth().testTag("add_poll_options_text_field"),
                  textStyle = TextStyle(color = Color.Black),
                  singleLine = true,
                  placeholder = { Text(stringResource(R.string.enter_poll_options)) },
              )
              Spacer(modifier = Modifier.height(8.dp))
            }
          }
          Row(
              modifier =
                  Modifier.fillMaxWidth().padding(8.dp).testTag("add_poll_single_choice_row"),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = stringResource(R.string.single_choice),
                    style = TextStyle(color = Color.Black),
                    modifier = Modifier.testTag("add_poll_single_choice_text"))
                Switch(
                    checked = singleChoice.value,
                    onCheckedChange = { singleChoice.value = it },
                    colors =
                        SwitchDefaults.colors(
                            checkedThumbColor = White,
                            checkedTrackColor = Blue,
                            uncheckedTrackColor = Color.LightGray))
              }
        }
      },
      button = {
        val nonEmptyOptions = options.value.filter { it.isNotBlank() }
        SaveButton(question.value.isNotBlank() && nonEmptyOptions.size >= 2) {
          messageViewModel.sendPollMessage(
              question.value, singleChoice.value, nonEmptyOptions.toList())
          showAddPoll.value = false
          question.value = ""
          options.value = listOf("")
          singleChoice.value = true
        }
      })
}

/**
 * Composable function to send a file message. It facilitates file selection and sends the message
 * through the ViewModel.
 *
 * @param messageViewModel ViewModel that handles sending messages.
 * @param showAddFile Mutable state to control the dialog for adding a file.
 */
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

/**
 * Composable function to send a link message. Allows the user to enter a URL and send it as a
 * message.
 *
 * @param messageViewModel ViewModel that handles sending messages.
 * @param showAddLink Mutable state to control the dialog for adding a link.
 */
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
                  textStyle = TextStyle(color = Color.Black),
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

/**
 * Composable function to send a photo message. Facilitates selecting a photo and sending it through
 * the ViewModel.
 *
 * @param messageViewModel ViewModel that handles sending messages.
 * @param showAddImage Mutable state to control the dialog for adding an image.
 */
@Composable
fun PickPicture(showAddImage: MutableState<Boolean>, onSave: (Uri) -> Unit) {
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
          onSave(photoState.value)
          showAddImage.value = false
          photoState.value = Uri.EMPTY
        }
      })
}
