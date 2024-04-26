package com.github.se.studybuddies.tests

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.screens.TimerScreen
import com.github.se.studybuddies.viewModels.SharedTimerViewModel
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import junit.framework.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class TimerTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

    @get:Rule val composeTestRule = createComposeRule()

    // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
    @get:Rule
    val mockkRule = MockKRule(this)

    // Relaxed mocks methods have a default implementation returning values
    @RelaxedMockK
    lateinit var mockNavActions: NavigationActions

    @Before
    fun testSetup() {
        val vm = SharedTimerViewModel() // Changed to SharedTimerViewModel
        composeTestRule.setContent { TimerScreen("groupId", vm) } // Changed to TimerScreen
    }

    @Test
    fun timerAdjustmentButton() = run {
        onComposeScreen<TimerScreen> {
            plusMinuteButton { // Changed to plusMinuteButton
                // arrange: verify pre-conditions
                assertIsDisplayed()
                performClick()
            }
            minusMinuteButton { // Changed to minusMinuteButton
                // arrange: verify pre-conditions
                assertIsDisplayed()
                performClick()
            }
        }
    }

    @Test
    fun timerButton() = run {
        onComposeScreen<TimerScreen> {
            startTimerButton {
                // arrange: verify pre-conditions
                assertIsDisplayed()
                performClick()
            }
            resetTimerButton {
                // arrange: verify pre-conditions
                assertIsDisplayed()
                performClick()
            }
            pauseTimerButton {
                // arrange: verify pre-conditions
                assertIsDisplayed()
                performClick()
            }
        }
    }

    @Test
    fun timerCard() = run {
        onComposeScreen<TimerScreen> {
            timerCard {
                // arrange: verify pre-conditions
                assertIsDisplayed()
            }
        }
    }

    @Test
    fun topAppBar() = run {
        onComposeScreen<TimerScreen> {
            topAppBar { // Changed to topAppBar
                // arrange: verify pre-conditions
                assertIsDisplayed()
            }
        }
    }
}
