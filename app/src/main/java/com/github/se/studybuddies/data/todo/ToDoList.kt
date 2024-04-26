package com.github.se.studybuddies.data.todo

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.tasks.await
import java.time.ZoneId
import java.util.Date

data class ToDoList(private val todos: MutableList<ToDo>) {



  fun getAllTasks(): MutableList<ToDo> {
    return todos
  }


  fun getFilteredTasks(searchQuery: String): MutableList<ToDo> {
    val filteredToDos =
        todos.filter { toDo ->
          toDo.name.contains(searchQuery, ignoreCase = true) ||
              toDo.description.contains(searchQuery, ignoreCase = true)
        }
    return filteredToDos.toMutableList()
  }


}
