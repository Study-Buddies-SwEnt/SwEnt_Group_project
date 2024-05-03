package com.github.se.studybuddies.ui.topics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.ItemArea
import com.github.se.studybuddies.data.TopicItem
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.screens.GoBackRouteButton
import com.github.se.studybuddies.ui.screens.Sub_title
import com.github.se.studybuddies.ui.screens.TopNavigationBar
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.TopicViewModel

@Composable
fun TopicScreen(
    groupUID: String,
    topicUID: String,
    topicViewModel: TopicViewModel,
    navigationActions: NavigationActions
) {
  topicViewModel.fetchTopicData(topicUID)
  val topicData by topicViewModel.topic.collectAsState()

  val nameState = remember { mutableStateOf(topicData.name) }
  var exercisesState by remember { mutableStateOf(topicData.exercises) }
  val theoryState = remember { mutableStateOf(topicData.theory) }

  val areaState = remember { mutableStateOf(ItemArea.EXERCISES) }

  LaunchedEffect(topicData) {
    nameState.value = topicData.name
    exercisesState = topicData.exercises
    theoryState.value = topicData.theory
  }

  val floatingButtonsVisible = remember { mutableStateOf(false) }
  val folderFieldVisible = remember { mutableStateOf(false) }
  val fileFieldVisible = remember { mutableStateOf(false) }
  val enteredName = remember { mutableStateOf("") }

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopNavigationBar(
            title = { Sub_title(nameState.value) },
            navigationIcon = { GoBackRouteButton(navigationActions, "${Route.GROUP}/$groupUID") },
            actions = {})
      },
      floatingActionButton = {
        Column(horizontalAlignment = Alignment.End) {
          if (floatingButtonsVisible.value) {
            Button(
                modifier = Modifier.width(100.dp).height(45.dp),
                shape = RoundedCornerShape(50),
                onClick = {
                  folderFieldVisible.value = !folderFieldVisible.value
                  fileFieldVisible.value = false
                  floatingButtonsVisible.value = false
                  enteredName.value = ""
                }) {
                  Text(
                      text = stringResource(R.string.folder),
                      color = White,
                      style = TextStyle(fontSize = 16.sp))
                }
            Spacer(modifier = Modifier.size(7.dp))
            Button(
                modifier = Modifier.width(100.dp).height(45.dp),
                shape = RoundedCornerShape(50),
                onClick = {
                  fileFieldVisible.value = !fileFieldVisible.value
                  folderFieldVisible.value = false
                  floatingButtonsVisible.value = false
                  enteredName.value = ""
                }) {
                  Text(
                      text = stringResource(R.string.file),
                      color = White,
                      style = TextStyle(fontSize = 16.sp))
                }
          }
          Spacer(modifier = Modifier.size(10.dp))
          Row(
              modifier = Modifier.fillMaxWidth().padding(16.dp),
              verticalAlignment = Alignment.Bottom,
              horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = { floatingButtonsVisible.value = !floatingButtonsVisible.value },
                    modifier =
                        Modifier.width(64.dp).height(64.dp).clip(MaterialTheme.shapes.medium)) {
                      Icon(
                          imageVector = Icons.Default.Add,
                          contentDescription = stringResource(R.string.create_a_topic_item),
                          tint = White)
                    }
              }
        }
      },
      floatingActionButtonPosition = FabPosition.End) {
        Column(
            modifier = Modifier.fillMaxSize().padding(it),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top)) {
              Box(
                  modifier =
                      Modifier.fillMaxWidth()
                          .background(Color.White)
                          .clickable { navigationActions.navigateTo(Route.CHAT) }
                          .drawBehind {
                            val strokeWidth = 1f
                            val y = size.height - strokeWidth / 2
                            drawLine(
                                Color.LightGray, Offset(0f, y), Offset(size.width, y), strokeWidth)
                          }) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                      Spacer(modifier = Modifier.size(16.dp))
                      Text(
                          text = stringResource(R.string.chat),
                          modifier = Modifier.align(Alignment.CenterVertically),
                          style = TextStyle(fontSize = 20.sp, lineHeight = 28.sp))
                    }
                  }
              Divider(color = Blue, thickness = 2.dp)
              Box(
                  modifier =
                      Modifier.fillMaxWidth()
                          .background(Color.White)
                          .clickable { navigationActions.navigateTo(Route.CHAT) }
                          .drawBehind {
                            val strokeWidth = 1f
                            val y = size.height - strokeWidth / 2
                            drawLine(
                                Color.LightGray, Offset(0f, y), Offset(size.width, y), strokeWidth)
                          }) {
                    Column {
                      Row(
                          horizontalArrangement = Arrangement.SpaceBetween,
                          verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Practice",
                                modifier =
                                    Modifier.weight(1f)
                                        .clickable { areaState.value = ItemArea.EXERCISES }
                                        .padding(horizontal = 16.dp, vertical = 16.dp)
                                        .align(Alignment.CenterVertically),
                                style = TextStyle(fontSize = 20.sp),
                                textAlign = TextAlign.Center)
                            Text(
                                text = "Theory",
                                modifier =
                                    Modifier.weight(1f)
                                        .clickable { areaState.value = ItemArea.THEORY }
                                        .padding(horizontal = 16.dp, vertical = 16.dp)
                                        .align(Alignment.CenterVertically),
                                style = TextStyle(fontSize = 20.sp),
                                textAlign = TextAlign.Center)
                          }
                      Divider(
                          modifier =
                              Modifier.align(
                                      if (areaState.value == ItemArea.EXERCISES) Alignment.Start
                                      else Alignment.End)
                                  .fillMaxWidth(0.5f),
                          color = Blue,
                          thickness = 4.dp)
                    }
                  }
              LazyColumn(
                  modifier = Modifier.fillMaxSize(),
                  verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
                  horizontalAlignment = Alignment.Start,
                  content = {
                    if (areaState.value == ItemArea.EXERCISES) {
                      items(exercisesState) { topicItem -> TopicContentItem(topicItem) }
                    } else if (areaState.value == ItemArea.THEORY) {
                      items(theoryState.value) { topicItem -> TopicContentItem(topicItem) }
                    }
                  })
            }
        if (folderFieldVisible.value) {
          TopicItemField(
              enteredName = enteredName, label = stringResource(R.string.enter_a_folder_name)) {
                folderFieldVisible.value = false
                topicViewModel.createTopicFolder(enteredName.value, areaState.value)
                enteredName.value = ""
              }
        }
        if (fileFieldVisible.value) {
          TopicItemField(
              enteredName = enteredName, label = stringResource(R.string.enter_a_file_name)) {
                fileFieldVisible.value = false
                topicViewModel.createTopicFile(enteredName.value, areaState.value)
                enteredName.value = ""
              }
        }
      }
}

@Composable
fun TopicContentItem(topicItem: TopicItem) {
  Box(
      modifier =
          Modifier.fillMaxWidth().background(Color.White).drawBehind {
            val strokeWidth = 1f
            val y = size.height - strokeWidth / 2
            drawLine(Color.LightGray, Offset(0f, y), Offset(size.width, y), strokeWidth)
          }) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
          Spacer(modifier = Modifier.size(16.dp))
          Text(
              text = topicItem.name,
              modifier = Modifier.align(Alignment.CenterVertically),
              style = TextStyle(fontSize = 20.sp),
              lineHeight = 28.sp)
        }
      }
}

@Composable
fun TopicItemField(enteredName: MutableState<String>, label: String, onDone: () -> Unit) {
  Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center) {
        TextField(
            value = enteredName.value,
            onValueChange = { enteredName.value = it },
            label = { Text(label) },
            colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    unfocusedLabelColor = Blue,
                    unfocusedIndicatorColor = Blue),
            modifier =
                Modifier.fillMaxWidth()
                    .padding(38.dp)
                    .border(width = 1.dp, color = Blue, shape = RoundedCornerShape(4.dp)),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            singleLine = true)
      }
}
