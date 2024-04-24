
package com.github.se.studybuddies.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.se.studybuddies.data.todo.ToDoList
import com.github.se.studybuddies.database.DatabaseConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ToDoListViewModel() : ViewModel() {
    private val firebaseConnection = DatabaseConnection()

    private val _todos = MutableStateFlow(ToDoList(emptyList()))
    val todos: StateFlow<ToDoList> = _todos
    /*
    private val _uiState = MutableStateFlow(ToDoList(emptyList()))
    val uiState: StateFlow<ToDoList> = _uiState

    val todoList = _uiState.map { t -> t.getFilteredTask }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList())
    */
    init {
        fetchAllTodos()
    }

    fun fetchAllTodos() {
        viewModelScope.launch {
            try {
                val todos = firebaseConnection.getAllItems()
                val sortedTodos = todos.getAllTasks().sortedBy { it.dueDate }
                _todos.value = ToDoList(sortedTodos)
            } catch (e: Exception) {
                Log.d("MyPrint", "Could not fetch items $e")
            }
        }
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
