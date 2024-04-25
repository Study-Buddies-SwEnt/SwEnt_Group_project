package com.github.se.studybuddies.tests

import com.github.se.studybuddies.utility.FirebaseUtils.createGroupInviteLink
import org.junit.Assert.assertEquals
import org.junit.Test

class GroupInvitationTest {

  @Test
  fun generateLink() {
    var groupUID = "xtheb45"
    var inviteLink = createGroupInviteLink(groupUID)
    assertEquals(("https://studybuddies.page.link/JoinGroup/xtheb45"), inviteLink)

    groupUID = "sjsueh3ks8"
    inviteLink = createGroupInviteLink(groupUID)
    assertEquals(("https://studybuddies.page.link/JoinGroup/sjsueh3ks8"), inviteLink)
  }

  @Test
  fun generateLinkWrongArgument() {
    val groupUID = ""
    val inviteLink = createGroupInviteLink(groupUID)
    assertEquals(("https://studybuddies.page.link/JoinGroup/"), inviteLink)
  }

  // add test for the main activity, checking if user is indeed in the good group
  // call this function
  // checkIncomingDynamicLink(intent, this, navigationActions)
}
