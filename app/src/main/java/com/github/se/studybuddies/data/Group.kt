package com.github.se.studybuddies.data

import android.net.Uri

data class Group(
    val uid: String,
    val name: String,
    val picture: Uri,
    val members: List<String>,
    val topics: List<String>,
    val timerState: TimerState
) {
  companion object {
    fun empty(): Group {
      return Group(
          uid = "",
          name = "",
          picture = Uri.EMPTY,
          members = emptyList(),
          topics = emptyList(),
          timerState = TimerState(0L, false))
    }
  }
}

class GroupList(private val groups: List<Group>) {
  fun getAllTasks(): List<Group> {
    return groups
  }

  fun isGroupInside(groupUID: String): Boolean {
    return groups.any { group -> group.uid == groupUID }
  }

  fun isEmpty(): Boolean {
    return groups.isEmpty()
  }

  fun getFilteredTasks(searchQuery: String): List<Group> {
    val filteredGroups =
        groups.filter { group -> group.name.contains(searchQuery, ignoreCase = true) }
    return filteredGroups
  }
}
