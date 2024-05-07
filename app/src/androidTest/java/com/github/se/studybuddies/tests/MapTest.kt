package com.github.se.studybuddies.tests

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
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

  @RelaxedMockK private lateinit var mockContext: Context

  private fun granted() {
    assert(true)
  }

  val uid = "111testUser"

  @Before
  fun setup() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val userVM = UserViewModel(uid)
    val usersVM = UsersViewModel(uid)

    composeTestRule.setContent {
      MapScreen(uid = uid, userVM, usersVM, navigationActions = mockNavActions, context = context)
    }
  }

  @Test
  fun mapIconIsDisplayed() {
    onComposeScreen<MapScreen>(composeTestRule) { mapIcon { assertIsDisplayed() } }
  }
}
