package com.github.se.studybuddies.database

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class StorageDatabaseConnection {
  private val storage = FirebaseStorage.getInstance().reference

  suspend fun getDefaultProfilePicture(): Uri {
    return storage.child("userData/default.jpg").downloadUrl.await()
  }

  suspend fun getDefaultGroupPicture(): Uri {
    return storage.child("groupData/default_group.jpg").downloadUrl.await()
  }

  suspend fun uploadUserProfilePicture(uid: String, profilePictureUri: Uri): Uri? {
    return try {
      val pictureRef = storage.child("userData/$uid/profilePicture.jpg")
      pictureRef.putFile(profilePictureUri).await()
      pictureRef.downloadUrl.await()
    } catch (e: Exception) {
      Log.e("StorageDatabase", "Failed to upload user profile picture: ", e)
      null
    }
  }

  suspend fun uploadGroupPicture(groupUID: String, photoUri: Uri): Uri? {
    return try {
      val pictureRef = storage.child("groupData/$groupUID/picture.jpg")
      pictureRef.putFile(photoUri).await()
      pictureRef.downloadUrl.await()
    } catch (e: Exception) {
      Log.e("StorageDatabase", "Failed to upload group picture: ", e)
      null
    }
  }

  fun uploadChatImage(uid: String, chatUID: String, imageUri: Uri, callback: (Uri?) -> Unit) {
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

  fun uploadChatFile(uid: String, chatUID: String, fileUri: Uri, callback: (Uri?) -> Unit) {
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

  suspend fun deleteGroupData(groupUID: String) {
    try {
      storage.child("groupData/$groupUID").delete().await()
      Log.d("StorageDatabase", "Group data successfully deleted")
    } catch (e: Exception) {
      Log.e("StorageDatabase", "Failed to delete group data: ", e)
    }

    try {
      storage.child("chatData/$groupUID").delete().await()
      Log.d("StorageDatabase", "Group chat data successfully deleted")
    } catch (e: Exception) {
      Log.e("StorageDatabase", "Failed to delete group chat data: ", e)
    }
  }
}
