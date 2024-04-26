package com.github.se.studybuddies.viewModels

import androidx.lifecycle.ViewModel
import com.github.se.studybuddies.data.todo.ToDo
import com.github.se.studybuddies.data.todo.ToDoStatus
import com.github.se.studybuddies.database.DatabaseConnection
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.DocumentSnapshot
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


/*
class ToDoViewModel(private val uid: String? = null) : ViewModel() {

  private val _todo = MutableStateFlow(emptyToDo())
  val todo: StateFlow<ToDo> = _todo

  init {
    if (uid != null) {
      fetchTodoByUID(uid)
    }
  }


    /*
  fun addNewTodo(todo: ToDo) {
    val convertedDate = Date.from(todo.dueDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
    firebaseConnection.addNewTodo(
        todo.name,
        convertedDate,
        todo.description,
        todo.status.name)
  }

  fun updateTodo(uid: String, todo: ToDo) {
    val convertedDate = Date.from(todo.dueDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
    firebaseConnection.updateTodo(
        uid,
        todo.name,
        convertedDate,
        todo.description,
        todo.status.name)
  }

  fun fetchTodoByUID(uid: String) {
    firebaseConnection
        .fetchTaskByUID(uid)
        .addOnCompleteListener(
            OnCompleteListener<DocumentSnapshot> { task ->
              if (task.isSuccessful) {
                val document = task.result
                if (document != null && document.exists()) {
                  val name = document.getString("title") ?: ""
                  val assigneeName = document.getString("assigneeName") ?: ""
                  val dueDate = document.getDate("dueDate")
                  val convertedDate =
                      dueDate!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                  val description = document.getString("description") ?: ""
                  val status = ToDoStatus.valueOf(document.getString("status") ?: "")

                  val todoItem =
                      ToDo(uid, name, convertedDate, description, status)
                  _todo.value = todoItem
                } else {
                  _todo.value = emptyToDo()
                }
              } else {
                _todo.value = emptyToDo()
              }
            })
  }

  fun deleteTodo(uid: String) {
    firebaseConnection.deleteTodo(uid)
  }

  private fun emptyToDo(): ToDo {
    return ToDo("", "", LocalDate.now(), "", ToDoStatus.CREATED)
  }

     */
}

 */
