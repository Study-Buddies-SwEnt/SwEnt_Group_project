package com.github.se.studybuddies.database

import android.net.Uri
import android.util.Log
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.ChatType
import com.github.se.studybuddies.data.ChatVal
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.data.MessageVal
import com.github.se.studybuddies.data.TimerState
import com.github.se.studybuddies.data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.UUID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RealtimeConnection : RealtimeRepository {
  private val db =
      Firebase.database(
          "https://study-buddies-e655a-default-rtdb.europe-west1.firebasedatabase.app/")

  private fun getMessagePath(
      chatUID: String,
      chatType: ChatType,
      additionalUID: String = ""
  ): String {
    return when (chatType) {
      ChatType.PRIVATE -> getPrivateMessagesPath(chatUID)
      ChatType.GROUP -> getGroupMessagesPath(chatUID)
      ChatType.TOPIC -> getTopicMessagesPath(chatUID, additionalUID)
    }
  }

  private fun getGroupMessagesPath(groupUID: String): String {
    return ChatVal.GROUPS + "/$groupUID/" + ChatVal.MESSAGES
  }

  private fun getTopicMessagesPath(groupUID: String, topicUID: String): String {
    return ChatVal.GROUPS + "/$topicUID/" + ChatVal.TOPICS + "/$groupUID/" + ChatVal.MESSAGES
  }

  private fun getPrivateMessagesPath(chatUID: String): String {
    return ChatVal.DIRECT_MESSAGES + "/$chatUID/" + ChatVal.MESSAGES
  }

  private fun getPrivateChatMembersPath(chatUID: String): String {
    return ChatVal.DIRECT_MESSAGES + "/$chatUID/" + ChatVal.MEMBERS
  }

  override fun getMessages(
      chat: Chat,
      liveData: MutableStateFlow<List<Message>>,
      ioDispatcher: CoroutineDispatcher,
      mainDispatcher: CoroutineDispatcher
  ) {
    val ref = db.getReference(getMessagePath(chat.uid, chat.type, chat.additionalUID))

    ref.addValueEventListener(
        object : ValueEventListener {
          override fun onDataChange(snapshot: DataSnapshot) {
            CoroutineScope(ioDispatcher).launch {
              val newMessages =
                  snapshot.children.mapNotNull { postSnapshot ->
                    val senderUID = postSnapshot.child(MessageVal.SENDER_UID).value.toString()
                    val timestamp =
                        postSnapshot.child(MessageVal.TIMESTAMP).value.toString().toLongOrNull()
                            ?: return@mapNotNull null
                    val user = DatabaseConnection().getUser(senderUID)
                    when (val type = postSnapshot.child(MessageVal.TYPE).value.toString()) {
                      MessageVal.TEXT -> {
                        val text = postSnapshot.child(MessageVal.TEXT).value.toString()
                        Message.TextMessage(postSnapshot.key.toString(), text, user, timestamp)
                      }
                      MessageVal.PHOTO -> {
                        val photoUri =
                            postSnapshot.child(MessageVal.PHOTO).value.toString().let(Uri::parse)
                        Message.PhotoMessage(postSnapshot.key.toString(), photoUri, user, timestamp)
                      }
                      MessageVal.FILE -> {
                        val fileUri =
                            postSnapshot.child(MessageVal.FILE).value.toString().let(Uri::parse)
                        val fileName = postSnapshot.child(MessageVal.FILE_NAME).value.toString()
                        Message.FileMessage(
                            postSnapshot.key.toString(), fileName, fileUri, user, timestamp)
                      }
                      MessageVal.LINK -> {
                        val linkUri =
                            postSnapshot.child(MessageVal.LINK).value.toString().let(Uri::parse)
                        val linkName = postSnapshot.child(MessageVal.LINK_NAME).value.toString()
                        Message.LinkMessage(
                            postSnapshot.key.toString(), linkName, linkUri, user, timestamp)
                      }
                      MessageVal.POLL -> {
                        val question = postSnapshot.child(MessageVal.POLL_QUESTION).value.toString()
                        val singleChoice =
                            postSnapshot
                                .child(MessageVal.POLL_SINGLE_CHOICE)
                                .value
                                .toString()
                                .toBoolean()
                        val options =
                            postSnapshot.child(MessageVal.POLL_OPTIONS).value.toString().split(",")
                        val votes = mutableMapOf<String, List<User>>()
                        val votesSnapshot = postSnapshot.child(MessageVal.POLL_VOTES)
                        if (votesSnapshot.exists()) {
                          votesSnapshot.children.forEach { voteEntry ->
                            val option = voteEntry.key.toString()
                            val userUIDs = voteEntry.value.toString().split(",")
                            val users = userUIDs.map { uid -> DatabaseConnection().getUser(uid) }
                            votes[option] = users
                          }
                        }
                        Message.PollMessage(
                            postSnapshot.key.toString(),
                            question,
                            singleChoice,
                            options,
                            votes,
                            user,
                            timestamp)
                      }
                      else -> {
                        Log.d("MyPrint", "Message type not recognized: $type")
                        null
                      }
                    }
                  }

              // Post new message list to the main thread to update the UI
              withContext(mainDispatcher) { liveData.value = newMessages }
            }
          }

          override fun onCancelled(error: DatabaseError) {
            Log.w(
                "DatabaseConnection - getMessages()", "Failed to read value.", error.toException())
          }
        })
  }

  override fun sendMessage(
      chatUID: String,
      message: Message,
      chatType: ChatType,
      additionalUID: String
  ) {
    val messagePath = getMessagePath(chatUID, chatType, additionalUID) + "/${message.uid}"

    val messageData =
        mutableMapOf(
            MessageVal.SENDER_UID to message.sender.uid, MessageVal.TIMESTAMP to message.timestamp)
    when (message) {
      is Message.TextMessage -> {
        messageData[MessageVal.TEXT] = message.text
        messageData[MessageVal.TYPE] = MessageVal.TEXT
        saveMessage(messagePath, messageData)
      }
      is Message.PhotoMessage -> {
        DatabaseConnection().uploadChatImage(message.uid, chatUID, message.photoUri) { uri ->
          if (uri != null) {
            Log.d("MyPrint", "Successfully uploaded photo with uri: $uri")
            messageData[MessageVal.PHOTO] = uri.toString()
            messageData[MessageVal.TYPE] = MessageVal.PHOTO
            saveMessage(messagePath, messageData)
          } else {
            Log.d("MyPrint", "Failed to upload photo")
          }
        }
      }
      is Message.FileMessage -> {
        DatabaseConnection().uploadChatFile(message.uid, chatUID, message.fileUri) { uri ->
          if (uri != null) {
            Log.d("MyPrint", "Successfully uploaded file with uri: $uri")
            messageData[MessageVal.FILE] = uri.toString()
            messageData[MessageVal.FILE_NAME] = message.fileName
            messageData[MessageVal.TYPE] = MessageVal.FILE
            saveMessage(messagePath, messageData)
          } else {
            Log.d("MyPrint", "Failed to upload file")
          }
        }
      }
      is Message.LinkMessage -> {
        messageData[MessageVal.LINK] = message.linkUri.toString()
        messageData[MessageVal.TYPE] = MessageVal.LINK
        saveMessage(messagePath, messageData)
      }
      is Message.PollMessage -> {
        messageData[MessageVal.POLL_QUESTION] = message.question
        messageData[MessageVal.POLL_SINGLE_CHOICE] = message.singleChoice
        messageData[MessageVal.POLL_OPTIONS] = message.options.joinToString(",")
        val votesMap = message.votes.mapValues { entry -> entry.value.joinToString(",") { it.uid } }
        messageData[MessageVal.POLL_VOTES] =
            votesMap.entries.joinToString(",") { "${it.key}:${it.value}" }
        messageData[MessageVal.TYPE] = MessageVal.POLL
        saveMessage(messagePath, messageData)
      }
    }
  }

  override fun saveMessage(path: String, data: Map<String, Any>) {
    db.getReference(path)
        .updateChildren(data)
        .addOnSuccessListener { Log.d("MessageSend", "Message successfully written!") }
        .addOnFailureListener { e -> Log.w("MessageSend", "Failed to write message.", e) }
  }

  override fun deleteMessage(groupUID: String, message: Message, chatType: ChatType) {
    val messagePath = getMessagePath(groupUID, chatType) + "/${message.uid}"
    db.getReference(messagePath).removeValue()
  }

  override fun subscribeToPrivateChats(
      userUID: String,
      scope: CoroutineScope,
      ioDispatcher: CoroutineDispatcher,
      mainDispatcher: CoroutineDispatcher,
      onUpdate: (List<Chat>) -> Unit
  ) {
    val ref = db.getReference(ChatVal.DIRECT_MESSAGES)

    ref.addValueEventListener(
        object : ValueEventListener {
          override fun onDataChange(snapshot: DataSnapshot) {
            scope.launch(ioDispatcher) {
              val chatList =
                  snapshot.children.mapNotNull { chat ->
                    val members = chat.child(ChatVal.MEMBERS).children.mapNotNull { it.key }
                    if (userUID in members) {
                      val otherUserId = members.firstOrNull { it != userUID }
                      otherUserId?.let { userId ->
                        val otherUser = DatabaseConnection().getUser(userId)
                        val currentUser = DatabaseConnection().getUser(userUID)
                        Chat(
                            uid = chat.key ?: "",
                            name = otherUser.username,
                            picture = otherUser.photoUrl,
                            type = ChatType.PRIVATE,
                            members = listOf(otherUser, currentUser))
                      }
                    } else {
                      null
                    }
                  }

              withContext(mainDispatcher) { onUpdate(chatList.sortedBy { it.name }) }
            }
          }

          override fun onCancelled(error: DatabaseError) {
            println("Database read failed: " + error.code)
          }
        })
  }

  override fun editMessage(
      groupUID: String,
      message: Message,
      chatType: ChatType,
      newText: String
  ) {
    when (message) {
      is Message.TextMessage -> {
        val messagePath = getMessagePath(groupUID, chatType) + "/${message.uid}"
        db.getReference(messagePath).updateChildren(mapOf(MessageVal.TEXT to newText))
      }
      is Message.LinkMessage -> {
        val messagePath = getMessagePath(groupUID, chatType) + "/${message.uid}"
        db.getReference(messagePath).updateChildren(mapOf(MessageVal.LINK to newText))
      }
      else -> {
        Log.d("MyPrint", "Message type not recognized")
      }
    }
  }

  override fun votePollMessage(chat: Chat, message: Message.PollMessage) {
    val messagePath =
        getMessagePath(chat.uid, chat.type, chat.additionalUID) +
            "/${message.uid}/${MessageVal.POLL_VOTES}"
    val reference = db.getReference(messagePath)

    reference.runTransaction(
        object : Transaction.Handler {
          override fun doTransaction(currentData: MutableData): Transaction.Result {
            val updatedVotesMap = mutableMapOf<String, String>()
            message.votes.forEach { (option, users) ->
              val userIds = users.filter { it.uid.isNotEmpty() }.joinToString(",") { it.uid }
              if (userIds.isNotEmpty()) {
                updatedVotesMap[option] = userIds
              }
            }

            currentData.value = if (updatedVotesMap.isEmpty()) null else updatedVotesMap
            return Transaction.success(currentData)
          }

          override fun onComplete(
              error: DatabaseError?,
              committed: Boolean,
              currentData: DataSnapshot?
          ) {
            if (error != null) {
              Log.w("DatabaseConnection", "Failed to update poll vote", error.toException())
            } else {
              Log.d("DatabaseConnection", "Poll vote successfully updated")
            }
          }
        })
  }

  override fun checkForExistingChat(
      currentUserUID: String,
      otherUID: String,
      onResult: (Boolean, String?) -> Unit
  ) {
    val query =
        db.getReference(ChatVal.DIRECT_MESSAGES)
            .orderByChild("${ChatVal.MEMBERS}/$currentUserUID")
            .equalTo(true)

    query.addListenerForSingleValueEvent(
        object : ValueEventListener {
          override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.children.forEach { chatSnapshot ->
              if (chatSnapshot.hasChild("${ChatVal.MEMBERS}/$otherUID")) {
                onResult(true, chatSnapshot.key)
                return
              }
            }
            onResult(false, null)
          }

          override fun onCancelled(databaseError: DatabaseError) {
            Log.w(
                "DatabaseConnect", "Failed to check for existing chat", databaseError.toException())
            onResult(false, null)
          }
        })
  }

  override fun startDirectMessage(otherUID: String) {
    val currentUserUID = DatabaseConnection().getCurrentUserUID()
    checkForExistingChat(currentUserUID, otherUID) { chatExists, chatId ->
      if (chatExists) {
        Log.d("MyPrint", "startDirectMessage: chat already exists with ID: $chatId")
      } else {
        Log.d("MyPrint", "startDirectMessage: creating new chat")
        val newChatId = UUID.randomUUID().toString()
        val memberPath = getPrivateChatMembersPath(newChatId)
        val members = mapOf(currentUserUID to true, otherUID to true)
        db.getReference(memberPath)
            .updateChildren(members)
            .addOnSuccessListener {
              Log.d("DatabaseConnect", "startDirectMessage : Members successfully added!")
            }
            .addOnFailureListener {
              Log.w("DatabaseConnect", "startDirectMessage : Failed to write members.", it)
            }
      }
    }
  }

  override fun getTimerUpdates(groupUID: String, timerValue: MutableStateFlow<Long>): Boolean {
    var isRunning = false
    groupUID.let { uid ->
      val timerRef = db.getReference("timer/$uid")
      timerRef.addValueEventListener(
          object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
              snapshot.getValue(TimerState::class.java)?.let { timerState ->
                timerValue.value = timerState.endTime - System.currentTimeMillis()
                isRunning = timerState.isRunning
              }
            }

            override fun onCancelled(error: DatabaseError) {
              Log.e("TimerViewModel", "Failed to read timer", error.toException())
            }
          })
    }
    return isRunning
  }

  override suspend fun removeTopic(uid: String) {
    DatabaseConnection().getTopic(uid) { topic -> db.getReference(topic.toString()).removeValue() }
  }
}
