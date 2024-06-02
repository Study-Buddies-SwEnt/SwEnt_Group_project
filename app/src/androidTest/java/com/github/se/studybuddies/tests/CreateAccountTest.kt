package com.github.se.studybuddies.tests

import android.net.Uri
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.screens.CreateAccountScreen
import com.github.se.studybuddies.ui.account.CreateAccount
import com.github.se.studybuddies.utility.fakeDatabase.MockDatabase
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
class CreateAccountTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private val uid = "userTest"
  private val db = MockDatabase()
  private var userVM = UserViewModel(uid, db)

  @Before
  fun testSetup() {
    composeTestRule.setContent { CreateAccount(userVM, mockNavActions) }
  }

  @Test
  fun elementsAreDisplayed() {
    ComposeScreen.onComposeScreen<CreateAccountScreen>(composeTestRule) {
      createAccountColumn { assertIsDisplayed() }
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
    ComposeScreen.onComposeScreen<CreateAccountScreen>(composeTestRule) {
      saveButton { assertIsNotEnabled() }
      val userTest = "test user"
      usernameField {
        performTextClearance()
        performTextInput(userTest)
        assertTextContains(userTest)
      }
      closeSoftKeyboard()
      saveButton {
        assertIsEnabled()
        performClick()
      }
    }
    verify { mockNavActions.navigateTo(Route.SOLOSTUDYHOME) }
    confirmVerified(mockNavActions)
  }

  @Test
  fun selectPicture() {
    ComposeScreen.onComposeScreen<CreateAccountScreen>(composeTestRule) {
      inputUsername()
      profileButton { performClick() }
      userVM.createUser(uid, "", "", Uri.EMPTY)
    }
  }
}
