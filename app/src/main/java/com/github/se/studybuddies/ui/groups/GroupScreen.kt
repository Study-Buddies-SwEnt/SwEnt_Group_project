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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import coil.compose.rememberAsyncImagePainter
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.ChatType
import com.github.se.studybuddies.data.Topic
import com.github.se.studybuddies.database.DbRepository
import com.github.se.studybuddies.navigation.Destination
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.shared_elements.BottomNavigationBar
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
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
    navigationActions: NavigationActions,
    db: DbRepository
) {
  val group by groupViewModel.group.observeAsState()
  val topics by groupViewModel.topics.collectAsState()

  val nameState = remember { mutableStateOf(group?.name ?: "") }
  val pictureState = remember { mutableStateOf(group?.picture ?: Uri.EMPTY) }
  val membersState = remember { mutableStateOf(group?.members ?: emptyList()) }

  group?.let {
    nameState.value = it.name
    pictureState.value = it.picture
    membersState.value = it.members
  }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("GroupScreen"),
      topBar = {
        TopNavigationBar(
            title = { Sub_title(nameState.value) },
            navigationIcon = {
              GoBackRouteButton(navigationActions = navigationActions, Route.GROUPSHOME)
            },
            actions = { GroupsSettingsButton(groupUID, navigationActions, db) })
      },
      floatingActionButton = {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("floating_action_row"),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.End) {
              Button(
                  onClick = { navigationActions.navigateTo("${Route.TOPICCREATION}/$groupUID") },
                  modifier =
                      Modifier.width(64.dp)
                          .height(64.dp)
                          .clip(MaterialTheme.shapes.medium)
                          .testTag("create_topic_button")) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.create_a_task),
                        tint = White,
                        modifier = Modifier.testTag("create_topic_icon"))
                  }
            }
      },
      bottomBar = {
        BottomNavigationBar(
            navigationActions = navigationActions,
            destinations =
                listOf(
                    Destination(
                        route = "${Route.CALLLOBBY}/$groupUID",
                        icon = R.drawable.active_call,
                        textId = stringResource(R.string.video_call)),
                    Destination(
                        route = "${Route.SHAREDTIMER}/$groupUID",
                        icon = R.drawable.timer,
                        textId = stringResource(R.string.timer))),
            currentRoute = Route.GROUP,
            iconSize = 32)
      }) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).testTag("GroupScreenColumn"),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
        ) {
          Box(
              modifier =
                  Modifier.fillMaxWidth()
                      .testTag("GroupBox")
                      .background(Color.White)
                      .clickable {
                        chatViewModel.setChat(
                            group?.let {
                              Chat(
                                  it.uid,
                                  it.name,
                                  it.picture,
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
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("GroupRowChat")) {
                  Box(
                      modifier =
                          Modifier.size(52.dp)
                              .clip(CircleShape)
                              .background(Color.Transparent)
                              .testTag("BoxPP")) {
                        Image(
                            painter = rememberAsyncImagePainter(pictureState.value),
                            contentDescription = stringResource(R.string.group_picture),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop)
                      }
                  Spacer(modifier = Modifier.size(16.dp).testTag("SpacerPP"))
                  Text(
                      text = stringResource(R.string.group_chat),
                      modifier =
                          Modifier.align(Alignment.CenterVertically).testTag("GeneralChatText"),
                      style = TextStyle(fontSize = 20.sp, lineHeight = 28.sp))
                }
              }
          HorizontalDivider(
              thickness = 4.dp, color = Blue, modifier = Modifier.testTag("GroupDivider"))
          LazyColumn(
              modifier = Modifier.fillMaxSize().testTag("GroupLazyColumn"),
              verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
              horizontalAlignment = Alignment.Start,
          ) {
            items(topics.getAllTopics()) { topic -> TopicItem(groupUID, topic, navigationActions) }
          }
        }
      }
}

@Composable
fun TopicItem(groupUID: String, topic: Topic, navigationActions: NavigationActions) {
  val topicUid = topic.uid
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .testTag("${topic.uid}_item")
              .background(Color.White)
              .clickable { navigationActions.navigateTo("${Route.TOPIC}/$topicUid/$groupUID") }
              .drawBehind {
                val strokeWidth = 1f
                val y = size.height - strokeWidth / 2
                drawLine(Color.LightGray, Offset(0f, y), Offset(size.width, y), strokeWidth)
              }) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("${topic.uid}_row")) {
          Spacer(modifier = Modifier.size(16.dp).testTag("${topic.uid}_spacer"))
          Text(
              text = topic.name,
              modifier = Modifier.align(Alignment.CenterVertically).testTag("${topic.uid}_text"),
              style = TextStyle(fontSize = 20.sp),
              lineHeight = 28.sp)
        }
      }
}
