package com.github.se.studybuddies.utility

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.github.se.studybuddies.database.DatabaseConnection
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase

object FirebaseUtils {

  private val db = DatabaseConnection()

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

  fun checkIncomingDynamicLink(
      intent: Intent,
      activity: Activity,
      navigationActions: NavigationActions
  ) {
    FirebaseDynamicLinks.getInstance()
        .getDynamicLink(intent)
        .addOnSuccessListener(activity) { pendingDynamicLinkData ->
          var deepLink: Uri? = null
          if (pendingDynamicLinkData != null) {
            deepLink = pendingDynamicLinkData.link
          }
          // Handle the deep link.
          val groupUID = deepLink?.lastPathSegment
          if (groupUID != null) {
            val currentUserUid =
                FirebaseAuth.getInstance().currentUser?.uid // Get the current user's UID

            if (currentUserUid != null) {
              // Add the current user to the group in your Firebase database
              db.updateGroup(groupUID, currentUserUid)

              // Go to the newly joined group
              navigationActions.navigateTo("${Route.GROUP}/$groupUID")
            } else {
              // If the user is not logged go to login page (link will have to be clicked again)
              navigationActions.navigateTo(Route.LOGIN)
            }
          }
        }
        .addOnFailureListener(activity) { e -> Log.w("MyPrint", "getDynamicLink:onFailure", e) }
  }
}
