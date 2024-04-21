package com.github.se.studybuddies.tests

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.MainActivity
import com.github.se.studybuddies.screens.SoloStudyScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SoloStudyTest : TestCase() {

  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @get:Rule val intentsTestRule = IntentsTestRule(MainActivity::class.java)

  @Test
  fun soloStudyButton() {
    onComposeScreen<SoloStudyScreen>(composeTestRule) {
      /*
      soloStudyButton {
        // arrange: verify pre-conditions
        assertIsDisplayed()
        performClick()
      }*/
      /*
      soloStudyButtonText{
          assertIsDisplayed()
      }*/
    }
  }
}
