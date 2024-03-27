package com.github.se.studybuddies.data

import android.net.Uri

data class Group(val uid: String, val name: String, val picture: Uri, val members: List<String>) {
  companion object {
    fun empty(): Group {
      return Group(uid = "", name = "", picture = Uri.EMPTY, members = emptyList())
    }
  }
}

class GroupList(private val groups: List<Group>) {
  fun getAllTasks(): List<Group> {
    return groups
  }

  fun getFilteredTasks(searchQuery: String): List<Group> {
    val filteredGroups =
        groups.filter { group -> group.name.contains(searchQuery, ignoreCase = true) }
    return filteredGroups
  }
}
