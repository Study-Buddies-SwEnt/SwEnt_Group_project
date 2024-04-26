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
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase

object FirebaseUtils {

  private val db = DatabaseConnection()

  fun createGroupInviteLink(groupUID: String): Uri {
      val dynamicLink = Firebase.dynamicLinks.dynamicLink {
          link = Uri.parse("https://studybuddies.page.link/?JoinGroup/$groupUID") // good link ?
          domainUriPrefix = "https://studybuddies.page.link"
      }
    Log.d("Link", dynamicLink.uri.toString())
      Log.d("Link", dynamicLink.toString())
    return dynamicLink.uri
  }

  fun checkIncomingDynamicLink(
      intent: Intent,
      activity: Activity,
      navigationActions: NavigationActions
  ) {
    FirebaseDynamicLinks.getInstance()
        .getDynamicLink(intent)
        .addOnSuccessListener(activity) { pendingDynamicLinkData: PendingDynamicLinkData? ->
            // Get deep link from result (may be null if no link is found)
            if (pendingDynamicLinkData != null) {
                val deepLink = pendingDynamicLinkData.link

            Log.d("Link", "Dynamic Link Detected")
                //Todo remove
                Log.d("Link", pendingDynamicLinkData.toString())
                Log.d("Link", deepLink.toString())
          // Handle the deep link.
          val groupUID = deepLink.toString().substringAfterLast("/")
            Log.d("Link", "Group to join : $groupUID")
          if (groupUID != "") {
            val currentUserUid =
                FirebaseAuth.getInstance().currentUser?.uid // Get the current user's UID

            if (currentUserUid != null) {
              // Add the current user to the group in your Firebase database
              db.updateGroup(groupUID)
                Log.d("Link", "Go add user to group")
              // Go to the newly joined group
              navigationActions.navigateTo("${Route.GROUP}/$groupUID")
            } else {
              // If the user is not logged go to login page (link will have to be clicked again)
                Log.d("Link", "The user is not logged in")
              navigationActions.navigateTo(Route.LOGIN)
            }
          }
            }
        }
        .addOnFailureListener(activity) { e -> Log.w("Link", "getDynamicLink:onFailure", e) }
  }
}
