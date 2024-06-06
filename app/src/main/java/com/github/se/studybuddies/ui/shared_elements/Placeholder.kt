package com.github.se.studybuddies.ui.shared_elements

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.NavigationActions

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Placeholder(navigationActions: NavigationActions) {

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("placeholder_scaffold"),
      topBar = {
        TopNavigationBar(
            title = { Sub_title(title = "") },
            leftButton = { GoBackRouteButton(navigationActions = navigationActions) },
            rightButton = {})
      },
  ) {
    Column(
        modifier = Modifier.fillMaxSize().testTag("placeholder_column"),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally) {
          Spacer(modifier = Modifier.height(120.dp))
          Text(
              stringResource(R.string.feature_not_implemented_yet),
              textAlign = TextAlign.Center,
              modifier = Modifier.testTag("placeholder_text"))
        }
  }
}
