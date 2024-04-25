package com.github.se.studybuddies

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.content.ContextCompat
import com.github.se.studybuddies.ui.permissions.checkMultiplePermissions
import com.github.se.studybuddies.ui.permissions.checkPermission
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(MockitoJUnitRunner::class)
class PermissionsTest {

  @RelaxedMockK private lateinit var mockContext: Context
  private lateinit var mockLauncher: ManagedActivityResultLauncher<String, Boolean>
  private lateinit var mockLauncherMultiple:
      ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>

  fun granted(): Unit {
    assert(true)
  }

  @Test
  fun permissionsRequestLaunched() {
    val permission = "android.permission.CALENDAR"
    mockLauncher =
        mockk<ManagedActivityResultLauncher<String, Boolean>>(relaxed = true) { launch(permission) }
    mockContext = mockk<Context>(relaxed = true) {}

    checkPermission(mockContext, permission, mockLauncher) { granted() }
    verify { mockLauncher.launch(permission) }
  }

  @Test
  fun permissionsRequestNotLaunched() {
    val permission = "android.permission.CALENDAR"
    mockLauncher =
        mockk<ManagedActivityResultLauncher<String, Boolean>>(relaxed = true) { launch(permission) }
    mockContext = mockk<Context>(relaxed = true) {}

    checkPermission(mockContext, permission, mockLauncher) { granted() }
    assert(
        ContextCompat.checkSelfPermission(mockContext, permission) ==
            PackageManager.PERMISSION_GRANTED)
    checkPermission(mockContext, permission, mockLauncher) { granted() }
    verify(exactly = 1) { mockLauncher.launch(permission) }
  }

  @Test
  fun multiplePermissionsRequestLaunched() {
    mockContext = mockk<Context>(relaxed = true) {}
    val permissions =
        listOf(
            "android.permission.CALENDAR",
            "android.permission.CAMERA",
            "android.permission.READ_CONTACTS")
    val permissionsToRequest =
        permissions
            .filter {
              ContextCompat.checkSelfPermission(mockContext, it) !=
                  PackageManager.PERMISSION_GRANTED
            }
            .toTypedArray()
    mockLauncherMultiple =
        mockk<ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>>(relaxed = true) {
          launch(permissionsToRequest)
        }

    checkMultiplePermissions(mockContext, permissions, mockLauncherMultiple) { granted() }
    verify { mockLauncherMultiple.launch(permissionsToRequest) }
  }
}
