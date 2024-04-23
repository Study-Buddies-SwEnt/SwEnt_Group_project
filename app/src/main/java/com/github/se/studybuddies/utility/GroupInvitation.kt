package com.github.se.studybuddies.utility

import android.net.Uri
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase

object FirebaseUtils {
  fun createGroupInviteLink(groupUID: String): Uri {
    if (groupUID != "") {
      val dynamicLink =
          Firebase.dynamicLinks.dynamicLink {
            link = Uri.parse("https://studybuddies.page.link/JoinGroup/$groupUID")
            domainUriPrefix = "https://studybuddies.page.link"
          }
      return dynamicLink.uri
    } else return Uri.parse("https://studybuddies.page.link/JoinGroup")
  }
}

// To call this function : val inviteLink = FirebaseUtils.createGroupInviteLink(groupUID)
// Or just : val inviteLink = createGroupInviteLink(groupUID)
