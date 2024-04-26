package com.github.se.studybuddies.data.todo

data class ToDoList(private val todos: List<ToDo>) {

  fun getAllTasks(): List<ToDo> {
    return todos
  }

  fun getFilteredTasks(searchQuery: String): List<ToDo> {
    val filteredToDos =
        todos.filter { toDo ->
          toDo.name.contains(searchQuery, ignoreCase = true) ||
              toDo.description.contains(searchQuery, ignoreCase = true)
        }
    return filteredToDos
  }
}
