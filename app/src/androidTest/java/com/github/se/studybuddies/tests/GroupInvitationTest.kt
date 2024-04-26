package com.github.se.studybuddies.tests

import com.github.se.studybuddies.utility.createGroupInviteLink
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GroupInvitationTest {

  @Test
  fun generateLinkTest() = runBlocking {
    var groupUID = "xtheb45SJUEHD"
    val groupName = "StudyBuddiesIsTheBest"
    var inviteLink = createGroupInviteLink(groupUID, groupName)
    assertEquals(("studybuddiesJoinGroup=StudyBuddiesIsTheBest/xtheb45SJUEHD"), inviteLink)

    groupUID = "sjsueh3ks8"
    inviteLink = createGroupInviteLink(groupUID)
    assertEquals(("studybuddiesJoinGroup=/sjsueh3ks8"), inviteLink)
  }

  @Test
  fun generateLinkEmptyNameTest() = runBlocking {
    val groupUID = "wUHd562G62H"
    val groupName = ""
    val inviteLink = createGroupInviteLink(groupUID, groupName)
    assertEquals(("studybuddiesJoinGroup=/wUHd562G62H"), inviteLink)
  }

  @Test
  fun generateLinkWrongArgumentTest() = runBlocking {
    val groupUID = ""
    val groupName = "Test28"
    val inviteLink = createGroupInviteLink(groupUID, groupName)
    assertEquals(("Group not found"), inviteLink)
  }
}
