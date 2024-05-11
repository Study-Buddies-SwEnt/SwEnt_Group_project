package com.github.se.studybuddies

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.database.DatabaseConnection
import com.github.se.studybuddies.mapService.LocationApp
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
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
import com.github.se.studybuddies.ui.video_call.CallLobbyScreen
import com.github.se.studybuddies.ui.video_call.StreamVideoInitHelper
import com.github.se.studybuddies.ui.video_call.VideoCallScreen
import com.github.se.studybuddies.viewModels.CallLobbyViewModel
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
import io.getstream.video.android.core.StreamVideo

class MainActivity : ComponentActivity() {
  private lateinit var auth: FirebaseAuth

  @SuppressLint("StateFlowValueCalledInComposition")
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
          val chatViewModel = ChatViewModel()
          val startDestination = Route.START

          NavHost(navController = navController, startDestination = startDestination) {
            composable(Route.START) {
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
                navController.navigate(Route.LOGIN)
              }
            }
            composable(Route.LOGIN) {
              Log.d("MyPrint", "Successfully navigated to LoginScreen")
              LoginScreen(navigationActions)
            }
            composable(Route.GROUPSHOME) {
              LaunchedEffect(key1 = Unit) {
                if (auth.currentUser != null && !StreamVideo.isInstalled) {
                  StreamVideoInitHelper.init(applicationContext)
                  StreamVideoInitHelper.loadSdk()
                  Log.d("MyPrint", "StreamVideo SDK is installed")
                }
                if (StreamVideo.isInstalled) {
                  StreamVideoInitHelper.reloadSdk()
                }
              }
              val currentUser = auth.currentUser
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
                DirectMessageScreen(
                    DirectMessageViewModel(currentUser.uid),
                    chatViewModel,
                    UsersViewModel(currentUser.uid),
                    navigationActions)
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
              ChatScreen(
                  MessageViewModel(chatViewModel.getChat() ?: Chat.empty()), navigationActions)
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

            composable(
                route = "${Route.CALLLOBBY}/{groupUID}",
                arguments = listOf(navArgument("groupUID") { type = NavType.StringType })) {
                    backStackEntry ->
                  val groupUID = backStackEntry.arguments?.getString("groupUID")
                  if (groupUID != null && StreamVideo.isInstalled) {
                    CallLobbyScreen(groupUID, CallLobbyViewModel(groupUID), navigationActions)
                  } else {
                    Log.d("MyPrint", "Failed bc video call client isn't installed")
                    navigationActions.navigateTo("${Route.GROUP}/$groupUID")
                  }
                }

            composable(
                route = "${Route.VIDEOCALL}/{groupUID}",
                arguments = listOf(navArgument("groupUID") { type = NavType.StringType })) {
                    backStackEntry ->
                  val groupUID = backStackEntry.arguments?.getString("groupUID")
                  val streamVideo = StreamVideo.instance()
                  val activeCall = streamVideo.state.activeCall.value
                  if (groupUID != null) {
                    val call =
                        if (activeCall != null) {
                          if (activeCall.id != groupUID) {
                            Log.w("CallActivity", "A call with id: ${groupUID} existed. Leaving.")
                            // If the call id is different leave the previous call
                            activeCall.leave()
                            // Return a new call
                            streamVideo.call("default", groupUID)
                          } else {
                            // Call ID is the same, use the active call
                            activeCall
                          }
                        } else {
                          // There is no active call, create new call
                          streamVideo.call("default", groupUID)
                        }
                    VideoCallScreen(
                        call,
                        VideoCallViewModel(call, groupUID),
                    ) {
                      StreamVideo.instance().state.activeCall.value?.leave()
                      call.leave()
                      navController.popBackStack("${Route.GROUP}/$groupUID", false)
                    }
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
