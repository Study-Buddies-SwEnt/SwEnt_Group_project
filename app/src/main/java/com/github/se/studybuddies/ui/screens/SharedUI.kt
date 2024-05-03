package com.github.se.studybuddies.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.Group
import com.github.se.studybuddies.navigation.BOTTOM_NAVIGATION_DESTINATIONS
import com.github.se.studybuddies.navigation.Destination
import com.github.se.studybuddies.navigation.GROUPS_SETTINGS_DESTINATIONS
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.navigation.SETTINGS_DESTINATIONS
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.White
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenScaffold(
    navigationActions: NavigationActions,
    backRoute: String,
    content: @Composable (PaddingValues) -> Unit,
    title: String,
    iconOptions: @Composable () -> Unit,
) {

  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()
  ModalNavigationDrawer(
      modifier = Modifier.testTag(title + "_menu"),
      drawerState = drawerState,
      drawerContent = {
        var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
        ModalDrawerSheet(
            modifier = Modifier.requiredWidth(200.dp),
        ) {
          Spacer(modifier = Modifier.size(16.dp))
          SETTINGS_DESTINATIONS.forEachIndexed { index, item ->
            NavigationDrawerItem(
                label = { Text(item.textId, color = Blue) },
                selected = false,
                onClick = {
                  navigationActions.navigateTo("${item.route}/$backRoute")
                  selectedItemIndex = index
                  scope.launch { drawerState.close() }
                },
                icon = {
                  Icon(
                      painter = painterResource(item.icon),
                      contentDescription = item.textId,
                      tint = Blue)
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding))
          }
        }
      }) {
        Scaffold(
            modifier = Modifier.fillMaxSize().testTag(title + "_drawer_scaffold"),
            topBar = {
              Box {
                CenterAlignedTopAppBar(
                    title = { Main_title(title = title) },
                    navigationIcon = { DrawerMenuIcon(scope, drawerState) },
                    actions = { iconOptions() })
                Divider(
                    color = Blue,
                    thickness = 4.dp,
                    modifier = Modifier.align(Alignment.BottomStart))
              }
            },
            bottomBar = {
              BottomNavigationBar(
                  navigationActions = navigationActions,
                  destinations = BOTTOM_NAVIGATION_DESTINATIONS,
                  currentRoute = backRoute)
            },
            content = content)
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable () -> Unit,
) {
  Box(
      modifier = Modifier.testTag("top_app_box"),
  ) {
    CenterAlignedTopAppBar(
        title = { title() },
        navigationIcon = { navigationIcon() },
        actions = { actions() },
        modifier = Modifier.testTag("top_app_bar"))
    Divider(
        color = Blue,
        thickness = 4.dp,
        modifier = Modifier.align(Alignment.BottomStart).testTag("divider"))
  }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BottomNavigationBar(
    navigationActions: NavigationActions,
    destinations: List<Destination>,
    currentRoute: String = ""
) {
  var selectedItemIndex by rememberSaveable { mutableStateOf(0) }
  NavigationBar(
      modifier =
          Modifier.clip(RoundedCornerShape(100.dp)).padding(10.dp).testTag("bottom_navigation_bar"),
      containerColor = Color.White,
      contentColor = Color.White,
      tonalElevation = 0.dp,
  ) {
    destinations.forEachIndexed { index, item ->
      NavigationBarItem(
          modifier = Modifier.testTag(item.textId + "_item"),
          selected = selectedItemIndex == index,
          enabled = (currentRoute != item.route),
          onClick = {
            selectedItemIndex = index
            navigationActions.navigateTo(item.route)
          },
          label = { Text(text = item.textId, color = Blue) },
          alwaysShowLabel = true,
          icon = {
            Icon(
                painter = painterResource(item.icon), contentDescription = item.textId, tint = Blue)
          },
          colors =
              androidx.compose.material3.NavigationBarItemDefaults.colors(
                  selectedIconColor = Color.Transparent, indicatorColor = Color.Transparent))
    }
  }
}

@Composable
fun Main_title(title: String) {
  Text(
      text = title,
      fontFamily = FontFamily(Font(R.font.coolvetica_regular)),
      fontSize = 45.sp,
      modifier = Modifier.testTag("main_title"))
}

@Composable
fun Sub_title(title: String) {
  Text(
      text = title,
      fontFamily = FontFamily(Font(R.font.coolvetica_regular)),
      fontSize = 30.sp,
      modifier = Modifier.testTag("sub_title"))
}

@Composable
fun DrawerMenuIcon(
    scope: CoroutineScope,
    drawerState: DrawerState,
) {
  IconButton(onClick = { scope.launch { drawerState.open() } }) {
    Icon(imageVector = Icons.Default.Menu, contentDescription = "Go back")
  }
}

@Composable
fun SearchIcon() {
  IconButton(onClick = { /*TODO*/}) {
    Icon(imageVector = Icons.Default.Search, tint = Blue, contentDescription = "Search groups")
  }
}

@Composable
fun GoBackRouteButton(
    navigationActions: NavigationActions,
    backRoute: String,
) {
  Icon(
      imageVector = Icons.Default.ArrowBack,
      contentDescription = "Go back",
      modifier =
          Modifier.clickable { navigationActions.navigateTo(backRoute) }.testTag("go_back_button"))
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
fun SecondaryTopBar(onClick: () -> Unit, content: @Composable RowScope.() -> Unit) {
  TopAppBar(
      modifier = Modifier.width(412.dp).height(90.dp).padding(bottom = 2.dp),
      contentColor = Color.Transparent,
      backgroundColor = Color.Transparent,
      elevation = 0.dp) {
        IconButton(onClick = { onClick() }) {
          Icon(
              painterResource(R.drawable.arrow_back),
              contentDescription = stringResource(R.string.ContentDescription_go_back_button),
              modifier = Modifier.size(28.dp))
        }
        content()
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
            modifier = Modifier.size(28.dp),
            tint = Blue)
      }
}
