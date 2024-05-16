package com.github.se.studybuddies.utility.fakeDatabase

import android.net.Uri
import android.util.Log
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.ChatType
import com.github.se.studybuddies.data.ChatVal
import com.github.se.studybuddies.data.Group
import com.github.se.studybuddies.data.GroupList
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.data.MessageVal
import com.github.se.studybuddies.data.TimerState
import com.github.se.studybuddies.data.Topic
import com.github.se.studybuddies.data.TopicItem
import com.github.se.studybuddies.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap


class FakeDataBaseConnection {
    private val userDataCollection  = ConcurrentHashMap<String, User>()
    private val userMembershipsCollection = ConcurrentHashMap<String, List<String>>()
    private val groupDataCollection = ConcurrentHashMap<String, Group>()
    private val topicDataCollection = ConcurrentHashMap<String, Topic>()
    private val topicItemCollection = ConcurrentHashMap<String, TopicItem>()
    private val rtDb = ConcurrentHashMap<String, Map<String, Any>>()

    suspend fun getUser(uid: String): User {
        return userDataCollection[uid] ?: User.empty()
    }

    suspend fun getCurrentUser(): User {
        return getUser(getCurrentUserUID())
    }

    fun getCurrentUserUID(): String {
        return "userTest"
    }

    suspend fun getAllFriends(uid: String): List<User> {
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

    suspend fun getDefaultProfilePicture(): Uri {
        return Uri.parse("https://firebasestorage.googleapis.com/v0/b/study-buddies-e655a.appspot.com/o/userData%2Fdefault.jpg?alt=media&token=678a8343-a3b1-4a2e-aa5a-aeca956a3f5e")
    }



    suspend fun createUser(
        uid: String,
        email: String,
        username: String,
        profilePictureUri: Uri,
        location: String = "offline"
    ) {
         if (profilePictureUri != getDefaultProfilePicture()) {
            userDataCollection[uid] = User(uid, email, username, profilePictureUri, location)

        } else {
            userDataCollection[uid] = User(uid, email, username, getDefaultProfilePicture(), location)
        }

        val membership = hashMapOf("groups" to emptyList<String>())
        userMembershipsCollection[uid] = membership["groups"] as List<String>
    }

    fun updateUserData(
        uid: String,
        email: String,
        username: String,
        profilePictureUri: Uri,
        location: String
    ) {
        val task = hashMapOf("email" to email, "username" to username, "location" to location)
        userDataCollection[uid] = User(uid, email, username, profilePictureUri, location)
    }
    fun updateLocation(uid: String, location: String) {
        userDataCollection[uid] = userDataCollection[uid]!!.copy(location = location)
    }

    fun userExists(uid: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
        userDataCollection[uid]?.let { onSuccess(true) } ?: onSuccess(false)
    }

    suspend fun getAllGroups(uid: String): GroupList {
        try {
            val snapshot = userMembershipsCollection[uid]
            val items = mutableListOf<Group>()

            if (snapshot != null) {
                val groupUIDs = snapshot
                groupUIDs?.let { groupsIDs ->
                    groupsIDs.forEach { groupUID ->
                        val document = groupDataCollection[groupUID]
                        val name = document?.name ?: ""
                        val photo = document?.picture ?: Uri.EMPTY
                        val members = document?.members ?: emptyList()
                        val timerStateMap = document?.timerState as? Map<String, Any>
                        val endTime = timerStateMap?.get("endTime") as? Long ?: System.currentTimeMillis()
                        val isRunning = timerStateMap?.get("isRunning") as? Boolean ?: false
                        val timerState = TimerState(endTime, isRunning)
                        val topics = document?.topics  ?: emptyList()
                        items.add(Group(groupUID, name, photo, members, topics, timerState))
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

    suspend fun updateGroupTimer(groupUID: String, newEndTime: Long, newIsRunning: Boolean): Int {
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
            groupDataCollection[groupUID] = document.copy(timerState = TimerState(newEndTime, newIsRunning))
        } catch (e: Exception) {
            Log.e("MyPrint", "Exception when updating timer: ", e)
            return -1
        }

        return 0
    }

    suspend fun getGroup(groupUID: String): Group {
        val document = groupDataCollection[groupUID]
        return if (document != null){
            val name = document.name
            val picture = document.picture
            val members = document.members
            val timerStateMap = document.timerState as? Map<String, Any>
            val endTime = timerStateMap?.get("endTime") as? Long ?: System.currentTimeMillis()
            val isRunning = timerStateMap?.get("isRunning") as? Boolean ?: false
            val timerState = TimerState(endTime, isRunning)
            val topics = document.topics
            Group(groupUID, name, picture, members, topics, timerState)
        } else {
            Log.d("MyPrint", "group document not found for group id $groupUID")
            Group.empty()
        }
    }

    suspend fun getGroupName(groupUID: String): String {
        val document = groupDataCollection[groupUID]
        return if (document != null) {
            document.name
        } else {
            Log.d("MyPrint", "group document not found for group id $groupUID")
            ""
        }
    }

    suspend fun getDefaultPicture(): Uri {
        return Uri.parse("https://firebasestorage.googleapis.com/v0/b/study-buddies-e655a.appspot.com/o/userData%2Fdefault.jpg?alt=media&token=678a8343-a3b1-4a2e-aa5a-aeca956a3f5e")
    }

    suspend fun createGroup(name: String, photoUri: Uri) {
        val uid = if (name == "Official Group Testing") "111testUser" else getCurrentUserUID()
        val timerState = TimerState(System.currentTimeMillis(), false) // current time as placeholder, timer is not running initially
        val groupUID  = "groupTest${groupDataCollection.size}"

        if (photoUri != getDefaultPicture()) {
            val group =
                Group(
                    groupUID,
                    name,
                    photoUri,
                    emptyList(),
                    emptyList(),
                    timerState)
            groupDataCollection[uid] = group
            userMembershipsCollection[uid]?.let {
                val updatedList = it + uid
                userMembershipsCollection[uid] = updatedList
            }
        } else {
            val group =
                Group(
                    groupUID,
                    name,
                    getDefaultPicture(),
                    emptyList(),
                    emptyList(),
                    timerState
                )
            groupDataCollection[uid] = group
            userMembershipsCollection[uid]?.let {
                val updatedList = it + uid
                userMembershipsCollection[uid] = updatedList
            }
        }
    }

    suspend fun addUserToGroup(groupUID: String, user: String = "") {
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
            userMembershipsCollection[userToAdd] = updatedList
        }
    }
    fun updateGroup(groupUID: String, name: String, photoUri: Uri) {

        // change name of group
        groupDataCollection[groupUID] = groupDataCollection[groupUID]!!.copy(name = name)

        // change picture of group
        groupDataCollection[groupUID] = groupDataCollection[groupUID]!!.copy(picture = photoUri)

        // change picture of group
        groupDataCollection[groupUID] = groupDataCollection[groupUID]!!.copy(picture = photoUri)

    }

    suspend fun removeUserFromGroup(groupUID: String, userUID: String = "") {
        val user =
            if (userUID == "") {
                getCurrentUserUID()
            } else {
                userUID
            }

        groupDataCollection[groupUID]?.let {
            val updatedMembers = it.members - user
            groupDataCollection[groupUID] = it.copy(members = updatedMembers)
        }

        val document = groupDataCollection[groupUID]
        val members = document?.members ?: emptyList()

        if (members.isEmpty()) {
            groupDataCollection[groupUID] = Group.empty()
        }

        userMembershipsCollection[user]?.let {
            val updatedList = it - groupUID
            userMembershipsCollection[user] = updatedList
        }
    }

    suspend fun deleteGroup(groupUID: String) {
        val document = groupDataCollection[groupUID]
        val members = document?.members ?: emptyList()

        if (members.isNotEmpty()) {
            val listSize = members.size

            for (i in 0 until listSize) {
                val user = members[i]

                userMembershipsCollection[user]?.let {
                    val updatedList = it - groupUID
                    userMembershipsCollection[user] = updatedList
                }
            }
        }
        groupDataCollection[groupUID] = Group.empty()
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
    private fun saveMessage(path: String, data: Map<String, Any>) {
        rtDb[path] = data
    }
    private fun uploadChatImage(
        uid: String,
        chatUID: String,
        imageUri: Uri,
        callback: (Uri?) -> Unit
    ) {
        callback(imageUri)
    }

    fun deleteMessage(groupUID: String, message: Message, chatType: ChatType) {
        val messagePath = getMessagePath(groupUID, chatType) + "/${message.uid}"
        rtDb.remove(messagePath)
    }

    fun editMessage(
        groupUID: String,
        message: Message.TextMessage,
        chatType: ChatType,
        newText: String
    ) {
        val messagePath = getMessagePath(groupUID, chatType) + "/${message.uid}"
        rtDb[messagePath]?.let {
            val updatedMessage = it.toMutableMap()
            updatedMessage[MessageVal.TEXT] = newText
            rtDb[messagePath] = updatedMessage
        }
    }

    fun sendMessage(
        chatUID: String,
        message: Message,
        chatType: ChatType,
        additionalUID: String = ""
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
    suspend fun subscribeToPrivateChats(
        userUID: String,
        onUpdate: (List<Chat>) -> Unit
    ) {
        val chatList = mutableListOf<Chat>()

        // Iterate over the rtDb ConcurrentHashMap
        for ((key, value) in rtDb) {
            // Check if the key starts with "DIRECT_MESSAGES", which indicates a private chat
            if (key.startsWith(ChatVal.DIRECT_MESSAGES)) {
                // Extract the members from the value map
                val members = value[ChatVal.MEMBERS] as? List<String>

                // Check if the userUID is in the members list
                if (members != null && userUID in members) {
                    // Find the other user's UID
                    val otherUserId = members.firstOrNull { it != userUID }

                    // Get the other user and current user data
                    otherUserId?.let { userId ->
                        val otherUser = getUser(userId)
                        val currentUser = getUser(userUID)

                        // Create a Chat object and add it to the chatList
                        chatList.add(
                            Chat(
                                uid = key,
                                name = otherUser.username,
                                picture = otherUser.photoUrl,
                                type = ChatType.PRIVATE,
                                members = listOf(otherUser, currentUser)
                            )
                        )
                    }
                }
            }
        }

        // Call the onUpdate function with the sorted chatList
        onUpdate(chatList.sortedBy { it.name })
    }

}
