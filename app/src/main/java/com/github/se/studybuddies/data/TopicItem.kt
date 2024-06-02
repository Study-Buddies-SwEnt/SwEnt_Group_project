package com.github.se.studybuddies.data

sealed class TopicItem(open val uid: String, val name: String, open val parentUID: String)

data class TopicFile(
    override val uid: String,
    val fileName: String,
    var strongUsers: List<String>,
    override val parentUID: String
) : TopicItem(uid, fileName, parentUID) {
    companion object {
        fun empty(): TopicFile {
            return TopicFile("", "", emptyList(), "")
        }
    }
}

data class TopicFolder(
    override val uid: String,
    val folderName: String,
    var items: List<TopicItem>,
    override val parentUID: String
) : TopicItem(uid, folderName, parentUID)

enum class ItemType {
  FILE,
  FOLDER
}

enum class ItemArea {
  EXERCISES,
  THEORY
}

enum class FileArea {
    RESOURCES,
    STRONG_USERS
}