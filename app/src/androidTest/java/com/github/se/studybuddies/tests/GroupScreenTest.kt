package com.github.se.studybuddies.tests

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.database.MockDatabase
import com.github.se.studybuddies.navigation.NavigationActions
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroupScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  // userTest
  // aloneUserTest
  val uid = "userTest2"
  private val db = MockDatabase()
}
