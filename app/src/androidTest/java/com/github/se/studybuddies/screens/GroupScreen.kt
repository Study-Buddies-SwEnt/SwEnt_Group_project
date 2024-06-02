package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class GroupScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<GroupScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("GroupScreen") }) {

  val topAppBox: KNode = child { hasTestTag("top_app_box") }
  val topAppBar: KNode = topAppBox.child { hasTestTag("top_app_bar") }
  val goBackButton: KNode = topAppBar.child { hasTestTag("go_back_button") }
  val divider: KNode = onNode { hasTestTag("divider") }

  val floatingActionRow: KNode = onNode { hasTestTag("floating_action_row") }
  val createTopicButton: KNode = floatingActionRow.child { hasTestTag("create_topic_button") }

  val groupBottomBar: KNode = onNode { hasTestTag("Group_bottom_nav_bar") }
  val videoCallButton: KNode = onNode { hasTestTag("Video Call_item") }
  val timerButton: KNode = onNode { hasTestTag("Timer_item") }

  val groupScreenColumn: KNode = onNode { hasTestTag("GroupScreenColumn") }
  val groupBox: KNode = groupScreenColumn.child { hasTestTag("GroupBox") }
}
