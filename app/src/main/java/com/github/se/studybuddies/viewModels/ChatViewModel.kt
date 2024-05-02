package com.github.se.studybuddies.viewModels

import androidx.lifecycle.ViewModel
import com.github.se.studybuddies.data.Chat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatViewModel : ViewModel() {
  private val _chat = MutableStateFlow<Chat?>(null)
  val chat: StateFlow<Chat?> = _chat.asStateFlow()

  fun setChat(chat: Chat?) {
    _chat.value = chat
  }

  fun getChat(): Chat? {
    return _chat.value
  }
}
