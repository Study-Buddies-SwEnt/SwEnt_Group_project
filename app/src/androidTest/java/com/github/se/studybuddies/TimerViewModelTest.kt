import android.content.Context
import com.github.se.studybuddies.viewModels.TimerViewModel
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TimerViewModelTest {

  private lateinit var viewModel: TimerViewModel
  @RelaxedMockK private lateinit var mockContext: Context

  @Before
  fun setup() {
    viewModel = TimerViewModel(mockContext)
  }

  @Test
  fun add_sec() = runBlocking {
    viewModel.addSeconds(10)
    assertEquals(10, viewModel.timerValue.value)
  }

  @Test
  fun add_minut() = runBlocking {
    viewModel.addMinutes(1)
    assertEquals(60, viewModel.timerValue.value)
  }

  @Test
  fun increase() = runBlocking {
    viewModel.addHours(1)
    assertEquals(3600, viewModel.timerValue.value)
  }

  @Test
  fun reseT_timer_sets_value_to_zero() = runBlocking {
    viewModel.addSeconds(150)
    viewModel.resetTimer()
    assertEquals(0, viewModel.timerValue.value)
  }
}
