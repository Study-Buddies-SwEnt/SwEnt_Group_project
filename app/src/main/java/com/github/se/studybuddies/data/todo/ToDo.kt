package com.github.se.studybuddies.data.todo

import com.github.se.studybuddies.data.Location
import java.time.LocalDate

data class ToDo(
    val uid: String,
    val name: String,
    val assigneeName: String,
    val dueDate: LocalDate,
    val location: Location,
    val description: String,
    val status: ToDoStatus
) {
  /* fun toMap(): Map<String, Any> {
      val map = mutableMapOf<String, Any>()
      map["uid"] = uid
      map["name"] = name
      map["description"] = description
      map["assigneeName"] = assigneeName
      map["location"] = location
      map["dueDate"] = dueDate
      return map
  }
  */
}
