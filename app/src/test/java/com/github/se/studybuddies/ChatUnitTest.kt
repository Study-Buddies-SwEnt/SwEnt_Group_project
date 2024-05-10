package com.github.se.studybuddies

import android.net.Uri
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.ChatType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ChatUnitTest {

  @Test
  fun testEmptyChat() {
    // Act
    val emptyChat = Chat.empty()
    assert(emptyChat.uid.isEmpty())
    assert(emptyChat.name.isEmpty())
    assert(emptyChat.picture == Uri.EMPTY)
    assert(emptyChat.type == ChatType.GROUP)
    assert(emptyChat.members.isEmpty())
  }
}
