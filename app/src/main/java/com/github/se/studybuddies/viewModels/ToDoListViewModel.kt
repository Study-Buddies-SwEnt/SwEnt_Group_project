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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.LocalDate



class ToDoListViewModel(studyBuddies: Application) : AndroidViewModel(studyBuddies) {

  private val _todos = MutableStateFlow(ToDoList(emptyList()))
  val todos: StateFlow<ToDoList> = _todos

  init {
    fetchAllTodos()
  }


  private val gson = Gson()
  private val toDoFile = File(studyBuddies.filesDir, "ToDoList.json")


  fun addOrUpdateToDo(todo: ToDo) {
    // Read existing data from file
    val existingData = readToDoListFromFile()

    // Add or update the ToDo item
    existingData[todo.uid] = todo

    // Write updated data back to file
    writeToDoListToFile(existingData)
  }

  fun deleteToDo(todo: ToDo) {
    val existingData = readToDoListFromFile()
    existingData.remove(todo.uid)
    writeToDoListToFile(existingData)
  }


  private fun readToDoListFromFile(): MutableMap<String, ToDo> {
    if (!toDoFile.exists()) {
      return mutableMapOf() // Return an empty map if file doesn't exist yet
    }
    val json = toDoFile.readText()
    // Deserialize JSON string to map of ToDo objects
    val type: Type = object : TypeToken<Map<String, ToDo>>() {}.type
    return gson.fromJson(json, type) ?: mutableMapOf()
  }

  private fun writeToDoListToFile(todoList: Map<String, ToDo>) {
    // Serialize the map of ToDo objects to JSON
    val json = gson.toJson(todoList)
    toDoFile.writeText(json)
  }



  fun fetchAllTodos() {
    viewModelScope.launch {
      try {
        val todos = getAllItems()
        val sortedTodos = todos.getAllTasks().toList().sortedBy {it.dueDate }
        _todos.value = ToDoList(sortedTodos.toMutableList())
      } catch (e: Exception) {
        Log.d("MyPrint", "Could not fetch items $e")
      }
    }
  }


  /*fun updateTodo(
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
   */

  private fun getAllItems(): ToDoList {

    val items = mutableListOf<ToDo>()
    val toDoList = readToDoListFromFile()

    for (item in toDoList) {
      items.add(item.value)
    }

    return ToDoList(items)
  }


  /*fun addNewTodo(

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

  */

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
