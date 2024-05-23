package com.github.se.studybuddies.tests

import android.app.Application
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.github.se.studybuddies.MainActivity
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.ui.todo.CreateToDo
import com.github.se.studybuddies.viewModels.ToDoListViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateToDoTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @get:Rule var activityRule = ActivityTestRule(MainActivity::class.java)

  private lateinit var toDoListViewModel: ToDoListViewModel
  private lateinit var application: Application

  @Before
  fun testSetup() {
    application =
        InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application
    toDoListViewModel = ToDoListViewModel(application)
    composeTestRule.setContent { CreateToDo(toDoListViewModel, mockNavActions) }
  }
}
  /*
    @Test
    fun topAppBarTest() = run {
      onComposeScreen<CreateToDoScreen>(composeTestRule) {
        topAppBox {
          // arrange: verify pre-conditions
          assertIsDisplayed()
        }
        topAppBar {
          // arrange: verify pre-conditions
          assertIsDisplayed()
        }
        divider {
          // arrange: verify pre-conditions
          assertIsDisplayed()
        }
        goBackButton {
          // arrange: verify pre-conditions
          assertIsDisplayed()
          performClick()
        }
      }
      // assert: the nav action has been called
      verify { mockNavActions.navigateTo(Route.TODOLIST) }
      confirmVerified(mockNavActions)
    }
  }
  /
     */

  /*
  @Test
  fun saveToDoDoesNotWorkWithEmptyTitle() = run {
    onComposeScreen<CreateToDoScreen>(composeTestRule) {
      step("Open todo screen") {
        inputTitle {
          assertIsDisplayed()

          // interact with the text field
          performClick()

          // assert that both the label and placeholder are correct
          assertTextContains("Title")
          assertTextContains("Name the task")

          // clear the text field
          performTextClearance()
        }

        saveButton {
          assertIsDisplayed()
          performClick()
        }

        // verify that the nav action has not been called
        verify { mockNavActions wasNot Called }
        confirmVerified(mockNavActions)
      }
    }
  }

   */
