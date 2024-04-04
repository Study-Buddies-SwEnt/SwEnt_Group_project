package com.github.se.studybuddies.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import com.github.se.studybuddies.ui.theme.Red
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerMenu(
    navigationActions: NavigationActions,
    backRoute: String,
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
                label = { Text(item.textId,color = Red) },
                selected = false,
                onClick = {
                  navigationActions.navigateTo("${item.route}/$backRoute")
                  selectedItemIndex = index
                  scope.launch { drawerState.close() }
                },
                icon = {
                  Icon(painter = painterResource(item.icon), contentDescription = item.textId,tint = Red)
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding))
          }
        }
      }) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopNavigationBar(navigationActions = navigationActions,scope,drawerState)
            },
            bottomBar = {
                BottomNavigationBar(navigationActions = navigationActions, destinations = BOTTOM_NAVIGATION_DESTINATIONS)
            },
            content = content)
      }
}

@Composable
fun MainTopBar(content: @Composable() (RowScope.() -> Unit)) {
  TopAppBar(
      modifier = Modifier
          .width(412.dp)
          .height(90.dp)
          .padding(bottom = 2.dp),
      contentColor = Color.Transparent,
      backgroundColor = Color.Transparent,
      elevation = 0.dp,
      content = content)
}

@Composable
fun SecondaryTopBar(onClick: () -> Unit) {
  TopAppBar(
      modifier = Modifier
          .width(412.dp)
          .height(90.dp)
          .padding(bottom = 2.dp),
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
            modifier = Modifier.size(28.dp),
            tint = Red)
      }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BottomNavigationBar(
    navigationActions: NavigationActions,
    destinations: List<Destination>
){
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(0)
    }

    NavigationBar (
         modifier = Modifier
             .clip(RoundedCornerShape(50.dp))
             .padding(8.dp),

    ){
        destinations.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItemIndex == index,
                onClick = {
                    selectedItemIndex = index
                    navigationActions.navigateTo(item.route)
                    },
                label = {
                    Text(text = item.textId, color = Red)
                },
                alwaysShowLabel = true,
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = item.textId,
                        tint = Red
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TopNavigationBar(
    navigationActions: NavigationActions,
    scope : CoroutineScope,
    drawerState: DrawerState
){
    val expandedState = remember { mutableStateOf(false) }
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Study Buddies",
                fontFamily = FontFamily(Font(R.font.playball_regular)),
                fontSize = 45.sp,
            )
        },
        navigationIcon = {
            IconButton(onClick = {  scope.launch {
                drawerState.open()
            } }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Go back"
                )
            }
        },
            actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    tint = Red,
                    contentDescription = "Search groups"
                )
            }
        },
        //modifier = Modifier.height(50.dp)
    )
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
        Modifier
            .fillMaxWidth()
            .clickable {
                val groupUid = group.uid
                navigationActions.navigateTo("${Route.GROUP}/$groupUid")
            }
            .drawBehind {
                val strokeWidth = 1f
                val y = size.height - strokeWidth / 2
                drawLine(Color.LightGray, Offset(0f, y), Offset(size.width, y), strokeWidth)
            }) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Image(
                painter = rememberImagePainter(group.picture),
                contentDescription = "Group profile picture",
                modifier = Modifier.size(32.dp),
                contentScale = ContentScale.Crop)
            Text(text = group.name, style = TextStyle(fontSize = 16.sp), lineHeight = 28.sp)
        }
    }
}
