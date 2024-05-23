package com.github.se.studybuddies.ui.groups

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.database.DbRepository
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.permissions.checkPermission
import com.github.se.studybuddies.permissions.imagePermissionVersion
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
import com.github.se.studybuddies.ui.shared_elements.SaveButton
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.GroupViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GroupMembers(
    groupUID: String,
    groupViewModel: GroupViewModel,
    navigationActions: NavigationActions,
    db: DbRepository
) {

    if (groupUID.isEmpty()) return
    groupViewModel.fetchGroupData(groupUID)
    val groupData by groupViewModel.group.observeAsState()

    val nameState = remember { mutableStateOf(groupData?.name ?: "") }
    val members = remember { mutableStateOf (???) }

    val context = LocalContext.current

    groupData?.let {
        nameState.value = it.name
        members.value = ???
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(White).testTag("members_scaffold"),
        topBar = {
            TopNavigationBar(
                title = { Sub_title("Members") },
                navigationIcon = {
                    GoBackRouteButton(navigationActions = navigationActions, Route.GROUPSHOME)
                },
                actions = { GroupsSettingsButton(groupUID, navigationActions, db) })
        }) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top) {
            LazyColumn(
                modifier =
                Modifier.fillMaxSize().padding(paddingValues).testTag("draw_member_column"),
                verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally) {
                item { Spacer(modifier = Modifier.padding(10.dp)) }
                item { Name(nameState) }
                item { Spacer(modifier = Modifier.padding(10.dp)) }
                }
            }
        }
    }
}