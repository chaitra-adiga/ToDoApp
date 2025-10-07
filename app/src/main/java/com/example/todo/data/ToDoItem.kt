package com.example.todo.data

data class TodoItem(
    val id: Int,
    val task: String,
    var isCompleted: Boolean = false
)