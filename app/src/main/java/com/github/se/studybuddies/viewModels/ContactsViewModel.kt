package com.github.se.studybuddies.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.ContactList
import com.github.se.studybuddies.database.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ContactsViewModel(private val uid: String? = null) : ViewModel() {
  private val db = ServiceLocator.provideDatabase()
  private val _contacts = MutableStateFlow<ContactList>(ContactList(emptyList()))
  val contacts: StateFlow<ContactList> = _contacts

  init {
    if (uid != null) {
      fetchAllContacts(uid)
    }
  }

  fun createContact(otherUID: String) {
    viewModelScope.launch { db.createContact(otherUID) }
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
