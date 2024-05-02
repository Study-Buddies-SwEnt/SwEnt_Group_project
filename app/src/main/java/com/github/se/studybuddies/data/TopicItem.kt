package com.github.se.studybuddies.data

sealed class TopicItem(open val uid: String, val name: String)

data class TopicFile(
    override val uid: String,
    val fileName: String,
    val strongUsers: List<String>
) : TopicItem(uid, fileName)

data class TopicFolder(
    override val uid: String,
    val folderName: String,
    val items: List<TopicItem>
) : TopicItem(uid, folderName)

enum class ItemType {
  FILE,
  FOLDER
}

enum class ItemArea {
  EXERCISES,
  THEORY
}
