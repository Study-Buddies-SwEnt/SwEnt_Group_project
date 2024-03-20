package com.github.se.studybuddies.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.studybuddies.ui.navigation.NavigationActions

@Composable
fun HomeScreen(navigationActions: NavigationActions) {
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center)
    {
        Text(
            text = "You are signed in!",
            style =
            TextStyle(
                fontSize = 42.sp,
                fontWeight = FontWeight(700),
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier.width(258.dp).height(150.dp))
        Spacer(Modifier.height(150.dp))
        SignOutButton(navigationActions)
    }
}