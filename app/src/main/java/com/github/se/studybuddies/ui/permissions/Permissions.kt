package com.github.se.studybuddies.ui.permissions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.content.ContextCompat

fun imagePermissionVersion(): String {
  return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
    // For older Android versions, use READ_EXTERNAL_STORAGE permission
    "android.permission.READ_EXTERNAL_STORAGE"
  } else {
    "android.permission.READ_MEDIA_IMAGES"
  }
}

// Function to check and request a single permission.
fun checkPermission(
    context: Context,
    permission: String,
    launcher: ManagedActivityResultLauncher<String, Boolean>,
    alreadyGranted: () -> Unit
) {
    if (ContextCompat.checkSelfPermission(
            context,
            permission
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        launcher.launch(permission)
    } else {
        alreadyGranted()
    }
}
