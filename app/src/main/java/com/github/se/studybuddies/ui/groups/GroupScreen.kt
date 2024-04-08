package com.github.se.studybuddies.ui.groups

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.DrawerMenu
import com.github.se.studybuddies.ui.SearchIcon
import com.github.se.studybuddies.viewModels.GroupViewModel

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

  DrawerMenu(
      navigationActions,
      Route.GROUPSHOME,
      content = { innerPadding ->
        Image(
            painter = rememberImagePainter(pictureState.value),
            contentDescription = "Group picture",
            modifier = Modifier.fillMaxWidth().height(200.dp),
            contentScale = ContentScale.Crop)
        Text(
            text = "In group ${nameState.value} with uid $groupUID",
            style = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
            modifier =
                Modifier.padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp)
                    .wrapContentHeight(Alignment.CenterVertically),
            textAlign = TextAlign.Center)
      },
      iconOption = { SearchIcon() })
}
