package com.github.se.studybuddies.tests

import com.github.se.studybuddies.utility.FirebaseUtils
import com.github.se.studybuddies.utility.FirebaseUtils.createGroupInviteLink
import org.junit.Assert.assertEquals
import org.junit.Test

class GroupInvitationTest {

  @Test
  fun generateLink() {
    var groupUID = "213"
    var inviteLink = createGroupInviteLink(groupUID)
    assertEquals(("https://studybuddies.page.link/JoinGroup/213"), inviteLink)

    groupUID = "356"
    inviteLink = createGroupInviteLink(groupUID)
    assertEquals(("https://studybuddies.page.link/JoinGroup/356"), inviteLink)
  }

  @Test
  fun generateLinkWrongArgument() {
    val groupUID = ""
    val inviteLink = createGroupInviteLink(groupUID)
    assertEquals(("https://studybuddies.page.link/JoinGroup/"), inviteLink)
  }
}
