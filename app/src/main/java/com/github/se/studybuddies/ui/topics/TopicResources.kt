package com.github.se.studybuddies.ui.topics

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.FileArea
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.viewModels.TopicFileViewModel

@Composable
fun TopicResources(
    fileID: String,
    topicFileViewModel: TopicFileViewModel,
    navigationActions: NavigationActions
) {
  topicFileViewModel.fetchTopicFile(fileID)
  val fileData by topicFileViewModel.topicFile.collectAsState()

  val nameState = remember { mutableStateOf(fileData.fileName) }
  val strongUserIDs = remember { mutableStateOf(fileData.strongUsers) }
  val strongUsers = remember { mutableStateOf(emptyList<User>()) }

  topicFileViewModel.getStrongUsers(strongUserIDs.value) { strongUsers.value = it }

  val areaState = remember { mutableStateOf(FileArea.RESOURCES) }

  LaunchedEffect(fileData.fileName) {
    nameState.value = fileData.fileName
    strongUserIDs.value = fileData.strongUsers
    topicFileViewModel.getStrongUsers(strongUserIDs.value) { strongUsers.value = it }
  }

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopNavigationBar(
            title = { Sub_title(nameState.value) },
            leftButton = {
              Icon(
                  imageVector = Icons.Default.ArrowBack,
                  contentDescription = "Go back",
                  modifier =
                      Modifier.clickable { navigationActions.goBack() }.testTag("go_back_button"))
            },
            rightButton = {})
      }) {
        Column(
            modifier = Modifier.fillMaxSize().padding(it),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top)) {
              Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                      Text(
                          text = "Resources",
                          modifier =
                              Modifier.weight(1f)
                                  .clickable { areaState.value = FileArea.RESOURCES }
                                  .padding(horizontal = 16.dp, vertical = 16.dp)
                                  .align(Alignment.CenterVertically),
                          style = TextStyle(fontSize = 20.sp),
                          textAlign = TextAlign.Center)
                      Text(
                          text = "Strong Users",
                          modifier =
                              Modifier.weight(1f)
                                  .clickable { areaState.value = FileArea.STRONG_USERS }
                                  .padding(horizontal = 16.dp, vertical = 16.dp)
                                  .align(Alignment.CenterVertically),
                          style = TextStyle(fontSize = 20.sp),
                          textAlign = TextAlign.Center)
                    }
                HorizontalDivider(
                    modifier =
                        Modifier.align(
                                if (areaState.value == FileArea.RESOURCES) Alignment.Start
                                else Alignment.End)
                            .fillMaxWidth(0.5f),
                    color = Blue,
                    thickness = 4.dp)
              }
              LazyColumn(
                  modifier = Modifier.fillMaxSize(),
                  verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
                  horizontalAlignment = Alignment.Start,
                  content = {
                    if (areaState.value == FileArea.RESOURCES) {
                      item {
                        Column(modifier = Modifier.fillMaxSize()) { Text("Resources go here") }
                      }
                    } else {
                      items(strongUsers.value) { user -> UserBox(user) }
                    }
                  })
            }
      }
}

@Composable
private fun UserBox(user: User) {
  Column {
    Box(
        modifier =
            Modifier.fillMaxWidth().background(Color.White).drawBehind {
              val strokeWidth = 1f
              val y = size.height - strokeWidth / 2
              drawLine(Color.LightGray, Offset(0f, y), Offset(size.width, y), strokeWidth)
            }) {
          Row(
              modifier = Modifier.fillMaxWidth().padding(6.dp),
              verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.size(10.dp))
                Box(
                    modifier =
                        Modifier.size(52.dp).clip(CircleShape).background(Color.Transparent)) {
                      Image(
                          painter = rememberImagePainter(user.photoUrl),
                          contentDescription = stringResource(R.string.user_profile_picture),
                          modifier = Modifier.fillMaxSize(),
                          contentScale = ContentScale.Crop)
                    }
                Spacer(modifier = Modifier.size(20.dp))
                Text(
                    text = user.username,
                    modifier = Modifier.align(Alignment.CenterVertically),
                    style = TextStyle(fontSize = 20.sp),
                    lineHeight = 28.sp)
              }
        }
  }
}