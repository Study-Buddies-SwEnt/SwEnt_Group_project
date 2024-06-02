package com.github.se.studybuddies.endToEndTests

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.se.studybuddies.MainActivity
import com.github.se.studybuddies.R
import com.github.se.studybuddies.database.MockDatabase
import com.github.se.studybuddies.database.ServiceLocator
import com.github.se.studybuddies.screens.AccountSettingsScreen
import com.github.se.studybuddies.screens.CreateAccountScreen
import com.github.se.studybuddies.screens.CreateGroupScreen
import com.github.se.studybuddies.screens.GroupScreen
import com.github.se.studybuddies.screens.GroupSettingScreen
import com.github.se.studybuddies.screens.GroupsHomeScreen
import com.github.se.studybuddies.screens.LoginScreen
import com.github.se.studybuddies.screens.SoloStudyScreen
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.junit4.MockKRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroupCreateJoin : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val mockkRule = MockKRule(this)
  private val userUID = "E2EUserTest"

  @Before
  fun setUp() {
    ServiceLocator.setMockDatabase(MockDatabase())
    ServiceLocator.setCurrentUserUID(userUID)
    ActivityScenario.launch(MainActivity::class.java)
  }

  @After
  fun tearDown() {
    ServiceLocator.reset()
  }

  @Test
  fun userFlow1() {
    // Create a test user account
    ComposeScreen.onComposeScreen<CreateAccountScreen>(composeTestRule) {
      saveButton { assertIsNotEnabled() }
      usernameField {
        performTextClearance()
        val text = "test user"
        performTextInput(text)
        assertTextContains(text)
      }
      Espresso.closeSoftKeyboard()
      saveButton {
        assertIsEnabled()
        performClick()
      }
    }
    // Navigate to the group home screen
    ComposeScreen.onComposeScreen<SoloStudyScreen>(composeTestRule) {
      soloStudyScreen { assertIsDisplayed() }
      groupsBottom { performClick() }
    }
    // Click on the add group button
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      addButton { performClick() }
    }
    ComposeScreen.onComposeScreen<CreateGroupScreen>(composeTestRule) {
      // Create a group
      val groupName = "testGroup"
      composeTestRule.onNodeWithTag("group_name_field").performClick()
      composeTestRule.onNodeWithTag("group_name_field").performTextClearance()
      composeTestRule.onNodeWithTag("group_name_field").performTextInput(groupName)
      composeTestRule.onNodeWithTag("group_name_field").assertTextContains(groupName)
      /*
      groupField {
        performClick()
        performTextClearance()
        performTextInput(groupName)
        assertTextContains(groupName)
      }*/
      Espresso.closeSoftKeyboard()
      saveButton { performClick() }
    }
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      //click on a group
      composeTestRule.waitForIdle()
      composeTestRule
        .onNodeWithTag("GroupsList", useUnmergedTree = true)
        .performScrollToNode(hasTestTag("groupTest1_box"))
      composeTestRule
        .onNodeWithTag("groupTest1_box", useUnmergedTree = true)
        .performClick()
    }
    val groupUID = "groupTest1"
    ComposeScreen.onComposeScreen<GroupScreen>(composeTestRule) {
      //Open the settings of the group
      composeTestRule
        .onNodeWithTag(groupUID + "_settings_button", useUnmergedTree = true)
        .performClick()
      composeTestRule
        .onNodeWithTag(groupUID + "_Modify group_item", useUnmergedTree = true)
        .performClick()
    }
    ComposeScreen.onComposeScreen<GroupSettingScreen>(composeTestRule) {
      //Get the link of the group to share it with friends
      composeTestRule
        .onNodeWithTag("setting_lazy_column", useUnmergedTree = true)
        .performScrollToNode(hasTestTag("share_link_column"))
      shareLinkButton {
        performClick()
      }
      composeTestRule
        .onNodeWithTag("setting_lazy_column", useUnmergedTree = true)
        .performScrollToNode(hasTestTag("share_link_text"))
      shareLinkTextField {
        assertTextContains("studybuddiesJoinGroup=${groupUID}/${groupUID}")
      }
      goBackButton {
        // arrange: verify pre-conditions
        assertIsDisplayed()
        performClick()
      }
    }
    ComposeScreen.onComposeScreen<GroupScreen>(composeTestRule) {
      goBackButton {
        // arrange: verify pre-conditions
        assertIsDisplayed()
        performClick()
      }
    }
      ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      //Open the drawer menu and click on the account button
      drawerMenuButton { performClick() }
      accountButton { performClick() }
    }
    ComposeScreen.onComposeScreen<AccountSettingsScreen>(composeTestRule) {
      //Sign out
      signOutButton {
        assertIsEnabled()
        assertHasClickAction()
        performClick()
      }
      composeTestRule.waitForIdle()
    }
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      // Verify that we indeed went back to the login screen
      composeTestRule.waitForIdle()
      loginTitle {
        assertIsDisplayed()
        assertTextEquals(
            InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.app_name))
      }
    }
  }
}
