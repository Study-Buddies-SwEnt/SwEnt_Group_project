package com.github.se.studybuddies.tests

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.screens.ChatScreen
import com.github.se.studybuddies.ui.ChatScreen
import com.github.se.studybuddies.viewModels.MessageViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  val groupUID = "automaticTestGroupUID"

  @Before
  fun testSetup() {
    val vm = MessageViewModel(groupUID)
    composeTestRule.setContent { ChatScreen(vm, mockNavActions) }
  }

  @Test
  fun textAndButtonAreCorrectlyDisplayed() {
    onComposeScreen<ChatScreen>(composeTestRule) {
      sendButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
      text_field { assertIsDisplayed() }
    }
  }

  val message_to_send = "Hello, World!"

  @Test
  fun testSendMessage() {
    onComposeScreen<ChatScreen>(composeTestRule) {
      // Test the UI elements
      text_field { performTextInput(message_to_send) }
      sendButton { performClick() }
    }
  }

  @Test
  fun testSendReceiveMessage() = run {
    onComposeScreen<ChatScreen>(composeTestRule) {
      text_field { performTextInput(message_to_send) }
      sendButton { performClick() }
      ownMsg {
        assertExists(message_to_send)
        assertTextEquals(message_to_send)
      }
    }
  }
}
