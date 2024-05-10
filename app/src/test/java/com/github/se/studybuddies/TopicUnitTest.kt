package com.github.se.studybuddies

import com.github.se.studybuddies.data.Topic
import com.github.se.studybuddies.data.TopicList
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TopicUnitTest {
  @Test
  fun testEmptyTopic() {
    // Act
    val emptyTopic = Topic.empty()
    assert(emptyTopic.uid.isEmpty())
    assert(emptyTopic.name.isEmpty())
    assert(emptyTopic.exercises.isEmpty())
    assert(emptyTopic.theory.isEmpty())
  }

  @Test
  fun testEmptyTopicList() {
    // Arrange
    val topicList = TopicList(emptyList())

    // Act
    val allTopics = topicList.getAllTopics()

    // Assert
    assert(allTopics.isEmpty())
  }
}
