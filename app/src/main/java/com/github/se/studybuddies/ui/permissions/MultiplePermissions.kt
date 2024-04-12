package com.github.se.studybuddies.ui.permissions

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.github.se.studybuddies.ui.permissions.PermissionsCode.CAMERA_PERMISSION_CODE
import com.github.se.studybuddies.ui.permissions.PermissionsCode.LOCATION_PERMISSION_CODE
import com.github.se.studybuddies.ui.permissions.PermissionsCode.MICROPHONE_PERMISSION_CODE
import com.github.se.studybuddies.ui.permissions.PermissionsCode.STORAGE_PERMISSION_CODE
import com.google.accompanist.permissions.ExperimentalPermissionsApi

/*
fun MultiplePermissions(){

private val _permissions = MutableList<Int>(4) { 0 } // 0: Camera, 1: Location, 2: Storage, 3: Microphone
val permissions: List<Int> = _permissions
*/

// Function to check and request permission.
@OptIn(ExperimentalPermissionsApi::class)
fun checkPermission(
    context: Context,
    permission: String,
    launcher: ManagedActivityResultLauncher<String, Boolean>
) {
  launcher.launch(permission)
}

// This function is called when the user accepts or decline the permission.
// Request Code is used to check which permission called this function.
// This request code is provided when the user is prompt for permission.
// For now, accepting or declining permissions only shows a message on the screen.

@Composable
fun OnRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
) {
  val context = LocalContext.current
  when (requestCode) {
    CAMERA_PERMISSION_CODE -> {
      if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(context as Activity, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
      } else {
        Toast.makeText(context as Activity, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
      }
    }
    STORAGE_PERMISSION_CODE -> {
      if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(context as Activity, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
      } else {
        Toast.makeText(context as Activity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
      }
    }
    LOCATION_PERMISSION_CODE -> {
      if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(context as Activity, "Location Permission Granted", Toast.LENGTH_SHORT)
            .show()
      } else {
        Toast.makeText(context as Activity, "Location Permission Denied", Toast.LENGTH_SHORT).show()
      }
    }
    MICROPHONE_PERMISSION_CODE -> {
      if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(context as Activity, "Microphone Permission Granted", Toast.LENGTH_SHORT)
            .show()
      } else {
        Toast.makeText(context as Activity, "Microphone Permission Denied", Toast.LENGTH_SHORT)
            .show()
      }
    }
  }
}
