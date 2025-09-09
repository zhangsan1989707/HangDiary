package com.example.hangdiary.data.dao

import androidx.room.*
import com.example.hangdiary.data.model.Settings
import kotlinx.coroutines.flow.Flow

/**
 * 设置数据访问对象
 * 定义对设置表的数据库操作
 */
@Dao
interface SettingsDao {
    /**
     * 插入设置
     * @param settings 要插入的设置对象
     * @return 插入后的设置ID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: Settings): Long

    /**
     * 更新设置
     * @param settings 要更新的设置对象
     * @return 更新的行数
     */
    @Update
    suspend fun update(settings: Settings): Int

    /**
     * 删除设置
     * @param settings 要删除的设置对象
     * @return 删除的行数
     */
    @Delete
    suspend fun delete(settings: Settings): Int

    /**
     * 根据ID查询设置
     * @param id 设置ID
     * @return 对应的设置对象，若不存在则为null
     */
    @Query("SELECT * FROM settings WHERE id = :id")
    suspend fun getById(id: Long): Settings?

    /**
     * 查询所有设置
     * @return 设置列表的Flow
     */
    @Query("SELECT * FROM settings")
    fun getAllSettings(): Flow<List<Settings>>

    /**
     * 获取第一个设置（通常只有一个设置记录）
     * @return 设置对象的Flow
     */
    @Query("SELECT * FROM settings LIMIT 1")
    fun getFirstSettings(): Flow<Settings?>

    /**
     * 更新视图设置
     * @param id 设置ID
     * @param viewMode 视图设置
     * @return 更新的行数
     */
    @Query("UPDATE settings SET viewMode = :viewMode WHERE id = :id")
    suspend fun updateViewMode(id: Long, viewMode: String): Int



    /**
     * 更新暗黑模式设置
     * @param id 设置ID
     * @param isDarkMode 是否启用暗黑模式
     * @return 更新的行数
     */
    @Query("UPDATE settings SET isDarkMode = :isDarkMode WHERE id = :id")
    suspend fun updateDarkMode(id: Long, isDarkMode: Boolean): Int

    /**
     * 更新卡片视图设置
     * @param id 设置ID
     * @param isCardView 是否启用卡片视图
     * @return 更新的行数
     */
    @Query("UPDATE settings SET isCardView = :isCardView WHERE id = :id")
    suspend fun updateCardView(id: Long, isCardView: Boolean): Int















    /**
     * 更新默认日记颜色设置
     * @param id 设置ID
     * @param defaultDiaryColor 默认日记颜色
     * @return 更新的行数
     */
    @Query("UPDATE settings SET defaultDiaryColor = :defaultDiaryColor WHERE id = :id")
    suspend fun updateDefaultDiaryColor(id: Long, defaultDiaryColor: String?): Int
}