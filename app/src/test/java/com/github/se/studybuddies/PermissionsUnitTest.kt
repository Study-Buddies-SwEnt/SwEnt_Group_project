package com.github.se.studybuddies

import android.content.Context
import android.content.pm.PackageManager
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiSelector
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.test.platform.app.InstrumentationRegistry
import com.github.se.studybuddies.ui.permissions.checkPermission
import io.mockk.clearMocks
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(MockitoJUnitRunner::class)
class PermissionsUnitTest {

  @RelaxedMockK private lateinit var mockContext: Context
  private lateinit var mockLauncher: ManagedActivityResultLauncher<String, Boolean>
  private var device : UiDevice? = null

  @Before
  fun setUp() {
    this.device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
  }
/*
  @Test
  fun permissionsRequestLaunched() {
    val permission = "android.permission.READ_MEDIA_IMAGES"
    mockLauncher =
        mockk<ManagedActivityResultLauncher<String, Boolean>> { launch(permission) }
    mockContext = mockk<Context> {}
    assert(
        ContextCompat.checkSelfPermission(mockContext, permission) ==
            PackageManager.PERMISSION_DENIED)

    checkPermission(mockContext, permission, mockLauncher)
    verify { mockLauncher.launch(permission) }
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

 */

  @Test
  fun testFeedbackPermissionDenied() {
    val denyButton = this.device?.findObject(UiSelector().text("DENY"))
    denyButton!!.click()
    assert(ContextCompat.checkSelfPermission(mockContext, "android.permission.READ_MEDIA_IMAGES") == PackageManager.PERMISSION_DENIED)
  }
}


