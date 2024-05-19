package com.github.se.studybuddies.tests

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.screens.CalendarScreen
import com.github.se.studybuddies.ui.calender.CalendarApp
import com.github.se.studybuddies.viewModels.CalendarViewModel
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
class CalendarAppTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions
  val uid = "111testUser"

  @Before
  fun testSetup() {
    val vm = CalendarViewModel(uid) // Consider using a mock or a test ViewModel instance
    composeTestRule.setContent { CalendarApp(viewModel = vm, navigationActions = mockNavActions) }
  }

  @Test
  fun navigationButtonsTest() = run {
    onComposeScreen<CalendarScreen>(composeTestRule) {
      previousMonthButton {
        assertIsDisplayed()
        performClick()
      }
      nextMonthButton {
        assertIsDisplayed()
        performClick()
      }
    }
  }

  @Test
  fun dateSelectionTest() = run {
    onComposeScreen<CalendarScreen>(composeTestRule) {
      dateButton("15").apply {
        assertIsDisplayed()
        performClick()
      }
    }
  }
}
