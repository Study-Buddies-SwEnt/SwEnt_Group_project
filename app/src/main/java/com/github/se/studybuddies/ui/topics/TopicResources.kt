package com.github.se.studybuddies.ui.topics

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.FileArea
import com.github.se.studybuddies.data.ItemArea
import com.github.se.studybuddies.data.TopicFile
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.viewModels.TopicViewModel
import com.google.android.material.navigation.NavigationView

@Composable
fun TopicResources(fileID: String, topicID: String, topicViewModel: TopicViewModel, navigationActions: NavigationActions) {
    topicViewModel.fetchTopicData(topicID)
    val fileData by topicViewModel.topicFile.collectAsState()

    val nameState = remember { mutableStateOf(fileData.fileName) }
    val strongUsers = remember { mutableStateOf(fileData.strongUsers) }

    val areaState = remember { mutableStateOf(FileArea.RESOURCES) }

    LaunchedEffect(fileData) {
        nameState.value = fileData.fileName
        strongUsers.value = fileData.strongUsers
    }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopNavigationBar(
                title = { Sub_title(nameState.value) },
                navigationIcon = { Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Go back",
                    modifier =
                    Modifier
                        .clickable { navigationActions.goBack() }
                        .testTag("go_back_button")) },
                actions = {}
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top)
        ) {
            Box(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .clickable { navigationActions.navigateTo(Route.CHAT) }
                    .drawBehind {
                        val strokeWidth = 1f
                        val y = size.height - strokeWidth / 2
                        drawLine(
                            Color.LightGray, Offset(0f, y), Offset(size.width, y), strokeWidth
                        )
                    }) {
                Column {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Practice",
                            modifier =
                            Modifier
                                .weight(1f)
                                .clickable { areaState.value = FileArea.RESOURCES }
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                                .align(Alignment.CenterVertically),
                            style = TextStyle(fontSize = 20.sp),
                            textAlign = TextAlign.Center)
                        Text(
                            text = "Theory",
                            modifier =
                            Modifier
                                .weight(1f)
                                .clickable { areaState.value = FileArea.STRONG_USERS }
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                                .align(Alignment.CenterVertically),
                            style = TextStyle(fontSize = 20.sp),
                            textAlign = TextAlign.Center)
                    }
                    HorizontalDivider(
                        modifier =
                        Modifier
                            .align(
                                if (areaState.value == FileArea.RESOURCES) Alignment.Start
                                else Alignment.End
                            )
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
                    if (areaState.value == FileArea.RESOURCES) {
                        item{Column(modifier = Modifier.fillMaxSize()) { Text("Resources go here")}}
                    } else {
                        items(strongUsers.value) {
                            UserBox()
                        }
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
            Modifier.fillMaxWidth()
                .background(Color.White)
                .drawBehind {
                    val strokeWidth = 1f
                    val y = size.height - strokeWidth / 2
                    drawLine(Color.LightGray, Offset(0f, y), Offset(size.width, y), strokeWidth)
                }) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(6.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = ,
                    modifier = Modifier.align(Alignment.CenterVertically),
                    style = TextStyle(fontSize = 20.sp),
                    lineHeight = 28.sp)
                AddInFolderButton(
                    folderItem.uid,
                    folderFieldVisible,
                    fileFieldVisible,
                    isExpanded,
                    parentUID,
                    depth)
            }
        }
        if (isExpanded.value) {
            Log.d("MyPrint", "isExpanded")
            folderItem.items.forEach { child ->
                TopicContentItem(
                    child, folderFieldVisible, fileFieldVisible, parentUID, depth + 1, topicViewModel)
            }
        }
    }
}
