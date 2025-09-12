package com.example.hangdiary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hangdiary.data.model.Todo
import com.example.hangdiary.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

/**
 * 待办列表ViewModel
 * 管理待办事项的状态和业务逻辑
 */
@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val todoRepository: TodoRepository
) : ViewModel() {

    // UI状态
    data class TodoListState(
        val todos: List<Todo> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val searchQuery: String = "",
        val categories: List<String> = emptyList(),
        val selectedCategory: String? = null,
        val selectedDate: LocalDate = LocalDate.now()
    )

    // 状态流
    private val _state = MutableStateFlow(TodoListState())
    val state: StateFlow<TodoListState> = _state.asStateFlow()

    // 事件
    sealed class TodoListEvent {
        data class CreateTodo(
            val title: String, 
            val notes: String? = null, 
            val dueDate: LocalDate? = null,
            val dueTime: LocalTime? = null,
            val category: String? = null,
            val priority: Int = 1
        ) : TodoListEvent()
        data class UpdateTodo(val todo: Todo) : TodoListEvent()
        data class DeleteTodo(val todo: Todo) : TodoListEvent()
        data class ToggleTodoCompletion(val todo: Todo) : TodoListEvent()
        data class SearchTodos(val query: String) : TodoListEvent()
        data class FilterByCategory(val category: String?) : TodoListEvent()
        data class SelectDate(val date: LocalDate) : TodoListEvent()
    }

    init {
        loadTodos()
        loadCategories()
    }

    /**
     * 加载待办事项
     */
    private fun loadTodos() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                todoRepository.getAllTodos()
                    .collect { todos ->
                        _state.update { it.copy(todos = todos, isLoading = false) }
                    }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    /**
     * 处理事件
     */
    fun handleEvent(event: TodoListEvent) {
        viewModelScope.launch {
            try {
                when (event) {
                    is TodoListEvent.CreateTodo -> {
                        todoRepository.createTodo(
                            title = event.title,
                            notes = event.notes,
                            dueDate = event.dueDate,
                            dueTime = event.dueTime,
                            category = event.category,
                            priority = event.priority
                        )
                    }
                    is TodoListEvent.UpdateTodo -> {
                        todoRepository.updateTodo(event.todo)
                    }
                    is TodoListEvent.DeleteTodo -> {
                        todoRepository.deleteTodo(event.todo)
                    }
                    is TodoListEvent.ToggleTodoCompletion -> {
                        todoRepository.toggleTodoCompletion(event.todo)
                    }
                    is TodoListEvent.SearchTodos -> {
                        _state.update { it.copy(searchQuery = event.query) }
                        if (event.query.isBlank()) {
                            loadTodos()
                        } else {
                            todoRepository.searchTodos(event.query)
                                .collect { todos ->
                                    _state.update { it.copy(todos = todos) }
                                }
                        }
                    }
                    is TodoListEvent.FilterByCategory -> {
                        _state.update { it.copy(selectedCategory = event.category) }
                        if (event.category == null) {
                            loadTodos()
                        } else {
                            todoRepository.getTodosByCategory(event.category)
                                .collect { todos ->
                                    _state.update { it.copy(todos = todos) }
                                }
                        }
                    }
                    is TodoListEvent.SelectDate -> {
                        _state.update { it.copy(selectedDate = event.date) }
                        todoRepository.getTodosForDate(event.date)
                            .collect { todos ->
                                _state.update { it.copy(todos = todos) }
                            }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    /**
     * 清除错误状态
     */
    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    /**
     * 获取活跃的待办事项
     */
    fun getActiveTodos(): Flow<List<Todo>> = todoRepository.getActiveTodos()

    /**
     * 获取已完成的待办事项
     */
    fun getCompletedTodos(): Flow<List<Todo>> = todoRepository.getCompletedTodos()

    /**
     * 加载分类列表
     */
    private fun loadCategories() {
        viewModelScope.launch {
            try {
                todoRepository.getAllCategories()
                    .collect { categories ->
                        _state.update { it.copy(categories = categories) }
                    }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}