package com.github.se.studybuddies.database

import android.net.Uri
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.ChatType
import com.github.se.studybuddies.data.Contact
import com.github.se.studybuddies.data.ContactList
import com.github.se.studybuddies.data.DailyPlanner
import com.github.se.studybuddies.data.Group
import com.github.se.studybuddies.data.GroupList
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.data.TimerState
import com.github.se.studybuddies.data.Topic
import com.github.se.studybuddies.data.TopicFile
import com.github.se.studybuddies.data.TopicFolder
import com.github.se.studybuddies.data.TopicItem
import com.github.se.studybuddies.data.TopicList
import com.github.se.studybuddies.data.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

interface DbRepository {

  /**
   * Used in debugging to know if using real DB or MockDB
   *
   * @return true if is the MockDB, false otherwise
   */
  fun isFakeDatabase(): Boolean

  // using the userData collection
  /**
   * Fetches userData from the DB
   *
   * @return User created from data fetched
   */
  suspend fun getUser(uid: String): User?

  /**
   * Fetches the userData of the current signed-in user
   *
   * @return User object for the signed-in user
   */
  suspend fun getCurrentUser(): User

  /**
   * Fetches contact from DB
   *
   * @return Contact object
   */
  suspend fun getContact(contactUID: String): Contact

  /**
   * Creates the contact relationship between current user and another one
   *
   * @param otherUID user that the current user wants to create a contact relationship with
   */
  suspend fun createContact(otherUID: String)

  /**
   * Fetches all contacts of the current user
   *
   * @param uid uid of the user to get all the contacts of
   * @return ContactList object
   */
  suspend fun getAllContacts(uid: String): ContactList

  /**
   * Fetches the UID of the current user, from Google authentication
   *
   * @param
   * @return UID of the currently signed-in user
   */
  fun getCurrentUserUID(): String

  /**
   * Early function to get all "friends" -> other app users
   *
   * @param uid of the user to get friends from
   * @return list of User objects
   */
  suspend fun getAllFriends(uid: String): List<User>

  /**
   * Fetches the picture used when user doesn't put their own profile picture
   *
   * @return Uri of the picture
   */
  suspend fun getDefaultProfilePicture(): Uri

  /**
   * Creates all the relevant objects needed in the DB to represent a user
   *
   * @param uid given by the Google authentication, also used in DB
   * @param email used by the user to sign in with Google
   * @param username given by the user
   * @param profilePictureUri Uri of the picture chosen by user, or of default picture if none
   *   chosen
   * @param location set to "offline" when first creating an account
   */
  suspend fun createUser(
      uid: String,
      email: String,
      username: String,
      profilePictureUri: Uri,
      location: String = "offline"
  )

  /**
   * Updates the DB with user-modified information
   *
   * @param uid of the user with modified information
   * @param email new email (currently cannot be changed by user)
   * @param username new username
   * @param profilePictureUri new picture set
   * @param location updated location
   */
  fun updateUserData(
      uid: String,
      email: String,
      username: String,
      profilePictureUri: Uri,
      location: String
  )

  /**
   * Updates the user's location
   *
   * @param uid of the user
   * @param location new location
   */
  fun updateLocation(uid: String, location: String)

  /**
   * Checks if the relevant data exists for user
   *
   * @param uid of the user to check
   * @param onSuccess uses boolean true or false whether or not the user exists
   * @param onFailure ran if there was an error while checking for user existence
   */
  fun userExists(uid: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit)

  /** Checks if group exists in DB */
  fun groupExists(groupUID: String, onSuccess: (Boolean) -> Unit, onFailure: (Exception) -> Unit)

  // using the groups & userMemberships collections
  /**
   * Fetches all groups the current user is subscribed to
   *
   * @param uid of the user
   * @return GroupList object
   */
  suspend fun getAllGroups(uid: String): GroupList

  /** Fetches timer details of group */
  fun subscribeToGroupTimerUpdates(
      groupUID: String,
      _timerValue: MutableStateFlow<Long>,
      _isRunning: MutableStateFlow<Boolean>,
      ioDispatcher: CoroutineDispatcher,
      mainDispatcher: CoroutineDispatcher,
      onTimerStateChanged: suspend (TimerState) -> Unit
  )

  /**
   * Updates time of group timer
   *
   * @return -1 if there was an error, 0 otherwise
   */
  suspend fun updateGroupTimer(groupUID: String, timerState: TimerState): Int

  /** Fetches group data from DB */
  suspend fun getGroup(groupUID: String): Group

  /**
   * Fetches only name data of the group
   *
   * @return group name
   */
  suspend fun getGroupName(groupUID: String): String

  /**
   * Fetches picture given by default for a group
   *
   * @return Uri of the default picture
   */
  suspend fun getDefaultPicture(): Uri

  /**
   * Creates all relevant data in DB for a new group
   *
   * @param name of the group
   * @param photoUri picture of the group, or default one
   */
  suspend fun createGroup(name: String, photoUri: Uri)

  /**
   * Adds the group to userMemberships, and the user to the group's list of members
   *
   * @param callBack block ran with true if there was an error, false otherwise
   */
  suspend fun addUserToGroup(groupUID: String, user: String = "", callBack: (Boolean) -> Unit)

  /** Adds current user to group */
  suspend fun addSelfToGroup(groupUID: String)

  /** Updates group information in DB */
  fun updateGroup(groupUID: String, name: String, photoUri: Uri)

  /** Removes user from group's members, and group from the user's memberships */
  suspend fun removeUserFromGroup(groupUID: String, userUID: String = "")

  /** Delete group from DB */
  suspend fun deleteGroup(groupUID: String)

  fun sendMessage(chatUID: String, message: Message, chatType: ChatType, additionalUID: String = "")

  fun saveMessage(path: String, data: Map<String, Any>)

  fun uploadChatImage(uid: String, chatUID: String, imageUri: Uri, callback: (Uri?) -> Unit)

  fun deleteMessage(groupUID: String, message: Message, chatType: ChatType)

  suspend fun removeTopic(uid: String)

  fun editMessage(groupUID: String, message: Message, chatType: ChatType, newText: String)

  fun getMessagePath(chatUID: String, chatType: ChatType, additionalUID: String = ""): String

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

  fun votePollMessage(chat: Chat, message: Message.PollMessage)

  fun checkForExistingChat(
      currentUserUID: String,
      otherUID: String,
      onResult: (Boolean, String?) -> Unit
  )

  suspend fun startDirectMessage(otherUID: String): String

  // using the topicData and topicItemData collections
  /**
   * Fetches topic data from DB
   *
   * @param callBack block to be ran with Topic fetched
   */
  suspend fun getTopic(uid: String, callBack: (Topic) -> Unit)

  /** Fetches a topic item of type file */
  suspend fun getTopicFile(id: String): TopicFile

  /**
   * Fetches all items inside a topic
   *
   * @param listUID ids of all topic items
   */
  suspend fun fetchTopicItems(listUID: List<String>): List<TopicItem>

  /**
   * Create new topic in DB
   *
   * @param callBack returned with topic ID
   */
  fun createTopic(name: String, callBack: (String) -> Unit)

  /** Adds created topic reference to its group */
  suspend fun addTopicToGroup(topicUID: String, groupUID: String)

  /** Add a topic item in exercises tab of a topic */
  fun addExercise(uid: String, exercise: TopicItem)

  /** Add a topic item in theory tab of a topic */
  fun addTheory(uid: String, theory: TopicItem)

  /** Delete topic and all its referred children from DB */
  suspend fun deleteTopic(topicId: String, groupUID: String, callBack: () -> Unit)

  fun updateTopicName(uid: String, name: String)

  /** Topic item of type folder */
  fun createTopicFolder(name: String, parentUID: String, callBack: (TopicFolder) -> Unit)

  /** Topic item of type file */
  fun createTopicFile(name: String, parentUID: String, callBack: (TopicFile) -> Unit)

  fun updateTopicItem(item: TopicItem)

  /** Gets whether current user is self-declared as strong on topic */
  suspend fun getIsUserStrong(fileID: String, callBack: (Boolean) -> Unit)

  suspend fun updateStrongUser(fileID: String, newValue: Boolean)

  fun updateDailyPlanners(uid: String, dailyPlanners: List<DailyPlanner>)

  fun getAllTopics(
      groupUID: String,
      scope: CoroutineScope,
      ioDispatcher: CoroutineDispatcher,
      mainDispatcher: CoroutineDispatcher,
      onUpdate: (TopicList) -> Unit
  )

  /** Add image resource to a topic file */
  suspend fun createContact(otherUID: String, contactID: String)

  fun deleteContact(contactID: String)

  fun deletePrivateChat(chatID: String)

  fun updateContact(contactID: String, showOnMap: Boolean)

  fun fileAddImage(fileID: String, image: Uri, callBack: () -> Unit)

  /** Get all image resources for given topic */
  suspend fun getTopicFileImages(fileID: String): List<Uri>

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
