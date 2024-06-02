package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class GroupSettingScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<GroupSettingScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("groupSettingScaffold") }) {

  val topAppBox: KNode = child { hasTestTag("top_app_box") }
  val topAppBar: KNode = topAppBox.child { hasTestTag("top_app_bar") }
  val goBackButton: KNode = topAppBar.child { hasTestTag("go_back_button") }
  val divider: KNode = onNode { hasTestTag("divider") }

  val settingColumn: KNode = onNode { hasTestTag("setting_column") }
  val settingLazyColumn: KNode = settingColumn.child { hasTestTag("setting_lazy_column") }
  val spacer1: KNode = settingLazyColumn.child { hasTestTag("setting_spacer1") }
  val spacer2: KNode = settingLazyColumn.child { hasTestTag("setting_spacer2") }
  val spacer3: KNode = settingLazyColumn.child { hasTestTag("setting_spacer3") }
  val spacer4: KNode = settingLazyColumn.child { hasTestTag("setting_spacer4") }
  val modifyName: KNode = settingLazyColumn.child { hasTestTag("group_name_field") }

  val imagePP: KNode = settingLazyColumn.child { hasTestTag("image_pp") }
  val spacerPP: KNode = settingLazyColumn.child { hasTestTag("spacer_pp") }
  val buttonPP: KNode = settingLazyColumn.child { hasTestTag("set_picture_button") }

  val addMemberColumn: KNode = settingLazyColumn.child { hasTestTag("add_member_column") }
  val addMemberButton: KNode = addMemberColumn.child { hasTestTag("add_member_button") }
  val addMemberText: KNode = addMemberButton.child { hasTestTag("add_member_button_text") }

  val contactColumn: KNode = onNode { hasTestTag("contact_box") }
  val contactLazyColumn: KNode = onNode { hasTestTag("contact_lazy_column") }
}
