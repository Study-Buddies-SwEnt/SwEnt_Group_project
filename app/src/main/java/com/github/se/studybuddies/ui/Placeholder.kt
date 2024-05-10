package com.github.se.studybuddies.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.screens.MainScreenScaffold
import com.github.se.studybuddies.ui.solo_study.CALENDAR
import com.github.se.studybuddies.ui.solo_study.FLASH_CARD
import com.github.se.studybuddies.ui.solo_study.Solo_study_buttons
import com.github.se.studybuddies.ui.solo_study.TIMER
import com.github.se.studybuddies.ui.solo_study.TODO_LIST

@Composable
fun Placeholder(navigationActions: NavigationActions) {
    MainScreenScaffold(
        navigationActions,
        Route.PLACEHOLDER,
        content = {
        },
        title = "Feature not implemented yet",
        iconOptions = {})
}
