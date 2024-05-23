package com.github.se.studybuddies.data.todo

import com.github.se.studybuddies.viewModels.ToDoListViewModel
import java.time.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class ToDo(
    val uid: String,
    val name: String,
    @Serializable(with = ToDoListViewModel.LocalDateSerializer::class) val dueDate: LocalDate,
    val description: String,
    var status: ToDoStatus
)

fun nextStatus(toDo: ToDo) {
  when (toDo.status) {
    ToDoStatus.CREATED -> toDo.status = ToDoStatus.STARTED
    ToDoStatus.STARTED -> toDo.status = ToDoStatus.DONE
    ToDoStatus.DONE -> toDo.status = ToDoStatus.CREATED
  }
}
