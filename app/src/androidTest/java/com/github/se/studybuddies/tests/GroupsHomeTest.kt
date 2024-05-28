package com.github.se.studybuddies.tests

// ***************************************************************************** //
// ***                                                                       *** //
// *** THIS FILE WILL BE OVERWRITTEN DURING GRADING. IT SHOULD BE LOCATED IN *** //
// *** `app/src/androidTest/java/com/github/se/bootcamp/authentication/`.    *** //
// *** DO **NOT** IMPLEMENT YOUR OWN TESTS IN THIS FILE                      *** //
// ***                                                                       *** //
// ***************************************************************************** //

import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.screens.GroupsHomeScreen
import com.github.se.studybuddies.ui.groups.GroupsHome
import com.github.se.studybuddies.utility.fakeDatabase.MockDatabase
import com.github.se.studybuddies.viewModels.GroupsHomeViewModel
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
class AloneGroupsHomeTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)
  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  // userTest
  // aloneUserTest
  val uid = "userTest2"
  private val db = MockDatabase()

  @Before
  fun testSetup() {
    composeTestRule.setContent { GroupsHome(uid, GroupsHomeViewModel(uid, db), mockNavActions, db) }
  }

  @Test
  fun assessEmptyGroup() {
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      // As the tests don't have waiting time, the circular loading is never displayed
      groupBox { assertDoesNotExist() }
      circularLoading { assertDoesNotExist() }
      groupScreenEmpty { assertIsDisplayed() }
      emptyGroupText { assertIsDisplayed() }
    }
  }

  @Test
  fun buttonCorrectlyDisplay() {
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      addButtonRow { assertIsDisplayed() }
      addButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
      // addButtonIcon { assertExists() }

      addLinkRow { assertIsDisplayed() }
      addLinkButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
      // addLinkIcon { assertExists() }
    }
  }

  @Test
  fun buttonAreWorking() {
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      addButton {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
      verify { mockNavActions.navigateTo(Route.CREATEGROUP) }
      confirmVerified(mockNavActions)

      addLinkButton {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
      addLinkTextField {
        assertIsDisplayed()
        assertIsEnabled()
      }
    }
  }

  @Test
  fun enterWrongLink() {
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      addLinkButton {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
      addLinkTextField {
        assertIsDisplayed()
        assertIsEnabled()
        performTextInput("https://www.wronglink.com")
        performImeAction() // Simulate pressing the enter key
      }
      errorSnackbar { assertIsDisplayed() }
    }
  }

  @Test
  fun enterCorrectLink() {
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      addLinkButton {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
      val link = "studybuddiesJoinGroup=TestGroup1/groupTest1"
      addLinkTextField {
        assertIsDisplayed()
        assertIsEnabled()
        performTextInput(link)
        performImeAction() // Simulate pressing the enter key
      }
      successSnackbar { assertIsDisplayed() }
      verify { mockNavActions.navigateTo("${Route.GROUP}/groupTest1") }
      confirmVerified(mockNavActions)
    }
  }

  @Test
  fun testDrawerGroup() {
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      drawerScaffold { assertIsDisplayed() }
      groupsTitle {
        assertIsDisplayed()
        assertTextEquals("Groups")
      }
      topAppBox { assertIsDisplayed() }
      topAppBar { assertIsDisplayed() }
      drawerMenuButton {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
      drawerSheet { assertIsDisplayed() }
      settingsButton {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
      verify { mockNavActions.navigateTo("${Route.SETTINGS}/${Route.GROUPSHOME}") }
      confirmVerified(mockNavActions)

      drawerMenuButton {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
      accountButton {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
      verify { mockNavActions.navigateTo("${Route.ACCOUNT}/${Route.GROUPSHOME}") }
      confirmVerified(mockNavActions)
    }
  }

  @Test
  fun testBottomBarGroups() {
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      groupBottomBar { assertIsDisplayed() }

      soloStudyBottom {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
      verify { mockNavActions.navigateTo(Route.SOLOSTUDYHOME) }
      confirmVerified(mockNavActions)

      groupsBottom { assertIsDisplayed() }

      messagesBottom {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
      verify { mockNavActions.navigateTo(Route.DIRECT_MESSAGE) }
      confirmVerified(mockNavActions)

      mapBottom {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
      verify { mockNavActions.navigateTo(Route.MAP) }
      confirmVerified(mockNavActions)
    }
  }
}

@RunWith(AndroidJUnit4::class)
class GroupsHomeTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private val db = MockDatabase()

  // Use a user that have friends
  private val uid = "userTest1"
  private val groupHomeVM = GroupsHomeViewModel(uid, db)

  @Before
  fun testSetup() {
    composeTestRule.setContent { GroupsHome(uid, groupHomeVM, mockNavActions, db) }
  }

  @Test
  fun listGroupDisplayed() {
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      groupList { assertIsDisplayed() }
      testGroup1Box {
        assertIsDisplayed()
        assertHasClickAction()
      }
      /*
      composeTestRule.onNodeWithTag("groupTest1_row", useUnmergedTree = true).assertExists()
      composeTestRule.onNodeWithTag("groupTest1_box_picture", useUnmergedTree = true).assertExists()
      composeTestRule.onNodeWithTag("groupTest1_picture", useUnmergedTree = true).assertExists()
      composeTestRule
          .onNodeWithTag("groupTest1_text", useUnmergedTree = true)
          .assertExists()
          .assertTextContains("TestGroup1")*/
    }
  }

  @Test
  fun groupItemElementsDisplay() {
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      composeTestRule
          .onNodeWithTag("GroupsList", useUnmergedTree = true)
          .assertIsDisplayed()
          .performScrollToNode(hasTestTag("groupTest1_box"))
      /*
      composeTestRule
          .onNodeWithTag("groupTest1_settings_row", useUnmergedTree = true)
          .assertIsDisplayed()

      composeTestRule
          .onNodeWithTag("groupTest1_settings_button", useUnmergedTree = true)
          .assertIsDisplayed()
          .performClick()
      composeTestRule
          .onNodeWithTag("groupTest1_dropDownMenu", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("groupTest1_Modify group_item", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("groupTest1_Members_item", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("groupTest1_Leave group_item", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("groupTest1_Delete group_item", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("groupTest1_Modify group_text", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("Modify group")
      composeTestRule
          .onNodeWithTag("groupTest1_Members_text", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("Members")
      composeTestRule
          .onNodeWithTag("groupTest1_Leave group_text", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("Leave group")
      composeTestRule
          .onNodeWithTag("groupTest1_Delete group_text", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("Delete group")*/
    }
  }

  @Test
  fun ModifyGroup() {
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      composeTestRule
          .onNodeWithTag("groupTest1_settings_button", useUnmergedTree = true)
          .assertIsDisplayed()
          .performScrollTo()
          .performClick()
      composeTestRule
          .onNodeWithTag("groupTest1_Modify group_item", useUnmergedTree = true)
          .assertIsDisplayed()
          .performClick()
      verify { mockNavActions.navigateTo("GroupSetting/groupTest1") }
      confirmVerified(mockNavActions)
    }
  }

  @Test
  fun seeMembers() {
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      composeTestRule
          .onNodeWithTag("GroupsList", useUnmergedTree = true)
          .assertExists()
          .performScrollToNode(hasTestTag("groupTest1_box"))
      composeTestRule
          .onNodeWithTag("groupTest1_box", useUnmergedTree = true)
          .assertExists()
          .performScrollTo()
      composeTestRule
          .onNodeWithTag("groupTest1_settings_button", useUnmergedTree = true)
          .assertExists()
          .performClick()
      composeTestRule
          .onNodeWithTag("groupTest1_Members_item", useUnmergedTree = true)
          .assertExists()
          .performClick()
      verify { mockNavActions.navigateTo("${Route.GROUPMEMBERS}/groupTest1") }
      confirmVerified(mockNavActions)
    }
  }

  @Test
  fun leavingGroupDisplayed() {
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      composeTestRule
          .onNodeWithTag("groupTest1_settings_button", useUnmergedTree = true)
          .assertIsDisplayed()
          .performClick()
      /*
      composeTestRule
          .onNodeWithTag("groupTest1_Leave group_item", useUnmergedTree = true)
          .assertIsDisplayed()
          .performClick()
      composeTestRule
          .onNodeWithTag("groupTest1_leave_box", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("groupTest1_leave_column", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("groupTest1_leave_text", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("Are you sure you want to leave the group ?")
      composeTestRule
          .onNodeWithTag("groupTest1_leave_row", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("groupTest1_leave_yes_button", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("groupTest1_leave_yes_text", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("Yes")
      composeTestRule
          .onNodeWithTag("groupTest1_leave_no_button", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("groupTest1_leave_no_text", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("No")*/
    }
  }

  @Test
  fun leaveOptionsGroup() {
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      composeTestRule
          .onNodeWithTag("groupTest1_settings_button", useUnmergedTree = true)
          .assertExists()
          .performClick()
      composeTestRule
          .onNodeWithTag("groupTest1_Leave group_item", useUnmergedTree = true)
          .assertExists()
          .performClick()
      composeTestRule
          .onNodeWithTag("groupTest1_leave_yes_button", useUnmergedTree = true)
          .assertExists()
          .performClick()
      verify { mockNavActions.navigateTo(Route.GROUPSHOME) }
      confirmVerified(mockNavActions)
      composeTestRule
          .onNodeWithTag("groupTest1_settings_button", useUnmergedTree = true)
          .assertExists()
          .performClick()
      composeTestRule
          .onNodeWithTag("groupTest1_Leave group_item", useUnmergedTree = true)
          .assertExists()
          .performClick()
      composeTestRule
          .onNodeWithTag("groupTest1_leave_no_button", useUnmergedTree = true)
          .assertExists()
          .performClick()
    }
  }

  @Test
  fun deleteGroupDisplayed() {
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      composeTestRule.waitForIdle()
      composeTestRule
          .onNodeWithTag("GroupsList", useUnmergedTree = true)
          .assertIsDisplayed()
          .performScrollToNode(hasTestTag("groupTest1_box"))
          .performScrollToNode(hasTestTag("groupTest1_settings_button"))
      composeTestRule
          .onNodeWithTag("groupTest1_settings_button", useUnmergedTree = true)
          .assertIsDisplayed()
          .performClick()
      composeTestRule
          .onNodeWithTag("groupTest1_Delete group_item", useUnmergedTree = true)
          .assertIsDisplayed()
          .performClick()
      composeTestRule
          .onNodeWithTag("groupTest1_delete_box", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("groupTest1_delete_column", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("groupTest1_delete_text1", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("Are you sure you want to delete the group ?")
      composeTestRule
          .onNodeWithTag("groupTest1_delete_text2", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("This will delete the group and all its content for all members")
      composeTestRule
          .onNodeWithTag("groupTest1_delete_row", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("groupTest1_delete_yes_button", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("groupTest1_delete_yes_text", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("Yes")
      composeTestRule
          .onNodeWithTag("groupTest1_delete_no_button", useUnmergedTree = true)
          .assertIsDisplayed()
      composeTestRule
          .onNodeWithTag("groupTest1_delete_no_text", useUnmergedTree = true)
          .assertIsDisplayed()
          .assertTextContains("No")
    }
  }

  @Test
  fun deleteGroupOption() = run {
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      step("DeleteYes") {
        composeTestRule
            .onNodeWithTag("groupTest1_settings_button", useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()
        composeTestRule
            .onNodeWithTag("groupTest1_Delete group_item", useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()
        composeTestRule
            .onNodeWithTag("groupTest1_delete_yes_button", useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()
        verify { mockNavActions.navigateTo(Route.GROUPSHOME) }
        confirmVerified(mockNavActions)
      }
      step("DeleteNo") {
        composeTestRule
            .onNodeWithTag("groupTest1_settings_button", useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()
        composeTestRule
            .onNodeWithTag("groupTest1_Delete group_item", useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()
        composeTestRule
            .onNodeWithTag("groupTest1_delete_no_button", useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()
      }
    }
  }

  private fun printNodeTree(loc: String = "") {
    composeTestRule
        .onAllNodes(isRoot(), useUnmergedTree = true)
        .printToLog("Print root @${loc} : ", maxDepth = 10)
  }

  @Test
  fun clickOnGroup() {
    printNodeTree("Before")
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag("GroupsList", useUnmergedTree = true)
        .assertExists()
        .performScrollToNode(hasTestTag("groupTest1_box"))
    composeTestRule
        .onNodeWithTag("groupTest1_box", useUnmergedTree = true)
        .assertExists()
        .assertHasClickAction()
        .performClick()
    verify { mockNavActions.navigateTo("${Route.GROUP}/groupTest1") }
    confirmVerified(mockNavActions)
  }

  @Test
  fun clickOnGroup1() {
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      composeTestRule.onNodeWithTag("groupList", useUnmergedTree = true).assertExists()
    }
  }

  @Test
  fun Test() {
    composeTestRule.waitForIdle()
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      composeTestRule.onNodeWithTag("GroupsHome", useUnmergedTree = true).assertExists()
    }
  }

  @Test
  fun Test1() {
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("GroupEmpty", useUnmergedTree = true).assertExists()
  }

  @Test
  fun Test2() {
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("GroupsBox", useUnmergedTree = true).assertExists()
  }

  @Test
  fun Test3() {
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("Groups_drawer_scaffold", useUnmergedTree = true).assertExists()
  }

  @Test
  fun Test4() {
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("Groups_menu", useUnmergedTree = true).assertExists()
  }
}
