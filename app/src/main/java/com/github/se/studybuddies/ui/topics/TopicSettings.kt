package com.github.se.studybuddies.ui.topics

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.ui.screens.GoBackRouteButton
import com.github.se.studybuddies.ui.screens.Sub_title
import com.github.se.studybuddies.ui.screens.TopNavigationBar
import com.github.se.studybuddies.viewModels.TopicViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TopicSettings(
    topicUID: String,
    topicViewModel: TopicViewModel,
    backRoute: String,
    navigationActions: NavigationActions
) {
  if (topicUID.isEmpty()) return
  topicViewModel.fetchTopicData(topicUID)
  val topicData by topicViewModel.topic.collectAsState()

  val nameState = remember { mutableStateOf(topicData.name) }

  topicData.let { nameState.value = it.name }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("account_settings"),
      topBar = {
        TopNavigationBar(
            title = { Sub_title(title = stringResource(R.string.profile_setting)) },
            navigationIcon = {
              GoBackRouteButton(navigationActions = navigationActions, backRoute)
            },
            actions = {})
      }) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top) {
              Spacer(Modifier.height(150.dp))
              // TODO: Topic settings page
            }
      }
}
