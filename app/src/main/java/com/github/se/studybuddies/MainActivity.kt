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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.database.DbRepository
import com.github.se.studybuddies.database.ServiceLocator
import com.github.se.studybuddies.mapService.LocationApp
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.account.AccountSettings
import com.github.se.studybuddies.ui.account.CreateAccount
import com.github.se.studybuddies.ui.account.LoginScreen
import com.github.se.studybuddies.ui.calender.CalendarApp
import com.github.se.studybuddies.ui.calender.DailyPlannerScreen
import com.github.se.studybuddies.ui.chat.ChatScreen
import com.github.se.studybuddies.ui.chat.ContactScreen
import com.github.se.studybuddies.ui.chat.DirectMessageScreen
import com.github.se.studybuddies.ui.groups.CreateGroup
import com.github.se.studybuddies.ui.groups.GroupMembers
import com.github.se.studybuddies.ui.groups.GroupScreen
import com.github.se.studybuddies.ui.groups.GroupSetting
import com.github.se.studybuddies.ui.groups.GroupsHome
import com.github.se.studybuddies.ui.groups.MembersList
import com.github.se.studybuddies.ui.map.MapScreen
import com.github.se.studybuddies.ui.settings.Settings
import com.github.se.studybuddies.ui.shared_elements.Placeholder
import com.github.se.studybuddies.ui.solo_study.SoloStudyHome
import com.github.se.studybuddies.ui.theme.StudyBuddiesTheme
import com.github.se.studybuddies.ui.timer.SharedTimerScreen
import com.github.se.studybuddies.ui.timer.TimerScreenContent
import com.github.se.studybuddies.ui.todo.CreateToDo
import com.github.se.studybuddies.ui.todo.EditToDo
import com.github.se.studybuddies.ui.todo.ToDoListScreen
import com.github.se.studybuddies.ui.topics.TopicCreation
import com.github.se.studybuddies.ui.topics.TopicResources
import com.github.se.studybuddies.ui.topics.TopicScreen
import com.github.se.studybuddies.ui.topics.TopicSettings
import com.github.se.studybuddies.ui.video_call.CallLobbyScreen
import com.github.se.studybuddies.ui.video_call.CallState
import com.github.se.studybuddies.ui.video_call.StreamVideoInitHelper
import com.github.se.studybuddies.ui.video_call.VideoCallScreen
import com.github.se.studybuddies.viewModels.CalendarViewModel
import com.github.se.studybuddies.viewModels.CalendarViewModelFactory
import com.github.se.studybuddies.viewModels.CallLobbyViewModel
import com.github.se.studybuddies.viewModels.ChatViewModel
import com.github.se.studybuddies.viewModels.ContactsViewModel
import com.github.se.studybuddies.viewModels.DirectMessagesViewModel
import com.github.se.studybuddies.viewModels.GroupViewModel
import com.github.se.studybuddies.viewModels.GroupsHomeViewModel
import com.github.se.studybuddies.viewModels.MessageViewModel
import com.github.se.studybuddies.viewModels.SharedTimerViewModel
import com.github.se.studybuddies.viewModels.TimerViewModel
import com.github.se.studybuddies.viewModels.ToDoListViewModel
import com.github.se.studybuddies.viewModels.TopicFileViewModel
import com.github.se.studybuddies.viewModels.TopicViewModel
import com.github.se.studybuddies.viewModels.UserViewModel
import com.github.se.studybuddies.viewModels.UsersViewModel
import com.github.se.studybuddies.viewModels.VideoCallViewModel
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.StreamVideo

class MainActivity : ComponentActivity() {
  @SuppressLint("StateFlowValueCalledInComposition")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val db: DbRepository = ServiceLocator.provideDatabase()
    val directMessageViewModel = DirectMessagesViewModel(userUid = "", db = db)
    val usersViewModel = UsersViewModel(userUid = "", db = db)
    val chatViewModel = ChatViewModel()
    val userViewModel = UserViewModel()
    val contactsViewModel = ContactsViewModel()
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
              ifNotNullElse(remember { ServiceLocator.getCurrentUserUID() }, navController) {
                  currentUser ->
                db.userExists(
                    uid = db.getCurrentUserUID(),
                    onSuccess = { userExists ->
                      if (userExists) {
                        directMessageViewModel.setUserUID(currentUser)
                        usersViewModel.setUserUID(currentUser)
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
              LoginScreen(navigationActions) {}
            }

            composable(Route.GROUPSHOME) {
              LaunchedEffect(key1 = Unit) {
                if (ServiceLocator.getCurrentUserUID() != null && !StreamVideo.isInstalled) {
                  StreamVideoInitHelper.init(applicationContext)
                  StreamVideoInitHelper.loadSdk()
                  Log.d("MyPrint", "StreamVideo SDK is installed")
                }
                if (StreamVideo.isInstalled) {
                  StreamVideoInitHelper.reloadSdk()
                }
              }

              ifNotNull(remember { ServiceLocator.getCurrentUserUID() }) { currentUser ->
                val groupsHomeViewModel = remember { GroupsHomeViewModel(currentUser, db) }
                GroupsHome(currentUser, groupsHomeViewModel, navigationActions, db)
                Log.d("MyPrint", "Successfully navigated to GroupsHome")
              }
            }
            composable(Route.CALENDAR) {
              ifNotNull(remember { ServiceLocator.getCurrentUserUID() }) { _ ->
                val calendarViewModel = remember {
                  ServiceLocator.getCurrentUserUID()?.let { it1 -> CalendarViewModel(it1) }
                }
                if (calendarViewModel != null) {
                  CalendarApp(calendarViewModel, navigationActions)
                }
                Log.d("MyPrint", "Successfully navigated to Calendar")
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
                  val currentUser = ServiceLocator.getCurrentUserUID()
                  if (date != null && currentUser != null) {
                    val viewModelFactory = CalendarViewModelFactory(currentUser)
                    DailyPlannerScreen(date, viewModelFactory, navigationActions)
                    Log.d("MyPrint", "Successfully navigated to Daily Planner")
                  }
                }
            composable(
                route = "${Route.ACCOUNT}/{backRoute}",
                arguments = listOf(navArgument("backRoute") { type = NavType.StringType })) {
                    backStackEntry ->
                  val backRoute = backStackEntry.arguments?.getString("backRoute")
                  val currentUser = remember { ServiceLocator.getCurrentUserUID() }
                  if (backRoute != null && currentUser != null) {
                    val userViewModel = remember { UserViewModel(currentUser, db) }
                    AccountSettings(currentUser, userViewModel, backRoute, navigationActions)
                    Log.d("MyPrint", "Successfully navigated to Account")
                  }
                }
            composable(Route.CREATEACCOUNT) {
              ifNotNull(ServiceLocator.getCurrentUserUID()) { _ ->
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
              ifNotNull(ServiceLocator.getCurrentUserUID()) { _ ->
                val groupViewModel = remember { GroupViewModel(db = db) }
                CreateGroup(groupViewModel, navigationActions)
                Log.d("MyPrint", "Successfully navigated to CreateGroup")
              }
            }
            composable(Route.DIRECT_MESSAGE) {
              ifNotNull(remember { ServiceLocator.getCurrentUserUID() }) { currentUser ->
                directMessageViewModel.setUserUID(currentUser)
                usersViewModel.setUserUID(currentUser)
                DirectMessageScreen(
                    directMessageViewModel,
                    chatViewModel,
                    usersViewModel,
                    navigationActions,
                    ContactsViewModel(currentUser))
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
            composable(
                route = "${Route.GROUPMEMBERADD}/{groupUID}",
                arguments = listOf(navArgument("groupUID") { type = NavType.StringType })) {
                    backStackEntry ->
                  val groupUID = backStackEntry.arguments?.getString("groupUID")
                  ifNotNull(groupUID) { groupUid ->
                    val groupViewModel = remember { GroupViewModel(groupUID, db) }
                    MembersList(groupUid, groupViewModel, navigationActions, db)
                    Log.d("MyPrint", "Successfully navigated to MembersList")
                  }
                }
            composable(Route.CHAT) {
              val chat = remember { chatViewModel.getChat() ?: Chat.empty() }
              val messageViewModel = remember { MessageViewModel(chat) }
              ChatScreen(messageViewModel, navigationActions)
            }

            composable(Route.SOLOSTUDYHOME) {
              ifNotNull(ServiceLocator.getCurrentUserUID()) { _ ->
                Log.d("MyPrint", "Successfully navigated to SoloStudyHome")
                SoloStudyHome(navigationActions)
              }
            }

            composable(Route.TODOLIST) {
              ifNotNull(ServiceLocator.getCurrentUserUID()) { _ ->
                val toDoListViewModel = remember { ToDoListViewModel(studyBuddies) }
                ToDoListScreen(toDoListViewModel, navigationActions)
                Log.d("MyPrint", "Successfully navigated to ToDoList")
              }
            }

            composable(Route.CREATETODO) {
              ifNotNull(ServiceLocator.getCurrentUserUID()) { _ ->
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
                  ifNotNull(todoUID) { todoID ->
                    val toDoListViewModel = remember { ToDoListViewModel(studyBuddies) }
                    EditToDo(todoID, toDoListViewModel, navigationActions)
                    Log.d("MyPrint", "Successfully navigated to EditToDoScreen")
                  }
                }

            composable(
                route = "${Route.CONTACT_SETTINGS}/{contactID}",
                arguments = listOf(navArgument("contactID") { type = NavType.StringType })) {
                    backStackEntry ->
                  val contactID = backStackEntry.arguments?.getString("contactID")
                  val uid = db.getCurrentUserUID()
                  ifNotNull(contactID) { contactUID ->
                    val contactsVM = remember { ContactsViewModel(uid) }
                    val userVM = remember { UserViewModel(uid, db) }
                    val directMessageVM = remember { DirectMessagesViewModel(uid, db) }
                    ContactScreen(
                        contactUID, contactsVM, navigationActions, userVM, directMessageVM)
                    Log.d(
                        "MyPrint", "Successfully navigated to Contact Settings with ID $contactUID")
                  }
                }

            composable(Route.MAP) {
              ifNotNull(remember { ServiceLocator.getCurrentUserUID() }) { currentUser ->
                val userViewModel = remember { UserViewModel(currentUser, db) }
                MapScreen(
                    currentUser,
                    userViewModel,
                    usersViewModel,
                    navigationActions,
                    applicationContext)
              }
            }

            composable(Route.TIMER) {
              ifNotNull(ServiceLocator.getCurrentUserUID()) { _ ->
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
                    val viewModel: CallLobbyViewModel = viewModel {
                      CallLobbyViewModel(groupUID, callType)
                    }
                    val state = viewModel.callState
                    LaunchedEffect(key1 = state.isConnected) {
                      if (state.isConnected ||
                          StreamVideo.instance().state.activeCall.value?.id == groupUID) {
                        Log.d("MyPrint", "Joined same call")
                        navigationActions.navigateTo("${Route.VIDEOCALL}/$groupUID")
                      }
                    }
                    Log.d("MyPrint", "Join VideoCall lobby")
                    CallLobbyScreen(state, viewModel, viewModel::onAction, navigationActions)
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
                    val videoVM: VideoCallViewModel = viewModel {
                      VideoCallViewModel(callId, call, navigationActions)
                    }
                    val state = videoVM.callState
                    LaunchedEffect(key1 = state.callState) {
                      if (state.callState == CallState.ENDED) {
                        navController.popBackStack("${Route.GROUP}/$groupUID", false)
                        Log.d("MyPrint", "Successfully left the call")
                      }
                    }
                    VideoCallScreen(callId, state, videoVM::onAction, navigationActions)
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

            composable(
                route = "${Route.TOPICRESOURCES}/{topicFileID}",
                arguments = listOf(navArgument("topicFileID") { type = NavType.StringType })) {
                    backStackEntry ->
                  val topicFileID = backStackEntry.arguments?.getString("topicFileID")
                  if (topicFileID != null) {
                    val topicFileViewModel = remember { TopicFileViewModel(topicFileID, db) }
                    TopicResources(topicFileID, topicFileViewModel, navigationActions)
                    Log.d("MyPrint", "Successfully navigated to TopicResources")
                  }
                }

            composable(Route.PLACEHOLDER) {
              ifNotNull(remember { ServiceLocator.getCurrentUserUID() }) { _ ->
                Placeholder(navigationActions)
              }
            }
          }
        }
      }
    }
  }

  private fun startCall(activeCall: Call?, groupUID: String, callType: String) =
      if (activeCall != null) {
        if (activeCall.id != groupUID) {
          Log.w("CallActivity", "A call with id: $groupUID existed. Leaving.")
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
    val db = ServiceLocator.provideDatabase()
    val currentUser = ServiceLocator.getCurrentUserUID()
    // Set the user to offline when he closes the app
    if (currentUser != null) {
      val userViewModel = UserViewModel(currentUser, db)
      userViewModel.updateLocation(currentUser, "offline")
    }
  }
}
