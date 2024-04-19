package com.github.se.studybuddies.tests

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.settings.CreateAccount
import com.github.se.studybuddies.viewModels.UserViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountCreationTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  val uid = "111testUser"

  @Before
  fun testSetup() {
    val userVM = UserViewModel(uid)
    composeTestRule.setContent { CreateAccount(userVM, mockNavActions) }
  }

  @Test
  fun elementsAreDisplayed() {
    ComposeScreen.onComposeScreen<com.github.se.studybuddies.screens.CreateAccountScreen>(
        composeTestRule) {
          runBlocking {
            delay(6000) // Adjust the delay time as needed
          }
          content { assertIsDisplayed() }
          usernameField { assertIsDisplayed() }
          profileButton {
            assertIsDisplayed()
            assertHasClickAction()
          }
          saveButton {
            assertIsDisplayed()
            assertHasClickAction()
          }
        }
  }

  @Test
  fun inputUsername() {
    ComposeScreen.onComposeScreen<com.github.se.studybuddies.screens.CreateAccountScreen>(
        composeTestRule) {
          runBlocking { delay(6000) }
          saveButton { assertIsNotEnabled() }
          usernameField {
            performTextClearance()
            performTextInput("test user")
            assertTextContains("test user")
          }
          saveButton { assertIsEnabled() }
        }
  }
}
