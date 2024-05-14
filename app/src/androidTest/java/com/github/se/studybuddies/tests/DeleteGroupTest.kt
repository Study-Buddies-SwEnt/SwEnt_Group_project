package com.github.se.studybuddies.tests

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.ui.groups.GroupsSettingsButton
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeleteGroupTest : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Before
  fun setUpTest() {
    composeTestRule.setContent { GroupsSettingsButton("015OJ6Lhmbp0XrLAkqHV", mockNavActions) }
  }

  @Test fun testDeleteGroupDisplay() {}
}
