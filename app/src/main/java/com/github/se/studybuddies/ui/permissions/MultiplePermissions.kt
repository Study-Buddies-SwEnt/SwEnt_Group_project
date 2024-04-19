package com.github.se.studybuddies.ui.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.content.ContextCompat

// Function to check and request permission.
fun checkPermission(
    context: Context,
    permission: String,
    launcher: ManagedActivityResultLauncher<String, Boolean>
) {
  if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
    launcher.launch(permission)
  }
}

fun checkMultiplePermissions(
    context: Context,
    permissions: List<String>,
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>
) {
  val permissionsToRequest =
      permissions
          .filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
          }
          .toTypedArray()
  if (permissionsToRequest.isNotEmpty()) {
    launcher.launch(permissionsToRequest)
  }
}
