package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class GroupScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<GroupScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("GroupScreen") }) {

  val groupHomeColumn: KNode = onNode { hasTestTag("GroupsHomeColumn") }
}
