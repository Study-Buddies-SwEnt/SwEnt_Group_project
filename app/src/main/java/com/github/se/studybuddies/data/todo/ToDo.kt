package com.github.se.studybuddies.data.todo

import com.github.se.studybuddies.data.Location
import java.time.LocalDate

data class ToDo(
    val uid: String,
    val name: String,
    val dueDate: LocalDate,
    val description: String,
    val status: ToDoStatus
)

