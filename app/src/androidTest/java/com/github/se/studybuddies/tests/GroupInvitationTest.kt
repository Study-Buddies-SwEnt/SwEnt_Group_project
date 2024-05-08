package com.github.se.studybuddies.tests

import com.github.se.studybuddies.viewModels.GroupViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GroupInvitationTest {

  @Test
  fun generateLinkTest() = runBlocking {
    var groupUID = "xtheb45SJUEHD"
    val groupName = "StudyBuddiesIsTheBest"
    val groupVM = GroupViewModel(groupUID)
    var inviteLink = groupVM.createGroupInviteLink(groupUID, groupName)
    assertEquals(("studybuddiesJoinGroup=StudyBuddiesIsTheBest/xtheb45SJUEHD"), inviteLink)

    groupUID = "sjsueh3ks8"
    inviteLink = groupVM.createGroupInviteLink(groupUID, "")
    assertEquals(("Current group not found"), inviteLink)
  }

  @Test
  fun generateLinkEmptyNameTest() = runBlocking {
    val groupUID = "wUHd562G62H"
    val groupName = ""
    val groupVM = GroupViewModel(groupUID)
    val inviteLink = groupVM.createGroupInviteLink(groupUID, groupName)
    assertEquals(("Current group not found"), inviteLink)
  }

  @Test
  fun generateLinkWrongArgumentTest() = runBlocking {
    var groupUID = "xtheb45SJUEHD"
    val groupName = "Test28"
    val groupVM = GroupViewModel(groupUID)
    val inviteLink = groupVM.createGroupInviteLink("", groupName)
    assertEquals(("Current group not found"), inviteLink)
  }
}
