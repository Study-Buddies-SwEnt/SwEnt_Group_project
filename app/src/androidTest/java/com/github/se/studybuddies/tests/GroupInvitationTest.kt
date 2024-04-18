package com.github.se.studybuddies.tests

import com.github.se.studybuddies.utility.FirebaseUtils.createGroupInviteLink
import org.junit.Assert.assertEquals
import org.junit.Test

class GroupInvitationTest {

  @Test
  fun createLink() {
    val result = createGroupInviteLink("validGroupUID")
    assertEquals(("https://example.page.link/group?groupUID=validGroupUID"), result)
  }

  // can't be null ?
  /*@Test(expected = IllegalArgumentException::class)
  fun NullGroupUID() {
      createGroupInviteLink(null)
  }*/

  @Test(expected = IllegalArgumentException::class)
  fun EmptyGroupUID() {
    createGroupInviteLink("")
  }
}
