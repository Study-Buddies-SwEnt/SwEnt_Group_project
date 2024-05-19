package com.github.se.studybuddies.data

import android.net.Uri

data class User(
    val uid: String,
    val email: String,
    val username: String,
    val photoUrl: Uri,
    val location: String,
    val dailyPlanners: List<DailyPlanner> = emptyList() // Default value provided here
) {
  companion object {
    fun empty(): User {
      return User(uid = "", email = "", username = "", photoUrl = Uri.EMPTY, location = "")
    }
  }
}
