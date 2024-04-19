package com.github.se.studybuddies.tests

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.ui.groups.CreateGroup
import com.github.se.studybuddies.viewModels.GroupViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroupCreationTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Before
  fun testSetup() {
    val groupVM = GroupViewModel()
    composeTestRule.setContent { CreateGroup(groupVM, mockNavActions) }
  }

  @Test
  fun elementsAreDisplayed() {
    ComposeScreen.onComposeScreen<com.github.se.studybuddies.screens.CreateGroupScreen>(
        composeTestRule) {
          runBlocking {
            delay(6000) // Adjust the delay time as needed
          }
          content { assertIsDisplayed() }
          groupNameField { assertIsDisplayed() }
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
  fun inputGroupName() {
    ComposeScreen.onComposeScreen<com.github.se.studybuddies.screens.CreateGroupScreen>(
        composeTestRule) {
          runBlocking { delay(6000) }
          saveButton { assertIsNotEnabled() }
          groupNameField {
            performTextClearance()
            performTextInput("test group")
            assertTextContains("test group")
          }
          saveButton { assertIsEnabled() }
        }
  }
}
