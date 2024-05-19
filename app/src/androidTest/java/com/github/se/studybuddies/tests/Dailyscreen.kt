package com.github.se.studybuddies.tests

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.data.DailyPlanner
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.ui.calender.DailyPlannerScreen
import com.github.se.studybuddies.viewModels.CalendarViewModel
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DailyPlannerScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private lateinit var viewModel: CalendarViewModel

  @Before
  fun setUp() {
    MockKAnnotations.init(this)
    viewModel = CalendarViewModel("testUID")

    // Mock some data to ensure the planner has data to display
    val mockPlanner =
        DailyPlanner(
            date = "2024-05-19",
            goals = listOf("Mock Goal 1", "Mock Goal 2"),
            appointments = mapOf("10:00" to "Appointment 1", "14:00" to "Appointment 2"),
            notes = listOf("Mock Note 1", "Mock Note 2"))
    viewModel.updateDailyPlanner("2024-05-19", mockPlanner)
  }

  @Test
  fun testDailyPlannerScreenDisplays() {
    // Setting up the Composable in the test environment
    composeTestRule.setContent {
      DailyPlannerScreen(
          date = "2024-05-19", viewModel = viewModel, navigationActions = mockNavActions)
    }

    // Assertions to verify that sections are displayed
    composeTestRule.onNodeWithText("Today's Goals").assertIsDisplayed()
    composeTestRule.onNodeWithText("Notes").assertIsDisplayed()
    composeTestRule.onNodeWithText("Appointments").assertIsDisplayed()
    composeTestRule.onNodeWithTag("SaveButton").assertIsDisplayed()
  }
}
