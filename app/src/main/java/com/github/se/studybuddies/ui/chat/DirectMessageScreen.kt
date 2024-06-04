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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.shared_elements.MainScreenScaffold
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.LightBlue
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.ChatViewModel
import com.github.se.studybuddies.viewModels.ContactsViewModel
import com.github.se.studybuddies.viewModels.DirectMessagesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Displays the main screen for direct messaging, providing interfaces to initiate new messages and
 * manage existing ones.
 *
 * @param viewModel ViewModel that handles direct messaging operations.
 * @param chatViewModel ViewModel for chat-specific data and functions.
 * @param usersViewModel ViewModel for user data and interactions.
 * @param navigationActions Provides navigation functionality within the application.
 * @param contactsViewModel ViewModel that manages contact interactions.
 */
@Composable
fun DirectMessageScreen(
    viewModel: DirectMessagesViewModel,
    chatViewModel: ChatViewModel,
    navigationActions: NavigationActions,
    contactsViewModel: ContactsViewModel
) {
  val showAddPrivateMessageList = remember { mutableStateOf(false) }
  val chats = viewModel.directMessages.collectAsState(initial = emptyList()).value

  MainScreenScaffold(
      navigationActions = navigationActions,
      backRoute = Route.DIRECT_MESSAGE,
      content = { innerPadding ->
        if (showAddPrivateMessageList.value) {
          Box(
              modifier =
              Modifier
                  .fillMaxSize()
                  .padding(innerPadding)
                  .testTag("add_private_message")) {
                ListAllUsers(
                    showAddPrivateMessageList, viewModel, contactsViewModel)
              }
        } else {
          if (chats.isEmpty()) {
            Log.d("MyPrint", "DirectMessageScreen: chats is empty")
            Text(
                modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("direct_messages_empty"),
                text = stringResource(R.string.direct_messages_empty))
          } else {
            Log.d("MyPrint", "DirectMessageScreen: chats is not empty")
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("direct_messages_not_empty"),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
            ) {
              LazyColumn(
                  modifier =
                  Modifier
                      .padding(vertical = 65.dp)
                      .fillMaxSize()
                      .background(LightBlue)
                      .testTag("direct_messages_list")) {
                    items(chats) { chat ->
                      DirectMessageItem(chat) {
                        chatViewModel.setChat(chat)
                        navigationActions.navigateTo(Route.CHAT)
                      }
                    }
                  }
            }
          }
        }

          Box(
              contentAlignment = Alignment.BottomEnd, // Aligns the button to the bottom end (right)
              modifier =
              Modifier
                  .fillMaxSize()
                  .padding(bottom = innerPadding.calculateBottomPadding())) {
              GoToContactList(navigationActions)
          }

        Box(
            contentAlignment = Alignment.BottomStart, // Aligns the button to the bottom start (left)
            modifier =
            Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())) {
              AddNewPrivateMessage(showAddPrivateMessageList)
            }

      },
      title =
          if (showAddPrivateMessageList.value) stringResource(R.string.start_direct_message_title)
          else stringResource(R.string.direct_messages_title),
      iconOptions = {})
}

@Composable
fun GoToContactList(navigationActions: NavigationActions) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.End) {
            IconButton(
                onClick = { navigationActions.navigateTo(Route.CONTACTLIST) },
                modifier =
                Modifier
                    .width(64.dp)
                    .height(64.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(color = Blue)
                    .testTag("add_private_message_button")) {
                    Icon(
                        painterResource(id = R.drawable.user),
                        contentDescription = stringResource(R.string.contentDescription_icon_contacts),
                        modifier = Modifier.size(40.dp),
                        tint = White)
                }
            }
        }


/*TODO

make startDM take a contactID, update "HasDM", and make createContact generate the ID
change startDm so it uses an existing contactID, not the opposite
clicking on a contact will call startDM(contactID) if no chat exists

change startDM calls to sendContactRequest calls
check in sendContactRequest that the user is not already in contacts

search bar on DMscreen and chat screens
search bar for adding contacts, also change this UI in general
show on map toggle button?
generalize search bar?
showonmap feature
 */




/**
 * Displays an interactive row for adding new private messages. The button toggles its icon based on
 * the state.
 *
 * @param showAddPrivateMessageList State that controls the visibility of the user listing for new
 *   messages.
 */
@Composable
fun AddNewPrivateMessage(showAddPrivateMessageList: MutableState<Boolean>) {
  Row(
      modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
      verticalAlignment = Alignment.Bottom,
      horizontalArrangement = Arrangement.Start) {
        Button(
            onClick = { showAddPrivateMessageList.value = !showAddPrivateMessageList.value },
            modifier =
            Modifier
                .width(64.dp)
                .height(64.dp)
                .clip(MaterialTheme.shapes.medium)
                .testTag("add_private_message_button")) {
              if (showAddPrivateMessageList.value) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.create_a_task),
                    tint = White)
              } else {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.create_a_task),
                    tint = White)
              }
            }
      }
}

/**
 * Represents a single chat item in the list of direct messages.
 *
 * @param chat The chat data to display.
 * @param onClick Action to perform when the chat item is clicked.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DirectMessageItem(chat: Chat, onClick: () -> Unit = {}) {
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
            painter = rememberAsyncImagePainter(chat.picture),
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
        Text(text = chat.name, modifier = Modifier.testTag("chat_name"))
        Spacer(modifier = Modifier.weight(1f))
      }
}

/**
 * Displays a list of all users, allowing the initiation of new direct messages or managing existing
 * contacts.
 *
 * @param showAddPrivateMessageList Mutable state controlling the display of the user list.
 * @param viewModel ViewModel for direct messaging functionalities.
 * @param contactsViewModel ViewModel for contact operations.
 */
@Composable
fun ListAllUsers(
    showAddPrivateMessageList: MutableState<Boolean>,
    viewModel: DirectMessagesViewModel,
    contactsViewModel: ContactsViewModel
) {

    contactsViewModel.fetchAllUsers()
  val allUsers = contactsViewModel.allUsers.collectAsState().value
      //contactsViewModel.friends.collectAsState(initial = emptyList()).value

  var isLoading by remember { mutableStateOf(true) }

  LaunchedEffect(allUsers) {
    if (allUsers.isNotEmpty()) {
      isLoading = false
    } else {
      repeat(10) {
        delay(500)
        if (allUsers.isNotEmpty()) {
          isLoading = false
          return@repeat
        }
      }
      isLoading = false
    }
  }

  if (isLoading) {
    Box(modifier = Modifier.fillMaxSize()) {
      CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
  } else {
    if (allUsers.isEmpty()) {
      Text(text = stringResource(R.string.no_friends_found))
    } else {
      LazyColumn(modifier = Modifier
          .fillMaxWidth()
          .testTag("all_users_list")) {
        items(allUsers) { friend ->
          UserItem(friend, viewModel, showAddPrivateMessageList, contactsViewModel)
        }
      }
    }
  }
}

/**
 * Creates an individual user item for the user list, which can be clicked to start a direct message
 * or manage contacts.
 *
 * @param user The user data to display.
 * @param viewModel ViewModel for handling direct message initiation.
 * @param showAddPrivateMessageList State controlling the list display for adding new messages.
 * @param contactsViewModel ViewModel for managing contacts.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserItem(
    user: User,
    viewModel: DirectMessagesViewModel,
    showAddPrivateMessageList: MutableState<Boolean>,
    contactsViewModel: ContactsViewModel
) {

  val coroutineScope = rememberCoroutineScope()

  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier =
      Modifier
          .fillMaxWidth()
          .padding(8.dp)
          .combinedClickable(
              onClick = {
                  coroutineScope.launch {
                      showAddPrivateMessageList.value = false
                      val contactID = viewModel.startDirectMessage(user.uid)
                      Log.d("MyPrint", "DMscreen contactID is $contactID")
                      // contactsViewModel.createContact(user.uid, contactID)
                  }
              })
          .testTag("user_item")) {
        Image(
            painter = rememberAsyncImagePainter(user.photoUrl),
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
        Text(text = user.username, modifier = Modifier.testTag("chat_name"))
      }
}
