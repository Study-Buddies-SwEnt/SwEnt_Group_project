package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class GroupsHomeScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<GroupsHomeScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("GroupsHomeScreen") }) {

  val drawerScaffold: KNode = onNode { hasTestTag("Groups_drawer_scaffold") }
  val groupScreenEmpty: KNode = drawerScaffold.child { hasTestTag("GroupEmpty") }
  val groupBox: KNode = drawerScaffold.child { hasTestTag("GroupsBox") }
  val circularLoading: KNode = groupBox.child { hasTestTag("CircularLoading") }

  val emptyGroupText: KNode = groupScreenEmpty.child { hasTestTag("EmptyGroupText") }

  val addButtonRow: KNode = groupScreenEmpty.child { hasTestTag("AddGroupRow") }
  val addButton: KNode = addButtonRow.child { hasTestTag("AddGroupButton") }
  val addButtonIcon: KNode = addButton.child { hasTestTag("AddGroupIcon") }

  val addLinkRow: KNode = groupScreenEmpty.child { hasTestTag("AddLinkRow") }
  val addLinkButton: KNode = addLinkRow.child { hasTestTag("AddLinkButton") }
  val addLinkIcon: KNode = addLinkButton.child { hasTestTag("AddLinkIcon") }
  val addLinkTextField: KNode = addLinkRow.child { hasTestTag("AddLinkTextField") }

  val groupScreen: KNode = drawerScaffold.child { hasTestTag("GroupsHome") }
  val groupList: KNode = groupScreen.child { hasTestTag("GroupsList") }

  val testGroupBox: KNode = groupList.child { hasTestTag("TestGroup_box") }

  // Structural elements of the UI
  val loginTitle: KNode = child { hasTestTag("LoginTitle") }
  val loginButton: KNode = child { hasTestTag("LoginButton") }

  val GroupsSettingsButton: KNode = onNode { hasTestTag("GroupsSettingsButtonText") }
  val DropDownMenu: KNode = onNode { hasTestTag("DropDownMenuText") }
  val DropDownMenuItem: KNode = onNode { hasTestTag("DropDownMenuItemText") }
  val textDialogues: KNode = onNode { hasTestTag("LeaveGroupDialogText") }
  val textDialoguesYes: KNode = onNode { hasTestTag("LeaveGroupDialogYesButton") }
  val textDialoguesNo: KNode = onNode { hasTestTag("LeaveGroupDialogNoButton") }

  val topAppBox: KNode = drawerScaffold.child { hasTestTag("Groups_top_app_box") }
  val topAppBar: KNode = topAppBox.child { hasTestTag("Groups_top_app_bar") }
  val drawerMenuButton: KNode = topAppBar.child { hasTestTag("drawer_menu_icon") }
  val drawerSheet: KNode = onNode { hasTestTag("Groups_drawer_sheet") }
  val settingsButton: KNode = drawerSheet.child { hasTestTag("Settings_button") }
  val accountButton: KNode = drawerSheet.child { hasTestTag("Account_button") }
  val groupsTitle: KNode = topAppBar.child { hasTestTag("main_title") }

  val groupBottomBar: KNode = drawerScaffold.child { hasTestTag("GroupsHome_bottom_nav_bar") }
  val soloStudyBottom: KNode = onNode { hasTestTag("Solo study_item") }
  val groupsBottom: KNode = onNode { hasTestTag("Groups_item") }
  val messagesBottom: KNode = onNode { hasTestTag("Messages_item") }
  val mapBottom: KNode = onNode { hasTestTag("Map_item") }
}
