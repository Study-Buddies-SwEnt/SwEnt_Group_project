package com.github.se.studybuddies

import android.net.Uri
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.ChatType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ChatUnitTest {
  private val testChat =
      Chat(
          "test-uid",
          "test",
          Uri.parse("https://images.pexels.com/photos/6031345/pexels-photo-6031345.jpeg"),
          ChatType.GROUP,
          emptyList())

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
