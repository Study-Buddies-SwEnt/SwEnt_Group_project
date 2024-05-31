package com.github.se.studybuddies.tests

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.ui.groups.MembersList
import com.github.se.studybuddies.utility.fakeDatabase.MockDatabase
import com.github.se.studybuddies.viewModels.GroupViewModel
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MembersListTest {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  private val db = MockDatabase()
  private val viewModel = GroupViewModel("uid", db)

  @Test
  fun TestMembersListIsDisplayed() {
    composeTestRule.setContent {
      MembersList("015OJ6Lhmbp0XrLAkqHV", viewModel, mockNavActions, db)
    }
    composeTestRule.onNodeWithTag("close_contact_list").assertExists()
  }

  @Test
  fun TestMembersListReturnWithEmptyGroup() {
    composeTestRule.setContent { MembersList("", viewModel, mockNavActions, db) }
    composeTestRule.onNodeWithText("Add Member").assertDoesNotExist()
  }
}
