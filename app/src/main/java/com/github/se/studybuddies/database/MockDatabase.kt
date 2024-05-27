package com.github.se.studybuddies.database

import android.net.Uri
import android.util.Log
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.ChatType
import com.github.se.studybuddies.data.ChatVal
import com.github.se.studybuddies.data.DailyPlanner
import com.github.se.studybuddies.data.Group
import com.github.se.studybuddies.data.GroupList
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.data.MessageVal
import com.github.se.studybuddies.data.TimerState
import com.github.se.studybuddies.data.Topic
import com.github.se.studybuddies.data.TopicFile
import com.github.se.studybuddies.data.TopicFolder
import com.github.se.studybuddies.data.TopicItem
import com.github.se.studybuddies.data.TopicList
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.testUtilities.fakeDatabase.fakeGroupDataCollection
import com.github.se.studybuddies.testUtilities.fakeDatabase.fakeTopicDataCollection
import com.github.se.studybuddies.testUtilities.fakeDatabase.fakeTopicItemCollection
import com.github.se.studybuddies.testUtilities.fakeDatabase.fakeUserDataCollection
import com.github.se.studybuddies.testUtilities.fakeDatabase.fakeUserMembershipsCollection
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.UUID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MockDatabase : DbRepository {
  private val userDataCollection = fakeUserDataCollection
  private val userMembershipsCollection = fakeUserMembershipsCollection
  private val groupDataCollection = fakeGroupDataCollection
  private val topicDataCollection = fakeTopicDataCollection
  private val topicItemCollection = fakeTopicItemCollection
  private val rtDb = mutableMapOf<String, Map<String, Any>>()
  private val storage = mutableMapOf<String, Uri>()

  override fun isFakeDatabase(): Boolean {
    return true
  }

  override suspend fun getUser(uid: String): User {
    return userDataCollection[uid] ?: User.empty()
  }

  override suspend fun getCurrentUser(): User {
    return getUser(getCurrentUserUID())
  }

  override fun getCurrentUserUID(): String {
    return "E2EUserTest"
  }

  override suspend fun getAllFriends(uid: String): List<User> {
    return try {
      val snapshot = userDataCollection[uid]
      val snapshotQuery = userDataCollection
      val items = mutableListOf<User>()

      if (snapshot != null) {
        // val userUIDs = snapshot.data?.get("friends") as? List<String>
        for (item in snapshotQuery) {
          val id = item.value.uid
          items.add(getUser(id))
        }
      } else {
        Log.d("MyPrint", "User with uid $uid does not exist")
      }
      items
    } catch (e: Exception) {
      Log.d("MyPrint", "Could not fetch friends with error: $e")
      emptyList()
    }
  }

  override suspend fun getDefaultProfilePicture(): Uri {
    return Uri.parse(
        "https://firebasestorage.googleapis.com/v0/b/study-buddies-e655a.appspot.com/o/userData%2Fdefault.jpg?alt=media&token=678a8343-a3b1-4a2e-aa5a-aeca956a3f5e")
  }

  override suspend fun createUser(
      uid: String,
      email: String,
      username: String,
      profilePictureUri: Uri,
      location: String
  ) {
    if (profilePictureUri != getDefaultProfilePicture()) {
      userDataCollection[uid] = User(uid, email, username, profilePictureUri, location)
    } else {
      userDataCollection[uid] = User(uid, email, username, getDefaultProfilePicture(), location)
    }

    val membership = hashMapOf("groups" to emptyList<String>())
    userMembershipsCollection[uid] = (membership["groups"] as List<String>).toMutableList()
  }

  override fun updateUserData(
      uid: String,
      email: String,
      username: String,
      profilePictureUri: Uri,
      location: String
  ) {
    val task = hashMapOf("email" to email, "username" to username, "location" to location)
    userDataCollection[uid] = User(uid, email, username, profilePictureUri, location)
  }

  override fun updateLocation(uid: String, location: String) {
    val user = userDataCollection[uid]
    if (user != null) {
      userDataCollection[uid] = User(uid, user.email, user.username, user.photoUrl, location)
    }
  }

  override fun userExists(
      uid: String,
      onSuccess: (Boolean) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    userDataCollection[uid]?.let { onSuccess(true) } ?: onSuccess(false)
  }

  override fun groupExists(
      groupUID: String,
      onSuccess: (Boolean) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    groupDataCollection[groupUID]?.let { onSuccess(true) } ?: onSuccess(false)
  }

  override suspend fun getAllGroups(uid: String): GroupList {
    val groupUIDs = userMembershipsCollection[uid]
    return if (groupUIDs == null) {
      Log.d("MyPrint", "User with uid $uid does not have any groups")
      GroupList(emptyList())
    } else {
      val groupList = mutableListOf<Group>()
      for (groupUID in groupUIDs) {
        val group = groupDataCollection[groupUID]
        if (group != null) {
          groupList.add(group)
        }
      }
      GroupList(groupList)
    }
  }

  override suspend fun updateGroupTimer(
      groupUID: String,
      newEndTime: Long,
      newIsRunning: Boolean
  ): Int {
    if (groupUID.isEmpty()) {
      Log.d("MyPrint", "Group UID is empty")
      return -1
    }

    val document = groupDataCollection[groupUID]
    if (document == null) {
      Log.d("MyPrint", "Group with UID $groupUID does not exist")
      return -1
    }

    // Create a map for the new timer state
    val newTimerState = mapOf("endTime" to newEndTime, "isRunning" to newIsRunning)

    // Update the timerState field in the group document
    try {
      groupDataCollection[groupUID] =
          document.copy(timerState = TimerState(newEndTime, newIsRunning))
    } catch (e: Exception) {
      Log.e("MyPrint", "Exception when updating timer: ", e)
      return -1
    }

    return 0
  }

  override suspend fun getGroup(groupUID: String): Group {
    val document = groupDataCollection[groupUID]
    return if (document != null) {
      Group(
          document.uid,
          document.name,
          document.picture,
          document.members,
          document.topics,
          document.timerState)
    } else {
      Log.d("MyPrint", "group document not found for group id $groupUID")
      Group.empty()
    }
  }

  override suspend fun getGroupName(groupUID: String): String {
    val document = groupDataCollection[groupUID]
    return if (document != null) {
      document.name
    } else {
      Log.d("MyPrint", "group document not found for group id $groupUID")
      ""
    }
  }

  override suspend fun getDefaultPicture(): Uri {
    return Uri.parse(
        "https://firebasestorage.googleapis.com/v0/b/study-buddies-e655a.appspot.com/o/userData%2Fdefault.jpg?alt=media&token=678a8343-a3b1-4a2e-aa5a-aeca956a3f5e")
  }

  override suspend fun createGroup(name: String, photoUri: Uri) {
    val uid = if (name == "Official Group Testing") "111testUser" else getCurrentUserUID()
    val timerState =
        TimerState(
            System.currentTimeMillis(),
            false) // current time as placeholder, timer is not running initially
    val groupUID = "groupTest${groupDataCollection.size}"

    if (photoUri != getDefaultPicture()) {
      val group = Group(groupUID, name, photoUri, emptyList(), emptyList(), timerState)
      groupDataCollection[uid] = group
      userMembershipsCollection[uid]?.let {
        val updatedList = it + uid
        userMembershipsCollection[uid] = updatedList.toMutableList()
      }
    } else {
      val group = Group(groupUID, name, getDefaultPicture(), emptyList(), emptyList(), timerState)
      groupDataCollection[uid] = group
      userMembershipsCollection[uid]?.let {
        val updatedList = it + uid
        userMembershipsCollection[uid] = updatedList.toMutableList()
      }
    }
  }

  override suspend fun addUserToGroup(groupUID: String, user: String) {
    if (groupUID == "") {
      Log.d("MyPrint", "Group UID is empty")
      return
    }

    // only look if userUID exist, can't find user by username
    val userToAdd: String =
        if (user == "") {
          getCurrentUserUID()
        } else {
          user
        }

    if (getUser(userToAdd) == User.empty()) {
      Log.d("MyPrint", "User with uid $userToAdd does not exist")
      return
    }

    val document = groupDataCollection[groupUID]
    if (document == null) {
      Log.d("MyPrint", "Group with uid $groupUID does not exist")
      return
    }
    // add user to group
    groupDataCollection[groupUID] = document.copy(members = document.members + userToAdd)

    // add group to the user's list of groups
    userMembershipsCollection[userToAdd]?.let {
      val updatedList = it + groupUID
      userMembershipsCollection[userToAdd] = updatedList.toMutableList()
    }
  }

  override fun updateGroup(groupUID: String, name: String, photoUri: Uri) {

    // change name of group
    groupDataCollection[groupUID] = groupDataCollection[groupUID]!!.copy(name = name)

    // change picture of group
    groupDataCollection[groupUID] = groupDataCollection[groupUID]!!.copy(picture = photoUri)
  }

  override suspend fun removeUserFromGroup(groupUID: String, userUID: String) {
    val user =
        if (userUID == "") {
          getCurrentUserUID()
        } else {
          userUID
        }
    groupDataCollection[groupUID]?.let {
      val updatedList = it.members - user
      groupDataCollection[groupUID] = it.copy(members = updatedList)
    }

    val document = groupDataCollection[groupUID]
    val members = document?.members as? List<String> ?: emptyList()

    if (members.isEmpty()) {
      groupDataCollection.remove(groupUID)
    }
  }

  override suspend fun deleteGroup(groupUID: String) {
    val document = groupDataCollection[groupUID]
    val members = document?.members ?: emptyList()

    if (members.isNotEmpty()) {
      val listSize = members.size

      for (i in 0 until listSize) {
        val user = members[i]

        userMembershipsCollection[user]?.let {
          val updatedList = it - groupUID
          userMembershipsCollection[user] = updatedList.toMutableList()
        }
      }
    }
    groupDataCollection[groupUID] = Group.empty()
  }

  override fun getGroupMessagesPath(groupUID: String): String {
    return ChatVal.GROUPS + "/$groupUID/" + ChatVal.MESSAGES
  }

  override fun getTopicMessagesPath(groupUID: String, topicUID: String): String {
    return ChatVal.GROUPS + "/$topicUID/" + ChatVal.TOPICS + "/$groupUID/" + ChatVal.MESSAGES
  }

  override fun getPrivateMessagesPath(chatUID: String): String {
    return ChatVal.DIRECT_MESSAGES + "/$chatUID/" + ChatVal.MESSAGES
  }

  override fun getPrivateChatMembersPath(chatUID: String): String {
    return ChatVal.DIRECT_MESSAGES + "/$chatUID/" + ChatVal.MEMBERS
  }

  override fun getMessagePath(chatUID: String, chatType: ChatType, additionalUID: String): String {
    return when (chatType) {
      ChatType.PRIVATE -> getPrivateMessagesPath(chatUID)
      ChatType.GROUP -> getGroupMessagesPath(chatUID)
      ChatType.TOPIC -> getTopicMessagesPath(chatUID, additionalUID)
    }
  }

  override fun saveMessage(path: String, data: Map<String, Any>) {
    rtDb[path] = data
  }

  override fun uploadChatImage(
      uid: String,
      chatUID: String,
      imageUri: Uri,
      callback: (Uri?) -> Unit
  ) {
    callback(imageUri)
  }

  override fun deleteMessage(groupUID: String, message: Message, chatType: ChatType) {
    val messagePath = getMessagePath(groupUID, chatType) + "/${message.uid}"
    rtDb.remove(messagePath)
  }

  override suspend fun removeTopic(uid: String) {
    val topic = getTopic(uid)
    // rtDb.getReference(topic.toString()).removeValue()
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
        rtDb[messagePath] = mapOf(MessageVal.TEXT to newText)
      }
      is Message.LinkMessage -> {
        val messagePath = getMessagePath(groupUID, chatType) + "/${message.uid}"
        rtDb[messagePath] = mapOf(MessageVal.LINK to newText)
      }
      else -> {
        Log.d("MyPrint", "Message type not recognized")
      }
    }
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

        uploadChatImage(message.uid, chatUID, message.photoUri) { uri ->
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
        messageData[MessageVal.PHOTO] = message.fileUri.toString()
        messageData[MessageVal.TYPE] = MessageVal.FILE
        saveMessage(messagePath, messageData)
      }
      is Message.LinkMessage -> {
        messageData[MessageVal.LINK] = message.linkUri.toString()
        messageData[MessageVal.TYPE] = MessageVal.LINK
        saveMessage(messagePath, messageData)
      }
      else -> {
        Log.d("MyPrint", "Message type not recognized")
      }
    }
  }

  override fun subscribeToPrivateChats(
      userUID: String,
      scope: CoroutineScope,
      ioDispatcher: CoroutineDispatcher,
      mainDispatcher: CoroutineDispatcher,
      onUpdate: (List<Chat>) -> Unit
  ) {
    val ref = rtDb[ChatVal.DIRECT_MESSAGES]

    object : ValueEventListener {
      override fun onDataChange(snapshot: DataSnapshot) {
        scope.launch(ioDispatcher) {
          val chatList =
              snapshot.children.mapNotNull { chat ->
                val members = chat.child(ChatVal.MEMBERS).children.mapNotNull { it.key }
                if (userUID in members) {
                  val otherUserId = members.firstOrNull { it != userUID }
                  otherUserId?.let { userId ->
                    val otherUser = getUser(userId)
                    val currentUser = getUser(userUID)
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
    }
  }

  override fun getMessages(
      chat: Chat,
      liveData: MutableStateFlow<List<Message>>,
      ioDispatcher: CoroutineDispatcher,
      mainDispatcher: CoroutineDispatcher
  ) {
    val ref = rtDb[getMessagePath(chat.uid, chat.type, chat.additionalUID)]

    object : ValueEventListener {
      override fun onDataChange(snapshot: DataSnapshot) {
        CoroutineScope(ioDispatcher).launch {
          val newMessages =
              snapshot.children.mapNotNull { postSnapshot ->
                val senderUID = postSnapshot.child(MessageVal.SENDER_UID).value.toString()
                val timestamp =
                    postSnapshot.child(MessageVal.TIMESTAMP).value.toString().toLongOrNull()
                        ?: return@mapNotNull null
                val user = getUser(senderUID)
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
        Log.w("DatabaseConnection - getMessages()", "Failed to read value.", error.toException())
      }
    }
  }

  override fun checkForExistingChat(
      currentUserUID: String,
      otherUID: String,
      onResult: (Boolean, String?) -> Unit
  ) {
    val query =
        rtDb[ChatVal.DIRECT_MESSAGES]
            ?.filterValues { it is Map<*, *> }
            ?.mapValues { it.value as Map<*, *> }
            ?.filterValues { it[ChatVal.MEMBERS] is List<*> }
            ?.mapValues { it.value[ChatVal.MEMBERS] as List<*> }
            ?.filterValues { otherUID in it }

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
        Log.w("DatabaseConnect", "Failed to check for existing chat", databaseError.toException())
        onResult(false, null)
      }
    }
  }

  override fun startDirectMessage(otherUID: String) {
    val currentUserUID = getCurrentUserUID()
    checkForExistingChat(currentUserUID, otherUID) { chatExists, chatId ->
      if (chatExists) {
        Log.d("MyPrint", "startDirectMessage: chat already exists with ID: $chatId")
      } else {
        Log.d("MyPrint", "startDirectMessage: creating new chat")
        val newChatId = UUID.randomUUID().toString()
        val memberPath = getPrivateChatMembersPath(newChatId)
        val members = mapOf(currentUserUID to true, otherUID to true)
        rtDb[memberPath] = members
      }
    }
  }

  override suspend fun getTopic(uid: String): Topic {
    val topic = topicDataCollection[uid]
    if (topic != null) {
      return topic
    } else {
      Log.d("MyPrint", "topic document not found for id $uid")
      return Topic.empty()
    }
  }

  override suspend fun fetchTopicItems(listUID: List<String>): List<TopicItem> {
    val items = mutableListOf<TopicItem>()
    for (itemUID in listUID) {
      val document = topicItemCollection[itemUID]
      if (document != null) {
        items.add(document)
        /*
        val name = document.name ?: ""
        val parentUID = document.parentUID ?: ""
        when (document) {
          is TopicFolder -> {
            val folderItemsList = document.items
            val folderItems = fetchTopicItems(folderItemsList)
            items.add(TopicFolder(itemUID, name, folderItems, parentUID))
          }
          is TopicFile -> {
            val strongUsers = document.get(DatabaseConnection.item_strongUsers) as List<String>
            items.add(TopicFile(itemUID, name, strongUsers, parentUID))
          }
        }*/
      } else Log.d("MyPrint", "topic item document not found for id $itemUID")
    }
    return items
  }

  private fun uploadChatFile(uid: String, chatUID: String, fileUri: Uri, callback: (Uri?) -> Unit) {
    val storagePath = "chatData/$chatUID/$uid"
    val fileRef = storage[storagePath]
    storage[storagePath] = fileUri
  }

  override fun createTopic(name: String, callBack: (String) -> Unit) {
    val topicUID = "topicTest${topicDataCollection.size}"
    topicDataCollection[topicUID] = Topic(topicUID, name, emptyList(), emptyList())
  }

  override suspend fun addTopicToGroup(topicUID: String, groupUID: String) {
    val document = groupDataCollection[groupUID]
    if (document != null) {
      groupDataCollection[groupUID] = document.copy(topics = document.topics + topicUID)
    }
  }

  override fun addExercise(uid: String, exercise: TopicItem) {
    val exerciseUID = exercise.uid
    topicDataCollection[exerciseUID] = exercise as Topic
  }

  override fun addTheory(uid: String, theory: TopicItem) {
    val theoryUID = theory.uid
    topicDataCollection[theoryUID] = theory as Topic
  }

  override suspend fun deleteTopic(topicId: String) {
    topicDataCollection.remove(topicId)
  }

  override fun updateTopicName(uid: String, name: String) {
    topicDataCollection[uid] = topicDataCollection[uid]!!.copy(name = name)
  }

  override fun createTopicFolder(name: String, parentUID: String, callBack: (TopicFolder) -> Unit) {
    val folderUID = "folderTest${topicItemCollection.size}"
    topicItemCollection[folderUID] = TopicFolder(folderUID, name, emptyList(), parentUID)
  }

  override fun createTopicFile(name: String, parentUID: String, callBack: (TopicFile) -> Unit) {
    val fileUID = "fileTest${topicItemCollection.size}"
    topicItemCollection[fileUID] = TopicFile(fileUID, name, emptyList(), parentUID)
  }

  override fun updateTopicItem(item: TopicItem) {
    topicItemCollection[item.uid] = item
  }

  override fun getTimerUpdates(groupUID: String, _timerValue: MutableStateFlow<Long>): Boolean {
    var isRunning = false
    groupUID?.let { uid ->
      val timerState = rtDb["timer/$uid"] as? TimerState
      timerState?.let {
        _timerValue.value = it.endTime - System.currentTimeMillis()
        isRunning = it.isRunning
      }
    } ?: error("Group UID is not set. Call setup() with valid Group UID.")
    return isRunning
  }

  override fun getAllTopics(
      groupUID: String,
      scope: CoroutineScope,
      ioDispatcher: CoroutineDispatcher,
      mainDispatcher: CoroutineDispatcher,
      onUpdate: (TopicList) -> Unit
  ) {
    val group = groupDataCollection[groupUID]

    if (group != null) {
      scope.launch(ioDispatcher) {
        val items = mutableListOf<Topic>()
        val topicUIDs = group.topics
        if (topicUIDs.isNotEmpty()) {
          topicUIDs
              .map { topicUid -> async { getTopic(topicUid) } }
              .awaitAll()
              .forEach { topic -> items.add(topic) }
        } else {
          Log.d("MyPrint", "List of topics is empty for this group")
        }

        withContext(mainDispatcher) { onUpdate(TopicList(items)) }
      }
    } else {
      Log.d("MyPrint", "Group with uid $groupUID does not exist")
      onUpdate(TopicList(emptyList()))
    }
  }

  override fun updateDailyPlanners(uid: String, dailyPlanners: List<DailyPlanner>) {
    userDataCollection[uid] = userDataCollection[uid]!!.copy(dailyPlanners = dailyPlanners)
  }

  companion object {
    const val topic_name = "name"
    const val topic_exercises = "exercises"
    const val topic_theory = "theory"
    const val item_parent = "parent"
    const val item_type = "type"
    const val item_items = "items"
    const val item_strongUsers = "strongUsers"
  }
}
