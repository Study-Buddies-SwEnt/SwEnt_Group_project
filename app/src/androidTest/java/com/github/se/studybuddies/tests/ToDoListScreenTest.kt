package com.github.se.studybuddies.tests

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.data.todo.ToDo
import com.github.se.studybuddies.data.todo.ToDoStatus
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.screens.GroupsHomeScreen
import com.github.se.studybuddies.screens.ToDoListScreen
import com.github.se.studybuddies.ui.groups.GroupsHome
import com.github.se.studybuddies.ui.todo.ToDoListScreen
import com.github.se.studybuddies.viewModels.GroupsHomeViewModel
import com.github.se.studybuddies.viewModels.ToDoListViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import java.time.LocalDate
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ToDoListScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

  @get:Rule
  val composeTestRule = createComposeRule()

  // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
  @get:Rule
  val mockkRule = MockKRule(this)

  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK
  lateinit var mockNavActions: NavigationActions
  private lateinit var toDoListViewModel: ToDoListViewModel

  private val testID = "testTodo1"
  private val testTask = ToDo(testID, "Name", LocalDate.now(), "Description", ToDoStatus.CREATED)

  @Before
  fun testSetup() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    toDoListViewModel = ToDoListViewModel(context)
    toDoListViewModel.addToDo(testTask)
    composeTestRule.setContent { ToDoListScreen(toDoListViewModel, mockNavActions) }
  }

  @Test
  fun topAppBarTest() = run {
    onComposeScreen<ToDoListScreen>(composeTestRule) {
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
    verify { mockNavActions.goBack() }
    confirmVerified(mockNavActions)
  }


  @Test
  fun elementsAreDisplayed() {
    onComposeScreen<ToDoListScreen>(composeTestRule) {
      addToDoButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
      todoListColumn {
        assertIsDisplayed()
      }
    }
  }

}

  @RunWith(AndroidJUnit4::class)
  class EmptyToDoListTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

    @get:Rule val composeTestRule = createComposeRule()

    // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
    @get:Rule val mockkRule = MockKRule(this)

    // Relaxed mocks methods have a default implementation returning values
    @RelaxedMockK lateinit var mockNavActions: NavigationActions
    private lateinit var toDoListViewModel: ToDoListViewModel

  @Before
  fun testSetup() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    toDoListViewModel = ToDoListViewModel(context)
    toDoListViewModel.addToDo(testTask)
    composeTestRule.setContent { ToDoListScreen(toDoListViewModel, mockNavActions) }
  }

  @Test
  fun assessEmptyGroup() {
    ComposeScreen.onComposeScreen<GroupsHomeScreen>(composeTestRule) {
      // As the tests don't have waiting time, the circular loading is never displayed
      groupBox { assertDoesNotExist() }
      circularLoading { assertDoesNotExist() }
      groupScreenEmpty { assertIsDisplayed() }
      emptyGroupText { assertIsDisplayed() }
    }
  }

}
