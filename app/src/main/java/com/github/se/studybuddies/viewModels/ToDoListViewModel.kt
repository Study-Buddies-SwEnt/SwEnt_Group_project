package com.github.se.studybuddies.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.todo.ToDo
import com.github.se.studybuddies.data.todo.ToDoList
import com.github.se.studybuddies.data.todo.ToDoStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import java.time.format.DateTimeFormatter


class ToDoListViewModel(studyBuddies: Application) : AndroidViewModel(studyBuddies) {

  private val _todos = MutableStateFlow(ToDoList(emptyList()))
  val todos: StateFlow<ToDoList> = _todos

  private val _todo = MutableStateFlow(emptyToDo())
  val todo: StateFlow<ToDo> = _todo


  private val json = Json { prettyPrint = true }
  //private val gson = Gson()


  private val toDoFile = File(studyBuddies.filesDir, "ToDoList.json")

  // val encryption = Encryption(studyBuddies)

  init {
    fetchAllTodos()
  }

  /*
  private fun readToDoListFromFile(): MutableMap<String, ToDo> {
    if (!toDoFile.exists()) {
      return mutableMapOf() // Return an empty map if file doesn't exist yet
    }

    val json = toDoFile.readText()
    // Check if the json is a string representation of an object
    if (json.startsWith("{") && json.endsWith("}")) {
      // Deserialize JSON string to map of ToDo objects
      val type: Type = object : TypeToken<Map<String, ToDo>>() {}.type
      Log.d("time", "readToDoListFromFile $json")

      return gson.fromJson(json, type) ?: mutableMapOf()
    } else {
      // Handle case where json is not an object but a string, perhaps log the issue
      Log.e("JsonError", "JSON data is not a valid object: $json")
      return mutableMapOf()
    }
  }
*/
  private fun readToDoListFromFile(): MutableMap<String, ToDo> {
    if (!toDoFile.exists()) {
      return mutableMapOf() // Return an empty map if the file doesn't exist yet
    }

    val jsonString = toDoFile.readText()

    return if (jsonString.startsWith("{") && jsonString.endsWith("}")) {
      try {
        json.decodeFromString<Map<String, ToDo>>(jsonString).toMutableMap()
      } catch (e: Exception) {
        Log.e("JsonError", "Error deserializing JSON data: $jsonString", e)
        mutableMapOf()
      }
    } else {
      Log.e("JsonError", "JSON data is not a valid object: $jsonString")
      mutableMapOf()
    }
  }

/*
  private fun writeToDoListToFile(todoList: Map<String, ToDo>) {
    // Serialize the map of ToDo objects to JSON
    val json = gson.toJson(todoList)
    toDoFile.writeText(json)

    Log.d("time", "writeToDoListToFile $json")
    // encryption.encryptAndSaveFile(toDoFile)
  }
 */

  private fun writeToDoListToFile(todoList: Map<String, ToDo>) {
    val jsonString = json.encodeToString(todoList)
    toDoFile.writeText(jsonString)
    Log.d("time", "writeToDoListToFile $jsonString")
  }

  fun addToDo(todo: ToDo) {
    // Read existing data from file
    val existingData = readToDoListFromFile()

    // Add or update the ToDo item
    existingData[todo.uid] = todo

    Log.d("time", "addToDo ${existingData[todo.uid]?.dueDate}")

    // Write updated data back to file
    writeToDoListToFile(existingData)
  }

  fun updateToDo(uid: String, todo: ToDo) {
    // Read existing data from file
    val existingData = readToDoListFromFile()

    // Add or update the ToDo item
    existingData[uid] = todo

    Log.d("time", "updateTodo ${existingData[uid]?.dueDate}")

    // Write updated data back to file
    writeToDoListToFile(existingData)
  }

  fun deleteToDo(uid: String) {
    val existingData = readToDoListFromFile()
    existingData.remove(uid)
    writeToDoListToFile(existingData)
  }

  fun fetchAllTodos() {
    viewModelScope.launch {
      try {
        val todos = getAllItems()
        val sortedTodos = todos.getAllTasks().toList().sortedBy { it.dueDate }
        _todos.value = ToDoList(sortedTodos.toMutableList())
      } catch (e: Exception) {
        Log.d("MyPrint", "Could not fetch items $e")
      }
    }
  }

  fun fetchTodoByUID(uid: String) {

    val existingData = readToDoListFromFile()
    val fetchedToDo = existingData[uid]
    val todoItem =
        fetchedToDo?.let {
          ToDo(uid, it.name, fetchedToDo.dueDate, fetchedToDo.description, fetchedToDo.status)
        }
    if (todoItem != null) {
      _todo.value = todoItem
    }
  }

  private fun getAllItems(): ToDoList {

    val items = mutableListOf<ToDo>()
    val toDoList = readToDoListFromFile()

    for (item in toDoList) {
      items.add(item.value)
      Log.d("time", "getAllItems ${item.value.dueDate}")
    }

    return ToDoList(items)
  }

  private fun emptyToDo(): ToDo {
    return ToDo("", "", LocalDate.now(), "", ToDoStatus.CREATED)
  }


  object LocalDateSerializer : KSerializer<LocalDate> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    override val descriptor: SerialDescriptor =
      PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
      val string = value.format(formatter)
      encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): LocalDate {
      val string = decoder.decodeString()
      return LocalDate.parse(string, formatter)
    }
  }


}
