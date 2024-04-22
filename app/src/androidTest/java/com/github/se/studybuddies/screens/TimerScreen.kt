package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class TimerScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<TimerScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("timer_scaffold") }) {

  val timerColumn: KNode = onNode { hasTestTag("timer_column") }
  val timerRedCard: KNode = child { hasTestTag("timer_red_card") }
  val timerCard: KNode = onNode { hasTestTag("timer_card") }
  val timerAdjustment: KNode = child { hasTestTag("timer_adjustment") }

  val plusSecondButton: KNode = onNode { hasTestTag("+Seconds_button") }
  val minusSecondButton: KNode = onNode { hasTestTag("-Seconds_button") }
  val plusMinuteButton: KNode = onNode { hasTestTag("+Minutes_button") }
  val minusMinuteButton: KNode = onNode { hasTestTag("-Minutes_button") }
  val plusHourButton: KNode = onNode { hasTestTag("+Hours_button") }
  val minusHourButton: KNode = onNode { hasTestTag("-Hours_button") }

  val startTimerButton: KNode = onNode { hasTestTag("Start_timer_button") }
  val resetTimerButton: KNode = onNode { hasTestTag("Reset_timer_button") }
  val pauseTimerButton: KNode = onNode { hasTestTag("Pause_timer_button") }

  val topAppBox: KNode = onNode { hasTestTag("top_app_box") }
  val topAppBar: KNode = onNode { hasTestTag("top_app_bar") }
  val goBackButton: KNode = topAppBar.child { hasTestTag("go_back_button") }
  val divider: KNode = onNode { hasTestTag("divider") }
}
