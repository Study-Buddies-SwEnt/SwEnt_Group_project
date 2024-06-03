package com.github.se.studybuddies.ui.chat

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.firebase.ui.auth.data.model.User.getUser
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.Contact
import com.github.se.studybuddies.data.ContactList
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.shared_elements.MainScreenScaffold
import com.github.se.studybuddies.ui.theme.LightBlue
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.ChatViewModel
import com.github.se.studybuddies.viewModels.ContactsViewModel
import com.github.se.studybuddies.viewModels.DirectMessagesViewModel
import com.github.se.studybuddies.viewModels.UserViewModel

@Composable
fun ContactListScreen(
    navigationActions: NavigationActions,
    contactsViewModel: ContactsViewModel,
    directMessagesViewModel: DirectMessagesViewModel
) {
    val showAddPrivateMessageList = remember { mutableStateOf(false) }

    val contacts = contactsViewModel.contacts.collectAsState().value
    val contactList = contacts.getAllTasks()

    val userVM = UserViewModel()
    val currentUID = userVM.getCurrentUserUID()

    val requests by contactsViewModel.requests.collectAsState()
    val requestList = remember { mutableStateOf(requests.getAllTasks() ?: emptyList()) }


    MainScreenScaffold(
        navigationActions = navigationActions,
        backRoute = Route.DIRECT_MESSAGE,
        content = { innerPadding ->
            if (showAddPrivateMessageList.value) {
                Box(
                    modifier =
                    Modifier.fillMaxSize().padding(innerPadding).testTag("add_private_message")) {
                    ListAllUsers(
                        showAddPrivateMessageList, directMessagesViewModel, contactsViewModel)
                }
            } else {
                if (contactList.isEmpty()) {
                    Log.d("MyPrint", "Contact list is empty")
                    Text(
                        modifier =
                        Modifier.fillMaxSize().padding(innerPadding).testTag("direct_messages_empty"),
                        text = stringResource(R.string.direct_messages_empty)
                    )
                } else {
                    Log.d("MyPrint", "DirectMessageScreen: chats is not empty")
                    Column(
                        modifier = Modifier.fillMaxSize().testTag("direct_messages_not_empty"),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
                    ) {
                        LazyColumn(
                            modifier =
                            Modifier.padding(vertical = 65.dp)
                                .fillMaxSize()
                                .background(LightBlue)
                                .testTag("direct_messages_list")) {
                            items(contactList) { contact ->

                                val friendID = contactsViewModel.getOtherUser(contact.id, currentUID)
                                val friend = userVM.getUser(friendID)
                                ContactItem(friend) {
                                    navigationActions.navigateTo("${Route.CONTACT_SETTINGS}/${contact.id}")
                                }
                            }
                        }
                    }
                }
            }

            Box(
                contentAlignment = Alignment.BottomEnd, // Aligns the button to the bottom end (right)
                modifier =
                Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding())) {
                GoToMessages(navigationActions)
            }

            /*TODO
          Box(
              contentAlignment = Alignment.BottomStart, // Aligns the button to the bottom end (right)
              modifier =
                  Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding())) {
                AddNewPrivateMessage(showAddPrivateMessageList)
              }

             */
        },
        title =
        if (showAddPrivateMessageList.value) stringResource(R.string.contact_list)
        else stringResource(R.string.direct_messages_title),
        iconOptions = {})
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactItem(friend: User, onClick: () -> Unit = {}) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
            Modifier
                .fillMaxWidth()
                .background(color = White)
                .border(color = LightBlue, width = Dp.Hairline)
                .padding(8.dp)
                .combinedClickable(onClick = { onClick() })
                .testTag("chat_item")) {
            Image(
                painter = rememberAsyncImagePainter(friend.photoUrl),
                contentDescription = stringResource(R.string.contentDescription_user_profile_picture),
                modifier =
                Modifier
                    .padding(8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
                    .align(Alignment.CenterVertically)
                    .testTag("chat_user_profile_picture"),
                contentScale = ContentScale.Crop)
            Text(text = friend.username, modifier = Modifier.testTag("chat_name"))
            Spacer(modifier = Modifier.weight(1f))
        }
    }


@Composable
fun GoToMessages(navigationActions: NavigationActions) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End) {
        IconButton(
            onClick = { navigationActions.navigateTo(Route.DIRECT_MESSAGE) },
            modifier =
            Modifier
                .width(64.dp)
                .height(64.dp)
                .clip(MaterialTheme.shapes.medium)
                .testTag("add_private_message_button")) {
            Icon(
                painterResource(id = R.drawable.messages),
                contentDescription = stringResource(R.string.contentDescription_icon_messages),
                tint = White
            )
        }
    }
}

