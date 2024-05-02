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
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.data.MessageVal
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
            items.add(Group(groupUID, name, photo, members))
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
      Group(groupUID, name, picture, members)
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
        hashMapOf("name" to name, "picture" to photoUri.toString(), "members" to listOf(uid))
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

  // using the Realtime Database for messages
  fun sendMessage(groupUID: String, message: Message) {
    val messagePath = getGroupMessagesPath(groupUID) + "/${message.uid}"
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

  fun deleteMessage(groupUID: String, message: Message) {
    val messagePath = getGroupMessagesPath(groupUID) + "/${message.uid}"
    rt_db.getReference(messagePath).removeValue()
  }

  fun editMessage(groupUID: String, message: Message, newText: String) {
    val messagePath = getGroupMessagesPath(groupUID) + "/${message.uid}"
    rt_db.getReference(messagePath).updateChildren(mapOf(MessageVal.TEXT to newText))
  }

  fun getMessagePath(UID: String, chatType: ChatType): String {
    return when (chatType) {
      ChatType.PRIVATE -> getPrivateMessagesPath(UID)
      ChatType.GROUP -> getGroupMessagesPath(UID)
    }
  }

  fun getGroupMessagesPath(groupUID: String): String {
    return ChatVal.GROUPS + "/$groupUID/" + ChatVal.MESSAGES
  }

  fun getPrivateMessagesPath(chatUID: String): String {
    return ChatVal.DIRECT_MESSAGES + "/$chatUID/" + ChatVal.MESSAGES
  }

  fun getPrivateChatMembersPath(chatUID: String): String {
    return ChatVal.DIRECT_MESSAGES + "/$chatUID/" + ChatVal.MEMBERS
  }

  fun getPrivateChatMembers(chatUID: String, callback: (List<String?>) -> Unit) {
    val ref = rt_db.getReference(getPrivateChatMembersPath(chatUID))

    ref.addListenerForSingleValueEvent(
        object : ValueEventListener {
          override fun onDataChange(dataSnapshot: DataSnapshot) {
            val members = dataSnapshot.children.map { it.key }.toList()
            callback(members)
          }

          override fun onCancelled(databaseError: DatabaseError) {
            println("Database error: ${databaseError.message}")
          }
        })
  }

  fun getPrivateChatsList(userUID: String, liveData: MutableStateFlow<List<Chat>>) {
    val ref = rt_db.getReference(ChatVal.DIRECT_MESSAGES)
    val userRef = rt_db.getReference("users") // Assuming 'users' node holds user data

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
                      messages = messages.value)
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

    Log.d("DB Connection - getMessages", "Getting messages for $uid")
    Log.d("DB Connection - getMessages", "Chat type: $chatType")

    Log.d("DB Connection - getMessages", "Reference: $ref")

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
            handler.post(runnable!!)
          }

          override fun onCancelled(error: DatabaseError) {
            Log.w("MessageViewModel", "Failed to read value.", error.toException())
          }
        })
  }
}
