package com.github.se.studybuddies

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.ChatScreen
import com.github.se.studybuddies.ui.LoginScreen
import com.github.se.studybuddies.ui.groups.CreateGroup
import com.github.se.studybuddies.ui.groups.GroupScreen
import com.github.se.studybuddies.ui.groups.GroupsHome
import com.github.se.studybuddies.ui.settings.AccountSettings
import com.github.se.studybuddies.ui.settings.CreateAccount
import com.github.se.studybuddies.ui.settings.Settings
import com.github.se.studybuddies.ui.solo_study.SoloStudyHome
import com.github.se.studybuddies.ui.theme.StudyBuddiesTheme
import com.github.se.studybuddies.ui.timer.TimerScreenContent
import com.github.se.studybuddies.utility.FirebaseUtils.checkIncomingDynamicLink
import com.github.se.studybuddies.viewModels.GroupViewModel
import com.github.se.studybuddies.viewModels.GroupsHomeViewModel
import com.github.se.studybuddies.viewModels.MessageViewModel
import com.github.se.studybuddies.viewModels.TimerViewModel
import com.github.se.studybuddies.viewModels.UserViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
  private lateinit var auth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    auth = FirebaseAuth.getInstance()

    setContent {
      StudyBuddiesTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          val navController = rememberNavController()
          val navigationActions = NavigationActions(navController)
          val currentUser = auth.currentUser
          val startDestination =
              if (currentUser != null) {
                Route.SOLOSTUDYHOME
              } else {
                Route.LOGIN
              }
          NavHost(navController = navController, startDestination = startDestination) {
            composable(Route.LOGIN) {
              LoginScreen(navigationActions)
              Log.d("MyPrint", "Successfully navigated to LoginScreen")
            }
            composable(Route.GROUPSHOME) {
              if (currentUser != null) {
                GroupsHome(currentUser.uid, GroupsHomeViewModel(currentUser.uid), navigationActions)
                Log.d("MyPrint", "Successfully navigated to GroupsHome")
              }
            }
            composable(
                route = "${Route.GROUP}/{groupUID}",
                arguments = listOf(navArgument("groupUID") { type = NavType.StringType })) {
                    backStackEntry ->
                  val groupUID = backStackEntry.arguments?.getString("groupUID")
                  if (groupUID != null) {
                    GroupScreen(groupUID, GroupViewModel(groupUID), navigationActions)
                    Log.d("MyPrint", "Successfully navigated to GroupScreen")
                  }
                }
            composable(
                route = "${Route.SETTINGS}/{backRoute}",
                arguments = listOf(navArgument("backRoute") { type = NavType.StringType })) {
                    backStackEntry ->
                  val backRoute = backStackEntry.arguments?.getString("backRoute")
                  if (backRoute != null) {
                    Settings(backRoute, navigationActions)
                    Log.d("MyPrint", "Successfully navigated to Settings")
                  }
                }
            composable(
                route = "${Route.ACCOUNT}/{backRoute}",
                arguments = listOf(navArgument("backRoute") { type = NavType.StringType })) {
                    backStackEntry ->
                  val backRoute = backStackEntry.arguments?.getString("backRoute")
                  if (backRoute != null && currentUser != null) {
                    AccountSettings(
                        currentUser.uid,
                        UserViewModel(currentUser.uid),
                        backRoute,
                        navigationActions)
                    Log.d("MyPrint", "Successfully navigated to Settings")
                  }
                }
            composable(Route.CREATEACCOUNT) {
              if (currentUser != null) {
                CreateAccount(UserViewModel(), navigationActions)
                Log.d("MyPrint", "Successfully navigated to CreateAccount")
              }
            }
            composable(Route.CREATEGROUP) {
              if (currentUser != null) {
                CreateGroup(GroupViewModel(), navigationActions)
                Log.d("MyPrint", "Successfully navigated to CreateGroup")
              }
            }
            composable(Route.CHAT) {
              if (currentUser != null) {
                ChatScreen(MessageViewModel("general_group"), navigationActions)
              }
            }
            composable(Route.SOLOSTUDYHOME) {
              if (currentUser != null) {
                SoloStudyHome(navigationActions)
                Log.d("MyPrint", "Successfully navigated to SoloStudyHome")
              }
            }
            composable(Route.TIMER) {
              if (currentUser != null) {
                TimerScreenContent(TimerViewModel(), navigationActions)
                Log.d("MyPrint", "Successfully navigated to TimerScreen")
              }
            }
          }
          // For the group invitation link
          checkIncomingDynamicLink(intent, this, navigationActions)
        }
      }
    }
  }
}
