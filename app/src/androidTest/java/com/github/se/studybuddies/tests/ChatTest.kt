package com.github.se.studybuddies.tests

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.ChatType
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.screens.ChatScreen
import com.github.se.studybuddies.ui.chat.ChatScreen
import com.github.se.studybuddies.ui.chat.EditDialog
import com.github.se.studybuddies.ui.chat.OptionsDialog
import com.github.se.studybuddies.ui.chat.TextBubble
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

  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private val groupUID = "automaticTestGroupUID"

  @Before
  fun testSetup() {
    val chat =
        Chat(
            uid = groupUID,
            type = ChatType.GROUP,
            name = "Test Group",
            members =
                listOf(
                    User(
                        "1",
                        "e@a",
                        "best 1",
                        Uri.parse(
                            "https://images.pexels.com/photos/6031345/pexels-photo-6031345.jpeg"),
                        "offline")),
            picture =
                Uri.parse("https://images.pexels.com/photos/6031345/pexels-photo-6031345.jpeg"))
    val vm = MessageViewModel(chat)
    composeTestRule.setContent { ChatScreen(vm, mockNavActions) }
  }

  @Test
  fun textAndButtonAreCorrectlyDisplayed() {
    onComposeScreen<ChatScreen>(composeTestRule) {
      sendButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
      textField { assertIsDisplayed() }
      chatMessage { assertIsDisplayed() }
    }
  }

  @Test
  fun testTitle() {
    onComposeScreen<ChatScreen>(composeTestRule) {
      /*
      chatGroupTitleImage { assertIsDisplayed() }
      chatGroupTitleText { assertIsDisplayed() }
      chatGroupTitleMembers { assertIsDisplayed() }
      chatGroupTitleMember { assertIsDisplayed() }
         */
    }
  }

  private val messageToSend = "Hello, World!"

  @Test
  fun testSendMessage() {
    onComposeScreen<ChatScreen>(composeTestRule) {
      // Test the UI elements
      textField { performTextInput(messageToSend) }
      sendButton { performClick() }
    }
  }
}

@RunWith(AndroidJUnit4::class)
class ChatTestPrivate : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private val groupUID = "automaticTestGroupUID"

  @Before
  fun testSetup() {
    val chat =
        Chat(
            uid = groupUID,
            type = ChatType.PRIVATE,
            name = "Test Group",
            members =
                listOf(
                    User(
                        "1",
                        "e@a",
                        "best 1",
                        Uri.parse(
                            "https://images.pexels.com/photos/6031345/pexels-photo-6031345.jpeg"),
                        "offline")),
            picture =
                Uri.parse("https://images.pexels.com/photos/6031345/pexels-photo-6031345.jpeg"))
    val vm = MessageViewModel(chat)
    composeTestRule.setContent { ChatScreen(vm, mockNavActions) }
  }

  @Test
  fun testTitle() {
    onComposeScreen<ChatScreen>(composeTestRule) {
      chatPrivateTitleImage { assertIsDisplayed() }
      chatPrivateTitleText { assertIsDisplayed() }
    }
  }
}

@RunWith(AndroidJUnit4::class)
class ChatTestBubble : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @Before
  fun testSetup() {
    val message =
        Message.TextMessage(
            text = "Hello, World!",
            sender = User("testUser", "testUser", "testUser", Uri.EMPTY, location = "offline"),
            timestamp = System.currentTimeMillis())
    composeTestRule.setContent { TextBubble(message, true) }
  }

  @Test
  fun testTextBubble() {
    onComposeScreen<ChatScreen>(composeTestRule) {
      textBubble { assertIsDisplayed() }
      textBubbleImage { assertIsDisplayed() }
      textBubbleBox { assertIsDisplayed() }
      textBubbleName { assertIsDisplayed() }
      textBubbleText { assertIsDisplayed() }
      textBubbleTime { assertIsDisplayed() }
    }
  }
}

@RunWith(AndroidJUnit4::class)
class ChatTestBubbleNotUser : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @Before
  fun testSetup() {
    val message =
        Message.TextMessage(
            text = "Hello, World!",
            sender = User("testUser", "testUser", "testUser", Uri.EMPTY, location = "offline"),
            timestamp = System.currentTimeMillis())
    composeTestRule.setContent { TextBubble(message, false) }
  }

  @Test
  fun testTextBubble() {
    onComposeScreen<ChatScreen>(composeTestRule) {
      textBubble { assertIsDisplayed() }

      textBubbleBox { assertIsDisplayed() }
      textBubbleText { assertIsDisplayed() }
      textBubbleTime { assertIsDisplayed() }
    }
  }
}

@RunWith(AndroidJUnit4::class)
class ChatTestOption : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private val groupUID = "automaticTestGroupUID"

  @Before
  fun testSetup() {
    composeTestRule.setContent {
      val chat =
          Chat(
              uid = groupUID,
              type = ChatType.GROUP,
              name = "Test Group",
              members = emptyList(),
              picture = Uri.EMPTY)
      val vm = MessageViewModel(chat)
      val message =
          Message.TextMessage(
              text = "Hello, World!",
              sender =
                  User(User.empty().uid, "testUser", "testUser", Uri.EMPTY, location = "offline"),
              timestamp = System.currentTimeMillis())
      val showOptionsDialog = remember { mutableStateOf(true) }
      val showEditDialog = remember { mutableStateOf(false) }
      OptionsDialog(vm, message, showOptionsDialog, showEditDialog, mockNavActions)
    }
  }

  @Test
  fun testOptionDialog() {
    onComposeScreen<ChatScreen>(composeTestRule) {
      optionDialog { assertIsDisplayed() }
      optionDialogEdit {
        assertIsDisplayed()
        assertHasClickAction()
      }
      optionDialogDeleteButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
      optionDialogCancelButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }
}

@RunWith(AndroidJUnit4::class)
class ChatTestOptionOther : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private val groupUID = "automaticTestGroupUID"

  @Before
  fun setup() {
    composeTestRule.setContent {
      val chat =
          Chat(
              uid = groupUID,
              type = ChatType.GROUP,
              name = "Test Group",
              members = emptyList(),
              picture = Uri.EMPTY)
      val vm = MessageViewModel(chat)
      val message =
          Message.TextMessage(
              text = "Hello, World!",
              sender = User("userUID", "testUser", "testUser", Uri.EMPTY, "offline"),
              timestamp = System.currentTimeMillis())
      val showOptionsDialog = remember { mutableStateOf(true) }
      val showEditDialog = remember { mutableStateOf(false) }
      OptionsDialog(vm, message, showOptionsDialog, showEditDialog, mockNavActions)
    }
  }

  @Test
  fun testOptionDialog() {
    onComposeScreen<ChatScreen>(composeTestRule) {
      optionDialog { assertIsDisplayed() }
      optionDialogStartPMButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
      optionDialogCancelButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }
}

@RunWith(AndroidJUnit4::class)
class ChatTestEdit : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  private val groupUID = "automaticTestGroupUID"
  private val chat =
      Chat(
          uid = groupUID,
          type = ChatType.GROUP,
          name = "Test Group",
          members = emptyList(),
          picture = Uri.EMPTY)

  @Before
  fun testSetup() {
    composeTestRule.setContent {
      val vm = MessageViewModel(chat)
      val message =
          Message.TextMessage(
              text = "Hello, World!",
              sender =
                  User(User.empty().uid, "testUser", "testUser", Uri.EMPTY, location = "offline"),
              timestamp = System.currentTimeMillis())
      val showEditDialog = remember { mutableStateOf(true) }
      EditDialog(vm, message, showEditDialog)
    }
  }

  @Test
  fun testEditDialog() {
    onComposeScreen<ChatScreen>(composeTestRule) {
      editDialog { assertIsDisplayed() }
      editDialogTextField { assertIsDisplayed() }
      editDialogCancelButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }
}
