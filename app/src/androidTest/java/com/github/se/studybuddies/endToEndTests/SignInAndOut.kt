package com.github.se.studybuddies.endToEndTests

import android.net.Uri
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.se.studybuddies.MainActivity
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.database.DatabaseConnection
import com.github.se.studybuddies.mapService.LocationApp
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.screens.LoginScreen
import com.github.se.studybuddies.screens.SoloStudyScreen
import com.github.se.studybuddies.ui.account.CreateAccount
import com.github.se.studybuddies.ui.account.LoginScreen
import com.github.se.studybuddies.ui.settings.Settings
import com.github.se.studybuddies.ui.solo_study.SoloStudyHome
import com.github.se.studybuddies.ui.theme.StudyBuddiesTheme
import com.github.se.studybuddies.viewModels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SignInAndOut : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
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

    @Before
    fun testSetup() {
        var auth: FirebaseAuth
        auth = FirebaseAuth.getInstance()
        val db = DatabaseConnection()

        composeTestRule.setContent {
            StudyBuddiesTheme {
                val navController = rememberNavController()
                navigationActions = NavigationActions(navController)
                NavHost(
                    navController = navController,
                    startDestination = Route.CREATEACCOUNT
                ) {
                    composable(Route.CREATEACCOUNT) {
                        CreateAccount(UserViewModel(), navigationActions)
                    }
                    composable(Route.SOLOSTUDYHOME) {
                        if (auth.currentUser != null) {
                            Log.d("MyPrint", "Successfully navigated to SoloStudyHome")
                            SoloStudyHome(navigationActions)
                        }
                    }
                    composable(
                        route = "${Route.SETTINGS}/{backRoute}",
                        arguments = listOf(navArgument("backRoute") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val backRoute = backStackEntry.arguments?.getString("backRoute")
                        if (backRoute != null) {
                            Settings(backRoute, navigationActions)
                            Log.d("MyPrint", "Successfully navigated to Settings")
                        }
                    }
                }
            }
        }
    }



    @Test
    fun createAccount() {
        ComposeScreen.onComposeScreen<com.github.se.studybuddies.screens.CreateAccountScreen>(
            composeTestRule) {
            runBlocking { delay(6000) }
            saveButton { assertIsNotEnabled() }
            usernameField {
                performTextClearance()
                performTextInput("userTest")
                assertTextContains("userTest")
            }
            Espresso.closeSoftKeyboard()
            saveButton {
                performScrollTo()
                assertIsEnabled()
                performClick()
            }
        }
        ComposeScreen.onComposeScreen<SoloStudyScreen>(
            composeTestRule) {
            soloStudyScreen {
                assertIsDisplayed()
            }
        }

    }
}