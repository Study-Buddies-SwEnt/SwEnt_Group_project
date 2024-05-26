package com.github.se.studybuddies.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.todo.ToDo
import com.github.se.studybuddies.data.todo.ToDoList
import com.github.se.studybuddies.data.todo.ToDoStatus
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

class ToDoListViewModel(private val context: Context) : ViewModel() {

  private val _todos = MutableStateFlow(ToDoList(emptyList()))
  val todos: StateFlow<ToDoList> = _todos

  private val _todo = MutableStateFlow(emptyToDo())
  val todo: StateFlow<ToDo> = _todo

  private val json = Json { prettyPrint = true }
  // private val gson = Gson()

  private val toDoFile = File(context.filesDir, "ToDoList.json")

  init {
    fetchAllTodos()
  }

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

  private fun writeToDoListToFile(todoList: Map<String, ToDo>) {
    val jsonString = json.encodeToString(todoList)
    toDoFile.writeText(jsonString)
  }

  fun addToDo(todo: ToDo) {
    // Read existing data from file
    val existingData = readToDoListFromFile()

    // Add or update the ToDo item
    existingData[todo.uid] = todo

    // Write updated data back to file
    writeToDoListToFile(existingData)
  }

  fun updateToDo(uid: String, todo: ToDo) {
    // Read existing data from file
    val existingData = readToDoListFromFile()

    // Add or update the ToDo item
    existingData[uid] = todo

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
