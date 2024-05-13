package com.github.se.studybuddies.endToEndTests

import android.net.Uri
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.screens.LoginScreen
import com.github.se.studybuddies.ui.account.AccountSettings
import com.github.se.studybuddies.ui.account.CreateAccount
import com.github.se.studybuddies.ui.account.LoginScreen
import com.github.se.studybuddies.ui.groups.CreateGroup
import com.github.se.studybuddies.ui.groups.GroupsHome
import com.github.se.studybuddies.ui.settings.Settings
import com.github.se.studybuddies.ui.solo_study.SoloStudyHome
import com.github.se.studybuddies.ui.theme.StudyBuddiesTheme
import com.github.se.studybuddies.viewModels.GroupViewModel
import com.github.se.studybuddies.viewModels.GroupsHomeViewModel
import com.github.se.studybuddies.viewModels.UserViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.junit4.MockKRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroupCreateJoin : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  lateinit var navigationActions: NavigationActions

  // Use the userTest created manually in the database
  private val uid = "userTest"
  private val userTest =
      User(
          uid = uid,
          email = "test@gmail.com",
          username = "testUser",
          photoUrl =
              Uri.parse("https://images.pexels.com/photos/6031345/pexels-photo-6031345.jpeg"),
          location = R.string.offline.toString())
  private val userVM = UserViewModel(uid)
  private val groupHomeVM = GroupsHomeViewModel(uid)
  private val groupVM = GroupViewModel(uid)

  @Before
  fun testSetup() {
    composeTestRule.setContent {
      StudyBuddiesTheme {
        val navController = rememberNavController()
        navigationActions = NavigationActions(navController)
        val startDestination = Route.CREATEACCOUNT
        NavHost(navController = navController, startDestination = startDestination) {
          /*composable(Route.START) {
            if (auth.currentUser != null) {
              db.userExists(
                  uid = db.getCurrentUserUID(),
                  onSuccess = { userExists ->
                    if (userExists) {
                      navController.navigate(Route.SOLOSTUDYHOME)
                    } else {
                      navController.navigate(Route.CREATEACCOUNT)
                    }
                  },
                  onFailure = { navController.navigate(Route.SOLOSTUDYHOME) })
            } else {
              navController.navigate(Route.CREATEACCOUNT)
            }
          }*/
          composable(Route.LOGIN) { LoginScreen(navigationActions) }
          composable(Route.CREATEACCOUNT) { CreateAccount(userVM, navigationActions) }
          composable(Route.SOLOSTUDYHOME) { SoloStudyHome(navigationActions) }
          composable(
              route = "${Route.SETTINGS}/{backRoute}",
              arguments = listOf(navArgument("backRoute") { type = NavType.StringType })) {
                  backStackEntry ->
                val backRoute = backStackEntry.arguments?.getString("backRoute")
                if (backRoute != null) {
                  Settings(backRoute, navigationActions)
                }
              }
          composable(
              route = "${Route.ACCOUNT}/{backRoute}",
              arguments = listOf(navArgument("backRoute") { type = NavType.StringType })) {
                  backStackEntry ->
                val backRoute = backStackEntry.arguments?.getString("backRoute")
                if (backRoute != null) {
                  AccountSettings(uid, userVM, backRoute, navigationActions)
                }
              }
          composable(Route.GROUPSHOME) { GroupsHome(uid, groupHomeVM, navigationActions) }
          composable(Route.CREATEGROUP) { CreateGroup(groupVM, navigationActions) }
        }
      }
    }
  }

  @Test
  fun groupCreateJoin() {
    ComposeScreen.onComposeScreen<com.github.se.studybuddies.screens.CreateAccountScreen>(
        composeTestRule) {
          // Create account
          saveButton { assertIsNotEnabled() }
          usernameField {
            performTextClearance()
            performTextInput("UserTest")
            assertTextContains("UserTest")
          }
          Espresso.closeSoftKeyboard()
          // saveButton { performClick() }
        }
    // Go the settings
    /*ComposeScreen.onComposeScreen<SoloStudyScreen>(composeTestRule) {
      soloStudyScreen { assertIsDisplayed() }
      groupsBottom { performClick() }
    }
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      addButton { performClick() }
    }
    ComposeScreen.onComposeScreen<CreateGroupScreen>(composeTestRule) {
      // Create a group
      groupField {
        performTextClearance()
        performTextInput("testGroup")
        assertTextContains("testGroup")
      }
      Espresso.closeSoftKeyboard()
      // saveButton { performClick() }
      goBackButton { performClick() }
    }
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      drawerMenuButton { performClick() }
      accountButton { performClick() }
    }
    ComposeScreen.onComposeScreen<AccountSettingsScreen>(composeTestRule) {
      signOutButton {
        assertIsEnabled()
        assertHasClickAction()
        performClick()
      }
    }
    ComposeScreen.onComposeScreen<LoginScreen>(composeTestRule) {
      // Verify that we indeed went back to the login screen
      loginTitle {
        assertIsDisplayed()
        assertTextEquals("Study Buddies")
      }
    }*/
  }
}
