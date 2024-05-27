package com.github.se.studybuddies.tests

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.database.MockDatabase
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.screens.GroupScreen
import com.github.se.studybuddies.ui.groups.GroupScreen
import com.github.se.studybuddies.viewModels.ChatViewModel
import com.github.se.studybuddies.viewModels.GroupViewModel
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroupScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  val uid = "userTest1"
  private val db = MockDatabase()
  val groupUID = "groupTest1"
  val groupVM = GroupViewModel(groupUID, db)
  val chatVM = ChatViewModel()

  @Before
  fun testSetup() {
    composeTestRule.setContent { GroupScreen(groupUID, groupVM, chatVM, mockNavActions, db) }
  }

  @Test
  fun elementsAreDisplayed() {
    ComposeScreen.onComposeScreen<GroupScreen>(composeTestRule) {
      groupHomeColumn { assertIsDisplayed() }
    }
  }
}
