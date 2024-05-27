package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

class GroupScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<GroupScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("GroupsHomeScreen") }) {}
