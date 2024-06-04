package com.github.se.studybuddies.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.Contact
import com.github.se.studybuddies.data.ContactList
import com.github.se.studybuddies.data.RequestList
import com.github.se.studybuddies.data.User
import com.github.se.studybuddies.database.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ContactsViewModel(private val uid: String? = null) : ViewModel() {
  private val db = ServiceLocator.provideDatabase()
  private val _contacts = MutableStateFlow<ContactList>(ContactList(emptyList()))
  val contacts: StateFlow<ContactList> = _contacts

  private val _contact = MutableLiveData(Contact.empty())
  val contact: LiveData<Contact> = _contact

  private val _requests = MutableStateFlow<RequestList>(RequestList(emptyList()))
  val requests: StateFlow<RequestList> = _requests

  private val _friends = MutableStateFlow<List<User>>(emptyList())
  val friends: StateFlow<List<User>> = _friends

  private val _allUsers = MutableStateFlow<List<User>>(emptyList())
  val allUsers: StateFlow<List<User>> = _allUsers

  init {
    if (uid != null) {
      fetchAllContacts(uid)
      fetchAllRequests(uid)
      fetchAllFriends(uid)
    }
  }

  fun createContact(otherUID: String) {
    viewModelScope.launch { db.createContact(otherUID) }
  }

  fun fetchContactData(contactID: String) {
    Log.d("contact", "A fetch contact called with ID $contactID")
    viewModelScope.launch {
      _contact.value = db.getContact(contactID)
      Log.d("contact", "A fetched contact in VMscope ${_contact.value}")
    }
    Log.d("contact", "A fetched contact ${_contact.value}")
  }

  fun getOtherUser(contactID: String, uid: String): String {
    fetchContactData(contactID)
    if (_contact.value == Contact.empty()) {
      return ""
    }
    return if ((contact.value?.members?.get(0) ?: "") == uid) {
      Log.d("contact", "getOtherUser 1")
      contact.value?.members?.get(1) ?: ""
    } else {
      Log.d("contact", "getOtherUser 0")
      contact.value?.members?.get(0) ?: ""
    }
  }

  fun fetchAllUsers() {
    viewModelScope.launch {
      try {
        val allUsers = db.getAllUsers()
        _allUsers.value = allUsers
      } catch (e: Exception) {
        Log.d("MyPrint", "In ViewModel, could not fetch all users with error $e")
      }
    }
  }

  fun fetchAllContacts(uid: String) {
    viewModelScope.launch {
      try {
        val contacts = db.getAllContacts(uid)
        _contacts.value = contacts
      } catch (e: Exception) {
        Log.d("MyPrint", "In ViewModel, could not fetch contacts with error $e")
      }
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

  fun fetchAllRequests(uid: String) {
    viewModelScope.launch {
      try {
        val requests = db.getAllRequests(uid)
        _requests.value = requests
      } catch (e: Exception) {
        Log.d("MyPrint", "In ViewModel, could not fetch contact requests with error $e")
      }
    }
  }

  fun dismissRequest(requestID: String) {
    viewModelScope.launch { db.deleteRequest(requestID) }
  }

  fun acceptRequest(requestID: String) {
    viewModelScope.launch { db.acceptRequest(requestID) }
  }

  fun sendContactRequest(targetID: String) {
    viewModelScope.launch { db.sendContactRequest(targetID) }
  }

  fun updateContactShowOnMap(contactID: String, showOnMap: Boolean) {
    db.updateContactShowOnMap(contactID, showOnMap)
  }

  fun updateContactHasDM(contactID: String, hasDM: Boolean) {
    db.updateContactHasDM(contactID, hasDM)
  }
  fun deleteContact(contactID: String) {
    Log.d("contact", "called delete contact in VM")
    db.deleteContact(contactID)
  }
}
