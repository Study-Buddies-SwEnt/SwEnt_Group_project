package com.github.se.studybuddies.tests

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.screens.MapScreen
import com.github.se.studybuddies.ui.map.MapScreen
import com.github.se.studybuddies.viewModels.UserViewModel
import com.github.se.studybuddies.viewModels.UsersViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

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
  private val usersVM = UsersViewModel(uid)

  @Before
  fun setup() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    composeTestRule.setContent {
      MapScreen(uid = uid, userVM, usersVM, navigationActions = mockNavActions, context = context)
    }
  }

  @Test
  fun elementsAreDisplayed() {
    onComposeScreen<MapScreen>(composeTestRule) {
      loading { assertIsDisplayed() }
      mapIcon { assertIsDisplayed() }
      mapScreen { assertIsDisplayed() }
    }
  }
}

@RunWith(AndroidJUnit4::class)
class MapDatabase : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private val uid = "userTest"
  private val userVM = UserViewModel(uid)
  private val usersVM = UsersViewModel(uid)

  @Before
  fun setup() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    composeTestRule.setContent {
      MapScreen(uid = uid, userVM, usersVM, navigationActions = mockNavActions, context = context)
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun getUserFriends() {
    onComposeScreen<MapScreen>(composeTestRule) {
      // Now the friends list is not empty
      var friends = usersVM.friends.value
      assert(friends.isEmpty())
      // wait for the friends list to be updated
      runBlocking {
        delay(10000) // Adjust the delay time as needed
      }
      usersVM.fetchAllFriends(uid)
      friends = usersVM.friends.value
      // After the delay, the friends list should be finally retrieved
      assert(friends.isNotEmpty())
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun updateUserLocation() {
    onComposeScreen<MapScreen>(composeTestRule) {
      userVM.fetchUserData(uid)
      // Set the location of the user to online
      userVM.updateLocation(uid, "20.0,30.0")
      // Advance time to ensure that the update operation has completed
      runBlocking {
        delay(10000) // Adjust the delay time as needed
      }
      // Now the location of the user should be online
      var location = userVM.userData.value?.location
      assert(location == "20.0,30.0")
    }
  }
}
