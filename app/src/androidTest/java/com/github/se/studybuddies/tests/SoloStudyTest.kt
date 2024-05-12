package com.github.se.studybuddies.tests

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.screens.SoloStudyScreen
import com.github.se.studybuddies.ui.solo_study.SoloStudyHome
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SoloStudyTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)

  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Before
  fun setup() {
    composeTestRule.setContent { SoloStudyHome(mockNavActions) }
  }

  @Test
  fun rowColDisplayed() {
    onComposeScreen<SoloStudyScreen>(composeTestRule) {
      soloStudyScreen { assertIsDisplayed() }
      row1 { assertIsDisplayed() }
      row2 { assertIsDisplayed() }

      flashCardColumn { assertIsDisplayed() }
      /*
      todoListColumn {
          assertIsDisplayed()
      }

      timerColumn {
          assertIsDisplayed()
      }
      calendarColumn {
          assertIsDisplayed()
      }*/
    }
  }

  @Test
  fun buttonDisplayed() {
    onComposeScreen<SoloStudyScreen>(composeTestRule) {
      soloStudyScreen { assertIsDisplayed() }
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
  /*
  @Test
  fun iconDisplayed() {
    onComposeScreen<SoloStudyScreen>(composeTestRule) {
      flashCardIcon {
        assertIsDisplayed()
      }

      todoListIcon {
        assertIsDisplayed()
      }
      timerIcon {
        assertIsDisplayed()
      }
      calendarIcon {
        assertIsDisplayed()
      }
    }
  }

  @Test
  fun textDisplayed() {
    onComposeScreen<SoloStudyScreen>(composeTestRule) {
      flashCardText {
        assertIsDisplayed()
        assertTextEquals("Flash Card")
      }
      todoListText {
        assertIsDisplayed()
        assertTextEquals("ToDo List")
      }
      timerText {
        assertIsDisplayed()
        assertTextEquals("Timer")
      }
      calendarText {
        assertIsDisplayed()
        assertTextEquals("Calendar")
      }
    }
  }*/

  @Test
  fun buttonsWorking() {
    onComposeScreen<SoloStudyScreen>(composeTestRule) {
      flashCardButton { performClick() }
      verify { mockNavActions.navigateTo(Route.PLACEHOLDER) }
      confirmVerified(mockNavActions)

      todoListButton { performClick() }
      verify { mockNavActions.navigateTo(Route.TODOLIST) }
      confirmVerified(mockNavActions)

      timerButton { performClick() }
      verify { mockNavActions.navigateTo(Route.TIMER) }
      confirmVerified(mockNavActions)

      calendarButton { performClick() }
      verify { mockNavActions.navigateTo(Route.PLACEHOLDER) }
      confirmVerified(mockNavActions)
    }
  }

  @Test
  fun testDrawerSoloStudy() {
    onComposeScreen<SoloStudyScreen>(composeTestRule) {
      drawerScaffold { assertIsDisplayed() }
      topAppBox { assertIsDisplayed() }
      topAppBar { assertIsDisplayed() }
      drawerMenuButton {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
      drawerSheet { assertIsDisplayed() }
      settingsButton {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
      verify { mockNavActions.navigateTo("${Route.SETTINGS}/${Route.SOLOSTUDYHOME}") }
      confirmVerified(mockNavActions)

      drawerMenuButton {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
      accountButton {
        assertIsDisplayed()
        assertHasClickAction()
        performClick()
      }
      verify { mockNavActions.navigateTo("${Route.ACCOUNT}/${Route.SOLOSTUDYHOME}") }
      confirmVerified(mockNavActions)
    }
  }

  @Test
  fun testBottomBarSoloStudy() {
    onComposeScreen<SoloStudyScreen>(composeTestRule) {
      soloBottomBar { assertIsDisplayed() }
      soloStudyBottom { assertIsDisplayed() }
      groupsBottom {
        assertIsDisplayed()
        performClick()
      }
      verify { mockNavActions.navigateTo(Route.GROUPSHOME) }
      confirmVerified(mockNavActions)

      messagesBottom {
        assertIsDisplayed()
        performClick()
      }
      verify { mockNavActions.navigateTo(Route.DIRECT_MESSAGE) }
      confirmVerified(mockNavActions)

      mapBottom {
        assertIsDisplayed()
        performClick()
      }
      verify { mockNavActions.navigateTo(Route.MAP) }
      confirmVerified(mockNavActions)
    }
  }
}
