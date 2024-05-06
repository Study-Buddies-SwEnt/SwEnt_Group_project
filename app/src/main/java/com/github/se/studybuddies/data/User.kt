package com.github.se.studybuddies.data

import android.net.Uri
import com.google.android.gms.maps.model.LatLng

data class User(val uid: String, val email: String, val username: String, val photoUrl: Uri, val location : String){
  companion object {
    fun empty(): User {
      return User(uid = "", email = "", username = "", photoUrl = Uri.EMPTY, location = "")
    }
  }
}
