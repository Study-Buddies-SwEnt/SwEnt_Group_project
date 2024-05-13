package com.github.se.studybuddies.viewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.Contact
import com.github.se.studybuddies.database.DatabaseConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ContactsViewModel(private val uid: String? = null) : ViewModel() {
  private val db = DatabaseConnection()
  private val _contact = MutableStateFlow<List<Contact>>(emptyList())
  val contacts: StateFlow<List<Contact>> = _contact

  init {
    if (uid != null) {
      fetchAllContacts(uid)
    }
  }


  fun createContact(name: String) {
    viewModelScope.launch { db.createContact(name) }
  }


  fun fetchAllContacts(uid: String) {
    viewModelScope.launch {
      try {
        val contacts = db.getAllContacts(uid)
        _contact.value = contacts
      } catch (e: Exception) {
        Log.d("MyPrint", "In ViewModel, could not fetch contacts with error $e")
      }
    }
  }
}
