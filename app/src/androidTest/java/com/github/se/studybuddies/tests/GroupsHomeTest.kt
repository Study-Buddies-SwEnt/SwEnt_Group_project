package com.github.se.studybuddies.tests

// ***************************************************************************** //
// ***                                                                       *** //
// *** THIS FILE WILL BE OVERWRITTEN DURING GRADING. IT SHOULD BE LOCATED IN *** //
// *** `app/src/androidTest/java/com/github/se/bootcamp/authentication/`.    *** //
// *** DO **NOT** IMPLEMENT YOUR OWN TESTS IN THIS FILE                      *** //
// ***                                                                       *** //
// ***************************************************************************** //

import androidx.compose.ui.test.junit4.createComposeRule
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
  val uid = "aloneUserTest"
  private val db = MockDatabase()

  @Before
  fun testSetup() {
    composeTestRule.setContent { GroupsHome(uid, GroupsHomeViewModel(uid, db), mockNavActions, db) }
  }

  /*
  @Test
  fun assessEmptyGroup() {
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      groupBox { assertIsDisplayed() }
      circularLoading { assertIsDisplayed() }
      Thread.sleep(4000)
      groupScreenEmpty { assertIsDisplayed() }
      emptyGroupText { assertIsDisplayed() }
    }
  }

  @Test
  fun buttonCorrectlyDisplay() {
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      Thread.sleep(4000)
      addButtonRow { assertIsDisplayed() }
      addButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
      // addButtonIcon { assertIsDisplayed() }

      addLinkRow { assertIsDisplayed() }
      addLinkButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
      // addLinkIcon { assertIsDisplayed() }
    }
  }

  @Test
  fun buttonAreWorking() {
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      Thread.sleep(4000)
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
    }
  }*/

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
  private val uid = "cYD8bTcYDyX3ngzssP22GYedcsh2"
  private val groupHomeVM = GroupsHomeViewModel(uid, db)

  @Before
  fun testSetup() {
    composeTestRule.setContent { GroupsHome(uid, groupHomeVM, mockNavActions, db) }
  }

  @Test
  fun groupList() {
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      groupBox { assertIsDisplayed() }
      circularLoading { assertIsDisplayed() }
      // runBlocking { delay(4000) }
      // groupScreen { assertIsDisplayed() }
      // addButton { assertIsDisplayed() }
      // addLinkButton { assertIsDisplayed() }
    }
  }
}
