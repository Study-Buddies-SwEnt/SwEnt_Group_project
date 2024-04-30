package com.github.se.studybuddies.viewModels

import android.app.Application
import android.content.SharedPreferences
import android.content.QuickViewConstants
import android.util.Base64
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
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class ToDoListViewModel(studyBuddies: Application) : AndroidViewModel(studyBuddies) {

  private val _todos = MutableStateFlow(ToDoList(emptyList()))
  val todos: StateFlow<ToDoList> = _todos

  private val _todo = MutableStateFlow(emptyToDo())
  val todo: StateFlow<ToDo> = _todo

  private val gson = Gson()
  private val toDoFile = File(studyBuddies.filesDir, "ToDoList.json")
  private val encryptedToDoFile = encryptAndSaveFile(toDoFile)
  private val sharedPref = SharedPreferences()

  init {
    fetchAllTodos()
  }


  //encryption scheme

  private val secretKeyPref = "SECRET_KEY_PREF"

  @Throws(Exception::class)
  fun generateSecretKey(): SecretKey? {
    val secureRandom = SecureRandom()
    val keyGenerator = KeyGenerator.getInstance("AES")
    //generate a key with secure random
    keyGenerator?.init(128, secureRandom)
    return keyGenerator?.generateKey()
  }

  fun saveSecretKey(sharedPref: SharedPreferences, secretKey: SecretKey): String {
    val encodedKey = Base64.encodeToString(secretKey.encoded, Base64.NO_WRAP)
    sharedPref.edit().putString(secretKeyPref, encodedKey).apply()
    return encodedKey
  }

  fun getSecretKey(sharedPref: SharedPreferences): SecretKey {

    val key = sharedPref.getString(secretKeyPref, null)

    if (key == null) {
      //generate secure random
      val secretKey = generateSecretKey()
      saveSecretKey(sharedPref, secretKey!!)
      return secretKey
    }

    val decodedKey = Base64.decode(key, Base64.NO_WRAP)
    val originalKey = SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")

    return originalKey
  }

  @Throws(Exception::class)
  fun readFile(filePath: String): ByteArray {
    val file = File(filePath)
    val fileContents = file.readBytes()
    val inputBuffer = BufferedInputStream(
      FileInputStream(file)
    )

    inputBuffer.read(fileContents)
    inputBuffer.close()

    return fileContents
  }

  @Throws(Exception::class)
  fun saveFile(fileData: ByteArray, path: String) {
    val file = File(path)
    val bos = BufferedOutputStream(FileOutputStream(file, false))
    bos.write(fileData)
    bos.flush()
    bos.close()
  }

  @Throws(Exception::class)
  fun encrypt(yourKey: SecretKey, fileData: ByteArray): ByteArray {
    val data = yourKey.getEncoded()
    val skeySpec = SecretKeySpec(data, 0, data.size, "AES")
    val cipher = Cipher.getInstance("AES", "BC")
    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, IvParameterSpec(ByteArray(cipher.getBlockSize())))
    return cipher.doFinal(fileData)
  }

  fun encryptAndSaveFile(file: File) : File {
    try {
      val fileData = readFile(file.path)

      //get secret key
      val secretKey = getSecretKey(sharedPref)
      //encrypt file
      val encodedData = secretKey?.let { encrypt(it, fileData) }

      if (encodedData != null) {
        saveFile(encodedData, file.path)
      }
      return file

    } catch (e: Exception) {
      Log.e("Encryption", "Encryption error")
    }
  }

  @Throws(Exception::class)
  fun decrypt(yourKey: SecretKey, fileData: ByteArray): ByteArray {
    val decrypted: ByteArray
    val cipher = Cipher.getInstance("AES", "BC")
    cipher.init(Cipher.DECRYPT_MODE, yourKey, IvParameterSpec(ByteArray(cipher.blockSize)))
    decrypted = cipher.doFinal(fileData)
    return decrypted
  }

  private fun decryptFile(file: File): ByteArray {
    val fileData = readFile(file.path)
    val secretKey = getSecretKey(sharedPref)
    return decrypt(secretKey, fileData)
  }





  private fun readToDoListFromFile(): MutableMap<String, ToDo> {
    if (!encryptedToDoFile.exists()) {
      return mutableMapOf() // Return an empty map if file doesn't exist yet
    }
    val json = encryptedToDoFile.readText()
    // Deserialize JSON string to map of ToDo objects
    val type: Type = object : TypeToken<Map<String, ToDo>>() {}.type
    return gson.fromJson(json, type) ?: mutableMapOf()
  }

  private fun writeToDoListToFile(todoList: Map<String, ToDo>) {
    // Serialize the map of ToDo objects to JSON
    val json = gson.toJson(todoList)
    encryptedToDoFile.writeText(json)
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

  private fun emptyToDo(): ToDo {
    return ToDo("", "", LocalDate.now(), "", ToDoStatus.CREATED)
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
