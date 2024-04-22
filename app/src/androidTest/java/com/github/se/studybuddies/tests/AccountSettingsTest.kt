package com.github.se.studybuddies.tests

import android.net.Uri
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.screens.AccountSettingsScreen
import com.github.se.studybuddies.ui.settings.AccountSettings
import com.github.se.studybuddies.viewModels.UserViewModel
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
class AccountSettingsTest : TestCase() {

  @get:Rule val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @RelaxedMockK var fackUser = User("111testUser", "", "", Uri.EMPTY)

  @Before
  fun testSetup() {
    val vm = UserViewModel()
    composeTestRule.setContent {
      AccountSettings(fackUser.uid, vm, Route.SOLOSTUDYHOME, mockNavActions)
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
        assertTextEquals("Profile setting")
      }
      goBackButton {
        // arrange: verify pre-conditions
        assertIsDisplayed()
        performClick()
      }
    }
    // assert: the nav action has been called
    verify { mockNavActions.navigateTo(Route.SOLOSTUDYHOME) }
    confirmVerified(mockNavActions)
  }
}
