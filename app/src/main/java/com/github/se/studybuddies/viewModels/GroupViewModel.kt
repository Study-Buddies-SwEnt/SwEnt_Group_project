package com.github.se.studybuddies.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.se.studybuddies.data.Group

class GroupViewModel(private val uid: String? = null) {
    private val db = DatabaseConnection()
    private val _group = MutableLiveData<Group>(Group.empty())
    val group: LiveData<Group> = _group
}