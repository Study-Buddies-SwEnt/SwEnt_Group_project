package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class PlaceHolderScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<LoginScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("LoginScreen") }) {

    // Structural elements of the UI
    val loginTitle: KNode = onNode { hasTestTag("placeholder_scaffold") }
    val loginButton: KNode = onNode { hasTestTag("placeholder_column") }
}
