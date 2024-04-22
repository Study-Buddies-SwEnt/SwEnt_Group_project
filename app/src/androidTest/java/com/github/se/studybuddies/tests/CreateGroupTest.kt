package com.github.se.studybuddies.tests

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.screens.CreateGroupScreen
import com.github.se.studybuddies.ui.groups.CreateGroup
import com.github.se.studybuddies.viewModels.GroupViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.Called
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateGroupTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Before
  fun testSetup() {
    val vm = GroupViewModel()
    composeTestRule.setContent { CreateGroup(vm, mockNavActions) }
  }

  @Test
  fun nameGroupInput() = run {
    onComposeScreen<CreateGroupScreen>(composeTestRule) {
      step("Check group name") {
        groupField {
          // arrange: verify pre-conditions
          assertIsDisplayed()

          // act: interact with the text field
          performClick()

          // assert: check that both the label and placeholder are correct
          assertTextContains("Group Name")
          assertTextContains("Enter a group name")
        }
      }

      step("Enter group name") {
        // arrange: verify pre-conditions + enter search query
        groupField {
          performTextClearance()
          performTextInput("Test name")
        }

        // act: click on Nominatim's proposition
        groupFieldProposal { performClick() }

        // assert: check the suggestion box proposition
        groupField { assertTextContains(value = "Test name", substring = true) }
      }
    }
  }

  @Test
  fun saveToDoDoesNotWorkWithEmptyTitle() = run {
    onComposeScreen<CreateGroupScreen>(composeTestRule) {
      step("Open group screen") {
        groupField {
          assertIsDisplayed()

          // interact with the text field
          performClick()

          // clear the text field
          performTextClearance()
        }

        /*
        saveButtonText {
          assertIsDisplayed()
          assertTextEquals("Save")
        }*/

        // verify that the nav action has not been called
        verify { mockNavActions wasNot Called }
        confirmVerified(mockNavActions)
      }
    }
  }

  @Test
  fun topAppBarTest() = run {
    onComposeScreen<CreateGroupScreen>(composeTestRule) {
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
    verify { mockNavActions.navigateTo(Route.GROUPSHOME) }
    confirmVerified(mockNavActions)
  }
}
