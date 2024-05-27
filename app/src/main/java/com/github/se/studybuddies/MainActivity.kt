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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.database.DatabaseConnection
import com.github.se.studybuddies.database.DbRepository
import com.github.se.studybuddies.mapService.LocationApp
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.Placeholder
import com.github.se.studybuddies.ui.account.AccountSettings
import com.github.se.studybuddies.ui.account.CreateAccount
import com.github.se.studybuddies.ui.account.LoginScreen
import com.github.se.studybuddies.ui.calender.CalendarApp
import com.github.se.studybuddies.ui.calender.DailyPlannerScreen
import com.github.se.studybuddies.ui.chat.ChatScreen
import com.github.se.studybuddies.ui.chat.DirectMessageScreen
import com.github.se.studybuddies.ui.groups.CreateGroup
import com.github.se.studybuddies.ui.groups.GroupMembers
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
import com.github.se.studybuddies.viewModels.CalendarViewModelFactory
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
import com.google.firebase.auth.FirebaseAuth
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.StreamVideo

open class MainActivity : ComponentActivity() {
  lateinit var auth: FirebaseAuth
  lateinit var db: DbRepository

  @SuppressLint("StateFlowValueCalledInComposition")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    db = DatabaseConnection()
    startApp(currentUser?.uid, db)
  }

  fun startApp(uid_: String?, db: DbRepository) {
    val directMessageViewModel = DirectMessageViewModel(userUid = "", db = db)
    val usersViewModel = UsersViewModel(userUid = "", db = db)
    val chatViewModel = ChatViewModel()

    val studyBuddies = application as LocationApp

    setContent {
      StudyBuddiesTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          val navController = rememberNavController()
          val navigationActions = NavigationActions(navController)
          val startDestination = Route.START

            var uid =  remember { mutableStateOf(uid_) }

            val callType = "default"

          NavHost(navController = navController, startDestination = startDestination) {
            composable(Route.START) {
              ifNotNullElse(remember { uid.value }, navController) { uid ->
                db.userExists(
                    uid = db.getCurrentUserUID(),
                    onSuccess = { userExists ->
                      if (userExists) {
                         directMessageViewModel.setUserUID(uid)
                         usersViewModel.setUserUID(uid)
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
              LoginScreen(navigationActions){ uid.value = db.getCurrentUserUID()}
            }

            composable(Route.GROUPSHOME) {
              LaunchedEffect(key1 = Unit) {
                if (uid != null && !StreamVideo.isInstalled) {
                  StreamVideoInitHelper.init(applicationContext)
                  StreamVideoInitHelper.loadSdk()
                  Log.d("MyPrint", "StreamVideo SDK is installed")
                }
                if (StreamVideo.isInstalled) {
                  StreamVideoInitHelper.reloadSdk()
                }
              }

              ifNotNull(remember { uid.value }) { uid ->
                val groupsHomeViewModel = remember { GroupsHomeViewModel(uid, db) }
                  GroupsHome(uid, groupsHomeViewModel, navigationActions, db)
                Log.d("MyPrint", "Successfully navigated to GroupsHome")
              }
            }
            composable(Route.CALENDAR) {
              ifNotNull(remember { uid.value }) { uid ->
                val calendarViewModel = remember { uid?.let { it1 -> CalendarViewModel(uid) } }
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
                  ifNotNull(groupUID) { groupUid ->
                    val groupViewModel = remember { GroupViewModel(groupUID, db) }
                    GroupScreen(groupUid, groupViewModel, chatViewModel, navigationActions, db)
                    Log.d("MyPrint", "Successfully navigated to GroupScreen")
                  }
                }
            composable(
                route = "${Route.SETTINGS}/{backRoute}",
                arguments = listOf(navArgument("backRoute") { type = NavType.StringType })) {
                    backStackEntry ->
                  val backRoute = backStackEntry.arguments?.getString("backRoute")
                  ifNotNull(backRoute) {
                    Settings(it, navigationActions)
                    Log.d("MyPrint", "Successfully navigated to Settings")
                  }
                }
            composable(
                route = "${Route.DAILYPLANNER}/{date}",
                arguments = listOf(navArgument("date") { type = NavType.StringType })) {
                    backStackEntry ->
                  val date = backStackEntry.arguments?.getString("date")
                  if (date != null && uid.value != null) {
                    val viewModelFactory = CalendarViewModelFactory(uid.value!!)
                    DailyPlannerScreen(date, viewModelFactory, navigationActions)
                    Log.d("MyPrint", "Successfully navigated to Daily Planner")
                  }
                }

            composable(
                route = "${Route.ACCOUNT}/{backRoute}",
                arguments = listOf(navArgument("backRoute") { type = NavType.StringType })) {
                    backStackEntry ->
                  val backRoute = backStackEntry.arguments?.getString("backRoute")
                  val currentUID = remember { uid.value }
                  if (backRoute != null && currentUID != null) {
                    val userViewModel = remember { UserViewModel(currentUID, db) }
                    AccountSettings(currentUID, userViewModel, backRoute, navigationActions)
                    Log.d("MyPrint", "Successfully navigated to Account")
                  }
                }
            composable(Route.CREATEACCOUNT) {
              ifNotNull(uid) { _ ->
                val userViewModel = remember { UserViewModel(db = db) }
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
                    val topicViewModel = remember { TopicViewModel(db = db) }
                    TopicCreation(groupUID, topicViewModel, navigationActions)
                    Log.d("MyPrint", "Successfully navigated to Creation of topic ")
                  }
                }
            composable(Route.CREATEGROUP) {
              ifNotNull(uid.value) { uid ->
                val groupViewModel = remember { GroupViewModel(db = db) }
                CreateGroup(groupViewModel, navigationActions)
                Log.d("MyPrint", "Successfully navigated to CreateGroup")
              }
            }
            composable(Route.DIRECT_MESSAGE) {
              ifNotNull(remember { uid.value }) { uid ->
                directMessageViewModel.setUserUID(uid)
                usersViewModel.setUserUID(uid)
                DirectMessageScreen(
                    directMessageViewModel,
                    chatViewModel,
                    usersViewModel,
                    navigationActions,
                    ContactsViewModel(uid))
              }
            }
            composable(
                route = "${Route.GROUPSETTING}/{groupUID}",
                arguments = listOf(navArgument("groupUID") { type = NavType.StringType })) {
                    backStackEntry ->
                  val groupUID = backStackEntry.arguments?.getString("groupUID")
                  ifNotNull(groupUID) { groupUid ->
                    val groupViewModel = remember { GroupViewModel(groupUID, db) }
                    GroupSetting(groupUid, groupViewModel, navigationActions, db)
                    Log.d("MyPrint", "Successfully navigated to GroupSetting")
                  }
                }
            composable(
                route = "${Route.GROUPMEMBERS}/{groupUID}",
                arguments = listOf(navArgument("groupUID") { type = NavType.StringType })) {
                    backStackEntry ->
                  val groupUID = backStackEntry.arguments?.getString("groupUID")
                  ifNotNull(groupUID) { groupUid ->
                    val groupViewModel = remember { GroupViewModel(groupUID, db) }
                    GroupMembers(groupUid, groupViewModel, navigationActions, db)
                    Log.d("MyPrint", "Successfully navigated to GroupMembers")
                  }
                }
            composable(Route.CHAT) {
              val chat = remember { chatViewModel.getChat() ?: Chat.empty() }
              val messageViewModel = remember { MessageViewModel(chat) }
              ChatScreen(messageViewModel, navigationActions)
            }
            composable(Route.SOLOSTUDYHOME) {
              ifNotNull(uid) { _ ->
                Log.d("MyPrint", "Successfully navigated to SoloStudyHome")
                SoloStudyHome(navigationActions)
              }
            }

            composable(Route.TODOLIST) {
              ifNotNull(uid) { _ ->
                val toDoListViewModel = remember { ToDoListViewModel(studyBuddies) }
                ToDoListScreen(toDoListViewModel, navigationActions)
                Log.d("MyPrint", "Successfully navigated to ToDoList")
              }
            }

            composable(Route.CREATETODO) {
              ifNotNull(uid) { _ ->
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
              ifNotNull(remember { uid.value }) { uid ->
                val userViewModel = remember { UserViewModel(uid, db) }
                val usersViewModel = remember { UsersViewModel(uid, db) }
                MapScreen(uid, userViewModel, usersViewModel, navigationActions, applicationContext)
              }
            }

            composable(Route.TIMER) {
              ifNotNull(uid) { _ ->
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
                    val viewModel: CallLobbyViewModel = remember {
                      CallLobbyViewModel(groupUID, callType)
                    }
                    Log.d("MyPrint", "Join VideoCall lobby")
                    CallLobbyScreen(groupUID, viewModel, navigationActions)
                  } else {
                    Log.d("MyPrint", "Failed bc video call client isn't installed")
                    navController.popBackStack("${Route.GROUP}/$groupUID", false)
                  }
                }

            composable(
                route = "${Route.VIDEOCALL}/{groupUID}",
                arguments = listOf(navArgument("groupUID") { type = NavType.StringType })) {
                    backStackEntry ->
                  val groupUID = backStackEntry.arguments?.getString("groupUID")
                  ifNotNull(groupUID) { callId ->
                    val call =
                        startCall(StreamVideo.instance().state.activeCall.value, callId, callType)
                    Log.d("MyPrint", "Join VideoCallScreen")
                    VideoCallScreen(
                        call,
                        { navigationActions.navigateTo("${Route.GROUP}/$callId") },
                        { leaveCall(call, navController, callId) })
                  }
                }

            composable(
                route = "${Route.SHAREDTIMER}/{groupUID}",
                arguments = listOf(navArgument("groupUID") { type = NavType.StringType })) {
                    backStackEntry ->
                  val groupUID = backStackEntry.arguments?.getString("groupUID")
                  ifNotNull(groupUID) { groupUid ->
                    val viewModel2 = remember { SharedTimerViewModel(groupUid, db) }
                    SharedTimerScreen(navigationActions, viewModel2, groupUid)
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
                    val groupViewModel = remember { GroupViewModel(groupUID, db) }
                    val topicViewModel = remember { TopicViewModel(topicUID, db) }
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
                    val topicViewModel = remember { TopicViewModel(topicUID, db) }
                    TopicSettings(topicUID, groupUID, topicViewModel, navigationActions)
                    Log.d("MyPrint", "Successfully navigated to TopicSettings")
                  }
                }

            composable(Route.PLACEHOLDER) {
              ifNotNull(remember { uid }) { _ -> Placeholder(navigationActions) }
            }
          }
        }
      }
    }
  }

  private fun leaveCall(call: Call, navController: NavHostController, groupUID: String) {
    StreamVideo.instance().state.activeCall.value?.leave()
    call.leave()
    navController.popBackStack("${Route.GROUP}/$groupUID", false)
  }

  private fun startCall(activeCall: Call?, groupUID: String, callType: String) =
      if (activeCall != null) {
        if (activeCall.id != groupUID) {
          Log.w("CallActivity", "A call with id: ${groupUID} existed. Leaving.")
          activeCall.leave()
          // Return a new call
          StreamVideo.instance().call(callType, groupUID)
        } else {
          // Call ID is the same, use the active call
          activeCall
        }
      } else {
        // There is no active call, create new call
        StreamVideo.instance().call(callType, groupUID)
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
      db = DatabaseConnection()
    auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    offlineLocation(currentUser?.uid, db)
  }
    fun offlineLocation(uid: String?, db: DbRepository) {
    val userViewModel = UserViewModel(uid, db)
    // Set the user to offline when he closes the app
    if (uid != null) {
      userViewModel.updateLocation(uid, "offline")
    }
  }
}
