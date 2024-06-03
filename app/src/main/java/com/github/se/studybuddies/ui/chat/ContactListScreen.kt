package com.github.se.studybuddies.ui.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.viewModels.ChatViewModel
import com.github.se.studybuddies.viewModels.ContactsViewModel
import com.github.se.studybuddies.viewModels.DirectMessagesViewModel

@Composable
fun ContactListScreen(
    navigationActions: NavigationActions,
    contactsViewModel: ContactsViewModel
) {
    val showAddPrivateMessageList = remember { mutableStateOf(false) }
    val chats = viewModel.directMessages.collectAsState(initial = emptyList()).value
    val contacts by contactsViewModel.contacts.collectAsState()
    val contactList = remember { mutableStateOf(contacts.getAllTasks() ?: emptyList()) }
