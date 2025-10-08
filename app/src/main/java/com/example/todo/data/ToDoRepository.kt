package com.example.todo.data

class ToDoRepository {
    private val _todoItems= mutableListOf<TodoItem>()
    /*gettodo  gettodoby Id update todo delete todo toggletodocomplete*/
    fun getTodos(): List<TodoItem>{
        return _todoItems.toList() //returning a new list
    }

    fun getTodoById(id: Int) : TodoItem? { //it could be null as well
        return _todoItems.toList().find { it.id == id }
    }

    fun addTodo(task: String){
        val newId = (_todoItems.maxOfOrNull { it.id }?: 0)+1
        _todoItems.add(0, TodoItem(id=newId,task))
    }

    fun updateTodo(id:Int, newTask: String){
        val itemIndex = _todoItems.indexOfFirst { it.id == id }
        if(itemIndex != -1){
            val originalItem = _todoItems[itemIndex]
            val updatedItem = originalItem.copy(task=newTask)
            _todoItems.removeAt(itemIndex)
            _todoItems.add(0, updatedItem)
        }
    }

    fun deleteTodo(id: Int){
        _todoItems.removeAt(id)
    }

    fun toggleTodoCompleted(id: Int) {
        val itemIndex = _todoItems.indexOfFirst { it.id == id }
        if (itemIndex != -1) {
            val item = _todoItems[itemIndex]
            _todoItems[itemIndex] = item.copy(isCompleted = !item.isCompleted)
        }
    }
}