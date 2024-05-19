package com.github.se.studybuddies.tests

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.ChatType
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.screens.ChatScreen
import com.github.se.studybuddies.ui.chat.ChatScreen
import com.github.se.studybuddies.ui.chat.EditDialog
import com.github.se.studybuddies.ui.chat.IconsOptionsList
import com.github.se.studybuddies.ui.chat.MessageBubble
import com.github.se.studybuddies.ui.chat.OptionsDialog
import com.github.se.studybuddies.ui.chat.SendFileMessage
import com.github.se.studybuddies.ui.chat.SendLinkMessage
import com.github.se.studybuddies.ui.chat.SendPhotoMessage
import com.github.se.studybuddies.viewModels.MessageViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import org.junit.After
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
  fun setUp() {
    Intents.init()
  }

  @After
  fun tearDown() {
    Intents.release()
  }

  @Test
  fun textAndButtonAreCorrectlyDisplayed() {
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
                        "offline"),
                ),
            picture =
                Uri.parse("https://images.pexels.com/photos/6031345/pexels-photo-6031345.jpeg"))
    val vm = MessageViewModel(chat)
    composeTestRule.setContent { ChatScreen(vm, mockNavActions) }
    onComposeScreen<ChatScreen>(composeTestRule) {
      sendButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
      textField { assertIsDisplayed() }
      chatMessage { assertIsDisplayed() }
      messageMoreType { assertIsDisplayed() }
    }
  }

  @Test
  fun messageLongClick() {
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
                        "offline"),
                ),
            picture =
                Uri.parse("https://images.pexels.com/photos/6031345/pexels-photo-6031345.jpeg"))
    val vm = MessageViewModel(chat)
    composeTestRule.setContent { ChatScreen(vm, mockNavActions) }
    onComposeScreen<ChatScreen>(composeTestRule) {
      chatMessage {
        assertIsDisplayed()
        performGesture { longClick() }
      }
      optionDialog { assertIsDisplayed() }
    }
  }

  @Test
  fun clickOption() {
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
                        "offline"),
                ),
            picture =
                Uri.parse("https://images.pexels.com/photos/6031345/pexels-photo-6031345.jpeg"))
    val vm = MessageViewModel(chat)
    composeTestRule.setContent { ChatScreen(vm, mockNavActions) }
    onComposeScreen<ChatScreen>(composeTestRule) {
      messageMoreType { performClick() }
      sendMoreMessagesType { assertIsDisplayed() }
    }
  }

  @Test
  fun testTitle() {
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
    onComposeScreen<ChatScreen>(composeTestRule) {
      chatGroupTitleImage { assertIsDisplayed() }
      chatGroupTitleText { assertIsDisplayed() }
      chatGroupTitleMembers { assertIsDisplayed() }
      chatGroupTitleMember { assertIsDisplayed() }
    }
  }

  private val messageToSend = "Hello, World!"

  @Test
  fun testSendMessage() {
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
    onComposeScreen<ChatScreen>(composeTestRule) {
      // Test the UI elements
      textField { performTextInput(messageToSend) }
      sendButton { performClick() }
    }
  }

  @Test
  fun testTitle2() {
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
    onComposeScreen<ChatScreen>(composeTestRule) {
      chatPrivateTitleImage { assertIsDisplayed() }
      chatPrivateTitleText { assertIsDisplayed() }
    }
  }

  private val sender = User("testUser", "testUser", "testUser", Uri.EMPTY, location = "offline")

  @Test
  fun testTextBubble() {
    val message =
        Message.TextMessage(
            text = "Hello, World!", sender = sender, timestamp = System.currentTimeMillis())

    composeTestRule.setContent { MessageBubble(message, true) }
    onComposeScreen<ChatScreen>(composeTestRule) {
      messageBubble { assertIsDisplayed() }
      textBubbleImage { assertIsDisplayed() }
      textBubbleBox { assertIsDisplayed() }
      textBubbleName { assertIsDisplayed() }
      textBubbleText { assertIsDisplayed() }
      textBubbleTime { assertIsDisplayed() }
    }
  }

  @Test
  fun testPhotoMessageBubble() {
    val message =
        Message.PhotoMessage(
            sender = sender,
            timestamp = System.currentTimeMillis(),
            photoUri =
                Uri.parse("https://images.pexels.com/photos/6031345/pexels-photo-6031345.jpeg"))
    composeTestRule.setContent { MessageBubble(message, true) }
    onComposeScreen<ChatScreen>(composeTestRule) {
      messageBubble { assertIsDisplayed() }
      textBubbleImage { assertIsDisplayed() }
      textBubbleBox { assertIsDisplayed() }
      textBubbleName { assertIsDisplayed() }
      photoMessage { assertIsDisplayed() }
      textBubbleTime { assertIsDisplayed() }
    }
  }

  @Test
  fun testLinkMessageBubble() {
    val message =
        Message.LinkMessage(
            sender = sender,
            timestamp = System.currentTimeMillis(),
            linkName = "Test Link",
            linkUri = Uri.parse("https://www.epfl.ch"))
    composeTestRule.setContent { MessageBubble(message, true) }
    onComposeScreen<ChatScreen>(composeTestRule) {
      messageBubble { assertIsDisplayed() }
      textBubbleImage { assertIsDisplayed() }
      textBubbleBox { assertIsDisplayed() }
      textBubbleName { assertIsDisplayed() }
      linkMessage {
        assertIsDisplayed()
        assertHasClickAction()
      }
      textBubbleTime { assertIsDisplayed() }
    }
  }

  @Test
  fun testLinkMessageClick() {
    val message =
        Message.LinkMessage(
            sender = sender,
            timestamp = System.currentTimeMillis(),
            linkName = "Test Link",
            linkUri = Uri.parse("https://www.epfl.ch"))
    composeTestRule.setContent { MessageBubble(message, true) }
    onComposeScreen<ChatScreen>(composeTestRule) {
      linkMessage {
        performClick()
        intended(IntentMatchers.hasAction(Intent.ACTION_VIEW))
        intended(IntentMatchers.hasData(message.linkUri))
      }
    }
  }

  @Test
  fun testFileMessageBubble() {
    val message =
        Message.FileMessage(
            sender = sender,
            timestamp = System.currentTimeMillis(),
            fileName = "Test File",
            fileUri =
                Uri.parse(
                    "https://firebasestorage.googleapis.com/v0/b/study-buddies-e655a.appspot.com/o/chatData%2F093a42fc-f032-4979-befd-49f939a36de4%2Fcfd241c5-985e-46f1-8812-c3d9d91109ac?alt=media&token=47aeeabe-2abf-440e-b2f6-ec7cdccf9e1e"))
    composeTestRule.setContent { MessageBubble(message, true) }
    onComposeScreen<ChatScreen>(composeTestRule) {
      messageBubble { assertIsDisplayed() }
      textBubbleImage { assertIsDisplayed() }
      textBubbleBox { assertIsDisplayed() }
      textBubbleName { assertIsDisplayed() }
      fileMessage {
        assertIsDisplayed()
        assertHasClickAction()
      }
      textBubbleTime { assertIsDisplayed() }
    }
  }

  @Test
  fun testFileMessageClick() {
    val message =
        Message.FileMessage(
            sender = sender,
            timestamp = System.currentTimeMillis(),
            fileName = "Test File",
            fileUri =
                Uri.parse(
                    "https://firebasestorage.googleapis.com/v0/b/study-buddies-e655a.appspot.com/o/chatData%2F093a42fc-f032-4979-befd-49f939a36de4%2Fcfd241c5-985e-46f1-8812-c3d9d91109ac?alt=media&token=47aeeabe-2abf-440e-b2f6-ec7cdccf9e1e"))
    composeTestRule.setContent { MessageBubble(message, true) }
    onComposeScreen<ChatScreen>(composeTestRule) {
      fileMessage {
        performClick()
        intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER))
      }
    }
  }

  @Test
  fun testTextBubbleOwn() {
    val message =
        Message.TextMessage(
            text = "Hello, World!",
            sender = User("testUser", "testUser", "testUser", Uri.EMPTY, location = "offline"),
            timestamp = System.currentTimeMillis())
    composeTestRule.setContent { MessageBubble(message, false) }
    onComposeScreen<ChatScreen>(composeTestRule) {
      messageBubble { assertIsDisplayed() }

      textBubbleBox { assertIsDisplayed() }
      textBubbleText { assertIsDisplayed() }
      textBubbleTime { assertIsDisplayed() }
    }
  }

  @Test
  fun testOptionDialog() {
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
    }
  }

  @Test
  fun testOptionDialog2() {
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
    onComposeScreen<ChatScreen>(composeTestRule) {
      optionDialog { assertIsDisplayed() }
      optionDialogStartPMButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  private val chat =
      Chat(
          uid = groupUID,
          type = ChatType.GROUP,
          name = "Test Group",
          members = emptyList(),
          picture = Uri.EMPTY)

  @Test
  fun testEditDialog() {
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
    onComposeScreen<ChatScreen>(composeTestRule) {
      editDialog { assertIsDisplayed() }
      editDialogTextField { assertIsDisplayed() }
    }
  }

  @Test
  fun testSendMoreMessagesType() {
    composeTestRule.setContent {
      val showIconsOptions = remember { mutableStateOf(true) }
      val showAddImage = remember { mutableStateOf(false) }
      val showAddLink = remember { mutableStateOf(false) }
      val showAddFile = remember { mutableStateOf(false) }
      IconsOptionsList(showIconsOptions, showAddImage, showAddLink, showAddFile)
    }
    onComposeScreen<ChatScreen>(composeTestRule) {
      sendMoreMessagesType { assertIsDisplayed() }
      sendMoreMessagesTypePhoto {
        assertIsDisplayed()
        assertHasClickAction()
      }
      sendMoreMessagesTypeLink {
        assertIsDisplayed()
        assertHasClickAction()
      }
      sendMoreMessagesTypeFile {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  @Test
  fun testSendPhoto() {
    composeTestRule.setContent {
      val chat =
          Chat(
              uid = groupUID,
              type = ChatType.GROUP,
              name = "Test Group",
              members = emptyList(),
              picture = Uri.EMPTY)
      val vm = MessageViewModel(chat)
      val showAddImage = remember { mutableStateOf(true) }
      SendPhotoMessage(vm, showAddImage)
    }
    onComposeScreen<ChatScreen>(composeTestRule) {
      sendPhotoMessageDialog { assertIsDisplayed() }
      sendPhotoMessageBox { assertIsDisplayed() }
      sendPhotoMessageSetPicture {
        assertIsDisplayed()
        assertHasClickAction()
      }
      sendPhotoMessageSaveButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  @Test
  fun testSendLink() {
    composeTestRule.setContent {
      val chat =
          Chat(
              uid = groupUID,
              type = ChatType.GROUP,
              name = "Test Group",
              members = emptyList(),
              picture = Uri.EMPTY)
      val vm = MessageViewModel(chat)
      val showAddLink = remember { mutableStateOf(true) }
      SendLinkMessage(vm, showAddLink)
    }
    onComposeScreen<ChatScreen>(composeTestRule) {
      sendLinkMessageDialog { assertIsDisplayed() }
      sendLinkMessageBox { assertIsDisplayed() }
      sendLinkMessageTextFiled { assertIsDisplayed() }
      sendLinkMessageSaveButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  @Test
  fun testSendFile() {
    composeTestRule.setContent {
      val chat =
          Chat(
              uid = groupUID,
              type = ChatType.GROUP,
              name = "Test Group",
              members = emptyList(),
              picture = Uri.EMPTY)
      val vm = MessageViewModel(chat)
      val showAddFile = remember { mutableStateOf(true) }
      SendFileMessage(vm, showAddFile)
    }
    onComposeScreen<ChatScreen>(composeTestRule) {
      sendFileMessageDialog { assertIsDisplayed() }
      sendFileMessageBox { assertIsDisplayed() }
      sendFileMessageSaveButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }
}