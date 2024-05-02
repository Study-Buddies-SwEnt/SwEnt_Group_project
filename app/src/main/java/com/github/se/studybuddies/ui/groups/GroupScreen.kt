package com.github.se.studybuddies.ui.groups

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.BOTTOM_NAVIGATION_DESTINATIONS
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.BottomNavigationBar
import com.github.se.studybuddies.ui.GoBackRouteButton
import com.github.se.studybuddies.ui.Sub_title
import com.github.se.studybuddies.ui.TopNavigationBar
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.GroupViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreen(
    groupUID: String,
    groupViewModel: GroupViewModel,
    navigationActions: NavigationActions
) {
  val group by groupViewModel.group.observeAsState()

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
              Icon(
                  imageVector = Icons.Default.Add,
                  tint = White,
                  contentDescription = "Create Topic"
              )
          }
      },
      bottomBar = {
        BottomNavigationBar(
            navigationActions = navigationActions, destinations = BOTTOM_NAVIGATION_DESTINATIONS)
      },
  ) {
    Image(
        painter = rememberImagePainter(pictureState.value),
        contentDescription = stringResource(R.string.group_picture),
        modifier = Modifier.fillMaxWidth().height(200.dp),
        contentScale = ContentScale.Crop)
    Text(
        text = stringResource(R.string.in_group_with_uid, nameState.value, groupUID),
        style = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
        modifier =
            Modifier.fillMaxSize().padding(16.dp).wrapContentHeight(Alignment.CenterVertically),
        textAlign = TextAlign.Center)
  }

}
