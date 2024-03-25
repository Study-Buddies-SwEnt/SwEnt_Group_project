package com.github.se.studybuddies.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserViewModel ( val uid: String? = null) : ViewModel() {
    private val db = DatabaseConnection()
    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> = _userData
    init {
        if (uid != null) {
            fetchUserData(getCurrentUserUID())
            Log.d("MyPrint", "UserViewModel initialized with uid $uid")
        } else {
            Log.d("MyPrint", "UserViewModel initialized without uid")
        }
    }

    fun getCurrentUserUID(): String {
        return db.getCurrentUserUID()
    }
    fun fetchUserData(uid: String) {
        viewModelScope.launch {
            try {
                val document = db.getUserData(uid).await()
                if (document.exists()) {
                    val email = document.getString("email") ?: ""
                    val username = document.getString("username") ?: ""
                    val photoUrl = Uri.parse(document.getString("photoUrl") ?: "")
                    val user = User(uid, email, username, photoUrl)
                    _userData.value = user
                } else {
                    Log.d ("MyPrint", "In ViewModel, document not found")
                    _userData.value = User.empty()
                }
            } catch (e: Exception) {
                Log.d("MyPrint", "In ViewModel, failed to fetch user data with error: ", e)
                _userData.value = User.empty()
            }
        }
    }
    suspend fun getDefaultProfilePicture(): Uri {
        return withContext(Dispatchers.IO) {
            db.getDefaultProfilePicture()
        }
    }
    fun createUser(uid: String, email: String, username: String, profilePictureUri: Uri) {
        db.createUser(uid, email, username, profilePictureUri)
    }
    fun updateUserData(uid: String, email: String, username: String, profilePictureUri: Uri) {
        db.updateUserData(uid, email, username, profilePictureUri)
    }


}