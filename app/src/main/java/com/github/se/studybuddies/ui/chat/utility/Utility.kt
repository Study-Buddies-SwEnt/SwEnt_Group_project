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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.MessageVal
import com.github.se.studybuddies.permissions.checkPermission
import com.github.se.studybuddies.ui.shared_elements.SetPicture

/**
 * Composable function to display a customizable AlertDialog.
 *
 * @param modifier Modifier for styling and layout of the AlertDialog.
 * @param showDialog State controlling the visibility of the AlertDialog.
 * @param onDismiss Function to execute when the dialog is dismissed.
 * @param title Composable function that provides the title content of the AlertDialog.
 * @param content Composable function that provides the body content of the AlertDialog.
 * @param button Composable function that provides the button content of the AlertDialog.
 */
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

/**
 * Sets up a ManagedActivityResultLauncher for content picking, updating state with the selected
 * file's URI and name.
 *
 * @param fileState Mutable state for storing the file's URI.
 * @param fileName Mutable state for storing the file's name.
 * @param context Android context.
 * @return ManagedActivityResultLauncher for file content.
 */
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

/**
 * Sets up a ManagedActivityResultLauncher to request permissions and handle its result.
 *
 * @param getContent Launcher used to retrieve content if permission is granted.
 * @param fileInput The MIME type of files to retrieve.
 * @return ManagedActivityResultLauncher for permission requests.
 */
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

/**
 * Composable that displays a clickable box for file selection, requesting permission if needed.
 *
 * @param fileState Mutable state holding the selected file's URI.
 * @param fileName Mutable state holding the selected file's name.
 * @param permission The required permission to access files.
 * @param getContent Launcher to pick a file.
 * @param requestPermissionLauncher Launcher to request permissions.
 */
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

/**
 * Sets up a ManagedActivityResultLauncher for picking photo content.
 *
 * @param uriState Mutable state for storing the photo's URI.
 * @return ManagedActivityResultLauncher for photo content.
 */
@Composable
fun setupGetContentLauncherPhoto(
    uriState: MutableState<Uri>,
): ManagedActivityResultLauncher<String, Uri?> {
  return rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
    uri?.let { uriState.value = it }
  }
}

/**
 * Composable that displays a clickable box for photo selection, requesting permission if needed.
 *
 * @param photoState Mutable state holding the selected photo's URI.
 * @param permission The required permission to access photos.
 * @param getContent Launcher to pick a photo.
 * @param requestPermissionLauncher Launcher to request permissions.
 */
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

/**
 * Validates if the provided URL string is a well-formed URL with HTTP or HTTPS scheme.
 *
 * @param url The URL string to validate.
 * @return True if the URL is valid, otherwise false.
 */
fun isValidUrl(url: String): Boolean {
  return try {
    val uri = Uri.parse(url)
    uri.scheme == "http" || uri.scheme == "https"
  } catch (e: Exception) {
    false
  }
}
