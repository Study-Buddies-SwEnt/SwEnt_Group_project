package com.github.se.studybuddies.tests

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.screens.DirectMessageScreen
import com.github.se.studybuddies.ui.chat.DirectMessageScreen
import com.github.se.studybuddies.viewModels.ChatViewModel
import com.github.se.studybuddies.viewModels.ContactsViewModel
import com.github.se.studybuddies.viewModels.DirectMessageViewModel
import com.github.se.studybuddies.viewModels.UsersViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DirectMessageTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Test
  fun testDirectMessageScreen() {
    val directMessageViewModel = DirectMessageViewModel()
    val chatViewModel = ChatViewModel()
    val usersViewModel = UsersViewModel()
    val navigationActions = mockNavActions
    val contactsViewModel = ContactsViewModel()
    composeTestRule.setContent {
      DirectMessageScreen(
          viewModel = directMessageViewModel,
          chatViewModel = chatViewModel,
          usersViewModel = usersViewModel,
          navigationActions = navigationActions,
          contactsViewModel = contactsViewModel)
    }
    onComposeScreen<DirectMessageScreen>(composeTestRule) {
      directMessagesEmpty { assertIsDisplayed() }
      directMessagesNotEmpty { assertIsNotDisplayed() }
      addPrivateMessageButton {
        assertIsDisplayed()
        performClick()
      }
    }
  }
}
