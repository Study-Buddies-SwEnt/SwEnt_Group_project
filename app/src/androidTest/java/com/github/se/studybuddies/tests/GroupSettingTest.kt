package com.github.se.studybuddies.tests

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.database.MockDatabase
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.screens.GroupSettingScreen
import com.github.se.studybuddies.ui.groups.GroupSetting
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
class GroupSettingTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  // userTest
  // aloneUserTest
  private val groupUID = "groupTest1"
  private val db = MockDatabase()
  private val groupVM = GroupViewModel(groupUID, db)
  val chatVM = ChatViewModel()

  @Before
  fun testSetup() {
    composeTestRule.setContent { GroupSetting(groupUID, groupVM, mockNavActions, db) }
  }

  @Test
  fun topAppBarTest() {
    ComposeScreen.onComposeScreen<GroupSettingScreen>(composeTestRule) {
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
    verify { mockNavActions.goBack() }
    confirmVerified(mockNavActions)
  }

  @Test
  fun elementAreDisplayed() {
    ComposeScreen.onComposeScreen<GroupSettingScreen>(composeTestRule) {
      settingColumn { assertIsDisplayed() }
      settingLazyColumn { assertIsDisplayed() }
      composeTestRule
          .onNodeWithTag("setting_lazy_column", useUnmergedTree = true)
          .assertExists()
          .performScrollToNode(hasTestTag("setting_spacer1"))
      spacer1 { assertExists() }
      composeTestRule
          .onNodeWithTag("setting_lazy_column", useUnmergedTree = true)
          .assertExists()
          .performScrollToNode(hasTestTag("setting_spacer2"))
      spacer2 { assertExists() }
      composeTestRule
          .onNodeWithTag("setting_lazy_column", useUnmergedTree = true)
          .assertExists()
          .performScrollToNode(hasTestTag("setting_spacer3"))
      spacer3 { assertExists() }
      composeTestRule
          .onNodeWithTag("setting_lazy_column", useUnmergedTree = true)
          .assertExists()
          .performScrollToNode(hasTestTag("setting_spacer4"))
      spacer4 { assertExists() }
      val groupName = "test group"
      modifyName {
        assertIsDisplayed()
        performTextClearance()
        performTextInput(groupName)
        assertTextContains(groupName)
      }
      Espresso.closeSoftKeyboard()
      imagePP { assertIsDisplayed() }
      spacerPP { assertExists() }
      buttonPP { assertIsDisplayed() }

      composeTestRule
          .onNodeWithTag("setting_lazy_column", useUnmergedTree = true)
          .assertExists()
          .performScrollToNode(hasTestTag("add_member_column"))
      addMemberColumn { assertIsDisplayed() }
      addMemberButton { assertIsDisplayed() }
      composeTestRule
          .onNodeWithTag("add_member_button_text", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("Add member with UID")
      composeTestRule
          .onNodeWithTag("setting_lazy_column", useUnmergedTree = true)
          .assertExists()
          .performScrollToNode(hasTestTag("share_link_column"))
      shareLinkColumn { assertIsDisplayed() }
      shareLinkButton { assertIsDisplayed() }
      composeTestRule
          .onNodeWithTag("share_link_button_text", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("Share Link")
      composeTestRule
          .onNodeWithTag("setting_lazy_column", useUnmergedTree = true)
          .assertExists()
          .performScrollToNode(hasTestTag("save_button"))
      saveButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  @Test
  fun addMemberUID() = run {
    ComposeScreen.onComposeScreen<GroupSettingScreen>(composeTestRule) {
      step("Add valid user") {
        composeTestRule
            .onNodeWithTag("setting_lazy_column", useUnmergedTree = true)
            .assertExists()
            .performScrollToNode(hasTestTag("add_member_column"))
        addMemberButton {
          assertIsDisplayed()
          performClick()
        }
        composeTestRule
            .onNodeWithTag("setting_lazy_column", useUnmergedTree = true)
            .assertExists()
            .performScrollToNode(hasTestTag("add_memberUID_text_field"))
        addMemberUIDTextField { assertIsDisplayed() }
        composeTestRule
            .onNodeWithTag("add_memberUID_text", useUnmergedTree = true)
            .assertIsDisplayed()
            .assertTextContains("Enter UserID")
        val validUser = "userTest2"
        addMemberUIDTextField {
          performTextClearance()
          performTextInput(validUser)
          assertTextContains(validUser)
          performImeAction() // Simulate the Enter key press
        }
        composeTestRule.waitForIdle()
        Espresso.closeSoftKeyboard()
        composeTestRule.waitForIdle()
        val group = groupVM.group.value // get the group from the ViewModel
        if (group != null) {
          val members = group.members // get the members of the group
          assert(members.contains(validUser)) // check if the members list contains "testUser2"
        }
        successSnackbar { assertIsDisplayed() }

        composeTestRule
            .onNodeWithTag("success_text")
            .assertIsDisplayed()
            .assertTextContains("User have been successfully added to the group")
        composeTestRule
            .onNodeWithTag("success_button")
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
      }
      step("Add invalid user") {
        composeTestRule
            .onNodeWithTag("setting_lazy_column", useUnmergedTree = true)
            .assertExists()
            .performScrollToNode(hasTestTag("add_member_column"))
        addMemberButton {
          assertIsDisplayed()
          performClick()
        }
        composeTestRule
            .onNodeWithTag("setting_lazy_column", useUnmergedTree = true)
            .assertExists()
            .performScrollToNode(hasTestTag("add_memberUID_text_field"))
        addMemberUIDTextField { assertIsDisplayed() }
        composeTestRule
            .onNodeWithTag("add_memberUID_text", useUnmergedTree = true)
            .assertIsDisplayed()
            .assertTextContains("Enter UserID")
        val invalidUser = "wrongUser"
        addMemberUIDTextField {
          performTextClearance()
          performTextInput(invalidUser)
          assertTextContains(invalidUser)
          performImeAction() // Simulate the Enter key press
        }
        composeTestRule.waitForIdle()
        Espresso.closeSoftKeyboard()
        composeTestRule.waitForIdle()
        val group = groupVM.group.value // get the group from the ViewModel
        composeTestRule.waitForIdle()
        if (group != null) {
          val members = group.members // get the members of the group
          assert(!members.contains(invalidUser)) // check if the members list contains "testUser2"
        }
        errorSnackbar { assertIsDisplayed() }

        composeTestRule
            .onNodeWithTag("error_text")
            .assertIsDisplayed()
            .assertTextContains("Can\'t find a member with this UID")
        composeTestRule
            .onNodeWithTag("error_button")
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
      }
    }
  }

  @Test
  fun shareLink() {
    ComposeScreen.onComposeScreen<GroupSettingScreen>(composeTestRule) {
      composeTestRule
          .onNodeWithTag("setting_lazy_column", useUnmergedTree = true)
          .assertExists()
          .performScrollToNode(hasTestTag("share_link_column"))
      shareLinkButton {
        assertIsDisplayed()
        performClick()
      }
      composeTestRule
          .onNodeWithTag("setting_lazy_column", useUnmergedTree = true)
          .assertExists()
          .performScrollToNode(hasTestTag("share_link_text"))
      val link = "studybuddiesJoinGroup=TestGroup1/groupTest1"
      shareLinkTextField {
        assertIsDisplayed()
        assertTextContains(link)
      }
    }
  }

  @Test
  fun clickOnSave() {
    ComposeScreen.onComposeScreen<GroupSettingScreen>(composeTestRule) {
      composeTestRule
          .onNodeWithTag("setting_lazy_column", useUnmergedTree = true)
          .assertExists()
          .performScrollToNode(hasTestTag("save_button"))
      saveButton {
        assertIsDisplayed()
        performClick()
      }
    }
  }
}
