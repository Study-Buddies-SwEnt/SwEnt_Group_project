package com.github.se.studybuddies

import android.net.Uri
import com.github.se.studybuddies.data.User
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UserUnitTest {
  @Test
  fun testEmptyUser() {
    // Act
    val emptyUser = User.empty()
    assert(emptyUser.uid.isEmpty())
    assert(emptyUser.email.isEmpty())
    assert(emptyUser.username.isEmpty())
    assert(emptyUser.photoUrl == Uri.EMPTY)
  }
}
