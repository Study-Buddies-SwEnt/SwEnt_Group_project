package com.github.se.studybuddies.data

data class Contact(val id: String, val members: List<String>, val showOnMap: Boolean) {
  companion object {
    fun empty(): Contact {
      return Contact(id = "", members = emptyList(), false)
    }
  }
}

class RequestList(private val requests : List<User>) {
  fun getAllTasks(): List<User> {
    return requests
  }

  fun getFilteredContacts(searchQuery: String): List<User> {
    val filteredRequests =
      requests.filter { request ->
        request.uid.contains(searchQuery, ignoreCase = true) or
                request.username.contains(searchQuery, ignoreCase = true)
      }
    return filteredRequests
  }
}


class ContactList(private val contacts: List<Contact>) {
  fun getAllTasks(): List<Contact> {
    return contacts
  }

  fun getFilteredContacts(searchQuery: String): List<Contact> {
    val filteredContacts =
        contacts.filter { contact ->
          contact.members.get(0).contains(searchQuery, ignoreCase = true) or
              contact.members.get(1).contains(searchQuery, ignoreCase = true)
        }
    return filteredContacts
  }
}
