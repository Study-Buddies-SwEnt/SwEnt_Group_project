package com.github.se.studybuddies.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.github.se.studybuddies.data.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.ZoneId

class userViewModel(private val uid: String? = null) : ViewModel() {
    private val db = FirebaseConnection()
    private val _user = MutableStateFlow(User.empty())
    val user: StateFlow<User> = _user

    fun getUserData(uid: String) {
        db.getUserData(uid).addOnCompleteListener(
            OnCompleteListener<DocumentSnapshot> { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        val email = document.getString("email") ?: ""
                        val username = document.getString("username") ?: ""
                        val photoUrl = document.getString("photoUrl") ?: ""
                        _user.value = User(uid, email, username, photoUrl)
                    } else {
                        Log.d("ErrorPrint", "Document not found with uid $uid")
                    }
                } else {
                    Log.d("ErrorPrint", "Fetching document with uid $uid failed with: ", task.exception)
                }
            })
    }
}