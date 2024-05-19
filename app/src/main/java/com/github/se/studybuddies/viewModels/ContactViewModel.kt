package com.github.se.studybuddies.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.Contact
import com.github.se.studybuddies.database.DatabaseConnection
import kotlinx.coroutines.launch

class ContactViewModel(private val contactID: String? = null) : ViewModel() {
  private val db = DatabaseConnection()
  private val _contact = MutableLiveData(Contact.empty())
  val contact: LiveData<Contact> = _contact

  init {
    if (contactID != null) {
      fetchContactData(contactID)
    }
  }

  fun getOtherUser(uid: String): String {
    if ((contact.value?.members?.first ?: "") == uid) {
      return contact.value?.members?.second ?: ""
    } else return contact.value?.members?.first ?: ""
  }

  fun fetchContactData(contactID: String) {
    viewModelScope.launch { _contact.value = db.getContact(contactID) }
  }
}
