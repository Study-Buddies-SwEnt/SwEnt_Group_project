package com.github.se.studybuddies.data

data class Topic(
    val uid: String,
    val name: String,
    val exercises: List<String>,
    val theory: List<String>
) {
  companion object {
    fun empty(): Topic {
      return Topic(uid = "", name = "", exercises = emptyList(), theory = emptyList())
    }
  }
}

class TopicList(private val topics: List<Topic>) {
  fun getAllTopics(): List<Topic> {
    return topics
  }
}
