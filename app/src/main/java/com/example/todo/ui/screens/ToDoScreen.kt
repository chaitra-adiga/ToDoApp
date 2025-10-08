package com.example.todo.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todo.data.TodoItem
import com.example.todo.ui.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// CHANGE 1: The function signature is updated.
// It now accepts the ViewModel as the single source of state and logic,
// instead of receiving 'userName' and 'todoItems' directly.
fun TodoScreen(
    viewModel: TodoViewModel,
    onNavigateToDetail: (Int) -> Unit
) {
    // CHANGE 2: We collect the UI state from the ViewModel.
    // The `collectAsState()` function subscribes this Composable to the ViewModel's StateFlow.
    // Whenever the uiState in the ViewModel changes, this Composable will automatically recompose.
    val uiState by viewModel.uiState.collectAsState() //d

    // These states are purely for controlling the UI elements within this screen
    // and don't need to be in the ViewModel.
    var text by rememberSaveable { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current //d

    // The filtering logic now uses the list from the collected uiState.
    val filteredItems = if (searchQuery.isBlank()) {
        uiState.items
    } else {
        uiState.items.filter { it.task.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(//d
        bottomBar = {
            Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, //d
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = text,
                        onValueChange = { newText -> text = newText },
                        label = { Text("Enter a new task") },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            // CHANGE 3: Instead of modifying a list directly, we send an "event"
                            // to the ViewModel, telling it our intent to add a new todo. //d
                            viewModel.addTodo(text)
                            // The UI resets its own temporary state.
                            text = ""
                        },
                        modifier = Modifier.size(48.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary //d
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Task"
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // The user's name is now sourced from the //uiState// object.
            Text(
                text = "${uiState.userName}'s To-Do List",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                label = { Text("Search tasks...") },
                singleLine = true
            )

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(filteredItems, key = { it.id }) { item ->
                    TodoItemRow(
                        item = item,
                        onItemClick = { onNavigateToDetail(item.id) },
                        onItemChecked = { isChecked ->
                            // CHANGE 4: Send another "event" to the ViewModel.
                            // The ViewModel will handle the logic for toggling the item's status.
                            viewModel.toggleCompleted(item)

                            // The Toast logic can remain, but it also gets its data from the single source of truth.
                            if (isChecked) {
                                val remainingTasks = (uiState.items.count { !it.isCompleted }) -1
                                val message = "One Down : $remainingTasks to go!"
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TodoItemRow(
    item: TodoItem,
    onItemClick: () -> Unit,
    onItemChecked: (Boolean) -> Unit
) {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val debounceInterval = 500L //d

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime > debounceInterval) {
                    lastClickTime = currentTime
                    onItemClick()
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = item.isCompleted,
            onCheckedChange = onItemChecked
        )
        Text(
            text = item.task,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        )
    }
}