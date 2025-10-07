package com.example.todo.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todo.data.TodoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    userName: String,
    todoItems: MutableList<TodoItem>,
    onNavigateToDetail: (Int) -> Unit
) {
    var text by rememberSaveable { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val filteredItems = if (searchQuery.isBlank()) {
        todoItems
    } else {
        todoItems.filter { it.task.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        bottomBar = {
            Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
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
                            if (text.isNotBlank()) {
                                val newTodo = TodoItem(System.currentTimeMillis().toInt(), text)
                                todoItems.add(0, newTodo)
                                text = ""
                                searchQuery = ""
                            }
                        },
                        modifier = Modifier.size(48.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
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
            Text(
                text = "$userName's To-Do List",
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
                            val index = todoItems.indexOf(item)
                            if (index >= 0) {
                                todoItems[index] = item.copy(isCompleted = isChecked)
                                if (isChecked) {
                                    val remainingTasks = todoItems.count { !it.isCompleted }
                                    val message = "One Down : $remainingTasks to go!"
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
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
    val debounceInterval = 500L

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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TodoScreen(userName = "Android", todoItems = mutableListOf(), onNavigateToDetail = {})
}