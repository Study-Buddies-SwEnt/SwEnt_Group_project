package com.github.se.studybuddies.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.groups.GroupFields
import com.github.se.studybuddies.ui.groups.SaveButton
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.GroupViewModel
import com.github.se.studybuddies.viewModels.TopicViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TopicCreaction(topicViewModel: TopicViewModel, navigationActions: NavigationActions) {
    val nameState = remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(Color.White),
        topBar = {
            TopNavigationBar(
                title = { Sub_title("Create Topic") },
                navigationIcon = {
                    GoBackRouteButton(navigationActions = navigationActions, Route.GROUPSHOME)
                },
                actions = {}
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Enter Topic Name")

            TextField(
                value = nameState.value,
                onValueChange = { nameState.value = it },
                label = { Text("Topic Name") }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    topicViewModel.createTopic(nameState.value)
                    navigationActions.navigateTo(Route.GROUPSHOME)
                }
            ) {
                Text("Save Topic")
            }
        }
    }
}





