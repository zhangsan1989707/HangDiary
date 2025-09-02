package com.example.hangdiary.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.hangdiary.data.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * 分类数据访问对象
 * 定义对分类表的数据库操作
 */
@Dao
interface CategoryDao {
    /**
     * 插入分类
     * @param category 要插入的分类对象
     * @return 插入后的分类ID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category): Long

    /**
     * 更新分类
     * @param category 要更新的分类对象
     * @return 更新的行数
     */
    @Update
    suspend fun update(category: Category): Int

    /**
     * 删除分类
     * @param category 要删除的分类对象
     * @return 删除的行数
     */
    @Delete
    suspend fun delete(category: Category): Int

    /**
     * 根据ID查询分类
     * @param id 分类ID
     * @return 对应的分类对象，若不存在则为null
     */
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): Category?

    /**
     * 查询所有分类
     * @return 分类列表的Flow
     */
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<Category>>

    /**
     * 根据名称查询分类
     * @param name 分类名称
     * @return 对应的分类对象，若不存在则为null
     */
    @Query("SELECT * FROM categories WHERE name = :name")
    suspend fun getByName(name: String): Category?
}