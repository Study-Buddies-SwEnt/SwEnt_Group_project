package com.github.se.studybuddies.viewModels

import androidx.lifecycle.ViewModel
import com.github.se.studybuddies.data.Chat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel responsible for managing chat data. It handles the storage, retrieval, and updating of
 * the current chat state.
 */
class ChatViewModel : ViewModel() {
  // A private mutable state flow to handle updates and state changes internally.
  private val _chat = MutableStateFlow<Chat?>(null)
  // A public state flow to expose the current chat state as an immutable flow.
  val chat: StateFlow<Chat?> = _chat.asStateFlow()

  /**
   * Updates the current chat in the ViewModel.
   *
   * @param chat The new chat object to set, or null to clear the current chat.
   */
  fun setChat(chat: Chat?) {
    _chat.value = chat
  }

  /**
   * Retrieves the current chat.
   *
   * @return The current chat object or null if no chat is set.
   */
  fun getChat(): Chat? {
    return _chat.value
  }
}
