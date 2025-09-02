package com.example.hangdiary.data.repository

import com.example.hangdiary.data.dao.CategoryDao
import com.example.hangdiary.data.model.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 分类仓库
 * 负责协调对分类数据的访问
 */
@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    /**
     * 插入分类
     * @param category 要插入的分类
     * @return 插入后的分类ID
     */
    suspend fun insert(category: Category): Long {
        return categoryDao.insert(category)
    }

    /**
     * 更新分类
     * @param category 要更新的分类
     * @return 更新的行数
     */
    suspend fun update(category: Category): Int {
        return categoryDao.update(category)
    }

    /**
     * 删除分类
     * @param category 要删除的分类
     * @return 删除的行数
     */
    suspend fun delete(category: Category): Int {
        return categoryDao.delete(category)
    }

    /**
     * 根据ID获取分类
     * @param id 分类ID
     * @return 分类对象，若不存在则为null
     */
    suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getById(id)
    }

    /**
     * 获取所有分类
     * @return 分类列表的Flow
     */
    fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories()
    }

    /**
     * 根据名称获取分类
     * @param name 分类名称
     * @return 分���对象，若不存在则为null
     */
    suspend fun getCategoryByName(name: String): Category? {
        return categoryDao.getByName(name)
    }

}