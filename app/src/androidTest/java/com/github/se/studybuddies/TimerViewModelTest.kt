import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.se.studybuddies.viewModels.TimerViewModel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TimerViewModelTest {

  private lateinit var viewModel: TimerViewModel

  @Before
  fun setUp() {
    viewModel = TimerViewModel.getInstance()
    viewModel.resetTimer()
  }

  @Test
  fun addMinutes_increasesTimerByExpectedMinutes() {
    InstrumentationRegistry.getInstrumentation().runOnMainSync {
      viewModel.resetTimer()
      viewModel.addMinutes(1) // 1 minute
      assertEquals(60000L, viewModel.timerValue.value)
    }
  }

  @Test
  fun `add_hours`() = runBlockingTest {
    InstrumentationRegistry.getInstrumentation().runOnMainSync {
      viewModel.resetTimer()
      viewModel.addHours(1) // 1 minute
      assertEquals(3600000L, viewModel.timerValue.value)
    }
  }

  @Test
  fun `starting_timer`() = runBlockingTest {
    viewModel.startTimer()
    assertTrue(viewModel.isRunning)
  }

  @Test
  fun reset_timer() = runBlocking {
    InstrumentationRegistry.getInstrumentation().runOnMainSync {
      viewModel.addMinutes(5)
      viewModel.startTimer()
      viewModel.resetTimer()
      assertEquals(0L, viewModel.timerValue.value)
      assertFalse(viewModel.isRunning)
    }
  }
}
