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
    suspend fun createTodo(title: String, content: String? = null, dueDate: LocalDateTime? = null): Long {
        val todo = Todo(
            title = title,
            content = content,
            createdAt = LocalDateTime.now(),
            dueDate = dueDate
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
}