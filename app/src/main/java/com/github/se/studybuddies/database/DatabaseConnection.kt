package com.github.se.studybuddies.database

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.ChatType
import com.github.se.studybuddies.data.ChatVal
import com.github.se.studybuddies.data.Group
import com.github.se.studybuddies.data.GroupList
import com.github.se.studybuddies.data.ItemType
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.data.MessageVal
import com.github.se.studybuddies.data.Topic
import com.github.se.studybuddies.data.TopicFile
import com.github.se.studybuddies.data.TopicFolder
import com.github.se.studybuddies.data.TopicItem
import com.github.se.studybuddies.data.TopicList
import com.github.se.studybuddies.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class DatabaseConnection {
  private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
  private val storage = FirebaseStorage.getInstance().reference

  val rt_db =
      Firebase.database(
          "https://study-buddies-e655a-default-rtdb.europe-west1.firebasedatabase.app/")

  // all collections
  private val userDataCollection = db.collection("userData")
  private val userMembershipsCollection = db.collection("userMemberships")
  private val groupDataCollection = db.collection("groupData")
  private val topicDataCollection = db.collection("topicData")
  private val topicItemCollection = db.collection("topicItemData")

  // using the userData collection
  suspend fun getUser(uid: String): User {
    if (uid.isEmpty()) {
      return User.empty()
    }
    val document = userDataCollection.document(uid).get().await()
    return if (document.exists()) {
      val email = document.getString("email") ?: ""
      val username = document.getString("username") ?: ""
      val photoUrl = Uri.parse(document.getString("photoUrl") ?: "")
      User(uid, email, username, photoUrl)
    } else {
      Log.d("MyPrint", "user document not found for id $uid")
      User.empty()
    }
  }

  suspend fun getCurrentUser(): User {
    return getUser(getCurrentUserUID())
  }

  fun getCurrentUserUID(): String {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    return if (uid != null) {
      Log.d("MyPrint", "Fetched user UID is $uid")
      uid
    } else {
      Log.d("MyPrint", "Failed to get current user UID")
      ""
    }
  }

  suspend fun getGroupName(groupUID: String): String {
    val document = groupDataCollection.document(groupUID).get().await()
    return if (document.exists()) {
      document.getString("name") ?: ""
    } else {
      Log.d("MyPrint", "group document not found for group id $groupUID")
      ""
    }
  }

  suspend fun getDefaultProfilePicture(): Uri {
    return storage.child("userData/default.jpg").downloadUrl.await()
  }

  suspend fun createUser(uid: String, email: String, username: String, profilePictureUri: Uri) {
    Log.d(
        "MyPrint",
        "Creating new user with uid $uid, email $email, username $username and picture link ${profilePictureUri.toString()}")
    val user =
        hashMapOf(
            "email" to email, "username" to username, "photoUrl" to profilePictureUri.toString())
    if (profilePictureUri != getDefaultProfilePicture()) {
      userDataCollection
          .document(uid)
          .set(user)
          .addOnSuccessListener {
            val pictureRef = storage.child("userData/$uid/profilePicture.jpg")
            pictureRef
                .putFile(profilePictureUri)
                .addOnSuccessListener {
                  pictureRef.downloadUrl.addOnSuccessListener { uri ->
                    userDataCollection.document(uid).update("photoUrl", uri.toString())
                  }
                  Log.d("MyPrint", "User data successfully created")
                }
                .addOnFailureListener { e ->
                  Log.d(
                      "MyPrint",
                      "Failed to upload photo with error with link $profilePictureUri: ",
                      e)
                }
            Log.d("MyPrint", "User data successfully created for uid $uid")
          }
          .addOnFailureListener { e ->
            Log.d("MyPrint", "Failed to create user data with error: ", e)
          }
    } else {
      // If the profile picture URI is the default one, copy it to the user's folder
      val defaultPictureRef = storage.child("userData/default.jpg")
      val profilePictureRef = storage.child("userData/$uid/profilePicture.jpg")

      defaultPictureRef
          .getBytes(Long.MAX_VALUE)
          .addOnSuccessListener { defaultPictureData ->
            profilePictureRef
                .putBytes(defaultPictureData)
                .addOnSuccessListener {
                  // Once the default picture is uploaded, update the user data with the correct
                  // photo URL
                  profilePictureRef.downloadUrl.addOnSuccessListener { uri ->
                    val updatedUserData = user + mapOf("photoUrl" to uri.toString())
                    userDataCollection
                        .document(uid)
                        .set(updatedUserData)
                        .addOnSuccessListener {
                          Log.d("MyPrint", "User data successfully created for uid $uid")
                        }
                        .addOnFailureListener { e ->
                          Log.d("MyPrint", "Failed to update user data with error: ", e)
                        }
                  }
                }
                .addOnFailureListener { e ->
                  Log.d(
                      "MyPrint",
                      "Failed to upload default profile picture for user $uid with error: ",
                      e)
                }
          }
          .addOnFailureListener { e ->
            Log.d("MyPrint", "Failed to retrieve default profile picture with error: ", e)
          }
    }

    val membership = hashMapOf("groups" to emptyList<String>())
    userMembershipsCollection
        .document(uid)
        .set(membership)
        .addOnSuccessListener { Log.d("MyPrint", "User memberships successfully created") }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to create user memberships with error: ", e)
        }
  }

  fun updateUserData(uid: String, email: String, username: String, profilePictureUri: Uri) {
    val task = hashMapOf("email" to email, "username" to username)
    userDataCollection
        .document(uid)
        .update(task as Map<String, Any>)
        .addOnSuccessListener {
          val pictureRef = storage.child("userData/$uid/profilePicture.jpg")
          pictureRef
              .putFile(profilePictureUri)
              .addOnSuccessListener {
                pictureRef.downloadUrl.addOnSuccessListener { uri ->
                  userDataCollection.document(uid).update("photoUrl", uri.toString())
                }
              }
              .addOnFailureListener { e ->
                Log.d("MyPrint", "Failed to upload photo with error: ", e)
              }
          Log.d("MyPrint", "User data successfully updated")
        }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to update user data with error: ", e)
        }
  }

  fun userExists(uid: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
    userDataCollection
        .document(uid)
        .get()
        .addOnSuccessListener { document -> onSuccess(document.exists()) }
        .addOnFailureListener { e -> onFailure(e) }
  }

  // using the groups & userMemberships collections
  suspend fun getAllGroups(uid: String): GroupList {
    try {
      val snapshot = userMembershipsCollection.document(uid).get().await()
      val items = mutableListOf<Group>()

      if (snapshot.exists()) {
        val groupUIDs = snapshot.data?.get("groups") as? List<String>
        groupUIDs?.let { groupsIDs ->
          groupsIDs.forEach { groupUID ->
            val document = groupDataCollection.document(groupUID).get().await()
            val name = document.getString("name") ?: ""
            val photo = Uri.parse(document.getString("picture") ?: "")
            val members = document.get("members") as? List<String> ?: emptyList()
            val topics = document.get("topics") as? List<String> ?: emptyList()
            items.add(Group(groupUID, name, photo, members, topics))
          }
        }
        return GroupList(items)
      } else {
        Log.d("MyPrint", "User with uid $uid does not exist")
        return GroupList(emptyList())
      }
    } catch (e: Exception) {
      Log.d("MyPrint", "In ViewModel, could not fetch groups with error: $e")
    }
    return GroupList(emptyList())
  }

  suspend fun getGroup(groupUID: String): Group {
    val document = groupDataCollection.document(groupUID).get().await()
    return if (document.exists()) {
      val name = document.getString("name") ?: ""
      val picture = Uri.parse(document.getString("picture") ?: "")
      val members = document.get("members") as List<String>
      val topics = document.get("topics") as List<String>
      Group(groupUID, name, picture, members, topics)
    } else {
      Log.d("MyPrint", "group document not found for group id $groupUID")
      Group.empty()
    }
  }

  suspend fun getDefaultPicture(): Uri {
    return storage.child("groupData/default_group.jpg").downloadUrl.await()
  }

  suspend fun createGroup(name: String, photoUri: Uri) {
    val uid = if (name == "Official Group Testing") "111testUser" else getCurrentUserUID()
    Log.d("MyPrint", "Creating new group with uid $uid and picture link ${photoUri.toString()}")
    val group =
        hashMapOf(
            "name" to name,
            "picture" to photoUri.toString(),
            "members" to listOf(uid),
            "topics" to emptyList<String>())
    if (photoUri != getDefaultPicture()) {
      groupDataCollection
          .add(group)
          .addOnSuccessListener { documentReference ->
            val groupUID = documentReference.id
            userMembershipsCollection
                .document(uid)
                .update("groups", FieldValue.arrayUnion(groupUID))
                .addOnSuccessListener { Log.d("MyPrint", "Group successfully created") }
                .addOnFailureListener { e ->
                  Log.d("MyPrint", "Failed to update user memberships with error: ", e)
                }
            val pictureRef = storage.child("groupData/$groupUID/picture.jpg")
            pictureRef
                .putFile(photoUri)
                .addOnSuccessListener {
                  pictureRef.downloadUrl.addOnSuccessListener { uri ->
                    groupDataCollection.document(groupUID).update("picture", uri.toString())
                  }
                }
                .addOnFailureListener { e ->
                  Log.d("MyPrint", "Failed to upload photo with error: ", e)
                }
          }
          .addOnFailureListener { e -> Log.d("MyPrint", "Failed to create group with error: ", e) }
    } else {
      val defaultPictureRef = storage.child("groupData/default_group.jpg")
      val pictureRef = storage.child("groupData/$uid/picture.jpg")

      groupDataCollection.add(group).addOnSuccessListener { documentReference ->
        val groupUID = documentReference.id
        userMembershipsCollection
            .document(uid)
            .update("groups", FieldValue.arrayUnion(groupUID))
            .addOnSuccessListener { Log.d("MyPrint", "Group successfully created") }
            .addOnFailureListener { e ->
              Log.d("MyPrint", "Failed to update user memberships with error: ", e)
            }
        defaultPictureRef
            .getBytes(Long.MAX_VALUE)
            .addOnSuccessListener { defaultPictureData ->
              pictureRef
                  .putBytes(defaultPictureData)
                  .addOnSuccessListener {
                    pictureRef.downloadUrl.addOnSuccessListener { uri ->
                      val updatedGroupData = group + mapOf("picture" to uri.toString())
                      groupDataCollection
                          .document(groupUID)
                          .set(updatedGroupData)
                          .addOnSuccessListener {
                            Log.d("MyPrint", "Group data successfully created for uid $uid")
                          }
                          .addOnFailureListener { e ->
                            Log.d("MyPrint", "Failed to update group data with error: ", e)
                          }
                    }
                  }
                  .addOnFailureListener { e ->
                    Log.d(
                        "MyPrint",
                        "Failed to upload default picture for group $uid with error: ",
                        e)
                  }
            }
            .addOnFailureListener { e ->
              Log.d("MyPrint", "Failed to retrieve default picture with error: ", e)
            }
      }
    }
  }

  suspend fun updateGroup(groupUID: String): Int {
    if (groupUID == "") {
      Log.d("MyPrint", "Group UID is empty")
      return -1
    }

    val document = groupDataCollection.document(groupUID).get().await()
    if (!document.exists()) {
      Log.d("MyPrint", "Group with uid $groupUID does not exist")
      return -1
    }
    val userToAdd = getCurrentUserUID()

    // add user to group
    groupDataCollection
        .document(groupUID)
        .update("members", FieldValue.arrayUnion(userToAdd))
        .addOnSuccessListener { Log.d("MyPrint", "User successfully added to group") }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to add user to group with error: ", e)
        }

    // add group to the user's list of groups
    userMembershipsCollection
        .document(userToAdd)
        .update("groups", FieldValue.arrayUnion(groupUID))
        .addOnSuccessListener { Log.d("MyPrint", "Group successfully added to user") }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to add group to user with error: ", e)
        }
    return 0
  }

  // using the Realtime Database for messages
  fun sendMessage(UID: String, message: Message, chatType: ChatType) {
    val messagePath = getMessagePath(UID, chatType) + "/${message.uid}"
    val messageData =
        mapOf(
            MessageVal.TEXT to message.text,
            MessageVal.SENDER_UID to message.sender.uid,
            MessageVal.TIMESTAMP to message.timestamp)
    rt_db
        .getReference(messagePath)
        .updateChildren(messageData)
        .addOnSuccessListener { Log.d("MessageSend", "Message successfully written!") }
        .addOnFailureListener { Log.w("MessageSend", "Failed to write message.", it) }
  }

  fun deleteMessage(groupUID: String, message: Message, chatType: ChatType) {
    val messagePath = getMessagePath(groupUID, chatType) + "/${message.uid}"
    rt_db.getReference(messagePath).removeValue()
  }

  fun editMessage(groupUID: String, message: Message, chatType: ChatType, newText: String) {
    val messagePath = getMessagePath(groupUID, chatType) + "/${message.uid}"
    rt_db.getReference(messagePath).updateChildren(mapOf(MessageVal.TEXT to newText))
  }

  private fun getMessagePath(UID: String, chatType: ChatType, additionalUID: String = ""): String {
    return when (chatType) {
      ChatType.PRIVATE -> getPrivateMessagesPath(UID)
      ChatType.GROUP -> getGroupMessagesPath(UID)
      ChatType.TOPIC -> getTopicMessagesPath(UID, additionalUID)
    }
  }

  private fun getGroupMessagesPath(groupUID: String): String {
    return ChatVal.GROUPS + "/$groupUID/" + ChatVal.MESSAGES
  }

  private fun getTopicMessagesPath(groupUID: String, topicUID: String): String {
    return ChatVal.GROUPS + "/$groupUID/" + ChatVal.TOPICS + "/$topicUID/" + ChatVal.MESSAGES
  }

  private fun getPrivateMessagesPath(chatUID: String): String {
    return ChatVal.DIRECT_MESSAGES + "/$chatUID/" + ChatVal.MESSAGES
  }

  private fun getPrivateChatMembersPath(chatUID: String): String {
    return ChatVal.DIRECT_MESSAGES + "/$chatUID/" + ChatVal.MEMBERS
  }

  fun getPrivateChatsList(userUID: String, liveData: MutableStateFlow<List<Chat>>) {
    val ref = rt_db.getReference(ChatVal.DIRECT_MESSAGES)

    CoroutineScope(Dispatchers.IO).launch {
      val chatList = mutableListOf<Chat>()

      val chatsSnapshot = ref.get().await()

      chatsSnapshot.children.forEach { chat ->
        val members = chat.child(ChatVal.MEMBERS).children.map { it.key }.toList()
        if (userUID in members) {
          val otherUserId = members.first { it != userUID }

          Log.d("MyPrint", "Found chat with other user ID: $otherUserId")

          if (otherUserId != null) {
            getUser(otherUserId).let { otherUser ->
              val otherUserName = otherUser.username
              val otherUserPhotoUrl = otherUser.photoUrl.toString()

              // Create a new Chat object with the other user's name and photo URL
              val messages = MutableStateFlow<List<Message>>(emptyList())
              getMessages(chat.key ?: "", ChatType.PRIVATE, messages)
              Log.d("MyPrint", "Chat key ${chat.key}")
              Log.d("MyPrint", "Messages: ${messages.value}")
              val newChat =
                  Chat(
                      uid = chat.key ?: "",
                      name = otherUserName,
                      photoUrl = otherUserPhotoUrl,
                      type = ChatType.PRIVATE,
                      members = listOf(otherUser, getUser(userUID)),
                  /*messages = messages.value*/ )
              chatList.add(newChat)
            }
          }
        }
      }

      liveData.value = chatList
    }
  }

  fun getMessages(uid: String, chatType: ChatType, liveData: MutableStateFlow<List<Message>>) {
    val ref = rt_db.getReference(getMessagePath(uid, chatType))

    ref.addValueEventListener(
        object : ValueEventListener {
          private var handler = Handler(Looper.getMainLooper())
          private var runnable: Runnable? = null

          override fun onDataChange(snapshot: DataSnapshot) {
            runnable?.let { handler.removeCallbacks(it) }
            runnable = Runnable {
              CoroutineScope(Dispatchers.IO).launch {
                val newMessages = mutableListOf<Message>()

                // Process snapshot data to fetch user details and create message objects
                for (postSnapshot in snapshot.children) {
                  val text = postSnapshot.child(MessageVal.TEXT).value.toString()
                  val senderUID = postSnapshot.child(MessageVal.SENDER_UID).value.toString()
                  val timestamp = postSnapshot.child(MessageVal.TIMESTAMP).value.toString().toLong()

                  // Assuming db.getUser is adapted to fetch user details without being suspending
                  val user = getUser(senderUID)

                  val message = Message(postSnapshot.key.toString(), text, user, timestamp)
                  newMessages.add(message)
                }

                // Post new message list to the main thread to update the UI
                withContext(Dispatchers.Main) { liveData.value = newMessages }
              }
            }
            handler.postDelayed(runnable!!, 500)
          }

          override fun onCancelled(error: DatabaseError) {
            Log.w(
                "DatabaseConnection - getMessages()", "Failed to read value.", error.toException())
          }
        })
  }

  fun startDirectMessage(otherUID: String) {
    val memberPath = getPrivateChatMembersPath(UUID.randomUUID().toString())
    val members = mapOf(getCurrentUserUID() to true, otherUID to true)
    val listChat = MutableStateFlow<List<Chat>>(emptyList())

    getPrivateChatsList(otherUID, listChat)

    CoroutineScope(Dispatchers.Default).launch {
      listChat.collect { chats ->
        chats.forEach { chat ->
          if (chat.members.none { user -> user.uid == getCurrentUserUID() }) {
            rt_db
                .getReference(memberPath)
                .updateChildren(members)
                .addOnSuccessListener { Log.d("DatabaseConnect", "Members successfully added!") }
                .addOnFailureListener { Log.w("DatabaseConnect", "Failed to write members.", it) }
          }
        }
      }
    }
  }

  // using the topicData collection
  suspend fun getTopic(uid: String): Topic {
    val document = topicDataCollection.document(uid).get().await()
    return if (document.exists()) {
      val name = document.getString("name") ?: ""
      val exercisesList = document.get("exercises") as List<String>
      val theoryList = document.get("theory") as List<String>
      val exercises = fetchTopicItems(exercisesList)
      val theory = fetchTopicItems(theoryList)
      Topic(uid, name, exercises, theory)
    } else {
      Log.d("MyPrint", "topic document not found for id $uid")
      Topic.empty()
    }
  }

  private suspend fun fetchTopicItems(uids: List<String>): List<TopicItem> {
    val items = mutableListOf<TopicItem>()
    for (itemUID in uids) {
      val document = topicItemCollection.document(itemUID).get().await()
      if (document.exists()) {
        val name = document.getString("name") ?: ""
        val type = ItemType.valueOf(document.getString("type") ?: ItemType.FILE.toString())
        when (type) {
          ItemType.FOLDER -> {
            val folderItemsList = document.get("items") as List<String> ?: emptyList()
            val folderItems = fetchTopicItems(folderItemsList)
            items.add(TopicFolder(itemUID, name, folderItems))
          }
          ItemType.FILE -> {
            val strongUsers = document.get("strongUsers") as List<String> ?: emptyList()
            items.add(TopicFile(itemUID, name, strongUsers))
          }
        }
      }
    }
    return items
  }

  suspend fun createTopic(name: String) {
    val topic =
        hashMapOf(
            "name" to name, "exercises" to emptyList<String>(), "theory" to emptyList<String>())
    topicDataCollection
        .add(topic)
        .addOnSuccessListener { Log.d("MyPrint", "topic successfully created") }
        .addOnFailureListener { e -> Log.d("MyPrint", "Failed to create topic with error ", e) }
  }

  fun updateTopicData(
      uid: String,
      name: String,
      exercises: List<TopicItem>,
      theory: List<TopicItem>
  ) {
    val exercisesUIDs = exercises.map { it.uid }
    val theoryUIDs = theory.map { it.uid }
    val task = hashMapOf("name" to name, "exercises" to exercisesUIDs, "theory" to theoryUIDs)
    topicDataCollection
        .document(uid)
        .update(task as Map<String, Any>)
        .addOnSuccessListener {
          Log.d("MyPrint", "topic data successfully updated")
          updateTopicItems(exercises)
          updateTopicItems(theory)
        }
        .addOnFailureListener { e -> Log.d("MyPrint", "topic failed to update with error ", e) }
  }

  fun createTopicFolder(name: String): TopicFolder {
    val folder =
        hashMapOf(
            "name" to name, "type" to ItemType.FOLDER.toString(), "items" to emptyList<String>())
    var uid = ""
    topicItemCollection
        .add(folder)
        .addOnSuccessListener { document ->
          uid = document.id
          Log.d("MyPrint", "New topic folder successfully created")
        }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to create new topic folder with error ", e)
        }
    return TopicFolder(uid, name, emptyList())
  }

  fun createTopicFile(name: String): TopicFile {
    val file =
        hashMapOf(
            "name" to name,
            "type" to ItemType.FILE.toString(),
            "strongUsers" to emptyList<String>())
    var uid = ""
    topicItemCollection
        .add(file)
        .addOnSuccessListener { document ->
          uid = document.id
          Log.d("MyPrint", "New topic file successfully created")
        }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to create new topic file with error ", e)
        }
    return TopicFile(uid, name, emptyList())
  }

  private fun updateTopicItems(items: List<TopicItem>) {
    for (item in items) {
      var type = ""
      var folderItems = emptyList<String>()
      var strongUsers = emptyList<String>()
      when (item) {
        is TopicFolder -> {
          type = ItemType.FOLDER.toString()
          folderItems = item.items.map { it.uid }
        }
        is TopicFile -> {
          type = ItemType.FILE.toString()
          strongUsers = item.strongUsers
        }
      }

      val task =
          hashMapOf(
              "name" to item.name,
              "type" to type,
              "items" to folderItems,
              "strongUsers" to strongUsers,
          )

      topicItemCollection
          .document(item.uid)
          .update(task as Map<String, Any>)
          .addOnSuccessListener { Log.d("MyPrint", "topic item ${item.uid} successfully updated") }
          .addOnFailureListener { e ->
            Log.d("MyPrint", "topic item ${item.uid} failed to update with error ", e)
          }
    }
  }

  suspend fun getALlTopics(groupUID: String): TopicList {
    try {
      val snapshot = groupDataCollection.document(groupUID).get().await()
      val items = mutableListOf<Topic>()

      return if (snapshot.exists()) {
        val topicUIDs = snapshot.data?.get("topics") as? List<String>
        topicUIDs?.let { topicUID ->
          topicUID.forEach { topicUID ->
            val topic = getTopic(topicUID)
            items.add(topic)
          }
        }
        TopicList(items)
      } else {
        Log.d("MyPrint", "Group with uid $groupUID does not exist")
        TopicList(emptyList())
      }
    } catch (e: Exception) {
      Log.d("MyPrint", "Could not fetch topics with error ", e)
    }
    return TopicList(emptyList())
  }
}
