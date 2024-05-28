package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class AccountSettingsScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<AccountSettingsScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("account_settings_column") }) {

  val topAppBox: KNode = onNode { hasTestTag("top_app_box") }
  val topAppBar: KNode = onNode { hasTestTag("top_app_bar") }
  val goBackButton: KNode = topAppBar.child { hasTestTag("go_back_button") }
  val divider: KNode = onNode { hasTestTag("divider") }
  val subTitle: KNode = onNode { hasTestTag("sub_title") }

  val columnAccountSetting: KNode = onNode { hasTestTag("account_settings_column") }
  val spacer1: KNode = columnAccountSetting.child { hasTestTag("account_settings_spacer1") }
  val spacer2: KNode = columnAccountSetting.child { hasTestTag("account_settings_spacer2") }
  val spacer3: KNode = columnAccountSetting.child { hasTestTag("account_settings_spacer3") }
  val signOutButton: KNode = columnAccountSetting.child { hasTestTag("sign_out_button") }
}
