package com.github.se.studybuddies.database

import android.net.Uri
import android.util.Log
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.ChatType
import com.github.se.studybuddies.data.ChatVal
import com.github.se.studybuddies.data.Contact
import com.github.se.studybuddies.data.ContactList
import com.github.se.studybuddies.data.DailyPlanner
import com.github.se.studybuddies.data.Group
import com.github.se.studybuddies.data.GroupList
import com.github.se.studybuddies.data.ItemType
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.data.MessageVal
import com.github.se.studybuddies.data.RequestList
import com.github.se.studybuddies.data.TimerState
import com.github.se.studybuddies.data.Topic
import com.github.se.studybuddies.data.TopicFile
import com.github.se.studybuddies.data.TopicFolder
import com.github.se.studybuddies.data.TopicItem
import com.github.se.studybuddies.data.TopicList
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.viewModels.ContactsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
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

class DatabaseConnection : DbRepository {
  private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
  private val storage = FirebaseStorage.getInstance().reference

  private val rtDb =
      Firebase.database(
          "https://study-buddies-e655a-default-rtdb.europe-west1.firebasedatabase.app/")

  // all collections
  private val userDataCollection = db.collection("userData")
  private val userMembershipsCollection = db.collection("userMemberships")
  private val groupDataCollection = db.collection("groupData")
  private val topicDataCollection = db.collection("topicData")
  private val topicItemCollection = db.collection("topicItemData")
  private val contactDataCollection = db.collection("contactData")
  private val userContactsCollection = db.collection("userContacts")
  private val dailyPlannerDataCollection = db.collection("dailyPlannerData")
  private val dailyPlannerGroupConnection = db.collection("dailyPlannerGroup")

  override fun isFakeDatabase(): Boolean {
    return false
  }

  // using the userData collection
  override suspend fun getUser(uid: String): User {
    if (uid.isEmpty()) {
      return User.empty()
    }
    val document = userDataCollection.document(uid).get().await()
    return if (document.exists()) {
      val email = document.getString("email") ?: ""
      val username = document.getString("username") ?: ""
      val photoUrl = Uri.parse(document.getString("photoUrl") ?: "")
      val location = document.getString("location") ?: "offline"
      User(uid, email, username, photoUrl, location)
    } else {
      Log.d("MyPrint", "user document not found for id $uid")
      User.empty()
    }
  }

  suspend fun getAllDailyPlanners(uid: String): List<DailyPlanner> {
    if (uid.isEmpty()) {
      return emptyList()
    }

    val document = dailyPlannerDataCollection.document(uid).get().await()

    return if (document.exists()) {
      val dailyPlanners =
          document.get("dailyPlanners") as? Map<String, Map<String, Any>> ?: emptyMap()
      dailyPlanners.values.map { plannerMap ->
        DailyPlanner(
            date = plannerMap["date"] as String,
            goals = plannerMap["goals"] as? List<String> ?: emptyList(),
            appointments = plannerMap["appointments"] as? Map<String, String> ?: emptyMap(),
            notes = plannerMap["notes"] as? List<String> ?: emptyList())
      }
    } else {
      emptyList()
    }
  }

  suspend fun getAllGroupDailyPlanners(uid: String): List<DailyPlanner> {
    if (uid.isEmpty()) {
      return emptyList()
    }

    val document_group = dailyPlannerGroupConnection.document(uid).get().await()
    return if (document_group.exists()) {
      val dailyPlannersforgroup =
          document_group.get("dailyPlanners") as? Map<String, Map<String, Any>> ?: emptyMap()
      dailyPlannersforgroup.values.map { plannerMap ->
        DailyPlanner(
            date = plannerMap["date"] as String,
            goals = plannerMap["goals"] as? List<String> ?: emptyList(),
            appointments = plannerMap["appointments"] as? Map<String, String> ?: emptyMap(),
            notes = plannerMap["notes"] as? List<String> ?: emptyList())
      }
    } else {
      emptyList()
    }
  }

  suspend fun getDailyPlanner(uid: String, date: String): DailyPlanner {
    if (uid.isEmpty() || date.isEmpty()) {
      return DailyPlanner(date)
    }

    val document = dailyPlannerDataCollection.document(uid).get().await()
    return if (document.exists()) {
      val dailyPlanners =
          document.get("dailyPlanners") as? Map<String, Map<String, Any>> ?: emptyMap()
      val plannerMap = dailyPlanners[date]
      if (plannerMap != null) {
        DailyPlanner(
            date = plannerMap["date"] as String,
            goals = plannerMap["goals"] as? List<String> ?: emptyList(),
            appointments = plannerMap["appointments"] as? Map<String, String> ?: emptyMap(),
            notes = plannerMap["notes"] as? List<String> ?: emptyList())
      } else {
        DailyPlanner(date)
      }
    } else {
      DailyPlanner(date)
    }
  }

  suspend fun getDailyGroupPlanner(uid: String, date: String): DailyPlanner {
    if (uid.isEmpty() || date.isEmpty()) {
      return DailyPlanner(date)
    }

    val document_group = dailyPlannerGroupConnection.document(uid).get().await()
    return if (document_group.exists()) {
      val dailyPlanners_Group =
          document_group.get("dailyPlanners") as? Map<String, Map<String, Any>> ?: emptyMap()
      val plannerMap = dailyPlanners_Group[date]
      if (plannerMap != null) {
        DailyPlanner(
            date = plannerMap["date"] as String,
            goals = plannerMap["goals"] as? List<String> ?: emptyList(),
            appointments = plannerMap["appointments"] as? Map<String, String> ?: emptyMap(),
            notes = plannerMap["notes"] as? List<String> ?: emptyList())
      } else {
        DailyPlanner(date)
      }
    } else {
      DailyPlanner(date)
    }
  }

  override fun updateDailyPlanners(uid: String, dailyPlanners: List<DailyPlanner>) {
    if (uid.isEmpty()) return

    val planners_Map = dailyPlanners.associateBy { it.date }
    val data = mapOf("dailyPlanners" to planners_Map)

    dailyPlannerDataCollection
        .document(uid)
        .set(data)
        .addOnSuccessListener {
          Log.d("MyPrint", "DailyPlanners successfully updated for user $uid")
        }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to update DailyPlanners for user $uid: ", e)
        }
  }

  fun updateGroupPlanners(groupId: String, dailyPlanners: List<DailyPlanner>) {
    if (groupId.isEmpty()) return

    val plannersMap = dailyPlanners.associateBy { it.date }
    val data = mapOf("dailyPlanners" to plannersMap)

    dailyPlannerGroupConnection
        .document(groupId)
        .set(data)
        .addOnSuccessListener {
          Log.d("MyPrint", "DailyPlanners  successfully updated for group $groupId")
        }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to update DailyPlanners for group $groupId: ", e)
        }
  }

  override suspend fun getCurrentUser(): User {
    return getUser(getCurrentUserUID())
  }

  override fun getCurrentUserUID(): String {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    return if (uid != null) {
      Log.d("MyPrint", "Fetched user UID is $uid")
      uid
    } else {
      Log.d("MyPrint", "Failed to get current user UID")
      ""
    }
  }

  override suspend fun getAllUsers(): List<User> {
    val uid = getCurrentUserUID()
    return try {
      val snapshot = userDataCollection.document(uid).get().await()
      val snapshotQuery = userDataCollection.get().await()
      val items = mutableListOf<User>()

      if (snapshot.exists()) {
        // val userUIDs = snapshot.data?.get("friends") as? List<String>
        for (item in snapshotQuery.documents) {
          val id = item.id
          items.add(getUser(id))
        }
      } else {
        Log.d("MyPrint", "User with uid $uid does not exist")
      }
      items
    } catch (e: Exception) {
      Log.d("MyPrint", "Could not fetch all users with error: $e")
      emptyList()
    }
  }

  override suspend fun getAllFriends(uid: String): List<User> {
    return try {
      val snapshot = userContactsCollection.document(uid).get().await()
      val items = mutableListOf<User>()

      val contactVM = ContactsViewModel()

      if (snapshot.exists()) {
        val contactsUID = snapshot.data?.get("contacts") as? List<String>
        contactsUID?.let { contactsID ->
          contactsID.forEach { contactID ->
            val friendID = contactVM.getOtherUser(contactID, uid)
            items.add(getUser(friendID))
          }
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
    return storage.child("userData/default.jpg").downloadUrl.await()
  }

  override suspend fun createUser(
      uid: String,
      email: String,
      username: String,
      profilePictureUri: Uri,
      location: String
  ) {
    Log.d(
        "MyPrint",
        "Creating new user with uid $uid, email $email, username $username and picture link $profilePictureUri")
    val user =
        hashMapOf(
            "email" to email,
            "username" to username,
            "photoUrl" to profilePictureUri.toString(),
            "location" to location)
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

    val contactList =
        hashMapOf("pendingRequests" to emptyList<String>(), "contacts" to emptyList<String>())
    userContactsCollection
        .document(uid)
        .set(contactList)
        .addOnSuccessListener { Log.d("MyPrint", "User contact list successfully created") }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to create user contact list with error: ", e)
        }
    val daily = hashMapOf("dailyPlanners" to emptyList<Map<String, Any>>())
    dailyPlannerDataCollection
        .document(uid)
        .set(daily)
        .addOnSuccessListener { Log.d("MyPrint", "User daily palnner successfully created") }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to create user daily palnenr with error: ", e)
        }
  }

  override fun updateUserData(
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

  override fun updateLocation(uid: String, location: String) {
    userDataCollection
        .document(uid)
        .update("location", location)
        .addOnSuccessListener { Log.d("MyPrint", "User data successfully updated") }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to update user data with error: ", e)
        }
  }

  override fun userExists(
      uid: String,
      onSuccess: (Boolean) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    userDataCollection
        .document(uid)
        .get()
        .addOnSuccessListener { document -> onSuccess(document.exists()) }
        .addOnFailureListener { e -> onFailure(e) }
  }

  override fun groupExists(
      groupUID: String,
      onSuccess: (Boolean) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    groupDataCollection
        .document(groupUID)
        .get()
        .addOnSuccessListener { document -> onSuccess(document.exists()) }
        .addOnFailureListener { e -> onFailure(e) }
  }

  // using the groups & userMemberships collections
  override suspend fun getAllGroups(uid: String): GroupList {
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
            val timerStateMap = document.get("timerState") as? Map<String, Any>
            val endTime = timerStateMap?.get("endTime") as? Long ?: System.currentTimeMillis()
            val isRunning = timerStateMap?.get("isRunning") as? Boolean ?: false
            val timerState = TimerState(endTime, isRunning)

            val topics = document.get("topics") as? List<String> ?: emptyList()
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

  override suspend fun updateGroupTimer(groupUID: String, timerState: TimerState): Int {
    if (groupUID.isEmpty()) {
      Log.d("DatabaseConnection", "Group UID is empty")
      return -1
    }

    try {
      groupDataCollection
          .document(groupUID)
          .update("timerState", timerState)
          .addOnSuccessListener {
            Log.d(
                "DatabaseConnection",
                "Timer parameter updated successfully for group with UID $groupUID")
          }
          .addOnFailureListener { e ->
            Log.d(
                "DatabaseConnection",
                "Failed to update timer parameter for group with UID $groupUID with error: ",
                e)
          }
    } catch (e: Exception) {
      Log.e("DatabaseConnection", "Exception when updating timer: ", e)
      return -1
    }

    return 0
  }

  override suspend fun getGroup(groupUID: String): Group {
    val document = groupDataCollection.document(groupUID).get().await()
    return if (document.exists()) {
      val name = document.getString("name") ?: ""
      val picture = Uri.parse(document.getString("picture") ?: "")
      val members = document.get("members") as List<String>
      val timerStateMap = document.get("timerState") as? Map<String, Any>
      val endTime = timerStateMap?.get("endTime") as? Long ?: System.currentTimeMillis()
      val isRunning = timerStateMap?.get("isRunning") as? Boolean ?: false
      val timerState = TimerState(endTime, isRunning)

      val topics = document.get("topics") as List<String>
      Group(groupUID, name, picture, members, topics, timerState)
    } else {
      Log.d("MyPrint", "group document not found for group id $groupUID")
      Group.empty()
    }
  }

  override suspend fun getGroupName(groupUID: String): String {
    val document = groupDataCollection.document(groupUID).get().await()
    return if (document.exists()) {
      document.getString("name") ?: ""
    } else {
      Log.d("MyPrint", "group document not found for group id $groupUID")
      ""
    }
  }

  override suspend fun getDefaultPicture(): Uri {
    return storage.child("groupData/default_group.jpg").downloadUrl.await()
  }

  override suspend fun createGroup(name: String, photoUri: Uri) {
    val uid = if (name == "Official Group Testing") "111testUser" else getCurrentUserUID()
    Log.d("MyPrint", "Creating new group with uid $uid and picture link $photoUri")
    Log.d("MyPrint", "Creating new group with uid $uid and picture link ${photoUri.toString()}")
    val timerState =
        mapOf(
            "endTime" to System.currentTimeMillis(), // current time as placeholder
            "isRunning" to false // timer is not running initially
            )
    val group =
        hashMapOf(
            "name" to name,
            "picture" to photoUri.toString(),
            "members" to listOf(uid),
            "topics" to emptyList<String>(),
            "timerState" to timerState)
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
      val daily2 = hashMapOf("dailyPlanners" to emptyList<Map<String, Any>>())
      dailyPlannerGroupConnection
          .document(uid)
          .set(daily2)
          .addOnSuccessListener { Log.d("MyPrint", "User daily palnner successfully created") }
          .addOnFailureListener { e ->
            Log.d("MyPrint", "Failed to create user daily palnenr with error: ", e)
          }

      groupDataCollection.add(group).addOnSuccessListener { documentReference ->
        val groupUID = documentReference.id
        val pictureRef = storage.child("groupData/$groupUID/picture.jpg")
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

  /*
   * Add the user given in the parameter to the group given in the parameter
   * If no user is given add the user actually logged in
   *
   * return nothing but use callBack to know if the add was a succes or a failure
   */
  override suspend fun addUserToGroup(groupUID: String, user: String, callBack: (Boolean) -> Unit) {

    if (groupUID == "") {
      callBack(true)
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
      callBack(true)
      Log.d("MyPrint", "User with uid $userToAdd does not exist")
      return
    }

    val document = groupDataCollection.document(groupUID).get().await()
    if (!document.exists()) {
      callBack(true)
      Log.d("MyPrint", "Group with uid $groupUID does not exist")
      return
    }
    // add user to group
    groupDataCollection
        .document(groupUID)
        .update("members", FieldValue.arrayUnion(userToAdd))
        .addOnSuccessListener {
          Log.d("MyPrint", "User successfully added to group")
          callBack(false)
        }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to add user to group with error: ", e)
        }

    // add group to the user's list of groups
    userMembershipsCollection
        .document(userToAdd)
        .update("groups", FieldValue.arrayUnion(groupUID))
        .addOnSuccessListener { Log.d("MyPrint", "Group successfully added to user") }
        .addOnSuccessListener {
          Log.d("MyPrint", "Group successfully added to user")
          callBack(false)
        }
  }

  /*
   * Add the user actually logged in to the group given in the parameter
   */
  override suspend fun addSelfToGroup(groupUID: String) {

    if (groupUID == "") {
      Log.d("MyPrint", "Group UID is empty")
      return
    }

    val userToAdd = getCurrentUserUID()

    if (getUser(userToAdd) == User.empty()) {
      // should never happen
      Log.d("MyPrint", "User not connected")
      return
    }

    val document = groupDataCollection.document(groupUID).get().await()
    if (!document.exists()) {
      Log.d("MyPrint", "Group with uid $groupUID does not exist")
      return
    }
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
  }

  override fun updateGroup(groupUID: String, name: String, photoUri: Uri) {

    // change name of group
    groupDataCollection
        .document(groupUID)
        .update("name", name)
        .addOnSuccessListener { Log.d("UpdateGroup", "group name successfully updated") }
        .addOnFailureListener { e ->
          Log.d("UpdateGroup", "Failed modify group name with error: ", e)
        }

    // change picture of group
    groupDataCollection
        .document(groupUID)
        .update("picture", photoUri.toString())
        .addOnSuccessListener { Log.d("MyPrint", "picture successfully updated") }
        .addOnFailureListener { e ->
          Log.d("UpdateGroup", "Failed modify group picture with error: ", e)
        }

    val pictureRef = storage.child("groupData/$groupUID/picture.jpg")
    pictureRef
        .putFile(photoUri)
        .addOnSuccessListener {
          pictureRef.downloadUrl.addOnSuccessListener { uri ->
            groupDataCollection.document(groupUID).update("picture", uri.toString())
            Log.d("UpdateGroup", "Successfully upload group photo")
          }
        }
        .addOnFailureListener { e ->
          Log.d("UpdateGroup", "Failed to upload photo with error: ", e)
        }
  }

  override suspend fun removeUserFromGroup(groupUID: String, userUID: String) {

    val user =
        if (userUID == "") {
          getCurrentUserUID()
        } else {
          userUID
        }

    groupDataCollection
        .document(groupUID)
        .update("members", FieldValue.arrayRemove(user))
        .addOnSuccessListener { Log.d("Deletion", "User successfully removed from group") }
        .addOnFailureListener { e ->
          Log.d("Deletion", "Failed to remove user from group with error: ", e)
        }

    val document = groupDataCollection.document(groupUID).get().await()
    val members = document.get("members") as? List<String> ?: emptyList()

    if (members.isEmpty()) {
      groupDataCollection
          .document(groupUID)
          .delete()
          .addOnSuccessListener {
            storage
                .child("groupData/$groupUID")
                .delete()
                .addOnSuccessListener { Log.d("Deletion", "Group picture successfully deleted") }
                .addOnFailureListener { e ->
                  Log.d("Deletion", "Failed to delete group picture with error: ", e)
                }

            storage
                .child("chatData/$groupUID")
                .delete()
                .addOnSuccessListener { Log.d("Deletion", "Group picture successfully deleted") }
                .addOnFailureListener { e ->
                  Log.d("Deletion", "Failed to delete group picture with error: ", e)
                }
            Log.d("Deletion", "User successfully removed from group")
          }
          .addOnFailureListener { e ->
            Log.d("Deletion", "Failed to remove user from group with error: ", e)
          }
    }

    userMembershipsCollection
        .document(user)
        .update("groups", FieldValue.arrayRemove(groupUID))
        .addOnSuccessListener { Log.d("Deletion", "Remove group from user successfully") }
        .addOnFailureListener { e ->
          Log.d("Deletion", "Failed to remove group from user with error: ", e)
        }
  }

  override suspend fun deleteGroup(groupUID: String) {

    val document = groupDataCollection.document(groupUID).get().await()
    val members = document.get("members") as? List<String> ?: emptyList()

    if (groupUID != "") {
      storage
          .child("groupData/$groupUID")
          .delete()
          .addOnSuccessListener { Log.d("Deletion", "Group picture successfully deleted") }
          .addOnFailureListener { e ->
            Log.d("Deletion", "Failed to delete group picture with error: ", e)
          }

      storage
          .child("chatData/$groupUID")
          .delete()
          .addOnSuccessListener { Log.d("Deletion", "Group picture successfully deleted") }
          .addOnFailureListener { e ->
            Log.d("Deletion", "Failed to delete group picture with error: ", e)
          }
    }

    if (members.isNotEmpty()) {
      val listSize = members.size

      for (i in 0 until listSize) {
        val user = members[i]

        userMembershipsCollection
            .document(user)
            .update("groups", FieldValue.arrayRemove(groupUID))
            .addOnSuccessListener { Log.d("Deletion", "Remove group from user successfully") }
            .addOnFailureListener { e ->
              Log.d("Deletion", "Failed to remove group from user with error: ", e)
            }
      }
    }

    groupDataCollection
        .document(groupUID)
        .delete()
        .addOnSuccessListener { Log.d("Deletion", "User successfully removed from group") }
        .addOnFailureListener { e ->
          Log.d("Deletion", "Failed to remove user from group with error: ", e)
        }
  }

  // using the Realtime Database for messages

  // using the Realtime Database for messages
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
        uploadChatFile(message.uid, chatUID, message.fileUri) { uri ->
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
        messageData[MessageVal.LINK_NAME] = message.linkName
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
      else -> {
        Log.d("MyPrint", "Message type not recognized")
      }
    }
  }

  override fun saveMessage(path: String, data: Map<String, Any>) {
    rtDb
        .getReference(path)
        .updateChildren(data)
        .addOnSuccessListener { Log.d("MessageSend", "Message successfully written!") }
        .addOnFailureListener { e -> Log.w("MessageSend", "Failed to write message.", e) }
  }

  override fun uploadChatImage(
      uid: String,
      chatUID: String,
      imageUri: Uri,
      callback: (Uri?) -> Unit
  ) {
    val storagePath = "chatData/$chatUID/$uid.jpg"
    val pictureRef = storage.child(storagePath)

    pictureRef
        .putFile(imageUri)
        .addOnSuccessListener {
          pictureRef.downloadUrl.addOnSuccessListener { uri -> callback(uri) }
        }
        .addOnFailureListener { e ->
          Log.e("UploadChatImage", "Failed to upload image: ", e)
          callback(null)
        }
  }

  private fun uploadChatFile(uid: String, chatUID: String, fileUri: Uri, callback: (Uri?) -> Unit) {
    val storagePath = "chatData/$chatUID/$uid"
    val fileRef = storage.child(storagePath)

    fileRef
        .putFile(fileUri)
        .addOnSuccessListener { fileRef.downloadUrl.addOnSuccessListener { uri -> callback(uri) } }
        .addOnFailureListener { e ->
          Log.e("UploadChatFile", "Failed to upload file: ", e)
          callback(null)
        }
  }

  override fun deleteMessage(groupUID: String, message: Message, chatType: ChatType) {
    val messagePath = getMessagePath(groupUID, chatType) + "/${message.uid}"
    rtDb.getReference(messagePath).removeValue()
  }

  override suspend fun removeTopic(uid: String) {
    getTopic(uid) { topic -> rtDb.getReference(topic.toString()).removeValue() }
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
        rtDb.getReference(messagePath).updateChildren(mapOf(MessageVal.TEXT to newText))
      }
      is Message.LinkMessage -> {
        val messagePath = getMessagePath(groupUID, chatType) + "/${message.uid}"
        rtDb.getReference(messagePath).updateChildren(mapOf(MessageVal.LINK to newText))
      }
      else -> {
        Log.d("MyPrint", "Message type not recognized")
      }
    }
  }

  override fun getMessagePath(chatUID: String, chatType: ChatType, additionalUID: String): String {
    return when (chatType) {
      ChatType.PRIVATE -> getPrivateMessagesPath(chatUID)
      ChatType.GROUP -> getGroupMessagesPath(chatUID)
      ChatType.TOPIC -> getTopicMessagesPath(chatUID, additionalUID)
    }
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

  override fun subscribeToPrivateChats(
      userUID: String,
      scope: CoroutineScope,
      ioDispatcher: CoroutineDispatcher,
      mainDispatcher: CoroutineDispatcher,
      onUpdate: (List<Chat>) -> Unit
  ) {
    val ref = rtDb.getReference(ChatVal.DIRECT_MESSAGES)

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
        })
  }

  override fun getMessages(
      chat: Chat,
      liveData: MutableStateFlow<List<Message>>,
      ioDispatcher: CoroutineDispatcher,
      mainDispatcher: CoroutineDispatcher
  ) {
    val ref = rtDb.getReference(getMessagePath(chat.uid, chat.type, chat.additionalUID))

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
                            val users = userUIDs.map { uid -> getUser(uid) }
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

  override fun checkForExistingChat(
      currentUserUID: String,
      otherUID: String,
      onResult: (Boolean, String?) -> Unit
  ) {
    val query =
        rtDb
            .getReference(ChatVal.DIRECT_MESSAGES)
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

  override suspend fun startDirectMessage(otherUID: String, contactID: String) {
    val currentUserUID = getCurrentUserUID()
    val contactsViewModel = ContactsViewModel()
    Log.d("MyPrint", "startDirectMessage called in db")
    checkForExistingChat(currentUserUID, otherUID) { chatExists, chatId ->
      if (chatExists) {
        Log.d("MyPrint", "startDirectMessage: chat already exists with ID: $chatId")
      } else {
        Log.d("MyPrint", "startDirectMessage: creating new chat")
        val newChatId = contactID
        // val newChatId = UUID.randomUUID().toString()
        val memberPath = getPrivateChatMembersPath(newChatId)
        val members = mapOf(currentUserUID to true, otherUID to true)
        rtDb
            .getReference(memberPath)
            .updateChildren(members)
            .addOnSuccessListener {
              Log.d("DatabaseConnect", "startDirectMessage : Members successfully added!")
              // contactsViewModel.createContact(otherUID, contactID)
            }
            .addOnFailureListener {
              Log.w("DatabaseConnect", "startDirectMessage : Failed to write members.", it)
            }
      }
    }
  }

  override fun updateContact(contactID: String, showOnMap: Boolean) {
    contactDataCollection
        .document(contactID)
        .update("showOnMap", showOnMap)
        .addOnSuccessListener { Log.d("UpdateContact", "contact successfully updated") }
        .addOnFailureListener { e ->
          Log.d("UpdateContact", "Failed modify contact with error: ", e)
        }
  }

  override fun votePollMessage(chat: Chat, message: Message.PollMessage) {
    val messagePath =
        getMessagePath(chat.uid, chat.type, chat.additionalUID) +
            "/${message.uid}/${MessageVal.POLL_VOTES}"
    val reference = rtDb.getReference(messagePath)

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

  // using the topicData and topicItemData collections
  override suspend fun getTopic(uid: String, callBack: (Topic) -> Unit) {
    val document = topicDataCollection.document(uid).get().await()
    if (document.exists()) {
      val name = document.getString(topic_name) ?: ""
      val exercisesList = document.get(topic_exercises) as List<String>
      val theoryList = document.get(topic_theory) as List<String>
      val exercises =
          if (exercisesList.isNotEmpty()) {
            fetchTopicItems(exercisesList)
          } else {
            emptyList()
          }
      val theory =
          if (theoryList.isNotEmpty()) {
            fetchTopicItems(theoryList)
          } else {
            emptyList()
          }
      val topic = Topic(uid, name, exercises, theory)
      callBack(topic)
    } else {
      Log.d("MyPrint", "topic document not found for id $uid")
      callBack(Topic.empty())
    }
  }

  override suspend fun getTopicFile(id: String): TopicFile {
    val document = topicItemCollection.document(id).get().await()
    return if (document.exists()) {
      val name = document.getString(topic_name) ?: ""
      val strongUsers = document.get(item_strongUsers) as List<String>
      val parentUID = document.getString(item_parent) ?: ""
      TopicFile(id, name, strongUsers, parentUID)
    } else {
      TopicFile.empty()
    }
  }

  override fun fileAddImage(fileID: String, image: Uri, callBack: () -> Unit) {
    val imageRef = storage.child("topicResources/$fileID/${image.lastPathSegment}.jpg")
    imageRef.putFile(image).addOnSuccessListener { callBack() }
  }

  override suspend fun getTopicFileImages(fileID: String): List<Uri> {
    val path = storage.child("topicResources/$fileID")

    return try {
      val result = path.listAll().await()
      if (result.items.isEmpty()) {
        emptyList()
      } else {
        result.items
            .filter { item -> item.name.endsWith(".jpg", true) }
            .map { image -> image.downloadUrl.await() }
      }
    } catch (e: Exception) {
      emptyList()
    }
  }

  override suspend fun fetchTopicItems(listUID: List<String>): List<TopicItem> {
    val items = mutableListOf<TopicItem>()
    for (itemUID in listUID) {
      val document = topicItemCollection.document(itemUID).get().await()
      if (document.exists()) {
        val name = document.getString(topic_name) ?: ""
        val parentUID = document.getString(item_parent) ?: ""
        val type = ItemType.valueOf(document.getString(item_type) ?: ItemType.FILE.toString())
        when (type) {
          ItemType.FOLDER -> {
            val folderItemsList = document.get(item_items) as List<String>
            val folderItems = fetchTopicItems(folderItemsList)
            items.add(TopicFolder(itemUID, name, folderItems, parentUID))
          }
          ItemType.FILE -> {
            val strongUsers = document.get(item_strongUsers) as List<String>
            items.add(TopicFile(itemUID, name, strongUsers, parentUID))
          }
        }
      }
    }
    return items
  }

  override fun createTopic(name: String, callBack: (String) -> Unit) {
    val topic =
        hashMapOf(
            topic_name to name,
            topic_exercises to emptyList<String>(),
            topic_theory to emptyList<String>())
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

  override suspend fun addTopicToGroup(topicUID: String, groupUID: String) {
    val document = groupDataCollection.document(groupUID).get().await()
    if (document.exists()) {
      groupDataCollection
          .document(groupUID)
          .update("topics", FieldValue.arrayUnion(topicUID))
          .addOnSuccessListener { Log.d("MyPrint", ("topic successfully added to group")) }
          .addOnFailureListener { e ->
            Log.d("MyPrint", ("failed to add topic to group with error $e"))
          }
    } else {
      Log.d("MyPrint", ("group document not found for uid $groupUID"))
    }
  }

  override fun addExercise(uid: String, exercise: TopicItem) {
    val exerciseUID = exercise.uid
    topicDataCollection
        .document(uid)
        .update(topic_exercises, FieldValue.arrayUnion(exerciseUID))
        .addOnSuccessListener {
          Log.d("MyPrint", "topic data successfully updated")
          updateTopicItem(exercise)
        }
        .addOnFailureListener { e -> Log.d("MyPrint", "topic failed to update with error ", e) }
  }

  override fun addTheory(uid: String, theory: TopicItem) {
    val theoryUID = theory.uid
    topicDataCollection
        .document(uid)
        .update(topic_theory, FieldValue.arrayUnion(theoryUID))
        .addOnSuccessListener {
          Log.d("MyPrint", "topic data successfully updated")
          updateTopicItem(theory)
        }
        .addOnFailureListener { e -> Log.d("MyPrint", "topic failed to update with error ", e) }
  }

  override suspend fun deleteTopic(topicId: String, groupUID: String, callBack: () -> Unit) {
    getTopic(topicId) { topic ->
      val items: List<TopicItem> = topic.exercises + topic.theory
      iterateTopicItemDeletion(items) {
        topicDataCollection
            .document(topic.uid)
            .delete()
            .addOnSuccessListener {
              groupDataCollection
                  .document(groupUID)
                  .update("topics", FieldValue.arrayRemove(topicId))
                  .addOnSuccessListener {
                    callBack()
                    Log.d("MyPrint", "Topic successfully removed from group")
                  }
                  .addOnFailureListener { Log.d("MyPrint", "Failed to remove topic from group") }
              Log.d("MyPrint", "Topic ${topic.uid} successfully deleted")
            }
            .addOnFailureListener { Log.d("MyPrint", "Failed to delete topic ${topic.uid}") }
      }
    }
  }

  private fun iterateTopicItemDeletion(items: List<TopicItem>, callBack: () -> Unit) {
    items.forEach { topicItem ->
      when (topicItem) {
        is TopicFile -> {
          topicItemCollection
              .document(topicItem.uid)
              .delete()
              .addOnSuccessListener {
                Log.d("MyPrint", "Topic file ${topicItem.uid} successfully deleted")
              }
              .addOnFailureListener {
                Log.d("MyPrint", "Failed to delete topic file ${topicItem.uid}")
              }
        }
        is TopicFolder -> {
          val children = topicItem.items
          iterateTopicItemDeletion(children) {
            topicItemCollection
                .document(topicItem.uid)
                .delete()
                .addOnSuccessListener {
                  Log.d("MyPrint", "Topic folder ${topicItem.uid} successfully deleted")
                }
                .addOnFailureListener {
                  Log.d("MyPrint", "Failed to delete topic folder ${topicItem.uid}")
                }
          }
        }
      }
    }
    callBack()
  }

  override fun updateTopicName(uid: String, name: String) {
    topicDataCollection
        .document(uid)
        .update(topic_name, name)
        .addOnSuccessListener { Log.d("MyPrint", "topic data successfully updated") }
        .addOnFailureListener { e -> Log.d("MyPrint", "topic failed to update with error ", e) }
  }

  override fun createTopicFolder(name: String, parentUID: String, callBack: (TopicFolder) -> Unit) {
    val folder =
        hashMapOf(
            topic_name to name,
            item_type to ItemType.FOLDER.toString(),
            item_items to emptyList<String>(),
            item_parent to parentUID)
    var uid: String
    topicItemCollection
        .add(folder)
        .addOnSuccessListener { document ->
          uid = document.id
          if (parentUID.isNotBlank()) {
            topicItemCollection.document(parentUID).update(item_items, FieldValue.arrayUnion(uid))
          }
          Log.d("MyPrint", "New topic folder successfully created")
          callBack(TopicFolder(uid, name, emptyList(), parentUID))
        }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to create new topic folder with error ", e)
          callBack(TopicFolder("", "", emptyList(), parentUID))
        }
  }

  override fun createTopicFile(name: String, parentUID: String, callBack: (TopicFile) -> Unit) {
    val file =
        hashMapOf(
            topic_name to name,
            item_type to ItemType.FILE.toString(),
            item_strongUsers to emptyList<String>(),
            item_parent to parentUID)
    var uid = ""
    topicItemCollection
        .add(file)
        .addOnSuccessListener { document ->
          uid = document.id
          if (parentUID.isNotBlank()) {
            topicItemCollection.document(parentUID).update(item_items, FieldValue.arrayUnion(uid))
          }
          Log.d("MyPrint", "New topic file successfully created with uid ${document.id}")
          callBack(TopicFile(uid, name, emptyList(), parentUID))
        }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "Failed to create new topic file with error ", e)
          callBack(TopicFile("", "", emptyList(), parentUID))
        }
  }

  override fun updateTopicItem(item: TopicItem) {
    var task = emptyMap<String, Any>()
    var type = ""
    var folderItems = emptyList<String>()
    var strongUsers = emptyList<String>()
    when (item) {
      is TopicFolder -> {
        type = ItemType.FOLDER.toString()
        folderItems = item.items.map { it.uid }
        task = hashMapOf(topic_name to item.name, item_type to type, item_items to folderItems)
      }
      is TopicFile -> {
        type = ItemType.FILE.toString()
        strongUsers = item.strongUsers
        task =
            hashMapOf(topic_name to item.name, item_type to type, item_strongUsers to strongUsers)
      }
    }
    topicItemCollection
        .document(item.uid)
        .update(task as Map<String, Any>)
        .addOnSuccessListener { Log.d("MyPrint", "topic item ${item.uid} successfully updated") }
        .addOnFailureListener { e ->
          Log.d("MyPrint", "topic item ${item.uid} failed to update with error ", e)
        }
  }

  override suspend fun getIsUserStrong(fileID: String, callBack: (Boolean) -> Unit) {
    val document = topicItemCollection.document(fileID).get().await()
    if (document.exists()) {
      val strongUsers = document.get(item_strongUsers) as List<String>
      val currentUser = getCurrentUserUID()
      callBack(strongUsers.contains(currentUser))
    }
  }

  override suspend fun updateStrongUser(fileID: String, newValue: Boolean) {
    val currentUser = getCurrentUserUID()
    if (newValue) {
      topicItemCollection
          .document(fileID)
          .update(item_strongUsers, FieldValue.arrayUnion(currentUser))
    } else {
      topicItemCollection
          .document(fileID)
          .update(item_strongUsers, FieldValue.arrayRemove(currentUser))
    }
  }

  override fun subscribeToGroupTimerUpdates(
      groupUID: String,
      _timerValue: MutableStateFlow<Long>,
      _isRunning: MutableStateFlow<Boolean>,
      ioDispatcher: CoroutineDispatcher,
      mainDispatcher: CoroutineDispatcher,
      onTimerStateChanged: suspend (TimerState) -> Unit
  ) {
    groupDataCollection.document(groupUID).addSnapshotListener { snapshot, e ->
      if (e != null) {
        Log.w("DatabaseConnection", "Listen failed.", e)
        return@addSnapshotListener
      }

      if (snapshot != null && snapshot.exists()) {
        val timerStateMap = snapshot.get("timerState") as? Map<String, Any>
        val endTime = timerStateMap?.get("endTime") as? Long ?: 0L
        val isRunning = timerStateMap?.get("running") as? Boolean ?: false
        val timerState = TimerState(endTime, isRunning)
        CoroutineScope(ioDispatcher).launch {
          val remainingTime = endTime
          withContext(mainDispatcher) {
            _timerValue.value = remainingTime
            _isRunning.value = isRunning
            onTimerStateChanged(timerState)
          }
        }
      } else {
        Log.d("DatabaseConnection", "Current data: null")
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
    val docRef = groupDataCollection.document(groupUID)
    docRef.addSnapshotListener { snapshot, e ->
      if (e != null) {
        Log.w("MyPrint", "Listen failed.", e)
        return@addSnapshotListener
      }

      if (snapshot != null && snapshot.exists()) {

        scope.launch(ioDispatcher) {
          val items = mutableListOf<Topic>()
          val topicUIDs = snapshot.data?.get("topics") as? List<String> ?: emptyList()
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
  }

  // TODO add to mockdb
  override suspend fun getAllRequests(uid: String): RequestList {
    try {
      val snapshot = userContactsCollection.document(uid).get().await()
      val items = mutableListOf<User>()

      if (snapshot.exists()) {
        val requestsUIDs = snapshot.data?.get("pendingRequests") as? List<String>
        requestsUIDs?.let { requestsIDs ->
          requestsIDs.forEach { requestID ->
            try {
              val request = getUser(requestID)
              items.add(request)
            } catch (e: Exception) {
              Log.e("contacts", "Error fetching pending Request with ID $requestID: $e")
            }
          }
        }
        Log.d("contacts", "fetched all pending Requests for user $uid")
        return RequestList(items)
      } else {
        Log.d("contacts", "User with uid $uid does not exist")
        return RequestList(emptyList())
      }
    } catch (e: Exception) {
      Log.e("contacts", "could not fetch all contacts for user $uid with error: $e")
    }
    return RequestList(emptyList())
  }

  // TODO() add mockdb
  override suspend fun deleteRequest(requestID: String) {
    val uid = getCurrentUserUID()
    userContactsCollection
        .document(uid)
        .update("pendingRequests", FieldValue.arrayRemove(requestID))
  }

  // TODO() add mockdb
  override suspend fun acceptRequest(requestID: String) {
    val uid = getCurrentUserUID()
    userContactsCollection
        .document(uid)
        .update("pendingRequests", FieldValue.arrayRemove(requestID))
    val testID = "AAAAAAAAAAAAAAA"
    createContact(requestID)
  }

  // TODO() add to  mockdb
  override suspend fun sendContactRequest(targetID: String) {
    val uid = getCurrentUserUID()
    userContactsCollection.document(targetID).update("pendingRequests", FieldValue.arrayUnion(uid))
  }

  override suspend fun getAllContacts(uid: String): ContactList {
    try {
      val snapshot = userContactsCollection.document(uid).get().await()
      val items = mutableListOf<Contact>()

      if (snapshot.exists()) {
        val contactsUIDs = snapshot.data?.get("contacts") as? List<String>
        contactsUIDs?.let { contactsIDs ->
          contactsIDs.forEach { contactID ->
            try {
              val document = contactDataCollection.document(contactID).get().await()
              val members = document.get("members") as? List<String> ?: emptyList()
              val showOnMap = document.get("showOnMap") as Boolean ?: false
              // val hasDM = document.get("hasStartedDM") as Boolean ?: false
              val hasDM = false
              items.add(Contact(contactID, members, showOnMap, hasDM))
            } catch (e: Exception) {
              Log.e("contacts", "Error fetching contact with ID $contactID: $e")
            }
          }
        }
        Log.d("contacts", "fetched all contacts for user $uid")
        return ContactList(items)
      } else {
        Log.d("contacts", "User with uid $uid does not exist")
        return ContactList(emptyList())
      }
    } catch (e: Exception) {
      Log.e("contacts", "could not fetch all contacts for user $uid with error: $e")
    }
    return ContactList(emptyList())
  }

  override suspend fun getContact(contactID: String): Contact {

    Log.d("contact", "getContact before $contactID")
    val document = contactDataCollection.document(contactID).get().await()
    Log.d("contact", "getContact after $contactID")

    return if (document.exists()) {
      Log.d("contact", "contact document found for contact id $contactID")
      val members = document.get("members") as? List<String> ?: emptyList()
      val showOnMap = document.get("showOnMap") as Boolean
      // val hasDM = document.get("hasStartedDM") as Boolean
      val hasDM = false
      Contact(contactID, members, showOnMap, hasDM)
    } else {
      Log.d("contact", "contact document not found for contact id $contactID")
      Contact.empty()
    }
  }

  override suspend fun createContact(otherUID: String) {

    val uid = getCurrentUserUID()
    Log.d("MyPrint", "Creating new contact with between $uid and $otherUID")
    // Log.d("MyPrint", "Creating new contact with with ID $contactID")
    // check if contact already exists
    val contactList = getAllContacts(uid)
    val filteredList = contactList.getFilteredContacts(otherUID)
    if (filteredList.isNotEmpty()) {
      (Log.d("MyPrint", "Contact already exists"))
    } else {
      val contact =
          hashMapOf(
              "members" to listOf(uid, otherUID), "showOnMap" to false, "hasStartedDM" to false)
      // updating contacts collection
      /*val contactRef = contactDataCollection.document(contactID)
      contactRef
          .set(contact)

       */
      contactDataCollection
          .add(contact)
          .addOnSuccessListener { doc ->
            Log.d("MyPrint", "Contact $contact successfully created")

            val contactID = doc.id
            // updating current user's list of contacts
            userContactsCollection
                .document(uid)
                .update("contacts", FieldValue.arrayUnion(contactID))
                .addOnSuccessListener {
                  Log.d("MyPrint", "Contact successfully added to userContacts")
                }
                .addOnFailureListener { e ->
                  Log.d("MyPrint", "Failed to add new contact to userContacts with error: ", e)
                }

            // updating other user's list of contacts
            userContactsCollection
                .document(otherUID)
                .update("contacts", FieldValue.arrayUnion(contactID))
                .addOnSuccessListener {
                  Log.d("MyPrint", "Contact successfully added to userContacts")
                }
                .addOnFailureListener { e ->
                  Log.d("MyPrint", "Failed to add new contact to userContacts with error: ", e)
                }
          }
          .addOnFailureListener { e ->
            Log.d("MyPrint", "Failed to create contact with error: ", e)
          }
    }
  }

  override fun deletePrivateChat(chatID: String) {

    val memberPath = getPrivateChatMembersPath(chatID)
    rtDb.getReference(memberPath).removeValue()
  }

  override fun deleteContact(contactID: String) {

    val uid = getCurrentUserUID()
    val contactsViewModel = ContactsViewModel()
    val otherUID = contactsViewModel.getOtherUser(contactID, uid)

    userContactsCollection.document(uid).update("contacts", FieldValue.arrayRemove(contactID))
    userContactsCollection.document(otherUID).update("contacts", FieldValue.arrayRemove(contactID))

    contactDataCollection
        .document(contactID)
        .delete()
        .addOnSuccessListener { Log.d("MyPrint", "Contact successfully deleted") }
        .addOnFailureListener { e -> Log.d("MyPrint", "Failed to delete contact with error: ", e) }

    Log.d("contact", "called deletecontact in db")
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
