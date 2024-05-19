package com.github.se.studybuddies.tests

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.action.ViewActions
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.bootcamp.screens.CreateToDoScreen
import com.github.se.studybuddies.mapService.LocationApp
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.screens.CreateGroupScreen
import com.github.se.studybuddies.ui.todo.CreateToDo
import com.github.se.studybuddies.viewModels.ToDoListViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.Called
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateToDoTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

    @get:Rule val composeTestRule = createComposeRule()

    // This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK, etc.
    @get:Rule val mockkRule = MockKRule(this)

    // Relaxed mocks methods have a default implementation returning values
    @RelaxedMockK lateinit var mockNavActions: NavigationActions

    @Before
    fun testSetup(app: LocationApp) {
        val vm = ToDoListViewModel(app)
        composeTestRule.setContent { CreateToDo(vm, mockNavActions) }
    }

    @Test
    fun inputToDoTitle() {
        onComposeScreen<CreateToDoScreen>(
            composeTestRule) {
            saveButton { assertIsNotEnabled() }
            todoFields {
                performTextClearance()
                performTextInput("Official ToDo Testing")
                assertTextContains("Official ToDo Testing")
            }
            ViewActions.closeSoftKeyboard()
              saveButton {
                assertIsEnabled()
                performClick()
              }
        }
    }

    @Test
    fun goBackButtonTriggersBackNavigation() = run {
        onComposeScreen<CreateToDoScreen>(composeTestRule) {
            goBackButton {
                // arrange: verify the pre-conditions
                assertIsDisplayed()
                assertIsEnabled()

                // act: go back !
                performClick()
            }
        }

        // assert: the nav action has been called
        verify { mockNavActions.goBack() }
        confirmVerified(mockNavActions)
    }

    @Test
    fun locationTextInput() = run {
        /**
         * Update 06/03/2024 : Adapted the tests to be more flexible
         *
         * Depending on your implementation, this test could fail. We expected you to utilize an
         * ExposedDropdownMenuBox instead of a Box for the location. We modified the tests to
         * accommodate both implementations. Make sure you have the latest version of
         * `CreateToDoScreen.kt` (if some KNode in your file do not compile, it very likely means that
         * you don't have the latest version)
         *
         * To accommodate both implementations, we included two test tags that are not present in the
         * Figma. While you can derive them from the screen, here is a brief guide for your convenience:
         * - locationDropDownMenuBox: this KNode represents the ExposedDropdownMenuBox or Box containing
         *   both the text field `inputLocation` and the drop down menu `locationDropDownMenu`
         * - inputLocation (already existed): this KNode represents the actual text field the user can
         *   input the location into
         * - locationDropDownMenu: this KNode represents the drop down menu containing Nominatim
         *   different propositions. It is a direct child of `locationDropDownMenuBox`
         * - inputLocationProposal (already existed): this KNode represents a singular Nominatim
         *   proposition. It is a direct child of `locationDropDownMenu`
         */
        onComposeScreen<com.github.se.bootcamp.screens.CreateToDoScreen>(composeTestRule) {
            step("Open todo screen") {
                inputLocation {
                    // arrange: verify pre-conditions
                    assertIsDisplayed()

                    // act: interact with the text field
                    performClick()

                    // assert: check that both the label and placeholder are correct
                    assertTextContains("Location")
                    assertTextContains("Enter an address")
                }
            }

            step("Change location") {
                // arrange: verify pre-conditions + enter search query
                inputLocation {
                    performTextClearance()

                    // input "ecole polytechnique federale" in the text input
                    // https://nominatim.openstreetmap.org/search?q=ecole%20polytechnique%20federale
                    performTextInput("ecole polytechnique federale")
                }

                // act: click on Nominatim's proposition
                inputLocationProposal { performClick() }

                // assert: check the suggestion box proposition
                inputLocation {
                    assertTextContains(value = "École Polytechnique Fédérale de Lausanne", substring = true)
                }
            }
        }
    }

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
