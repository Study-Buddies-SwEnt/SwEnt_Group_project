import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.navigation.NavigationActions
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JoinGroupFromLinkTest {

  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  /*@Test
  fun testAddLinkButtonWrongLink() {

    composeTestRule.setContent { AddLinkButton(mockNavActions) }

    // Perform click on the button
    composeTestRule.onNodeWithText("Create a task").performClick()

    // Perform text input in the TextField
    composeTestRule
        .onNodeWithText("Enter Link")
        .performTextInput(
            "https://studybuddies.page.link/JoinGroup/4nlAlJSZwjjdXrXTZ") // Is not an existing
    // group
  }*/

  /*fun testAddLinkButtonGoodLink() {

    composeTestRule.setContent { AddLinkButton(mockNavActions) }

    // Perform click on the button
    composeTestRule.onNodeWithText("Create a task").performClick()

    // Perform text input in the TextField
    composeTestRule
        .onNodeWithText("Enter Link")
        .performTextInput("https://studybuddies.page.link/JoinGroup/4nlAlCyONOiQIBKXrXTZ")
    // The link should work if it's not modified in the Firebase
  }*/
}
