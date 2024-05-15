package com.github.se.studybuddies.data

data class Contact(val id: String, val members: Pair<String, String>, val showOnMap: Boolean) {
  companion object {
    fun empty(): Contact {
      return Contact(id = "", members = Pair("", ""), false)
    }
  }
}

class ContactList(private val contacts: List<Contact>) {
  fun getAllTasks(): List<Contact> {
    return contacts
  }

  fun getFilteredTasks(searchQuery: String): List<Contact> {
    val filteredContacts =
        contacts.filter { contact ->
          contact.members.first.contains(searchQuery, ignoreCase = true) or
              contact.members.second.contains(searchQuery, ignoreCase = true)
        }
    return filteredContacts
  }
}
