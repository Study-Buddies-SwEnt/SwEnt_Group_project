package com.github.se.studybuddies.data

data class Topic(
    val uid: String,
    val name: String,
    var exercises: List<TopicItem>,
    var theory: List<TopicItem>
) {
  fun sortItems() {
    exercises = sortItemsList(exercises.toMutableList())
    theory = sortItemsList(theory.toMutableList())
  }

  private fun sortItemsList(items: MutableList<TopicItem>): MutableList<TopicItem> {
    return items
        .sortedWith(compareByDescending<TopicItem> { it is TopicFile }.thenByDescending { it.name })
        .toMutableList()
        .also { sortedItems ->
          sortedItems.filterIsInstance<TopicFolder>().forEach { folder ->
            folder.items = sortItemsList(folder.items.toMutableList())
          }
        }
  }

  companion object {
    fun empty(): Topic {
      return Topic(uid = "", name = "", exercises = mutableListOf(), theory = mutableListOf())
    }
  }
}

class TopicList(private val topics: List<Topic>) {
  fun getAllTopics(): List<Topic> {
    return topics
  }
}
