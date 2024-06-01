package com.github.se.studybuddies.database

import android.net.Uri
import android.util.Log
import com.github.se.studybuddies.data.Contact
import com.github.se.studybuddies.data.ContactList
import com.github.se.studybuddies.data.DailyPlanner
import com.github.se.studybuddies.data.Group
import com.github.se.studybuddies.data.GroupList
import com.github.se.studybuddies.data.TimerState
import com.github.se.studybuddies.data.Topic
import com.github.se.studybuddies.data.TopicFile
import com.github.se.studybuddies.data.TopicFolder
import com.github.se.studybuddies.data.TopicItem
import com.github.se.studybuddies.data.TopicList
import com.github.se.studybuddies.data.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MockDatabase : DbRepository {
  private val userDataCollection = fakeUserDataCollection
  private val userMembershipsCollection = fakeUserMembershipsCollection
  private val groupDataCollection = fakeGroupDataCollection
  private val topicDataCollection = fakeTopicDataCollection
  private val topicItemCollection = fakeTopicItemCollection
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

  override suspend fun getContact(contactUID: String): Contact {
    TODO("Not yet implemented")
  }

  override suspend fun createContact(otherUID: String) {
    TODO("Not yet implemented")
  }

  override suspend fun getAllContacts(uid: String): ContactList {
    TODO("Not yet implemented")
  }

  override fun getCurrentUserUID(): String {
    return "E2EUserTest"
  }

  override suspend fun getAllFriends(uid: String): List<User> {
    return try {
      val user = userDataCollection.getOrElse(uid) { User.empty() }
      val snapshotQuery = userDataCollection
      val items = mutableListOf<User>()

      if (user != User.empty()) {
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
      Log.d("MockDatabase : updateGroupTimer", "Group UID is empty")
      return -1
    }

    val group = groupDataCollection.getOrElse(groupUID) { Group.empty() }
    if (group == Group.empty()) {
      Log.d("MockDatabase: updateGroupTimer", "Group with UID $groupUID does not exist")
      return -1
    }
    // Update the timerState field in the group document
    try {
      groupDataCollection[groupUID] = group.copy(timerState = TimerState(newEndTime, newIsRunning))
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

  override suspend fun addUserToGroup(groupUID: String, user: String, callBack: (Boolean) -> Unit) {
    val group = groupDataCollection.getOrElse(groupUID) { Group.empty() }
    if (group == Group.empty()) {
      callBack(true)
      Log.d("MockDatabase : addUserToGroup", "Group with uid $groupUID does not exist")
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
      callBack(true)
      Log.d("MyPrint", "User with uid $userToAdd does not exist")
      return
    }

    // add user to group
    groupDataCollection[groupUID] = group.copy(members = group.members + userToAdd)

    // add group to the user's list of groups
    userMembershipsCollection[userToAdd]?.let {
      val updatedList = it + groupUID
      userMembershipsCollection[userToAdd] = updatedList.toMutableList()
    }
    callBack(false)
  }

  override fun updateGroup(groupUID: String, name: String, photoUri: Uri) {

    val group = groupDataCollection.getOrElse(groupUID) { Group.empty() }
    if (group == Group.empty()) {
      Log.d("MockDatabase : updateGroup", "Group with uid $groupUID does not exist")
      return
    }

    groupDataCollection[groupUID] =
        Group(group.uid, name, photoUri, group.members, group.topics, group.timerState)
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

    val group = groupDataCollection.getOrElse(groupUID) { Group.empty() }
    if (group == Group.empty()) {
      Log.d("MockDatabase : removeUserFromGroup", "Group with uid $groupUID does not exist")
      return
    }
    val members = group.members as? List<String> ?: emptyList()

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

  override fun uploadChatImage(
      uid: String,
      chatUID: String,
      imageUri: Uri,
      callback: (Uri?) -> Unit
  ) {
    callback(imageUri)
  }

  override suspend fun getTopic(uid: String, callBack: (Topic) -> Unit) {
    val topic = topicDataCollection[uid]
    if (topic != null) {
      callBack(topic)
    } else {
      Log.d("MyPrint", "topic document not found for id $uid")
      callBack(Topic.empty())
    }
  }

  override suspend fun getTopicFile(id: String): TopicFile {
    val document = topicItemCollection[id]
    return if (document != null && document is TopicFile) {
      val name = document.name
      val strongUsers = document.strongUsers
      val parentUID = document.parentUID
      TopicFile(id, name, strongUsers, parentUID)
    } else {
      TopicFile.empty()
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

  override suspend fun deleteTopic(topicId: String, groupUID: String, callBack: () -> Unit) {
    getTopic(topicId) { topic ->
      val items: List<TopicItem> = topic.exercises + topic.theory
      iterateTopicItemDeletion(items) {
        topicDataCollection.remove(topic.uid)
        val currentGroup = groupDataCollection[groupUID]!!
        val newList = currentGroup.topics.filter { it != topicId }
        val updatedGroup =
            Group(
                currentGroup.uid,
                currentGroup.name,
                currentGroup.picture,
                currentGroup.members,
                newList,
                currentGroup.timerState)
        groupDataCollection.remove(groupUID)
        groupDataCollection[groupUID] = updatedGroup
        callBack()
      }
    }
  }

  private fun iterateTopicItemDeletion(items: List<TopicItem>, callBack: () -> Unit) {
    items.forEach { topicItem ->
      when (topicItem) {
        is TopicFile -> {
          fakeTopicItemCollection.remove(topicItem.uid)
        }
        is TopicFolder -> {
          val children = topicItem.items
          iterateTopicItemDeletion(children) { fakeTopicItemCollection.remove(topicItem.uid) }
        }
      }
    }
    callBack()
  }

  override fun updateTopicName(uid: String, name: String) {
    val topic = topicDataCollection.getOrElse(uid) { Topic.empty() }
    if (topic == Topic.empty()) {
      Log.d("MockDatabase : updateTopicName", "Topic with uid $uid does not exist")
      return
    }
    topicDataCollection[uid] = Topic(topic.uid, name, topic.exercises, topic.theory)
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

  override suspend fun getIsUserStrong(fileID: String, callBack: (Boolean) -> Unit) {
    val document = topicItemCollection[fileID]
    if (document != null && document is TopicFile) {
      val strongUsers = document.strongUsers
      val currentUser = getCurrentUserUID()
      callBack(strongUsers.contains(currentUser))
    } else {
      callBack(false)
    }
  }

  override suspend fun updateStrongUser(fileID: String, newValue: Boolean) {
    val currentUser = getCurrentUserUID()
    val document = topicItemCollection[fileID]
    if (document != null && document is TopicFile) {
      val strongUsers = document.strongUsers.toMutableList()
      if (newValue) {
        strongUsers.add(currentUser)
        document.strongUsers = strongUsers.toList()
      } else {
        strongUsers.remove(currentUser)
        document.strongUsers = strongUsers.toList()
      }
    }
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
          topicUIDs.map { topicUID -> getTopic(topicUID) { topic -> items.add(topic) } }
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
    val user = userDataCollection.getOrElse(uid) { User.empty() }
    if (user == User.empty()) {
      Log.d("MockDatabase : updateDailyPlanners", "User with uid $uid does not exist")
      return
    }
    userDataCollection[uid] =
        User(user.uid, user.email, user.username, user.photoUrl, user.location, dailyPlanners)
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
