package com.github.se.studybuddies.tests

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.screens.PlaceHolderScreen
import com.github.se.studybuddies.ui.shared_elements.Placeholder
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlaceHolderTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Before
  fun testSetup() {
    composeTestRule.setContent { Placeholder(mockNavActions) }
  }

  @Test
  fun placeHolderScreenDisplayed() = run {
    onComposeScreen<PlaceHolderScreen>(composeTestRule) {
      // Test the UI elements
      composeTestRule
          .onNodeWithTag("placeholder_column", useUnmergedTree = true)
          .assertIsDisplayed()

      composeTestRule
          .onNodeWithTag("placeholder_text", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextEquals("feature not implemented yet")
    }
  }

  @Test
  fun topAppBarTest() = run {
    onComposeScreen<PlaceHolderScreen>(composeTestRule) {
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
}
