package com.github.se.studybuddies

import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import com.github.se.studybuddies.ui.permissions.checkPermission
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(MockitoJUnitRunner::class)
class PermissionsUnitTest {

  @Mock private lateinit var mockContext: Context
  private lateinit var mockLauncher: ManagedActivityResultLauncher<String, Boolean>

  @Test
  fun permissionsRequestLaunched() {
    val permission = "android.permission.READ_MEDIA_IMAGES"
    mockLauncher =
        mockk<ManagedActivityResultLauncher<String, Boolean>>(relaxed = true) { launch(permission) }
    /*var requestGranted = false
    val requestPermissionLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
          requestGranted = true
        }
      }
     */

    mockContext = mockk<Context>(relaxed = true) {}

    checkPermission(mockContext, permission, mockLauncher)
    verify { mockLauncher.launch(permission) }
  }
}
