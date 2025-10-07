package com.example.todo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todo.data.TodoItem
import com.example.todo.ui.screens.TodoDetailScreen
import com.example.todo.ui.screens.TodoScreen
import com.example.todo.ui.screens.WelcomeScreen

object AppRoutes {
    const val WELCOME_SCREEN = "welcome_screen"
    const val TODO_SCREEN = "todo_screen/{name}"
    const val TODO_DETAIL_SCREEN = "todo_detail_screen/{todoId}"

    fun createTodoRoute(name: String) = "todo_screen/$name"
    fun createTodoDetailRoute(todoId: Int) = "todo_detail_screen/$todoId"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val todoItems = remember { mutableStateListOf<TodoItem>() }

    NavHost(
        navController = navController,
        startDestination = AppRoutes.WELCOME_SCREEN
    ) {
        composable(route = AppRoutes.WELCOME_SCREEN) {
            WelcomeScreen(
                onNavigateToTodo = { name ->
                    navController.navigate(AppRoutes.createTodoRoute(name))
                }
            )
        }

        composable(
            route = AppRoutes.TODO_SCREEN,
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: "User"
            TodoScreen(
                userName = name,
                todoItems = todoItems,
                onNavigateToDetail = { todoId ->
                    navController.navigate(AppRoutes.createTodoDetailRoute(todoId))
                }
            )
        }

        composable(
            route = AppRoutes.TODO_DETAIL_SCREEN,
            arguments = listOf(navArgument("todoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val todoId = backStackEntry.arguments?.getInt("todoId")
            val item = todoItems.find { it.id == todoId }

            if (item != null) {
                TodoDetailScreen(
                    item = item,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    // onSave logic
                    onSave = { id, newTask ->
                        // Find the original item to remove it
                        val originalItem = todoItems.find { it.id == id }
                        if (originalItem != null) {
                            // Create the updated version
                            val updatedItem = originalItem.copy(task = newTask)
                            // Remove the old one from the list
                            todoItems.remove(originalItem)
                            // Add the updated one to the top (index 0)
                            todoItems.add(0, updatedItem)
                        }
                        // Navigate back to the list screen after saving
                        navController.popBackStack()
                    },
                    onDelete = { id ->
                        navController.popBackStack()
                        todoItems.removeIf { it.id == id }
                    }
                )
            }
        }
    }
}