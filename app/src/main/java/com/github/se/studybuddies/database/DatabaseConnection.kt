package com.github.se.studybuddies.database

import android.net.Uri
import android.util.Log
import com.github.se.studybuddies.data.Contact
import com.github.se.studybuddies.data.ContactList
import com.github.se.studybuddies.data.DailyPlanner
import com.github.se.studybuddies.data.Group
import com.github.se.studybuddies.data.GroupList
import com.github.se.studybuddies.data.ItemType
import com.github.se.studybuddies.data.TimerState
import com.github.se.studybuddies.data.Topic
import com.github.se.studybuddies.data.TopicFile
import com.github.se.studybuddies.data.TopicFolder
import com.github.se.studybuddies.data.TopicItem
import com.github.se.studybuddies.data.TopicList
import com.github.se.studybuddies.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DatabaseConnection {

  private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

  // all collections
  private val userDataCollection = db.collection("userData")
  private val userMembershipsCollection = db.collection("userMemberships")
  private val groupDataCollection = db.collection("groupData")
  private val topicDataCollection = db.collection("topicData")
  private val topicItemCollection = db.collection("topicItemData")
  private val contactDataCollection = db.collection("contactData")
  private val userContactsCollection = db.collection("userContacts")

  // using the userData collection
  suspend fun getUser(uid: String): User {
    if (uid.isEmpty()) {
      return User.empty()
    }
    val document = userDataCollection.document(uid).get().await()
    return document.toUser()
  }

  fun updateDailyPlanners(uid: String, dailyPlanners: List<DailyPlanner>) {
    val plannerMap = dailyPlanners.map { it.toMap() }
    userDataCollection
        .document(uid)
        .update("dailyPlanners", plannerMap)
        .addOnSuccessListener {
          Log.d("MyPrint", "DailyPlanners successfully updated for user $uid")
        }
        .addOnFailureListener { e -> Log.d("MyPrint", "Failed to update DailyPlanners: ", e) }
  }

  suspend fun getCurrentUser(): User {
    return getUser(getCurrentUserUID())
  }

  fun getCurrentUserUID(): String {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    return uid?.also { Log.d("MyPrint", "Fetched user UID is $uid") }
        ?: run {
          Log.d("MyPrint", "Failed to get current user UID")
          ""
        }
  }

  suspend fun getAllFriends(uid: String): List<User> {
    return try {
      val snapshot = userDataCollection.document(uid).get().await()
      val snapshotQuery = userDataCollection.get().await()
      snapshotQuery.documents.mapNotNull {
        it.id.takeIf { snapshot.exists() }?.let { id -> getUser(id) }
      }
    } catch (e: Exception) {
      Log.d("MyPrint", "Could not fetch friends with error: $e")
      emptyList()
    }
  }

  suspend fun createUser(
      uid: String,
      email: String,
      username: String,
      profilePictureUri: Uri,
      location: String = "offline"
  ) {
    Log.d(
        "MyPrint",
        "Creating new user with uid $uid, email $email, username $username and picture link $profilePictureUri")
    val user =
        hashMapOf(
            "email" to email,
            "username" to username,
            "photoUrl" to profilePictureUri.toString(),
            "location" to location,
            "dailyPlanners" to emptyList<Map<String, Any>>())

    val defaultProfilePictureUri = StorageDatabaseConnection().getDefaultProfilePicture()
    userDataCollection
        .document(uid)
        .set(user)
        .addOnSuccessListener {
          onCreateUserSuccess(uid, profilePictureUri, defaultProfilePictureUri)
        }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to create user data with error: ", e)
        }

    createUserMembershipAndContact(uid)
  }

  private fun onCreateUserSuccess(
      uid: String,
      profilePictureUri: Uri,
      defaultProfilePictureUri: Uri
  ) {
    if (profilePictureUri != defaultProfilePictureUri) {
      updateProfilePicture(uid, profilePictureUri)
    } else {
      val defaultPictureRef = defaultProfilePictureUri
      updateProfilePicture(uid, defaultPictureRef)
    }
  }

  private fun updateProfilePicture(uid: String, profilePictureUri: Uri) {
    CoroutineScope(Dispatchers.IO).launch {
      val uploadedUri = StorageDatabaseConnection().uploadUserProfilePicture(uid, profilePictureUri)
      uploadedUri?.let { userDataCollection.document(uid).update("photoUrl", it.toString()) }
      Log.d("MyPrint", "User data successfully created")
    }
  }

  private fun createUserMembershipAndContact(uid: String) {
    val membership = hashMapOf("groups" to emptyList<String>())
    userMembershipsCollection
        .document(uid)
        .set(membership)
        .addOnSuccessListener { Log.d("MyPrint", "User memberships successfully created") }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to create user memberships with error: ", e)
        }

    val contactList = hashMapOf("contacts" to emptyList<String>())
    userContactsCollection
        .document(uid)
        .set(contactList)
        .addOnSuccessListener { Log.d("MyPrint", "User contact list successfully created") }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to create user contact list with error: ", e)
        }
  }

  fun updateUserData(
      uid: String,
      email: String,
      username: String,
      profilePictureUri: Uri,
      location: String
  ) {
    val task = hashMapOf("email" to email, "username" to username, "location" to location)
    userDataCollection
        .document(uid)
        .update(task as Map<String, Any>)
        .addOnSuccessListener { updateProfilePicture(uid, profilePictureUri) }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to update user data with error: ", e)
        }
  }

  fun updateLocation(uid: String, location: String) {
    userDataCollection
        .document(uid)
        .update("location", location)
        .addOnSuccessListener { Log.d("MyPrint", "User data successfully updated") }
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
    return try {
      val snapshot = userMembershipsCollection.document(uid).get().await()
      val groupUIDs = snapshot.data?.get("groups") as? List<String> ?: emptyList()
      val groups = groupUIDs.mapNotNull { groupUID -> getGroupOrNull(groupUID) }
      GroupList(groups)
    } catch (e: Exception) {
      Log.d("MyPrint", "In ViewModel, could not fetch groups with error: $e")
      GroupList(emptyList())
    }
  }

  private suspend fun getGroupOrNull(groupUID: String): Group? {
    val document = groupDataCollection.document(groupUID).get().await()
    return if (document.exists()) document.toGroup() else null
  }

  suspend fun updateGroupTimer(groupUID: String, newEndTime: Long, newIsRunning: Boolean): Int {
    if (groupUID.isEmpty()) {
      Log.d("MyPrint", "Group UID is empty")
      return -1
    }
    return try {
      val document = groupDataCollection.document(groupUID).get().await()
      if (!document.exists()) {
        Log.d("MyPrint", "Group with UID $groupUID does not exist")
        return -1
      }
      val newTimerState = mapOf("endTime" to newEndTime, "isRunning" to newIsRunning)
      groupDataCollection
          .document(groupUID)
          .update("timerState", newTimerState)
          .addOnSuccessListener {
            Log.d("MyPrint", "Timer parameter updated successfully for group with UID $groupUID")
          }
          .addOnFailureListener { e ->
            Log.d(
                "MyPrint",
                "Failed to update timer parameter for group with UID $groupUID with error: ",
                e)
          }
      0
    } catch (e: Exception) {
      Log.e("MyPrint", "Exception when updating timer: ", e)
      -1
    }
  }

  suspend fun getGroup(groupUID: String): Group {
    return groupDataCollection.document(groupUID).get().await().toGroup()
  }

  suspend fun getGroupName(groupUID: String): String {
    return groupDataCollection.document(groupUID).get().await().getString("name") ?: ""
  }

  suspend fun createGroup(name: String, photoUri: Uri) {
    val uid = if (name == "Official Group Testing") "111testUser" else getCurrentUserUID()
    Log.d("MyPrint", "Creating new group with uid $uid and picture link $photoUri")
    val group = createGroupMap(uid, name, photoUri)
    val defaultGroupPictureUri = StorageDatabaseConnection().getDefaultGroupPicture()
    groupDataCollection
        .add(group)
        .addOnSuccessListener { documentReference ->
          val groupUID = documentReference.id
          addUserToGroupList(uid, groupUID)
          if (photoUri != defaultGroupPictureUri) {
            updateGroupPicture(groupUID, photoUri)
          } else {
            updateGroupPicture(groupUID, defaultGroupPictureUri)
          }
        }
        .addOnFailureListener { e -> Log.d("MyPrint", "Failed to create group with error: ", e) }
  }

  private fun createGroupMap(uid: String, name: String, photoUri: Uri): Map<String, Any> {
    val timerState =
        mapOf(
            "endTime" to System.currentTimeMillis(), // current time as placeholder
            "isRunning" to false // timer is not running initially
            )
    return hashMapOf(
        "name" to name,
        "picture" to photoUri.toString(),
        "members" to listOf(uid),
        "topics" to emptyList<String>(),
        "timerState" to timerState)
  }

  private fun addUserToGroupList(uid: String, groupUID: String) {
    userMembershipsCollection
        .document(uid)
        .update("groups", FieldValue.arrayUnion(groupUID))
        .addOnSuccessListener { Log.d("MyPrint", "Group successfully created") }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to update user memberships with error: ", e)
        }
  }

  private fun updateGroupPicture(groupUID: String, photoUri: Uri) {
    CoroutineScope(Dispatchers.IO).launch {
      val uploadedUri = StorageDatabaseConnection().uploadGroupPicture(groupUID, photoUri)
      uploadedUri?.let { groupDataCollection.document(groupUID).update("picture", it.toString()) }
    }
  }

  /*
   * Add the user given in the parameter to the group given in the parameter
   * If no user is given add the user actually logged in
   *
   * return -1 in case of invalid entries
   */
  suspend fun addUserToGroup(groupUID: String, userUID: String = getCurrentUserUID()) {
    val userExists = getUser(userUID) != User.empty()
    val groupExists = groupDataCollection.document(groupUID).get().await().exists()

    if (!userExists || !groupExists) {
      Log.d("MyPrint", "User or Group does not exist")
      return
    }

    addUserToGroupMembers(userUID, groupUID)
    addGroupToUserGroups(userUID, groupUID)
  }

  private fun addUserToGroupMembers(userUID: String, groupUID: String) {
    groupDataCollection
        .document(groupUID)
        .update("members", FieldValue.arrayUnion(userUID))
        .addOnSuccessListener { Log.d("MyPrint", "User successfully added to group") }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to add user to group with error: ", e)
        }
  }

  private fun addGroupToUserGroups(userUID: String, groupUID: String) {
    userMembershipsCollection
        .document(userUID)
        .update("groups", FieldValue.arrayUnion(groupUID))
        .addOnSuccessListener { Log.d("MyPrint", "Group successfully added to user") }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to add group to user with error: ", e)
        }
  }

  fun updateGroup(groupUID: String, name: String, photoUri: Uri) {
    groupDataCollection
        .document(groupUID)
        .update("name", name)
        .addOnSuccessListener { Log.d("UpdateGroup", "group name successfully updated") }
        .addOnFailureListener { e ->
          Log.d("UpdateGroup", "Failed modify group name with error: ", e)
        }

    groupDataCollection
        .document(groupUID)
        .update("picture", photoUri.toString())
        .addOnSuccessListener { Log.d("MyPrint", "picture successfully updated") }
        .addOnFailureListener { e ->
          Log.d("UpdateGroup", "Failed modify group picture with error: ", e)
        }

    CoroutineScope(Dispatchers.IO).launch {
      val uploadedUri = StorageDatabaseConnection().uploadGroupPicture(groupUID, photoUri)
      uploadedUri?.let { groupDataCollection.document(groupUID).update("picture", it.toString()) }
    }
  }

  suspend fun removeUserFromGroup(groupUID: String, userUID: String = getCurrentUserUID()) {
    removeUserFromGroupMembers(groupUID, userUID)
    if (isGroupEmpty(groupUID)) {
      deleteGroupAndData(groupUID)
    }
    removeGroupFromUserGroups(groupUID, userUID)
  }

  private fun removeUserFromGroupMembers(groupUID: String, userUID: String) {
    groupDataCollection
        .document(groupUID)
        .update("members", FieldValue.arrayRemove(userUID))
        .addOnSuccessListener { Log.d("Deletion", "User successfully removed from group") }
        .addOnFailureListener { e ->
          Log.d("Deletion", "Failed to remove user from group with error: ", e)
        }
  }

  private suspend fun isGroupEmpty(groupUID: String): Boolean {
    val document = groupDataCollection.document(groupUID).get().await()
    val members = document.get("members") as? List<String> ?: emptyList()
    return members.isEmpty()
  }

  private fun deleteGroupAndData(groupUID: String) {
    groupDataCollection
        .document(groupUID)
        .delete()
        .addOnSuccessListener {
          CoroutineScope(Dispatchers.IO).launch {
            StorageDatabaseConnection().deleteGroupData(groupUID)
            Log.d("Deletion", "Group successfully deleted")
          }
        }
        .addOnFailureListener { e -> Log.d("Deletion", "Failed to delete group with error: ", e) }
  }

  private fun removeGroupFromUserGroups(groupUID: String, userUID: String) {
    userMembershipsCollection
        .document(userUID)
        .update("groups", FieldValue.arrayRemove(groupUID))
        .addOnSuccessListener { Log.d("Deletion", "Group successfully removed from user") }
        .addOnFailureListener { e ->
          Log.d("Deletion", "Failed to remove group from user with error: ", e)
        }
  }

  suspend fun deleteGroup(groupUID: String) {
    val members = getGroupMembers(groupUID)
    if (groupUID.isNotEmpty()) {
      deleteGroupData(groupUID)
    }
    members.forEach { user ->
      userMembershipsCollection
          .document(user)
          .update("groups", FieldValue.arrayRemove(groupUID))
          .addOnSuccessListener { Log.d("Deletion", "Remove group from user successfully") }
          .addOnFailureListener { e ->
            Log.d("Deletion", "Failed to remove group from user with error: ", e)
          }
    }
    groupDataCollection
        .document(groupUID)
        .delete()
        .addOnSuccessListener { Log.d("Deletion", "Group successfully deleted") }
        .addOnFailureListener { e -> Log.d("Deletion", "Failed to delete group with error: ", e) }
  }

  private suspend fun getGroupMembers(groupUID: String): List<String> {
    val document = groupDataCollection.document(groupUID).get().await()
    return document.get("members") as? List<String> ?: emptyList()
  }

  private fun deleteGroupData(groupUID: String) {
    CoroutineScope(Dispatchers.IO).launch { StorageDatabaseConnection().deleteGroupData(groupUID) }
  }

  // using the topicData and topicItemData collections
  suspend fun getTopic(uid: String): Topic {
    val document = topicDataCollection.document(uid).get().await()
    return if (document.exists()) document.toTopic() else Topic.empty()
  }

  fun createTopic(name: String, callBack: (String) -> Unit) {
    val topic =
        hashMapOf(
            TOPIC_NAME to name,
            TOPIC_EXERCISES to emptyList<String>(),
            TOPIC_THEORY to emptyList<String>())
    topicDataCollection
        .add(topic)
        .addOnSuccessListener { document ->
          val uid = document.id
          Log.d("MyPrint", "topic successfully created")
          callBack(uid)
        }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to create topic with error ", e)
          callBack("")
        }
  }

  suspend fun addTopicToGroup(topicUID: String, groupUID: String) {
    groupDataCollection
        .document(groupUID)
        .get()
        .await()
        .takeIf { it.exists() }
        ?.let {
          groupDataCollection
              .document(groupUID)
              .update("topics", FieldValue.arrayUnion(topicUID))
              .addOnSuccessListener { Log.d("MyPrint", ("topic successfully added to group")) }
              .addOnFailureListener { e ->
                Log.d("MyPrint", ("failed to add topic to group with error $e"))
              }
        } ?: Log.d("MyPrint", ("group document not found for uid $groupUID"))
  }

  fun addExercise(uid: String, exercise: TopicItem) {
    topicDataCollection
        .document(uid)
        .update(TOPIC_EXERCISES, FieldValue.arrayUnion(exercise.uid))
        .addOnSuccessListener {
          Log.d("MyPrint", "topic data successfully updated")
          updateTopicItem(exercise)
        }
        .addOnFailureListener { e -> Log.d("MyPrint", "topic failed to update with error ", e) }
  }

  fun addTheory(uid: String, theory: TopicItem) {
    topicDataCollection
        .document(uid)
        .update(TOPIC_THEORY, FieldValue.arrayUnion(theory.uid))
        .addOnSuccessListener {
          Log.d("MyPrint", "topic data successfully updated")
          updateTopicItem(theory)
        }
        .addOnFailureListener { e -> Log.d("MyPrint", "topic failed to update with error ", e) }
  }

  suspend fun deleteTopic(topicId: String) {
    val itemRef = topicDataCollection.document(topicId)
    try {
      itemRef.delete().await()
      Log.d("Database", "Item deleted successfully: $topicId")
    } catch (e: Exception) {
      Log.e("Database", "Error deleting item: $topicId, Error: $e")
      throw e
    }
  }

  fun updateTopicName(uid: String, name: String) {
    topicDataCollection
        .document(uid)
        .update(TOPIC_NAME, name)
        .addOnSuccessListener { Log.d("MyPrint", "topic data successfully updated") }
        .addOnFailureListener { e -> Log.d("MyPrint", "topic failed to update with error ", e) }
  }

  fun createTopicFolder(name: String, parentUID: String, callBack: (TopicFolder) -> Unit) {
    val folder = createTopicFolderMap(name, parentUID)
    topicItemCollection
        .add(folder)
        .addOnSuccessListener { document ->
          val uid = document.id
          if (parentUID.isNotBlank()) {
            topicItemCollection.document(parentUID).update(ITEM_ITEMS, FieldValue.arrayUnion(uid))
          }
          Log.d("MyPrint", "New topic folder successfully created")
          callBack(TopicFolder(uid, name, emptyList(), parentUID))
        }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to create new topic folder with error ", e)
          callBack(TopicFolder("", "", emptyList(), parentUID))
        }
  }

  private fun createTopicFolderMap(name: String, parentUID: String): HashMap<String, Any> {
    return hashMapOf(
        TOPIC_NAME to name,
        ITEM_TYPE to ItemType.FOLDER.toString(),
        ITEM_ITEMS to emptyList<String>(),
        ITEM_PARENT to parentUID)
  }

  fun createTopicFile(name: String, parentUID: String, callBack: (TopicFile) -> Unit) {
    val file = createTopicFileMap(name, parentUID)
    topicItemCollection
        .add(file)
        .addOnSuccessListener { document ->
          val uid = document.id
          if (parentUID.isNotBlank()) {
            topicItemCollection.document(parentUID).update(ITEM_ITEMS, FieldValue.arrayUnion(uid))
          }
          Log.d("MyPrint", "New topic file successfully created with uid ${document.id}")
          callBack(TopicFile(uid, name, emptyList(), parentUID))
        }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to create new topic file with error ", e)
          callBack(TopicFile("", "", emptyList(), parentUID))
        }
  }

  private fun createTopicFileMap(name: String, parentUID: String): HashMap<String, Any> {
    return hashMapOf(
        TOPIC_NAME to name,
        ITEM_TYPE to ItemType.FILE.toString(),
        ITEM_STRONG_USERS to emptyList<String>(),
        ITEM_PARENT to parentUID)
  }

  private fun updateTopicItem(item: TopicItem) {
    val task = item.toMap()
    topicItemCollection
        .document(item.uid)
        .update(task)
        .addOnSuccessListener { Log.d("MyPrint", "topic item ${item.uid} successfully updated") }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "topic item ${item.uid} failed to update with error ", e)
        }
  }

  fun getAllTopics(
      groupUID: String,
      scope: CoroutineScope,
      ioDispatcher: CoroutineDispatcher,
      mainDispatcher: CoroutineDispatcher,
      onUpdate: (TopicList) -> Unit
  ) {
    val docRef = groupDataCollection.document(groupUID)
    docRef.addSnapshotListener { snapshot, e ->
      if (e != null) {
        Log.w("MyPrint", "Listen failed.", e)
        return@addSnapshotListener
      }
      if (snapshot != null && snapshot.exists()) {
        scope.launch(ioDispatcher) {
          val items = snapshot.getTopicList()
          withContext(mainDispatcher) { onUpdate(TopicList(items)) }
        }
      } else {
        Log.d("MyPrint", "Group with uid $groupUID does not exist")
        onUpdate(TopicList(emptyList()))
      }
    }
  }

  private suspend fun DocumentSnapshot.getTopicList(): List<Topic> {
    val topicUIDs = this.data?.get("topics") as? List<String> ?: emptyList()
    return topicUIDs.mapNotNull { topicUid -> getTopic(topicUid) }
  }

  suspend fun getAllContacts(uid: String): ContactList {
    return try {
      val snapshot = userContactsCollection.document(uid).get().await()
      val contactsUIDs = snapshot.data?.get("contacts") as? List<String> ?: emptyList()
      val contacts = contactsUIDs.mapNotNull { contactID -> getContactOrNull(contactID) }
      ContactList(contacts)
    } catch (e: Exception) {
      Log.d("MyPrint", "In ViewModel, could not fetch contacts with error: $e")
      ContactList(emptyList())
    }
  }

  private suspend fun getContactOrNull(contactUID: String): Contact? {
    val document = contactDataCollection.document(contactUID).get().await()
    return if (document.exists()) document.toContact() else null
  }

  suspend fun getContact(contactUID: String): Contact {
    return contactDataCollection.document(contactUID).get().await().toContact()
  }

  suspend fun createContact(otherUID: String) {
    val uid = getCurrentUserUID()
    Log.d("MyPrint", "Creating new contact with between $uid and $otherUID")
    if (isContactExists(uid, otherUID)) {
      Log.d("MyPrint", "Contact already exists")
      return
    }
    val contact = hashMapOf("members" to listOf(uid, otherUID), "showOnMap" to false)
    contactDataCollection
        .add(contact)
        .addOnSuccessListener { document ->
          val contactID = document.id
          Log.d("MyPrint", "Contact successfully created")
          addContactToUsers(uid, otherUID, contactID)
        }
        .addOnFailureListener { e -> Log.d("MyPrint", "Failed to create contact with error: ", e) }
  }

  private suspend fun isContactExists(uid: String, otherUID: String): Boolean {
    val contactList = getAllContacts(uid)
    return contactList.getFilteredContacts(otherUID).isNotEmpty()
  }

  private fun addContactToUsers(uid: String, otherUID: String, contactID: String) {
    userContactsCollection
        .document(uid)
        .update("contacts", FieldValue.arrayUnion(contactID))
        .addOnSuccessListener { Log.d("MyPrint", "Contact successfully added to userContacts") }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to add new contact to userContacts with error: ", e)
        }

    userContactsCollection
        .document(otherUID)
        .update("contacts", FieldValue.arrayUnion(contactID))
        .addOnSuccessListener { Log.d("MyPrint", "Contact successfully added to userContacts") }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to add new contact to userContacts with error: ", e)
        }
  }

  fun deleteContact(contactUID: String, userUID: String = "") {
    contactDataCollection
        .document(contactUID)
        .delete()
        .addOnSuccessListener { Log.d("MyPrint", "Contact successfully deleted") }
        .addOnFailureListener { e -> Log.d("MyPrint", "Failed to delete contact with error: ", e) }
  }

  private fun DocumentSnapshot.toUser(): User {
    return if (this.exists()) {
      val email = this.getString("email") ?: ""
      val username = this.getString("username") ?: ""
      val photoUrl = Uri.parse(this.getString("photoUrl") ?: "")
      val location = this.getString("location") ?: "offline"
      val dailyPlanners = this.get("dailyPlanners") as? List<Map<String, Any>> ?: emptyList()
      val plannerList = dailyPlanners.map { it.toDailyPlanner() }
      User(this.id, email, username, photoUrl, location, plannerList)
    } else {
      Log.d("MyPrint", "user document not found for id ${this.id}")
      User.empty()
    }
  }

  private fun DailyPlanner.toMap(): Map<String, Any> {
    return mapOf(
        "date" to this.date,
        "goals" to this.goals,
        "appointments" to this.appointments,
        "notes" to this.notes)
  }

  private fun Map<String, Any>.toDailyPlanner(): DailyPlanner {
    return DailyPlanner(
        date = this["date"] as String,
        goals = this["goals"] as List<String>,
        appointments = this["appointments"] as Map<String, String>,
        notes = this["notes"] as List<String>)
  }

  private fun DocumentSnapshot.toGroup(): Group {
    val name = this.getString("name") ?: ""
    val pictureUri = this.getString("picture")?.let { Uri.parse(it) } ?: Uri.EMPTY
    val members = this.get("members") as? List<String> ?: emptyList()
    val timerStateMap = this.get("timerState") as? Map<String, Any>
    val endTime = timerStateMap?.get("endTime") as? Long ?: System.currentTimeMillis()
    val isRunning = timerStateMap?.get("isRunning") as? Boolean ?: false
    val timerState = TimerState(endTime, isRunning)
    val topics = this.get("topics") as? List<String> ?: emptyList()

    return Group(this.id, name, pictureUri, members, topics, timerState)
  }

  private suspend fun DocumentSnapshot.toTopic(): Topic {
    val name = this.getString(TOPIC_NAME) ?: ""
    val exercisesList = this.get(TOPIC_EXERCISES) as List<String>
    val theoryList = this.get(TOPIC_THEORY) as List<String>
    val exercises = fetchTopicItems(exercisesList)
    val theory = fetchTopicItems(theoryList)
    return Topic(this.id, name, exercises, theory)
  }

  private suspend fun fetchTopicItems(listUID: List<String>): List<TopicItem> {
    val items = mutableListOf<TopicItem>()
    for (itemUID in listUID) {
      val document = topicItemCollection.document(itemUID).get().await()
      if (document.exists()) {
        items.add(document.toTopicItem())
      }
    }
    return items
  }

  private suspend fun DocumentSnapshot.toTopicItem(): TopicItem {
    val name = this.getString(TOPIC_NAME) ?: ""
    val parentUID = this.getString(ITEM_PARENT) ?: ""
    val type = ItemType.valueOf(this.getString(ITEM_TYPE) ?: ItemType.FILE.toString())
    return when (type) {
      ItemType.FOLDER -> {
        val folderItemsList = this.get(ITEM_ITEMS) as List<String>
        val folderItems = fetchTopicItems(folderItemsList)
        TopicFolder(this.id, name, folderItems, parentUID)
      }
      ItemType.FILE -> {
        val strongUsers = this.get(ITEM_STRONG_USERS) as List<String>
        TopicFile(this.id, name, strongUsers, parentUID)
      }
    }
  }

  private fun TopicItem.toMap(): Map<String, Any> {
    return when (this) {
      is TopicFolder -> {
        mapOf(
            TOPIC_NAME to this.name,
            ITEM_TYPE to ItemType.FOLDER.toString(),
            ITEM_ITEMS to this.items.map { it.uid })
      }
      is TopicFile -> {
        mapOf(
            TOPIC_NAME to this.name,
            ITEM_TYPE to ItemType.FILE.toString(),
            ITEM_STRONG_USERS to this.strongUsers)
      }
    }
  }

  private suspend fun DocumentSnapshot.toContact(): Contact {
    val members = this.get("members") as? Pair<String, String> ?: Pair("", "")
    val showOnMap = this.get("showOnMap") as Boolean
    return Contact(this.id, members, showOnMap)
  }

  companion object {
    const val TOPIC_NAME = "name"
    const val TOPIC_EXERCISES = "exercises"
    const val TOPIC_THEORY = "theory"
    const val ITEM_PARENT = "parent"
    const val ITEM_TYPE = "type"
    const val ITEM_ITEMS = "items"
    const val ITEM_STRONG_USERS = "strongUsers"
  }
}
