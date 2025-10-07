package com.example.todo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todo.data.TodoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreen(
    item: TodoItem,
    onNavigateBack: () -> Unit,
    onSave: (id: Int, newTask: String) -> Unit,
    onDelete: (id: Int) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedTask by remember { mutableStateOf(item.task) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Task" else "Task Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = {
                            onSave(item.id, editedTask)
                            // We will let AppNavigation handle navigation after save
                        }) {
                            Icon(Icons.Default.Done, contentDescription = "Save Task")
                        }
                    } else {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Task")
                        }
                    }

                    IconButton(onClick = { onDelete(item.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Task")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            if (isEditing) {
                OutlinedTextField(
                    value = editedTask,
                    onValueChange = { editedTask = it },
                    label = { Text("Task Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = "Task:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = item.task,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Text(
                    text = "Status:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (item.isCompleted) "Completed" else "Pending",
                    fontSize = 18.sp
                )
            }
        }
    }
}