package com.github.se.studybuddies.ui.permissions

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.se.studybuddies.ui.permissions.PermissionsCode.CAMERA_PERMISSION_CODE
import com.github.se.studybuddies.ui.permissions.PermissionsCode.LOCATION_PERMISSION_CODE
import com.github.se.studybuddies.ui.permissions.PermissionsCode.MICROPHONE_PERMISSION_CODE
import com.github.se.studybuddies.ui.permissions.PermissionsCode.STORAGE_PERMISSION_CODE

class MultiplePermissions {

  companion object {
    private const val CAMERA = Manifest.permission.CAMERA
    private const val MIC = Manifest.permission.RECORD_AUDIO
    private const val STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    private const val LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
  }

  // Function to check and request permission.
  @Composable
  private fun CheckPermission(permission: String, requestCode: Int) {
    if (ContextCompat.checkSelfPermission(LocalContext.current, permission) ==
        PackageManager.PERMISSION_DENIED) {
      // Requesting the permission
      ActivityCompat.requestPermissions(
          LocalContext.current as Activity, arrayOf(permission), requestCode)
    } else {
      Toast.makeText(
              LocalContext.current as Activity, "Permission already granted", Toast.LENGTH_SHORT)
          .show()
    }
  }

  // This function is called when the user accepts or decline the permission.
  // Request Code is used to check which permission called this function.
  // This request code is provided when the user is prompt for permission.
  // For now, accepting or declining permissions only shows a message on the screen.
  // ToDo: Implement the mutable state that keeps track of the accepted and declined permissions.
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
          Toast.makeText(context as Activity, "Camera Permission Granted", Toast.LENGTH_SHORT)
              .show()
        } else {
          Toast.makeText(context as Activity, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
        }
      }
      STORAGE_PERMISSION_CODE -> {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          Toast.makeText(context as Activity, "Storage Permission Granted", Toast.LENGTH_SHORT)
              .show()
        } else {
          Toast.makeText(context as Activity, "Storage Permission Denied", Toast.LENGTH_SHORT)
              .show()
        }
      }
      LOCATION_PERMISSION_CODE -> {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          Toast.makeText(context as Activity, "Location Permission Granted", Toast.LENGTH_SHORT)
              .show()
        } else {
          Toast.makeText(context as Activity, "Location Permission Denied", Toast.LENGTH_SHORT)
              .show()
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
}
