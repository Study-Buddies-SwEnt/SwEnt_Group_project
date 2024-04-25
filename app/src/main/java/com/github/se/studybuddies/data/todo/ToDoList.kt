package com.github.se.studybuddies.data.todo

class ToDoList(private val todos: List<ToDo>) {
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

  fun getCurrentTask(): ToDo? {
    // You can implement logic to get the current task here if needed
    return null
  }
}
