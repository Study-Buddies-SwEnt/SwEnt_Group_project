package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

class CreateGroupScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<CreateGroupScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("CreateGroup") }) {}
