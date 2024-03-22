package com.github.se.studybuddies


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.se.studybuddies.ui.HomeScreen
import com.github.se.studybuddies.ui.LoginScreen
import com.github.se.studybuddies.ui.navigation.NavigationActions
import com.github.se.studybuddies.ui.navigation.Route
import com.github.se.studybuddies.ui.theme.StudyBuddiesTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    //private val overviewViewModel: OverviewViewModel by viewModels()

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
                        Route.HOME
                    } else {
                        Route.SIGNIN
                    }
                    NavHost(navController = navController, startDestination = startDestination) {
                        composable(Route.SIGNIN) { LoginScreen(navigationActions) }
                        composable(Route.HOME) { HomeScreen(navigationActions) }
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