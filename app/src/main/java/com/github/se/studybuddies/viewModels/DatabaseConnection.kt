package com.github.se.studybuddies.viewModels

import android.net.Uri
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.github.se.studybuddies.data.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.time.ZoneId
import java.util.Date
import kotlinx.coroutines.tasks.await

class DatabaseConnection {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

    // all collections
    private val userDataCollection = db.collection("userData")

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

    fun getUserData(uid: String): Task<DocumentSnapshot> {
        return userDataCollection.document(uid).get()
    }
    suspend fun getDefaultProfilePicture(): Uri {
        return storage.child("userData/default.jpg").downloadUrl.await()
    }

    fun createUser(uid: String, email: String, username: String, profilePictureUri: Uri) {
        val user = hashMapOf(
            "email" to email,
            "username" to username,
            "photoUrl" to profilePictureUri.toString()
        )
        userDataCollection.document(uid).set(user)
            .addOnSuccessListener {
                val pictureRef = storage.child("userData/$uid/profilePicture.jpg")
                pictureRef.putFile(profilePictureUri)
                    .addOnSuccessListener {
                        pictureRef.downloadUrl.addOnSuccessListener { uri ->
                            userDataCollection.document(uid).update("photoUrl", uri.toString())
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.d("MyPrint", "Failed to upload photo with error: ", e)
                    }
                Log.d("MyPrint", "User data successfully created")
            }
            .addOnFailureListener { e ->
                Log.d("MyPrint", "Failed to create user data with error: ", e)
            }
    }

    fun updateUserData(uid: String, email: String, username: String, profilePictureUri: Uri) {
        val task = hashMapOf("email" to email, "username" to username)
        userDataCollection.document(uid).update(task as Map<String, Any>)
            .addOnSuccessListener {
                val pictureRef = storage.child("userData/$uid/profilePicture.jpg")
                pictureRef.putFile(profilePictureUri)
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
        userDataCollection.document(uid).get()
            .addOnSuccessListener { document ->
                onSuccess(document.exists())
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}
