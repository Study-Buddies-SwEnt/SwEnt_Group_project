package com.github.se.studybuddies.ui.groups

import android.annotation.SuppressLint
import android.net.Uri
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.ChatType
import com.github.se.studybuddies.data.Topic
import com.github.se.studybuddies.navigation.BOTTOM_NAVIGATION_DESTINATIONS
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.BottomNavigationBar
import com.github.se.studybuddies.ui.GoBackRouteButton
import com.github.se.studybuddies.ui.Sub_title
import com.github.se.studybuddies.ui.TopNavigationBar
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.ChatViewModel
import com.github.se.studybuddies.viewModels.GroupViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GroupScreen(
    groupUID: String,
    groupViewModel: GroupViewModel,
    chatViewModel: ChatViewModel,
    navigationActions: NavigationActions
) {
  val group by groupViewModel.group.observeAsState()
  val topics by groupViewModel.topics.observeAsState()

  val nameState = remember { mutableStateOf(group?.name ?: "") }
  val pictureState = remember { mutableStateOf(group?.picture ?: Uri.EMPTY) }
  val membersState = remember { mutableStateOf(group?.members ?: emptyList()) }
  val topicList = remember { mutableStateOf(topics?.getAllTopics() ?: emptyList()) }

  group?.let {
    nameState.value = it.name
    pictureState.value = it.picture
    membersState.value = it.members
  }
  topics?.let { topicList.value = it.getAllTopics() }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("GroupScreen"),
      topBar = {
        TopNavigationBar(
            title = { Sub_title(nameState.value) },
            navigationIcon = {
              GoBackRouteButton(navigationActions = navigationActions, Route.GROUPSHOME)
            },
            actions = {
              IconButton(
                  onClick = {},
              ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    tint = Blue,
                    contentDescription = stringResource(R.string.group_option))
              }
            })
      },
      floatingActionButton = {
        FloatingActionButton(
            onClick = { navigationActions.navigateTo(Route.TOPICCREATION) },
        ) {
          Icon(imageVector = Icons.Default.Add, tint = White, contentDescription = "Create Topic")
        }
      },
      bottomBar = {
        BottomNavigationBar(
            navigationActions = navigationActions, destinations = BOTTOM_NAVIGATION_DESTINATIONS)
      },
  ) {
    Column(
        modifier = Modifier.fillMaxSize().padding(it).testTag("GroupsHome"),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
    ) {
      Box(
          modifier =
              Modifier.fillMaxWidth()
                  .background(Color.White)
                  .clickable {
                    chatViewModel.setChat(
                        group?.let {
                          Chat(
                              it.uid,
                              it.name,
                              it.picture.toString(),
                              ChatType.GROUP,
                              groupViewModel.members.value!!.toList())
                        })
                    navigationActions.navigateTo(Route.CHAT)
                  }
                  .drawBehind {
                    val strokeWidth = 1f
                    val y = size.height - strokeWidth / 2
                    drawLine(Color.LightGray, Offset(0f, y), Offset(size.width, y), strokeWidth)
                  }) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
              Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(Color.Transparent)) {
                Image(
                    painter = rememberImagePainter(pictureState.value),
                    contentDescription = stringResource(R.string.group_picture),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop)
              }
              Spacer(modifier = Modifier.size(16.dp))
              Text(
                  text = stringResource(R.string.group_chat),
                  modifier = Modifier.align(Alignment.CenterVertically),
                  style = TextStyle(fontSize = 20.sp, lineHeight = 28.sp))
            }
          }
      Divider(color = Blue, thickness = 4.dp)
      LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
          horizontalAlignment = Alignment.Start,
          content = {
            items(topicList.value) { topic -> TopicItem(groupUID, topic, navigationActions) }
          })
    }
  }
}

@Composable
fun TopicItem(groupUID: String, topic: Topic, navigationActions: NavigationActions) {
  val topicUid = topic.uid
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .background(Color.White)
              .clickable { navigationActions.navigateTo("${Route.TOPIC}/$topicUid/$groupUID") }
              .drawBehind {
                val strokeWidth = 1f
                val y = size.height - strokeWidth / 2
                drawLine(Color.LightGray, Offset(0f, y), Offset(size.width, y), strokeWidth)
              }) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
          Spacer(modifier = Modifier.size(16.dp))
          Text(
              text = topic.name,
              modifier = Modifier.align(Alignment.CenterVertically),
              style = TextStyle(fontSize = 20.sp),
              lineHeight = 28.sp)
        }
      }
}
