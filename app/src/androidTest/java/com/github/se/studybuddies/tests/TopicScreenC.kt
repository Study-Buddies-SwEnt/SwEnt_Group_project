package com.github.se.studybuddies.tests

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.viewModels.TopicViewModel
import io.github.kakaocup.compose.node.element.KNode
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.github.se.studybuddies.ui.TopicCreaction

class TopicCreationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // Mocks for the ViewModel and NavigationActions
    private lateinit var topicViewModel: TopicViewModel
    private lateinit var navigationActions: NavigationActions

    @Before
    fun setup() {
        // Initialize mocks
        topicViewModel = mockk(relaxed = true)
        navigationActions = mockk(relaxed = true)

        // Set the Composable content
        composeTestRule.setContent {
            TopicCreaction(topicViewModel, navigationActions)
        }
    }

    @Test
    fun testTopicCreationUI() {
        // Check if the input field is displayed
        composeTestRule.onNodeWithTag("create_topic_column").assertIsDisplayed()
        composeTestRule.onNodeWithText("Enter Topic Name").assertIsDisplayed()

        // Check if the 'Save Topic' button is displayed and interactable
        composeTestRule.onNodeWithText("Save Topic").assertIsDisplayed().assertHasClickAction()

        // Simulate text input
        composeTestRule.onNodeWithText("Topic Name").performTextInput("New Topic")
        composeTestRule.onNodeWithText("New Topic").assertTextEquals("New Topic")

        // Optionally, check if clicking the button triggers ViewModel function
        composeTestRule.onNodeWithText("Save Topic").performClick()
        // Verification that the ViewModel received the call can be done if ViewModel is a mock
        io.mockk.verify { topicViewModel.createTopic("New Topic") }
    }
}