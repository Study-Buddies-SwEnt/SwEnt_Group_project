package com.github.se.studybuddies.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.database.DbRepository
import com.github.se.studybuddies.database.ServiceLocator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing direct message interactions, handling chat retrieval and
 * updates.
 *
 * @param userUid The user identifier for whom the chats are relevant. Defaults to an empty string
 *   if not specified.
 * @param db An instance of DbRepository for database operations, defaulting to a provided database
 *   instance.
 */
class DirectMessageViewModel(
    private val userUid: String = "",
    private val db: DbRepository = ServiceLocator.provideDatabase()
) : ViewModel() {
  // Private MutableStateFlow for user UID
  private val _userUid = MutableStateFlow(userUid)
  // Private MutableStateFlow to handle updates in the list of direct messages
  private val _directMessages = MutableStateFlow<List<Chat>>(emptyList())
  // Publicly exposed flow of direct messages, sorted by chat name
  val directMessages =
      _directMessages.asStateFlow().map { directMessages -> directMessages.sortedBy { it.name } }

  init {
    // Launches a coroutine that subscribes to updates in private chats related to the user
    viewModelScope.launch {
      _userUid.collect { userUid ->
        if (userUid.isNotEmpty()) {
          db.subscribeToPrivateChats(userUid, viewModelScope, Dispatchers.IO, Dispatchers.Main) {
              chats ->
            _directMessages.value = chats
          }
        }
      }
    }
  }

  /**
   * Sets a new user UID for the ViewModel. If the new UID is different from the current one, it
   * updates the internal state.
   *
   * @param userUID The new user UID to set.
   */
  fun setUserUID(userUID: String) {
    if (_userUid.value != userUID) {
      _userUid.value = userUID
    }
  }

  /**
   * Initiates a direct message with another user.
   *
   * @param messageUserUID The UID of the user with whom to start a direct message.
   */
  fun startDirectMessage(messageUserUID: String) {
    db.startDirectMessage(messageUserUID)
  }
}
