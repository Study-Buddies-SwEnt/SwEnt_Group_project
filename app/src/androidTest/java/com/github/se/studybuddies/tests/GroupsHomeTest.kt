package com.github.se.studybuddies.tests

// ***************************************************************************** //
// ***                                                                       *** //
// *** THIS FILE WILL BE OVERWRITTEN DURING GRADING. IT SHOULD BE LOCATED IN *** //
// *** `app/src/androidTest/java/com/github/se/bootcamp/authentication/`.    *** //
// *** DO **NOT** IMPLEMENT YOUR OWN TESTS IN THIS FILE                      *** //
// ***                                                                       *** //
// ***************************************************************************** //

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.screens.GroupsHomeList
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
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
      errorSnackbar {
        assertIsDisplayed()
      }
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
      successSnackbar {
        assertIsDisplayed()
      }
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
  fun groupListElementDisplay() {
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      groupList {
        assertIsDisplayed()
      }
      testGroup1Box {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  @Test
  fun groupList(){
    ComposeScreen.onComposeScreen<GroupsHomeList>(composeTestRule) {
      testGroup1Box {
        assertIsDisplayed()
        assertHasClickAction()
      }
    }
  }

  @Test
  fun clickOnGroup() {
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
        testGroup1Box {
          assertIsDisplayed()
          assertHasClickAction()
          performClick()
        }
      verify { mockNavActions.navigateTo("${Route.GROUP}/groupTest1") }
      confirmVerified(mockNavActions)
    }
  }
}

