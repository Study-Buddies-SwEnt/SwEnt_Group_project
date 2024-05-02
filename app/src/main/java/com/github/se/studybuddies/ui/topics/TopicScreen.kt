package com.github.se.studybuddies.ui.topics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.GoBackRouteButton
import com.github.se.studybuddies.ui.Sub_title
import com.github.se.studybuddies.ui.TopNavigationBar
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.TopicViewModel

@Composable
fun TopicScreen(
    groupUID: String,
    topicUID: String,
    topicViewModel: TopicViewModel,
    navigationActions: NavigationActions
) {
    val topicData by topicViewModel.topic.observeAsState()

    val nameState = remember { mutableStateOf(topicData?.name ?: "") }
    val exercisesState = remember { mutableStateOf(topicData?.exercises ?: emptyList()) }
    val theoryState = remember { mutableStateOf(topicData?.theory ?: emptyList()) }

    topicData?.let {
        nameState.value = it.name
        exercisesState.value = it.exercises
        theoryState.value= it.theory
    }

    val floatingButtonsVisible = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopNavigationBar(
                title = { Sub_title(nameState.value) },
                navigationIcon = { GoBackRouteButton(navigationActions, "${Route.GROUP}/$groupUID") },
                actions = {}
            )
        },
        floatingActionButton = {
            IconButton(
                modifier = Modifier
                    .width(64.dp)
                    .height(64.dp)
                    .clip(MaterialTheme.shapes.medium),
                onClick = {
                    floatingButtonsVisible.value != floatingButtonsVisible.value
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.create_a_topic_item),
                    tint = White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top)
        ) {

        }
    }
}
