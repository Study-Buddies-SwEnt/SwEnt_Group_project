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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
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

fun isValidUrl(url: String): Boolean {
  return try {
    val uri = Uri.parse(url)
    uri.scheme == "http" || uri.scheme == "https"
  } catch (e: Exception) {
    false
  }
}
