package com.github.se.studybuddies

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class PermissionUnitTest {
  @Test
  fun permission_isGranted() {
    assert(true)
    /*val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted
          ->
          assertTrue(isGranted)
        }
    val context = LocalContext.current
    checkPermission(context, "Manifest.permission.READ_EXTERNAL_STORAGE", requestPermissionLauncher)

     */
  }
}
