package com.example.todo

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todo.ui.screens.TodoDetailScreen
import com.example.todo.ui.screens.TodoScreen
import com.example.todo.ui.screens.WelcomeScreen
import com.example.todo.ui.viewmodel.TodoViewModel

object AppRoutes {
    const val WELCOME_SCREEN = "welcome_screen"
    const val TODO_SCREEN = "todo_screen"
    const val TODO_DETAIL_SCREEN = "todo_detail_screen/{todoId}"

    fun createTodoDetailRoute(todoId: Int) = "todo_detail_screen/$todoId"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // viewModel() creates a ViewModel instance scoped to the NavHost.
    // It will be the same instance for all screens in this graph.
    val todoViewModel: TodoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.WELCOME_SCREEN
    ) {
        composable(route = AppRoutes.WELCOME_SCREEN) {
            WelcomeScreen(
                onNavigateToTodo = { name ->
                    todoViewModel.setUserName(name) // Send event to ViewModel
                    navController.navigate(AppRoutes.TODO_SCREEN) {
                        popUpTo(AppRoutes.WELCOME_SCREEN) { inclusive = true }
                    }
                }
            )
        }

        composable(route = AppRoutes.TODO_SCREEN) {
            TodoScreen(
                viewModel = todoViewModel,
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
            val item = todoId?.let { todoViewModel.getTodoById(it) }

            if (item != null) {
                TodoDetailScreen(
                    item = item,
                    onNavigateBack = { navController.popBackStack() },
                    onSave = { id, newTask ->
                        todoViewModel.updateTodo(id, newTask) // Send event
                        navController.popBackStack()
                    },
                    onDelete = { id ->
                        todoViewModel.deleteTodo(id) // Send event
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}