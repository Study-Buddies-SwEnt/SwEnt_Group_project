package com.github.se.studybuddies

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.content.ContextCompat
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

  @Test
  fun permissionsRequestLaunched() {
    val permission = "android.permission.CALENDAR"
    mockLauncher =
        mockk<ManagedActivityResultLauncher<String, Boolean>>(relaxed = true) { launch(permission) }
    mockContext = mockk<Context>(relaxed = true) {}

    checkPermission(mockContext, permission, mockLauncher)
    verify { mockLauncher.launch(permission) }
    // tearDown()
  }

  @Test
  fun permissionsRequestNotLaunched() {
    val permission = "android.permission.CALENDAR"
    mockLauncher =
        mockk<ManagedActivityResultLauncher<String, Boolean>>(relaxed = true) { launch(permission) }
    mockContext = mockk<Context>(relaxed = true) {}

    checkPermission(mockContext, permission, mockLauncher)
    assert(
        ContextCompat.checkSelfPermission(mockContext, permission) ==
            PackageManager.PERMISSION_GRANTED)
    checkPermission(mockContext, permission, mockLauncher)
    verify(exactly = 1) { mockLauncher.launch(permission) }

  }
}
