
package com.github.se.studybuddies.tests
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.ui.timer.SharedTimerScreen
import com.github.se.studybuddies.viewModels.SharedTimerViewModel
import org.junit.runner.RunWith
import io.mockk.mockk
import com.google.firebase.database.DatabaseReference
import junit.framework.TestCase

@RunWith(AndroidJUnit4::class)
class SharedTimerViewModelInteractionTest{
    @get:Rule
    val composeTestRule = createComposeRule()

    @RelaxedMockK
    lateinit var mockDbRef: DatabaseReference

    private lateinit var viewModel: SharedTimerViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        viewModel = SharedTimerViewModel("test_group")
        viewModel.dbRef = mockDbRef
        composeTestRule.setContent {
            SharedTimerScreen(navigationActions = mockk(relaxed = true), sharedTimerViewModel = viewModel)
        }
    }

    @Test
    fun timerInfoIsDisplayed() {
        composeTestRule.onNodeWithTag("sharedtimer_scaffold").assertIsDisplayed()

        composeTestRule.onNodeWithText("Start").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText("Pause").assertIsDisplayed().assertHasClickAction()
        composeTestRule.onNodeWithText("Reset").assertIsDisplayed().assertHasClickAction()
    }

    @Test
    fun addTimeAndStartTimer() {
        composeTestRule.onNodeWithText("timer_adjustment").performClick()
        verify { mockDbRef.setValue(any<SharedTimerViewModel.TimerInfo>()) }

        composeTestRule.onNodeWithText("Start").performClick()
        verify { mockDbRef.setValue(any<SharedTimerViewModel.TimerInfo>()) }
    }

    @Test
    fun resetTimer() {
        composeTestRule.onNodeWithText("Reset").performClick()
        verify { mockDbRef.setValue(SharedTimerViewModel.TimerInfo()) }
    }
}

/* composable(
   route = "${Route.SHAREDTIMER}/{groupUID}",
   arguments = listOf(navArgument("groupUID") { type = NavType.StringType })) {
     backStackEntry ->
   val groupUID = backStackEntry.arguments?.getString("groupUID")
   if (groupUID != null) {
     SharedTimerScreen( navigationActions,SharedTimerViewModel(groupUID))
     Log.d("MyPrint", "Successfully navigated to GroupScreen")
   }*/
