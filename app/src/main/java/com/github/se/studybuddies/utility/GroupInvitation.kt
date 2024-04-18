package com.github.se.studybuddies.utility

import android.net.Uri
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase

object FirebaseUtils {
    fun createGroupInviteLink(groupUID: String): Uri {
        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link = Uri.parse("https://www.example.com/group?groupUID=$groupUID")
            domainUriPrefix = "https://example.page.link"
            androidParameters("com.github.se.studybuddies") {
                minimumVersion = 123
            }
        }
        val dynamicLinkUri = dynamicLink.uri
        return dynamicLinkUri
    }
}

// To call this function : val inviteLink = FirebaseUtils.createGroupInviteLink(groupUID)