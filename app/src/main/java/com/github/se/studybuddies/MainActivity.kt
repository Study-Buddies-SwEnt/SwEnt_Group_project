package com.github.se.studybuddies


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.se.studybuddies.ui.LoginScreen
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.groups.GroupsHome
import com.github.se.studybuddies.ui.settings.AccountSettings
import com.github.se.studybuddies.ui.settings.CreateAccount
import com.github.se.studybuddies.ui.theme.StudyBuddiesTheme
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
                    val startDestination = if (currentUser != null) {
                        Route.GROUPSHOME
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
                                GroupsHome(navigationActions)
                                Log.d("MyPrint", "Successfully navigated to GroupsHome")
                            }
                        }
                        composable(Route.ACCOUNT) {
                            if (currentUser != null) {
                                AccountSettings(currentUser.uid, UserViewModel(currentUser.uid), navigationActions)
                                Log.d("MyPrint", "Successfully navigated to AccountSettings")
                            }
                        }
                        composable(Route.CREATEACCOUNT) {
                            if (currentUser != null) {
                                CreateAccount(UserViewModel(), navigationActions)
                                Log.d("MyPrint", "Successfully navigated to CreateAccount")
                            }
                        }
                        /*
                        composable(Route.OVERVIEW) { Overview(overviewViewModel, navigationActions) }
                        composable(Route.CREATETODO) { CreateToDo(ToDoViewModel(), navigationActions) }
                        composable(
                            route = "${Route.EDITTODO}/{todoUID}",
                            arguments = listOf(navArgument("todoUID") { type = NavType.StringType })) {
                                backStackEntry ->
                            val todoUID = backStackEntry.arguments?.getString("todoUID")
                            if (todoUID != null) {
                                EditToDo(todoUID, ToDoViewModel(todoUID), navigationActions)
                            }
                        }
                        composable(Route.MAP) { MapView(overviewViewModel, navigationActions) }
                         */
                    }
                }
            }
        }
    }
}