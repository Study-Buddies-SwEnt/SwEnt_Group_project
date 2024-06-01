package com.github.se.studybuddies.tests

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.database.MockDatabase
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.screens.AccountSettingsScreen
import com.github.se.studybuddies.ui.account.AccountSettings
import com.github.se.studybuddies.utilities.MockDatabase
import com.github.se.studybuddies.viewModels.UserViewModel
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
class AccountSettingsTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private val uid = "userTest"
  private val backRoute = Route.GROUPSHOME
  private val db = MockDatabase()

  @Before
  fun testSetup() {
    val userVM = UserViewModel(uid, db)
    composeTestRule.setContent { AccountSettings(uid, userVM, backRoute, mockNavActions) }
  }

  @Test
  fun elementsAreDisplayed() {
    ComposeScreen.onComposeScreen<AccountSettingsScreen>(composeTestRule) {
      columnAccountSetting { assertIsDisplayed() }
      spacer1 { assertIsDisplayed() }
      spacer2 { assertIsDisplayed() }
      spacer3 { assertIsDisplayed() }
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

  // Sign out test used to often fail the CI on git
  @Test
  fun signOut() {
    ComposeScreen.onComposeScreen<AccountSettingsScreen>(composeTestRule) {
      composeTestRule
          .onNodeWithTag("sign_out_button")
          .assertIsEnabled()
          .assertHasClickAction()
          .performClick()
      composeTestRule.waitForIdle()
      verify { mockNavActions.navigateTo(Route.LOGIN) }
      confirmVerified(mockNavActions)
    }
  }
}
