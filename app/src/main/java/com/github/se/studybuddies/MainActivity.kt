package com.github.se.studybuddies

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.database.DatabaseConnection
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.DirectMessageScreen
import com.github.se.studybuddies.ui.groups.CreateGroup
import com.github.se.studybuddies.ui.groups.GroupScreen
import com.github.se.studybuddies.ui.groups.GroupsHome
import com.github.se.studybuddies.ui.map.MapScreen
import com.github.se.studybuddies.ui.screens.ChatScreen
import com.github.se.studybuddies.ui.screens.LoginScreen
import com.github.se.studybuddies.ui.screens.VideoCallScreen
import com.github.se.studybuddies.ui.settings.AccountSettings
import com.github.se.studybuddies.ui.settings.CreateAccount
import com.github.se.studybuddies.ui.settings.Settings
import com.github.se.studybuddies.ui.solo_study.SoloStudyHome
import com.github.se.studybuddies.ui.theme.StudyBuddiesTheme
import com.github.se.studybuddies.ui.timer.TimerScreenContent
import com.github.se.studybuddies.ui.topics.TopicCreation
import com.github.se.studybuddies.ui.topics.TopicScreen
import com.github.se.studybuddies.ui.topics.TopicSettings
import com.github.se.studybuddies.viewModels.ChatViewModel
import com.github.se.studybuddies.viewModels.DirectMessageViewModel
import com.github.se.studybuddies.viewModels.GroupViewModel
import com.github.se.studybuddies.viewModels.GroupsHomeViewModel
import com.github.se.studybuddies.viewModels.MessageViewModel
import com.github.se.studybuddies.viewModels.TimerViewModel
import com.github.se.studybuddies.viewModels.TopicViewModel
import com.github.se.studybuddies.viewModels.UserViewModel
import com.github.se.studybuddies.viewModels.VideoCallViewModel
import com.google.firebase.auth.FirebaseAuth
import io.getstream.video.android.core.GEO
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.model.User

class MainActivity : ComponentActivity() {
  private lateinit var auth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    auth = FirebaseAuth.getInstance()
    val db = DatabaseConnection()

    setContent {
      StudyBuddiesTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          val navController = rememberNavController()
          val navigationActions = NavigationActions(navController)
          val chatViewModel = ChatViewModel()
          val currentUser = auth.currentUser
          val startDestination =
              if (currentUser != null) {
                Route.SOLOSTUDYHOME
              } else {
                Route.LOGIN
              }
          val context = LocalContext.current
          val apiKey = "x52wgjq8qyfc"
          val test_apiKey = "mmhfdzb5evj2" // test
          val callID = "default_a0546550-933a-4aa8-b3f4-06cd068f998c" // test
          // val groupUID = "vMsJ8zIUDzwh" // test
          val test_token =
              "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiSm9ydXVzX0NfQmFvdGgiLCJpc3MiOiJodHRwczovL3Byb250by5nZXRzdHJlYW0uaW8iLCJzdWIiOiJ1c2VyL0pvcnV1c19DX0Jhb3RoIiwiaWF0IjoxNzE0NjUzOTg0LCJleHAiOjE3MTUyNTg3ODl9.WkUHrFvbIdfjqKIcxi4FQB6GmQB1q0uyQEAfJ61P_g0"
          LaunchedEffect(key1 = Unit) {
            if (currentUser != null) {
              StreamVideoBuilder(
                      context = context,
                      apiKey = apiKey, // demo API key
                      geo = GEO.GlobalEdgeNetwork,
                      user = User(id = db.getCurrentUser().username),
                      // token = StreamVideo.devToken(currentUser.uid))
                      token = test_token)
                  .build()
            }
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
                    GroupScreen(
                        groupUID, GroupViewModel(groupUID), chatViewModel, navigationActions)
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
            composable(
                route = "${Route.TOPICCREATION}/{groupUID}",
                arguments = listOf(navArgument("groupUID") { type = NavType.StringType })) {
                    backStackEntry ->
                  val groupUID = backStackEntry.arguments?.getString("groupUID")
                  if (groupUID != null) {
                    TopicCreation(groupUID, TopicViewModel(), navigationActions)
                    Log.d("MyPrint", "Successfully navigated to Creation of topic ")
                  }
                }
            composable(Route.CREATEGROUP) {
              if (currentUser != null) {
                CreateGroup(GroupViewModel(), navigationActions)
                Log.d("MyPrint", "Successfully navigated to CreateGroup")
              }
            }
            composable(Route.DIRECT_MESSAGE) {
              if (currentUser != null) {
                DirectMessageScreen(
                    DirectMessageViewModel(currentUser.uid), chatViewModel, navigationActions)
              }
            }
            composable(Route.CHAT) {
              ChatScreen(
                  MessageViewModel(chatViewModel.getChat() ?: Chat.empty()), navigationActions)
            }
            composable(Route.SOLOSTUDYHOME) {
              if (currentUser != null) {
                SoloStudyHome(navigationActions)
                Log.d("MyPrint", "Successfully navigated to SoloStudyHome")
              }
            }
            composable(Route.MAP) {
              if (currentUser != null) {
                MapScreen(currentUser.uid, navigationActions, applicationContext)
              }
            }
            composable(Route.TIMER) {
              if (currentUser != null) {
                TimerScreenContent(TimerViewModel(), navigationActions)
                Log.d("MyPrint", "Successfully navigated to TimerScreen")
              }
            }
            composable(Route.VIDEOCALL) {
              if (StreamVideo.isInstalled) {
                val call = StreamVideo.instance().call("default", callID)
                if (currentUser != null) {
                  VideoCallScreen(VideoCallViewModel(call, currentUser.uid), navigationActions)
                  Log.d("MyPrint", "Successfully navigated to VideoGroupScreen")
                }
              } else {
                navigationActions.goBack()
              }
            }
            composable(
                route = "${Route.TOPIC}/{topicUID}/{groupUID}",
                arguments =
                    listOf(
                        navArgument("topicUID") { type = NavType.StringType },
                        navArgument("groupUID") { type = NavType.StringType })) { backStackEntry ->
                  val topicUID = backStackEntry.arguments?.getString("topicUID")
                  val groupUID = backStackEntry.arguments?.getString("groupUID")
                  if (topicUID != null && groupUID != null) {
                    TopicScreen(groupUID, topicUID, TopicViewModel(topicUID), navigationActions)
                    Log.d("MyPrint", "Successfully navigated to TopicScreen")
                  }
                }
            composable(
                route = "${Route.TOPIC_SETTINGS}/{backRoute}/{topicUID}",
                arguments =
                    listOf(
                        navArgument("backRoute") { type = NavType.StringType },
                        navArgument("topicUID") { type = NavType.StringType })) { backStackEntry ->
                  val backRoute = backStackEntry.arguments?.getString("backRoute")
                  val topicUID = backStackEntry.arguments?.getString("topicUID")
                  if (backRoute != null && topicUID != null) {
                    TopicSettings(topicUID, TopicViewModel(topicUID), backRoute, navigationActions)
                    Log.d("MyPrint", "Successfully navigated to TopicSettings")
                  }
                }
          }
        }
      }
    }
  }
}
