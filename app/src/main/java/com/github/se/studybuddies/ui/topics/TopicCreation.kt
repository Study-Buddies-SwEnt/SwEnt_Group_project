package com.github.se.studybuddies.ui.topics

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
import com.github.se.studybuddies.ui.shared_elements.SaveButton
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.viewModels.TopicViewModel

/**
 * Composable function to create a new topic.
 * @param groupUID: The UID of the group the topic is being created in.
 * @param topicViewModel: The view model for the topic.
 * @param navigationActions: The navigation actions for the app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TopicCreation(
    groupUID: String,
    topicViewModel: TopicViewModel,
    navigationActions: NavigationActions
) {
  val nameState = remember { mutableStateOf("") }

  Scaffold(
      modifier = Modifier
          .fillMaxSize()
          .background(Color.White)
          .testTag("create_topic_scaffold"),
      topBar = {
        TopNavigationBar(
            title = { Sub_title(stringResource(R.string.create_topic)) },
            navigationIcon = { GoBackRouteButton(navigationActions, "${Route.GROUP}/$groupUID") },
            actions = {})
      }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .testTag("create_topic_column"),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(stringResource(R.string.enter_topic_name))

              Spacer(Modifier.height(20.dp))
              OutlinedTextField(
                  value = nameState.value,
                  onValueChange = { nameState.value = it },
                  label = { Text(stringResource(R.string.topic_name), color = Blue) },
                  placeholder = { Text(stringResource(R.string.enter_topic_name), color = Blue) },
                  singleLine = true,
                  modifier =
                  Modifier
                      .padding(0.dp)
                      .width(300.dp)
                      .height(65.dp)
                      .clip(MaterialTheme.shapes.small)
                      .testTag("topic_name"),
                  colors =
                      TextFieldDefaults.outlinedTextFieldColors(
                          focusedBorderColor = Blue,
                          unfocusedBorderColor = Blue,
                          cursorColor = Blue))
              Spacer(modifier = Modifier.height(20.dp))

              SaveButton(nameState.value.isNotBlank()) {
                topicViewModel.createTopic(nameState.value, groupUID) {
                  navigationActions.navigateTo("${Route.GROUP}/$groupUID")
                }
              }
            }
      }
}
