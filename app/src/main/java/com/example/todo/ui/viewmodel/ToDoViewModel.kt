package com.example.todo.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.todo.data.TodoItem
import com.example.todo.data.ToDoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// This data class represents the entire state of the UI for the todo screens.
data class TodoUiState(
    val items: List<TodoItem> = emptyList(),
    val userName: String = "User"
)

class TodoViewModel : ViewModel() {

    // The ViewModel gets an instance of the repository.
    private val repository = ToDoRepository()

    // Private mutable state that can only be changed within the ViewModel.
    private val _uiState = MutableStateFlow(TodoUiState())
    // Public, immutable state that the UI can observe.
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    init {
        // Load the initial list of todos when the ViewModel is first created.
        loadTodos()
    }

    private fun loadTodos() {
        // Update the state with the list from the repository.
        _uiState.update { currentState ->
            currentState.copy(items = repository.getTodos())
        }
    }

    // This function is an "event" triggered by the UI.
    fun setUserName(name: String) {
        _uiState.update { it.copy(userName = name) }
    }

    // --- Events from the UI ---

    fun addTodo(task: String) {
        if (task.isNotBlank()) {
            repository.addTodo(task)
            loadTodos() // Refresh the state after adding.
        }
    }

    fun deleteTodo(id: Int) {
        repository.deleteTodo(id)
        loadTodos() // Refresh the state.
    }

    fun updateTodo(id: Int, newTask: String) {
        repository.updateTodo(id, newTask)
        loadTodos() // Refresh the state.
    }

    fun toggleCompleted(item: TodoItem) {
        repository.toggleTodoCompleted(item.id)
        loadTodos() // Refresh the state.
    }

    fun getTodoById(id: Int): TodoItem? {
        return repository.getTodoById(id)
    }
}