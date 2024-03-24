package com.github.se.studybuddies.data

data class User (
    val uid: String,
    val email: String,
    val username: String,
    val photoUrl: String
) {
    companion object {
        fun empty(): User {
            return User(
                uid = "",
                email = "",
                username = "",
                photoUrl = ""
            )
        }
    }
}