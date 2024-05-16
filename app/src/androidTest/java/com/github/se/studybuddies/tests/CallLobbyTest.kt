package com.github.se.studybuddies.tests

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.GrantPermissionRule
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.screens.CallLobbyScreen
import com.github.se.studybuddies.ui.video_call.CallLobbyScreen
import com.github.se.studybuddies.viewModels.CallLobbyViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.model.User
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CallLobbyTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(
          android.Manifest.permission.CAMERA,
          android.Manifest.permission.RECORD_AUDIO,
      )

  @get:Rule val mockkRule = MockKRule(this)
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private val uid = "111test"
  private val userID = "testUser"

  @Before
  fun setup() {
    val context: Context = ApplicationProvider.getApplicationContext()
    getInstrumentation()
        .uiAutomation
        .grantRuntimePermission("com.github.se.studybuddies.tests", "android.permission.CAMERA")
    getInstrumentation()
        .uiAutomation
        .grantRuntimePermission(
            "com.github.se.studybuddies.tests", "android.permission.RECORD_AUDIO")
    if (StreamVideo.isInstalled) {
      StreamVideo.removeClient()
    }
    StreamVideoBuilder(
            context = context,
            apiKey = "x52wgjq8qyfc",
            user =
                User(
                    id = userID,
                    name = "test",
                ),
            token =
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiSm9ydXVzX0NfQmFvdGgiLCJpc3MiOiJodHRwczovL3Byb250by5nZXRzdHJlYW0uaW8iLCJzdWIiOiJ1c2VyL0pvcnV1c19DX0Jhb3RoIiwiaWF0IjoxNzE0NjUzOTg0LCJleHAiOjE3MTUyNTg3ODl9.WkUHrFvbIdfjqKIcxi4FQB6GmQB1q0uyQEAfJ61P_g0")
        .build()

    composeTestRule.setContent {
      CallLobbyScreen(uid, CallLobbyViewModel(uid, "default"), mockNavActions)
    }
  }

  @Test
  fun elementsAreDisplayed() {
    ComposeScreen.onComposeScreen<CallLobbyScreen>(composeTestRule) {
      /*
      runBlocking {
        delay(10000) // Adjust the delay time as needed
      }
      content { assertIsDisplayed() }
      topAppBar { assertIsDisplayed() }
      callIcon { assertIsDisplayed() }
      previewText { assertIsDisplayed() }
      callLobby { assertIsDisplayed() }
      joinCallButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
         */
    }
  }

  @After
  fun afterTest() {
    StreamVideo.removeClient()
  }
}
