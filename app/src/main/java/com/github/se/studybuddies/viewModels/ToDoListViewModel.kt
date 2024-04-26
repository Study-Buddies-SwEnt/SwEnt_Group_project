package com.github.se.studybuddies.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.todo.ToDo
import com.github.se.studybuddies.data.todo.ToDoList
import com.github.se.studybuddies.data.todo.ToDoStatus
import com.github.se.studybuddies.database.DatabaseConnection
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.time.ZoneId
import java.util.Date
import android.app.Application
import androidx.lifecycle.AndroidViewModel

class ToDoListViewModel(studyBuddies: Application) : AndroidViewModel(studyBuddies) {

  //private val firebaseConnection = DatabaseConnection()


  private val _todos = MutableStateFlow(ToDoList(emptyList()))
  val todos: StateFlow<ToDoList> = _todos

  init {
    val file = File(studyBuddies.filesDir, "myData.txt")
    fetchAllTodos()
  }

  fun fetchAllTodos() {
    viewModelScope.launch {
      try {
        val todos = firebaseConnection.getAllItems()
        val sortedTodos = todos.getAllTasks().sortedBy { it.dueDate }
        _todos.value = ToDoList(sortedTodos.toMutableList())
      } catch (e: Exception) {
        Log.d("MyPrint", "Could not fetch items $e")
      }
    }
  }


  fun updateTodo(
    todoId: String,
    name: String,
    dueDate: Date,
    description: String,
    status: String
  ) {
    val task =
      hashMapOf(
        "title" to name,
        "dueDate" to dueDate,
        "description" to description,
        "status" to status)
    todoCollection
      .document(todoId)
      .update(task as Map<String, Any>)
      .addOnSuccessListener { Log.d("MyPrint", "Task $todoId succesfully updated") }
      .addOnFailureListener { Log.d("MyPrint", "Task $todoId failed to update") }
  }

  suspend fun getAllItems(): ToDoList {
    val querySnapshot = todoCollection.get().await()
    val items = mutableListOf<ToDo>()

    for (document in querySnapshot.documents) {
      val uid = document.id
      val name = document.getString("title") ?: ""
      val assigneeName = document.getString("assigneeName") ?: ""
      val dueDate = document.getDate("dueDate")
      val convertedDate = dueDate!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
      val description = document.getString("description") ?: ""
      val status = ToDoStatus.valueOf(document.getString("status") ?: "")

      val item = ToDo(uid, name, convertedDate, description, status)
      items.add(item)
    }

    return ToDoList(items)
  }

  fun addNewTodo(
    name: String,
    dueDate: Date,
    description: String,
    status: String
  ) {
    Log.d("addNewTodo", "Successfully navigated to addNewTodo")
    val task = ToDo()
    hashMapOf(
      "title" to name,
      "dueDate" to dueDate,
      "description" to description,
      "status" to status)

    todos
      .add(task)
      .addOnSuccessListener { Log.d("MyPrint", "Task successfully added") }
      .addOnFailureListener { Log.d("MyPrint", "Failed to add task") }
  }

  fun fetchTaskByUID(uid: String): Task<DocumentSnapshot> {
    return todoCollection.document(uid).get()
  }

  fun deleteTodo(todoId: String) {
    todoCollection
      .document(todoId)
      .delete()
      .addOnSuccessListener { Log.d("MyPrint", "Successfully deleted task") }
      .addOnFailureListener { Log.d("MyPrint", "Failed to delete task") }
  }






  /*
  fun updateToDoList(toDos: List<ToDo>) {
      _uiState.value = ToDoList(toDos)
  }

  fun filterToDoList(searchQuery: String) {
      _uiState.update { currentState ->
          val filteredTasks = currentState.todos.filter { todo ->
              todo.name.contains(searchQuery, ignoreCase = true) ||
                      todo.description.contains(searchQuery, ignoreCase = true)
          }
          ToDoList(filteredTasks)
      }
  }

  private fun updateToDoListState() {
      _toDoListState.value = _toDoListState.value.copy(getAllTask = _toDoList)
  }

   */
}
