package com.github.se.studybuddies.tests

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.screens.GroupScreen
import com.github.se.studybuddies.ui.groups.GroupScreen
import com.github.se.studybuddies.utilities.MockDatabase
import com.github.se.studybuddies.viewModels.ChatViewModel
import com.github.se.studybuddies.viewModels.GroupViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroupScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  // userTest
  // aloneUserTest
  private val groupUID = "groupTest1"
  private val topicUID = "topicTest1"
  private val db = MockDatabase()
  private val groupVM = GroupViewModel(groupUID, db)
  private val chatVM = ChatViewModel()

  @Before
  fun testSetup() {
    composeTestRule.setContent { GroupScreen(groupUID, groupVM, chatVM, mockNavActions, db) }
  }

  @Test
  fun topAppBarTest() = run {
    ComposeScreen.onComposeScreen<GroupScreen>(composeTestRule) {
      topAppBox {
        // arrange: verify pre-conditions
        assertIsDisplayed()
      }
      topAppBar {
        // arrange: verify pre-conditions
        assertIsDisplayed()
      }

      divider {
        // arrange: verify pre-conditions
        assertIsDisplayed()
      }
      goBackButton {
        // arrange: verify pre-conditions
        assertIsDisplayed()
        performClick()
      }
    }
    // assert: the nav action has been called
    verify { mockNavActions.navigateTo(Route.GROUPSHOME) }
    confirmVerified(mockNavActions)
  }

  @Test
  fun groupItemElementsDisplay() {
    ComposeScreen.onComposeScreen<GroupScreen>(composeTestRule) {
      composeTestRule
          .onNodeWithTag(groupUID + "_settings_row", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(groupUID + "_settings_button", useUnmergedTree = true)
          .assertIsDisplayed()
          .performClick()
      composeTestRule
          .onNodeWithTag(groupUID + "_dropDownMenu", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(groupUID + "_Modify group_item", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(groupUID + "_Members_item", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(groupUID + "_Leave group_item", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(groupUID + "_Delete group_item", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(groupUID + "_Modify group_text", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("Modify group")
      composeTestRule
          .onNodeWithTag(groupUID + "_Members_text", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("Members")
      composeTestRule
          .onNodeWithTag(groupUID + "_Leave group_text", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("Leave group")
      composeTestRule
          .onNodeWithTag(groupUID + "_Delete group_text", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("Delete group")
    }
  }

  @Test
  fun modifyGroup() {
    ComposeScreen.onComposeScreen<GroupScreen>(composeTestRule) {
      composeTestRule
          .onNodeWithTag(groupUID + "_settings_button", useUnmergedTree = true)
          .assertIsDisplayed()
          .performClick()
      composeTestRule
          .onNodeWithTag(groupUID + "_Modify group_item", useUnmergedTree = true)
          .assertIsDisplayed()
          .performClick()
      verify { mockNavActions.navigateTo("GroupSetting/groupTest1") }
      confirmVerified(mockNavActions)
    }
  }

  @Test
  fun seeMembers() {
    ComposeScreen.onComposeScreen<GroupScreen>(composeTestRule) {
      composeTestRule
          .onNodeWithTag(groupUID + "_settings_button", useUnmergedTree = true)
          .assertIsDisplayed()
          .performClick()
      composeTestRule
          .onNodeWithTag(groupUID + "_Members_item", useUnmergedTree = true)
          .assertIsDisplayed()
          .performClick()
      verify { mockNavActions.navigateTo("${Route.GROUPMEMBERS}/groupTest1") }
      confirmVerified(mockNavActions)
    }
  }

  @Test
  fun leavingGroupDisplayed() {
    ComposeScreen.onComposeScreen<GroupScreen>(composeTestRule) {
      composeTestRule
          .onNodeWithTag(groupUID + "_settings_button", useUnmergedTree = true)
          .assertIsDisplayed()
          .performClick()
      composeTestRule
          .onNodeWithTag(groupUID + "_Leave group_item", useUnmergedTree = true)
          .assertIsDisplayed()
          .performClick()
      composeTestRule
          .onNodeWithTag(groupUID + "_leave_box", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(groupUID + "_leave_column", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(groupUID + "_leave_text", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("Are you sure you want to leave the group ?")
      composeTestRule
          .onNodeWithTag(groupUID + "_leave_row", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(groupUID + "_leave_yes_button", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(groupUID + "_leave_yes_text", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("Yes")
      composeTestRule
          .onNodeWithTag(groupUID + "_leave_no_button", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(groupUID + "_leave_no_text", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("No")
    }
  }

  @Test
  fun leaveOptionsGroup() {
    ComposeScreen.onComposeScreen<GroupScreen>(composeTestRule) {
      composeTestRule
          .onNodeWithTag(groupUID + "_settings_button", useUnmergedTree = true)
          .assertIsDisplayed()
          .performClick()
      composeTestRule
          .onNodeWithTag(groupUID + "_Leave group_item", useUnmergedTree = true)
          .assertIsDisplayed()
          .performClick()
      composeTestRule
          .onNodeWithTag(groupUID + "_leave_yes_button", useUnmergedTree = true)
          .assertIsDisplayed()
          .performClick()
      verify { mockNavActions.navigateTo(Route.GROUPSHOME) }
      confirmVerified(mockNavActions)
      composeTestRule
          .onNodeWithTag(groupUID + "_settings_button", useUnmergedTree = true)
          .assertIsDisplayed()
          .performClick()
      composeTestRule
          .onNodeWithTag(groupUID + "_Leave group_item", useUnmergedTree = true)
          .assertIsDisplayed()
          .performClick()
      composeTestRule
          .onNodeWithTag(groupUID + "_leave_no_button", useUnmergedTree = true)
          .assertIsDisplayed()
          .performClick()
    }
  }

  @Test
  fun deleteGroupDisplayed() {
    ComposeScreen.onComposeScreen<GroupScreen>(composeTestRule) {
      composeTestRule
          .onNodeWithTag(groupUID + "_settings_button", useUnmergedTree = true)
          .assertIsDisplayed()
          .performClick()
      composeTestRule
          .onNodeWithTag(groupUID + "_Delete group_item", useUnmergedTree = true)
          .assertIsDisplayed()
          .performClick()
      composeTestRule
          .onNodeWithTag(groupUID + "_delete_box", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(groupUID + "_delete_column", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(groupUID + "_delete_text1", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("Are you sure you want to delete the group ?")
      composeTestRule
          .onNodeWithTag(groupUID + "_delete_text2", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("This will delete the group and all its content for all members")
      composeTestRule
          .onNodeWithTag(groupUID + "_delete_row", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(groupUID + "_delete_yes_button", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(groupUID + "_delete_yes_text", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("Yes")
      composeTestRule
          .onNodeWithTag(groupUID + "_delete_no_button", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(groupUID + "_delete_no_text", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("No")
    }
  }

  @Test
  fun deleteGroupOption() = run {
    ComposeScreen.onComposeScreen<GroupScreen>(composeTestRule) {
      step("DeleteYes") {
        composeTestRule
            .onNodeWithTag(groupUID + "_settings_button", useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()
        composeTestRule
            .onNodeWithTag(groupUID + "_Delete group_item", useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()
        composeTestRule
            .onNodeWithTag(groupUID + "_delete_yes_button", useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()
        verify { mockNavActions.navigateTo(Route.GROUPSHOME) }
        confirmVerified(mockNavActions)
      }
      step("DeleteNo") {
        composeTestRule
            .onNodeWithTag(groupUID + "_settings_button", useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()
        composeTestRule
            .onNodeWithTag(groupUID + "_Delete group_item", useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()
        composeTestRule
            .onNodeWithTag(groupUID + "_delete_no_button", useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()
      }
    }
  }

  @Test
  fun createTopic() {
    ComposeScreen.onComposeScreen<GroupScreen>(composeTestRule) {
      floatingActionRow { assertIsDisplayed() }
      composeTestRule.onNodeWithTag("create_topic_icon", useUnmergedTree = true).assertIsDisplayed()
      createTopicButton {
        assertIsDisplayed()
        performClick()
      }
    }
    verify { mockNavActions.navigateTo("${Route.TOPICCREATION}/groupTest1") }
    confirmVerified(mockNavActions)
  }

  @Test
  fun bottomBarTest() {
    ComposeScreen.onComposeScreen<GroupScreen>(composeTestRule) {
      groupBottomBar { assertIsDisplayed() }
      videoCallButton {
        assertIsDisplayed()
        performClick()
      }
      verify { mockNavActions.navigateTo("${Route.CALLLOBBY}/groupTest1") }
      confirmVerified(mockNavActions)
      timerButton {
        assertIsDisplayed()
        performClick()
      }
      verify { mockNavActions.navigateTo("${Route.SHAREDTIMER}/groupTest1") }
      confirmVerified(mockNavActions)
    }
  }

  @Test
  fun generalElementAreDisplayed() {
    ComposeScreen.onComposeScreen<GroupScreen>(composeTestRule) {
      groupScreenColumn { assertIsDisplayed() }
      groupBox {
        assertIsDisplayed()
        assertHasClickAction()
      }
      composeTestRule.onNodeWithTag("GroupRowChat", useUnmergedTree = true).assertIsDisplayed()
      composeTestRule.onNodeWithTag("BoxPP", useUnmergedTree = true).assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("GeneralChatText", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("General chat")
      composeTestRule.onNodeWithTag("SpacerPP", useUnmergedTree = true).assertIsDisplayed()
      composeTestRule.onNodeWithTag("GroupDivider", useUnmergedTree = true).assertIsDisplayed()
    }
  }

  @Test
  fun goToChat() {
    ComposeScreen.onComposeScreen<GroupScreen>(composeTestRule) {
      groupBox { performClick() }
      verify { mockNavActions.navigateTo(Route.CHAT) }
      confirmVerified(mockNavActions)
    }
  }

  @Test
  fun topicAreDisplayed() {
    ComposeScreen.onComposeScreen<GroupScreen>(composeTestRule) {
      composeTestRule.waitForIdle()
      composeTestRule
          .onNodeWithTag(topicUID + "_item", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertHasClickAction()
      composeTestRule.onNodeWithTag(topicUID + "_row", useUnmergedTree = true).assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(topicUID + "_spacer", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(topicUID + "_text", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("TestTopic")
    }
  }

  @Test
  fun clickOnTopic() {
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag(topicUID + "_item", useUnmergedTree = true).performClick()
    verify { mockNavActions.navigateTo("${Route.TOPIC}/topicTest1/groupTest1") }
    confirmVerified(mockNavActions)
  }
}

@RunWith(AndroidJUnit4::class)
class GroupTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  // userTest
  // aloneUserTest
  private val groupUID = "groupTest1"
  private val topicUID = "topicTest1"
  private val db = MockDatabase()
  private val groupVM = GroupViewModel(groupUID, db)
  private val chatVM = ChatViewModel()

  @Before
  fun testSetup() {
    composeTestRule.setContent { GroupScreen(groupUID, groupVM, chatVM, mockNavActions, db) }
  }

  @Test
  fun topicAreDisplayed() {
    ComposeScreen.onComposeScreen<GroupScreen>(composeTestRule) {
      composeTestRule.waitForIdle()
      composeTestRule
          .onNodeWithTag(topicUID + "_item", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertHasClickAction()
      composeTestRule.onNodeWithTag(topicUID + "_row", useUnmergedTree = true).assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(topicUID + "_spacer", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag(topicUID + "_text", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("TestTopic")
    }
  }

  @Test
  fun clickOnTopic() {
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag(topicUID + "_item", useUnmergedTree = true).performClick()
    verify { mockNavActions.navigateTo("${Route.TOPIC}/topicTest1/groupTest1") }
    confirmVerified(mockNavActions)
  }
}
