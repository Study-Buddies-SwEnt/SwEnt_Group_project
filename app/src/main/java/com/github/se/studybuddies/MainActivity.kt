package com.github.se.studybuddies

import android.annotation.SuppressLint
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
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.se.studybuddies.calender.CalendarApp
import com.github.se.studybuddies.calender.DailyPlannerScreen
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
import com.github.se.studybuddies.ui.video_call.CallLobbyScreen
import com.github.se.studybuddies.ui.video_call.StreamVideoInitHelper
import com.github.se.studybuddies.ui.video_call.VideoCallScreen
import com.github.se.studybuddies.viewModels.CalendarViewModel
import com.github.se.studybuddies.viewModels.CallLobbyViewModel
import com.github.se.studybuddies.viewModels.ChatViewModel
import com.github.se.studybuddies.viewModels.ContactsViewModel
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
import javax.inject.Inject

class MainActivity : ComponentActivity() {
  private lateinit var auth: FirebaseAuth
  @Inject lateinit var locationApp: LocationApp
  private val chatViewModel: ChatViewModel by viewModels()
  private val usersViewModel: UsersViewModel by viewModels()
  private val directMessageViewModel: DirectMessageViewModel by viewModels()

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

          val startDestination = Route.START

          val callType = "default"


          NavHost(navController = navController, startDestination = startDestination) {
            composable(Route.START) {
              ifNotNullElse(remember { auth.currentUser }, navController) { currentUser ->
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

              ifNotNull(remember { auth.currentUser }) { currentUser ->
                val groupsHomeViewModel = remember { GroupsHomeViewModel(currentUser.uid) }
                GroupsHome(currentUser.uid, groupsHomeViewModel, navigationActions)
                Log.d("MyPrint", "Successfully navigated to GroupsHome")
              }
            }
            composable(Route.CALENDAR) {
              ifNotNull(remember { auth.currentUser }) { _ ->
                val calendarViewModel = remember {
                  auth.currentUser?.let { it1 -> CalendarViewModel(it1.uid) }
                }
                if (calendarViewModel != null) {
                  CalendarApp(calendarViewModel, navigationActions)
                }

                Log.d("MyPrint", "Successfully navigated to Clendar")
              }
            }
            composable(
                route = "${Route.GROUP}/{groupUID}",
                arguments = listOf(navArgument("groupUID") { type = NavType.StringType })) {
                    backStackEntry ->
                  val groupUID = backStackEntry.arguments?.getString("groupUID")
                  ifNotNull(groupUID) { groupUID ->
                    val groupViewModel = remember { GroupViewModel(groupUID) }
                    GroupScreen(groupUID, groupViewModel, chatViewModel, navigationActions)
                    Log.d("MyPrint", "Successfully navigated to GroupScreen")
                  }
                }
            composable(
                route = "${Route.SETTINGS}/{backRoute}",
                arguments = listOf(navArgument("backRoute") { type = NavType.StringType })) {
                    backStackEntry ->
                  val backRoute = backStackEntry.arguments?.getString("backRoute")
                  ifNotNull(backRoute) { backRoute ->
                    Settings(backRoute, navigationActions)
                    Log.d("MyPrint", "Successfully navigated to Settings")
                  }
                }
            composable(
                route = "${Route.DAILYPLANNER}/{date}",
                arguments = listOf(navArgument("date") { type = NavType.StringType })) {
                    backStackEntry ->
                  val date = backStackEntry.arguments?.getString("date")
                  val currentUser = auth.currentUser
                  if (date != null && currentUser != null) {
                    DailyPlannerScreen(date, CalendarViewModel(currentUser.uid), navigationActions)
                    Log.d("MyPrint", "Successfully navigated to Daily Planner")
                  }
                }
            composable(
                route = "${Route.ACCOUNT}/{backRoute}",
                arguments = listOf(navArgument("backRoute") { type = NavType.StringType })) {
                    backStackEntry ->
                  val backRoute = backStackEntry.arguments?.getString("backRoute")
                  val currentUser = remember { auth.currentUser }
                  if (backRoute != null && currentUser != null) {
                    val userViewModel = remember { UserViewModel(currentUser.uid) }
                    AccountSettings(currentUser.uid, userViewModel, backRoute, navigationActions)
                    Log.d("MyPrint", "Successfully navigated to Settings")
                  }
                }
            composable(Route.CREATEACCOUNT) {
              ifNotNull(auth.currentUser) { _ ->
                val userViewModel = remember { UserViewModel() }
                CreateAccount(userViewModel, navigationActions)
                Log.d("MyPrint", "Successfully navigated to CreateAccount")
              }
            }
            composable(
                route = "${Route.TOPICCREATION}/{groupUID}",
                arguments = listOf(navArgument("groupUID") { type = NavType.StringType })) {
                    backStackEntry ->
                  val groupUID = backStackEntry.arguments?.getString("groupUID")
                  if (groupUID != null) {
                    val topicViewModel = remember { TopicViewModel() }
                    TopicCreation(groupUID, topicViewModel, navigationActions)
                    Log.d("MyPrint", "Successfully navigated to Creation of topic ")
                  }
                }
            composable(Route.CREATEGROUP) {
              ifNotNull(auth.currentUser) { _ ->
                val groupViewModel = remember { GroupViewModel() }
                CreateGroup(groupViewModel, navigationActions)
                Log.d("MyPrint", "Successfully navigated to CreateGroup")
              }
            }
            composable(Route.DIRECT_MESSAGE) {
              ifNotNull(remember { auth.currentUser }) { currentUser ->
                directMessageViewModel.setUserUID(currentUser.uid)
                usersViewModel.setUserUID(currentUser.uid)
                DirectMessageScreen(
                    directMessageViewModel,
                    chatViewModel,
                    usersViewModel,
                    navigationActions,
                    ContactsViewModel(currentUser.uid))
              }
            }
            composable(
                route = "${Route.GROUPSETTING}/{groupUID}",
                arguments = listOf(navArgument("groupUID") { type = NavType.StringType })) {
                    backStackEntry ->
                  val groupUID = backStackEntry.arguments?.getString("groupUID")
                  ifNotNull(groupUID) { groupUID ->
                    val groupViewModel = remember { GroupViewModel(groupUID) }
                    GroupSetting(groupUID, groupViewModel, navigationActions)
                    Log.d("MyPrint", "Successfully navigated to GroupSetting")
                  }
                }
            composable(Route.CHAT) {
              val chat = remember { chatViewModel.getChat() ?: Chat.empty() }
              val messageViewModel = remember { MessageViewModel(chat) }
              ChatScreen(messageViewModel, navigationActions)
            }
            composable(Route.SOLOSTUDYHOME) {
              ifNotNull(auth.currentUser) { _ ->
                Log.d("MyPrint", "Successfully navigated to SoloStudyHome")
                SoloStudyHome(navigationActions)
              }
            }

            composable(Route.TODOLIST) {
              ifNotNull(auth.currentUser) { _ ->
                val toDoListViewModel = remember { ToDoListViewModel(studyBuddies) }
                ToDoListScreen(toDoListViewModel, navigationActions)
                Log.d("MyPrint", "Successfully navigated to ToDoList")
              }
            }

            composable(Route.CREATETODO) {
              ifNotNull(auth.currentUser) { _ ->
                val toDoListViewModel = remember { ToDoListViewModel(studyBuddies) }
                CreateToDo(toDoListViewModel, navigationActions)
                Log.d("MyPrint", "Successfully navigated to CreateToDo")
              }
            }

            composable(
                route = "${Route.EDITTODO}/{todoUID}",
                arguments = listOf(navArgument("todoUID") { type = NavType.StringType })) {
                    backStackEntry ->
                  val todoUID = backStackEntry.arguments?.getString("todoUID")
                  ifNotNull(todoUID) { todoUID ->
                    val toDoListViewModel = remember { ToDoListViewModel(studyBuddies) }
                    EditToDoScreen(todoUID, toDoListViewModel, navigationActions)
                    Log.d("MyPrint", "Successfully navigated to EditToDoScreen")
                  }
                }

            composable(Route.MAP) {
              ifNotNull(remember { auth.currentUser }) { currentUser ->
                val userViewModel = remember { UserViewModel(currentUser.uid) }
                val usersViewModel = remember { UsersViewModel(currentUser.uid) }
                MapScreen(
                    currentUser.uid,
                    userViewModel,
                    usersViewModel,
                    navigationActions,
                    applicationContext)
              }
            }

            composable(Route.TIMER) {
              ifNotNull(auth.currentUser) { _ ->
                val viewModel = remember { TimerViewModel.getInstance() }

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
                    val viewModel = remember { CallLobbyViewModel(groupUID, callType) }
                    Log.d("MyPrint", "Join VideoCall lobby")
                    CallLobbyScreen(groupUID, viewModel, navigationActions)
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
                            streamVideo.call(callType, groupUID)
                          } else {
                            // Call ID is the same, use the active call
                            activeCall
                          }
                        } else {
                          // There is no active call, create new call
                          streamVideo.call(callType, groupUID)
                        }
                    val videoVM = remember { VideoCallViewModel(call, groupUID) }
                    Log.d("MyPrint", "Join VideoCallScreen")
                    VideoCallScreen(
                        call,
                        videoVM,
                    ) {
                      videoVM.call.leave()
                      StreamVideo.instance().state.activeCall.value?.leave()
                      navController.popBackStack("${Route.GROUP}/$groupUID", false)
                    }
                  }
                }

            composable(
                route = "${Route.SHAREDTIMER}/{groupUID}",
                arguments = listOf(navArgument("groupUID") { type = NavType.StringType })) {
                    backStackEntry ->
                  val groupUID = backStackEntry.arguments?.getString("groupUID")
                  ifNotNull(groupUID) { groupUID ->
                    val viewModel2 = remember { SharedTimerViewModel.getInstance(groupUID) }
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
                    val groupViewModel = remember { GroupViewModel(groupUID) }
                    val topicViewModel = remember { TopicViewModel(topicUID) }
                    TopicScreen(
                        groupUID,
                        topicUID,
                        groupViewModel,
                        topicViewModel,
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
                    val topicViewModel = remember { TopicViewModel(topicUID) }
                    TopicSettings(topicUID, groupUID, topicViewModel, navigationActions)
                    Log.d("MyPrint", "Successfully navigated to TopicSettings")
                  }
                }

            composable(Route.PLACEHOLDER) {
              ifNotNull(remember { auth.currentUser }) { _ -> Placeholder(navigationActions) }
            }
          }
        }
      }
    }
  }

  private inline fun <T> ifNotNull(value: T?, action: (T) -> Unit) {
    if (value != null) {
      action(value)
    }
  }

  private inline fun <T> ifNotNullElse(
      value: T?,
      navController: NavHostController,
      action: (T) -> Unit
  ) {
    if (value != null) {
      action(value)
    } else {
      navController.navigate(Route.LOGIN)
    }
  }

  override fun onStop() {
    super.onStop()

    auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val userViewModel = UserViewModel(currentUser?.uid)
    // Set the user to offline when he closes the app
    if (currentUser != null) {
      userViewModel.updateLocation(currentUser.uid, "offline")
    }
  }
}
