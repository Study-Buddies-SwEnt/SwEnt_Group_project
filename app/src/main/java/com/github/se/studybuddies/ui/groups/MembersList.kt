package com.github.se.studybuddies.ui.groups

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.se.studybuddies.R
import com.github.se.studybuddies.database.DbRepository
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteToLastPageButton
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.GroupViewModel

/**
 * This composable is used to display the list of members in a group.
 *
 * @param groupUID The unique identifier of the group.
 * @param groupViewModel The view model for the group.
 * @param navigationActions The navigation actions.
 * @param db The database repository.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MembersList(
    groupUID: String,
    groupViewModel: GroupViewModel,
    navigationActions: NavigationActions,
    db: DbRepository
) {

  if (groupUID.isEmpty()) return
  val isBoxVisible = remember { mutableStateOf(true) }

  Scaffold(
      modifier = Modifier.fillMaxSize().background(White),
      topBar = {
        TopNavigationBar(
            title = { Sub_title(stringResource(R.string.add_member)) },
            navigationIcon = { GoBackRouteToLastPageButton(navigationActions = navigationActions) },
            actions = { GroupsSettingsButton(groupUID, navigationActions, db) })
      }) {
      Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
        ) {
          ShowContact(groupUID, groupViewModel, isBoxVisible)
          if (!isBoxVisible.value) {
            navigationActions.navigateTo(Route.GROUPSHOME)
          }
        }
      }
}
