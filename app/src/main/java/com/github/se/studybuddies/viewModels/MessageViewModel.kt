package com.github.se.studybuddies.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.ChatType
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.database.DbRepository
import com.github.se.studybuddies.database.ServiceLocator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * ViewModel that handles messaging functionalities for a specific chat. It manages the retrieval,
 * filtering, sending, and editing of messages within a chat.
 *
 * @param chat The chat session this ViewModel is associated with.
 * @param db The database repository for data operations. Defaults to a ServiceLocator provided
 *   instance.
 */
class MessageViewModel(
    val chat: Chat,
    private val db: DbRepository = ServiceLocator.provideDatabase()
) : ViewModel() {
  // Holds a list of messages, providing thread-safe updates.
  private val _messages = MutableStateFlow<List<Message>>(emptyList())
  // Holds the current user information to identify the message sender.
  private val _currentUser = MutableLiveData<User>()
  val currentUser = _currentUser

  // Filter for displaying messages of a specific type.
  private val _filterType = MutableStateFlow<Class<out Message>?>(null)
  val filterType: StateFlow<Class<out Message>?> = _filterType.asStateFlow()
  // Holds the search query to filter messages based on text content.
  private val _searchQuery = MutableStateFlow("")

  /**
   * A flow that emits a list of messages filtered by type and search query and sorted by timestamp.
   * This combines several sources of data to provide a dynamically updating filtered list of
   * messages.
   */
  val messages =
      combine(_messages, _searchQuery, _filterType) { messages, query, filterType ->
        messages
            .filter { message ->
              // Filter by type if not null
              filterType?.isInstance(message) ?: true
            }
            .filter { message ->
              // Filter by search query if not empty
              if (query.isEmpty()) true
              else
                  when (message) {
                    is Message.TextMessage -> message.text.contains(query, ignoreCase = true)
                    is Message.LinkMessage -> message.linkName.contains(query, ignoreCase = true)
                    is Message.FileMessage -> message.fileName.contains(query, ignoreCase = true)
                    is Message.PollMessage ->
                        message.question.contains(query, ignoreCase = true) ||
                            message.options.any { it.contains(query, ignoreCase = true) }
                    else -> false
                  }
            }
            .sortedBy { it.timestamp }
      }

  init {
    getMessage()
    getCurrentUser()
  }

  fun setFilterType(type: Class<out Message>?) {
    Log.d("MessageViewModel", "setFilterType: $type")
    _filterType.value = type
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  private fun getMessage() {
    // Retrieve messages for the chat from the database.
    db.getMessages(chat, _messages, Dispatchers.IO, Dispatchers.Main)
  }

  private fun getCurrentUser() {
    // Asynchronously fetch the current user and set it.
    viewModelScope.launch { _currentUser.value = db.getCurrentUser() }
  }

  fun sendTextMessage(text: String) {
    // Compose and send a text message.
    val message =
        _currentUser.value?.let {
          Message.TextMessage(text = text, sender = it, timestamp = System.currentTimeMillis())
        }

    sendMessage(message!!)
  }

  fun sendLinkMessage(linkName: String, linkUri: Uri) {
    // Compose and send a link message.
    val message =
        _currentUser.value?.let {
          Message.LinkMessage(
              linkName = linkName,
              linkUri = linkUri,
              sender = it,
              timestamp = System.currentTimeMillis())
        }

    sendMessage(message!!)
  }

  fun sendPhotoMessage(photoUri: Uri) {
    // Compose and send a photo message.
    val message =
        _currentUser.value?.let {
          Message.PhotoMessage(
              photoUri = photoUri, sender = it, timestamp = System.currentTimeMillis())
        }
    sendMessage(message!!)
  }

  fun sendFileMessage(fileName: String, fileUri: Uri) {
    // Compose and send a file message.
    val message =
        _currentUser.value?.let {
          Message.FileMessage(
              fileName = fileName,
              fileUri = fileUri,
              sender = it,
              timestamp = System.currentTimeMillis())
        }
    sendMessage(message!!)
  }

  fun sendPollMessage(question: String, singleChoice: Boolean, options: List<String>) {
    // Compose and send a poll message.
    val message =
        _currentUser.value?.let {
          Message.PollMessage(
              question = question,
              singleChoice = singleChoice,
              options = options,
              votes = mutableMapOf(),
              sender = it,
              timestamp = System.currentTimeMillis())
        }
    sendMessage(message!!)
  }

  fun votePollMessage(message: Message.PollMessage, option: String) {
    // Handle voting on a poll message.
    val currentUserUID = _currentUser.value?.uid ?: return

    val updatedVotes = message.votes.toMutableMap()
    if (message.singleChoice) {
      // Remove user's previous votes if it's a single-choice poll
      updatedVotes.keys.forEach { opt ->
        if (opt != option) {
          updatedVotes[opt] = updatedVotes[opt]?.filter { it.uid != currentUserUID } ?: emptyList()
        }
      }
    }

    // For both single and multiple-choice polls
    val currentVotes = updatedVotes[option]?.toMutableList() ?: mutableListOf()
    if (currentVotes.any { it.uid == currentUserUID }) {
      // Remove vote if already selected
      currentVotes.removeAll { it.uid == currentUserUID }
    } else {
      // Add vote if not selected
      currentVotes.add(_currentUser.value!!)
    }
    updatedVotes[option] = currentVotes

    // Create a new message instance to trigger recomposition
    val updatedMessage = message.copy(votes = updatedVotes)

    // Update the message in the database
    db.votePollMessage(chat, updatedMessage)

    // Update the local state immutably
    _messages.value = _messages.value.map { if (it.uid == message.uid) updatedMessage else it }
  }

  private fun sendMessage(message: Message) {
    if (chat.type == ChatType.TOPIC)
        db.sendMessage(chat.uid, message, chat.type, chat.additionalUID)
    else db.sendMessage(chat.uid, message, chat.type)
  }

  fun deleteMessage(message: Message) {
    if (!isUserMessageSender(message)) return
    else {
      db.deleteMessage(chat.uid, message, chat.type)
      _messages.value = _messages.value.filter { it.uid != message.uid }
    }
  }

  fun editMessage(message: Message, newText: String) {
    if (!isUserMessageSender(message)) return
    // Check if the message UID exists in the list before proceeding.
    val messageExists = _messages.value.any { it.uid == message.uid }
    if (!messageExists) return

    // Update the message in the database
    db.editMessage(chat.uid, message, chat.type, newText)

    // Update the message in the local state
    _messages.value =
        _messages.value.map {
          if (it.uid == message.uid) {
            when (it) {
              is Message.TextMessage -> {
                it.copy(text = newText)
              }
              is Message.LinkMessage -> {
                it.copy(linkUri = Uri.parse(newText))
              }
              else -> {
                it
              }
            }
          } else it
        }
  }

  /**
   * Checks if the current user is the sender of a given message.
   *
   * @param message The message to check.
   * @return True if the current user is the sender, false otherwise.
   */
  fun isUserMessageSender(message: Message): Boolean {
    return if (_currentUser.value != null) {
      message.sender.uid == _currentUser.value!!.uid
    } else {
      false
    }
  }
}
