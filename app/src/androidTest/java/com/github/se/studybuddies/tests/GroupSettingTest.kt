package com.github.se.studybuddies.tests

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.screens.GroupSettingScreen
import com.github.se.studybuddies.ui.groups.GroupSetting
import com.github.se.studybuddies.utilities.MockDatabase
import com.github.se.studybuddies.viewModels.ChatViewModel
import com.github.se.studybuddies.viewModels.GroupViewModel
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
class GroupSettingTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  // userTest
  // aloneUserTest
  val groupUID = "groupTest1"
  private val db = MockDatabase()
  val groupVM = GroupViewModel(groupUID, db)
  val chatVM = ChatViewModel()

  @Before
  fun testSetup() {
    composeTestRule.setContent { GroupSetting(groupUID, groupVM, mockNavActions, db) }
  }

  @Test
  fun topAppBarTest() = run {
    ComposeScreen.onComposeScreen<GroupSettingScreen>(composeTestRule) {
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
    verify { mockNavActions.navigateTo("${Route.GROUP}/groupTest1") }
    confirmVerified(mockNavActions)
  }

  @Test
  fun elementAreDisplayed() {
    ComposeScreen.onComposeScreen<GroupSettingScreen>(composeTestRule) {
      settingColumn { assertIsDisplayed() }
      settingLazyColumn { assertIsDisplayed() }
      composeTestRule
          .onNodeWithTag("setting_lazy_column", useUnmergedTree = true)
          .assertExists()
          .performScrollToNode(hasTestTag("setting_spacer1"))
      spacer1 { assertExists() }
      composeTestRule
          .onNodeWithTag("setting_lazy_column", useUnmergedTree = true)
          .assertExists()
          .performScrollToNode(hasTestTag("setting_spacer2"))
      spacer2 { assertExists() }
      composeTestRule
          .onNodeWithTag("setting_lazy_column", useUnmergedTree = true)
          .assertExists()
          .performScrollToNode(hasTestTag("setting_spacer3"))
      spacer3 { assertExists() }
      composeTestRule
          .onNodeWithTag("setting_lazy_column", useUnmergedTree = true)
          .assertExists()
          .performScrollToNode(hasTestTag("setting_spacer4"))
      spacer4 { assertExists() }
      val groupName = "test group"
      modifyName {
        assertIsDisplayed()
        performTextClearance()
        performTextInput(groupName)
        assertTextContains(groupName)
      }
      Espresso.closeSoftKeyboard()
      imagePP { assertIsDisplayed() }
      spacerPP { assertExists() }
      buttonPP { assertIsDisplayed() }

      addMemberColumn { assertIsDisplayed() }
      addMemberButton { assertIsDisplayed() }

      // composeTestRule.onNodeWithTag("setting_lazy_column", useUnmergedTree =
      // true).assertIsDisplayed().performScrollToNode(hasTestTag("setting_spacer1"))
      // composeTestRule.onNodeWithTag("setting_spacer1", useUnmergedTree = true).assertExists()
      // composeTestRule.onNodeWithTag("setting_spacer2", useUnmergedTree =
      // true).assertIsDisplayed()
      // composeTestRule.onNodeWithTag("setting_spacer3", useUnmergedTree =
      // true).assertIsDisplayed()
      // composeTestRule.onNodeWithTag("setting_spacer4", useUnmergedTree =
      // true).assertIsDisplayed()

    }
  }
}
