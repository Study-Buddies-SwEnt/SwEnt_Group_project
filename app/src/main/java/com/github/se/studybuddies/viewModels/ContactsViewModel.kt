package com.github.se.studybuddies.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.Contact
import com.github.se.studybuddies.data.ContactList
import com.github.se.studybuddies.database.DatabaseConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ContactsViewModel(private val uid: String? = null) : ViewModel() {
  private val db = DatabaseConnection()
  private val _contacts = MutableStateFlow<ContactList>(ContactList(emptyList()))
  val contacts: StateFlow<ContactList> = _contacts

  private val _contact = MutableLiveData(Contact.empty())
  val contact: LiveData<Contact> = _contact

  init {
    if (uid != null) {
      fetchAllContacts(uid)
    }
  }

  fun createContact(otherUID: String) {
    viewModelScope.launch { db.createContact(otherUID) }
  }

  fun fetchContactData(contactID: String) {
    viewModelScope.launch { _contact.value = db.getContact(contactID) }
  }

  fun getOtherUser(contactID: String, uid: String): String {
    fetchContactData(contactID)
    return if ((contact.value?.members?.get(0) ?: "") == uid) {
      contact.value?.members?.get(1) ?: ""
    } else contact.value?.members?.get(0) ?: ""
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
}
