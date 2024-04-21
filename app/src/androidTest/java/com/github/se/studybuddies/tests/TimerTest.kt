package com.github.se.studybuddies.tests

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.screens.TimerScreen
import com.github.se.studybuddies.ui.timer.TimerScreenContent
import com.github.se.studybuddies.viewModels.TimerViewModel
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
class TimerTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Before
  fun testSetup() {
    val vm = TimerViewModel()
    composeTestRule.setContent { TimerScreenContent(vm, mockNavActions) }
  }

  /*
  @Test
  fun timerAdjustmentButton() = run {
      onComposeScreen<TimerScreen>(composeTestRule) {
          timerAdjustmentButton{
              // arrange: verify pre-conditions
              assertIsDisplayed()
              performClick()
          }
      }
  }*/

  @Test
  fun timerButton() = run {
    onComposeScreen<TimerScreen>(composeTestRule) {
      timerButton {
        // arrange: verify pre-conditions
        assertIsDisplayed()
        performClick()
      }
    }
  }

  fun timerCard() = run {
    onComposeScreen<TimerScreen>(composeTestRule) {
      timerCard {
        // arrange: verify pre-conditions
        assertIsDisplayed()
      }
    }
  }
}
