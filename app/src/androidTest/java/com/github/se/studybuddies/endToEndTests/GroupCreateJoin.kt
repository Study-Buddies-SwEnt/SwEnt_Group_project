package com.github.se.studybuddies.endToEndTests

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.screens.CreateAccountScreen
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
    ComposeScreen.onComposeScreen<CreateAccountScreen>(composeTestRule) {
      saveButton { assertIsNotEnabled() }
      usernameField {
        performTextClearance()
        performTextInput("test user")
        assertTextContains("test user")
      }
      Espresso.closeSoftKeyboard()
      saveButton {
        performScrollTo()
        assertIsEnabled()
        performClick()
      }
    }
    /*
    ComposeScreen.onComposeScreen<CreateAccountScreen>(composeTestRule) {
      // Create account
      saveButton { assertIsNotEnabled() }
      usernameField {
        performTextClearance()
        performTextInput("E2EUserTest")
        assertTextContains("E2EUserTest")
      }
      Espresso.closeSoftKeyboard()
      composeTestRule.waitForIdle()
      saveButton {
        performScrollTo()
        assertIsEnabled()
        performClick()
      }
    }
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
    }*/
  }
}
