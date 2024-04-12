
import com.github.se.studybuddies.viewModels.TimerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class TimerViewModelTest {

    private lateinit var viewModel: TimerViewModel

    @Before
    fun setup() {
        viewModel = TimerViewModel()
    }

    @Test
    fun add_seconds_increases_timer_correctly() = runBlocking {
        viewModel.addSeconds(10)
        assertEquals(10, viewModel.timer.value)
    }

    @Test
    fun add_minutes_increases_timer_correctly() = runBlocking {
        viewModel.addMinutes(1)
        assertEquals(60, viewModel.timer.value)
    }

    @Test
    fun add_hours_increases_timer_correctly() = runBlocking {
        viewModel.addHours(1)
        assertEquals(3600, viewModel.timer.value)
    }

    @Test
    fun reset_timer_sets_value_to_zero() = runBlocking {
        viewModel.addSeconds(150)
        viewModel.resetTimer()
        assertEquals(0, viewModel.timer.value)
    }
}
