package com.github.se.studybuddies.ui.chat.utility

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.MessageVal
import com.github.se.studybuddies.permissions.checkPermission
import com.github.se.studybuddies.permissions.imagePermissionVersion
import com.github.se.studybuddies.ui.shared_elements.SaveButton
import com.github.se.studybuddies.ui.shared_elements.SetPicture

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
fun PickFile(showAddFile: MutableState<Boolean>, onSave: (String, Uri) -> Unit) {
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
          onSave(fileName.value, fileState.value)
          showAddFile.value = false
          fileState.value = Uri.EMPTY
          fileName.value = ""
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

fun isValidUrl(url: String): Boolean {
  return try {
    val uri = Uri.parse(url)
    uri.scheme == "http" || uri.scheme == "https"
  } catch (e: Exception) {
    false
  }
}

@Composable
fun PickLink(showAddLink: MutableState<Boolean>, onSave: (String, Uri) -> Unit) {
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
          onSave(linkName.value, uri)
          showAddLink.value = false
          linkState.value = ""
          linkName.value = ""
        }
      })
}
