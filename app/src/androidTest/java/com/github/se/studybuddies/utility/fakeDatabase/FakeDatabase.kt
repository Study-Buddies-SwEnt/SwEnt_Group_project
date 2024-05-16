package com.github.se.studybuddies.utility.fakeDatabase

import android.net.Uri
import android.util.Log
import com.github.se.studybuddies.data.Group
import com.github.se.studybuddies.data.Topic
import com.github.se.studybuddies.data.TopicItem
import com.github.se.studybuddies.data.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import java.util.concurrent.ConcurrentHashMap


class FakeDataBaseConnection {
    private val userDataCollection  = ConcurrentHashMap<String, User>()
    private val userMembershipsCollection = ConcurrentHashMap<String, List<String>>()
    private val groupDataCollection = ConcurrentHashMap<String, Group>()
    private val topicDataCollection = ConcurrentHashMap<String, Topic>()
    private val topicItemCollection = ConcurrentHashMap<String, TopicItem>()

    suspend fun getUser(uid: String): User {
        if (uid.isEmpty()) {
            return User.empty()
        }
        val document = userDataCollection[uid]
        return if (document != null) {
            val email = document.email ?: ""
            val username = document.username ?: ""
            val photoUrl = document.photoUrl ?: Uri.EMPTY
            val location = document.location ?: "offline"
            User(uid, email, username, photoUrl, location)
        } else {
            Log.d("MyPrint", "user document not found for id $uid")
            User.empty()
        }
    }

    suspend fun getCurrentUser(): User {
        return getUser(getCurrentUserUID())
    }

    fun getCurrentUserUID(): String {
        val uid = "userTest"
        return if (uid != null) {
            Log.d("MyPrint", "Fetched user UID is $uid")
            uid
        } else {
            Log.d("MyPrint", "Failed to get current user UID")
            ""
        }
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
            userDataCollection[uid] = User(uid, email, username, profilePictureUri, location)

        } else {
            userDataCollection[uid] = User(uid, email, username, getDefaultProfilePicture(), location)
        }

        val membership = hashMapOf("groups" to emptyList<String>())
    }
}
