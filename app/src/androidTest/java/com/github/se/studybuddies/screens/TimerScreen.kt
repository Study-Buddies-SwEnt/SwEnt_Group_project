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
  val timerAdjustmentButton: KNode = timerAdjustment.child { hasTestTag("timer_adjustment_buttons") }
  val timerButton: KNode = child { hasTestTag("timer_button") }
}
