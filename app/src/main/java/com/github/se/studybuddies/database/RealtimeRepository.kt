package com.github.se.studybuddies.database

import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.ChatType
import com.github.se.studybuddies.data.Message
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

interface RealtimeRepository {
  fun getMessages(
      chat: Chat,
      liveData: MutableStateFlow<List<Message>>,
      ioDispatcher: CoroutineDispatcher,
      mainDispatcher: CoroutineDispatcher
  )

  fun sendMessage(chatUID: String, message: Message, chatType: ChatType, additionalUID: String = "")

  fun saveMessage(path: String, data: Map<String, Any>)

  fun deleteMessage(groupUID: String, message: Message, chatType: ChatType)

  fun subscribeToPrivateChats(
      userUID: String,
      scope: CoroutineScope,
      ioDispatcher: CoroutineDispatcher,
      mainDispatcher: CoroutineDispatcher,
      onUpdate: (List<Chat>) -> Unit
  )

  fun editMessage(groupUID: String, message: Message, chatType: ChatType, newText: String)

  fun votePollMessage(chat: Chat, message: Message.PollMessage)

  fun checkForExistingChat(
      currentUserUID: String,
      otherUID: String,
      onResult: (Boolean, String?) -> Unit
  )

  fun startDirectMessage(otherUID: String)

  fun getTimerUpdates(groupUID: String, timerValue: MutableStateFlow<Long>): Boolean

  suspend fun removeTopic(uid: String)
}
