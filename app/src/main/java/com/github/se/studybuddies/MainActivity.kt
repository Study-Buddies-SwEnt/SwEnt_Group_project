package com.github.se.studybuddies

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.se.studybuddies.calender.CalendarApp
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.database.DatabaseConnection
import com.github.se.studybuddies.mapService.LocationApp
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.Placeholder
import com.github.se.studybuddies.ui.account.AccountSettings
import com.github.se.studybuddies.ui.account.CreateAccount
import com.github.se.studybuddies.ui.account.LoginScreen
import com.github.se.studybuddies.ui.chat.ChatScreen
import com.github.se.studybuddies.ui.chat.DirectMessageScreen
import com.github.se.studybuddies.ui.groups.CreateGroup
import com.github.se.studybuddies.ui.groups.GroupScreen
import com.github.se.studybuddies.ui.groups.GroupSetting
import com.github.se.studybuddies.ui.groups.GroupsHome
import com.github.se.studybuddies.ui.map.MapScreen
import com.github.se.studybuddies.ui.settings.Settings
import com.github.se.studybuddies.ui.solo_study.SoloStudyHome
import com.github.se.studybuddies.ui.theme.StudyBuddiesTheme
import com.github.se.studybuddies.ui.timer.SharedTimerScreen
import com.github.se.studybuddies.ui.timer.TimerScreenContent
import com.github.se.studybuddies.ui.todo.CreateToDo
import com.github.se.studybuddies.ui.todo.EditToDoScreen
import com.github.se.studybuddies.ui.todo.ToDoListScreen
import com.github.se.studybuddies.ui.topics.TopicCreation
import com.github.se.studybuddies.ui.topics.TopicScreen
import com.github.se.studybuddies.ui.topics.TopicSettings
import com.github.se.studybuddies.ui.video_call.VideoCallScreen
import com.github.se.studybuddies.viewModels.CalendarViewModel
import com.github.se.studybuddies.viewModels.ChatViewModel
import com.github.se.studybuddies.viewModels.DirectMessageViewModel
import com.github.se.studybuddies.viewModels.GroupViewModel
import com.github.se.studybuddies.viewModels.GroupsHomeViewModel
import com.github.se.studybuddies.viewModels.MessageViewModel
import com.github.se.studybuddies.viewModels.SharedTimerViewModel
import com.github.se.studybuddies.viewModels.TimerViewModel
import com.github.se.studybuddies.viewModels.ToDoListViewModel
import com.github.se.studybuddies.viewModels.TopicViewModel
import com.github.se.studybuddies.viewModels.UserViewModel
import com.github.se.studybuddies.viewModels.UsersViewModel
import com.github.se.studybuddies.viewModels.VideoCallViewModel
import com.google.firebase.auth.FirebaseAuth
import io.getstream.video.android.core.GEO
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.model.User

class MainActivity : ComponentActivity() {
  private lateinit var auth: FirebaseAuth
  private val chatViewModel: ChatViewModel by viewModels()
  private val usersViewModel: UsersViewModel by viewModels()
  private val directMessageViewModel: DirectMessageViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    auth = FirebaseAuth.getInstance()
    val db = DatabaseConnection()

    val studyBuddies = application as LocationApp

    setContent {
      StudyBuddiesTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          val navController = rememberNavController()
          val navigationActions = NavigationActions(navController)

          val startDestination = Route.START
          val context = LocalContext.current
          val apiKey = "x52wgjq8qyfc"
          val test_apiKey = "mmhfdzb5evj2" // test
          val callID = "default_a0546550-933a-4aa8-b3f4-06cd068f998c" // test
          // val groupUID = "vMsJ8zIUDzwh" // test
          val test_token =
              "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiSm9ydXVzX0NfQmFvdGgiLCJpc3MiOiJodHRwczovL3Byb250by5nZXRzdHJlYW0uaW8iLCJzdWIiOiJ1c2VyL0pvcnV1c19DX0Jhb3RoIiwiaWF0IjoxNzE0NjUzOTg0LCJleHAiOjE3MTUyNTg3ODl9.WkUHrFvbIdfjqKIcxi4FQB6GmQB1q0uyQEAfJ61P_g0"
          LaunchedEffect(key1 = Unit) {
            if (auth.currentUser != null && !StreamVideo.isInstalled) {
              StreamVideoBuilder(
                      context = context,
                      apiKey = apiKey, // demo API key
                      geo = GEO.GlobalEdgeNetwork,
                      user = User(id = "Test"),
                      // token = StreamVideo.devToken(currentUser.uid))
                      token = test_token)
                  .build()
            }
          }
          NavHost(navController = navController, startDestination = startDestination) {
            composable(Route.START) {
              val currentUser = auth.currentUser
              if (currentUser != null) {
                db.userExists(
                    uid = db.getCurrentUserUID(),
                    onSuccess = { userExists ->
                      if (userExists) {
                        directMessageViewModel.setUserUID(currentUser.uid)
                        usersViewModel.setUserUID(currentUser.uid)
                        navController.navigate(Route.SOLOSTUDYHOME)
                      } else {
                        navController.navigate(Route.CREATEACCOUNT)
                      }
                    },
                    onFailure = { navController.navigate(Route.SOLOSTUDYHOME) })
              } else {
                navController.navigate(Route.LOGIN)
              }
            }
            composable(Route.LOGIN) {
              Log.d("MyPrint", "Successfully navigated to LoginScreen")
              LoginScreen(navigationActions)
            }
            composable(Route.GROUPSHOME) {
              val currentUser = auth.currentUser
              if (currentUser != null) {
                GroupsHome(currentUser.uid, GroupsHomeViewModel(currentUser.uid), navigationActions)
                Log.d("MyPrint", "Successfully navigated to GroupsHome")
              }
            }
            composable(Route.CALENDAR) {
              val currentUser = auth.currentUser
              if (currentUser != null) {
                CalendarApp(CalendarViewModel(), navigationActions)
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
                  val currentUser = auth.currentUser
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
              if (auth.currentUser != null) {
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
              if (auth.currentUser != null) {
                CreateGroup(GroupViewModel(), navigationActions)
                Log.d("MyPrint", "Successfully navigated to CreateGroup")
              }
            }
            composable(Route.DIRECT_MESSAGE) {
              val currentUser = auth.currentUser
              if (currentUser != null) {
                directMessageViewModel.setUserUID(currentUser.uid)
                usersViewModel.setUserUID(currentUser.uid)
                DirectMessageScreen(
                    directMessageViewModel, chatViewModel, usersViewModel, navigationActions)
              }
            }
            composable(
                route = "${Route.GROUPSETTING}/{groupUID}",
                arguments = listOf(navArgument("groupUID") { type = NavType.StringType })) {
                    backStackEntry ->
                  val groupUID = backStackEntry.arguments?.getString("groupUID")
                  if (groupUID != null) {
                    GroupSetting(groupUID, GroupViewModel(groupUID), navigationActions)
                    Log.d("MyPrint", "Successfully navigated to GroupSetting")
                  }
                }
            composable(Route.CHAT) {
              val chat = remember { chatViewModel.getChat() ?: Chat.empty() }
              val messageViewModel = remember { MessageViewModel(chat) }
              ChatScreen(messageViewModel, navigationActions)
            }
            composable(Route.SOLOSTUDYHOME) {
              if (auth.currentUser != null) {
                Log.d("MyPrint", "Successfully navigated to SoloStudyHome")
                SoloStudyHome(navigationActions)
              }
            }

            composable(Route.TODOLIST) {
              if (auth.currentUser != null) {
                ToDoListScreen(ToDoListViewModel(studyBuddies = studyBuddies), navigationActions)
                Log.d("MyPrint", "Successfully navigated to ToDoList")
              }
            }

            composable(Route.CREATETODO) {
              if (auth.currentUser != null) {
                CreateToDo(ToDoListViewModel(studyBuddies), navigationActions)
                Log.d("MyPrint", "Successfully navigated to CreateToDo")
              }
            }

            composable(
                route = "${Route.EDITTODO}/{todoUID}",
                arguments = listOf(navArgument("todoUID") { type = NavType.StringType })) {
                    backStackEntry ->
                  val todoUID = backStackEntry.arguments?.getString("todoUID")
                  if (todoUID != null) {
                    EditToDoScreen(todoUID, ToDoListViewModel(studyBuddies), navigationActions)
                    Log.d("MyPrint", "Successfully navigated to EditToDoScreen")
                  }
                }

            composable(Route.MAP) {
              val currentUser = auth.currentUser
              if (currentUser != null) {
                MapScreen(
                    currentUser.uid,
                    UserViewModel(currentUser.uid),
                    UsersViewModel(currentUser.uid),
                    navigationActions,
                    applicationContext)
              }
            }
            composable(Route.TIMER) {
              if (auth.currentUser != null) {

                val viewModel = TimerViewModel.getInstance()

                TimerScreenContent(viewModel, navigationActions = navigationActions)
                Log.d("MyPrint", "Successfully navigated to TimerScreen")
              }
            }
            composable(Route.VIDEOCALL) {
              if (StreamVideo.isInstalled) {
                val call = StreamVideo.instance().call("default", callID)
                val currentUser = auth.currentUser
                if (currentUser != null) {
                  VideoCallScreen(VideoCallViewModel(call, currentUser.uid), navigationActions)
                  Log.d("MyPrint", "Successfully navigated to VideoGroupScreen")
                }
              } else {
                navigationActions.goBack()
              }
            }
            composable(
                route = "${Route.SHAREDTIMER}/{groupUID}",
                arguments = listOf(navArgument("groupUID") { type = NavType.StringType })) {
                    backStackEntry ->
                  val groupUID = backStackEntry.arguments?.getString("groupUID")
                  if (groupUID != null) {
                    val viewModel2 = SharedTimerViewModel.getInstance(groupUID)

                    SharedTimerScreen(navigationActions, viewModel2, groupUID)
                    Log.d("MyPrint", "Successfully navigated to SharedTimer")
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
                    TopicScreen(
                        groupUID,
                        topicUID,
                        GroupViewModel(groupUID),
                        TopicViewModel(topicUID),
                        chatViewModel,
                        navigationActions)
                    Log.d("MyPrint", "Successfully navigated to TopicScreen")
                  }
                }
            composable(
                route = "${Route.TOPIC_SETTINGS}/{groupUID}/{topicUID}",
                arguments =
                    listOf(
                        navArgument("groupUID") { type = NavType.StringType },
                        navArgument("topicUID") { type = NavType.StringType })) { backStackEntry ->
                  val topicUID = backStackEntry.arguments?.getString("topicUID")
                  val groupUID = backStackEntry.arguments?.getString("groupUID")
                  if (topicUID != null && groupUID != null) {
                    TopicSettings(topicUID, groupUID, TopicViewModel(topicUID), navigationActions)
                    Log.d("MyPrint", "Successfully navigated to TopicSettings")
                  }
                }

            composable(Route.PLACEHOLDER) {
              val currentUser = auth.currentUser
              if (currentUser != null) {
                Placeholder(navigationActions)
              }
            }
          }
        }
      }
    }
  }

  override fun onStop() {
    super.onStop()
    auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val userViewModel = UserViewModel(currentUser?.uid)
    // Set the user to offline when he closes the app
    if (currentUser != null) {
      userViewModel.updateLocation(currentUser.uid, R.string.offline.toString())
    }
  }
}
