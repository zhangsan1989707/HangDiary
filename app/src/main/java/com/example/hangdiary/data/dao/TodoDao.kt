package com.example.hangdiary.data.dao

import androidx.room.*
import com.example.hangdiary.data.model.Todo
import kotlinx.coroutines.flow.Flow

/**
 * 待办事项数据访问对象
 */
@Dao
interface TodoDao {

    /**
     * 获取所有待办事项，按创建时间降序排列
     */
    @Query("SELECT * FROM todos ORDER BY createdAt DESC")
    fun getAllTodos(): Flow<List<Todo>>

    /**
     * 获取未完成的待办事项
     */
    @Query("SELECT * FROM todos WHERE isCompleted = 0 ORDER BY createdAt DESC")
    fun getActiveTodos(): Flow<List<Todo>>

    /**
     * 获取已完成的待办事项
     */
    @Query("SELECT * FROM todos WHERE isCompleted = 1 ORDER BY createdAt DESC")
    fun getCompletedTodos(): Flow<List<Todo>>

    /**
     * 根据ID获取单个待办事项
     */
    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getTodoById(id: Long): Todo?

    /**
     * 插入新的待办事项
     */
    @Insert
    suspend fun insertTodo(todo: Todo): Long

    /**
     * 更新待办事项
     */
    @Update
    suspend fun updateTodo(todo: Todo)

    /**
     * 删除待办事项
     */
    @Delete
    suspend fun deleteTodo(todo: Todo)

    /**
     * 根据ID删除待办事项
     */
    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun deleteTodoById(id: Long)

    /**
     * 标记待办事项为已完成/未完成
     */
    @Query("UPDATE todos SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateTodoCompletion(id: Long, isCompleted: Boolean)

    /**
     * 搜索待办事项
     */
    @Query("SELECT * FROM todos WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchTodos(query: String): Flow<List<Todo>>
}