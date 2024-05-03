package com.github.se.studybuddies.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.database.DatabaseConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsersViewModel(private val uid: String?= null) : ViewModel() {
    private val db = DatabaseConnection()
    private val _friends = MutableStateFlow<List<User>>(emptyList())
    val friends: StateFlow<List<User>> = _friends

    init {
        if (uid != null) {
            fetchAllFriends(uid)
        }
    }

    fun fetchAllFriends(uid: String) {
        viewModelScope.launch {
            try {
                val users = db.getAllFriends(uid)
                _friends.value = users
            } catch (e: Exception) {
                Log.d("MyPrint", "In ViewModel, could not fetch friends with error $e")
            }
        }
    }
}