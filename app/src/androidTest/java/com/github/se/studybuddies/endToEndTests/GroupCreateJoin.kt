package com.github.se.studybuddies.endToEndTests

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.MainActivity
import com.github.se.studybuddies.database.MockDatabase
import com.github.se.studybuddies.database.ServiceLocator
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.junit4.MockKRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroupCreateJoin : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @Before
  fun setUp() {
    ServiceLocator.setMockDatabase(MockDatabase())
    ServiceLocator.setCurrentUserUID("E2EUserTest")
    ActivityScenario.launch(MainActivity::class.java)
  }

  @After
  fun tearDown() {
    ServiceLocator.reset()
  }

  /*@Test
  fun userFlow1() {
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
    ComposeScreen.onComposeScreen<SoloStudyScreen>(composeTestRule) {
      soloStudyScreen { assertIsDisplayed() }
      groupsBottom { performClick() }
    }

    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      addButton { performClick() }
    }
    ComposeScreen.onComposeScreen<CreateGroupScreen>(composeTestRule) {
      // Create a group
//      groupField {
//        performTextClearance()
//        val text = "testGroup"
//        performTextInput(text)
//        assertTextContains(text)
//      }
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
  }*/
}
