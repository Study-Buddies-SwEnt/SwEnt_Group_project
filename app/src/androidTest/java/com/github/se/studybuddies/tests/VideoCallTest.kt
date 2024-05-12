package com.github.se.studybuddies.tests

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.se.studybuddies.ui.video_call.VideoCallScreen
import com.github.se.studybuddies.viewModels.VideoCallViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.getstream.log.Priority
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.core.logging.LoggingLevel
import io.getstream.video.android.model.User
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.junit4.MockKRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VideoCallTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  private var context: Context = InstrumentationRegistry.getInstrumentation().targetContext
  private val uid = "111test"
  private var videoCallVM: VideoCallViewModel? = null
  private val userID = "testUser"

  @Before
  fun testSetup() {
    StreamVideoBuilder(
            context = context,
            apiKey = "x52wgjq8qyfc",
            user =
                User(
                    id = userID,
                    name = "test",
                ),
            token =
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiSm9ydXVzX0NfQmFvdGgiLCJpc3MiOiJodHRwczovL3Byb250by5nZXRzdHJlYW0uaW8iLCJzdWIiOiJ1c2VyL0pvcnV1c19DX0Jhb3RoIiwiaWF0IjoxNzE0NjUzOTg0LCJleHAiOjE3MTUyNTg3ODl9.WkUHrFvbIdfjqKIcxi4FQB6GmQB1q0uyQEAfJ61P_g0",
            loggingLevel = LoggingLevel(priority = Priority.VERBOSE),
        )
        .build()

    val call = StreamVideo.instance().call("default", uid)
    videoCallVM = VideoCallViewModel(call, uid)
    composeTestRule.setContent { VideoCallScreen(call, videoCallVM!!) }
  }

  @Test
  fun elementsAreDisplayed() {
    ComposeScreen.onComposeScreen<com.github.se.studybuddies.screens.VideoCallScreen>(
        composeTestRule) {
          controls { assertIsDisplayed() }
        }
  }
}
