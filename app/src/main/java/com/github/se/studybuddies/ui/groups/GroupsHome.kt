package com.github.se.studybuddies.ui.groups

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.Group
import com.github.se.studybuddies.navigation.GROUPS_SETTINGS_DESTINATIONS
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.DrawerMenu
import com.github.se.studybuddies.viewModels.GroupsHomeViewModel

@Composable
fun GroupsHome(
    uid: String,
    groupsHomeViewModel: GroupsHomeViewModel,
    navigationActions: NavigationActions
) {
  groupsHomeViewModel.fetchGroups(uid)
  val groups by groupsHomeViewModel.groups.observeAsState()
  val groupList = remember { mutableStateOf(groups?.getAllTasks() ?: emptyList()) }

  groups?.let { groupList.value = it.getAllTasks() }

  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()

  Column(modifier = Modifier.testTag("GroupsHomeScreen")) {
    DrawerMenu(
        navigationActions,
        Route.GROUPSHOME,
        topBarContent = { GroupsSettingsButton(navigationActions) },
        content = { innerPadding ->
          if (groupList.value.isEmpty()) {
            Text(
                text = "Join a group or create one.",
                style = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
                modifier =
                    Modifier.padding(innerPadding)
                        .fillMaxSize()
                        .padding(16.dp)
                        .wrapContentHeight(Alignment.CenterVertically),
                textAlign = TextAlign.Center)
          } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
                horizontalAlignment = Alignment.Start,
                content = {
                  items(groupList.value) { group -> GroupItem(group, navigationActions) }
                })
          }
        })
  }
}

@Composable
fun GroupsSettingsButton(navigationActions: NavigationActions) {
  val expandedState = remember { mutableStateOf(false) }
  IconButton(
      onClick = { expandedState.value = true },
  ) {
    Icon(painter = painterResource(R.drawable.dots_menu), contentDescription = "Dots Menu")
  }
  DropdownMenu(expanded = expandedState.value, onDismissRequest = { expandedState.value = false }) {
    GROUPS_SETTINGS_DESTINATIONS.forEach { item ->
      DropdownMenuItem(
          onClick = {
            expandedState.value = false
            navigationActions.navigateTo(item.route)
          }) {
            Text(item.textId)
          }
    }
  }
}

@Composable
fun GroupItem(group: Group, navigationActions: NavigationActions) {
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .clickable {
                val groupUid = group.uid
                navigationActions.navigateTo("${Route.GROUP}/$groupUid")
              }
              .drawBehind {
                val strokeWidth = 1f
                val y = size.height - strokeWidth / 2
                drawLine(Color.LightGray, Offset(0f, y), Offset(size.width, y), strokeWidth)
              }) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
          Image(
              painter = rememberImagePainter(group.picture),
              contentDescription = "Group profile picture",
              modifier = Modifier.size(32.dp),
              contentScale = ContentScale.Crop)
          Text(text = group.name, style = TextStyle(fontSize = 16.sp), lineHeight = 28.sp)
        }
      }
}
