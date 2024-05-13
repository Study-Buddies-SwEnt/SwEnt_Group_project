package com.github.se.studybuddies.tests

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.screens.AccountSettingsScreen
import com.github.se.studybuddies.ui.account.AccountSettings
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
class AccountSettingsTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  val uid = "111testUser"
  val backRoute = Route.GROUPSHOME

  @Before
  fun testSetup() {
    val userVM = UserViewModel(uid)
    composeTestRule.setContent { AccountSettings(uid, userVM, backRoute, mockNavActions) }
  }

  @Test
  fun elementsAreDisplayed() {
    ComposeScreen.onComposeScreen<com.github.se.studybuddies.screens.AccountSettingsScreen>(
        composeTestRule) {
          runBlocking {
            delay(6000) // Adjust the delay time as needed
          }
          signOutButton { assertIsDisplayed() }
        }
  }

  @Test
  fun topAppBar() = run {
    ComposeScreen.onComposeScreen<AccountSettingsScreen>(composeTestRule) {
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
      subTitle {
        assertIsDisplayed()
        assertTextEquals("Profile settings")
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
  fun canSignOut() {
    ComposeScreen.onComposeScreen<com.github.se.studybuddies.screens.AccountSettingsScreen>(
        composeTestRule) {
          runBlocking { delay(6000) }
          signOutButton {
            assertIsEnabled()
            assertHasClickAction()
            performClick()
          }
        }
    verify { mockNavActions.navigateTo(Route.LOGIN) }
    confirmVerified(mockNavActions)
  }
}
