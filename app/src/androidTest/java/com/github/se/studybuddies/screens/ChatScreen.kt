package com.github.se.studybuddies.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class ChatScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ChatScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("chat_screen") }) {

  val textField: KNode = onNode { hasTestTag("chat_text_field") }
  val sendButton: KNode = textField.child { hasTestTag("chat_send_button") }

  val chatMessage: KNode = onNode { hasTestTag("chat_message_row") }
  val textBubble: KNode = chatMessage.child { hasTestTag("chat_text_bubble") }
  val textBubbleImage: KNode = textBubble.child { hasTestTag("chat_user_profile_picture") }
  val textBubbleBox: KNode = textBubble.child { hasTestTag("chat_text_bubble_box") }
  val textBubbleName: KNode = textBubbleBox.child { hasTestTag("chat_message_sender_name") }
  val textBubbleText: KNode = textBubbleBox.child { hasTestTag("chat_message_text") }
  val textBubbleTime: KNode = textBubbleBox.child { hasTestTag("chat_message_time") }

  val optionDialog: KNode = onNode { hasTestTag("option_dialog") }
  val optionDialogEdit: KNode = optionDialog.child { hasTestTag("option_dialog_edit") }
  val optionDialogDeleteButton: KNode = optionDialog.child { hasTestTag("option_dialog_delete") }
  val optionDialogCancelButton: KNode = optionDialog.child { hasTestTag("option_dialog_cancel") }

  val editDialog: KNode = onNode { hasTestTag("edit_dialog") }
  val editDialogTextField: KNode = editDialog.child { hasTestTag("chat_text_field") }
  val editDialogSaveButton: KNode = editDialog.child { hasTestTag("chat_send_button") }
  val editDialogCancelButton: KNode = editDialog.child { hasTestTag("edit_dialog_cancel") }

  val chatGroupTitleImage: KNode = onNode { hasTestTag("group_title_profile_picture") }
  val chatGroupTitleText: KNode = onNode { hasTestTag("group_title_name") }
  val chatGroupTitleMembers: KNode = onNode { hasTestTag("group_title_members_row") }
  val chatGroupTitleMember: KNode =
      chatGroupTitleMembers.child { hasTestTag("group_title_member_name") }
}
