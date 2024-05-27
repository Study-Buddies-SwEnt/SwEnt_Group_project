package com.github.se.studybuddies.ui.topics

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
import com.github.se.studybuddies.ui.shared_elements.SaveButton
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.viewModels.TopicViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TopicSettings(
    topicUID: String,
    groupUID: String,
    topicViewModel: TopicViewModel,
    navigationActions: NavigationActions
) {

  if (topicUID.isEmpty()) return
  topicViewModel.fetchTopicData(topicUID) {}
  val topicData by topicViewModel.topic.collectAsState()

  val nameState = remember { mutableStateOf(topicData.name) }
  var exercisesState by remember { mutableStateOf(topicData.exercises) }
  val theoryState = remember { mutableStateOf(topicData.theory) }

  topicData.let { nameState.value = it.name }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("topic_settings"),
      topBar = {
        TopNavigationBar(
            title = { Sub_title(title = stringResource(R.string.topic_settings)) },
            navigationIcon = {
              GoBackRouteButton(
                  navigationActions = navigationActions, "${Route.TOPIC}/$topicUID/$groupUID")
            },
            actions = {})
      }) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(it),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
              Spacer(Modifier.height(40.dp))
              Text(stringResource(R.string.edit_topic_name), style = TextStyle(fontSize = 20.sp))
              Spacer(Modifier.height(20.dp))
              OutlinedTextField(
                  value = nameState.value,
                  onValueChange = { nameState.value = it },
                  singleLine = true,
                  colors =
                      OutlinedTextFieldDefaults.colors(
                          cursorColor = Color.Blue,
                          focusedBorderColor = Color.Blue,
                          unfocusedBorderColor = Color.Blue))
              Spacer(Modifier.height(20.dp))

              Spacer(modifier = Modifier.padding(20.dp))
              SaveButton(nameState) {
                topicViewModel.updateTopicName(nameState.value) {
                    navigationActions.navigateTo("${Route.GROUP}/$groupUID")
                }
              }
            }
      }
}

/*

              topicData.exercises.plus(topicData.theory).forEach { item ->
                TopicItemRow(item, onDelete = { stagedDeletions = stagedDeletions.plus(item.uid) })
              }
              Button(
                  onClick = {
                    topicViewModel.updateTopicName(nameState.value)
                    topicViewModel.applyDeletions(stagedDeletions) {
                      stagedDeletions = setOf() // Reset staged deletions after application
                      Toast.makeText(
                              LocalContext.current,
                              "Changes saved successfully",
                              Toast.LENGTH_SHORT)
                          .show()
                    }
                  },
                  modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Save Changes")
                  }
            }
      }
}



/*
@Composable
fun TopicItemRow(item: TopicItem, onDelete: () -> Unit) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(item.name, Modifier.weight(1f))
        IconButton(onClick = onDelete) {
          Icon(Icons.Filled.Delete, contentDescription = "Delete ${item.name}")
        }
      }
}
*/
*/
