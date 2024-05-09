package com.github.se.studybuddies.tests

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.rule.IntentsTestRule
import com.github.se.studybuddies.MainActivity
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Rule
import org.junit.Test

class LeaveGroupTest : TestCase() {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val intentsTestRule = IntentsTestRule(MainActivity::class.java)

    @Test
    fun testTextDisplay() {
        ComposeScreen.onComposeScreen<com.github.se.studybuddies.screens.GroupsHomeScreen>(
            composeTestRule) {
            textDialogues { assertIsDisplayed() }
            textDialoguesYes { assertIsDisplayed() }
            textDialoguesNo { assertIsDisplayed() }
        }
    }
}