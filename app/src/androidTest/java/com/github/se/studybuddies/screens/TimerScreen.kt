package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class TimerScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<TimerScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("timer_scaffold") }) {
  val timerRedCard: KNode = child { hasTestTag("timer_red_card") }
  val timerCard: KNode = child { hasTestTag("timer_card") }
  val timerAdjustment: KNode = child { hasTestTag("timer_adjustment") }

  val plusSecondButton: KNode = timerAdjustment.child { hasTestTag("+Seconds_button") }
  val minusSecondButton: KNode = timerAdjustment.child { hasTestTag("-Seconds_button") }
  val plusMinuteButton: KNode = timerAdjustment.child { hasTestTag("+Minutes_button") }
  val minusMinuteButton: KNode = timerAdjustment.child { hasTestTag("-Minutes_button") }
  val plusHourButton: KNode = timerAdjustment.child { hasTestTag("+Hours_button") }
  val minusHourButton: KNode = timerAdjustment.child { hasTestTag("-Hours_button") }
  val timerAdjustmentButton: KNode =
      timerAdjustment.child { hasTestTag("timer_adjustment_buttons") }

  val startTimerButton: KNode = child { hasTestTag("Start_timer_button") }
  val resetTimerButton: KNode = child { hasTestTag("Reset_timer_button") }
  val pauseTimerButton: KNode = child { hasTestTag("Pause_timer_button") }

  val topAppBox: KNode = onNode { hasTestTag("top_app_box") }
  val topAppBar: KNode = onNode { hasTestTag("top_app_bar") }
  val goBackButton: KNode = topAppBar.child { hasTestTag("go_back_button") }
  val divider: KNode = onNode { hasTestTag("divider") }
}
