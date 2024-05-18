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
  val messageBubble: KNode = onNode { hasTestTag("chat_text_bubble") }
  val textBubbleImage: KNode = messageBubble.child { hasTestTag("chat_user_profile_picture") }
  val textBubbleBox: KNode = messageBubble.child { hasTestTag("chat_text_bubble_box") }
  val textBubbleName: KNode = textBubbleBox.child { hasTestTag("chat_message_sender_name") }
  val textBubbleText: KNode = textBubbleBox.child { hasTestTag("chat_message_text") }
  val textBubbleTime: KNode = textBubbleBox.child { hasTestTag("chat_message_time") }

  val photoMessage: KNode = textBubbleBox.child { hasTestTag("chat_message_image") }
  val linkMessage: KNode = textBubbleBox.child { hasTestTag("chat_message_link") }
  val fileMessage: KNode = textBubbleBox.child { hasTestTag("chat_message_file") }

  val optionDialog: KNode = onNode { hasTestTag("option_dialog") }
  val optionDialogEdit: KNode = optionDialog.child { hasTestTag("option_dialog_edit") }
  val optionDialogDeleteButton: KNode = optionDialog.child { hasTestTag("option_dialog_delete") }
  val optionDialogStartPMButton: KNode = onNode { hasTestTag("option_dialog_start_direct_message") }

  val editDialog: KNode = onNode { hasTestTag("edit_dialog") }
  val editDialogTextField: KNode = editDialog.child { hasTestTag("chat_text_field") }
  val editDialogSave: KNode = onNode { hasTestTag("save_button") }
  val chatGroupTitleImage: KNode = onNode { hasTestTag("group_title_profile_picture") }
  val chatGroupTitleText: KNode = onNode { hasTestTag("group_title_name") }
  val chatGroupTitleMembers: KNode = onNode { hasTestTag("group_title_members_row") }
  val chatGroupTitleMember: KNode =
      chatGroupTitleMembers.child { hasTestTag("group_title_member_name") }

  val chatPrivateTitleImage: KNode = onNode { hasTestTag("private_title_profile_picture") }
  val chatPrivateTitleText: KNode = onNode { hasTestTag("private_title_name") }

  val sendMoreMessagesType: KNode = onNode { hasTestTag("dialog_more_messages_types") }
  val sendMoreMessagesTypePhoto: KNode = onNode { hasTestTag("icon_send_image") }
  val sendMoreMessagesTypeLink: KNode = onNode { hasTestTag("icon_send_link") }
  val sendMoreMessagesTypeFile: KNode = onNode { hasTestTag("icon_send_file") }

  val sendPhotoMessageDialog: KNode = onNode { hasTestTag("add_image_dialog") }
  val sendPhotoMessageBox: KNode = onNode { hasTestTag("add_image_box") }
  val sendPhotoMessageSetPicture: KNode = sendPhotoMessageBox.child { hasTestTag("set_picture") }
  val sendPhotoMessageSaveButton: KNode = onNode { hasTestTag("save_button") }

  val sendLinkMessageDialog: KNode = onNode { hasTestTag("add_link_dialog") }
  val sendLinkMessageBox: KNode = onNode { hasTestTag("add_link_box") }
  val sendLinkMessageTextFiled: KNode =
      sendLinkMessageBox.child { hasTestTag("add_link_text_field") }
  val sendLinkMessageSaveButton: KNode = onNode { hasTestTag("save_button") }

  val sendFileMessageDialog: KNode = onNode { hasTestTag("add_file_dialog") }
  val sendFileMessageBox: KNode = onNode { hasTestTag("add_file_box") }
  val sendFileMessageSaveButton: KNode = onNode { hasTestTag("save_button") }
}
