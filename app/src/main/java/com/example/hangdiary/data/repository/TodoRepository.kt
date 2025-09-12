package com.example.hangdiary.data.repository

import com.example.hangdiary.data.dao.TodoDao
import com.example.hangdiary.data.model.Todo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 待办事项仓库
 * 封装待办相关的业务逻辑
 */
@Singleton
class TodoRepository @Inject constructor(
    private val todoDao: TodoDao
) {

    /**
     * 获取所有待办事项
     */
    fun getAllTodos(): Flow<List<Todo>> = todoDao.getAllTodos()

    /**
     * 获取活跃的待办事项
     */
    fun getActiveTodos(): Flow<List<Todo>> = todoDao.getActiveTodos()

    /**
     * 获取已完成的待办事项
     */
    fun getCompletedTodos(): Flow<List<Todo>> = todoDao.getCompletedTodos()

    /**
     * 根据ID获取待办事项
     */
    suspend fun getTodoById(id: Long): Todo? = todoDao.getTodoById(id)

    /**
     * 创建新的待办事项
     */
    suspend fun createTodo(
        title: String, 
        notes: String? = null, 
        dueDate: java.time.LocalDate? = null,
        dueTime: java.time.LocalTime? = null,
        category: String? = null,
        priority: Int = 1
    ): Long {
        val todo = Todo(
            title = title,
            notes = notes,
            createdAt = LocalDateTime.now(),
            dueDate = dueDate,
            dueTime = dueTime,
            category = category,
            priority = priority
        )
        return todoDao.insertTodo(todo)
    }

    /**
     * 更新待办事项
     */
    suspend fun updateTodo(todo: Todo) {
        val updatedTodo = todo.copy(updatedAt = LocalDateTime.now())
        todoDao.updateTodo(updatedTodo)
    }

    /**
     * 删除待办事项
     */
    suspend fun deleteTodo(todo: Todo) {
        todoDao.deleteTodo(todo)
    }

    /**
     * 切换待办事项完成状态
     */
    suspend fun toggleTodoCompletion(todo: Todo) {
        val updatedTodo = todo.copy(
            isCompleted = !todo.isCompleted,
            updatedAt = LocalDateTime.now()
        )
        todoDao.updateTodo(updatedTodo)
    }

    /**
     * 搜索待办事项
     */
    fun searchTodos(query: String): Flow<List<Todo>> = todoDao.searchTodos(query)

    /**
     * 根据分类获取待办事项
     */
    fun getTodosByCategory(category: String): Flow<List<Todo>> = todoDao.getTodosByCategory(category)

    /**
     * 获取所有分类
     */
    fun getAllCategories(): Flow<List<String>> = todoDao.getAllCategories()

    /**
     * 根据优先级获取待办事项
     */
    fun getTodosByPriority(priority: Int): Flow<List<Todo>> = todoDao.getTodosByPriority(priority)

    /**
     * 获取今日到期的待办事项
     */
    fun getTodosForDate(date: java.time.LocalDate): Flow<List<Todo>> = 
        todoDao.getTodosForDate(date.toString())
}