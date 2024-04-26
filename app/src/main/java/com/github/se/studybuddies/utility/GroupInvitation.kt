package com.github.se.studybuddies.utility

import android.util.Log
import com.github.se.studybuddies.R
import com.github.se.studybuddies.database.DatabaseConnection

private val db = DatabaseConnection()

// Function to create a group invitation link
suspend fun createGroupInviteLink(groupUID: String, groupName: String = ""): String {
  return if (groupUID == "") {
    Log.d("Link", "The Group id is empty")
      "Group not found"
  } else {
    var newGroupName = groupName
    if (groupName == "") {
      newGroupName = db.getGroupName(groupUID)
      if (newGroupName == "") {
        Log.d("Link", "The Group has no name")
      }
    }
    val link = "studybuddiesJoinGroup=$newGroupName/$groupUID"
    Log.d("Link", "Successfully created the link")
    link
  }
}

  // function not used anymore, but can be useful for future development if wanting to add a Dynamic
  // Link
  /*fun checkIncomingDynamicLink(
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
  }*/
