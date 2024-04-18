package com.github.se.studybuddies.utility

import android.net.Uri
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
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

    // Alternative implementation
    fun createGroupInviteLink2(groupUID: String): Uri {
        val dynamicLink: DynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("..."))
            .setDynamicLinkDomain("...")
            .setAndroidParameters(Builder().build())
            .buildDynamicLink()
        val dynamicLinkUri: Uri = dynamicLink.getUri()
        return dynamicLinkUri
    }
}

// to call this function : val inviteLink = FirebaseUtils.createGroupInviteLink(groupUID)