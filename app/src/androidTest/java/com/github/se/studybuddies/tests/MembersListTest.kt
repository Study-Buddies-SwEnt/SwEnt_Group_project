package com.github.se.studybuddies.tests;

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.ui.groups.GroupsSettingsButton
import com.github.se.studybuddies.utility.fakeDatabase.MockDatabase
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
public class MembersListTest {
    @get:Rule val composeTestRule = createComposeRule()

    @get:Rule val mockkRule = MockKRule(this)

    @RelaxedMockK lateinit var mockNavActions: NavigationActions

    private val db = MockDatabase()

    @Before
    fun setUp() {
        composeTestRule.setContent { GroupsSettingsButton("userTest", mockNavActions, db) }
    }

    @Composable
    @Test
    fun TestMembersListDisplay() {
        composeTestRule.onNodeWithText(stringResource(R.string.add_member)).assertExists()
        }
    }
}
