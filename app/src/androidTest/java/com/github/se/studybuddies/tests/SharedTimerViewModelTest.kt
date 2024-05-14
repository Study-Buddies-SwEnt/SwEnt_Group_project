package com.github.se.studybuddies.tests

import com.github.se.studybuddies.database.DatabaseConnection
import com.github.se.studybuddies.viewModels.SharedTimerViewModel
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class SharedTimerViewModelTest {

  private lateinit var viewModel: SharedTimerViewModel
  private lateinit var databaseConnection: DatabaseConnection
  private val groupUID = "test-group"

  @Before
  fun setup() {
    // Mock the DatabaseConnection
    databaseConnection = mockk(relaxed = true)
    // Creating instance of the ViewModel with mocked database connection
    viewModel = SharedTimerViewModel.getInstance(groupUID)
  }

  @Test
  fun addSeconds_increases_timer_by_specified_seconds() = runBlockingTest {
    // Arrange
    val secondsToAdd = 10L
    val expectedMilliseconds = secondsToAdd * 1000
    viewModel.resetTimer()

    // Act
    viewModel.addSeconds(secondsToAdd)

    // Assert
    assertEquals(expectedMilliseconds, viewModel.timerValue.value)
  }

  @Test
  fun addMinutes_increases_timer_by_specified_minutes() = runBlockingTest {
    // Arrange
    val minutesToAdd = 1L
    val expectedMilliseconds = minutesToAdd * 60 * 1000
    viewModel.resetTimer()

    // Act
    viewModel.addMinutes(minutesToAdd)

    // Assert
    assertEquals(expectedMilliseconds, viewModel.timerValue.value)
  }

  @Test
  fun addHours_increases_timer_by_specified_hours() = runBlockingTest {
    // Arrange
    val hoursToAdd = 1L
    val expectedMilliseconds = hoursToAdd * 3600 * 1000
    viewModel.resetTimer()

    // Act
    viewModel.addHours(hoursToAdd)

    // Assert
    assertEquals(expectedMilliseconds, viewModel.timerValue.value)
  }

  @Test
  fun resetTimer_sets_value_to_zero() = runBlockingTest {
    // Arrange
    viewModel.addSeconds(150) // Set some random time

    // Act
    viewModel.resetTimer()

    // Assert
    assertEquals(0L, viewModel.timerValue.value)
  }
}
