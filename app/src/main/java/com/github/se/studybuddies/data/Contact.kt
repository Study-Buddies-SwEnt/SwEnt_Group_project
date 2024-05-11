package com.github.se.studybuddies.data

import android.net.Uri

data class Contact(
    val uid: String,
    val members: Pair<String, String>,
) {
    companion object {
        fun empty(): Contact {
            return Contact(
                uid = "",
                members = Pair("","")
            )
        }
    }
}

class ContactList(private val contacts: List<Contact>) {
    fun getAllTasks(): List<Contact> {
        return contacts
    }

    fun getFilteredTasks(searchQuery: String): List<Contact> {
        val filteredContacts =
            contacts.filter { contact -> contact.members.first.contains(searchQuery, ignoreCase = true) or
                    contact.members.second.contains(searchQuery, ignoreCase = true) }
        return filteredContacts
    }
}
