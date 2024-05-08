package com.github.se.studybuddies

import com.github.se.studybuddies.data.TopicFile
import com.github.se.studybuddies.data.TopicFolder
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TopicItemUnitTest {
  @Test
  fun testEmptyTopicItem() {
    // Act
    val topicFile = TopicFile("test", "test", listOf("test"))
    val topicFolder = TopicFolder("test", "test", listOf(topicFile))
    assert(topicFolder.folderName == "test")
  }
}
