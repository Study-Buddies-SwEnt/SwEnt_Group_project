package com.github.se.studybuddies.database

import android.net.Uri
import android.util.Log
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.ChatType
import com.github.se.studybuddies.data.ChatVal
import com.github.se.studybuddies.data.Group
import com.github.se.studybuddies.data.GroupList
import com.github.se.studybuddies.data.ItemType
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.data.MessageVal
import com.github.se.studybuddies.data.TimerState
import com.github.se.studybuddies.data.Topic
import com.github.se.studybuddies.data.TopicFile
import com.github.se.studybuddies.data.TopicFolder
import com.github.se.studybuddies.data.TopicItem
import com.github.se.studybuddies.data.TopicList
import com.github.se.studybuddies.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

interface DbRepository {
    fun isFakeDatabase(): Boolean

        // using the userData collection
    suspend fun getUser(uid: String): User?

    suspend fun getCurrentUser(): User?

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

    suspend fun getAllFriends(uid: String): List<User>


    suspend fun getDefaultProfilePicture(): Uri

    suspend fun createUser(
        uid: String,
        email: String,
        username: String,
        profilePictureUri: Uri,
        location: String = "offline"
    )

    fun updateUserData(
        uid: String,
        email: String,
        username: String,
        profilePictureUri: Uri,
        location: String
    )

    fun updateLocation(uid: String, location: String)

    fun userExists(uid: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit)

    // using the groups & userMemberships collections
    suspend fun getAllGroups(uid: String): GroupList

    suspend fun updateGroupTimer(groupUID: String, newEndTime: Long, newIsRunning: Boolean): Int


    suspend fun getGroup(groupUID: String): Group
    suspend fun getGroupName(groupUID: String): String

    suspend fun getDefaultPicture(): Uri

    suspend fun createGroup(name: String, photoUri: Uri)


    suspend fun addUserToGroup(groupUID: String, user: String = "")


    fun updateGroup(groupUID: String, name: String, photoUri: Uri)


    suspend fun removeUserFromGroup(groupUID: String, userUID: String = "")

    suspend fun deleteGroup(groupUID: String)

    // using the Realtime Database for messages
    fun sendMessage(
        chatUID: String,
        message: Message,
        chatType: ChatType,
        additionalUID: String = ""
    )

    fun saveMessage(path: String, data: Map<String, Any>)
    fun uploadChatImage(
        uid: String,
        chatUID: String,
        imageUri: Uri,
        callback: (Uri?) -> Unit
    )

    fun deleteMessage(groupUID: String, message: Message, chatType: ChatType)

    suspend fun removeTopic(uid: String)

    fun editMessage(
        groupUID: String,
        message: Message.TextMessage,
        chatType: ChatType,
        newText: String
    )

     fun getMessagePath(
        chatUID: String,
        chatType: ChatType,
        additionalUID: String = ""
    ): String

    fun getGroupMessagesPath(groupUID: String): String

    fun getTopicMessagesPath(groupUID: String, topicUID: String): String

    fun getPrivateMessagesPath(chatUID: String): String

    fun getPrivateChatMembersPath(chatUID: String): String

    fun subscribeToPrivateChats(
        userUID: String,
        scope: CoroutineScope,
        ioDispatcher: CoroutineDispatcher,
        mainDispatcher: CoroutineDispatcher,
        onUpdate: (List<Chat>) -> Unit
    )

    fun getMessages(
        chat: Chat,
        liveData: MutableStateFlow<List<Message>>,
        ioDispatcher: CoroutineDispatcher,
        mainDispatcher: CoroutineDispatcher
    )

     fun checkForExistingChat(
        currentUserUID: String,
        otherUID: String,
        onResult: (Boolean, String?) -> Unit
    )

    fun startDirectMessage(otherUID: String)

    // using the topicData and topicItemData collections
    suspend fun getTopic(uid: String): Topic
     suspend fun fetchTopicItems(listUID: List<String>): List<TopicItem>

    fun createTopic(name: String, callBack: (String) -> Unit)

    suspend fun addTopicToGroup(topicUID: String, groupUID: String)

    fun addExercise(uid: String, exercise: TopicItem)
    fun addTheory(uid: String, theory: TopicItem)

    suspend fun deleteTopic(topicId: String)


    fun updateTopicName(uid: String, name: String)

    fun createTopicFolder(name: String, parentUID: String, callBack: (TopicFolder) -> Unit)

    fun createTopicFile(name: String, parentUID: String, callBack: (TopicFile) -> Unit)
    fun updateTopicItem(item: TopicItem)
    fun getTimerUpdates(groupUID: String, _timerValue: MutableStateFlow<Long>) : Boolean

        suspend fun getALlTopics(groupUID: String): TopicList

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

