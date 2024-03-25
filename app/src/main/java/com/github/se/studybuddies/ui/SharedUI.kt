package com.github.se.studybuddies.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.SETTINGS_DESTINATIONS
import com.github.se.studybuddies.ui.groups.GroupItem
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
            var selectedItemIndex by rememberSaveable {
                mutableIntStateOf(0)
            }
            ModalDrawerSheet {
                Spacer(modifier = Modifier.size(16.dp))
                SETTINGS_DESTINATIONS.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        label = { Text(item.textId) },
                        selected = index == selectedItemIndex,
                        onClick = {
                            navigationActions.navigateTo("${item.route}/$backRoute")
                            selectedItemIndex = index
                            scope.launch { drawerState.close() }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = item.textId
                            )
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { MainTopBar{ scope.launch{drawerState.open()} }},
            content = content
        )
    }

}

@Composable
fun MainTopBar(onClick: () -> Unit) {
    TopAppBar(
        modifier = Modifier
            .width(412.dp)
            .height(90.dp)
            .padding(bottom = 2.dp),
        contentColor = Color.Transparent,
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    ) {
        IconButton(
            onClick = { onClick()
            Log.d("MyPrint", "Clicked on the menu button")}
        ) {
            Icon(
                painterResource(R.drawable.menu),
                contentDescription = "Settings",
                modifier = Modifier.size(28.dp)
            )
        }
    }

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
        elevation = 0.dp
    ) {
        IconButton(
            onClick = { onClick() }
        ) {
            Icon(
                painterResource(R.drawable.arrow_back),
                contentDescription = "Go back button",
                modifier = Modifier.size(28.dp)
            )
        }
    }


}