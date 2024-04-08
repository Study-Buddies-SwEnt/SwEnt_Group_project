package com.github.se.studybuddies.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.Destination
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.SETTINGS_DESTINATIONS
import kotlinx.coroutines.launch

@Composable
fun DrawerMenu(
    navigationActions: NavigationActions,
    backRoute: String,
    topBarContent: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()
  ModalNavigationDrawer(
      drawerState = drawerState,
      drawerContent = {
        var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
        ModalDrawerSheet(
            modifier = Modifier.requiredWidth(200.dp),
        ) {
          Spacer(modifier = Modifier.size(16.dp))
          SETTINGS_DESTINATIONS.forEachIndexed { index, item ->
            NavigationDrawerItem(
                label = { Text(item.textId) },
                selected = false,
                onClick = {
                  navigationActions.navigateTo("${item.route}/$backRoute")
                  selectedItemIndex = index
                  scope.launch { drawerState.close() }
                },
                icon = {
                  Icon(painter = painterResource(item.icon), contentDescription = item.textId)
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding))
          }
        }
      }) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
              MainTopBar(
                  content = {
                    MenuButton { scope.launch { drawerState.open() } }
                    topBarContent()
                  })
            },
            content = content)
      }
}

@Composable
fun MainTopBar(content: @Composable() (RowScope.() -> Unit)) {
  TopAppBar(
      modifier = Modifier.width(412.dp).height(90.dp).padding(bottom = 2.dp),
      contentColor = Color.Transparent,
      backgroundColor = Color.Transparent,
      elevation = 0.dp,
      content = content)
}

@Composable
fun SecondaryTopBar(onClick: () -> Unit) {
  TopAppBar(
      modifier = Modifier.width(412.dp).height(90.dp).padding(bottom = 2.dp),
      contentColor = Color.Transparent,
      backgroundColor = Color.Transparent,
      elevation = 0.dp) {
        IconButton(onClick = { onClick() }) {
          Icon(
              painterResource(R.drawable.arrow_back),
              contentDescription = "Go back button",
              modifier = Modifier.size(28.dp))
        }
      }
}

@Composable
private fun MenuButton(onClick: () -> Unit) {
  IconButton(
      onClick = {
        onClick()
        Log.d("MyPrint", "Clicked on the menu button")
      }) {
        Icon(
            painterResource(R.drawable.menu),
            contentDescription = "Settings",
            modifier = Modifier.size(28.dp))
      }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BottomNavigationBar(navigationActions: NavigationActions, destinations: List<Destination>) {
  var selectedItemIndex by rememberSaveable { mutableStateOf(0) }
  Scaffold(
      bottomBar = {
        NavigationBar {
          destinations.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItemIndex == index,
                onClick = {
                  selectedItemIndex = index
                  navigationActions.navigateTo(item.route)
                },
                label = { Text(text = item.textId) },
                alwaysShowLabel = true,
                icon = {
                  Icon(painter = painterResource(item.icon), contentDescription = item.textId)
                })
          }
        }
      }) {}
}
