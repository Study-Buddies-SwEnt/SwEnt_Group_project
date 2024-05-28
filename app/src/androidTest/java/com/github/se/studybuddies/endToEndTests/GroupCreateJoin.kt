package com.github.se.studybuddies.endToEndTests

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
import com.github.se.studybuddies.screens.AccountSettingsScreen
import com.github.se.studybuddies.screens.CreateAccountScreen
import com.github.se.studybuddies.screens.CreateGroupScreen
import com.github.se.studybuddies.screens.GroupsHomeScreen
import com.github.se.studybuddies.screens.LoginScreen
import com.github.se.studybuddies.screens.SoloStudyScreen
import com.github.se.studybuddies.testUtilities.MockMainActivity
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.junit4.MockKRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroupCreateJoin : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @Before
  fun setUp() {
    ActivityScenario.launch(MockMainActivity::class.java)
  }

  @Test
  fun userFlow1() {
    composeTestRule.waitForIdle()
    ComposeScreen.onComposeScreen<CreateAccountScreen>(composeTestRule) {
      usernameField {
        performTextClearance()
        performTextInput("test user")
        assertTextContains("test user")
      }
    }
    Espresso.closeSoftKeyboard()
    composeTestRule
        .onNodeWithTag("accountLazyColumn")
        .performScrollToNode(hasTestTag("save_button_account"))
        .performClick()
    composeTestRule.onNodeWithTag("save_button_account").performClick()
    ComposeScreen.onComposeScreen<SoloStudyScreen>(composeTestRule) {
      soloStudyScreen { assertIsDisplayed() }
      groupsBottom { performClick() }
    }

    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      addButton { performClick() }
    }
    ComposeScreen.onComposeScreen<CreateGroupScreen>(composeTestRule) {
      // Create a group
      groupField {
        performTextClearance()
        performTextInput("testGroup")
        assertTextContains("testGroup")
      }
      Espresso.closeSoftKeyboard()
      saveButton { performClick() }
    }
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      drawerMenuButton { performClick() }
      accountButton { performClick() }
    }
    ComposeScreen.onComposeScreen<AccountSettingsScreen>(composeTestRule) {
      signOutButton {
        assertIsEnabled()
        assertHasClickAction()
        performClick()
      }
    }
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      // Verify that we indeed went back to the login screen
      loginTitle {
        assertIsDisplayed()
        assertTextEquals("Study Buddies")
      }
    }
  }
}
