package com.github.se.studybuddies.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.Group
import com.github.se.studybuddies.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class GroupViewModel(private val uid: String? = null): ViewModel() {
    private val db = DatabaseConnection()
    private val _group = MutableLiveData<Group>(Group.empty())
    val group: LiveData<Group> = _group

    init {
        if (uid != null) {
            fetchGroupData(uid)
        }
    }

    fun fetchGroupData(uid: String) {
        viewModelScope.launch {
            try {
                val document = db.getGroupData(uid).await()
                if (document.exists()) {
                    val name = document.getString("name") ?: ""
                    val picture = Uri.parse(document.getString("picture") ?: "")
                    val members = document.get("members") as List<String>
                    val group = Group(uid, name, picture, members)
                    _group.value = group
                } else {
                    Log.d ("MyPrint", "In ViewModel, document not found")
                    _group.value = Group.empty()
                }
            } catch (e: Exception) {
                Log.d("MyPrint", "In ViewModel, failed to fetch user data with error: ", e)
                _group.value = Group.empty()
            }
        }
    }
    fun createGroup(name: String, photoUri: Uri) {
        db.createGroup(name, photoUri)
    }
    suspend fun getDefaultPicture(): Uri {
        return withContext(Dispatchers.IO)  {
            db.getDefaultPicture()
        }
    }
}