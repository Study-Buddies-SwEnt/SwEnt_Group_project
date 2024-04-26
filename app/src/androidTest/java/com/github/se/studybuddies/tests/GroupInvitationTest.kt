package com.github.se.studybuddies.tests

import org.junit.Assert.assertEquals
import org.junit.Test
import com.github.se.studybuddies.utility.GroupInvitation.createGroupInviteLink

class GroupInvitationTest {

  @Test
  fun generateLink() {
    var groupUID = "xtheb45SJUEHD"
    var groupName = "StudyBuddiesIsTheBest"
    var inviteLink = createGroupInviteLink(groupUID, groupName)
    assertEquals(("studybuddiesJoinGroup=StudyBuddiesIsTheBest/xtheb45SJUEHD"), inviteLink)

    groupUID = "sjsueh3ks8"
    groupName = "BestNameEver"
    inviteLink = createGroupInviteLink(groupUID, groupName)
    assertEquals(("studybuddiesJoinGroup=BestNameEver/sjsueh3ks8"), inviteLink)
  }

  @Test
  fun generateLinkEmptyName() {
    val groupUID = "wUHd562G62H"
    val groupName = ""
    val inviteLink = createGroupInviteLink(groupUID, groupName)
    assertEquals(("studybuddiesJoinGroup=NotNamedGroup/wUHd562G62H"), inviteLink)
  }

  @Test
  fun generateLinkWrongArgument() {
    val groupUID = ""
    val groupName = "Test28"
    val inviteLink = createGroupInviteLink(groupUID, groupName)
    assertEquals(("Group_not_founded"), inviteLink)
  }
}
