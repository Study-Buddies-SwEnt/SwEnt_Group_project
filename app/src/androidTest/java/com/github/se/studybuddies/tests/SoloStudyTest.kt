package com.github.se.studybuddies.tests

import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.MainActivity
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.screens.SoloStudyScreen
import com.github.se.studybuddies.ui.map.MapScreen
import com.github.se.studybuddies.ui.solo_study.SoloStudyHome
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SoloStudyTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK
  lateinit var mockNavActions: NavigationActions

  @Before
  fun setup() {
    composeTestRule.setContent {
      SoloStudyHome(mockNavActions)
    }
  }

  @Test
  fun elementDisplayed() {
    onComposeScreen<SoloStudyScreen>(composeTestRule) {
      soloStudyScreen {
        assertIsDisplayed()
      }
      row1 {
        assertIsDisplayed()
      }
      row2 {
        assertIsDisplayed()
      }
      flashCardButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
      todoListButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
      timerButton {
        assertIsDisplayed()
        assertHasClickAction()
      }
      calendarButton {
        assertIsDisplayed()
        assertHasClickAction()
      }

    }
  }
}
