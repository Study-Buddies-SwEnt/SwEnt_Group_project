package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class CalendarScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CalendarScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("calendar_scaffold") }) {

  val previousMonthButton: KNode = onNode { hasTestTag("PreviousMonthButton") }
  val nextMonthButton: KNode = onNode { hasTestTag("NextMonthButton") }

  fun dateButton(day: String): KNode = onNode { hasTestTag("Date_$day") }

  val topAppBar: KNode = onNode { hasTestTag("TopAppBar") }
  val goBackButton: KNode = topAppBar.child { hasTestTag("GoBackButton") }
}
