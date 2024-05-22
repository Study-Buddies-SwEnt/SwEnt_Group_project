package com.github.se.studybuddies.data.todo

import com.github.se.studybuddies.viewModels.ToDoListViewModel
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class ToDo(
    val uid: String,
    val name: String,
    @Serializable(with = ToDoListViewModel.LocalDateSerializer::class) val dueDate: LocalDate,
    val description: String,
    val status: ToDoStatus
)
