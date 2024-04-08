package com.github.se.studybuddies.ui

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope

const val DRAWER = "DRAWER"
const val BACK = "BACK"

class SharedUISettings(
    title: @Composable () -> Unit,
    navigationIcon: @Composable (CoroutineScope, DrawerState) -> Unit,
    actions: @Composable () -> Unit,
) {}
