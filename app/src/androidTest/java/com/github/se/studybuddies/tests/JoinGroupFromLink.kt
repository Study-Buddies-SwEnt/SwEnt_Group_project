import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JoinGroupFromLink {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testAddLinkButtonWrongLink() {

    // Perform click on the button
    composeTestRule.onNodeWithText("Create a task").performClick()

    // Perform text input in the TextField
    composeTestRule
        .onNodeWithText("Enter Link")
        .performTextInput(
            "https://studybuddies.page.link/JoinGroup/4nlAlJSZwjjdXrXTZ") // Is not an existing
                                                                          // group
  }

  fun testAddLinkButtonGoodLink() {

    // Perform click on the button
    composeTestRule.onNodeWithText("Create a task").performClick()

    // Perform text input in the TextField
    composeTestRule
        .onNodeWithText("Enter Link")
        .performTextInput(
            "https://studybuddies.page.link/JoinGroup/4nlAlCyONOiQIBKXrXTZ")
  // The link should work if it's not modified in the Firebase
  }
}
