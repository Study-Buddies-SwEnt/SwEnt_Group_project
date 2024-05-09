package com.github.se.studybuddies

import android.net.Uri
import com.github.se.studybuddies.data.Message
import com.github.se.studybuddies.data.User
import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MessageUnitTest {
  private val testUser =
      User(
          "test",
          "test@email.ch",
          "test",
          Uri.parse("https://images.pexels.com/photos/6031345/pexels-photo-6031345.jpeg"),
          "offline")

  @Test
  fun testGetTime() {
    // Arrange
    val calendar =
        Calendar.getInstance().apply {
          set(2022, Calendar.JANUARY, 1, 15, 30) // 1st Jan 2022, 15:30
        }
    val timestamp = calendar.timeInMillis
    val message = Message("test-uid", "Hello, World!", testUser, timestamp)

    // Act
    val time = message.getTime()

    // Assert
    assertEquals("15:30", time)
  }

  @Test
  fun testGetDate() {
    // Arrange
    val calendar =
        Calendar.getInstance().apply {
          set(2022, Calendar.JANUARY, 1) // 1st Jan 2022
        }
    val timestamp = calendar.timeInMillis

    val message = Message("test-uid", "Hello, World!", testUser, timestamp)

    // Act
    val date = message.getDate()

    // Assert
    assertEquals("01 January", date)
  }

  @Test
  fun testEmptyMessage() {
    // Act
    val emptyMessage = Message.empty()

    // Assert
    assertEquals("", emptyMessage.text)
    assertEquals(0, emptyMessage.timestamp)
    assertEquals(User.empty(), emptyMessage.sender)
  }
}
