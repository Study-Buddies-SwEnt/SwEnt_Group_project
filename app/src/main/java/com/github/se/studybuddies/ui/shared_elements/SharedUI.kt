package com.github.se.studybuddies.ui.shared_elements

//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.github.se.studybuddies.R
import com.github.se.studybuddies.R.string.select_a_picture
import com.github.se.studybuddies.navigation.BOTTOM_NAVIGATION_DESTINATIONS
import com.github.se.studybuddies.navigation.Destination
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.SETTINGS_DESTINATIONS
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.White
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** Main screen scaffold element. */
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
            modifier = Modifier.requiredWidth(200.dp).testTag(title + "_drawer_sheet"),
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
                modifier =
                    Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        .testTag(item.textId + "_button"))
          }
        }
      }) {
        Scaffold(
            modifier = Modifier.fillMaxSize().testTag(title + "_drawer_scaffold"),
            topBar = {
              Box(modifier = Modifier.testTag(title + "_top_app_box")) {
                CenterAlignedTopAppBar(
                    title = { Main_title(title = title) },
                    navigationIcon = { DrawerMenuIcon(scope, drawerState) },
                    actions = { iconOptions() },
                    modifier = Modifier.testTag(title + "_top_app_bar"))
                HorizontalDivider(
                    modifier = Modifier.align(Alignment.BottomStart),
                    thickness = 4.dp,
                    color = Blue)
              }
            },
            bottomBar = {
              BottomNavigationBar(
                  navigationActions = navigationActions,
                  destinations = BOTTOM_NAVIGATION_DESTINATIONS,
                  currentRoute = backRoute,
                  iconSize = 32)
            },
            content = content)
      }
}

/** Top Navigation bar present throughout almost the entirety of the app. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(
    title: @Composable () -> Unit,
    leftButton: @Composable () -> Unit,
    rightButton: @Composable () -> Unit,
) {
  Box(
      modifier = Modifier.testTag("top_app_box"),
  ) {
    CenterAlignedTopAppBar(
        title = { title() },
        navigationIcon = { leftButton() },
        actions = { rightButton() },
        modifier = Modifier.testTag("top_app_bar"))
    HorizontalDivider(
        modifier = Modifier.align(Alignment.BottomStart).testTag("divider"),
        thickness = 4.dp,
        color = Blue)
  }
}

/** Bottom navigation bar, allows also to chose icon size. */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BottomNavigationBar(
    navigationActions: NavigationActions,
    destinations: List<Destination>,
    currentRoute: String = "",
    iconSize: Int
) {
  var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
  NavigationBar(
      modifier =
          Modifier.clip(RoundedCornerShape(100.dp))
              .padding(10.dp)
              .testTag(currentRoute + "_bottom_nav_bar"),
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
                painter = painterResource(item.icon),
                contentDescription = item.textId,
                tint = Blue,
                modifier = Modifier.size(iconSize.dp))
          },
          colors =
              androidx.compose.material3.NavigationBarItemDefaults.colors(
                  selectedIconColor = Color.Transparent, indicatorColor = Color.Transparent))
    }
  }
}

/** Main title element. */
@Composable
fun Main_title(title: String) {
  Text(
      text = title,
      fontFamily = FontFamily(Font(R.font.coolvetica_regular)),
      fontSize = 45.sp,
      modifier = Modifier.testTag("main_title"))
}

/** Sub title element. */
@Composable
fun Sub_title(title: String) {
  Text(
      text = title,
      fontFamily = FontFamily(Font(R.font.coolvetica_regular)),
      fontSize = 30.sp,
      modifier = Modifier.testTag("sub_title"))
}

/** Drawer menu icon element. */
@Composable
fun DrawerMenuIcon(
    scope: CoroutineScope,
    drawerState: DrawerState,
) {
  IconButton(
      onClick = { scope.launch { drawerState.open() } },
      modifier = Modifier.testTag("drawer_menu_icon")) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = stringResource(id = R.string.go_back))
      }
}

/** Search icon element. */
@Composable
fun SearchIcon() {
  IconButton(onClick = { /*TODO*/}) {
    Icon(
        imageVector = Icons.Default.Search,
        tint = Blue,
        contentDescription = stringResource(R.string.search_groups))
  }
}

/** Go back button element. */
@Composable
fun GoBackRouteButton(
    navigationActions: NavigationActions,
    backRoute: String,
) {
  Icon(
      imageVector = Icons.AutoMirrored.Filled.ArrowBack,
      contentDescription = stringResource(id = R.string.go_back),
      modifier =
          Modifier.clickable { navigationActions.navigateTo(backRoute) }.testTag("go_back_button"))
}

/** Alternative go back button element. */
@Composable
fun GoBackRouteToLastPageButton(
    navigationActions: NavigationActions,
) {
  Icon(
      imageVector = Icons.AutoMirrored.Filled.ArrowBack,
      contentDescription = stringResource(id = R.string.go_back),
      modifier = Modifier.clickable { navigationActions.goBack() }.testTag("go_back_button"))
}

/** Secondary top bar element. */
@Composable
fun ChatTopBar(
    leftButton: @Composable () -> Unit,
    rightButton: @Composable () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
  TopAppBar(
      modifier = Modifier.fillMaxWidth(),
      contentColor = Color.White,
      backgroundColor = Color.White,
      elevation = 0.dp,
      contentPadding = PaddingValues(4.dp)) {
        leftButton()
        content()
        rightButton()
      }
  Divider(
      color = Blue,
      thickness = 4.dp,
      // modifier = Modifier.align(Alignment.BottomStart).testTag("divider")
  )
}

/** Save button element. */
@Composable
fun SaveButton(enabled: Boolean, save: () -> Unit) {
  Button(
      onClick = save,
      enabled = enabled,
      modifier =
          Modifier.padding(0.dp)
              .width(300.dp)
              .height(50.dp)
              .background(color = Color.Transparent, shape = RoundedCornerShape(size = 10.dp))
              .testTag("save_button"),
      colors =
          ButtonDefaults.buttonColors(
              containerColor = Blue,
          )) {
        Text(
            stringResource(R.string.save),
            color = White,
            modifier = Modifier.testTag("save_button_text"))
      }
}

/** Shared profile picture setting element. */
@Composable
fun DeleteButton(onClick: () -> Unit) {
  Button(
      onClick = { onClick() },
      modifier =
          Modifier.padding(0.dp)
              .width(300.dp)
              .height(45.dp)
              .background(Color.Transparent, shape = RoundedCornerShape(10.dp))
              .testTag("todo_delete"),
      colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
        Icon(
            painter = painterResource(R.drawable.delete),
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(36.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Delete", color = Color.Red)
      }
}

@Composable
fun SetPicture(photoState: MutableState<Uri>, onClick: () -> Unit) {
  Box(
      modifier = Modifier.clickable { onClick() }.testTag("set_picture"),
      contentAlignment = Alignment.Center) {
        Image(
            painter = rememberAsyncImagePainter(photoState.value),
            contentDescription = stringResource(R.string.picture),
            modifier = Modifier.size(200.dp),
            contentScale = ContentScale.Crop)
        if (photoState.value == Uri.EMPTY) {
          Spacer(Modifier.height(20.dp))
          Text(text = stringResource(select_a_picture))
        }
      }
}
